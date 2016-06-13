package clg.gradProject;

import edu.stanford.nlp.trees.Tree;

import java.util.List;

public class Node {

	public Tree parseTreeNode; // this is the parse tree node

	int sentIndex;
	int wordIndex;
	public Node ref;
	public String wordSense;

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
		for (Tree tree : trees) {
			Node a = SEPTBuilder.sentenceBuilder(tree, index++);
			SEPTBuilder.printSEPT(a);
			Node current = SEPTBuilder.getNodeByWordIndex(a, 2);
			System.out.println("current node index is " + current.wordIndex
					+ "   " + current.parseTreeNode);
		}
	}
}