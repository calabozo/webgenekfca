package es.uc3m.tsc.kfca.explore;

import java.io.FileNotFoundException;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.gene.ProcessCELFilesTest;
import es.uc3m.tsc.general.JSONConcept;
import es.uc3m.tsc.genetools.GOpValue;
import es.uc3m.tsc.genetools.GeneInfo;
import flexjson.JSONDeserializer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	
@RooIntegrationTest(entity = KFCAResults.class)
public class KFCAResultsIntegrationTest {
	@Resource
    private GeneInfo geneInfo;
	
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
    public void testMaxPlusConcepts() throws FileNotFoundException {
    	KFCAResults results=this.getKFCAResultsIntegrationTest();
    	//results.setGeneInfo(geneInfo);
    	
    	boolean[][] IMaxPlus=results.getMatrixMaxPlus(-8);
    	
    	String str=results.toJSONMaxPlusIntents(-8);
    	System.out.println(str);
    	System.out.println(str.length());
    	
    	String str2=results.toJSONMaxPlusSortConcept(-0.0805,0);
    	System.out.println(str2);
    	//System.out.println(str2.length());
    	Assert.assertTrue("wrong start",str2.startsWith("{\"m\":[],\"o\":[\"265023_at\",\"253546_at\",\"261953_at\""));
    	Assert.assertTrue("wrong end",str2.endsWith("\"264642_at\",\"261891_at\"]}"));
    	
    	str2=results.toJSONMaxPlusConcept(-0.4,3);
    	System.out.println(str2);
    	JSONConcept concept1=(JSONConcept) new JSONDeserializer().use( null, JSONConcept.class ).deserialize(str2);
    	
    	str2=results.toJSONMaxPlusSortConcept(-0.4,3);
    	System.out.println(str2);
    	JSONConcept concept2=(JSONConcept) new JSONDeserializer().use( null, JSONConcept.class ).deserialize(str2);
    	    	
    	for (String o1:concept1.o){
    		if (!concept2.o.contains(o1)){
    			Assert.assertTrue("o1: "+o1+" Not found in toJSONMaxPlusSortConcept",false);
    		}
    	}
    	
    	for (String o2:concept2.o){
    		if (!concept1.o.contains(o2)){
    			Assert.assertTrue("m2: "+o2+" Not found in toJSONMaxPlusConcept",false);
    		}
       	}
    	/*
    	long t=System.currentTimeMillis();
    	str=results.toJSONMaxMinPlusPValueConcept(-0.045,2,true);    	
    	System.out.println(str);
    	System.out.println(System.currentTimeMillis()-t);
		*/
    	    	
    	
    	//str=results.toJSONMaxMinPlusPValueConcept(-2,0,true);    	
    	
    	//TODO: check URPVALUE http://localhost:8080/GeneAnalyzer/kfcaresultses/gopvalues_maxplus/2//-0.2096972138889388/192
    }
    /*
    @Test
    public void testSortMaxPlusConcepts() throws FileNotFoundException {
    	KFCAResults results=this.getKFCAResultsIntegrationTest();
    	results.testFillObjectExplored();    	    	    	
    	
    	long t1 = System.currentTimeMillis();
    	String str2=results.toJSONMaxPlusSortConcept(-8,0);
    	long t2 = System.currentTimeMillis();
    	
    	System.out.println(str2.length());
    	
    	long t3 = System.currentTimeMillis();
    	String str3=results.toJSONMaxPlusSortConcept_hash(-8,0);
    	long t4 = System.currentTimeMillis();
    	
    	System.out.println(str3.length());
    	
    	System.out.println("1:"+(t2-t1)+" 2:"+(t4-t3));    	
    	Assert.assertEquals("Sistema de ordenación distintos!",str2,str3);
    	
    }  
    
    @Test
    public void testSortMaxPlusRangeConcepts() throws FileNotFoundException {
    	KFCAResults results=this.getKFCAResultsIntegrationTest();
    	results.testFillObjectExplored();    	    	    	
    	String s=results.toJSONMaxMinPlusRangeInfo("244901_at",true);
    	System.out.println(s);
    	
    }
    */
}
