package es.uc3m.tsc.gene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.util.StringUtils;

import es.uc3m.tsc.file.FileTypeEnum;
import es.uc3m.tsc.file.ProcessUploadedFiles;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.math.GeneFunctions;
import es.uc3m.tsc.math.MatrixInfo;
import es.uc3m.tsc.threads.ThreadLoadData;

@RooJavaBean
@RooJpaActiveRecord
public class DataMatrix {

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
	            org.hibernate.annotations.CascadeType.DELETE,
	            org.hibernate.annotations.CascadeType.MERGE,
	            org.hibernate.annotations.CascadeType.PERSIST})
	@Basic(fetch = FetchType.LAZY)
    private Set<Preprocessor> preprocessor = new HashSet<Preprocessor>();

    @NotNull
    private String name;

    @Size(max = 2000)
    private String description;

    @Column(columnDefinition = "LONGBLOB")
    private String[] colNames;

    @Column(columnDefinition = "LONGBLOB")
    private String[] rowNames;

    @Size(max = 100000000)
    @Lob
    private double[][] rawData;
        
    @Transient
	@Resource
    private ProcessUploadedFiles processFiles;
        
    @Transient
    private String celIDFiles;
    

    @Enumerated
    private DataTypeEnum microArrayType;        

    public DataMatrix(){
    	this.microArrayType=DataTypeEnum.GENERIC;
    }
    
    public void launchThreadLoadDataFromFileName() {
    	ThreadLoadData t=new ThreadLoadData(this);    	
    	t.addToExecutor();    	
    }
    
    public static int isCompletedDataMatrix(Long id){
    	if (ThreadLoadData.getRunningThreadLoadData(id)!=null){
    		return 0;
    	}else if (DataMatrix.findDataMatrix(id)!=null){
    		return 1;
    	}else{
    		return -1;
    	}
    }
    

    public StringBuilder loadDataFromFileName() {   
    	FileTypeEnum fileType=this.processFiles.getUploadedFileType(this.celIDFiles);
    	String fileName;
    	List<String> fileNames;
    	boolean ret=false;
    	StringBuilder aptOutput = new StringBuilder();
    	switch(fileType){
    		case FILETYPE_TABLE:
    			this.colNames=null;
    			fileNames=this.processFiles.getAllFileNames(this.celIDFiles);
    			ret=this.loadDataFromFileName(fileNames.get(0));		
    			break;
    		case FILETYPE_CEL:
    			//Create the apt-summary
    			fileName=this.processFiles.process(this.celIDFiles, this.microArrayType.getCDF(),aptOutput);
    	    	if (fileName==null) return aptOutput;
    	    	ret=this.loadDataFromFileName(fileName);
    			break;
    		case FILETYPE_COUNT:
    			fileNames=this.processFiles.getAllFileNames(this.celIDFiles);
    			ret=this.loadDataFromFileNames(fileNames);
    			break;
    		default:
    			break;
    	}
    	this.processFiles.removeFiles(this.celIDFiles);
    	if (ret == false)
			this.rawData = null;
		return aptOutput;
    }

    /*
     * Loads a matrix created by apt-tools
     */
    public boolean loadDataFromFileName(String fileName) {
		Logger logger = Logger.getLogger(this.getClass());
        try {
        	
        	logger.info("Reading "+fileName);
        	
            this.rawData = null;
            File f = new File(fileName);                   
            InputStream isr=new FileInputStream(f);                    	
        	
            ArrayList<String> listRowNames = new ArrayList<String>();
            ArrayList<double[]> listRowArrays = new ArrayList<double[]>();
            BufferedReader bf = new BufferedReader(new InputStreamReader(isr));
            boolean readFirstLine = false;
            boolean skipRow=false;
            String ln;
            int numCols = -1;
            String regexcols="[ \t]+";
            
            while ((ln = bf.readLine()) != null) {
            	ln=ln.trim();
            	if (ln.startsWith("#")) continue; //Comments start by #
            	
            	//Read the header line
                String[] lnArray = ln.split(regexcols);
                if (!readFirstLine && lnArray.length > 0) {
                	readFirstLine = true;
                	
                	
                	//Choose the propper separator
                	String[] lnArray1 = ln.split(" ");                	 
                	String[] lnArray2 = ln.split("\t");  
                	String[] lnArray3 = ln.split(";");
                	String[] lnArray4 = ln.split(",");  
                	if (lnArray1.length>lnArray2.length && lnArray1.length>lnArray3.length && lnArray1.length>lnArray4.length ){
                		lnArray=lnArray1;
                		regexcols=" ";
                	}else if (lnArray2.length>lnArray1.length && lnArray2.length>lnArray3.length && lnArray2.length>lnArray4.length ){
                		lnArray=lnArray2;
                		regexcols="\t";
                	}else if (lnArray3.length>lnArray4.length){
                		lnArray=lnArray3;
                		regexcols=";";
                	}else{
                		lnArray=lnArray4;
                		regexcols=",";
                	}
                	
                	
                	if (this.colNames==null){	                    
	                    String[] colNames = new String[lnArray.length];
	                    for (int i = 0; i < lnArray.length; i++) {
	                        colNames[i] = StringUtils.replace(lnArray[i], "\"", "");
	                    }
	                    
	                    this.setColNames(colNames);
	                    numCols = colNames.length;
	                                        
	                    lnArray = bf.readLine().split(regexcols);
	                    if (lnArray.length == numCols){
	                    	numCols--;
	                    	colNames = new String[numCols];
	                        for (int i = 0; i < numCols; i++) {	                        	
	                            colNames[i] = this.colNames[i+1];
	                        }
	                        this.setColNames(colNames);
	                	}
                	}else{
                		numCols=this.colNames.length;
                		lnArray = bf.readLine().split(regexcols);
                	}
                } 
                
                if (lnArray.length == numCols + 1) {                	                    
                    double[] nums = new double[numCols];
                    double val;                    
                    skipRow=false;
                    for (int i = 0; i < numCols; i++) {
                    	try{
                    		val = new Double(lnArray[i + 1]).doubleValue();
                    		nums[i] = val;
                    	}catch(Exception e){
                    		skipRow=true;
                    	}                        
                    }
                    if (skipRow) continue;
                    
                    listRowArrays.add(nums);
                    listRowNames.add(StringUtils.replace(lnArray[0], "\"", ""));
                } else {
                	bf.close();
                	if (this.loadSimpleMatrixFromFileName(fileName)) return true;
                	
                	logger.error("Error parsing "+fileName+" Expected cols "+numCols+" found cols: "+(lnArray.length-1)+" for separator '"+regexcols+"'\n"+ln);
                	return false;
                }
            }
            this.rawData = new double[listRowArrays.size()][];
            Iterator<double[]> iter = listRowArrays.iterator();
            int k = 0;
            while (iter.hasNext()) {
                this.rawData[k++] = iter.next();
            }
            bf.close();
            this.rowNames = new String[listRowNames.size()];
            this.rowNames = listRowNames.toArray(this.rowNames);       
            
            Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?([e,E]?-?\\d+)?");
            
            for (String name:this.rowNames){
            	if (!doublePattern.matcher(name).matches())	
            		return true;
            }
            for (String name:this.colNames){
            	if (!doublePattern.matcher(name).matches())	
            		return true;
            }
            //The columns and rows are all number
            return this.loadSimpleMatrixFromFileName(fileName);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
			logger.error(e.getMessage());
			logger.error(e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
			logger.error(e.getMessage());
			logger.error(e);
            return false;
        }
    }
    
    private boolean loadSimpleMatrixFromFileName(String fileName) {
		Logger logger = Logger.getLogger(this.getClass());
        try {
        	
        	logger.info("Reading "+fileName);
        	
            this.rawData = null;
            File f = new File(fileName);                   
            InputStream isr=new FileInputStream(f);                    	
        	            
            ArrayList<double[]> listRowArrays = new ArrayList<double[]>();
            BufferedReader bf = new BufferedReader(new InputStreamReader(isr));
            boolean readFirstLine = false;
            boolean skipRow=false;
            String ln;
            int numCols = -1;
            String regexcols="[ \t]+";
            
            while ((ln = bf.readLine()) != null) {
            	ln=ln.trim();
            	if (ln.startsWith("#")) continue; //Comments start by #
            	if (ln.length()==0) continue; //Omit empty lines
            	
            	//Read the header line
                String[] lnArray = ln.split(regexcols);
                if (!readFirstLine) {
                	readFirstLine = true;
                	                	
                	//Choose the propper separator
                	String[] lnArray1 = ln.split(" ");                	 
                	String[] lnArray2 = ln.split("\t");  
                	String[] lnArray3 = ln.split(";");  
                	String[] lnArray4 = ln.split(",");  
                	if (lnArray1.length>lnArray2.length && lnArray1.length>lnArray3.length && lnArray1.length>lnArray4.length ){
                		lnArray=lnArray1;
                		regexcols=" ";
                	}else if (lnArray2.length>lnArray1.length && lnArray2.length>lnArray3.length && lnArray2.length>lnArray4.length ){
                		lnArray=lnArray2;
                		regexcols="\t";
                	}else if (lnArray3.length>lnArray4.length){
                		lnArray=lnArray3;
                		regexcols=";";
                	}else{
                		lnArray=lnArray4;
                		regexcols=",";
                	}
                	                
                	numCols=lnArray.length;
                } 
                
                if (lnArray.length == numCols) {                	                    
                    double[] nums = new double[numCols];
                    double val;                    
                    skipRow=false;
                    for (int i = 0; i < numCols; i++) {
                    	try{
                    		val = new Double(lnArray[i]).doubleValue();
                    		nums[i] = val;
                    	}catch(Exception e){
                    		skipRow=true;
                    	}                        
                    }
                    if (skipRow) continue;
                    
                    listRowArrays.add(nums);                    
                } else {
                	bf.close();
                	logger.error("Error parsing "+fileName+" Expected cols "+numCols+" found cols: "+(lnArray.length)+"\n"+ln);
                	return false;
                }
            }
            this.rawData = new double[listRowArrays.size()][];
            Iterator<double[]> iter = listRowArrays.iterator();
            int k = 0;
            while (iter.hasNext()) {
                this.rawData[k++] = iter.next();
            }
            bf.close();
            this.rowNames = new String[listRowArrays.size()];
            for (int i=0;i<this.rowNames.length;i++){
            	this.rowNames[i]="g"+i;
            }
            this.colNames = new String[numCols];
            for (int i=0;i<numCols;i++){
            	this.colNames[i]="m"+i;
            }
                        
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    
    /*
     * Loads count matrix
     */
    public boolean loadDataFromFileNames(List<String> fileNames) {
        try {
        	Logger logger = Logger.getLogger("es.uc3m.tsc.gene");        	      	        	
        	int ncols=fileNames.size();
        	HashMap<String,double[]> hashCount=new HashMap<String,double[]> ();
        	this.colNames=new String[ncols];
        	int i;
        	for (i=0;i<ncols;i++){
        		String fileName=fileNames.get(i);
        		
        		int lst=fileName.lastIndexOf("/")+1;
        		this.colNames[i]=fileName.substring(lst);
        		
        		logger.info("Reading "+fileName);
        	
        	
        		this.rawData = null;
        		File f = new File(fileName);                   
        		InputStream isr=new FileInputStream(f);                    	        	        		
        		
        		BufferedReader bf = new BufferedReader(new InputStreamReader(isr));
            
        		String ln;
            
        		String regexcols="[ \t]+";
            
        		while ((ln = bf.readLine()) != null) {
        			ln=ln.trim();
        			if (ln.startsWith("#")) continue; //Comments start by #
            	
            		String[] lnArray = ln.split(regexcols);
                
        			String geneName=lnArray[0];
        			String counts=lnArray[1];
                	
        			double[] row=hashCount.get(geneName);
        			if (row==null){
        				row=new double[ncols]; 
        				Arrays.fill(row, -1);
        				hashCount.put(geneName, row);
        			}
        			row[i]=new Double(counts);
        			if (row[i]==0) row[i]=0.1; //Avoid log(0)
        		}
        		bf.close();
        	}
        	ArrayList<double[]> listRowArrays = new ArrayList<double[]>();
        	ArrayList<String> listRowNames = new ArrayList<String>();
        	for (String gene:hashCount.keySet()){
        		double[] row=hashCount.get(gene);
        		boolean completeGene=true;
        		for (i=0;i<ncols;i++){
        			if (row[i]<0){
        				completeGene=false;
        				break;
        			}
        		}
        		if (completeGene){
        			listRowArrays.add(row);
        			listRowNames.add(gene);
        		}        		
        	}
        	
        	this.rawData = new double[listRowArrays.size()][];
            Iterator<double[]> iter = listRowArrays.iterator();
            int k = 0;
            while (iter.hasNext()) {
                this.rawData[k++] = iter.next();
            }            
            this.rowNames = new String[listRowNames.size()];
            this.rowNames = listRowNames.toArray(this.rowNames);       
            
        	
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void copyDataFromMatrix(es.uc3m.tsc.gene.DataMatrix dataMatrix) {
        this.setName(dataMatrix.getName());
        this.setDescription(dataMatrix.getDescription());
        this.setColNames(dataMatrix.getColNames());
        this.setPreprocessor(dataMatrix.getPreprocessor());
    }

    public String getRawDataSummary() {
        return this.getNumRows() + "x" + this.getNumCols();
    }

    public String toString() {
        return this.getName() + "(" + this.getRawDataSummary() + ")";
    }

    public void addPreprocessor(Preprocessor p) {
        this.preprocessor.add(p);
        this.merge();        
    }
    
    public Preprocessor getNewPreprocessor() {
        Preprocessor p = new Preprocessor();
        p.setDataMatrix(this);
        p.setName("Preprocessor "+(this.preprocessor.size()+1));        
        p.setPreprocessorType(PreprocessorEnum.LOGPREPROARITMEAN);
        MatrixInfo m = this.getHistogram(PreprocessorEnum.LOGPREPROARITMEAN);
        p.setMaxPhiToExplore(250);
        if (this.getMicroArrayType().isRNASeq() || !rowsAreProbablyAffymetrixProbesets()){
        	p.setGeneExpressionType(GeneExpType.GEXP_UNKNOWN);
        }
        //p.persist();
        //this.preprocessor.add(p);
        //this.merge();
        //return p.getId();
        return p;
    }
    
    private boolean rowsAreProbablyAffymetrixProbesets(){
    	for (int i=0;i<100;i++){
    		if (i<rowNames.length){
    			if (rowNames[i].endsWith("_at")) return true;
    		}else{
    			break;
    		}
    	}
    	return false;
    }
    
    public void removePreprocessor(long id){
    	Iterator<Preprocessor> it=this.preprocessor.iterator();
    	while(it.hasNext()){
    		Preprocessor p=it.next();
    		if (p.getId()==id){
    			this.preprocessor.remove(p);    			
    			this.merge();      			   			
    			break;
    		}
    	}
    }
    
    public void removeAllPreprocessors(){
    	this.preprocessor.clear();
    	this.merge();    	
    }

    public int getNumRows() {
        if (this.rawData != null) {
            return this.rawData.length;
        } else {
            return 0;
        }
    }

    public int getNumCols() {
        if (this.rawData != null && this.rawData.length>0) {
            return this.rawData[0].length;
        } else {
            return 0;
        }
    }

    public MatrixInfo getHistogram(PreprocessorEnum preprocessorType) {
        if (preprocessorType == null) return null;                
        int numBins = 100;
        
        GeneFunctions.ProcessedDataMatrix pm = GeneFunctions.calcPreprocessor(preprocessorType, this.rawData);
        MatrixInfo mi = new MatrixInfo(pm,numBins);        
        return mi;
    }
    
    public MatrixInfo getHistogram() {                       
        int numBins = 100;
        GeneFunctions.ProcessedDataMatrix pm = new GeneFunctions().new ProcessedDataMatrix();
        pm.data=this.rawData;
        int nc=this.getNumCols();
        int nr=this.getNumRows();
        double max=-Double.MAX_VALUE;
        double min=Double.MAX_VALUE;
        for (int i=0;i<nr;i++){
        	for (int j=0;j<nc;j++){
        		if (this.rawData[i][j]<min) min=this.rawData[i][j];
        		if (this.rawData[i][j]>max) max=this.rawData[i][j];
        	}
        }
        pm.min=min;
        pm.max=max;
        MatrixInfo mi = new MatrixInfo(pm,numBins);        
        return mi;
    }
    
    public String getCanvas(){
    	return ArrayUtils.getCanvas(this.rawData, this.colNames,null);    
    }
    
    public String getCanvasSmall(){
    	return ArrayUtils.getCanvas(this.rawData, this.colNames,null,100,100);    
    }
    
    public boolean getIsGeneric(){
    	return this.microArrayType.getId()<0;
    }
}
