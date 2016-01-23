package es.uc3m.tsc.genetools;

import java.io.FileNotFoundException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.gene.ProcessCELFilesTest;
import es.uc3m.tsc.general.JSONConcept;
import es.uc3m.tsc.kfca.explore.KFCAExplore;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import flexjson.JSONDeserializer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	
public class GeneClusterAnalysisTest {

	private KFCAResults getKFCAResultsIntegrationTest() throws FileNotFoundException{
		
		String celIDFiles="1237";
    	String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL","GSM237292.CEL","GSM237293.CEL","GSM237294.CEL","GSM237295.CEL"};
    	//
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setCelIDFiles(celIDFiles);
    	dm.setMicroArrayType(DataTypeEnum.ATH1121501);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessCELFilesTest.createSummary(processCELFiles,celFiles, celIDFiles);    	
    	
    	dm.setColNames(celFiles);
    	dm.loadDataFromFileName();
    	
    	processCELFiles.removeFiles(celIDFiles);
    	
    	Preprocessor p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	p.setPreprocessorType(PreprocessorEnum.LOGPREPROARITMEAN);    	    	    	
    	p.setMaxPhiToExplore(10);	
    	p.execute();
    	
    	KFCAExplore ke=new KFCAExplore(p);    	
    	
    	ke.execute();
    	KFCAResults results=ke.getResults();    	
    	    	
    	Assert.assertTrue("There should be more than one maxplus concept", results.getMaxPlusNumConcepts().length>0);
    	Assert.assertTrue("There should be more than one minplus concept", results.getMaxPlusNumConcepts().length>0);
    	
    	Assert.assertTrue("There should be more than one row name", results.getRowNames().length>0);
    	Assert.assertTrue("There should be more than one col name", results.getColNames().length>0);
    	
    	Assert.assertEquals("Wrong name of first column","GSM237280.CEL", results.getColNames()[0]);
    	
    	Assert.assertEquals("Wrong microarray type", DataTypeEnum.ATH1121501 , results.getMicroArrayType());
    	
    	return results;
	}
	
	@Test
	public void testGOTerms() throws FileNotFoundException{
		KFCAResults result=this.getKFCAResultsIntegrationTest();		
		GeneClusterAnalysis gca=new GeneClusterAnalysis(result);
		result.setGeneClusterAnalysis(gca);
		
		String str=result.toJSONMaxPlusSortConcept(-0.5,3);    	
    	JSONConcept concept=(JSONConcept) new JSONDeserializer<JSONConcept>().use( null, JSONConcept.class ).deserialize(str);
    	
    	System.out.println(concept.getO().size());
    	
    	GOpValue[] go=gca.getGOpValue(concept.getO());
    	Assert.assertEquals("0046423",go[1].GOID);
    	Assert.assertEquals(3,go[1].numTotal);
    	Assert.assertEquals(1,go[1].getNumAppear());

    	for (int i=0;i<go.length-1;i++){
    		Assert.assertTrue(go[i].pValue>=0);
    		Assert.assertTrue(go[i].pValue<=go[i+1].pValue);
    		Assert.assertTrue(go[i].pValue<1.0);
    	}
    	
    	str=result.toJSONMaxMinPlusPValueConcept(-0.5, 3, true);
    	System.out.println(str);
    	Assert.assertTrue( str.startsWith("{\"numMicro\":22810,\"clusterSize\":4,\"go\":[{\"g\":\"0000024\",\"d\""));
    	Assert.assertTrue( str.endsWith(",\"p\":0.3177109397525336,\"na\":1,\"nt\":2079}]}"));    	                                 
		str=result.toJSONMaxPlusSortConcept(-2,0);    	
    	concept=(JSONConcept) new JSONDeserializer<JSONConcept>().use( null, JSONConcept.class ).deserialize(str);
    	System.out.println(concept.getO().size());
    	
    	go=gca.getGOpValue(concept.getO());
    	System.out.println(go.length);
    	System.out.println(go[0]);
    	for (int i=0;i<go.length-1;i++){
    		Assert.assertTrue(go[i].pValue>=0);
    		Assert.assertTrue(go[i].pValue<=go[i+1].pValue);
    		Assert.assertTrue(go[i].pValue<1.0);
    	}    	
    	
    	
	}
}
