package squidpony.math;

/**
 *
 * @author Eben
 */
public class PerlinTest {

    public static void main(String[] args){
        for (int i = 0;i<255;i++){
            System.out.println("i: " + ImprovedNoise.noise(1000000,5000000, 25500000));
        }
    }
}
