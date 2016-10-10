package squidpony.exception;

/**
 *
 * @author Eben
 */
public class SystemErrorLogWarningListener implements WarningListener {

    @Override
    public void notifyWarning(Warning warning) {
        System.err.println(warning.getMessage());
    }
}
