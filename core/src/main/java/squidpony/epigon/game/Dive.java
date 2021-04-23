package squidpony.epigon.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import squidpony.StringKit;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.Stat;
import squidpony.epigon.input.key.FallingGameOverKeyHandler;
import squidpony.epigon.input.key.FallingKeyHandler;
import squidpony.epigon.input.mouse.FallingMouseHandler;
import squidpony.epigon.mapping.MapConstants;
import squidpony.epigon.mapping.WobblyCanyonGenerator;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidMouse;
import squidpony.squidgrid.gui.gdx.SubcellLayers;

import squidpony.epigon.display.FallingHandler;
import squidpony.epigon.files.Config;
import squidpony.squidmath.Coord;

import static squidpony.squidgrid.gui.gdx.SColor.DB_INK;

public class Dive extends Epigon {

    private SubcellLayers fallingSLayers;
    private Stage fallingStage;
    private Viewport fallingViewport;

    public SquidInput fallingInput;
    public FallingHandler fallingHandler;

    // Timing
    private long fallDelay = 300;
    public Instant nextFall = Instant.now();
    public boolean paused = true;
    public Instant pausedAt = Instant.now();
    private Instant unpausedAt = Instant.now();
    public long inputDelay = 100;
    public Instant nextInput = Instant.now();
    private long fallDuration = 0L, currentFallDuration = 0L;

    public float startingY, finishY, timeToFall;

    public SquidInput.KeyHandler fallingKeys;
    public SquidInput.KeyHandler fallingGameOverKeys;
    public SquidMouse fallingMouse;

    public Dive(Config config) {
        super(config);
    }

    @Override
    public void buildDisplay(Batch batch) {
        super.buildDisplay(batch);

        fallingViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());
        fallingStage = new Stage(fallingViewport, batch);


        fallingSLayers = new SubcellLayers(
            100, // weird because falling uses a different view
            settings.diveWorldDepth,
            mapSize.cellWidth * 0.5f,
            mapSize.cellHeight,
            defaultFont.copy());
        fallingSLayers.setDefaultBackground(colorCenter.desaturate(DB_INK, 0.8));
        fallingSLayers.setDefaultForeground(SColor.LIME);
        fallingHandler = new FallingHandler(fallingSLayers);

        fallingSLayers.setPosition(0, 0);
        fallingViewport.setScreenBounds(0, messageSize.pixelHeight(), mapSize.pixelWidth(), mapSize.pixelHeight());

        fallingStage.addActor(fallingSLayers);

        fallingStage.getCamera().position.y = startingY = fallingSLayers.worldY(mapSize.gridHeight / 2);
        finishY = fallingSLayers.worldY(settings.diveWorldDepth);
        timeToFall = Math.abs(finishY - startingY) * fallDelay / mapSize.cellHeight;
    }

    @Override
    public void buildInputProcessors() {
        super.buildInputProcessors();

        fallingKeys = new FallingKeyHandler(this);
        fallingGameOverKeys = new FallingGameOverKeyHandler(this);
        fallingMouse = new SquidMouse(1, 1, new FallingMouseHandler());
        fallingInput = new SquidInput(fallingKeys, fallingMouse);

        multiplexer.prependProcessor(fallingInput);
    }

    @Override
    public void startGame() {
        super.startGame();

        prepFall();
    }

    @Override
    public void initPlayer() {
        super.initPlayer();

        fallingHandler.setPlayer(player);
    }

    @Override
    public void internalResize(int width, int height) {
        float currentZoomX = (float) width / config.displayConfig.defaultPixelWidth();
        float currentZoomY = (float) height / config.displayConfig.defaultPixelHeight();

        int x = 0;
        int y = (int) (height - mapSize.pixelHeight() * currentZoomY);
        int pixelWidth = (int) (currentZoomX * mapSize.pixelWidth());
        int pixelHeight = (int) (currentZoomY * mapSize.pixelHeight());

        fallingViewport.update(width, height, false);
        fallingViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);
    }

    @Override
    public void renderStart() {
        if (fallingHandler.reachedGoal) {
            paused = true;
            pausedAt = Instant.now();
            showFallingWin();
            fallingHandler.reachedGoal = false;
            fallingHandler.update();
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
    }

    @Override
    public void renderEnd() {
        fallingViewport.apply(false);
        fallingStage.act();
        batch.setProjectionMatrix(fallingStage.getCamera().combined);
        fallingSLayers.font.configureShader(batch);
        fallingStage.getRoot().draw(batch, 1f);
    }

    public void prepFall() {
        message("Falling..... Press SPACE to continue");
        int w = MapConstants.DIVE_HEADER[0].length();
        WobblyCanyonGenerator wcg = new WobblyCanyonGenerator(mapDecorator);
        map = wcg.buildDive(world = worldGenerator.buildWorld(w, 23, config.settings.diveWorldDepth), w, config.settings.diveWorldDepth);
        contextHandler.setMap(map, world);

        // Start out in the horizontal middle and visual a bit down
        player.location = Coord.get(w / 2, 0);
        fallDuration = 0;

        fallingInput.flush();
        fallingInput.setRepeatGap(Long.MAX_VALUE);
        fallingInput.setKeyHandler(fallingKeys);
        fallingInput.setMouse(fallingMouse);
        fallingHandler.show(map);

        paused = true;
        nextFall = Instant.now().plusMillis(fallDelay);
        pausedAt = Instant.now();
    }

    public void showFallingGameOver() {
        message("");
        message("");
        message("");
        message("");
        message("You have died.");
        message("");
        message("Try Again (t) or Quit (Shift-Q)?");

        fallingInput.flush();
        fallingInput.setKeyHandler(fallingGameOverKeys);
    }

    public void showFallingWin() {
        message("You have reached the Dragon's Hoard!");
        message("On the way, you gathered:");

        StringBuilder sb = new StringBuilder(100);
        for (Physical item : player.inventory) {
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

        fallingInput.flush();
        fallingInput.setKeyHandler(fallingGameOverKeys);
    }
}
