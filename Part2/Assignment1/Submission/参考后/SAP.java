import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Queue;

public class SAP {

    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        RedBlackBST<Integer, Integer> vST = bfs(digraph, v);
        RedBlackBST<Integer, Integer> wST = bfs(digraph, w);

        int dist = -1;
        for (Integer vAncestor : vST.keys()) {
            if (wST.contains(vAncestor)) {
                int currentDist = wST.get(vAncestor) + vST.get(vAncestor);
                if (dist < 0 || currentDist < dist)
                    dist = currentDist;
            }
        }

        return dist;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        RedBlackBST<Integer, Integer> vST = bfs(digraph, v);
        RedBlackBST<Integer, Integer> wST = bfs(digraph, w);

        int dist = -1, anc = -1;
        for (Integer vAncestor : vST.keys()) {
            if (wST.contains(vAncestor)) {
                int currentDist = wST.get(vAncestor) + vST.get(vAncestor);
                if (dist < 0 || currentDist < dist) {
                    dist = currentDist;
                    anc = vAncestor;
                }
            }
        }
        return anc;
    }

    private RedBlackBST<Integer, Integer> bfs(Digraph digraph, int s) {
        RedBlackBST<Integer, Integer> ancestors = new RedBlackBST<>();
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(s);
        ancestors.put(s, 0);

        while (!queue.isEmpty()) {
            int head = queue.dequeue();
            int currentDist = ancestors.get(head);
            for (Integer w : digraph.adj(head)) {
                if (!ancestors.contains(w) || ancestors.get(w) > currentDist + 1) {
                    queue.enqueue(w);
                    ancestors.put(w, currentDist + 1);
                }
            }
        }

        return ancestors;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateIterable(v) || !validateIterable(w)) {
            throw new IllegalArgumentException();
        }
        int dist = -1;
        for (Integer eV : v) {
            for (Integer eW : w) {
                int currentDist = length(eV, eW);
                if (currentDist > 0 && (dist < 0 || currentDist < dist))
                    dist = currentDist;
            }
        }
        return dist;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateIterable(v) || !validateIterable(w)) {
            throw new IllegalArgumentException();
        }

        int dist = -1, anc = -1;
        for (Integer eV : v) {
            for (Integer eW : w) {
                int currentDist = length(eV, eW);
                if (currentDist > 0 && (dist < 0 || currentDist < dist)) {
                    dist = currentDist;
                    anc = ancestor(eV, eW);
                }
            }
        }
        return anc;
    }

    private boolean validateIterable(Iterable<Integer> v) {
        if (v == null) return false;
        for (Integer i : v) {
            if (i == null) return false;
        }
        return true;
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

