package grad.project.srl;


import grad.project.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static ArrayList<FrameBuilder> frames = new ArrayList<>();

    private static void buildDMRStepOne() throws IOException, CloneNotSupportedException {

        /*
        Here the first term part has to be called as it is and the SEPTS arraylist must be initialized
         */

        String fileName = "out_4.txt";
        String line = null;
        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader =
                new BufferedReader(fileReader);

        ArrayList<WordRepresentation> words = new ArrayList<>();
        int sentenceNumber = 1, wordNumber = 1;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split("\\s");
            int position = Integer.parseInt(parts[0]);
            String word = parts[1];
            String partOfSpeech = parts[4];
            boolean isPredicate = parts[12].equals("Y");
            String pred = parts[13];
            String[] argument = Arrays.copyOfRange(parts, 14, parts.length);
            WordRepresentation w = new WordRepresentation(position, word, partOfSpeech, isPredicate, argument, sentenceNumber, wordNumber, pred);
            words.add(w);
            wordNumber++;
            if (word.equals(".")) {
                sentenceNumber++;
                wordNumber = 1;
            }
        }
        ArrayList<ArgumentBuilder> arguments = new ArrayList<>();
        int count = 0;
        for (WordRepresentation w : words) {
            if (w.isPredicate) {
                FrameBuilder f = new FrameBuilder(w.pred, w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, count++);
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
                ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, w.argument, w.argument[argNumber]);
                arguments.add(a);
            }
        }

        for(ArgumentBuilder arg : arguments) {
            for(int i = 0; i < arg.argumentTypes.length; ++i) {
                if(!arg.argumentTypes[i].equals("_")) {
                    frames.get(i).addArgument(arg);
                }
            }
        }
    }

    private static void enchanceFrames() throws IOException, CloneNotSupportedException {
        buildDMRStepOne();
        /*
        Do first term here
         */
        for(int i = 0; i < frames.size(); i++) {
            for(String key : frames.get(i).arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frames.get(i).arguments.get(key);
                for(int j = 0; j < args.size(); j++) {
                    ArgumentBuilder word = args.get(j);
                    Node node = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(word.sentenceNumber), word.wordNumber);
                    word.word = node.ref.parseTreeNode.value();
                    word.ws = node.wordSense;
                }
            }
        }
    }

    public static DMRGraph generateTree() throws IOException, CloneNotSupportedException {
        enchanceFrames();
        DMRGraph graphBuilder = new DMRGraph(frames);
        graphBuilder.createGraph();

        return graphBuilder;
    }
}