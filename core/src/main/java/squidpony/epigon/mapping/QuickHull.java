package squidpony.epigon.mapping;

import squidpony.squidmath.Coord;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * QuickHull is an algorithm to compute the convex hull of a set of points. The time complexity is O(n^2) in the worst
 * case and O(n*log n) on average.
 *
 * Based on https://github.com/Thurion/algolab and modified to work in the SquidLib ecosystem
 *
 * @author Sebastian Bauer
 * @author Eben Howard
 */
public class QuickHull {

    /**
     * Implementation of the QuickHull algorithm.
     *
     * @param inputPoints
     * @return A list of points which form the convex hull of the given list of points.
     */
    public List<Coord> executeQuickHull(Coord[] inputPoints) {
        List<Coord> convexHull = new ArrayList<>();
        if (inputPoints == null || inputPoints.length < 1) {
            throw new IllegalArgumentException("Cannot compute convex hull of zero points.");
        }

//        System.out.println("Starting quickhull for " + inputPoints.length + " points.");
//        long millis = System.currentTimeMillis();

        // search extreme values
        Coord rightmostPoint = inputPoints[0];
        Coord leftmostPoint = inputPoints[0];
        for (Coord point : inputPoints) {
            if (point.getX() < rightmostPoint.getX()) {
                rightmostPoint = point;
            } else if (point.getX() > leftmostPoint.getX()) {
                leftmostPoint = point;
            }
        }

        // divide the set into two halves
        List<Coord> leftOfLine = new LinkedList<>();
        List<Coord> rightOfLine = new LinkedList<>();
        for (Coord point : inputPoints) {
            if (point.equals(rightmostPoint) || point.equals(leftmostPoint)) {
                continue;
            }

            if (isLeftOfLine(point, leftmostPoint, rightmostPoint)) {
                leftOfLine.add(point);
            } else {
                rightOfLine.add(point);
            }
        }

        convexHull.add(leftmostPoint);
        List<Coord> hull = divide(leftOfLine, leftmostPoint, rightmostPoint);
        convexHull.addAll(hull);
        convexHull.add(rightmostPoint);

        hull = divide(rightOfLine, rightmostPoint, leftmostPoint);
        convexHull.addAll(hull);

//        millis = System.currentTimeMillis() - millis;
//        System.out.println("Quickhull took " + millis + " milliseconds.");
        return convexHull;
    }

    /**
     * Calculate the crossproduct of vectors origin->p2 and origin->this.
     *
     * @param origin The point in which both vectors originate
     * @param p2 The point that determines the second vector.
     * @return 0 if both points are collinear, a value > 0 if this point lies left of vector origin->p2 (when standing
     * in origin looking at p2), a value < 0 if this point lies right of vector origin->p2.
     */
    private double calcCrossProductWithOrigin(Coord origin, Coord p1, Coord p2) {
        return (p2.x - origin.x) * (p1.y - origin.y)
            - (p2.y - origin.y) * (p1.x - origin.x);
    }

    /**
     * A point is considered left of a line between points from and to if it is on the lefthand side when looking along
     * the line from point "from" to point "to".
     *
     * The method uses the cross-product to determine if this point is left of the line.
     *
     * @param from Point from which the line is drawn and from where we "look" along the line in direction of point "to"
     * to determine whether the point is left or right of it.
     * @param to Point to which the line is drawn
     */
    private boolean isLeftOfLine(Coord point, Coord from, Coord to) {
        return Double.compare(calcCrossProductWithOrigin(from, point, to), 0) > 0;
    }

    /**
     * Calculates the distance of this point to the line which is formed by points a and b.
     *
     * @param a
     * @param b
     * @return The distance to the line.
     */
    private double getDistanceToLine(Coord point, Coord a, Coord b) {
        return Math.abs((b.getX() - a.getX()) * (a.getY() - point.y) - (a.getX() - point.x) * (b.getY() - a.getY()))
            / Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

    /**
     * Recursive implementation of QuickHull to find the furthest point to the line between the points p1 and p2 and
     * divide the list of points. Caution: The points p1 and p2 must be in correct order so that the points which are
     * outside if the triangle furthest point - p1 - p2 are left of the viewing direction. The viewing directions are as
     * follow: p1 -> furthest point -> p2.
     *
     * @param points The list of points
     * @param p1
     * @param p2
     * @return a List of points, I guess? // TODO: Eben what am this do
     */
    private List<Coord> divide(List<Coord> points, Coord p1, Coord p2) {

        List<Coord> hull = new ArrayList<>();

        if (points.isEmpty()) {
            return hull;
        } else if (points.size() == 1) {
            hull.add(points.get(0));
            return hull;
        }

        Coord maxDistancePoint = points.get(0);
        List<Coord> l1 = new LinkedList<>();
        List<Coord> l2 = new LinkedList<>();
        double distance = 0.0;
        for (Coord point : points) {
            if (getDistanceToLine(point, p1, p2) > distance) {
                distance = getDistanceToLine(point, p1, p2);
                maxDistancePoint = point;
            }
        }

        points.remove(maxDistancePoint);

        for (Coord point : points) {
            if (isLeftOfLine(point, p1, maxDistancePoint)) {
                l1.add(point);
            } else if (isLeftOfLine(point, maxDistancePoint, p2)) {
                l2.add(point);
            }
        }

        points.clear();

        List<Coord> hullPart = divide(l1, p1, maxDistancePoint);
        hull.addAll(hullPart);
        hull.add(maxDistancePoint);
        hullPart = divide(l2, maxDistancePoint, p2);
        hull.addAll(hullPart);

        return hull;
    }
}
