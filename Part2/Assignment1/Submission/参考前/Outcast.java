import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDistance = 0;
        String outcastNoun = "";
        int N = nouns.length;
        for (int i = 0; i < N; i++) {
            int distance = 0;
            for (int j = 0; j < N; j++) {
                distance += wordNet.distance(nouns[i], nouns[j]);
            }
            if (distance > maxDistance) {
                maxDistance = distance;
                outcastNoun = nouns[i];
            }
        }

        return outcastNoun;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
