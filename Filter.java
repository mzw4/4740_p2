import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Filter {
	public static final int STOP_THRESHOLD = 1000;
	
	public static HashSet<String> stopWords = new HashSet<>();
	
	/*
	 * Filter features by lemmatizing, then removing irrelevant features from the list,
	 * and keeping only lexemes that may have an impact on semantic meaning
	 * 
	 * Remove stop words, and words with 2 or less letters
	 */
	public static String[] filterFeatures(String[] features) {
		ArrayList<String> filtered = new ArrayList<>();
		
		for(int i = 0; i < features.length; i++) {
			if(!stopWords.contains(features[i])) {
				filtered.add(features[i]);
			}
		}
		
		return features;
	}
	
	/*
	 * Returns a set of stop words based on a frequency threshold
	 */
	public static void setStopWords(String filename) {
		File file = new File(filename);
		
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}	

		HashMap<String, Integer> counts = new HashMap<>();
		String[] processed = text.replaceAll("([(),!.?;:])", " ").split(" ");
		for(String s: processed) {
			if(counts.containsKey(s)) {
				counts.put(s, counts.get(s)+1);
			} else {
				counts.put(s, 1);
			}
		}
		
		//For tuning the threshold
//		Integer[] vals = counts.values().toArray(new Integer[counts.size()]);
//		Arrays.sort(vals);
		
		for(String s: counts.keySet()) {
			if(counts.get(s) >= STOP_THRESHOLD) {
				stopWords.add(s);
			}
		}
	}
}
