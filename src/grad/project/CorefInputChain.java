package grad.project;

import java.util.ArrayList;
import java.util.List;

public class CorefInputChain {

	private class CorefNode {
		
		private int sentenceIndex;
		private int wordIndex;
		
		public CorefNode() {
			// TODO Auto-generated constructor stub
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
	
	private List<CorefNode> leaves;
	private CorefNode source;
	
	public CorefInputChain(String corefList) {
		String r = corefList.substring(1,corefList.length()-1);
		String[] list = r.split(",");
		
		leaves = new ArrayList<CorefInputChain.CorefNode>(list.length-1);
		String[] sourceNode = list[0].split(" ");
		source = new CorefNode(Integer.parseInt(sourceNode[0]), Integer.parseInt(sourceNode[1]));
		for(int i = 1; i < list.length; i++) {
			
		}
	}
}
