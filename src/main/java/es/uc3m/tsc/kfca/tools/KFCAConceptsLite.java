package es.uc3m.tsc.kfca.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;

public class KFCAConceptsLite implements KFCAConcepts {
	
 
	@Deprecated
	public static int getNumberConceptsOld(boolean [][] inputMatrix ){
		int nr=inputMatrix.length;
		int nc=inputMatrix[0].length;
		boolean[] row1;
		boolean[] row2;
		int[] intent;
		int intentSize,i,j,k;
		KFCAConceptsSet intents=new KFCAConceptsSet(inputMatrix);		
		for (i=0;i<nr;i++){
			row1=inputMatrix[i];
			
			intentSize=0;
			intent=new int[nc];
			for (j=0;j<nc;j++){
				if (row1[j]){
					intent[intentSize++]=j;					
				}
			}
			if (intentSize>0){
				intent=Arrays.copyOf(intent, intentSize);			
				intents.addConcept(intent, null);
				//System.out.println(Arrays.toString(intent));
			}
			
			for(k=(i+1);k<nr;k++){
				row2=inputMatrix[k];
				intentSize=0;
				intent=new int[nc];
				for (j=0;j<nc;j++){
					if (row1[j]&row2[j]){
						intent[intentSize++]=j;
					}
				}
				if (intentSize>0){
					intent=Arrays.copyOf(intent, intentSize);			
					intents.addConcept(intent, null);
					//System.out.println(Arrays.toString(intent));
				}
			}
		}
		//Ads top
		intents.addConcept(new int[0], null);
		
		//Ads bottom
		intent=new int[nc];
		for (j=0;j<nc;j++) intent[j]=j;			
		intents.addConcept(intent, null);
		
		return intents.getNumberConcepts();
	}
	
	public int getNumberConcepts(boolean [][] inputMatrix ){
		int nr=inputMatrix.length;
		int nc=inputMatrix[0].length;
		boolean[] row;
		long[] I=new long[nr];
		int j;
		for (int i=0;i<nr;i++){
			row=inputMatrix[i];
			int ret=0;
			for (j=0;j<nc;j++){
				if (row[j])	ret+=1<<j;		
			}
			I[i]=ret;
		}
		return  getNumberConcepts(I, nc);
	}

	/*
	 * inputMatrix: Each row is a long with up to 64bits
	 * nc: Number of columns, number of useful bits from a inputMatrix row
	 */
	public int getNumberConcepts(long [] inputMatrix, int nc){
		int nr=inputMatrix.length;
		
		long row1;		
		int i,k;
		HashSet<Long> intents=new HashSet<Long>();
		
				
		for (i=0;i<nr;i++){
			row1=inputMatrix[i];
			intents.add(new Long(row1));
			
			for(k=(i+1);k<nr;k++){																
				intents.add(new Long(row1&inputMatrix[k]));							
			}
		}
		//Ads top
		intents.add(new Long(0));
		
		//Ads bottom
		Long bottom=new Long((1<<nc)-1);
		intents.add(bottom);
		//System.out.println(Arrays.toString(intents.toArray()));
		return intents.size();
	}



	/*
	 * Returns the number of objects which only belongs to a single concept.
	 * It returns a HashMap<A,B> where:
	 *  - A is the concept Id
	 *  - B is the number of elements in that concept
	 *  
	 *  For the conceptId A = -1 indicates the real total number of concepts.
	 */	
	public HashMap<Long,Integer> numElementsBySingleConcept(long [] inputMatrix, int nc){
		
		int nr=inputMatrix.length;
		
		long row1;		
		int i,k;
		
		HashMap<Long,Integer> concepts = new HashMap<Long,Integer>(); 
		HashSet<Long> intents=new HashSet<Long>();
		Long conceptId;
		
		
		for (i=0;i<nr;i++){
			row1=inputMatrix[i];
			conceptId=new Long(row1);
			
			intents.add(conceptId);
			
			
			
			Integer numConcepts=concepts.get(conceptId);
			if (numConcepts==null){
				concepts.put(conceptId, new Integer(1));
			}else{
				concepts.put(conceptId, numConcepts+1);
			}
			/* 
			//Comment this to increase SPEED. 
			//BUT it will fail to give the correct number of real concepts!!!
			for(k=(i+1);k<nr;k++){																
				intents.add(new Long(row1&inputMatrix[k]));							
			}
			*/
			
		}
		//Ads top
		intents.add(new Long(0));
		
		//Ads bottom
		Long bottom=new Long((1<<nc)-1);
		intents.add(bottom);
		int numElements=intents.size();
		intents.clear();
		/*
		if (maxNumConcepts<concepts.size()){
			int val;
			List<Integer> num=new ArrayList<Integer>(concepts.values());
			Collections.sort(num);
		
			int maxVal=num.get(num.size()-maxNumConcepts);
		
			HashMap<Long,Integer> ret=new HashMap<Long,Integer>();
			Iterator<Long> iter=concepts.keySet().iterator();
			while(iter.hasNext()){
				conceptId=iter.next();
				val=concepts.get(conceptId);
				if (val >=maxVal){
					ret.put(conceptId, val);
				}
			}
			concepts=ret;
		}
		*/
		concepts.put(new Long(-1), new Integer(numElements));
		//System.out.println(Arrays.toString(intents.toArray()));
		return concepts;
	}
	
	/*
	 * inputMatrix: Each row is a long with up to 64bits
	 * nc: Number of columns, number of useful bits from a inputMatrix row
	 */
	public static int getNumberConcepts2(long [] inputMatrix, int nc){
		int nr=inputMatrix.length;
		
		long row1,row2;		
		int i,k;
		int ni=0;
		int numIntents=0;
		boolean newIntent;
		long[] intents=new long[10*nr];
		long[] lowerLimitIntent=new long[2];
		long[] upperLimitIntent=new long[2];
		
		//Ads top
		intents[numIntents]=0;
		lowerLimitIntent[numIntents]=0;			
		upperLimitIntent[numIntents]=0;		
		
		
		//Ads bottom
		numIntents++;
		intents[numIntents]=(1<<nc)-1;
		lowerLimitIntent[numIntents]=(1<<nc)-1;		
		upperLimitIntent[numIntents]=(1<<nc)-1;
		

		for (i=0;i<nr;i++){
			row1=inputMatrix[i];
			newIntent=true;
			for (ni=0;ni<lowerLimitIntent.length;ni++){
				if (lowerLimitIntent[ni]<=row1 && row1<=upperLimitIntent[ni]){
					newIntent=false;
					break;
				}
			}
			if (newIntent){
				++numIntents;
				if (numIntents>=intents.length){
					numIntents=0;
					long [][] sortIntents=cleanIntents(intents, lowerLimitIntent, upperLimitIntent);
					lowerLimitIntent=sortIntents[0];
					upperLimitIntent=sortIntents[1];
				}
				intents[numIntents]=row1;					
			}
			
			
			
			for(k=(i+1);k<nr;k++){																
				row2=row1&inputMatrix[k];
				newIntent=true;
				for (ni=0;ni<lowerLimitIntent.length;ni++){
					if (lowerLimitIntent[ni]<=row2 && row2<=upperLimitIntent[ni]){
						newIntent=false;
						break;
					}
				}
				if (newIntent){
					++numIntents;
					if (numIntents>=intents.length){
						numIntents=0;
						long [][] sortIntents=cleanIntents(intents, lowerLimitIntent, upperLimitIntent);
						lowerLimitIntent=sortIntents[0];
						upperLimitIntent=sortIntents[1];
					}
				
					intents[numIntents]=row2;					
				}
				
			}
		}
		long [][] sortIntents=cleanIntents(intents, lowerLimitIntent, upperLimitIntent);
		lowerLimitIntent=sortIntents[0];
		upperLimitIntent=sortIntents[1];
		
		numIntents=0;
		for (i=0;i<lowerLimitIntent.length;i++){
			numIntents+=(upperLimitIntent[i]-lowerLimitIntent[i])+1;
		}
				
		return numIntents;
	}

	private static long[][] cleanIntents(long[] intents, long[] lowerLimitIntent, long[] upperLimitIntent){
		
		
		long[][] ret=new long[2][];		
		ArrayList<Long> aLowLimit=new ArrayList<Long>();
		ArrayList<Long> aUpLimit=new ArrayList<Long>();
		int iLimit=0;
		long c,l,u;
		l=lowerLimitIntent[0];
		u=upperLimitIntent[0];
		long tmpl=0,tmpu=0;
		Arrays.sort(intents);
		/*
		System.out.println("Input --->");
		System.out.println(Arrays.toString(intents));
		System.out.println(Arrays.toString(lowerLimitIntent));
		System.out.println(Arrays.toString(upperLimitIntent));
		*/
		int i=0;
		c=0;
		boolean addLimit=false;
		
		c=intents[i];
		do{
				
			
				if (l<c){
					if (c<=u){						
						//Keep calm and continue, nothing here
						while(i<intents.length && intents[i]<=u) i++;
						if (i<intents.length) c=intents[i];
						
					}else if (u+1==c){
						iLimit++;
						tmpl= lowerLimitIntent[iLimit];
						tmpu= upperLimitIntent[iLimit];
						do{							
							if (c+1>=tmpl){
								u=tmpu;								
								iLimit++;
								if (iLimit<lowerLimitIntent.length){
									tmpl= lowerLimitIntent[iLimit];
									tmpu= upperLimitIntent[iLimit];
								}
							}else{ //c<tmpl
								u=c;														
							}
							addLimit=true;
							while(i<intents.length && u>=intents[i] ) i++;
							if (i<intents.length){
								c=intents[i];
							}
						}while(u+1==c);
						
					}else{ // c>u
						iLimit++;
		
						tmpl= lowerLimitIntent[iLimit];
						tmpu= upperLimitIntent[iLimit];				
						addLimit=true;

					}
				}else if (l==c){
					while(i<intents.length && c==intents[i]) i++;
					if (i<intents.length) c=intents[i];
					
				}else{ // l>c				
					tmpl=l;
					tmpu=u;						
					l=c;
					do{
						u=c;
						addLimit=true;
					
						while(i<intents.length && u>=intents[i]) i++;
						if (i<intents.length){
							c=intents[i];
						}else{
						//	break;
						}
					}while(u+1==c);
				}
				
				if (addLimit){
					addLimit=false;
					aLowLimit.add(l);
					aUpLimit.add(u);
					l=tmpl;
					u=tmpu;							
				}
			
		}while(i<intents.length);
		
			
		for (;iLimit<lowerLimitIntent.length;iLimit++){
			l= lowerLimitIntent[iLimit];
			u= upperLimitIntent[iLimit];
			aLowLimit.add(l);
			aUpLimit.add(u);
		}
		
		
		long[] low=new long[aLowLimit.size()];
		long[] up=new long[aUpLimit.size()];
		i=0;
		for (Long v : aLowLimit) low[i++]=v;
		i=0;
		for (Long v : aUpLimit) up[i++]=v;
		
		ret[0]=low;
		ret[1]=up;
		/*
		System.out.println("Output --->");
		System.out.println(Arrays.toString(low));
		System.out.println(Arrays.toString(up));
		*/
		return ret;
		
	}
	
}
