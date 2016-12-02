package squidpony.epigon.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import javax.swing.GroupLayout;
import squidpony.epigon.AOE;
import squidpony.epigon.Prefs;
import squidpony.epigon.data.ConditionChangeType;
import squidpony.epigon.data.InventoryChangeType;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.generic.Energy;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Item;
import squidpony.epigon.data.specific.Terrain;
import squidpony.epigon.dm.Planner;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.universe.Achievement;
import squidpony.epigon.universe.Rating;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

/**
 * Controls what is on screen at any given time.
 *
 * @author Eben Howard - http://squidpony.com - eben@squidpony.com
 */
public class DisplayMaster implements Screen {

    //game world fields
    private boolean runningTurn = false;//indicates that a turn is being calculated and animated
    private EpiMap map;//the currently displayed map
    //
    //UI output fields
    private Table primaryTable,
            defaultActionTable,
            actionBarTable,
            healthBarTable,
            equipmentDummyTable,
            multiAreaTable,
            multiViewSelectionTable,
            leftTable,
            rightTable;
    private Stack mainView;
    //private ScrollPane viewScrollPane;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private SoundManager sound = SoundManager.getInstance();
    private SquidPanel viewPanel;
    private TextCellFactory tcf;

    /**
     * Sets the view to the map provided and initializes display of the map, with the provided location as close to
     * center as possible given the size of the provided map and the screen space.
     *
     * The view only needs to be reset if changes to the map have occurred that were not incrementally passed in through
     * an Action or if an entirely new map should be displayed.
     *
     * @param map
     * @param startx the x coordinate that should be visible
     * @param starty the y coordinate that should be visible
     */
    public void setView(EpiMap map, int startx, int starty) {
        if (viewPanel != null) {
            viewPanel.remove();
        }

        this.map = map;

        int w = map.width;
        int h = map.height;
        viewPanel = new SquidPanel(w, h, tcf.width(12).height(18).initBySize());
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                EpiTile tile = map.contents[x][y];
                viewPanel.put(x, y, tile.getSymbol(), tile.getForegroundColor());
            }
        }
        
        viewPanel.put(startx, starty, '@', SColor.BLACK_DYE);

        viewPanel.setSize(12 * w, 18 * h);
        mainView.add(viewPanel);
        mainView.setSize(viewPanel.getWidth(), viewPanel.getHeight());
        viewPanel.setSize(viewPanel.getWidth(), viewPanel.getHeight());
        //viewScrollPane.setWidget(viewPanel);
        //viewScrollPane.layout();
        primaryTable.invalidateHierarchy();
        //mainView.setPosition(startx * viewPanel.getTextCellFactory().width(), starty * viewPanel.getTextCellFactory().height());
    }

    /**
     * Causes display to show that the condition was changed.
     *
     * @param condition the condition changing
     * @param type the type of change
     */
    public void indicateConditionChange(Condition condition, ConditionChangeType type) {
        //TODO -- fill out indication
    }

    /**
     * Shows that a creature (not the player) had the condition change passed in.
     *
     * @param x
     * @param y
     * @param condition
     * @param type
     */
    public void indicateConditionChange(int x, int y, Condition condition, ConditionChangeType type) {
        //TODO -- fill out
    }

    /**
     * Shows a change, either negative or positive, in one of the player's energy bars.
     *
     * @param energy
     * @param amount
     * @param maxChanged if true then the max value displayable is shifted by the given amount
     */
    public void indicateEnergyChange(Energy energy, int amount, boolean maxChanged) {
        //TODO -- show energy change
    }

    /**
     * Shows that a creature (not the player) had the energy change passed in.
     *
     * @param x
     * @param y
     * @param energy
     * @param amount
     * @param maxChanged
     */
    public void indicateEnergyChange(int x, int y, Energy energy, int amount, boolean maxChanged) {
        //TODO -- fill out
    }

    /**
     * Shows that the inventory or equipment of the player has changed in some way.
     *
     * @param item
     * @param type
     */
    public void indicateInventoryChange(Item item, InventoryChangeType type) {
        //TODO -- show inventory change
    }

    /**
     * Shows that the inventory or equipment of a creature (not the player) has changed.
     *
     * @param x
     * @param y
     * @param item
     * @param type
     */
    public void indicateInventoryChange(int x, int y, Item item, InventoryChangeType type) {
        //TODO -- fill out
    }

    /**
     * Shows that the player's given stat changed by the given amount.
     *
     * @param stat
     * @param amount
     */
    public void indicateStatChange(Stat stat, int amount) {
        //TODO -- show stat change
    }

    /**
     * Shows that a creature (not the player) had a stat changed by the given amount.
     *
     * @param x
     * @param y
     * @param stat
     * @param amount
     */
    public void indicateStatChange(int x, int y, Stat stat, int amount) {
        //TODO -- fill out
    }

    /**
     * Shows that the player's given skill changed by the given amount.
     *
     * @param skill
     * @param oldRating
     * @param newRating
     */
    public void indicateSkillChange(Skill skill, Rating oldRating, Rating newRating) {
        //TODO -- show skill change
    }

    /**
     * Shows that a creature (not the player) had a skill changed by the given amount.
     *
     * @param x
     * @param y
     * @param skill
     * @param oldRating
     * @param newRating
     */
    public void indicateSkillChange(int x, int y, Skill skill, Rating oldRating, Rating newRating) {
        //TODO -- fill out
    }

    /**
     * Shows that an item was created or destroyed at the given location.
     *
     * @param x
     * @param y
     * @param item
     * @param destroyed
     */
    public void indicateItemChange(int x, int y, Item item, boolean destroyed) {
        //TODO -- fill out
    }

    /**
     * Shows that a creature was either created or destroyed at the given location.
     *
     * @param x
     * @param y
     * @param killed
     */
    public void indicateCreatureChange(int x, int y, boolean killed) {
        //TODO -- fill out
    }

    /**
     * Shows that the terrain changed to become the new passed in terrain.
     *
     * @param x
     * @param y
     * @param terrain
     */
    public void indicateTerrainChange(int x, int y, Terrain terrain) {
        //TODO -- fill out
    }

    /**
     * Indicates an Area-Of-Effect centered at the given location.
     *
     * @param x
     * @param y
     * @param aoe
     */
    public void indicateAOE(int x, int y, AOE aoe) {
        //TODO -- fill out
    }

    /**
     * Indicates that displayed item at the given start location should be moved to the given end position.
     *
     * @param startx
     * @param starty
     * @param endx
     * @param endy
     */
    public void indicateMovement(int startx, int starty, int endx, int endy) {
        //TODO -- fill out
    }

    /**
     * Animates the given character moving from the starting position to the end position.
     *
     * This is appropriate for something like an arrow that is not meant to stay on the screen once its movement has
     * finished.
     *
     * @param startx
     * @param starty
     * @param endx
     * @param endy
     * @param c
     */
    public void indicateMovement(int startx, int starty, int endx, int endy, char c) {
        //TODO -- fill out
    }

    /**
     * Shows that the player has died and then progresses to the appropriate end game screen.
     */
    public void indicatePlayerDied() {
        //TODO -- show that player died and bring up appropriate screen
    }

    /**
     * Shows that the player gained the given achievement.
     *
     * Effects of earning this achievement must be handled through a separate action.
     *
     * @param achievement
     */
    public void indicateAchievement(Achievement achievement) {
        //TODO -- show the achievement
    }

    public SquidPanel getViewPanel() {
        return viewPanel;
    }

    /**
     * Resizes entire game display.
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Performs any rendering that needs to be done.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {

        atlas = new TextureAtlas("images/textures.txt");

        int width = Prefs.getScreenWidth(),
                height = Prefs.getScreenHeight();

        tcf = new TextCellFactory().font("Inconsolata-LGC-Square-25x25.fnt").width(25).height(25).initBySize();

        //set up the display tables
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        primaryTable = new Table(skin);
        primaryTable.setFillParent(true);
//        stage = new Stage(width, height, true);
        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);
        stage.addActor(primaryTable);

        leftTable = new Table(skin);
        primaryTable.add(leftTable).width(width * 0.6f).height(height);

        mainView = new Stack();
//        viewScrollPane = new ScrollPane(mainView, skin);
//        viewScrollPane.setScrollbarsOnTop(false);
//        viewScrollPane.setFadeScrollBars(false);
//        leftTable.add(viewScrollPane).expand().fill().width(width * 0.6f).height(height * 0.9f).colspan(2);
        leftTable.add(mainView).expand().fill().width(width * 0.6f).height(height * 0.9f).colspan(2);

        leftTable.row().height(height * 0.1f);

        defaultActionTable = new Table(skin);
        Image image = new Image(atlas.findRegion("default action"));
        image.setScaling(Scaling.fit);
        defaultActionTable.add(image);
        leftTable.add(defaultActionTable).width(width * 0.05f);

        actionBarTable = new Table(skin);
        image = new Image(atlas.findRegion("action bar"));
        image.setHeight(height * 0.08f);
        image.setScaling(Scaling.fillY);
        actionBarTable.add(image);
        ScrollPane viewScrollPane2 = new ScrollPane(actionBarTable, skin);
        viewScrollPane2.setScrollbarsOnTop(false);
        viewScrollPane2.setFadeScrollBars(false);
        leftTable.add(viewScrollPane2).width(width * 0.55f);
//        leftTable.add(actionBarTable).width(width * 0.55f);

        rightTable = new Table(skin);
        primaryTable.add(rightTable).top().right().width(width * 0.4f).height(height);

        healthBarTable = new Table(skin);
        image = new Image(atlas.findRegion("health bars"));
        image.setScaling(Scaling.fit);
        healthBarTable.add(image);
        rightTable.add(healthBarTable).size(width * 0.4f, height * 0.2f);

        rightTable.row();

        equipmentDummyTable = new Table(skin);
        image = new Image(atlas.findRegion("equipment dummies"));
        image.setScaling(Scaling.fit);
        equipmentDummyTable.add(image);
        rightTable.add(equipmentDummyTable).size(width * 0.4f, height * 0.12f);

        rightTable.row();

        multiViewSelectionTable = new Table(skin);
        image = new Image(atlas.findRegion("multiview selection"));
        image.setScaling(Scaling.fit);
        multiViewSelectionTable.add(image);
        rightTable.add(multiViewSelectionTable).size(width * 0.4f, height * 0.12f);

        rightTable.row();

        multiAreaTable = new Table(skin);
        image = new Image(atlas.findRegion("multiview"));
        image.setScaling(Scaling.fit);
        multiAreaTable.add(image);
        rightTable.add(multiAreaTable).size(width * 0.4f, height * 0.56f);

        //set all tables to debug draw if needed
        if (Prefs.isDebugMode()) {
            primaryTable.debug();
            leftTable.debug();
            rightTable.debug();
            //viewScrollPane.debug();
            viewScrollPane2.debug();
            mainView.debug();
            defaultActionTable.debug();
            actionBarTable.debug();
            healthBarTable.debug();
            equipmentDummyTable.debug();
            multiViewSelectionTable.debug();
            multiAreaTable.debug();
        }
        primaryTable.pack();
        primaryTable.invalidateHierarchy();
        Planner.INSTANCE.launch(this);//indicate that the display is set up and ready for action
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
        sound.dispose();
    }

}
