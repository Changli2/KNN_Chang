/**
 * 
 * @author Team 14
 * A data type encapsulates enough info for each neighbor.
 * Two fields are necessary: label & its similarity with a testing entry
 */
public class Node implements Comparable<Node> {
	private String label;
	private double similarity;
	public Node(double sim, String c) {
		this.similarity = sim;
		this.label = c;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public double getSimilarity() {
		return this.similarity;
	}
 	
	@Override
	public int compareTo(Node o) {
		double other = o.similarity;
		double own = this.similarity;
		if (own < other) {
			return 1;
		} else if (own > other) {
			return -1;
		} else {
			return 0;
		}
				
	}

}
