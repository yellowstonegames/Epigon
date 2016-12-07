package squidpony.epigon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.World;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

import java.util.ArrayList;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes. Doesn't use any
 * platform-specific code.
 */
public class Epigon extends Game {

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final int MAP_WIDTH = 80;
    public static final int MAP_HEIGHT = 60;

    public static final int INFO_WIDTH = 20;
    public static final int INFO_HEIGHT = MAP_HEIGHT;

    public static final int MESSAGE_HEIGHT = 7;

    public static final int TOTAL_WIDTH = MAP_WIDTH + INFO_WIDTH;
    public static final int TOTAL_HEIGHT = MAP_HEIGHT + MESSAGE_HEIGHT;

    public static final int MESSAGE_WIDTH = TOTAL_WIDTH;

    // Cell sizing
    public static final int CELL_WIDTH = 12;
    public static final int CELL_HEIGHT = 18;

    // Pixels
    public static final int TOTAL_PIXEL_WIDTH = TOTAL_WIDTH * CELL_WIDTH;
    public static final int TOTAL_PIXEL_HEIGHT = TOTAL_HEIGHT * CELL_HEIGHT;

    // 
    SpriteBatch batch;

    private RNG rng;
    private SquidLayers display;
//    private DungeonGenerator dungeonGen;
//    private char[][] decoDungeon, bareDungeon, lineDungeon;
//    private int[][] colorIndices, bgColorIndices;
    private char[][] simpleChars;
    private EpiMap map;
    private SquidInput input;
    private Color bgColor;
    private Stage stage;
    private DijkstraMap playerToCursor;
    private Coord cursor, player;
    private ArrayList<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;
    private float secondsWithoutMoves;

    @Override
    public void create() {
        System.out.println("Creating new game.");
        System.out.println("Working in folder: " + System.getProperty("user.dir"));

        //super.setScreen(new DisplayMaster());
        //getScreen().show();

        // gotta have a random number generator. We can seed an RNG with any long we want, or even a String.
        rng = new RNG("SquidLib!");

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();
        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(new StretchViewport(TOTAL_PIXEL_WIDTH, TOTAL_PIXEL_HEIGHT), batch);

        // display is a SquidLayers object, and that class has a very large number of similar methods for placing text
        // on a grid, with an optional background color and lightness modifier per cell. It also handles animations and
        // other effects, but you don't need to use them at all. SquidLayers also automatically handles the stretchable
        // distance field fonts, which are a big improvement over fixed-size bitmap fonts and should probably be
        // preferred for new games. SquidLayers needs to know what the size of the grid is in columns and rows, how big
        // an individual cell is in pixel width and height, and lastly how to handle text, which can be a BitmapFont or
        // a TextCellFactory. Either way, it will use what is given to make its TextCellFactory, and that handles the
        // layout of text in a cell, among other things. DefaultResources stores pre-configured BitmapFont objects but
        // also some TextCellFactory objects for distance field fonts; either one can be passed to this constructor.
        // the font will try to load Inconsolata-LGC-Custom as a bitmap font with a distance field effect.
        display = new SquidLayers(TOTAL_WIDTH, TOTAL_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getStretchableSquareFont());

        // a bit of a hack to increase the text height slightly without changing the size of the cells they're in.
        // this causes a tiny bit of overlap between cells, which gets rid of an annoying gap between vertical lines.
        // if you use '#' for walls instead of box drawing chars, you don't need this.
        display.setTextSize(CELL_WIDTH, CELL_HEIGHT);

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        display.setAnimationDuration(0.03f);

        //These need to have their positions set before adding any entities if there is an offset involved.
        //There is no offset used here, but it's still a good practice here to set positions early on.
        display.setPosition(0, 0);

        //This uses the seeded RNG we made earlier to build a procedural dungeon using a method that takes rectangular
        //sections of pre-drawn dungeon and drops them into place in a tiling pattern. It makes good "ruined" dungeons.
//        dungeonGen = new DungeonGenerator(MAP_WIDTH, MAP_HEIGHT, rng);

        //uncomment this next line to randomly add water to the dungeon in pools.
        //dungeonGen.addWater(15);
        //decoDungeon is given the dungeon with any decorations we specified. (Here, we didn't, unless you chose to add
        //water to the dungeon. In that case, decoDungeon will have different contents than bareDungeon, next.)
//        decoDungeon = dungeonGen.generate();

        //There are lots of options for dungeon generation in SquidLib; you can pass a TilesetType enum to generate()
        //as shown on the following lines to change the style of dungeon generated from ruined areas, which are made
        //when no argument is passed to generate or when TilesetType.DEFAULT_DUNGEON is, to caves or other styles.
        //decoDungeon = dungeonGen.generate(TilesetType.REFERENCE_CAVES); // generate caves
        //decoDungeon = dungeonGen.generate(TilesetType.ROUND_ROOMS_DIAGONAL_CORRIDORS); // generate large round rooms
        //getBareDungeon provides the simplest representation of the generated dungeon -- '#' for walls, '.' for floors.
//        bareDungeon = dungeonGen.getBareDungeon();

        //When we draw, we may want to use a nicer representation of walls. DungeonUtility has lots of useful methods
        //for modifying char[][] dungeon grids, and this one takes each '#' and replaces it with a box-drawing character.
//        lineDungeon = DungeonUtility.hashesToLines(decoDungeon);

        //Coord is the type we use as a general 2D point, usually in a dungeon.
        //Because we know dungeons won't be huge, Coord is optimized for x and y values between -3 and 255, inclusive.
        cursor = Coord.get(-1, -1);

        map = World.getDefaultMap();
        
        //player is, here, just a Coord that stores his position. In a real game, you would probably have a class for
        //creatures, and possibly a subclass for the player.
//        player = dungeonGen.utility.randomCell(CoordPacker.pack(bareDungeon, '.'));
        player = Coord.get(20, 20);

        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        //DijkstraMap is the pathfinding swiss-army knife we use here to find a path to the latest cursor position.
        simpleChars = map.simpleChars();
        playerToCursor = new DijkstraMap(simpleChars, DijkstraMap.Measurement.MANHATTAN);

        bgColor = SColor.DARK_SLATE_GRAY;

        // DungeonUtility provides various ways to get default colors or other information from a dungeon char 2D array.
//        colorIndices = DungeonUtility.generatePaletteIndices(decoDungeon);
//        bgColorIndices = DungeonUtility.generateBGPaletteIndices(decoDungeon);

        input = new SquidInput(keys, mapMouse);

        //Setting the InputProcessor is ABSOLUTELY NEEDED TO HANDLE INPUT
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));

        // and then add display, our one visual component, to the list of things that act in Stage.
        stage.addActor(display);
    }

    /**
     * Move the player if he isn't bumping into a wall or trying to go off the map somehow.
     * In a fully-fledged game, this would not be organized like this, but this is a one-file demo.
     * @param dir
     */
    private void move(Direction dir) {
        int newX = player.x + dir.deltaX;
        int newY = player.y + dir.deltaY;
        if (newX >= 0 && newY >= 0 && newX < MAP_WIDTH && newY < MAP_HEIGHT && map.contents[newX][newY].getSymbol() != '#') {
            player = player.translate(dir.deltaX, dir.deltaY);
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap() {
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (map.inBounds(Coord.get(x, y))) {
                EpiTile tile = map.contents[x][y];
                //display.put(x, y, lineDungeon[x][y], colorIndices[x][y], bgColorIndices[x][y], 40);
                display.put(x, y, tile.getSymbol(), tile.getForegroundColor(), SColor.BLACK);
                } else {
                    display.put(x, y, '`', SColor.SLATE, SColor.BLACK);
                }
            }
        }

        for (Coord pt : toCursor) {
            // use a brighter light to trace the path to the cursor, from 170 max lightness to 0 min.
            display.highlight(pt.x, pt.y, 100);
        }

        //places the player as an '@' at his position in orange (6 is an index into SColor.LIMITED_PALETTE).
        display.put(player.x, player.y, '@', 6);

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
            secondsWithoutMoves += Gdx.graphics.getDeltaTime();
            if (secondsWithoutMoves >= 0.1) {
                secondsWithoutMoves = 0;
                Coord m = awaitedMoves.remove(0);
                toCursor.remove(0);
                move(Direction.toGoTo(player, m));
            }
        } // if we are waiting for the player's input and get input, process it.
        else if (input.hasNext()) {
            input.next();
        }

        //stage has its own batch and must be explicitly told to draw(). this also causes it to act().
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        //very important to have the mouse behave correctly if the user fullscreens or resizes the game!
        input.getMouse().reinitialize((float) width / TOTAL_WIDTH, (float) height / TOTAL_HEIGHT, TOTAL_WIDTH, TOTAL_HEIGHT, 0, 0);
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

        // if the user clicks and there are no awaitedMoves queued up, generate toCursor if it
        // hasn't been generated already by mouseMoved, then copy it over to awaitedMoves.
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (awaitedMoves.isEmpty()) {
                if (toCursor.isEmpty()) {
                    cursor = Coord.get(screenX, screenY);
                    toCursor = playerToCursor.findPath(100, null, null, player, cursor);
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
            if (cursor.x == screenX && cursor.y == screenY) {
                return false;
            }
            cursor = Coord.get(screenX, screenY);
            if (cursor.x >= 0 && cursor.x < MAP_WIDTH && cursor.y >= 0 && cursor.y < MAP_HEIGHT) {
                toCursor = playerToCursor.findPath(100, null, null, player, cursor);
            }
            return false;
        }
    });
}
