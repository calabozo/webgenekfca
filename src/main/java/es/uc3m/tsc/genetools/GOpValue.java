package es.uc3m.tsc.genetools;

import java.util.HashSet;
import java.util.Set;

import org.springframework.roo.addon.javabean.RooJavaBean;

import es.uc3m.tsc.math.ClusterAnalysis;
import flexjson.JSONSerializer;

@RooJavaBean
public class GOpValue{
	String GOID; //GO Identifier
	String description; //GO description	
	int numTotal; //Number of times that appears in the genome
	double pValue;
	Set<String> genes;//List of genes or probesets in the GOTerm cluster
	GoDomainEnum ontology;
	
	public GOpValue(String id, String description, int numTotal, GoDomainEnum ontology){
		this.GOID=id;
		this.description=description;
		this.numTotal=numTotal;		
		this.pValue=-1;
		this.genes=new HashSet<String>();
		this.ontology=ontology;
	}
	public String toString(){
		//return "{\"goid\":\""+GOID+"\",\"num\":"+this.numAppear+",\"numTotal\":"+this.numTotal+", \"pvalue\":"+this.pValue+"}";
		return "goid:"+GOID+" num:"+this.getNumAppear()+" numTotal:"+this.numTotal+" pvalue:"+this.pValue+"\n";
	}
	public int getNumAppear(){
		return this.genes.size();
	}
	public void addGene(String gen){
		this.genes.add(gen);
	}
	public double calcpValue(int clusterSize, int genomeSize){
		this.pValue=ClusterAnalysis.pValue(this.getNumAppear(), clusterSize, this.numTotal, genomeSize);
		return this.pValue;
	}
	public String toJson() {
        return new JSONSerializer().exclude("class").serialize(this);	
    }
}
