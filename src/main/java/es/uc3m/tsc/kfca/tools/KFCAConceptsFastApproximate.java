package es.uc3m.tsc.kfca.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class KFCAConceptsFastApproximate implements KFCAConcepts {
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
		}
		//Ads top
		intents.add(new Long(0));
		
		//Ads bottom
		Long bottom=new Long((1<<nc)-1);
		intents.add(bottom);
		
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
	public HashMap<Long,Integer> numElementsBySingleConcept(long [] inputMatrix, int nc, int maxNumConcepts){
		
		int nr=inputMatrix.length;
		
		long row1;		
		int i,k;
		
		HashMap<Long,Integer> concepts = new HashMap<Long,Integer>(); 		
		Long conceptId;
		
		
		for (i=0;i<nr;i++){
			row1=inputMatrix[i];
			conceptId=new Long(row1);
			
			Integer numConcepts=concepts.get(conceptId);
			if (numConcepts==null){
				concepts.put(conceptId, new Integer(1));
			}else{
				concepts.put(conceptId, numConcepts+1);
			}										
		}
		int numElements=concepts.size();
		
		
		//Ads top
		if (!concepts.containsKey(0L))
			numElements++;
		
		//Ads bottom
		if (!concepts.containsKey(new Long(1<<nc)))
			numElements++;
		
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
					if (ret.size()>=maxNumConcepts)
						break;
				}
			}
			concepts=ret;
		}
		
		concepts.put(new Long(-1), new Integer(numElements));
		//System.out.println(Arrays.toString(intents.toArray()));
		return concepts;
	}

}
