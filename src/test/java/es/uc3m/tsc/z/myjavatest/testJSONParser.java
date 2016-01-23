package es.uc3m.tsc.z.myjavatest;


import java.util.ArrayList;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class testJSONParser {
	public class ColsSelected{		
		Boolean[] cols;
		ColsSelected(Boolean[] cols){ this.cols=cols;}
		void setCols(Boolean [] cols){this.cols=cols;}
		Boolean[] getCols(){return this.cols;}		
	}
	
	public class ColName{		
		String col;		
		ArrayList<String> cols;
		public ColName(){this.col=null;this.cols=new ArrayList<String>();};
		public ColName(String col,ArrayList<String> cols){ this.col=col; this.cols=cols;}
		void setCol(String col){this.col=col;}
		String getCol(){return this.col;}		
		void setCols(ArrayList<String> cols){this.cols=cols;}
		ArrayList<String> getCols(){return this.cols;}
		@Override
		public String toString() {
			return "ColName [col=" + col + ", cols=" + cols.size() + "]";
		}		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new testJSONParser().launch();
	}
	public void launch(){
		// TODO Auto-generated method stub
		Boolean[] cols={true,false,true,true};
		ArrayList<String>  colNames=new ArrayList<String>();
		colNames.add("a");
		colNames.add("b");
		colNames.add("c");
		colNames.add("d");

		
		ColsSelected c = new ColsSelected(cols);
		String json = new JSONSerializer().serialize(c);
		System.out.println(json);
		
		ColName cn = new ColName("myNewColName",colNames);
		String jsonColName = new JSONSerializer().serialize(cn);
		System.out.println(jsonColName);
		
		ColName colsName = new JSONDeserializer<ColName>().use(null, ColName.class).deserialize(jsonColName);
		System.out.println(colsName);
		
	}

}
