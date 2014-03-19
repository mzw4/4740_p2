package SupervisedWSD;
import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class Main {


	private HashMap<String, Model> models = new HashMap<String, Model>();
	
	public static void main(String[] args) {
		Main handler = new Main();
		Scanner in = new Scanner(System.in);
		
		if(args.length != 2) {
			System.out.println("There are an incorrect number of inputs. Please restart the program entering first a training set, and then a testing set as inputs.");
		}

		System.out.println("Processing training data...");
		handler.processTraining(args[0]);
		System.out.println("Processing complete.");
		
		while(true) {
			System.out.println("If this is a validation set, enter v.");
			System.out.println("If it's a test set, enter t.");
			String val = in.nextLine();
			if(val.equals("v")) {
				handler.processValidation(args[1]);
				break;
			}
			else if(val.equals("t")) {
				handler.processTest(args[1]);
				break;
			}
			else {
				System.out.println("Unrecognized input. Please enter either 'v' or 't'.");
			}
		}
		
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
	
	public void processTest(String filename) {
		File file = new File(filename);
		Scanner fileReader;
		
		try {
			fileReader = new Scanner(file);
			
			while(fileReader.hasNextLine()) {
				String example = fileReader.nextLine();
				String[] portions = example.split("[|]");
				String exWord = portions[0].split("[.]")[0];
				
				if(!models.containsKey(exWord)) {
					System.out.println(0);
				}
				
				System.out.println(models.get(exWord).predictSense(portions[2]));
			}
			
			fileReader.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("Your training file was not found.");
		}
	}

	public void processValidation(String filename) {
		File file = new File(filename);
		Scanner fileReader;
		int numerator = 0;
		int denominator = 0;
		
		try {
			fileReader = new Scanner(file);
			
			while(fileReader.hasNextLine()) {
				String example = fileReader.nextLine();
				String[] portions = example.split("[|]");
				String exWord = portions[0].split("[.]")[0];
				int trueVal = Integer.parseInt(portions[1].trim());
				
				if(!models.containsKey(exWord)) {
					System.out.println(0);
				}
				
				int predictedSense = models.get(exWord).predictSense(portions[2]);
				System.out.println(predictedSense);
				denominator++;
				if(predictedSense == trueVal) numerator++;
			}
			
			fileReader.close();
			System.out.println(numerator + "/" + denominator + " correct predictions.");
		}
		catch(FileNotFoundException e) {
			System.out.println("Your validation file was not found.");
		}
	}
}
