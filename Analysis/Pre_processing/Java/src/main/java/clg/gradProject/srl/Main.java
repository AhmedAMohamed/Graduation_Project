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

        String line = null;
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
                if (w.partOfSpeech.startsWith("V")) continue;
                else if (w.word.contains("everyday") || w.word.contains("every")) continue;
                else if(w.argument[argNumber].contains("AM-MOD")) continue;
                else if(w.argument[argNumber].contains("R-A0")) continue;
                else if (w.partOfSpeech.contains("PP") || w.partOfSpeech.contains("IN") || w.partOfSpeech.contains("TO")) {
                    Node rootNode = SEPTBuilder.getSentenceByIndex(w.sentenceNumber);
                    Node argRootNode = SEPTBuilder.getNodeByWordIndex(rootNode, w.wordNumber);
                    Node requestedNode = SEPTBuilder.getNodeParent(rootNode, argRootNode);
                    requestedNode = SEPTBuilder.getNodeParent(rootNode, requestedNode, true);
                    requestedNode = SEPTBuilder.getNP(requestedNode);
                    String value = requestedNode.parseTreeNode.pennString();
                    String[] values = value.split(" ");
                    value = "";
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                        if (values[i].endsWith(")")) {
                            value += values[i].substring(0, values[i].indexOf(')')) + " ";
                        }
                    }
                    value = value.trim();
                    value = value.toLowerCase();
                    // parse the request node value
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, value, value, w.argument, w.argument[argNumber]);
                    arguments.add(a);

                }
                else if(!w.partOfSpeech.startsWith("N")) {
                    Node rootNode = SEPTBuilder.getSentenceByIndex(w.sentenceNumber);
                    Node argNode = SEPTBuilder.getNodeByWordIndex(rootNode, w.wordNumber);
                    Node requestedNode = SEPTBuilder.getNodeParent(rootNode, argNode);
                    requestedNode = SEPTBuilder.getNodeParent(rootNode, requestedNode, true);
                    String value = requestedNode.parseTreeNode.pennString();
                    String[] values = value.split(" ");
                    value = "";
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                        if (values[i].endsWith(")")) {
                               value += values[i].substring(0, values[i].indexOf(')')) + " ";
                        }
                    }
                    value = value.trim();
                    value = value.toLowerCase();
                    // parse the request node value
                    ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, value, value, w.argument, w.argument[argNumber]);
                    arguments.add(a);
                }
                else {

                    Node rootNode = SEPTBuilder.getSentenceByIndex(w.sentenceNumber);
                    Node argNode = SEPTBuilder.getNodeByWordIndex(rootNode, w.wordNumber);
                    Node requestedNode = SEPTBuilder.getNodeParent(rootNode, argNode);
                    requestedNode = SEPTBuilder.getNodeParent(rootNode, requestedNode, true);
                    String value = requestedNode.parseTreeNode.pennString();
                    String[] values = value.split(" ");
                    value = "";
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                        if (values[i].endsWith(")")) {
                            value += values[i].substring(0, values[i].indexOf(')')) + " ";
                        }
                    }
                    value = value.trim();
                    value = value.toLowerCase();

                    // parse the value
                    if (value.contains("(CC and)")) {
                        String[] values_2 = value.split("(CC and)");
                        ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, requestedNode.parseTreeNode.nodeString(), values_2[0], w.argument, w.argument[argNumber]);
                        arguments.add(a);

                        ArgumentBuilder a2 = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, requestedNode.parseTreeNode.nodeString(), values_2[1], w.argument, w.argument[argNumber]);
                        arguments.add(a2);
                    }
                    else {
                        ArgumentBuilder a = new ArgumentBuilder(w.sentenceNumber, w.wordNumber, w.partOfSpeech, value, w.argument, w.argument[argNumber]);
                        arguments.add(a);
                    }
                }

            }
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
                    ArgumentBuilder word = args.get(j);
                    Node node = SEPTBuilder.getNodeByWordIndex(SEPTBuilder.getSentenceByIndex(word.sentenceNumber), word.wordNumber);
                    if (node.wordIndex == -1 || node.parseTreeNode.pennString().contains("(")) {
                        node = SEPTBuilder.getLeaf(node);
                    }
                    if (node != null) {
                        if (node.ref != null) {
                            ArgumentBuilder correctArg = findArgument(node.ref.parseTreeNode.value());
                            if (correctArg != null) {
                                args.set(j, correctArg);
                            }
                        }
                        word.ws = node.wordSense;
                    }
                }
            }
        }
    }

    private static ArgumentBuilder findArgument(String corefValue) {
        corefValue = corefValue.toLowerCase();
        for (FrameBuilder frame : frames) {
            for (String argType : frame.arguments.keySet()) {
                ArrayList<ArgumentBuilder> args = frame.arguments.get(argType);
                for (ArgumentBuilder arg : args) {
                    if(arg.word.contains(corefValue)) {
                        return arg;
                    }
                }
            }
        }
        return null;
    }

    public static DMRGraph generateTree(String input) throws Throwable, IOException, CloneNotSupportedException, ClassNotFoundException {
        frames = new ArrayList();
        DMRGraph multiLevelGraph = null;

        buildDMRStepOne(input);
        DMRGraph singleLevelGraph = new DMRGraph(frames);
        singleLevelGraph.createGraph();
        singleLevelGraph.addLinkingActionFrames(SEPTBuilder.SEPTs.size() + 1);
        enchanceFrames();

        singleLevelGraph.setScores(singleLevelGraph.ActionFrames);
        if (singleLevelGraph.ActionFrames.size() < 10) {
            return singleLevelGraph;
        } else {
            //for (int i = 4; i > 1; i--) {
                KMeans clusters = new KMeans(singleLevelGraph, 3);
                int zeroClusters = findZeroClusters(clusters);

                //MeaningDistance meanining = new MeaningDistance(clusters.Clusters);
                //HierarchicalNew x = new HierarchicalNew(multiLevelGraph, meanining);
            //    i = i - zeroClusters - 1;
            //    if (i == 1 || i == 0) {
            //        return singleLevelGraph;
            //    }
                multiLevelGraph = new DMRGraph(singleLevelGraph, clusters.Clusters);
                calculateMultiLevelScore(multiLevelGraph);
            //}
        }
        return multiLevelGraph;
    }

    private static int findZeroClusters(KMeans clusters) {
        int count = 0;
        for (ArrayList<FrameBuilder> cluster : clusters.Clusters) {
            if (cluster.size() == 0) {
                count++;
            }
        }
        return count;
    }

    private static void calculateMultiLevelScore(DMRGraph multiLevelGraph) {
        for (FrameBuilder frame : multiLevelGraph.ActionFrames) {
            if(frame.nextLevel != null) {
                multiLevelGraph.setScores(frame.nextLevel);
            }
        }
    }
}