package squidpony.epigon;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.Messaging;
import squidpony.epigon.combat.ActionOutcome;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.mixin.Grouping;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.display.*;
import squidpony.epigon.display.MapOverlayHandler.PrimaryMode;
import squidpony.epigon.dm.RecipeMixer;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;
import squidpony.epigon.mapping.*;
import squidpony.epigon.playground.HandBuilt;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;
import squidpony.epigon.universe.WieldSlot;
import squidpony.panel.IColoredString;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static squidpony.squidgrid.gui.gdx.SColor.lerpFloatColors;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final PanelSize mapSize;
    public static final PanelSize messageSize;
    public static final PanelSize infoSize;
    public static final PanelSize contextSize;
    public static final int messageCount;
    public static final long seed = 0xBEEFD00DFADEFEEL;
    // this is separated from the StatefulRNG so you can still call ThrustAltRNG-specific methods, mainly skip()
    public final ThrustAltRNG thrustAltRNG = new ThrustAltRNG(seed);
    public final StatefulRNG rng = new StatefulRNG(thrustAltRNG);
    // used for certain calculations where the state changes per-tile
    // allowed to be static because posrng is expected to have its move() method called before each use, which seeds it
    public static final PositionRNG posrng = new PositionRNG(seed ^ seed >>> 1);
    // meant to be used to generate seeds for other RNGs; can be seeded when they should be fixed
    public static final ThrustAltRNG rootChaos = new ThrustAltRNG();
    public final RecipeMixer mixer;
    public HandBuilt handBuilt;
    public static final char BOLD = '\u4000', ITALIC = '\u8000', REGULAR = '\0';

    // Audio
    private SoundManager sound;

    // Display
    SpriteBatch batch;
    private SquidColorCenter colorCenter;
    private SparseLayers mapSLayers;
    private SquidLayers mapOverlaySLayers;
    private SquidLayers infoSLayers;
    private SquidLayers contextSLayers;
    private SquidLayers messageSLayers;
    private SquidInput mapInput;
    private SquidInput contextInput;
    private SquidInput infoInput;
    private Color bgColor, unseenColor;
    private float bgColorFloat, unseenColorFloat, unseenCreatureColorFloat;
    private List<Coord> toCursor;
    private TextCellFactory font;

    // Set up the text display portions
    private List<IColoredString<Color>> messages = new ArrayList<>();
    private int messageIndex;

    private ControlMapping currentMapping;

    // World
    private WorldGenerator worldGenerator;
    private EpiMap map;
    private FxHandler fxHandler;
    private MapOverlayHandler mapOverlayHandler;
    private ContextHandler contextHandler;
    private InfoHandler infoHandler;
    private GreasedRegion blocked;
    private DijkstraMap toPlayerDijkstra;
    private Coord cursor;
    private Physical player;
    private ArrayList<Coord> awaitedMoves;
    private double[][] fovResult;
    private double[][] priorFovResult;
    private OrderedMap<Coord, Physical> creatures = new OrderedMap<>();
    private int autoplayTurns = 0;
    private boolean processingCommand = true;

    // WIP stuff, needs large sample map
    private Stage mapStage, messageStage, infoStage, contextStage, mapOverlayStage;
    private Viewport mapViewport, messageViewport, infoViewport, contextViewport, mapOverlayViewport;
    //private Camera camera;
    //private TextCellFactory.Glyph playerEntity;

    private float[] lightLevels;

    public static final int worldWidth, worldHeight;
    // Set up sizing all in one place
    static {
        worldWidth = 100;
        worldHeight = 50;
        int bigW = 70;
        int bigH = 26;
        int smallW = 50;
        int smallH = 22;
        int cellW = 15;
        int cellH = 28;
        int bottomH = 6;
        mapSize = new PanelSize(bigW, bigH, cellW, cellH);
        messageSize = new PanelSize(bigW, bottomH, cellW, cellH);
        infoSize = new PanelSize(smallW, smallH * 7 / 4, 7, 16);
        contextSize = new PanelSize(smallW, (bigH + bottomH - smallH) * 7 / 4, 7, 16);
        messageCount = bottomH - 2;

    }
//    public static final String outlineFragmentShader = "#ifdef GL_ES\n"
//            + "precision mediump float;\n"
//            + "precision mediump int;\n"
//            + "#endif\n"
//            + "\n"
//            + "uniform sampler2D u_texture;\n"
//            + "uniform float u_smoothing;\n"
//            + "varying vec4 v_color;\n"
//            + "varying vec2 v_texCoords;\n"
//            + "\n"
//            + "void main() {\n"
//            + "  if(u_smoothing <= 0.0) {\n"
//            + "    float smoothing = -u_smoothing;\n"
//            + "	   vec4 box = vec4(v_texCoords-0.000125, v_texCoords+0.000125);\n"
//            + "	   float asum = smoothstep(0.5 - smoothing, 0.5 + smoothing, texture2D(u_texture, v_texCoords).a) + 0.5 * (\n"
//            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, texture2D(u_texture, box.xy).a) +\n"
//            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, texture2D(u_texture, box.zw).a) +\n"
//            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, texture2D(u_texture, box.xw).a) +\n"
//            + "                 smoothstep(0.5 - smoothing, 0.5 + smoothing, texture2D(u_texture, box.zy).a));\n"
//            + "    gl_FragColor = vec4(v_color.rgb, (asum / 3.0) * v_color.a);\n"
//            + "	 } else {\n"
//            + "    float distance = texture2D(u_texture, v_texCoords).a;\n"
//            + "	   vec2 box = vec2(0.0, 0.00375 * (u_smoothing + 0.0825));\n"
//            + "	   float asum = 0.7 * (smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, distance) + \n"
//            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords + box.xy).a) +\n"
//            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords - box.xy).a) +\n"
//            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords + box.yx).a) +\n"
//            + "                   smoothstep(0.5 - u_smoothing, 0.5 + u_smoothing, texture2D(u_texture, v_texCoords - box.yx).a)),\n"
//            + "                 outline = clamp((distance * 0.8 - 0.415) * 18, 0, 1);\n"
//            + "	   gl_FragColor = vec4(mix(vec3(0.0), v_color.rgb * 1.2, outline), asum * v_color.a);\n" // the only change from SquidLib's version is: rgb * 1.2
//            + "  }\n"
//            + "}\n";

    public Epigon()
    {
        mixer = new RecipeMixer();
        handBuilt = new HandBuilt(rng, mixer);
        Weapon.init();
    }

    @Override
    public void create() {
        System.out.println("Working in folder: " + System.getProperty("user.dir"));

        System.out.println("Loading sound manager.");
        sound = new SoundManager();
        colorCenter = new SquidColorCenter();

        // Set the map size early so things can reference it
        System.out.println(rng.getState());

        Coord.expandPoolTo(worldWidth, worldHeight);

        bgColor = SColor.WHITE;
        unseenColor = SColor.BLACK_DYE;
        unseenCreatureColorFloat = SColor.CW_DARK_GRAY.toFloatBits();
        bgColorFloat = bgColor.toFloatBits();
        unseenColorFloat = unseenColor.toFloatBits();
        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();

        System.out.println("Putting together display.");
        mapViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());
        messageViewport = new StretchViewport(messageSize.pixelWidth(), messageSize.pixelHeight());
        infoViewport = new StretchViewport(infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport = new StretchViewport(contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlayViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());

        // Here we make sure our Stages, which holds any text-based grids we make, uses our Batch.
        mapStage = new Stage(mapViewport, batch);
        messageStage = new Stage(messageViewport, batch);
        infoStage = new Stage(infoViewport, batch);
        contextStage = new Stage(contextViewport, batch);
        mapOverlayStage = new Stage(mapOverlayViewport, batch);
        font = DefaultResources.getLeanFamily();
        TextCellFactory smallFont = font.copy();
        messageIndex = messageCount;
        messageSLayers = new SquidLayers(
                messageSize.gridWidth,
                messageSize.gridHeight,
                messageSize.cellWidth,
                messageSize.cellHeight,
                font);

        infoSLayers = new SquidLayers(
                infoSize.gridWidth,
                infoSize.gridHeight,
                infoSize.cellWidth,
                infoSize.cellHeight,
                smallFont);
        infoSLayers.getBackgroundLayer().setDefaultForeground(SColor.CW_ALMOST_BLACK);
        infoSLayers.getForegroundLayer().setDefaultForeground(colorCenter.lighter(SColor.CW_PALE_AZURE));

        contextSLayers = new SquidLayers(
                contextSize.gridWidth,
                contextSize.gridHeight,
                contextSize.cellWidth,
                contextSize.cellHeight,
                smallFont);
        contextSLayers.getBackgroundLayer().setDefaultForeground(SColor.CW_ALMOST_BLACK);
        contextSLayers.getForegroundLayer().setDefaultForeground(SColor.CW_PALE_LIME);

        mapSLayers = new SparseLayers(
                worldWidth,
                worldHeight,
                mapSize.cellWidth,
                mapSize.cellHeight,
                font);
//        mapSLayers.font.shader = new ShaderProgram(DefaultResources.vertexShader, outlineFragmentShader);
//        if (!mapSLayers.font.shader.isCompiled()) {
//            Gdx.app.error("shader", "Outlined Distance Field font shader compilation failed:\n" + mapSLayers.font.shader.getLog());
//        }

        //ArrayTools.fill(mapSLayers.getBackgrounds(), unseenColorFloat);
        infoHandler = new InfoHandler(infoSLayers, colorCenter);
        contextHandler = new ContextHandler(contextSLayers, mapSLayers);

        mapOverlaySLayers = new SquidLayers(
                mapSize.gridWidth,
                mapSize.gridHeight,
                mapSize.cellWidth,
                mapSize.cellHeight,
                font);
        mapOverlaySLayers.getBackgroundLayer().setDefaultForeground(colorCenter.desaturate(SColor.DB_INK, 0.8));
        mapOverlaySLayers.getForegroundLayer().setDefaultForeground(SColor.LIME);
        mapOverlayHandler = new MapOverlayHandler(mapOverlaySLayers, colorCenter);

        font.tweakWidth(mapSize.cellWidth * 1.125f).tweakHeight(mapSize.cellHeight * 1.07f).initBySize();
        smallFont.tweakWidth(infoSize.cellWidth * 1.125f).tweakHeight(infoSize.cellHeight * 1.1f).initBySize();

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        //mapSLayers.setAnimationDuration(0.145f);

        messageSLayers.setBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        infoSLayers.setBounds(0, 0, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextSLayers.setBounds(0, 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlaySLayers.setBounds(0, 0, mapSize.pixelWidth(), mapSize.pixelWidth());
        mapSLayers.setPosition(0, 0);
        mapViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());
        infoViewport.setScreenBounds(mapSize.pixelWidth(), contextSize.pixelHeight(), infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport.setScreenBounds(mapSize.pixelWidth(), 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlayViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        mapInput = new SquidInput(mapKeys, mapMouse);
        contextInput = new SquidInput(contextMouse);
        infoInput = new SquidInput(infoMouse);
        Gdx.input.setInputProcessor(new InputMultiplexer(mapStage, messageStage, mapInput, contextInput, infoInput));

        mapStage.addActor(mapSLayers);
        mapOverlayStage.addActor(mapOverlaySLayers);
        messageStage.addActor(messageSLayers);
        infoStage.addActor(infoSLayers);
        contextStage.addActor(contextSLayers);


//        Color backLight = SColor.AMUR_CORK_TREE;
//        lights = colorCenter.gradient(colorCenter.lerp(RememberedTile.memoryColor, backLight, 0.2), backLight, 12, Interpolation.sineOut); // work from outside color in
//        lights.addAll(colorCenter.gradient(backLight, SColor.ALICE_BLUE, 64, Interpolation.sineOut));
        lightLevels = new float[76];
        float initial = lerpFloatColors(RememberedTile.memoryColorFloat, -0x1.7583e6p125F, 0.4f); // the float is SColor.AMUR_CORK_TREE
        for (int i = 0; i < 12; i++) {
            lightLevels[i] = lerpFloatColors(initial, -0x1.7583e6p125F, Interpolation.sineOut.apply(i / 12f)); // AMUR_CORK_TREE again
        }
        for (int i = 0; i < 64; i++) {
            lightLevels[12 + i] = lerpFloatColors(-0x1.7583e6p125F, -0x1.fff1ep126F,  Interpolation.sineOut.apply(i / 63f)); // AMUR_CORK_TREE , then ALICE_BLUE
        }

        startGame();
    }

    public static CharSequence style(CharSequence text) {
        return GDXMarkup.instance.styleString(text);
    }

    private void startGame() {
        mapSLayers.clear();
        mapSLayers.glyphs.clear();
        mapSLayers.animationCount = 0;
        creatures.clear();
        handBuilt = new HandBuilt(rng, mixer);
        map = new EpiMap(worldWidth, worldHeight);
        fovResult = new double[map.width][map.height];
        priorFovResult = new double[map.width][map.height];
        mapSLayers.addLayer();//first added panel adds at level 1, used for cases when we need "extra background"
        mapSLayers.addLayer();//next adds at level 2, used for the cursor line
        mapSLayers.addLayer();//next adds at level 3, used for effects
        IColoredString<Color> emptyICS = IColoredString.Impl.create();
        for (int i = 0; i < messageCount; i++) {
            messages.add(emptyICS);
        }
        fxHandler = new FxHandler(mapSLayers, 3, colorCenter, fovResult);
        message("Generating world.");
        worldGenerator = new WorldGenerator();
        map = worldGenerator.buildWorld(map.width, map.height, 1, handBuilt)[0];

        GreasedRegion floors = new GreasedRegion(map.opacities(), 0.999);

        player = mixer.buildPhysical(handBuilt.playerBlueprint);
        player.stats.get(Stat.VIGOR).set(99.0);
        player.stats.get(Stat.HUNGER).delta(-0.1);
        player.stats.get(Stat.HUNGER).min(0);
        player.stats.get(Stat.DEVOTION).actual(player.stats.get(Stat.DEVOTION).base() * 1.7);
        player.stats.values().forEach(lv -> lv.max(Double.max(lv.max(), lv.actual())));

        player.location = floors.singleRandom(rng);
        floors.remove(player.location);
        floors.copy().randomScatter(rng, 3)
                .forEach(c -> map.contents[c.x][c.y].add(mixer.applyModification(
                        mixer.buildWeapon(Weapon.randomPhysicalWeapon(++player.chaos).copy(), player.chaos),
                        GauntRNG.getRandomElement(++player.chaos, Element.allEnergy).weaponModification())));
        infoHandler.setPlayer(player);
        mapOverlayHandler.setPlayer(player);
        floors.randomScatter(rng, 5);
        for (Coord coord : floors) {
            if (map.contents[coord.x][coord.y].blockage == null) {
                Physical p = mixer.buildPhysical(GauntRNG.getRandomElement(rootChaos.nextLong(), Inclusion.values()));
                mixer.applyModification(p, handBuilt.makeAlive());
                if (SColor.saturationOfFloat(p.color) < 0.8f) {
                    p.color = SColor.floatGetHSV(SColor.hueOfFloat(p.color),
                        0.8f,
                        SColor.valueOfFloat(p.color), SColor.alphaOfFloat(p.color) * 0x1.011p-8f);
                }
                Physical pMeat = mixer.buildPhysical(p);
                mixer.applyModification(pMeat, handBuilt.makeMeats());
                WeightedTableWrapper<Physical> pt = new WeightedTableWrapper<>(p.chaos, pMeat, 1.0, 2, 4);
                p.physicalDrops.add(pt);
                p.location = coord;
                map.contents[coord.x][coord.y].add(p);
                creatures.put(coord, p);
            }
        }

        player.appearance = mapSLayers.glyph(player.symbol, player.color, player.location.x, player.location.y);

        calcFOV(player.location.x, player.location.y);

        toPlayerDijkstra = new DijkstraMap(map.simpleChars(), DijkstraMap.Measurement.EUCLIDEAN);
        toPlayerDijkstra.rng = new RNG(); // random seed, player won't make deterministic choices
        blocked = new GreasedRegion(map.width, map.height);
        calcDijkstra();

        contextHandler.message("Have fun!",
                "The fates of countless worlds rest on you...",
                style("Bump into statues ([*][/]s[,]) and stuff."),
                style("Now [/]90% fancier[/]!"),
                "Use ? for help, or q to quit.",
                "Use mouse, numpad, or arrow keys to move.");
        processingCommand = false; // let the player do input
        infoHandler.showPlayerHealthAndArmor();
        putMap();
    }

    private void runTurn() {
        int size = creatures.size();
        for (int i = 0; i < size; i++) {
            final Physical creature = creatures.getAt(i);
            Coord c = creature.location;
            if (creature.stats.get(Stat.MOBILITY).actual() > 0 && (fovResult[c.x][c.y] > 0)) {
                List<Coord> path = toPlayerDijkstra.findPathPreScanned(c);
                if (path != null && path.size() > 1) {
                    Coord step = path.get(path.size() - 2);
                    if(player.location.x == step.x && player.location.y == step.y)
                    {
                        mapSLayers.bump(creature.appearance, c.toGoTo(player.location), 0.13f);
                        ActionOutcome ao = ActionOutcome.attack(creature, player);
                        if (ao.hit) {
                            int amt = ao.actualDamage >> 1;
                            Element element = creature.weaponData.elements.random();
                            applyStatChange(player, Stat.VIGOR, amt);
                            amt *= -1; // flip sign for output message
                            if (player.stats.get(Stat.VIGOR).actual() <= 0) {
                                if(ao.crit)
                                    message(Messaging.transform("The " + creature.name + " [Blood]brutally[] slay$ you with " +
                                            amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                else
                                    message(Messaging.transform("The " + creature.name + " slay$ you with " +
                                        amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                            } else {
                                if(ao.crit) {
                                    mapSLayers.wiggle(player.appearance, 0.3f);
                                    message(Messaging.transform("The " + creature.name + " [CW Bright Orange]critically[] " + element.verb + " you for " +
                                            amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                }
                                else
                                {
                                    message(Messaging.transform("The " + creature.name + " " + element.verb + " you for " +
                                            amt + " " + element.styledName + " damage!", player.name, Messaging.NounTrait.NO_GENDER));
                                }
                                if(ao.targetConditioned)
                                {
                                    player.overlaySymbol = '~';//'ʻ';
                                    player.overlayColor = element.floatColor;
                                    if(player.overlayAppearance != null) mapSLayers.removeGlyph(player.overlayAppearance);
                                    player.overlayAppearance = mapSLayers.glyph(player.overlaySymbol, player.overlayColor, step.x, step.y);
                                }

                            }
                        } else
                        {
                            if(ao.crit)
                                message("The " + creature.name + " missed you, but just barely.");
                            else
                                message("The " + creature.name + " missed you.");
                        }
                    }
                    else if (map.contents[step.x][step.y].blockage == null && !creatures.containsKey(step)) {
                        map.contents[c.x][c.y].remove(creature);
                        if (creature.appearance == null) {
                            creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, c.x, c.y);
                            if(creature.overlaySymbol != null)
                                creature.overlayAppearance = mapSLayers.glyph(creature.overlaySymbol, creature.overlayColor, c.x, c.y);
                        }
                        creatures.alterAt(i, step);
                        creature.location = step;
                        map.contents[step.x][step.y].add(creature);
                        mapSLayers.slide(creature.appearance, c.x, c.y, step.x, step.y, 0.145f, null);
                        if(creature.overlayAppearance != null)
                            mapSLayers.slide(creature.overlayAppearance, c.x, c.y, step.x, step.y, 0.145f, null);
                    }
                }
            }
        }

        // Update all the stats in motion
        OrderedMap<ImmutableKey, Double> changes = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
        for (Entry<ImmutableKey, LiveValue> entry : player.stats.entrySet()) {
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

    private void applyStatChange(Physical target, Map<ImmutableKey, Double> amounts) {
        OrderedMap<ImmutableKey, Double> changes = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
        for (Entry<ImmutableKey, LiveValue> entry : target.stats.entrySet()) {
            Double amount = amounts.get(entry.getKey());
            if (amount != null) {
                changes.put(entry.getKey(), amount);
                entry.getValue().addActual(amount);
            }
        }
        for (Stat s : Stat.rolloverProcessOrder) {
            LiveValue lv = target.stats.get(s);
            if (lv == null){
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
    }

    private void applyStatChange(Physical target, Stat stat, double amount) {
        OrderedMap<ImmutableKey, Double> changes = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
        changes.put(stat, amount);
        target.stats.get(stat).addActual(amount);
        for (Stat s : Stat.rolloverProcessOrder) {
            LiveValue lv = target.stats.get(s);
            if (lv == null){
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
    }

    private void clearContents(SquidLayers layers, Color background) {
        int w = layers.getGridWidth();
        int h = layers.getGridHeight();
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                layers.put(x, y, ' ', SColor.TRANSPARENT, background);
            }
        }
    }

    private void clearAndBorder(SquidLayers layers, Color borderColor, Color background) {
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

    private void message(String text) {
        clearAndBorder(messageSLayers, SColor.APRICOT, unseenColor);
        messages.set(messageIndex++ % messageCount, GDXMarkup.instance.colorString("[]"+text));
        for (int i = messageIndex % messageCount, c = 0; c < messageCount; i = (i + 1) % messageCount, c++) {
            messageSLayers.getForegroundLayer().put(1, 1 + c, messages.get(i));
        }
    }

    private void calcFOV(int checkX, int checkY) {
        FOV.reuseFOV(map.opacities(), fovResult, checkX, checkY, player.stats.get(Stat.SIGHT).actual(), Radius.CIRCLE);
        Physical creature;
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                if (fovResult[x][y] > 0) {
                    posrng.move(x, y);
                    if (map.remembered[x][y] == null) {
                        map.remembered[x][y] = new RememberedTile(map.contents[x][y]);
                    } else {
                        map.remembered[x][y].remake(map.contents[x][y]);
                    }
                    if ((creature = creatures.get(Coord.get(x, y))) != null) {
                        if (creature.appearance == null)
                            creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, x, y);
                        else if (!mapSLayers.glyphs.contains(creature.appearance)) {
                            mapSLayers.glyphs.add(creature.appearance);
                            if(creature.overlayAppearance != null)
                                mapSLayers.glyphs.add(creature.overlayAppearance);
                        }
                    }
                } else if ((creature = creatures.get(Coord.get(x, y))) != null && creature.appearance != null) {
                    mapSLayers.removeGlyph(creature.appearance);
                    if(creature.overlayAppearance != null)
                        mapSLayers.removeGlyph(creature.overlayAppearance);
                }
            }
        }
    }

    private void mixFOV(int checkX, int checkY) {
        for (int i = 0; i < priorFovResult.length; i++) {
            System.arraycopy(fovResult[i], 0, priorFovResult[i], 0, priorFovResult[0].length);
        }
        calcFOV(checkX, checkY);
        for (int x = 0; x < fovResult.length; x++) {
            for (int y = 0; y < fovResult[0].length; y++) {
                double found = fovResult[x][y];
                fovResult[x][y] = Double.max(found, priorFovResult[x][y]);
                priorFovResult[x][y] = found;
            }
        }
    }

    private void calcDijkstra() {
        toPlayerDijkstra.clearGoals();
        toPlayerDijkstra.resetMap();
        toPlayerDijkstra.setGoal(player.location);
        toPlayerDijkstra.scan(blocked);
    }

    private void equipItem() {
        if (player.inventory.isEmpty()) {
            message("Nothing equippable found.");
        } else {
            GauntRNG.shuffleInPlace(player.chaos += player.inventory.size() - 1, player.inventory);
            for (int i = 0; i < player.inventory.size(); i++) {
                Physical chosen = player.inventory.get(i);
                if(chosen.weaponData != null)
                {
                    equipItem(chosen);
                    return;
                }
            }
            if(player.statEffects.contains(player.weaponData.calcStats))
                player.statEffects.alter(player.weaponData.calcStats, (player.weaponData = player.unarmedData).calcStats);
            else
                player.statEffects.add((player.weaponData = player.unarmedData).calcStats);

        }
    }
    public static final List<WieldSlot> RIGHT = Collections.singletonList(WieldSlot.RIGHT_HAND),
            LEFT = Collections.singletonList(WieldSlot.LEFT_HAND),
            BOTH = Arrays.asList(WieldSlot.RIGHT_HAND, WieldSlot.LEFT_HAND);

    private void equipItem(Physical item) {
        if(player.statEffects.contains(player.weaponData.calcStats))
            player.statEffects.alter(player.weaponData.calcStats, (player.weaponData = item.weaponData).calcStats);
        else
            player.statEffects.add((player.weaponData = item.weaponData).calcStats);
        switch (item.weaponData.hands)
        {
            case 2: player.equip(item, BOTH);
            break;
            default: player.equip(item, RIGHT);
        }
    }

    private void scheduleMove(Direction dir)
    {
        awaitedMoves.add(player.location.translate(dir));
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     */
    private void move(Direction dir) {

        int newX = player.location.x + dir.deltaX;
        int newY = player.location.y + dir.deltaY;
        Coord newPos = Coord.get(newX, newY);
        if (!map.inBounds(newX, newY)) {
            return; // can't move, should probably be error or something
        }

        if (map.contents[newX][newY].blockage == null) {
            mapSLayers.slide(player.appearance, player.location.x, player.location.y, newX, newY, 0.145f, () ->
            {
                calcFOV(newX, newY);
                calcDijkstra();
                runTurn();
            });
            if(player.overlayAppearance != null)
                mapSLayers.slide(player.overlayAppearance, player.location.x, player.location.y, newX, newY, 0.145f, null);
            player.location = newPos;
            sound.playFootstep();
        } else {
            Physical thing = map.contents[newX][newY].getCreature();
            if (thing != null) {
                awaitedMoves.clear(); // don't keep moving if something hit
                toCursor.clear();
                mapSLayers.bump(player.appearance, dir, 0.145f);
                ActionOutcome ao = ActionOutcome.attack(player, thing);
                if (ao.hit) {
                    Element element = player.weaponData.elements.random();
                    applyStatChange(thing, Stat.VIGOR, ao.actualDamage);
                    if (thing.stats.get(Stat.VIGOR).actual() <= 0) {
                        mapSLayers.removeGlyph(thing.appearance);
                        if (thing.overlayAppearance != null) {
                            mapSLayers.removeGlyph(thing.overlayAppearance);
                        }
                        creatures.remove(thing.location);
                        map.contents[newX][newY].remove(thing);
                        if (ao.crit) {
                            Stream.concat(thing.physicalDrops.stream(), thing.elementDrops.getOrDefault(element, Collections.emptyList()).stream())
                                .map(table -> {
                                    int quantity = table.quantity();
                                    Physical p = mixer.buildPhysical(table.random());
                                    if (p.groupingData != null) {
                                        p.groupingData.quantity += quantity;
                                    } else {
                                        p.groupingData = new Grouping(quantity);
                                    }
                                    return p;
                                })
                                .forEach(item -> {
                                    map.contents[newX][newY].add(item);
                                    if (map.resistances[newX + GauntRNG.between(player.chaos + 10, -1, 2)][newY + GauntRNG.between(player.chaos + 11, -1, 2)] < 0.9) {
                                        map.contents[newX + GauntRNG.between(player.chaos + 10, -1, 2)][newY + GauntRNG.between(player.chaos + 11, -1, 2)].add(item);
                                    }
                                });
                            mapSLayers.burst(newX, newY, 1, Radius.CIRCLE, thing.appearance.shown, thing.color, SColor.translucentColor(thing.color, 0f), 1);
                            message("You [Blood]brutally[] defeat the " + thing.name + " with " + -ao.actualDamage + " " + element.styledName + " damage!");
                        } else {
                            Stream.concat(thing.physicalDrops.stream(), thing.elementDrops.getOrDefault(element, Collections.emptyList()).stream())
                                .map(table -> {
                                    int quantity = table.quantity();
                                    Physical p = mixer.buildPhysical(table.random());
                                    if (p.groupingData != null) {
                                        p.groupingData.quantity += quantity;
                                    } else {
                                        p.groupingData = new Grouping(quantity);
                                    }
                                    return p;
                                })
                                .forEach(item -> map.contents[newX][newY].add(item));
                            mapSLayers.burst(newX, newY, 1, Radius.CIRCLE, thing.appearance.shown, thing.color, SColor.translucentColor(thing.color, 0f), 1);
                            message("You defeat the " + thing.name + " with " + -ao.actualDamage + " " + element.styledName + " damage!");
                        }
                    } else {
                        String amtText = String.valueOf(-ao.actualDamage);
                        int startX = newX - (amtText.length() >> 1);
                        for (int i = 0; i < amtText.length(); i++, startX++) {
                            mapSLayers.summon(startX, newY, startX + 1, newY - 1, amtText.charAt(i), element.floatColor, SColor.translucentColor(element.floatColor, 0f), 1f);
                        }
                        if(ao.crit)
                        {
                            mapSLayers.wiggle(thing.appearance, 0.3f);
                            message(Messaging.transform("You [CW Bright Orange]critically[] " + element.verb + " the " + thing.name + " for " +
                                    amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                        }
                        else
                        {
                            message(Messaging.transform("You " + element.verb + " the " + thing.name + " for " +
                                    amtText + " " + element.styledName + " damage!", "you", Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                        }
                        if(ao.targetConditioned)
                        {
                            thing.overlaySymbol = '˝';
                            thing.overlayColor = element.floatColor;
                            if(thing.overlayAppearance != null) mapSLayers.removeGlyph(thing.overlayAppearance);
                            thing.overlayAppearance = mapSLayers.glyph(thing.overlaySymbol, thing.overlayColor, newX, newY);
                        }

                    }
                } else {
                    message("Missed the " + thing.name + (ao.crit ? ", but just barely." : "..."));
                }
                calcFOV(player.location.x, player.location.y);
                calcDijkstra();
                runTurn();
            } else if ((thing = map.contents[newX][newY].getLargeNonCreature()) != null) {
                awaitedMoves.clear(); // don't keep moving if something hit
                toCursor.clear();
                message("Ran into " + thing.name);
                runTurn();
            } else {
                runTurn();
            }
        }
    }

    public void putWithLight(int x, int y, char c, float foreground, float lightAmount, float noise) {
        //float base = 1 - RememberedTile.frontFade;
        //float front = lerpFloatColors(RememberedTile.memoryColorFloat, foreground, base + RememberedTile.frontFade * lightAmount); // objects don't get lit, just a fade to memory
        // The NumberTools.swayTight call here helps increase the randomness in a way that isn't directly linked to the other parameters.
        // By multiplying noise by pi here, it removes most of the connection between swayTight's result and the other calculations involving noise.
        lightAmount = Math.max(0, Math.min(lightAmount - NumberTools.swayTight(noise * 3.141592f) * 0.1f - 0.1f + 0.2f * noise, lightAmount)); // 0.1f * noise for light theme, 0.2f * noise for dark theme
        int n = (int)(lightAmount * lightLevels.length);
        n = Math.min(Math.max(n, 0), lightLevels.length - 1);
        //float back = lightLevels[n]; // background gets both lit and faded to memory
        //mapSLayers.put(x, y, c, front, back); // "light" theme
        mapSLayers.put(x, y, c, lerpFloatColors(foreground, lightLevels[n], 0.5f), RememberedTile.memoryColorFloat); // "dark" theme
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap() {
        float time = (System.currentTimeMillis() & 0xffffffL) * 0.00125f; // if you want to adjust the speed of flicker, change the multiplier
        long time0 = Noise.longFloor(time);

        // we can use either Noise.querp (quintic Hermite spline) or Noise.cerp (cubic Hermite splne); cerp is cheaper but querp seems to look better.
        // querp() is extremely close to cos(); see https://www.desmos.com/calculator/l31nflff3g for graphs. It is likely that querp performs better than cos.
        float noise = Noise.querp(NumberTools.randomFloatCurved(time0), NumberTools.randomFloatCurved(time0 + 1L), time - time0);
        Physical creature;
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                float sightAmount = (float) fovResult[x][y];
                if (sightAmount > 0) {
                    EpiTile tile = map.contents[x][y];
                    mapSLayers.clear(x, y, 1);
                    // sightAmount should only be 1.0 if the player is standing in that cell, currently
                    if ((creature = creatures.get(Coord.get(x, y))) != null ) {
                        putWithLight(x, y, ' ', 0f, sightAmount, noise);
                        creature.appearance.color = lerpFloatColors(unseenCreatureColorFloat, creature.color, 0.5f + 0.35f * sightAmount);
                        if(creature.overlayAppearance != null)
                            creature.overlayAppearance.color = lerpFloatColors(unseenCreatureColorFloat, creature.overlayColor, 0.5f + 0.35f * sightAmount);
                        mapSLayers.clear(x, y, 0);
                        if (!creature.wasSeen) { // stop auto-move if a new creature pops into view
                            awaitedMoves.clear();
                            toCursor.clear();
                        }
                        creature.wasSeen = true;
                    } else {
                        posrng.move(x, y);
                        putWithLight(x, y, tile.getSymbol(), tile.getForegroundColor(), sightAmount, noise);
                    }
                } else {
                    RememberedTile rt = map.remembered[x][y];
                    if (rt != null) {
                        mapSLayers.clear(x, y);
                        mapSLayers.put(x, y, rt.symbol, rt.front, rt.back, 0);
                    }
                }
            }
        }

        mapSLayers.clear(player.location.x, player.location.y, 0);

        mapSLayers.clear(2);
        for (int i = 0; i < toCursor.size(); i++) {
            Coord c = toCursor.get(i);
            Direction dir;
            if (i == toCursor.size() - 1){
                dir = Direction.NONE; // last spot shouldn't have arrow
            } else if (i == 0) {
                dir = Direction.toGoTo(player.location, c);
            }else {
                dir = Direction.toGoTo(toCursor.get(i - 1), c);
            }
            mapSLayers.put(c.x, c.y, Utilities.arrowsFor(dir).charAt(0), SColor.CW_PURPLE, null, 2);
        }
    }

    @Override
    public void render() {
        super.render();

        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(unseenColor.r, unseenColor.g, unseenColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapStage.getCamera().position.x = player.appearance.getX();
        mapStage.getCamera().position.y = player.appearance.getY();
        putMap();
        // if the user clicked, we have a list of moves to perform.
        if (!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Points from awaitedMoves.
            if (!mapSLayers.hasActiveAnimations()) {
                Coord m = awaitedMoves.remove(0);
                if(!toCursor.isEmpty())
                    toCursor.remove(0);
                move(Direction.toGoTo(player.location, m));
                infoHandler.updateDisplay();
            }
        } else if (mapInput.hasNext()) {// if we are waiting for the player's input and get input, process it.
            mapInput.next();
            infoHandler.updateDisplay();
        } else if (contextInput.hasNext()) {
            contextInput.next();
            infoHandler.updateDisplay();
        } else if (infoInput.hasNext()) {
            infoInput.next();
            infoHandler.updateDisplay();
        }

        // the order here matters. We apply multiple viewports at different times to clip different areas.
        contextViewport.apply(false);
        contextStage.act();
        contextStage.draw();

        infoViewport.apply(false);
        infoStage.act();
        infoStage.draw();

        messageViewport.apply(false);
        messageStage.act();
        messageStage.draw();

        //here we apply the other viewport, which clips a different area while leaving the message area intact.
        mapViewport.apply(false);
        mapStage.act();
        //we use a different approach here because we can avoid ending the batch by setting this matrix outside a batch
        batch.setProjectionMatrix(mapStage.getCamera().combined);
        //then we start a batch and manually draw the stage without having it handle its batch...
        batch.begin();
        mapSLayers.font.configureShader(batch);
        mapStage.getRoot().draw(batch, 1f);
        //so we can draw the actors independently of the stage while still in the same batch
        //player.appearance.draw(batch, 1.0f);
        //we still need to end
        batch.end();

        mapOverlayStage.act();
        mapOverlayStage.draw();

        //uncomment the upcoming line if you want to see how fast this can run at top speed...
        //for me, tommyettinger, on a laptop with recent integrated graphics, I get about 500 FPS.
        //this needs vsync set to false in DesktopLauncher.
        //Gdx.graphics.setTitle(Gdx.graphics.getFramesPerSecond() + " FPS");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        float currentZoomX = (float) width / totalPixelWidth();
        float currentZoomY = (float) height / totalPixelHeight();

        messageSLayers.setBounds(0, 0, currentZoomX * messageSize.pixelWidth(), currentZoomY * messageSize.pixelHeight());
        contextSLayers.setBounds(0, 0, currentZoomX * contextSize.pixelWidth(), currentZoomY * contextSize.pixelHeight());
        infoSLayers.setBounds(0, 0, currentZoomX * infoSize.pixelWidth(), currentZoomY * infoSize.pixelHeight());

        // SquidMouse turns screen positions to cell positions, and needs to be told that cell sizes have changed
        // a quirk of how the camera works requires the mouse to be offset by half a cell if the width or height is odd
        mapMouse.reinitialize(currentZoomX * mapSize.cellWidth, currentZoomY * mapSize.cellHeight,
            mapSize.gridWidth, mapSize.gridHeight,
            (mapSize.gridWidth & 1) * (int) (mapSize.cellWidth * currentZoomX * -0.5f),
            (mapSize.gridHeight & 1) * (int) (mapSize.cellHeight * currentZoomY * -0.5f));
        equipmentMouse.reinitialize(currentZoomX * mapSize.cellWidth, currentZoomY * mapSize.cellHeight,
            mapSize.gridWidth, mapSize.gridHeight,
            (mapSize.gridWidth & 1) * (int) (mapSize.cellWidth * currentZoomX * -0.5f),
            (mapSize.gridHeight & 1) * (int) (mapSize.cellHeight * currentZoomY * -0.5f));
        contextMouse.reinitialize(currentZoomX * contextSize.cellWidth, currentZoomY * contextSize.cellHeight,
            contextSize.gridWidth, contextSize.gridHeight,
            -(int) (messageSLayers.getRight()),
            -(int) (infoSLayers.getTop() + infoSize.cellHeight * currentZoomY));
        infoMouse.reinitialize(currentZoomX * infoSize.cellWidth, currentZoomY * infoSize.cellHeight,
            infoSize.gridWidth, infoSize.gridHeight,
            -(int) (messageSLayers.getRight()), -(int)(infoSize.cellHeight * currentZoomY));

        contextViewport.update(width, height, false);
        contextViewport.setScreenBounds((int) (currentZoomX * mapSize.pixelWidth()), 0,
            (int) (currentZoomX * contextSize.pixelWidth()), (int) (currentZoomY * contextSize.pixelHeight()));

        infoViewport.update(width, height, false);
        infoViewport.setScreenBounds((int) (currentZoomX * mapSize.pixelWidth()), (int) (currentZoomY * contextSize.pixelHeight()),
            (int) (currentZoomX * infoSize.pixelWidth()), (int) (currentZoomY * infoSize.pixelHeight()));

        messageViewport.update(width, height, false);
        messageViewport.setScreenBounds(0, 0,
            (int) (currentZoomX * messageSize.pixelWidth()), (int) (currentZoomY * messageSize.pixelHeight()));

        mapViewport.update(width, height, false);
        mapViewport.setScreenBounds(0, (int) (currentZoomY * messageSize.pixelHeight()),
                width - (int) (currentZoomX * infoSize.pixelWidth()), height - (int) (currentZoomY * messageSize.pixelHeight()));

        mapOverlayViewport.update(width, height, false);
        mapOverlayViewport.setScreenBounds(0, (int) (currentZoomY * messageSize.pixelHeight()),
                width - (int) (currentZoomX * infoSize.pixelWidth()), height - (int) (currentZoomY * messageSize.pixelHeight()));
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
        super.dispose();
    }

    public static int totalPixelWidth() {
        return mapSize.pixelWidth() + infoSize.pixelWidth();
    }

    public static int totalPixelHeight() {
        return mapSize.pixelHeight() + messageSize.pixelHeight();
    }

    private final KeyHandler mapKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            if(combined == (0x60000 | SquidInput.BACKSPACE))
            {
                startGame();
                return;
            }
            Verb verb = ControlMapping.defaultMapViewMapping.get(combined);
            if (verb == null){
                message("Unknown input for map mode: " + key);
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    scheduleMove(Direction.DOWN);
                    return;
                case MOVE_UP:
                    scheduleMove(Direction.UP);
                    return;
                case MOVE_LEFT:
                    scheduleMove(Direction.LEFT);
                    return;
                case MOVE_RIGHT:
                    scheduleMove(Direction.RIGHT);
                    return;
                case MOVE_DOWN_LEFT:
                    scheduleMove(Direction.DOWN_LEFT);
                    return;
                case MOVE_DOWN_RIGHT:
                    scheduleMove(Direction.DOWN_RIGHT);
                    return;
                case MOVE_UP_LEFT:
                    scheduleMove(Direction.UP_LEFT);
                    return;
                case MOVE_UP_RIGHT:
                    scheduleMove(Direction.UP_RIGHT);
                    return;
                case OPEN: // Open all the doors nearby
                    message("Opening nearby doors");
                    Arrays.stream(Direction.OUTWARDS)
                        .map(d -> player.location.translate(d))
                        .filter(c -> map.inBounds(c))
                        .filter(c -> fovResult[c.x][c.y] > 0)
                        .flatMap(c -> map.contents[c.x][c.y].contents.stream())
                        .filter(p -> p.countsAs(handBuilt.baseClosedDoor))
                        .forEach(p -> mixer.applyModification(p, handBuilt.openDoor));
                    calcFOV(player.location.x, player.location.y);
                    calcDijkstra();
                    break;
                case SHUT: // Close all the doors nearby
                    message("Closing nearby doors");
                    Arrays.stream(Direction.OUTWARDS)
                        .map(d -> player.location.translate(d))
                        .filter(c -> map.inBounds(c))
                        .filter(c -> fovResult[c.x][c.y] > 0)
                        .flatMap(c -> map.contents[c.x][c.y].contents.stream())
                        .filter(p -> p.countsAs(handBuilt.baseOpenDoor))
                        .forEach(p -> mixer.applyModification(p, handBuilt.closeDoor));
                    calcFOV(player.location.x, player.location.y);
                    calcDijkstra();
                    break;
                case GATHER: // Pick everything nearby up
                    message("Picking up all nearby small things");
                    for (int i = 0; i < 8; i++) {
                        Coord c = player.location.translate(Direction.OUTWARDS[i]);
                        if (map.inBounds(c) && fovResult[c.x][c.y] > 0) {
                            EpiTile tile = map.contents[c.x][c.y];
                            ListIterator<Physical> it = tile.contents.listIterator();
                            Physical p;
                            while (it.hasNext()) {
                                p = it.next();
                                if (p.attached || p.creatureData != null) {
                                    continue;
                                }
                                player.addToInventory(p);
                                it.remove();
                            }
                        }
                    }
                    break;
                case EQUIPMENT:
                    mapOverlayHandler.setMode(PrimaryMode.EQUIPMENT);
                    mapInput.setKeyHandler(equipmentKeys);
                    toCursor.clear();
                    mapInput.setMouse(equipmentMouse);
                    break;
                case DRAW:
                    equipItem();
                    break;
                case DROP:
                    message("Dropping all held items");
                    for (Physical dropped : player.unequip(BOTH)) {
                        for (int i = 0, offset = GauntRNG.next(++player.chaos, 3); i < 8; i++) {
                            Coord c = player.location.translate(Direction.OUTWARDS[i + offset & 7]);
                            if (map.inBounds(c) && fovResult[c.x][c.y] > 0) {
                                map.contents[c.x][c.y].add(dropped);
                                break;
                            }
                        }
                    }
                    break;
                case CONTEXT_PRIOR:
                    contextHandler.prior();
                    break;
                case CONTEXT_NEXT:
                    contextHandler.next();
                    break;
                case INFO_PRIOR:
                    infoHandler.prior();
                    break;
                case INFO_NEXT:
                    infoHandler.next();
                    break;
                case HELP:
                    mapOverlayHandler.setMode(PrimaryMode.HELP);
                    mapInput.setKeyHandler(helpKeys);
                    toCursor.clear();
                    mapInput.setMouse(helpMouse);
                    break;
                case QUIT:
                    // TODO - confirmation
                    Gdx.app.exit();
                    return;
                case WAIT:
                    scheduleMove(Direction.NONE);
                    return;
                default:
                    message("Can't " + verb.name + " from main view.");
                    return;
            }

            // check if the turn clock needs to run
            if (verb.isAction()){
                runTurn();
            }
        }
    };

    private final KeyHandler equipmentKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.defaultEquipmentViewMapping.get(combined);
            if (verb == null){
                message("Unknown input for equipment mode: " + key);
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    mapOverlayHandler.moveDown();
                    break;
                case MOVE_UP:
                    mapOverlayHandler.moveUp();
                    break;
                case MOVE_LEFT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case MOVE_RIGHT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case MOVE_DOWN_LEFT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case MOVE_DOWN_RIGHT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case MOVE_UP_LEFT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case MOVE_UP_RIGHT:
                    // TODO - keyboard controls in equipment screen
                    break;
                case DRAW:
                    equipItem();
                    mapOverlayHandler.updateDisplay();
                    break;
                case INTERACT:
                    Physical selected = mapOverlayHandler.getSelected();
                    if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
                        message("Interactions for " + selected.name + ": " + selected.interactableData
                            .stream()
                            .map(interact -> interact.phrasing)
                            .collect(Collectors.joining(", ")));
                        Interactable interaction = selected.interactableData.get(0);
                        if (interaction.consumes){
                            player.removeFromInventory(selected);
                        }
                        interaction.effects
                            .stream()
                            .flatMap(s -> s.sourceModifications.stream())
                            .forEachOrdered(mod -> mixer.applyModification(player, mod));
                        mapOverlayHandler.updateDisplay();
                    } else if (selected.countsAs(handBuilt.rawMeat)) {
                        player.removeFromInventory(selected);
                        List<Physical> steaks = mixer.mix(handBuilt.steakRecipe, Collections.singletonList(selected), Collections.emptyList());
                        player.inventory.addAll(steaks);
                        mapOverlayHandler.updateDisplay();
                        message("Made " + steaks.size() + " steaks.");
                    } else {
                        message("No interaction for " + selected.name);
                    }
                    break;
                case CONTEXT_PRIOR:
                    contextHandler.prior();
                    break;
                case CONTEXT_NEXT:
                    contextHandler.next();
                    break;
                case INFO_PRIOR:
                    infoHandler.prior();
                    break;
                case INFO_NEXT:
                    infoHandler.next();
                    break;
                case HELP:
                    mapOverlayHandler.setMode(PrimaryMode.HELP);
                    mapInput.setKeyHandler(helpKeys);
                    mapInput.setMouse(helpMouse);
                    break;
                case EQUIPMENT:
                case UI_CLOSE_WINDOW:
                    mapInput.setKeyHandler(mapKeys);
                    mapInput.setMouse(mapMouse);
                    mapOverlayHandler.hide();
                    break;
                default:
                    message("Can't " + verb.name + " from equipment view.");
                    break;
            }
        }
    };

    private final KeyHandler helpKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.defaultHelpViewMapping.get(combined);
            if (verb == null){
                message("Unknown input for help mode: " + key);
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    mapOverlayHandler.moveDown();
                    break;
                case MOVE_UP:
                    mapOverlayHandler.moveUp();
                    break;
                case MOVE_LEFT:
                    // TODO - keyboard controls in help screen
                    break;
                case MOVE_RIGHT:
                    // TODO - keyboard controls in help screen
                    break;
                case MOVE_DOWN_LEFT:
                    // TODO - keyboard controls in help screen
                    break;
                case MOVE_DOWN_RIGHT:
                    // TODO - keyboard controls in help screen
                    break;
                case MOVE_UP_LEFT:
                    // TODO - keyboard controls in help screen
                    break;
                case MOVE_UP_RIGHT:
                    // TODO - keyboard controls in help screen
                    break;
                case INFO_PRIOR:
                    infoHandler.prior();
                    break;
                case INFO_NEXT:
                    infoHandler.next();
                    break;
                case EQUIPMENT:
                    mapOverlayHandler.setMode(PrimaryMode.EQUIPMENT);
                    mapInput.setKeyHandler(equipmentKeys);
                    mapInput.setMouse(equipmentMouse);
                    break;
                case HELP:
                case UI_CLOSE_WINDOW:
                    mapInput.setKeyHandler(mapKeys);
                    mapInput.setMouse(mapMouse);
                    mapOverlayHandler.hide();
                    break;
                default:
                    message("Can't " + verb.name + " from help view.");
                    break;
            }
        }
    };
    private final KeyHandler debugKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            switch (key) {
                case 'x':
                    fxHandler.sectorBlast(player.location, Element.ACID, 7, Radius.CIRCLE);
                    break;
                case 'X':
                    Element e = rng.getRandomElement(Element.allEnergy);
                    fxHandler.zapBoom(player.location, player.location.translateCapped(rng.between(-20, 20), rng.between(-10, 10), map.width, map.height), e);
                    break;
                case 'z':
                    fxHandler.staticStorm(player.location, Element.ICE, 7, Radius.CIRCLE);
                    break;
                case 'Z':
                    for (Coord c : rng.getRandomUniqueCells(0, 0, mapSize.gridWidth, mapSize.gridHeight, 400)) {
                        fxHandler.twinkle(c, Element.LIGHT);
                    }
                    break;
                case '=':
                    fxHandler.layeredSparkle(player.location, 4, Radius.CIRCLE);
                    break;
                case '+':
                    fxHandler.layeredSparkle(player.location, 8, Radius.CIRCLE);
                    break;

            }
        }
    };

    private final SquidMouse equipmentMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, 0, 0, new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false; // No-op for now
        }
    });

    private final SquidMouse helpMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, 0, 0, new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false; // No-op for now
        }
    });

    private final SquidMouse mapMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, 0, 0, new InputAdapter() {
        // if the user clicks within FOV range and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            screenX += player.location.x - (mapSize.gridWidth >> 1);
            screenY += player.location.y - (mapSize.gridHeight >> 1);
            if (screenX < 0 || screenY < 0 || screenX >= map.width || screenY >= map.height) {
                return false;
            }
            switch (button) {
                case Input.Buttons.LEFT:
                    if (awaitedMoves.isEmpty()) {
                        if (toCursor.isEmpty()) {
                            cursor = Coord.get(screenX, screenY);
                            toCursor = toPlayerDijkstra.findPathPreScanned(cursor);
                            if (!toCursor.isEmpty()) {
                                toCursor = toCursor.subList(1, toCursor.size()); // Remove cell you're in from list
                            }
                        }
                        awaitedMoves.addAll(toCursor);
                    }
                    break;
                case Input.Buttons.RIGHT:
                    contextHandler.tileContents(Coord.get(screenX, screenY), map.contents[screenX][screenY]);
                    break;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return mouseMoved(screenX, screenY);
        }

        // causes the path to the mouse position to become highlighted (toCursor contains a list of points that
        // receive highlighting). Uses DijkstraMap.findPath() to find the path, which is surprisingly fast.
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if (!awaitedMoves.isEmpty()) {
                return false;
            }
            screenX += player.location.x - (mapSize.gridWidth >> 1);
            screenY += player.location.y - (mapSize.gridHeight >> 1);

            // Check if the cursor didn't move in grid space
            if (cursor.x == screenX && cursor.y == screenY){
                return false;
            }
            
            if (screenX < 0 || screenX >= map.width || screenY < 0 || screenY >= map.height || fovResult[screenX][screenY] <= 0.0) {
                toCursor.clear(); // don't show path when mouse moves out of range or view
                return false;
            }
            cursor = Coord.get(screenX, screenY);
            toCursor = toPlayerDijkstra.findPathPreScanned(cursor);
            if (!toCursor.isEmpty()) {
                toCursor = toCursor.subList(1, toCursor.size());
            }
            return false;
        }
    });

    private final SquidMouse contextMouse = new SquidMouse(contextSize.cellWidth, contextSize.cellHeight, contextSize.gridWidth, contextSize.gridHeight,
            mapSize.gridWidth * mapSize.cellWidth, infoSize.gridHeight * infoSize.cellHeight + (infoSize.cellHeight >> 1), new InputAdapter() {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (screenX < 0 || screenX >= contextSize.gridWidth || screenY < 0 || screenY >= contextSize.gridHeight) {
                return false;
            }
            switch (button) {
                case Input.Buttons.LEFT:
                    if (screenX == contextHandler.arrowLeft.x && screenY == contextHandler.arrowLeft.y) {
                        contextHandler.prior();
                    } else if (screenX == contextHandler.arrowRight.x && screenY == contextHandler.arrowRight.y) {
                        contextHandler.next();
                    }
                    return true;
                case Input.Buttons.RIGHT:
                default:
                    return false;
            }
        }


        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return mouseMoved(screenX, screenY);
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }
    });

    private final SquidMouse infoMouse = new SquidMouse(infoSize.cellWidth, infoSize.cellHeight, infoSize.gridWidth, infoSize.gridHeight,
            mapSize.gridWidth * mapSize.cellWidth, contextSize.gridHeight * contextSize.cellHeight, new InputAdapter() {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            System.out.println("info: " + screenX + ", " + screenY);
            switch (button) {
                case Input.Buttons.LEFT:
                    if (screenX == infoHandler.arrowLeft.x && screenY == infoHandler.arrowLeft.y) {
                        infoHandler.prior();
                    } else if (screenX == infoHandler.arrowRight.x && screenY == infoHandler.arrowRight.y) {
                        infoHandler.next();
                    }
                    return true;
                case Input.Buttons.RIGHT:
                default:
                    return false;
            }
        }


        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return mouseMoved(screenX, screenY);
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }
    });
}
