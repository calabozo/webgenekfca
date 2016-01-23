package es.uc3m.tsc.kfca.tools;


import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.kfca.tools.KFCAConceptsBordat;
import es.uc3m.tsc.kfca.tools.KFCAConceptsSet;
import es.uc3m.tsc.math.ArrayUtils;

@RunWith(JUnit4.class)
public class KFCAConceptsBordatTest {

	@Test
	public void testMaxModPartition(){
		boolean [][] R={{true,true,false,false,false,true},				
				{true,false,false,false,true,true},				
				{false,false,false,false,false,false}};
		
		int [] P ={0,1,2,3,4,5};
		int [] O ={0,1,2};
		
		int expMaxMod[][]={{0,5},{1},{4},{2,3}};
		
		ArrayList<int[]> part=KFCAConceptsBordat.getMaxModPartition(P,O,R);
		for (int i=0;i<part.size();i++){
			System.out.print(Arrays.toString(part.get(i))+",");
		}
		
		int[][] aPart=new int[0][0];
		aPart=part.toArray(aPart);
		
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aPart,expMaxMod));
		
		ArrayList<int[]> nonDominating=KFCAConceptsBordat.getNonDominatingMaxMod(part,O,R);		
		System.out.println("-");
		for (int i=0;i<nonDominating.size();i++){
			System.out.println(Arrays.toString(nonDominating.get(i)));
		}

		int expND[][]={{0,5}};
		int[][] aND=new int[0][0];
		aND=nonDominating.toArray(aND);
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aND,expND));
		
		
	}
    
	@Test
	public void testMaxModPartition2(){
		boolean [][] R={{false,true,true,true,true,false,false,false},
				{true,true,true,false,false,false,true,true},
				{true,true,false,false,false,true,true,true},
				{false,false,false,true,true,false,false,false},
				{false,false,true,true,false,false,false,false},
				{true,false,false,false,false,false,false,true}};
		
		int [] P ={1,2,3,4,5,6};
		int [] O ={1,2,5};
		
		int expMaxMod[][]={{1,6},{2},{5},{3,4}};
		
		ArrayList<int[]> part=KFCAConceptsBordat.getMaxModPartition(P,O,R);
		for (int i=0;i<part.size();i++){
			System.out.print(Arrays.toString(part.get(i))+",");
		}
		
		int[][] aPart=new int[0][0];
		aPart=part.toArray(aPart);
		
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aPart,expMaxMod));
		
		ArrayList<int[]> nonDominating=KFCAConceptsBordat.getNonDominatingMaxMod(part,O,R);		
		System.out.println("-");
		for (int i=0;i<nonDominating.size();i++){
			System.out.print(Arrays.toString(nonDominating.get(i))+",");
		}

		int expND[][]={{1,6}};
		int[][] aND=new int[0][0];
		aND=nonDominating.toArray(aND);
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aND,expND));
		
		System.out.println("-");		
	}

	@Test
	public void testMaxModPartition3(){
		boolean [][] R={{false,true,true,true,true,false,false,false},
				{true,true,true,false,false,false,true,true},
				{true,true,false,false,false,true,true,true},
				{false,false,false,true,true,false,false,false},
				{false,false,true,true,false,false,false,false},
				{true,false,false,false,false,false,false,true}};
		
		int [] P ={0,1,2,3,4,5,6,7};
		int [] O ={0,1,2,3,4,5};
		
		int expMaxMod[][]={{1},{2},{3},{4},{0,7},{6},{5}};
		
		ArrayList<int[]> part=KFCAConceptsBordat.getMaxModPartition(P,O,R);
		for (int i=0;i<part.size();i++){
			System.out.println(Arrays.toString(part.get(i)));
		}
		
		int[][] aPart=new int[0][0];
		aPart=part.toArray(aPart);
		
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aPart,expMaxMod));
		
		ArrayList<int[]> nonDominating=KFCAConceptsBordat.getNonDominatingMaxMod(part,O,R);		
		System.out.println("-");
		for (int i=0;i<nonDominating.size();i++){
			System.out.println(Arrays.toString(nonDominating.get(i)));
		}

		int expND[][]={{1},{2},{3},{0,7}};
		int[][] aND=new int[0][0];
		aND=nonDominating.toArray(aND);
		Assert.assertTrue("Error ModPartition",ArrayUtils.equal(aND,expND));
		
		System.out.println("-");
		KFCAConceptsSet c=KFCAConceptsBordat.getConcepts(R);
		
		Assert.assertEquals("Wrong number of concepts", 13, c.getNumberConcepts());
		Assert.assertEquals("Wrong number of concepts", 13, KFCAConceptsBordat.getNumberConcepts(R));
	}

	
    @Test
    public void testNumConcepts(){
    	boolean [][] I={{true,true,false,true,false,true,false},
		         {true,true,false,true,true,false,false},
		         {true,true,false,true,true,true,true},
		         {true,false,true,false,true,true,false},
		         {false,true,false,true,false,false,false},
		         {true,false,false,false,false,true,false}};
    	
    	boolean [][] expectExtents={{true,true,true,true,true,true},
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
                    
    	KFCAConceptsSet c=KFCAConceptsBordat.getConcepts(I);    	
    	int numConcepts=c.getNumberConcepts();
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, numConcepts);
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, KFCAConceptsBordat.getNumberConcepts(I));
    	
    	boolean b=ArrayUtils.equalByRows(c.getExtents(),expectExtents);
    	Assert.assertTrue("Wrong extents",b);
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
    	
    	KFCAConceptsSet c=KFCAConceptsBordat.getConcepts(I);	
    	int numConcepts=c.getNumberConcepts();
    	Assert.assertEquals("wrong number of concepts", expectExtents.length, numConcepts);
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, KFCAConceptsBordat.getNumberConcepts(I));
    	
    	
    	boolean[][] A=c.getExtents();    	    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equalByRows(A,expectExtents));    	    	
    	boolean[][] B=c.getIntents();
    	System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equalByRows(B,expectIntents));
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
    	
    	KFCAConceptsSet c=KFCAConceptsBordat.getConcepts(I);
    	
    	int numConcepts=c.getNumberConcepts();
    	Assert.assertEquals("wrong number of concepts", expectExtents.length, numConcepts);
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, KFCAConceptsBordat.getNumberConcepts(I));
    	
    	
    	boolean[][] A=c.getExtents();    	    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equalByRows(A,expectExtents));    	    	
    	boolean[][] B=c.getIntents();
    	System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equalByRows(B,expectIntents));    	
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
    	
    	
    	KFCAConceptsSet c=KFCAConceptsBordat.getConcepts(I);
    	
    	int numConcepts=c.getNumberConcepts();
    	Assert.assertEquals("wrong number of concepts", expectExtents.length, numConcepts);
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, KFCAConceptsBordat.getNumberConcepts(I));
    	
    	
    	boolean[][] A=c.getExtents();    	    	
    	//System.out.println(Arrays.deepToString(A));
    	Assert.assertTrue("Error in Extents",ArrayUtils.equalByRows(A,expectExtents));    	    	
    	boolean[][] B=c.getIntents();
    	//System.out.println(Arrays.deepToString(B));
    	Assert.assertTrue("Error in Intents",ArrayUtils.equalByRows(B,expectIntents));
    }
	@Test
	public void testNumConcepts5(){
		boolean[][] I={{false, false, false, false}, {false, false, true, true}, {true, true, false, false}, {true, true, true, true}};
    	
		Assert.assertEquals("Wrong number of concepts", 4, KFCAConceptsBordat.getNumberConcepts(I));
	}
   
}
