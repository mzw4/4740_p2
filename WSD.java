import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class WSD {

	public String dictionaryText;
	public String trainingText;
	
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
			bufferedReader.close();
		}
		
		
		
		return 0;
	}
	
}
