// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uc3m.tsc.genetools;

import es.uc3m.tsc.genetools.GoTerm;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect GoTerm_Roo_Jpa_Entity {
    
    declare @type: GoTerm: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long GoTerm.id;
    
    @Version
    @Column(name = "version")
    private Integer GoTerm.version;
    
    public Long GoTerm.getId() {
        return this.id;
    }
    
    public void GoTerm.setId(Long id) {
        this.id = id;
    }
    
    public Integer GoTerm.getVersion() {
        return this.version;
    }
    
    public void GoTerm.setVersion(Integer version) {
        this.version = version;
    }
    
}
