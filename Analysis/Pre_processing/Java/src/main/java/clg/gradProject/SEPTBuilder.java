package clg.gradProject;

import edu.stanford.nlp.trees.Tree;
import is2.transitionR6j.Tagger2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AhmedA on 12/18/15.
 */
public class SEPTBuilder {

	private static int globalWordIndex;
	public static List<Node> SEPTs = new ArrayList<Node>();
	/**
	 * 
	 * @param rootNode of the tree of Core NLP library
	 * @param index sentence index
	 * @return root node of the SEPT
	 */
	public static Node sentenceBuilder(Tree rootNode, int index) { 
		globalWordIndex = 1;
		Node node = new Node(rootNode, index);
		treeBuilder(node, index);
		return node;
	}

	/**
	 * 
	 * @param node root node of SEPT
	 * @param index sentence index
	 * recursive generates a full tree
	 */
	public static void treeBuilder(Node node, int index) {
		for (int i = 0; i < node.children.length; i++) {
			node.children[i] = new Node(node.parseTreeNode.children()[i], index);
			if (node.children[i].parseTreeNode.isLeaf()) {
				node.children[i].wordIndex = globalWordIndex++;
			} else {
				treeBuilder(node.children[i], index);
			}
		}
	}

	public static void printSEPT(Node node) {
		System.out.println(node);
		for (Node t : node.children) {
			if (t.parseTreeNode.isLeaf()) {
				System.out.println("This node is : \n\t in sentence "
						+ t.sentIndex + " and its index is " + t.wordIndex);
				System.out.println("coref " + t.ref);
			} else {
				printSEPT(t);
			}
		}
	}

	public static String SEPTString(Node node) {
		for (Node t : node.children) {
			return node + "\n" + SEPTString(t);
		}
		return "";
	}


	/**
	 * 
	 * @param node root node of the sentence
	 * @param wordIndex requested word index
	 * @return node with requested word index
	 */
	public static Node getNodeByWordIndex(Node node, int wordIndex) {
		if (node.wordIndex == wordIndex) {
			return node;
		} else {
			for (int i = 0; i < node.children.length; i++) {
				Node n = getNodeByWordIndex(node.children[i], wordIndex);
				if (n != null) {
					return n;
				}
			}
		}
		return null;
	}

	public static void addWSToNode(Node rootNode, int wordIndex, String meaning) {
		Node node = getNodeByWordIndex(rootNode, wordIndex);
		node.wordSense = meaning;
	}

	/**
	 * 
	 * @param sentenceIndex
	 * @return the root node of this sentence
	 */
	public static Node getSentenceByIndex(int sentenceIndex) {
		for (Node root : SEPTs) {
			if (root.sentIndex == sentenceIndex) {
				return root;
			}
		}
		return null;
	}

	public static void addWS(HashMap<Pair, String> wordSense) {
		for (Pair word : wordSense.keySet()) {
			Node rootNode = getSentenceByIndex(word.getSentenceIndex());		
			Node node = getNodeByWordIndex(rootNode, word.getWordIndex());
			node.wordSense = wordSense.get(word);
		}
	}

	public static Node getNodeParent(Node root, Node node) {

		for(int i  = 0; i < root.children.length; i++) {
			Node n = root.children[i];
			try {
				if(n.children[0].equals(node)) {
					return n;
				}
				else {
					Node e = getNodeParent(n, node);
					if(e != null) {
						return e;
					}
				}
			}
			catch (Exception e) {

			}
		}
		return null;
	}

	public static Node getNodeParent(Node root, Node node, boolean check) {
		for(int i  = 0; i < root.children.length; i++) {
			Node n = root.children[i];
			try {
				for(int j = 0; j < n.children.length; j++) {
					if(n.children[j].equals(node)) {
						return n;
					}
					else {
						Node e = getNodeParent(n, node, true);
						if(e != null) {
							return e;
						}
					}
				}
			}
			catch (Exception e) {

			}
		}
		return null;
	}
}