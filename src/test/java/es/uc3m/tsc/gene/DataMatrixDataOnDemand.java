package es.uc3m.tsc.gene;

import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = DataMatrix.class)
public class DataMatrixDataOnDemand {
	
	public void setRawData(DataMatrix obj, int index) {
        double[][] rawData = null;
        obj.setRawData(rawData);
    }

}
