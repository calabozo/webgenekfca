package es.uc3m.tsc.threads;

import java.util.List;

import es.uc3m.tsc.genetools.GOpValue;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import flexjson.JSONSerializer;


public class ThreadCalcPValue extends ThreadGeneric {	
	List<String> genes;
	KFCAResults kfcaResults;
	GOpValue[] goTerms;
	
	public static ThreadCalcPValue getRunningThreadCalcPValue(Long id){
		return (ThreadCalcPValue)ThreadExecutor.getInstance().getThread(ThreadExecutor.ExecutorType.CLUSTERPVALUE,id);
	}
	
	public ThreadCalcPValue(KFCAResults kfcaResults, List<String> genes) {				
		super(ThreadExecutor.ExecutorType.CLUSTERPVALUE, -1L);
		this.kfcaResults=kfcaResults;
		this.genes=genes;
		this.autoRemove=false;
	}

	@Override
	protected void execute() {		
		goTerms=this.kfcaResults.getGeneClusterAnalysis().getGOpValue(genes, this);		
	}
	
	@Override
	protected void saveResults() {
		// TODO Auto-generated method stub		
	}
	
	public String getResult(){

		StringBuffer json=new StringBuffer();
		json.append("{\"numMicro\":"+this.kfcaResults.getRowNames().length+",\"clusterSize\":"+this.genes.size()+",\"go\":[");
		
		for (int k=0;k<goTerms.length;k++){
			GOpValue go=goTerms[k];
			if (k==0)
				json.append("{\"g\":\""+go.getGOID());
			else
				json.append(",{\"g\":\""+go.getGOID());
			
			json.append("\",\"d\":\""+go.getDescription());
			json.append("\",\"p\":"+go.getPValue());
			json.append(",\"na\":"+go.getNumAppear());
			json.append(",\"nt\":"+go.getNumTotal());	
			json.append(",\"o\":\""+go.getOntology()+"\"");	
			json.append(",\"elems\":"+new JSONSerializer().exclude("class").serialize(go.getGenes()));
			json.append("}");
		}
		json.append("]}");
		
		ThreadExecutor.getInstance().removeThread(this.type,this.id);
		return json.toString();
	}
	
	public boolean getShouldStop(){
		return this.shouldStop;
	}
	public void setRatioDone(double ratio){
		this.ratioDone=ratio;
	}

	@Override
	protected void exceptionCaptured(Exception e) {
		// TODO Auto-generated method stub

	}

}
