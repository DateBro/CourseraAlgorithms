import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private int width;
    private int height;
    private Picture picture;
    private double[][] energyMatrix;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if(picture==null) throw new IllegalArgumentException();

        this.picture = picture;
        width = picture.width();
        height = picture.height();
        calculateEnergy();
    }

    private void calculateEnergy() {
        energyMatrix = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) throw new IllegalArgumentException();
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return 1000;

        double xGradient = xDualGradient(x, y);
        double yGradient = yDualGradient(x, y);
        double energy = Math.sqrt(xGradient + yGradient);

        return energy;
    }

    private double xDualGradient(int x,int y) {
        int p1RGB = picture.getRGB(x - 1, y);
        int[] p1RGBs = new int[3];
        p1RGBs[0] = (p1RGB & 0xff0000) >> 16;
        p1RGBs[1] = (p1RGB & 0xff00) >> 8;
        p1RGBs[2] = (p1RGB & 0xff);

        int p2RGB = picture.getRGB(x + 1, y);
        int[] p2RGBs = new int[3];
        p2RGBs[0] = (p2RGB & 0xff0000) >> 16;
        p2RGBs[1] = (p2RGB & 0xff00) >> 8;
        p2RGBs[2] = (p2RGB & 0xff);

        double rDual = (p1RGBs[0] - p2RGBs[0]) * (p1RGBs[0] - p2RGBs[0]);
        double gDual = (p1RGBs[1] - p2RGBs[1]) * (p1RGBs[1] - p2RGBs[1]);
        double bDual = (p1RGBs[2] - p2RGBs[2]) * (p1RGBs[2] - p2RGBs[2]);

        return rDual + gDual + bDual;
    }

    private double yDualGradient(int x,int y) {
        int p1RGB = picture.getRGB(x, y - 1);
        int[] p1RGBs = new int[3];
        p1RGBs[0] = (p1RGB & 0xff0000) >> 16;
        p1RGBs[1] = (p1RGB & 0xff00) >> 8;
        p1RGBs[2] = (p1RGB & 0xff);

        int p2RGB = picture.getRGB(x, y + 1);
        int[] p2RGBs = new int[3];
        p2RGBs[0] = (p2RGB & 0xff0000) >> 16;
        p2RGBs[1] = (p2RGB & 0xff00) >> 8;
        p2RGBs[2] = (p2RGB & 0xff);

        double rDual = (p1RGBs[0] - p2RGBs[0]) * (p1RGBs[0] - p2RGBs[0]);
        double gDual = (p1RGBs[1] - p2RGBs[1]) * (p1RGBs[1] - p2RGBs[1]);
        double bDual = (p1RGBs[2] - p2RGBs[2]) * (p1RGBs[2] - p2RGBs[2]);

        return rDual + gDual + bDual;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width];
        double[][] minEnergy = new double[width][height];
        int[][] nextStep = new int[width][height];
        for (int y = 0; y < height; y++) {
            dfsShortestPath(0, y, false, minEnergy, nextStep);
        }
        double seamEnergy = Double.POSITIVE_INFINITY;
        for (int y = 0; y < height; y++) {
            if (seamEnergy > minEnergy[0][y]) {
                seamEnergy = minEnergy[0][y];
                seam[0] = y;
            }
        }
        for (int x = 1; x < width; x++) {
            seam[x] = nextStep[x - 1][seam[x - 1]];
        }
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height];
        double[][] minEnergy = new double[width][height];
        int[][] nextStep = new int[width][height];
        for (int x = 0; x < width; x++) {
            dfsShortestPath(x, 0, true, minEnergy, nextStep);
        }
        double seamEnergy = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width; x++) {
            if (seamEnergy > minEnergy[x][0]) {
                seamEnergy = minEnergy[x][0];
                seam[0] = x;
            }
        }
        for (int y = 1; y < height; y++) {
            seam[y] = nextStep[seam[y - 1]][y - 1];
        }
        return seam;
    }

    private void dfsShortestPath(int x, int y, boolean vertical, double[][] minEnergy, int[][] nextStep) {
        if ((vertical && y == height - 1) || (!vertical && x == width - 1)) {
            minEnergy[x][y] = energyMatrix[x][y];
            nextStep[x][y] = -1;
            return;
        }
        double shortestPath = Double.POSITIVE_INFINITY;
        int bestMove = 0;
        for (int i = -1; i <= 1; i++) {
            if (vertical) {
                int nextX = x + i;
                if (nextX < 0 || nextX > width - 1)
                    continue;
                if (nextStep[nextX][y + 1] == 0)
                    dfsShortestPath(nextX, y + 1, vertical, minEnergy, nextStep);
                if (minEnergy[nextX][y + 1] < shortestPath) {
                    shortestPath = minEnergy[nextX][y + 1];
                    bestMove = nextX;
                }
            } else {
                int nextY = y + i;
                if (nextY < 0 || nextY > height - 1)
                    continue;
                if (nextStep[x + 1][nextY] == 0)
                    dfsShortestPath(x + 1, nextY, vertical, minEnergy, nextStep);
                if (minEnergy[x + 1][nextY] < shortestPath) {
                    shortestPath = minEnergy[x + 1][nextY];
                    bestMove = nextY;
                }
            }
        }
        nextStep[x][y] = bestMove;
        minEnergy[x][y] = energyMatrix[x][y] + shortestPath;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam==null || height <= 1 || seam.length != width) throw new IllegalArgumentException();
        if (!validateSeam(seam,false)) throw new IllegalArgumentException();

        Picture pic = new Picture(width, --height);
        for (int y = 0; y < height + 1; y++) {
            for (int x = 0; x < width; x++) {
                if (seam[x] > y) {
                    pic.setRGB(x, y, picture.getRGB(x, y));
                } else if (seam[x] < y) {
                    pic.setRGB(x, y - 1, picture.getRGB(x, y));
                }
            }
        }

        picture = pic;
        calculateEnergy();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || width <= 1 || seam.length != height)
            throw new IllegalArgumentException();
        if (!validateSeam(seam,true)) throw new IllegalArgumentException();

        Picture pic = new Picture(--width, height);
        for (int x = 0; x < width + 1; x++) {
            for (int y = 0; y < height; y++) {
                if (seam[y] > x) {
                    pic.setRGB(x, y, picture.getRGB(x, y));
                } else if (seam[y] < x) {
                    pic.setRGB(x - 1, y, picture.getRGB(x, y));
                }
            }
        }

        picture = pic;
        calculateEnergy();
    }

    private boolean validateSeam(int[] seam, boolean vertical) {
        int N = seam.length;
        for (int i = 0; i < N; i++) {
            if (vertical) {
                if (seam[i] < 0 || seam[i] > width)
                    return false;

                if (i < N - 1 && Math.abs(seam[i] - seam[i + 1]) > 1)
                    return false;
            } else {
                if (seam[i] < 0 || seam[i] > height)
                    return false;

                if (i < N - 1 && Math.abs(seam[i] - seam[i + 1]) > 1)
                    return false;
            }
        }

        return true;
    }
}