package squidpony.epigon.messaging;

/**
 *
 * @author Eben
 */
public interface DebugMessageListener extends RLMessageListener {
    
    public void notifyListener(DebugMessage message);
}
