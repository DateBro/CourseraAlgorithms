import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * @author DateBro
 */

public class PercolationStats {

    private double x_mean;
    private double[] x_all_grid;
    private double stddev = 0;
    private int expNum;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        expNum = trials;
        x_all_grid = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation test = new Percolation(n);
            while (!test.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                if (test.isOpen(row, col)) {
                    continue;
                } else {
                    test.open(row, col);
                }
            }
            x_all_grid[i] = (test.numberOfOpenSites() + 0.0) / (n * n);
        }

        x_mean = StdStats.mean(x_all_grid);
    }

    // sample mean of percolation threshold
    public double mean() {
        return x_mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        stddev = StdStats.stddev(x_all_grid);
        return stddev;
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return (x_mean - (1.96 / Math.sqrt(expNum)));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (x_mean + (1.96 / Math.sqrt(expNum)));
    }

    // test client (described below)
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(N, T);
        StdOut.printf("mean = %f\n", stats.mean());
        StdOut.printf("stddev = %f\n", stats.stddev());
        StdOut.printf("95%% confidence interval = %f, %f\n", stats.confidenceLo(), stats.confidenceHi());
    }
}
