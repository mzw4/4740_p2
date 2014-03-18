import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;



public class WSD {

	public static String dictionaryText;
	public static HashMap<String, HashMap<Integer, String[]>> dictMap = new HashMap<>();
	public String trainingText;
		
	private static WordNetDatabase database;
	
	private static String[] stopWords = {"the"
	
	public static void initDictionary() {
		System.setProperty("wordnet.database.dir", "/Users/mike/Documents/workspace-NLP/WordNet-3.0/dict/");
		database = WordNetDatabase.getFileInstance();
	}
	
	/*
	 * 
	 * Punctuation should be stripped
	 */
	public static void dictionaryWSD(String text, String target) {
		
	}
	
	/*
	 * Look up target word in dictionary. Need to keep track of senses.
	 * Returns a map in the form <sense id, array of words in that sense definition> 
	 */
	public static HashMap<Integer, String[]> lookUpDictionaryTarget(String target) {
		Synset[] synset = queryWordNet(target);
		if(synset == null) {
			return null;
		}
		
		HashMap<Integer, String[]> defs = new HashMap<>();
		for(int i = 0; i < synset.length; i++) {
			// Process and tokenize
			String processed = synset[i].getDefinition().replaceAll("([(),!.?;:])", " $1 ");
			String[] tokens = processed.split("\\s+");
			
			// Filter irrelevant words and insert into map
			defs.put(i, filterFeatures(tokens));
		}
		return defs;
	}
	
	/*
	 * Look up context feature words. Senses don't matter.
	 * Returns a list of tokens aggregated from all definitions of the target
	 */
	public static String[] lookUpDictionaryContext(String target) {
		Synset[] synset = queryWordNet(target);
		if(synset == null) {
			return null;
		}
		
		ArrayList<String> tokens = new ArrayList<>();
		for(Synset form: synset) {
			// Process and tokenize
			String processed = form.getDefinition().replaceAll("([(),!.?;:])", " $1 ");
			String[] strings = processed.split("\\s+");
			
			// Filter irrelevant features and insert into list
			tokens.addAll(Arrays.asList(filterFeatures(strings)));
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	/*
	 * Look up target word in WordNet.
	 * Returns an Synset array 
	 */
	public static Synset[] queryWordNet(String target) {
		if(target.length() < 2) {
			System.out.println("Invalid target word \"" + target + "\"");
			return null;
		}
		String type = target.substring(target.length() - 1);
		
		Synset[] synset = null;
		switch(type) {
		case "n":
			synset = database.getSynsets(target.substring(0, target.indexOf(".")), SynsetType.NOUN);
			break;
		case "v":
			synset = database.getSynsets(target, SynsetType.VERB);
			break;
		case "a":
			synset = database.getSynsets(target, SynsetType.ADJECTIVE);
			break;
		default:
			break;
		}
		return synset;
	}
	
	/*
	 * Filter features by lemmatizing, then removing irrelevant features from the list,
	 * and keeping only lexemes that may have an impact on semantic meaning
	 */
	private static String[] filterFeatures(String[] features) {
		ArrayList<String> filtered = new ArrayList<>();
		
		return features;
	}
	
	//Return the sense/definition of the target word
	public int parseTestData(String filename) {
		
		File file = new File(filename);
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = bufferedReader.readLine();
			
			while (line != null) {
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//bufferedReader.close();
		}

		return 0;
	}
	
	public static void main(String[] args) {
		WSD.initDictionary();
		WSD.lookUpDictionaryContext("chair.n");
		
	}
	
}
