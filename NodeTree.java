import java.util.ArrayList;
import java.util.Arrays;

public class NodeTree {
	private NodeTree parent;
	private NodeTree left;
	private NodeTree right;
	
	//class which represent this node
	private int dClass;
	//splitting score of this node
	private double score;
	// row number from the data file we split by*/
	private int splitByIndexI;
	// column number from the data file we split by*/
	private int splitByIndexJ;
	// the data we split in this node 
	private ArrayList<DataPoint1> dataList;
	


	/**
	 * @param parent
	 * @param dClass
	 * @param score
	 * @param splitByIndexI
	 * @param splitByIndexJ
	 * @param dataList
	 */
	public NodeTree(NodeTree parent, int dClass, double score, int splitByIndexI, int splitByIndexJ,
			ArrayList<DataPoint1> dataList) {
		this.parent = parent;
		this.dClass = dClass;
		this.score = score;
		this.splitByIndexI = splitByIndexI;
		this.splitByIndexJ = splitByIndexJ;
		this.dataList = dataList;
	}

	public NodeTree getParent() { return parent; }

	public void setParent(NodeTree parent) { this.parent = parent; }

	public NodeTree getLeft() { return left; }

	public void setLeft(NodeTree left) { this.left = left; }

	public NodeTree getRight() { return right; }

	public void setRight(NodeTree right) { this.right = right; }

	public int getdClass() { return dClass; }

	public void setdClass(int dClass) { this.dClass = dClass; }

	public double getScore() { return score; }

	public void setScore(double score) { this.score = score; }

	public ArrayList<DataPoint1> getDataList() { return dataList; }

	public void setDataList(ArrayList<DataPoint1> dataList) { this.dataList = dataList; }

	/**
	 * @return the splitByIndexI
	 */
	public int getSplitByIndexI() {
		return splitByIndexI;
	}

	/**
	 * @param splitByIndexI the splitByIndexI to set
	 */
	public void setSplitByIndexI(int splitByIndexI) {
		this.splitByIndexI = splitByIndexI;
	}

	/**
	 * @return the splitByIndexJ
	 */
	public int getSplitByIndexJ() {
		return splitByIndexJ;
	}

	/**
	 * @param splitByIndexJ the splitByIndexJ to set
	 */
	public void setSplitByIndexJ(int splitByIndexJ) {
		this.splitByIndexJ = splitByIndexJ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeTree [dClass=" + dClass + ", score=" + score + ", splitByIndexI="
				+ splitByIndexI + ", splitByIndexJ=" + splitByIndexJ + ", dataList=" + dataList.size() + "]";
	}


	
}
