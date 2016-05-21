package grad.project;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AhmedA on 4/25/2016.
 */
public class FrameBuilder {

    private static HashMap<String, Double> argFrameConstants;
    public String pred;
    public int sentenceNumber;
    public int wordNumber;
    public String partOfSpeech;
    public String word;
    public HashMap<String, ArrayList<ArgumentBuilder>> arguments;
    public double score;
    public int index;


    public FrameBuilder(String pred, int sentenceNumber, int wordNumber, String partOfSpeech, String word, int index) {
        this.pred = pred;
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.arguments = new HashMap<>();
        this.index = index;
        this.score = 0;
    }

    public static void intializeConstants(String... args) {
        argFrameConstants = new HashMap(args.length);
        for (int i = 0; i < args.length; i++) {
            argFrameConstants.put(args[i], 1.5);
        }
    }

    public void addArgument(ArgumentBuilder arg) throws CloneNotSupportedException {
        ArgumentBuilder a = (ArgumentBuilder) arg.clone();
        a.argumentTypes = new String[] {arg.argumentTypes[index]};
        a.argumentType = arg.argumentTypes[index];
        ArrayList<ArgumentBuilder> list = new ArrayList();
        list.add(a);
        arguments.put(a.argumentType, list);
    }

    public void setScore() {
        for (String argType : this.arguments.keySet()) {
            ArrayList<ArgumentBuilder> args = this.arguments.get(argType);
            for (int i = 0; i < args.size(); i++) {
                this.score += (argFrameConstants.get(argType) * args.get(i).score);
            }
        }
    }

    public ArgumentBuilder findArgumentByType(String type) {
        return arguments.get(type).get(0);
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