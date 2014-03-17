import java.nio.file.Files;
import java.nio.file.Paths;



public class WSD {

	public String dictionaryText;
	
	public static void parseDictionary() {
		String content = new String(Files.readAllBytes(Paths.get(trainFile.getAbsolutePath())));

		
	}
	
	/*
	 * 
	 * Punctuation should be stripped
	 */
	public static void dictionaryWSD(String text, String target) {
		
	}
	
	public static String[] lookUpDictionary(String target) {
		

	}
	
}
