import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class Main {

	private Scanner in = new Scanner(System.in);
	private HashMap<String, Model> models = new HashMap<String, Model>();
	
	public static void main(String[] args) {
		Main handler = new Main();
		
		if(args.length != 2) {
			System.out.println("There are an incorrect number of inputs. Please restart the program entering first a training set, and then a testing set as inputs.");
		}

		System.out.println("Processing training data...");
		handler.processTraining(args[0]);
		System.out.println("Processing complete.");
		
	}
	
	public void processTraining(String filename) {
		File file = new File(filename);
		Scanner fileReader;
		
		try {
			fileReader = new Scanner(file);
			
			while(fileReader.hasNextLine()) {
				String example = fileReader.nextLine();
				String[] portions = example.split("[|]");
				//The relevant word is in the first section of the line before the period.
				String exWord = portions[0].split("[.]")[0];
				//The number of the sense is the second section minus the whitespace.
				int senseNum = Integer.parseInt(portions[1].trim());
				
				//If a model for this word doesn't already exist, make an empty one.
				if(!models.containsKey(exWord)) {
					models.put(exWord, new Model(exWord));
				}
				
				//Pass this word's model the example, which is in the third section of the line.
				models.get(exWord).inputExample(portions[2], senseNum);
			}
			
			fileReader.close();
			
			Iterator<Model> iterator = models.values().iterator();
			while(iterator.hasNext()) {
				iterator.next().finalize();
			}
		}
		catch(FileNotFoundException e) {
			System.out.println("Your training file was not found.");
		}
	}

}
