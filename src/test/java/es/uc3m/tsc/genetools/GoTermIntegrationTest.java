package es.uc3m.tsc.genetools;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;

import es.uc3m.tsc.gene.DataTypeEnum;

@RooIntegrationTest(entity = GoTerm.class)
public class GoTermIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }
    
    @Test
    public void testDB() {
    	GoTerm go=GoTerm.getGoID(DataTypeEnum.ATH1121501,"0019008");
    	Assert.assertEquals("0019008",go.getGoid());
    	Assert.assertEquals(new Integer(1),go.getNumGenes());
    	Assert.assertEquals(new Integer(2),go.getNumProbesets());
    	Assert.assertEquals("265696_at",go.getProbesets()[0]);
    	Assert.assertEquals("263472_at",go.getProbesets()[1]);
    	Assert.assertEquals("AT2G31955",go.getGenes()[0]);
    	Assert.assertEquals("CC",go.getOntology());
    	
    	
    	go=GoTerm.getGoID(DataTypeEnum.ATH1121501,"0042132");
    	Assert.assertEquals("0042132",go.getGoid());
    	Assert.assertEquals(new Integer(4),go.getNumGenes());
    	Assert.assertEquals(new Integer(4),go.getNumProbesets());
    	Assert.assertEquals("251762_at",go.getProbesets()[0]);
    	Assert.assertEquals("260837_at",go.getProbesets()[3]);
    	Assert.assertEquals("AT3G55800",go.getGenes()[0]);
    	Assert.assertEquals("AT5G64380",go.getGenes()[3]);
    	Assert.assertEquals("MF",go.getOntology());
    	
    	go=GoTerm.getGoID(DataTypeEnum.HGU133A,"0042132");
    	Assert.assertEquals("0042132",go.getGoid());
    	Assert.assertEquals(new Integer(2),go.getNumGenes());
    	Assert.assertEquals(new Integer(2),go.getNumProbesets());
    	Assert.assertEquals("209696_at",go.getProbesets()[0]);
    	Assert.assertEquals("206844_at",go.getProbesets()[1]);
    	Assert.assertEquals("FBP1",go.getGenes()[0]);
    	Assert.assertEquals("FBP2",go.getGenes()[1]);
    	Assert.assertEquals("MF",go.getOntology());
    	
    	Assert.assertEquals(2,GoTerm.getNumProbesetsForGOid(DataTypeEnum.HGU133A,"0042132"));
    	Assert.assertEquals(2,GoTerm.getNumGenesForGOid(DataTypeEnum.HGU133A,"0042132"));
    	
    	Assert.assertEquals(15,GoTerm.getNumProbesetsForGOid(DataTypeEnum.PRIMEVIEW,"0042133"));
    	Assert.assertEquals(4,GoTerm.getNumGenesForGOid(DataTypeEnum.PRIMEVIEW,"0042133"));
    	
    }
}
