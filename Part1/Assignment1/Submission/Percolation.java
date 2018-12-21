import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * @author DateBro
 */

public class Percolation {

    private int[][] grid;
    private WeightedQuickUnionUF grid1D;
    private int size;
    private int openSitesNum = 0;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        size = n;
        grid = new int[n][n];
        grid1D = new WeightedQuickUnionUF(n * n + 2);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = -1;
            }
        }
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!validateIndicies(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        if (!isOpen(row, col)) {
            grid[row - 1][col - 1] = 0;
            openSitesNum++;

            if (row == 1) {
                // 和虚拟的 top 相连
                grid1D.union(size * size, xyTo1D(row, col));
                full(row, col);
            }  else {
                grid[row - 1][col - 1] = 0;
                if (row == size) {
                    // 和虚拟的 bottom 相连
                    grid1D.union(size * size + 1, xyTo1D(row, col));
                }

                if (validateIndicies(row, col - 1) && isFull(row, col - 1)) {
                    grid1D.union(xyTo1D(row, col), xyTo1D(row, col - 1));
                    full(row, col);
                }
                if (validateIndicies(row, col + 1) && isFull(row, col + 1)) {
                    grid1D.union(xyTo1D(row, col), xyTo1D(row, col + 1));
                    full(row, col);
                }
                if (validateIndicies(row + 1, col) && isFull(row + 1, col)) {
                    grid1D.union(xyTo1D(row + 1, col), xyTo1D(row + 1, col));
                    full(row, col);
                }
                if (validateIndicies(row - 1, col) && isFull(row - 1, col)) {
                    grid1D.union(xyTo1D(row, col), xyTo1D(row - 1, col));
                    full(row, col);
                }
            }
        }

    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        return grid[row - 1][col - 1] >= 0;
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        return grid[row - 1][col - 1] == 1;
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openSitesNum;
    }

    // does the system percolate?
    public boolean percolates() {
        return grid1D.connected(size * size, size * size + 1);
    }

    private int xyTo1D(int x, int y) {
        return (x - 1) * size + (y - 1);
    }

    private boolean validateIndicies(int x, int y) {
        if (x < 1 || y < 1 || x > size || y > size) {
            return false;
        }
        return true;
    }

    private void full(int row, int col) {
        if (validateIndicies(row, col) && grid[row - 1][col - 1] == 0) {
            grid[row - 1][col - 1] = 1;

            if (validateIndicies(row, col - 1) && isOpen(row, col - 1)) {
                grid1D.union(xyTo1D(row, col), xyTo1D(row, col - 1));
                full(row, col - 1);
            }
            if (validateIndicies(row, col + 1) && isOpen(row, col + 1)) {
                grid1D.union(xyTo1D(row, col), xyTo1D(row, col + 1));
                full(row, col + 1);
            }
            if (validateIndicies(row + 1, col) && isOpen(row + 1, col)) {
                grid1D.union(xyTo1D(row + 1, col), xyTo1D(row + 1, col));
                full(row + 1, col);
            }
            if (validateIndicies(row - 1, col) && isOpen(row - 1, col)) {
                grid1D.union(xyTo1D(row, col), xyTo1D(row - 1, col));
                full(row - 1, col);
            }
        } else {
            return;
        }
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation test = new Percolation(20);
        while (!test.percolates()) {
            int row = StdRandom.uniform(1, 21);
            int col = StdRandom.uniform(1, 21);
            if (test.isOpen(row, col)) {
                continue;
            } else {
                test.open(row, col);
            }
        }

        System.out.println("Estimate percolation");
        System.out.println(test.numberOfOpenSites());
    }
}
