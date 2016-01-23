package es.uc3m.tsc.kfca.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.uc3m.tsc.math.ArrayUtils;

public class KFCAConceptsIntersection {
	public void print(){
		System.out.println("****es.uc3m.kfcatools.Concepts**");
	}	
	
	/*
	 * Returns the concepts.
	 * Each row is an extend.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public static boolean[][] getExtents(boolean [][] inputMatrix ){

		int numG=inputMatrix.length;
		
		int[] pending=ArrayUtils.sumCols(inputMatrix);		
		int[] m=sortByCols(pending);

		boolean addEmptyExtend=(m.length>0 && pending[m[m.length-1]]==0);
		boolean[] emptyExtent=new boolean[numG];
		Arrays.fill(emptyExtent, 0, numG, false);
		
		Iterator<boolean[]> it;
		ArrayList<boolean[]> A=new ArrayList<boolean[]>();
		int maximal;

		for (int k=0;k<m.length;k++){
			maximal=m[k];
		    if (pending[maximal] == 0) break; //end%may be erased by previous exploration
		    
//		    cand=I(:,maximal);%candidate column to be explored

//		    %full extent cannot be in pending cols
//		    lA=size(A,2);
//		    nomatch = any(xor(A(:,2:end),my.logical.mtimes(cand,true(1,lA-1))));%one less columns than A!!
		
		    
		    boolean[] cand=new boolean[numG];
		    for (int i=0; i<numG; i++){
		    	cand[i]=inputMatrix[i][maximal];
	    	}
		    
		//    boolean[] cand=It[maximal];
		    
		    boolean nomatch=true;
		    it=A.iterator();
		    while(it.hasNext()){
		    	boolean[] Acol=it.next();
		    	if (Arrays.equals(Acol, cand)){
		    		nomatch=false;
		    		break;		    				    		
		    	}
		    }
		    if (nomatch){
		    	
		    	boolean[][] nextents=new boolean[A.size()][numG];		    	
		    	it=A.iterator();
		    	int i=0;
		    	while(it.hasNext()){
			    	boolean[] Acol=it.next();
			    	boolean[] extent=nextents[i];
			    	for (int j=0;j<cand.length;j++){
			    		extent[j]=Acol[j]&cand[j];
			    	}
			    	i++;	  					    				    	
			    }
		    	
		    	A.add(cand);
		    	boolean addCol;
		    	for (boolean[] nCol:nextents){
		    		addCol=true;
		    		it=A.iterator();
		    		while(it.hasNext()){
		    			if (Arrays.equals(nCol, it.next())){
		    				addCol=false;
		    				break;
		    			}
		    		}
		    		if (addCol){
		    			A.add(nCol);
		    			if (Arrays.equals(emptyExtent, nCol)) addEmptyExtend=false;
		    		}
		    	}		    	
		    }		    		  

		}

		boolean[] topConcept=new boolean[numG];
		Arrays.fill(topConcept, 0, numG, true);
		A.add(0, topConcept);
		
		if (addEmptyExtend){			
			A.add(emptyExtent);
		}
		
		//The empty extent is generated if there is a 
		
		boolean[][] A_Matrix=new boolean[0][0];//=new boolean[numG][A.size()];
		A_Matrix=A.toArray(A_Matrix);
		/*
		boolean[][] A_Matrix=new boolean[numG][A.size()];
		it=A.iterator();
		int i=0,j=0;
		while(it.hasNext()){
			boolean[] A_col=it.next();
			for (i=0;i<numG;i++){
				A_Matrix[i][j]=A_col[i];
			}
			j++;
		}
		*/
		
		return A_Matrix;
	}
	
	/*
	 * Returns the concepts.
	 * Each row is an intent.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public static boolean[][] getIntents(boolean [][] inputMatrix,boolean [][] extents){
		int numG=inputMatrix.length;
		int numM=inputMatrix[0].length;
		int numConcepts=extents.length;
		boolean[][] intents=new boolean[numConcepts][numM];
		
		for (int k=0;k<numConcepts;k++){
			boolean[] extent=extents[k];
			boolean[] intentCanditate=new boolean[numM];
			
			
			Arrays.fill(intentCanditate, 0, numM, true);			
			for (int t=0;t<numG;t++){
				if (extent[t]){					
					boolean rowIntents[]=inputMatrix[t];
					for (int j=0;j<numM;j++){
						intentCanditate[j]=intentCanditate[j]&rowIntents[j];
					}					
				}
			}
			
			intents[k]=intentCanditate;			
		}
		
		return intents;
	}

	
	public static int getNumberConcepts(boolean [][] inputMatrix ){
		int numObjects=0;
		boolean[][] intents=getExtents(inputMatrix );
		if (intents!=null){
			numObjects=intents.length;
		}
		return numObjects; 
	}
	
	private static int[] sortByCols(int[] inputMatrix){
		int n=inputMatrix.length;
		
		int[] indxMatrix=new int[n];
		int[] sortMatrix=new int[n];
		
		int i,j,aux;
		for (i=0;i<n;i++){
			indxMatrix[i]=i;
			sortMatrix[i]=inputMatrix[i];
		}
		
		for (i=0;i<(n-1);i++){
			
			for (j=(i+1);j<n;j++){
				if (sortMatrix[i]<sortMatrix[j]){
					aux=sortMatrix[i];
					sortMatrix[i]=sortMatrix[j];
					sortMatrix[j]=aux;
					
					aux=indxMatrix[i];
					indxMatrix[i]=indxMatrix[j];
					indxMatrix[j]=aux;
				}
			}
		}
		
		
		return indxMatrix;
	}

	/*
	 * Returns the number of objects which only belongs to a single concept.
	 * It returns for all the possible concepts which are 2^(number of atributes), thus some of those concepts can be 0;
	 */	
	public static int[] numElementsBySingleConcept(boolean[][] I){
		int nc=I[0].length;
		int val;
		int indx;
		int[] num=new int[(int) Math.pow(2, nc)];
		Arrays.fill(num, -1);
		
		
		boolean[][] A=KFCAConceptsIntersection.getExtents(ArrayUtils.transpose(I));
		
		//Set the known concepts with 0 elements 
		for (boolean[] row:A){
			val=1;
			indx=0;
			for (boolean c:row){				
				if (c) indx+=val;
				val=val*2;
					
			}
			num[indx]=0;
		}		

		//Fills with the correct value of elements. It can be 0. 
		for (boolean[] row : I){
			val=1;
			indx=0;
			for (boolean c:row){				
				if (c) indx+=val;
				val=val*2;
					
			}
			num[indx]++;			
		}
		
		return num;
	}

	
	/*
	 * Returns the number of objects which only belongs to a single concept.
	 * It returns a HashMap<A,B> where:
	 *  - A is the concept Id
	 *  - B is the number of elements in that concept
	 */	
	public static HashMap<Integer,Integer> numElementsBySingleConcept(boolean[][] Im, int maxNumConcepts){
		HashMap<Integer,Integer> concepts=new HashMap<Integer,Integer>();
		Integer conceptId;
		int val;
		int indx;
		
		for (boolean[] row : Im){
			val=1;
			indx=0;
			for (boolean c:row){				
				if (c) indx+=val;
				val=val*2;
				
			}
			conceptId=new Integer(indx);
			
			Integer numConcepts=concepts.get(conceptId);
			if (numConcepts==null){
				concepts.put(conceptId, new Integer(1));
			}else{
				concepts.put(conceptId, numConcepts+1);
			}
		}
		if (maxNumConcepts>concepts.size()) return concepts;
		
		List<Integer> num=new ArrayList<Integer>(concepts.values());
		Collections.sort(num);
		
		int maxVal=num.get(num.size()-maxNumConcepts);
		
		HashMap<Integer,Integer> ret=new HashMap<Integer,Integer>();
		Iterator<Integer> iter=concepts.keySet().iterator();
		while(iter.hasNext()){
			conceptId=iter.next();
			val=concepts.get(conceptId);
			if (val >=maxVal){
				ret.put(conceptId, val);
			}
		}
		
		return ret;
	}


}
