package squidpony.epigon;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.ArrayTools;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.display.ContextHandler;
import squidpony.epigon.display.FxHandler;
import squidpony.epigon.display.InfoHandler;
import squidpony.epigon.display.PanelSize;
import squidpony.epigon.dm.RecipeMixer;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.RememberedTile;
import squidpony.epigon.mapping.WorldGenerator;
import squidpony.epigon.playground.HandBuilt;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.LightRNG;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static final long seed = 0xBEEFD00DFADEFEEL;
    // this is separated from the StatefulRNG so you can still call LightRNG-specific methods, mainly skip()
    public static final LightRNG lightRNG = new LightRNG(seed);
    public static final StatefulRNG rng = new StatefulRNG(lightRNG);
    public static final RecipeMixer mixer = new RecipeMixer();
    public static final HandBuilt handBuilt = new HandBuilt();


    // Audio
    private SoundManager sound;

    // Display
    SpriteBatch batch;
    private SquidColorCenter colorCenter;
    private SparseLayers mapSLayers;
    private SquidLayers infoSLayers;
    private SquidLayers contextSLayers;
    private SquidLayers messageSLayers;
    private SquidInput mapInput;
    private SquidInput contextInput;
    private SquidInput infoInput;
    private Color bgColor;
    private int framesWithoutAnimation;
    private List<Coord> toCursor;
    private TextCellFactory font;

    // World
    private WorldGenerator worldGenerator;
    private EpiMap map;
    private FxHandler fxHandler;
    private ContextHandler contextHandler;
    private InfoHandler infoHandler;
    private GreasedRegion blocked;
    private DijkstraMap toPlayerDijkstra;
    private Coord cursor;
    private Physical player;
    private ArrayList<Coord> awaitedMoves;
    private FOV fov = new FOV(FOV.SHADOW);
    private double[][] fovResult;
    private double[][] priorFovResult;
    private List<Physical> creatures = new ArrayList<>();
    private int autoplayTurns = 0;
    private boolean processingCommand = true;

    // WIP stuff, needs large sample map
    private Stage mapStage, messageStage, infoStage, contextStage;
    private Viewport mapViewport, messageViewport, infoViewport, contextViewport;
    private Camera camera;
    private TextCellFactory.Glyph playerEntity;

    // Set up sizing all in one place
    static {
        int bigW = 70;
        int bigH = 31;
        int smallW = 43;
        int smallH = 22;
        int cellW = 12;
        int cellH = 24;
        int bottomH = 3;
        mapSize = new PanelSize(bigW, bigH, cellW, cellH);
        messageSize = new PanelSize(bigW, bottomH, cellW, cellH);
        infoSize = new PanelSize(smallW, smallH * 3 / 2, 8, 16);
        contextSize = new PanelSize(smallW, (bigH + bottomH - smallH) * 3 / 2, 8, 16);
    }

    @Override
    public void create() {
        System.out.println("Working in folder: " + System.getProperty("user.dir"));

        System.out.println("Loading sound manager.");
        sound = new SoundManager();
        colorCenter = new SquidColorCenter();

        // Set the map size early so things can reference it
        map = new EpiMap(100, 50);
        Coord.expandPoolTo(map.width, map.height);

        bgColor = SColor.BLACK_DYE;

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();

        System.out.println("Putting together display.");
        mapViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());
        messageViewport = new StretchViewport(messageSize.pixelWidth(), messageSize.pixelHeight());
        infoViewport = new StretchViewport(infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport = new StretchViewport(contextSize.pixelWidth(), contextSize.pixelHeight());

        camera = mapViewport.getCamera();

        // Here we make sure our Stages, which holds any text-based grids we make, uses our Batch.
        mapStage = new Stage(mapViewport, batch);
        messageStage = new Stage(messageViewport, batch);
        infoStage = new Stage(infoViewport, batch);
        contextStage = new Stage(contextViewport, batch);
        font = DefaultResources.getStretchableLeanFont();
        TextCellFactory smallFont = font.copy();
        // Set up the text display portions
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
        contextSLayers.getBackgroundLayer().setDefaultForeground(SColor.COSMIC_LATTE);
        contextSLayers.getForegroundLayer().setDefaultForeground(SColor.FLIRTATIOUS_INDIGO_TEA);

        mapSLayers = new SparseLayers(
            map.width,
            map.height,
            mapSize.cellWidth,
            mapSize.cellHeight,
            font);
        ArrayTools.fill(mapSLayers.getBackgrounds(), bgColor.toFloatBits());
        infoHandler = new InfoHandler(infoSLayers, colorCenter);
        contextHandler = new ContextHandler(contextSLayers, mapSLayers);


        font.tweakWidth(mapSize.cellWidth * 1.1f).tweakHeight(mapSize.cellHeight * 1.1f).initBySize();
        smallFont.tweakWidth(infoSize.cellWidth * 1.15f).tweakHeight(infoSize.cellHeight * 1.15f).initBySize();

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        //mapSLayers.setAnimationDuration(0.145f);

        messageSLayers.setBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        infoSLayers.setBounds(0, 0, infoSize.pixelWidth(), infoSize.pixelHeight());
        contextSLayers.setBounds(0, 0, contextSize.pixelWidth(), contextSize.pixelHeight());
        mapSLayers.setPosition(0, 0);
        mapViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());
        infoViewport.setScreenBounds(mapSize.pixelWidth(), contextSize.pixelHeight(), infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport.setScreenBounds(mapSize.pixelWidth(), 0, contextSize.pixelWidth(), contextSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        mapInput = new SquidInput(keys, mapMouse);
        contextInput = new SquidInput(contextMouse);
        infoInput = new SquidInput(infoMouse);
        Gdx.input.setInputProcessor(new InputMultiplexer(mapStage, messageStage, mapInput, contextInput, infoInput));

        mapStage.addActor(mapSLayers);
        messageStage.addActor(messageSLayers);
        infoStage.addActor(infoSLayers);
        contextStage.addActor(contextSLayers);

        startGame();
    }

    private void startGame() {
        fovResult = new double[map.width][map.height];
        priorFovResult = new double[map.width][map.height];
        mapSLayers.addLayer();//first added panel adds at level 1
        fxHandler = new FxHandler(mapSLayers, 1, colorCenter, fovResult);
        message("Generating world.");
        worldGenerator = new WorldGenerator();
        map = worldGenerator.buildWorld(map.width, map.height, 1)[0];

        GreasedRegion floors = new GreasedRegion(map.opacities(), 0.999);

        player = mixer.buildPhysical(handBuilt.playerBlueprint);
        player.stats.get(Stat.HUNGER).delta(-1);
        player.stats.get(Stat.HUNGER).min(0);
        player.stats.get(Stat.CONVICTION).actual(player.stats.get(Stat.CONVICTION).base() * 1.7);
        player.stats.values().forEach(lv -> lv.max(Double.max(lv.max(), lv.actual())));

        player.location = floors.singleRandom(rng);

//        Arrays.stream(Direction.OUTWARDS)
//            .map(d -> player.location.translate(d))
//            .filter(c -> map.inBounds(c))
//            .filter(c -> rng.nextBoolean())
//            .forEach(c -> map.contents[c.x][c.y].add(mixer.mix(handBuilt.swordRecipe, Collections.singletonList(mixer.buildPhysical(rng.getRandomElement(Inclusion.values()))), Collections.emptyList())));


        infoHandler.setPlayer(player);

        // NOTE - turn off creatures while testing other things
//        for (Coord coord : floors.quasiRandomSeparated(0.05)) {
//            if (map.contents[coord.x][coord.y].getLargeObject() == null) {
//                Physical p = mixer.buildPhysical(rng.getRandomElement(Inclusion.values()));
//                mixer.applyModification(p, handBuilt.makeAlive);
//                p.location = coord;
//                map.contents[coord.x][coord.y].add(p);
//                creatures.add(p);
//            }
//        }

        playerEntity = mapSLayers.glyph(player.symbol, player.color, player.location.x, player.location.y);

//        mapSLayers.setGridOffsetX(player.location.x - (mapSize.gridWidth >> 1));
//        mapSLayers.setGridOffsetY(player.location.y - (mapSize.gridHeight >> 1));

        calcFOV(player.location.x, player.location.y);

        toPlayerDijkstra = new DijkstraMap(map.simpleChars(), DijkstraMap.Measurement.EUCLIDEAN);
        toPlayerDijkstra.rng = DefaultResources.getGuiRandom();
        blocked = new GreasedRegion(map.width, map.height);
        calcDijkstra();

        clearAndBorder(contextSLayers, SColor.FLIRTATIOUS_INDIGO_TEA, SColor.COSMIC_LATTE);
        contextHandler.message(new String[]{"Have fun!",
            "The fate of the worlds is in your hands...",
            "Bump into walls and stuff.",
            "Use ? for help, or q to quit.",
            "Use mouse, numpad, or arrow keys to move."});
        processingCommand = false; // let the player do input
        putMap();
        infoHandler.showPlayerHealthAndArmor();
    }

    private void runTurn() {
        for (Physical creature : creatures) {
            Coord c = creature.location;
            if (creature.stats.get(Stat.MOBILITY).actual() > 0 && (fovResult[c.x][c.y] > 0 || map.remembered[c.x][c.y] != null)) {
                List<Coord> path = toPlayerDijkstra.findPathPreScanned(Coord.get(c.x, c.y)); // TODO - figure out why this messes up mouse cursor
                if (path != null && path.size() > 1) {
                    Coord step = path.get(path.size() - 2);
                    if (map.contents[step.x][step.y].getLargeObject() == null && !(player.location.x == step.x && player.location.y == step.y)) {
                        map.contents[c.x][c.y].remove(creature);
                        TextCellFactory.Glyph critter = mapSLayers.glyphFromGrid(c.x, c.y);
                        mapSLayers.slide(critter, c.x, c.y, step.x, step.y, 0.145f, () ->
                                {
                                    mapSLayers.recallToGrid(critter);
                                    map.contents[step.x][step.y].add(creature);
                                    creature.location = step;
                                }
                        );
                        mapSLayers.put(c.x, c.y, map.contents[c.x][c.y].floor.symbol, map.contents[c.x][c.y].floor.color);
                    }
                }
            }
        }

        putMap();

        // Update all the stats in motion
        player.stats.values().stream().forEach(LiveValue::tick);
        for (Stat s : Stat.rolloverProcessOrder) {
            double val = player.stats.get(s).actual();
            if (val < 0) {
                player.stats.get(s).actual(0);
                player.stats.get(s.getRollover()).actual(player.stats.get(s.getRollover()).actual() + val);
            }
        }

        infoHandler.updateDisplay();
        if (player.stats.get(Stat.LIFE_FORCE).actual() <= 0) {
            message("You are now dead with Life Force: " + player.stats.get(Stat.LIFE_FORCE).actual());
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
        // all box drawing chars we know we can use:
        // ┼├┤┴┬┌┐└┘│─
        // ┌───┐
        // │┌┐ │
        // ├┴┼┬┤
        // │ └┘│
        // └───┘
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
        clearAndBorder(messageSLayers, SColor.APRICOT, bgColor);
        messageSLayers.putString(1, 1, text, SColor.APRICOT); // TODO - make this do the scroll things
    }

    private void calcFOV(int checkX, int checkY) {
        FOV.reuseFOV(map.opacities(), fovResult, checkX, checkY, player.stats.get(Stat.SIGHT).actual(), Radius.CIRCLE);
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                if (fovResult[x][y] > 0) {
                    if (map.remembered[x][y] == null) {
                        map.remembered[x][y] = new RememberedTile(map.contents[x][y]);
                    } else {
                        map.remembered[x][y].remake(map.contents[x][y]);
                    }
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
        //blocked.refill(fovResult, 0.0001, 1000.0).fringe8way();
        toPlayerDijkstra.scan(blocked); //(int)(player.stats.get(Stat.SIGHT).actual * 1.45),
    }

    private Color calcFadeoutColor(Color color, double amount){
        double d = Double.max(amount, 0.3);
        return colorCenter.lerp(SColor.BLACK, color, d);
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     */
    private void move(Direction dir) {

        int newX = player.location.x + dir.deltaX;
        int newY = player.location.y + dir.deltaY;
        if (!map.inBounds(newX, newY)) {
            return; // can't move, should probably be error or something
        }

        if (map.contents[newX][newY].getLargeObject() == null) {
//            final float midX = player.location.x + dir.deltaX * 0.5f;
//            final float midY = player.location.y + dir.deltaY * 0.5f;
//            final Vector3 pos = camera.position.cpy();
//            final Vector3 original = camera.position.cpy();
//
//            double checkWidth = (mapSize.gridWidth + 1) * 0.5f;
//            double checkHeight = (mapSize.gridHeight + 1) * 0.5f;
//            float cameraDeltaX = 0;
//            if (midX <= map.width - checkWidth && midX >= checkWidth - 0.5f) { // not sure why the lower bound is offset...
//                cameraDeltaX = (dir.deltaX * mapSize.cellWidth);
//            }
//            float cameraDeltaY = 0;
//            if (midY <= map.height - checkHeight && midY >= checkHeight - 0.5f) { // but it causes the camera to jerk without "- 0.5f"
//                cameraDeltaY = (-dir.deltaY * mapSize.cellHeight);
//            }
//            final Vector3 nextPos = camera.position.cpy().add(cameraDeltaX, cameraDeltaY, 0);

            mapSLayers.slide(playerEntity, player.location.x, player.location.y, newX, newY, 0.145f, () ->
            {
                calcFOV(newX, newY);
                calcDijkstra();
                runTurn();
            });
//            mixFOV(newX, newY);
            player.location = Coord.get(newX, newY);
            sound.playFootstep();

//            mapSLayers.addAction(new TemporalAction(0.145f) {
//                @Override
//                protected void update(float percent) {
//                    pos.lerp(nextPos, percent);
//                    camera.position.set(pos);
//                    pos.set(original);
//                    camera.update();
//                }
//
//                @Override
//                protected void end() {
//                    super.end();
//
//                    // Set the map and camera at the same time to have the same offset
////                    mapSLayers.setGridOffsetX(newX - (mapSize.gridWidth >> 1));
////                    mapSLayers.setGridOffsetY(newY - (mapSize.gridHeight >> 1));
//                    camera.position.set(original);
//                    camera.update();
//
//                    calcFOV(newX, newY);
//                    calcDijkstra();
//                    runTurn();
//                }
//            });
        } else {
            Physical creature = map.contents[newX][newY].getCreature();
            if (creature != null) {
                mapSLayers.bump(playerEntity, dir, 0.145f);
                creatures.remove(creature);
                map.contents[newX][newY].remove(creature);
                message("Killed the " + creature.name);

                calcFOV(newX, newY);
                calcDijkstra();
                runTurn();
            } else {
                message("Ran into " + map.contents[newX][newY].getLargeNonCreature().name);
                runTurn();
            }
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the
     * player.
     */
    public void putMap() {
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                double sightAmount = fovResult[x][y];
                if (sightAmount > 0) {
                    EpiTile tile = map.contents[x][y];
                    mapSLayers.put(x, y, tile.getSymbol(), tile.getForegroundColor().toFloatBits(),
                            tile.getBackgroundColor().toFloatBits());
                } else {
                    RememberedTile rt = map.remembered[x][y];
                    if (rt != null) {
                        mapSLayers.put(x, y, rt.symbol, rt.front, rt.back);
                    }
                }
            }
        }

        // Clear the tile the player is on
        mapSLayers.put(player.location.x, player.location.y, ' ', player.color);

        // NOTE - turned off while testing things
//        for (Coord pt : toCursor) {
//            // use a brighter light to trace the path to the cursor, from 170 max lightness to 0 min.
//            mapSLayers.highlight(pt.x, pt.y, 100);
//        }
    }

    @Override
    public void render() {
        super.render();

        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        mapStage.getCamera().position.x = playerEntity.getX();
        mapStage.getCamera().position.y = playerEntity.getY();

        // if the user clicked, we have a list of moves to perform.
        if (!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Points from awaitedMoves.
            if (!mapSLayers.hasActiveAnimations()) {
                    Coord m = awaitedMoves.remove(0);
                    toCursor.remove(0);
                    move(Direction.toGoTo(player.location, m));
                    putMap();
                    infoHandler.updateDisplay();
            }
        } else if (mapInput.hasNext()) {// if we are waiting for the player's input and get input, process it.
            mapInput.next();
            putMap();
            infoHandler.updateDisplay();
        } else if (contextInput.hasNext()) {
            contextInput.next();
            putMap();
            infoHandler.updateDisplay();
        } else if (infoInput.hasNext()){
            infoInput.next();;
            putMap();
            infoHandler.updateDisplay();
        }

        // the order here matters. We apply multiple viewports at different times to clip different areas.
        contextViewport.apply(false);
        contextStage.act();
        // the next line is similar to the next two but handles starting and ending the batch
        contextStage.draw();
        //batch.setProjectionMatrix(contextStage.getCamera().combined);
        //contextStage.getRoot().draw(batch, 1f);

        infoViewport.apply(false);
        infoStage.act();
        // the next line is similar to the next two but handles starting and ending the batch
        infoStage.draw();
        //batch.setProjectionMatrix(infoStage.getCamera().combined);
        //infoStage.getRoot().draw(batch, 1f);

        messageViewport.apply(false);
        messageStage.act();
        // the next line is similar to the next two but handles starting and ending the batch
        messageStage.draw();
        //batch.setProjectionMatrix(messageViewport.getCamera().combined);
        //messageStage.getRoot().draw(batch, 1f);

        //here we apply the other viewport, which clips a different area while leaving the message area intact.
        mapViewport.apply(false);
        mapStage.act();
        //we use a different approach here because we can avoid ending the batch by setting this matrix outside a batch
        batch.setProjectionMatrix(mapStage.getCamera().combined);
        //then we start a batch and manually draw the stage without having it handle its batch...
        batch.begin();
        mapStage.getRoot().draw(batch, 1f);
        //so we can draw the actors independently of the stage while still in the same batch
        playerEntity.draw(batch, 1.0f);
        //we still need to end
        batch.end();
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
        // (gridWidth & 1) is 1 if gridWidth is odd or 0 if it is even; it's good to know and faster than using % , plus
        // in some other cases it has useful traits (x % 2 can be 0, 1, or -1 depending on whether x is negative, while
        // x & 1 will always be 0 or 1).
        mapInput.getMouse().reinitialize(currentZoomX * mapSize.cellWidth, currentZoomY * mapSize.cellHeight,
                mapSize.gridWidth, mapSize.gridHeight,
                (mapSize.gridWidth & 1) * (int) (mapSize.cellWidth * currentZoomX * -0.5f),
                (mapSize.gridHeight & 1) * (int) (mapSize.cellHeight * currentZoomY * -0.5f));
        contextInput.getMouse().reinitialize(currentZoomX * contextSize.cellWidth, currentZoomY * contextSize.cellHeight,
            contextSize.gridWidth, contextSize.gridHeight,
            -(int) (messageSLayers.getRight()),
            -(int) (infoSLayers.getTop() + 8f));
        infoInput.getMouse().reinitialize(currentZoomX * infoSize.cellWidth, currentZoomY * infoSize.cellHeight,
            infoSize.gridWidth, infoSize.gridHeight,
            -(int) (messageSLayers.getRight()), 0);

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

    private final KeyHandler keys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            switch (key) {
                case 'x':
                    fxHandler.sectorBlast(player.location, Element.ACID, 7, Radius.CIRCLE);
                    break;
                case 'X':
                    Element e = rng.getRandomElement(Element.values());
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
                    fxHandler.layeredSparkle(player.location,4, Radius.CIRCLE);
                    break;
                case '+':
                    fxHandler.layeredSparkle(player.location,8, Radius.CIRCLE);
                    break;
                case '[':
                    contextHandler.prior();
                    break;
                case ']':
                    contextHandler.next();
                    break;
                case '{':
                    infoHandler.prior();;
                    break;
                case '}':
                    infoHandler.next();;
                    break;
                case SquidInput.UP_ARROW:
                case 'w':
                    move(Direction.UP);
                    break;
                case SquidInput.DOWN_ARROW:
                case 's':
                    move(Direction.DOWN);
                    break;
                case SquidInput.LEFT_ARROW:
                case 'a':
                    move(Direction.LEFT);
                    break;
                case SquidInput.RIGHT_ARROW:
                case 'd':
                    move(Direction.RIGHT);
                    break;
                case '.':
                    message("Waiting...");
                    runTurn();
                    break;
                case 'o': // Open all the doors nearby
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
                case 'c': // Close all the doors nearby
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
                case 'g': // Pick everythin nearby up
                    message("Picking up all nearby small things");
                    Arrays.stream(Direction.values())
                        .map(d -> player.location.translate(d))
                        .filter(c -> map.inBounds(c))
                        .filter(c -> fovResult[c.x][c.y] > 0)
                        .map(c -> map.contents[c.x][c.y])
                        .forEach(tile -> {
                            Set<Physical> removing = tile.contents
                                .stream()
                                .filter(p -> !p.attached)
                                .collect(Collectors.toSet());
                            tile.contents.removeAll(removing);
                            player.inventory.addAll(removing);
                        });
                    break;
                case 'i': // List out inventory
                    message(player.inventory.stream()
                        .map(i -> i.name)
                        .collect(Collectors.joining(", ", "Carrying: ", "")));
                    break;
                case SquidInput.ESCAPE: {
                    Gdx.app.exit();
                    break;
                }
            }
        }
    };

    private final SquidMouse mapMouse = new SquidMouse(mapSize.cellWidth, mapSize.cellHeight, mapSize.gridWidth, mapSize.gridHeight, 0, 0, new InputAdapter() {
        // if the user clicks within FOV range and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            screenX += player.location.x - (mapSize.gridWidth >> 1);
            screenY += player.location.y - (mapSize.gridHeight >> 1);
            message("TOUCH_UP: " + screenX + ", " + screenY);
            if (screenX < 0 || screenY < 0 || screenX >= map.width || screenY >= map.height){ // Only process if it's in the map view area
                return false;
            }
//            int sx = screenX + mapSLayers.getGridOffsetX(), sy = screenY + mapSLayers.getGridOffsetY();
            switch (button) {
                case Input.Buttons.LEFT:
                    if (awaitedMoves.isEmpty()) {
                        if (toCursor.isEmpty()) {
                            cursor = Coord.get(screenX, screenY);
                            //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                            // player position to the position the user clicked on. The "PreScanned" part is an optimization
                            // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                            // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                            // moves, we only need to do a fraction of the work to find the best path with that info.

                            //toPlayerDijkstra.partialScan((int)(player.stats.get(Stat.SIGHT).actual * 1.45), blocked);
                            toCursor = toPlayerDijkstra.findPathPreScanned(cursor);

                            //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                            // you're finding a path to a monster or loot, and want to bump into it, but here can be
                            // confusing because you would "move into yourself" as your first move without this.
                            // Getting a sublist avoids potential performance issues with removing from the start of an
                            // ArrayList, since it keeps the original list around and only gets a "view" of it.
                            if (!toCursor.isEmpty()) {
                                toCursor = toCursor.subList(1, toCursor.size());
                            }
                        }
                        awaitedMoves.addAll(toCursor);
                    }
                    break;
                case Input.Buttons.RIGHT:
                    contextHandler.tileContents(Coord.get(screenX, screenY), map.contents[screenX][screenY]);
                    break;
            }
            putMap();
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
            message(screenX + ", " + screenY + "; player x=" + player.location.x + ", player y="  + player.location.y);
            //int sx = screenX + mapSLayers.getGridOffsetX(), sy = screenY + mapSLayers.getGridOffsetY();
            if ((screenX < 0 || screenX >= map.width || screenY < 0 || screenY >= map.height) || (cursor.x == screenX && cursor.y == screenY)
                    || fovResult[screenX][screenY] <= 0.0) {
                return false;
            }
            cursor = Coord.get(screenX, screenY);

            //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
            // player position to the position the user clicked on. The "PreScanned" part is an optimization
            // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
            // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
            // moves, we only need to do a fraction of the work to find the best path with that info.
            //toPlayerDijkstra.partialScan((int)(player.stats.get(Stat.SIGHT).actual * 1.45), blocked);
            toCursor = toPlayerDijkstra.findPathPreScanned(cursor);

            //findPathPreScanned includes the current cell (goal) by default, which is helpful when
            // you're finding a path to a monster or loot, and want to bump into it, but here can be
            // confusing because you would "move into yourself" as your first move without this.
            // Getting a sublist avoids potential performance issues with removing from the start of an
            // ArrayList, since it keeps the original list around and only gets a "view" of it.
            if (!toCursor.isEmpty()) {
                toCursor = toCursor.subList(1, toCursor.size());
            }
            putMap();
            return false;
        }
    });

    private final SquidMouse contextMouse = new SquidMouse(contextSize.cellWidth, contextSize.cellHeight, contextSize.gridWidth, contextSize.gridHeight,
            mapSize.gridWidth * mapSize.cellWidth, infoSize.gridHeight * infoSize.cellHeight, new InputAdapter() {
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (screenX < 0 || screenX >= infoSize.gridWidth || screenY < 0 || screenY >= infoSize.gridHeight){
                return false;
            }
            switch (button) {
                case Input.Buttons.LEFT:
                    if (screenX == contextHandler.arrowLeft.x && screenY == contextHandler.arrowLeft.y){
                        contextHandler.prior();
                    } else if (screenX == contextHandler.arrowRight.x && screenY == contextHandler.arrowRight.y){
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
                    if (screenX == infoHandler.arrowLeft.x && screenY == infoHandler.arrowLeft.y){
                        infoHandler.prior();
                    } else if (screenX == infoHandler.arrowRight.x && screenY == infoHandler.arrowRight.y){
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

    /* Iosevka Slab contents
    ABCDEFGHIJKLMNOPQRSTUVWXYZ
    abcdefghijklmnopqrstuvwxyz
    ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ
    ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ
    ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ
    ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ
    ⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵
    0123456789
    ₀₁₂₃₄₅₆₇₈₉
    ⁰¹²³⁴⁵⁶⁷⁸⁹
    ０１２３４５６７８９
    ⓪①②③④⑤⑥⑦⑧⑨
    ⑴⑵⑶⑷⑸⑹⑺⑻⑼
    ¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞↉‰‱℅℆
    ₊₋₌₍₎ₐₑₔₕᵢₖₗₘₙₒₚₛₜₓᵣᵤᵥᵦᵧᵨᵩᵪ
    ⁺⁻⁼⁽⁾ⁱⁿᴬᴭᴮᴯᴰᴱᴲᴳᴴᴵᴶᴷᴸᴹᴺᴻᴼᴽᴾᴿᵀᵁᵂᵃᵄᵅᵆᵇᵈᵉᵊᵋᵌᵍᵸᵎᵏᵐᵑᵒᵓᵔᵕᵖᵗᵘᵚᵛᵜᵝᵞᵟᵠᵡᶛᶜᶝᶞᶟᶠᶡᶢᶣᶤᶥᶦᶧᶨᶩᶫᶬᶭᶮᶯᶰᶱᶲᶳᶴᶵᶶᶷᶸᶹᶺᶻᶼᶽᶾʰʱʲʳʴʵʶʷʸ˟ˠˡˢˣˤ᾿῀῁
    
    ´῾‎‘’‚‛“”„‟•․‥…‧′″‴‵‶‷ʹʺʻʼʽˀˁˆˇˈˉ˭ˊˋ˘˙˚˜˝ˍˎˏ˒˓˔˕˖˗˛˳˷ͺˬ
    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~¡¢£¤¥¦§¨©ª«¬®¯°±´µ¶·¸º»¿
    ！＂＃＄％＆＇（）＊＋，－．／：；＜＝＞？＠［＼］＾＿｀｛｜｝～｡｢｣､￠￡￥、。「」『』
    
    ─━╴╵╶╷╸╹╺╻╼╽╾╿│┃├┝┞┟┠┡┢┣┤┥┦┧┨┩┪┫┼┽┾┿╀╁╂╃╄╅╆╇╈╉╊╋
    ┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻
    ╌╍╎╏┄┅┆┇┈┉┊┋
    ═║╒╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬
    ╭╮╯╰
    ▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▐░▒▓
    ▀■□▬▭▮▯▲△▴▵▶▷▸▹▼▽▾▿◀◁◂◃◆◇○◌●◢◣◤◥◦◯★⋆∗∘∙⬟⬠⬡⬢⬣⭓⭔
    −‐‑‒–—―‾∼∽≁≈≉≠≡≢⌈⌉⌊⌋╳╱⁄⌿╲⍀∣ǀǁǂ†‡∤⍭⍧⍦∫∬∮˥˦˧˨˩
    ←↑→↓↔↕↖↗↘↙⤡⤢↞↟↠↡↢↣ːˑ
    〈〉❬❭❮❯❰❱⟨⟩⟪⟫‹›≤≥⩽⩾≮≯≰≱⊂⊃⊄⊅∈∉∋∌⋀⋀∧⍲⋁⋁∨⍱⋂⋂∩⋃⋃∪∏∐⨿
    ♀♁♂♠♣♥♦♪⚐⚑⚡√✓✔✕✖✗✘✚∝∞⊕⊖⊙⊛
    ⌶⌷⌸⌹⌺⌻⌼⌽⌾⍁⍂⍃⍄⍅⍆⍇⍈⍉⍊⍋⍌⍍⍎⍏⍐⍑⍒⍓⍔⍕⍖⍗⍘⍙⍚⍛⍜⍝⍞⍟⍠⍡⍢⍣⍤⍥⍨⍩⍪⍫⍬⍮⍯⍰
    ∀∂∃∄∅∆∇∑∓⊢⊣⊤⊥⊦⊧⊨⊬⊭⋸※‼‽₣₤₧₨₩€₽℃℉ℓ№℗℠™Ω℧℩Å℮ﬁﬂ⍳⍴⍵⍶⍷⍸⍹⍺ⱫⱬⱭⱯⱰⱱⱲⱳⱷⱹⱻⱼⱽⱾⱿꜧꝚꝛꞀꞁꞎꞒꞓꞰꞱꬰꬵꭓꭤꭥ?ͻͼͽ
    ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ
    АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩ
    αβγδεζηθικλμνξοπρςστυφχψω
    ᴀᴁᴂᴃᴄᴅᴆᴇᴈᴉᴊᴋᴌᴍᴎᴏᴐᴔᴕᴖᴗᴘᴙᴚᴛᴜᴠᴡᴢᴣᴤᴥᴦᴧᴨᴩᴪᴫᵷᵻᵼᵽᵿ
    
    
    ⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛
    ⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿⢀⢁⢂⢃⢄⢅⢆⢇⢈⢉⢊⢋⢌⢍⢎⢏⢐⢑⢒⢓⢔⢕⢖⢗⢘⢙⢚⢛⢜⢝⢞⢟⢠⢡⢢⢣⢤⢥⢦⢧⢨⢩⢪⢫⢬⢭⢮⢯⢰⢱⢲⢳⢴⢵⢶⢷
    ⢸⢹⢺⢻⢼⢽⢾⢿⣀⣁⣂⣃⣄⣅⣆⣇⣈⣉⣊⣋⣌⣍⣎⣏⣐⣑⣒⣓⣔⣕⣖⣗⣘⣙⣚⣛⣜⣝⣞⣟⣠⣡⣢⣣⣤⣥⣦⣧⣨⣩⣪⣫⣬⣭⣮⣯⣰⣱⣲⣳⣴⣵⣶⣷⣸⣹⣺⣻⣼⣽⣾⣿
    
    
    ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄ
    ąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧ
    ŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǃǄǅǆǇǈǉǊǋǌ
    ǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱ
    ȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕʖʗ
    ʘʙʚʛʜʝʞʟʠʡʢʣʤʥʦʧʨʩʪʫʬʭʮʯˌ˞;Ϳ΄΅Ά·ΈΉΊΌΎΏΐΪΫάέήίΰϊϋόύώϕϖϲϳϴϷϸϹϽϾϿЀЁЂЃЄЅІЇЈЉЊЋЌЍЎЏ
    ЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяѐёђѓєѕіїјљњћќѝўџѦѧѪѫѴѵѶѷѸѹҐґҒғҖҗҘҙҚқҢңҪҫҬҭҮүҰұҲҳҶҷҸҹҺһӀӁӂ
    ӏӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԚԛԜԝ᪲ᶏᶐᶑᶙ
    ᶿᷧᷨᷩᷪᷫᷮᷯᷰᷱᷲᷳᷴḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓ
    ṔṕṖṗṘṙṚṛṜṝṞṟṠṡṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẖẗẘẙẚẛẜẝẞẟẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặ
    ẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἁἂἃἄἅἆἇἈἉἊἋἌἍἎἏἐἑἒἓἔἕἘἙἚἛἜἝ
    ἠἡἢἣἤἥἦἧἨἩἪἫἬἭἮἯἰἱἲἳἴἵἶἷἸἹἺἻἼἽἾἿὀὁὂὃὄὅὈὉὊὋὌὍὐὑὒὓὔὕὖὗὙὛὝὟὠὡὢὣὤὥὦὧὨὩὪὫὬὭὮὯὰάὲέὴήὶίὸόὺύὼώᾀᾁᾂᾃᾄᾅᾆᾇᾈᾉᾊᾋᾌᾍᾎᾏ
    ᾐᾑᾒᾓᾔᾕᾖᾗᾘᾙᾚᾛᾜᾝᾞᾟᾠᾡᾢᾣᾤᾥᾦᾧᾨᾩᾪᾫᾬᾭᾮᾯᾰᾱᾲᾳᾴᾶᾷᾸᾹᾺΆᾼ᾽ιῂῃῄῆῇῈΈῊΉῌ῍῎῏ῐῑῒΐῖῗῘῙῚΊ῝῞῟ῠῡῢΰῤῥῦῧῨῩῪΎῬ῭΅`ῲῳῴῶῷῸΌῺΏῼ
     */
}
