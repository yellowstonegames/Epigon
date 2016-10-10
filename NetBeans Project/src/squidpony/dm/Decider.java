package squidpony.dm;

import java.util.LinkedList;
import java.util.Queue;
import squidpony.actions.AbstractAction;

/**
 * Responsible for deciding which actions are successful.
 * 
 * Will spin off new actions that are the result a previous action.
 *
 * @author Eben
 */
public class Decider {

    private static Decider instance = new Decider();
    private static Queue<AbstractAction> actions = new LinkedList<>();

    private Decider() {
    }

    public static Decider getInstance() {
        return instance;
    }

    private void runNextAction() {
        AbstractAction action = actions.poll();

    }
}
