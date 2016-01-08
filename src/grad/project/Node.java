package grad.project;

import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.Tree;
import grad.project.CorefInputChain.CorefNode;

public class Node {

	Tree parseTreeNode; // this is the parse tree node

	int sentIndex;
	int wordIndex;
	Node coref;
	String wordSense;
	Node[] children;

	private static int globalWordIndex = 1;

	Node(Tree node, int sentIndex) {
		parseTreeNode = node;
		this.sentIndex = sentIndex;
		children = new Node[parseTreeNode.children().length];
	}

	/**
	 * Trial
	 * 
	 * @param rootNode
	 * @param index
	 * @return
	 */
	public static Node sentenceBuilder(Tree rootNode, int index) { // returns
																	// the head
																	// node,
																	// index is
																	// the
																	// sentence
																	// index
		Node node = new Node(rootNode, index);
		treeBuilder(node, index);
		globalWordIndex = 3;
		return node;
	}

	/**
	 * 
	 * @param node
	 * @param index
	 */
	private static void treeBuilder(Node node, int index) {
		for (int i = 0; i < node.children.length; i++) {
			node.children[i] = new Node(node.parseTreeNode.children()[i], index);
			if (node.children[i].parseTreeNode.isLeaf()) {
				node.children[i].wordIndex = globalWordIndex++;
			} else {
				treeBuilder(node.children[i], index);
			}
		}
	}

	public String toString() {

		return this.parseTreeNode.toString();
	}

	public static Node getNodebyWordIndex(Node rootNode, int wordIndex) { // done
		return null;
	}

	public static void addWsdToNode(Node rootNode, int wordIndex, String def) {
		Node node = getNodebyWordIndex(rootNode, wordIndex);
		node.wordSense = def;
	}

	private static void printSEPT(Node node) {
		System.out.println(node);
		for (Node t : node.children) {
			if (t.parseTreeNode.isLeaf()) {
				System.out.println("This node is : \n\t in sentence "
						+ t.sentIndex + " and its index is " + t.wordIndex);
			} else {
				printSEPT(t);
			}
		}
	}

	public static void addCoref(List<Node> tree, List<CorefInputChain> corefs) {
		for(CorefInputChain chain : corefs) {
			Node sourceNode = findNode(chain.getSource(), tree);
			for(CorefNode leaf : chain.getLeaves()) {
				Node ref = findNode(leaf, tree);
				ref.coref = sourceNode;
			}
		}
	}

	private static Node findNode(CorefNode corefNode, List<Node> tree) {
		for(Node root : tree) {
			if(root.sentIndex == corefNode.getSentenceIndex()) {
				return Node.getNodebyWordIndex(root, corefNode.getWordIndex());
			}
		}
		return null;
	}

	public static void main(String[] args) {

		String sentence = "Khaled goes to college every Sunday in ASU";
		Annotation anotate = API.annotate(sentence);
		Tree t = API.getAllTrees(anotate).get(0);
		List coref = API.getAllMentionsSets(anotate);
		Node a = Node.sentenceBuilder(t, 1);
		printSEPT(a);
	}

}