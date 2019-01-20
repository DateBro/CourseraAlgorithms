import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;

public class BoggleSolver {

//    private TrieSET dictionarySet;
    private SET<String> validWords;
    private Stack<String> allPaths;
    private TreeSet dictionaryTree;
    private BoggleBoard lastBoard = null;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        validWords = new SET<>();
        allPaths = new Stack<>();
        dictionaryTree = new TreeSet();
        int N = dictionary.length;
        for (int i = 0; i < N; i++) {
            dictionaryTree.add(dictionary[i]);
        }
    }

    // Returns the set of all valid words in the given Boggle lastBoard, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board != lastBoard) 
            dfsValidWords(board);
        return validWords;
    }

    private void dfsValidWords(BoggleBoard board) {
        validWords = new SET<>();
        allPaths = new Stack<>();
        int row = board.rows();
        int col = board.cols();
        boolean[][] marked;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                marked = new boolean[row][col];
                allPaths = new Stack<>();
                dfsValidWords(board, i, j, marked);
            }
        }
    }

    private void dfsValidWords(BoggleBoard board, int startRow, int startCol, boolean[][] marked) {
        if (startRow < 0 || startCol < 0 || startCol >= board.cols() || startRow >= board.rows() || marked[startRow][startCol])
            return;
        marked[startRow][startCol] = true;
        char letter = board.getLetter(startRow, startCol);
        if (letter == 'Q') {
            allPaths.push("QU");
        } else {
            allPaths.push(String.valueOf(letter));
        }
        String currentPath = "";
        for (String i : allPaths)
            currentPath = i + currentPath;

        if (dictionaryTree.contains(currentPath) && currentPath.length() >= 3)
            validWords.add(currentPath);

        if (!isPrefixOfDictionary(currentPath)) {
            allPaths.pop();
            marked[startRow][startCol] = false;
            return;
        }

        for (int i = 1; i >= -1; i--) {
            for (int j = 1; j >= -1; j--) {
                int nextRow = startRow + i;
                int nextCol = startCol + j;
                dfsValidWords(board, nextRow, nextCol, marked);
            }
        }

        allPaths.pop();
        marked[startRow][startCol] = false;
    }

    private boolean isPrefixOfDictionary(String str) {
        String prefixStr = (String) dictionaryTree.ceiling(str);
        if (prefixStr != null && prefixStr.length() >= str.length() && (prefixStr.substring(0, str.length())).equals(str))
            return true;
        return false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictionaryTree.contains(word)) {
            int N = word.length();
            if (N > 0 && N <= 2) {
                return 0;
            } else if (N >= 3 && N <= 4) {
                return 1;
            } else if (N == 5) {
                return 2;
            } else if (N == 6) {
                return 3;
            } else if (N == 7) {
                return 5;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;

//        SET<String> set = (SET<String>) solver.getAllValidWords(lastBoard);
//        StdOut.println(set.size());

        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
