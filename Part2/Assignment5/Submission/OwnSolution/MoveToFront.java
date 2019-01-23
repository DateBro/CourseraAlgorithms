import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] radix = new char[R];
        initializeRadix(radix);
        int N = radix.length;
        while (!BinaryStdIn.isEmpty()) {
            char inChar = BinaryStdIn.readChar(8);
            int inCharIndex = 0;
            for (int i = 0; i < N; i++) {
                if (radix[i] == inChar) {
                    BinaryStdOut.write(i, 8);
                    inCharIndex = i;
                    break;
                }
            }
            changeRadixArr(radix, inChar, inCharIndex);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    private static void changeRadixArr(char[] radix, char inChar, int inCharIndex) {
        for (int i = inCharIndex; i > 0; i--)
            radix[i] = radix[i-1];
        radix[0] = inChar;
    }

    private static void initializeRadix(char[] radix) {
        for (char i = 0; i < R; i++)
            radix[i] = i;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] radix = new char[R];
        initializeRadix(radix);
        while (!BinaryStdIn.isEmpty()) {
            int inChar = BinaryStdIn.readChar(8);
            BinaryStdOut.write(radix[inChar], 8);
            changeRadixArr(radix, inChar);
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    private static void changeRadixArr(char[] radix, int inChar) {
        char toFrontChar = radix[inChar];
        for (int i = inChar; i > 0; i--)
            radix[i] = radix[i-1];
        radix[0] = toFrontChar;
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            return;
        }
    }
}