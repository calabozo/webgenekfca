package es.uc3m.tsc.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import es.uc3m.tsc.math.ClusterAnalysis.SimilarityEnum;

public class ClusterAnalysisTest {

	@Test
	public void testEuclidean() {		
		double[][] data={ 
    			{1,1,1,1},
    			{1,1,1,1},
    			{1,1,0,0},
    			{1,2,1,0},
    			{3,3,0,0}
    	};
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		double ecl=cla.euclideanDistance(15, data[0], data[1]);
		Assert.assertEquals("Wrong euclidean distance", 0.0 ,ecl);
    	
		ecl=cla.euclideanDistance(15, data[0], data[2]);
		Assert.assertEquals("Wrong euclidean distance", 1.41421356 ,ecl,0.0001);
		
		ecl=cla.euclideanDistance(3, data[0], data[2]);
		Assert.assertEquals("Wrong euclidean distance", 0.0 ,ecl);
		
		ecl=cla.euclideanDistance(15, data[0], data[4]);
		Assert.assertEquals("Wrong euclidean distance", Math.sqrt(10), ecl );
		
		ecl=cla.euclideanDistance(7, data[0], data[4]);
		Assert.assertEquals("Wrong euclidean distance", 3.0, ecl );
		
		ecl=cla.euclideanDistance(11, data[2], data[3]);
		Assert.assertEquals("Wrong euclidean distance", 1.0, ecl );
		
	}

	@Test
	public void testPearsonCorrelation() {		
		double[][] data={ 
    			{1,1,1,1},    			
    			{1,1,0,0},
    			{1,2,1,0},
    			{3,4,0,0}
    	};
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		double ecl=cla.pearsonCorrelation(15, data[0], data[1]);
		Assert.assertEquals("Wrong pearson correlation", 0.0 ,ecl);
    	
		ecl=cla.pearsonCorrelation(15, data[1], data[2]);
		Assert.assertEquals("Wrong pearson distance", 1/Math.sqrt(2) ,ecl);
		
		ecl=cla.pearsonCorrelation(3, data[0], data[2]);
		Assert.assertEquals("Wrong pearson distance", 0.0 ,ecl);
		
		ecl=cla.pearsonCorrelation(3, data[2], data[3]);
		Assert.assertEquals("Wrong pearson distance", 1.0, ecl );
		
		ecl=cla.pearsonCorrelation(7, data[2], data[3]);
		Assert.assertEquals("Wrong pearson distance", 0.69338, ecl,0.001);
		
		ecl=cla.pearsonCorrelation(15, data[2], data[3]);
		Assert.assertEquals("Wrong pearson distance", 0.79212, ecl,0.001 );
		
	}
	@Test
	public void testHomogeneity1Euclidean() {		
		double[][] data={ 
    			{1,1,1,1},
    			{1,1,1,1},
    			{1,1,0,0},
    			{1,1,0,0}    			
    	};
		Integer[] group0={0,1};
		Integer[] group1={2,3};
		Integer[] group2={0,1,2,3};
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		
		double h1=cla.homogeneity1(15, group0,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity1(15, group1,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity1(3, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity1(7, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 2.0/3.0, h1 );
		
		h1=cla.homogeneity1(15, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 2.0*Math.sqrt(2)/3.0, h1 );
		
	}
	
	@Test
	public void testHomogeneity2Euclidean() {		
		double[][] data={ 
    			{1,1,1,1},
    			{1,1,1,1},
    			{1,1,0,0},
    			{1,1,0,0}    			
    	};
		Integer[] group0={0,1};
		Integer[] group1={2,3};
		Integer[] group2={0,1,2,3};
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		
		double h1=cla.homogeneity2(15, group0,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity2(15, group1,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity2(3, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.0, h1 );
		
		h1=cla.homogeneity2(7, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", 0.5, h1 );
		
		h1=cla.homogeneity2(15, group2,SimilarityEnum.EUCLIDEAN);
		Assert.assertEquals("Wrong homogeneity", Math.sqrt(0.5), h1 );
		
	}
	
	@Test
	public void testHomogeneity1Correlation() {		
		double[][] data={ 
    			{1,0,1,0},    			
    			{2,0,2,0},
    			{1,2,1,0},
    			{3,4,0,0}
    	};

		Integer[] group0={0,1};
		Integer[] group1={2,3};
		Integer[] group2={0,1,2,3};
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
				
		double h1=cla.homogeneity1(15, group0,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", 1.0, h1 );
		
		h1=cla.homogeneity1(15, group1,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", 0.79212, h1, 0.0001 );
		
		h1=cla.homogeneity1(3, group1,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", 1.0, h1 );
		
		h1=cla.homogeneity1(3, group2,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", -1.0/3.0, h1 );
		
		h1=cla.homogeneity1(7, group2,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", -0.28223, h1,0.001 );
		
		h1=cla.homogeneity1(15, group2,SimilarityEnum.CORRELATION);
		Assert.assertEquals("Wrong homogeneity", 0.25201, h1 , 0.001);
	}
	
	@Test
	public void testSeparation1Euclidean() {		
		double[][] data={ 
    			{1,1,1,1},
    			{1,1,1,1},
    			{1,1,0,0},
    			{1,1,0,0},
    			{8,20,20,5},
    			{8,20,20,8},
    			{0,0,8,7},
    			{0,0,7,8}
    	};
		Integer[] group0={0,1};
		Integer[] group1={2,3};
		Integer[] group2={0,1,2,3};
		
		Integer[] group3={4,5}; //conceptid=9
		Integer[] group4={6,7}; //conceptid=12
		
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		HashMap<Long,List<Integer>> rows1=new HashMap<Long,List<Integer>>();
		rows1.put(3L, new ArrayList<Integer>(Arrays.asList(group1)));
		double sep1=cla.separation1(15L, group0, rows1, SimilarityEnum.EUCLIDEAN);		
		
		HashMap<Long,List<Integer>> rows2=new HashMap<Long,List<Integer>>();
		rows2.put(9L, new ArrayList<Integer>(Arrays.asList(group3)));
		double sep2=cla.separation1(15L, group0, rows2, SimilarityEnum.EUCLIDEAN);
		
		HashMap<Long,List<Integer>> rows3=new HashMap<Long,List<Integer>>();
		rows3.put(12L, new ArrayList<Integer>(Arrays.asList(group4)));
		double sep3=cla.separation1(15L, group0, rows3, SimilarityEnum.EUCLIDEAN);

		rows1.putAll(rows2);
		rows1.putAll(rows3);
		double sept=cla.separation1(15L, group0, rows1, SimilarityEnum.EUCLIDEAN);
				
		Assert.assertTrue(sept==(sep1+sep2+sep3)/3);
		Assert.assertTrue(sep1<sep2);
		Assert.assertTrue(sep1<sep3);
		Assert.assertTrue(sep3<sep2);
		Assert.assertEquals(13.028724605947934, sept);
	}
	
	public void testSeparation2Euclidean() {		
		double[][] data={ 
    			{1,1,1,1},
    			{1,1,1,1},
    			{1,1,0,0},
    			{1,1,0,0},
    			{8,20,20,5},
    			{8,20,20,8},
    			{0,0,8,7},
    			{0,0,7,8}
    	};
		Integer[] group0={0,1};
		Integer[] group1={2,3};
		Integer[] group2={0,1,2,3};
		
		Integer[] group3={4,5}; //conceptid=9
		Integer[] group4={6,7}; //conceptid=12
		
		
		ClusterAnalysis cla=new ClusterAnalysis(data);
		HashMap<Long,List<Integer>> rows1=new HashMap<Long,List<Integer>>();
		rows1.put(3L, new ArrayList<Integer>(Arrays.asList(group1)));
		double sep1=cla.separation2(15L, group0, rows1, SimilarityEnum.EUCLIDEAN);		
		
		HashMap<Long,List<Integer>> rows2=new HashMap<Long,List<Integer>>();
		rows2.put(9L, new ArrayList<Integer>(Arrays.asList(group3)));
		double sep2=cla.separation2(15L, group0, rows2, SimilarityEnum.EUCLIDEAN);
		
		HashMap<Long,List<Integer>> rows3=new HashMap<Long,List<Integer>>();
		rows3.put(12L, new ArrayList<Integer>(Arrays.asList(group4)));
		double sep3=cla.separation2(15L, group0, rows3, SimilarityEnum.EUCLIDEAN);

		rows1.putAll(rows2);
		rows1.putAll(rows3);
		double sept=cla.separation2(15L, group0, rows1, SimilarityEnum.EUCLIDEAN);
				
		Assert.assertTrue(sept==(sep1+sep2+sep3)/3);
		Assert.assertTrue(sep1<sep2);
		Assert.assertTrue(sep1<sep3);
		Assert.assertTrue(sep3<sep2);
		Assert.assertEquals(13.028724605947934, sept);
	}
	
	@Test
	public void testHipergeometricDisribution() {		
		double p=ClusterAnalysis.hipergeometricDisribution(4, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0.003964583, p, 0.001);
		
		
		p=ClusterAnalysis.hipergeometricDisribution(5, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0.0001189375, p, 0.001);
	
		p=ClusterAnalysis.hipergeometricDisribution(2, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0.20983971, p, 0.001);
		
		p=ClusterAnalysis.hipergeometricDisribution(1, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0.43133719, p, 0.001);
		
		p=ClusterAnalysis.hipergeometricDisribution(0, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0.310562782, p, 0.001);
		
		
		p=ClusterAnalysis.pValue(3, 1000, 3, 22810);
		Assert.assertEquals("Wrong HiperGeometric", 0, p, 0.001);
		
		p=ClusterAnalysis.pValue(6, 10, 5, 50);
		Assert.assertEquals("Wrong HiperGeometric", 0, p, 0.001);
		
		p=ClusterAnalysis.pValue(13, 44, 111, 54675);		
		Assert.assertEquals("Wrong HiperGeometric", 0.0, p);
		
		p=ClusterAnalysis.pValue(1, 1000, 2, 22810);
		Assert.assertEquals("Wrong HiperGeometric", 0.0857, p, 0.001);
		
		/*
		p=ClusterAnalysis.pValue(2500, 10000, 3000, 22810);
		Assert.assertEquals("Wrong HiperGeometric", 0, p, 0.001);
		*/
		
		
	}
}
