package clg.gradProject.srl;

import clg.gradProject.FrameBuilder;

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((word1 == null) ? 0 : word1.hashCode());
		result = prime * result + ((word2 == null) ? 0 : word2.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeaningDistancePair other = (MeaningDistancePair) obj;
		if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
			return false;
		if (word1 == null) {
			if (other.word1 != null)
				return false;
		} else if (!word1.equals(other.word1))
			return false;
		if (word2 == null) {
			if (other.word2 != null)
				return false;
		} else if (!word2.equals(other.word2))
			return false;
		return true;
	}
	
	
	
}
