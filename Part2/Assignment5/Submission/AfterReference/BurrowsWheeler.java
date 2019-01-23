import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Merge;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder str = new StringBuilder();
        while (!BinaryStdIn.isEmpty())
            str = str.append(BinaryStdIn.readChar());
        CircularSuffixArray suffixArray = new CircularSuffixArray(str.toString());
        int N = str.length();
        for (int i = 0; i < suffixArray.length(); i++) {
            if (suffixArray.index(i) == 0) {
                BinaryStdOut.write(i, 32);
                break;
            }
        }
        for (int i = 0; i < suffixArray.length(); i++)
            BinaryStdOut.write(str.charAt((suffixArray.index(i) + N - 1) % N));
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
//        StringBuilder str = new StringBuilder();
//        int first = BinaryStdIn.readInt(32);
//        while (!BinaryStdIn.isEmpty())
//            str = str.append(BinaryStdIn.readChar());
//        int N = str.length();
//        Character[] head = new Character[N];
//        initializeTHead(head, str);
//        Merge.sort(head);
//        int next[] = new int[N];
//        initializeNext(next, head, str);
//        outputRealMessage(first, next, head);
//        BinaryStdIn.close();

        StringBuilder str = new StringBuilder();
        int s = BinaryStdIn.readInt(32);
        while (!BinaryStdIn.isEmpty())
            str = str.append(BinaryStdIn.readChar());
        int next[] = new int[str.length()];
        // System.out.println(s+" "+str.length());
        // build next array
        int[] index = argsort(str.toString().toCharArray(), true);
        for (int i = 0; i < str.length(); i++) {
            next[i] = index[i];
            // System.out.println(next[i]);
        }

        // output
        int idx = 0;
        int pt = next[s];
        while (idx < str.length() - 1) {
            BinaryStdOut.write(str.charAt(pt));
            // System.out.print(str.charAt(pt));
            pt = next[pt];
            idx++;
        }
        BinaryStdOut.write(str.charAt(s));
        // System.out.print(str.charAt(s));
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    private static void outputRealMessage(int first, int[] next, Character[] head) {
        int N = next.length;
        int nextIndex = first;
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(head[nextIndex], 8);
            nextIndex = next[nextIndex];
        }
        BinaryStdOut.close();
    }

    private static void initializeTHead(Character[] head, StringBuilder str) {
        int N = str.length();
        for (int i = 0; i < N; i++)
            head[i] = str.charAt(i);
    }

    private static void initializeNext(int[] next,Character[] head,StringBuilder str) {
        int nextIndex = 0;
        int N = str.length();
        boolean[] marked = new boolean[N];
        for (int i = 0; i < N; i++) {
            char currentHead = head[i];
            for (int j = 0; j < N; j++) {
                if (!marked[j] && currentHead == str.charAt(j) && j != i) {
                    marked[j] = true;
                    nextIndex = j;
                    break;
                }
            }
            next[i] = nextIndex;
        }
    }

    private static int[] argsort(final char[] a, final boolean ascending) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Float.compare(a[i1], a[i2]);
            }
        });
        return asArray(indexes);
    }

    @SafeVarargs
    private static <T extends Number> int[] asArray(final T... a) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i].intValue();
        }
        return b;
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
        else{
            return;
        }
    }
}
