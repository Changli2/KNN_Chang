import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class KNN {
	private static int k;
	private final static int kFold = 5;
	// Used when normalization
	private static double[] numFactor;
	// Used in similarity part
	private static double[] symFactor;
	double[] min;
	double[] max;
	boolean labelNum = false;
	private ArrayList<Databean> trainData;
	private ArrayList<Databean> testData;
	private HashMap<Integer, HashMap<String, Integer>> indexMaps;
	private HashMap<Integer, double[][]> matrixMaps;

	public KNN(String trainFile, String testFile,
			HashMap<Integer, double[][]> matrixMaps,
			HashMap<Integer, HashMap<String, Integer>> indexMaps,
			double[] numFactor, double[] symFactor, int k) {
		KNN.k = k;
		this.matrixMaps = matrixMaps;
		this.indexMaps = indexMaps;

		KNN.numFactor = numFactor;
		KNN.symFactor = symFactor;

		this.trainData = parseTrainData(trainFile);
		this.testData = parseTestData(testFile);

		train();
	}

	/**
	 * cross validation part, kFold times and each time 1 / kFold of the
	 * training data is used as testing data. weight will be adjusted to get
	 * sound accuracy
	 */
	private void train() {
		double globalErrorRate = 0.0;
		setBaseValues(this.trainData);
		// There will be five rounds, each picks 1/5 as testing data
		int slice = this.trainData.size() / kFold;
		for (int i = 0; i < kFold; i++) {
			ArrayList<Databean> trainingSubset, testingSubset, trainingRealData, testingRealData;
			trainingSubset = new ArrayList<Databean>();
			testingSubset = new ArrayList<Databean>();
			// in order not to harm the original data, deep copy to new list
			trainingRealData = new ArrayList<Databean>();
			testingRealData = new ArrayList<Databean>();

			// extract training data (1 / kFold) set and testing data set
			for (int index = 0; index < this.trainData.size(); index++) {
				if (index / (slice) == i) {
					testingSubset.add(this.trainData.get(index));
				} else {
					trainingSubset.add(this.trainData.get(index));
				}
			}
			// normalize the data to fall between 0 and 1
			normalize(trainingSubset, trainingRealData);
			normalize(testingSubset, testingRealData);

			// get the accumulative error number
			double curErrorNum = 0;

			for (Databean testBean : testingRealData) {
				// priority queue is used to get the closest
				// k neighbors
				Queue<Node> queue = new PriorityQueue<Node>();
				for (Databean trainBean : trainingRealData) {
					queue.offer(getSimilarity(trainBean, testBean));
				}

				if (!this.labelNum) { // if the label is a categorical value
					// use hashmap to calculate the total similarity for each
					// category
					HashMap<String, Double> map = new HashMap<String, Double>();
					for (int index = 0; index < queue.size() && index < k; index++) {
						Node temp = queue.poll();
						if (map.containsKey(temp.getLabel())) {
							map.put(temp.getLabel(), map.get(temp.getLabel())
									+ temp.getSimilarity());
						} else {
							map.put(temp.getLabel(), temp.getSimilarity());
						}
					}
					String classBelong = null;
					double sim = -1;
					for (Map.Entry<String, Double> pair : map.entrySet()) {
						if (pair.getValue() > sim
						// when there is a tie, break it by category name
								|| (pair.getValue() == sim && pair.getKey()
										.compareTo(classBelong) < 0)) {
							sim = pair.getValue();
							classBelong = pair.getKey();
						}
					}

					if (!testBean.getLabel().equals(classBelong)) {

						curErrorNum++;
					}
				} else { // when the target attribute is contiguous
					double productAccum = 0;
					double simAccum = 0;
					for (int index = 0; index < queue.size() && index < k; index++) {
						Node temp = queue.poll();
						productAccum += Double.parseDouble(temp.getLabel())
								* temp.getSimilarity();
						simAccum += temp.getSimilarity();
					}
					double predictedValue = productAccum / simAccum;
					// for contiguous target value, calculate the MSE
					curErrorNum += Math.pow(
							predictedValue
									- Double.parseDouble(testBean.getLabel()),
							2);
				}

			}

			globalErrorRate += (curErrorNum / testingSubset.size());
		}
		// get the average error rate/MSE
		double avgErrorRate = globalErrorRate / kFold;
		System.out.println("*************");
		if (!this.labelNum) {
			System.out.println("For the training, current error rate is: "
					+ avgErrorRate * 100 + "%");
			System.out.println("For the training, current accuracy rate is: "
					+ (1 - avgErrorRate) * 100 + "%");
		} else {
			System.out.println("MSE: " + avgErrorRate);
		}

		System.out.println("Training is done!!!");
		System.out.println("*************");

	}

	/**
	 * use the parameters we are confident of to predict on new data
	 */
	public void predict() {
		ArrayList<Databean> trainingRealData, testingRealData;
		trainingRealData = new ArrayList<Databean>();
		testingRealData = new ArrayList<Databean>();
		setBaseValues(this.trainData);
		normalize(this.trainData, trainingRealData);
		normalize(this.testData, testingRealData);

		// make sure each numerical value falls between 0 and 1
		// if a value is above 1 then make it 1
		// if a value is below 0 then make it 0
		for (Databean bean : testingRealData) {
			ArrayList<Double> sample = bean.getNumList();
			for (int i = 0; i < sample.size(); i++) {
				if (sample.get(i) > 1) {
					sample.set(i, 1.0);
				}

				if (sample.get(i) < 0) {
					sample.set(i, 0.0);
				}
			}
		}

		for (int i = 0; i < testingRealData.size(); i++) {

			Databean testBean = testingRealData.get(i);
			Queue<Node> queue = new PriorityQueue<Node>();
			for (Databean trainBean : trainingRealData) {
				queue.offer(getSimilarity(trainBean, testBean));
			}

			if (!this.labelNum) { // if the label is a categorical value
				HashMap<String, Double> map = new HashMap<String, Double>();
				for (int index = 0; index < queue.size() && index < k; index++) {
					Node temp = queue.poll();
					if (map.containsKey(temp.getLabel())) {
						map.put(temp.getLabel(), map.get(temp.getLabel())
								+ temp.getSimilarity());
					} else {
						map.put(temp.getLabel(), temp.getSimilarity());
					}
				}
				String classBelong = null;
				double sim = -1;
				for (Map.Entry<String, Double> pair : map.entrySet()) {
					if (pair.getValue() > sim
							|| (pair.getValue() == sim && pair.getKey()
									.compareTo(classBelong) < 0)) {
						sim = pair.getValue();
						classBelong = pair.getKey();
					}
				}
				System.out.println(testData.get(i) + classBelong);

			} else {
				double productAccum = 0;
				double simAccum = 0;
				for (int index = 0; index < queue.size() && index < k; index++) {
					Node temp = queue.poll();
					productAccum += Double.parseDouble(temp.getLabel())
							* temp.getSimilarity();
					simAccum += temp.getSimilarity();
				}
				System.out.print(testData.get(i));
				System.out.println(productAccum / simAccum);
			}

		}

		System.out.println("Predicting is done!!!");

	}

	/**
	 * 
	 * @param trainBean
	 * @param testBean
	 * @return a node containing the its similarity with a testing entry and its
	 *         category
	 */
	private Node getSimilarity(Databean trainBean, Databean testBean) {
		double sum = 0.0;

		// For numeric distances
		for (int i = 0; i < trainBean.getNumList().size(); i++) {
			double temp = trainBean.getNumList().get(i)
					- testBean.getNumList().get(i);
			sum += Math.pow(temp, 2);
		}
		
		// for discrete attributes
		for (int i = 0; i < trainBean.getSymList().size(); i++) {
			double[][] matrix = this.matrixMaps.get(i);
			HashMap<String, Integer> curIndexMap = this.indexMaps.get(i);
			int row = curIndexMap.get(trainBean.getSymList().get(i));
			int col = curIndexMap.get(testBean.getSymList().get(i));
			double sim = matrix[row][col];
			// discrete attributes' weighs are integrated here
			sum += symFactor[i] * (1.0 - sim);
		}
		
		// get the similarity and encapsulate into a node
		double value = 1 / Math.sqrt(sum);
		String classBelong = trainBean.getLabel();
		return new Node(value, classBelong);
	}
	
	/**
	 * 
	 * @param list original data list
	 * @param realList new list after normalization
	 */
	private void normalize(ArrayList<Databean> list,
			ArrayList<Databean> realList) {
		for (Databean bean : list) {
			Databean newBean = new Databean();
			realList.add(newBean);
			for (String str : bean.getSymList()) {
				newBean.getSymList().add(str);
			}
			ArrayList<Double> numricList = bean.getNumList();
			for (int i = 0; i < numricList.size(); i++) {
				double value = numricList.get(i);
				value = (value - min[i]) / (max[i] - min[i]);

				// put weight here for numeric attributes
				newBean.getNumList().add(value * numFactor[i]);
			}
			newBean.setLabel(bean.getLabel());
		}

	}
	
	/**
	 * 
	 * @param trainingSubset the training data set
	 * the max and min value of each attribute of training data entries
	 * will be record for normalization
	 */
	private void setBaseValues(ArrayList<Databean> trainingSubset) {
		int size = trainingSubset.get(0).getNumList().size();

		for (Databean bean : trainingSubset) {
			for (int i = 0; i < size; i++) {
				min[i] = Math.min(min[i], bean.getNumList().get(i));
				max[i] = Math.max(max[i], bean.getNumList().get(i));
			}
		}

	}
	
	/**
	 * 
	 * @param testFile fill path of testing data
	 * @return a list of data beans
	 */
	private ArrayList<Databean> parseTestData(String testFile) {
		int labelIndex = this.trainData.get(0).getNumList().size()
				+ this.trainData.get(0).getSymList().size();
		ArrayList<Databean> list = new ArrayList<Databean>();

		BufferedReader reader = null;
		int count = 0;
		boolean dataMark = false;
		try {
			reader = new BufferedReader(new FileReader(testFile));
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.equals("@data")) {
					dataMark = true;
				} else if (dataMark) {
					count++;
					String[] arr = line.split(",");
					Databean bean = new Databean();

					for (int i = 0; i < arr.length && i < labelIndex; i++) {
						String item = arr[i];
						try {
							Double value = Double.parseDouble(item);
							bean.getNumList().add(value);
						} catch (NumberFormatException e) {
							bean.getSymList().add(item);
						}

					}
					list.add(bean);
				}

			}
			System.out.println("***** find " + count + " testing data items");
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find the file");
		} catch (IOException e) {
			System.err.println("Error while reading line");
		}

		return list;
	}

	private ArrayList<Databean> parseTrainData(String trainDataPath) {
		ArrayList<Databean> list = new ArrayList<Databean>();

		BufferedReader reader = null;
		int count = 0;
		boolean dataMark = false;
		try {
			reader = new BufferedReader(new FileReader(trainDataPath));
			String line = null;
			int nonNumIndex = 0;
			int numIndex = 0;

			while ((line = reader.readLine()) != null) {
				if (line.equals("@data")) {
					dataMark = true;
				} else if (dataMark) {
					count++;
					// System.out.println(line);
					String[] arr = line.split(",");
					Databean bean = new Databean();

					for (int i = 0; i < arr.length; i++) {
						String item = arr[i];
						if (i == arr.length - 1) {
							bean.setLabel(item);
							continue;
						}

						try {
							Double value = Double.parseDouble(item);
							bean.getNumList().add(value);
						} catch (NumberFormatException e) {
							bean.getSymList().add(item);
						}

					}
					list.add(bean);
				} else if (line.trim().length() != 0) {
					String[] arr = line.split("\\s+");

					if (arr.length != 0 && arr[1].equalsIgnoreCase("label")
							&& arr[2].equalsIgnoreCase("real")) {
						this.labelNum = true;
					} else if (arr.length != 0
							&& !arr[1].equalsIgnoreCase("label")
							&& arr[0].equals("@attribute")) {

						if (arr[2].equalsIgnoreCase("real")) {
							numIndex++;
						} else {
							nonNumIndex++;
						}

					}
				}
			}

			this.min = new double[numIndex];
			this.max = new double[numIndex];
			Arrays.fill(min, Double.MAX_VALUE);
			Arrays.fill(max, Double.MIN_VALUE);

			System.out.println("***** find " + count + " training data items");
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find the file");
		} catch (IOException e) {
			System.err.println("Error while reading line");
		}

		return list;
	}

}
