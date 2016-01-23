package es.uc3m.tsc.math;

public class MaxPlus {
	private double[][] A;
	private int m;
	private int n;
	
	public MaxPlus(boolean A[][]){
		m=A.length;
		n=A[0].length;
		this.A=new double[m][n];
		
		for (int i=0;i<m;i++){
			boolean[] rowA=A[i];
			for (int j=0;j<n;j++){
				this.A[i][j]=(rowA[j])?1:-Double.MAX_VALUE;
			}
		}
	}
	
	public MaxPlus(double A[][]){
		m=A.length;
		n=A[0].length;
		this.A=A;		
	}
	
	public static MaxPlus makeIdentidy(int n){
		double[][] A=new double[n][n];		
		for (int i=0;i<n;i++){			
			for (int j=0;j<n;j++){
				A[i][j]=(i==j)?0:-Double.MAX_VALUE;
			}
		}		
		return new MaxPlus(A);
	}
	
	public int getNumRows(){
		return m;		
	}
	public int getNumCols(){
		return n;		
	}
	public double[][] getMatrix(){
		return A;
	}
	
	
	public MaxPlus add(MaxPlus B){
		if (m!=B.getNumRows()) return null;
		if (n!=B.getNumCols()) return null;
				
		double[][] R=new double[m][n];
		for (int i=0;i<m;i++){
			double[] rowA=A[i];
			double[] rowB=B.A[i];
			for (int j=0;j<n;j++){				
				R[i][j]=(rowA[j]>rowB[j])?rowA[j]:rowB[j];
			}
		}
		return new MaxPlus(R);
	}
	
	public MaxPlus multiply(MaxPlus B){
		if (n!=B.getNumRows()) return null;
		int R_m=m;
		int R_n=B.getNumCols();
		double[][] R=new double[R_m][R_n];
		for (int i=0;i<R_m;i++){
			double[] rowA=A[i];
			for (int j=0;j<R_n;j++){
				double v=-Double.MAX_VALUE;
				for (int k=0;k<rowA.length;k++){
					double d=rowA[k]+B.A[k][j];
					if (v<d) v=d;										
				}				
				R[i][j]=v;
			}
		}
		return new MaxPlus(R);
	}

	public MaxPlus multiplyTranspose(MaxPlus B){
		if (n!=B.getNumCols()) return null;
		int R_m=m;
		int R_n=B.getNumRows();
		double[][] R=new double[R_m][R_n];
		for (int i=0;i<R_m;i++){
			double[] rowA=A[i];
			for (int j=0;j<R_n;j++){
				double[] rowB=B.A[j];
				double v=-Double.MAX_VALUE;
				for (int k=0;k<rowA.length;k++){
					double d=rowA[k]+rowB[k];
					if (v<d) v=d;										
				}				
				R[i][j]=v;
			}
		}
		return new MaxPlus(R);
	}
	
	public MaxPlus calcMaxPath(){
		return this.calcPath(m);
	}
	
	public MaxPlus calcPath(int k){
		MaxPlus Rout=this;
		MaxPlus Rs=this;
		for (int i=1;i<k;i++){
			Rs=Rs.multiply(Rs);
			Rout=Rout.add(Rs);
		}
		return Rout;
	}
	
	public MaxPlus calcKleeneStarN(){
		return this.calcMaxPath().add(MaxPlus.makeIdentidy(m));		
	}
	
	public boolean[][] shortestAdjacentMatrix(){
		MaxPlus R=this.calcMaxPath();
		boolean[][] bRout=new boolean[m][n];
		
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				bRout[i][j]=R.A[i][j]==1;
			}			
		}
		
		return bRout;
	}
	
	public boolean equals(MaxPlus B){
		return ArrayUtils.equal(A, B.A);		
	}
}

