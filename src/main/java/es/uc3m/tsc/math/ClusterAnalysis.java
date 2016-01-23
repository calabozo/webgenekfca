package es.uc3m.tsc.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.math.BigIntegerMath;

import es.uc3m.tsc.kfca.explore.KFCAResults;


/*
 * Inspired by Cluster Analysis for Gene Expression Data: A Survey, 2014
 * Daxin Jiang, Chun Tang, and Aidong Zhang
 */
public class ClusterAnalysis {
	
	public enum SimilarityEnum{EUCLIDEAN,CORRELATION};
	
	protected double[][] matrix;
	protected int nc;
	protected KFCAResults kfcaResults;
	//Logger logger;
	
	public ClusterAnalysis(double[][] matrix){
		this.matrix=matrix;
		this.nc=this.matrix[0].length;
		//logger=Logger.getLogger("es.uc3m.tsc.math.ClusterAnalysis");
	}
	public ClusterAnalysis(KFCAResults kfcaResults){
		this.kfcaResults=kfcaResults;
	}
	private synchronized void initIfRequired(){
		if (this.matrix==null){
			this.matrix=this.kfcaResults.getProcessedMatrix();
			this.nc=this.matrix[0].length;			
		}
	}
	
	private double[] getCentroid(long conceptId,Integer[] rows){
		double[] centroid=new double[nc];
		
		for (int rowi:rows){
			long tmpConceptId=conceptId;
			for (int d=0;d<nc;d++){
				if (tmpConceptId%2==1){
					centroid[d]+=matrix[rowi][d];
				}
				tmpConceptId=tmpConceptId>>1;
			}
		}		
		
		for (int d=0;d<nc;d++){
			centroid[d]=centroid[d]/rows.length;			
		}
		return centroid;
	}
	
	
	/*
	 * Calculates the euclidean distance
	 * Euclidean(Oi,Oj)=sqrt(sum(d=0,p,(Oid-Ojd)^2))
	 */
	public double euclideanDistance(long conceptId,double[] rowi, double[] rowj){		
		double ecl=0;
		for (int d=0;d<nc;d++){
			if (conceptId%2==1){
				double dif=rowi[d]-rowj[d];
				ecl+=dif*dif;
			}
			conceptId=conceptId>>1;
		}		
		return Math.sqrt(ecl);
	}
	
	/*
	 * Calculates the pearson correlation coefficient
	 * corr (x,y) = cov (x,y) / (std (x) * std (y)
	 * 
	 */
	public double pearsonCorrelation(long conceptId,double[] rowi, double[] rowj){		
		int d=0;
		int p=0; //Object cluster length
		double[] Oi=new double[nc];
		double[] Oj=new double[nc];
		double mui=0; //Mean object i
		double muj=0; //Mean object j
		for (int i=0;i<nc;i++){
			if (conceptId%2==1){
				Oi[d]=rowi[d];
				mui+=Oi[d];
				
				Oj[d]=rowj[d];
				muj+=Oj[d];
				
				d++;
			}
			conceptId=conceptId>>1;
		}		
		p=d;
		
		mui=mui/p;
		muj=muj/p;
		
		double numerator=0;
		double denominatori=0;
		double denominatorj=0;
		double dOi;
		double dOj;
		for (d=0;d<p;d++){
			dOi=(Oi[d]-mui);
			dOj=(Oj[d]-muj);
			numerator+=dOi*dOj;
			denominatori+=dOi*dOi;
			denominatorj+=dOj*dOj;				
		}
		if (denominatori==0 || denominatorj==0 || numerator==0) return 0;
		return numerator/Math.sqrt(denominatori*denominatorj);
	}

	//TODO:Add Mean Squared Residue Score. From: Biclustering of Expression Data, Chenc and George, 2000
	private class MatrixMeans{
		double aIJ; //Mean by rows&cols
		double[] aiJ; //Mean by cols
		double[] aIj; //Mean by rows
		int[] colIndex;
	}
	public MatrixMeans matrixMeans (long conceptId, Integer[] rowIndex){
		this.initIfRequired();
		
		int[] colIndex=new int[nc];
		long tmp=conceptId;
		int clustNumCol=0;
		int clustNumRow=rowIndex.length;
		for (int j=0;j<nc;j++){
			if (tmp%2==1){
				colIndex[clustNumCol++]=j;
			}
			tmp=tmp>>1;
		}
		colIndex=Arrays.copyOf(colIndex, clustNumCol);
		
		MatrixMeans m=new MatrixMeans();
		m.aIJ=0;
		m.aiJ=new double[clustNumRow];
		m.aIj=new double[clustNumCol];
		m.colIndex=colIndex;
		
		for (int i=0;i<clustNumRow;i++){
			double[] row=this.matrix[rowIndex[i]];						
			for (int j=0;j<clustNumCol;j++){
				double aij=row[colIndex[j]];
				m.aIJ+=aij;
				m.aIj[j]+=aij/clustNumCol;
				m.aiJ[i]+=aij/clustNumRow;
			}
		}
		m.aIJ=m.aIJ/(clustNumRow*clustNumCol);
		return m;
	}
	/*
	 * This does not work
	 */
	@Deprecated
	public double homogeneity1MSR(long conceptId, Integer[] rowIndex){
		MatrixMeans m=matrixMeans(conceptId,rowIndex);
		int[] colIndex=m.colIndex;
		int clustNumCol=colIndex.length;
		int clustNumRow=rowIndex.length;
		double h=0;
		for (int i=0;i<clustNumRow;i++){
			double[] row=this.matrix[rowIndex[i]];						
			for (int j=0;j<clustNumCol;j++){
				double d=row[colIndex[j]]-m.aiJ[i]-m.aIj[j]+m.aIJ;
				h+=d*d;
			}
		}
		h=h/(clustNumCol*clustNumRow);
		return h;
	}
	
	
	
	/*
	 * Calculates the homogeneity.
	 * From: Cluster Analysis for Gene Expression Data: A Survey, 2004, Daxin Jiang, Chun Tang, and Aidong Zhang
	 * Section 3.1
	 */
	public double homogeneity1(long conceptId, Integer[] rows, SimilarityEnum s){
		this.initIfRequired();
		double h1=0;
		int numObjs=rows.length;
		for (int i=0;i<numObjs-1;i++){
			for (int j=i+1;j<numObjs;j++){
				switch (s){
				case EUCLIDEAN:
					h1+=2*euclideanDistance(conceptId,this.matrix[rows[i]],this.matrix[rows[j]]);
					break;
				case CORRELATION:
					h1+=2*pearsonCorrelation(conceptId,this.matrix[rows[i]],this.matrix[rows[j]]);
					break;
				}
					
			}
		}
		h1=h1/(numObjs*(numObjs-1));
		//logger.info("Calculating H1: "+h1+" size: "+rows.length+" similarity:"+s+ " rows:"+Arrays.toString(rows));		
		return h1;
	}
	public double homogeneity2(long conceptId, Integer[] rows, SimilarityEnum s){
		this.initIfRequired();
		double h1=0;
		int numObjs=rows.length;
		double[] centroid=this.getCentroid(conceptId, rows);
		for (int rowId:rows){
			double[] row=this.matrix[rowId];
			switch (s){
				case EUCLIDEAN:
					h1+=euclideanDistance(conceptId,row,centroid);
					break;
				case CORRELATION:
					h1+=pearsonCorrelation(conceptId,row,centroid);
					break;
					
			}
		}
		h1=h1/numObjs;
		//logger.info("Calculating H1: "+h1+" size: "+rows.length+" similarity:"+s+ " rows:"+Arrays.toString(rows));		
		return h1;
	}
	
	
	/*
	 * Calculates the separation between two clusters
	 */
	public double separation1(long conceptId1, Integer[] rows1,HashMap<Long,List<Integer>> rows2, SimilarityEnum s){
		this.initIfRequired();
		double sep=0;
		Iterator<Long> iter=rows2.keySet().iterator();
		while(iter.hasNext()){
			Long conceptId2=iter.next();
			sep+=separation1(conceptId1,rows1,conceptId2.longValue(),rows2.get(conceptId2).toArray(new Integer[0]),s);			
		}
		return sep/rows2.size();
	}
	
	private double separation1(long conceptId1, Integer[] rows1,long conceptId2, Integer[] rows2, SimilarityEnum s){		
		double h1=0;
		int numObjs1=rows1.length;
		int numObjs2=rows2.length;
		for (int i=0;i<numObjs1;i++){
			for (int j=0;j<numObjs2;j++){
				switch (s){
				case EUCLIDEAN:
					h1+=euclideanDistance(conceptId1,this.matrix[rows1[i]],this.matrix[rows2[j]]);
					break;
				case CORRELATION:
					h1+=pearsonCorrelation(conceptId1,this.matrix[rows1[i]],this.matrix[rows2[j]]);
					break;
				}					
			}
		}
		h1=h1/(numObjs1*numObjs2);
		//logger.info("Calculating H1: "+h1+" size: "+rows.length+" similarity:"+s+ " rows:"+Arrays.toString(rows));		
		return h1;
	}
	
	public double separation2(long conceptId1, Integer[] rows1,HashMap<Long,List<Integer>> rows2, SimilarityEnum s){
		this.initIfRequired();
		double sep=0;
		Iterator<Long> iter=rows2.keySet().iterator();
		while(iter.hasNext()){
			Long conceptId2=iter.next();
			sep+=separation2(conceptId1,rows1,conceptId2.longValue(),rows2.get(conceptId2).toArray(new Integer[0]),s);			
		}
		return sep/rows2.size();
	}
	private double separation2(long conceptId1, Integer[] rows1,long conceptId2, Integer[] rows2, SimilarityEnum s){		
		double h1=0;
		double[] centroid1=this.getCentroid(conceptId1, rows1);
		double[] centroid2=this.getCentroid(conceptId1, rows2);
		switch (s){
		case EUCLIDEAN:
			h1+=euclideanDistance(conceptId1,centroid1,centroid2);
			break;
		case CORRELATION:
			h1+=pearsonCorrelation(conceptId1,centroid1,centroid2);
			break;
		}					
		
		//logger.info("Calculating H1: "+h1+" size: "+rows.length+" similarity:"+s+ " rows:"+Arrays.toString(rows));		
		return h1;
	}
	
	/*
	 * Probability to found at least
	 * k-genes from a functional category
	 * n-cluster size
	 * f-total number of genes within a functional category
	 * g-total number of genes within the genome
	 * 
	 */
	public static double hipergeometricDisribution(int k, int n, int f, int g){		
		return nCr(f,k).multiply(nCr(g-f,n-k)).divide(nCr(g,n),MathContext.DECIMAL64).doubleValue();
		//return (double)(nCr(f,k)*nCr(g-f,n-k))/(double)nCr(g,n);
	}
	public static double pValue(int k, int n, int f, int g){
		double p=0;		
		BigDecimal nCrgn=new BigDecimal(BigIntegerMath.binomial(g, n));
		//BigDecimal nCrgn=nCr(g,n);
		for (int i=0;i<k;i++){
			BigInteger num=BigIntegerMath.binomial(f, i).multiply(BigIntegerMath.binomial(g-f,n-i));
			p+=new BigDecimal(num).divide(nCrgn,MathContext.DECIMAL64).doubleValue();
			//p+=nCr(f,i).multiply(nCr(g-f,n-i)).divide(nCrgn,MathContext.DECIMAL64).doubleValue();
			
		}
		if (p>1 && p<1.00000000000001)	p=1;		
		return 1-p;
	}
	
	private static BigDecimal nCr(long n, int k){	
		if (k==0) return BigDecimal.ONE;
		if (n<=k) return BigDecimal.ONE;
		BigDecimal num=BigDecimal.valueOf(n+1-1);
		BigDecimal kfact=BigDecimal.ONE;
		for (int i=2;i<=k;i++){
			num=num.multiply(BigDecimal.valueOf(n+1-i));
			kfact=kfact.multiply(BigDecimal.valueOf(i));
		}
		return num.divide(kfact);
	}
}
