package es.uc3m.tsc.kfca.explore;

import org.springframework.roo.addon.dod.RooDataOnDemand;

import es.uc3m.tsc.kfca.explore.KFCAObjectExplored;

@RooDataOnDemand(entity = KFCAObjectExplored.class)
public class KFCAGeneExploredDataOnDemand {
    public void setMaxplusConceptId(KFCAObjectExplored obj, int index) {
        long[] maxplusConceptId = null;
        obj.setMaxplusConceptId(maxplusConceptId);
    }
    
    public void setMaxplusPhi(KFCAObjectExplored obj, int index) {
        double[] maxplusPhi = null;
        obj.setMaxplusPhi(maxplusPhi);
    }
    
    public void setMinplusConceptId(KFCAObjectExplored obj, int index) {
        long[] minplusConceptId = null;
        obj.setMinplusConceptId(minplusConceptId);
    }
    
    public void setMinplusVarphi(KFCAObjectExplored obj, int index) {
        double[] minplusVarphi = null;
        obj.setMinplusVarphi(minplusVarphi);
    }
}
