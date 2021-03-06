// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.gene;

import es.uc3m.tsc.gene.DataMatrix;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect DataMatrix_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager DataMatrix.entityManager;
    
    public static final List<String> DataMatrix.fieldNames4OrderClauseFilter = java.util.Arrays.asList("preprocessor", "name", "description", "colNames", "rowNames", "rawData", "processFiles", "celIDFiles", "microArrayType");
    
    public static final EntityManager DataMatrix.entityManager() {
        EntityManager em = new DataMatrix().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long DataMatrix.countDataMatrixes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM DataMatrix o", Long.class).getSingleResult();
    }
    
    public static List<DataMatrix> DataMatrix.findAllDataMatrixes() {
        return entityManager().createQuery("SELECT o FROM DataMatrix o", DataMatrix.class).getResultList();
    }
    
    public static List<DataMatrix> DataMatrix.findAllDataMatrixes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM DataMatrix o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, DataMatrix.class).getResultList();
    }
    
    public static DataMatrix DataMatrix.findDataMatrix(Long id) {
        if (id == null) return null;
        return entityManager().find(DataMatrix.class, id);
    }
    
    public static List<DataMatrix> DataMatrix.findDataMatrixEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM DataMatrix o", DataMatrix.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<DataMatrix> DataMatrix.findDataMatrixEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM DataMatrix o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, DataMatrix.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void DataMatrix.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void DataMatrix.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DataMatrix attached = DataMatrix.findDataMatrix(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void DataMatrix.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void DataMatrix.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public DataMatrix DataMatrix.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DataMatrix merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
