package squidpony.epigon.exception;

/**
 * This class allows for a low level exception where data that is not recognized is
 * encountered, but it is not critical. This exception should serve as a warning rather than
 * a stopping condition.
 *
 * @author Eben
 */
public class UnknownInputFoundWarning implements Warning {

    String message;

    public UnknownInputFoundWarning(String message) {
        this.message = message;
    }

    public UnknownInputFoundWarning() {
        message = "Generic UnknownInputFoundWarning";
    }

    @Override
    public String getMessage() {
        return message;
    }
}