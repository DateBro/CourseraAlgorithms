import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WordNet {

    private Digraph digraph;
    private int synsetsNum;
    private SAP sap;
    private Map<String, List<Integer>> dict = new HashMap<String, List<Integer>>();
    private Map<Integer, String> reverseDict = new HashMap<Integer, String>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();

        synsetsNum = 0;
        readInSynset(synsets);
        readInHypernym(hypernyms);
        sap = new SAP(digraph);
    }

    private void readInSynset(String synsets) {
        In in = new In(synsets);
        Queue<String> queue = new Queue<>();

        while (in.hasNextLine()) {
            String str = in.readLine();
            String[] arr = str.split(",");
            queue.enqueue(arr[1]);

            String nouns[] = arr[1].split(" ");
            List<Integer> nounList = null;
            for (int i = 0; i < nouns.length; i++) {
                if (!dict.containsKey(nouns[i]))
                    nounList = new LinkedList<Integer>();
                else
                    nounList = dict.get(nouns[i]);
                nounList.add(Integer.parseInt(arr[0]));
                dict.put(nouns[i], nounList);
            }
            reverseDict.put(Integer.parseInt(arr[0]), arr[1]);
            synsetsNum++;
        }
    }

    private void readInHypernym(String hypernyms) {
        In in = new In(hypernyms);
        Queue<Node> queue = new Queue<>();

        while (in.hasNextLine()) {
            String str = in.readLine();
            String[] arr = str.split(",");
            int inV = Integer.parseInt(arr[0]);
            for (int i = 1; i < arr.length; i++) {
                int toV = Integer.parseInt(arr[i]);
                Node node = new Node(inV, toV);
                queue.enqueue(node);
            }
        }

        digraph = new Digraph(synsetsNum);

        while (!queue.isEmpty()) {
            Node node = queue.dequeue();
            digraph.addEdge(node.inVertex, node.toVertex);
        }
    }

    private class Node {
        int inVertex;
        int toVertex;
        public Node(int inVertex, int toVertex) {
            this.inVertex = inVertex;
            this.toVertex = toVertex;
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return dict.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word==null) throw new IllegalArgumentException();

        return dict.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();

        return sap.length(dict.get(nounA), dict.get(nounB));
    }

    // a synset (second field of synsetST.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        int id = sap.ancestor(dict.get(nounA), dict.get(nounB));
        return reverseDict.get(id);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            System.out.println(wordnet.sap(nounA, nounB));
        }
    }
}
