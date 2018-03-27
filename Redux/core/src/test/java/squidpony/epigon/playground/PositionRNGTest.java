package squidpony.epigon.playground;

import squidpony.epigon.PositionRNG;

/**
 * Created by Tommy Ettinger on 3/26/2018.
 */
public class PositionRNGTest {
    public static void main(String[] args)
    {
        testBitPair();
    }
    public static void testBit0()
    {
        PositionRNG p = new PositionRNG(12345L);
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                p.move(x, y);
                System.out.printf("%1d ", p.next(1));
            }
            System.out.println();
        }
    }
    public static void testBitPair()
    {
        PositionRNG p = new PositionRNG(12345L);
        PositionRNG p2 = new PositionRNG(0L);
        int i = 0, total = 0;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                p.move(x, y);
                p2.move(1000000000L + p.stream, x, y);
                System.out.printf("%1d ", i = (p.next(1) + p2.next(1)));
                total += i;
            }
            System.out.println();
        }
        System.out.println(total);
    }
}
