package es.uc3m.tsc.genetools;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.gene.DataTypeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	
public class GeneInfoTest {

	@Resource
    private GeneInfo geneInfo;
	
	@Test
    public void testMethodArab() {
    	Assert.assertEquals("Wrong probeID", "244912_at",geneInfo.getGeneDescription(DataTypeEnum.ATH1121501,"244912_at").probeSetID);
    	Assert.assertEquals("Wrong AGI","AT2G07783",geneInfo.getGeneDescription(DataTypeEnum.ATH1121501, "244912_at").agiName.get(0));
    }
    
    @Test
    public void testMethodHGU() {
    	Assert.assertEquals("Wrong probeID", "208991_at",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"208991_at").probeSetID);
    	Assert.assertEquals("Wrong geneSymbol","STAT3",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"208991_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","STAT3",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"208992_s_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","FOS",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"209189_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","JUN",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"213281_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","SP1",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"214732_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","SP1",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"224754_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","SP1",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"224760_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","SP1",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"1553685_s_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","CDX2",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"206387_at").geneSymbol);
    	Assert.assertEquals("Wrong geneSymbol","CEBPA",geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"204039_at").geneSymbol);
    }
    
    @Test
    public void testJSONArab() {
    	GeneDescription g=geneInfo.getGeneDescription(DataTypeEnum.ATH1121501,"244926_s_at");    	
    	System.out.println(g.toJson());
    }
    
    @Test
    public void testJSONHGU() {
    	GeneDescription g=geneInfo.getGeneDescription(DataTypeEnum.HGU133PLUS2,"208991_at");    	
    	System.out.println(g.toJson());
    }
    
    @Test
    public void testGetGenes(){
    	List<GeneDescription> gds=geneInfo.getGenesFromForGeneInProbeset(DataTypeEnum.HGU133PLUS2,"208991_at");
    	Assert.assertEquals("Wrong number genes",4,gds.size());
    	System.out.println("probesets: ");
    	for (GeneDescription g:gds){
    		System.out.println("probesetId "+g.probeSetID);	
    		Assert.assertEquals("Wrong geneSymbol","STAT3",g.geneSymbol);
    	}
    	
    	Assert.assertEquals("Wrong probeset","208991_at",gds.get(0).probeSetID);
    	Assert.assertEquals("Wrong probeset","208992_s_at",gds.get(1).probeSetID);
    	Assert.assertEquals("Wrong probeset","225289_at",gds.get(2).probeSetID);
    	Assert.assertEquals("Wrong probeset","243213_at",gds.get(3).probeSetID);
    }
    
    @Test
    public void testGetGenes2(){
    	List<GeneDescription> gds=geneInfo.getGenesFromForGeneInProbeset(DataTypeEnum.HGU133PLUS2,"204631_at");
    	Assert.assertEquals("Wrong number genes",2,gds.size());
    	System.out.println("probesets: ");
    	for (GeneDescription g:gds){
    		System.out.println("probesetId "+g.probeSetID);	
    		Assert.assertEquals("Wrong geneSymbol","MYH2 /// MYH4",g.geneSymbol);
    	}
    	Assert.assertEquals("Wrong probeset","204631_at",gds.get(0).probeSetID);
    	Assert.assertEquals("Wrong probeset","208148_at",gds.get(1).probeSetID);
    }
    
    @Test
    public void testGetGenes3(){
    	List<GeneDescription> gds=geneInfo.getGenesFromForGeneInProbeset(DataTypeEnum.ATH1121501,"244906_at");
    	Assert.assertEquals("Wrong number genes",1,gds.size());

    	Assert.assertEquals("Wrong probeset","244906_at",gds.get(0).probeSetID);
    	Assert.assertEquals("Wrong probeset","ATMG00690",gds.get(0).getStrAgiName());
    	
    	gds=geneInfo.getGenesFromForGeneInProbeset(DataTypeEnum.ATH1121501,"244911_at");
    	Assert.assertEquals("Wrong number genes",1267,gds.size());
    	Assert.assertEquals("Wrong probeset","---",gds.get(0).getStrAgiName());
    }
    @Test
    public void testGetGenes4(){	
    	List<String> probesets=geneInfo.getProbesetsSimilarGeneFromProbesetID(DataTypeEnum.HGU133PLUS2,"204631_at");
    	Assert.assertEquals("Wrong number genes",3,probesets.size());
    	
    	Assert.assertEquals("Wrong probeset","204631_at",probesets.get(0));
    	Assert.assertEquals("Wrong probeset","208148_at",probesets.get(1));
    	Assert.assertEquals("Wrong geneName","MYH2 /// MYH4",probesets.get(2));
    	
    	
    	probesets=geneInfo.getProbesetsSimilarGeneFromProbesetID(DataTypeEnum.ATH1121501,"244906_at");
    	Assert.assertEquals("Wrong number genes",2,probesets.size());

    	Assert.assertEquals("Wrong probeset","244906_at",probesets.get(0));
    	Assert.assertEquals("Wrong geneName","ATMG00690",probesets.get(1));
    	
    	
    	probesets=geneInfo.getProbesetsSimilarGeneFromProbesetID(DataTypeEnum.ATH1121501,"244911_at");
    	Assert.assertEquals("Wrong number genes",1268,probesets.size());
    	Assert.assertEquals("Wrong geneName","---",probesets.get(probesets.size()-1));
    	
    }
    
    @Test
    public void testGetGenes5(){
    	List<String> probesets=geneInfo.getProbesetsSimilarGeneFromProbesetID(DataTypeEnum.TEST,"prob1");
    	Assert.assertEquals("Wrong number genes",5,probesets.size());
    	
    	Assert.assertEquals("Wrong probeset","prob1",probesets.get(0));
    	Assert.assertEquals("Wrong probeset","prob2",probesets.get(1));
    	Assert.assertEquals("Wrong probeset","prob3",probesets.get(2));
    	Assert.assertEquals("Wrong probeset","prob4",probesets.get(3));    	
    	Assert.assertEquals("Wrong geneName","GEN1",probesets.get(probesets.size()-1));
    	
    	Assert.assertEquals("Wrong probeset", "prob2",geneInfo.getGeneDescription(DataTypeEnum.TEST,"prob2").probeSetID);
    	Assert.assertEquals("Wrong description", "Gene description",geneInfo.getGeneDescription(DataTypeEnum.TEST,"prob2").getDescription());
    	Assert.assertEquals("Wrong description", "GEN1",geneInfo.getGeneDescription(DataTypeEnum.TEST,"prob2").getGeneSymbol());
    	
    	
    	probesets=geneInfo.getProbesetsSimilarGeneFromProbesetID(DataTypeEnum.TEST,"prob12");
    	Assert.assertEquals("Wrong number genes",2,probesets.size());
    	
    	Assert.assertEquals("Wrong probeset","prob12",probesets.get(0));
    	Assert.assertEquals("Wrong geneName","GEN5",probesets.get(1));
    	
    	
    	
    }    
    
    @Test
    public void testGetGenes6(){
    	
    	GeneDescription gd=geneInfo.getGenesFromGeneName(DataTypeEnum.TEST,"GEN1");    	
    	Assert.assertEquals("Wrong probests","prob1, prob2, prob3, prob4",gd.probeSetID);
    	System.out.println(gd.toJson());
    }
    
    @Test
    public void testGetGenes7(){
    	
    	int num=geneInfo.getNumProbeSetsForGeneOntology(DataTypeEnum.ATH1121501,"0006412");
    	Assert.assertEquals("Wrong number of GOIDs",457L,num);
    	
    	num=geneInfo.getNumProbeSetsForGeneOntology(DataTypeEnum.ATH1121501,"0046423");
    	Assert.assertEquals("Wrong number of GOIDs",3L,num);
    	
    	num=geneInfo.getNumGenesForGeneOntology(DataTypeEnum.ATH1121501,"0006412");
    	Assert.assertEquals("Wrong number of GOIDs",502L,num);
    	
    	num=geneInfo.getNumGenesForGeneOntology(DataTypeEnum.HGU133PLUS2,"0006417");
    	Assert.assertEquals("Wrong number of GOIDs", 103L,num);

    }
    
    @Test
    public void testGetGenes8(){
    	GeneDescription gd=geneInfo.getGeneDescription(DataTypeEnum.ATH1121501,"244912_at");
    	int num=gd.getGoBP().size()+gd.getGoCC().size()+gd.getGoMF().size();
    	Assert.assertEquals("Wrong number of GOIDs",num,gd.getAllGO().size());
    	
    	gd=geneInfo.getGeneDescription(DataTypeEnum.ATH1121501,"244911_at");    	
    	Assert.assertEquals("Wrong number of GOIDs",0,gd.getAllGO().size());
    	
    	gd=geneInfo.getGeneDescription(DataTypeEnum.ATH1121501,"244906_at");
    	num=gd.getGoBP().size()+gd.getGoCC().size()+gd.getGoMF().size();
    	Assert.assertEquals("Wrong number of GOIDs",num,gd.getAllGO().size());
    	
    }
    
    
    @Test
    public void testGetGenes9(){
    	List<GODescription> goDescription=geneInfo.findGeneOntologyFromProbeSetID(DataTypeEnum.ATH1121501, "245296_at");
    	Assert.assertEquals(11,goDescription.size());
    	Assert.assertEquals("0006810", goDescription.get(0).goID);
    	Assert.assertEquals("0006857", goDescription.get(1).goID);
    	Assert.assertEquals("0006875", goDescription.get(2).goID);
    	Assert.assertEquals("0007275", goDescription.get(3).goID);
    	Assert.assertEquals("0015031", goDescription.get(4).goID);
    	Assert.assertEquals("0015833", goDescription.get(5).goID);
    	Assert.assertEquals("0055085", goDescription.get(6).goID);
    	Assert.assertEquals("0016020", goDescription.get(7).goID);
    	Assert.assertEquals("0016020", goDescription.get(8).goID);
    	Assert.assertEquals("0016021", goDescription.get(9).goID);
    	Assert.assertEquals("0015198", goDescription.get(10).goID);
    }
    
    @Test
    public void testGetGenes10(){
    	GeneDescription genDescription = geneInfo.getGenesFromGeneName(DataTypeEnum.ATH1121501, "At4g16370");
    	Assert.assertEquals("Wrong probests","245296_at",genDescription.probeSetID);
    	List<GODescription> goDescription = genDescription.getAllGO();
		
    	Assert.assertEquals(11,goDescription.size());
    	Assert.assertEquals("0006810", goDescription.get(0).goID);
    	Assert.assertEquals("0006857", goDescription.get(1).goID);
    	Assert.assertEquals("0006875", goDescription.get(2).goID);
    	Assert.assertEquals("0007275", goDescription.get(3).goID);
    	Assert.assertEquals("0015031", goDescription.get(4).goID);
    	Assert.assertEquals("0015833", goDescription.get(5).goID);
    	Assert.assertEquals("0055085", goDescription.get(6).goID);
    	Assert.assertEquals("0016020", goDescription.get(7).goID);
    	Assert.assertEquals("0016020", goDescription.get(8).goID);
    	Assert.assertEquals("0016021", goDescription.get(9).goID);
    	Assert.assertEquals("0015198", goDescription.get(10).goID);
    	
    	
    	genDescription = geneInfo.getGenesFromGeneName(DataTypeEnum.ATH1121501, "AT5G46470");
    	Assert.assertEquals("Wrong probests","248845_at, 248875_at",genDescription.probeSetID);
    	goDescription = genDescription.getAllGO();
		
    	Assert.assertEquals(12,goDescription.size());
    	Assert.assertEquals("0006915", goDescription.get(0).goID);
    	Assert.assertEquals("0045087", goDescription.get(5).goID);
    	Assert.assertEquals("0017111", goDescription.get(11).goID);
    	
    	
    	goDescription = geneInfo.findGeneOntologyFromGeneName(DataTypeEnum.ATH1121501, "AT5G46470");
    	
    	Assert.assertEquals(12,goDescription.size());
    	Assert.assertEquals("0006915", goDescription.get(0).goID);
    	Assert.assertEquals("0045087", goDescription.get(5).goID);
    	Assert.assertEquals("0017111", goDescription.get(11).goID);
    	Assert.assertEquals("Wrong probests","248845_at, 248875_at",genDescription.probeSetID);
    }
    
    @Test
    public void testGetGenes11(){
    	List<GODescription> goDescription=geneInfo.findGeneOntologyFromProbeSetID(DataTypeEnum.ATH1121501, "245296_at",GoDomainEnum.BP);
    	Assert.assertEquals(7,goDescription.size());
    	Assert.assertEquals("0006810", goDescription.get(0).goID);
    	Assert.assertEquals("0006857", goDescription.get(1).goID);
    	Assert.assertEquals("0006875", goDescription.get(2).goID);
    	Assert.assertEquals("0007275", goDescription.get(3).goID);
    	Assert.assertEquals("0015031", goDescription.get(4).goID);
    	Assert.assertEquals("0015833", goDescription.get(5).goID);
    	Assert.assertEquals("0055085", goDescription.get(6).goID);
    	
    	goDescription=geneInfo.findGeneOntologyFromProbeSetID(DataTypeEnum.ATH1121501, "245296_at",GoDomainEnum.CC);
    	Assert.assertEquals(3,goDescription.size());
    	Assert.assertEquals("0016020", goDescription.get(0).goID);
    	Assert.assertEquals("0016020", goDescription.get(1).goID);
    	Assert.assertEquals("0016021", goDescription.get(2).goID);
    	
    	goDescription=geneInfo.findGeneOntologyFromProbeSetID(DataTypeEnum.ATH1121501, "245296_at",GoDomainEnum.MF);
    	Assert.assertEquals(1,goDescription.size());
    	Assert.assertEquals("0015198", goDescription.get(0).goID);
    }
    
    
    @Test
    public void testGetGenes12(){
    	List<GODescription> goDescription = geneInfo.findGeneOntologyFromGeneName(DataTypeEnum.ATH1121501, "AT5G46470",GoDomainEnum.BP);
    	Assert.assertEquals(6,goDescription.size());
    	Assert.assertEquals("0006915", goDescription.get(0).goID);
    	Assert.assertEquals("0045087", goDescription.get(5).goID);
    	
    	goDescription = geneInfo.findGeneOntologyFromGeneName(DataTypeEnum.ATH1121501, "AT5G46470",GoDomainEnum.CC);    	
    	Assert.assertEquals(1,goDescription.size());
    	Assert.assertEquals("0031224", goDescription.get(0).goID);
    	
    	goDescription = geneInfo.findGeneOntologyFromGeneName(DataTypeEnum.ATH1121501, "AT5G46470",GoDomainEnum.MF);    	
    	Assert.assertEquals(5,goDescription.size());
    	Assert.assertEquals("0000166", goDescription.get(0).goID);
    	Assert.assertEquals("0004888", goDescription.get(1).goID);
    	Assert.assertEquals("0017111", goDescription.get(4).goID);
    	
    }
}
