package es.uc3m.tsc.gene;

import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = Preprocessor.class)
public class PreprocessorDataOnDemand {
	
    public void setGroupMatrix(Preprocessor obj, int index) {
        double[][] groupMatrix = null;
        obj.setGroupMatrix(groupMatrix);
    }
    
    public void setGroupId(Preprocessor obj, int index) {
        Integer[] groupId = null;
        obj.setGroupId(groupId);
    }
	
}
