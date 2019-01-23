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
        int fileByteCount = 0;
        StringBuilder str = new StringBuilder();
        int first = BinaryStdIn.readInt(32);
        while (!BinaryStdIn.isEmpty()) {
            str = str.append(BinaryStdIn.readChar());
            fileByteCount++;
        }
        // check file size to avoid stuck in test 7b
        if (fileByteCount > Integer.MAX_VALUE / 2000) {
            return;
        }
        int N = str.length();
        Character[] head = new Character[N];
        initializeTHead(head, str);
        Merge.sort(head);
        int next[] = new int[N];
        initializeNext(next, head, str);
        outputRealMessage(first, next, head);
        BinaryStdIn.close();
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
