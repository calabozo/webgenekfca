package es.uc3m.tsc.gene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.uc3m.tsc.file.FileTypeEnum;
import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.file.ProcessUploadedFiles.FileDimensions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")	
public class ProcessRNASeqFilesTest {	
	static String path="src/test/res/";
	@Resource
    private ProcessUploadedFiles processCELFiles;
	
	
	@Test
	public void testRemoveFile() throws FileNotFoundException{
		String id="12345";		
		String[] rnaSeqFiles={"AH1.txt","AH2.txt","AS1.txt","AS2.txt","AX1.txt","AX2.txt"};
		
		createSummary(processCELFiles,rnaSeqFiles,id);
				
		Assert.assertEquals("The file already exists ",FileTypeEnum.FILETYPE_ERROR,processCELFiles.saveFile(id, rnaSeqFiles[0], getInputStream(path+"rnaseq/"+rnaSeqFiles[0]),null));
		
		
		boolean b=processCELFiles.removeSingleFile(id, "AH1.txt");
		Assert.assertEquals("Cannot remove TXT file",true, b);
		
		processCELFiles.removeFiles(id);		
	}
	
	@Test
	public void testHasFile() throws FileNotFoundException{
		String id="13346";	
		String[] celFiles={"AH1.txt","AH2.txt","AS1.txt","AS2.txt"};
		
		Assert.assertEquals("Cannot remove TXT file",false, processCELFiles.hasFilesForId(id));
		createSummary(processCELFiles,celFiles,id);
				
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.hasFilesForId(id));
		
		Assert.assertEquals("Cannot remove TXT file",false, processCELFiles.removeSingleFile(id, "XXXXXX"));
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.removeSingleFile(id, celFiles[0]));
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.removeSingleFile(id, celFiles[1]));
		
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.hasFilesForId(id));
		
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.removeSingleFile(id, celFiles[2]));
		Assert.assertEquals("Cannot remove TXT file",true, processCELFiles.removeSingleFile(id, celFiles[3]));
		
		Assert.assertEquals("Cannot remove TXT file",false, processCELFiles.hasFilesForId(id));
		
		
		processCELFiles.removeFiles(id);
		
	}
	
	@Test
	public void testSummaryFail() throws FileNotFoundException{
		String id="12345";		
		String[] rnaSeqFiles={"AH1.txt","AH2.txt","AS1.txt","AS2.txt","AX1.txt","AX2.txt"};
		boolean fail=false;
		try{
			createSummaryFail(processCELFiles,rnaSeqFiles,id);						
		}catch(junit.framework.AssertionFailedError e){
			fail=true;
		}
		
		if (!fail){
			Assert.assertTrue("SummaryFail must Fail!", false);
		}
		
		processCELFiles.removeFiles(id);		
	}
	
	
	private static InputStream getInputStream(String fileName) throws FileNotFoundException{
		File f=new File(fileName);
		InputStream ios=new FileInputStream(f);
		
		return ios;
	}
	
	public static void createSummaryFail(ProcessUploadedFiles processCELFiles, String[] celFiles, String id) throws FileNotFoundException{
				
		FileTypeEnum ret;		
		FileDimensions fileDimensions=processCELFiles.new FileDimensions();
		for (String cel:celFiles){
			ret=processCELFiles.saveFile(id, cel, getInputStream(path+"rnaseq/"+cel),fileDimensions);
			Assert.assertEquals(FileTypeEnum.FILETYPE_CEL,ret);
		}		
	}

	public static void createSummary(ProcessUploadedFiles processCELFiles, String[] celFiles, String id) throws FileNotFoundException{
		
		FileTypeEnum ret;		
		FileDimensions fileDimensions=processCELFiles.new FileDimensions();
		for (String cel:celFiles){
			ret=processCELFiles.saveFile(id, cel, getInputStream(path+"rnaseq/"+cel),fileDimensions);
			Assert.assertEquals(FileTypeEnum.FILETYPE_COUNT,ret);
		}		
	}
}
