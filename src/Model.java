import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

public class Model {

	private HashMap<Integer, Sense> senses = new HashMap<Integer, Sense>();
	private ArrayList<String> usedFeatures = new ArrayList<String>();
	private ArrayList<String> prunedFeatures = new ArrayList<String>();
	private ArrayList<String> finalFeatures = new ArrayList<String>();
	private String target;
	private int totalExamples = 0;
	
	private final int NUM_FEATURES = 5;
	private final int MIN_USES = 3;
	
	public Model(String word) {
		target = word;
	}
	
	public void inputExample(String chunk, int senseNum) {
		if(!senses.containsKey(senseNum)) senses.put(senseNum, new Sense(target, senseNum));
		senses.get(senseNum).incrementExamples();
		totalExamples++;
		
		String processed = chunk.replaceAll("[(),!.?;:'\"#@*]", " ");
		String[] partitioned = processed.split("%%");
		String[] preFeatures = partitioned[0].split("\\s+");
		for(int a = 0; a < preFeatures.length; a++) {
			senses.get(senseNum).incrementFeature(preFeatures[a].trim());
			if(!usedFeatures.contains(preFeatures[a].trim())) usedFeatures.add(preFeatures[a].trim());
		}
		
		if(partitioned.length < 3) return;
		String[] postFeatures = partitioned[2].split("\\s+");
		for(int a = 0; a < postFeatures.length; a++) {
			senses.get(senseNum).incrementFeature(postFeatures[a].trim());
			if(!usedFeatures.contains(postFeatures[a].trim())) usedFeatures.add(postFeatures[a].trim());
		}
	}
	
	public void finalize() {
		//      finalize() is called after all of the training is complete for this word. Process the
		//      Senses into their final form, which prunes the number of features selected.
		Iterator<String> firstIterator = usedFeatures.iterator();
		while(firstIterator.hasNext()) {
			String curr = firstIterator.next();
			if(getVal(curr) >= MIN_USES) prunedFeatures.add(curr);
		}
		
		if(prunedFeatures.size() <= NUM_FEATURES) return; //This indicates no pruning is necessary
		
		ArrayList<Double> slacks = new ArrayList<Double>();
		
		Iterator<String> featureIterator = prunedFeatures.iterator();
		while(featureIterator.hasNext()) {
			slacks.add(getSlack(featureIterator.next()));
		}
		
		Collections.sort(slacks);			//Sorts the slacks in ascending order
		Collections.reverse(slacks);		//Brings them to descending order, which is what we want.
		
		double benchmark = slacks.get(NUM_FEATURES - 1); //Since the list of slacks is sorted, this is the benchmark. Any slacks below this should be removed.
		
		Iterator<String> newFeatureIterator = prunedFeatures.iterator();
		
		while(newFeatureIterator.hasNext()) {
			String currFeature = newFeatureIterator.next();
			double currSlack = getSlack(currFeature);
			if(currSlack >= benchmark) {					//If the value for this feature is less than the benchmark, remove it from all of the senses
				finalFeatures.add(currFeature);
			}
		}
	}
	
	public int getVal(String feature) {
		Iterator<Sense> iterator = senses.values().iterator();
		int total = 0;
		while(iterator.hasNext()) {
			total += iterator.next().getVal(feature);
		}
		return total;
	}
	
	/* "Slack" is defined to be the sum of the squares of the differences in expected percentages.
	 * For example, if we have two senses each with 5 input Strings and "boot" appears 10 times total, we would
	 * expect it to appear 5 times for each sense (or 50% of the time). If instead it appeared 8 times for one sense
	 * and 2 times for another (80% and 20%), the slack would be |80 - 50|^2 + |20 - 50|^2 = 30^2 + 30^2 = 1800.
	 */
	public double getSlack(String feature) {
		double slack = 0.0;
		
		Iterator<Sense> iterator = senses.values().iterator();
		while(iterator.hasNext()) {
			Sense curr = iterator.next();
			
			double actualPercentage = ((double) curr.getVal(feature)) / ((double) getVal(feature));
			double expectedPercentage = ((double) curr.getNumExamples()) / ((double) totalExamples);
			
			slack += Math.pow(actualPercentage - expectedPercentage, 2);
		}
	
		return slack;
	}

	public int predictSense(String chunk) {
		String processed = chunk.replaceAll("[(),!.?;:'\"#@*]", " ");
		String[] partitioned = processed.split("%%");
		String[] preFeatures = partitioned[0].split("\\s+");
		double bestProb = 0.0;
		int bestSense = 0;
		
		Iterator<Sense> iterator = senses.values().iterator();
		while(iterator.hasNext()) {
			Sense curr = iterator.next();
			double currProb = 1.0;
			
			for(int a = 0; a < preFeatures.length; a++) {
				if(finalFeatures.contains(preFeatures[a].trim())) {
					currProb *= ((double) curr.getVal(preFeatures[a].trim())) / ((double) curr.getNumExamples());
				}
			}
			if(partitioned.length >= 3){
				String[] postFeatures = partitioned[2].split("\\s+");
				for(int a = 0; a < postFeatures.length; a++) {
					if(finalFeatures.contains(postFeatures[a].trim())) {
						currProb *= ((double) curr.getVal(postFeatures[a].trim())) / ((double) curr.getNumExamples());
					}
				}
			}
			if(currProb > bestProb) {
				bestProb = currProb;
				bestSense = curr.getSenseNum();
			}
		}
		
		if(bestSense == 0 ) return 1;
		else return bestSense;
	}
}
