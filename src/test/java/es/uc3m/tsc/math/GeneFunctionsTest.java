package es.uc3m.tsc.math;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.gene.PreprocessorEnum;
import es.uc3m.tsc.math.GeneFunctions.ProcessedDataMatrix;


@RunWith(JUnit4.class)
public class GeneFunctionsTest {
	
	@Test
	public void calcPreprocessor(){
	
		double rawData[]={1,2,3,4,5,6};
		Integer groupId1[]={0,0,1,1,2,2};
		double result1_aritmean[]={1.5,3.5,5.5};
		double result1_geomean[]={1.414213562,3.464101615,5.477225575};
						
		double outData[]=new double[3];
		GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROARITMEAN, rawData, groupId1, outData);		
		Assert.assertArrayEquals(result1_aritmean, outData,0.001);
		
		GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROGEOMEAN, rawData, groupId1, outData);
		Assert.assertArrayEquals(result1_geomean, outData,0.001);
		

		Integer groupId2[]={0,0,0,0,0,1};
		double result2_aritmean[]={3,6};
		double result2_geomean[]={2.605171085,6};
		outData=new double[2];
		
		GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROARITMEAN, rawData, groupId2, outData);		
		Assert.assertArrayEquals(result2_aritmean, outData,0.001);
		
		GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROGEOMEAN, rawData, groupId2, outData);
		Assert.assertArrayEquals(result2_geomean, outData,0.001);		
	}
	
	@Test
	public void calcPreprocessorMatrix(){
		double[][] rawData={{1,1,1,1},{2,2,2,2},{1,1,2,2}};
		double[][] expData={{0,0,0,0},{0,0,0,0},{-0.405465108,-0.405465108,0.287682072,0.287682072}};
		
		ProcessedDataMatrix outData=GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROARITMEAN, rawData);
		
		Assert.assertTrue("Matrix is not equal", ArrayUtils.almostEqual(outData.data, expData,0.001));
		Assert.assertEquals(outData.max, 0.287682072,0.0001);
		Assert.assertEquals(outData.min, -0.405465108,0.0001);
		
		double[][] expData1={{0,0,0,0},{0,0,0,0},{-0.34657,-0.34657,0.34657,0.34657}};
		outData=GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROGEOMEAN, rawData);
				
		Assert.assertTrue("Matrix is not equal", ArrayUtils.almostEqual(outData.data, expData1,0.01));
		Assert.assertEquals(outData.max, 0.34657,0.001);
		Assert.assertEquals(outData.min, -0.34657,0.001);
    	
		
		double[][] expData2={{0,0,0,0},{0,0,0,0},{-0.693147,-0.693147,0,0}};
		outData=GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROGEOMAX, rawData);
				
		Assert.assertTrue("Matrix is not equal", ArrayUtils.almostEqual(outData.data, expData2,0.01));
		Assert.assertEquals(outData.max, 0,0.001);
		Assert.assertEquals(outData.min, -0.693147,0.001);
		
		double[][] rawData2={{1,1,3,5},{2,2,2,7},{1,1,2,2}};
		double[][] expData3={{-0.9662,-0.9662,0.6017,1.3307},{-0.5774,-0.5774,-0.5774,1.7321},{-1,-1,1,1}};
		outData=GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROMEANVAR, rawData2);
		Assert.assertTrue("Matrix is not equal", ArrayUtils.almostEqual(outData.data, expData3,0.01));
		Assert.assertEquals(outData.max, 1.7321,0.001);
		Assert.assertEquals(outData.min, -1,0.001);
		
		double[][] expData4={{-1,-1,1.0002,0.9998},{0.6898,0.6898,-1.7137,0.3342},{0.6900,0.6900,0.3335,-1.7136}};
		outData=GeneFunctions.calcPreprocessor(PreprocessorEnum.LOGPREPROMEANVARCOLROW, rawData2);
		Assert.assertTrue("Matrix is not equal", ArrayUtils.almostEqual(outData.data, expData4,0.01));
		Assert.assertEquals(outData.max, 1.0002,0.001);
		Assert.assertEquals(outData.min, -1.7137,0.001);
		
	}
    
}