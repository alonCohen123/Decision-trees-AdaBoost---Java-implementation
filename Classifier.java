import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.crypto.Data;

public abstract class Classifier {
	
	private ArrayList<DataPoint1> data;

	/**
	 * @param data
	 */
	public Classifier(ArrayList<DataPoint1> data2) {
		this.data = data2;
	}

	abstract public NodeTree training(NodeTree n, ArrayList<DataPoint1> data, int dClass);
	
	abstract public int testing (NodeTree root, DataPoint1 dp);
	
	abstract public int getClasses();
	
	/**
	 * 
	 * @param result - print confusion matrix according to the result
	 */
	public void printResult (HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result){
		System.out.println();
		//print confusion matrix
		for(int i=1;i<=result.size();i++)
			System.out.print(" #"+i+"");
		int errors = 0;
		for(int i=1;i<=result.size();i++){
			System.out.println();
			for(int j=1;j<=result.get(i).size();j++)
			{
				//count errors
				if(i!=j)
					errors+=result.get(i).get(j).size();
				System.out.print(result.get(i).get(j).size()+" ");
			}
			System.out.print(" #"+i);
		}
		System.out.println();
		System.out.println();
		//print the score
		System.out.println("Correct Answers Percentage Score :"+(1-(double)errors/(double)data.size()));
		System.out.println();
	}

}
