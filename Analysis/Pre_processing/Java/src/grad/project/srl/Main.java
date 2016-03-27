package grad.project.srl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

class Word {
    int position;
    String word;
    String partOfSpeech;
    boolean isPredicate;
    String[] argument;
    int sentenceNumber;
    int wordNumber;
    String pred;

    Word(int position, String word, String partOfSpeech, boolean isPredicate, String[] argument, int sentenceNumber, int wordNumber, String pred) {
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

public class Main {

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        String fileName = "out_4.txt";
        String line = null;
        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader =
                new BufferedReader(fileReader);

        ArrayList<Word> words = new ArrayList<>();
        int sentenceNumber = 1, wordNumber = 1;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split("\\s");
            int position = Integer.parseInt(parts[0]);
            String word = parts[1];
            String partOfSpeech = parts[4];
            boolean isPredicate = parts[12].equals("Y");
            String pred = parts[13];
            String[] argument = Arrays.copyOfRange(parts, 14, parts.length);
            Word w = new Word(position, word, partOfSpeech, isPredicate, argument, sentenceNumber, wordNumber, pred);
            words.add(w);
            wordNumber++;
            if (word.equals(".")) {
                sentenceNumber++;
                wordNumber = 1;
            }
        }

        ArrayList<Frame> frames = new ArrayList<>();
        ArrayList<Argument> arguments = new ArrayList<>();

        int count = 0;
        for (Word w : words) {
            if (w.isPredicate) {
                Frame f = new Frame(w.pred, w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, count++);
                frames.add(f);
            }
            boolean isArgument = false;
            int argNumber = 0;
            for (int i = 0; i < w.argument.length; ++i) {
                if (!w.argument[i].equals("_")) {
                    isArgument = true;
                    argNumber = i;
                    break;
                }
            }
            if (isArgument) {
                Argument a = new Argument(w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, w.argument, w.argument[argNumber]);
                arguments.add(a);
            }
        }

        for(Argument arg : arguments) {
            for(int i = 0; i < arg.argumentTypes.length; ++i) {
                if(!arg.argumentTypes[i].equals("_")) {
                    frames.get(i).addArgument(arg);
                }
            }
        }

        frames.forEach(System.out::println);
    }
}

class Frame {

    String pred;
    int sentenceNumber;
    int wordNumber;
    String partOfSpeech;
    String word;
    ArrayList<Argument> arguments;
    int index;

    Frame(String pred, int sentenceNumber, int wordNumber, String partOfSpeech, String word, int index) {
        this.pred = pred;
        this.sentenceNumber = sentenceNumber;
        this.wordNumber = wordNumber;
        this.partOfSpeech = partOfSpeech;
        this.word = word;
        this.arguments = new ArrayList<>();
        this.index = index;
    }

    public void addArgument(Argument arg) throws CloneNotSupportedException {
        Argument a = (Argument) arg.clone();
        a.argumentTypes = new String[] {arg.argumentTypes[index]};
        a.argumentType = arg.argumentTypes[index];
        arguments.add(a);
    }

    public Argument findArgumentByType(String type) {
        for (Argument arg : arguments)
            if (arg.argumentType.equals(type)) return arg;
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Argument i : arguments) sb.append(i + ", ");
        sb.append("]");
        return String.format(
                "[FRAME]: Word: [%s]\nPart of Speech: [%s]\nPred: [%s]\n" +
                        "Arguments: %s\nSentence Number: [%s]\nWord Number: [%s]\n",
                word, partOfSpeech, pred, sb.toString(), sentenceNumber, wordNumber);
    }


}

class Argument implements Cloneable {
    int sentenceNumber;
    int wordNumber;
    String partOfSpeech;
    String word;
    String[] argumentTypes;
    String argumentType;

    public Argument(int sentenceNumber, int wordNumber, String partOfSpeech, String word, String[] argumentTypes, String argumentType) {
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
    protected Object clone() throws CloneNotSupportedException {
        return new Argument(sentenceNumber, wordNumber, partOfSpeech, word, argumentTypes, argumentType);
    }
}