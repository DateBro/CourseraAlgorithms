import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.Queue;

public class WordNet {

    private Digraph digraph;
    private int synsetsNum;
    private Queue<String> nounsQueue;
    private String[] synsetArray;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();

        readInSynset(synsets);
        readInHypernym(hypernyms);
    }

    private void readInSynset(String synsets) {
        In in = new In(synsets);
        Queue<String> queue = new Queue<>();

        while (in.hasNextLine()) {
            synsetsNum++;
            String str = in.readLine();
            String[] arr = str.split(",");
            queue.enqueue(arr[1]);
        }

        synsetArray = new String[synsetsNum];

        for (int i = 0; i < synsetsNum; i++) {
            String str = queue.dequeue();
            synsetArray[i] = str;
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
        if (nounsQueue == null) {
            nounsQueue = new Queue<>();
            for (int i = 0; i < synsetsNum; i++) {
                String synset = synsetArray[i];
                String[] strArr = synset.split("\\s");
                for (int j = 0; j < strArr.length; j++) {
                    String noun = strArr[j];
                    nounsQueue.enqueue(noun);
                }
            }
        }
        return nounsQueue;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word==null) throw new IllegalArgumentException();

        return contains(word);
    }

    private boolean contains(String word) {
        for (int i = 0; i < synsetsNum; i++) {
            String synset = synsetArray[i];
            String[] strArr = synset.split("\\s");
            for (int j = 0; j < strArr.length; j++) {
                String noun = strArr[j];
                if (noun.equals(word)) {
                    return true;
                }
            }
        }

        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA==null || nounB==null || !contains(nounA) || !contains(nounB)) throw new IllegalArgumentException();

        Queue<Integer> aIdQueue = getNounInteger(nounA);
        Queue<Integer> bIdQueue = getNounInteger(nounB);

        if (sap == null) {
            sap = new SAP(digraph);
        }
        int length = sap.length(aIdQueue, bIdQueue);
        return length;
    }

    private Queue<Integer> getNounInteger(String noun) {
        Queue<Integer> idQueue = new Queue<>();
        for (int i = 0; i < synsetsNum; i++) {
            String synset = synsetArray[i];
            String[] strArr = synset.split("\\s");
            for (int j = 0; j < strArr.length; j++) {
                String word = strArr[j];
                if (noun.equals(word)) {
                    idQueue.enqueue(i);
                    break;
                }
            }
        }
        return idQueue;
    }

    // a synset (second field of synsetST.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if(nounA==null || nounB==null) throw new IllegalArgumentException();

        Queue<Integer> aIdQueue = getNounInteger(nounA);
        Queue<Integer> bIdQueue = getNounInteger(nounB);

        if (sap == null) {
            sap = new SAP(digraph);
        }
        int ancestor = sap.ancestor(aIdQueue, bIdQueue);
        if (ancestor == -1) {
            return synsetArray[0];
        }
        return synsetArray[ancestor];
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
