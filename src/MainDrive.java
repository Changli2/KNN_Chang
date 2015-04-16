import java.util.HashMap;

/**
 * 
 * @author Team 14 The main method of the code, User should run from here. All
 *         three problems are going to be solved
 */
public class MainDrive {

	public static void main(String[] args) {
		HashMap<Integer, double[][]> matrixMaps = new HashMap<Integer, double[][]>();
		// pass in the similarity matrixes
		double[][] customerType = { { 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1 } };
		double[][] lifeStyle = { { 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 } };
		matrixMaps.put(0, customerType);
		matrixMaps.put(1, lifeStyle);
		HashMap<Integer, HashMap<String, Integer>> indexMaps = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<String, Integer> mapA0 = new HashMap<String, Integer>();
		mapA0.put("student", 0);
		mapA0.put("engineer", 1);
		mapA0.put("librarian", 2);
		mapA0.put("professor", 3);
		mapA0.put("doctor", 4);
		indexMaps.put(0, mapA0);

		HashMap<String, Integer> mapA1 = new HashMap<String, Integer>();
		mapA1.put("spend<<saving", 0);
		mapA1.put("spend<saving", 1);
		mapA1.put("spend>saving", 2);
		mapA1.put("spend>>saving", 3);
		indexMaps.put(1, mapA1);

		// manually adjust the weights
		double[] numFactor = { 3.0, 12.0, 5.0, 8.0 };
		double[] symFactor = { 2.0, 0.0 };
		System.out.println("************ Part A begines! ************");
		// initialize an instance
		KNN partA = new KNN("randTrainProdSelection.arff",
				"testProdSelection.arff", matrixMaps, indexMaps, numFactor,
				symFactor, 3);
		partA.predict();

		System.out.println("************ Part A is done! ************");

		System.out.println("************ Part B binary begins! ************");
		matrixMaps.clear();
		double[][] serviceType = { { 1.0, 0, 0.1, 0.3, 0.2 },
				{ 0, 1, 0, 0, 0 }, { 0.1, 0, 1, 0.2, 0.2 },
				{ 0.3, 0, 0.2, 1, 0.1 }, { 0.2, 0, 0.2, 0.1, 1 } };
		double[][] customerType2 = { { 1, 0.2, 0.1, 0.2, 0 },
				{ 0.2, 1, 0.2, 0.1, 0 }, { 0.1, 0.2, 1, 0.1, 0 },
				{ 0.2, 0.1, 0.1, 1, 0 }, { 0, 0, 0, 0, 1 } };
		double[][] sizeType = { { 1, 0.1, 0 }, { 0.1, 1, 0.1 }, { 0, 0.1, 1 } };
		double[][] promType = { { 1, 0.8, 0, 0 }, { 0.8, 1, 0.1, 0.5 },
				{ 0, 0.1, 1, 0.4 }, { 0, 0.5, 0.4, 1 } };
		matrixMaps.put(0, serviceType);
		matrixMaps.put(1, customerType2);
		matrixMaps.put(2, sizeType);
		matrixMaps.put(3, promType);
		double[] numFactor2 = { 2.0, 2.0, 0.0, 2.0 };
		double[] symFactor2 = { 1.0, 1.0, 1.0, 0.0 };
		indexMaps.clear();
		HashMap<String, Integer> map0 = new HashMap<String, Integer>();
		map0.put("Loan", 0);
		map0.put("Bank_Account", 1);
		map0.put("CD", 2);
		map0.put("Mortgage", 3);
		map0.put("Fund", 4);
		indexMaps.put(0, map0);

		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put("Business", 0);
		map1.put("Professional", 1);
		map1.put("Student", 2);
		map1.put("Doctor", 3);
		map1.put("Other", 4);
		indexMaps.put(1, map1);

		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("Small", 0);
		map2.put("Medium", 1);
		map2.put("Large", 2);
		indexMaps.put(2, map2);

		HashMap<String, Integer> map3 = new HashMap<String, Integer>();
		map3.put("Full", 0);
		map3.put("Web&Email", 1);
		map3.put("Web", 2);
		map3.put("None", 3);
		indexMaps.put(3, map3);
		KNN partBBinary = new KNN("randTrainProdIntro.binary.arff",
				"testProdIntro.binary.arff", matrixMaps, indexMaps, numFactor2,
				symFactor2, 5);
		partBBinary.predict();
		System.out.println("************ Part B binary is done! ************");

		System.out.println("************ Part B real begins! ************");
		 KNN partBReal = new KNN("randTrainProdIntro.real.arff",
		 "testProdIntro.real.arff", matrixMaps, indexMaps, numFactor2,
		 symFactor2, 5);
		 partBReal.predict();
		System.out.println("************ Part B real is done! ************");

	}

}
