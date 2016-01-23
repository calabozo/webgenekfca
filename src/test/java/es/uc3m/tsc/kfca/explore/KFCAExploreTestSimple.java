package es.uc3m.tsc.kfca.explore;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.gene.ProcessCELFilesTest;
import es.uc3m.tsc.general.Constants;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.kfca.explore.KFCAExplore;
import es.uc3m.tsc.kfca.explore.KFCAObjectExplored;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.math.MatrixInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	

public class KFCAExploreTestSimple {

	DataMatrix dm;
	Preprocessor p;
	@Resource 
    GeneInfo geneInfo;
	
    @Test
    public void testExplore() {
    	
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.3,0.4,0.1},
    		    {0.1,0.15,3,2},
    		    {0.5,0.4,9,30},
    		    {5,9,0.1,0.3},
    		    {10,1,5,3},
    		    {5.1,5,7,5},
    		    {5,8,0.5,0.7},
    		    {5,50,1,1.1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"1","2","3","4","5","6","7","8","9","10"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.PRIMEVIEW);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	
    	Assert.assertEquals("Wrong number of differnet elements", 19 ,mi.getNumElements());
    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	//KFCAExplore ke=new KFCAExploreWithObjectInfo64bits(p);
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	System.out.println(Arrays.deepToString(results.getProcessedMatrix()));
    	
    	double[][] maxPlusConcepts=results.getMaxPlusNumConcepts();
    	double[][] minPlusConcepts=results.getMinPlusNumConcepts();
    	
    	
    	System.out.println(Arrays.deepToString(maxPlusConcepts));
    	System.out.println(Arrays.deepToString(minPlusConcepts));
    	
    	double[][] emax={{-2.3025850929940455, 5.0}, {-1.8971199848858813, 6.0}, {-0.916290731874155, 5.0}, {-0.35667494393873245, 4.0}, {0.0, 6.0}};
    	double[][] emin={{3.912023005428146, 3.0},{3.4011973816621555, 4.0},{2.302585092994046, 5.0},{1.9459101490553132, 6.0},{1.62924053973028, 7.0},{1.0986122886681098, 8.0},{0.6931471805599453, 6.0},{0.09531017980432493, 9.0},{0.0, 4.0}};    	

    	Assert.assertTrue("Wrong number of concepts and phi", Arrays.deepEquals(emax, maxPlusConcepts));
    	Assert.assertTrue("Wrong number of concepts and varphi", Arrays.deepEquals(emin, minPlusConcepts));
    	

    	KFCAObjectInfo o=new KFCAObjectInfo(results.getProcessedMatrix());
    	    	
    	
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(2,-10));
    	Assert.assertEquals("Wrong concept ID", 9, o.getMaxConceptId(2,-2.3025850929940455));
    	Assert.assertEquals("Wrong concept ID", 9, o.getMaxConceptId(2,-2));
    	Assert.assertEquals("Wrong concept ID", 11, o.getMaxConceptId(2,-1.2039728043259361));
    	Assert.assertEquals("Wrong concept ID", 15, o.getMaxConceptId(2,-0.5));
    	 	
    	System.out.println("Start phi: "+o.getMaxMarginPhi(2,9)[0]);    	    	
    	System.out.println("Stop phi: "+o.getMaxMarginPhi(2,9)[1]);
    	
    	System.out.println("Start phi: "+o.getMaxMarginPhi(2,11)[0]);
    	System.out.println("Stop phi: "+o.getMaxMarginPhi(2,11)[1]);
    	
    	Assert.assertEquals("Wrong Start phi", -2.3025850929940455, o.getMaxMarginPhi(2,9)[0]);
    	Assert.assertEquals("Wrong Start phi", -1.2039728043259361, o.getMaxMarginPhi(2,11)[0]);
    	Assert.assertEquals("Wrong Start phi", -0.916290731874155, o.getMaxMarginPhi(2,15)[0]);
    	Assert.assertEquals("Wrong Start phi", null, o.getMaxMarginPhi(2,7));
    	
    	
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(4,-10));
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(4,-2.3025850929940455));
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(4,-1));
    	Assert.assertEquals("Wrong concept ID", 2, o.getMaxConceptId(4,-0.916290731874155));
    	Assert.assertEquals("Wrong concept ID", 2, o.getMaxConceptId(4,-0.7));    	
    	
    	System.out.println(Arrays.toString(o.getMaxplusPhi(4)));
    	System.out.println(Arrays.toString(o.getMaxplusConceptId(4)));
    	System.out.println(Arrays.toString(o.getMinplusVarphi(4)));
    	System.out.println(Arrays.toString(o.getMinplusConceptId(4)));
    	
		double[] maxplusPhi={-2.3025850929940455, -0.916290731874155, -0.6931471805599453, 0.0};
    	long[] conceptsMax={0, 2, 3};
    	Assert.assertTrue("Wrong", Arrays.equals(maxplusPhi, o.getMaxplusPhi(4)));
    	Assert.assertTrue("Wrong", Arrays.equals(conceptsMax, o.getMaxplusConceptId(4)));
    	double[] minplusPhi={3.912023005428146, 3.4011973816621555, 2.1972245773362196, 0.0};
    	long [] concepts={0, 8, 12};
    	Assert.assertTrue("Wrong", Arrays.equals(minplusPhi, o.getMinplusVarphi(4)));
    	Assert.assertTrue("Wrong", Arrays.equals(concepts, o.getMinplusConceptId(4)));
    	
    	Assert.assertEquals("Wrong minplus concept ID", 0, o.getMinConceptId(7,4));
    	Assert.assertEquals("Wrong minplus concept ID", 0, o.getMinConceptId(7,3));
    	Assert.assertEquals("Wrong minplus concept ID", 4, o.getMinConceptId(7,1.9459101490553132));
    	Assert.assertEquals("Wrong minplus concept ID", 5, o.getMinConceptId(7,1.611));
    	Assert.assertEquals("Wrong minplus concept ID", 15, o.getMinConceptId(7,1.5));
    	
    	double[] maxplusPhi2={-2.3025850929940455, 0.0};
    	long[] conceptsMax2={0};
    	Assert.assertTrue("Wrong", Arrays.equals(maxplusPhi2, o.getMaxplusPhi(7)));
    	Assert.assertTrue("Wrong", Arrays.equals(conceptsMax2, o.getMaxplusConceptId(7)));
    	
    	
    	System.out.println(Arrays.toString(o.getMinplusVarphi(7)));
    	System.out.println(Arrays.toString(o.getMinplusConceptId(7)));
    	
    	double[] minplusPhi2={3.912023005428146, 1.9459101490553132, 1.62924053973028, 1.6094379124341003, 0.0};
    	long [] concepts2={0, 4, 5, 15};
    	Assert.assertTrue("Wrong", Arrays.equals(minplusPhi2, o.getMinplusVarphi(7)));
    	Assert.assertTrue("Wrong", Arrays.equals(concepts2, o.getMinplusConceptId(7)));
    	
    	Assert.assertEquals("Wrong minplus Start phi", 1.9459101490553132 , o.getMinMarginVarphi(7,0)[0]);    	
    	Assert.assertEquals("Wrong minplus Stop  phi", 3.912023005428146, o.getMinMarginVarphi(7,0)[1]);
    	Assert.assertEquals("Wrong minplus Start phi", 1.62924053973028 , o.getMinMarginVarphi(7,4)[0]);
    	Assert.assertEquals("Wrong minplus Stop phi" , 1.9459101490553132 , o.getMinMarginVarphi(7,4)[1]);    	
    	Assert.assertEquals("Wrong minplus Start phi", 1.6094379124341003, o.getMinMarginVarphi(7,5)[0]);    	
    	Assert.assertEquals("Wrong minplus Stop phi", 1.62924053973028, o.getMinMarginVarphi(7,5)[1]);
    	Assert.assertEquals("Wrong minplus Start phi", 0.0, o.getMinMarginVarphi(7,15)[0]);    	
    	Assert.assertEquals("Wrong minplus Stop phi", 1.6094379124341003, o.getMinMarginVarphi(7,15)[1]);
    	    	
    	Assert.assertEquals("Wrong minplus Start phi", null, o.getMinMarginVarphi(7,3));

    	
    	
    	
    	
    	
    	System.out.println(Arrays.toString(o.getMaxplusPhi(2)));
    	System.out.println(Arrays.toString(o.getMaxplusConceptId(2)));
    	
    	double[] maxplusPhi3={-2.3025850929940455,-1.2039728043259361, -0.916290731874155,0};
    	long[] conceptsMax3={0,9,11,15};
    	Assert.assertTrue("Wrong", Arrays.equals(maxplusPhi3, o.getMaxplusPhi(2)));
    	Assert.assertTrue("Wrong", Arrays.equals(conceptsMax3, o.getMaxplusConceptId(2)));
    	
    	
    	System.out.println(Arrays.toString(o.getMinplusVarphi(2)));
    	System.out.println(Arrays.toString(o.getMinplusConceptId(2)));
    	
    	double[] minplusPhi3={3.912023005428146, 0.0};
    	long [] concepts3={0};
    	Assert.assertTrue("Wrong", Arrays.equals(minplusPhi3, o.getMinplusVarphi(2)));
    	Assert.assertTrue("Wrong", Arrays.equals(concepts3, o.getMinplusConceptId(2)));
    	
    	
    }
    
    @Test
    public void testGeneExpression() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.3,0.4,0.1},
    		    {0.1,0.15,3,2},
    		    {0.2,0.4,-5,30},
    		    {0.2,9,-5,0.3},
    		    {0.2,1,-5,3},
    		    {0.2,5,-5,5},
    		    {0.2,8,-5,0.7},
    		    {0.3,50,-6,1.1},
    		    {0.4,0.1,1,-1},
    		    {0.5,0.2,1,-1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_AVERAGE);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	
    	Assert.assertEquals("Wrong number of differnet elements", 19 ,mi.getNumElements());
    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	//KFCAExplore ke=new KFCAExploreWithObjectInfo64bits(p);
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	System.out.println(Arrays.deepToString(results.getProcessedMatrix()));
    	//System.out.println(Arrays.deepToString(results.getRowNames()));
    	
    	double[][] geneExpMatrix={{0.4, 0.1, 1.0, -1.0}, {0.5, 0.2, 1.0, -1.0}, {0.1, 0.18750000000000003, 1.375, 1.3}, {0.2, 4.68, -5.0, 7.8}, {0.3, 50.0, -6.0, 1.1}};
    	Assert.assertTrue("Wrong gene Expression MEtrix", Arrays.deepEquals(results.getProcessedMatrix(), geneExpMatrix));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] maxPlusConcepts=results.getMaxPlusNumConcepts();
    	double[][] minPlusConcepts=results.getMinPlusNumConcepts();
    	
    	
    	//System.out.println(Arrays.deepToString(maxPlusConcepts));
    	//System.out.println(Arrays.deepToString(minPlusConcepts));
    	
    	double[][] emax={{-6.0, 3.0}, {-1.0, 4.0}};
    	double[][] emin={{50.0, 3.0}, {7.8, 4.0}, {1.375, 5.0}, {1.1, 4.0}, {1.0, 5.0}, {0.5, 6.0}, {0.4, 5.0}, {0.3, 6.0}, {0.1, 4.0}};    	

    	Assert.assertTrue("Wrong number of concepts and phi", Arrays.deepEquals(emax, maxPlusConcepts));
    	Assert.assertTrue("Wrong number of concepts and varphi", Arrays.deepEquals(emin, minPlusConcepts));
    	

    	KFCAObjectInfo o=new KFCAObjectInfo(results.getProcessedMatrix());
    	    	    	
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(2,0));
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(1,-2));
    	Assert.assertEquals("Wrong concept ID", 8, o.getMaxConceptId(1,-1));
    	Assert.assertEquals("Wrong concept ID", 8, o.getMaxConceptId(1,-0.5));
    	Assert.assertEquals("Wrong concept ID", 8, o.getMaxConceptId(1,-0.5));
    	
    	Assert.assertEquals("Wrong concept ID", 0, o.getMaxConceptId(3,-6));
    	Assert.assertEquals("Wrong concept ID", 4, o.getMaxConceptId(3,-5));
    	Assert.assertEquals("Wrong concept ID", 4, o.getMaxConceptId(3,0));
    	
    	//System.out.println("Start phi: "+o.getMaxMarginPhi(1,0)[0]);    	    	
    	//System.out.println("Stop phi: "+o.getMaxMarginPhi(1,0)[1]);
    	
    	Assert.assertEquals("Wrong Start phi", -6.0, o.getMaxMarginPhi(1,0)[0]);
    	Assert.assertEquals("Wrong Start phi", -5.0, o.getMaxMarginPhi(3,4)[0]);
    	Assert.assertEquals("Wrong Start phi", -1.0, o.getMaxMarginPhi(1,8)[0]);
    	Assert.assertEquals("Wrong Start phi", null, o.getMaxMarginPhi(2,9));
    	
    	
    	
		double[] maxplusPhi={-6.0, 0.0};
    	long[] conceptsMax={0, 4};
    	Assert.assertTrue("Wrong", Arrays.equals(maxplusPhi, o.getMaxplusPhi(4)));
    	Assert.assertTrue("Wrong", Arrays.equals(conceptsMax, o.getMaxplusConceptId(4)));
    	double[] minplusPhi={50.0,1.1,0.3, 0.0};
    	long [] concepts={0,2,10,11};
    	Assert.assertTrue("Wrong", Arrays.equals(minplusPhi, o.getMinplusVarphi(4)));
    	Assert.assertTrue("Wrong", Arrays.equals(concepts, o.getMinplusConceptId(4)));
    	
    	Assert.assertEquals("Wrong minplus concept ID", 0, o.getMinConceptId(0,4));
    	Assert.assertEquals("Wrong minplus concept ID", 4, o.getMinConceptId(0,1.0));
    	Assert.assertEquals("Wrong minplus concept ID", 5, o.getMinConceptId(0,0.3));
    	Assert.assertEquals("Wrong minplus concept ID", 7, o.getMinConceptId(1,0));
    	Assert.assertEquals("Wrong minplus concept ID", 5, o.getMinConceptId(1,0.5));
	}

    @Test
    public void testGeneExpressionEntropy() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.1,0.1,0.1},
    		    {0.1,0.15,0.11,0.12},
    		    {0.2,0.2,0.2,0.2},
    		    {0.2,0.2,0.2,0.2},
    		    {0.2,0.2,0.2,0.2},
    		    {0.2,5,7,0.1},
    		    {0.2,8,5,0.7},
    		    {0.3,50,6,1.1},
    		    {0.4,0.1,1,2},
    		    {0.5,0.2,1,1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_ENTROPY);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	
    	Assert.assertEquals("Wrong number of differnet elements", 18 ,mi.getNumElements());
    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	//System.out.println("->"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] geneExpMatrix={{0.4,0.1,1,2}, {0.5,0.2,1,1}, {0.1,0.1,0.1,0.1}, {0.2,0.2,0.2,0.2}, {0.3,50,6,1.1}};
    	Assert.assertTrue("Wrong gene Expression MEtrix", Arrays.deepEquals(results.getProcessedMatrix(), geneExpMatrix));
   	}

    @Test
    public void testGeneExpressionRandom() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.3,0.1,0.1},
    		    {0.1,0.4,0.11,0.12},
    		    {0.2,0.2,0.1,0.2},
    		    {0.2,0.2,0.2,0.2},
    		    {0.2,0.2,0.3,0.2},
    		    {0.2,0.2,0.4,0.2},    		    
    		    {0.2,8,5,0.7},
    		    {0.3,50,6,1.1},
    		    {0.4,0.1,1,2},
    		    {0.5,0.2,1,1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_RANDOM);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	   	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	   	
    	//System.out.println("->"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] pMatrix=results.getProcessedMatrix();
    	boolean equalMatrix=false;
    	for (int i=0;i<4;i++){
    		if (Arrays.equals(data[i],pMatrix[2])){
    			equalMatrix=true;
    		}
    	}
    	Assert.assertTrue("Wrong GEN1",equalMatrix);
    	
    	equalMatrix=false;
    	for (int i=4;i<9;i++){
    		if (Arrays.equals(data[i],pMatrix[3])){
    			equalMatrix=true;
    		}
    	}
    	Assert.assertTrue("Wrong GEN2",equalMatrix);
    	
    	Assert.assertTrue("Wrong GEN3",Arrays.equals(data[9],pMatrix[4]));
    	Assert.assertTrue("Wrong GEN4",Arrays.equals(data[10],pMatrix[0]));
    	Assert.assertTrue("Wrong GEN5",Arrays.equals(data[11],pMatrix[1]));
   	}    
    
    @Test
    public void testGeneExpressionMean() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.3,0.1,0.1},
    		    {0.1,0.4,0.11,0.12},
    		    {0.2,0.2,0.1,0.2},
    		    {0.2,0.2,0.2,0.2},
    		    {0.2,0.2,0.3,0.2},
    		    {0.2,0.2,0.4,0.2},    		    
    		    {0.2,8,5,0.7},
    		    {0.3,50,6,1.1},
    		    {0.4,0.1,1,2},
    		    {0.5,0.2,1,1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_MEAN);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	   	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	   	
    	System.out.println("->"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] pMatrix=results.getProcessedMatrix();
    	
    	Assert.assertTrue("Wrong GEN3",Arrays.equals(data[9],pMatrix[4]));
    	Assert.assertTrue("Wrong GEN4",Arrays.equals(data[10],pMatrix[0]));
    	Assert.assertTrue("Wrong GEN5",Arrays.equals(data[11],pMatrix[1]));
    	
    	Assert.assertTrue("Wrong GEN1",Arrays.equals(data[1],pMatrix[2]));
    	Assert.assertTrue("Wrong GEN2",Arrays.equals(data[8],pMatrix[3]));
    	
   	}
    
    
    @Test
    public void testGeneExpressionVar() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1}, //var 0
    			{0.1,0.2,2,3},     //1.5069
    		    {0.1,0.3,0.1,0.1}, //0.0075
    		    {0.1,0.4,7,0.12},  //8.66
    		    {0.2,0.2,0.1,0.2}, //0.0018
    		    {0.2,0.2,0.2,0.2}, //0
    		    {0.2,0.2,0.3,0.2}, //0.0018
    		    {0.2,0.2,0.4,0.2}, //0.0075 		    
    		    {0.2,0.2,0.2,0.2}, //0
    		    {0.3,50,6,1.1},
    		    {0.4,0.1,1,2},
    		    {0.5,0.2,1,1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_VARIANCE);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	   	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	   	
    	System.out.println("->"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] pMatrix=results.getProcessedMatrix();
    	
    	Assert.assertTrue("Wrong GEN3",Arrays.equals(data[9],pMatrix[4]));
    	Assert.assertTrue("Wrong GEN4",Arrays.equals(data[10],pMatrix[0]));
    	Assert.assertTrue("Wrong GEN5",Arrays.equals(data[11],pMatrix[1]));
    	
    	Assert.assertTrue("Wrong GEN1",Arrays.equals(data[3],pMatrix[2]));
    	Assert.assertTrue("Wrong GEN2",Arrays.equals(data[7],pMatrix[3]));
    	
   	}
    
    @Test
    public void testGeneExpressionCorr() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.11}, 
    			{0.2,0.2,0.25,0.2},
    		    {0.4,0.4,0.5,0.4}, 
    		    {0.1,0.4,7,0.12},  
    		    {0.2,0.2,0.1,0.2},
    		    {0.2,0.2,0.2,0.25},
    		    {0.2,0.2,0.3,0.2},
    		    {0.2,0.2,0.4,0.2}, 		    
    		    {0.2,0.2,0.2,0.25},
    		    {0.3,50,6,1.1},
    		    {0.4,0.1,1,2},
    		    {0.5,0.2,1,1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_CORRELATION);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	   	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	   	
    	System.out.println("->"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    	
    	double[][] pMatrix=results.getProcessedMatrix();
    	
    	Assert.assertTrue("Wrong GEN3",Arrays.equals(data[9],pMatrix[4]));
    	Assert.assertTrue("Wrong GEN4",Arrays.equals(data[10],pMatrix[0]));
    	Assert.assertTrue("Wrong GEN5",Arrays.equals(data[11],pMatrix[1]));
    	
    	Assert.assertTrue("Wrong GEN1",Arrays.equals(data[1],pMatrix[2]));
    	Assert.assertTrue("Wrong GEN2",Arrays.equals(data[5],pMatrix[3]));
    	
   	}
    
    @Test
    public void testGeneExpressionSummary() throws FileNotFoundException {
    	dm=new DataMatrix();
    	double[][] data={ 
    			{0.1,0.1,0.1,0.1},
    			{0.1,0.2,2,3},
    		    {0.1,0.3,0.4,0.1},
    		    {0.1,0.15,3,2},
    		    {0.2,0.4,-5,30},
    		    {0.2,9,-5,0.3},
    		    {0.2,1,-5,3},
    		    {0.2,5,-5,5},
    		    {0.2,8,-5,0.7},
    		    {0.3,50,-6,1.1},
    		    {0.4,0.1,1,-1},
    		    {0.5,0.2,1,-1}
    	};
    	dm.setRawData(data);
    	
    	String[] colNames={"A","B","C","D"};
    	dm.setColNames(colNames);
    	String[] rowNames={"prob1","prob2","prob3","prob4","prob5","prob6","prob7","prob8","prob9","prob10","prob11","prob12"};
    	dm.setRowNames(rowNames);
    	dm.setMicroArrayType(DataTypeEnum.TEST);
    	
    	p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(0);    	
    	
    	PreprocessorEnum penum=PreprocessorEnum.LOGPREPROUNIT;
    	
    	MatrixInfo mi=dm.getHistogram(penum);
    	
    	p.setPreprocessorType(penum);    	    	    	
    	p.setGeneExpressionType(GeneExpType.GEXP_SUMMARY);
    	p.setMaxPhiToExplore(mi.getNumElements());
    	p.execute();
    	
    	Assert.assertEquals("Wrong number of differnet elements", 19 ,mi.getNumElements());
    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	//KFCAExplore ke=new KFCAExploreWithObjectInfo64bits(p);
    	ke.geneInfo=geneInfo;
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	System.out.println("---"+Arrays.deepToString(results.getProcessedMatrix()));
    	
    	double[][] geneExpMatrix={{0.4, 0.1, 1.0, -1.0}, {0.5, 0.2, 1.0, -1.0}, {0.4, 0.7500000000000001, 5.5, 5.2}, {1.0, 23.4, -25.0, 39.0}, {0.3, 50.0, -6.0, 1.1}};
    	Assert.assertTrue("Wrong gene Expression MEtrix", Arrays.deepEquals(results.getProcessedMatrix(), geneExpMatrix));
    	
    	String[] rowGeneNames={"GEN4", "GEN5", "GEN1", "GEN2", "GEN3"};
    	Assert.assertTrue("Wrong gene names", Arrays.deepEquals(results.getRowNames(), rowGeneNames));
    }
    
    @Test
    public void testExploreArabidopsis() throws FileNotFoundException {
    	
    	String celIDFiles="1236";
    	String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL","GSM237292.CEL","GSM237293.CEL","GSM237294.CEL","GSM237295.CEL"};
    	//String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setMicroArrayType(DataTypeEnum.ATH1121501);
    	dm.setCelIDFiles(celIDFiles);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessCELFilesTest.createSummary(processCELFiles,celFiles, celIDFiles);    	
    	
    	dm.setColNames(celFiles);
    	dm.loadDataFromFileName();
    	
    	processCELFiles.removeFiles(celIDFiles);
    	

    	
    	Preprocessor p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(1);    	
    	
    	p.setPreprocessorType(PreprocessorEnum.LOGPREPROARITMEAN);    	    	    	
    	p.setMaxPhiToExplore(10);	
    	p.execute();
    	    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	
    	ke.execute();
    	KFCAResults results=ke.getResults();    	
    	    	    	    	
    	Assert.assertTrue("There should be more than one maxplus concept", results.getMaxPlusNumConcepts().length>0);
    	Assert.assertTrue("There should be more than one minplus concept", results.getMaxPlusNumConcepts().length>0);
    	   
    	
    	double[][] maxPlusConcepts=results.getMaxPlusNumConcepts();
    	double[][] minPlusConcepts=results.getMinPlusNumConcepts();
    	
    	
    	System.out.println(Arrays.deepToString(maxPlusConcepts));
    	System.out.println(Arrays.deepToString(minPlusConcepts));
    	    	    	
    	double[][] emax={{-0.8853959206468315, 3.0}, {-0.1219380438441847, 91.0}, {-0.0616979676203618, 138.0}, {-0.034736331037551096, 175.0}, {-0.01617795249675376, 212.0}, {-0.001427046132005338, 250.0}};
    	double[][] emin={{0.7406081521761284, 3.0}, {0.10808731045195762, 44.0}, {0.05688955981921596, 88.0}, {0.031546627905574734, 158.0}, {0.013411651195354413, 212.0}};    	

    	Assert.assertTrue("Wrong number of concepts and phi", Arrays.deepEquals(emax, maxPlusConcepts));
    	Assert.assertTrue("Wrong number of concepts and varphi", Arrays.deepEquals(emin, minPlusConcepts));
    	
    	
    	Assert.assertEquals("Wrong row number", results.getRowNames().length,22810);
    	Assert.assertEquals("Wrong col number", results.getColNames().length,8);
    	
    	Assert.assertEquals("Wrong name of first column","GSM237280.CEL", results.getColNames()[0]);
    	Assert.assertEquals("Wrong name of first row","AFFX-BioB-5_at", results.getRowNames()[0]);
    	Assert.assertEquals("Wrong name of last row","257589_at", results.getRowNames()[22809]);
    	Assert.assertEquals("Wrong name of last row","257587_at", results.getRowNames()[22807]);    	
    	
    	Assert.assertEquals("Wrong microarray type", DataTypeEnum.ATH1121501 , results.getMicroArrayType());
    	/*    	
    	KFCAObjectExplored o=results.getObjectExplored()[22746];
    	System.out.println(o);
    	
    	
    	boolean[] rowI=results.getMatrixMinPlus(1.9285915561440843)[22746];
    	System.out.println(Arrays.toString(rowI));
    	
    	rowI=results.getMatrixMinPlus(0.7809998863723978)[22746];
    	System.out.println(Arrays.toString(rowI));
    	
    	rowI=results.getMatrixMinPlus(0.3904999431861989)[6596];
    	System.out.println(Arrays.toString(rowI));
    	*/
	}        
    
    @Test
    public void testGeneExpressionArabidopsis() throws FileNotFoundException {
    	
    	String celIDFiles="1236";
    	String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL","GSM237292.CEL","GSM237293.CEL","GSM237294.CEL","GSM237295.CEL"};
    	//String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setMicroArrayType(DataTypeEnum.ATH1121501);
    	dm.setCelIDFiles(celIDFiles);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessCELFilesTest.createSummary(processCELFiles,celFiles, celIDFiles);    	
    	
    	dm.setColNames(celFiles);
    	dm.loadDataFromFileName();
    	
    	processCELFiles.removeFiles(celIDFiles);
    	
    	Preprocessor p=new Preprocessor();
    	p.setDataMatrix(dm);
    	p.setName(dm.getName());
    	p.setAlgorithm(1);    	
    	p.setGeneExpressionType(GeneExpType.GEXP_AVERAGE);
    	
    	p.setPreprocessorType(PreprocessorEnum.LOGPREPROARITMEAN);    	    	    	
    	p.setMaxPhiToExplore(10);	
    	p.execute();
    	    	
    	KFCAExplore ke=new KFCAExploreSimple64bits(p);
    	ke.geneInfo=geneInfo;
    	
    	ke.execute();
    	KFCAResults results=ke.getResults();    	
    	    	    	    	
    	Assert.assertTrue("There should be more than one maxplus concept", results.getMaxPlusNumConcepts().length>0);
    	Assert.assertTrue("There should be more than one minplus concept", results.getMaxPlusNumConcepts().length>0);
    	
    	Assert.assertEquals("Gene name","AT3G04220",results.getRowNames()[0]);
    	

	}        
    
}
