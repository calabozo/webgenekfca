package es.uc3m.tsc.kfca.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class KFCAConceptsSet{
	
	public class Concept{
		private int[] intent; //Attribute intent
		private int[] extent; //Object extent
		
		public Concept(int[] i, int[] e){			
			this.intent=i;
			this.extent=e;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o==this) return true;
			if (!(o instanceof Concept)) return false;			
			Concept c=(Concept)o;					
			return Arrays.equals(this.intent, c.intent);					
		}
		
		public int hashCode(){
			int ret=0;
			for (int n:this.intent){
				ret+=1<<n;		
			}
			return ret;
		}
	}
	int nr; //Num rows
	int nc; //Num cols
	
	HashSet<Concept> concepts;
	public KFCAConceptsSet(boolean[][] R){
		this.nr=R.length;
		this.nc=R[0].length;
		concepts=new HashSet<Concept>();
	}
	
	public int getNumberConcepts(){
		return concepts.size();
	}
	
	public boolean addConcept(int[] intent, int[] extent){
		Concept c=new Concept(intent,extent);
		return concepts.add(c);
	}
		
	/*
	 * Returns the Extents.
	 * Each row is an extend.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public boolean[][] getExtents(){
		boolean[][] extents=new boolean[this.getNumberConcepts()][nr];
		Iterator<Concept> iter=this.concepts.iterator();
		for (int i=0;i<this.getNumberConcepts();i++){
			extents[i]=new boolean[nr];
			Arrays.fill(extents[i], false);
			int[] cExtent=iter.next().extent;
			for (int j=0;j<cExtent.length;j++){
				extents[i][cExtent[j]]=true;
			}
		}
		
		return extents;		
	}
	
	/*
	 * Returns the Intents.
	 * Each row is an intent.
	 * The number of columns is equal the number of rows of inputMatrix
	 */
	public  boolean[][] getIntents(){
		boolean[][] intents=new boolean[this.getNumberConcepts()][nc];
		Iterator<Concept> iter=this.concepts.iterator();
		for (int i=0;i<this.getNumberConcepts();i++){
			intents[i]=new boolean[nc];
			Arrays.fill(intents[i], false);
			int[] cIntent=iter.next().intent;
			for (int j=0;j<cIntent.length;j++){
				intents[i][cIntent[j]]=true;
			}
		}		
		return intents;
	}
	
	
}
