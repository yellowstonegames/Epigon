package squidpony.epigon.exception;

/**
 *
 * @author Eben
 */
public class InvalidInputFormatException extends Exception {

    /**
     * Creates a new instance of <code>InvalidInputFormatException</code> without detail message.
     */
    public InvalidInputFormatException() {
    }

    /**
     * Constructs an instance of <code>InvalidInputFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidInputFormatException(String msg) {
        super(msg);
    }
}
