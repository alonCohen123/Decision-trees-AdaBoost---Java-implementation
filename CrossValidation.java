import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.tree.TreeNode;

public class CrossValidation {
   
    //The number of rounds of cross validation.
    private int k;
    //data to analysis
    private ArrayList<DataPoint1> data;
    //classifier 
    private Classifier classifier;
	/**
	 * @param k
	 * @param data
	 */
	public CrossValidation(int k, Classifier c, ArrayList<DataPoint1> data) {
		this.k = k;
		this.classifier = c;
		this.data = data;
		Collections.shuffle(this.data);
	}
	
	public void setClassifier(Classifier c)
	{
		this.classifier = c;
	}
	public void setK(int k){
		this.k=k;
	}

    /**
     * cross validation implementation
     */
	public void crossValidation (){
		//initialize data structure for confusion matrix
		int classes = classifier.getClasses();
		HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result = new HashMap<>();
		for(int i=1;i<=classes;i++){
			HashMap<Integer, ArrayList<DataPoint1>> h = new HashMap<>();
			for(int j=1;j<=classes;j++){
				ArrayList<DataPoint1> arr = new ArrayList<>();
				h.put(j, arr);
			}
			result.put(i, h);
		}
		
		int tclass=0;
		int size = data.size()/k , index=0 ;
		ArrayList<DataPoint1> train = new ArrayList<>(data.size()-size);
		ArrayList<DataPoint1> test = new ArrayList<>(size);
		//create k folds
		System.out.println("Cross Validation score:");
		for(int i=0; i<k; i++){
			index =i*size;
			train = (ArrayList<DataPoint1>) data.clone();
			//create test collection by index
			for(int j=0 ;j<size; j++){
				test.add(data.get(index+j));	
			}
			train.removeAll(test);
			NodeTree tree = classifier.training(null, train, 0);
			//validation part 
			for (DataPoint1 dp : test) {
				tclass = classifier.testing((NodeTree) tree, dp);
				result.get(tclass).get(dp.getDataClass()).add(dp);	
			}
			train.clear();
			test.clear();
		}
		classifier.printResult(result);
	  }    
    
}