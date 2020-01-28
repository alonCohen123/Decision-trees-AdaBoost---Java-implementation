import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		//initialize arraylists
		ArrayList<DataPoint1> dataPoints1 ;
		ArrayList<DataPoint1> dataPoints2 ;
		ArrayList<DataPoint1> dataPoints3 ;
		//get data points from files into the array lists
		String filePath1 = "data1.csv";
		dataPoints1=readCsv(filePath1);
		String filePath2 = "data2.csv";
		dataPoints2=readCsv(filePath2);
		String filePath3 = "data3.csv";
		dataPoints3=readCsv(filePath3);
		
		ArrayList<ArrayList<DataPoint1>> files = new ArrayList<>();
		files.add(dataPoints1);
		files.add(dataPoints2);
		files.add(dataPoints3);
		//result of the first decision tree (for the scatter dots reports)
		HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result = new HashMap<>();
		Scanner sc = new Scanner(System.in);
		boolean flag=true;
		int numOfClasses=0 ,n;
		for (ArrayList<DataPoint1> file : files) {
			System.out.println("=========================");
			System.out.println("=========New File========");
			System.out.println("=========================");
			System.out.println();
			//get the number of different classes in file
			numOfClasses = numOfClasses(file);
			
			DecisionTree decTree = new DecisionTree(file);
			decTree.setClasses(numOfClasses);
			//1) create Decision tree 
			NodeTree tree = decTree.training(null, file, 0);
			decTree.computeScore(tree, file);
			//get the result for the scatter dots reports
			if(flag){
				result = decTree.getResultHash();
				writeToCsv(result);
			}
			//2) cross validation on decision tree classifier
			System.out.println("Please enter number of CV folds :");
			n = sc.nextInt();
			CrossValidation c = new CrossValidation(n, decTree, file);
			c.crossValidation();
			//3) create Adaboost
			Adaboost ab = new Adaboost(file, numOfClasses, decTree);
			System.out.println("==========Adaboost==========");
			ab.training(null, file, 0);
			ab.computeAdaboost();
			if(flag){
				flag=false;
				result = decTree.getResultHash();
				//write to csv the second report
				ab.writeToCSV1("test2.csv");
			}
			//cross validation on adaboost classifier
			System.out.println("Please enter number of CV folds :");
			n = sc.nextInt();
			c.setK(n);
			c.setClassifier(ab);
			c.crossValidation();
		}

	}
	
	/**
	 * 
	 * @param filePath
	 * @return arraylist of data points
	 */
	public static ArrayList<DataPoint1> readCsv (String filePath){
		ArrayList<DataPoint1> dataToReturn = new ArrayList<>();
		BufferedReader reader = null;
		try{
			
			String line = "";
			reader = new BufferedReader(new FileReader(filePath));
			reader.readLine();
			int id=1;
			while((line= reader.readLine()) != null){
				String[] fields = line.split(",");
				if(fields.length > 0){
						DataPoint1 p2 = new DataPoint1(id, fields.length-1);
						double[] data = new double[fields.length-1];
						for(int i=0;i<fields.length-1;i++)
						{
							data[i] = Double.parseDouble(fields[i]);
						}
						p2.setData(data);
						p2.setDataClass(Integer.parseInt(fields[fields.length-1]));
						dataToReturn.add(p2);					
				}
				id++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try{
				reader.close();
				return dataToReturn;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dataToReturn;
	}
	
	/**
	 * write to csv the result of the first tree
	 * @param result - result hash map 
	 */
	public static void writeToCsv(HashMap<Integer,HashMap<Integer, ArrayList<DataPoint1>>> result){
		 PrintWriter pw;
		try {
			pw = new PrintWriter(new File("test.csv"));
			StringBuilder sb = new StringBuilder();
	        sb.append("X");
	        sb.append(',');
	        sb.append("Y");
	        sb.append(',');
	        sb.append("Class");
	        sb.append(',');
	        sb.append("Classified As");
	        sb.append('\n');
	       	//for each point put the values in each dimension , class , classified as        
	        for (int i=1;i<=2;i++) {
				for(int j=1;j<=2;j++){
					for (DataPoint1 dp : result.get(i).get(j)) {
						sb.append(dp.getValueInDimension(0));
						sb.append(',');
						sb.append(dp.getValueInDimension(1));
						sb.append(',');
						sb.append(j);
						sb.append(',');
						sb.append(i);
						sb.append('\n');
					}
					
				}
			}
	        pw.write(sb.toString());
	        pw.close();
	        System.out.println("CSV File was created successfully!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	        
	}
	
	/**
	 * 
	 * @param data
	 * @return number of different classes in the data
	 */
	public static int numOfClasses (ArrayList<DataPoint1> data){
		HashSet<Integer> numOfClasses=new HashSet<>();
		for (DataPoint1 dp : data) {
			numOfClasses.add(dp.getDataClass());
		}
		return numOfClasses.size();
	}
}
