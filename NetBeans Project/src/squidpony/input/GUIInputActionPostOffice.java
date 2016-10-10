package squidpony.input;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Gathers input and distributes said input to listeners.
 * 
 * Is a singleton.
 *
 * @author Eben
 */
public class GUIInputActionPostOffice implements InputCommandObserver {

    private volatile Set<InputCommandListener> listeners = new HashSet<>();
    private GUIInputActionPostOffice instance = new GUIInputActionPostOffice();

    private GUIInputActionPostOffice() {
    }

    public GUIInputActionPostOffice getInstance() {
        return instance;
    }

    /**
     * Modifies the provided JFrame's InputMap and ActionMap to signal this class
     * to pass out InputCommand messages as desired.
     * 
     * @param frame 
     */
    public void setUpKeyboardMapping(JFrame frame) {//TODO -- make all of this read from a config file so it's user customizable
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = frame.getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("SPACE"), "more");
        am.put("more", new InputKeyAction(InputCommand.MORE));

        im.put(KeyStroke.getKeyStroke("UP"), "north");
        am.put("north", new InputKeyAction(InputCommand.NORTH));

        im.put(KeyStroke.getKeyStroke("DOWN"), "south");
        am.put("south", new InputKeyAction(InputCommand.SOUTH));
    }

    @Override
    public void registerListener(InputCommandListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean unregisterListener(InputCommandListener listener) {
        return listeners.remove(listener);
    }

    private void notifyListeners(InputCommand command) {
        for (InputCommandListener l : listeners) {
            l.notifyListener(command);
        }
    }

    private class InputKeyAction extends AbstractAction {

        InputCommand command;

        InputKeyAction(InputCommand command) {
            this.command = command;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            notifyListeners(command);
        }
    }
}
