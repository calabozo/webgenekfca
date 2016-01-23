package es.uc3m.tsc.kfca.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import es.uc3m.tsc.math.ArrayUtils;

public class KFCAConceptsBordat {
	public void print(){
		System.out.println("****es.uc3m.kfcatools.KFCAConceptsBordat**");
	}	
		
	/*
	 * Returns the concepts.
	 * Each row is an extend.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public static KFCAConceptsSet getConcepts(boolean [][] R ){
		int nr=R.length;
		int[] O=new int[nr];
		int[] A=new int[0];
		for(int i=0;i<nr;i++) O[i]=i;
				
		KFCAConceptsSet c=new KFCAConceptsSet(R);
		c.addConcept(A, O);
		getRecursiveConcepts(A, O, R , new HashSet<Integer>(),c);
			
		return c;
	}

	/*
	 * Returns the concepts.
	 * Each row is an extend.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public static int getRecursiveConcepts(int[] A, int[] B, boolean [][] R , HashSet<Integer> marked, KFCAConceptsSet c){
		
		int nc=R[0].length;
		Iterator<int[]> iter;
		Iterator<int[]> iter2;
		int[] P=new int[nc];
		for(int i=0;i<nc;i++) P[i]=i;
		HashSet<Integer> newMarked=new HashSet<Integer>(marked);
		int numConcepts=0;
		
		int nPA=nc-A.length;
		int[] PA=new int[nPA];
		int k=0;
		for(int i=0;i<nc;i++){
			if (Arrays.binarySearch(A, P[i])<0){
				PA[k++]=P[i];
			}
		}
			
		ArrayList<int[]> part=getMaxModPartition(PA , B, R);
		ArrayList<int[]> nonD=KFCAConceptsBordat.getNonDominatingMaxMod(part,B,R);
		ArrayList<int[]> newND=new ArrayList<int[]>(nonD);
						
		for (int m:marked){
			iter=newND.iterator();
			while(iter.hasNext()){				
				int[] nd=iter.next();
				if (Arrays.binarySearch(nd, m)>=0){
					newND.remove(nd);
					break;
				}
			}
		}
		
			
		iter=newND.iterator();
		while(iter.hasNext()){
			int[] X=iter.next();
			if (X.length==0) continue;
		
			int[] AX=Arrays.copyOf(A,A.length+X.length);
			for (int i=A.length;i<AX.length;i++){
				AX[i]=X[i-A.length];
			}
			Arrays.sort(AX);
				
				
			ArrayList<Integer> aBRX=new ArrayList<Integer>();								
			for (int i=0;i<B.length;i++){
				boolean tmpc=true;
				for (int j=0;j<X.length;j++){
					tmpc&=R[B[i]][X[j]];
				}
				if (tmpc){
					aBRX.add(new Integer(B[i]));
				}
			}				
			int[] BRX=new int[aBRX.size()];
			for(int i=0;i<BRX.length;i++){
				BRX[i]=aBRX.get(i);
			}
						
			/*
			for (int i=0;i<AX.length;i++){
				System.out.print(AX[i]+",");
			}
			System.out.print(" X ");
			for (int i=0;i<BRX.length;i++){
				System.out.print(BRX[i]+",");
			}
			System.out.println("");
			*/
			
			if (c!=null) c.addConcept(AX, BRX);
			numConcepts++;
			
			if (BRX.length>0){
				numConcepts+=getRecursiveConcepts(AX,BRX,R,new HashSet<Integer>(newMarked),c);
			}
			
			for (int x : X)	newMarked.add(new Integer(x));
			
			
			iter2=part.iterator();
			while(iter2.hasNext()){
				int[] d=iter2.next();
				if (dominates(d,X, B, R)){
					for (int y : d){
						newMarked.add(new Integer(y));
					}
				}
			}
			/*
			boolean[] bX=getObjectsByAttrs(B,X,R);
			iter2=part.iterator();
			while(iter2.hasNext()){
				int[] d=iter2.next();
				boolean[] bd=getObjectsByAttrs(B,d,R);
				if (dominates(bd,bX)){
					for (int y : d){
						newMarked.add(new Integer(y));
					}
				}
			}
			*/
	
		}					
		return numConcepts;
	}

	
	/*
	 * Returns the concepts.
	 * Each row is an intent.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public static boolean[][] getIntents(boolean [][] inputMatrix,boolean [][] extents){			
		return null;
	}

	public static ArrayList<int[]> getMaxModPartition(int[] P , int[] O, boolean[][] R){			
		int nO=O.length;
		int nP=P.length;		
		ArrayList<int[]> part=new ArrayList<int[]>();
		part.add(P);
		for(int i=0;i<nO;i++){
			boolean[] row=R[O[i]];			
			Iterator<int[]> it=part.iterator();			
			if (ArrayUtils.sumElems(row)>0){
				ArrayList<int[]> newPart=new ArrayList<int[]>(part.size());
				while (it.hasNext()){
					int[] K=it.next();
					int[] Kp=new int[K.length];
					int[] Kpp=new int[K.length];
					int i_Kp=0;
					int i_Kpp=0;
					for (int j=0;j<nP;j++){
						if (Arrays.binarySearch(K, P[j])>=0){
							if (row[P[j]]){
								Kp[i_Kp++]=P[j];
							}else{
								Kpp[i_Kpp++]=P[j];
							}
						}
					}
					if (i_Kp>0 && i_Kpp>0){					
						newPart.add(Arrays.copyOf(Kp, i_Kp));
						newPart.add(Arrays.copyOf(Kpp, i_Kpp));
					}else{
						newPart.add(K);
					}			
				}
				part=newPart;
			}
		}				
		return part;
	}
	
	public static ArrayList<int[]> getNonDominatingMaxMod(ArrayList<int[]> partOrig, int[] O, boolean[][] R){
		ArrayList<int[]> ND=new ArrayList<int[]>();
		ArrayList<int[]> part=new ArrayList<int[]>(partOrig);
		do{
			int[] X=part.remove(0);
			ND.add(X);
			
			
			
			Iterator<int[]> iter=part.iterator();
			ArrayList<int[]> MaxNods=new ArrayList<int[]>();			
			while(iter.hasNext()){				
				int[] p=iter.next();				
				if (dominates(p,X,O,R)) MaxNods.add(p);
			}
			
			
			/*
			boolean[] col=getObjectsByAttrs(O,X,R);
						
			Iterator<int[]> iter=part.iterator();
			ArrayList<int[]> MaxNods=new ArrayList<int[]>();			
			while(iter.hasNext()){				
				int[] p=iter.next();
				boolean[] Rc=getObjectsByAttrs(O,p,R);
				if (dominates(Rc,col)) MaxNods.add(p);
			}
			*/
			
			iter=MaxNods.iterator();
			while(iter.hasNext()){
				part.remove(iter.next());
			}
			
			
		}while(part.size()>0);
		return ND;		
	}
	
	/*
	 * Return true if a dominates b. Ex:
	 * a={F,T,F,F,F}
	 * b={T,T,T,F,F}
	 */
	private static boolean dominates(boolean[] a, boolean[] b){
		boolean d=true;
		for (int i=0;i<b.length;i++){
			if (a[i] && !b[i]){
				d=false;
				break;
			}					
		}				
		return d;
	}
	
	/*
	 * Return true if a dominates b. Ex:
	 * a={F,T,F,F,F}
	 * b={T,T,T,F,F}
	 */
	private static boolean dominates(int[] a,int[] b, int[] O, boolean[][] R){		
		for (int i=0;i<O.length;i++){
			boolean ba=true;
			for (int ia:a){
				if (!R[O[i]][ia]){
					ba=false;
					break;
				}
			}
			if (ba){				
				for (int ib:b){
					if (!R[O[i]][ib]){
						return false;						
					}
				}			
			}
		}				
		return true;
	}
	
	
	private static boolean[] getObjectsByAttrs(int[]O, int[] indx,boolean[][] R){
		int nr=O.length;
		boolean[] col=new boolean[nr];
		for (int i=0;i<nr;i++){
			boolean tmpc=true;
			for (int j=0;j<indx.length;j++){
				tmpc&=R[O[i]][indx[j]];
			}
			col[i]=tmpc;
		}
		return col;
	}
	
	public static int getNumberConcepts(boolean [][] inputMatrix ){
		int nr=inputMatrix.length;
		int[] O=new int[nr];
		int[] A=new int[0];
		for(int i=0;i<nr;i++) O[i]=i;
				
		int n=getRecursiveConcepts(A, O, inputMatrix , new HashSet<Integer>(),null)+1;
			
		return n;
	}
	
	/*
	 * Returns the number of objects which only belongs to a single concept.
	 * It returns for all the possible concepts which are 2^(number of atributes), thus some of those concepts can be 0;
	 */	
	public static int[] numElementsBySingleConcept(boolean[][] I){
		return null;
	}



}
