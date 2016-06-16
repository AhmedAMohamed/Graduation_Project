package Analysis.Pre_processing.Java.src.main.java.clg.gradProject.srl;

import Analysis.Pre_processing.Java.src.main.java.clg.gradProject.FrameBuilder;

public class MeaningDistancePair {

	public FrameBuilder getWord1() {
		return word1;
	}
	public void setWord1(FrameBuilder frameBuilder) {
		this.word1 = frameBuilder;
	}
	public FrameBuilder getWord2() {
		return word2;
	}
	public void setWord2(FrameBuilder frameBuilder) {
		this.word2 = frameBuilder;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	FrameBuilder word1;
	FrameBuilder word2;
	double distance;
}
