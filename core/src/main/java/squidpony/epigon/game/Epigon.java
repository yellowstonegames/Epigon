package squidpony.epigon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import squidpony.ArrayTools;
import squidpony.Messaging;
import squidpony.panel.IColoredString;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.*;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.*;

import squidpony.epigon.combat.ActionOutcome;
import squidpony.epigon.data.*;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.control.DataStarter;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.display.*;
import squidpony.epigon.files.*;
import squidpony.epigon.input.InputSpecialMultiplexer;
import squidpony.epigon.input.key.*;
import squidpony.epigon.input.mouse.*;
import squidpony.epigon.mapping.*;
import squidpony.epigon.SoundManager;
import squidpony.epigon.util.ConstantKey;
import squidpony.epigon.util.Utilities;

import static squidpony.squidgrid.gui.gdx.SColor.*;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public abstract class Epigon extends Game {

    public final StatefulRNG rng;
    // meant to be used to generate seeds for other RNGs; can be seeded when they should be fixed
    public static final DiverRNG rootChaos = new DiverRNG();
    public static final long startMillis = TimeUtils.millis();

    public RecipeMixer mixer;
    public DataStarter dataStarter;
    public MapDecorator mapDecorator;
    public static final char BOLD = '\0', ITALIC = '\0', REGULAR = '\0';
//    public static final char BOLD = '\u4000', ITALIC = '\u8000', REGULAR = '\0';

    public Config config;
    public Settings settings;
    public ScreenDisplayConfig displayConfig;

    // Audio
    private SoundManager sound;

    // Display
    public FilterBatch batch;
    public SquidColorCenter colorCenter;
    public FloatFilters.YCwCmFilter filter = new FloatFilters.YCwCmFilter(0.9f, 1.3f, 1.3f);
    public FloatFilter //identityFilter = new FloatFilters.IdentityFilter(),
        grayscale = new FloatFilters.YCwCmFilter(0.75f, 0.2f, 0.2f);
    public SparseLayers mapSLayers;
    public SparseLayers passiveSLayers;
    public SparseLayers mapHoverSLayers;
    public SparseLayers mapOverlaySLayers;
    private SparseLayers infoSLayers;
    private SparseLayers contextSLayers;
    private SparseLayers messageSLayers;

    public InputSpecialMultiplexer multiplexer;
    public SquidInput contextInput;
    public SquidInput infoInput;
    public SquidInput messageInput;
    public SquidInput fallbackInput;

    private Color unseenColor;
    public float unseenCreatureColorFloat;
    public ArrayList<Coord> toCursor;

    public boolean showingMenu = false;
    public Coord menuLocation = null;
    public Physical currentTarget = null;
    public OrderedMap<String, Weapon> maneuverOptions = new OrderedMap<>(12);
    public OrderedMap<String, Interactable> interactionOptions = new OrderedMap<>(8);

    // Set up the text display portions
    private ArrayList<IColoredString<Color>> messages = new ArrayList<>();
    private int messageIndex;
    public PanelSize mapSize;
    public PanelSize messageSize;
    public PanelSize infoSize;
    public PanelSize contextSize;
    public TextCellFactory defaultFont;
    public TextCellFactory smallFont;

    // World
    public LocalAreaGenerator worldGenerator;
    public CastleGenerator castleGenerator;
    public EpiMap[] world;
    public EpiMap map;
    public char[][] simple;
    public char[][] lineDungeon, prunedDungeon;
    public float[][] wallColors, walls;

    public GreasedRegion blockage, floors;

    private int messageCount;
    public int depth;
    public FxHandler fxHandler;
    public FxHandler fxHandlerPassive;
    public MapOverlayHandler mapOverlayHandler;
    public ContextHandler contextHandler;
    public InfoHandler infoHandler;
    public DijkstraMap toPlayerDijkstra, monsterDijkstra;
    public LOS los;
    public Coord cursor;
    public Physical player;
    public ArrayList<Coord> awaitedMoves;
    public OrderedMap<Coord, Physical> creatures;
    private int autoplayTurns = 0;

    private Stage messageStage, infoStage, contextStage;
    private Viewport messageViewport, infoViewport, contextViewport;

    private GLProfiler glp;
    private final StringBuilder tempSB = new StringBuilder(16);

    // input handlers
    public KeyHandler fallbackKeys;
    public SquidMouse contextMouse;
    public SquidMouse infoMouse;
    public SquidMouse messageMouse;

    public Epigon(Config config) {
        this.config = config;
        settings = config.settings;
        displayConfig = config.displayConfig;
        rng = new StatefulRNG(config.settings.seedValue);
    }

    @Override
    public void create() {
        System.out.println("Working in folder: " + System.getProperty("user.dir"));

        System.out.println("Loading sound manager.");
        sound = new SoundManager();
        colorCenter = new SquidColorCenter();
//        filter = new FloatFilters.DistinctRedGreenFilter();
//        filter = new FloatFilters.GrayscaleFilter();
//        filter = new FloatFilters.ColorizeFilter(SColor.CLOVE_BROWN, 0.6f, 0.0f);
//        filter = new FloatFilters.YCbCrFilter(0.9f, 1.3f, 1.3f);
//        identityFilter = new FloatFilters.IdentityFilter();
        // mostly mutes colors but doesn't fully grayscale everything; also darkens colors slightly

        //grayscale = new FloatFilters.YCbCrFilter(0.75f, 0f, 0f); // an option to fully grayscale/darken
        //System.out.println(rootChaos.getState());
        glp = new GLProfiler(Gdx.graphics);
        if (config.debugConfig.debugActive) {
            glp.enable();
        } else {
            glp.disable();
        }

        defaultFont = new TextCellFactory().font("Iosevka_outlined.fnt");
        defaultFont.width(displayConfig.mapSize.cellWidth).height(displayConfig.mapSize.cellHeight);
        defaultFont.initBySize();

//        defaultFont = DefaultResources.getCrispLeanFamily();
        defaultFont.bmpFont.setFixedWidthGlyphs(Utilities.USABLE_CHARS);
        defaultFont.setSmoothingMultiplier(1.25f);

        smallFont = defaultFont.copy();
        smallFont.bmpFont.setFixedWidthGlyphs(Utilities.USABLE_CHARS);
        smallFont.setSmoothingMultiplier(1.5f);

        mapSize = displayConfig.mapSize;
        messageSize = displayConfig.messageSize;
        infoSize = displayConfig.infoSize;
        contextSize = displayConfig.contextSize;
        messageCount = displayConfig.messageCount;

        mixer = new RecipeMixer();
        //handBuilt = new DataStarter(mixer);
        Weapon.init();

        Coord.expandPoolTo(settings.worldWidth + 1, Math.max(settings.worldHeight, settings.diveWorldDepth + MapConstants.DIVE_HEADER.length) + 1);

        // this matches the background color outside the map to the background color of unseen areas inside the map,
        // using the same filter (reducing brightness and saturation using YCwCm) as that stage of the map draw.
        float unseenY = lumaYCwCm(DB_INK) * 0.7f,
            unseenCw = chromaWarm(DB_INK) * 0.65f,
            unseenCm = chromaMild(DB_INK) * 0.65f;
        unseenColor = colorFromFloat(floatGetYCwCm(unseenY, unseenCw, unseenCm, 1f));
        unseenCreatureColorFloat = SColor.CW_DARK_GRAY.toFloatBits();

        System.out.println("Putting together display.");
        //FilterBatch automatically filters all text colors and image tints with a FloatFilter
        batch = new FilterBatch(filter);
        // uncomment the line below to see the game with no filters
        //batch = new FilterBatch();
        buildDisplay(batch);

        buildInputProcessors();
        Gdx.input.setInputProcessor(multiplexer);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // force a refresh to align all the windows and listeners

        startGame();
    }

    public void buildDisplay(Batch batch) {
        messageViewport = new StretchViewport(messageSize.pixelWidth(), messageSize.pixelHeight());
        infoViewport = new StretchViewport(infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport = new StretchViewport(contextSize.pixelWidth(), contextSize.pixelHeight());

        // Here we make sure our Stages, which holds any text-based grids we make, uses our Batch.
        messageStage = new Stage(messageViewport, batch);
        infoStage = new Stage(infoViewport, batch);
        contextStage = new Stage(contextViewport, batch);

        IColoredString<Color> emptyICS = IColoredString.Impl.create();

        messageIndex = messageCount;
        for (int i = 0; i <= messageCount; i++) {
            messages.add(emptyICS);
        }

        messageSLayers = new SparseLayers(
            messageSize.gridWidth,
            messageSize.gridHeight,
            messageSize.cellWidth,
            messageSize.cellHeight,
            defaultFont.copy());
        messageSLayers.font.bmpFont.getData().setScale(0.5f, 0.5f);
        messageSLayers.font.width(messageSize.cellWidth).height(messageSize.cellHeight).initBySize();


        messageSLayers.setDefaultBackground(unseenColor);

        infoSLayers = new SparseLayers(
            infoSize.gridWidth,
            infoSize.gridHeight,
            infoSize.cellWidth,
            infoSize.cellHeight,
            smallFont);
        infoSLayers.setDefaultBackground(SColor.CW_ALMOST_BLACK);
        infoSLayers.setDefaultForeground(colorCenter.lighter(SColor.CW_PALE_AZURE));

        contextSLayers = new SparseLayers(
            contextSize.gridWidth,
            contextSize.gridHeight,
            contextSize.cellWidth,
            contextSize.cellHeight,
            smallFont);
        contextSLayers.setDefaultBackground(SColor.CW_ALMOST_BLACK);
        contextSLayers.setDefaultForeground(SColor.CW_PALE_LIME);

        TextCellFactory mapFont = defaultFont.copy();
        mapFont.bmpFont.getData().setScale(0.5f, 0.5f);
        mapFont.bmpFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mapFont.width(mapSize.cellWidth).height(mapSize.cellHeight).initBySize();
        mapSLayers = new SparseLayers(
            settings.worldWidth,
            settings.worldHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            mapFont);

        passiveSLayers = new SparseLayers(
            settings.worldWidth,
            settings.worldHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            mapFont);

        mapHoverSLayers = new SparseLayers(settings.worldWidth, settings.worldHeight, mapSize.cellWidth, mapSize.cellHeight, mapFont);

        infoHandler = new InfoHandler(infoSLayers, colorCenter, this);
        contextHandler = new ContextHandler(contextSLayers, mapSLayers, this, config);

        mapOverlaySLayers = new SparseLayers(
            mapSize.gridWidth,
            mapSize.gridHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            mapFont);
        mapOverlaySLayers.setDefaultBackground(colorCenter.desaturate(DB_INK, 0.8));
        mapOverlaySLayers.setDefaultForeground(SColor.LIME);
        mapOverlaySLayers.addLayer();
        mapOverlaySLayers.addLayer();
        mapOverlayHandler = new MapOverlayHandler(mapOverlaySLayers);

        // oversize slightly so that line drawing items meet smoothly
//        smallFont.tweakWidth(infoSize.cellWidth * 1.075f).initBySize();
        smallFont.bmpFont.getData().setScale(0.3125f, 0.3125f);
        smallFont.bmpFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        smallFont.width(infoSize.cellWidth).height(infoSize.cellHeight).initBySize();

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        //mapSLayers.setAnimationDuration(0.145f);
        messageSLayers.setBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        infoSLayers.setBounds(0, 0, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextSLayers.setBounds(0, 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlaySLayers.setBounds(0, 0, mapSize.pixelWidth(), mapSize.pixelWidth());

        mapSLayers.setPosition(0, 0);
        passiveSLayers.setPosition(0, 0);
        mapHoverSLayers.setPosition(0, 0);

        messageViewport.setScreenBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());

        int top = Gdx.graphics.getHeight() - infoSize.pixelHeight();
        infoViewport.setScreenBounds(mapSize.pixelWidth(), top, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport.setScreenBounds(mapSize.pixelWidth(), 0, contextSize.pixelWidth(), contextSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        messageStage.addActor(messageSLayers);
        infoStage.addActor(infoSLayers);
        contextStage.addActor(contextHandler.group);
    }

    public void buildInputProcessors(){
        fallbackKeys = new FallbackKeyHandler(this, config);

        // Lower right
        contextMouse = new SquidMouse(1, 1, new ContextMouseHandler().setContextHandler(contextHandler));
        // Upper right
        infoMouse = new SquidMouse(1, 1, new InfoMouseHandler().setInfoHandler(infoHandler));
        // Lower left
        messageMouse = new SquidMouse(1, 1, new MessageMouseHandler().setEpigon(this));

        contextInput = new SquidInput(contextMouse);
        infoInput = new SquidInput(infoMouse);
        messageInput = new SquidInput(messageMouse);
        fallbackInput = new SquidInput(fallbackKeys);
        multiplexer = new InputSpecialMultiplexer(messageInput, contextInput, infoInput, fallbackInput);
    }

    public static String style(CharSequence text) {
        return GDXMarkup.instance.styleString(text).toString();
    }

    public void startGame() {
        messages.clear();
        mapSLayers.clear();
        mapSLayers.glyphs.clear();
        mapSLayers.clearActions();
        mapHoverSLayers.clear();
        mapHoverSLayers.glyphs.clear();
        mapHoverSLayers.clearActions(); //  not sure if needed
        dataStarter = DataPool.instance().dataStarter;
        mapDecorator = new MapDecorator(dataStarter);

        mapSLayers.addLayer();//first added layer adds at level 1, used for cases when we need "extra background"
        mapSLayers.addLayer();//next adds at level 2, used for the cursor line
        mapSLayers.addLayer();//next adds at level 3, used for effects

        IColoredString<Color> emptyICS = IColoredString.Impl.create();
        for (int i = 0; i <= messageCount; i++) {
            messages.add(emptyICS);
        }

        worldGenerator = new LocalAreaGenerator(mapDecorator);
        castleGenerator = new CastleGenerator(mapDecorator);
        contextHandler.message("Have fun!",
            style("Bump into statues ([*][/]s[,]) and stuff."),
            style("Now [/]90% fancier[/]!"),
            "Use ? for help, or q to quit.",
            "Use numpad or arrow keys to move.");

        initPlayer();
    }

    public void initPlayer() {
        player = RecipeMixer.buildPhysical(dataStarter.playerBlueprint);
        player.visualCondition = new VisualCondition();
        player.stats.get(Stat.VIGOR).set(42.0);
        player.stats.get(Stat.NUTRITION).delta(-0.1);
        player.stats.get(Stat.NUTRITION).min(0);
        player.stats.values().forEach(lv -> lv.max(Double.max(lv.max(), lv.actual())));

        infoHandler.setPlayer(player);
        mapOverlayHandler.setPlayer(player);

        infoHandler.showPlayerHealthAndArmor();
    }

    public void runTurn() {
        OrderedSet<Coord> creaturePositions = creatures.keysAsOrderedSet();
        Coord[] pa = new Coord[]{player.location};
        ArrayList<Coord> path = new ArrayList<>(9);
        for (int i = 0; i < creatures.size(); i++) {
            path.clear();
            final Physical creature = creatures.getAt(i);
            creature.update();
            if (creature.overlayAppearance != null && creature.overlaySymbol == '\uffff') {
                mapSLayers.removeGlyph(creature.overlayAppearance);
                creature.overlayAppearance = null;
            }
            Coord c = creature.location;
            if (creature.stats.get(Stat.MOBILITY).actual() > 0
                && map.lighting.colorLighting[0][player.location.x][player.location.y] > 0.1
                && los.isReachable(map.lighting.resistances, c.x, c.y, player.location.x, player.location.y, Radius.CIRCLE)) {
                Weapon weapon = chooseValidWeapon(creature, player);
                creaturePositions.remove(c);
                monsterDijkstra.reset();
                if (weapon == null) {
                    if (creature.weaponData != null) {
                        ((StatefulRNG) monsterDijkstra.rng).setState(creature.location.hashCode() ^ (long) player.location.hashCode() << 32);
//                        message(creature.name + " has sight " + creature.stats.get(Stat.SIGHT).actual());
//                        monsterDijkstra.findTechniquePath(path, (int) creature.stats.get(Stat.SIGHT).actual(), creature.weaponData.technique, simple, los, creaturePositions, null, c, ps);
                        monsterDijkstra.findAttackPath(path, 1, creature.weaponData.technique.aoe.getMinRange(), creature.weaponData.technique.aoe.getMaxRange(), los, creaturePositions, null, c, pa);
                    }
                } else {
                    ((StatefulRNG) monsterDijkstra.rng).setState(creature.location.hashCode() ^ (long) player.location.hashCode() << 32);
                    monsterDijkstra.findAttackPath(path, 1, weapon.technique.aoe.getMinRange(), weapon.technique.aoe.getMaxRange(), los, creaturePositions, null, c, pa);
//                if(!path.isEmpty())
//                {
//                    messageIndex = Math.max(messages.size(), messageCount);
//                    messages.add(IColoredString.Impl.create(creature.name + " #" + StringKit.hex(((EpiData)creature).hashCode())
//                            + " has a path! " + path.toString(), Color.WHITE));
//                    updateMessages();
//                }
                }
                if (weapon == null && path.isEmpty()) // && monsterDijkstra.targetMap[c.x][c.y] == null
                {
                    Coord next = c.translateCapped(creature.between(-1, 2), creature.between(-1, 2), map.width, map.height);
                    if (!map.creatures.containsKey(next) && map.contents[next.x][next.y].blockage == null) {
                        path.add(next);
                    }
//                    if(map.lighting.fovResult[c.x][c.y] > 0.0) 
//                        message(creature.name + " #" + StringKit.hex(((EpiData)creature).hashCode()) + " moved randomly.");
                }
                if (weapon != null || !path.isEmpty()) {
                    Coord step;
                    if (!path.isEmpty()) {
                        step = path.get(0);
                    } else {
                        step = c;
                    }
                    if (weapon != null) {
                        ActionOutcome ao = ActionOutcome.attack(creature, weapon, player);
                        {
                            Element element = ao.element;
                            if (map.lighting.fovResult[c.x][c.y] > 0.0) {
                                Direction dir = Direction.getDirection(player.location.x - creature.location.x, player.location.y - creature.location.y);
                                fxHandler.attackEffect(creature, player, ao, dir);
                            }
                            if (ao.hit) {
                                int amt = ao.actualDamage >> 1;
                                applyStatChange(player, Stat.VIGOR, amt);
                                amt = -amt; // flip sign for output message
                                if (player.stats.get(Stat.VIGOR).actual() <= 0.0) {
                                    if (ao.crit) {
                                        message(Messaging.transform("The " + creature.name + " [Blood]brutally[] slay$ you with "
                                            + amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                    } else {
                                        message(Messaging.transform("The " + creature.name + " slay$ you with "
                                            + amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                    }
                                } else {
                                    if (ao.crit) {
                                        if (map.lighting.fovResult[c.x][c.y] > 0) {
                                            mapSLayers.wiggle(player.appearance, 0.4f);
                                        }
                                        message(Messaging.transform("The " + creature.name + "[CW Bright Orange] critically[] " + element.verb + " you for "
                                            + amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                    } else {
                                        message(Messaging.transform("The " + creature.name + " " + element.verb + " you for "
                                            + amt + " " + element.styledName + " damage!", creature.name, Messaging.NounTrait.NO_GENDER));
                                    }
                                    if (ao.targetConditioned) {
                                        message(Messaging.transform("The " + creature.name + " "
                                            + ConditionBlueprint.CONDITIONS.getOrDefault(ao.targetCondition, ConditionBlueprint.CONDITIONS.randomValue(creature)).verb + " you with @my attack!", creature.name, Messaging.NounTrait.NO_GENDER));
                                        if (player.overlaySymbol != '\uffff') {
                                            if (player.overlayAppearance != null) {
                                                mapSLayers.removeGlyph(player.overlayAppearance);
                                            }
                                            player.overlayAppearance = mapSLayers.glyph(player.overlaySymbol, player.overlayColor, player.location.x, player.location.y);
                                        }
                                    }
                                }
                            } else {
                                if (ao.crit) {
                                    message("The " + creature.name + " missed you, but just barely.");
                                } else {
                                    message("The " + creature.name + " missed you.");
                                }
                            }
                        }
                    } else {
                        if (creature.creatureData != null
                            && creature.creatureData.lastUsedItem != null
                            && creature.creatureData.lastUsedItem.radiance != null) {
                            creature.creatureData.lastUsedItem.radiance.flare = 0f;
                        }
                        if (map.contents[step.x][step.y].blockage == null && !creatures.containsKey(step) && creatures.alterAtCarefully(i, step) != null) {
                            map.contents[c.x][c.y].remove(creature);
                            if (creature.appearance == null) {
                                System.out.println("runTurn: recreating appearance of " + creature);
                                creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, c.x, c.y);
                                if (creature.overlayAppearance != null && creature.overlaySymbol != '\uffff') {
                                    creature.overlayAppearance = mapSLayers.glyph(creature.overlaySymbol, creature.overlayColor, c.x, c.y);
                                }
                            }
                            if (map.lighting.fovResult[c.x][c.y] > 0) {
                                creature.appearance.setVisible(true);
                            }
                            //creatures.putAt(step, creatures.remove(c), i);

                            creature.location = step;
                            map.contents[step.x][step.y].add(creature);
                            if (creature.appearance != null) {
//                            if (map.lighting.fovResult[c.x][c.y] > 0) {
                                mapSLayers.slide(creature.appearance, c.x, c.y, step.x, step.y, 0.145f, null);
                                if (creature.overlayAppearance != null) {
                                    mapSLayers.slide(creature.overlayAppearance, c.x, c.y, step.x, step.y, 0.145f, null);
                                }
                            }
//                            else if() 
//                            {
//                                creature.appearance.setPosition(mapSLayers.worldX(step.x), mapSLayers.worldY(step.y));
//                                if(creature.overlayAppearance != null)
//                                    creature.overlayAppearance.setPosition(mapSLayers.worldX(step.x), mapSLayers.worldY(step.y));
//                            }
                        }
                    }
                    creaturePositions.add(creature.location);
                }
            }
        }

        // Update all the stats in motion
        OrderedMap<ConstantKey, Double> changes = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
        for (Entry<ConstantKey, LiveValue> entry : player.stats.entrySet()) {
            double amt = entry.getValue().tick();
            if (amt != 0) {
                changes.put(entry.getKey(), amt);
            }
        }
        for (Stat s : Stat.rolloverProcessOrder) {
            double val = player.stats.get(s).actual();
            if (val < 0) {
                player.stats.get(s).actual(0);
                player.stats.get(s.getRollover()).actual(player.stats.get(s.getRollover()).actual() + val);
                changes.merge(s.getRollover(), val, Double::sum);
            }
        }

        infoHandler.updateDisplay(player, changes);
        if (player.stats.get(Stat.VIGOR).actual() <= 0) {
            message("You are now dead with Vigor: " + player.stats.get(Stat.VIGOR).actual());
            batch.setFilter(grayscale);
        }

        if (autoplayTurns > 0) {
            autoplayTurns--;
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    move(rng.getRandomElement(Arrays.stream(Direction.OUTWARDS)
                        .filter(d -> map.contents[player.location.x + d.deltaX][player.location.y + d.deltaY].getLargeNonCreature() == null)
                        .collect(Collectors.toList())
                    ));
                }
            }, 0.2f);
        }
    }

    private void applyStatChange(Physical target, Stat stat, double amount) {
        OrderedMap<ConstantKey, Double> changes = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
        changes.put(stat, amount);
        target.stats.get(stat).addActual(amount);
        for (Stat s : Stat.rolloverProcessOrder) {
            LiveValue lv = target.stats.get(s);
            if (lv == null) {
                continue; // doesn't have this stat so skip it
            }
            double val = lv.actual();
            if (val < 0) {
                target.stats.get(s).actual(0);
                target.stats.get(s.getRollover()).addActual(val);
                changes.merge(s.getRollover(), val, Double::sum);
            }
        }

        infoHandler.updateDisplay(target, changes);

        for (Entry<ConstantKey, Double> entry : changes.entrySet()) {
            double val = entry.getValue();
            SColor color = val >= 0 ? SColor.CW_RICH_JADE : SColor.CW_RED;
            fxHandlerPassive.floatText(target.location, String.format("%.1f %s", val, Utilities.capitalizeFirst(entry.getKey().toString())), color);
        }
    }

    private void clearContents(SparseLayers layers, Color background) {
        layers.clear();
        layers.fillBackground(background);
    }

    private void clearAndBorder(SparseLayers layers, Color borderColor, Color background) {
        clearContents(layers, background);

        int w = layers.getGridWidth();
        int h = layers.getGridHeight();
        for (int x = 0; x < w; x++) {
            layers.put(x, 0, '─', borderColor, background);
            layers.put(x, h - 1, '─', borderColor, background);
        }
        for (int y = 0; y < h; y++) {
            layers.put(0, y, '│', borderColor, background);
            layers.put(w - 1, y, '│', borderColor, background);
        }
        layers.put(0, 0, '┌', borderColor, background);
        layers.put(w - 1, 0, '┐', borderColor, background);
        layers.put(0, h - 1, '└', borderColor, background);
        layers.put(w - 1, h - 1, '┘', borderColor, background);
    }

    public void updateMessages() {
        clearContents(messageSLayers, unseenColor);

        int w = messageSLayers.getGridWidth();
        int h = messageSLayers.getGridHeight();
        for (int x = 0; x < w; x++) {
            messageSLayers.put(x, 0, '─', APRICOT, unseenColor);
            messageSLayers.put(x, h - 1, '─', APRICOT, unseenColor);
        }
        String text = "Click to Scroll ";
        messageSLayers.put(w - 7 - text.length(), 0, text + "↑ ──", APRICOT, unseenColor);
        messageSLayers.put(w - 7 - text.length(), h - 1, text + "↓ ──", APRICOT, unseenColor);
        for (int y = 0; y < h; y++) {
            messageSLayers.put(0, y, '│', APRICOT, unseenColor);
            messageSLayers.put(w - 1, y, '│', APRICOT, unseenColor);
        }
        messageSLayers.put(0, 0, '┌', APRICOT, unseenColor);
        messageSLayers.put(w - 1, 0, '┐', APRICOT, unseenColor);
        messageSLayers.put(0, h - 1, '└', APRICOT, unseenColor);
        messageSLayers.put(w - 1, h - 1, '┘', APRICOT, unseenColor);
        for (int i = messageIndex, c = 0; i >= 0 && c < messageCount; i--, c++) {
            messageSLayers.put(1, messageCount - c, messages.get(i));
        }
    }

    /**
     * @param amount negative to scroll to previous messages, positive for later messages
     */
    public void scrollMessages(int amount) {
        messageIndex = MathUtils.clamp(messageIndex + amount, messageCount, messages.size() - 1);
        updateMessages();
    }

    public void message(String text) {
        messageIndex = Math.max(messages.size(), messageCount);
        messages.add(GDXMarkup.instance.colorString("[WHITE]" + text));
        updateMessages();
    }

    public void calcFOV(int checkX, int checkY) {
        //map.lighting.viewerRange = player.stats.get(Stat.SIGHT).actual();
        // this is really important; it sets the resistances of the map's lighting
        map.opacities();
        //we'll search for lights every time we move
        map.lighting.lights.clear();
        Radiance radiance;
        // adds the most relevant Radiance in each cell to the collection of lights in map.lighting
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                radiance = map.contents[x][y].getAnyRadiance();
                if (radiance != null) {
                    map.lighting.addLight(x, y, radiance);
                }
            }
        }
        map.lighting.calculateFOV(checkX, checkY,
            checkX - 1 - (mapSize.gridWidth >>> 1), checkY - 1 - (mapSize.gridHeight >>> 1),
            checkX + 1 + (mapSize.gridWidth >>> 1), checkY + 1 + (mapSize.gridHeight >>> 1));
        FOV.addFOVsInto(map.lighting.fovResult, FOV.reuseFOV(map.lighting.resistances, map.lighting.tempFOV,
            checkX, checkY, 4.0, Radius.CIRCLE));

        if (config.debugConfig.debugActive && config.debugConfig.odinView) {
            //choice of tempFOV is arbitrary; we just need a 2D array of all 0.6
            ArrayTools.fill(map.lighting.tempFOV, 0.6);
            //makes tempColorLighting filled with 0.6-strength white light
            SColor.colorLightingInto(map.lighting.tempColorLighting, map.lighting.tempFOV, FLOAT_WHITE);
            //mixes the full screen of 0.6-strength white light with existing lights
            SColor.mixColoredLighting(map.lighting.colorLighting, map.lighting.tempColorLighting);
            for (int x = 0; x < map.width; x++) {
                for (int y = 0; y < map.height; y++) {
                    //all of colorLighting will be lit now, so all of fovResult will have a value greater than 0.
                    map.lighting.fovResult[x][y]
                        = MathUtils.clamp(map.lighting.fovResult[x][y] + map.lighting.colorLighting[0][x][y], 0, 1);
                }
            }
            map.seen.allOn();
            ArrayTools.insert(lineDungeon, prunedDungeon, 0, 0);
        } else {
            map.seen.or(map.tempSeen.refill(map.lighting.fovResult, 0.0001, Double.POSITIVE_INFINITY));
            LineKit.pruneLines(lineDungeon, map.seen, LineKit.lightAlt, prunedDungeon);
        }

        Physical creature;
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                if (map.lighting.fovResult[x][y] > 0) {
                    if (map.remembered[x][y] == null) {
                        map.remembered[x][y] = new RememberedTile(map.contents[x][y]);
                    } else {
                        map.remembered[x][y].remake(map.contents[x][y]);
                    }
                    if (config.debugConfig.debugActive && config.debugConfig.odinView) {
                        map.remembered[x][y].seenInDebug = true;
                    }
                    if ((creature = creatures.get(Coord.get(x, y))) != null) {
                        if (creature.appearance == null) {
                            creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, x, y);
                        }
                        creature.appearance.setVisible(true);
                    }
                } else if ((creature = creatures.get(Coord.get(x, y))) != null && creature.appearance != null
                    && creature.appearance.isVisible()) {
                    creature.appearance.setVisible(false);
                    if (creature.overlayAppearance != null) {
                        creature.overlayAppearance.setVisible(false);
                    }
                }
            }
        }
    }

    public void calcDijkstra() {
        toPlayerDijkstra.clearGoals();
        toPlayerDijkstra.resetMap();
        monsterDijkstra.clearGoals();
        monsterDijkstra.resetMap();
        blockage.refill(map.lighting.fovResult, 0.0);
        //blockage.fringe8way(); // seems like this is preventing the player from clicking into tiles they can see at the edge of light
        toPlayerDijkstra.setGoal(player.location);
        toPlayerDijkstra.scan(blockage);
    }

    public void scheduleMove(Direction dir) {
        awaitedMoves.add(player.location.translate(dir));
    }

    public void buildAttackOptions(Physical target) {
        currentTarget = target;
        maneuverOptions.clear();

        for (Weapon w : validAttackOptions(player, target)) {
            maneuverOptions.put(w.rawWeapon.name + " Attack", w);
            for (int j = 0; j < w.maneuvers.size(); j++) {
                maneuverOptions.put(w.rawWeapon.name + ' ' + w.maneuvers.get(j), w);
            }
        }
    }

    public Weapon chooseValidWeapon(Physical attacker, Physical target) {
        List<Weapon> weapons = validAttackOptions(attacker, target);
        return weapons == null || weapons.isEmpty() ? null : rng.getRandomElement(weapons);
    }

    public List<Weapon> validAttackOptions(Physical attacker, Physical target) {
        if (attacker == null || attacker.creatureData == null || attacker.creatureData.weaponChoices == null || target == null) {
            return null;
        }

        Arrangement<Weapon> table = attacker.creatureData.weaponChoices.table;
        List<Weapon> weapons = new ArrayList<>(table.keySet().size());
        double range;
        for (Weapon w : table.keySet()) {
            range = Radius.CIRCLE.radius(attacker.location, target.location);
            if ((w.shape == Weapon.ARC || w.shape == Weapon.BURST)) {
                if (range < 2.5) {
                    continue;
                }
                if (range <= w.rawWeapon.range + 2.5) {
                    weapons.add(w);
                }
            } else if (range <= w.rawWeapon.range + 1.5) {
                weapons.add(w);
            }
        }
        return weapons;
    }

    public void showAttackOptions(Physical target, OrderedMap<String, Weapon> options) {
        int sz = options.size(), len = 0;
        for (int i = 0; i < sz; i++) {
            len = Math.max(options.keyAt(i).length(), len);
        }
        int startY = MathUtils.clamp(target.location.y - (sz >> 1), 0, map.height - sz - 1),
            startX = target.location.x + 2;
        final float smoke = SColor.DB_DARK_LEATHER.toFloatBits();//-0x1.fefefep125F;//SColor.CW_GRAY

        if (target.location.x + len + 1 < map.width) {
            for (int i = 0; i < sz; i++) {
                String name = options.keyAt(i);
                for (int j = 0; j < len; j++) {
                    mapHoverSLayers.put(startX + j, startY + i, smoke);
                }
                mapHoverSLayers.put(startX, startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null);
            }
        } else {
            startX = target.location.x - len;
            for (int i = 0; i < sz; i++) {
                String name = options.keyAt(i);
                for (int j = 0; j < len; j++) {
                    mapHoverSLayers.put(startX + j, startY + i, smoke);
                }
                mapHoverSLayers.put(target.location.x - name.length(), startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null);
            }
        }
        showingMenu = true;
        menuLocation = Coord.get(startX - 2, startY);
        System.out.println("Target at " + target.location + " menu at " + menuLocation); // not sure why menu isn't visible, it's near the target...
    }

    public Coord showInteractOptions(Physical interactable, Physical user, Coord target, EpiMap map) {
        if (interactable.interactableData == null || interactable.interactableData.isEmpty()) {
            return null;
        }
        mapOverlaySLayers.clear(1);
        mapOverlaySLayers.clear(2);
        int sz = interactable.interactableData.size(), len = 0;
        for (int i = 0; i < sz; i++) {
            len = Math.max(interactable.interactableData.get(i).verb.length(), len);
        }
        len += 2;
        int startY = MathUtils.clamp(target.y - (sz >> 1), 0, map.height - sz - 1),
            startX = target.x + 2;
        final float smoke = SColor.DB_DARK_LEATHER.toFloatBits();//-0x1.fefefep125F;//SColor.CW_GRAY
        final float highlight = SColor.lerpFloatColors(
            smoke,
            AURORA_EMBERS.toFloatBits(),
            NumberTools.zigzag(TimeUtils.timeSinceMillis(Epigon.startMillis) * 0x1.2p-9f) * 0x3p-4f + 0x5p-4f);
        int sub = mapOverlayHandler.getSubselection() == null ? -1 : mapOverlayHandler.getSubselection().y;
        if (target.x + len + 1 < map.width) {
            for (int i = 0; i < sz; i++) {
                String name = interactable.interactableData.get(i).verb;
                for (int j = 0; j < len; j++) {
                    mapOverlaySLayers.put(startX + j, startY + i, '\0', i == sub ? highlight : smoke, 0f, 1);
                }
                if (i == sub) {
                    mapOverlaySLayers.put(startX, startY + i, '→', SColor.CW_BRIGHT_GREEN, null, 2);
                }
                mapOverlaySLayers.put(startX + 2, startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null, 2);
            }
        } else {
            startX = target.x - len + 2;
            for (int i = 0; i < sz; i++) {
                String name = interactable.interactableData.get(i).verb;
                for (int j = 0; j < len; j++) {
                    mapOverlaySLayers.put(startX + j, startY + i, '\0', i == sub ? highlight : smoke, 0f, 1);
                }
                if (i == sub) {
                    mapOverlaySLayers.put(startX, startY + i, '→', SColor.CW_BRIGHT_GREEN, null, 2);
                }
                mapOverlaySLayers.put(target.x - name.length() + 2, startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null, 2);
            }
        }
        showingMenu = true;
        return Coord.get(startX, startY);
    }

    public void buildInteractOptions(Physical interactable) {
        interactionOptions.clear();
        if (interactable.interactableData == null || interactable.interactableData.isEmpty()) {
            return;
        }
        for (Interactable inter : interactable.interactableData) {
            interactionOptions.put(inter.verb, inter);
        }
    }

    public void attack(Physical target) {
        attack(target, chooseValidWeapon(player, target));
    }

    public void attack(Physical target, Weapon choice) {
        int targetX = target.location.x, targetY = target.location.y;
        if (target == player){
            message("It's not wise to attack yourself!");
            return;
        }

        ActionOutcome ao = ActionOutcome.attack(player, choice, target);
        Element element = ao.element;
        Direction dir = Direction.getDirection(target.location.x - player.location.x, target.location.y - player.location.y);

        calcFOV(player.location.x, player.location.y);
        fxHandler.attackEffect(player, target, ao, dir); // TODO - tie creature glyph removal to appropriate moment in attack effect

        if (ao.hit) {
            applyStatChange(target, Stat.VIGOR, ao.actualDamage);
            if (target.stats.get(Stat.VIGOR).actual() <= 0) {
                if (target.appearance != null) {
                    mapSLayers.removeGlyph(target.appearance);
                    target.appearance = null;
                }
                if (target.overlayAppearance != null) {
                    mapSLayers.removeGlyph(target.overlayAppearance);
                    target.overlayAppearance = null;
                }
                creatures.remove(target.location);
                map.contents[targetX][targetY].remove(target);
                if (ao.crit) {
                    Stream.concat(target.physicalDrops.stream(), target.elementDrops.getOrDefault(element, new ArrayList<>(0)).stream())
                        .map(table -> {
                            int quantity = table.quantity();
                            Physical p = RecipeMixer.buildPhysical(table.random());
                            if (p.groupingData != null) {
                                p.groupingData.quantity += quantity;
                            } else {
                                p.groupingData = new Grouping(quantity);
                            }
                            return p;
                        })
                        .forEach(item -> {
                            if (item.attached) {
                                return;
                            }
                            map.contents[targetX][targetY].add(item);
                            int tx = MathUtils.clamp(targetX + player.between(-1, 2), 0, config.settings.worldWidth - 1),
                                ty = MathUtils.clamp(targetY + player.between(-1, 2), 0, config.settings.worldHeight - 1);
                            if (map.lighting.resistances[tx][ty] < 0.9) {
                                map.contents[tx][ty].add(item);
                            }
                        });
                    if (target.appearance != null && target.appearance.isVisible()) {
                        mapSLayers.burst(targetX, targetY, 1, Radius.CIRCLE, target.appearance.shown, target.color, SColor.translucentColor(target.color, 0f), 1);
                    }
                    message("You [Blood]brutally[] defeat the " + target.name + " with " + -ao.actualDamage + " " + element.styledName + " damage!");
                } else {
                    Stream.concat(target.physicalDrops.stream(), target.elementDrops.getOrDefault(element, new ArrayList<>()).stream())
                        .map(table -> {
                            int quantity = table.quantity();
                            Physical p = RecipeMixer.buildPhysical(table.random());
                            if (p.groupingData != null) {
                                p.groupingData.quantity += quantity;
                            } else {
                                p.groupingData = new Grouping(quantity);
                            }
                            return p;
                        })
                        .forEach(item -> {
                            if (item.attached) {
                                return;
                            }
                            map.contents[targetX][targetY].add(item);
                        });
                    if (target.appearance != null && target.appearance.isVisible()) {
                        mapSLayers.burst(targetX, targetY, 1, Radius.CIRCLE, target.appearance.shown, target.color, SColor.translucentColor(target.color, 0f), 1);
                    }
                    message("You defeat the " + target.name + " with " + -ao.actualDamage + " " + element.styledName + " damage!");
                }
            } else {
                String amtText = String.valueOf(-ao.actualDamage);
                if (ao.crit) {
                    if (target.appearance != null && target.appearance.isVisible()) {
                        mapSLayers.wiggle(0.0f, target.appearance, 0.4f, () -> target.appearance.setPosition(
                            mapSLayers.worldX(target.location.x), mapSLayers.worldY(target.location.y)));
                    }
                    message(Messaging.transform("You [CW Bright Orange]critically[] " + element.verb + " the " + target.name + " for "
                        + amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                } else {
                    message(Messaging.transform("You " + element.verb + " the " + target.name + " for "
                        + amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                }
                if (ao.targetConditioned) {
                    message(Messaging.transform("You "
                        + ConditionBlueprint.CONDITIONS.getOrDefault(ao.targetCondition, ConditionBlueprint.CONDITIONS.randomValue(player)).verb
                        + " the " + target.name + " with your attack!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                    if (target.overlaySymbol != '\uffff') {
                        if (target.overlayAppearance != null) {
                            mapSLayers.removeGlyph(target.overlayAppearance);
                        }
                        target.overlayAppearance = mapSLayers.glyph(target.overlaySymbol, target.overlayColor, targetX, targetY);
                    }
                }
            }
        } else {
            message("Missed the " + target.name + (ao.crit ? ", but just barely." : "..."));
        }
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     */
    public void move(Direction dir) {
        player.update();
        if (player.overlayAppearance != null && player.overlaySymbol == '\uffff') {
            mapSLayers.removeGlyph(player.overlayAppearance);
            player.overlayAppearance = null;
        }
        int newX = player.location.x + dir.deltaX;
        int newY = player.location.y + dir.deltaY;
        Coord newPos = Coord.get(newX, newY);
        if (!map.inBounds(newX, newY)) {
            message("You've reached the edge of the world, you can go no further.");
            return;
        }
        map.contents[player.location.x][player.location.y].remove(player);
        if (map.contents[newX][newY].blockage == null) {
            mapSLayers.slide(player.appearance, player.location.x, player.location.y, newX, newY, 0.145f, () -> {
                calcFOV(newX, newY);
                calcDijkstra();
                runTurn();
            });
            if (player.overlayAppearance != null) {
                mapSLayers.slide(player.overlayAppearance, player.location.x, player.location.y, newX, newY, 0.145f, null);
            }
            player.location = newPos;
            map.contents[player.location.x][player.location.y].add(player);
            sound.playFootstep();
        } else {
            Physical thing = map.contents[newX][newY].getCreature();
            if (thing != null) {
                cancelMove();
                attack(thing);
                calcFOV(player.location.x, player.location.y);
                calcDijkstra();
                runTurn();
            } else if (!map.contents[newX][newY].contents.isEmpty()
                && (thing = map.contents[newX][newY].contents.get(0)).interactableData != null
                && !thing.interactableData.isEmpty()
                && thing.interactableData.get(0).bumpAction) {
                cancelMove();
                thing.location = newPos; // total hack; needed by door-opening interaction
                message(Messaging.transform(thing.interactableData.get(0).interaction.interact(player, thing, this),
                    player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                runTurn();
            } else if ((thing = map.contents[newX][newY].getLargeNonCreature()) != null) {
                cancelMove();
                if (thing.interactableData != null && !thing.interactableData.isEmpty() && thing.interactableData.get(0).bumpAction) {
                    thing.location = newPos; // total hack; needed by door-opening interaction
                    message(Messaging.transform(thing.interactableData.get(0).interaction.interact(player, thing, this),
                        player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                } else {
                    message("Ran into " + thing.name);
                }
                runTurn();
            } else {
                runTurn();
            }
        }
    }

    public void cancelMove() {
        awaitedMoves.clear();
        toCursor.clear();
    }

    public void putWithLight(int x, int y, char c, float foreground, float background) {
        foreground = lerpFloatColorsBlended(foreground, map.lighting.colorLighting[1][x][y], map.lighting.colorLighting[0][x][y] * 0.6f);
        mapSLayers.backgrounds[x][y] = SColor.lerpFloatColorsBlended(mapSLayers.backgrounds[x][y], background, 0.3f);
        if (c == '#') {
            wallColors[x][y] = foreground;
        } else {
            mapSLayers.put(x, y, c, foreground); // "dark" theme
        }
    }

    public void toggleDebug() {
        message("Toggling debug.");
        config.debugConfig.debugActive = !config.debugConfig.debugActive;
        if (config.debugConfig.debugActive) {
            if (glp == null) {
                glp = new GLProfiler(Gdx.graphics);
            }
            glp.enable();
        } else {
            setOdinView(false);
            glp.disable();
            Gdx.graphics.setTitle(Config.gameTitle);
        }

        calcFOV(player.location.x, player.location.y);
    }

    public void setOdinView(boolean shouldShow) {
        config.debugConfig.odinView = shouldShow;
        if (shouldShow) {
            // noop for now
        } else {
            map.tempSeen.clear();
            map.seen.clear();
            prunedDungeon = ArrayTools.copy(lineDungeon);
        }
        calcFOV(player.location.x, player.location.y);
    }

    /**
     * This is the time to do any processing that needs to be done each frame before visual rendering.
     */
    public abstract void renderStart();

    /**
     * This is the time to do any processing that needs to be done after panels are drawn to.
     */
    public abstract void renderEnd();

    @Override
    public void render() {
        if (config.debugConfig.debugActive) {
            glp.reset();
        }

        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(unseenColor.r, unseenColor.g, unseenColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderStart();

        // if the user clicked, we have a list of moves to perform.
        if (!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Points from awaitedMoves.
            if (!mapSLayers.hasActiveAnimations()) {
                if (player.creatureData.lastUsedItem != null && player.creatureData.lastUsedItem.radiance != null && player.creatureData.lastUsedItem.radiance.flare > 0f) {
                    player.creatureData.lastUsedItem.radiance.flare = 0f;
                }
                Coord m = awaitedMoves.remove(0);
                if (!toCursor.isEmpty()) {
                    toCursor.remove(0);
                }
                move(Direction.toGoTo(player.location, m));
                infoHandler.updateDisplay();
            }
        } else {
            multiplexer.process();
        }

        // the order here matters. We apply multiple viewports at different times to clip different areas.
        batch.begin();

        contextViewport.apply(false);
        contextStage.act();
        batch.setProjectionMatrix(contextViewport.getCamera().combined);
        contextStage.getRoot().draw(batch, 1);

        infoViewport.apply(false);
        infoStage.act();
        batch.setProjectionMatrix(infoViewport.getCamera().combined);
        infoStage.getRoot().draw(batch, 1);

        if (config.debugConfig.debugActive) {
            int drawCalls = glp.getDrawCalls();
            int textureBindings = glp.getTextureBindings();
            int calls = glp.getCalls();
            int switches = glp.getShaderSwitches();
            tempSB.setLength(0);
            tempSB.append(Config.gameTitle)
                .append(" - Debug");
            if (config.debugConfig.odinView) {
                tempSB.append(" Odin");
            }
            tempSB.append(" -")
                .append(" Draw Calls: ").append(drawCalls)
                .append(" Calls: ").append(calls)
                .append(" Texture Binds: ").append(textureBindings)
                .append(" Shader Switches: ").append(switches)
                .append(" Lights: ").append(map.lighting.lights.size())
                .append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
            Gdx.graphics.setTitle(tempSB.toString());
        }

        messageViewport.apply(false);
        messageStage.act();
        batch.setProjectionMatrix(messageViewport.getCamera().combined);
        messageStage.getRoot().draw(batch, 1);

        renderEnd();
        batch.end();
    }

    /**
     * This is used to resize components that are specific to a game mode and not general purpose.
     * @param width
     * @param height
     */
    public void internalResize(int width, int height){}

    @Override
    public void resize(int width, int height) {
        // if not going to fullscreen, probably
        if (Gdx.graphics.getDisplayMode().width != width && !config.displayConfig.maximized) {
            config.displayConfig.windowWidth = width;
            config.displayConfig.windowHeight = height;
            config.displayConfig.monitorName = Gdx.graphics.getMonitor().name;
        }
        super.resize(width, height);

        float currentZoomX = (float) width / config.displayConfig.defaultPixelWidth();
        float currentZoomY = (float) height / config.displayConfig.defaultPixelHeight();

        mapSLayers.setBounds(0, 0, currentZoomX * mapSize.pixelWidth(), currentZoomY * mapSize.pixelHeight());
        mapOverlaySLayers.setBounds(0, 0, currentZoomX * mapSize.pixelWidth(), currentZoomY * mapSize.pixelHeight());
        messageSLayers.setBounds(0, 0, currentZoomX * messageSize.pixelWidth(), currentZoomY * messageSize.pixelHeight());
        contextSLayers.setBounds(0, 0, currentZoomX * contextSize.pixelWidth(), currentZoomY * contextSize.pixelHeight());
        infoSLayers.setBounds(0, 0, currentZoomX * infoSize.pixelWidth(), currentZoomY * infoSize.pixelHeight());

        // SquidMouse turns screen positions to cell positions, and needs to be told that cell sizes have changed
        internalResize(width, height); // make sure game-specific items are resized
        // Bottom Left
        messageViewport.update(width, height, false);
        int x = 0;
        int y = 0;
        int pixelWidth = (int) (currentZoomX * messageSize.pixelWidth());
        int pixelHeight = (int) (currentZoomY * messageSize.pixelHeight());
        messageViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        float cellWidth = currentZoomX * messageSize.cellWidth;
        float cellHeight = currentZoomY * messageSize.cellHeight;
        int offsetX = 0;
        int offsetY = (int) (height - cellHeight * messageSize.gridHeight);
        messageMouse.reinitialize(cellWidth, cellHeight, messageSize.gridWidth, messageSize.gridHeight, -offsetX, -offsetY);

        // Top Right
        x = (int) (currentZoomX * mapSize.pixelWidth());
        y = (int) (height - infoSize.pixelHeight() * currentZoomY); // isn't pinned to Context panel below it
        pixelWidth = (int) (currentZoomX * infoSize.pixelWidth());
        pixelHeight = (int) (currentZoomY * infoSize.pixelHeight());
        infoViewport.update(width, height, false);
        infoViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        cellWidth = currentZoomX * infoSize.cellWidth;
        cellHeight = currentZoomY * infoSize.cellHeight;
        offsetX = x;
        offsetY = 0;
        infoMouse.reinitialize(cellWidth, cellHeight, infoSize.gridWidth, infoSize.gridHeight, -offsetX, -offsetY);

        // Bottom Right
        x = (int) (currentZoomX * mapSize.pixelWidth());
        y = 0;
        pixelWidth = (int) (currentZoomX * contextSize.pixelWidth());
        pixelHeight = (int) (currentZoomY * contextSize.pixelHeight());
        contextViewport.update(width, height, false);
        contextViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        cellWidth = currentZoomX * contextSize.cellWidth;
        cellHeight = currentZoomY * contextSize.cellHeight;
        offsetX = x;
        offsetY = (int) (height - cellHeight * contextSize.gridHeight); // isn't pinned to Info panel above it
        contextMouse.reinitialize(cellWidth, cellHeight, contextSize.gridWidth, contextSize.gridHeight, -offsetX, -offsetY);

        // save current settings
        config.displayConfig.windowWidth = width;
        config.displayConfig.windowHeight = height;
        config.saveDisplay();
    }

    @Override
    public void pause() {
        System.out.println("Pausing game.");
        super.pause();
    }

    @Override
    public void resume() {
        System.out.println("Resuming game.");
        super.resume();
    }

    @Override
    public void dispose() {
        System.out.println("Disposing game.");
        System.out.println("Saving current configs.");
        config.saveAll();
        System.out.println("Finished saving configs.");
        super.dispose();
    }

    public void exit(){
        Gdx.app.exit();
    }
}
