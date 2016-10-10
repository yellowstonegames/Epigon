package squidpony.messaging;

import java.util.ArrayList;

/**
 * Message class that contains the results of an attempted action.
 *
 * @author Eben
 */
public class ActionResultMessage {

    public boolean success;
    public boolean noticed;//marks if the player could tell something happened
    public String message;//compiled message regarding the results of this event
    public ArrayList<ActionResultDetail> details;//specifics of what happened
    public ActionMessage actionStarter;//immediate cause of this result
}
