package es.uc3m.tsc.kfca.explore;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.kfca.explore.KFCAExplore;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.math.MatrixInfo;

@RunWith(JUnit4.class)
public class KFCAExploreTest {

	DataMatrix dm;
	Preprocessor p;
	
	
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
    	dm.setMicroArrayType(DataTypeEnum.HGU133PLUS2);
    	
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
    	
    	KFCAExplore ke=new KFCAExplore(p);
    	ke.execute();
    	KFCAResults results=ke.getResults();    	    
    	
    	double[][] maxPlusConcepts=results.getMaxPlusNumConcepts();
    	double[][] minPlusConcepts=results.getMinPlusNumConcepts();
    	
    	
    	System.out.println(Arrays.deepToString(maxPlusConcepts));
    	System.out.println(Arrays.deepToString(minPlusConcepts));
    	
    	double[][] emax={{-2.3025850929940455, 5.0}, {-1.8971199848858813, 6.0}, {-1.6094379124341003, 6.0}, {-1.2039728043259361, 6.0}, {-0.916290731874155, 5.0}, {-0.6931471805599453, 5.0}, {-0.35667494393873245, 4.0}, {0.0, 6.0}};
    	double[][] emin={{0.0, 4.0}, {0.0, 4.0}, {0.09531017980432493, 9.0}, {0.6931471805599453, 6.0}, {1.0986122886681098, 8.0}, {1.6094379124341003, 7.0}, {1.62924053973028, 7.0}, {1.9459101490553132, 6.0}, {2.0794415416798357, 5.0}, {2.1972245773362196, 5.0}, {2.302585092994046, 5.0}, {3.4011973816621555, 4.0}, {3.912023005428146, 3.0}};
    	

    	Assert.assertTrue("Wrong number of concepts and phi", ArrayUtils.almostEqual(emax, maxPlusConcepts,0.1));
    	Assert.assertTrue("Wrong number of concepts and varphi", ArrayUtils.almostEqual(emin, minPlusConcepts,0.1));
    	
    }
}
