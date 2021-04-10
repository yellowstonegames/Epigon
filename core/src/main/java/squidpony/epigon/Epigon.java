package squidpony.epigon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import squidpony.ArrayTools;
import squidpony.Messaging;
import squidpony.StringKit;
import squidpony.panel.IColoredString;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.*;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.*;

import squidpony.epigon.combat.ActionOutcome;
import squidpony.epigon.data.*;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.control.DataStarter;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.raw.RawCreature;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.display.*;
import squidpony.epigon.files.Config;
import squidpony.epigon.files.Settings;
import squidpony.epigon.input.key.*;
import squidpony.epigon.input.mouse.*;
import squidpony.epigon.mapping.*;
import squidpony.epigon.util.Utilities;

import static squidpony.squidgrid.gui.gdx.SColor.*;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {

    public final StatefulRNG rng;
    // meant to be used to generate seeds for other RNGs; can be seeded when they should be fixed
    public static final DiverRNG rootChaos = new DiverRNG();
    public RecipeMixer mixer;
    public DataStarter dataStarter;
    private MapDecorator mapDecorator;
//    public static final char BOLD = '\0', ITALIC = '\0', REGULAR = '\0';
    public static final char BOLD = '\u4000', ITALIC = '\u8000', REGULAR = '\0';

    private GameMode mode;
    private Config config;

    // Audio
    private SoundManager sound;

    // Display
    private FilterBatch batch;
    private SquidColorCenter colorCenter;
    public static final FloatFilters.YCwCmFilter filter = new FloatFilters.YCwCmFilter(0.9f, 1.3f, 1.3f);
    public static final FloatFilter
            //identityFilter = new FloatFilters.IdentityFilter(),
            grayscale = new FloatFilters.YCwCmFilter(0.75f, 0.2f, 0.2f);
    private SparseLayers mapSLayers;
    private SparseLayers passiveSLayers;
    public SparseLayers mapHoverSLayers;
    public SparseLayers mapOverlaySLayers;
    private SparseLayers infoSLayers;
    private SparseLayers contextSLayers;
    private SparseLayers messageSLayers;
//    private ShapeRenderer shaper;
    private SubcellLayers fallingSLayers;

    public InputSpecialMultiplexer multiplexer;
    public SquidInput mapInput;
    public SquidInput contextInput;
    public SquidInput infoInput;
    public SquidInput messageInput;
    public SquidInput debugInput;
    public SquidInput fallbackInput;

    private Color unseenColor;
    private float unseenCreatureColorFloat;
    public ArrayList<Coord> toCursor;
    private TextCellFactory font;

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

    // World
    private LocalAreaGenerator worldGenerator;
    private CastleGenerator castleGenerator;
    public EpiMap[] world;
    public EpiMap map;
    public char[][] simple;
    public char[][] lineDungeon, prunedDungeon;
    public float[][] wallColors, walls;
    
    private int messageCount;
    public int depth;
    public FxHandler fxHandler;
    public FxHandler fxHandlerPassive;
    public MapOverlayHandler mapOverlayHandler;
    public ContextHandler contextHandler;
    public InfoHandler infoHandler;
    public FallingHandler fallingHandler;
    private GreasedRegion blockage, floors;
    public DijkstraMap toPlayerDijkstra, monsterDijkstra;
    public LOS los;
    public Coord cursor;
    public Physical player;
    public ArrayList<Coord> awaitedMoves;
    public OrderedMap<Coord, Physical> creatures;
    private int autoplayTurns = 0;

    // Timing
    public static final long startMillis = TimeUtils.millis();
    private long fallDelay = 300;
    public Instant nextFall = Instant.now();
    public boolean paused = true;
    public Instant pausedAt = Instant.now();
    private Instant unpausedAt = Instant.now();
    public long inputDelay = 100;
    public Instant nextInput = Instant.now();
    private long fallDuration = 0L, currentFallDuration = 0L;

    private Stage mapStage, messageStage, infoStage, contextStage, mapOverlayStage, fallingStage;
    private Viewport mapViewport, messageViewport, infoViewport, contextViewport, mapOverlayViewport, fallingViewport;

    public float startingY, finishY, timeToFall;

    private GLProfiler glp;
    private StringBuilder tempSB = new StringBuilder(16);
    private Vector2 screenPosition = new Vector2(20, 20);
    public static final Radiance[] softWhiteChain = Radiance.makeChain(8, 1.2f, SColor.FLOAT_WHITE, 0.4f);

    // input handlers
    public EpigonKeyHandler mapKeys;
    public EpigonKeyHandler fallbackKeys;
    public EpigonKeyHandler equipmentKeys;
    public EpigonKeyHandler helpKeys;
    public EpigonKeyHandler fallingKeys;
    public EpigonKeyHandler fallingGameOverKeys;
    public EpigonKeyHandler debugKeys;
    public SquidMouse equipmentMouse;
    public SquidMouse helpMouse;
    public SquidMouse fallingMouse;
    public SquidMouse mapMouse;
    public SquidMouse contextMouse;
    public SquidMouse infoMouse;
    public SquidMouse messageMouse;

    public Epigon(Config config) {
        this.config = config;
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
        System.out.println(rootChaos.getState());

        Settings settings = config.settings;

        mode = config.settings.mode;
        mapSize = config.settings.mapSize();
        messageSize = config.settings.messageSize();
        infoSize = config.settings.infoSize();
        contextSize = config.settings.contextSize();
        messageCount = config.settings.messageCount();

        mixer = new RecipeMixer();
        //handBuilt = new DataStarter(mixer);
        Weapon.init();
        
        Coord.expandPoolTo(settings.worldGridWidth + 1, Math.max(settings.worldGridHeight, settings.totalGridDepth + MapConstants.DIVE_HEADER.length) + 1);

        // this matches the background color outside the map to the background color of unseen areas inside the map,
        // using the same filter (reducing brightness and saturation using YCwCm) as that stage of the map draw.
        float unseenY = lumaYCwCm(DB_INK) * 0.7f,
            unseenCw = chromaWarm(DB_INK) * 0.65f,
            unseenCm = chromaMild(DB_INK) * 0.65f;
        unseenColor = colorFromFloat(floatGetYCwCm(unseenY, unseenCw, unseenCm, 1f));
        unseenCreatureColorFloat = SColor.CW_DARK_GRAY.toFloatBits();

        //FilterBatch is new, and automatically filters all text colors and image tints with a FloatFilter
        batch = new FilterBatch(filter);
        // uncomment the line below to see the game with no filters
        //batch = new FilterBatch();

        if (config.debugConfig.debugActive) {
            glp = new GLProfiler(Gdx.graphics);
            glp.enable();
        }

        System.out.println("Putting together display.");
        mapViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());
        messageViewport = new StretchViewport(messageSize.pixelWidth(), messageSize.pixelHeight());
        infoViewport = new StretchViewport(infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport = new StretchViewport(contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlayViewport = new StretchViewport(messageSize.pixelWidth(), mapSize.pixelHeight());
        fallingViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());

        // Here we make sure our Stages, which holds any text-based grids we make, uses our Batch.
        mapStage = new Stage(mapViewport, batch);
        messageStage = new Stage(messageViewport, batch);
        infoStage = new Stage(infoViewport, batch);
        contextStage = new Stage(contextViewport, batch);
        mapOverlayStage = new Stage(mapOverlayViewport, batch);
        fallingStage = new Stage(fallingViewport, batch);
//        font = new TextCellFactory().font("7-12-serif.fnt");
        font = DefaultResources.getCrispLeanFamily();
        font.bmpFont.setFixedWidthGlyphs(Utilities.USABLE_CHARS);
        TextCellFactory smallFont = font.copy();
        smallFont.bmpFont.setFixedWidthGlyphs(Utilities.USABLE_CHARS);
        font.setSmoothingMultiplier(1.5f);
        smallFont.setSmoothingMultiplier(1.5f);
//        TextCellFactory smallFont = new TextCellFactory().font("7-12-serif.fnt");
//        smallFont.bmpFont.setFixedWidthGlyphs(Utilities.USABLE_CHARS);
        //smallFont.bmpFont.getData().scale(2);
//        font = DefaultResources.getCrispLeanFamily();
//        TextCellFactory smallFont = font.copy();

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
            font);

        messageSLayers.setDefaultBackground(unseenColor);

//        shaper = new ShapeRenderer();
/*
                messageSize.gridWidth,
                messageSize.gridHeight,
                messageSize.cellWidth,
                messageSize.cellHeight,

         */
        infoSLayers = new SparseLayers(
            infoSize.gridWidth,
            infoSize.gridHeight,
            infoSize.cellWidth,
            infoSize.cellHeight,
            smallFont);
        infoSLayers.setDefaultBackground(SColor.CW_ALMOST_BLACK);
        infoSLayers.setDefaultForeground(colorCenter.lighter(SColor.CW_PALE_AZURE));
//        infoSLayers.getBackgroundLayer().setDefaultForeground(SColor.CW_ALMOST_BLACK);
//        infoSLayers.getForegroundLayer().setDefaultForeground(colorCenter.lighter(SColor.CW_PALE_AZURE));

        contextSLayers = new SparseLayers(
            contextSize.gridWidth,
            contextSize.gridHeight,
            contextSize.cellWidth,
            contextSize.cellHeight,
            smallFont);
        contextSLayers.setDefaultBackground(SColor.CW_ALMOST_BLACK);
        contextSLayers.setDefaultForeground(SColor.CW_PALE_LIME);
//        contextSLayers.getBackgroundLayer().setDefaultForeground(SColor.CW_ALMOST_BLACK);
//        contextSLayers.getForegroundLayer().setDefaultForeground(SColor.CW_PALE_LIME);

        mapSLayers = new SparseLayers(
            settings.worldGridWidth,
            settings.worldGridHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            font.copy().width(mapSize.cellWidth).height(mapSize.cellHeight).initBySize());

        passiveSLayers = new SparseLayers(
            settings.worldGridWidth,
            settings.worldGridHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            mapSLayers.font);

        mapHoverSLayers = new SparseLayers(settings.worldGridWidth, settings.worldGridHeight, messageSize.cellWidth, mapSize.cellHeight, font);

        infoHandler = new InfoHandler(infoSLayers, colorCenter, this);
        contextHandler = new ContextHandler(contextSLayers, mapSLayers, this);

        mapOverlaySLayers = new SparseLayers(
            messageSize.gridWidth,
            mapSize.gridHeight,
            mapSize.cellWidth,
            mapSize.cellHeight,
            font);
        mapOverlaySLayers.setDefaultBackground(colorCenter.desaturate(DB_INK, 0.8));
        mapOverlaySLayers.setDefaultForeground(SColor.LIME);
        mapOverlaySLayers.addLayer();
        mapOverlaySLayers.addLayer();
        mapOverlayHandler = new MapOverlayHandler(mapOverlaySLayers);

        fallingSLayers = new SubcellLayers(
            100, // weird because falling uses a different view
            settings.totalGridDepth,
            mapSize.cellWidth,
            mapSize.cellHeight,
            font);
        fallingSLayers.setDefaultBackground(colorCenter.desaturate(DB_INK, 0.8));
        fallingSLayers.setDefaultForeground(SColor.LIME);
        fallingHandler = new FallingHandler(fallingSLayers);
        mapSLayers.font.tweakWidth(15f).tweakHeight(28f).initBySize();
        font.tweakWidth(messageSize.cellWidth * 1.1f).initBySize();
        smallFont.tweakWidth(infoSize.cellWidth * 1.075f).initBySize();

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        //mapSLayers.setAnimationDuration(0.145f);

        messageSLayers.setBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        infoSLayers.setBounds(0, 0, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextSLayers.setBounds(0, 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlaySLayers.setBounds(0, 0, mapSize.pixelWidth(), mapSize.pixelWidth());
        fallingSLayers.setPosition(0, 0);
        mapSLayers.setPosition(0, 0);
        passiveSLayers.setPosition(0, 0);
        mapHoverSLayers.setPosition(0, 0);

        messageViewport.setScreenBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        int top = Gdx.graphics.getHeight() - mapSize.pixelHeight(); // messageSize.pixelHeight(
        mapViewport.setScreenBounds(0, top, mapSize.pixelWidth(), mapSize.pixelHeight());
        top = Gdx.graphics.getHeight() - infoSize.pixelHeight(); // contextSize.pixelHeight()
        infoViewport.setScreenBounds(mapSize.pixelWidth(), top, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport.setScreenBounds(mapSize.pixelWidth(), 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlayViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());
        fallingViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        // set up input handlers
        mapKeys = new MapKeyHandler().setEpigon(this);
        fallbackKeys = new FallbackKeyHandler().setEpigon(this).setConfig(config);
        equipmentKeys = new EquipmentKeyHandler().setEpigon(this);
        helpKeys = new HelpKeyHandler().setEpigon(this);
        fallingKeys = new FallingKeyHandler().setEpigon(this);
        fallingGameOverKeys = new FallingGameOver().setEpigon(this);
        debugKeys = new DebugKeyHandler().setEpigon(this).setConfig(config);

        // NOTE - a resize() operation is called after everything is initialized, so below values are set to min values for simplicity
        // Upper left
        equipmentMouse = new SquidMouse(1, 1, new EquipmentMouseHandler().setEpigon(this));
        helpMouse = new SquidMouse(1, 1, new HelpMouseHandler());
        fallingMouse = new SquidMouse(1, 1, new FallingMouseHandler());
        mapMouse = new SquidMouse(1, 1, new MapMouseHandler().setEpigon(this));
        // Lower right
        contextMouse = new SquidMouse(1, 1, new ContextMouseHandler().setContextHandler(contextHandler));
        // Upper right
        infoMouse = new SquidMouse(1, 1, new InfoMouseHandler().setInfoHandler(infoHandler));
        // Lower left
        messageMouse = new SquidMouse(1, 1, new MessageMouseHandler().setEpigon(this));

        mapInput = new SquidInput(mapKeys, mapMouse);
        contextInput = new SquidInput(contextMouse);
        infoInput = new SquidInput(infoMouse);
        messageInput = new SquidInput(messageMouse);
        debugInput = new SquidInput(debugKeys);
        fallbackInput = new SquidInput(fallbackKeys);
        multiplexer = new InputSpecialMultiplexer(mapInput, messageInput, contextInput, infoInput, debugInput, fallbackInput); //mapStage, messageStage, 
        Gdx.input.setInputProcessor(multiplexer);

        mapStage.addActor(mapSLayers);
        mapStage.addActor(passiveSLayers);
        mapStage.addActor(mapHoverSLayers);
        mapOverlayStage.addActor(mapOverlaySLayers);
        fallingStage.addActor(fallingSLayers);
        messageStage.addActor(messageSLayers);
        infoStage.addActor(infoSLayers);
        contextStage.addActor(contextHandler.group);

        fallingStage.getCamera().position.y = startingY = fallingSLayers.worldY(mapSize.gridHeight >> 1);
        finishY = fallingSLayers.worldY(settings.totalGridDepth);
        timeToFall = Math.abs(finishY - startingY) * fallDelay / mapSize.cellHeight;
//        lightLevels = new float[76];
//        float initial = lerpFloatColors(RememberedTile.memoryColorFloat, -0x1.7583e6p125F, 0.4f); // the float is SColor.AMUR_CORK_TREE
//        for (int i = 0; i < 12; i++) {
//            lightLevels[i] = lerpFloatColors(initial, -0x1.7583e6p125F, Interpolation.sineOut.apply(i / 12f)); // AMUR_CORK_TREE again
//        }
//        for (int i = 0; i < 64; i++) {
//            lightLevels[12 + i] = lerpFloatColors(-0x1.7583e6p125F, -0x1.fff1ep126F, Interpolation.sineOut.apply(i / 63f)); // AMUR_CORK_TREE , then ALICE_BLUE
//        }

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // force a refresh to align all the windows and listeners

        startGame();
    }

    public static String style(CharSequence text) {
//        return text.toString();
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
//        mapSLayers.addLayer();//level 3, backgrounds for hovering menus
//        mapSLayers.addLayer();//level 4, text for hovering menus
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

        prepCrawl();
        putCrawlMap();
//        prepFall();
        //message("Starting with " + EpiData.count + " EpiData instances.");
    }

    public void initPlayer() {
        player = RecipeMixer.buildPhysical(dataStarter.playerBlueprint);
        player.stats.get(Stat.VIGOR).set(42.0);
        player.stats.get(Stat.NUTRITION).delta(-0.1);
        player.stats.get(Stat.NUTRITION).min(0);
        //player.stats.get(Stat.DEVOTION).actual(player.stats.get(Stat.DEVOTION).base() * 1.7);
        player.stats.values().forEach(lv -> lv.max(Double.max(lv.max(), lv.actual())));
        
        infoHandler.setPlayer(player);
        mapOverlayHandler.setPlayer(player);
        fallingHandler.setPlayer(player);
        
        infoHandler.showPlayerHealthAndArmor();
    }

    public void prepFall() {
        message("Falling..... Press SPACE to continue");
        int w = MapConstants.DIVE_HEADER[0].length();
        WobblyCanyonGenerator wcg = new WobblyCanyonGenerator(mapDecorator);
        map = wcg.buildDive(worldGenerator.buildWorld(w, 23, config.settings.totalGridDepth), w, config.settings.totalGridDepth);
        contextHandler.setMap(map, world);

        // Start out in the horizontal middle and visual a bit down
        player.location = Coord.get(w / 2, 0); // for... reasons, y is an offset from the camera position
        fallDuration = 0;
        mode = GameMode.DIVE;
        mapInput.flush();
        mapInput.setRepeatGap(Long.MAX_VALUE);
        mapInput.setKeyHandler(fallingKeys);
        mapInput.setMouse(fallingMouse);
        fallingHandler.show(map);

        paused = true;
        nextFall = Instant.now().plusMillis(fallDelay);
        pausedAt = Instant.now();
    }
    
    private void setupLevel() {
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                if (map.contents[x][y] == null) {
                    map.contents[x][y] = new EpiTile();
                }
            }
        }

        simple = map.simpleChars();
        lineDungeon = map.line;
        prunedDungeon = ArrayTools.copy(lineDungeon);
        wallColors = new float[map.width][map.height];
        walls = MapUtility.generateLinesToBoxes(prunedDungeon, wallColors);
        floors.refill(map.opacities(), 0.999);

        if (map.populated) {
            return;
        }
        map.populated = true;

        GreasedRegion floors2 = floors.copy();
        floors2.andNot(map.downStairPositions).andNot(map.upStairPositions);
        floors2.copy().randomScatter(rng, 9)
            .stream()
            .filter(c -> map.contents[c.x][c.y].floor != null) // TODO - allow flying/floating objects
            .forEach(c -> {
                map.contents[c.x][c.y].add(RecipeMixer.applyModification(
                    RecipeMixer.buildWeapon(Weapon.randomPhysicalWeapon(player).copy(), player),
                    player.getRandomElement(Element.allEnergy).weaponModification()));
            });
        floors2.randomScatter(rng, 16);
        for (Coord coord : floors2) {
            if (map.contents[coord.x][coord.y].blockage == null) {
                if (map.contents[coord.x][coord.y].floor == null) {
                    continue; // TODO - allow spawning of flying things
                }
                //Physical p = RecipeMixer.buildPhysical(GauntRNG.getRandomElement(rootChaos.nextLong(), Inclusion.values()));
                //RecipeMixer.applyModification(p, handBuilt.makeAlive());
                Physical p = RecipeMixer.buildCreature(RawCreature.ENTRIES[
                        rootChaos.nextInt(RawCreature.ENTRIES.length)
                        ]);
                p.color = Utilities.progressiveLighten(p.color);
                Physical pMeat = RecipeMixer.buildPhysical(p);
                RecipeMixer.applyModification(pMeat, dataStarter.makeMeats());
                Physical[] held = new Physical[p.creatureData.equippedDistinct.size() + 1];
                p.creatureData.equippedDistinct.toArray(held);
                held[held.length - 1] = pMeat;
                double[] weights = new double[held.length];
                Arrays.fill(weights, 1.0);
                weights[held.length - 1] = 3.0;
                int[] mins = new int[held.length], maxes = new int[held.length];
                Arrays.fill(mins, 1);
                Arrays.fill(maxes, 1);
                mins[held.length - 1] = 2;
                maxes[held.length - 1] = 4;
                WeightedTableWrapper<Physical> pt = new WeightedTableWrapper<>(p.nextLong(), held, weights, mins, maxes);
                p.physicalDrops.add(pt);
                p.location = coord;
                map.contents[coord.x][coord.y].add(p);
                p.appearance = mapSLayers.glyph(p.symbol, p.color, coord.x, coord.y);
                p.appearance.setVisible(false);
                map.creatures.put(coord, p);
            }
        }
    }

    private void prepCrawl() {
        message("Generating crawl.");
        //world = worldGenerator.buildWorld(worldWidth, worldHeight, 8, handBuilt);
        int aboveGround = 7;
        EpiMap[] underground = worldGenerator.buildWorld(config.settings.worldGridWidth, config.settings.worldGridHeight, config.settings.worldGridDepth);
        EpiMap[] castle = castleGenerator.buildCastle(config.settings.worldGridWidth, config.settings.worldGridHeight, aboveGround);
        world = Stream.of(castle, underground).flatMap(Stream::of).toArray(EpiMap[]::new);
        depth = aboveGround + 1; // higher is deeper; aboveGround is surface-level
//        depth = 0;
//        world = underground;
        map = world[depth];
        fxHandler = new FxHandler(mapSLayers, 3, colorCenter, map.lighting.fovResult);
        fxHandlerPassive = new FxHandler(passiveSLayers, 0, colorCenter, map.lighting.fovResult);
        floors = new GreasedRegion(map.width, map.height);

        simple = new char[map.width][map.height];

        StatefulRNG dijkstraRNG = new StatefulRNG();// random seed, player won't make deterministic choices
        toPlayerDijkstra = new DijkstraMap(simple, Measurement.EUCLIDEAN, dijkstraRNG);
        monsterDijkstra = new DijkstraMap(simple, Measurement.EUCLIDEAN, dijkstraRNG); // shared RNG
        los = new LOS(LOS.BRESENHAM);
        blockage = new GreasedRegion(map.width, map.height);
        player.location = Coord.get(0, 0);
        changeLevel(depth);

        mode = GameMode.CRAWL;
        mapInput.flush();
        mapInput.setRepeatGap(220);
        mapInput.setKeyHandler(mapKeys);
        mapInput.setMouse(mapMouse);
    }

    public void changeLevel(int level){
        changeLevel(level, null);
    }

    public void changeLevel(int level, Coord location) {
        map.contents[player.location.x][player.location.y].remove(player);

        depth = level;
        map = world[depth];
        mapSLayers.clear();
        for (int i = mapSLayers.glyphs.size() - 1; i >= 0; i--) {
            mapSLayers.removeGlyph(mapSLayers.glyphs.get(i));
        }

        setupLevel();
        
        if (location == null) { // set up a valid random start location
            //// when validating that map setup is deterministic, the following print should always be the same:
            //System.out.println(rng.getState() + ", floors hash " + floors.hash64());
            GreasedRegion floors2 = floors.copy();
            floors2.andNot(map.downStairPositions).andNot(map.upStairPositions);
            do {
                location = floors2.singleRandom(rng);
            } while (map.contents[location.x][location.y].blockage != null);
        }
        
        player.location = location;
        map.contents[player.location.x][player.location.y].add(player);
        player.appearance = mapSLayers.glyph(player.symbol, player.color, player.location.x, player.location.y);

        fxHandlerPassive.seen =fxHandler.seen = map.lighting.fovResult;
        creatures = map.creatures;
        simple = map.simpleChars();
        lineDungeon = map.line;
        calcFOV(player.location.x, player.location.y);
        toPlayerDijkstra.initialize(simple);
        monsterDijkstra.initialize(simple);
        calcDijkstra();
        contextHandler.setMap(map, world);
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
                if(weapon == null) {
                    if (creature.weaponData != null)
                    {
                        ((StatefulRNG)monsterDijkstra.rng).setState(creature.location.hashCode() ^ (long)player.location.hashCode() << 32);
//                        message(creature.name + " has sight " + creature.stats.get(Stat.SIGHT).actual());
//                        monsterDijkstra.findTechniquePath(path, (int) creature.stats.get(Stat.SIGHT).actual(), creature.weaponData.technique, simple, los, creaturePositions, null, c, ps);
                        monsterDijkstra.findAttackPath(path, 1,  creature.weaponData.technique.aoe.getMinRange(), creature.weaponData.technique.aoe.getMaxRange(), los, creaturePositions, null, c, pa);
                    }
                }
                else {
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
                if(weapon == null && path.isEmpty()) // && monsterDijkstra.targetMap[c.x][c.y] == null
                {
                    Coord next = c.translateCapped(creature.between(-1, 2), creature.between(-1, 2), map.width, map.height);
                    if(!map.creatures.containsKey(next) && map.contents[next.x][next.y].blockage == null)
                        path.add(next);
//                    if(map.lighting.fovResult[c.x][c.y] > 0.0) 
//                        message(creature.name + " #" + StringKit.hex(((EpiData)creature).hashCode()) + " moved randomly.");
                }
                if (weapon != null || !path.isEmpty()) {
                    Coord step;
                    if(!path.isEmpty()) 
                        step = path.get(0);
                    else
                        step = c;
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
                                        if (map.lighting.fovResult[c.x][c.y] > 0)
                                            mapSLayers.wiggle(player.appearance, 0.4f);
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
                    }
                    else {
                        if (creature.creatureData != null &&
                                creature.creatureData.lastUsedItem != null &&
                                creature.creatureData.lastUsedItem.radiance != null)
                            creature.creatureData.lastUsedItem.radiance.flare = 0f;
                        if (map.contents[step.x][step.y].blockage == null && !creatures.containsKey(step) && creatures.alterAtCarefully(i, step) != null) {
                            map.contents[c.x][c.y].remove(creature);
                            if (creature.appearance == null) {
                                System.out.println("runTurn: recreating appearance of " + creature);
                                creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, c.x, c.y);
                                if (creature.overlayAppearance != null && creature.overlaySymbol != '\uffff')
                                    creature.overlayAppearance = mapSLayers.glyph(creature.overlaySymbol, creature.overlayColor, c.x, c.y);
                            }
                            if(map.lighting.fovResult[c.x][c.y] > 0)
                                creature.appearance.setVisible(true);
                            //creatures.putAt(step, creatures.remove(c), i);

                            creature.location = step;
                            map.contents[step.x][step.y].add(creature);
                            if (creature.appearance != null) {
//                            if (map.lighting.fovResult[c.x][c.y] > 0) {
                                mapSLayers.slide(creature.appearance, c.x, c.y, step.x, step.y, 0.145f, null);
                                if (creature.overlayAppearance != null)
                                    mapSLayers.slide(creature.overlayAppearance, c.x, c.y, step.x, step.y, 0.145f, null);
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
        if (config.debugConfig.odinView) {
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
                    //if (!odinView) { // Don't remember things seen in all-view (or do remember if you need mini-map)
                        if (map.remembered[x][y] == null) {
                            map.remembered[x][y] = new RememberedTile(map.contents[x][y]);
                        } else {
                            map.remembered[x][y].remake(map.contents[x][y]);
                        }
                    //}
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
        blockage.fringe8way();
        toPlayerDijkstra.setGoal(player.location);
        toPlayerDijkstra.scan(blockage);
    }

    /**
     * Attempts to equip a random weapon from the player's inventory
     */
    public void equipItem() {
        if (player.inventory.isEmpty()) {
            message("Nothing equippable found.");
        } else {
            player.shuffleInPlace(player.inventory);
            for (int i = 0; i < player.inventory.size(); i++) {
                Physical chosen = player.inventory.get(i);
                if (chosen.weaponData != null) {
                    equipItem(chosen);
                    return;
                }
            }
        }
    }

    public void equipItem(Physical item) {
        player.equipItem(item);
        if (item.weaponData != null && item.radiance != null) { // TODO mix light sources from player held items
//            player.radiance = new Radiance((float) player.stats.get(Stat.SIGHT).actual(), item.radiance.color, item.radiance.flicker, item.radiance.strobe, item.radiance.flare);
            // TODO - recalc lighting
            calcFOV(player.location.x, player.location.y);
        }
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
                if(range < 2.5) continue;
                if (range <= w.rawWeapon.range + 2.5) {
                    weapons.add(w);
                }
            }
            else if (range <= w.rawWeapon.range + 1.5) {
                weapons.add(w);
            }
        }
        return weapons;
    }

    public Coord showAttackOptions(Physical target, OrderedMap<String, Weapon> options) {
        int sz = options.size(), len = 0;
        for (int i = 0; i < sz; i++) {
            len = Math.max(options.keyAt(i).length(), len);
        }
        int startY = MathUtils.clamp(target.location.y - (sz >> 1), 0, map.height - sz - 1),
                startX = target.location.x * 2 + 2;
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
            startX = target.location.x * 2 - len;
            for (int i = 0; i < sz; i++) {
                String name = options.keyAt(i);
                for (int j = 0; j < len; j++) {
                    mapHoverSLayers.put(startX + j, startY + i, smoke);
                }
                mapHoverSLayers.put(target.location.x * 2 - name.length(), startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null);
            }
        }
        showingMenu = true;
        return Coord.get(startX - 2 >> 1, startY);
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
                if(i == sub)
                    mapOverlaySLayers.put(startX, startY + i, '→', SColor.CW_BRIGHT_GREEN, null, 2);
                mapOverlaySLayers.put(startX + 2, startY + i, name, SColor.COLOR_WHEEL_PALETTE_LIGHT[(i * 3) & 15], null, 2);
            }
        } else {
            startX = target.x - len + 2;
            for (int i = 0; i < sz; i++) {
                String name = interactable.interactableData.get(i).verb;
                for (int j = 0; j < len; j++) {
                    mapOverlaySLayers.put(startX + j, startY + i, '\0', i == sub ? highlight : smoke, 0f, 1);
                }
                if(i == sub)
                    mapOverlaySLayers.put(startX, startY + i, '→', SColor.CW_BRIGHT_GREEN, null, 2);
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
        ActionOutcome ao = ActionOutcome.attack(player, choice, target);
        Element element = ao.element;
        Direction dir = Direction.getDirection(target.location.x - player.location.x, target.location.y - player.location.y);

        calcFOV(player.location.x, player.location.y);
        fxHandler.attackEffect(player, target, ao, dir); // TODO - tie creature glyph removal to appropriate moment in attack effect

        if (ao.hit) {
            applyStatChange(target, Stat.VIGOR, ao.actualDamage);
            if (target.stats.get(Stat.VIGOR).actual() <= 0) {
                if(target.appearance != null)
                {
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
                            int tx = MathUtils.clamp(targetX + player.between(-1, 2), 0, config.settings.worldGridWidth - 1),
                                ty = MathUtils.clamp(targetY + player.between(-1, 2), 0, config.settings.worldGridHeight - 1);
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
                                if(item.attached) return;
                                map.contents[targetX][targetY].add(item);
                            });
                    if(target.appearance != null && target.appearance.isVisible())
                        mapSLayers.burst(targetX, targetY, 1, Radius.CIRCLE, target.appearance.shown, target.color, SColor.translucentColor(target.color, 0f), 1);
                    message("You defeat the " + target.name + " with " + -ao.actualDamage + " " + element.styledName + " damage!");
                }
            } else {
                String amtText = String.valueOf(-ao.actualDamage);
                if (ao.crit) {
                    if(target.appearance != null && target.appearance.isVisible())
                        mapSLayers.wiggle(0.0f, target.appearance, 0.4f, () -> target.appearance.setPosition(
                            mapSLayers.worldX(target.location.x), mapSLayers.worldY(target.location.y)));
                    message(Messaging.transform("You [CW Bright Orange]critically[] " + element.verb + " the " + target.name + " for " +
                            amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                } else {
                    message(Messaging.transform("You " + element.verb + " the " + target.name + " for " +
                            amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                }
                if (ao.targetConditioned) {
                    message(Messaging.transform("You " +
                            ConditionBlueprint.CONDITIONS.getOrDefault(ao.targetCondition, ConditionBlueprint.CONDITIONS.randomValue(player)).verb +
                            " the " + target.name + " with your attack!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                    if (target.overlaySymbol != '\uffff') {
                        if (target.overlayAppearance != null) mapSLayers.removeGlyph(target.overlayAppearance);
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
            } else if(!map.contents[newX][newY].contents.isEmpty()
                    && (thing = map.contents[newX][newY].contents.get(0)).interactableData != null
                    && !thing.interactableData.isEmpty()
                    && thing.interactableData.get(0).bumpAction
            ){
                cancelMove();
                thing.location = newPos; // total hack; needed by door-opening interaction
                message(Messaging.transform(thing.interactableData.get(0).interaction.interact(player, thing, this),
                        player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                runTurn();
            } else if ((thing = map.contents[newX][newY].getLargeNonCreature()) != null) {
                cancelMove();
                if(thing.interactableData != null && !thing.interactableData.isEmpty() && thing.interactableData.get(0).bumpAction) {
                    thing.location = newPos; // total hack; needed by door-opening interaction
                    message(Messaging.transform(thing.interactableData.get(0).interaction.interact(player, thing, this),
                            player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                }
                else 
                    message("Ran into " + thing.name);
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
        if(c == '#')
            wallColors[x][y] = foreground;
        else
            mapSLayers.put(x, y, c, foreground); // "dark" theme
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putCrawlMap() {
        ArrayTools.fill(mapSLayers.backgrounds, map.lighting.backgroundColor);
        map.lighting.update();
        if(!showingMenu) {
            for (int i = 0; i < toCursor.size(); i++) {
                map.lighting.updateUI(toCursor.get(i), softWhiteChain[i * 3 & 7]);
            }
        }
        map.lighting.draw(mapSLayers);
        Physical creature;
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                double sight = map.lighting.fovResult[x][y];
                if (sight > 0.0) {
                    EpiTile tile = map.contents[x][y];
                    mapSLayers.clear(x, y, 1);
                    if ((creature = creatures.get(Coord.get(x, y))) != null) {
                        if(creature.appearance == null)
                            creature.appearance = mapSLayers.glyph(creature.symbol, lerpFloatColorsBlended(unseenCreatureColorFloat, creature.color, 0.5f + 0.35f * (float) sight), x, y);                         
                        else
                        {
                            creature.appearance.setVisible(true);
                            creature.appearance.setPackedColor(lerpFloatColorsBlended(unseenCreatureColorFloat, creature.color, 0.5f + 0.35f * (float) sight));
                        }
                        if (creature.overlayAppearance != null)
                        {
                            creature.overlayAppearance.setVisible(true);
                            creature.overlayAppearance.setPackedColor(lerpFloatColorsBlended(unseenCreatureColorFloat, creature.overlayColor, 0.5f + 0.35f * (float) sight));
                        }
                        mapSLayers.clear(x, y, 0);
                        if (!creature.wasSeen) { // stop auto-move if a new creature pops into view
                            cancelMove();
                            creature.creatureData.culture.messaging.language.shift = creature.nextLong();
                            message(creature.creatureData.culture.messaging.transform(
//                                    creature.creatureData.sayings[creature.creatureData.sayings.length - 1], 
                                    creature.getRandomElement(creature.creatureData.sayings), 
                                    creature.name, creature.creatureData.genderPronoun, 
                                    player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                        }
                        creature.wasSeen = true;
                    } else {
                        putWithLight(x, y, tile.getSymbol(), tile.getForegroundColor(), tile.getBackgroundColor(x, y, TimeUtils.timeSinceMillis(startMillis)));
                    }
                } else {
                    RememberedTile rt = map.remembered[x][y];
                    if (rt != null) {
                        mapSLayers.clear(x, y, 0);
                        if(rt.symbol == '#')
                            wallColors[x][y] = rt.front;
                        else 
                            mapSLayers.put(x, y, rt.symbol, rt.front, rt.back, 0);
                    }
                }
            }
        }
        MapUtility.fillLinesToBoxes(walls, prunedDungeon, wallColors);
        mapSLayers.clear(player.location.x, player.location.y, 0);

        mapSLayers.clear(2);
//        if (!showingMenu) {
//            for (int i = 0; i < toCursor.size(); i++) {
//                Coord c = toCursor.get(i);
//                Direction dir;
//                if (i == toCursor.size() - 1) {
//                    dir = Direction.NONE; // last spot shouldn't have arrow
//                } else if (i == 0) {
//                    dir = Direction.toGoTo(player.location, c);
//                } else {
//                    dir = Direction.toGoTo(toCursor.get(i - 1), c);
//                }
//                mapSLayers.put(c.x, c.y, Utilities.arrowsFor(dir).charAt(0), SColor.CW_PURPLE.toFloatBits(), 0f, 2);
//            }
//        }
    }

    public void showFallingGameOver() {
        message("");
        message("");
        message("");
        message("");
        message("You have died.");
        message("");
        message("Try Again (t) or Quit (Shift-Q)?");

        mapInput.flush();
        mapInput.setKeyHandler(fallingGameOverKeys);
    }

    public void showFallingWin() {
        message("You have reached the Dragon's Hoard!");
        message("On the way, you gathered:");
        
        StringBuilder sb = new StringBuilder(100);
        for(Physical item : player.inventory)
        {
            sb.append(item.groupingData != null && item.groupingData.quantity > 1 ? item.toString() + " x" + item.groupingData.quantity : item.toString()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        List<String> lines = StringKit.wrap(sb, messageSize.gridWidth - 2);
        int start;
        for (start = 0; start < lines.size() && start < 4; start++) {
            message(lines.get(start));
        }
//        for (; start < 4; start++) {
//            message("");
//        }
        message("Try Again (t) or Quit (Shift-Q)?");

        mapInput.flush();
        mapInput.setKeyHandler(fallingGameOverKeys);
    }

    @Override
    public void render() {
        if(config.debugConfig.debugActive)
            glp.reset();
        //super.render();

        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(unseenColor.r, unseenColor.g, unseenColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (mode) {
            case CRAWL:
                // crawl mode needs the camera to move around with the player since the playable crawl map is bigger than the view space
                mapStage.getCamera().position.x = player.appearance.getX();
                mapStage.getCamera().position.y = player.appearance.getY();
                putCrawlMap();
                break;
            case DIVE:
                if (fallingHandler.reachedGoal) {
                    paused = true;
                    pausedAt = Instant.now();
                    showFallingWin();
                    fallingHandler.reachedGoal = false;
                    fallingHandler.update();
                    break;
                }
                // this goes here; otherwise the player "skips" abruptly when coming out of pause
                fallingHandler.setCurrentDepth(fallingSLayers.gridY(fallingStage.getCamera().position.y = MathUtils.lerp(startingY, finishY,
                        (currentFallDuration + fallDuration) / timeToFall)));
                if (!paused) {
                    currentFallDuration = (unpausedAt.until(Instant.now(), ChronoUnit.MILLIS));
                    if (Instant.now().isAfter(nextInput)) {
                        fallingHandler.processInput();
                        nextInput = Instant.now().plusMillis(inputDelay);
                    }
                    if (Instant.now().isAfter(nextFall)) {
                        nextFall = Instant.now().plusMillis(fallDelay);
                        fallingHandler.fall();
                    }
                    infoHandler.updateDisplay();

                    for (Stat s : Stat.healths) {
                        if (player.stats.get(s).actual() <= 0) {
                            paused = true;
                            showFallingGameOver();
                        }
                    }
                } else {
                    fallDuration += currentFallDuration;
                    currentFallDuration = 0L;
                    unpausedAt = Instant.now();
                    fallingHandler.update();
                }
                break;
        }
        // if the user clicked, we have a list of moves to perform.
        if (!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Points from awaitedMoves.
            if (!mapSLayers.hasActiveAnimations()) {
                if(player.creatureData.lastUsedItem != null && 
                        player.creatureData.lastUsedItem.radiance != null && player.creatureData.lastUsedItem.radiance.flare > 0f)
                    player.creatureData.lastUsedItem.radiance.flare = 0f;
                Coord m = awaitedMoves.remove(0);
                if (!toCursor.isEmpty())
                    toCursor.remove(0);
                move(Direction.toGoTo(player.location, m));
                infoHandler.updateDisplay();
            }
        } else {
            multiplexer.process();
        }
        // the order here matters. We apply multiple viewports at different times to clip different areas.
        contextViewport.apply(false);
        contextStage.act();

        batch.begin();

        batch.setProjectionMatrix(contextViewport.getCamera().combined);
        contextStage.getRoot().draw(batch, 1);

        infoViewport.apply(false);
        infoStage.act();
//        infoStage.draw();
        batch.setProjectionMatrix(infoViewport.getCamera().combined);
        infoStage.getRoot().draw(batch, 1);

        if (config.debugConfig.debugActive) {
            int drawCalls = glp.getDrawCalls();
            int textureBindings = glp.getTextureBindings();
            int calls = glp.getCalls();
            int switches = glp.getShaderSwitches();
            tempSB.setLength(0);
            tempSB.append(Gdx.graphics.getFramesPerSecond())
                    .append(" FPS, Draw Calls: ").append(drawCalls)
                    .append(", Calls: ").append(calls)
                    .append(", Texture Binds: ").append(textureBindings)
                    .append(", Shader Switches: ").append(switches)
                    .append(", Lights: ").append(map.lighting.lights.size())
            ;
            screenPosition.set(16, 8);
            mapViewport.unproject(screenPosition);
            messageSLayers.put(1, 1, tempSB.toString(), WHITE);
//            font.bmpFont.draw(batch, tempSB, screenPosition.x, screenPosition.y);
        }
        messageViewport.apply(false);
        messageStage.act();
//        messageStage.draw();
        batch.setProjectionMatrix(messageViewport.getCamera().combined);
        messageStage.getRoot().draw(batch, 1);

//        batch.end();

//        shaper.setProjectionMatrix(messageStage.getCamera().combined);
//        UIUtil.drawRectangle(shaper, 1, 1, messageSize.pixelWidth() - 2, messageSize.pixelHeight() - 2,
//                ShapeRenderer.ShapeType.Line, CW_LIGHT_APRICOT);
//        messageSLayers.drawBorder(batch);

        if (mode.equals(GameMode.CRAWL)) {
            //here we apply the other viewport, which clips a different area while leaving the message area intact.
            mapViewport.apply(false);
            mapStage.act();
            //we use a different approach here because we can avoid ending the batch by setting this matrix outside a batch
            batch.setProjectionMatrix(mapStage.getCamera().combined);
            //then we start a batch and manually draw the stage without having it handle its batch...
//            batch.begin();
            mapSLayers.font.configureShader(batch);
            if (mapOverlaySLayers.isVisible()) {
                mapOverlayStage.act();
                mapOverlayHandler.updateDisplay();
                if(mapOverlayHandler.getSubselection() != null)
                    showInteractOptions(mapOverlayHandler.getSelected(), player, mapOverlayHandler.getSelection(), map);
                batch.setProjectionMatrix(mapOverlayStage.getCamera().combined);
                mapOverlayStage.getRoot().draw(batch, 1f);
            }
            else {
                mapSLayers.draw(batch, 1f);
                passiveSLayers.draw(batch, 1f);
            }
            mapHoverSLayers.draw(batch, 1f);
            batch.end();
        } else {
            //here we apply the other viewport, which clips a different area while leaving the message area intact.
            fallingViewport.apply(false);
            fallingStage.act();
            //we use a different approach here because we can avoid ending the batch by setting this matrix outside a batch
            batch.setProjectionMatrix(fallingStage.getCamera().combined);
            //then we start a batch and manually draw the stage without having it handle its batch...
//            batch.begin();
            fallingSLayers.font.configureShader(batch);
            fallingStage.getRoot().draw(batch, 1f);
            //so we can draw the actors independently of the stage while still in the same batch
            //player.appearance.draw(batch, 1.0f);
            //we still need to end
            batch.end();
        }
        //uncomment the upcoming line if you want to see how fast this can run at top speed...
        //this needs vsync set to false in DesktopLauncher.
        Gdx.graphics.setTitle(Gdx.graphics.getFramesPerSecond() + " FPS");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resizing to (" + width + ", " + height);
        // if not going to fullscreen, probably
        if (Gdx.graphics.getDisplayMode().width != width && !config.displayConfig.maximized) {
            config.displayConfig.windowWidth = width;
            config.displayConfig.windowHeight = height;
            config.displayConfig.monitorName = Gdx.graphics.getMonitor().name;
        }
        super.resize(width, height);

        float currentZoomX = (float) width / config.settings.defaultPixelWidth();
        float currentZoomY = (float) height / config.settings.defaultPixelHeight();

        mapSLayers.setBounds(0, 0, currentZoomX * mapSize.pixelWidth(), currentZoomY * mapSize.pixelHeight());
        mapOverlaySLayers.setBounds(0, 0, currentZoomX * mapSize.pixelWidth(), currentZoomY * mapSize.pixelHeight());
        messageSLayers.setBounds(0, 0, currentZoomX * messageSize.pixelWidth(), currentZoomY * messageSize.pixelHeight());
        contextSLayers.setBounds(0, 0, currentZoomX * contextSize.pixelWidth(), currentZoomY * contextSize.pixelHeight());
        infoSLayers.setBounds(0, 0, currentZoomX * infoSize.pixelWidth(), currentZoomY * infoSize.pixelHeight());

        // SquidMouse turns screen positions to cell positions, and needs to be told that cell sizes have changed
        // a quirk of how the camera works requires the mouse to be offset by half a cell if the width or height is odd
        float oddAddX = width % 2 == 0 ? 0 : 0;//.5f;
        float oddAddY = height % 2 == 0 ? 0 : 0;//.5f;

        // Top Left
        int x = 0;
        int y = (int) (height - mapSize.pixelHeight() * currentZoomY);
        int pixelWidth = (int) (currentZoomX * mapSize.pixelWidth());
        int pixelHeight = (int) (currentZoomY * mapSize.pixelHeight());
        mapViewport.update(width, height, false);
        mapViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);
        mapOverlayViewport.update(width, height, false);
        mapOverlayViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);
        fallingViewport.update(width, height, false);
        fallingViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        float cellWidth = currentZoomX * mapSize.cellWidth;
        float cellHeight = currentZoomY * mapSize.cellHeight;
        int offsetX = (int) (mapSize.cellWidth * currentZoomX * oddAddX);
        int offsetY = (int) (mapSize.cellHeight * currentZoomY * oddAddY);
        mapMouse.reinitialize(cellWidth, cellHeight, mapSize.gridWidth, mapSize.gridHeight, offsetX, offsetY);
        equipmentMouse.reinitialize(cellWidth, cellHeight, mapSize.gridWidth, mapSize.gridHeight, offsetX, offsetY);

        // Bottom Left
        messageViewport.update(width, height, false);
        x = 0;
        y = 0;
        pixelWidth = (int) (currentZoomX * messageSize.pixelWidth());
        pixelHeight = (int) (currentZoomY * messageSize.pixelHeight());
        messageViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        cellWidth = currentZoomX * messageSize.cellWidth;
        cellHeight = currentZoomY * messageSize.cellHeight;
        offsetX = (int) (messageSize.cellWidth * currentZoomX * oddAddX);
        offsetY = (int) (height - cellHeight * messageSize.gridHeight) + (int) (messageSize.cellHeight * currentZoomY * oddAddY);
        messageMouse.reinitialize(cellWidth, cellHeight, messageSize.gridWidth, messageSize.gridHeight, -offsetX, -offsetY);

        // Top Right
        x = (int) (currentZoomX * mapSize.pixelWidth());
        y = (int) (height - infoSize.pixelHeight() * currentZoomY);
        pixelWidth = (int) (currentZoomX * infoSize.pixelWidth());
        pixelHeight = (int) (currentZoomY * infoSize.pixelHeight());
        infoViewport.update(width, height, false);
        infoViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);

        cellWidth = currentZoomX * infoSize.cellWidth;
        cellHeight = currentZoomY * infoSize.cellHeight;
        offsetX = x + (int) (infoSize.cellWidth * currentZoomX * oddAddX);
        offsetY = (int) (infoSize.cellHeight * currentZoomY * oddAddY);
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
        offsetX = x + (int) (contextSize.cellWidth * currentZoomX * oddAddX);
        offsetY = (int) (height - cellHeight * contextSize.gridHeight) + (int) (contextSize.cellHeight * currentZoomY * oddAddY);
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

}
