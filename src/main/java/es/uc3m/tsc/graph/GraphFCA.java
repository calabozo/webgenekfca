package es.uc3m.tsc.graph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import es.uc3m.tsc.kfca.tools.KFCAConceptsIntersection;
import es.uc3m.tsc.math.MaxPlus;

public class GraphFCA {

	public static boolean[][] getAdjacentMatrix(boolean[][] I){
		boolean[][] A=KFCAConceptsIntersection.getExtents(I);    	    	    	    	    
    	int m=A.length;
    	boolean[][] AdjacentMatrix=new boolean[m][m];
    	    	
    	for(int i=0;i<m;i++){
    		boolean[] rowUp=A[i];
    		for(int j=0;j<m;j++){
    			if (i==j){
    				AdjacentMatrix[i][j]=false;
    			}else{
    				boolean v=true;
    				boolean[] rowDown=A[j];
    				for (int k=0;k<m;k++){
    					if (rowDown[k] && !rowUp[k]){
    						v=false;
    						break;    						
    					}
    				}
    				AdjacentMatrix[i][j]=v;
    			}
        		
        	}	    	
    	}    	    	
    	return new MaxPlus(AdjacentMatrix).shortestAdjacentMatrix();
    	//return AdjacentMatrix;
    	
	}
	/*
	{
        "adjacencies": [
            "nodeA","nodeB",                
            ],
            "data": {
                "$color": "#83548B",
                "$type": "circle",
                "$dim": 10
              },
              "id": "nodeC",
              "name": "nodeC"
      }
	*/
	public static String getJSONjit(boolean[][] IMaxMinPlus,String[] colNames,String[] rowNames){
		StringBuffer jsonOut=new StringBuffer();
    	boolean[][] A=getAdjacentMatrix(IMaxMinPlus);
    	int dim=10;
    	jsonOut.append("[\n");
    	for (int i=0;i<A.length;i++){
    		boolean[] rowA=A[i];
    		if (i>0){
    			jsonOut.append(",");	
    		}
    		jsonOut.append("{\"adjacencies\": [\n");
    		boolean addColon=false;
    		for (int j=0;j<rowA.length;j++){
    			if (rowA[j]){
	    			if (addColon){
	    				jsonOut.append(",");	
	    			}else{
	    				addColon=true;
	    			}
	    			jsonOut.append("\"node"+j+"\"");
    			}
    		}
    		
    		jsonOut.append("\n],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": "+dim+"},");
    		jsonOut.append("\n\"id\": \"node"+i+"\",");
    		jsonOut.append("\n\"name\": \"node"+i+"\"}");
    		
    	}    	
    	jsonOut.append("\n]");
		return jsonOut.toString();
	}
	
/*
 * {"numattr":4,"fca":[
 *    {"n":10},
 *    {"n":0},
 *    {"n":10},
 *    {"n":10},
 *    {"n":50},
 *    {"n":10},
 *    {"n":10}
 *    ]} 	
 */
	public static String getJSONfullFCAGraph(boolean[][] I){
		StringBuffer jsonOut=new StringBuffer();
		int[] numConcepts=KFCAConceptsIntersection.numElementsBySingleConcept(I);
		String objNstr=",{\"n\":";
		String voidNstr=",{\"n\":-1}";
		
		jsonOut.append("{\"numattr\":"+I[0].length+",\"fca\": [");
		
		jsonOut.append("{\"n\":"+numConcepts[0]+"}");
		for (int i=1;i<numConcepts.length;i++){
			if (numConcepts[i]>0){
				jsonOut.append(objNstr+numConcepts[i]+"}");
			}else{
				jsonOut.append(voidNstr);
			}
		}
		jsonOut.append("]}");
		return jsonOut.toString();
	}
	
	public static boolean[][] getAdjacentMatrix(HashMap<String,Set<String>> vals,List<String> colNames, List<String> rowNames){
		boolean[][] ret=new boolean[rowNames.size()][colNames.size()];
		for (int i=0;i<rowNames.size();i++){
			Set<String> activeCols=vals.get(rowNames.get(i));
			for (String colName:activeCols){
				ret[i][colNames.indexOf(colName)]=true;
			}
		}
		return ret;
	}
	
	public static String getCSVFormat(boolean[][] I, List<String> colNames, List<String> rowNames) {
		StringBuilder str=new StringBuilder();
		int nr=rowNames.size();
		int nc=colNames.size();

		Iterator<String> iter=colNames.iterator();
		if (!iter.hasNext()) return "";
		str.append(iter.next());
		while (iter.hasNext()) str.append("," + iter.next());
		
		str.append("\n");
		for (int i=0; i < nr; i++) {
			str.append(rowNames.get(i));

			for (int j=0; j < nc; j++) {
				if (I[i][j]) {
					str.append(",1");
				} else {
					str.append(",0");
				}
			}
			str.append("\n");
		}
		return str.toString();
	}

	public static String getConexpCXTFormat(boolean[][] I,List<String> colNames, List<String> rowNames){
		StringBuilder str=new StringBuilder();
		int nr=rowNames.size();
		int nc=colNames.size();
		str.append("B\n");
		str.append("\n");
		str.append(nr+"\n");
		str.append(nc+"\n");
		str.append("\n");
		for (String rowName:rowNames) str.append(rowName+"\n");
		for (String colName:colNames) str.append(colName+"\n");
		for (int i=0;i<nr;i++){
			for (int j=0;j<nc;j++){
				if (I[i][j]){
					str.append("X");
				}else{
					str.append(".");
				}
			}
			str.append("\n");
		}
		return str.toString();
	}
	public static String getConexpCXTFormat(boolean[][] I,List<String> colNames, List<String> rowNames,String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		String ret=getConexpCXTFormat( I,colNames, rowNames);
		PrintWriter writer = new PrintWriter(fileName, "US-ASCII");
		writer.print(ret);
		writer.close();
		
		return ret;
		
	}
}
