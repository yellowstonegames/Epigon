package squidpony.messaging;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

/**
 * This class is the central point all messages pass through.
 * 
 * @author SquidPony
 */
public class PostOffice implements Runnable {//TODO -- merge error, warning, and debug messaging into here
    private static PostOffice instance = new PostOffice();
    private boolean keepRunning = true;
    volatile TreeMap<String, ArrayList<RLMessageListener>> listeners = new TreeMap<>();
    volatile Queue<RLMessage> messages = new LinkedList<>();

    private PostOffice() {
    }

    public static PostOffice getInstance() {
        return instance;
    }

    /**
     * Adds the listener to delivery list. The listener will only get messages
     * of the type that's provided in the second parameter.
     * 
     * @param listener 
     * @param messageType should be a class implementation of RLMessage
     */
    public void registerListener(RLMessageListener listener, Class<? extends RLMessage> messageType) {
        if (!listeners.containsKey(messageType.toString())) {//ensure there's somewhere to put the listener
            listeners.put(messageType.toString(), new ArrayList<RLMessageListener>());
        }
        listeners.get(messageType.toString()).add(listener);
    }

    /**
     * Removes the listener from the delivery list of the associated message type.
     * If the listener is listening to more than one type, it must explicitly
     * unregister from all types it wishes to stop receiving.
     * 
     * @param listener
     * @param messageType 
     */
    public void unregisterListener(RLMessageListener listener, Class<? extends RLMessage> messageType) {
        if (listeners.containsKey(messageType.toString())) {
            listeners.get(messageType.toString()).remove(listener);
        }
    }

    /**
     * Delivers the provided message to the Post Office for later use.
     * 
     * @param message 
     */
    public void registerMessage(RLMessage message) {
        messages.add(message);
    }

    private void notifyListeners() {
        RLMessage message;
        ArrayList<RLMessageListener> list;//temp variable
        while (!messages.isEmpty()) {
            message = messages.poll();
            list = listeners.get(message.getClass().toString());
            if (list != null) {//make sure the list has data
                for (RLMessageListener listener : list) {
                    listener.notifyListener(message);
                }
            }
        }
    }

    /**
     * Should be called when it's time to kill this thread.
     * 
     * If the thread is currently notifying listeners, it will continue
     * to do so until the current message queue empties, and then
     * the thread will exit.
     */
    public void stopThread() {
        keepRunning = false;
    }

    @Override
    public void run() {
        while (keepRunning) {
            notifyListeners();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                //don't do anything during an interruption
            }
        }
    }
}
