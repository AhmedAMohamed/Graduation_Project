package grad.project;

import edu.stanford.nlp.trees.Tree;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AhmedA on 12/18/15.
 */
public class SEPTBuilder {

    private static int globalWordIndex;
    private static List<Node> SEPTs;

    public static Node sentenceBuilder(Tree rootNode, int index) { // returns the head node, index is the sentence index
        globalWordIndex = 1;
        Node node = new Node(rootNode, index);
        treeBuilder(node, index);
        return node;
    }

    public static void treeBuilder(Node node, int index) {
        for(int i = 0; i < node.children.length; i++) {
            node.children[i] = new Node(node.parseTreeNode.children()[i], index);
            if(node.children[i].parseTreeNode.isLeaf()) {
                node.children[i].wordIndex = globalWordIndex++;
            }
            else {
                treeBuilder(node.children[i],index);
            }
        }
    }

    public static void printSEPT(Node node) {
        System.out.println(node);
        for (Node t : node.children) {
            if(t.parseTreeNode.isLeaf()) {
                System.out.println("This node is : \n\t in sentence " + t.sentIndex + " and its index is " + t.wordIndex);
            }
            else {
                printSEPT(t);
            }
        }
    }

    public static Node getNodeByWordIndex(Node node, int wordIndex) {
        if(node.wordIndex == wordIndex) {
            return node;
        }
        else {
            for(int i = 0; i < node.children.length; i++) {
                Node n = getNodeByWordIndex(node.children[i], wordIndex);
                if(n != null) {
                    return n;
                }
            }
        }
        return null;
    }

    public static void addWSToNode(Node rootNode, int wordIndex, String meaning) {
        Node node = getNodeByWordIndex(rootNode,wordIndex);
        node.wordSense = meaning;
    }

    public static Node getSentenceByIndex(int sentenceIndex) {
        for(Node root : SEPTs) {
            if(root.sentIndex == sentenceIndex) {
                return root;
            }
        }
        return null;
    }

    public static void addWS(HashMap<Pair,String> wordSense) {
        for(Pair word : wordSense.keySet()) {
            Node node = getSentenceByIndex(word.getSentenceIndex());
            addWSToNode(node,word.getWordIndex(),wordSense.get(word));
        }
    }
    
}
