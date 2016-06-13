package clg.gradProject;

/**
 * Created by AhmedA on 12/18/15.
 */
public class Pair {

	private int wordIndex;
	private int sentenceIndex;

	public int getWordIndex() {
		return wordIndex;
	}

	public void setWordIndex(int wordIndex) {
		this.wordIndex = wordIndex;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	@Override
	public String toString() {
		return "Pair{" + "wordIndex=" + wordIndex + ", sentenceIndex="
				+ sentenceIndex + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Pair pair = (Pair) o;

		if (wordIndex != pair.wordIndex)
			return false;
		return sentenceIndex == pair.sentenceIndex;

	}

	@Override
	public int hashCode() {
		int result = wordIndex;
		result = 31 * result + sentenceIndex;
		return result;
	}
}
