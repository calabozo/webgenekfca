package es.uc3m.tsc.genetools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import es.uc3m.tsc.gene.DataTypeEnum;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class GeneDescription {
	/*
	 * Genes obtained from Affymetrix annotation file
	 * 
	 */
	
	final static String COL="\"";
	final static String SEP="\",\"";
	final static String EMPTY="---";
	final static String SEP_GO1=" /// ";
	final static String SEP_GO2=" // ";	
	
	public Integer microArrayType;
	public String probeSetID;	
	//public String geneChipArray;
	//public String speciesScientificName;
	public String annotationDate;
	public String sequenceType;
	public String sequenceSource;
	public String transcriptID;
	
	@Column(columnDefinition = "LONGBLOB")
	public String description; //Target Description
	public String representativePublicID;//Representative Public ID
	public String archivalUnigeneCluster;
	public String uniGeneID;
	public String genomeVersion;
	@Size(max = 1024)
	public String alignments;
	@Size(max = 4096)
	public String geneTitle; //Gene Title
	@Size(max = 1024)	
	public String geneSymbol; //Gene Symbol
	public String chromosomalLocation;
	public String uniGeneClusterType;
	@Size(max = 1024)
	public String ensembl;
	public String entrezGene;
	public String swissProt;
	public String EC;
	public String OMIM;
	//public String refSeqProteinID;
	//public String refSeqTranscriptID;	
	public String flybase;
	
	@Transient
	public List<String> agiName; //AGI
	public String strAgiName; //AGI
	
	//public String wormBase; 
	public String MGIName;
	public String RGDName;
	public String SGDAccesionNumber;
	
	@Transient	
	List<GODescription> goBP; //Gene Ontology Biological Process
	@Column(columnDefinition = "LONGBLOB")
	String strGoBP; //Gene Ontology Biological Process	
	@Transient
    List<GODescription> goCC; //Gene Ontology Cellular Component
	@Column(columnDefinition = "LONGBLOB")
	String strGoCC; //Gene Ontology Cellular Component
	
	@Transient
    List<GODescription> goMF; //Gene Ontology Molecular Function	
	@Column(columnDefinition = "LONGBLOB")    
	String strGoMF; //Gene Ontology Molecular Function
	
	@Column(columnDefinition = "LONGBLOB")    
    public String pathway;  //KEGG Pathway	
	
	//public String interPro;
	//public String transMembrane;
	//public String QTL;
	//public String annotationDescription;
	//public String annotationTranscriptCluster;
	//public String annotationAssignment;
	//public String annotationNotes;

    public DataTypeEnum getMicroArrayType(){
    	return DataTypeEnum.getType(this.microArrayType.intValue());
    }
	
    public void setMicroArrayType(DataTypeEnum m){
    	this.microArrayType=m.getId();
    }

    public String toJson() {
        return new JSONSerializer().exclude("class").deepSerialize(this);
		//return new JSONSerializer().deepSerialize(this);
    }
	
	public String getProperGeneName(){
		if (this.microArrayType==DataTypeEnum.ATH1121501.getId()){
			return strAgiName;
		}
		return geneSymbol;
	}

	public List<GODescription> getAllGO(){
		List<GODescription> go=new ArrayList<GODescription>();
		if (this.goBP!=null) go.addAll(this.goBP);
		if (this.goCC!=null) go.addAll(this.goCC);
		if (this.goMF!=null) go.addAll(this.goMF);		
		return go;
	}
	
	public static GeneDescription findGeneDescriptionFromProbeSetID(DataTypeEnum microArrayType, String probeSetId) {
		TypedQuery<GeneDescription> q=entityManager().createQuery("SELECT o FROM GeneDescription o WHERE microArrayType=? and probeSetID=?", GeneDescription.class);
		q.setParameter(1, microArrayType.getId());
		q.setParameter(2, probeSetId);		
        GeneDescription gd=null;
        gd=q.getSingleResult();
        
       
        if (gd!=null){
        	gd.agiName=gd.getAGI(gd.strAgiName);
	        gd.goBP=GeneDescription.getGO(gd.strGoBP,GoDomainEnum.BP); //Gene Ontology Biological Process
	        gd.goCC=GeneDescription.getGO(gd.strGoCC,GoDomainEnum.CC); //Gene Ontology Cellular Component
	        gd.goMF=GeneDescription.getGO(gd.strGoMF,GoDomainEnum.MF); //Gene Ontology Molecular Function
        }
        return gd;
    }
	

	public static Object[] findGeneOntologyFromProbeSetID(DataTypeEnum microArrayType, String probeSetId) {
		Query q=entityManager().createNativeQuery("SELECT str_gobp,str_gocc,str_gomf FROM gene_description WHERE micro_array_type=? and probe_setid=?");
		q.setParameter(1, microArrayType.getId());
		q.setParameter(2, probeSetId);		
		//List<Object> resultList = q.getResultList();  
        Object[] res=(Object[])q.getSingleResult();
        return res;
    }
	
	public static  List<GeneDescription>  getGenesFromProbesetID(DataTypeEnum microArrayType, String probesetId) {
		TypedQuery<GeneDescription> q;
		
		if (microArrayType==DataTypeEnum.ATH1121501){
			//q=entityManager().createNativeQuery("SELECT * FROM gene_description  WHERE micro_array_type=? and str_agi_name=(SELECT str_agi_name FROM gene_description WHERE probe_setid=? limit 1)");
			q=entityManager().createQuery("SELECT o FROM GeneDescription o WHERE microArrayType=? and strAgiName=(SELECT strAgiName FROM GeneDescription o WHERE microArrayType=? and probeSetID=?)", GeneDescription.class);
		}else{
		//	q=entityManager().createNativeQuery("SELECT * FROM gene_description  WHERE micro_array_type=? and gene_symbol=(SELECT gene_symbol FROM gene_description WHERE probe_setid=? limit 1)");
			q=entityManager().createQuery("SELECT o FROM GeneDescription o WHERE microArrayType=? and geneSymbol=(SELECT geneSymbol FROM GeneDescription o WHERE microArrayType=? and probeSetID=?)", GeneDescription.class);
		}
		
		q.setParameter(1, microArrayType.getId());
		q.setParameter(2, microArrayType.getId());		
		q.setParameter(3, probesetId);		               
        List<GeneDescription> gd=q.getResultList();
        
        return gd;
    }
	
	public static  List<GeneDescription>  getGenesFromGeneName(DataTypeEnum microArrayType, String geneName) {
		TypedQuery<GeneDescription> q;
		q=entityManager().createQuery("SELECT o FROM GeneDescription o WHERE microArrayType=? and (geneSymbol=? or strAgiName=? or swissProt=? or flybase=?)", GeneDescription.class);

		q.setParameter(1, microArrayType.getId());
		q.setParameter(2, geneName);
		q.setParameter(3, geneName);
		q.setParameter(4, geneName);
		q.setParameter(5, geneName);
        List<GeneDescription> gdlist=q.getResultList();
        if (gdlist!=null){
        	Iterator<GeneDescription> iter=gdlist.iterator();
        	while(iter.hasNext()){
        		GeneDescription gd=iter.next();        	
        		gd.agiName=gd.getAGI(gd.strAgiName);
        		gd.goBP=GeneDescription.getGO(gd.strGoBP,GoDomainEnum.BP); //Gene Ontology Biological Process
        		gd.goCC=GeneDescription.getGO(gd.strGoCC,GoDomainEnum.CC); //Gene Ontology Cellular Component
        		gd.goMF=GeneDescription.getGO(gd.strGoMF,GoDomainEnum.MF); //Gene Ontology Molecular Function
        	}
        }
        
        return gdlist;
    }
	

	public static Object[] findGeneOntologyFromGeneName(DataTypeEnum microArrayType, String geneName) {
		Query q;
		q=entityManager().createNativeQuery("SELECT str_gobp,str_gocc,str_gomf FROM gene_description WHERE micro_array_type=? and (gene_symbol=? or str_agi_name=? or swiss_prot=? or flybase=?)");

		q.setParameter(1, microArrayType.getId());
		q.setParameter(2, geneName);
		q.setParameter(3, geneName);
		q.setParameter(4, geneName);
		q.setParameter(5, geneName);
        List<Object[]> reslist=q.getResultList();
        return reslist.get(0);
    }
	
	public static long getNumProbeSets(DataTypeEnum microArrayType) {
		TypedQuery<Long> q=entityManager().createQuery("SELECT count(*) FROM GeneDescription o WHERE microArrayType=?", Long.class);
		q.setParameter(1, microArrayType.getId());
		return q.getSingleResult();
    }
	
	public static long getNumGenes(DataTypeEnum microArrayType) {
		TypedQuery<Long> q;
		if (microArrayType==DataTypeEnum.ATH1121501){
			q=entityManager().createQuery("SELECT count(distinct strAgiName) from GeneDescription o WHERE microArrayType=?", Long.class);
		}else{
			q=entityManager().createQuery("SELECT count(distinct geneSymbol) from GeneDescription o WHERE microArrayType=?", Long.class);
		}			
		q.setParameter(1, microArrayType.getId());
		return q.getSingleResult();
    }
	
	@Deprecated
	public static long getNumProbeSetsForGeneOntology(DataTypeEnum microArrayType,String goID) {		
		TypedQuery<Long> q=entityManager().createQuery("SELECT count(*) from GeneDescription o WHERE ( strGoBP LIKE ? or strGoMF LIKE ? or strGoCC LIKE ? ) and microArrayType=?", Long.class);
		q.setParameter(1, goID);
		q.setParameter(2, goID);
		q.setParameter(3, goID);
		q.setParameter(4, microArrayType.getId());
		return q.getSingleResult();
    }

	@Deprecated
	public static long getNumGenesForGeneOntology(DataTypeEnum microArrayType,String goID) {		
		TypedQuery<Long> q;
		if (microArrayType==DataTypeEnum.ATH1121501){
			q=entityManager().createQuery("SELECT count(distinct strAgiName) from GeneDescription o WHERE ( strGoBP LIKE ? or strGoMF LIKE ? or strGoCC LIKE ? ) and microArrayType=?", Long.class);
		}else{
			q=entityManager().createQuery("SELECT count(distinct geneSymbol) from GeneDescription o WHERE ( strGoBP LIKE ? or strGoMF LIKE ? or strGoCC LIKE ? ) and microArrayType=?", Long.class);
		}
		q.setParameter(1, goID);
		q.setParameter(2, goID);
		q.setParameter(3, goID);
		q.setParameter(4, microArrayType.getId());
		return q.getSingleResult();
    }
	
	private List<String> getAGI(String agi){
		List<String> agiList=null;
		if (agi!=null){
			agiList=new ArrayList<String>();
			String[] agis= agi.split(SEP_GO1);
			for (int i=0;i<agis.length;i++){
				agiList.add(agis[i]);
			}
		}
		
		return agiList;
	}
	
	static List<GODescription> getGO(String goStr, GoDomainEnum goDomain) {
		List<GODescription> goArray=null;
		if (!EMPTY.equals(goStr)){
		    String[] goEntry=goStr.split(SEP_GO1);
		    goArray=new ArrayList<GODescription>(goEntry.length);
		    for (int i=0;i<goEntry.length;i++){
		    	String goAttrs[]=goEntry[i].split(SEP_GO2);
		    	if (goAttrs.length>1)
		    		goArray.add(new GODescription(goAttrs[0], goAttrs[1], goAttrs[2], goDomain));
		        
		    }
		}
		return goArray;
	}
}
