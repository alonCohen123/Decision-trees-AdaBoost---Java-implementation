import java.awt.List;
import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.Painter;

import org.omg.CORBA.INTERNAL;

public class DecisionTree extends Classifier {
	
	//the data which used to built the tree
	private ArrayList<DataPoint1> data;
	// number of classes
	private int classes=0;
	// map for result matrix :keys: 1- class '1' & true , 2- class '2' & true , 3- class '1' & false ,4- class '2' & false   
	private HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result;
	

	/**
	 * @param data
	 */
	public DecisionTree(ArrayList<DataPoint1> data) {
		super(data);
		this.data = data;
	}

	public void setClasses(int classes){
		this.classes = classes;
	}
	@Override
	public int getClasses(){
		return classes;
	}
	
	/** 
	 * build the tree from training data
	 * @param n - should be initialized with null
	 * @param data - training data to build the tree with
	 * @param dClass - should be initialized with 0;
	 * @return decision tree
	 */
	@Override
	public NodeTree training(NodeTree n, ArrayList<DataPoint1> data, int dClass) {
		//if data is empty create node 
		if(!data.isEmpty()){
			SplitFeature bestSplit = findBestSplit(data);
			if(n==null)//check if this is the root
			{
				NodeTree root =new NodeTree(null, 0, bestSplit.getScore(), bestSplit.getindexI(), bestSplit.getindexJ(), data);
				NodeTree l = training(root, bestSplit.getSplits().get(0), bestSplit.getClassL());
				root.setLeft(l);
				NodeTree r = training(root, bestSplit.getSplits().get(1), bestSplit.getClassR());
				root.setRight(r);
				return root;
			}
			else{//
				//check if need to stop
				if(bestSplit.getScore() > n.getScore() || data.size() < 2 || data.size()==n.getDataList().size() || bestSplit.getClassL() == bestSplit.getClassR()){
					return new NodeTree(n, dClass, 1.0, 0,0, data);
				}
				else{
					NodeTree leaf = new NodeTree(n, dClass, bestSplit.getScore(), bestSplit.getindexI(), bestSplit.getindexJ(), data);
					NodeTree l = training(leaf, bestSplit.getSplits().get(0), bestSplit.getClassL());
					leaf.setLeft(l);
					NodeTree r = training(leaf, bestSplit.getSplits().get(1), bestSplit.getClassR());
					leaf.setRight(r);
					return leaf;
				}
			}
		}
		else return new NodeTree(n, dClass, 1.0, 0,0, data);
	}

	/**
	 * find the best way to split a given data  
	 * @param dataToSplit
	 * @return split data , score and how we split the data
	 */
	public SplitFeature findBestSplit(ArrayList<DataPoint1> dataToSplit){
		ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> bestSplit = new ArrayList<>();
		ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> tmpSplit = new ArrayList<>();
		double scoreMin =1000.0, tmpScore;
		int indexI=0, indexJ=0;
		int dataDimensions = dataToSplit.get(0).getDimensions();
		//for each dimension -> check what is the best split for each value in the dimension
		for(int j=0; j<dataDimensions;j++){
			for(int i=0;i<dataToSplit.size();i++){;
				tmpSplit = split(dataToSplit.get(i).getId(),j,dataToSplit);
				tmpScore = score(tmpSplit);
				if(tmpScore <= scoreMin){
					scoreMin = tmpScore;
					bestSplit = tmpSplit;
					indexI = dataToSplit.get(i).getId()-1;
					indexJ = j;
				}
			}
		}
		SplitFeature sf = new SplitFeature(scoreMin, indexI, indexJ , classes);
		sf.setSplits(bestSplit);
		return sf;
	}
	
	/**
	 * 
	 * @param i - index of the data point
	 * @param j - index of the feature
	 * @param dataToSplit -array of data points
	 * @return array of split data by classes
	 */
	public ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> split(int i,int j,ArrayList<DataPoint1> dataToSplit){
		ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> result = new ArrayList<>();
		HashMap<Integer, ArrayList<DataPoint1>> left = new HashMap<>();
		HashMap<Integer, ArrayList<DataPoint1>> right = new HashMap<>();
		double valueToSplit = data.get(i-1).getValueInDimension(j);
		//initialize arrays for result
		for(int indx=1; indx<=classes ; indx++){
			left.put(indx, new ArrayList<DataPoint1>());
			right.put(indx, new ArrayList<DataPoint1>());
		}
		//for each point in data set it in the correct array
		for (DataPoint1 dp : dataToSplit){
			if(dp.getValueInDimension(j) >= valueToSplit)
				left.get(dp.getDataClass()).add(dp);
			else
				right.get(dp.getDataClass()).add(dp);
		}
		result.add(left);
		result.add(right);
		return result;
	}

	/**
	 * 
	 * @param tmpSplit - split data by classes
	 * @return the score of the split by % mistakes
	 */
	public double score(ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> tmpSplit){
		double classSizeL = 0;
		double classSizeR = 0;
		double dataSizeL = 0;
		double dataSizeR = 0;
		double maxClassL= 0;
		double maxClassR= 0;
		HashMap<Integer, ArrayList<DataPoint1>>left = tmpSplit.get(0);
		HashMap<Integer, ArrayList<DataPoint1>>right = tmpSplit.get(1);
		//for each point in each class - summarize the weight and check which class is the major one
		for(int i=1; i<=classes;i++){
			classSizeL =0;
			if(!(left.get(i)== null))
				for (DataPoint1 dp : left.get(i)) {
					classSizeL += dp.getWeight();			
				}
			dataSizeL+=classSizeL;
			if(maxClassL < classSizeL)
				maxClassL = classSizeL;
			
			classSizeR =0;
			if(!(right.get(i)== null))
				for (DataPoint1 dp : right.get(i)) {
					classSizeR += dp.getWeight();			
				}
			dataSizeR+=classSizeR;
			if(maxClassR < classSizeR)
				maxClassR = classSizeR;
		}
		//calculate the error rate in this split
		double score = ((dataSizeL-maxClassL)/dataSizeL)*(dataSizeL/(dataSizeL+dataSizeR))+((dataSizeR-maxClassR)/dataSizeR)*(dataSizeR/(dataSizeL+dataSizeR));
		return score;
	}
	
	/**
	 * 
	 * @param root - decision tree to test
	 * @param dp - data point to test
	 * @return - class number
	 */
	@Override
	public int testing (NodeTree root, DataPoint1 dp){
		NodeTree tree = root;
		int dClass=0,i,j;
		double valToCompare = 0.0;
		if(tree==null)
			return dClass;
		while(tree != null){
			dClass = tree.getdClass();
			i = tree.getSplitByIndexI();
			j = tree.getSplitByIndexJ();
			valToCompare = data.get(i).getValueInDimension(j);
			//decide which way to go in the decision tree
			if(dp.getValueInDimension(j) < valToCompare)
				tree = tree.getRight();
			else tree = tree.getLeft();
			
		}
		return dClass;
	}
	
	public void printTree(NodeTree tree){
		if(tree==null)
			return;
		System.out.println(tree);
		printTree(tree.getLeft());
		printTree(tree.getRight());
	}
	/**
	 * 
	 * @param tree - the decision tree we want to get the score of
	 * @param data - the data we want to test with
	 */
	public void computeScore(NodeTree tree, ArrayList<DataPoint1> data){
		System.out.println("========NEW TREE========");
		//initialize data structure for confusion matrix
		HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result = new HashMap<>();
		for(int i=1;i<=classes;i++){
			HashMap<Integer, ArrayList<DataPoint1>> h = new HashMap<>();
			for(int j=1;j<=classes;j++){
				ArrayList<DataPoint1> arr = new ArrayList<>();
				h.put(j, arr);
			}
			result.put(i, h);
		}
		int dClass=0 , tClass=0;
		//put each point in the right cell according to the testing result
		for (DataPoint1 dp : data) {
			dClass = dp.getDataClass();
			tClass = testing(tree, dp);
			result.get(tClass).get(dClass).add(dp);
		}
		//print the confusion matrix and the final score
		printResult(result);
		this.result=result;
	 }
	
	public HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> getResultHash (){
		return this.result;
	}
	
	
	/**
	 * unused function
	 * supposed to be the better function to split the data but no success
	 * 
	 */
	public SplitFeature findBestSplit2(ArrayList<DataPoint1> data){
		//initialize
		HashMap<Integer, ArrayList<DataPoint1>> hL = new HashMap<>();
		HashMap<Integer, Double>wL = new HashMap<>();
		HashMap<Integer, ArrayList<DataPoint1>> hR = new HashMap<>();
		HashMap<Integer, Double>wR = new HashMap<>();
		
		for(int i=1; i<=classes; i++){
			hL.put(i,new ArrayList<DataPoint1>());
			hR.put(i,new ArrayList<DataPoint1>());
			wL.put(i, 0.0);
			wR.put(i, 0.0);
		}
		
		//initialize left part
		int classL = 0,classR = 0,cL=0,cR=0;
		double maxWeightL=0.0, maxWeightR=0.0;
		double totalL=0,totalR=0;
		int indI=0,indJ=0;
		double minScore =1.0 , tmpScore=0.0; 
		ArrayList<HashMap<Integer,ArrayList<DataPoint1>>> res = new ArrayList<>();
		ArrayList<ArrayList<DataPoint1>> result= new ArrayList<>();
		ArrayList<DataPoint1> left = new ArrayList<>(data);
		int dataDimensions = data.get(0).getDimensions();
		//for each dimension , sort and move dp by dp to the second array
		for(int j=0; j<dataDimensions; j++){
			
			//reset the data structure
			for(int i=1; i<=classes; i++){
				hL.get(i).clear();
				hR.get(i).clear();
				wL.put(i, 0.0);
				wR.put(i, 0.0);
			}
			
			left = sortDP(data, j);
			//insert point to the data structures
			for(DataPoint1 dp : data){
				hL.get(dp.getDataClass()).add(dp);
				wL.put(dp.getDataClass() , wL.get(dp.getDataClass())+dp.getWeight());
			}
			//calculate the min score
			totalL=0;totalR=0;
			for(int i=1;i<=classes;i++){
				if(maxWeightL<wL.get(i)){
					maxWeightL=wL.get(i);
					classL =j;
				}
				totalL+=wL.get(i);
			}
			tmpScore =(totalL-maxWeightL)/totalL ;
						
			for(int i=0; i<left.size(); i++){
				if(i==599){
					System.out.println("stop");
				}
				DataPoint1 dp = left.get(i);
				//transfer from left size to right size
				 wL.put(dp.getDataClass(), wL.get(dp.getDataClass())-dp.getWeight());
				 hL.get(dp.getDataClass()).remove(dp);
				 wR.put(dp.getDataClass(), wR.get(dp.getDataClass())+dp.getWeight());
				 hR.get(dp.getDataClass()).add(dp);
				 //calculate the score
				 totalL=0; totalR=0; maxWeightL=0.0; maxWeightR=0.0;
					for(int x=1;x<=classes;x++){
						if(maxWeightL<wL.get(x)){
							maxWeightL=wL.get(x);
							classL =x;
						}
						if(maxWeightR<wR.get(x)){
							maxWeightR=wR.get(x);
							classR =x;
						}
						totalL+=wL.get(x);
						totalR+=wR.get(x);
					}
					tmpScore = ((totalL-maxWeightL)/totalL)*(totalL/(totalL+totalR))+((totalR-maxWeightR)/totalR)*(totalR/(totalL+totalR));
				 
				 //check if it is the min Score
					if(minScore >= tmpScore){
						minScore = tmpScore;
						indI = dp.getId()-1;
						indJ = j;
						res.clear();
						/******need investigation !!!!************/
						HashMap<Integer,ArrayList<DataPoint1>> l = new HashMap<>();
						l.putAll(hL);
						HashMap<Integer,ArrayList<DataPoint1>> r = new HashMap<>();
						r.putAll(hR);
						res.add(l);
						res.add(r);
						/******need investigation !!!!************/
						cL=classL;
						cR=classR;
					}				
			}
		
		}
		SplitFeature sf = new SplitFeature(minScore, indI, indJ, classes,cL ,cR);
		sf.setSplits2(res);
		return sf;
	}
	
	/**
	 * @param data
	 * @param j - index of dimension
	 * @return data sorted by dimension j
	 */
	public ArrayList<DataPoint1> sortDP(ArrayList<DataPoint1> data , int j){
		data.sort(new java.util.Comparator<DataPoint1>() {
		    public int compare(DataPoint1 a, DataPoint1 b) {
		        return Double.compare(a.getValueInDimension(j),b.getValueInDimension(j));
		    }
		});
		return data;
	}

}
