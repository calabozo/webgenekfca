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
public class ProcessCELFilesTest {
	static String path="src/test/res/";
	@Resource
    private ProcessUploadedFiles processCELFiles;
	
	@Test
	public void testStartup(){
		String[] cdfFiles=processCELFiles.getCDFNames();
		Assert.assertEquals("ATH1-121501.CDF",cdfFiles[0]);
		Assert.assertEquals(true,processCELFiles.getAptToolsExist());
		Assert.assertEquals(true,processCELFiles.getFilesPathExist());		
	}
	
	@Test
	public void testCreateSummary() throws FileNotFoundException{
		String id="12345";
		String ret;
		String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL"};
		
		createSummary(processCELFiles,celFiles,id);
		ret=processCELFiles.process(id, "ATH1-121501.CDF", new StringBuilder());
		Assert.assertEquals(processCELFiles.getFilesPath()+"/"+id+"/rma-sketch.summary.txt",ret);
		
		processCELFiles.removeFiles(id);
		
	}
	
	@Test
	public void testRemoveFile() throws FileNotFoundException{
		String id="12345";
		String ret;
		String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL","GSM237292.CEL"};
		
		createSummary(processCELFiles,celFiles,id);
				
		Assert.assertEquals("The file already exists ",FileTypeEnum.FILETYPE_ERROR,processCELFiles.saveFile(id, celFiles[0], getInputStream(path+"cel/"+celFiles[0]),null));
		
		
		boolean b=processCELFiles.removeSingleFile(id, "GSM237292.CEL");
		Assert.assertEquals("Cannot remove CEL file",true, b);
		
		ret=processCELFiles.process(id, "ATH1-121501.CDF", new StringBuilder());
		Assert.assertEquals(processCELFiles.getFilesPath()+"/"+id+"/rma-sketch.summary.txt",ret);
		
		processCELFiles.removeFiles(id);		
	}
	
	@Test
	public void testHasFile() throws FileNotFoundException{
		String id="13346";	
		String[] celFiles={"GSM237280.CEL","GSM237281.CEL","GSM237282.CEL","GSM237283.CEL"};
		
		Assert.assertEquals("Cannot remove CEL file",false, processCELFiles.hasFilesForId(id));
		createSummary(processCELFiles,celFiles,id);
				
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.hasFilesForId(id));
		
		Assert.assertEquals("Cannot remove CEL file",false, processCELFiles.removeSingleFile(id, "XXXXXX"));
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.removeSingleFile(id, celFiles[0]));
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.removeSingleFile(id, celFiles[1]));
		
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.hasFilesForId(id));
		
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.removeSingleFile(id, celFiles[2]));
		Assert.assertEquals("Cannot remove CEL file",true, processCELFiles.removeSingleFile(id, celFiles[3]));
		
		Assert.assertEquals("Cannot remove CEL file",false, processCELFiles.hasFilesForId(id));
		
		
		processCELFiles.removeFiles(id);
		
	}
	
	
	@Test
	public void testTableFile() throws FileNotFoundException{
		String id="789346";	
		
		FileDimensions fileDimensions = processCELFiles.new FileDimensions();
		
		FileTypeEnum ret=processCELFiles.saveFile(id, "micro_array.txt", getInputStream(path+"micro_array.txt"),fileDimensions);
		
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, ret);
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, processCELFiles.getUploadedFileType(id));
		Assert.assertEquals("Wrong num rows",22810, fileDimensions.rows);
		Assert.assertEquals("Wrong num cols",8, fileDimensions.cols);
		
		ret=processCELFiles.saveFile(id, "plier-mm-sketch.summary.txt", getInputStream(path+"plier-mm-sketch.summary.txt"),fileDimensions);
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, ret);
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, processCELFiles.getUploadedFileType(id));
		Assert.assertEquals("Wrong num rows",22810, fileDimensions.rows);
		Assert.assertEquals("Wrong num cols",8, fileDimensions.cols);
		
		
		processCELFiles.removeFiles(id);
		Assert.assertEquals("Cannot remove file",false, processCELFiles.hasFilesForId(id));
		
		processCELFiles.removeFiles(id);
		
		
		ret=processCELFiles.saveFile(id, "octave_matrix.txt", getInputStream(path+"octave_matrix.txt"),fileDimensions);
		
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, ret);
		Assert.assertEquals("Wrong file type",FileTypeEnum.FILETYPE_TABLE, processCELFiles.getUploadedFileType(id));
		Assert.assertEquals("Wrong num rows",100, fileDimensions.rows);
		Assert.assertEquals("Wrong num cols",10, fileDimensions.cols);
		

		processCELFiles.removeFiles(id);
		Assert.assertEquals("Cannot remove file",false, processCELFiles.hasFilesForId(id));
		
		
	}
	private static InputStream getInputStream(String fileName) throws FileNotFoundException{
		File f=new File(fileName);
		InputStream ios=new FileInputStream(f);
		
		return ios;
	}
	
	public static void createSummary(ProcessUploadedFiles processCELFiles, String[] celFiles, String id) throws FileNotFoundException{
				
		FileTypeEnum ret;		
		
		for (String cel:celFiles){
			ret=processCELFiles.saveFile(id, cel, getInputStream(path+"cel/"+cel),null);
			Assert.assertEquals(FileTypeEnum.FILETYPE_CEL,ret);
		}		
	}

}
