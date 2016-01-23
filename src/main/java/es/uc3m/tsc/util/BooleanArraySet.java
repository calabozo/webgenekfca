package es.uc3m.tsc.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class BooleanArraySet implements BooleanArray{
	public class BooleanArray{
		boolean[] b;
		public BooleanArray(boolean[] b){
			this.b=b;
		}
		public boolean equals(Object o){
			if (o==this) return true;
			if (!(o instanceof BooleanArray)) return false;
			BooleanArray bo=(BooleanArray)o;
			return Arrays.equals(this.b, bo.b);
		}
		public int hashCode(){
			int ret=0;
			for (boolean v:this.b){
				if (v)
					ret=ret*2+1;
				else
					ret=ret*2;
			}
			return ret;
		}
	}
	
	HashSet<BooleanArray> set;
	
	public BooleanArraySet(){
		this.set=new HashSet<BooleanArray>();
	}
	
	public boolean add(boolean[] b){
		BooleanArray ba=new BooleanArray(b);
		return this.set.add(ba);		
	}
	
	private static final long serialVersionUID = 1L;
	
	public boolean contains(boolean[] b){
		return this.set.contains(new BooleanArray(b));		
	}
	public boolean contains(BooleanArray b){
		return this.set.contains(b);		
	}
	public int size(){
		return this.set.size();
	}
	
	public boolean equals(Object o){
		if (o==this) return true;
		if (!(o instanceof BooleanArraySet)) return false;
		
		BooleanArraySet ba=(BooleanArraySet)o;
		if (this.set.size()!=ba.set.size()) return false;
	
		Iterator<BooleanArray> iter=this.set.iterator();
				
		while(iter.hasNext()){
			if (!ba.contains(iter.next())){				
				return false;
			}
		}
						
		return true;				
	}
	
	public boolean[][] toArray(){
		boolean[][] A_Matrix=null;
		Iterator<BooleanArray> iter=this.set.iterator();
		
		if (iter.hasNext()){
			BooleanArray ba=iter.next();
			A_Matrix=new boolean[this.set.size()][ba.b.length];
			A_Matrix[0]=ba.b;
			
		}
		int k=1;
		while(iter.hasNext()){
			BooleanArray ba=iter.next();
			A_Matrix[k++]=ba.b;
		}				
		return A_Matrix;
	}
	
	public String toString(){
		String s="";
		boolean [][] m=this.toArray();
		for (boolean[] row:m){
			s=s+Arrays.toString(row)+"\n";
		}
		return s;
	}
}
