package clg.gradProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by AhmedA on 4/25/2016.
 */
public class FrameBuilder {

    public String frameID;
    public String pred;
    public int sentenceNumber;
    public int wordNumber;
    public String partOfSpeech;
    public String word;
    public HashMap<String, ArrayList<ArgumentBuilder>> arguments;
    public int index;
    public double score;


    public FrameBuilder(String pred, int sentenceNumber, int wordNumber, String partOfSpeech, String word, int index) {
        this.pred = pred;
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.arguments = new HashMap<String, ArrayList<ArgumentBuilder>>();
        this.index = index;
        this.score = 0;
        this.frameID = UUID.randomUUID().toString();
    }

    public void addArgument(ArgumentBuilder arg) throws CloneNotSupportedException {
        ArgumentBuilder a = (ArgumentBuilder) arg.clone();
        a.argumentTypes = new String[] {arg.argumentTypes[index]};
        a.argumentType = arg.argumentTypes[index];
        ArrayList<ArgumentBuilder> list = arguments.get(a.argumentType);
        if(list == null) list = new ArrayList<>();
        list.add(a);
        arguments.put(a.argumentType, list);
    }

    public ArgumentBuilder findArgumentByType(String type) {
        return arguments.get(type).get(0);
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (ArrayList<ArgumentBuilder> i : arguments.values()) sb.append(i.get(0) + ", ");
        sb.append("]");
        return String.format(
                "[FRAME]: Word: [%s]\nPart of Speech: [%s]\nPred: [%s]\n" +
                        "Arguments: %s\nSentence Number: [%s]\nWord Number: [%s]\n",
                word, partOfSpeech, pred, sb.toString(), sentenceNumber, wordNumber);
    }
}
