import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * @author DateBro
 * 这个 assignment 有参考过 GitHub 上的代码，当时没想到自己实现合适的数据结构太傻了...
 */

public class Solver {

    private MinPQ<Node> pq;
    private Node node;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        pq = new MinPQ<>();
        pq.insert(new Node(initial, null));

        MinPQ<Node> twinPq = new MinPQ<>();
        twinPq.insert(new Node(initial.twin(), null));

        boolean solvable = false;
        boolean twinSolvable = false;

        while (!solvable && !twinSolvable) {
            node = expand(pq);
            solvable = (node != null);
            twinSolvable = (expand(twinPq) != null);
        }
    }

    private Node expand(MinPQ<Node> pq) {
        if (pq.isEmpty()) return null;
        Node min = pq.delMin();
        if (min.board.isGoal()) return min;

        for (Board neighbor : min.board.neighbors()) {
            if (min.predecessor == null || !neighbor.equals(min.predecessor.board)) {
                pq.insert(new Node(neighbor, min));
            }
        }

        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return node != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (node == null) return -1;

        return node.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (node == null){ return null;}

        Stack<Board> stack = new Stack<>();
        Node temp = new Node(node.board, node.predecessor);
        while (temp != null) {
            stack.push(temp.board);
            temp = temp.predecessor;
        }

        return stack;
    }

    private class Node implements Comparable<Node> {
        private Board board;
        private int moves;
        private Node predecessor;
        public Node(Board board, Node predecessor){
            this.board = board;
            this.predecessor = predecessor;
            if (predecessor == null) {
                this.moves = 0;
            } else {
                this.moves = predecessor.moves + 1;
            }
        }
        @Override
        public int compareTo(Node o) {
            return (this.board.manhattan() - o.board.manhattan()) + (this.moves - o.moves);
        }

    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
