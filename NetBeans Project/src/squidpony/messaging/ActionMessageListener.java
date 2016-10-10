package squidpony.messaging;

/**
 *
 * @author SquidPony
 */
public interface ActionMessageListener extends RLMessageListener{
    
    public void notifyListener(ActionMessage message);
}
