import java.util.HashMap;

public class Sense {

	private HashMap<String, Integer> features = new HashMap<String, Integer>();
	private int senseNum;
	private String senseOf;
	
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

	public int getVal(String feature) {
		if(!features.containsKey(feature)) {
			return 0;
		}
		else return features.get(feature);
	}
}
