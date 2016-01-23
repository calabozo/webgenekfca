package es.uc3m.tsc.util;

import java.util.Arrays;



public class BooleanArraySimple implements BooleanArray{
	int[] v;
	int numCols;
	int currentSize;
	
	/*
	 * @params: 
	 * int maxSize: Maximun number of rows for the array
	 * int numCols: Number of columns for the array 
	 */
	public BooleanArraySimple(int initSize,int numCols){
		this.v=new int[initSize];		
		this.numCols=numCols;
		this.currentSize=0;
	}
		
	public boolean add(boolean[] row){
		int d=convertToDecimal(row);
		for (int dv:v){
			if (dv==d)
				return false;
		}
		if (this.currentSize>=this.v.length){			
			this.v=Arrays.copyOf(this.v,2*this.currentSize);
		}
		v[this.currentSize++]=d;
		return true;
	}
	
	private int convertToDecimal(boolean[] row){
		int ret=0;
		for (boolean c:row){
			if (c)
				ret=ret*2+1;
			else
				ret=ret*2;
		}
		return ret+1;
	}

	private boolean[] convertToBinary(int d){
		boolean b[]=new boolean[numCols];
		int remainder;
		int quotient=d-1;
		for (int i=(numCols-1);i>=0;i--){
			remainder=quotient%2;
			quotient=quotient/2;
			b[i]=(remainder==1);
		}
				
		return b;		
	}
	
	public int size(){
		return this.currentSize;
	}
	
	public boolean contains(boolean[] b){
		return contains(convertToDecimal(b));
	}
	private boolean contains(int d){
		for (int dv:v){
			if (dv==d)
				return true;
		}
		return false;
	}
	
	public boolean equals(Object o){
		if (o==this) return true;
		if (!(o instanceof BooleanArraySimple)) return false;
		
		BooleanArraySimple ba=(BooleanArraySimple)o;
		if (ba.size()!=this.size()) return false;
		if (ba.numCols!=this.numCols) return false;
		
		for (int dv:v){
			if (!ba.contains(dv))
				return false;
		}
									
		return true;				
	}
	
	public boolean[][] toArray(){		
		if (this.currentSize==0) return null;
		boolean[][] A_Matrix=new boolean[this.currentSize][this.numCols];
		
		for (int i=0;i<this.currentSize;i++){
			A_Matrix[i]=this.convertToBinary(this.v[i]);
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
