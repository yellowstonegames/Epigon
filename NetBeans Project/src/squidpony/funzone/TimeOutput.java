package squidpony.funzone;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eben Howard
 */
public class TimeOutput {
    public static void main(String... args){
        while(true){
            System.out.println(System.currentTimeMillis() + "\n");
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(TimeOutput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
