package es.uc3m.tsc.util;

import java.util.Arrays;


public class BooleanArrayLarge implements BooleanArray{
	boolean[] rows;
	int numCols;
	
	
	/*
	 * @params: 
	 * int maxSize: Maximun number of rows for the array
	 * int numCols: Number of columns for the array 
	 */
	public BooleanArrayLarge(int numCols){
		this.rows=new boolean[(int)Math.pow(2, numCols)];		
		this.numCols=numCols;		
	}
		
	public boolean add(boolean[] row){
		int d=convertToDecimal(row);
		rows[d]=true;
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
		return ret;
	}

	private boolean[] convertToBinary(int d){
		boolean b[]=new boolean[numCols];
		int remainder;
		int quotient=d;
		for (int i=(numCols-1);i>=0;i--){
			remainder=quotient%2;
			quotient=quotient/2;
			b[i]=(remainder==1);
		}
				
		return b;		
	}
			
	public boolean contains(boolean[] b){
		return contains(convertToDecimal(b));
	}
	private boolean contains(int d){		
		return this.rows[d];
	}
	
	public boolean equals(Object o){
		if (o==this) return true;
		if (!(o instanceof BooleanArrayLarge)) return false;
		
		BooleanArrayLarge ba=(BooleanArrayLarge)o;		
		if (ba.numCols!=this.numCols) return false;
											
		return Arrays.equals(this.rows, ba.rows);				
	}
	
	public int size(){
		int r=0;
		for (boolean b:rows){
			if (b) r++;
		}
		return r;
	}
	
	public boolean[][] toArray(){
		int size=this.size();
		if (size==0) return null;
		boolean[][] A_Matrix=new boolean[size][this.numCols];
		int k=0;
		for (int i=0;i<rows.length;i++){
			if (rows[i]){				
				A_Matrix[k++]=this.convertToBinary(i);				
			}
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
