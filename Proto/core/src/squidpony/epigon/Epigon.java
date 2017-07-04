package squidpony.epigon;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.epigon.actions.MovementAction;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.dm.RecipeMixer;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.RememberedTile;
import squidpony.epigon.mapping.WorldGenerator;
import squidpony.epigon.playground.HandBuilt;
import squidpony.epigon.universe.Stat;
import squidpony.panel.IColoredString;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {

    // Set up sizing all in one place
    static{
        int w = 70;
        int h = 30;
        int cellW = 12;
        int cellH = 24;
        int bottomH = 6;
        int rightW = 30;
        mapSize = new PanelSize(300, 90, cellW, cellH);
        mapViewportSize = new PanelSize(w, h, cellW, cellH);
        messageSize = new PanelSize(w, bottomH, cellW, cellH);
        infoSize = new PanelSize(rightW, h, cellW, cellH);
        contextSize = new PanelSize(rightW, bottomH, cellW, cellH);
    }

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final PanelSize mapSize;
    public static final PanelSize mapViewportSize;
    public static final PanelSize messageSize;
    public static final PanelSize infoSize;
    public static final PanelSize contextSize;

    public static long seed = 0xBEEFD00DFADEAFADL;
    // this is separated from the StatefulRNG so you can still call LightRNG-specific methods, mainly skip()
    public static final LightRNG lightRNG = new LightRNG(seed);
    public static final StatefulRNG rng = new StatefulRNG(lightRNG);
    public static final RecipeMixer mixer = new RecipeMixer();
    public static final HandBuilt handBuilt = new HandBuilt();

    // Audio
    private SoundManager sound;

    // Display
    SpriteBatch batch;
    private SquidLayers display;
    private TextCellFactory messageText;
    private TextCellFactory infoText;
    private SquidColorCenter colorCenter;
    private LinesPanel<Color> messages, infoPanel;
    private SquidInput input;
    private Color bgColor;
    private List<Coord> toCursor;
    private int framesWithoutAnimation;

    // World
    private WorldGenerator worldGenerator;
    private EpiMap map;
    private RememberedTile[][] remembered;
    private GreasedRegion blocked;
    private DijkstraMap toPlayerDijkstra;
    private Coord cursor;
    private Physical player;
    private ArrayList<Coord> awaitedMoves;
    private FOV fov = new FOV(FOV.SHADOW);
    private double[][] fovResult;
    private double[][] priorFovResult;

    // WIP stuff, needs large sample map
    private Stage stage, messageStage, infoStage, contextStage;
    private Viewport viewport, messageViewport, infoViewport, contextViewport;
    private Camera camera;
    private AnimatedEntity playerEntity;

    private void init(){
        fovResult = new double[mapSize.gridWidth][mapSize.gridHeight];
        priorFovResult = new double[mapSize.gridWidth][mapSize.gridHeight];
        remembered = new RememberedTile[mapSize.gridWidth][mapSize.gridHeight];

        Coord.expandPoolTo(mapSize.gridWidth, mapSize.gridHeight);
        bgColor = SColor.BLACK_DYE;
    }

    @Override
    public void create() {
        System.out.println("Working in folder: " + System.getProperty("user.dir"));
        init();

        System.out.println("Loading sound manager.");
        sound = new SoundManager();
        colorCenter = new SquidColorCenter();

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();

        System.out.println("Putting together display.");
        viewport = new StretchViewport(mapViewportSize.pixelWidth(), mapViewportSize.pixelHeight());
        messageViewport = new StretchViewport(messageSize.pixelWidth(), messageSize.pixelHeight());
        infoViewport = new StretchViewport(infoSize.pixelWidth(), infoSize.pixelHeight());
        contextViewport = new StretchViewport(contextSize.pixelWidth(), contextSize.pixelHeight());

        camera = viewport.getCamera();

        // Here we make sure our Stages, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(viewport, batch);
        messageStage = new Stage(messageViewport, batch);
        infoStage = new Stage(infoViewport, batch);
        contextStage = new Stage(contextViewport, batch);

        // Set up the text display portions
        messageText = DefaultResources.getStretchablePrintFont()
            .width(5f)
            .height(messageSize.cellHeight)
            .initBySize();
        messages = new LinesPanel<>(new GDXMarkup(), messageText, messageSize.gridHeight);
        messages.clearingColor = null;

        infoText = DefaultResources.getStretchablePrintFont()
            .width(5f)
            .height(infoSize.cellHeight)
            .initBySize();
        infoPanel = new LinesPanel<>(new GDXMarkup(), infoText, infoSize.gridHeight);
        infoPanel.clearingColor = null;

        display = new SquidLayers(
            mapViewportSize.gridWidth,
            mapViewportSize.gridHeight,
            mapViewportSize.cellWidth,
            mapViewportSize.cellHeight,
            DefaultResources.getStretchableSlabFont(),
            colorCenter,
            colorCenter,
            new char[mapSize.gridWidth][mapSize.gridHeight]);

        display.setTextSize(mapSize.cellWidth + 2, mapSize.cellHeight + 2); // weirdly, this seems to help with flicker

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        display.setAnimationDuration(0.13f);

        messages.setBounds(0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        infoPanel.setBounds(0, contextSize.pixelHeight(), infoSize.pixelWidth(), infoSize.pixelHeight());
        display.setPosition(0, 0);
        viewport.setScreenBounds(0, messageSize.pixelHeight(), mapViewportSize.pixelWidth(), mapViewportSize.pixelHeight());
        infoViewport.setScreenBounds(mapViewportSize.pixelWidth(), contextSize.pixelHeight(), infoSize.pixelWidth(), infoSize.pixelHeight());

        cursor = Coord.get(-1, -1);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        input = new SquidInput(keys, mapMouse);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, messageStage, input));

        stage.addActor(display);
        messageStage.addActor(messages);
        infoStage.addActor(infoPanel);

        startGame();
    }

    private void startGame() {
        message("Generating world.");
        worldGenerator = new WorldGenerator();
        map = worldGenerator.buildWorld(mapSize.gridWidth, mapSize.gridHeight, 1)[0];

        GreasedRegion floors = new GreasedRegion(map.opacities(), 0.999);

        player = mixer.buildPhysical(handBuilt.playerBlueprint);

        player.location = floors.singleRandom(rng);
        Arrays.stream(Direction.OUTWARDS)
            .map(d -> player.location.translate(d))
            .filter(c -> map.inBounds(c))
            .filter(c -> rng.nextBoolean())
            .forEach(c -> map.contents[c.x][c.y].add(mixer.mix(handBuilt.swordRecipe, Collections.singletonList(mixer.buildPhysical(rng.getRandomElement(Inclusion.values()))), Collections.emptyList())));

        playerEntity = display.animateActor(player.location.x, player.location.y, player.symbol, player.color);

        display.setGridOffsetX(player.location.x - (mapViewportSize.gridWidth >> 1));
        display.setGridOffsetY(player.location.y - (mapViewportSize.gridHeight >> 1));

        calcFOV(player.location.x, player.location.y);

        toPlayerDijkstra = new DijkstraMap(map.simpleChars(), DijkstraMap.Measurement.EUCLIDEAN);
        blocked = new GreasedRegion(mapSize.gridWidth, mapSize.gridHeight);
        calcDijkstra();

        message("Have fun!");
        message("The fate of the worlds is in your hands...");
        message("Bump into walls and stuff.");
        message("Use ? for help, or q to quit.");
        message("Use mouse, numpad, or arrow keys to move.");
        Stat[] stats = Stat.values();
        for (int s = 0; s < stats.length; s++) {
            info(stats[s].toString());
        }
    }

    private void message(String text) {
        messages.addFirst(new IColoredString.Impl<>(text, Color.WHITE));
    }

    private void info(String text) {
        infoPanel.addLast(new IColoredString.Impl<>(text, SColor.DB_INK));
    }

    private void calcFOV(int checkX, int checkY) {
        FOV.reuseFOV(map.opacities(), fovResult, checkX, checkY, player.stats.get(Stat.SIGHT).actual, Radius.CIRCLE);
        for (int x = 0; x < mapSize.gridWidth; x++) {
            for (int y = 0; y < mapSize.gridHeight; y++) {
                if (fovResult[x][y] > 0) {
                    if (remembered[x][y] == null) {
                        remembered[x][y] = new RememberedTile(map.contents[x][y]);
                    } else {
                        remembered[x][y].remake(map.contents[x][y]);
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
        blocked.refill(fovResult, 0.0001, 1000.0).fringe8way();
        toPlayerDijkstra.partialScan((int)(player.stats.get(Stat.SIGHT).actual * 1.45), blocked);
    }

    private Color calcFadeoutColor(Color color, double amount){
        double d = Double.max(amount, 0.3);
        return colorCenter.lerp(SColor.BLACK, color, d);
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     */
    private void move(Direction dir) {
        MovementAction move = new MovementAction(player, dir, false);
        if (map.actionValid(move)) {
            final float midX = player.location.x + dir.deltaX * 0.5f; // this was 0.2f instead of 0.5f.
            final float midY = player.location.y + dir.deltaY * 0.5f; // 0.2f is bad and wrong. badong.
            final Vector3 pos = camera.position.cpy();
            final Vector3 original = camera.position.cpy();

            double checkWidth = (mapViewportSize.gridWidth + 1) * 0.5f;
            double checkHeight = (mapViewportSize.gridHeight + 1) * 0.5f;
            float cameraDeltaX = 0;
            if (midX <= mapSize.gridWidth - checkWidth && midX >= checkWidth) {
                cameraDeltaX = (dir.deltaX * mapViewportSize.cellWidth);
            }
            float cameraDeltaY = 0;
            if (midY <= mapSize.gridHeight - checkHeight && midY >= checkHeight) {
                cameraDeltaY = (-dir.deltaY * mapViewportSize.cellHeight);
            }
            final Vector3 nextPos = camera.position.cpy().add(cameraDeltaX, cameraDeltaY, 0);

            int newX = player.location.x + dir.deltaX;
            int newY = player.location.y + dir.deltaY;
            display.slide(playerEntity, newX, newY);
            mixFOV(newX, newY);
            player.location = Coord.get(newX, newY);
            sound.playFootstep();

            display.addAction(new TemporalAction(display.getAnimationDuration()) {
                @Override
                protected void update(float percent) {
                    pos.lerp(nextPos, percent);
                    camera.position.set(pos);
                    pos.set(original);
                    camera.update();
                }

                @Override
                protected void end() {
                    super.end();

                    // Set the map and camera at the same time to have the same offset
                    display.setGridOffsetX(newX - (mapViewportSize.gridWidth >> 1));
                    display.setGridOffsetY(newY - (mapViewportSize.gridHeight >> 1));
                    camera.position.set(original);
                    camera.update();

                    calcFOV(newX, newY);
                    calcDijkstra();
                }
            });
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the
     * player.
     */
    public void putMap() {
        int offsetX = display.getGridOffsetX();
        int offsetY = display.getGridOffsetY();
        for (int i = -1, x = Math.max(0, offsetX - 1); i <= mapViewportSize.gridWidth && x < mapSize.gridWidth; i++, x++) {
            for (int j = -1, y = Math.max(0, offsetY - 1); j <= mapViewportSize.gridHeight && y < mapSize.gridHeight; j++, y++) {
                if (map.inBounds(Coord.get(x, y))) {
                    double sightAmount = fovResult[x][y];
                    Color fore;
                    Color back;
                    if (sightAmount > 0) {
                        EpiTile tile = map.contents[x][y];
                        fore = calcFadeoutColor(tile.getForegroundColor(), sightAmount);
                        back = calcFadeoutColor(tile.getBackgroundColor(), sightAmount);
                        display.put(x, y, tile.getSymbol(), fore, back);
                    } else {
                        RememberedTile rt = remembered[x][y];
                        if (rt != null) {
                            display.put(x, y, rt.symbol, rt.front, rt.back);
                        } else {
                            display.put(x, y, ' ', SColor.SLATE, bgColor);
                        }
                    }
                } else {
                    display.put(x, y, ' ', SColor.SLATE, bgColor);
                }
            }
        }

        // Clear the tile the player is on
        display.put(player.location.x, player.location.y, ' ', SColor.TRANSPARENT);

//        SColor front;
//        SColor back;

//        back = SColor.OLD_LACE;
//        for (int x = mapViewportSize.gridWidth; x < TOTAL_WIDTH; x++) {
//            for (int y = 0; y < TOTAL_HEIGHT; y++) {
//                display.getBackgroundLayer().put(x, y, back);
//            }
//        }

//        front = SColor.JAPANESE_IRIS;
//        display.putString(mapViewportSize.gridWidth + 4, 1, "STATS", front, back);
//        int y = 3;
//        int x = mapViewportSize.gridWidth + 1;
//        int spacing = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().orElse(0) + 2;
//        for (Entry<Stat, LiveValue> e : player.stats.entrySet()) {
//            int diff = (int) Math.round(e.getValue().actual - e.getValue().base);
//            String diffString = "";
//            if (diff < 0) {
//                diffString = " " + diff;
//            } else {
//                diffString = " +" + diff;
//            }
//            display.putString(x, y, e.getKey().toString() + ":", front, back);
//            display.putString(x + spacing, y, (int)Math.round(e.getValue().base) + diffString, front, back);
//            y++;
//        }

        for (Coord pt : toCursor) {
            // use a brighter light to trace the path to the cursor, from 170 max lightness to 0 min.
            display.highlight(pt.x, pt.y, 100);
        }
    }

    @Override
    public void render() {
        super.render();

        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // need to display the map every frame, since we clear the screen to avoid artifacts.
        putMap();

        // if the user clicked, we have a list of moves to perform.
        if (!awaitedMoves.isEmpty()) {
            // this doesn't check for input, but instead processes and removes Points from awaitedMoves.
            if (!display.hasActiveAnimations()) {
                if (++framesWithoutAnimation >= 2) {
                    framesWithoutAnimation = 0;
                    Coord m = awaitedMoves.remove(0);
                    toCursor.remove(0);
                    move(Direction.toGoTo(player.location, m));
                }
            }
        } else if (input.hasNext()) {// if we are waiting for the player's input and get input, process it.
            input.next();
        }
        // the order here matters. We apply multiple viewports at different times to clip different areas.
        messageViewport.apply(false);
        messageStage.act();
        batch.begin();
        batch.setProjectionMatrix(messageViewport.getCamera().combined);
        batch.setColor(SColor.INDIGO_DYE);
        batch.draw(messageText.getSolid(), 0, 0, messageSize.pixelWidth(), messageSize.pixelHeight());
        messageStage.getRoot().draw(batch, 1f);

        infoViewport.apply(false);
        infoStage.act();
        batch.setProjectionMatrix(infoStage.getCamera().combined);
        batch.setColor(SColor.PEACH_YELLOW);
        batch.draw(infoText.getSolid(), 0, 0, infoSize.pixelWidth(), infoSize.pixelHeight());
        infoStage.getRoot().draw(batch, 1f);
        batch.end();

        //here we apply the other viewport, which clips a different area while leaving the message area intact.
        viewport.apply(false);
        stage.act();
        // each stage has its own batch that it starts an ends, so certain batch-wide effects only change one stage.
        stage.draw();
        batch.begin();
        display.drawActor(batch, 1.0f, playerEntity);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        //very important to have the mouse behave correctly if the user fullscreens or resizes the game!

        // message box won't respond to clicks on the far right if the stage hasn't been updated with a larger size
        float currentZoomX = (float) width / (mapViewportSize.gridWidth + infoSize.gridWidth);
        // total new screen height in pixels divided by total number of rows on the screen
        float currentZoomY = (float) height / (mapViewportSize.gridHeight + messageSize.gridHeight);

        // message box should be given updated bounds since I don't think it will do this automatically
        messages.setBounds(0, 0, currentZoomX * messageSize.gridWidth, currentZoomY * messageSize.gridHeight);
        infoPanel.setBounds(0, 0, currentZoomX * infoSize.gridWidth, currentZoomY * infoSize.gridHeight);

        // SquidMouse turns screen positions to cell positions, and needs to be told that cell sizes have changed
        input.getMouse().reinitialize(currentZoomX, currentZoomY, mapViewportSize.gridWidth, mapViewportSize.gridHeight, 0, 0);

        //currentZoomX = CELL_WIDTH / currentZoomX;
        //currentZoomY = CELL_HEIGHT / currentZoomY;
        //printText.bmpFont.getData().lineHeight /= currentZoomY;
        //printText.bmpFont.getData().descent /= currentZoomY;
        infoViewport.update(width, height, false);
        infoViewport.setScreenBounds((int) messages.getWidth(), (int) messages.getHeight(), (int) infoPanel.getWidth(), (int) infoPanel.getHeight());

        messageViewport.update(width, height, false);
        messageViewport.setScreenBounds(0, 0, (int) messages.getWidth(), (int) messages.getHeight());

        viewport.update(width, height, false);
        viewport.setScreenBounds(0, (int) messages.getHeight(), width - (int) infoPanel.getWidth(), height - (int) messages.getHeight());
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
        return mapViewportSize.pixelWidth() + infoSize.pixelWidth();
    }

    public static int totalPixelHeight() {
        return mapViewportSize.pixelHeight() + messageSize.pixelHeight();
    }

    private final KeyHandler keys = new KeyHandler() {
        @Override
        public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            Direction dir = Direction.NONE;
            switch (key) {
                case SquidInput.UP_ARROW:
                case 'w':
                    dir = Direction.UP;
                    break;
                case SquidInput.DOWN_ARROW:
                case 's':
                    dir = Direction.DOWN;
                    break;
                case SquidInput.LEFT_ARROW:
                case 'a':
                    dir = Direction.LEFT;
                    break;
                case SquidInput.RIGHT_ARROW:
                case 'd':
                    dir = Direction.RIGHT;
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
            move(dir);
        }
    };

    private final SquidMouse mapMouse = new SquidMouse(mapViewportSize.cellWidth, mapViewportSize.cellHeight, mapViewportSize.gridWidth, mapViewportSize.gridHeight, 0, 0, new InputAdapter() {
        // if the user clicks within FOV range and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            int sx = screenX + display.getGridOffsetX(), sy = screenY + display.getGridOffsetY();
            switch (button) {
                case Input.Buttons.LEFT:
                    if (awaitedMoves.isEmpty()) {
                        if (toCursor.isEmpty()) {
                            cursor = Coord.get(sx, sy);
                            //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                            // player position to the position the user clicked on. The "PreScanned" part is an optimization
                            // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                            // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                            // moves, we only need to do a fraction of the work to find the best path with that info.
                            toPlayerDijkstra.partialScan((int)(player.stats.get(Stat.SIGHT).actual * 1.45), blocked);
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
                    String tileDescription = "[" + sx + ", " + sy + "] ";
                    EpiTile tile = map.contents[sx][sy];
                    if (tile.floor != null) {
                        tileDescription += tile.floor.name + " floor";
                    } else {
                        tileDescription += "empty space";
                    }
                    if (!tile.contents.isEmpty()) {
                        tileDescription = tile.contents.stream()
                            .map(p -> p.name)
                            .collect(Collectors.joining(", ", tileDescription + ", ", ""));
                    }
                    message(tileDescription);
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
            int sx = screenX + display.getGridOffsetX(), sy = screenY + display.getGridOffsetY();
            if ((sx < 0 || sx >= mapSize.gridWidth || sy < 0 || sy >= mapSize.gridHeight) || (cursor.x == sx && cursor.y == sy)) {
                return false;
            }
            cursor = Coord.get(sx, sy);

            //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
            // player position to the position the user clicked on. The "PreScanned" part is an optimization
            // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
            // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
            // moves, we only need to do a fraction of the work to find the best path with that info.
            toPlayerDijkstra.partialScan((int)(player.stats.get(Stat.SIGHT).actual * 1.45), blocked);
            toCursor = toPlayerDijkstra.findPathPreScanned(cursor);

            //findPathPreScanned includes the current cell (goal) by default, which is helpful when
            // you're finding a path to a monster or loot, and want to bump into it, but here can be
            // confusing because you would "move into yourself" as your first move without this.
            // Getting a sublist avoids potential performance issues with removing from the start of an
            // ArrayList, since it keeps the original list around and only gets a "view" of it.
            if (!toCursor.isEmpty()) {
                toCursor = toCursor.subList(1, toCursor.size());
            }

            return false;
        }
    });
}
