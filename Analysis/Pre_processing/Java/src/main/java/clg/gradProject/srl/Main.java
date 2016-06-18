package clg.gradProject.srl;


import clg.gradProject.*;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static ArrayList<FrameBuilder> frames = new ArrayList<FrameBuilder>();

    private static CompletePipeline completePipeline = null;
    private static CompletePipelineCMDLineOptions completePipelineCMDLineOptions;

    private static void buildDMRStepOne(String input) throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {

        if (completePipeline == null) {
            completePipelineCMDLineOptions = new CompletePipelineCMDLineOptions();
            completePipelineCMDLineOptions.parseCmdLineArgs(CompletePipeline.pipelineOptions);

            completePipeline = CompletePipeline.getCompletePipeline(completePipelineCMDLineOptions);
        }

        InputStream is = new ByteArrayInputStream(input.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String srl_output = CompletePipeline.parseCoNLL09(completePipelineCMDLineOptions, completePipeline, bufferedReader, null) + "\n";
        System.out.println("SRL output: " + srl_output);

//        String fileName = "out_4.txt";
        String line = null;
//        FileReader fileReader = new FileReader(fileName);
//
//        BufferedReader bufferedReader =
//                new BufferedReader(fileReader);
        is = new ByteArrayInputStream(srl_output.getBytes());
        bufferedReader = new BufferedReader(new InputStreamReader(is));

        ArrayList<WordRepresentation> words = new ArrayList<WordRepresentation>();
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
            if (word.endsWith(".")) {
                sentenceNumber++;
                wordNumber = 1;
            }
        }
        ArrayList<ArgumentBuilder> arguments = new ArrayList<ArgumentBuilder>();
        int count = 0;
        for (WordRepresentation w : words) {
            if (w.isPredicate && !w.partOfSpeech.startsWith("N")) {
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
                if(w.partOfSpeech.startsWith("V")) {
                    String val = SEPTBuilder.getSentenceByIndex(w.sentenceNumber).parseTreeNode.nodeString();
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, 0, "S", val, w.argument, w.argument[argNumber]);
                    arguments.add(a);

                } else if(!w.partOfSpeech.startsWith("N")) {
                    Node n = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(w.sentenceNumber), w.wordNumber);
                    String val = n.parseTreeNode.parent().nodeString();
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, 0, "PP", val, w.argument, w.argument[argNumber]);
                    arguments.add(a);
                }
                if(w.argument[argNumber].contains("AM-MOD")) continue;
                else {
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, w.argument, w.argument[argNumber]);
                    arguments.add(a);
                }

            }
            System.out.println("Word: " + w + ", predicate: " + (w.isPredicate? "YES" : "NO") + ", is argument: " +
                    (isArgument? "YES": "NO"));
        }

        for(ArgumentBuilder arg : arguments) {
            for(int i = 0; i < arg.argumentTypes.length; ++i) {
                if(!arg.argumentTypes[i].equals("_")) {
                    frames.get(i).addArgument(arg);
                }
            }
        }
    }

    private static void enchanceFrames(String input) throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {
        for(int i = 0; i < frames.size(); i++) {
            for(String key : frames.get(i).arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frames.get(i).arguments.get(key);
                for(int j = 0; j < args.size(); j++) {
                    //System.out.println(i);
                    ArgumentBuilder word = args.get(j);
                    System.out.println("ENHANCE: " + word);
                    Node node = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(word.sentenceNumber), word.wordNumber);
                    if (node.ref != null) {
                        word.word = node.ref.parseTreeNode.value();

                    }
                    word.ws = node.wordSense;
                }
            }
        }
    }

    public static DMRGraph generateTree(String input) throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {
        // handle error here
        buildDMRStepOne(input);
        enchanceFrames(input);
        DMRGraph singleLevelGraph = new DMRGraph(frames);
        singleLevelGraph.createGraph();
        singleLevelGraph.setScores();

        KMeans clusters = new KMeans(singleLevelGraph, 3);

        DMRGraph multiLevelGraph = new DMRGraph(singleLevelGraph, clusters.Clusters);
        System.out.println(multiLevelGraph.ArgsHash.size());
        System.out.println(clusters.Clusters.size());
        return singleLevelGraph;
    }
}