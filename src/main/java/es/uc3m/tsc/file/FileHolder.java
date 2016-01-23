package es.uc3m.tsc.file;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

class FileHolder{
	Date startDate;
	List<String> fileNames;
	String id;
	FileTypeEnum fileType;
	FileHolder(String id){
		this.id=id;
		this.startDate=new Date();
		this.fileNames=new ArrayList<String>();			
	}
	public void setFileType(FileTypeEnum fileType){
		this.fileType=fileType;
	}
	public FileTypeEnum getFileType(){
		return this.fileType;
	}
	
	public boolean containsFile(String completeFilename){
		return this.fileNames.contains(completeFilename);
	}
	public boolean addFileName(String completeFilename){
		if (this.fileNames.contains(completeFilename)) return false;
		this.fileNames.add(completeFilename);
		return true;
	}
	public boolean removeFileName(String completeFilename){
		return this.fileNames.remove(completeFilename);			
	}
	public void removeAllFiles(){
		this.fileNames.clear();			
	}
	public List<String> getFileNames(){
		return this.fileNames;
	}
	public int getNumFiles(){
		return this.fileNames.size();
	}
}