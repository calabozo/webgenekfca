package es.uc3m.tsc.kfca.explore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import es.uc3m.tsc.math.ArrayUtils;

public class KFCAObjectInfo {
	private double[][] processedMatrix;
	double minVal;
	double maxVal;
	private KFCAResults kfcaResults;
	
	public KFCAObjectInfo(KFCAResults kfcaResults){
		this.kfcaResults=kfcaResults;
		this.processedMatrix=null;
	}
	
	public KFCAObjectInfo(double[][] processedMatrix){
		this.processedMatrix=processedMatrix;
		this.minVal=Double.MAX_VALUE;
		this.maxVal=Double.MIN_VALUE;
		for (double[] row:processedMatrix){
			for (double val:row){
				if (val<minVal) minVal=val;
				if (maxVal<val) maxVal=val;
			}
		}
	}
	
	private void initIfRequired(){
		if (this.processedMatrix==null){
			this.processedMatrix=this.kfcaResults.getProcessedMatrix();
			this.minVal=Double.MAX_VALUE;
			this.maxVal=Double.MIN_VALUE;
			for (double[] row:processedMatrix){
				for (double val:row){
					if (val<minVal) minVal=val;
					if (maxVal<val) maxVal=val;
				}
			}
		}
		
	}
	
	
	/*
	 * Return the concept id to which the object at rowId position belongs 
	 * for the given phi value in the MaxPlus domain.
	 * 
	 * @param rowId desired object index
	 * @param phi threhold value
	 * @return concept identifier
	 * 
	 */	
	public long getMaxConceptId(int rowId, double phi){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId
		long ret=0;
		for (int i=0;i<intent.length;i++){
			if (intent[i]<=phi){
				ret+=1<<i;				
			}
		}
		return ret;
	}

	
	/*
	 * Return the concept id to which the object at rowId position belongs 
	 * for the given varphi value in the MinPlus domain.
	 * 
	 * @param rowId desired object index
	 * @param varphi threhold value
	 * @return concept identifier
	 * 
	 */	
	public long getMinConceptId(int rowId, double varphi){		
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId		
		long ret=0;
		for (int i=0;i<intent.length;i++){
			if (intent[i]>=varphi){
				ret+=1<<i;				
			}
		}
		return ret;
	}
	
	/*	 
	 * Gets the phi range for the given concept id at the object row id.
	 * 
	 * @param rowId desired object index
	 * @param phi threhold value
	 * @return Range in the interval [phi0,phi1) in which the object belongs to conceptId
	 * 
	 */
	public double[] getMaxMarginPhi(int rowId, long conceptId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId
		double[] ret=new double[2];
		double minPhi=this.minVal;
		double maxPhi=this.maxVal;
		long remainderId=conceptId;
		for (int i=0;i<intent.length;i++){

			if ((remainderId&1)==1){
				if (intent[i]>minPhi) minPhi=intent[i];
			}else{
				if (intent[i]<maxPhi) maxPhi=intent[i];
			}
			remainderId=remainderId>>1;
		}
		remainderId=conceptId;
		if (minPhi>0) return null; 
		for (int i=0;i<intent.length;i++){
			if (intent[i]>maxPhi && ((remainderId&1)==1)) return null;			
			if (minPhi<=intent[i] && intent[i]<maxPhi && ((remainderId&1)==0)) return null;
			remainderId=remainderId>>1;
		}		
		ret[0]=minPhi;
		ret[1]=maxPhi>0?0:maxPhi;
		return ret;
	}
		
	/*	 
	 * Gets the varphi range for the given concept id at the object row id.
	 * 
	 * @param rowId desired object index
	 * @param varphi threhold value
	 * @return Range in the interval (varphi0,varphi1] in which the object belongs to conceptId
	 * 
	 */
	public double[] getMinMarginVarphi(int rowId, long conceptId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId
		double[] ret=new double[2];
		double minPhi=this.minVal;
		double maxPhi=this.maxVal;
		long remainderId=conceptId;
		for (int i=0;i<intent.length;i++){

			if ((remainderId&1)==0){
				if (intent[i]>minPhi) minPhi=intent[i];
			}else{
				if (intent[i]<maxPhi) maxPhi=intent[i];
			}
			remainderId=remainderId>>1;
		}
		remainderId=conceptId;
		if (maxPhi<0) return null;
		for (int i=0;i<intent.length;i++){
			if (intent[i]<minPhi && ((remainderId&1)==1)) return null;			
			if (minPhi<intent[i] && intent[i]<=maxPhi && ((remainderId&1)==0)) return null;
			remainderId=remainderId>>1;
		}
		ret[0]=minPhi<0?0:minPhi;
		ret[1]=maxPhi;
		return ret;
						
	}
	
	
	public long[] getMaxplusConceptId(int rowId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId
		int[] index=ArrayUtils.sortIndex(intent);
		long[] conceptsId=new long[index.length+1];
		int k=1;
		conceptsId[0]=0;
		if (intent[index[0]]>0) return Arrays.copyOf(conceptsId, 1);
		
		conceptsId[k]=(1<<index[0]);
		
		double lastIntentValue=intent[index[0]];
		double intentValue;
		for (int i=1;i<index.length;i++){
			intentValue=intent[index[i]];
			if (intentValue>0) break;
			if (lastIntentValue!=intentValue){
				k++;
				conceptsId[k]=conceptsId[k-1]+(1<<index[i]);
			}else{
				conceptsId[k]=conceptsId[k]+(1<<index[i]);
			}
			lastIntentValue=intentValue;
		}
		
		return Arrays.copyOf(conceptsId, k+1);
	}
	public double[] getMaxplusPhi(int rowId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId].clone(); //Set of attributes belonging to object rowId
		double[] aux=new double[intent.length+2];
		Arrays.sort(intent);
		aux[0]=this.minVal;
		int i=1;
		for (double val: intent){
			if (val>0){
				aux[i]=0;
				return Arrays.copyOf(aux,i+1);
			}
			if (val!=aux[i-1]){
				aux[i]=val;
				i++;
			}			
		}
		if (0!=aux[i-1]){
			aux[i]=0;
		}
		return Arrays.copyOf(aux,i+1);	
	}
	public long[] getMinplusConceptId(int rowId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId]; //Set of attributes belonging to object rowId
		int[] index=ArrayUtils.sortIndex(intent);
		long[] conceptsId=new long[index.length+1];
		int k=1;
		int lasti=index.length-1;
		conceptsId[0]=0;
		
		if (intent[index[lasti]]<0) return Arrays.copyOf(conceptsId, 1);
		conceptsId[k]=(1<<index[lasti]);
		
		double lastIntentValue=intent[index[lasti]];
		double intentValue;
		for (int i=lasti-1;i>=0;i--){
			intentValue=intent[index[i]];
			if (intentValue<0) break;
			if (lastIntentValue!=intentValue){
				k++;
				conceptsId[k]=conceptsId[k-1]+(1<<index[i]);
			}else{
				conceptsId[k]=conceptsId[k]+(1<<index[i]);
			}
			lastIntentValue=intentValue;
		}
		
		return Arrays.copyOf(conceptsId, k+1);
	}
	public double[] getMinplusVarphi(int rowId){
		this.initIfRequired();
		double[] intent=this.processedMatrix[rowId].clone(); //Set of attributes belonging to object rowId
		double[] aux=new double[intent.length+2];
		Arrays.sort(intent);
		aux[0]=this.maxVal;
		int i=1;
		double val;
		for (int k=intent.length-1;k>=0;k--){
			val=intent[k];
			if (val<0){
				aux[i]=0;
				return Arrays.copyOf(aux,i+1);
			}
			if (val!=aux[i-1]){
				aux[i]=val;
				i++;
			}
			
		}
		if (0!=aux[i-1]){
			aux[i]=0;
		}
		return Arrays.copyOf(aux,i+1);
	}

	
	
}
