package es.uc3m.tsc.general;

import java.util.List;

public class JSONConcept{
	public JSONConcept(List<String> o, List<String> m, Double h1) {
		this.o = o;
		this.m = m;
	}
	public JSONConcept(){};
	public List<String> getO() {return o;}
	public void setO(List<String> o) {	this.o = o;	}
	public List<String> getM() {return m;}
	public void setM(List<String> m) {	this.m = m;	}	
	public List<String> o;
	public List<String> m;	
}