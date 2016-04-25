package grad.project;

/**
 * Created by AhmedA on 4/25/2016.
 */
public class WordRepresentation {
    public int position;
    public String word;
    public String partOfSpeech;
    public boolean isPredicate;
    public String[] argument;
    public int sentenceNumber;
    public int wordNumber;
    public String pred;

    public WordRepresentation(int position, String word, String partOfSpeech, boolean isPredicate, String[] argument, int sentenceNumber, int wordNumber, String pred) {
        this.position = position;
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.isPredicate = isPredicate;
        this.argument = argument;
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.pred = pred;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (String i : argument) sb.append(i + ", ");
        sb.append("]");
        return String.format("Position: [%s]\n" +
                        "Word: [%s]\nPart of Speech: [%s]\nPredicate?: [%s]\nPred: [%s]" +
                        "Argument?: [%s]\nSentence Number: [%s]\nWord Number: [%s]\n",
                position, word, partOfSpeech, isPredicate, pred, sb.toString(), sentenceNumber, wordNumber);
    }
}
