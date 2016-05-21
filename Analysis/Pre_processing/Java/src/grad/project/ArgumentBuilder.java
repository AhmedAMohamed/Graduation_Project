package grad.project;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AhmedA on 4/25/2016.
 */
public class ArgumentBuilder implements Cloneable {

    public static HashMap<String, Double> argConstants;

    public int sentenceNumber;
    public int wordNumber;
    public String partOfSpeech;
    public String word;
    public String[] argumentTypes;
    public String argumentType;
    public float score;
    public String ws;

    public ArgumentBuilder(int sentenceNumber, int wordNumber, String partOfSpeech, String word, String[] argumentTypes, String argumentType) {
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.score = 0;
        this.argumentTypes = argumentTypes;
        this.argumentType = argumentType;
    }

    public static void intializeConstants(String... args) {
        argConstants = new HashMap(args.length);
        for (int i = 0; i < args.length; i++) {
            argConstants.put(args[i], 1.5);
        }
    }

    public void setScore(ArrayList<FrameBuilder> frames) {
        for (FrameBuilder frame : frames) {
            for (String argType : frame.arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frame.arguments.get(argType);
                for(ArgumentBuilder arg: args) {
                    if(arg.word.equalsIgnoreCase(this.word)) {
                        score += argConstants.get(argType);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                "[ARGUMENT]: Word: [%s], Part of Speech: [%s], " +
                        "Argument Type: [%s], " +
                        "Sentence Number: [%s], Word Number: [%s]",
                word, partOfSpeech, argumentType, sentenceNumber, wordNumber);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new ArgumentBuilder(sentenceNumber, wordNumber, partOfSpeech, word, argumentTypes, argumentType);
    }
}
