package squidpony.epigon.dm;

import squidpony.epigon.mapping.World;
import squidpony.epigon.ui.DisplayMaster;
import squidpony.epigon.universe.Options;

/**
 * This singleton class has overall command of the game. It directs game flow
 * and keeps track of all high level information. It also interacts with the
 * display responding to input and then pushing desired display updates.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Planner {

    INSTANCE;
    //
    private World world = new World();
    private Options options = Options.INSTANCE;

    /**
     * Starts the game logic running.
     *
     * Should be called once all resources are set up for the game to
     * initialize.
     * 
     * @param display
     */
    public void launch(DisplayMaster display) {
        System.out.println("Game Launched!");
        display.setView(world.getDefaultMap(), world.getDefaultMap().width / 2, world.getDefaultMap().height / 2);
    }
}