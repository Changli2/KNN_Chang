import java.util.ArrayList;

/**
 * 
 * @author Team 14
 * A data type encapsulate a row of the 
 * data
 */
public class Databean {
	private ArrayList<Double> numList;
	private ArrayList<String> symList;
	private String label;
	
	public Databean() {
		numList = new ArrayList<Double>();
		symList = new ArrayList<String>();
	}
	
	public void setLabel(String str) {
		this.label = str;
	}
	
	public ArrayList<Double> getNumList() {
		return this.numList;
	}
	
	public ArrayList<String> getSymList() {
		return this.symList;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		
		for (String item : this.symList) {
			str += item + ",";
		}
		for (Double value : this.numList) {
			str += value + ",";
		}
		
		return str;
	} 
}
