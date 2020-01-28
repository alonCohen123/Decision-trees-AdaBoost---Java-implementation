import java.util.ArrayList;
import java.util.HashMap;

public class SplitFeature {
	// score of the feature
		private double score;
		// row number from the data file
		private int indexI;
		// column number from the data file
		private int indexJ;
		//class of left array
		private int classL;
		//class of righ array
		private int classR;
		// split data
		private ArrayList<ArrayList<DataPoint1>> splits;
		//number of classes
		private int classes;
		
		/**
		 * constructor
		 * @param scoreMin
		 * @param indexI
		 * @param indexJ
		 * @param classes
		 */
		public SplitFeature(double scoreMin, int indexI, int indexJ , int classes) {
			this.score = scoreMin;
			this.indexI = indexI;
			this.indexJ = indexJ;
			this.classes = classes;
			splits = new ArrayList<>();
		}
		
		/**
		 * unused constructor
		 * supposed to help for the better split method
		 */
		public SplitFeature(double scoreMin, int indexI, int indexJ , int classes ,int classL , int classR) {
			this.score = scoreMin;
			this.indexI = indexI;
			this.indexJ = indexJ;
			this.classes = classes;
			this.classL = classL;
			this.classR = classR;
			splits = new ArrayList<>();
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public int getindexI() {
			return indexI;
		}
		
		public void setindexI(int indexI) {
			this.indexI = indexI;
		}
		
		public int getindexJ() {
			return indexJ;
		}
		
		public void setindexJ(int indexJ) {
			this.indexJ = indexJ;
		}

		public ArrayList<ArrayList<DataPoint1>> getSplits() {
			return splits;
		}
		
		
		
		public int getClassL() {
			return classL;
		}

		
		public void setClassL(int classL) {
			this.classL = classL;
		}

		/**
		 * @return the classR
		 */
		public int getClassR() {
			return classR;
		}

		/**
		 * @param classR the classR to set
		 */
		public void setClassR(int classR) {
			this.classR = classR;
		}
		
		/**
		 * should decide which class will be in the next children
		 * and split the data to 2 arrays
		 * @param tmpSplit
		 */
		public void setSplits(ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> tmpSplit) {
			if(!tmpSplit.isEmpty()){
			HashMap<Integer, ArrayList<DataPoint1>>left = tmpSplit.get(0);
			HashMap<Integer, ArrayList<DataPoint1>>right = tmpSplit.get(1);
			ArrayList<DataPoint1> l = new ArrayList<>();
			ArrayList<DataPoint1> r = new ArrayList<>();
			double maxClassL= 0;
			double maxClassR= 0;
			double classSizeL = 0;
			double classSizeR = 0;
			int classL = 0, classR = 0;
			for(int i=1; i<=classes;i++){
				classSizeL =0;
				if(!(left.get(i)== null)){
					l.addAll(left.get(i));
					for (DataPoint1 dp : left.get(i)) {
						classSizeL += dp.getWeight();			
					}
				}
				if(maxClassL < classSizeL){
					maxClassL = classSizeL;
					classL = i;
				}
				classSizeR =0;
				if(!(right.get(i)== null)){
					r.addAll(right.get(i));
					for (DataPoint1 dp : right.get(i)) {
						classSizeR += dp.getWeight();			
					}
				}
				if(maxClassR < classSizeR){
					maxClassR = classSizeR;
					classR = i;
				}	
			}
			splits.add(l);
			splits.add(r);
			this.classL = classL;
			this.classR = classR;
			}
		}
		
		/**
		 * unused method
		 * supposed to help for the better split method
		 * @param tmpSplit
		 */
		public void setSplits2(ArrayList<HashMap<Integer, ArrayList<DataPoint1>>> tmpSplit){
			if(!tmpSplit.isEmpty()){
				HashMap<Integer, ArrayList<DataPoint1>>left = tmpSplit.get(0);
				HashMap<Integer, ArrayList<DataPoint1>>right = tmpSplit.get(1);
				ArrayList<DataPoint1> l = new ArrayList<>();
				ArrayList<DataPoint1> r = new ArrayList<>();
				for(int i=1;i<=classes;i++){
					l.addAll(left.get(i));
					r.addAll(right.get(i));
				}
				splits.add(l);
				splits.add(r);
			}
		}
}
