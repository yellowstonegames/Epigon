package squidpony.math;

import java.util.Random;

/**
 *
 * @author Eben
 */
public class RLMath {

    public static Random RNG = new Random();

    /**
     * Returns a value with x as the mean within 5 sigma based on a normal distribution.
     * 
     * @param x
     * @param scale
     * @return
     */
    public static double getModifiedRandom(double x, double scale) {
        double deviation = RNG.nextGaussian();
        deviation = Math.max(deviation, -5.0);
        deviation = Math.min(deviation, 5.0);
        return x + deviation * scale;
    }
}
