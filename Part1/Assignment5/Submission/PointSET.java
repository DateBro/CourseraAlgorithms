import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

/**
 * @author DateBro
 */

public class PointSET {

    private SET<Point2D> pointSET;

    // construct an empty set of points
    public PointSET() {
        pointSET = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointSET.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointSET.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        if (!pointSET.contains(p)) {
            pointSET.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p==null) throw new IllegalArgumentException();

        return pointSET.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : pointSET) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect==null) throw new IllegalArgumentException();

        SET<Point2D> pointsIn = new SET<>();
        for (Point2D p : pointSET) {
            double x = p.x();
            double y = p.y();
            if (x <= rect.xmax() && x >= rect.xmin() && y <= rect.ymax() && y >= rect.ymin()) {
                pointsIn.add(p);
            }
        }

        return pointsIn;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p==null) throw new IllegalArgumentException();

        Point2D nearestP = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D point : pointSET) {
            double distance = point.distanceTo(p);
            if (distance < minDistance) {
                minDistance = distance;
                nearestP = point;
            }
        }

        return nearestP;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
