import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author DateBro
 */

public class BruteCollinearPoints {

    private int lineSegmentNum = 0;
    private LineSegment[] segmentArr;
    private Stack<LineSegment> segmentStack;
    private Point[] newPoints;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
            for (int j = 0; j < points.length; j++) {
                if (j != i && points[j].compareTo(points[i]) == 0) {
                    throw new IllegalArgumentException();
                }
            }
        }

        int N = points.length;
        segmentStack = new Stack<>();

        newPoints = new Point[N];
        for (int i = 0; i < N; i++) {
            newPoints[i] = points[i];
        }
        Arrays.sort(newPoints);

        for (int i = 0; i < N; i++) {
            Point p = newPoints[i];
            for (int j = i + 1; j < N; j++) {
                Point q = newPoints[j];
                double lineSlope = p.slopeTo(q);

                for (int k = j + 1; k < N; k++) {
                    Point r = newPoints[k];
                    double slope2 = q.slopeTo(r);
                    if (slope2 == lineSlope) {
                        for (int l = k + 1; l < N; l++) {
                            Point s = newPoints[l];
                            double slope3 = r.slopeTo(s);
                            if (slope3 == lineSlope) {
                                segmentStack.push(new LineSegment(p, s));
                                lineSegmentNum++;
                            }
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegmentNum;
    }

    // the line segments
    public LineSegment[] segments() {
        segmentArr = new LineSegment[segmentStack.size()];

        for (int i = 0; i < segmentArr.length; i++) {
            segmentArr[i] = segmentStack.pop();
        }

        return segmentArr;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
