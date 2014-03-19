package DictionaryWSD;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Filter {
	public static final int STOP_THRESHOLD = 10;
	
	public HashSet<String> stopWords = new HashSet<>();
	
    protected StanfordCoreNLP pipeline;

    public Filter() {
        // Create StanfordCoreNLP object properties, with POS tagging and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }
    
	/*
	 * Filter features by lemmatizing, then removing irrelevant features from the list,
	 * and keeping only lexemes that may have an impact on semantic meaning
	 * 
	 * Remove stop words, and words with 2 or less letters
	 */
	public String[] filterFeatures(String text) {
		ArrayList<String> filtered = new ArrayList<>();
		
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text.replaceAll("[^a-zA-Z ]", ""));
		// run all Annotators on this text
		pipeline.annotate(document);		
		
		for (CoreLabel token : document.get(TokensAnnotation.class)) {
			if(!stopWords.contains(token.word()) && token.word().length() > 2) {
				filtered.add(token.get(LemmaAnnotation.class));
			}
		}
		
		return filtered.toArray(new String[filtered.size()]);
	}
	
	/*
	 * Returns a set of stop words based on a frequency threshold
	 */
	public void setStopWords(String filename) {
		File file = new File(filename);
		
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}	

		HashMap<String, Integer> counts = new HashMap<>();
		String[] processed = text.replaceAll("[^a-zA-Z ]", "").split(" ");

		for(String s: processed) {
			if(counts.containsKey(s)) {
				counts.put(s, counts.get(s)+1);
			} else if(!s.isEmpty()){
				counts.put(s, 1);
			}
		}
		
		//For tuning the threshold
//		Integer[] vals = counts.values().toArray(new Integer[counts.size()]);
//		Arrays.sort(vals);
//		for(Integer i: vals) {
//			System.out.print(i + ", ");
//		}
		
		for(String s: counts.keySet()) {
			if(counts.get(s) >= STOP_THRESHOLD) {
				stopWords.add(s);
			}
		}
	}
	
	public static void main(String[] args) {
		Filter filter = new Filter();
		filter.setStopWords("src/test.txt");
		String [] result = filter.filterFeatures("Hello, my name is Gordan, and I like running and to run. But I can't run. Shit.");
		System.out.println("Stopwords: " + filter.stopWords);
		
		for(String s: result) {
			System.out.print(s + ", ");
		}
		System.out.println();
	}
}
