package squidpony.epigon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import squidpony.DataConverter;

import squidpony.epigon.universe.Stat;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Humanoid;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.World;
import squidpony.epigon.universe.Rating;

import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.RNG;
import squidpony.squidmath.StatefulRNG;

/**
 * The main class of the game, constructed once in each of the platform-specific Launcher classes.
 * Doesn't use any platform-specific code.
 */
public class Epigon extends Game {

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public static final int MAP_WIDTH = 80;
    public static final int MAP_HEIGHT = 60;

    public static final int INFO_WIDTH = 30;
    public static final int INFO_HEIGHT = MAP_HEIGHT;

    public static final int MESSAGE_HEIGHT = 7;

    public static final int TOTAL_WIDTH = MAP_WIDTH + INFO_WIDTH;
    public static final int TOTAL_HEIGHT = MAP_HEIGHT + MESSAGE_HEIGHT;

    public static final int MESSAGE_WIDTH = TOTAL_WIDTH;

    // Cell sizing
    public static final int CELL_WIDTH = 12;
    public static final int CELL_HEIGHT = 12;

    // Pixels
    public static final int TOTAL_PIXEL_WIDTH = TOTAL_WIDTH * CELL_WIDTH;
    public static final int TOTAL_PIXEL_HEIGHT = TOTAL_HEIGHT * CELL_HEIGHT;

    public static final RNG rng = new StatefulRNG();

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
    private ArrayList<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;
    private float secondsWithoutMoves;

    @Override
    public void create() {
        System.out.println("Creating new game.");
        System.out.println("Working in folder: " + System.getProperty("user.dir"));
        System.out.println("Loading sound manager...");
        SoundManager sound = new SoundManager();
        //sound.playSoundFX("footsteps-1");

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();

        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(new StretchViewport(TOTAL_PIXEL_WIDTH, TOTAL_PIXEL_HEIGHT), batch);

        display = new SquidLayers(TOTAL_WIDTH, TOTAL_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getStretchableSquareFont());

        map = World.getDefaultMap();

        display.getTextFactory().fit(Arrays.deepToString(map.simpleChars()));
        display.setTextSize(CELL_WIDTH, CELL_HEIGHT);

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        display.setAnimationDuration(0.03f);

        display.setPosition(0, 0);

        cursor = Coord.get(-1, -1);

        // Create an actual player
        player = new Physical();
        player.creatureData = new Creature();
        player.creatureData.abilities = new HashSet<>();
        player.name = "Great Hero";
        Arrays.stream(Stat.values()).forEach(s -> player.baseStats.put(s, rng.between(20, 100)));
        Arrays.stream(Stat.values()).forEach(s -> player.currentStats.put(s, player.baseStats.get(s) + rng.between(-10, 30)));

//        Json json = new Json();
//        json.setIgnoreUnknownFields(true);
//        json.setUsePrototypes(false);
//        System.out.println(json.prettyPrint(player));
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        convert.setIgnoreUnknownFields(true);
        convert.setUsePrototypes(false);
//        System.out.println(convert.toJson(player));

        Physical sword = new Physical();
        sword.color = SColor.SILVER;
        sword.symbol = '/';
        sword.name = "Sword";
//        System.out.println(json.prettyPrint(sword));

        PhysicalBlueprint pj = new PhysicalBlueprint();
        pj.name = "Player";
        pj.description = "The main player's character.";
        pj.notes = "Voted most likely to die.";
        pj.symbol = '@';
        pj.color = SColor.FOX;
        pj.possibleAliases = Stream.of("Mario", "Link", "Sam").collect(Collectors.toList());
        pj.baseStats.put(Stat.OPACITY, 100);
        Humanoid cb = new Humanoid();
        pj.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);
//        System.out.println(convert.toJson(pj));

//        String playerFile = Gdx.files.internal("config/player.json").readString();
//        pj = convert.fromJson(PhysicalBlueprint.class, playerFile);
//        pj = convert.fromJson(PhysicalBlueprint.class, convert.toJson(pj));
//        System.out.println(convert.toJson(pj));
        //This is used to allow clicks or taps to take the player to the desired area.
        toCursor = new ArrayList<>(100);
        awaitedMoves = new ArrayList<>(100);

        //DijkstraMap is the pathfinding swiss-army knife we use here to find a path to the latest cursor position.
        simpleChars = map.simpleChars();
        player.location = Coord.get(20, 20);
        playerToCursor = new DijkstraMap(simpleChars, DijkstraMap.Measurement.MANHATTAN);

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
        if (newX >= 0 && newY >= 0 && newX < MAP_WIDTH && newY < MAP_HEIGHT && map.contents[newX][newY].getSymbol() != '#') {
            player.location = player.location.translate(dir.deltaX, dir.deltaY);
        }
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the
     * player.
     */
    public void putMap() {
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (map.inBounds(Coord.get(x, y))) {
                    EpiTile tile = map.contents[x][y];
                    display.put(x, y, tile.getSymbol(), tile.getForegroundColor(), SColor.BLACK);
                } else {
                    display.put(x, y, '`', SColor.SLATE, SColor.BLACK);
                }
            }
        }

        SColor front = SColor.TRANSPARENT;
        SColor back = SColor.OLD_LACE;
        for (int x = MAP_WIDTH; x < TOTAL_WIDTH; x++) {
            for (int y = 0; y < TOTAL_HEIGHT; y++) {
                display.put(x, y, ' ', front, back);
            }
        }

        front = SColor.JAPANESE_IRIS;
        display.putString(MAP_WIDTH + 4, 1, "STATS", front, back);
        int y = 3;
        int x = MAP_WIDTH + 1;
        int spacing = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().orElse(0) + 2;
        for (Entry<Stat, Integer> e : player.baseStats.entrySet()) {
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

        for (Coord pt : toCursor) {
            // use a brighter light to trace the path to the cursor, from 170 max lightness to 0 min.
            display.highlight(pt.x, pt.y, 100);
        }

        //places the player as an '@' at his position in orange (6 is an index into SColor.LIMITED_PALETTE).
        display.put(player.location.x, player.location.y, '@', 6);

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
                move(Direction.toGoTo(player.location, m));
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
                    toCursor = playerToCursor.findPath(100, null, null, player.location, cursor);
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
                toCursor = playerToCursor.findPath(100, null, null, player.location, cursor);
            }
            return false;
        }
    });
}
