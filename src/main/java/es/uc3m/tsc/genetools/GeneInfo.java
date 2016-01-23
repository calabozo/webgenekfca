package es.uc3m.tsc.genetools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import es.uc3m.tsc.gene.DataTypeEnum;

public class GeneInfo {
	
	final static String EMPTYstr="";
	final static String COL="\"";
	final static String SEP="\",\"";
	final static String EMPTY="---";
	final static String SEP_GO1=" /// ";
	final static String SEP_GO2=" // ";
	
	String fileName;
	Resource res;
	
	DataTypeEnum microArrayType;
	
	EntityManager entity;
	
	public String getFileName(){
		return this.fileName;
	}
	public GeneInfo(){
		this.entity=GeneDescription.entityManager();
	}
	
	public GeneInfo(Resource res, DataTypeEnum microArrayType){		
		//Resource res = new FileSystemResource(fileName);
		try {
			this.fileName=res.getURI().getPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.res=res;
		this.microArrayType=microArrayType;
		//this.loadFileIntoDB();
		this.entity=GeneDescription.entityManager();
	}
	@Deprecated
	private void loadFileIntoDB(){
		Logger logger = Logger.getLogger("es.uc3m.tsc.genetools");
		logger.info("Starting GeneInfo, reading "+fileName);
		
		
		try{
			this.fileName=res.getURI().getPath();
			logger.info("Reading GeneInfo... : "+fileName);
			InputStream fstream = res.getInputStream();
			
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String probeSetID;
			String[] M;

            
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				if (!strLine.startsWith("#")){
					//probeSetID=strLine.substring(1, strLine.indexOf(COL, 2));
					
					M=strLine.substring(1, strLine.length()-2).split(SEP);													  
		            
					probeSetID=M[0];
					 
		            GeneDescription gd=new GeneDescription();
		            
		            gd.microArrayType=this.microArrayType.getId();
		            gd.probeSetID=probeSetID;
		            
		            try{
		            	
			            //gd.geneChipArray=M[1];
			        	//gd.speciesScientificName=M[2];
			        	gd.annotationDate=M[3];
			        	gd.sequenceType=M[4];
			        	gd.sequenceSource=M[5];
			        	gd.transcriptID=M[6];
			        	gd.description=M[7]; //Target Description
			            gd.representativePublicID=M[8]; //Representative Public ID
			            gd.archivalUnigeneCluster=M[9];
			        	gd.uniGeneID=M[10];
			        	gd.genomeVersion=M[11];
			        	gd.alignments=M[12];
			        	gd.geneTitle=M[13]; //Gene Title
			        	gd.geneSymbol=M[14]; //Gene Symbol
			        	gd.chromosomalLocation=M[15];
			        	gd.uniGeneClusterType=M[16];
			        	gd.ensembl=M[17];
			        	gd.entrezGene=M[18];
			        	//gd.swissProt=M[19];
			        	gd.EC=M[20];
			        	gd.OMIM=M[21];
			        	//gd.refSeqProteinID=M[22];
			        	//gd.refSeqTranscriptID=M[23];	
			        	gd.flybase=M[24];
			        	
			        	//gd.agiName=this.getAGI(M[25]); //AGI
			        	
			        	//gd.wormBase=M[26]; 
			        	gd.MGIName=M[27];
			        	gd.RGDName=M[28];
			        	gd.SGDAccesionNumber=M[29];
			        	
			        	gd.strGoBP=M[30]; //Gene Ontology Biological Process
			        	gd.strGoCC=M[31]; //Gene Ontology Cellular Component
			            gd.strGoMF=M[32]; //Gene Ontology Molecular Function
			            
			            //gd.goBP=getGO(M[30]); //Gene Ontology Biological Process
			            //gd.goCC=getGO(M[31]); //Gene Ontology Cellular Component
			            //gd.goMF=getGO(M[32]); //Gene Ontology Molecular Function
			            
			            gd.pathway=M[33]; //Pathway
			            gd.pathway=M[34];  //KEGG Pathway	
			        	//gd.interPro=M[35];
			        	//gd.transMembrane=M[36];
			        	//gd.QTL=M[37];
			        	//gd.annotationDescription=M[38];
			        	//gd.annotationTranscriptCluster=M[39];
			        	//gd.annotationAssignment=M[40];
			        	//gd.annotationNotes=M[41];

			        	//gd.annotationNotes=null;
			        	gd.persist();
			        	
		            }catch(Exception e){
		            	e.printStackTrace();
						logger.error(e);
		            }
		            	        
				}
				
			}
			//Close the input stream
			in.close();
			logger.info("Initialized GeneInfo "+fileName);
		  }catch (Exception e){//Catch exception if any
			  e.printStackTrace();			  
			  logger.error("Errror initializing GeneInfo ("+fileName+") msg:"+e.getMessage());
			logger.error(e);
		  }
		  				
	}
	
	public Long getNumProbeSets(DataTypeEnum microArrayType){
		return GeneDescription.getNumProbeSets(microArrayType);		
	}
	public Long getNumGenes(DataTypeEnum microArrayType){
		return GeneDescription.getNumGenes(microArrayType);		
	}
	
	public int getNumProbeSetsForGeneOntology(DataTypeEnum microArrayType,String goID){
		return GoTerm.getNumProbesetsForGOid(microArrayType,goID);
	}
	public int getNumGenesForGeneOntology(DataTypeEnum microArrayType,String goID){
		return GoTerm.getNumGenesForGOid(microArrayType,goID);		
	}
	public GeneDescription getGeneDescription(DataTypeEnum microArrayType, String strGeneName){		
		
		GeneDescription gOut=GeneDescription.findGeneDescriptionFromProbeSetID(microArrayType,strGeneName);
		/*
		if (gOut==null){
			Iterator<GeneDescription> it=this.geneDescriptions.values().iterator();
			
			switch (this.microArrayType){
			
			case ATH1121501:
				while(it.hasNext()){
					GeneDescription g=it.next();
					if (g.agiName!=null){
						Iterator<String> itAgi=g.agiName.iterator();						
						while(itAgi.hasNext()){
							String agiName=itAgi.next();
							if (strGeneName.equalsIgnoreCase(agiName)){
								return g;
							}
						}
					}
				}
				break;
			case HGU133PLUS2:
				while(it.hasNext()){
					GeneDescription g=it.next();				
					if (strGeneName.equalsIgnoreCase(g.geneSymbol)){
						return g;
					}
				}
				break;
			default:
				break;
			}
		}
		*/
		return gOut;
	}	

	public List<GODescription> findGeneOntologyFromProbeSetID(DataTypeEnum microArrayType, String strGeneName){				
		Object res[]= GeneDescription.findGeneOntologyFromProbeSetID(microArrayType,strGeneName);
		if (res==null) return null;
		byte[] bp=(byte[])res[0];                
		byte[] cc=(byte[])res[1];
		byte[] mf=(byte[])res[2];
		
		List<GODescription> lst;
		List<GODescription> go=new ArrayList<GODescription>();
		
		lst=GeneDescription.getGO(new String(bp),GoDomainEnum.BP);
		if (lst!=null) go.addAll(lst);
		lst=GeneDescription.getGO(new String(cc),GoDomainEnum.CC);
		if (lst!=null) go.addAll(lst);
		lst=GeneDescription.getGO(new String(mf),GoDomainEnum.MF);
		if (lst!=null) go.addAll(lst);	
		return go;
	}
	public List<GODescription> findGeneOntologyFromProbeSetID(DataTypeEnum microArrayType, String strGeneName, GoDomainEnum ontology){				
		Object res[]= GeneDescription.findGeneOntologyFromProbeSetID(microArrayType,strGeneName);
		if (res==null) return null;
		byte[] bp=(byte[])res[0];                
		byte[] cc=(byte[])res[1];
		byte[] mf=(byte[])res[2];
		
		List<GODescription> go=new ArrayList<GODescription>();
		
		switch(ontology){
		case BP:
			return GeneDescription.getGO(new String(bp),GoDomainEnum.BP);
		case MF:
			return GeneDescription.getGO(new String(mf),GoDomainEnum.MF);
		case CC:
			return GeneDescription.getGO(new String(cc),GoDomainEnum.CC);
		}
		return null;
	}
	
	public List<GeneDescription> getGenesFromForGeneInProbeset(DataTypeEnum microArrayType, String strProbeset){
		List<GeneDescription> gd=GeneDescription.getGenesFromProbesetID(microArrayType,strProbeset);
		return gd;
	}

	public List<GODescription> findGeneOntologyFromGeneName(DataTypeEnum microArrayType, String strGeneName){
		Object[] res=GeneDescription.findGeneOntologyFromGeneName(microArrayType,strGeneName);
		if (res==null) return null;
		byte[] bp=(byte[])res[0];                
		byte[] cc=(byte[])res[1];
		byte[] mf=(byte[])res[2];
		
		List<GODescription> lst;
		List<GODescription> go=new ArrayList<GODescription>();
		
		lst=GeneDescription.getGO(new String(bp),GoDomainEnum.BP);
		if (lst!=null) go.addAll(lst);
		lst=GeneDescription.getGO(new String(cc),GoDomainEnum.CC);
		if (lst!=null) go.addAll(lst);
		lst=GeneDescription.getGO(new String(mf),GoDomainEnum.MF);
		if (lst!=null) go.addAll(lst);	
		return go;
	}
	
	public List<GODescription> findGeneOntologyFromGeneName(DataTypeEnum microArrayType, String strGeneName, GoDomainEnum ontology){				
		Object res[]= GeneDescription.findGeneOntologyFromGeneName(microArrayType,strGeneName);
		if (res==null) return null;
		byte[] bp=(byte[])res[0];                
		byte[] cc=(byte[])res[1];
		byte[] mf=(byte[])res[2];
		
		List<GODescription> go=new ArrayList<GODescription>();
		
		switch(ontology){
		case BP:
			return GeneDescription.getGO(new String(bp),GoDomainEnum.BP);
		case MF:
			return GeneDescription.getGO(new String(mf),GoDomainEnum.MF);
		case CC:
			return GeneDescription.getGO(new String(cc),GoDomainEnum.CC);
		}
		return null;
	}
	
	public GeneDescription getGenesFromGeneName(DataTypeEnum microArrayType, String geneName){
		List<GeneDescription> gdlist=GeneDescription.getGenesFromGeneName(microArrayType,geneName);
		if (gdlist==null)
			return null;
		Iterator<GeneDescription> iter=gdlist.iterator();
		if (!iter.hasNext())
			return null;
		GeneDescription gd=iter.next();
		while(iter.hasNext()){
			GeneDescription gd2=iter.next();
			gd.probeSetID+=", "+gd2.probeSetID;
		}
		
		return gd;
	}
	
	/*
	 * Returns a list of probesets with the same gene name than the one given by strProbeset.
	 * The last element of the list is the gene name.
	 * This is faster than getGenesFromForGeneInProbeset
	 */
	public List<String> getProbesetsSimilarGeneFromProbesetID(DataTypeEnum microArrayType, String strProbeset){
		
		TypedQuery<String> qGeneName;
		TypedQuery<String> qProbesets;
		
		if (microArrayType==DataTypeEnum.ATH1121501){
			qGeneName=this.entity.createQuery("SELECT strAgiName FROM GeneDescription o WHERE microArrayType=? and probeSetID=?", String.class);
		}else{
			qGeneName=this.entity.createQuery("SELECT geneSymbol FROM GeneDescription o WHERE microArrayType=? and probeSetID=?", String.class);
		}
		
		qGeneName.setParameter(1, microArrayType.getId());
		qGeneName.setParameter(2, strProbeset);		               
        String geneName=qGeneName.getSingleResult();
		
        if (microArrayType==DataTypeEnum.ATH1121501)
			qProbesets=this.entity.createQuery("SELECT probeSetID FROM GeneDescription o WHERE microArrayType=? and strAgiName=?", String.class);
		else
			qProbesets=this.entity.createQuery("SELECT probeSetID FROM GeneDescription o WHERE microArrayType=? and geneSymbol=?", String.class);
        
		qProbesets.setParameter(1, microArrayType.getId());
		qProbesets.setParameter(2, geneName);		               
        List<String> probesets=qProbesets.getResultList();
		
        probesets.add(geneName);
		
		return probesets;
	}
	
	public String[] getGODescription(String fileName,String goID){
		String[] goDescriptions=null;
		String id_goID="id: "+goID;
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if (strLine.startsWith("[Term]")){
					String goTermID = br.readLine();
					if (goTermID!=null && goTermID.startsWith(id_goID)){
						GeneOntology go=GeneOntology.parseGO(goID,br);
						goDescriptions=go.getAsArray();
						/*
						goDescriptions=new String[4];
						goDescriptions[0]=br.readLine();
						goDescriptions[1]=br.readLine();						
						goDescriptions[2]=br.readLine();
						if (goDescriptions[2].startsWith("alt_id:")){
							goDescriptions[2]=br.readLine();	
						}
						goDescriptions[3]=br.readLine();
						*/
						break;
					}				
				}
			}		
		in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return goDescriptions;
		
	}
	
	public String[] getGOOnlyChilds(String fileName,String goIDs[]){
		if (goIDs==null || goIDs.length<=0)
			return new String[0];
		
		ArrayList<GeneOntology> goIDsNoChilds=new ArrayList<GeneOntology>();
		ArrayList<GeneOntology> parsedGOs=new ArrayList<GeneOntology>();
		
		
		ArrayList<String> goIDsInput=new ArrayList<String>(Arrays.asList(goIDs));
		
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if (strLine.startsWith("[Term]")){
					String goTermID = br.readLine();
					if (goTermID!=null){
						Iterator<String> it=goIDsInput.iterator();
						
						while(it.hasNext()){
							String goID=it.next();					
							if (goTermID.startsWith("id: "+goID)){
								GeneOntology go=GeneOntology.parseGO(goID,br);
								parsedGOs.add(go);
								
								
								goIDsInput.remove(goID);
								break;
							}
						}						
					}
				}
			}
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		Iterator<GeneOntology> it=parsedGOs.iterator();
		while(it.hasNext()){
			GeneOntology go=it.next();
			goIDsNoChilds.add(go);
		}
		
		it=parsedGOs.iterator();
		while(it.hasNext()){
			GeneOntology go=it.next();
			if (go.getIs_a()!=null) {
				Iterator<String> its=go.getIs_a().iterator();
				while(its.hasNext()){
					String parentID=its.next();
					goIDsNoChilds.remove(new GeneOntology(parentID));
				}
			}
		}
		
		
		return GeneOntology.extractArrayIDs(goIDsNoChilds);
		
	}
	
}