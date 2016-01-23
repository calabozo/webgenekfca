package es.uc3m.tsc.general;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.general.Constants.ColType;
import es.uc3m.tsc.general.Constants.GeneExpType;

@RunWith(JUnit4.class)
public class ConstantsTest {

	@Test
	public void testEnumColType(){
		int type0=ColType.FULL_EXPLORATION.getValue();
		int type1=ColType.PARTIAL_EXPLORATION.getValue();
		
		Assert.assertEquals("Wrong type0", type0,0);
		Assert.assertEquals("Wrong type1", type1,1);
		
		Assert.assertEquals("Wrong maaked type0", 0, ColType.FULL_EXPLORATION.getMaskedValue());
		Assert.assertEquals("Wrong masked type1", 1, ColType.PARTIAL_EXPLORATION.getMaskedValue());
	
		
		Assert.assertEquals("Wrong type0", 0,GeneExpType.PROBESET.getValue());
		Assert.assertEquals("Wrong type0", 1,GeneExpType.GEXP_AVERAGE.getValue());
		
		Assert.assertEquals("Wrong maaked type None", 0, GeneExpType.PROBESET.getMaskedValue());
		Assert.assertEquals("Wrong masked type Avg", 2, GeneExpType.GEXP_AVERAGE.getMaskedValue());
		Assert.assertEquals("Wrong masked type Var", 10, GeneExpType.GEXP_VARIANCE.getMaskedValue());
		Assert.assertEquals("Wrong masked type Corr", 12, GeneExpType.GEXP_CORRELATION.getMaskedValue());
		Assert.assertEquals("Wrong masked type Entr", 14, GeneExpType.GEXP_ENTROPY.getMaskedValue());
		
		Assert.assertEquals("Wrong enum conversion", GeneExpType.PROBESET,GeneExpType.getEnum(0));
		Assert.assertEquals("Wrong enum conversion", GeneExpType.GEXP_AVERAGE,GeneExpType.getEnum(2));
		Assert.assertEquals("Wrong enum conversion", GeneExpType.GEXP_VARIANCE,GeneExpType.getEnum(10));
		Assert.assertEquals("Wrong enum conversion", GeneExpType.GEXP_CORRELATION,GeneExpType.getEnum(12));
		Assert.assertEquals("Wrong masked type Entr", GeneExpType.GEXP_ENTROPY,GeneExpType.getEnum(14));
	}
}
