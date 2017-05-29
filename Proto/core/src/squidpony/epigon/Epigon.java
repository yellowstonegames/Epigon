package squidpony.epigon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final int MAP_WIDTH = 100;
    public static final int BIG_MAP_WIDTH = MAP_WIDTH * 3;
    public static final int MAP_HEIGHT = 30;
    public static final int BIG_MAP_HEIGHT = MAP_HEIGHT * 3;

    public static final int INFO_WIDTH = 30;
    public static final int INFO_HEIGHT = MAP_HEIGHT;

    public static final int MESSAGE_HEIGHT = 7;

    public static final int TOTAL_WIDTH = MAP_WIDTH;// + INFO_WIDTH;
    public static final int TOTAL_HEIGHT = MAP_HEIGHT;// + MESSAGE_HEIGHT;

    private static final float HALF_MAP_HEIGHT = (MAP_HEIGHT + 1) * 0.5f;
    private static final float HALF_MAP_WIDTH = (MAP_WIDTH + 1) * 0.5f;

    public static final int MESSAGE_WIDTH = TOTAL_WIDTH;

    // Cell sizing
    public static final int CELL_WIDTH = 11;
    public static final int CELL_HEIGHT = 21;

    // Pixels
    public static final int TOTAL_PIXEL_WIDTH = TOTAL_WIDTH * CELL_WIDTH;
    public static final int TOTAL_PIXEL_HEIGHT = TOTAL_HEIGHT * CELL_HEIGHT;

    public static final StatefulRNG rng = new StatefulRNG(0xBEEFD00DBABAB00EL);

    // 
    SpriteBatch batch;
    private SquidLayers display;
    private char[][] simpleChars;
    private EpiMap map;
    private SquidInput input;
    private Color bgColor;
    private Stage stage;
    private DijkstraMap playerToCursor;
    private Coord cursor;
    private Physical player;
    private List<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;
    private int framesWithoutAnimation;

    // WIP stuff, needs large sample map
    private Viewport viewport;
    private Camera camera;
    private AnimatedEntity playerEntity;

    @Override
    public void create() {
        System.out.println("Creating new game.");
        System.out.println("Working in folder: " + System.getProperty("user.dir"));
        System.out.println("Loading sound manager...");
        SoundManager sound = new SoundManager();
        //sound.playSoundFX("footsteps-1");

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();

        viewport = new StretchViewport(TOTAL_PIXEL_WIDTH, TOTAL_PIXEL_HEIGHT);
        camera = viewport.getCamera();
        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(viewport, batch);

        //map = World.getDefaultMap();
        Coord.expandPoolTo(BIG_MAP_WIDTH, BIG_MAP_HEIGHT);
        DungeonGenerator sdg = new DungeonGenerator(BIG_MAP_WIDTH, BIG_MAP_HEIGHT, rng);
        sdg.addGrass(15);
        sdg.addWater(15);
        sdg.addDoors(20, true);
        simpleChars = DungeonUtility.closeDoors(sdg.generate());
        map = new EpiMap(simpleChars);
        display = new SquidLayers(MAP_WIDTH, MAP_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getStretchableSlabFont(),
            DefaultResources.getSCC(), DefaultResources.getSCC(), simpleChars);

        //display.getTextFactory().fit(Arrays.deepToString(simpleMap)); // not currently needed
        display.setTextSize(CELL_WIDTH+2, CELL_HEIGHT+2); // weirdly, this seems to help with flicker
        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        display.setAnimationDuration(0.13f);

        display.setPosition(0, 0);

        cursor = Coord.get(-1, -1);

        // Create an actual player
        player = new Physical();
        player.creatureData = new Creature();
        player.creatureData.abilities = new HashSet<>();
        player.name = "Great Hero";
//        Arrays.stream(Stat.values()).forEach(s -> player.stats.put(s, rng.between(20, 100)));
//        Arrays.stream(Stat.values()).forEach(s -> player.currentStats.put(s, player.stats.get(s) + rng.between(-10, 30)));

        Physical sword = new Physical();
        sword.color = SColor.SILVER;
        sword.symbol = '/';
        sword.name = "Sword";

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        //DijkstraMap is the pathfinding swiss-army knife we use here to find a path to the latest cursor position.
        GreasedRegion floors = new GreasedRegion(simpleChars, '.');
        player.location = floors.singleRandom(rng);
        playerEntity = display.animateActor(player.location.x, player.location.y, '@', SColor.ALICE_BLUE);

        // this is new, related to camera movement
        display.setGridOffsetX(player.location.x - (MAP_WIDTH >> 1));
        display.setGridOffsetY(player.location.y - (MAP_HEIGHT >> 1));

        playerToCursor = new DijkstraMap(simpleChars, DijkstraMap.Measurement.MANHATTAN);
        playerToCursor.setGoal(player.location);
        playerToCursor.scan(null);

        bgColor = SColor.DARK_SLATE_GRAY;

        input = new SquidInput(keys, mapMouse);

        //Setting the InputProcessor is ABSOLUTELY NEEDED TO HANDLE INPUT
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));

        // and then add display, our one visual component, to the list of things that act in Stage.
        stage.addActor(display);
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow. In a
     * fully-fledged game, this would not be organized like this, but this is a one-file demo.
     *
     * @param dir
     */
    private void move(Direction dir) {
        int newX = player.location.x + dir.deltaX;
        int newY = player.location.y + dir.deltaY;
        if (newX >= 0 && newY >= 0 && newX < BIG_MAP_WIDTH && newY < BIG_MAP_HEIGHT && map.contents[newX][newY].getSymbol() != '#') {
            final float midX = player.location.x + dir.deltaX * 0.5f;
            final float midY = player.location.y + dir.deltaY * 0.5f;
            final Vector3 pos = camera.position.cpy();
            final Vector3 original = camera.position.cpy();
            float cameraDeltaX = midX > BIG_MAP_WIDTH - HALF_MAP_WIDTH || midX < HALF_MAP_WIDTH ? 0 : (dir.deltaX * CELL_WIDTH);
            float cameraDeltaY = midY > BIG_MAP_HEIGHT - HALF_MAP_HEIGHT || midY < HALF_MAP_HEIGHT ? 0 : (dir.opposite().deltaY * CELL_HEIGHT);
            final Vector3 nextPos = camera.position.cpy().add(cameraDeltaX, cameraDeltaY, 0);

            display.slide(playerEntity, newX, newY);

            display.addAction(
                new TemporalAction(display.getAnimationDuration()) {
                @Override
                protected void update(float percent) {
                    pos.lerp(nextPos, percent);
                    camera.position.set(pos); //Math.round(pos.x), Math.round(pos.y), pos.z
                    pos.set(original);
                    camera.update();
                }

                @Override
                protected void end() {
                    super.end();
                    player.location = Coord.get(newX, newY);
                    display.setGridOffsetX(player.location.x - (MAP_WIDTH >> 1));
                    display.setGridOffsetY(player.location.y - (MAP_HEIGHT >> 1));
                    camera.position.set(original);
                    camera.update();

                }
            });
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the
     * player.
     */
    public void putMap() {
        int offsetX = display.getGridOffsetX(), offsetY = display.getGridOffsetY();
        for (int i = -1, x = Math.max(0, offsetX - 1); i <= MAP_WIDTH && x < BIG_MAP_WIDTH; i++, x++) {
            for (int j = -1, y = Math.max(0, offsetY - 1); j <= MAP_HEIGHT && y < BIG_MAP_HEIGHT; j++, y++) {
                if (map.inBounds(Coord.get(x, y))) {
                    EpiTile tile = map.contents[x][y];
                    display.put(x, y, tile.getSymbol(), tile.getForegroundColor(), SColor.DB_INK);
                } else {
                    display.put(x, y, '`', SColor.SLATE, SColor.DB_INK);
                }
            }
        }

        SColor front;
        SColor back;
        /*
        back = SColor.OLD_LACE;
        for (int x = MAP_WIDTH; x < TOTAL_WIDTH; x++) {
            for (int y = 0; y < TOTAL_HEIGHT; y++) {
                display.getBackgroundLayer().put(x, y, back);
            }
        }

        front = SColor.JAPANESE_IRIS;
        display.putString(MAP_WIDTH + 4, 1, "STATS", front, back);
        int y = 3;
        int x = MAP_WIDTH + 1;
        int spacing = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().orElse(0) + 2;
        for (Entry<Stat, Integer> e : player.stats.entrySet()) {
            int diff = player.currentStats.get(e.getKey()) - e.getValue();
            String diffString = "";
            if (diff < 0) {
                diffString = " " + diff;
            } else {
                diffString = " +" + diff;
            }
            display.putString(x, y, e.getKey().toString() + ":", front, back);
            display.putString(x + spacing, y, e.getValue() + diffString, front, back);
            y++;
        }
         */

        for (Coord pt : toCursor) {
            // use a brighter light to trace the path to the cursor, from 170 max lightness to 0 min.
            display.highlight(pt.x, pt.y, 100);
        }

        //places the player as an '@' at his position in orange (6 is an index into SColor.LIMITED_PALETTE).
        //display.put(player.location.x, player.location.y, '@', 6);
        for (int i = 0; i < MESSAGE_HEIGHT; i++) {
            // Output messages
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
                    if(awaitedMoves.isEmpty())
                    {
                        // the next two lines remove any lingering data needed for earlier paths
                        playerToCursor.clearGoals();
                        playerToCursor.resetMap();
                        // the next line marks the player as a "goal" cell, which seems counter-intuitive, but it works because all
                        // cells will try to find the distance between themselves and the nearest goal, and once this is found, the
                        // distances don't change as long as the goals don't change. Since the mouse will move and new paths will be
                        // found, but the player doesn't move until a cell is clicked, the "goal" is the non-changing cell, so the
                        // player's position, and the "target" of a pathfinding method like DijkstraMap.findPathPreScanned() is the
                        // currently-moused-over cell, which we only need to set where the mouse is being handled.
                        playerToCursor.setGoal(m);
                        playerToCursor.scan(null);
                    }
                }
            }
        } // if we are waiting for the player's input and get input, process it.
        else if (input.hasNext()) {
            input.next();
        }
        stage.act();
        viewport.apply(false);
        stage.draw();
        batch.begin();
        display.drawActor(batch, 1.0f, playerEntity);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        //very important to have the mouse behave correctly if the user fullscreens or resizes the game!
        input.getMouse().reinitialize((float) width / TOTAL_WIDTH, (float) height / TOTAL_HEIGHT, TOTAL_WIDTH, TOTAL_HEIGHT, 0, 0);
        viewport.update(width, height, false);
        viewport.setScreenBounds(0, 0, width, height);

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
                case SquidInput.ESCAPE: {
                    Gdx.app.exit();
                    break;
                }
            }
            move(dir);
        }
    };

    private final SquidMouse mapMouse = new SquidMouse(CELL_WIDTH, CELL_HEIGHT, MAP_WIDTH, MAP_HEIGHT, 0, 0, new InputAdapter() {
        // if the user clicks within FOV range and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            int sx = screenX + display.getGridOffsetX(), sy = screenY + display.getGridOffsetY();
            if (awaitedMoves.isEmpty()) {
                if (toCursor.isEmpty()) {
                    cursor = Coord.get(sx, sy);
                    //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                    // player position to the position the user clicked on. The "PreScanned" part is an optimization
                    // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                    // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                    // moves, we only need to do a fraction of the work to find the best path with that info.
                    toCursor = playerToCursor.findPathPreScanned(cursor);
                    //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                    // you're finding a path to a monster or loot, and want to bump into it, but here can be
                    // confusing because you would "move into yourself" as your first move without this.
                    // Getting a sublist avoids potential performance issues with removing from the start of an
                    // ArrayList, since it keeps the original list around and only gets a "view" of it.
                    if(!toCursor.isEmpty())
                        toCursor = toCursor.subList(1, toCursor.size());

                }
                awaitedMoves.addAll(toCursor);
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
            if(!awaitedMoves.isEmpty())
                return false;
            int sx = screenX + display.getGridOffsetX(), sy = screenY + display.getGridOffsetY();
            if((sx < 0 || sx >= BIG_MAP_WIDTH || sy < 0 || sy >= BIG_MAP_HEIGHT)
                    || (cursor.x == sx && cursor.y == sy))
            {
                return false;
            }
            cursor = Coord.get(sx, sy);
                //This uses DijkstraMap.findPathPreScannned() to get a path as a List of Coord from the current
                // player position to the position the user clicked on. The "PreScanned" part is an optimization
                // that's special to DijkstraMap; because the whole map has already been fully analyzed by the
                // DijkstraMap.scan() method at the start of the program, and re-calculated whenever the player
                // moves, we only need to do a fraction of the work to find the best path with that info.
                toCursor = playerToCursor.findPathPreScanned(cursor);
                //findPathPreScanned includes the current cell (goal) by default, which is helpful when
                // you're finding a path to a monster or loot, and want to bump into it, but here can be
                // confusing because you would "move into yourself" as your first move without this.
                // Getting a sublist avoids potential performance issues with removing from the start of an
                // ArrayList, since it keeps the original list around and only gets a "view" of it.
                if(!toCursor.isEmpty())
                    toCursor = toCursor.subList(1, toCursor.size());

            return false;
        }

        /*
        // if the user clicks and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            screenX += display.getGridOffsetX();
            screenY += display.getGridOffsetY();

            if (screenX >= 0 && screenX < BIG_MAP_WIDTH && screenY >= 0 && screenY < BIG_MAP_HEIGHT) {
                if (awaitedMoves.isEmpty()) {
                    if (toCursor.isEmpty()) {
                        cursor = Coord.get(screenX, screenY);
                        toCursor = playerToCursor.findPath(100, null, null, player.location, cursor);
                    }
                }
                awaitedMoves = new ArrayList<>(toCursor);
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
            screenX += display.getGridOffsetX();
            screenY += display.getGridOffsetY();
            if (cursor.x == screenX && cursor.y == screenY) {
                return false;
            }
            if (screenX >= 0 && screenX < BIG_MAP_WIDTH && screenY >= 0 && screenY < BIG_MAP_HEIGHT) {
                cursor = Coord.get(screenX, screenY);
                toCursor = playerToCursor.findPath(100, null, null, player.location, cursor);
            }
            return false;
        }*/
    });
}
