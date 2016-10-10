package squidpony.exception;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class gathers warnings and reports them to registered listeners.
 *
 * @author Eben
 */
public class WarningObserver {

    static WarningObserver instance = new WarningObserver();
    ArrayList<Warning> warnings = new ArrayList<>();
    ArrayList<WarningListener> listeners = new ArrayList<>();
    Timer timer;

    /**
     * Prevent creation of an instantiation.
     */
    private WarningObserver() {
    }

    static public WarningObserver getInstance() {
        return instance;
    }

    public void registerListener(WarningListener listener) {
        //the first time a listener is added, start up the notification timer task
        if (listeners.isEmpty()) {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new Notifier(), 1, 200);
        }
        listeners.add(listener);
    }

    public boolean unRegisterListener(WarningListener listener) {
        boolean success = listeners.remove(listener);
        if (listeners.isEmpty()) {
            timer.cancel();
        }
        return success;
    }

    public void reportWarning(Warning warning) {
        warnings.add(warning);
    }

    public void reportWarning(String warning) {
        warnings.add(new GeneralProcessingWarning(warning));
    }

    synchronized public void notifyListeners() {
        for (WarningListener wl : listeners) {
            for (Warning w : warnings) {
                wl.notifyWarning(w);
            }
        }
        warnings = new ArrayList<Warning>();//empty the warnings
    }

    class Notifier extends TimerTask {

        @Override
        public void run() {
            notifyListeners();
        }
    }
}
