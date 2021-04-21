package squidpony.epigon.game;

import java.util.Arrays;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import squidpony.ArrayTools;
import squidpony.Messaging;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.LOS;
import squidpony.squidgrid.Measurement;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.VisualCondition;
import squidpony.epigon.data.Weapon;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.raw.RawCreature;
import squidpony.epigon.display.FxHandler;
import squidpony.epigon.files.Config;
import squidpony.epigon.input.key.*;
import squidpony.epigon.input.mouse.EquipmentMouseHandler;
import squidpony.epigon.input.mouse.HelpMouseHandler;
import squidpony.epigon.input.mouse.MapMouseHandler;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.RememberedTile;
import squidpony.epigon.util.Utilities;

import static squidpony.squidgrid.gui.gdx.SColor.*;

public class Crawl extends Epigon {

    public SquidInput crawlInput; // TODO - create method to switch input without direct access
    private SquidInput debugInput;
    public KeyHandler mapKeys;
    public KeyHandler equipmentKeys;
    public KeyHandler helpKeys;
    public KeyHandler debugKeys;
    public SquidMouse mapMouse;
    public SquidMouse equipmentMouse;
    public SquidMouse helpMouse;
    private Stage mapStage, mapOverlayStage;
    private Viewport mapViewport, mapOverlayViewport;

    private static final Radiance[] softWhiteChain = Radiance.makeChain(8, 1.2f, SColor.FLOAT_WHITE, 0.4f);

    public Crawl(Config config) {
        super(config);
    }

    @Override
    public void buildDisplay(Batch batch) {
        super.buildDisplay(batch);

        mapViewport = new StretchViewport(mapSize.pixelWidth(), mapSize.pixelHeight());
        mapOverlayViewport = new StretchViewport(messageSize.pixelWidth(), mapSize.pixelHeight());
        mapStage = new Stage(mapViewport, batch);
        mapOverlayStage = new Stage(mapOverlayViewport, batch);

        int top = Gdx.graphics.getHeight() - mapSize.pixelHeight();
        mapViewport.setScreenBounds(0, top, mapSize.pixelWidth(), mapSize.pixelHeight());
        mapOverlayViewport.setScreenBounds(0, top, mapSize.pixelWidth(), mapSize.pixelHeight());

        mapStage.addActor(mapSLayers);
        mapStage.addActor(passiveSLayers);
        mapStage.addActor(mapHoverSLayers);
        mapOverlayStage.addActor(mapOverlaySLayers);
    }


    @Override
    public void buildInputProcessors() {
        super.buildInputProcessors();
        mapKeys = new MapKeyHandler(this);
        equipmentKeys = new EquipmentKeyHandler(this);
        helpKeys = new HelpKeyHandler(this);
        debugKeys = new DebugKeyHandler(this, config);

        equipmentMouse = new SquidMouse(1, 1, new EquipmentMouseHandler().setEpigon(this));
        helpMouse = new SquidMouse(1, 1, new HelpMouseHandler());
        mapMouse = new SquidMouse(1, 1, new MapMouseHandler().setEpigon(this));


        crawlInput = new SquidInput(mapKeys, mapMouse);
        debugInput = new SquidInput(debugKeys);

        multiplexer.prependProcessor(crawlInput);
        multiplexer.prependProcessor(debugInput);
    }

    @Override
    public void startGame() {
        super.startGame();

        prepCrawl();
        putCrawlMap();
    }

    @Override
    public void initPlayer() {
        super.initPlayer();
    }


    @Override
    public void internalResize(int width, int height) {
        float currentZoomX = (float) width / config.displayConfig.defaultPixelWidth();
        float currentZoomY = (float) height / config.displayConfig.defaultPixelHeight();

        float cellWidth = currentZoomX * mapSize.cellWidth;
        float cellHeight = currentZoomY * mapSize.cellHeight;
        int offsetX = 0;
        int offsetY = 0;
        mapMouse.reinitialize(cellWidth, cellHeight, mapSize.gridWidth, mapSize.gridHeight, -offsetX, -offsetY);
        equipmentMouse.reinitialize(cellWidth, cellHeight, mapSize.gridWidth, mapSize.gridHeight, -offsetX, -offsetY);

        int x = 0;
        int y = (int) (height - mapSize.pixelHeight() * currentZoomY);
        int pixelWidth = (int) (currentZoomX * mapSize.pixelWidth());
        int pixelHeight = (int) (currentZoomY * mapSize.pixelHeight());

        mapViewport.update(width, height, false);
        mapViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);
        mapOverlayViewport.update(width, height, false);
        mapOverlayViewport.setScreenBounds(x, y, pixelWidth, pixelHeight);
    }

    @Override
    public void renderStart() {
        mapStage.getCamera().position.x = player.appearance.getX();
        mapStage.getCamera().position.y = player.appearance.getY();
        putCrawlMap();
    }

    @Override
    public void renderEnd() {
        mapViewport.apply(false);
        mapStage.act();
        batch.setProjectionMatrix(mapStage.getCamera().combined);

        mapSLayers.font.configureShader(batch);

        // Update player vision modifications
        final int clen = player.conditions.size();
        player.visualCondition = new VisualCondition(); // TODO - marking conditions as changed can help prevent having to re-calculate every frame
        for (int i = clen - 1; i >= 0; i--) {
            VisualCondition vis = player.conditions.getAt(i).parent.visual;
            if (vis != null) {
                vis.update();
                player.visualCondition.lumaMul *= vis.lumaMul;
                player.visualCondition.warmMul *= vis.warmMul;
                player.visualCondition.mildMul *= vis.mildMul;
                player.visualCondition.lumaAdd += vis.lumaAdd;
                player.visualCondition.warmAdd += vis.warmAdd;
                player.visualCondition.mildAdd += vis.mildAdd;
                break;
            }
        }

        if (mapOverlaySLayers.isVisible()) {
            mapOverlayStage.act();
            mapOverlayHandler.updateDisplay();
            if (mapOverlayHandler.getSubselection() != null) {
                showInteractOptions(mapOverlayHandler.getSelected(), player, mapOverlayHandler.getSelection(), map);
            }
            batch.setProjectionMatrix(mapOverlayStage.getCamera().combined);
            mapOverlayStage.getRoot().draw(batch, 1f);
        } else {
            renderMapLayer(mapSLayers, batch, true);
            renderMapLayer(passiveSLayers, batch, false);
        }
        renderMapLayer(mapHoverSLayers, batch, false);
    }

    private void renderMapLayer(SparseLayers sLayers, Batch batch, boolean drawWalls) {
        float layerX = sLayers.getX();
        float layerY = sLayers.getY();

        TextCellFactory textCellFactory = sLayers.getFont();

        // Draw backgrounds
        updateVisionFilter(0.7f, 0.65f, 0.65f, 0.7f, 0.65f, 0.65f);
        textCellFactory.draw(batch, sLayers.backgrounds, layerX, layerY);

        // Draw walls
        if (drawWalls) {
            updateVisionFilter(0.9f, 0.95f, 0.95f, 0.9f, 0.95f, 0.95f);
            textCellFactory.draw(batch, walls, layerX, layerY, 3, 3);
        }

        Frustum frustum = null;
        Stage stage = sLayers.getStage();
        float yOff = layerY + sLayers.gridHeight * textCellFactory.actualCellHeight;
        if (stage != null) {
            Viewport viewport = stage.getViewport();
            if (viewport != null) {
                Camera camera = viewport.getCamera();
                if (camera != null && camera.frustum != null) {
                    if (!camera.frustum.boundsInFrustum(layerX, yOff, 0f, textCellFactory.actualCellWidth, textCellFactory.actualCellHeight, 0f)
                        || !camera.frustum.boundsInFrustum(layerX + textCellFactory.actualCellWidth * (sLayers.gridWidth - 1), layerY, 0f, textCellFactory.actualCellWidth, textCellFactory.actualCellHeight, 0f)) {
                        frustum = camera.frustum;
                    }
                }
            }
        }

        textCellFactory.configureShader(batch);
        int len = sLayers.layers.size();
        if (frustum == null) {
            for (int i = 0; i < len; i++) {
                sLayers.layers.get(i).draw(batch, textCellFactory, layerX, yOff);
            }
        } else {
            for (int i = 0; i < len; i++) {
                sLayers.layers.get(i).draw(batch, textCellFactory, frustum, layerX, yOff);
            }
        }

        // Draw foreground
        int x, y;
        float glyphX;
        float glyphY;
        updateVisionFilter(1.05f, 1.4f, 1.4f, 1.05f, 1.4f, 1.4f);
        for (int i = 0; i < sLayers.glyphs.size(); i++) {
            TextCellFactory.Glyph glyph = sLayers.glyphs.get(i);
            if (glyph == null) { // no glyph to draw
                continue;
            }
            glyph.act(Gdx.graphics.getDeltaTime());
            if (!glyph.isVisible()) { // can't see the glyph
                continue;
            }
            glyphX = glyph.getX() - layerX;
            x = Math.round(glyphX / textCellFactory.actualCellWidth);
            if (x < 0 || x >= sLayers.gridWidth) { // glyph off the view horizontally
                continue;
            }
            glyphY = glyph.getY() - layerY;
            y = Math.round(glyphY / -textCellFactory.actualCellHeight + sLayers.gridHeight);
            if (y < 0 || y >= sLayers.gridHeight) { // glyph off the view vertically
                continue;
            }
            if (sLayers.backgrounds[x][y] == 0f) { // marked to not be drawn
                continue;
            }
            if (frustum != null && !frustum.boundsInFrustum(glyphX, glyphY, 0f, textCellFactory.actualCellWidth, textCellFactory.actualCellHeight, 0f)) { // outside camera view
                continue;
            }
            glyph.draw(batch, 1f);
        }
    }

    private void updateVisionFilter(float yMultiplier, float cwMultiplier, float cmMultiplier, float yAdditive, float cwAdditive, float cmAdditive) {
        filter.yMul = player.visualCondition.lumaMul * yMultiplier;
        filter.cwMul = player.visualCondition.warmMul * cwMultiplier;
        filter.cmMul = player.visualCondition.mildMul * cmMultiplier;
        filter.yAdd = player.visualCondition.lumaAdd * yAdditive;
        filter.cwAdd = player.visualCondition.warmAdd * cwAdditive;
        filter.cmAdd = player.visualCondition.mildAdd * cmAdditive;
    }

    private void prepCrawl() {
        message("Generating crawl.");
        //world = worldGenerator.buildWorld(worldWidth, worldHeight, 8, handBuilt);
        int aboveGround = 7;
        EpiMap[] underground = worldGenerator.buildWorld(config.settings.worldWidth, config.settings.worldHeight, config.settings.worldDepth);
        EpiMap[] castle = castleGenerator.buildCastle(config.settings.worldWidth, config.settings.worldHeight, aboveGround);
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

        crawlInput.flush();
        crawlInput.setRepeatGap(220);
        crawlInput.setKeyHandler(mapKeys);
        crawlInput.setMouse(mapMouse);
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putCrawlMap() {
        ArrayTools.fill(mapSLayers.backgrounds, map.lighting.backgroundColor);
        map.lighting.update();
        if (!showingMenu) {
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
                        if (creature.appearance == null) {
                            creature.appearance = mapSLayers.glyph(creature.symbol, lerpFloatColorsBlended(unseenCreatureColorFloat, creature.color, 0.5f + 0.35f * (float) sight), x, y);
                        } else {
                            creature.appearance.setVisible(true);
                            creature.appearance.setPackedColor(lerpFloatColorsBlended(unseenCreatureColorFloat, creature.color, 0.5f + 0.35f * (float) sight));
                        }
                        if (creature.overlayAppearance != null) {
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
                        if (rt.seenInDebug && (!config.debugConfig.debugActive || !config.debugConfig.odinView)) {
                            map.remembered[x][y] = null;
                        } else {
                            if (rt.symbol == '#') {
                                wallColors[x][y] = rt.front;
                            } else {
                                mapSLayers.put(x, y, rt.symbol, rt.front, rt.back, 0);
                            }
                        }
                    }
                }
            }
        }
        MapUtility.fillLinesToBoxes(walls, prunedDungeon, wallColors);
        mapSLayers.clear(player.location.x, player.location.y, 0);

        mapSLayers.clear(2);
    }


    public void changeLevel(int level) {
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
            }
            while (map.contents[location.x][location.y].blockage != null);
        }

        player.location = location;
        map.contents[player.location.x][player.location.y].add(player);
        player.appearance = mapSLayers.glyph(player.symbol, player.color, player.location.x, player.location.y);

        fxHandlerPassive.seen = fxHandler.seen = map.lighting.fovResult;
        creatures = map.creatures;
        simple = map.simpleChars();
        lineDungeon = map.line;
        calcFOV(player.location.x, player.location.y);
        toPlayerDijkstra.initialize(simple);
        monsterDijkstra.initialize(simple);
        calcDijkstra();
        contextHandler.setMap(map, world);
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
                Physical p = RecipeMixer.buildCreature(RawCreature.ENTRIES[rootChaos.nextInt(RawCreature.ENTRIES.length)]);
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

}
