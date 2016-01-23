package es.uc3m.tsc.genetools;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import flexjson.JSONSerializer;


@RooJavaBean
@RooToString
public class GODescription {
	
	public String goID;
	public String description;
	public String inferred;
	public GoDomainEnum goDomain;
	
	public GODescription(String goID, String description, String inferred, GoDomainEnum domain) {
		this.goID=goID;
		this.description=description;
		this.inferred=inferred;
		this.goDomain=domain;
	}
    public String toJson() {
        return new JSONSerializer().exclude("class").serialize(this);
    }
}
