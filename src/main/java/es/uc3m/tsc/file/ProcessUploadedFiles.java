package es.uc3m.tsc.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import es.uc3m.tsc.gene.DataMatrix;

public class ProcessUploadedFiles {
	
	public class FileDimensions{
		public int cols;
		public int rows;
	}
	
	String filesPath;
	String affymetrixPowerToolsDir;
	String affymetrixCDFDir;
	
	final String aptcmd="apt-probeset-summarize";
	
	HashMap<String,FileHolder> filesHash;
	Logger logger;
	
	public String getFilesPath(){
		return this.filesPath;
	}

	public long getFilesPathSize() {
		return this.folderSize(new File(this.filesPath));
	}

	private long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}
	public String getAptToolsDir(){
		return this.affymetrixPowerToolsDir;
	}
	public String getCDFDir(){
		return this.affymetrixPowerToolsDir;
	}
	public String[] getCDFNames(){
		try{
			File f=new File(affymetrixCDFDir);		
			return f.list();
		}catch(Exception e){		
			return null;
		}
	}
	public boolean getAptToolsExist(){
		try{
			return new File(this.affymetrixPowerToolsDir+"/"+aptcmd).exists();
		}catch(Exception e){
			return false;
		}
	}
	public boolean getFilesPathExist(){
		try{
			File f=new File(this.filesPath);
			return f.exists() && f.isDirectory();
		}catch(Exception e){
			return false;
		}
	}
	
	public void cleanDirectory() {
		this.filesHash.clear();
		this.removeRecursively(new File(this.filesPath));
	}

	@Deprecated
	public ProcessUploadedFiles(String filesPath,String aptPath, String CDFPath) throws Exception{
		logger = Logger.getLogger(this.getClass());
		
		this.affymetrixPowerToolsDir=aptPath;
		this.affymetrixCDFDir=CDFPath;
		
		this.filesHash=new HashMap<String,FileHolder>(); //TODO: Create a timed hashmap		
		
		File f=new File(filesPath);
		if (f.exists()){
			if (!f.isDirectory()){
				logger.error("The file " + filesPath + " is not a directory!.\nPlease modify your applicationContext.xml");
			}else{			
				this.filesPath=filesPath+"/celtmpfiles/";
				f=new File(this.filesPath);
				
				boolean b=f.exists();
				if (b){
					this.removeRecursively(f);					
				}else{
					b=f.mkdir();
				}
								
				if (b){
										
					logger.info("Temporal CEL files directory: "+this.filesPath);
					logger.info("Affymetrix Power Tools directory: "+this.affymetrixPowerToolsDir);
					logger.info("Temporal CEL files directory: "+this.affymetrixCDFDir);
					
					if (!this.getAptToolsExist()){
						logger.error("Affymetrix Power Tools not found!\nFile " + this.affymetrixPowerToolsDir + "/" + aptcmd + " does not exist.");
					}
				}else{								
					logger.error("Cannot create directory " + this.filesPath + ".\nPlease check write permissions.");
				}
			}			
		}else{
			logger.error("The directory " + filesPath + "  does not exist!.\nPlease modify your applicationContext.xml");
		}
		
	}
	private void removeRecursively(File f){
		File[] files=f.listFiles();
		if (files==null) return;
		logger.info("RemoveRecursively "+f.getAbsolutePath());
		for (File file:files){
			if (file.isDirectory()){
				this.removeRecursively(file);
			}
			file.delete();
		}
	}

	public FileTypeEnum saveFile(String id, String filename, InputStream is, FileDimensions fileDimensions){
		FileTypeEnum fRet=FileTypeEnum.FILETYPE_NOVALID;
		FileOutputStream fos=null;
        try {            
        	String completeFileName=filesPath + id+"/"+filename;
                                 
            FileHolder fileHolder=this.filesHash.get(id);
            
            if (fileHolder==null){
            	fileHolder=new FileHolder(id);
            	this.filesHash.put(id,fileHolder);            	
            	new File(filesPath + id).mkdir();							
            }            
            
            if (!fileHolder.containsFile(completeFileName)){
            	fileHolder.addFileName(completeFileName);
            	fos = new FileOutputStream(new File(completeFileName));
            	IOUtils.copy(is, fos);
            
            	logger.info("File "+filename+" uploaded as "+completeFileName);
            	fRet=getFileType(completeFileName, fileDimensions);
            	
            	if (fRet!=FileTypeEnum.FILETYPE_NOVALID){
            		if (fRet==FileTypeEnum.FILETYPE_TABLE){
            			fileHolder.removeAllFiles();
            			fileHolder.addFileName(completeFileName);
            		}            		
            		fileHolder.setFileType(fRet);
            	}else{
            		fileHolder.removeFileName(completeFileName);
            	}
            	
            }else{
            	logger.info("File "+completeFileName+" already exists");
            	fRet=FileTypeEnum.FILETYPE_ERROR;
            }
            
        } catch (FileNotFoundException ex) {                        
			logger.error("has thrown an exception: " + ex.getMessage());
            fRet=FileTypeEnum.FILETYPE_ERROR;
        } catch (IOException ex) {            
			logger.error("has thrown an exception: " + ex.getMessage());
            fRet=FileTypeEnum.FILETYPE_ERROR;
        } finally {
            try {
                if (fos!=null) fos.close();
                is.close();
            } catch (IOException ignored) {
            }
        }    
                
               
        return fRet;
	}
	
	private FileTypeEnum getFileType(String completeFileName, FileDimensions fileDimensions){
		FileTypeEnum fRet=FileTypeEnum.FILETYPE_NOVALID;
		
		try{
		
			BufferedReader bf = new BufferedReader(new FileReader(completeFileName));		
			String ln;        
			ln= bf.readLine();
			if (ln!=null)
				if (ln.startsWith("[CEL]") || ln.startsWith("\u0040\u0000")|| ln.startsWith("\u003b\u0001")){					
					bf.close();
					return FileTypeEnum.FILETYPE_CEL;
				}
									
			bf.close();
			
			char[] cln=ln.toCharArray();
			System.out.println("-"+Integer.toHexString(cln[0])+"-"+Integer.toHexString(cln[1])+"-");
			
		}catch(Exception e){
			fRet=FileTypeEnum.FILETYPE_ERROR;			
		}
        	
		
		DataMatrix n=new DataMatrix();
		if (n.loadDataFromFileName(completeFileName)){
			/*
			double[][] data=n.getRawData();
			int nr=data.length;
			int nc=data[0].length;
			
			for (int i=0;i<nr;i++){
				for (int j=0;j<nc;j++){
					if (data[i][j]<0){
						return FileTypeEnum.FILETYPE_NOVALID;
					}					
				}
			}
			*/
			if (fileDimensions!=null){
				fileDimensions.cols=n.getNumCols();
				fileDimensions.rows=n.getNumRows();
			}
			if (fileDimensions.cols>1){
				fRet=FileTypeEnum.FILETYPE_TABLE;
			}else if (fileDimensions.cols==1){
				fRet=FileTypeEnum.FILETYPE_COUNT;
			}else{
				fRet=FileTypeEnum.FILETYPE_NOVALID;
			}			
		}
		
		return fRet;
	}
	
	public FileTypeEnum getUploadedFileType(String id){		
		FileHolder fileHolder=this.filesHash.get(id);
		if (fileHolder!=null){
			return fileHolder.getFileType();
		}
		return FileTypeEnum.FILETYPE_NOVALID;
	}
			
/*
 * 	./apt-probeset-summarize -a rma-sketch -a plier-mm-sketch -d ../celfiles/ATH1-121501.CDF  -o /tmp/celout/123456 ../celfiles/GSM237280.CEL ../celfiles/GSM237282.CEL 
 */
	public String process(String id, String cdfFile, StringBuilder aptOutput) {
		String summaryfile=null;
		FileHolder fileHolder=this.filesHash.get(id);
		if (fileHolder!=null){		
			if (fileHolder.fileType!=FileTypeEnum.FILETYPE_CEL){
				aptOutput.append("File of type " + fileHolder.fileType);
				return fileHolder.getFileNames().get(0);
			}
			if (cdfFile==null) return null;
			
			String cmdLine=affymetrixPowerToolsDir+"/"+aptcmd+" -a rma-sketch -d "+this.affymetrixCDFDir+"/"+cdfFile+" -o "+filesPath+"/"+id;
			Iterator<String> iter=fileHolder.getFileNames().iterator();
			while(iter.hasNext()){
				cmdLine+=" "+iter.next();
			}			
			logger.info("Executing:\n"+cmdLine);
			try {
				Process child = Runtime.getRuntime().exec(cmdLine);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));

				String s = null;
				aptOutput.append(cmdLine).append("\n");
				while ((s = stdInput.readLine()) != null) {
					aptOutput.append(s).append("\n");
				}

				while ((s = stdError.readLine()) != null) {
					aptOutput.append(s).append("\n");
				}
				logger.info(aptOutput.toString());

				child.waitFor();
				summaryfile = this.filesPath + "/" + id + "/rma-sketch.summary.txt";

				if (!new File(summaryfile).exists()) {
					logger.warn("File NOT FOUND: " + summaryfile);
					File f=new File(this.filesPath + "/" + id);
					File[] files=f.listFiles();
					if (files == null) return null;
					aptOutput.append("Files in dir: " + f.getAbsolutePath() + "\n");
					for (File file : files) {
						aptOutput.append(file.getAbsolutePath() + "\n");
					}
					return null;
				}
				logger.info("Created file: " + summaryfile);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
			
			return summaryfile;
		}
		return summaryfile;
	}
	
	public List<String> getAllFileNames(String id){		
		FileHolder fileHolder=this.filesHash.get(id);
		if (fileHolder!=null){					
			return fileHolder.getFileNames();
			
		}
		return null;
	}
	
	public boolean hasFilesForId(String id){
		FileHolder fileHolder=this.filesHash.get(id);
		if (fileHolder!=null){
			return fileHolder.getNumFiles()>0;
		}
		return false;
	}
	
	public boolean removeSingleFile(String id, String filename){
		boolean bRet=false;
		FileHolder fileHolder=this.filesHash.get(id);
		
		if (fileHolder!=null){
			String completeFileName=filesPath+ id+"/"+filename;
			if (fileHolder.removeFileName(completeFileName)){			
				File f;
				f=new File(completeFileName);
				if (f.exists()){
					logger.info("RemoveSingleFile file: "+completeFileName);
					bRet=f.delete();
				}
			}
			if (fileHolder.getNumFiles()==0){
				//this.filesHash.remove(id);
				this.removeFiles(id);
			}
		}
		return bRet;
	}
	
	public void removeFiles(String id){
		FileHolder fileHolder=this.filesHash.remove(id);
		if (fileHolder!=null){	
			/*
			Iterator<String> iter=fileHolder.getFileNames();
			File f;
			while(iter.hasNext()){
				f=new File(iter.next());
				if (f.exists()) f.delete();
			}
			
			fileHolder.removeAllFiles();
			*/
			File f=new File(this.filesPath+"/"+id);			
			removeRecursively(f);
			if (f.exists()) f.delete();
			
			this.filesHash.remove(id);
		}
	}
	
}
