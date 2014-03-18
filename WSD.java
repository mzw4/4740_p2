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
import java.util.HashSet;



public class WSD {

	public static String dictionaryText;
	public static HashMap<String, HashMap<Integer, String[]>> dictMap = new HashMap<>();
	public static String trainingText;
	//string of word points to an index/position in the text block
	public static ArrayList<HashMap<String,String>> targetPointers;
	//array to which hashmap of pointers points to
	public static ArrayList<String[]> targetSensesDefinitions;
	
	public static final String dict_path = "src/Data/dictionary.xml";
	
	public static void parseDictionary(File dictFile) {
		String dictionaryText;
		try {
			dictionaryText = new String(Files.readAllBytes(Paths.get(dictFile.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 * Punctuation should be stripped
	 */
	public static void dictionaryWSD(String text, String target) {
		
	}
	
	/*
	 * Look up target word in dictionary.
	 * Returns a map in the form <sense id, array of words in that sense> 
	 */
	public static HashMap<Integer, String[]> lookUpDictionaryTarget(String target) {
		return dictMap.get(target);
	}
	
	/*
	 * Look up context feature words. Senses don't matter.
	 */
	public static String[] lookUpDictionaryContext(String target) {
		ArrayList<String> defs = new ArrayList<>();
		for(String[] tokens: dictMap.get(target).values()) {
			defs.addAll(Arrays.asList(tokens));
		}
		return defs.toArray(new String[defs.size()]);
	}
	
	/*
	 * Make a new array with # of entries = # of senses of the target word
	 * populate hashmap pointing to the index at which it is located
	 */
	public static void processTarget(String target) {
		HashMap<Integer,String[]> targetMap = lookUpDictionaryTarget(target);
		int numSenses = targetMap.size();
		
		targetPointers = new ArrayList<HashMap<String,String>>(numSenses);
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
		
	}
	
	/*
	 * Return the sense/definition of the target word for each line
	 */
	public static int[] parseTestData(String filename) {
		File file = new File(filename);
		BufferedReader bufferedReader = null;
		
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
				String text = line.substring(textIndex,line.length()).trim();
				
				//retrieve hashmap for different senses of target word
				HashMap<Integer,String[]> targetWord = lookUpDictionaryTarget(target);
				int numSenses = targetWord.size();
				processTarget(target);
				
				//split text and remove ,.$'`%
				String[] splitText = text.split(" ");
				HashSet<String> notImportant = new HashSet<String>();
				notImportant.add(",");
				notImportant.add(".");
				notImportant.add("$");
				notImportant.add("'");
				notImportant.add("`");
				notImportant.add("%%");
				
				//TODO: Lemmatize target word and other words in block of text
				int posIndex = target.indexOf(".");
				String targetRoot = target.substring(0,posIndex);
				
				//HashMap<String,String[]> contextWords = new HashMap<String,String[]>();
				
				//points for each sense
				int[] points = new int[numSenses];
				
				for (String s: splitText) {
					//remove unimportant characters and target word
					if (!(notImportant.contains(s) || s.equals(targetRoot))) {
						String[] contextMeaning = lookUpDictionaryContext(s);
						
						//for each word, add the points accordingly for each sense
						//+1 for each word occurrence, +2 if there are consecutive words
						for (String t: contextMeaning) {
							for (int i = 0; i < numSenses; i++) {
								//if the key exists, add one for each key and check surrounding
								//keys to see if there are consecutive matches
								if (targetPointers.get(i).containsKey(t)) {
									points[i]++;
									
									//get indices (delimited by commas), convert to ints
									String[] tempIndices = targetPointers.get(i).get(t).split(",");
									int[] indices = new int[tempIndices.length];
									for (int k = 0; k < tempIndices.length; k++)
										indices[k] = Integer.parseInt(tempIndices[k]);
									
									for (int k = 0; k < indices.length; k++) {
										//store previous and next word surrounding context word
										String prev = 
									}
									
									
								}
							}
						}
					}
				}
				
				line = bufferedReader.readLine();
				//TODO: Check if it is actually in the dictionary
				
				
				//TODO: Change numbers into 1 entry?
				//TODO: Deal with contractions b/c we removed apostrophes
				
				
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bufferedReader.close();
		}
		
		
		
		return new int[0];
	}
	
}
