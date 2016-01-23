package es.uc3m.tsc.math;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

import es.uc3m.tsc.math.GeneFunctions.ProcessedDataMatrix;

/*
 * Matrix basic information for the histogram to be shown in a view
 */
public class MatrixInfo {

	private double[][] histogram;
	private long numElements;
	private double min;
	private double max;
	private Double[] phis;
	private double[][] data;
	private int numBins;
	
	public MatrixInfo(){
		this.histogram=null;
		this.numElements=0;
		this.min=0;
		this.max=0;
		this.numBins=0;
	}
	
	public MatrixInfo(double[][] data, int numBins){
		this.numBins=numBins;
		double max=-Double.MAX_VALUE;
		double min=Double.MAX_VALUE;
		for (int i=0;i<data.length;i++){
			for (int j=0;j<data[0].length;j++){
				if (data[i][j]>max){
					max=data[i][j];
				}
				if (data[i][j]<min){
					min=data[i][j];
				}
			}
		}
		this.fill(data, max, min, numBins);
		
	}
	
	public MatrixInfo(GeneFunctions.ProcessedDataMatrix pm,int numBins){
		this.fill(pm.data, pm.max, pm.min, numBins);
	}
		
	private void fill(double[][] data, double maxValue, double minValue, int numBins){
		Set<Double> elems = new HashSet<Double>();
		        
        double[][] hist = new double[numBins + 1][2];
        this.setHistogram(hist);
        this.setMax(maxValue);
        this.setMin(minValue);
        this.setData(data);
        for (int i = 0; i < numBins + 1; i++) {
            hist[i][0] = minValue + (maxValue - minValue) / numBins * i;
            hist[i][1] = 0;
        }
        try {
            double k = numBins / (maxValue - minValue);
            for (double[] row : data) {
                for (double num : row) {
                    elems.add(num);
                    int i = (int) ((num - minValue) * k);
                    hist[i][1]++;
                }
            }
            double norm = data.length * data[0].length / k;
            for (int i = 0; i < numBins + 1; i++) {
                hist[i][1] = hist[i][1] / norm;
            }
            
            Double[] phis=new Double[elems.size()];            		
            phis=(Double[])elems.toArray(phis);
    		Arrays.sort(phis);
    		this.setPhis(phis);            
            this.setNumElements(elems.size());
        } catch (Exception e) {
            Logger logger = Logger.getLogger("es.uc3m.tsc.math");
            logger.error("Cannot calculate Histogram from:" + minValue + " to " + maxValue + " : " + e.toString());
        }
	}
	
	
	
	public double[][] getHistogram() {
		return histogram;
	}
	public void setHistogram(double[][] histogram) {
		this.histogram = histogram;
	}
	public long getNumElements() {
		return numElements;
	}
	public void setNumElements(long numElements) {
		this.numElements = numElements;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}

	public Double[] getPhis() {
		return phis;
	}

	public void setPhis(Double[] phis) {
		this.phis = phis;
	}

	public double[][] getData() {
		return data;
	}

	public void setData(double[][] data) {
		this.data = data;
	}
	
	public int getNumBins(){
		return this.numBins;
	}
}
