package squidpony.math;

/**
 *
 * @author Eben
 */
public class Point3D {

    public int x, y, z;//three coordinates

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(Point3D other) {
        if (other != null) {
            x = other.x;
            y = other.y;
            z = other.z;
        }
    }

    public boolean equals(Point3D other){
        if (x == other.x && y == other.y && z==other.z){
            return true;
        }else{
            return false;
        }
    }
}
