package clg.gradProject.srl;


import clg.gradProject.*;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
            if (w.isPredicate) {
                if(w.partOfSpeech.startsWith("N")) {
                    FrameBuilder f = new FrameBuilder(w.pred, -1, -1, w.partOfSpeech, w.word, count++);
                    frames.add(f);
                }
                else {
                    FrameBuilder f = new FrameBuilder(w.pred, w.sentenceNumber, w.wordNumber, w.partOfSpeech, w.word, count++);
                    frames.add(f);
                }
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
                if (w.partOfSpeech.startsWith("V")) {
                    //String val = SEPTBuilder.getSentenceByIndex(w.sentenceNumber).parseTreeNode.value();
                    //ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, 0, "S", val, w.argument, w.argument[argNumber]);
                    //arguments.add(a);
                    continue;
                }else if(!w.partOfSpeech.startsWith("N")) {
                    //Node n = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(w.sentenceNumber), w.wordNumber);
                    //Node val = SEPTBuilder.getNodeParent(SEPTBuilder.getSentenceByIndex(w.sentenceNumber), n);
                    //ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, 0, "PP", val.parseTreeNode.value(), w.argument, w.argument[argNumber]);
                    //arguments.add(a);
                    Node rootNode = SEPTBuilder.getSentenceByIndex(w.sentenceNumber);
                    Node argNode = SEPTBuilder.getNodeByWordIndex(rootNode, w.wordNumber);
                    Node requestedNode = SEPTBuilder.getNodeParent(rootNode, argNode);
                    requestedNode = SEPTBuilder.getNodeParent(rootNode, requestedNode, true);
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, requestedNode.parseTreeNode.nodeString(), requestedNode.parseTreeNode.pennString(), w.argument, w.argument[argNumber]);
                    arguments.add(a);
                }
                else if(w.argument[argNumber].contains("AM-MOD")) continue;
                else {

                    Node rootNode = SEPTBuilder.getSentenceByIndex(w.sentenceNumber);
                    Node argNode = SEPTBuilder.getNodeByWordIndex(rootNode, w.wordNumber);
                    Node requestedNode = SEPTBuilder.getNodeParent(rootNode, argNode);
                    requestedNode = SEPTBuilder.getNodeParent(rootNode, requestedNode, true);
                    String value = requestedNode.parseTreeNode.pennString();
                    if (value.contains("(CC and)")) {
                        String[] values = value.split("(CC and)");
                        ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, requestedNode.parseTreeNode.nodeString(), values[0], w.argument, w.argument[argNumber]);
                        arguments.add(a);

                        ArgumentBuilder a2 = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, requestedNode.parseTreeNode.nodeString(), values[1], w.argument, w.argument[argNumber]);
                        arguments.add(a2);
                    }
                    else {
                        ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, w.partOfSpeech, value, w.argument, w.argument[argNumber]);
                        arguments.add(a);
                    }
                }

            }
            System.out.println("Word: " + w + ", predicate: " + (w.isPredicate ? "YES" : "NO") + ", is argument: " +
                    (isArgument ? "YES" : "NO"));
        }

        for (ArgumentBuilder arg : arguments) {
            for (int i = 0; i < arg.argumentTypes.length; ++i) {
                if (!arg.argumentTypes[i].equals("_")) {
                    frames.get(i).addArgument(arg);
                }
            }
        }

        for (Iterator<FrameBuilder> it = frames.iterator(); it.hasNext();){
            FrameBuilder f = it.next();
            if (f.sentenceNumber == -1 && f.wordNumber == -1){
                it.remove();
            }
        }

    }

    private static void enchanceFrames() throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {
        for (int i = 0; i < frames.size(); i++) {
            for (String key : frames.get(i).arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frames.get(i).arguments.get(key);
                for (int j = 0; j < args.size(); j++) {
                    //System.out.println(i);
                    ArgumentBuilder word = args.get(j);
                    System.out.println("ENHANCE: " + word);
                    Node node = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(word.sentenceNumber), word.wordNumber);
                    if (node.ref != null) {
                        //word.word = node.ref.parseTreeNode.value();
                        ArgumentBuilder correctArg = findArgument(node.ref.parseTreeNode.value());
                        args.set(j, correctArg);
                    }

                    word.ws = node.wordSense;
                }
            }
        }
    }

    private static ArgumentBuilder findArgument(String corefValue) {
        for (FrameBuilder frame : frames) {
            for (String argType : frame.arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frame.arguments.get(argType);
                for (ArgumentBuilder arg : args) {
                    if(arg.word.equalsIgnoreCase(corefValue)) {
                        return arg;
                    }
                }
            }
        }
        return null;
    }

    public static DMRGraph generateTree(String input) throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {
        buildDMRStepOne(input);
        enchanceFrames();
        DMRGraph singleLevelGraph = new DMRGraph(frames);
        singleLevelGraph.createGraph();
        singleLevelGraph.addLinkingActionFrames(2);
        singleLevelGraph.setScores(singleLevelGraph.ActionFrames);

        KMeans clusters = new KMeans(singleLevelGraph, 2);

        double tt = clusters.validity();
        System.out.println(tt);
        DMRGraph multiLevelGraph = new DMRGraph(singleLevelGraph, clusters.Clusters);
        calculateMultiLevelScore(multiLevelGraph);

        System.out.println(multiLevelGraph.ArgsHash.size());
        System.out.println(clusters.Clusters.size());
        return multiLevelGraph;
    }

    private static void calculateMultiLevelScore(DMRGraph multiLevelGraph) {
        for (FrameBuilder frame : multiLevelGraph.ActionFrames) {
            if(frame.nextLevel != null) {
                multiLevelGraph.setScores(frame.nextLevel);
            }
        }
    }
}