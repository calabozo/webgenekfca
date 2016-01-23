package es.uc3m.tsc.kfca.tools;


import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.math.ArrayUtils;

@RunWith(JUnit4.class)
public class KFCAConceptsIntersectionTest {

    
    @Test
    public void testNumConcepts(){
    	boolean [][] I={{true,true,false,true,false,true,false},
		         {true,true,false,true,true,false,false},
		         {true,true,false,true,true,true,true},
		         {true,false,true,false,true,true,false},
		         {false,true,false,true,false,false,false},
		         {true,false,false,false,false,true,false}};
    	
    	boolean [][] expectConcepts={{true,true,true,true,true,true},
    								 {true,true,true,true,false,true},
    								 {true,true,true,false,true,false},
    								 {true,true,true,false,false,false},
    								 {true,false,true,true,false,true},
    								 {true,false,true,false,false,false},
    								 {false,true,true,true,false,false},
    								 {false,true,true,false,false,false},
    								 {false,false,true,true,false,false},
    								 {false,false,true,false,false,false},
    								 {false,false,false,true,false,false},
    								 {false,false,false,false,false,false}};
                    
    	boolean[][] A=KFCAConceptsIntersection.getExtents(I);
    	boolean b=ArrayUtils.equal(A,expectConcepts);
    	Assert.assertTrue("Wrong number of concepts",b);
    	int numConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	Assert.assertEquals(expectConcepts.length, numConcepts);
    }
    
    
    @Test
    public void testNumConcepts2(){
    	boolean [][] I={
    			{true,true,true,true},
    			{true,true,false,false},
    			{true,true,true,true},
    			{true,true,false,false},
    			{true,true,false,false},
    			{false,false,true,true},
    			{false,true,false,false},
    			{false,false,false,false},
    			{false,false,true,true},
    			{false,false,true,false}
    			};
    	
    	boolean [][] expectExtents={
    			{true,true,true,true,true,true,true,true,true,true},
    			{true,true,true,true,true,false,true,false,false,false},
    			{true,true,true,true,true,false,false,false,false,false},
    			{true,false,true,false,false,true,false,false,true,true},
    			{true,false,true,false,false,false,false,false,false,false},
    			{true,false,true,false,false,true,false,false,true,false}
    			};
    	
    	
    	boolean [][] expectIntents={
    		{false,false,false,false},
    		{false,true,false,false},
    		{true,true,false,false},
    		{false,false,true,false},
    		{true,true,true,true},
    		{false,false,true,true}
    		};
    	
    	boolean[][] A=KFCAConceptsIntersection.getExtents(I);    	    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equal(A,expectExtents));    	    	
    	boolean[][] B=KFCAConceptsIntersection.getIntents(I,A);
    	//System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equal(B,expectIntents));
    	
    	int numConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	Assert.assertEquals(expectExtents.length, numConcepts);
    }
    
    @Test
    public void testNumConcepts3(){
    	boolean [][] I={
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,false,false,false},
    	{false,true,false,false}
    	};
    	

    	boolean [][] expectExtents={
    			{true,true,true,true,true,true,true,true,true,true},
    			{false,false,false,false,false,false,false,false,false,true},
    			{false,false,false,false,false,false,false,false,false,false}
    			};
    	
    	boolean [][] expectIntents={
    		{false,false,false,false},
    		{false,true,false,false},
    		{true,true,true,true}
    		};
    	
    	boolean[][] A=KFCAConceptsIntersection.getExtents(I);    	    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equal(A,expectExtents));    	    	
    	boolean[][] B=KFCAConceptsIntersection.getIntents(I,A);
    	//System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equal(B,expectIntents));
    	
    	int numConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	Assert.assertEquals(expectExtents.length, numConcepts);
    	
    }
    
    @Test
    public void testNumConcepts4(){
    	boolean [][] I={
    	{true,false,false,false,false,false,false,false},
    	{false,true,false,false,false,false,false,false},
    	{true,true,false,false,false,false,false,false},
    	{false,false,true,false,false,false,false,false},
    	{false,false,false,true,false,false,false,false},
    	{false,false,false,false,false,false,false,false},
    	{false,false,false,false,false,false,false,false}
    	};
    	

    	boolean [][] expectExtents={
    			{true, true, true, true, true, true, true}, 
    			{true, false, true, false, false, false, false},
    			{false, true, true, false, false, false, false},
    			{false, false, true, false, false, false, false},
    			{false, false, false, true, false, false, false},
    			{false, false, false, false, false, false, false},
    			{false, false, false, false, true, false, false}    			
    			};
    	
    	boolean [][] expectIntents={    			
    			{false, false, false, false, false, false, false, false},
    			{true, false, false, false, false, false, false, false},
    			{false, true, false, false, false, false, false, false},
    			{true, true, false, false, false, false, false, false},
    			{false, false, true, false, false, false, false, false},
    			{true, true, true, true, true, true, true, true},
    			{false, false, false, true, false, false, false, false}
    		};
    	
    	
    	boolean[][] A=KFCAConceptsIntersection.getExtents(I);    	    	
    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equal(A,expectExtents));    	    	
    	boolean[][] B=KFCAConceptsIntersection.getIntents(I,A);
    	//System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equal(B,expectIntents));   
    	
    	int numConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	Assert.assertEquals(expectExtents.length, numConcepts);
    }
    
	@Test
	public void testNumConcepts5(){
		boolean[][] I={{false, false, false, false}, {false, false, true, true}, {true, true, false, false}, {true, true, true, true}};
		
		Assert.assertEquals("Wrong number of concepts", 4, KFCAConceptsIntersection.getNumberConcepts(I));
	}
    
    @Test
    public void testNumElementsBySingleConcept(){
    	boolean [][] I={
    	    	{true,false,false,false},  // 1 - 1 
    	    	{false,false,false,false}, // 0 - 
    	    	{false,true,false,false},  // 2 - 1
    	    	{false,false,false,false}, // 0 -  
    	    	{true,true,false,false},   // 3 - 1
    	    	{false,false,false,false}, // 0 -  
    	    	{false,false,true,false},  // 4 - 1
    	    	{false,false,false,false}, // 0 - 
    	    	{false,false,false,true},  // 8 - 1
    	    	{false,false,false,false}, // 0 - 
    	    	{false,false,false,false}, // 0 - 
    	    	{false,false,false,false}, // 0 - 
    	    	{false,false,false,false}, // 0 - 
    	    	{false,false,false,false}, // 0 - 9
    	    	{true,true,true,true},     // 15- 
    	    	{true,true,true,true}      // 15- 2
    	    	};
    	
    	int[] eN={9, 1, 1, 1, 1,-1, -1, -1, 1, -1,-1, -1, -1, -1, -1, 2};
    	
    	int[] n=KFCAConceptsIntersection.numElementsBySingleConcept(I);
    	//System.out.println(Arrays.toString(n));
    	Assert.assertTrue("Error in numElementsBySingleConcept",Arrays.equals(eN, n));
    	
    	int expectedNumConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	int tstNumConcepts=0;
    	for (int i:n){ if (i>=0) tstNumConcepts++;}    	    	
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, tstNumConcepts);
    	
    	
    	HashMap<Integer,Integer> n2=KFCAConceptsIntersection.numElementsBySingleConcept(I,16);
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, n2.size());
    	for (int id=0;id<n.length;id++){
    		if (n[id]>0){
    			int numElements=n2.get(id);
    			Assert.assertEquals("Error in numElementsBySingleConcept",n[id],numElements);
    		}
    	}
    	
    	HashMap<Integer,Integer> n3=KFCAConceptsIntersection.numElementsBySingleConcept(I,2);
    	Assert.assertEquals("Wrong number of concepts", 2, n3.size());
    	Assert.assertEquals("Error in numElementsBySingleConcept",n3.get(15).intValue(),2);
    	Assert.assertEquals("Error in numElementsBySingleConcept",n3.get(0).intValue(),9);
    }
    
    @Test
    public void testNumElementsBySingleConcept2(){
    	boolean [][] I={
    	    	{false,false,false,false},  // 0 - 1 
    	    	{false,false,false,false},  // 0 - 
    	    	{false,true,false,false},   // 2 -
    	    	{true,true,false,false},    // 3 -
    	    	{false,false,true,false},   // 4 -
    	    	{false,false,false,false},  // 0 -
    	    	{false,true,true,false},    // 6 -
    	    	{true,true,true,false},     // 7 -
    	    	{false,false,false,true},   // 8 -
    	    	{true,false,false,true},    // 9 -
    	    	{false,true,false,true},    // 10 -
    	    	{true,true,false,true},     // 11 -
    	    	{false,false,true,true},    // 12 -
    	    	{true,false,true,true},     // 13 -
    	    	{false,true,true,true},     // 14 -
    	    	{true,true,true,true}      // 15 -    	    	
    	    	};
    	
    	int[] eN={3, 0, 1, 1, 1,0, 1, 1, 1, 1,1, 1, 1, 1, 1, 1};
    	
    	int[] n=KFCAConceptsIntersection.numElementsBySingleConcept(I);
    	//System.out.println(Arrays.toString(n));
    	Assert.assertTrue("Error in numElementsBySingleConcept",Arrays.equals(eN, n));
    	
    	int expectedNumConcepts=KFCAConceptsIntersection.getNumberConcepts(I);
    	int tstNumConcepts=0;
    	for (int i:n){ if (i>=0) tstNumConcepts++;}    	    	
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, tstNumConcepts);
    	
    	HashMap<Integer,Integer> n2=KFCAConceptsIntersection.numElementsBySingleConcept(I,16);
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, n2.size()+2);
    	for (int id=0;id<n.length;id++){
    		if (n[id]>0){
    			int numElements=n2.get(id);
    			Assert.assertEquals("Error in numElementsBySingleConcept",n[id],numElements);
    		}
    	}
    	
    }
}
