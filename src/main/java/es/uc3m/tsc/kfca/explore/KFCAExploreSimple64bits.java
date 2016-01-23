package es.uc3m.tsc.kfca.explore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.kfca.tools.KFCAConcepts;
import es.uc3m.tsc.kfca.tools.KFCAConceptsFastApproximate;
import es.uc3m.tsc.kfca.tools.KFCAConceptsLite;
import es.uc3m.tsc.math.MatrixInfo;
import es.uc3m.tsc.general.Constants;
import es.uc3m.tsc.general.Constants.ColType;

public class KFCAExploreSimple64bits extends KFCAExplore{
 	
	int minNumConceptsToSave;
	
	public KFCAExploreSimple64bits(Preprocessor p) {
		super(p);		
	
	}
	
	public void execute(){		
		this.ratioDone=0;						
		
		this.setMatrixInfo(new MatrixInfo(this.preprocessor.getGroupMatrix(),10));
		
		double[][] R=mi.getData();		
		int nc=R[0].length;
		
		if (nc<=Constants.maximumColsExploration){
			this.explorationType=this.explorationType | ColType.FULL_EXPLORATION.getMaskedValue();
		}else{
			this.explorationType=this.explorationType | ColType.PARTIAL_EXPLORATION.getMaskedValue();
		}
		
		this.explorePhi();
		this.exploreVarPhi();
		this.ratioDone=1;				
		
		
	}

	protected void explorePhi(){
		double maxValue=mi.getMax();
    	double minValue=mi.getMin();    	
    	double[][] R=mi.getData();
    	Double[] phis=mi.getPhis();
    	double dValue=maxValue-minValue;
    	
    	double step=phis.length/this.preprocessor.getMaxPhiToExplore();
    	if (step<=0) step=1;
		
    	numConceptsMaxPlus=new ArrayList<Tuple>();
    	
		int nr=R.length;
		int nc=R[0].length;
		
		long[] IMaxPlus=new long[nr]; Arrays.fill(IMaxPlus, 0);		
		long[] IMaxPlus_prev=null;
			
		double rowR[];
		long rowIMax;
		long rowIMax_prev;
		int lastNumConcepts=0;
		
		HashSet<Long> clarifiedMaxPlus=new HashSet<Long>();						
		
		double k=0;
		int ik=(int)k;
		double phi=phis[ik].doubleValue();
		KFCAConcepts kfcaConcepts=null;
		if (this.explorationType==0){
			kfcaConcepts=new KFCAConceptsLite();
		}else{
			kfcaConcepts=new KFCAConceptsFastApproximate();
		}
		
		double maxPhis=0;
		for (ik=0;(ik<phis.length && phis[ik].doubleValue()<0);ik++);
		maxPhis=(double)2*ik;
		ik=0;
		
		boolean explorationEnd=false;
		boolean matrixHasChanged=false;
		do{			 
    		this.ratioDone=k/maxPhis;
    		
    		IMaxPlus_prev=IMaxPlus;    	    		    		
    		IMaxPlus=new long[nr];
    		    			    		    		  		
    		clarifiedMaxPlus=new HashSet<Long>();
    		matrixHasChanged=false;
    		
    		for (int i=0;i<nr;i++){
    			rowR=R[i];    			
    			rowIMax_prev=IMaxPlus_prev[i];
    			
    			rowIMax=0;
    			for (int j=0;j<nc;j++){
    				if (rowR[j]<=phi) rowIMax+=1<<j;    				
    			}
    			IMaxPlus[i]=rowIMax;
    			
    			clarifiedMaxPlus.add(rowIMax);
    			if (rowIMax!=rowIMax_prev || ik==0){    
    				matrixHasChanged=true;    				
    			}
    		}
    		
    		//Compare if current and previous clarification are the same
    		if (matrixHasChanged){ 		
    			long[] lClarifiedIMaxPlus = new long[clarifiedMaxPlus.size()];
    			int i=0;
    			for (Long l:clarifiedMaxPlus) lClarifiedIMaxPlus[i++]=l;    			

    			int numConceptsMax=kfcaConcepts.getNumberConcepts(lClarifiedIMaxPlus,nc);    			
    			//logger.info("Processsing: "+ this.ratioDone+" Phi: "+phi+" maxplusConcepts:"+numConceptsMax);    			
    			if (numConceptsMax!=lastNumConcepts){
    				lastNumConcepts=numConceptsMax;
    				numConceptsMaxPlus.add(new Tuple(phi,numConceptsMax));
    			}    			
    		}
    		
    		k+=step;
    		ik=(int)k;
    		if (ik<phis.length){    		
    			phi=phis[ik].doubleValue();
    			if (phi>0){
    				explorationEnd=true;
    				phi=0;    				
    			}
    		}else{
				explorationEnd=true;    			    			    			
    		}
    		
    		if (explorationEnd){ 
    			if (!matrixHasChanged){	    			    	
    				long[] lClarifiedIMaxPlus = new long[clarifiedMaxPlus.size()];
    				int i=0; for (Long l:clarifiedMaxPlus) lClarifiedIMaxPlus[i++]=l;    			    			    		    			
    				numConceptsMaxPlus.add(new Tuple(phi,kfcaConcepts.getNumberConcepts(lClarifiedIMaxPlus,nc)));
    			}
	    		break;
    		}

    	}while (!this.shouldStop);
		
    	if (this.shouldStop) this.resetData();

	}
	
	protected void exploreVarPhi(){
		if (mi==null) return;
		
		double maxValue=mi.getMax();
    	double minValue=mi.getMin();    	
    	double[][] R=mi.getData();    	
    	Double[] varphis=mi.getPhis();
    	double dValue=maxValue-minValue;
    	
    	double step=varphis.length/this.preprocessor.getMaxPhiToExplore();
    	if (step<=0) step=1;
    	
    	
		
    	numConceptsMinPlus=new ArrayList<Tuple>();
    	
		
				
		int nr=R.length;
		int nc=R[0].length;
				
		KFCAConcepts kfcaConcepts=null;
		if (this.explorationType==0){
			kfcaConcepts=new KFCAConceptsLite();
		}else{
			kfcaConcepts=new KFCAConceptsFastApproximate();
		}
		
		long[] IMinPlus=new long[nr]; Arrays.fill(IMinPlus, 0);		
		long[] IMinPlus_prev=null;
			
		double rowR[];
		long rowIMin;
		long rowIMin_prev;
		int lastNumConcepts=0;
		
		HashSet<Long> clarifiedMinPlus=new HashSet<Long>();		
		
		double k=0;
		int lastk=varphis.length-1;
		k=lastk;
		int ik=(int)k;
		double varphi=varphis[ik];
		
		double maxPhis=0;
		for (ik=varphis.length-1;(ik>=0 && varphis[ik].doubleValue()>0);ik--);
		maxPhis=(double)2*ik;
		ik=0;
		
		boolean explorationEnd=false;
		boolean matrixHasChanged=false;
		do{		
			//varphi=0;    		 
    		this.ratioDone=(1-k/maxPhis)+0.5;
    		
    		IMinPlus_prev=IMinPlus;    	    		    		
    		IMinPlus=new long[nr];
    		    			    		    		  		
    		clarifiedMinPlus=new HashSet<Long>();
    		matrixHasChanged=false;
    		
    		for (int i=0;i<nr;i++){
    			rowR=R[i];    			
    			rowIMin_prev=IMinPlus_prev[i];
    			
    			rowIMin=0;
    			for (int j=0;j<nc;j++){
    				if (rowR[j]>=varphi)
    					rowIMin+=1<<j;    				
    			}
    			IMinPlus[i]=rowIMin;
    			
    			clarifiedMinPlus.add(rowIMin);
    			if (rowIMin!=rowIMin_prev || ik==0){    
    				matrixHasChanged=true;    				
    			}
    		}
    		
    		//Compare if current and previous clarification are the same
    		if (matrixHasChanged){ 		
    			long[] lClarifiedIMinPlus = new long[clarifiedMinPlus.size()];
    			int i=0;
    			for (Long l:clarifiedMinPlus) lClarifiedIMinPlus[i++]=l;    			
    			
    			if (varphi==0){
    				System.out.println(Arrays.toString(lClarifiedIMinPlus));
    			}
    			
    			int numConceptsMin=kfcaConcepts.getNumberConcepts(lClarifiedIMinPlus,nc);    			
    			//logger.info("Processsing: "+ this.ratioDone+" VarPhi: "+varphi+" minplusConcepts:"+numConceptsMin);
    			
    			if (lastNumConcepts!=numConceptsMin){
    				lastNumConcepts=numConceptsMin;		
    				numConceptsMinPlus.add(new Tuple(varphi,numConceptsMin));
    			}   			
    		}
    		
    		k-=step;    		
    		ik=(int)k;
    		
    		if (ik>=0){    		
    			varphi=varphis[ik].doubleValue();
    			if (varphi<0){
    				varphi=0;
    				explorationEnd=true;
    			}
    		}else{
    			explorationEnd=true;
    		}
    		
    		if (explorationEnd){
	    		
    			if(!matrixHasChanged){
    				long[] lClarifiedIMinPlus = new long[clarifiedMinPlus.size()];
    				int i=0; for (Long l:clarifiedMinPlus) lClarifiedIMinPlus[i++]=l;    			    			    		    			
    				numConceptsMinPlus.add(new Tuple(varphi,kfcaConcepts.getNumberConcepts(lClarifiedIMinPlus,nc)));
    			}
	    		
	    		break;
    		}

    	}while (!this.shouldStop);
		
    	if (this.shouldStop) this.resetData();
    	
	}
}
