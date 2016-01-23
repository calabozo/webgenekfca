package es.uc3m.tsc.kfca.explore;

import es.uc3m.tsc.kfca.tools.KFCAConceptsIntersection;
import es.uc3m.tsc.util.BooleanArray;
import es.uc3m.tsc.util.BooleanArrayLarge;

public class KFCATest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		boolean[][] m={{false, false, false, false, false, false, false, false},
			{false, false, true, true, false, false, false, false},
			{false, false, true, true, false, true, false, false},
			{false, false, true, true, false, false, true, false},
			{false, false, true, true, false, false, false, false}};
		
		
		BooleanArray bSet=new BooleanArrayLarge(m[0].length);
		
		bSet.add(m[0]);
		bSet.add(m[1]);
		bSet.add(m[2]);
		bSet.add(m[3]);
		bSet.add(m[4]);
		bSet.add(m[2]);
		
		
		
		System.out.println(bSet);
		
		System.exit(0);
				
		

		 boolean [][] I={{true,true,false,true,false,true,false},
 				         {true,true,false,true,true,false,false},
 				         {true,true,false,true,true,true,true},
 				         {true,false,true,false,true,true,false},
 				         {false,true,false,true,false,false,false},
 				         {true,false,false,false,false,true,false}};
		 boolean[][] A=KFCAConceptsIntersection.getExtents(I);
		 
		 printMatrix(I);
		 System.out.println("***");
		 printMatrix(A);
		 /*
		 Solution
	
     1     1     1     1     1     1
     1     1     1     1     0     1
     1     1     1     0     1     0
     1     1     1     0     0     0
     1     0     1     1     0     1
     1     0     1     0     0     0
     0     1     1     1     0     0
     0     1     1     0     0     0
     0     0     1     1     0     0
     0     0     1     0     0     0
     0     0     0     1     0     0
     0     0     0     0     0     0
		 */

	}
	
	public static void printMatrix(boolean[][] m){
		for (int i=0;i<m.length;i++){
			for (int j=0;j<m[i].length;j++){
				if (m[i][j])
					System.out.print(" 1");
				else
					System.out.print(" 0");
			}
			System.out.println("");
		}
		
	}

}
