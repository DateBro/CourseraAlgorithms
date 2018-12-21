import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author DateBro
 */

public class FastCollinearPoints {

    private int lineSegmentNum = 0;
    private Stack<LineSegment> segmentStack;
    private LineSegment[] segmentArr;
    private Point[] newPoints;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
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

            double[] slopeArr = new double[N - i - 1];
            Point[] copyPoints = new Point[N - i - 1];
            int index = 0;

            for (int j = i + 1; j < N; j++) {
                copyPoints[index] = newPoints[j];
                slopeArr[index] = p.slopeTo(newPoints[j]);
                index++;
            }

            Arrays.sort(slopeArr);
            Arrays.sort(copyPoints, p.slopeOrder());
            int collinearPointsNum = 2;

            for (int k = 0; k < slopeArr.length-1; k++) {
                if (slopeArr[k] == slopeArr[k + 1] || (Math.abs(slopeArr[k] - slopeArr[k + 1]) < 0.00001)) {
                    collinearPointsNum++;
                } else {
                    if (collinearPointsNum >= 4) {
                        segmentStack.push(new LineSegment(p, copyPoints[k]));
                        lineSegmentNum++;
                    }
                    collinearPointsNum = 2;
                }

                if (k == slopeArr.length - 2 && collinearPointsNum >= 4) {
                    segmentStack.push(new LineSegment(p, copyPoints[k + 1]));
                    lineSegmentNum++;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
