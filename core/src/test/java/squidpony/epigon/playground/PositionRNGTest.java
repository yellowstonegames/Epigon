package squidpony.epigon.playground;

import squidpony.StringKit;
import squidpony.epigon.PositionRNG;

/**
 * Created by Tommy Ettinger on 3/26/2018.
 */
public class PositionRNGTest {
    public static void main(String[] args)
    {
        testBit0();
        System.out.println();
        testBitPair();
        System.out.println("\nAnd old way:\n");
        testBit0Old();
        System.out.println();
        testBitPairOld();
    }
    public static void testBit0()
    {
        PositionRNG p = new PositionRNG(12345L);
        int total = 0, v;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                p.move(x, y);
                total += (v = p.next(1));
                System.out.printf("%1d ", v);
            }
            System.out.println();
        }
        System.out.println(total);
    }
    public static void testBitPair()
    {
        PositionRNG p = new PositionRNG();
        PositionRNG p2 = new PositionRNG();
        int i = 0, total = 0;
        int[] is = new int[9];
        for (int y = 0; y < 48; y++) {
            for (int x = 0; x < 48; x++) {
                p.move(x, y);
                p2.move(111L, x, y);
                System.out.printf("%1d ", i = (p.next(3) + p2.next(1)));
                is[i]++;
                total += i;
            }
            System.out.println();
        }
        System.out.println(total + " total, individual rates " + StringKit.join(", ", is));
    }
    public static void testBit0Old()
    {
        PositionRNGOld p = new PositionRNGOld(12345L);
        int total = 0, v;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                p.move(x, y);
                total += (v = p.next(1));
                System.out.printf("%1d ", v);
            }
            System.out.println();
        }
        System.out.println(total);
    }
    public static void testBitPairOld()
    {
        PositionRNGOld p = new PositionRNGOld();
        PositionRNGOld p2 = new PositionRNGOld();
        int i = 0, total = 0;
        int[] is = new int[9];
        for (int y = 0; y < 48; y++) {
            for (int x = 0; x < 48; x++) {
                p.move(x, y);
                p2.move(111L, x, y);
                System.out.printf("%1d ", i = (p.next(3) + p2.next(1)));
                is[i]++;
                total += i;
            }
            System.out.println();
        }
        System.out.println(total + " total, individual rates " + StringKit.join(", ", is));
    }
}
