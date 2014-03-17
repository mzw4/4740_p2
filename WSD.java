import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



public class WSD {

	public static String dictionaryText;
	public static HashMap<String, HashMap<Integer, String[]>> dictMap = new HashMap<>();
	
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
	
}
