package es.uc3m.tsc.graph;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.math.ArrayUtils;

@RunWith(JUnit4.class)
public class GraphFCATest {

	
	@Test
	public void testGraph(){
		boolean [][] I={
		    	{true,false,false,false,false,false,false,false},
		    	{false,true,false,false,false,false,false,false},
		    	{true,true,false,false,false,false,false,false},
		    	{false,false,true,false,false,false,false,false},
		    	{false,false,false,true,false,false,false,false},
		    	{false,false,false,false,false,false,false,false},
		    	{false,false,false,false,false,false,false,false}
		    	};

		boolean [][] eA={
		    	{false,true,true,false,true,false,true},
		    	{false,false,false,true,false,false,false},
		    	{false,false,false,true,false,false,false},
		    	{false,false,false,false,false,true,false},
		    	{false,false,false,false,false,true,false},
		    	{false,false,false,false,false,false,false},
		    	{false,false,false,false,false,true,false}
		    	};
		
		
		boolean[][] A=GraphFCA.getAdjacentMatrix(I);
		System.out.println(Arrays.deepToString(A));		
    	Assert.assertTrue("Error adding",ArrayUtils.equal(A, eA));   
		
	}
	
	@Test
	public void testJson(){
		boolean [][] I={
		    	{true,false,false,false,false,false,false,false},
		    	{false,true,false,false,false,false,false,false},
		    	{true,true,false,false,false,false,false,false},
		    	{false,false,true,false,false,false,false,false},
		    	{false,false,false,true,false,false,false,false},
		    	{false,false,false,false,false,false,false,false},
		    	{false,false,false,false,false,false,false,false}
		    	};

		String json=GraphFCA.getJSONjit(I, null, null);
		//System.out.println(json);				
		String exp="[\n"
				+"{\"adjacencies\": [\n"
				+"\"node1\",\"node2\",\"node4\",\"node6\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node0\",\n"
				+"\"name\": \"node0\"},{\"adjacencies\": [\n"
				+"\"node3\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node1\",\n"
				+"\"name\": \"node1\"},{\"adjacencies\": [\n"
				+"\"node3\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node2\",\n"
				+"\"name\": \"node2\"},{\"adjacencies\": [\n"
				+"\"node5\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node3\",\n"
				+"\"name\": \"node3\"},{\"adjacencies\": [\n"
				+"\"node5\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node4\",\n"
				+"\"name\": \"node4\"},{\"adjacencies\": [\n"
				+"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node5\",\n"
				+"\"name\": \"node5\"},{\"adjacencies\": [\n"
				+"\"node5\"\n"
				+"],\"data\": {\"$color\": \"#83548B\",\"$type\": \"circle\",\"$dim\": 10},\n"
				+"\"id\": \"node6\",\n"
				+"\"name\": \"node6\"}\n"
				+"]";
		Assert.assertEquals("Error JSON",json,exp);
		
	}
	
	@Test
	public void testJSONfullFCAGraph(){
		boolean [][] I={
    	    	{true,false,false,false},
    	    	{false,false,false,false},
    	    	{false,true,false,false},
    	    	{false,false,false,false},
    	    	{true,true,false,false},
    	    	{false,false,false,false},
    	    	{false,false,true,false},
    	    	{false,false,false,false},
    	    	{false,false,false,true},
    	    	{false,false,false,false},
    	    	{false,false,false,false},
    	    	{false,false,false,false},
    	    	{false,false,false,false},
    	    	{false,false,false,false},
    	    	{true,true,true,true},
    	    	{true,true,true,true}
    	    	};
    	    	
    	String json=GraphFCA.getJSONfullFCAGraph(I);
    	System.out.println(json);
    	System.out.println("---");
    	String exp="{\"numattr\":4,\"fca\": [{\"n\":9},{\"n\":1},{\"n\":1},{\"n\":1},{\"n\":1},{\"n\":-1},{\"n\":-1},{\"n\":-1},{\"n\":1},{\"n\":-1},{\"n\":-1},{\"n\":-1},{\"n\":-1},{\"n\":-1},{\"n\":-1},{\"n\":2}]}";
    	Assert.assertEquals("Error JSON",json,exp);
	}
	
	@Test
	public void testGetAdjacentMatrix() throws FileNotFoundException, UnsupportedEncodingException{
		List<String> rows=new ArrayList<String>();
		rows.add("row1");
		rows.add("row2");
		rows.add("row3");
		rows.add("row4");
		rows.add("row5");
		
		List<String> cols=new ArrayList<String>();
		cols.add("A");
		cols.add("B");
		cols.add("C");
		cols.add("D");
		
		HashMap<String,Set<String>> map=new HashMap<String,Set<String>>();
		map.put("row1", new HashSet<String>(Arrays.asList("A","B")));
		map.put("row2", new HashSet<String>(Arrays.asList("C","D")));
		map.put("row3", new HashSet<String>(Arrays.asList("A","B","C")));
		map.put("row4", new HashSet<String>(Arrays.asList("B","C","D")));
		map.put("row5", new HashSet<String>(Arrays.asList("A","B","C","D")));
		
		boolean[][] I= GraphFCA.getAdjacentMatrix(map,cols, rows);
		boolean[][] E={{true, true, false, false}, {false, false, true, true}, {true, true, true, false}, {false, true, true, true}, {true, true, true, true}};
		Assert.assertTrue(ArrayUtils.equal(I, E));

		String s="B\n\n5\n4\n\nrow1\nrow2\nrow3\nrow4\nrow5\nA\nB\nC\nD\nXX..\n..XX\nXXX.\n.XXX\nXXXX\n";
		Assert.assertEquals(s, GraphFCA.getConexpCXTFormat(I,cols, rows));
		GraphFCA.getConexpCXTFormat(I,cols, rows, "/tmp/context.cxt");
	}
}
