package squidpony.epigon.util;

/**
 * Utility class to do bounds checking.
 */
public class Bounds {

    public final int width, height, depth;

    public Bounds(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public boolean pointInBounds(int x, int y, int z) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
    }
}
