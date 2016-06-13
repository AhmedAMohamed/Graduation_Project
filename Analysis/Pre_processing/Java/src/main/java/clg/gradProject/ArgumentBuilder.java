package clg.gradProject;

import java.util.HashMap;

/**
 * Created by AhmedA on 4/25/2016.
 */
public class ArgumentBuilder implements Cloneable {
    public int sentenceNumber;
    public int wordNumber;
    public String partOfSpeech;
    public String word;
    public String[] argumentTypes;
    public String argumentType;
    public String ws;

    public ArgumentBuilder(int sentenceNumber, int wordNumber, String partOfSpeech, String word, String[] argumentTypes, String argumentType) {
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.argumentTypes = argumentTypes;
        this.argumentType = argumentType;
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
