package es.uc3m.tsc.kfca.explore;

import org.springframework.roo.addon.dod.RooDataOnDemand;

import es.uc3m.tsc.kfca.explore.KFCAObjectExplored;
import es.uc3m.tsc.kfca.explore.KFCAResults;

@RooDataOnDemand(entity = KFCAResults.class)
public class KFCAResultsDataOnDemand {
	
    public void setMaxPlusNumConcepts(KFCAResults obj, int index) {
        double[][] maxPlusNumConcepts = null;
        obj.setMaxPlusNumConcepts(maxPlusNumConcepts);
    }
    
    public void setMinPlusNumConcepts(KFCAResults obj, int index) {
        double[][] minPlusNumConcepts = null;
        obj.setMinPlusNumConcepts(minPlusNumConcepts);
    }
    
    public void setProcessedMatrix(KFCAResults obj, int index) {
        double[][] processedMatrix = null;
        obj.setProcessedMatrix(processedMatrix);
    }
    
}
