import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private String str;
    private int N;
    private int[] index;
    private int[] suffixArr;
    private final int CUTOFF = 15;   // cutoff to insertion sort
    private final int R = 256;   // extended ASCII alphabet size

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException();
        str = s;
        N = s.length();
        index = new int[N];
        suffixArr = new int[N];
        initializeIndex(index);
        initializeSuffixArr(suffixArr);
        sortSuffixArr(suffixArr, index);
    }

    private void initializeIndex(int[] index) {
        for (int i = 0; i < N; i++)
            index[i] = i;
    }

    private void initializeSuffixArr(int[] suffixArr) {
        initializeIndex(suffixArr);
    }

    private void sortSuffixArr(int[] suffixArr, int[] index) {
        int[] aux = new int[N];
        int[] indexAux = new int[N];
        sort(suffixArr, index, 0, N - 1, 0, aux, indexAux);
    }

    private void sort(int[] suffixArr, int[] index, int lo, int hi, int d, int[] aux, int[] indexAux) {
        if (hi <= lo + CUTOFF) {
            insertion(suffixArr, lo, hi, str, index);
            return;
        }

        int[] count = new int[R + 2];
        int[] indexCount = new int[R + 2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(str, suffixArr, i, d);
            count[c + 2]++;
            indexCount[c + 2]++;
        }

        for (int r = 0; r < R + 1; r++) {
            count[r + 1] += count[r];
            indexCount[r + 1] += indexCount[r];
        }


        for (int i = lo; i <= hi; i++) {
            int c = charAt(str, suffixArr, i, d);
            aux[count[c + 1]++] = suffixArr[i];
            indexAux[indexCount[c + 1]++] = index[i];
        }

        for (int i = lo; i <= hi; i++) {
            suffixArr[i] = aux[i - lo];
            index[i] = indexAux[i - lo];
        }

        for (int r = 0; r < R; r++)
            sort(suffixArr, index,lo + count[r], lo + count[r + 1] - 1, d + 1, aux, indexAux);
    }

    private int charAt(String str, int[] suffixArr, int suffixIndex, int d) {
        if (d == str.length()) return -1;
        int index = (suffixArr[suffixIndex] + d) % N;
        return str.charAt(index);
    }

    private boolean less(int v, int w, String str, int[] suffixArr) {
        for (int i = 0; i < N; i++) {
            if (charAt(str, suffixArr, v, i) > charAt(str, suffixArr, w, i)) {
                return false;
            } else if (charAt(str, suffixArr, v, i) < charAt(str, suffixArr, w, i)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    private void insertion(int[] suffixArr, int lo, int hi, String str, int[] index) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(j, j - 1, str, suffixArr); j--) {
                exch(index, j, j - 1);
                exch(suffixArr, j, j - 1);
            }
        }
    }

    // exchange a[i] and a[j]
    private void exch(int[] index, int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // length of s
    public int length() {
        return N;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= N)
            throw new IllegalArgumentException();

        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray suffixArray = new CircularSuffixArray(s);
        int N = s.length();
        for (int i = 0; i < N; i++)
            StdOut.println(suffixArray.index(i));
    }
}
