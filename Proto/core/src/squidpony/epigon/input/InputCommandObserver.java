package squidpony.epigon.input;

/**
 *
 * @author SquidPony
 */
public interface InputCommandObserver {
    
    /**
     * Registers a listener to be notified of input commands.
     * @param listener 
     */
    public void registerListener(InputCommandListener listener);
    
    /**
     * Removes the provided listener for the notification process.
     * 
     * @param listener
     * @return true if the listener was found and removed, false otherwise
     */
    public boolean unregisterListener(InputCommandListener listener);
    
    
}
