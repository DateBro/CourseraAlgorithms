import edu.princeton.cs.algs4.Queue;

/**
 * @author DateBro
 */

public class Board {

    private int[][] board;
    private int dimension;
    private int hammingDistance;
    private int manhattanDistance;
    private Queue<Board> neighbors;
    private int blankRow;
    private int blankColumn;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        dimension = blocks.length;
        neighbors = new Queue<>();
        hammingDistance = 0;
        manhattanDistance = 0;
        board = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                board[i][j] = blocks[i][j];
                if (blocks[i][j] == 0) {
                    blankRow = i;
                    blankColumn = j;
                }
            }
        }
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of blocks out of place
    public int hamming() {
        hammingDistance = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != 0 && board[i][j] != i * dimension + j + 1) {
                    hammingDistance++;
                }
            }
        }
        return hammingDistance;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        manhattanDistance = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != 0) {
                    int rightRow = (board[i][j] - 1) / dimension;
                    int rightColumn = (board[i][j] - 1) % dimension;
                    manhattanDistance += Math.abs(i - rightRow) + Math.abs(j - rightColumn);
                }
            }
        }
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (manhattanDistance != 0){ return false;}
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != 0 && board[i][j] != i * dimension + j + 1) {
                    return false;
                }
            }
        }
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        int[][] twinBlocks = new int[dimension][dimension];
        copyArr(board, twinBlocks);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i != blankRow || j != blankColumn) {
                    if (i - 1 >= 0 && (j != blankColumn || i - 1 != blankRow)) {
                        exch(twinBlocks, i, j, i - 1, j);
                        return new Board(twinBlocks);
                    }
                    if (i + 1 < dimension && (j != blankColumn || i + 1 != blankRow)) {
                        exch(twinBlocks, i, j, i + 1, j);
                        return new Board(twinBlocks);
                    }
                    if ((j - 1) >= 0 && (i != blankRow || j - 1 != blankColumn)) {
                        exch(twinBlocks, i, j, i, j - 1);
                        return new Board(twinBlocks);
                    }
                    if (j + 1 < dimension && (i != blankRow && j + 1 != blankRow)) {
                        exch(twinBlocks, i, j, i, j + 1);
                        return new Board(twinBlocks);
                    }
                }
            }
        }

        return new Board(twinBlocks);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y==null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board yBoard = (Board) y;
        int[][] yBlocks = new int[yBoard.dimension][yBoard.dimension];
        copyArr(yBoard.board, yBlocks);
        if (dimension != yBoard.dimension() || manhattan() != yBoard.manhattan()
                || hamming() != yBoard.hamming()) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != yBlocks[i][j]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        if (blankRow >= 0 && blankRow < dimension && (blankColumn - 1) >= 0 && blankColumn < dimension) {
            neighbors.enqueue(neighborBoard(board, blankRow, blankColumn, blankRow, (blankColumn - 1)));
        }
        if (blankRow >= 0 && blankRow < dimension && blankColumn >= 0 && blankColumn + 1 < dimension) {
            neighbors.enqueue(neighborBoard(board, blankRow, blankColumn, blankRow, (blankColumn + 1)));
        }
        if (blankRow - 1 >= 0 && blankColumn >= 0 && blankColumn < dimension) {
            neighbors.enqueue(neighborBoard(board, blankRow, blankColumn, (blankRow - 1), blankColumn));
        }
        if (blankRow + 1 < dimension && blankColumn >= 0 && blankColumn < dimension) {
            neighbors.enqueue(neighborBoard(board, blankRow, blankColumn, (blankRow + 1), blankColumn));
        }
        return neighbors;
    }

    private static Board neighborBoard(int[][] arr, int row1, int column1, int row2, int column2) {
        int N = arr.length;
        int[][] neighborBoard = new int[N][N];
        copyArr(arr, neighborBoard);
        int temp = neighborBoard[row1][column1];
        neighborBoard[row1][column1] = neighborBoard[row2][column2];
        neighborBoard[row2][column2] = temp;
        return new Board(neighborBoard);
    }

    private static void copyArr(int[][] originArr, int[][] newArr) {
        int N = originArr.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                newArr[i][j] = originArr[i][j];
            }
        }
    }

    private static void exch(int[][] arr, int row1, int column1, int row2, int column2) {
        int temp = arr[row1][column1];
        arr[row1][column1] = arr[row2][column2];
        arr[row2][column2] = temp;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%2d ", board[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args) {

    }
}
