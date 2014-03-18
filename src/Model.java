import java.util.HashMap;

public class Model {

	private HashMap<Integer, Sense> tempSenses = new HashMap<Integer, Sense>();
	private HashMap<Integer, Sense> finalSenses = new HashMap<Integer, Sense>();
	private String target;
	
	public Model(String word) {
		target = word;
	}
	
	public void inputExample(String chunk, int senseNum) {
		//TODO: Process a chunk's features, and increment the respective Sense's HashMap
		if(!tempSenses.containsKey(senseNum)) tempSenses.put(senseNum, new Sense(target, senseNum));
		
		String processed = chunk.replaceAll("[(),!.?;:'\"%#@*]", " ");
		String[] partitioned = processed.split("%%");
		String[] preFeatures = partitioned[0].split("\\s+");
		String[] postFeatures = partitioned[2].split("\\s+");
		for(int a = 0; a < preFeatures.length; a++) {
			tempSenses.get(senseNum).incrementFeature(preFeatures[a].trim());
		}
		for(int a = 0; a < postFeatures.length; a++) {
			tempSenses.get(senseNum).incrementFeature(postFeatures[a].trim());
		}
	}
	
	public void finalize() {
		//TODO: finalize() is called after all of the training is complete for this word. Process the
		//      Senses into their final form, which prunes the number of features selected.
	}

}
