package squidpony.epigon.exception;

/**
 *
 * @author Eben
 */
public class GeneralProcessingWarning implements Warning{

    String message;
    
    public GeneralProcessingWarning(String message){
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
}
