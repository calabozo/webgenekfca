package es.uc3m.tsc.gene;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.kfca.explore.KFCAExplore;
import es.uc3m.tsc.kfca.explore.KFCAExploreSimple64bits;
import es.uc3m.tsc.kfca.explore.KFCAResults;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.math.GeneFunctions;
import es.uc3m.tsc.math.GeneFunctions.ProcessedDataMatrix;
import es.uc3m.tsc.math.MatrixInfo;
import es.uc3m.tsc.threads.ThreadProcessor;
import es.uc3m.tsc.threads.ThreadSavePreprocessorInBackground;

@RooJavaBean
@RooJpaActiveRecord
public class Preprocessor {
    
    @Transient
	private static Preprocessor preprocessorCache;
	    
    private String name;
    
    @Enumerated
    private PreprocessorEnum preprocessorType;

    @OneToOne
    @Basic(fetch = FetchType.LAZY)
    private DataMatrix dataMatrix;

    private long maxPhiToExplore;
    
    //Length inferior or equal to dataMatrix.colNames
    @Column(columnDefinition = "LONGBLOB")
    private String[] groupName;
    
    //Equal length to dataMatrix.colNames. Each item points to the corresponding groupName
    @Column(columnDefinition = "LONGBLOB")
    private Integer[] groupId;
    
    @Column(columnDefinition = "LONGBLOB")
    private String[] rowNames;
    
    @Size(max = 100000000)
    @Lob
    private double[][] groupMatrix;
    
    @Enumerated
    GeneExpType geneExpressionType;

    @Transient
    private int algorithm;
        
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
	            org.hibernate.annotations.CascadeType.DELETE,
	            org.hibernate.annotations.CascadeType.MERGE,
	            org.hibernate.annotations.CascadeType.PERSIST})
    @Basic(fetch = FetchType.LAZY)
    private KFCAResults kfcaResults;
    
    @Transient 
    private Exception resultException;
    
    @DateTimeFormat(style="M-")
    private Date creationDate;
    
    @Transient
    @Resource 
    GeneInfo geneInfo;
    
    @Transient
    Logger logger;

    public Preprocessor() {
        this.algorithm = 1;
        this.resultException=null;
        this.kfcaResults=null;
        this.creationDate=new Date();
        this.geneExpressionType=GeneExpType.PROBESET;
        this.logger = Logger.getLogger("es.uc3m.tsc.gene");
    }

    private ThreadProcessor processorFactory() {
        ThreadProcessor processor;
        this.resultException=null;
        switch(algorithm) {
            case 0:
                processor = new KFCAExplore(this);
                break;
            case 1:
                processor = new KFCAExploreSimple64bits(this);
                break;
            
                
            default:
                processor = new ThreadProcessor(this);
        }
        return processor;
    }
    
    public void setGeneExpressionType(GeneExpType geneExpressionType) {
        this.geneExpressionType = geneExpressionType;
    }

    public ThreadProcessor startProcessor() {
        ThreadProcessor processor = ThreadProcessor.getRunningThreadProcessor(this.getId());
        if (processor == null) {
            processor = this.processorFactory();
            processor.addToExecutor();            
        }
        return processor;
    }

    public ThreadProcessor forceStartProcessor() {
        ThreadProcessor processor = processorFactory();
        processor.addToExecutor();
        return processor;
    }

    public boolean hasRunningProcess() {
        ThreadProcessor processor = ThreadProcessor.getRunningThreadProcessor(this.getId());
        return processor != null && processor.getStopDate() == null;
    }

    public ThreadProcessor getProcessor() {
        return ThreadProcessor.getRunningThreadProcessor(this.getId());
    }
    
    public void setKfcaResults(KFCAResults kfcaResults) {    	
    	KFCAResults origResult=this.kfcaResults;
        this.kfcaResults = kfcaResults;
        this.merge();
        if (origResult!=null){        	
        	origResult.remove();
        }
        Preprocessor.reloadCachePreprocessor(this.getId());
    }

    public Long savePreprocessorThread(){
    	ThreadSavePreprocessorInBackground gt=new ThreadSavePreprocessorInBackground(this);
    	gt.addToExecutor();    	
        return gt.getIdThread();    	
    }
    /*
     * Executes the preprocessor. It normalizes and groyp by cols and rows 
     */
    public void execute(){
    	double[][] groupedMatrid=this.createGroupData(); //Group by cols
    	double[][] genExp=calculateGeneExpression(groupedMatrid); //Group by rows
    	
    	//Normalize by cols
    	ProcessedDataMatrix pm=GeneFunctions.calcPreprocessor(this.preprocessorType,genExp );
    	this.groupMatrix=pm.data;        
    }
       
    private double[][] createGroupData(){
    	double[][] rawData=this.dataMatrix.getRawData();
    	double[][] tmpData;
    	int nr=this.getDataMatrix().getNumRows();    	
    	int nc;
    	if (this.groupName!=null){
    		nc=this.groupName.length;
    	}else{
    		nc=this.getDataMatrix().getNumCols();
    		this.groupName=this.dataMatrix.getColNames();
    	}
    	
    	if (this.groupId!=null){
    		tmpData=new double[nr][nc];
    		for (int i=0;i<nr;i++){    		
    			GeneFunctions.calcPreprocessor(this.preprocessorType, rawData[i], this.groupId, tmpData[i]);
    		}
    	}else{
    		tmpData=rawData;
    	}
    	return tmpData;
    }
    
    private double[][] calculateGeneExpression(double[][] probeSetData){
    	ThreadSavePreprocessorInBackground thProcessor=ThreadSavePreprocessorInBackground.getRunningThreadThreadSavePreprocessorInBackground(this.getId());
		if (GeneExpType.PROBESET==this.geneExpressionType || GeneExpType.GEXP_UNKNOWN==this.geneExpressionType || this.dataMatrix.getMicroArrayType().isRNASeq()){ 		
			this.rowNames=this.getDataMatrix().getRowNames();
			if (this.dataMatrix.getMicroArrayType().isRNASeq()){
				this.geneExpressionType=GeneExpType.GEXP_UNKNOWN;
			}
			if (thProcessor!=null) thProcessor.setRatioDone(0.99);
			return probeSetData;
		}else{//If we select to use genes instead of probesets
			//List<String> copyRowNames=Arrays.asList(this.preprocessor.getDataMatrix().getRowNames());
			String[] copyRowNames=Arrays.copyOf(this.getDataMatrix().getRowNames(),this.getDataMatrix().getRowNames().length);
			if (thProcessor!=null) thProcessor.setRatioDone(0.0);
			
			double numRows=(double)copyRowNames.length;
			
			//This hashmap will sort the row indices of the original matrix to a gene name.
			HashMap<String,int[]> probesetIndex=new HashMap<String,int[]>();
			DataTypeEnum microarrayType=this.getDataMatrix().getMicroArrayType();
			for (int i=0;i<copyRowNames.length;i++){
			//Iterator<String> iterRowNames=copyRowNames.iterator();
			//while (iterRowNames.hasNext()){
				//i++;
				//String rowName=iterRowNames.next();
				String rowName=copyRowNames[i];
				if (rowName!=null){					
					List<String> gds=geneInfo.getProbesetsSimilarGeneFromProbesetID(microarrayType,rowName);
										
					String geneNameString=gds.remove(gds.size()-1);
					
					
					if (probesetIndex.containsKey(geneNameString)){
						logger.warn("Gene name "+geneNameString+" was already found");
						continue;
					}
					
					int[] idxProbes=new int[gds.size()];
					int k=0;
					
					Iterator<String> gIter=gds.iterator();
					while(gIter.hasNext()){
						
						//GeneDescription g=gIter.next();
						String probeSetID=gIter.next();
						
						//int idx=copyRowNames.indexOf(g.probeSetID);
						//if (idx>=0){
						//	idxProbes[k++]=idx;
						//	copyRowNames.set(idx, null);							
						//}
						
						for (int j=i;j<copyRowNames.length;j++){
							if (copyRowNames[j]!=null && probeSetID.equals(copyRowNames[j])){
								idxProbes[k++]=j;
								copyRowNames[j]=null;
								break;
							}
						}
					}
					
					if (k<gds.size()){
						logger.warn("Gene name "+geneNameString+" supposed to have "+gds.size()+" probesets, but "+k+" were found");
						idxProbes=Arrays.copyOf(idxProbes, k);
					}
					
					if (i%1000==0){
						logger.info("Gene name "+geneNameString+ " size "+probesetIndex.size()+" max "+copyRowNames.length+" i:"+i);
					}
					if (thProcessor!=null) thProcessor.setRatioDone((double)i/numRows);
					probesetIndex.put(geneNameString, idxProbes);
				}
			}

			 
			logger.info("Total genes: "+probesetIndex.size()+" from probesets: "+copyRowNames.length);
			Set<String> setGeneNames=probesetIndex.keySet();
			String[] geneRowNames=setGeneNames.toArray(new String[setGeneNames.size()]);
			
			int nc=probeSetData[0].length;
			
			//Calculate the gene expression matrix based on the probesets			
			double [][] geneExpressionData=new double[probesetIndex.size()][nc];
			
			Iterator<String> iterGeneName=probesetIndex.keySet().iterator();
			int i=0;
			int j=0;
			while(iterGeneName.hasNext()){
				int[] idxProbes=probesetIndex.get(iterGeneName.next());
				double[] row=new double[nc];
				geneExpressionData[i++]=row;
				//The use or branch predictor makes this switch very fast
				// http://en.wikipedia.org/wiki/Branch_predictor
				switch (this.geneExpressionType){
				case GEXP_AVERAGE:
				{
					Arrays.fill(row, 0);
					for (j=0;j<nc;j++){
						for (int idx:idxProbes){
							row[j]+=probeSetData[idx][j];
						}
						row[j]=row[j]/idxProbes.length;
					}
					break;
				}
				case GEXP_CORRELATION:
				{
					double[] sumCorr=new double[idxProbes.length];
					double[][] corr=new double[idxProbes.length][idxProbes.length];
					double mxSumCorr;
					int idxMaxSumCorr;
					
					double[] mean=new double[idxProbes.length];
					double[] std=new double[idxProbes.length];
					
					Arrays.fill(mean, 0);					
					Arrays.fill(std, 0);					
					Arrays.fill(sumCorr, 0);					
					
					for (int k=0;k<idxProbes.length;k++){
						for (j=0;j<nc;j++){													
							mean[k]+=probeSetData[idxProbes[k]][j]/nc;
						}
						corr[k]=new double[idxProbes.length];
						Arrays.fill(corr[k], 0);
					}	
					for (int k=0;k<idxProbes.length;k++){	
						for (j=0;j<nc;j++){									
							double v=(probeSetData[idxProbes[k]][j]-mean[k]);
							std[k]+=v*v;
						}
						std[k]=Math.sqrt(std[k]/nc);
					}
					
					for (int k1=0;k1<idxProbes.length;k1++){	
						for (int k2=0;k2<idxProbes.length;k2++){	
							for (j=0;j<nc;j++){									
								double c1=(probeSetData[idxProbes[k1]][j]-mean[k1]);
								double c2=(probeSetData[idxProbes[k2]][j]-mean[k2]);
								corr[k1][k2]+=c1*c2/(std[k1]*std[k2])/nc;
							}
						}
					}
					for (int k1=0;k1<idxProbes.length;k1++){	
						for (int k2=0;k2<idxProbes.length;k2++){	
							sumCorr[k1]+=corr[k1][k2];
						}
					}
					
					mxSumCorr=sumCorr[0];
					idxMaxSumCorr=0;
					for (int k=1;k<idxProbes.length;k++){
						if (mxSumCorr<sumCorr[k]){
							mxSumCorr=sumCorr[k];
							idxMaxSumCorr=k;
						}
					}
					for (j=0;j<nc;j++){
						row[j]=probeSetData[idxProbes[idxMaxSumCorr]][j];
					}	
					
					break;
				}
				case GEXP_ENTROPY:{
					double[] entropy=new double[idxProbes.length];
					double[] sumrow=new double[idxProbes.length];
					double mxEntropy;
					int idxMaxEntropy;
					Arrays.fill(entropy, 0);					
					Arrays.fill(sumrow, 0);
					for (j=0;j<nc;j++){
						for (int k=0;k<idxProbes.length;k++){
							sumrow[k]+=probeSetData[idxProbes[k]][j];
						}
					}
					for (j=0;j<nc;j++){
						for (int k=0;k<idxProbes.length;k++){
							double p=probeSetData[idxProbes[k]][j]/sumrow[k];
							entropy[k]-=Math.log(p)/Math.log(2)*p;
						}
					}
					mxEntropy=entropy[0];
					idxMaxEntropy=0;
					for (int k=1;k<idxProbes.length;k++){
						if (mxEntropy<entropy[k]){
							mxEntropy=entropy[k];
							idxMaxEntropy=k;
						}
					}
					for (j=0;j<nc;j++){
						row[j]=probeSetData[idxProbes[idxMaxEntropy]][j];
					}	
					break;
				}
				case GEXP_MEAN:
				{
					double[] mean=new double[idxProbes.length];
					double mxMean;
					int idxMaxMean;
					Arrays.fill(mean, 0);					
					
					for (j=0;j<nc;j++){
						for (int k=0;k<idxProbes.length;k++){							
							mean[k]+=probeSetData[idxProbes[k]][j]/nc;
						}
					}
					mxMean=mean[0];
					idxMaxMean=0;
					for (int k=1;k<idxProbes.length;k++){
						if (mxMean<mean[k]){
							mxMean=mean[k];
							idxMaxMean=k;
						}
					}
					for (j=0;j<nc;j++){
						row[j]=probeSetData[idxProbes[idxMaxMean]][j];
					}
					
					break;
				}
				case GEXP_RANDOM:
				{
					int idx=(int)Math.floor(Math.random()*idxProbes.length);
					for (j=0;j<nc;j++){
						row[j]=probeSetData[idxProbes[idx]][j];
					}					
					break;
				}
				case GEXP_SUMMARY:
				{
					Arrays.fill(row, 0);
					for (j=0;j<nc;j++){
						for (int idx:idxProbes){
							row[j]+=probeSetData[idx][j];
						}						
					}
					break;
				}
				case GEXP_VARIANCE:
				{
					double[] mean=new double[idxProbes.length];
					double[] var=new double[idxProbes.length];
					
					double mxVar;
					int idxMaxVar;
					Arrays.fill(mean, 0);					
					Arrays.fill(var, 0);					
					
					for (j=0;j<nc;j++){
						for (int k=0;k<idxProbes.length;k++){							
							mean[k]+=probeSetData[idxProbes[k]][j]/nc;
						}
					}					
					for (j=0;j<nc;j++){
						for (int k=0;k<idxProbes.length;k++){				
							double v=(probeSetData[idxProbes[k]][j]-mean[k]);
							var[k]+=v*v/nc;
						}
					}
					mxVar=var[0];
					idxMaxVar=0;
					for (int k=1;k<idxProbes.length;k++){
						if (mxVar<var[k]){
							mxVar=var[k];
							idxMaxVar=k;
						}
					}
					for (j=0;j<nc;j++){
						row[j]=probeSetData[idxProbes[idxMaxVar]][j];
					}
					
					
					break;
				}
				default:
					break;
				}
			}					
			this.rowNames=geneRowNames;
			return geneExpressionData;
		}
	}    
    
    public String getCanvas(){
    	String[] toolTips=new String[this.groupName.length];
    	String[] colNames=this.dataMatrix.getColNames();
    	for (int i=0;i<toolTips.length;i++){
    		for (int j=0;j<this.groupId.length;j++){
    			if (this.groupId[j]==i){
    				if (toolTips[i]==null)
    					toolTips[i]=colNames[j];
    				else
    					toolTips[i]+=","+colNames[j];
    			}
    		}
    	}
    	return ArrayUtils.getCanvas(this.groupMatrix, this.groupName,toolTips);    
    }
    
    public String getSize(){
    	return this.groupMatrix.length+"x"+this.groupName.length;
    }
    
    
    public static synchronized Preprocessor findPreprocessor(Long id) {
    	if (id == null) return null;
    	if (preprocessorCache!=null){
    		if (id.equals(preprocessorCache.getId())){
    			return preprocessorCache;
    		}
    	}    	
    	preprocessorCache=Preprocessor.entityManager().find(Preprocessor.class, id);    		    	
        return preprocessorCache;
    }
    
    public static synchronized Preprocessor reloadCachePreprocessor(Long id) {
    	if (id == null) return null;
    	preprocessorCache=Preprocessor.entityManager().find(Preprocessor.class, id);    		    	
        return preprocessorCache;
    }
    
    public static synchronized KFCAResults findKFCAResults(Long id) {
    	if (id == null) return null;
    	if (preprocessorCache==null) return null;
    	
    	if (preprocessorCache.getKfcaResults()!=null && id.equals(preprocessorCache.getKfcaResults().getId())){
    		return preprocessorCache.getKfcaResults();
    	}
    	return null;    	
    }
    
    public String toString() {
        return this.name;
    }
    
    public MatrixInfo getHistogram() {                       
        int numBins = 100;
        GeneFunctions.ProcessedDataMatrix pm = new GeneFunctions().new ProcessedDataMatrix();
        pm.data=this.groupMatrix;
        int nc=this.groupMatrix[0].length;
        int nr=this.groupMatrix.length;
        double max=-Double.MAX_VALUE;
        double min=Double.MAX_VALUE;
        for (int i=0;i<nr;i++){
        	for (int j=0;j<nc;j++){
        		if (this.groupMatrix[i][j]<min) min=this.groupMatrix[i][j];
        		if (this.groupMatrix[i][j]>max) max=this.groupMatrix[i][j];
        	}
        }
        pm.min=min;
        pm.max=max;
        MatrixInfo mi = new MatrixInfo(pm,numBins);        
        return mi;
    }
    
}
