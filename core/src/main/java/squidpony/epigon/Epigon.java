package squidpony.epigon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.ArrayTools;
import squidpony.Maker;
import squidpony.Messaging;
import squidpony.StringKit;
import squidpony.epigon.combat.ActionOutcome;
import squidpony.epigon.data.*;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.control.DataStarter;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.raw.RawCreature;
import squidpony.epigon.data.slot.WieldSlot;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.display.ContextHandler;
import squidpony.epigon.display.FallingHandler;
import squidpony.epigon.display.FxHandler;
import squidpony.epigon.display.InfoHandler;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.display.MapOverlayHandler.PrimaryMode;
import squidpony.epigon.display.PanelSize;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;
import squidpony.epigon.mapping.CastleGenerator;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.LocalAreaGenerator;
import squidpony.epigon.mapping.MapConstants;
import squidpony.epigon.mapping.MapDecorator;
import squidpony.epigon.mapping.RememberedTile;
import squidpony.epigon.mapping.WobblyCanyonGenerator;
import squidpony.epigon.util.Utilities;
import squidpony.panel.IColoredString;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.*;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidgrid.mapping.LineKit;
import squidpony.squidmath.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static squidpony.squidgrid.gui.gdx.SColor.*;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {
    private enum GameMode {
        DIVE, CRAWL;
        private final String name;

        GameMode() {
            name = Utilities.caps(name(), "_", " ");
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final PanelSize mapSize;
    public static final PanelSize messageSize;
    public static final PanelSize infoSize;
    public static final PanelSize contextSize;
    public static final int messageCount;
    public static final long seed = 0xBEEFEEDADBA77L;
    public final StatefulRNG rng = new StatefulRNG(seed);
    // meant to be used to generate seeds for other RNGs; can be seeded when they should be fixed
    public static final DiverRNG rootChaos = new DiverRNG();
    public final RecipeMixer mixer;
    private DataStarter dataStarter;
    private MapDecorator mapDecorator;
//    public static final char BOLD = '\0', ITALIC = '\0', REGULAR = '\0';
    public static final char BOLD = '\u4000', ITALIC = '\u8000', REGULAR = '\0';

    private GameMode mode = GameMode.CRAWL;

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
    private SparseLayers mapHoverSLayers;
    private SparseLayers mapOverlaySLayers;
    private SparseLayers infoSLayers;
    private SparseLayers contextSLayers;
    private SparseLayers messageSLayers;
//    private ShapeRenderer shaper;
    private SubcellLayers fallingSLayers;

    private InputSpecialMultiplexer multiplexer;
    private SquidInput mapInput;
    private SquidInput contextInput;
    private SquidInput infoInput;
    private SquidInput messageInput;
    private SquidInput debugInput;
    private SquidInput fallbackInput;
    private Color unseenColor;
    private float unseenCreatureColorFloat;
    private ArrayList<Coord> toCursor;
    private TextCellFactory font;

    private boolean showingMenu = false;
    private Coord menuLocation = null;
    private Physical currentTarget = null;
    private OrderedMap<String, Weapon> maneuverOptions = new OrderedMap<>(12);
    private OrderedMap<String, Interactable> interactionOptions = new OrderedMap<>(8);
    // Set up the text display portions
    private ArrayList<IColoredString<Color>> messages = new ArrayList<>();
    private int messageIndex;

    // World
    private LocalAreaGenerator worldGenerator;
    private CastleGenerator castleGenerator;
    private EpiMap[] world;
    private EpiMap map;
    private char[][] simple;
    private char[][] lineDungeon, prunedDungeon;
    private float[][] wallColors, walls;
    
    private int depth;
    private FxHandler fxHandler;
    private MapOverlayHandler mapOverlayHandler;
    private ContextHandler contextHandler;
    private InfoHandler infoHandler;
    private FallingHandler fallingHandler;
    private GreasedRegion blockage, floors;
    private DijkstraMap toPlayerDijkstra, monsterDijkstra;
    private LOS los;
    private Coord cursor;
    public Physical player;
    private ArrayList<Coord> awaitedMoves;
    private OrderedMap<Coord, Physical> creatures;
    private int autoplayTurns = 0;

    // Timing
    public static final long startMillis = TimeUtils.millis();
    private long fallDelay = 300;
    private Instant nextFall = Instant.now();
    private boolean paused = true;
    private Instant pausedAt = Instant.now();
    private Instant unpausedAt = Instant.now();
    private long inputDelay = 100;
    private Instant nextInput = Instant.now();
    private long fallDuration = 0L, currentFallDuration = 0L;

    private Stage mapStage, messageStage, infoStage, contextStage, mapOverlayStage, fallingStage;
    private Viewport mapViewport, messageViewport, infoViewport, contextViewport, mapOverlayViewport, fallingViewport;
    
    public static final int worldWidth, worldHeight, worldDepth, totalDepth;
    public float startingY, finishY, timeToFall;

    private static final boolean DEBUG = true;
    private boolean odinView = false;
    private GLProfiler glp;
    private StringBuilder tempSB = new StringBuilder(16);
    private Vector2 screenPosition = new Vector2(20, 20);
    public static final Radiance[] softWhiteChain = Radiance.makeChain(8, 1.2f, SColor.FLOAT_WHITE, 0.4f);

    // Set up sizing all in one place
    static {
        worldWidth = 80;
        worldHeight = 80;
        worldDepth = 10;
        totalDepth = worldDepth + MapConstants.DIVE_HEADER.length;
        int bigW = 102;//World.DIVE_HEADER[0].length() + 2;
        int bigH = 26;
        int smallW = 50;
        int smallH = 22;
        int cellW = 14;
        int cellH = 28;
        int bottomH = 8;
        mapSize = new PanelSize(bigW / 2, bigH, cellH, cellH);
        messageSize = new PanelSize(bigW, bottomH, cellW, cellH);
        infoSize = new PanelSize(smallW, smallH * 7 / 5, 9, 20);
        contextSize = new PanelSize(smallW, (bigH + bottomH - smallH) * 7 / 5, 9, 20);
//        contextSize = new PanelSize(smallW, bottomH * 7 / 5, 8, 16);
        messageCount = bottomH - 2;
    }

    public Epigon() {
        mixer = new RecipeMixer();
        //handBuilt = new DataStarter(mixer);
        Weapon.init();
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

        Coord.expandPoolTo(worldWidth + 1, Math.max(worldHeight, worldDepth + MapConstants.DIVE_HEADER.length) + 1);

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
        if(DEBUG) {
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
                worldWidth,
                worldHeight,
                mapSize.cellWidth,
                mapSize.cellHeight,
                font.copy().width(mapSize.cellWidth).height(mapSize.cellHeight).initBySize()){
            //we override draw() to center chars in their cells
            @Override
            public void draw(Batch batch, float parentAlpha) {
                //super.draw(batch, parentAlpha);
                float xo = getX(), yo = getY(), yOff = yo + 1f + gridHeight * font.actualCellHeight, gxo, gyo,
                        conditionY = 1f, conditionCw = 1f, conditionCm = 1f,
                        conditionYAdd = 0f, conditionCwAdd = 0f, conditionCmAdd = 0f;
                if(player.visualCondition == null) {
                    final int clen = player.conditions.size();
                    for (int i = clen - 1; i >= 0; i--) {
                        VisualCondition vis = player.conditions.getAt(i).parent.visual;
                        if (vis != null) {
                            vis.update();
                            conditionY *= vis.lumaMul;
                            conditionCw *= vis.warmMul;
                            conditionCm *= vis.mildMul;
                            conditionYAdd += vis.lumaAdd;
                            conditionCwAdd += vis.warmAdd;
                            conditionCmAdd += vis.mildAdd;
                            break;
                        }
                    }
                }
                else
                {
                    VisualCondition vis = player.visualCondition;
                    vis.update();
                    conditionY *= vis.lumaMul;
                    conditionCw *= vis.warmMul;
                    conditionCm *= vis.mildMul;
                    conditionYAdd += vis.lumaAdd;
                    conditionCwAdd += vis.warmAdd;
                    conditionCmAdd += vis.mildAdd;
                }
                filter.yMul  = 0.7f  * conditionY;
                filter.cwMul = 0.65f * conditionCw;
                filter.cmMul = 0.65f * conditionCm;
                filter.yAdd  = 0.7f  * conditionYAdd;
                filter.cwAdd = 0.65f * conditionCwAdd;
                filter.cmAdd = 0.65f * conditionCmAdd;
//                font.draw(batch, backgrounds, xo, yo);
                font.draw(batch, backgrounds, xo - font.actualCellWidth * 0.25f, yo);
                int len = layers.size();
                Frustum frustum = null;
                Stage stage = getStage();
                if(stage != null) {
                    Viewport viewport = stage.getViewport();
                    if(viewport != null)
                    {
                        Camera camera = viewport.getCamera();
                        if(camera != null)
                        {
                            if(
                                    camera.frustum != null &&
                                            (!camera.frustum.boundsInFrustum(xo, yOff - font.actualCellHeight - 1f, 0f, font.actualCellWidth, font.actualCellHeight, 0f) ||
                                                    !camera.frustum.boundsInFrustum(xo + font.actualCellWidth * (gridWidth-1), yo, 0f, font.actualCellWidth, font.actualCellHeight, 0f))
                            )
                                frustum = camera.frustum;
                        }
                    }
                }
                filter.yMul  = 0.9f  * conditionY;
                filter.cwMul = 0.95f * conditionCw;
                filter.cmMul = 0.95f * conditionCm;
                filter.yAdd  = 0.9f  * conditionYAdd;
                filter.cwAdd = 0.95f * conditionCwAdd;
                filter.cmAdd = 0.95f * conditionCmAdd;

                font.draw(batch, walls, xo - font.actualCellWidth * 0.25f, yo, 3, 3);
//                font.draw(batch, walls, xo - font.actualCellWidth * 0.25f, yo, 3, 3);

                font.configureShader(batch);
                if(frustum == null) {
                    for (int i = 0; i < len; i++) {
                        layers.get(i).draw(batch, font, xo, yOff);
                    }

                }
                else
                {
                    for (int i = 0; i < len; i++) {
                        layers.get(i).draw(batch, font, frustum, xo, yOff);
                    }
                }
                filter.yMul  = 1.05f * conditionY;
                filter.cwMul = 1.4f  * conditionCw;
                filter.cmMul = 1.4f  * conditionCm;
                filter.yAdd  = 1.05f * conditionYAdd;
                filter.cwAdd = 1.4f  * conditionCwAdd;
                filter.cmAdd = 1.4f  * conditionCmAdd;
                int x, y;
                for (int i = 0; i < glyphs.size(); i++) {
                    TextCellFactory.Glyph glyph = glyphs.get(i);
                    if(glyph == null)
                        continue;
                    glyph.act(Gdx.graphics.getDeltaTime());
                    if(
                            !glyph.isVisible() ||
                                    (x = Math.round((gxo = glyph.getX() - xo) / font.actualCellWidth)) < 0 || x >= gridWidth ||
                                    (y = Math.round((gyo = glyph.getY() - yo)  / -font.actualCellHeight + gridHeight)) < 0 || y >= gridHeight ||
                                    backgrounds[x][y] == 0f || (frustum != null && !frustum.boundsInFrustum(gxo, gyo, 0f, font.actualCellWidth, font.actualCellHeight, 0f)))
                        continue;
                    glyph.draw(batch, 1f);
                }
            }
        };
        
        mapHoverSLayers = new SparseLayers(worldWidth * 2, worldHeight, messageSize.cellWidth, mapSize.cellHeight, font);
        
        infoHandler = new InfoHandler(infoSLayers, colorCenter);
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
                totalDepth,
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
        mapHoverSLayers.setPosition(-messageSize.cellWidth >> 1, 0);

        mapViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());
        infoViewport.setScreenBounds(mapSize.pixelWidth(), contextSize.pixelHeight(), infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport.setScreenBounds(mapSize.pixelWidth(), 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapOverlayViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());
        fallingViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        mapInput = new SquidInput(mapKeys, mapMouse);
        contextInput = new SquidInput(contextMouse);
        infoInput = new SquidInput(infoMouse);
        messageInput = new SquidInput(messageMouse);
        debugInput = new SquidInput(debugKeys);
        fallbackInput = new SquidInput(fallbackKeys);
        multiplexer = new InputSpecialMultiplexer(mapInput, messageInput, contextInput, infoInput, debugInput, fallbackInput); //mapStage, messageStage, 
        Gdx.input.setInputProcessor(multiplexer);

        mapStage.addActor(mapSLayers);
        mapStage.addActor(mapHoverSLayers);
        mapOverlayStage.addActor(mapOverlaySLayers);
        fallingStage.addActor(fallingSLayers);
        messageStage.addActor(messageSLayers);
        infoStage.addActor(infoSLayers);
        contextStage.addActor(contextHandler.group);

        fallingStage.getCamera().position.y = startingY = fallingSLayers.worldY(mapSize.gridHeight >> 1);
        finishY = fallingSLayers.worldY(totalDepth);
        timeToFall = Math.abs(finishY - startingY) * fallDelay / mapSize.cellHeight;
//        lightLevels = new float[76];
//        float initial = lerpFloatColors(RememberedTile.memoryColorFloat, -0x1.7583e6p125F, 0.4f); // the float is SColor.AMUR_CORK_TREE
//        for (int i = 0; i < 12; i++) {
//            lightLevels[i] = lerpFloatColors(initial, -0x1.7583e6p125F, Interpolation.sineOut.apply(i / 12f)); // AMUR_CORK_TREE again
//        }
//        for (int i = 0; i < 64; i++) {
//            lightLevels[12 + i] = lerpFloatColors(-0x1.7583e6p125F, -0x1.fff1ep126F, Interpolation.sineOut.apply(i / 63f)); // AMUR_CORK_TREE , then ALICE_BLUE
//        }

        startGame();
    }

    public static String style(CharSequence text) {
//        return text.toString();
        return GDXMarkup.instance.styleString(text).toString();
    }

    private void startGame() {
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

    private void initPlayer() {
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

    private void prepFall() {
        message("Falling..... Press SPACE to continue");
        int w = MapConstants.DIVE_HEADER[0].length();
        WobblyCanyonGenerator wcg = new WobblyCanyonGenerator(mapDecorator);
        map = wcg.buildDive(worldGenerator.buildWorld(w, 1, worldDepth), w, worldDepth);
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
        EpiMap[] underground = worldGenerator.buildWorld(worldWidth, worldHeight, worldDepth);
        EpiMap[] castle = castleGenerator.buildCastle(worldWidth, worldHeight, aboveGround);
        world = Stream.of(castle, underground).flatMap(Stream::of).toArray(EpiMap[]::new);
        depth = aboveGround+1; // higher is deeper; aboveGround is surface-level
        map = world[depth];
        fxHandler = new FxHandler(mapSLayers, 3, colorCenter, map.lighting.fovResult);
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

    private void changeLevel(int level){
        changeLevel(level, null);
    }

    private void changeLevel(int level, Coord location) {
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

        fxHandler.seen = map.lighting.fovResult;
        creatures = map.creatures;
        simple = map.simpleChars();
        lineDungeon = map.line;
        calcFOV(player.location.x, player.location.y);
        toPlayerDijkstra.initialize(simple);
        monsterDijkstra.initialize(simple);
        calcDijkstra();
        contextHandler.setMap(map, world);
    }

    private void runTurn() {
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
            fxHandler.floatText(target.location, String.format("%.1f %s", val, Utilities.capitalizeFirst(entry.getKey().toString())), color);
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
    private void scrollMessages(int amount) {
        messageIndex = MathUtils.clamp(messageIndex + amount, messageCount, messages.size() - 1);
        updateMessages();
    }

    private void message(String text) {
        messageIndex = Math.max(messages.size(), messageCount);
        messages.add(GDXMarkup.instance.colorString("[]" + text));
        updateMessages();
    }
    
    private void calcFOV(int checkX, int checkY) {
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
        if (odinView) {
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
        }
        else 
        {
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
//                            message("calcFOV: recreating appearance of " + creature + " " + ((EpiData)creature).hashCode());
//                            System.out.println("calcFOV: recreating appearance of " + creature + " " + ((EpiData)creature).hashCode());
                            creature.appearance = mapSLayers.glyph(creature.symbol, creature.color, x, y);
                        } /*else if (!mapSLayers.glyphs.contains(creature.appearance)) {
                            mapSLayers.glyphs.add(creature.appearance);
                            if (creature.overlayAppearance != null) {
                                mapSLayers.glyphs.add(creature.overlayAppearance);
                            }
                        }*/
                        
                        creature.appearance.setVisible(true);
                    }
                } else if ((creature = creatures.get(Coord.get(x, y))) != null && creature.appearance != null 
                        && creature.appearance.isVisible()) {
                    creature.appearance.setVisible(false);
                    //mapSLayers.removeGlyph(creature.appearance);
                    if (creature.overlayAppearance != null) {
                        //mapSLayers.removeGlyph(creature.overlayAppearance);
                        creature.overlayAppearance.setVisible(false);
                    }
//                    message("calcFOV: null-ing appearance of " + creature + " " + ((EpiData)creature).hashCode());
//                    System.out.println("calcFOV: null-ing appearance of " + creature + " " + ((EpiData)creature).hashCode());
                    //creature.appearance = null;
                }
            }
        }
    }

    private void calcDijkstra() {
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
    private void equipItem() {
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

    private void equipItem(Physical item) {
        player.equipItem(item);
        if (item.weaponData != null && item.radiance != null) { // TODO mix light sources from player held items
//            player.radiance = new Radiance((float) player.stats.get(Stat.SIGHT).actual(), item.radiance.color, item.radiance.flicker, item.radiance.strobe, item.radiance.flare);
            // TODO - recalc lighting
            calcFOV(player.location.x, player.location.y);
        }
    }

    private void scheduleMove(Direction dir) {
        awaitedMoves.add(player.location.translate(dir));
    }

    private void buildAttackOptions(Physical target) {
        currentTarget = target;
        maneuverOptions.clear();

        for (Weapon w : validAttackOptions(player, target)) {
            maneuverOptions.put(w.rawWeapon.name + " Attack", w);
            for (int j = 0; j < w.maneuvers.size(); j++) {
                maneuverOptions.put(w.rawWeapon.name + ' ' + w.maneuvers.get(j), w);
            }
        }
    }

    private Weapon chooseValidWeapon(Physical attacker, Physical target) {
        List<Weapon> weapons = validAttackOptions(attacker, target);
        return weapons == null || weapons.isEmpty() ? null : rng.getRandomElement(weapons);
    }

    private List<Weapon> validAttackOptions(Physical attacker, Physical target) {
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

    private Coord showAttackOptions(Physical target, OrderedMap<String, Weapon> options) {
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
    private Coord showInteractOptions(Physical interactable, Physical user, Coord target, EpiMap map) {
        if(interactable.interactableData == null || interactable.interactableData.isEmpty())
            return null;
        mapOverlaySLayers.clear(1);
        mapOverlaySLayers.clear(2);
        int sz = interactable.interactableData.size(), len = 0;
        for (int i = 0; i < sz; i++) {
            len = Math.max(interactable.interactableData.get(i).phrasing.length(), len);
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
                String name = interactable.interactableData.get(i).phrasing;
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
                String name = interactable.interactableData.get(i).phrasing;
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
    private void buildInteractOptions(Physical interactable) {
        interactionOptions.clear();
        if(interactable.interactableData == null || interactable.interactableData.isEmpty())
            return;
        for (Interactable inter : interactable.interactableData) {
            interactionOptions.put(inter.phrasing, inter);
        }
    }

    private void attack(Physical target) {
        attack(target, chooseValidWeapon(player, target));
    }

    private void attack(Physical target, Weapon choice) {
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
                            int tx = MathUtils.clamp(targetX + player.between(-1, 2), 0, worldWidth - 1),
                                    ty = MathUtils.clamp(targetY + player.between(-1, 2), 0, worldHeight - 1);
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
    private void move(Direction dir) {
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
            } else if ((thing = map.contents[newX][newY].getLargeNonCreature()) != null) {
                cancelMove();
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
        if(DEBUG)
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

        if (DEBUG) {
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
                //(mapSize.gridWidth & 1)
                (int) (messageSize.cellWidth * currentZoomX * -0.5), // this one's special
                (mapSize.gridHeight & 1) * (int) (mapSize.cellHeight * currentZoomY * -0.5f));
        equipmentMouse.reinitialize(currentZoomX * mapSize.cellWidth, currentZoomY * mapSize.cellHeight,
                mapSize.gridWidth, mapSize.gridHeight,
                (mapSize.gridWidth & 1) * (int) (mapSize.cellWidth * currentZoomX * -0.5f),
                (mapSize.gridHeight & 1) * (int) (mapSize.cellHeight * currentZoomY * -0.5f));
        contextMouse.reinitialize(currentZoomX * contextSize.cellWidth, currentZoomY * contextSize.cellHeight,
                contextSize.gridWidth, contextSize.gridHeight,
                (contextSize.gridWidth & 1) * (int) (contextSize.cellWidth * currentZoomX * 0.5f) - (int)(Gdx.graphics.getWidth() - currentZoomX * contextSize.pixelWidth()),
                (contextSize.gridHeight & 1) * (int) (contextSize.cellHeight * currentZoomY * 0.5f) - (int) (infoSLayers.getTop() + infoSize.cellHeight * currentZoomY));
        infoMouse.reinitialize(currentZoomX * infoSize.cellWidth, currentZoomY * infoSize.cellHeight,
                infoSize.gridWidth, infoSize.gridHeight,
                (infoSize.gridWidth & 1) * (int) (infoSize.cellWidth * currentZoomX * 0.5f) - (int)(Gdx.graphics.getWidth() - currentZoomX * infoSize.pixelWidth()),
                (~infoSize.gridHeight & 1) * (int) (infoSize.cellHeight * currentZoomY * -0.5f));
        messageMouse.reinitialize(currentZoomX * messageSize.cellWidth, currentZoomY * messageSize.cellHeight,
                messageSize.gridWidth, messageSize.gridHeight,
                (int) (messageSize.cellWidth * currentZoomX * -0.5),
                (messageSize.gridHeight & 1) * (int) (messageSize.cellHeight * currentZoomY * 0.5f) - (int) (mapSize.gridHeight * mapSize.cellHeight * currentZoomY));
// - (int) (infoSize.cellHeight * currentZoomY)
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

        fallingViewport.update(width, height, false);
        fallingViewport.setScreenBounds(0, (int) (currentZoomY * messageSize.pixelHeight()),
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
            if (multiplexer.processedInput) return;
            if (ctrl && shift && key == SquidInput.BACKSPACE && !alt) // ctrl-shift-backspace
            {
                multiplexer.processedInput = true;
                startGame();
                return;
            }
            Verb verb = ControlMapping.allMappings.get(SquidInput.combineModifiers(key, alt, ctrl, shift));
            if (!ControlMapping.defaultMapViewMapping.contains(verb)) {
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    scheduleMove(Direction.DOWN);
                    break;
                case MOVE_UP:
                    scheduleMove(Direction.UP);
                    break;
                case MOVE_LEFT:
                    scheduleMove(Direction.LEFT);
                    break;
                case MOVE_RIGHT:
                    scheduleMove(Direction.RIGHT);
                    break;
                case MOVE_DOWN_LEFT:
                    scheduleMove(Direction.DOWN_LEFT);
                    break;
                case MOVE_DOWN_RIGHT:
                    scheduleMove(Direction.DOWN_RIGHT);
                    break;
                case MOVE_UP_LEFT:
                    scheduleMove(Direction.UP_LEFT);
                    break;
                case MOVE_UP_RIGHT:
                    scheduleMove(Direction.UP_RIGHT);
                    break;
                case MOVE_LOWER:// up '≤', down '≥'
                    //  if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≥') {
                        if (depth >= world.length - 1) {
                            message("Theses down stairs turn out to lead nowhere.");
                        } else {
                            changeLevel(depth + 1, player.location);
                        }
//                    } else {
//                        message("You're not on stairs going down.");
//                    }
                    break;
                case MOVE_HIGHER:// up '≤', down '≥'
//                    if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≤') {
                        if (depth <= 0) {
                            message("Theses up stairs turn out to lead nowhere.");
                        } else {
                            changeLevel(depth - 1, player.location);
                        }
//                    } else {
//                        message("You're not on stairs going up.");
//                    }
                    break;
                case OPEN: // Open all the doors nearby
                    message("Opening nearby doors");
                    for (Direction d : Direction.OUTWARDS) {
                        Coord c = player.location.translate(d);
                        if (!map.inBounds(c)) {
                            continue;
                        }
                        if (map.lighting.fovResult[c.x][c.y] <= 0) {
                            continue;
                        }
                        EpiTile tile = map.contents[c.x][c.y];
                        if (tile.blockage != null && tile.blockage.countsAs(dataStarter.baseClosedDoor)){
                                RecipeMixer.applyModification(tile.blockage, dataStarter.openDoor);
                                tile.contents.add(tile.blockage);
                                tile.blockage = null;
                        }
                    }
                    calcFOV(player.location.x, player.location.y);
                    calcDijkstra();
                    break;
                case SHUT: // Close all the doors nearby
                    message("Closing nearby doors");
                    for (Direction d : Direction.OUTWARDS) {
                        Coord c = player.location.translate(d);
                        if (!map.inBounds(c)) {
                            continue;
                        }
                        if (map.lighting.fovResult[c.x][c.y] <= 0) {
                            continue;
                        }
                        EpiTile tile = map.contents[c.x][c.y];
                        for (Physical p : tile.contents) {
                            if (p.countsAs(dataStarter.baseOpenDoor)) {
                                if (tile.blockage != null) {
                                    message("Can't shut the door to the " + d.toString() + " there's a " + tile.blockage.name + " in the way!");
                                    continue;
                                }
                                RecipeMixer.applyModification(p, dataStarter.closeDoor);
                                tile.remove(p);
                                tile.blockage = p;
                            }
                        }
                    }
                    calcFOV(player.location.x, player.location.y);
                    calcDijkstra();
                    break;
                case GATHER: // Pick everything nearby up
                    List<Physical> pickedUp = new ArrayList<>();
                    for (Direction dir : Direction.values()) {
                        Coord c = player.location.translate(dir);
                        if (map.inBounds(c) && map.lighting.fovResult[c.x][c.y] > 0) {
                            EpiTile tile = map.contents[c.x][c.y];
                            ListIterator<Physical> it = tile.contents.listIterator();
                            Physical p;
                            while (it.hasNext()) {
                                p = it.next();
                                if (p.attached || p.creatureData != null) {
                                    continue;
                                }
                                player.addToInventory(p);
                                pickedUp.add(p);
                                it.remove();
                            }
                        }
                    }
                    if (pickedUp.isEmpty()) {
                        message("Nothing to pick up nearby.");
                    } else {
                        message(pickedUp.stream().map(p -> Utilities.colorize(p.name, p.rarity.color())).collect(Collectors.joining(", ", "Picked up ", ".")));
                    }
                    break;
                case EQUIPMENT:
                    mapOverlayHandler.setMode(PrimaryMode.EQUIPMENT);
                    mapInput.setKeyHandler(equipmentKeys);
                    toCursor.clear();
                    mapInput.setMouse(equipmentMouse);
                    break;
                case WIELD:
                    equipItem();
                    break;
                case DROP:
                    message("Dropping all held items");
                    for (Physical dropped : player.unequip(Maker.makeList(WieldSlot.RIGHT_HAND, WieldSlot.LEFT_HAND))) {
                        for (int i = 0, offset = player.next(3); i < 8; i++) {
                            Coord c = player.location.translate(Direction.OUTWARDS[i + offset & 7]);
                            if (map.inBounds(c) && map.lighting.fovResult[c.x][c.y] > 0) {
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
                case MESSAGE_PRIOR:
                    scrollMessages(-1);
                    break;
                case MESSAGE_NEXT:
                    scrollMessages(1);
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
                    break;
                case REST:
                    prepFall();
                    break;
                case INTERACT:
                    Optional<Physical> t;
                    if((t = player.inventory.stream().filter(ph -> ph.symbol == 'ῗ').findFirst()).isPresent())
                    {
                        if(player.creatureData.lastUsedItem != null && player.creatureData.lastUsedItem.symbol == 'ῗ')
                            player.creatureData.lastUsedItem = null;
                        else 
                            player.creatureData.lastUsedItem = t.get();
                    }
                    break;
                default:
                    //message("Can't " + verb.name + " from main view.");
                    return;
            }
            multiplexer.processedInput = true;
            infoHandler.updateDisplay();
            // check if the turn clock needs to run
            if (verb.isAction()) {
                runTurn();
            }
        }
    };

    private final KeyHandler fallbackKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            if (multiplexer.processedInput) return;
            Verb verb = ControlMapping.allMappings.get(SquidInput.combineModifiers(key, alt, ctrl, shift));
            String m;
            if (mapOverlaySLayers.isVisible()) {
                switch (mapOverlayHandler.getMode()) {
                    case HELP:
                        if (!ControlMapping.defaultHelpViewMapping.contains(verb)) verb = null;
                        m = "help";
                        break;
                    case CRAFTING:
                        if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) verb = null;
                        m = "crafting";
                        break;
                    default:
                        if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) verb = null;
                        m = "equipment";
                        break;
                }
            } else {
                switch (mode) {
                    case DIVE:
                        if (!ControlMapping.defaultFallingViewMapping.contains(verb)) verb = null;
                        m = "dive";
                        break;
                    default:
                        if (!ControlMapping.defaultMapViewMapping.contains(verb)) verb = null;
                        m = "map";
                        break;
                }
            }
            if (verb == null) {
                message("Unknown input for " + m + " mode: " + key);
            } else {
                message("Can't " + verb.name + " from " + m + " mode.");
            }
        }
    };

    private final KeyHandler equipmentKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            if (multiplexer.processedInput) return;
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.allMappings.get(combined);
            if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) {
                return;
            }
            if (showingMenu) {
                if(mapOverlayHandler.getSelected() == null || mapOverlayHandler.getSelected().interactableData == null
                        || mapOverlayHandler.getSelected().interactableData.isEmpty())
                {
                    showingMenu = false;
                    menuLocation = null;
                    mapOverlayHandler.setSubselection(null);
                    maneuverOptions.clear();
                    interactionOptions.clear();
                    currentTarget = null;
                    mapOverlaySLayers.clear(1);
                    mapOverlaySLayers.clear(2);
                    multiplexer.processedInput = true;
                    infoHandler.updateDisplay();
                    return;
                }
                List<Interactable> interactableData = mapOverlayHandler.getSelected().interactableData;
                Coord sub = mapOverlayHandler.getSubselection();
                switch (verb)
                {
                    case MOVE_DOWN:
                        if(sub.y + 1 < interactableData.size())
                            mapOverlayHandler.setSubselection(sub.x, sub.y + 1);
                        break;
                    case MOVE_UP:
                        if(sub.y > 0)
                            mapOverlayHandler.setSubselection(sub.x, sub.y - 1);
                        break;
                    case CLOSE_SCREEN:
                    case MOVE_LEFT:
                        showingMenu = false;
                        menuLocation = null;
                        mapOverlayHandler.setSubselection(null);
                        maneuverOptions.clear();
                        interactionOptions.clear();
                        currentTarget = null;
                        mapOverlaySLayers.clear(1);
                        mapOverlaySLayers.clear(2);
                        multiplexer.processedInput = true;
                        infoHandler.updateDisplay();
                        return;
                    case MOVE_RIGHT:
                    case INTERACT:
                        if (mapOverlayHandler.getSubselection() != null) {
                            Interactable interaction = interactionOptions.getAt(sub.y);
                            if (interaction == null)
                                break;
                            Physical selected = mapOverlayHandler.getSelected();
                            if (interaction.consumes) {
                                player.removeFromInventory(selected);
                            }
                            message(Messaging.transform(interaction.interaction.interact(player, selected, map),
                                    player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                            showingMenu = false;
                            menuLocation = null;
                            mapOverlayHandler.setSubselection(null);
                            maneuverOptions.clear();
                            interactionOptions.clear();
                            currentTarget = null;
                            mapOverlaySLayers.clear(1);
                            mapOverlaySLayers.clear(2);
                        }
                        break;
                    default:
                        return;
                }

                multiplexer.processedInput = true;
                infoHandler.updateDisplay();
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    mapOverlayHandler.move(Direction.DOWN);
                    break;
                case MOVE_UP:
                    mapOverlayHandler.move(Direction.UP);
                    break;
                case MOVE_LEFT:
                    mapOverlayHandler.move(Direction.LEFT);
                    break;
                case MOVE_RIGHT:
                    mapOverlayHandler.move(Direction.RIGHT);
                    break;
                case WIELD:
                    equipItem(mapOverlayHandler.getSelected());
                    break;
                case DROP:
                    map.contents[player.location.x][player.location.y].contents.add(player.removeFromInventory(mapOverlayHandler.getSelected()));
                    break;
                case INTERACT:
                    Physical selected = mapOverlayHandler.getSelected();
                    if(selected == null)
                        break;
                    if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
                        buildInteractOptions(selected);
                        menuLocation = showInteractOptions(selected, player, mapOverlayHandler.getSelection(), map);
                        mapOverlayHandler.setSubselection(0, 0);
//                        message("Interactions for " + selected.name + ": " + selected.interactableData
//                            .stream()
//                            .map(interact -> interact.phrasing)
//                            .collect(Collectors.joining(", ")));
//                        Interactable interaction = selected.interactableData.get(0);
//                        if (interaction.consumes) {
//                            player.removeFromInventory(selected);
//                        }
//                        message(Messaging.transform(interaction.interaction.interact(player, selected, map),
//                                player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                    } else if (selected.wearableData != null || selected.weaponData != null) {
                        if (player.creatureData.equippedDistinct.contains(selected)) {
                            player.unequip(selected);
                            player.addToInventory(selected); // Equip pulls from inventory if needed, but unequip does not put it back
                        } else {
                            player.equipItem(selected);
                        }
                    } else {
                        message("No interaction for " + selected.name);
                    }
                    break;
                case MESSAGE_PRIOR:
                    scrollMessages(-1);
                    break;
                case MESSAGE_NEXT:
                    scrollMessages(1);
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
                case CLOSE_SCREEN:
                    mapInput.setKeyHandler(mapKeys);
                    mapInput.setMouse(mapMouse);
                    mapOverlayHandler.hide();
                    break;
                default:
                    //message("Can't " + verb.name + " from equipment view.");
                    return; // note, this will not change processedInput
            }
            multiplexer.processedInput = true;
            infoHandler.updateDisplay();
        }
    };

    private final KeyHandler helpKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            if (multiplexer.processedInput) return;
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.allMappings.get(combined);
            if (!ControlMapping.defaultHelpViewMapping.contains(verb)) {
                return;
            }
            switch (verb) {
                case MOVE_DOWN:
                    mapOverlayHandler.move(Direction.DOWN);
                    break;
                case MOVE_UP:
                    mapOverlayHandler.move(Direction.UP);
                    break;
                case MOVE_LEFT:
                    mapOverlayHandler.move(Direction.LEFT);
                    break;
                case MOVE_RIGHT:
                    mapOverlayHandler.move(Direction.RIGHT);
                    break;
                case MESSAGE_PRIOR:
                    scrollMessages(-1);
                    break;
                case MESSAGE_NEXT:
                    scrollMessages(1);
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
                case EQUIPMENT:
                    mapOverlayHandler.setMode(PrimaryMode.EQUIPMENT);
                    mapInput.setKeyHandler(equipmentKeys);
                    mapInput.setMouse(equipmentMouse);
                    break;
                case HELP:
                case CLOSE_SCREEN:
                    mapInput.setKeyHandler(mapKeys);
                    mapInput.setMouse(mapMouse);
                    mapOverlayHandler.hide();
                    break;
                default:
                    //message("Can't " + verb.name + " from help view.");
                    return;
            }
            multiplexer.processedInput = true;
            infoHandler.updateDisplay();
        }
    };

    private final KeyHandler fallingKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            if (multiplexer.processedInput) return;
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.allMappings.get(combined);
            if (!ControlMapping.defaultFallingViewMapping.contains(verb)) {
                return;
            }
            switch (verb) {
                case MOVE_UP:
                    if (!paused) {
                        nextInput = Instant.now().plusMillis(inputDelay);
                        fallingHandler.move(Direction.UP);
                    }
                    break;
                case MOVE_DOWN:
                    if (!paused) {
                        nextInput = Instant.now().plusMillis(inputDelay);
                        fallingHandler.move(Direction.DOWN);
                    }
                    break;
                case MOVE_LEFT:
                    if (!paused) {
                        nextInput = Instant.now().plusMillis(inputDelay);
                        fallingHandler.move(Direction.LEFT);
                    }
                    break;
                case MOVE_RIGHT:
                    if (!paused) {
                        nextInput = Instant.now().plusMillis(inputDelay);
                        fallingHandler.move(Direction.RIGHT);
                    }
                    break;
                case PAUSE:
                    paused = !paused;
                    if (paused) {
                        pausedAt = Instant.now();
                        message("You are hovering, have a look around!");
                    } else { // need to calculate time offsets
                        long pausedFor = pausedAt.until(Instant.now(), ChronoUnit.MILLIS);
                        nextFall = nextFall.plusMillis(pausedFor);
                        message("Falling once more!");
                    }
                    break;
                case SAVE:
                    // TODO
                    break;
                case QUIT:
                    Gdx.app.exit();
                    break;
                default:
                    //message("Can't " + verb.name + " from falling view.");
                    return;
            }
            multiplexer.processedInput = true;
        }
    };

    private final KeyHandler fallingGameOverKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            if (multiplexer.processedInput) return;
            int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
            Verb verb = ControlMapping.allMappings.get(combined);
            if (!ControlMapping.defaultFallingViewGameOverMapping.contains(verb)) {
                return;
            }
            switch (verb) {
                case TRY_AGAIN:
                    initPlayer();
                    prepFall();
                    break;
                case QUIT:
                    Gdx.app.exit();
                    break;
                default:
                    //message("Can't " + verb.name + " from falling view.");
                    return;
            }
            multiplexer.processedInput = true;
        }
    };

    private final KeyHandler debugKeys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) { // TODO - only the first 2 seem to be working currently
            if (multiplexer.processedInput) return;
            Element el;
            switch (key) {
                case 'x':
                    el = rng.getRandomElement(Element.allEnergy);
                    message("Sector blast " + el.styledName);
                    fxHandler.sectorBlast(player.location, el, 7, Radius.CIRCLE);
                    break;
                case 'X':
                    el = rng.getRandomElement(Element.allEnergy);
                    message("Zap boom " + el.styledName);
                    fxHandler.zapBoom(player.location, player.location.translateCapped(rng.between(-20, 20), rng.between(-10, 10), map.width, map.height), el);
                    break;
                case 'z':
                    message("Fritzzzz");
                    fxHandler.fritz(player.location, Element.ICE, 7, Radius.CIRCLE);
                    break;
                case 'Z':
                    message("Twinkle time");
                    for (Coord c : rng.getRandomUniqueCells(0, 0, mapSize.gridWidth, mapSize.gridHeight, 400)) {
                        fxHandler.twinkle(c, Element.LIGHT);
                    }
                    break;
                case '=':
                    message("Layered sparkle small");
                    fxHandler.layeredSparkle(player.location, 4, Radius.CIRCLE);
                    break;
                case '+':
                    message("Layered sparkle large");
                    fxHandler.layeredSparkle(player.location, 8, Radius.CIRCLE);
                    break;
                case '|':
                    if (odinView) {
                        message("Odinview disabled.");
                        odinView = false;
                    } else {
                        message("Showing all");
                        odinView = true;
                    }
                    calcFOV(player.location.x, player.location.y);
                default:
                    return;
            }
            multiplexer.processedInput = true;
        }
    };

    private final SquidMouse equipmentMouse = new SquidMouse(mapSize.cellWidth * 0.5f, mapSize.cellHeight, mapSize.gridWidth * 2f, mapSize.gridHeight, 0, 0, new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (showingMenu) {
                if (menuLocation.x <= screenX + 2 && menuLocation.y <= screenY
                        && screenY - menuLocation.y < interactionOptions.size()
                        && mapOverlaySLayers.getLayer(1).getChar(screenX + 2, screenY, '\uFFFF') == '\0') {
                    Interactable interaction = interactionOptions.getAt(screenY - menuLocation.y);
                    if (interaction == null)
                        return false;
                    Physical selected = mapOverlayHandler.getSelected();
                    if (interaction.consumes) {
                        player.removeFromInventory(selected);
                    }
                    message(Messaging.transform(interaction.interaction.interact(player, selected, map),
                            player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                }
                showingMenu = false;
                menuLocation = null;
                maneuverOptions.clear();
                interactionOptions.clear();
                currentTarget = null;
                mapOverlaySLayers.clear(1);
                mapOverlaySLayers.clear(2);
                return true;
            }

            if(!mapOverlayHandler.setSelection(screenX, screenY)) 
                return false;
            Physical selected = mapOverlayHandler.getSelected();
            if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
                buildInteractOptions(selected);
                if (interactionOptions == null || interactionOptions.isEmpty()) {
                    message("Cannot interact with the " + selected.name);
                } else {
                    menuLocation = showInteractOptions(selected, player, mapOverlayHandler.getSelection(), map);
                    mapOverlayHandler.setSubselection(0, 0);
                }
                return true;

//                message("Interactions for " + selected.name + ": " + selected.interactableData
//                        .stream()
//                        .map(interact -> interact.phrasing)
//                        .collect(Collectors.joining(", ")));
//                Interactable interaction = selected.interactableData.get(0);
//                if (interaction.consumes) {
//                    player.removeFromInventory(selected);
//                }
//                message(Messaging.transform(interaction.interaction.interact(player, selected, map),
//                        player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
            } else if (selected.wearableData != null || selected.weaponData != null) {
                if (player.creatureData.equippedDistinct.contains(selected)) {
                    player.unequip(selected);
                    player.addToInventory(selected); // Equip pulls from inventory if needed, but unequip does not put it back
                } else {
                    player.equipItem(selected);
                }
            } else {
                message("No interaction for " + selected.name);
            }
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if(!showingMenu) 
                mapOverlayHandler.setSelection(screenX, screenY);
            return false;
        }
    });

    private final SquidMouse helpMouse = new SquidMouse(mapSize.cellWidth * 0.5f, mapSize.cellHeight, mapSize.gridWidth * 2f, mapSize.gridHeight, 0, 0, new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false; // No-op for now
        }
    });

    private final SquidMouse fallingMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, 0, 0, new InputAdapter() {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false; // No-op for now
        }
    });

    private final SquidMouse mapMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, messageSize.cellWidth, 0, new InputAdapter() {

        // if the user clicks within FOV range and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            screenX += player.location.x - (mapSize.gridWidth >> 1);
            screenY += player.location.y - (mapSize.gridHeight >> 1);

            if (!map.inBounds(screenX, screenY) || (!showingMenu && map.lighting.fovResult[screenX][screenY] <= 0.0)) {
                return false;
            }

            Physical thing = null;
            if (map.contents[screenX][screenY] != null) {
                thing = map.contents[screenX][screenY].getCreature();
            }
            switch (button) {
                case Input.Buttons.LEFT:
                    if (showingMenu) {
                        if (menuLocation.x <= screenX && menuLocation.y <= screenY 
                                && screenY - menuLocation.y < maneuverOptions.size() 
                                && currentTarget != null && mapHoverSLayers.backgrounds[screenX << 1][screenY] != 0f) 
                        {
                            attack(currentTarget, maneuverOptions.getAt(screenY - menuLocation.y));
                            calcFOV(player.location.x, player.location.y);
                            calcDijkstra();
                            runTurn();
                        }
                        showingMenu = false;
                        menuLocation = null;
                        maneuverOptions.clear();
                        interactionOptions.clear();
                        currentTarget = null;
                        mapHoverSLayers.clear();
                        return true;
                    }

                    if (cursor.x != screenX || cursor.y != screenY) {// clear cursor if lifted in space other than the one it went down in
                        toCursor.clear();
                        return false;//cleaned up but not considered "handled"
                    }

                    if (thing == null) {
                        if (toCursor.isEmpty()) {
                            cursor = Coord.get(screenX, screenY);
                            ((StatefulRNG)toPlayerDijkstra.rng).setState(player.location.hashCode() ^ (long)cursor.hashCode() << 32);
                            toPlayerDijkstra.findPathPreScanned(toCursor, cursor);
                            if (!toCursor.isEmpty()) {
                                toCursor.remove(0); // Remove cell you're in from list
                            }
                        }
                        awaitedMoves.addAll(toCursor);
                        return true;
                    } else {
                        List<Weapon> attackOptions = validAttackOptions(player, thing);
                        if (attackOptions == null || attackOptions.isEmpty()) {
                            message("Can't attack the " + thing.name + " from there.");
                        } else {
                            Weapon w = rng.getRandomElement(attackOptions);
                            attack(thing, w);
                            calcFOV(player.location.x, player.location.y);
                            calcDijkstra();
                            runTurn();
                        }
                    }

                    return true;
                case Input.Buttons.RIGHT:
                    if (thing == null) {
                        return false;
                    }
                    buildAttackOptions(thing);
                    if (maneuverOptions == null || maneuverOptions.isEmpty()) {
                        message("No attack options against the " + thing.name + " at this range.");
                    } else {
                        menuLocation = showAttackOptions(thing, maneuverOptions);
                    }
                    return true;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return mouseMoved(screenX, screenY);
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (!awaitedMoves.isEmpty()) {
                cancelMove();
                return true;
            }

            switch (button) {
                case Input.Buttons.LEFT:
                    return false;
                case Input.Buttons.RIGHT:
                    // TODO - add tooltip info for location
                    break;
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            screenX += player.location.x - (mapSize.gridWidth >> 1);
            screenY += player.location.y - (mapSize.gridHeight >> 1);
            if (!map.inBounds(screenX, screenY) || map.lighting.fovResult[screenX][screenY] <= 0.0){
                toCursor.clear();
                return false;
            }
            contextHandler.tileContents(screenX, screenY, depth, map.contents[screenX][screenY]); // TODO - have ground level read as depth 0
            infoHandler.setTarget(map.contents[screenX][screenY].getCreature());

            if (!awaitedMoves.isEmpty()) {
                return false;
            }

            cursor = Coord.get(screenX, screenY);
            toCursor.clear();
            ((StatefulRNG)toPlayerDijkstra.rng).setState(player.location.hashCode() ^ (long)cursor.hashCode() << 32);
            toPlayerDijkstra.findPathPreScanned(toCursor, cursor);
            if (!toCursor.isEmpty()) {
                toCursor.remove(0);
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
            //System.out.println("info: " + screenX + ", " + screenY);
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
    private final SquidMouse messageMouse = new SquidMouse(messageSize.cellWidth, messageSize.cellHeight, messageSize.gridWidth, messageSize.gridHeight,
            messageSize.cellWidth, mapSize.pixelHeight(), new InputAdapter() {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            //System.out.println("message: " + screenX + ", " + screenY);
            switch (button) {
                case Input.Buttons.LEFT:
                    if (screenY <= 0) {
                        scrollMessages(-1);
                    } else if (screenY >= messageSize.gridHeight - 1) {
                        scrollMessages(1);
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
