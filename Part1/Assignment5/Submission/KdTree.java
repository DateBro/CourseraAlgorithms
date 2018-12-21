import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

/**
 * @author DateBro
 */

public class KdTree {

    private Node root;
    private int size;
    private double minDistance;
    private Point2D minPoint;

    public KdTree() {
        root = new Node();
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        if (size == 0) {
            root.p = p;
            root.rect = new RectHV(p.x(), 0, p.x(), 1);
            size++;
            return;
        }
        if (!contains(p)) {
            root = insert(null, root, p, true);
            size++;
        }
    }

    private Node insert(Node root, Node node, Point2D p, boolean nodeVertical) {
        if (node == null) {
            Node newNode = new Node();
            newNode.p = p;
            if (nodeVertical) {
                if (p.y() < root.p.y()) {
                    storeXY(root, newNode, true, false);
                    newNode.rect = new RectHV(p.x(), newNode.minY, p.x(), newNode.maxY);
                } else {
                    storeXY(root, newNode, false, false);
                    newNode.rect = new RectHV(p.x(), newNode.minY, p.x(), newNode.maxY);
                }
            } else {
                if (p.x() < root.p.x()) {
                    storeXY(root, newNode, true, true);
                    newNode.rect = new RectHV(newNode.minX, p.y(), newNode.maxX, p.y());
                } else {
                    storeXY(root, newNode, false, true);
                    newNode.rect = new RectHV(newNode.minX, p.y(), newNode.maxX, p.y());
                }
            }
            return newNode;
        }

        if (nodeVertical) {
            double x1 = node.p.x();
            double x2 = p.x();
            if (x2 < x1) {
                node.lb = insert(node, node.lb, p, false);
            } else {
                node.rt = insert(node, node.rt, p, false);
            }
        } else {
            double y1 = node.p.y();
            double y2 = p.y();
            if (y2 < y1) {
                node.lb = insert(node,node.lb, p, true);
            } else {
                node.rt = insert(node, node.rt, p, true);
            }
        }
        return node;
    }

    // store the min and max x-coordinate and y-coordinate to avoid the rectangle intersect with the previous node's rect
    private void storeXY(Node root, Node node, boolean nodeLB,boolean rootVertical) {
        if (node == null) return;

        if (nodeLB && rootVertical) {
            node.maxX = root.p.x();
            node.minX = root.minX;
            node.minY = root.minY;
            node.maxY = root.maxY;
        } else if (!nodeLB && rootVertical) {
            node.minX = root.p.x();
            node.maxX = root.maxX;
            node.minY = root.minY;
            node.maxY = root.maxY;
        } else if (nodeLB && !rootVertical) {
            node.maxY = root.p.y();
            node.minY = root.minY;
            node.maxX = root.maxX;
            node.minX = root.minX;
        } else {
            node.minY = root.p.y();
            node.maxY = root.maxY;
            node.maxX = root.maxX;
            node.minX = root.minX;
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return contains(root, p, true);
    }

    private boolean contains(Node node, Point2D p, boolean nodeVertical) {
        if (node == null || root == null || node.p == null) {
            return false;
        }
        if (nodeVertical) {
            double x1 = node.p.x();
            double x2 = p.x();
            if (x2 < x1) {
                return contains(node.lb, p, false);
            } else if (x2 > x1) {
                return contains(node.rt, p, false);
            } else {
                if (node.p.y() == p.y()) {
                    return true;
                } else {
                    return contains(node.rt, p, false);
                }
            }
        } else {
            double y1 = node.p.y();
            double y2 = p.y();
            if (y2 < y1) {
                return contains(node.lb, p, true);
            } else if (y2 > y1) {
                return contains(node.rt, p, true);
            } else {
                if (node.p.x() == p.x()) {
                    return true;
                } else {
                    return contains(node.rt, p, true);
                }
            }
        }
    }

    public void draw() {
        draw(root, true);
    }

    private void draw(Node node, boolean nodeVertical) {
        if (node == null) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        if (nodeVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            node.rect.draw();

            draw(node.lb, false);
            draw(node.rt, false);
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            node.rect.draw();

            draw(node.lb, true);
            draw(node.rt, true);
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        SET<Point2D> set = new SET<>();
        range(root, set, rect, true);
        return set;
    }

    private void range(Node node, SET<Point2D> set, RectHV rect, boolean nodeVertical) {
        if (node == null || node.rect == null) {
            return;
        }

        if (node.rect.intersects(rect)) {
            double x = node.p.x();
            double y = node.p.y();
            if (x <= rect.xmax() && x >= rect.xmin() && y <= rect.ymax() && y >= rect.ymin()) {
                set.add(node.p);
            }
            range(node.lb, set, rect, !nodeVertical);
            range(node.rt, set, rect, !nodeVertical);
        } else {
            if (nodeVertical) {
                if (rect.xmax() < node.p.x()) {
                    range(node.lb, set, rect, false);
                } else if (rect.xmin() > node.p.x()) {
                    range(node.rt, set, rect, false);
                }
            } else {
                if (rect.ymax() < node.p.y()) {
                    range(node.lb, set, rect, true);
                } else if (rect.ymin() > node.p.y()) {
                    range(node.rt, set, rect, true);
                }
            }
        }

    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        minPoint = root.p;
        minDistance = Double.POSITIVE_INFINITY;
        nearest(root, p, true);
        return minPoint;
    }

    private void nearest(Node node, Point2D queryPoint, boolean nodeVertical) {
        if (node == null || node.p == null) {
            return;
        }

        double distance = queryPoint.distanceTo(node.p);
        if (distance < minDistance) {
            minDistance = distance;
            minPoint = node.p;
            if (nodeVertical) {
                if (queryPoint.x() < node.p.x()) {
                    nearest(node.lb, queryPoint, false);
                    if (node.rt != null && minDistance >= (Math.abs((node.p.x() - queryPoint.x())))) {
                        nearest(node.rt, queryPoint, false);
                    }
                } else {
                    nearest(node.rt, queryPoint, false);
                    if (node.lb != null && minDistance >= (Math.abs((node.p.x() - queryPoint.x())))) {
                        nearest(node.lb, queryPoint, false);
                    }
                }
            } else {
                if (queryPoint.y() < node.p.y()) {
                    nearest(node.lb, queryPoint, true);
                    if (node.rt != null && minDistance >= (Math.abs((node.p.y() - queryPoint.y())))) {
                        nearest(node.rt, queryPoint, true);
                    }
                } else {
                    nearest(node.rt, queryPoint, true);
                    if (node.lb != null && minDistance >= (Math.abs((node.p.y() - queryPoint.y())))) {
                        nearest(node.lb, queryPoint, true);
                    }
                }
            }
        } else {
            if (nodeVertical) {
                if (queryPoint.x() < node.p.x()) {
                    nearest(node.lb, queryPoint, false);
                    if (node.rt != null && minDistance >= (Math.abs((node.p.x() - queryPoint.x())))) {
                        nearest(node.rt, queryPoint, false);
                    }
                } else {
                    nearest(node.rt, queryPoint, false);
                    if (node.lb != null && minDistance >= (Math.abs((node.p.x() - queryPoint.x())))) {
                        nearest(node.lb, queryPoint, false);
                    }
                }
            } else {
                if (queryPoint.y() < node.p.y()) {
                    nearest(node.lb, queryPoint, true);
                    if (node.rt != null && minDistance >= (Math.abs((node.p.y() - queryPoint.y())))) {
                        nearest(node.rt, queryPoint, true);
                    }
                } else {
                    nearest(node.rt, queryPoint, true);
                    if (node.lb != null && minDistance >= (Math.abs((node.p.y() - queryPoint.y())))) {
                        nearest(node.lb, queryPoint, true);
                    }
                }
            }
        }
    }

    private static class Node {
        // the point
        private Point2D p;
        // the axis-aligned rectangle corresponding to this node
        private RectHV rect;
        // the left/bottom subtree
        private Node lb;
        // the right/top subtree
        private Node rt;

        // the first line x-coordinate below
        private double minX = 0;
        // the first line y-coordinate below
        private double minY = 0;
        // the first line x-coordinate above
        private double maxX = 1;
        // the first line y-coordinate above
        private double maxY = 1;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
