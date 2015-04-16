import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 * Generate input training data files with 
 * data in random order
 */
public class CreateInputFile {
	public void createRanFile(String fileName, String outputFile) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		boolean dataMark = false;
		
		try {
			reader = new BufferedReader(new FileReader(fileName));
			writer = new BufferedWriter(new FileWriter(outputFile));
			ArrayList<String> record = new ArrayList<String>();
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				if (line.equals("@data")) {
					writer.write(line + "\n");
					dataMark = true;
				} else if (!dataMark) {
					writer.write(line + "\n");
				} else {
					record.add(line);
				}
			}
			reader.close();
			System.out.println(record.size());
			int range = record.size();
			Random rand = new Random();
			int index;
			while (range > 0) {
				index = rand.nextInt(range);
				writer.write(record.get(index) + "\n");
				record.set(index, record.get(range - 1));
				range--;
			}
			writer.close();
			
		}catch (FileNotFoundException e) {
			System.err.println("Cannot find the file");
		} catch (IOException e) {
			System.err.println("Error while reading line");
		}
	}
	
	
	public static void main(String[] args) {
		CreateInputFile inst = new CreateInputFile();
		inst.createRanFile("trainProdSelection.arff", "randTrainProdSelection.arff");
		inst.createRanFile("trainProdIntro.binary.arff", "randTrainProdIntro.binary.arff");
		inst.createRanFile("trainProdIntro.real.arff", "randTrainProdIntro.real.arff");

	}

}
