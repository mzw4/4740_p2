package SupervisedWSD;
import java.util.HashMap;

public class Sense {

	private HashMap<String, Integer> features = new HashMap<String, Integer>();
	private int senseNum;
	private String senseOf;
	private int numExamples = 0;
	
	public Sense(String target, int num) {
		senseNum = num;
		senseOf = target;
	}
	
	public void incrementFeature(String feature) {
		if(features.containsKey(feature)) {
			features.put(feature, features.get(feature) + 1);
		}
		else {
			features.put(feature, 1);
		}
	}
	
	public void removeFeature(String feature) {
		features.remove(feature);
	}

	public int getVal(String feature) {
		if(!features.containsKey(feature)) {
			return 0;
		}
		else return features.get(feature);
	}
	
	public void incrementExamples() {
		numExamples++;
	}
	
	public int getNumExamples() {
		return numExamples;
	}
	
	public int getSenseNum() {
		return senseNum;
	}
}
