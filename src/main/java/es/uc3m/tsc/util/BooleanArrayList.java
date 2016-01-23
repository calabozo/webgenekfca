package es.uc3m.tsc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class BooleanArrayList extends ArrayList<boolean[]> implements BooleanArray{
	private static final long serialVersionUID = 1L;
	
	public BooleanArrayList(int i) {
		super(i);
	}

	public BooleanArrayList() {
		super();
	}
	
	public boolean add(boolean[] b){
		if (!this.contains(b)){
			return super.add(b);			
		}
		return false;
	}
	
	public boolean contains(boolean[] b){
		if (this.size()==0) return false;
				
		Iterator<boolean[]> iter=this.iterator();
		while (iter.hasNext()){
			if (Arrays.equals(iter.next(), b)){
				return true;
			}
		}		
		return false;
	}
	
	public boolean equals(Object o){
		if (o==this) return true;
		if (!(o instanceof BooleanArrayList)) return false;
		
		BooleanArrayList ba=(BooleanArrayList)o;
		if (ba.size()!=this.size()) return false;
				
		Iterator<boolean[]> iter=this.iterator();
		while(iter.hasNext()){
			if (!ba.contains(iter.next())){				
				return false;
			}
		}
						
		return true;				
	}
	
	public boolean[][] toArray(){
		boolean[][] A_Matrix=new boolean[0][0];
		A_Matrix=this.toArray(A_Matrix);
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
