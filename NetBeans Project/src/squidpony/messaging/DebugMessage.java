package squidpony.messaging;

/**
 *
 * @author Eben
 */
public class DebugMessage implements RLMessage{
    private String message;
    
    public DebugMessage(String message){
        this.message = message;
    }
    
    public String getMessage(){
        return message;
    }
}
