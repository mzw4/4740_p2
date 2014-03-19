import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;


public class WordFilter {

	
	public HashSet<String> getStopWordSet(String filename) {
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
		
		return null;
	}
}
