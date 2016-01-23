package es.uc3m.tsc.genetools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GeneOntology {
	
	private static String TXT_name="name: ";
	private static String TXT_namespace="namespace: ";
	private static String TXT_def="def: ";
	private static String TXT_is_a="is_a: ";
	
	public String id;
	public String name;
	public String namespace;
	public String def;
	public ArrayList<String> is_a;
	
	public GeneOntology(String id){
		this.id=id;
		this.name=null;
		this.def=null;
		this.is_a=null;
	}

	
	public static GeneOntology parseGO(String id,BufferedReader br) throws IOException{
		GeneOntology go=new GeneOntology(id);
		
		String strLine;
		
		while (( strLine=br.readLine()) !=null && !strLine.equals("")){
			if (strLine.startsWith(TXT_name)){
				go.name=strLine.substring(TXT_name.length());
			}
			if (strLine.startsWith(TXT_namespace)){
				go.namespace=strLine.substring(TXT_namespace.length());
			}
			if (strLine.startsWith(TXT_def)){
				go.def=strLine.substring(TXT_def.length());
			}
			if (strLine.startsWith(TXT_is_a)){
				if (go.is_a==null) go.is_a=new ArrayList<String>();
				String is_a=strLine.substring(TXT_is_a.length());
				go.is_a.add(is_a.substring(0,is_a.indexOf(' ')));
			}	
		}	
		return go;
	}

	public static String[] extractArrayIDs(ArrayList<GeneOntology> goList){
		String[] goStrArray=new String[goList.size()];
		for (int i=0;i<goList.size();i++){
			goStrArray[i]=goList.get(i).getId();
		}
		
		return goStrArray;
	}
	
	public String[] getAsArray(){
		String[] goDescriptions=new String[4];
		goDescriptions[0]=this.name;
		goDescriptions[1]=this.namespace;
		goDescriptions[2]=this.def;
		
		
		return goDescriptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		GeneOntology other = (GeneOntology) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getDef() {
		return def;
	}


	public ArrayList<String> getIs_a() {
		return is_a;
	}


	@Override
	public String toString() {
		return "GeneOntology [id=" + id + ", name=" + name + ", def=" + def
				+ ", is_a=" + is_a + "]";
	}
	
}
