package grad.project;

import java.util.ArrayList;
import java.util.Map;

import java.io.*;
import java.util.*;

import edu.mit.jwi.item.Synset;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.mit.jwi.*;

public class Node {

    Tree parseTreeNode; // this is the parse tree node

    int sentIndex;
    int wordIndex;
    Node ref;
    String wordSense;

    Node[] children;

    private static int globalWordIndex = 1;

    Node(Tree node, int sentIndex) {
        parseTreeNode = node;
        this.sentIndex = sentIndex;
        this.wordIndex = -1;
        children = new Node[parseTreeNode.children().length];
    }


    public String toString() {

        return this.parseTreeNode.toString();
    }



    public static void main(String[] args) {
        String sentence = "Khaled goes to college every Sunday in ASU. Ahmed is good he is student";

        List<Tree> trees = API.getAllTrees(API.annotate(sentence));
        int index = 1;
        for(Tree tree : trees) {
            Node a = SEPTBuilder.sentenceBuilder(tree, index++);
            SEPTBuilder.printSEPT(a);
            System.out.println();
            System.out.println();
            System.out.println();

            Node current = SEPTBuilder.getNodeByWordIndex(a, 2);
            System.out.println("current node index is " + current.wordIndex + "   " + current.parseTreeNode);
            Scanner s = new Scanner(System.in);
            int ty = s.nextInt();

        }


    }
}