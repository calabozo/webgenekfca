package es.uc3m.tsc.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.uc3m.tsc.genetools.GeneClusterAnalysis;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.kfca.explore.KFCAResults.HomogSeparation;
import es.uc3m.tsc.math.ClusterAnalysis.SimilarityEnum;
import flexjson.JSONSerializer;


public class ThreadCalcHomSep extends ThreadGeneric {
	boolean max_min_plus;
	int conceptId;
	KFCAResults result;
	int numMeasures;
	HomogSeparation[] hs;
	double from_phi;
	double to_phi;
	
	public static ThreadCalcHomSep getRunningThreadCalcHomSep(Long id){
		return (ThreadCalcHomSep)ThreadExecutor.getInstance().getThread(ThreadExecutor.ExecutorType.CLUSTERHS,id);
	}
	
	public ThreadCalcHomSep(KFCAResults result, int conceptId, boolean max_min_plus, double from_phi, double to_phi, int numMeasures) {				
		super(ThreadExecutor.ExecutorType.CLUSTERHS, -1L);
		this.max_min_plus=max_min_plus;
		this.conceptId=conceptId;
		this.result=result;
		this.numMeasures=numMeasures;
		this.autoRemove=false;
		this.from_phi=from_phi;
		this.to_phi=to_phi;
	}

	@Override
	protected void execute() {
		
		double step=(to_phi-from_phi)/(numMeasures-1);
		
		boolean[][] I;
		boolean[] row;
		hs=new HomogSeparation[numMeasures];
		int i,j,k;
		HashMap<Long,List<Integer>> rowsByConcept=new HashMap<Long,List<Integer>>();
		int nr=result.getProcessedMatrix().length;
		int nc=result.getProcessedMatrix()[0].length;
		GeneClusterAnalysis clusterAnalysis=result.getGeneClusterAnalysis();
		k=0;
		for (double p=from_phi;p<to_phi;p+=step){	
			if (this.shouldStop) return;
			
			I=(max_min_plus)?result.getMatrixMaxPlus(p):result.getMatrixMinPlus(p);
			
			for (i=0;i<nr;i++){
				row=I[i];
				int id=0;
				for (j=0;j<nc;j++){
					if (row[j])	id+=1<<j;		
				}
				Long concept=Long.valueOf(id);
				List<Integer> r=rowsByConcept.get(concept);
				if (r==null){
					r=new ArrayList<Integer>();
					rowsByConcept.put(new Long(id), r);					
				}
				r.add(Integer.valueOf(i));			
			}
			Long conceptId1=Long.valueOf(conceptId);
			List<Integer> rows1=rowsByConcept.remove(conceptId1);
			if (rows1!=null){
				Integer[] aRows1=new Integer[rows1.size()];
				aRows1=rows1.toArray(aRows1);
				double h=clusterAnalysis.homogeneity2(conceptId1, aRows1, SimilarityEnum.EUCLIDEAN);		
				if (Double.isNaN(h)) h=-1;
				double s=clusterAnalysis.separation2(conceptId1, aRows1, rowsByConcept, SimilarityEnum.EUCLIDEAN);
				hs[k++]=new HomogSeparation(h,s,p);				
			}else{
				hs[k++]=new HomogSeparation(-1,-1,p);
			}
			this.ratioDone=(double)k/(double)this.numMeasures;
		}
		
	

	}

	@Override
	protected void saveResults() {
		// TODO Auto-generated method stub		
	}
	
	public String getResult(){
		String ret=new JSONSerializer().exclude("class").deepSerialize(hs);
		hs=null;
		ThreadExecutor.getInstance().removeThread(this.type,this.id);
		return ret;
	}
	

	@Override
	protected void exceptionCaptured(Exception e) {
		// TODO Auto-generated method stub

	}

}
