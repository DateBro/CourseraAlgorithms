import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private Digraph digraph;
    private Node lastQuerryNode;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (lastQuerryNode != null && ((lastQuerryNode.v == v && lastQuerryNode.w == w) || (lastQuerryNode.v == w && lastQuerryNode.w == v))) {
            return lastQuerryNode.vwLength;
        }

        Queue<Queue<Integer>> vQ = bfs(digraph, v, w);
        Queue<Queue<Integer>> wQ = bfs(digraph, w, v);

        int ancestor = Integer.MAX_VALUE;
        int length = Integer.MAX_VALUE;

        int vLength = 0;
        for (Queue<Integer> vLevel : vQ) {
            int wLength = 0;
            for (Queue<Integer> wLevel : wQ) {
                int tempAncestor = ancestor(vLevel, wLevel);
                int distance = vLength + wLength;
                if (tempAncestor != -1 && distance < length) {
                    ancestor = tempAncestor;
                    length = distance;
                }
                wLength++;
            }
            vLength++;
        }

        if (ancestor == Integer.MAX_VALUE) {
            lastQuerryNode = new Node(v, w, -1, -1);
            return -1;
        } else {
            lastQuerryNode = new Node(v, w, ancestor, length);
            return length;
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (lastQuerryNode != null && ((lastQuerryNode.v == v && lastQuerryNode.w == w) || (lastQuerryNode.v == w && lastQuerryNode.w == v))) {
            return lastQuerryNode.ancestor;
        }

        Queue<Queue<Integer>> vQ = bfs(digraph, v, w);
        Queue<Queue<Integer>> wQ = bfs(digraph, w, v);

        int ancestor = Integer.MAX_VALUE;
        int length = Integer.MAX_VALUE;

        int vLength = 0;
        for (Queue<Integer> vLevel : vQ) {
            int wLength = 0;
            for (Queue<Integer> wLevel : wQ) {
                int tempAncestor = ancestor(vLevel, wLevel);
                int distance = vLength + wLength;
                if (tempAncestor != -1 && distance < length) {
                    ancestor = tempAncestor;
                    length = distance;
                }
                wLength++;
            }
            vLength++;
        }

        if (ancestor == Integer.MAX_VALUE) {
            lastQuerryNode = new Node(v, w, -1, -1);
            return -1;
        } else {
            lastQuerryNode = new Node(v, w, ancestor, length);
            return ancestor;
        }
    }

    private int ancestor(Queue<Integer> vLevel, Queue<Integer> wLevel) {
        for (int i : vLevel) {
            for (int j : wLevel) {
                if (i == j) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Queue<Queue<Integer>> bfs(Digraph digraph, int s, int breakPoint) {
        Queue<Integer> queue = new Queue<Integer>();
        Queue<Queue<Integer>> pathQueue = new Queue<Queue<Integer>>();
        queue.enqueue(s);

        boolean breakRoot = false;

        while (!queue.isEmpty()) {
            int size = queue.size();
            Queue<Integer> temp = new Queue<Integer>();
            for (int i = 0; i < size; i++) {
                int currentVertex = queue.dequeue();
                if (!inPath(pathQueue, currentVertex)) {
                    temp.enqueue(currentVertex);

                    if (currentVertex == breakPoint) {
                        breakRoot = true;
                        break;
                    }

                    for (int w : digraph.adj(currentVertex)) {
                        queue.enqueue(w);
                    }
                }
            }
            pathQueue.enqueue(temp);
            if (breakRoot) break;
        }

        return pathQueue;
    }

    private boolean inPath(Queue<Queue<Integer>> pathQueue, int vertex) {
        for (Queue<Integer> queue : pathQueue) {
            for (int i : queue) {
                if (i == vertex) {
                    return true;
                }
            }
        }
        return false;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateIterable(v) || !validateIterable(w)) {
            throw new IllegalArgumentException();
        }

        int shortestLength = Integer.MAX_VALUE;
        for (int i : v) {
            for (int j : w) {
                int length = length(i, j);
                if (length != -1 && length < shortestLength) {
                    shortestLength = length;
                }
            }
        }

        return shortestLength == Integer.MAX_VALUE ? -1 : shortestLength;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateIterable(v) || !validateIterable(w)) {
            throw new IllegalArgumentException();
        }

        Queue<Integer> ancestorQueue = new Queue<Integer>();
        Queue<Integer> lengthQueue = new Queue<Integer>();
        for (int i : v) {
            for (int j : w) {
                ancestorQueue.enqueue(ancestor(i, j));
                lengthQueue.enqueue(length(i, j));
            }
        }
        int shortestLength = Integer.MAX_VALUE;
        int shortestAncestor = -1;
        while (!lengthQueue.isEmpty()) {
            int length = lengthQueue.dequeue();
            int ancestor = ancestorQueue.dequeue();
            if (length != -1 && length < shortestLength) {
                shortestLength = length;
                shortestAncestor = ancestor;
            }
        }

        return shortestAncestor;
    }

    private boolean validateIterable(Iterable<Integer> v) {
        if (v == null) return false;

        for (Integer i : v) {
            if (i == null) {
                return false;
            }
        }

        return true;
    }

    private class Node {
        int v;
        int w;
        int vwLength;
        int ancestor;

        public Node(int v, int w, int ancestor, int vwLength) {
            this.v = v;
            this.w = w;
            this.ancestor = ancestor;
            this.vwLength = vwLength;
        }
    }


    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

