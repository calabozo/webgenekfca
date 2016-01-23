package es.uc3m.tsc.gene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;

import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.math.MatrixInfo;

@RooIntegrationTest(entity = DataMatrix.class)
public class DataMatrixIntegrationTest {
	
	private InputStream getInputStream(String fileName) throws FileNotFoundException{
		File f=new File(fileName);
		InputStream ios=new FileInputStream(f);
		
		return ios;
	}
	
    @Test
    public void testParser() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 8, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 22810, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "GSM237280.CEL", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "AFFX-BioB-5_at", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "257588_x_at", dm.getRowNames()[22808]);
    	Assert.assertEquals("Wrong value", 59.06530, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 12.87105, dm.getRawData()[22809][3],0);
    	
    }
    
    @Test
    public void testParserA() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/final-gene-expression-data-file.txt.magetab";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 30, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 54675, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "PDAC_Survival_155", dm.getColNames()[1]);
    	Assert.assertEquals("Wrong row name", "1007_s_at", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "1552393_at", dm.getRowNames()[100]);
    	Assert.assertEquals("Wrong value", 11.7868147606301, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 4.09169385951982, dm.getRawData()[54674][29],0);
    	
    }
    

    @Test
    public void testParser0() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/web_matrix.txt";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 6, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 7, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "ColA", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong col name", "ColF", dm.getColNames()[5]);
    	Assert.assertEquals("Wrong row name", "row1", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "row7", dm.getRowNames()[6]);
    	Assert.assertEquals("Wrong value", 6.21748, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 5.73925, dm.getRawData()[3][1],0);
    	Assert.assertEquals("Wrong value", 8.70269, dm.getRawData()[6][5],0);    	
    }
    
    @Test
    public void testParser1() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/data.txt";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 10, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 100, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "m0", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "g0", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "g50", dm.getRowNames()[50]);
    	Assert.assertEquals("Wrong value", 1.433060744367831, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 1.361475826441761, dm.getRawData()[3][1],0);
    	Assert.assertEquals("Wrong value", 0.07046699358082038, dm.getRawData()[99][9],0);
    	
    }

    @Test
    public void testParser2() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/octave_matrix.txt";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 10, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 100, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "m0", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "g0", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "g50", dm.getRowNames()[50]);
    	Assert.assertEquals("Wrong value", -1, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", -1, dm.getRawData()[3][1],0);
    	Assert.assertEquals("Wrong value", -2, dm.getRawData()[99][9],0);    	
    }

    @Test
    public void testParser3() throws FileNotFoundException{

    	String fileName=System.getProperty("user.dir")+"/src/test/res/matlab_matrix.csv";
    	
    	DataMatrix dm=new DataMatrix();    	    
    	dm.loadDataFromFileName(fileName);
    	    	    	
    	Assert.assertEquals("Wrong number of columns", 10, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 1000, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "m0", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "g0", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "g50", dm.getRowNames()[50]);
    	Assert.assertEquals("Wrong value", -0.91838, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 1.2885, dm.getRawData()[3][1],0);
    	Assert.assertEquals("Wrong value", 4.9849e-05, dm.getRawData()[278][0],0);    	
    	Assert.assertEquals("Wrong value", 0.10289, dm.getRawData()[999][9],0);
    	
    }
    
    @Test
    public void testParser4() throws FileNotFoundException{

    	String path=System.getProperty("user.dir")+"/src/test/res/cel/";
    	String celIDFiles="1234";
    	String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL"};
    	//String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setMicroArrayType(DataTypeEnum.ATH1121501);
    	dm.setCelIDFiles(celIDFiles);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessCELFilesTest.createSummary(processCELFiles,celFiles, celIDFiles);    	
    	
    	dm.loadDataFromFileName();
    	processCELFiles.removeFiles(celIDFiles);
    	    	
    	Assert.assertEquals("Wrong number of columns", 4, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 22810, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "GSM237280.CEL", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "AFFX-BioB-5_at", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "257588_x_at", dm.getRowNames()[22808]);
    	Assert.assertEquals("Wrong value", 6.25417, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 4.39881, dm.getRawData()[22809][3],0);
    	
    }
    
    @Test
    public void testParser5() throws FileNotFoundException{
    	String celIDFiles="1235";
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
    	    	
    	Assert.assertEquals("Wrong number of columns", 8, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 22810, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "GSM237280.CEL", dm.getColNames()[0]);
    	Assert.assertEquals("Wrong row name", "AFFX-BioB-5_at", dm.getRowNames()[0]);
    	Assert.assertEquals("Wrong row name", "257588_x_at", dm.getRowNames()[22808]);
    	Assert.assertEquals("Wrong value", 6.21748, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 4.65141, dm.getRawData()[22809][5],0);
    	
    	
    }
    
    
    @Test
    public void testParser6() throws FileNotFoundException{
    	String countIDFiles="1235";
    	String[] countFiles={"AH1.txt","AH2.txt","AS1.txt","AS2.txt","AX1.txt","AX2.txt"};
    	//String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setMicroArrayType(DataTypeEnum.SEQECOLI);
    	dm.setCelIDFiles(countIDFiles);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessRNASeqFilesTest.createSummary(processCELFiles,countFiles, countIDFiles);    	
    	
    	dm.setColNames(countFiles);
    	dm.loadDataFromFileName();
    	
    	processCELFiles.removeFiles(countIDFiles);
    	    	
    	Assert.assertEquals("Wrong number of columns", 6, dm.getNumCols());
    	Assert.assertEquals("Wrong number of rows", 4245, dm.getNumRows());
    	Assert.assertEquals("Wrong col name", "AH1.txt", dm.getColNames()[0] );
    	Assert.assertTrue("Wrong row name", Arrays.asList(dm.getRowNames()).contains("b2014"));
    	
    	Assert.assertEquals("Wrong value", 32, dm.getRawData()[0][0],0);    	
    	Assert.assertEquals("Wrong value", 5, dm.getRawData()[50][5],0);
    	
    	
    }
    
    @Test
    public void testHistogramMean() throws FileNotFoundException{

    	String path=System.getProperty("user.dir")+"/src/test/res/cel/";
    	String celIDFiles="1236";
    	String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL"};
    	//String fileName=System.getProperty("user.dir")+"/src/test/res/plier-mm-sketch.summary.txt";
    	
    	DataMatrix dm=new DataMatrix();
    	dm.setMicroArrayType(DataTypeEnum.ATH1121501);
    	dm.setCelIDFiles(celIDFiles);
    	
    	ProcessUploadedFiles processCELFiles=dm.getProcessFiles();
    	ProcessCELFilesTest.createSummary(processCELFiles,celFiles, celIDFiles);    	
    	
    	dm.loadDataFromFileName();
    	processCELFiles.removeFiles(celIDFiles);
    	    	
    	
    	MatrixInfo m=dm.getHistogram(PreprocessorEnum.LOGPREPROARITMEAN);
    	System.out.println(m.getMin());
    	System.out.println(m.getNumElements());
    	
    	Assert.assertEquals("Maximum ",0.39913505996800536, m.getMax(),0.000001);
    	Assert.assertEquals("Minimum ",-0.7242680661356514, m.getMin(),0.000001);
    	Assert.assertEquals("Number of elements erroneous ",90963, m.getNumElements());
    }
    
    

}
