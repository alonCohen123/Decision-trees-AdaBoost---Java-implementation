import java.util.Arrays;
import java.util.HashMap;

public class DataPoint1 {
	
	private int id;
	//number of dimensions
	private int dimensions;
	//data from the file - the size is set to be the number of dimensions
	private double[] data;
	//class of the point
	private int dataClass;
	//weight
	private double weight=1.0;
	//classified as
	private int classifiedAs;
	/**
	 * constructor
	 * @param id
	 * @param dimensions
	 */
	public DataPoint1(int id, int dimensions){
		this.id=id;
		this.dimensions=dimensions;
		data = new double[dimensions];
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the dataClass
	 */
	public int getDataClass() {
		return dataClass;
	}
	/**
	 * @param dataClass the dataClass to set
	 */
	public void setDataClass(int dataClass) {
		this.dataClass = dataClass;
	}
	
	
	/**
	 * @return the data
	 */
	public double[] getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(double[] data) {
		this.data = data;
	}
	
	public double getValueInDimension(int dimension){
		return data[dimension];
	}

	/**
	 * @return the dimensions
	 */
	public int getDimensions() {
		return dimensions;
	}
	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}
	
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	
	/**
	 * @return the classifiedAs
	 */
	public int getClassifiedAs() {
		return classifiedAs;
	}

	/**
	 * @param classifiedAs the classifiedAs to set
	 */
	public void setClassifiedAs(int classifiedAs) {
		this.classifiedAs = classifiedAs;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataPoint1 [id=" + id + ", dimensions=" + dimensions + ", data=" + Arrays.toString(data)
				+ ", dataClass=" + dataClass + "]";
	}
	
	
	
}
