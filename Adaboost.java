import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Adaboost extends Classifier{
	private ArrayList<NodeTree> trees;
	private ArrayList<Double> alpha;
	private ArrayList<DataPoint1> data;
	private int classes;
	private DecisionTree classifier;

	/**
	 * @param data
	 * @param classes
	 * @param classifier
	 */
	public Adaboost(ArrayList<DataPoint1> data, int classes, DecisionTree classifier) {
		super(data);
		this.data = data;
		this.classes = classes;
		this.classifier = classifier;
		trees = new ArrayList<>();
		alpha = new ArrayList<>();
	}
	/**
	 * adaboost training - creating trees: each tree is based on the previous tree
	 * by setting different weights for data points.
	 */
	@Override
	public NodeTree training(NodeTree n, ArrayList<DataPoint1> data, int dClass) {
		int i = 0;
		double treeScore ,terror ,avgScore =0.0 ;
		Queue<Double> q = new LinkedList<Double>();
		NodeTree tree;
		while(i<40){
			//build tree
			tree = classifier.training(null, data, 0);
			//compute error rate
			terror = computeError(tree);
			//compute tree score
			treeScore = Math.log((1-terror)/terror);
			//add the tree and its score to the lists
			trees.add(tree);
			alpha.add(treeScore);
			//stop rule
			if(q.size()>10){
				//stop adaboost when:
				if(Math.abs(avgScore/10 - treeScore)<0.025)
					break;
				avgScore-=q.remove();
			}
			q.add(treeScore);
			avgScore+=treeScore;
			//set weights for data points
			for (DataPoint1 dp : data) {
				if(dp.getClassifiedAs() != dp.getDataClass()){
					dp.setWeight(dp.getWeight()*Math.exp(treeScore));
				}
			}		
			i++;
		}
		resetWeights();
		return null;
	}
	
	public void resetWeights(){
		//reset the weights
		for(DataPoint1 dp : data){
			dp.setWeight(1.0);
		}
	}
	
	/**
	 * 
	 * @param tree
	 * @return tree error rate
	 */
	public double computeError(NodeTree tree){
		double error = 0.0 ,totalWeights=0.0;
		int tClass;
		//for each data point - check if it was classified correct
		for (DataPoint1 dp : data) {
			tClass = classifier.testing(tree, dp);
			dp.setClassifiedAs(tClass);
			if(tClass!=dp.getDataClass()){
				error+=dp.getWeight();
			}
			totalWeights+=dp.getWeight();
		}
		error = error/totalWeights;
		return error;
	}
	/**
	 * @param dp - data point to test
	 * @param mode - null
	 * @return the class of the dp based on all trees and weights
	 */
	@Override
	public int testing(NodeTree root, DataPoint1 dp) {
		double[] classesScore = new double[classes+1];
		int bestClass = 0;
		double maxClass = 0.0;
		//for each class summarize the weights
		for (NodeTree tree : trees) {
			double tmp = alpha.get(trees.indexOf(tree));
			classesScore[classifier.testing(tree, dp)]+=tmp;
		}
		//check which class has more weights - this class will be the result
		for(int i=1;i<=classes;i++){
			if(classesScore[i]>maxClass){
				bestClass=i;
				maxClass=classesScore[i];
			}
		}
		return bestClass;
	}
	/**
	 * compute the score of the adaboost
	 */
	public void computeAdaboost(){
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
		//for each data point - put it in the correct 'cell' 
		int tClass = 0;
		for (DataPoint1 dp : data) {
			tClass = testing(null, dp);
			result.get(tClass).get(dp.getDataClass()).add(dp);
		}
		//print matrix
		printResult(result);
		System.out.println("Number of trees : "+trees.size());
		System.out.println();
		
	}
	
	public void writeToCSV1(String fileName){
		 PrintWriter pw;
		 double[] classesScore = new double[classes+1];
		 double maxScore=0;
		 int maxClass=0, countErr=0;
			try {
				pw = new PrintWriter(new File(fileName));
				StringBuilder sb = new StringBuilder();
		       	//for each tree put the alpha and the score of the classifier till this tree  
				sb.append("ID");
	       		sb.append(',');
	       		sb.append("Alpha");
	       		sb.append(',');
	       		sb.append("Score");
				sb.append('\n');
		       	for(int i=0;i<trees.size();i++){
		       		for(DataPoint1 dp : data){
		       			for(int j=0;j<=i;j++){
		       				double tmp = alpha.get(j);
		       				classesScore[classifier.testing(trees.get(j), dp)]+=tmp;
		       			}
		       			//check what is the class of the point
		       			for(int j=1;j<=classes;j++){
		       				if(classesScore[j]>maxScore){
		       					maxScore = classesScore[j];
		       					maxClass = j;
		       				}
		       				//reset
		       				classesScore[j] =0;
		       			}
		       			//if the classification is false count as error
		       			if(dp.getDataClass()!=maxClass){
		       				countErr++;
		       			}
		       			maxScore = 0;
		       		}
		       		sb.append(i);
		       		sb.append(',');
		       		sb.append(alpha.get(i));
		       		sb.append(',');
		       		sb.append((double)countErr/(double)data.size());
					sb.append('\n');
					//reset variables
					countErr=0;
		       	}
		        pw.write(sb.toString());
		        pw.close();
		        System.out.println("CSV File was created successfully!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}

	public int getClasses(){
		return classes;
	} 

}
