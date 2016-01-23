package es.uc3m.tsc.gene;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

@RooIntegrationTest(entity = Preprocessor.class)
public class PreprocessorIntegrationTest {

	@Autowired
	private PreprocessorDataOnDemand dod;
	   
	
    @Test
    public void testMarkerMethod() {
    }
    

    
}
