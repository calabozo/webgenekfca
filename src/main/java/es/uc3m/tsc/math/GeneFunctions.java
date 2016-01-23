package es.uc3m.tsc.math;

import java.util.Arrays;

import org.apache.log4j.Logger;

import es.uc3m.tsc.gene.PreprocessorEnum;

public class GeneFunctions {
		
	public class ProcessedDataMatrix{				
		public double[][] data;
		public double min;
		public double max;				
	}
	
	public static ProcessedDataMatrix calcPreprocessor(PreprocessorEnum preprocessorType, double[][] rawData){
		ProcessedDataMatrix ret=new GeneFunctions().new ProcessedDataMatrix();
	    double maxValue=-Double.MAX_VALUE;
        double minValue=Double.MAX_VALUE;
        double data[][];
        		
		int numRows=rawData.length;
		int numCols=rawData[0].length;		
		double sumT=0;
		double sumT2=0;
		boolean useLogarithm=preprocessorType.getId()<PreprocessorEnum.PREPROUNIT.getId();
		
		switch(preprocessorType){
		case LOGPREPROARITMEAN:
		case PREPROARITMEAN:
			data=new double[numRows][numCols];
			for (int i=0;i<numRows;i++){
				sumT=0;
				for (double num: rawData[i]) sumT+=num;
				
				double aritMean=(sumT/numCols);
				if (aritMean==0){
					for (int j=0;j<numCols;j++) data[i][j]=0;					
					if (minValue>0) minValue=0;
					if (maxValue<0) maxValue=0;
				
				}else{
					for (int j=0;j<numCols;j++){
						double num=rawData[i][j]/aritMean;
						if (useLogarithm) num=Math.log(num);
						
						data[i][j]=num;					
						if (minValue>num) minValue=num;
						if (maxValue<num) maxValue=num;
					}
				}
			}
			break;
			
		case LOGPREPROGEOMEAN:
		case PREPROGEOMEAN:
			double prodT=1;
			data=new double[numRows][numCols];
			for (int i=0;i<numRows;i++){ 
				prodT=1;
				for (double num: rawData[i]) prodT*=num;
								
				if (prodT==0){
					for (int j=0;j<numCols;j++) data[i][j]=0;					
					if (minValue>0) minValue=0;
					if (maxValue<0) maxValue=0;
				
				}else{
					double geoMean=Math.pow(prodT, 1/(double)numCols);
					for (int j=0;j<numCols;j++){
						double num=rawData[i][j]/geoMean;
						if (useLogarithm) num=Math.log(num);
						
						data[i][j]=num;					
						if (minValue>num) minValue=num;
						if (maxValue<num) maxValue= num;					
						
					}
				}
			}
			break;
			
		case LOGPREPROGEOMAX:
		case PREPROGEOMAX:
			double maxValueInRow=-Double.MAX_VALUE;
			data=new double[numRows][numCols];
			for (int i=0;i<numRows;i++){ 
				
				for (double num: rawData[i]) if (Math.abs(num)>maxValueInRow) maxValueInRow=Math.abs(num);
				
				if (maxValueInRow==0){
					for (int j=0;j<numCols;j++) data[i][j]=0;					
					if (minValue>0) minValue=0;
					if (maxValue<0) maxValue=0;
				
				}else{
					for (int j=0;j<numCols;j++){
						double num=rawData[i][j]/maxValueInRow;
						if (useLogarithm) num=Math.log(num);
						
						data[i][j]=num;					
						if (minValue>num) minValue=num;
						if (maxValue<num) maxValue= num;					
						
					}
				}
			}
			break;
			
		case LOGPREPROUNIT:
		case PREPROUNIT:
			data=new double[numRows][numCols];
			for (int i=0;i<numRows;i++){ 				
				for (int j=0;j<numCols;j++){
					double num=rawData[i][j];
					if (useLogarithm) num=Math.log(num);
					
					data[i][j]=num;					
					if (minValue>num) minValue=num;
					if (maxValue<num) maxValue=num;							
				}
			}
			
			break;
		case LOGPREPROMEANVAR:
		case PREPROMEANVAR:
			if (useLogarithm){
				double[][] logData=new double[numRows][numCols];
				for (int i=0;i<numRows;i++){
					for (int j=0;j<numCols;j++){
						logData[i][j]=Math.log(rawData[i][j]);
					}
				}
				data=normalizeByRows(logData);
			}else{
				data=normalizeByRows(rawData);
			}
			double[] maxMinValue=ArrayUtils.calcMaxMin(data);
			minValue=maxMinValue[1];
			maxValue=maxMinValue[0];
			break;
		case LOGPREPROMEANVARCOLROW:
		case PREPROMEANVARCOLROW:
			if (useLogarithm){
				data=new double[numRows][numCols];
				for (int i=0;i<numRows;i++){
					for (int j=0;j<numCols;j++){
						data[i][j]=Math.log(rawData[i][j]);
					}
				}
			}else{
				data=rawData;
			}
			for (int c=0;c<100;c++){
				data=normalizeByRows(normalizeByColumns(data));
				double[] sumSqCols=new double[numCols];
				double[] sumCols=new double[numCols];
				Arrays.fill(sumSqCols, 0);
				Arrays.fill(sumCols, 0);
				for (int i=0;i<numRows;i++){
					for (int j=0;j<numCols;j++){
						sumSqCols[j]+=data[i][j]*data[i][j];
						sumCols[j]+=data[i][j];
					}
				}
				double norm=0;
				double sumabs=0;
				for (int j=0;j<numCols;j++){
					norm+=(sumSqCols[j]-numRows)*(sumSqCols[j]-numRows);
					sumabs+=Math.abs(sumCols[j]);
				}
				double error=Math.sqrt(norm)+sumabs;
				if (error<1e-6){
					break;
				}
				//error = norm(sum(M.^2)-m) + (sum (abs(sum(M))) ); 
			}
			double[] maxMin=ArrayUtils.calcMaxMin(data);
			minValue=maxMin[1];
			maxValue=maxMin[0];
			break;
			/*
		case PREPRONULL:
			data=new double[numRows][numCols];
			for (int i=0;i<numRows;i++){ 				
				for (int j=0;j<numCols;j++){
					double num=rawData[i][j];
					data[i][j]=num;	
					if (minValue>num) minValue=num;
					if (maxValue<num) maxValue=num;							
				}
			}		
			break;
			*/
			
		default:
			data=null;
			Logger logger = Logger.getLogger("es.uc3m.es.math");            
            logger.error("Unknown Preprocessor type "+preprocessorType);
		}
		
		ret.data=data;
		ret.max=maxValue;
		ret.min=minValue;
		
		return ret;
		
	}
	
	private static double[][] normalizeByRows(double[][] rawData){
		double sumT=0;
		double sumT2=0;
		int numRows=rawData.length;
		int numCols=rawData[0].length;		
		double[][] data=new double[numRows][numCols];
		
		double sqrtNumCols=Math.sqrt(numCols);
		for (int i=0;i<numRows;i++){
			sumT=0;
			sumT2=0;
			for (double num:rawData[i]) sumT+=num;
			double aritMean=(sumT/numCols);
			double[] row=new double[numCols];
			for (int j=0;j<numCols;j++) row[j]=rawData[i][j]-aritMean;
			for (int j=0;j<numCols;j++) sumT2+=row[j]*row[j];
			if (sumT2==0){
				for (int j=0;j<numCols;j++) data[i][j]=0;
			}else{
				double norm=sqrtNumCols/Math.sqrt(sumT2);
				for (int j=0;j<numCols;j++){
					data[i][j]=row[j]*norm;
				}
			}
		}
		return data;
	}
	
	private static double[][] normalizeByColumns(double[][] rawData){
		double sumT=0;
		double sumT2=0;
		int numRows=rawData.length;
		int numCols=rawData[0].length;		
		double[][] data=new double[numRows][numCols];
		
		double sqrtNumRows=Math.sqrt(numRows);
		for (int j=0;j<numCols;j++){
			sumT=0;
			sumT2=0;
			for (int i=0;i<numRows;i++){
				sumT+=rawData[i][j];
			}
			double aritMean=(sumT/numRows);
			double[] col=new double[numRows];
			for (int i=0;i<numRows;i++) col[i]=rawData[i][j]-aritMean;
			for (int i=0;i<numRows;i++) sumT2+=col[i]*col[i];
			if (sumT2==0){
				for (int i=0;i<numCols;i++) data[i][j]=0;
			}else{
				double norm=sqrtNumRows/Math.sqrt(sumT2);
				for (int i=0;i<numRows;i++){
					data[i][j]=col[i]*norm;
				}
			}
		}
		return data;
	}
	
	public static boolean calcPreprocessor(PreprocessorEnum preprocessorType, double[] row, Integer[] groupId, double[] resultRow){
		/*
		 * Groups the value of each row of the data matrix by its group id.
		 * The user has previously selected in the preprocessor view which columns are grouped together
		 * 
		 */
		if (row.length!= groupId.length) return false;
		double numGroup;
		
		if (preprocessorType!=PreprocessorEnum.LOGPREPROGEOMEAN && preprocessorType!=PreprocessorEnum.PREPROGEOMEAN){
			for (int i=0;i<resultRow.length;i++){
				resultRow[i]=0;
				numGroup=0;
				for(int j=0;j<row.length;j++){
					if (groupId[j]==i){
						resultRow[i]+=row[j];
						numGroup++;
					}
				}
				if (numGroup>0)	resultRow[i]=resultRow[i]/numGroup;
			}
		}else{
			for (int i=0;i<resultRow.length;i++){
				resultRow[i]=1;
				numGroup=0;
				for(int j=0;j<row.length;j++){
					if (groupId[j]==i){
						resultRow[i]*=row[j];
						numGroup++;
					}
				}
				if (numGroup>0){
					
					resultRow[i]=Math.pow(resultRow[i], 1/numGroup);
				}
			}
		}
		return true;

	}


}
