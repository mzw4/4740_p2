import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;



public class WSD {

	public static String dictionaryText;
	public static HashMap<String, HashMap<Integer, String[]>> dictMap = new HashMap<>();

	private static WordNetDatabase database;

	//string of word points to an index/position in the text block
	public static ArrayList<HashMap<String,String>> targetPointers;
	//array to which hashmap of pointers points to
	public static ArrayList<String[]> targetSensesDefinitions;
	
	public static String[] stopWords = {"the", "and", ""};
	
	public static void initDictionary() {
		// Will only work on mikeys computer
		System.setProperty("wordnet.database.dir", "/home/blee/workspace/4740_p2/WordNet-3.0/dict/");
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
	private static HashMap<Integer, String[]> lookUpDictionaryTarget(String target) {
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
			defs.put(i, Filter.filterFeatures(tokens));
		}
		return defs;
	}
	
	/*
	 * Look up context feature words. Senses don't matter.
	 * Returns a list of tokens aggregated from all definitions of the target
	 */
	private static String[] lookUpDictionaryContext(String target) {
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
			tokens.addAll(Arrays.asList(Filter.filterFeatures(strings)));
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	/*
	 * Look up target word in WordNet.
	 * Returns an Synset array 
	 */
	private static Synset[] queryWordNet(String target) {
		if(target.length() < 2) {
			System.out.println("Invalid target word \"" + target + "\"");
			return null;
		}
		
		Synset[] synset = null;
		if(target.indexOf(".") < 0) {
			synset = database.getSynsets(target);
		} else {
			String type = target.substring(target.length() - 1);
			target = target.substring(0, target.indexOf("."));
			switch(type) {
			case "n":
				synset = database.getSynsets(target, SynsetType.NOUN);
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
		}
		return synset;
	}
	
	/*
	 * Make a new array with # of entries = # of senses of the target word
	 * populate hashmap pointing to the index at which it is located.
	 * Returns number of Senses for the target Word in the dictionary
	 */
	public static int processTarget(String target) {
		HashMap<Integer,String[]> targetMap = lookUpDictionaryTarget(target);
		int numSenses = targetMap.size();
		
		// To account for consecutive features, for each definition, hold a map of <features, indices>
		targetPointers = new ArrayList<HashMap<String,String>>(numSenses);
		// To check if a feature is consecutive, hold an ordered array for each definition
		targetSensesDefinitions = new ArrayList<String[]>(numSenses);
		
		//populate targetSensesDefinitions
		for (int i = 0; i < numSenses; i++) {
			//retrieve ith sense and get string of words from defintion
			String[] senseStrings = targetMap.get(i);
			
			//create targetPointers in senseStrings, pointers are delimited by commas
			HashMap<String,String> targ = new HashMap<String,String>();
			for (int j = 0; j < senseStrings.length; j++) {
				String s = senseStrings[j].toLowerCase();
				//if string does not exist, add new entry into hashmap
				if (!targ.containsKey(s)) {
					String index = String.valueOf(j);
					targ.put(s,index);
				} else {
					//else if not empty, add to string
					String index = targ.get(s);
					index = index + "," + String.valueOf(j);
					targ.put(s,index);
				}
			}
			
			targetPointers.add(targ);
			//copy senseStrings to targetSensesDefinitions array
			targetSensesDefinitions.add(senseStrings);
		}
		
		return numSenses;
	}
	
	/*
	 * Return the sense/definition of the target word for each line
	 */
	public static ArrayList<Integer> parseTestData(String filename) {
		File file = new File(filename);
		
		System.out.println("Setting stop words...");
		Filter.setStopWords(filename);
		BufferedReader bufferedReader = null;
		
		//ArrayList to hold senses for each target word in each line
		ArrayList<Integer> senses = new ArrayList<Integer>();
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = bufferedReader.readLine();
			
			while (line != null) {
				//store target word
				int targetIndex = line.indexOf("|");
				String target = line.substring(0,targetIndex).trim();
				//update new string without target
				line = line.substring(targetIndex+1,line.length());
				
				//retrieve block of text
				int textIndex = line.indexOf("|");
				String text = line.substring(textIndex+1,line.length()).trim();
				
				int numSenses = processTarget(target);
				
				//split text and remove ,.$''``%?!--
				String[] splitText = text.split(" ");
				
				HashSet<String> notImportant = new HashSet<String>();
				notImportant.add(",");
				notImportant.add(".");
				notImportant.add("$");
				notImportant.add("''");
				notImportant.add("``");
				notImportant.add("%%");
				notImportant.add("%");
				notImportant.add("?");
				notImportant.add("!");
				notImportant.add("--");
				notImportant.add(")");
				notImportant.add("(");
				notImportant.add(":");
				notImportant.add(";");
				notImportant.add("&");
				notImportant.add("#");
				notImportant.add("{");
				notImportant.add("}");
				notImportant.add("...");
				
				//TODO: Lemmatize target word and other words in block of text
				//int posIndex = target.indexOf(".");
				//String targetRoot = target.substring(0,posIndex);
				
				//points for each sense
				int[] points = new int[numSenses];
				
				for (String s: splitText) {
					//remove unimportant characters, target word and words with digits
					if (!(notImportant.contains(s) || s.equals(target)
							|| (Pattern.compile("[0-9]").matcher(s).find()))) {
						String lower_s = s.toLowerCase();
						System.out.println(lower_s);
						String[] contextMeaning = lookUpDictionaryContext(lower_s);
						//System.out.println(contextMeaning);
						//System.out.println("hi");
						if (contextMeaning == null)
							continue;
						//for each word, add the points accordingly for each sense
						//+1 for each word occurrence, +2 if there are consecutive words
						for (int j = 0; j < contextMeaning.length; j++) {
							String curr = contextMeaning[j]; //current contextMeaning
							String nextContext = (j == contextMeaning.length-1)?null:contextMeaning[j+1];
							String prevContext = (j == 0)?null:contextMeaning[j-1];
							
							
							for (int i = 0; i < numSenses; i++) {
								//if the key exists, add one for each key and check surrounding
								//keys to see if there are consecutive matches
								if (targetPointers.get(i).containsKey(curr)) {
									points[i]++;
									
									//get indices (delimited by commas), convert to ints
									String[] tempIndices = targetPointers.get(i).get(curr).split(",");
									int[] indices = new int[tempIndices.length];
									for (int k = 0; k < tempIndices.length; k++)
										indices[k] = Integer.parseInt(tempIndices[k]);
									
									//for bonus points (+1 if there are 2 consecutive words)
									for (int k = 0; k < indices.length; k++) {
										//length of dictionary definition for that sense
										int targetSize = targetSensesDefinitions.get(i).length;
										//store previous and next word surrounding target word
										String[] targetDef = targetSensesDefinitions.get(i);
										
										int currTargIndex = indices[k];
										String prevTarget = (currTargIndex == 0)?null:targetDef[currTargIndex-1];
										String nextTarget = (currTargIndex == targetSize-1)?null:targetDef[currTargIndex-1];
										                           
										//if words in front or back of the word are the same, incrementing points
										if (prevTarget != null && prevTarget != null)
											if (nextTarget.equals(prevTarget))
												points[i]++;
			
										if (nextContext != null && prevContext != null)
											if (nextContext.equals(prevContext))
												points[i]++;
									}
								}
							}
						}
						
						//find max points value from senses, add to arraylist
						int max = 0;
						for (int i = 0; i < numSenses; i++) {
							if (points[i] > max) {
								max = points[i];
							}
						}
						senses.add(max);
					}
				}
				
				
				line = bufferedReader.readLine();
				//TODO: Check if it is actually in the dictionary
				
				//TODO: Blacklist specific words
				//TODO: Deal with contractions b/c we removed apostrophes (remove words)
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return senses;
	}
	
	public static void main(String[] args) {
		WSD.initDictionary();
		WSD.lookUpDictionaryContext("chair.n");
		WSD.parseTestData("/home/blee/workspace/4740_p2/src/Data/test_data.data");
		System.out.println("end");
	}
	
}
