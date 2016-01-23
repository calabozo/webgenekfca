package es.uc3m.tsc.kfca.explore;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import es.uc3m.tsc.gene.DataMatrix;
import es.uc3m.tsc.gene.DataTypeEnum;
import es.uc3m.tsc.gene.Preprocessor;
import es.uc3m.tsc.general.Constants;
import es.uc3m.tsc.general.Constants.GeneExpType;
import es.uc3m.tsc.genetools.GODescription;
import es.uc3m.tsc.genetools.GOpValue;
import es.uc3m.tsc.genetools.GeneClusterAnalysis;
import es.uc3m.tsc.genetools.GeneDescription;
import es.uc3m.tsc.genetools.GeneInfo;
import es.uc3m.tsc.genetools.GoDomainEnum;
import es.uc3m.tsc.graph.GraphFCA;
import es.uc3m.tsc.kfca.tools.KFCAConceptsFastApproximate;
import es.uc3m.tsc.kfca.tools.KFCAConceptsIntersection;
import es.uc3m.tsc.kfca.tools.KFCAConceptsLite;
import es.uc3m.tsc.math.ArrayUtils;
import es.uc3m.tsc.threads.ThreadCalcHomSep;
import es.uc3m.tsc.threads.ThreadCalcPValue;

@RooJavaBean
@RooJpaActiveRecord
public class KFCAResults {

	@Transient
	private static KFCAResults resultsCache; 
	
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date stopDate;
            
    @Lob
    private double[][] processedMatrix;
    
    @Lob
    private double[][] minPlusNumConcepts;
    
    @Lob
    private double[][] maxPlusNumConcepts;
    
    @Column(columnDefinition = "LONGBLOB")
    private String[] colNames;    
    
    @Column(columnDefinition = "LONGBLOB")
    private String[] rowNames;        
    
    @Enumerated
    private DataTypeEnum microArrayType;
    
    private int explorationType;
    
    //@Lob
    //private KFCAObjectExplored[] objectExplored;
    @Transient
    KFCAObjectInfo objectExplored;
    
    @Transient
    Logger logger;
    /*
    @Transient
    @Autowired
    private ApplicationContext appContext;
    */
    @Transient
    @Resource 
    GeneInfo geneInfo;
    
    @Transient
    GeneClusterAnalysis geneClusterAnalysis;

    private Long preprocessorId;
   
    public KFCAResults(){    	
    	logger=Logger.getLogger("es.uc3m.tsc.kfcatools.KFCAResults");
    	this.objectExplored=new KFCAObjectInfo(this);
    	this.geneClusterAnalysis=new GeneClusterAnalysis(this);
    }
    
    public void setProcessedMatrix(double[][] processedMatrix) {
        this.processedMatrix = processedMatrix;       
        if (this.processedMatrix!=null){
    		this.objectExplored=new KFCAObjectInfo(this.processedMatrix);
        }
    }
    
    public double getMinPhi(){ 	return this.maxPlusNumConcepts[0][0];  }
    public double getMinVarPhi(){ 	return this.minPlusNumConcepts[this.minPlusNumConcepts.length-1][0];  }
    public double getMaxPhi(){ 	return this.maxPlusNumConcepts[this.maxPlusNumConcepts.length-1][0];  }
    public double getMaxVarPhi(){ 	return this.minPlusNumConcepts[0][0];  }
       /*
    public void testFillObjectExplored(){
    	
    	this.objectExplored=new KFCAObjectExplored[rowNames.length];
    	int k=0;
    	for (String rowName:this.rowNames){
    		KFCAObjectExplored obj=new KFCAObjectExplored(rowName);
    		double phi=Math.random()*10;
    		obj.addMaxPlusConcept(-phi, 0L);
    		obj.addMaxPlusConcept(-phi+1, 0L);
    		
    		obj.addMaxPlusConcept(phi, 0L);
    		obj.addMaxPlusConcept(phi-1, 0L);
    		this.objectExplored[k++]=obj;
    		obj.optimizeArray();
    	}
    	
    }
    */
    
    public boolean getIsGeneric(){
    	return this.microArrayType.getId()<0;
    }
    /*
     * Returns true if the exploration is based on gene expression levels, not
     * Returns false if it is based in probeset levels
     */
    public boolean getIsGeneExpression(){
    	return Constants.GeneExpType.getEnum(this.explorationType)!=GeneExpType.PROBESET;
    }
    
    public boolean[][] getMatrixMaxPlus(double phi){    	
    	int nr=processedMatrix.length;
    	int nc=processedMatrix[0].length;
    	double rowR[];
    	boolean[][] IMaxPlus=new boolean[nr][nc];
    	boolean[] rowIMax;
    	
    	for (int i=0;i<nr;i++){
			rowR=processedMatrix[i];
			rowIMax=IMaxPlus[i];    			
			for (int j=0;j<nc;j++){
				rowIMax[j]=rowR[j]<=phi;    				
			}		
		}    	
    	return IMaxPlus;
    }
    
    public boolean[][] getMatrixMinPlus(double varphi){    	
    	int nr=processedMatrix.length;
    	int nc=processedMatrix[0].length;
    	double rowR[];
    	boolean[][] IMinPlus=new boolean[nr][nc];
    	boolean[] rowIMin;
    	
    	for (int i=0;i<nr;i++){
			rowR=processedMatrix[i];
			rowIMin=IMinPlus[i];    			
			for (int j=0;j<nc;j++){
				rowIMin[j]=rowR[j]>=varphi;    				
			}		
		}    	
    	return IMinPlus;
    }
    
    public PipedInputStream getPipedMatrixMaxPlus(double phi) throws IOException{
    	final boolean[][] I=getMatrixMaxPlus(phi);
    	return getPipedMatrixMaxMinPlus(I);
    }
    public PipedInputStream getPipedMatrixMinPlus(double phi) throws IOException{
    	final boolean[][] I=getMatrixMinPlus(phi);
    	return getPipedMatrixMaxMinPlus(I);
    }
    
    private PipedInputStream getPipedMatrixMaxMinPlus(final boolean[][] I) throws IOException{	
    	PipedInputStream pi=new PipedInputStream();
    	final PipedOutputStream po=new PipedOutputStream(pi);
    	
    	final String[] geneNames=this.rowNames;
    	final String[] attrNames=this.colNames;	
    	 new Thread(
    	            new Runnable() {
    	                public void run() {
    	                	try {
	    	                	String row="";
	    	                	for (String attrName : attrNames){
	    	                		row+=attrName+";";
	    	                	}
	    	                	row+="\r\n";
	    	                	po.write(row.getBytes());
	    	                	
	    	                	for(int i=0;i<I.length;i++){
	    	                		boolean[] rowI=I[i];
	    	                		row=geneNames[i]+";";
	    	                		for(int j=0;j<rowI.length;j++){
	    	                			if (rowI[j]){
	    	                				row+="1;";
	    	                			}else{
	    	                				row+="0;";
	    	                			}
	    	                		}
	    	                		row+="\r\n";
	    	                		po.write(row.getBytes());																	
	    	                	}
    	                	
								po.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
					logger.error(e);
							}
    	                }
    	            }).start();        	
    	return pi;
    }
    
    
    public PipedInputStream getPipedMatrixConcept(final boolean max_min_plus, final double phi,final int conceptId) throws IOException{	
    	
    	final boolean[][] I;    	
    	if (max_min_plus){
    		I=getMatrixMaxPlus(phi);
    	}else{
    		I=getMatrixMinPlus(phi);
    	}
    	
    	PipedInputStream pi=new PipedInputStream();
    	final PipedOutputStream po=new PipedOutputStream(pi);
    	
    	final boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;    	
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);    		
    		i=i>>1;
    	}
    	
    	        	
    	final String[] geneNames=this.rowNames;
    	final String[] attrNames=this.colNames;
    	final double[][] data=this.processedMatrix;
    	final DataTypeEnum microarray=this.microArrayType;
    	 new Thread(
    	            new Runnable() {
    	                public void run() {
    	                	try {
    	                		Preprocessor preprocessor=Preprocessor.findPreprocessor(preprocessorId);
    	                		DataMatrix dataMatrix=preprocessor.getDataMatrix();
    	                		double[][] rawData=dataMatrix.getRawData();
    	                		
    	                		List<Double> phiStart=new ArrayList<Double>();
    	                		List<Double> phiEnd=new ArrayList<Double>();
    	                		List<String> rowNames=new ArrayList<String>();
	    	                	String row;
	    	                	if (max_min_plus)
	    	                		row="#MaxPlus\r\n#varphi="+phi+"\r\n";
	    	                	else
	    	                		row="#MinPlus\r\n#phi="+phi+"\r\n";
	    	                	
	    	                	row+="#attributes="+Arrays.toString(attr)+"\r\n";
	    	                	po.write(row.getBytes());
	    	                	po.flush();
	    	                	
	    	                	row="#originalExperimentName=[";
	    	                	Integer[] groupId=preprocessor.getGroupId();
	    	                	//boolean[] originalExperimentUsed=new boolean[groupId.length];	    	                	
	    	                	for (int i=0;i<groupId.length;i++){	    	                		
	    	                		if (attr[groupId[i]]){
	    	                			row+=dataMatrix.getColNames()[i]+";";
	    	                		}
	    	                	}
	    	                	row+="]\r\n";
	    	                	po.write(row.getBytes());
	    	                	po.flush();
	    	                	
	    	                	row="Probe id;";
	    	                	if (microarray.equals(DataTypeEnum.ATH1121501)){
	    	                		row+="AGI;";
	    	                	}else{
	    	                		row+="GeneSymbol;";
	    	                	}
	    	                	for (String attrName : attrNames){
	    	                		row+=attrName+";";
	    	                	}
	    	                	for (String attrName : dataMatrix.getColNames()){
	    	                		row+=attrName+";";
	    	                	}
	    	                	row+="\r\n";
	    	                	po.write(row.getBytes());
	    	                	po.flush();
	    	                	
	    	                	
	    	                	
	    	                	for(int i=0;i<I.length;i++){
	    	                		boolean[] rowI=I[i];
	    	                		if (Arrays.equals(rowI, attr)){
	    	                			row=geneNames[i]+";";
	    	                			if (microarray.equals(DataTypeEnum.ATH1121501)){
	    	    	                		row+=geneInfo.getGeneDescription(microarray,geneNames[i]).strAgiName+";";
	    	    	                	}else{
	    	    	                		row+=geneInfo.getGeneDescription(microarray,geneNames[i]).geneSymbol+";";
	    	    	                	}
	    	                			
	    	                			double[] rowData=data[i];
	    	                			for(int j=0;j<rowData.length;j++){
	    	                				row+=rowData[j]+";";	    	                				
	    	                			}
	    	                			for(int j=0;j<rawData[i].length;j++){
	    	                				row+=rawData[i][j]+";";	    	                				
	    	                			}
	    	                			
	    	                			row+="\r\n";	    	                		
	    	                			//po.write(row.getBytes());
	    	                			if (max_min_plus){
	    	                				double[] p=objectExplored.getMaxMarginPhi(i,conceptId);
	    	                				if (p!=null){
	    	                					phiStart.add(p[0]);
	    	                					phiEnd.add(p[1]);
	    	                					rowNames.add(row);
	    	                				}else{
	    	                					phiStart.add(new Double(0));
	    	                					phiEnd.add(new Double(0));
	    	                					rowNames.add(row);
	    	                				}
	    	                			}else{
	    	                				double[] p=objectExplored.getMinMarginVarphi(i,conceptId);
	    	                				if (p!=null){
	    	                					phiStart.add(-p[1]);    
	    	                					phiEnd.add(-p[0]);
	    	                					rowNames.add(row);
	    	                				}else{
	    	                					phiStart.add(new Double(0));
	    	                					phiEnd.add(new Double(0));
	    	                					rowNames.add(row);
	    	                				}
	    	                			}
	    	                		}
	    	                	}
	    	                	
	    	                	ArrayPhisComparator arrayPhisComparator=new ArrayPhisComparator(phiStart,phiEnd);
	    	            		Integer[] indexes = arrayPhisComparator.createIndexArray();
	    	            		Arrays.sort(indexes,arrayPhisComparator);
	    	            		
	    	                	
	    	                	for(int k=0;k<rowNames.size();k++){
	    	                		String r=rowNames.get(indexes[k]);
	    	                		po.write(r.getBytes());
	    	                		
	    	                	}
	    	                	
    	                	
								po.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
					logger.error(e);
							}
    	                }
    	            }).start();        	
    	return pi;
    }
    
    
    public String toJSONMaxPlusIntents(double phi){
		//boolean[][] IMaxPlus=this.getMatrixMaxPlus(phi);
    	
    	int nr=processedMatrix.length;
    	int nc=processedMatrix[0].length;
    	double rowR[];
    	
    	long[] IMaxPlus=new long[nr];    	
    	long rowIMax;
    	
    	for (int i=0;i<nr;i++){
			rowR=processedMatrix[i];    						
			rowIMax=0;
			for (int j=0;j<nc;j++){
				if (rowR[j]<=phi) rowIMax+=1<<j;    				
			}
			IMaxPlus[i]=rowIMax;					
		}
    	return toJSONSparseMaxMinPlusAttributes(IMaxPlus,nc);
		//return toJSONMaxMinPlusAttributes(IMaxPlus);
		//return toJSONMaxMinPlusIntents(IMaxPlus);
    }   
    public String toJSONMinPlusIntents(double phi){
    	int nc=processedMatrix[0].length;

    	int nr=processedMatrix.length;
    	
    	double rowR[];
    	
    	long[] IMinPlus=new long[nr];    	
    	long rowIMin;
    	
    	for (int i=0;i<nr;i++){
			rowR=processedMatrix[i];    						
			rowIMin=0;
			for (int j=0;j<nc;j++){
				if (rowR[j]>=phi) rowIMin+=1<<j;    				
			}
			IMinPlus[i]=rowIMin;					
		}    	
    	return toJSONSparseMaxMinPlusAttributes(IMinPlus,nc);
    	
		//return toJSONMaxMinPlusAttributes(IMinPlus);
		//return toJSONMaxMinPlusIntents(IMinPlus);
    }   
        
    /*
     * {
     * "graph": {"numattr":4,
     *          "fca": [{"n":123},{"n":-1},{"n":1},{"n":-1},
     *         			{"n":11},{"n":10},{"n":1},{"n":-1},
     *         			{"n":3},{"n":-1},{"n":-1},{"n":-1},
     *         			{"n":-1},{"n":9},{"n":8},{"n":-1}],
     *          "attrnames": ["attr1","attr2","attr3","attr4"],
     *          "numconcepts":140}
     * }
     */
    
    private String toJSONMaxMinPlusAttributes(boolean[][] I){
    	StringBuffer jsonOut=new StringBuffer();
    	int[] numElements=KFCAConceptsIntersection.numElementsBySingleConcept(I);
    	int totalNumConcepts=0;
		String objNstr=",{\"n\":";
		String voidNstr=",{\"n\":-1}";
		
		
		jsonOut.append("{\"graph\": {\"numattr\":"+I[0].length+",\"fca\": [");
		
		if (numElements[0]>=0) totalNumConcepts=1;

		jsonOut.append("{\"n\":"+numElements[0]+"}");
		for (int i=1;i<numElements.length;i++){
			if (numElements[i]>=0){
				jsonOut.append(objNstr+numElements[i]+"}");
				totalNumConcepts++;
			}else{
				jsonOut.append(voidNstr);
			}
		}
		jsonOut.append("], \"attrnames\": [\"");
		
		jsonOut.append(colNames[0]);
		for (int i=1;i<colNames.length;i++){
			jsonOut.append("\",\""+colNames[i]);	
		}
		jsonOut.append("\"]},\"numconcepts\":");
		jsonOut.append(totalNumConcepts);
		jsonOut.append("}");
    	return jsonOut.toString();
    }
    
    /*
     * {
     * "graph": {"numattr":4,
     *          "fca": [{"id":0,"n":123},{"id":2,"n":1},
     *         			{"id":4,"n":11},{"id":5,"n":10},{"id":6,"n":1},
     *         			{"id":8,"n":3},
     *         			{"id":13,"n":9},{"id":4,"n":8},
     *          "attrnames": ["attr1","attr2","attr3","attr4"],
     *          "numconcepts":140}
     * }
     */
    private String toJSONSparseMaxMinPlusAttributes(long[] inputMatrix, int nc){
    	StringBuffer jsonOut=new StringBuffer();
    	int numConcepts;
    	HashMap<Long ,Integer> numElements;    	
    	
    	if (nc<es.uc3m.tsc.general.Constants.maximumCols){
    		numElements=new KFCAConceptsLite().numElementsBySingleConcept(inputMatrix, nc);
    	}else{
    		numElements=new KFCAConceptsFastApproximate().numElementsBySingleConcept(inputMatrix, nc,es.uc3m.tsc.general.Constants.maximumNumberConcepts);
    	}
    	String idNstr=",{\"id\":";
		String objNstr=",\"n\":";
		
		if (numElements.size()==0) return "";
		
		
		
		jsonOut.append("{\"graph\": {\"numattr\":"+nc+",\"fca\": [");
		
		
		
		numConcepts=numElements.get(-1L).intValue();		
		numElements.remove(-1L);
		
		Iterator<Long> iter=numElements.keySet().iterator();
		Long conceptId=iter.next();
		Integer num=numElements.get(conceptId);
		jsonOut.append("{\"id\":"+conceptId+",\"n\":"+num+"}");

		while(iter.hasNext()){
			conceptId=iter.next();
			num=numElements.get(conceptId);
			jsonOut.append(idNstr+conceptId);
			jsonOut.append(objNstr+num+"}");			
		}
							
				
		jsonOut.append("], \"attrnames\": [\"");
		
		jsonOut.append(colNames[0]);
		for (int i=1;i<colNames.length;i++){
			jsonOut.append("\",\""+colNames[i]);	
		}
		jsonOut.append("\"]},\"numconcepts\":");
		//jsonOut.append(numElements.size());
		jsonOut.append(numConcepts);
		jsonOut.append("}");
    	return jsonOut.toString();
    }

    
    /*
     * {
     * "graph": {"numattr":4,
     *          "fca": [{"id":0,"n":123},{"id":2,"n":1},
     *         			{"id":4,"n":11},{"id":5,"n":10},{"id":6,"n":1},
     *         			{"id":8,"n":3},
     *         			{"id":13,"n":9},{"id":4,"n":8},
     *          "attrnames": ["attr1","attr2","attr3","attr4"],
     *          "numconcepts":140}
     * }
     */
    private String toJSONSparseMaxMinPlusAttributes(boolean[][] I, int maxNumConcepts){
    	StringBuffer jsonOut=new StringBuffer();
    	HashMap<Integer,Integer> numElements=KFCAConceptsIntersection.numElementsBySingleConcept(I,maxNumConcepts);
    	int totalNumConcepts=0;
    	String idNstr=",{\"id\":";
		String objNstr=",\"n\":";
		
		if (numElements.size()==0) return "";
		
		Iterator<Integer> iter=numElements.keySet().iterator();
		
		jsonOut.append("{\"graph\": {\"numattr\":"+I[0].length+",\"fca\": [");
		
		Integer conceptId=iter.next();
		Integer num=numElements.get(conceptId);
		jsonOut.append("{\"id\":"+conceptId+",\"n\":"+num+"}");

		while(iter.hasNext()){
			conceptId=iter.next();
			num=numElements.get(conceptId);
			jsonOut.append(idNstr+conceptId);
			jsonOut.append(objNstr+num+"}");			
		}
							
				
		jsonOut.append("], \"attrnames\": [\"");
		
		jsonOut.append(colNames[0]);
		for (int i=1;i<colNames.length;i++){
			jsonOut.append("\",\""+colNames[i]);	
		}
		jsonOut.append("\"]},\"numconcepts\":");
		jsonOut.append(numElements.size());
		
		jsonOut.append("}");
    	return jsonOut.toString();
    }
    
    
    /*
     * {
     * "graph": {"numattr":4,
     *          "fca": [{"id":0,"n":123},{"id":2,"n":1},
     *         			{"id":4,"n":11},{"id":5,"n":10},{"id":6,"n":1},
     *         			{"id":8,"n":3},
     *         			{"id":13,"n":9},{"id":4,"n":8},
     *          "attrnames": ["attr1","attr2","attr3","attr4"],
     *          "numconcepts":140}
     * }
     */
    
    private String toJSONSparseMaxMinPlusAttributes(boolean[][] I){
    	StringBuffer jsonOut=new StringBuffer();
    	int[] numElements=KFCAConceptsIntersection.numElementsBySingleConcept(I);
    	int totalNumConcepts=0;
    	String idNstr=",{\"id\":";
		String objNstr=",\"n\":";
		
		
		
		jsonOut.append("{\"graph\": {\"numattr\":"+I[0].length+",\"fca\": [");
		
		if (numElements[0]>=0) totalNumConcepts=1;
		
		jsonOut.append("{\"id\":"+0+",\"n\":"+numElements[0]+"}");
		for (int i=1;i<numElements.length;i++){
			if (numElements[i]>=0){
				jsonOut.append(idNstr+i);
				jsonOut.append(objNstr+numElements[i]+"}");
				totalNumConcepts++;
			}
		}
		jsonOut.append("], \"attrnames\": [\"");
		
		jsonOut.append(colNames[0]);
		for (int i=1;i<colNames.length;i++){
			jsonOut.append("\",\""+colNames[i]);	
		}
		jsonOut.append("\"]},\"numconcepts\":");
		jsonOut.append(totalNumConcepts);
		jsonOut.append("}");
    	return jsonOut.toString();
    }
    
    /*
	 * { "intents": [	{ "m": ["attr1","attr2"], "n":100, "id":0 },
	 * 	 			{ "m": ["attr1","attr2","attr3"],"n":10, "id":1 }, 
	 * 				{ "m": ["attr1"], "n":7, "id":2 }
	 * ], "graph" : {
	 * 
	 *  }
	 * } 
	 */
    @Deprecated
    private String toJSONMaxMinPlusIntents(boolean[][] IMaxMinPlus){	
    	boolean[][] A=KFCAConceptsIntersection.getExtents(IMaxMinPlus);    	    	    	    	    
    	boolean[][] B=KFCAConceptsIntersection.getIntents(IMaxMinPlus,A);
    	
    	int[] numA=ArrayUtils.sumRows(A);
    	
    	boolean shouldAddComma=false;    	
    	
    	StringBuffer buf=new StringBuffer();
    	buf.append("{ \"intents\": [");
    	
    	
    	for (int i=0;i<A.length;i++){
    		
    		if (i>0) buf.append(",");
    		
    		buf.append("{\"m\": [");   
    		shouldAddComma=false;
    		boolean[] intent=B[i];
    		for (int j=0;j<intent.length;j++){
    			if (intent[j]){
    				if (shouldAddComma) 
    					buf.append(",");
    				else
    					shouldAddComma=true;
    				
    				buf.append("\"");
    				buf.append(colNames[j]);
    				buf.append("\"");
    			}
    		}
    		
    		buf.append("],\"n\":");
    		buf.append(numA[i]);
    		buf.append(",\"id\":");
    		buf.append(i);
    		buf.append("}");
    		
    	}
    	buf.append("],\"graph\": ");
    	String json=GraphFCA.getJSONfullFCAGraph(IMaxMinPlus);
    	buf.append(json);
    	
    	buf.append("}");
		return buf.toString();
    }
    
    @Deprecated //Use sored version
    public String toJSONMaxPlusConcept(double phi,int conceptId){
		boolean[][] IMaxPlus=this.getMatrixMaxPlus(phi);
		return toJSONMaxMinPlusSimpleConcept(IMaxPlus,conceptId);
		//return toJSONMaxMinPlusConcept(IMaxPlus,conceptId);
    }
    @Deprecated //Use sored version
    public String toJSONMinPlusConcept(double phi,int conceptId){
		boolean[][] IMinPlus=this.getMatrixMinPlus(phi);
		return toJSONMaxMinPlusSimpleConcept(IMinPlus,conceptId);
		//return toJSONMaxMinPlusConcept(IMinPlus,conceptId);
    }
    	
        
    public String toJSONMaxPlusSortConcept(double phi,int conceptId){
		boolean[][] IMaxPlus=this.getMatrixMaxPlus(phi);
		return toJSONMaxMinPlusSimpleSortedConcept(IMaxPlus, conceptId, true);
    }
    public String toJSONMinPlusSortConcept(double phi,int conceptId){
		boolean[][] IMinPlus=this.getMatrixMinPlus(phi);
		return toJSONMaxMinPlusSimpleSortedConcept(IMinPlus, conceptId, false);
    }
    
    public String toJSONMaxPlusSortConcept_hash(double phi,int conceptId){
    	logger.info("Maxplus for phi="+phi+" conceptId"+conceptId);
		boolean[][] IMaxPlus=this.getMatrixMaxPlus(phi);
		return toJSONMaxMinPlusSimpleSortedConcept_hash(IMaxPlus, conceptId, true);
    }
    public String toJSONMinPlusSortConcept_hash(double phi,int conceptId){
    	logger.info("Minplus for varphi="+phi+" conceptId"+conceptId);
		boolean[][] IMinPlus=this.getMatrixMinPlus(phi);
		return toJSONMaxMinPlusSimpleSortedConcept_hash(IMinPlus, conceptId, false);
    }
    
    private List<String> obtainRowNames(double phi,int conceptId, boolean max_min_plus ){
    	boolean[][] IMaxMinPlus;
		if (max_min_plus)
			IMaxMinPlus=this.getMatrixMaxPlus(phi);
		else
			IMaxMinPlus=this.getMatrixMinPlus(phi);
		List<String> rowNames=new ArrayList<String>();
		boolean[] rowI;
		
		boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);    	
    		i=i>>1;
    	}
		
		for (int k=0;k<IMaxMinPlus.length;k++){
    		rowI=IMaxMinPlus[k];
    		if (Arrays.equals(rowI, attr)){    		
    			rowNames.add(this.rowNames[k]);    				
    		}
		}
		return rowNames;
    }
    /*
     * Launches Thread to calculate PValues for the genes in a cluster based on the GeneOntologies
     */
    public String threadMaxMinPlusPValueConcept(double phi,int conceptId, boolean max_min_plus ){
    	List<String> rowNames=this.obtainRowNames(phi, conceptId, max_min_plus);
		ThreadCalcPValue threadCalcPValue=new ThreadCalcPValue(this,rowNames);
		long pValueId=threadCalcPValue.addToExecutor();
		return "{\"pValueId\":"+pValueId+"}";		
    }
    
	public String createFCAGeneOntology(double phi, int conceptId, boolean max_min_plus, GoDomainEnum ontology) {
    	Set<String> goIds=new HashSet<String>();
    	HashMap<String,Set<String>> hashList=new HashMap<String,Set<String>>();
		List<String> rowNames=this.obtainRowNames(phi, conceptId, max_min_plus);
    	for (String rowName:rowNames){
			List<GODescription> goDescription=null;
    		if (this.getIsGeneExpression()){
				goDescription=this.geneInfo.findGeneOntologyFromGeneName(microArrayType, rowName, ontology);
    		}else{
				GeneDescription geneDescription=this.geneInfo.getGeneDescription(microArrayType, rowName);
				rowName=geneDescription.getProperGeneName();
				switch(ontology){
				case BP:
					goDescription=geneDescription.getGoBP();
					break;
				case MF:
					goDescription=geneDescription.getGoMF();
					break;
				case CC:
					goDescription=geneDescription.getGoCC();
					break;
				}
    		}
			if (goDescription != null && goDescription.size() > 0) {
				Set<String> goGene=new HashSet<String>();
				for (GODescription god : goDescription) {
					goGene.add("GO:" + god.goID);
				}

				hashList.put(rowName, goGene);
				goIds.addAll(goGene);

			}
    	}
		List<String> lGoIds=new ArrayList<String>(goIds);
		List<String> lRowName=new ArrayList<String>(hashList.keySet());
		boolean[][] I=GraphFCA.getAdjacentMatrix(hashList, lGoIds, lRowName);
		return GraphFCA.getCSVFormat(I, lGoIds, lRowName);
    }

    @Deprecated
    public String toJSONMaxMinPlusPValueConcept(double phi,int conceptId, boolean max_min_plus ){
		boolean[][] IMaxMinPlus;
		if (max_min_plus)
			IMaxMinPlus=this.getMatrixMaxPlus(phi);
		else
			IMaxMinPlus=this.getMatrixMinPlus(phi);
		List<String> rowNames=new ArrayList<String>();
		boolean[] rowI;
		
		boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);    	
    		i=i>>1;
    	}
		
		for (int k=0;k<IMaxMinPlus.length;k++){
    		rowI=IMaxMinPlus[k];
    		if (Arrays.equals(rowI, attr)){    		
    			rowNames.add(this.rowNames[k]);    				
    		}
		}
		GOpValue[] pValues=this.geneClusterAnalysis.getGOpValue(rowNames);
		//String json=new JSONSerializer().exclude("class").deepSerialize(pValues);
		StringBuffer json=new StringBuffer();
		json.append("{\"numMicro\":"+this.rowNames.length+",\"clusterSize\":"+rowNames.size()+",\"go\":[");
		
		for (int k=0;k<pValues.length;k++){
			GOpValue go=pValues[k];
			if (k==0)
				json.append("{\"g\":\""+go.getGOID());
			else
				json.append(",{\"g\":\""+go.getGOID());
			
			json.append("\",\"d\":\""+go.getDescription());
			json.append("\",\"p\":"+go.getPValue());
			json.append(",\"na\":"+go.getNumAppear());
			json.append(",\"nt\":"+go.getNumTotal());						
			json.append("}");
		}
		json.append("]}");
		return json.toString();
    }
    
    /*
	 * {"o": ["gene1","gene2","gene3"],
	 *  "m": ["attr1","attr2"] }
	 * 
	 */
    @Deprecated
	private String toJSONMaxMinPlusConcept(boolean[][] IMaxMinPlus, int conceptId){
		
    	boolean[][] A=KFCAConceptsIntersection.getExtents(IMaxMinPlus);    	    	    	    	    
    	boolean[][] B=KFCAConceptsIntersection.getIntents(IMaxMinPlus,A);
    	
    	boolean shouldAddComma=false;
    	
    	StringBuffer buf=new StringBuffer();
    		
    	buf.append("{\"o\":[");
    	boolean[] extent=A[conceptId];
    	for (int j=0;j<extent.length;j++){
    		if (extent[j]){
    			if (shouldAddComma) 
    				buf.append(",");
    			else
    				shouldAddComma=true;
    			buf.append("\"");
    			buf.append(rowNames[j]);
    			buf.append("\"");
    			}
    		}
		
		
		buf.append("],\"m\":[");   
    	shouldAddComma=false;
    	boolean[] intent=B[conceptId];
		for (int j=0;j<intent.length;j++){
		if (intent[j]){
			if (shouldAddComma) 
				buf.append(",");
			else
					shouldAddComma=true;
				
				buf.append("\"");
				buf.append(colNames[j]);
				buf.append("\"");
			}
		}
		buf.append("]");
	
    	buf.append("}");
		return buf.toString();
    }
    
    /*
	 * {
	 * "m": ["attr1","attr2"],
	 * "o": ["gene1","gene2","gene3"]
	 *  }
	 * Deprecated by toJSONMaxMinPlusSimpleSortedConcept
	 */    
    @Deprecated
	private String toJSONMaxMinPlusSimpleConcept(boolean[][] IMaxMinPlus, int conceptId){
		
		
    	StringBuffer buf=new StringBuffer();
    	
    	buf.append("{\"m\":[");
    	
    	
    	boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;
    	boolean firstElem=true;
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);
    		if (attr[k]){
    			if (firstElem){
    				buf.append("\""+this.colNames[k]+"\"");
    				firstElem=false;
    			}else{
    				buf.append(",\""+this.colNames[k]+"\"");
    			}    			
    		}	
    		i=i>>1;
    	}
    	buf.append("],\"o\":[");
    	
    	Integer[] objArray=new Integer[IMaxMinPlus.length];
    	firstElem=true;
    	i=0;
    	for (int k=0;k<IMaxMinPlus.length;k++){
    		boolean[] rowI=IMaxMinPlus[k];
    		if (Arrays.equals(rowI, attr)){
    			if (firstElem){
    				buf.append("\""+this.rowNames[k]+"\"");
    				firstElem=false;
    			}else{
    				buf.append(",\""+this.rowNames[k]+"\"");
    			}
    			objArray[i++]=k;
    		}    		
    	}   	
    	    	
    	buf.append("]}");    	    	
		return buf.toString();
    }

	public class  ArrayPhisComparator implements Comparator<Integer>{
		private final Double[] first;
		private final Double[] second;
		
	    public ArrayPhisComparator(List<Double> first) {
	        this.first= new Double[first.size()];
	        this.second=null;
	        first.toArray(this.first);	        
	    }
	    
	    public ArrayPhisComparator(List<Double> first,List<Double> second) {
	        this.first= new Double[first.size()];
	        this.second=new Double[second.size()];
	        first.toArray(this.first);	 
	        second.toArray(this.second);
	    }
	    
	    public Integer[] createIndexArray()  {
	        Integer[] indexes = new Integer[first.length];
	        for (int i = 0; i < first.length; i++){
	            indexes[i] = i;
	        }
	        return indexes;
	    }
	    @Override
	    public int compare(Integer index1, Integer index2) {	    	
	        int r=first[index1].compareTo(first[index2]);
	        if (r==0 && second!=null){
	        	r=second[index1].compareTo(second[index2]);
	        }
	        return r;
	    }
	}
	/*
	 * {
	 * "m": ["attr1","attr2"],
	 * "o": ["gene1","gene2","gene3"],
	 * "h1": 0.0
	 *  }
	*/
	private String toJSONMaxMinPlusSimpleSortedConcept(boolean[][] IMaxMinPlus, int conceptId, boolean max_min_plus){		
		List<Double> phiStart=new ArrayList<Double>();
		List<Double> phiEnd=new ArrayList<Double>();
		List<String> rowNames=new ArrayList<String>();
		
    	StringBuffer buf=new StringBuffer();    	
    	buf.append("{\"m\":[");    	
    	
    	boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;
    	boolean firstElem=true;
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);
    		if (attr[k]){
    			if (firstElem){
    				buf.append("\""+this.colNames[k]+"\"");
    				firstElem=false;
    			}else{
    				buf.append(",\""+this.colNames[k]+"\"");
    			}    			
    		}	
    		i=i>>1;
    	}
    	buf.append("],\"o\":[");		
		
    	Integer[] objArray=new Integer[IMaxMinPlus.length];
    	i=0;
		boolean[] rowI;
		if (max_min_plus){		
			for (int k=0;k<IMaxMinPlus.length;k++){
	    		rowI=IMaxMinPlus[k];
	    		if (Arrays.equals(rowI, attr)){
	    			double[] p=this.objectExplored.getMaxMarginPhi(k,conceptId);
	    			if (p!=null){
	    				phiStart.add(p[0]);   
	    				phiEnd.add(p[1]);
	    				rowNames.add(this.rowNames[k]);
	    				objArray[i++]=k;
	    			}else{
	    				this.logger.warn("Concept id not found "+conceptId+" -> "+Arrays.toString(attr)+"rowId"+k+" rowName:"+this.rowNames[k]+" concepts:"+Arrays.toString(this.objectExplored.getMaxplusConceptId(k)));
	    			}
	    		}
			}
		}else{
			for (int k=0;k<IMaxMinPlus.length;k++){
	    		rowI=IMaxMinPlus[k];
	    		if (Arrays.equals(rowI, attr)){	    				    			
	    			double[] p=this.objectExplored.getMinMarginVarphi(k,conceptId);
	    			if (p!=null){
	    				phiStart.add(-p[1]);
	    				phiEnd.add(-p[0]);
	    				rowNames.add(this.rowNames[k]);
	    			}else{	    				
	    				this.logger.warn("Concept id not found "+conceptId+" -> "+Arrays.toString(attr)+"rowId"+k+" rowName:"+this.rowNames[k]+" concepts:"+Arrays.toString(this.objectExplored.getMinplusConceptId(k)));
	    			}
	    		}
			}
		}
		ArrayPhisComparator arrayPhisComparator=new ArrayPhisComparator(phiStart,phiEnd);
		Integer[] indexes = arrayPhisComparator.createIndexArray();
		Arrays.sort(indexes,arrayPhisComparator);
		int maxResults=1000;
		if (rowNames.size()<maxResults) maxResults=rowNames.size();
    	firstElem=true;
    	i=0;
    	
    	for(int k=0;k<maxResults;k++){
    		if (firstElem){
				buf.append("\""+rowNames.get(indexes[k])+"\"");
				firstElem=false;
			}else{
				buf.append(",\""+rowNames.get(indexes[k])+"\"");
			}
    	}
    	    	
    	buf.append("]}");
    	
    	
    	return buf.toString();
	}
	
	public class  HashPhisComparator implements Comparator<String>{
		private final HashMap<String,Double> hashPhi;		
	    public HashPhisComparator() {
	        this.hashPhi=new HashMap<String,Double>();
	    }
	    public void add(String name,Double phi){
	    	this.hashPhi.put(name, phi);
	    }
	    public String[] getSortedKeys(){
	    	String[] ret=new String[this.hashPhi.size()];
	    	Set<String> s=this.hashPhi.keySet();
	    	s.toArray(ret);
	    	Arrays.sort(ret,this);
	    	return ret;
	    }
	    @Override
	    public int compare(String index1, String index2) {	    	
	        return this.hashPhi.get(index1).compareTo(this.hashPhi.get(index2));
	    }
	}
	
	private String toJSONMaxMinPlusSimpleSortedConcept_hash(boolean[][] IMaxMinPlus, int conceptId, boolean max_min_plus){		
		
		
    	StringBuffer buf=new StringBuffer();    	
    	buf.append("{\"m\":[");    	
    	
    	boolean[] attr=new boolean[this.colNames.length];
    	int i=conceptId;
    	boolean firstElem=true;
    	for (int k=0;i!=0 || k<attr.length;k++){    	
    		attr[k]=(i%2==1);
    		if (attr[k]){
    			if (firstElem){
    				buf.append("\""+this.colNames[k]+"\"");
    				firstElem=false;
    			}else{
    				buf.append(",\""+this.colNames[k]+"\"");
    			}    			
    		}	
    		i=i>>1;
    	}
    	buf.append("],\"o\":[");		
		
    	HashPhisComparator hashPhisComparator=new HashPhisComparator();
		boolean[] rowI;
		if (max_min_plus){		
			for (int k=0;k<IMaxMinPlus.length;k++){
	    		rowI=IMaxMinPlus[k];
	    		if (Arrays.equals(rowI, attr)){
	    			double[] p=this.objectExplored.getMaxMarginPhi(k,conceptId);
	    			if (p!=null){
	    				hashPhisComparator.add(this.rowNames[k],p[0]);
	    			}else{
	    				this.logger.warn("Concept id not found "+conceptId+" -> "+Arrays.toString(attr)+"rowId"+k+" rowName:"+this.rowNames[k]+" concepts:"+Arrays.toString(this.objectExplored.getMaxplusConceptId(k)));
	    			}
	    		}
			}
		}else{
			for (int k=0;k<IMaxMinPlus.length;k++){
	    		rowI=IMaxMinPlus[k];
	    		if (Arrays.equals(rowI, attr)){
	    			
	    			double[] p=this.objectExplored.getMinMarginVarphi(k,conceptId);
	    			if (p!=null){
	    				hashPhisComparator.add(this.rowNames[k],p[0]);
	    			}else{
	    				this.logger.warn("Concept id not found "+conceptId+" -> "+Arrays.toString(attr)+"rowId"+k+" rowName:"+this.rowNames[k]+" concepts:"+Arrays.toString(this.objectExplored.getMaxplusConceptId(k)));
	    			}	    				    					
	    		}
			}
		}
		
		
		String[] sortedRows=hashPhisComparator.getSortedKeys();
		
    	firstElem=true;
    	i=0;
    	
    	for (String row:sortedRows){
    		if (firstElem){
				buf.append("\""+row+"\"");
				firstElem=false;
			}else{
				buf.append(",\""+row+"\"");
			}
    	}
    	    	
    	buf.append("]}");
    	
    	return buf.toString();
	}
	
	/*
	{"numattr":8,"conceptinfo": [{"id":0,"phi":-3.1415},{"id":2,"phi":-3},{"id":7,"phi":-2},{"id":71,"phi":-1}]};
	*/
	
	public String toJSONMaxMinPlusRangeInfo(String rowName,boolean max_min_plus){
		StringBuffer buf=new StringBuffer();    
		
		
		int rowId=Arrays.asList(this.rowNames).indexOf(rowName);
		if (rowId>=0){
			buf.append("{\"numattr\":"); 
			buf.append(this.colNames.length);
						
			double[] phi_varphi;
			long[] conceptId;
			if (max_min_plus) {
				phi_varphi=this.objectExplored.getMaxplusPhi(rowId);
				conceptId=this.objectExplored.getMaxplusConceptId(rowId);
			}else{
				phi_varphi=this.objectExplored.getMinplusVarphi(rowId);
				conceptId=this.objectExplored.getMinplusConceptId(rowId);
			}
			buf.append(",\"conceptinfo\":[");
			boolean firstElement=true;
			for (int i=0;i<conceptId.length;i++){
				if (firstElement){
					buf.append("{\"id\":"+conceptId[i]+",\"phi\":"+phi_varphi[i]+"}");
					firstElement=false;
				}else{
					buf.append(",{\"id\":"+conceptId[i]+",\"phi\":"+phi_varphi[i]+"}");
				}				
			}
			
			buf.append("], \"attrnames\": [\"");
			
			buf.append(colNames[0]);
			for (int i=1;i<colNames.length;i++){
				buf.append("\",\""+colNames[i]);	
			}
			buf.append("\"]}");
			
		}else{
			logger.info("No row with name "+rowName+" found");
		}
		
		return buf.toString();
	}
	
	public String getGeneAnalysis(int conceptId,boolean max_min_plus, int sortedby, int max_genes, boolean json_csv_format){
		List<Double> sortedValue1=new ArrayList<Double>();
		List<Double> sortedValue2=new ArrayList<Double>();
		
		List<String> jsonGenes=new ArrayList<String>();
		
		StringBuilder s=new StringBuilder();		
		String startS=",{\"id\":\"";		
		String p="\",\"p\":[";
		String c=",";
		String d="],\"d\":";
		String f="}";
		String n="\n";
		double maxPhi=-Double.MAX_VALUE;
		double minPhi=Double.MAX_VALUE;
		int nc=this.processedMatrix[0].length;
		if (max_min_plus){			
			for (int i=0;i<this.rowNames.length;i++){
				
				double[] phi=this.objectExplored.getMaxMarginPhi(i,conceptId);
				if (phi!=null){		
					double data[]=this.processedMatrix[i];
					double dp=(phi[1]-phi[0]);
					String g;
					if (json_csv_format){
						g=startS+this.rowNames[i]+p+phi[0]+c+phi[1]+d+dp+f;
					}else{
						g=this.rowNames[i]+c+phi[0]+c+phi[1]+c+dp;
						double datamean=0;
						for (int j=0;j<nc;j++){
							g+=c+data[j];
							datamean+=data[j];
						}
						g+=c+datamean/nc+n;
					}
					jsonGenes.add(g);
					
					switch (sortedby){
						case 0:
							sortedValue1.add(phi[0]);
							sortedValue2.add(-dp);
							break;
						
						case 1:
							sortedValue1.add(-dp);
							sortedValue2.add(phi[0]);
							break;						
					}
					
					
					
					if (phi[0]<minPhi){
						minPhi=phi[0];
					}
					if (phi[1]>maxPhi){
						maxPhi=phi[1];
					}					
				}
			}
		}else{
			for (int i=0;i<this.rowNames.length;i++){
				
				double[] varphi=this.objectExplored.getMinMarginVarphi(i,conceptId);
				if (varphi!=null){
					double data[]=this.processedMatrix[i];
					double dp=(varphi[1]-varphi[0]);
					String g;
					if (json_csv_format){
						g=startS+this.rowNames[i]+p+varphi[1]+c+varphi[0]+d+(varphi[0]-varphi[1])+f;
					}else{
						g=this.rowNames[i]+c+varphi[1]+c+varphi[0]+c+dp;
						double datamean=0;
						for (int j=0;j<nc;j++){
							g+=c+data[j];
							datamean+=data[j];
						}
						g+=c+datamean/nc+n;
					}
					jsonGenes.add(g);					
					
					switch (sortedby){
						case 0:
							sortedValue1.add(-varphi[1]);
							sortedValue2.add(-dp);
							break;
						
						case 1:
							sortedValue1.add(-dp);
							sortedValue2.add(-varphi[1]);
							break;						
					}
					
					if (varphi[0]<minPhi){
						minPhi=varphi[0];
					}
					if (varphi[1]>maxPhi){
						maxPhi=varphi[1];
					}					
				}
			}
		
		}
		
		
		ArrayPhisComparator arrayPhisComparator=new ArrayPhisComparator(sortedValue1,sortedValue2);
		Integer[] indexes = arrayPhisComparator.createIndexArray();
		Arrays.sort(indexes,arrayPhisComparator);
		if (indexes.length<max_genes || max_genes<0) max_genes=indexes.length;
		
    	for(int k=0;k<max_genes;k++){
    		s.append(jsonGenes.get(indexes[k]));
    	}
    	
		if (json_csv_format){		
			if (s.length()>0){
				Long hsId=this.calcHomogeneitySeparation(conceptId,max_min_plus, minPhi, maxPhi,Constants.numPhisToCalculateHomogeneitySeparation);
				return "{\"response\":\"OK\", \"conceptId\":"+conceptId+",\"minPhi\":"+minPhi+",\"maxPhi\":"+maxPhi+",\"hsId\":"+hsId+",\"genes\":["+s.substring(1)+"]}";
			}else{
				return "{\"response\":\"OK\", \"conceptId\":"+conceptId+",\"minPhi\":0,\"maxPhi\":0,\"hsId\":-1,\"genes\":[]}";
			}
		}else{
			return "GENE,START,END,RANGE\n"+s.toString();
		}
	}
	
	public static class HomogSeparation {
		double h;
		double s;
		double p;
		public HomogSeparation(double h, double s, double p){
			this.h=h;
			this.s=s;
			this.p=p;
		}
		double getH(){return h;};
		double getS(){return s;};
		double getP(){return p;};
		void setH(double h){this.h=h;}
		void setS(double s){this.s=s;}
		void setP(double p){this.p=p;}
	}
	
	public Long calcHomogeneitySeparation(int conceptId,boolean max_min_plus, double from_phi, double to_phi, int numMeasures){		
		ThreadCalcHomSep th=new ThreadCalcHomSep(this,conceptId,max_min_plus, from_phi, to_phi, numMeasures);
		return th.addToExecutor();
	}
	
    public static KFCAResults findKFCAResults(Long id) {
        if (id == null) return null;
        if (resultsCache!=null){
        	if (id.equals(resultsCache.getId())){
        		return resultsCache;
        	}
        }
        /*
        resultsCache=Preprocessor.findKFCAResults(id);
        if (resultsCache!=null){
        	return resultsCache;
        }
        */
        resultsCache=KFCAResults.entityManager().find(KFCAResults.class, id);
        
        return resultsCache;
    }

    public String toString() {
        return this.startDate+"("+this.processedMatrix.length+"x"+this.processedMatrix[0].length+")";
    }
}
