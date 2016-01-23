package es.uc3m.tsc.kfca.explore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.persistence.Transient;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.kfca.tools.KFCAConceptsIntersection;
import es.uc3m.tsc.math.MatrixInfo;
import es.uc3m.tsc.threads.ThreadProcessor;
import es.uc3m.tsc.util.BooleanArrayLarge;

public class KFCAExplore extends ThreadProcessor{
    
	@Transient
    @Resource 
    GeneInfo geneInfo;
    
	public class Tuple{
		private double phi;
		private int numConcepts;		
		
		public Tuple(double phi,int numConcepts){
			this.phi=phi;
			this.numConcepts=numConcepts;
		}
		public double getPhi(){
			return phi;
		}
		public int getNumConcepts(){
			return numConcepts;
		}
		public double[] getArray(){
			double[] a=new double[2];
			a[0]=phi;
			a[1]=numConcepts;
			return a;
		}
	}
			
	protected ArrayList<Tuple> numConceptsMaxPlus;
	protected ArrayList<Tuple> numConceptsMinPlus;
	protected MatrixInfo mi;
	int explorationType;
	protected String[] rowNames;
	protected String[] colNames;
	
	public KFCAExplore(Preprocessor p){
		super(p);		
		this.logger = Logger.getLogger("es.uc3m.tsc.kfcatools.KFCAExplore");   
		this.numConceptsMaxPlus=null;
		this.numConceptsMinPlus=null;
		this.mi=null;
		this.explorationType=0;
		this.rowNames=p.getRowNames();
		this.colNames=p.getGroupName();
		this.explorationType=p.getGeneExpressionType().getMaskedValue();
		this.geneInfo=p.getGeneInfo();
		
    	
	}
	
	public void resetData(){
		this.numConceptsMaxPlus=null;
		this.numConceptsMinPlus=null;
		this.mi=null;
	}
	
	protected void explorePhi(){
		double maxValue=mi.getMax();
    	double minValue=mi.getMin();    	
    	double[][] R=mi.getData();
    	Double[] phis=mi.getPhis();
    	double dValue=maxValue-minValue;
    	
    	double step=phis.length/this.preprocessor.getMaxPhiToExplore();
		
    	numConceptsMaxPlus=new ArrayList<Tuple>();
    	
		int nr=R.length;
		int nc=R[0].length;
		boolean[][] IMaxPlus=new boolean[nr][nc];		
			
		double rowR[];
		boolean rowIMax[];		
		boolean addLastPhi=false;
		
		//BooleanArrayList clarifiedMaxPlus=new BooleanArrayList(1000);
		//BooleanArrayList clarifiedMaxPlus_prev=null;
		BooleanArrayLarge clarifiedMaxPlus=new BooleanArrayLarge(nc);
		BooleanArrayLarge clarifiedMaxPlus_prev=null;
					
    	//for (double phi=minValue;phi<=0 && !this.shouldStop;phi+=step){
    	//	k++;
		double k=0;
		int ik;
		double phi=phis[0].doubleValue();		
		do{			
    		this.ratioDone=(phi-minValue)/dValue; 
    		//this.ratioDone=(double)k/maxPhis; //Don't know which is faster, this or the previous line
    		
    		clarifiedMaxPlus_prev=clarifiedMaxPlus;    	    		    		
    		//clarifiedMaxPlus=new BooleanArrayList(1000);    		
    		clarifiedMaxPlus=new BooleanArrayLarge(nc);
    		
    		for (int i=0;i<nr;i++){
    			rowR=R[i];
    			rowIMax=IMaxPlus[i];    			
    			for (int j=0;j<nc;j++){
    				rowIMax[j]=rowR[j]<=phi;    				
    			}
        		//We clarify by rows
    			//if (!clarifiedMaxPlus.contains(rowIMax))    				
    				clarifiedMaxPlus.add(rowIMax);    			
    		}    		
    		//Compare if current and previous clarification are the same
    		if (!clarifiedMaxPlus.equals(clarifiedMaxPlus_prev)){    
    			boolean[][] bClarifiedIMaxPlus=clarifiedMaxPlus.toArray();    			
    			int numConceptsMax=KFCAConceptsIntersection.getNumberConcepts(bClarifiedIMaxPlus);
    			logger.info("Processsing: "+ this.ratioDone+" Phi: "+phi+" maxplusConcepts:"+numConceptsMax);
    			
    			if (!addLastPhi && clarifiedMaxPlus_prev!=null && clarifiedMaxPlus_prev.size()>0){    				
    				boolean[][] bClarifiedIMaxPlus_prev=clarifiedMaxPlus_prev.toArray();
    				int numConceptsMax_prev=KFCAConceptsIntersection.getNumberConcepts(bClarifiedIMaxPlus_prev);
        			numConceptsMaxPlus.add(new Tuple(phis[(int)(k-step)],numConceptsMax_prev));        			
    			}    			
    			numConceptsMaxPlus.add(new Tuple(phi,numConceptsMax));
    			addLastPhi=true;
    		}else{
    			addLastPhi=false;
    		}
    		
    		k+=step;
    		ik=(int)k;
    		if (ik<phis.length){    		
    			phi=phis[ik].doubleValue();
    		}else{
    			break;
    		}
    	}while (phi<=0 && !this.shouldStop);
		
    	if (this.shouldStop) this.resetData();
    	
		
	}
	
	protected void exploreVarPhi(){
		double maxValue=mi.getMax();
    	double minValue=mi.getMin();    	
    	double[][] R=mi.getData();    	
    	Double[] varphis=mi.getPhis();
    	double dValue=maxValue-minValue;
    	
    	double step=varphis.length/this.preprocessor.getMaxPhiToExplore();
    	
    	
		
    	numConceptsMinPlus=new ArrayList<Tuple>();
    	
		
				
		int nr=R.length;
		int nc=R[0].length;
		boolean[][] IMinPlus=new boolean[nr][nc];		
			
		double rowR[];
		boolean rowIMin[];		
		boolean addLastVarPhi=false;
		
		//BooleanArrayList clarifiedMinPlus=new BooleanArrayList(1000);
		//BooleanArrayList clarifiedMinPlus_prev=null;
		
		BooleanArrayLarge clarifiedMinPlus=new BooleanArrayLarge(nc);
		BooleanArrayLarge clarifiedMinPlus_prev=null;
				
		
		double k=0;
		int ik;
		for (k=0;varphis[(int)k]<0 && k<varphis.length;k++);
		k=k-step;
		double varphi=0;	
		do{		    	
    		
    		this.ratioDone=(varphi-minValue)/dValue; 
    		//this.ratioDone=(double)k/maxPhis; //Don't know which is faster, this or the previous line
    		
    		clarifiedMinPlus_prev=clarifiedMinPlus;    	    		
    		//clarifiedMinPlus=new BooleanArrayList(1000);    		
    		clarifiedMinPlus=new BooleanArrayLarge(nc);
    		
    		for (int i=0;i<nr;i++){
    			rowR=R[i];
    			rowIMin=IMinPlus[i];    			
    			for (int j=0;j<nc;j++){
    				rowIMin[j]=rowR[j]>=varphi;    				
    			}
        		//We clarify by rows
    			//if (!clarifiedMinPlus.contains(rowIMin))    				
    				clarifiedMinPlus.add(rowIMin);    			
    		}    		
    		//Compare if current and previous clarification are the same
    		if (!clarifiedMinPlus.equals(clarifiedMinPlus_prev)){    
    			boolean[][] bClarifiedIMaxPlus=clarifiedMinPlus.toArray();    			
    			int numConceptsMin=KFCAConceptsIntersection.getNumberConcepts(bClarifiedIMaxPlus);
    			logger.info("Processsing: "+ this.ratioDone+" VarPhi: "+varphi+" minplusConcepts:"+numConceptsMin);
    			
    			if (!addLastVarPhi && clarifiedMinPlus_prev!=null && clarifiedMinPlus_prev.size()>0){    				
    				boolean[][] bClarifiedIMaxPlus_prev=clarifiedMinPlus_prev.toArray();
    				int numConceptsMin_prev=KFCAConceptsIntersection.getNumberConcepts(bClarifiedIMaxPlus_prev);
        			numConceptsMinPlus.add(new Tuple(varphis[(int)(k-step)],numConceptsMin_prev));    				
    			}    			
    			numConceptsMinPlus.add(new Tuple(varphi,numConceptsMin));
    			addLastVarPhi=true;
    		}else{
    			addLastVarPhi=false;
    		}
    		
    		k+=step;    		
    		ik=(int)k;
    		if (ik<varphis.length){    		
    			varphi=varphis[ik].doubleValue();
    		}else{
    			break;
    		}
    	}while (!this.shouldStop);	
		
    	if (this.shouldStop) this.resetData();
    	
	}
	public void execute(){
		this.ratioDone=0;						
		DataMatrix datamatrix=preprocessor.getDataMatrix();
		this.setMatrixInfo(datamatrix.getHistogram(this.preprocessor.getPreprocessorType()));				
		this.explorePhi();
		this.exploreVarPhi();
		this.ratioDone=1;
	}
	
	protected void setMatrixInfo(MatrixInfo mi){
		this.mi=mi;
	}
	
	public KFCAResults getResults(){
		logger.info("Obtaining KFCAResult");
		KFCAResults kfcaResult=new KFCAResults();
		kfcaResult.setStartDate(this.getStartDate());
		kfcaResult.setStopDate(this.getStopDate());
		
		
		double[][] numConcepts=new double[this.numConceptsMinPlus.size()][2];
		int i=0;
		Iterator<Tuple> iter=this.numConceptsMinPlus.iterator();		
		while(iter.hasNext()){
			Tuple t=iter.next();			
			numConcepts[i]=t.getArray();
			i++;
		}		
		kfcaResult.setMinPlusNumConcepts(numConcepts);
		
				
		numConcepts=new double[this.numConceptsMaxPlus.size()][2];
		i=0;
		iter=this.numConceptsMaxPlus.iterator();		
		while(iter.hasNext()){
			Tuple t=iter.next();
			numConcepts[i]=t.getArray();
			i++;
		}		
		kfcaResult.setMaxPlusNumConcepts(numConcepts);
		
		kfcaResult.setProcessedMatrix(this.mi.getData());
		kfcaResult.setColNames(this.colNames);
		kfcaResult.setRowNames(this.rowNames);
		kfcaResult.setMicroArrayType(this.preprocessor.getDataMatrix().getMicroArrayType());
		kfcaResult.setPreprocessorId(this.preprocessor.getId());
		kfcaResult.setExplorationType(this.explorationType);
		return kfcaResult;
	}
	
	protected void saveResults(){
		KFCAResults kfcaResult=this.getResults();
		logger.info("Saving KFCAResult");
		kfcaResult.setStopDate(new Date());		
		kfcaResult.persist();
		logger.info("End save KFCAResult");
		this.preprocessor.setKfcaResults(kfcaResult);						
	}
	
	
	
	
		
}
