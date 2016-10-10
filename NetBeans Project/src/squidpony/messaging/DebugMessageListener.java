package squidpony.messaging;

/**
 *
 * @author Eben
 */
public interface DebugMessageListener extends RLMessageListener {
    
    public void notifyListener(DebugMessage message);
}
