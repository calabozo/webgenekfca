// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.gene;

import es.uc3m.tsc.gene.Preprocessor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Preprocessor_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Preprocessor.entityManager;
    
    public static final List<String> Preprocessor.fieldNames4OrderClauseFilter = java.util.Arrays.asList("preprocessorCache", "name", "preprocessorType", "dataMatrix", "maxPhiToExplore", "groupName", "groupId", "rowNames", "groupMatrix", "geneExpressionType", "algorithm", "kfcaResults", "resultException", "creationDate", "geneInfo", "logger");
    
    public static final EntityManager Preprocessor.entityManager() {
        EntityManager em = new Preprocessor().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Preprocessor.countPreprocessors() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Preprocessor o", Long.class).getSingleResult();
    }
    
    public static List<Preprocessor> Preprocessor.findAllPreprocessors() {
        return entityManager().createQuery("SELECT o FROM Preprocessor o", Preprocessor.class).getResultList();
    }
    
    public static List<Preprocessor> Preprocessor.findAllPreprocessors(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Preprocessor o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Preprocessor.class).getResultList();
    }
    
    public static List<Preprocessor> Preprocessor.findPreprocessorEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Preprocessor o", Preprocessor.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Preprocessor> Preprocessor.findPreprocessorEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Preprocessor o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Preprocessor.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Preprocessor.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Preprocessor.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Preprocessor attached = Preprocessor.findPreprocessor(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Preprocessor.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Preprocessor.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Preprocessor Preprocessor.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Preprocessor merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}