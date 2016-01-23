package es.uc3m.tsc.genetools;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import es.uc3m.tsc.gene.DataTypeEnum;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class GoTerm {

    @NotNull
    private String goid;
    @NotNull
    private String description;
    @NotNull
    private Integer microArrayType;
    private Integer numProbesets;
    private Integer numGenes;
    @Lob
    private String strProbesets;
    @Lob
    private String strGeneSymbols;
    @Column(length = 3)
    private String ontology;
    @Transient
    private String[] probesets;
    @Transient
    private String[] genes;
    
    public static GoTerm getGoID(DataTypeEnum microArrayType,String goid) {
    	TypedQuery<GoTerm> q=entityManager().createQuery("SELECT o FROM GoTerm o WHERE  microArrayType=? and goid=?", GoTerm.class);
    	q.setParameter(1, microArrayType.getId());
		q.setParameter(2, goid);		
		GoTerm gd=q.getSingleResult();
        
       
        if (gd!=null){        	
        	gd.probesets=gd.strProbesets.split(",");
        	gd.genes=gd.strGeneSymbols.split(",");
        	
        }
        return gd;
    }
    
    public static int getNumProbesetsForGOid(DataTypeEnum microArrayType,String goid) {
    	//TypedQuery<Integer> q=entityManager().createQuery("SELECT numProbesets FROM GoTerm o WHERE microArrayType=? and goid=?", Integer.class);
    	Query q=entityManager().createNativeQuery("SELECT num_probesets FROM go_term WHERE micro_array_type=? and goid=?");
    	q.setParameter(1, microArrayType.getId());
		q.setParameter(2, goid);		
		
        Integer numProbesets=(Integer) q.getSingleResult();
        return numProbesets;
    }
    
    public static int getNumGenesForGOid(DataTypeEnum microArrayType,String goid) {
    	TypedQuery<Integer> q=entityManager().createQuery("SELECT numGenes FROM GoTerm o WHERE microArrayType=? and goid=?", Integer.class);
    	q.setParameter(1, microArrayType.getId());
		q.setParameter(2, goid);		
		
        Integer numGenes=q.getSingleResult();
        return numGenes;
    }
}
