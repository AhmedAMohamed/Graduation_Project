package grad.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CorefInputChain {

	class CorefNode {

		private int sentenceIndex;
		private int wordIndex;

		public CorefNode() {

		}

		public CorefNode(int sentenceIndex, int wordIndex) {
			setSentenceIndex(sentenceIndex);
			setWordIndex(wordIndex);
		}

		public int getSentenceIndex() {
			return sentenceIndex;
		}

		public void setSentenceIndex(int sentenceIndex) {
			this.sentenceIndex = sentenceIndex;
		}

		public int getWordIndex() {
			return wordIndex;
		}

		public void setWordIndex(int wordIndex) {
			this.wordIndex = wordIndex;
		}
	}

	private List<CorefNode> references;
	private CorefNode source;

	public CorefInputChain(String corefList) {
		corefList = corefList.substring(1, corefList.length()-1);
		String[] list = corefList.split(",");
		references = new ArrayList<CorefInputChain.CorefNode>(list.length - 1);
		String[] sourceNode = list[0].split(" ");
		source = new CorefNode(Integer.parseInt(sourceNode[0]),
				Integer.parseInt(sourceNode[1]));
		for (int i = 1; i < list.length; i++) {
			list[i] = list[i].trim();
			CorefNode node = new CorefNode();
			node.setSentenceIndex(Integer.parseInt(list[i].split(" ")[0]));
			node.setWordIndex(Integer.parseInt(list[i].split(" ")[1]));
			references.add(node);
		}
	}

	public List<CorefNode> getReferences() {
		return references;
	}

	public void setReferences(List<CorefNode> references) {
		this.references = references;
	}

	public CorefNode getSource() {
		return source;
	}

	public void setSource(CorefNode source) {
		this.source = source;
	}

	@Override
	public String toString() {
		String value = new String("The source node is in the "
				+ source.sentenceIndex
				+ " sentence and its index in this sentence is : "
				+ source.wordIndex);
		for (CorefNode n : references) {
			value += "\n one leaf is " + n.sentenceIndex
					+ " and the index in this sentence is " + n.wordIndex;
		}
		return value;
	}
}
