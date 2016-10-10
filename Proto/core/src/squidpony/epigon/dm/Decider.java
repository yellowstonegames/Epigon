package squidpony.epigon.dm;

import java.util.LinkedList;
import java.util.Queue;
import squidpony.epigon.actions.Action;

/**
 * Responsible for deciding which actions are successful.
 * 
 * Will spin off new actions that are the result a previous action.
 *
 * @author Eben
 */
public class Decider {

    private static Decider instance = new Decider();
    private static Queue<Action> actions = new LinkedList<>();

    private Decider() {
    }

    public static Decider getInstance() {
        return instance;
    }

    private void runNextAction() {
        Action action = actions.poll();

    }
}
