package squidpony.dm;

import squidpony.mapping.World;
import squidpony.ui.DisplayMaster;
import squidpony.universe.Options;

/**
 * This singleton class has overall command of the game. It directs game flow
 * and keeps track of all high level information. It also interacts with the
 * display responding to input and then pushing desired display updates.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
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
        display.setView(world.getDefaultMap(), world.getDefaultMap().width, world.getDefaultMap().height);
    }
}
