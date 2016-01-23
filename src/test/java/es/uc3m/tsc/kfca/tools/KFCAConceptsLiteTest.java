package es.uc3m.tsc.kfca.tools;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import es.uc3m.tsc.kfca.tools.KFCAConceptsLite;
import es.uc3m.tsc.math.ArrayUtils;

@RunWith(JUnit4.class)
public class KFCAConceptsLiteTest {

	
    

	@Test
	public void testNumConcepts0(){
		boolean [][] R={{false,true,true,true,true,false,false,false},
				{true,true,true,false,false,false,true,true},
				{true,true,false,false,false,true,true,true},
				{false,false,false,true,true,false,false,false},
				{false,false,true,true,false,false,false,false},
				{true,false,false,false,false,false,false,true}};
		
		
		Assert.assertEquals("Wrong number of concepts", 13, new KFCAConceptsLite().getNumberConcepts(R));
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
                    
    	
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, new KFCAConceptsLite().getNumberConcepts(I));
    	
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
    	
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, new KFCAConceptsLite().getNumberConcepts(I));
    	
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
    	
    	
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, new KFCAConceptsLite().getNumberConcepts(I));
    	    	    	    
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
    	
    	
    	Assert.assertEquals("Wrong number of concepts",expectExtents.length, new KFCAConceptsLite().getNumberConcepts(I));
    	
    }
    
	@Test
	public void testNumConcepts5(){
		boolean[][] I={{false, false, false, false}, {false, false, true, true}, {true, true, false, false}, {true, true, true, true}};
		
		Assert.assertEquals("Wrong number of concepts", 4, new KFCAConceptsLite().getNumberConcepts(I));
	}
	
	@Test
	public void testNumConcepts6(){
		long[] I={0,1,2,3,4,5,6,10,11,12};
		
		Assert.assertEquals("Wrong number of concepts", 12, new KFCAConceptsLite().getNumberConcepts(I,4));
	}
	
	@Test
	public void testNumConcepts7(){
		long[] I={0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 16, 19, 18, 21, 20, 23, 22, 25, 24, 27, 26, 29, 28, 31, 30, 34, 35, 32, 33, 39, 36, 37, 42, 43, 40, 41, 46, 47, 44, 45, 51, 50, 49, 48, 55, 54, 53, 52, 59, 58, 57, 56, 63, 60, 68, 69, 70, 71, 64, 65, 66, 67, 76, 77, 78, 79, 72, 74, 75, 84, 81, 80, 83, 82, 92, 88, 100, 98, 99, 96, 97, 111, 108, 106, 104, 116, 115, 114, 113, 112, 120, 137, 136, 139, 138, 141, 140, 143, 142, 129, 128, 131, 130, 133, 132, 135, 134, 152, 156, 144, 145, 146, 147, 148, 149, 150, 151, 170, 168, 172, 163, 162, 161, 160, 165, 164, 184, 188, 178, 179, 176, 177, 180, 204, 207, 206, 200, 202, 197, 196, 198, 193, 192, 195, 194, 216, 212, 208, 209, 210, 211, 236, 232, 228, 227, 226, 225, 224, 252, 248, 244, 242, 243, 240, 241};
		
		Assert.assertEquals("Wrong number of concepts", 175, new KFCAConceptsLite().getNumberConcepts(I,8));
	}
	
	@Test
	public void testNumConcepts8(){
		long[] I={0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 16, 19, 18, 21, 20, 23, 22, 25, 24, 27, 26, 29, 28, 31, 30, 34, 35, 32, 33, 38, 39, 36, 37, 42, 43, 40, 41, 46, 47, 44, 45, 51, 50, 49, 48, 55, 54, 53, 52, 59, 58, 57, 56, 63, 62, 61, 60, 68, 69, 70, 71, 64, 65, 66, 67, 76, 77, 78, 79, 72, 73, 74, 75, 85, 84, 87, 86, 81, 80, 83, 82, 92, 95, 89, 88, 91, 90, 102, 103, 100, 101, 98, 99, 96, 97, 110, 111, 108, 109, 106, 107, 104, 105, 116, 115, 114, 113, 112, 126, 124, 122, 120, 137, 136, 139, 138, 141, 140, 143, 142, 129, 128, 131, 130, 133, 132, 135, 134, 152, 153, 154, 155, 156, 157, 158, 159, 144, 145, 146, 147, 148, 149, 150, 151, 170, 169, 168, 175, 172, 163, 162, 161, 160, 167, 165, 164, 184, 188, 178, 179, 176, 177, 182, 180, 205, 204, 207, 206, 201, 200, 203, 202, 197, 196, 199, 198, 193, 192, 195, 194, 220, 216, 212, 208, 209, 210, 211, 236, 234, 232, 228, 227, 226, 225, 224, 252, 248, 244, 242, 243, 240, 241};
		
		Assert.assertEquals("Wrong number of concepts", 212, new KFCAConceptsLite().getNumberConcepts(I,8));
	}
	
	 @Test
    public void testNumElementsBySingleConcept(){
    	long [] I={
    	    	1, //{true,false,false,false},  // 1 - 1 
    	    	0, //{false,false,false,false}, // 0 - 
    	    	2, //{false,true,false,false},  // 2 - 1
    	    	0, //{false,false,false,false}, // 0 -  
    	    	3, //{true,true,false,false},   // 3 - 1
    	    	0, //{false,false,false,false}, // 0 -  
    	    	4, //{false,false,true,false},  // 4 - 1
    	    	0, //{false,false,false,false}, // 0 - 
    	    	8, //{false,false,false,true},  // 8 - 1
    	    	0, //{false,false,false,false}, // 0 - 
    	    	0, //{false,false,false,false}, // 0 - 
    	    	0, //{false,false,false,false}, // 0 - 
    	    	0, //{false,false,false,false}, // 0 - 
    	    	0, //{false,false,false,false}, // 0 - 9
    	    	15, //{true,true,true,true},     // 15- 
    	    	15, //{true,true,true,true}      // 15- 2
    	    	};
    	
    	KFCAConceptsLite k=new KFCAConceptsLite();
    	int nc=4;
    	
    	int[] n={9, 1, 1, 1, 1,-1, -1, -1, 1, -1,-1, -1, -1, -1, -1, 2};
    	
    	
    	int expectedNumConcepts=k.getNumberConcepts(I,nc);
    	
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, 7);
    	
    	
    	HashMap<Long,Integer> n2=k.numElementsBySingleConcept(I,nc);
    	
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, n2.get(-1L).intValue());
    	for (int id=0;id<n.length;id++){
    		if (n[id]>0){
    			int numElements=n2.get((long)id);
    			Assert.assertEquals("Error in numElementsBySingleConcept",n[id],numElements);
    		}
    	}
    	
    	HashMap<Long,Integer> n3=k.numElementsBySingleConcept(I,nc);
    	Assert.assertEquals("Error in numElementsBySingleConcept",n3.get(15L).intValue(),2);
    	Assert.assertEquals("Error in numElementsBySingleConcept",n3.get(0L).intValue(),9);
    }
    
    @Test
    public void testNumElementsBySingleConcept2(){
    	long [] I={
    	    	0, //{false,false,false,false},  // 0 - 1 
    	    	0, //{false,false,false,false},  // 0 - 2 
    	    	2, //{false,true,false,false},   // 2 - 3
    	    	3, //{true,true,false,false},    // 3 - 4
    	    	4, //{false,false,true,false},   // 4 - 5
    	    	0, //{false,false,false,false},  // 0 - 6
    	    	6, //{false,true,true,false},    // 6 - 7
    	    	7, //{true,true,true,false},     // 7 - 8
    	    	8, //{false,false,false,true},   // 8 - 9
    	    	9, //{true,false,false,true},    // 9 - 10
    	    	10, //{false,true,false,true},    // 10 - 11
    	    	11, //{true,true,false,true},     // 11 - 12
    	    	12, //{false,false,true,true},    // 12 - 13
    	    	13, //{true,false,true,true},     // 13 - 14
    	    	14, //{false,true,true,true},     // 14 - 15
    	    	15 //{true,true,true,true}      // 15 -   16    	
    	    	};
    	
    	int[] n={3, 0, 1, 1, 1,0, 1, 1, 1, 1,1, 1, 1, 1, 1, 1};
    	
    	KFCAConceptsLite k=new KFCAConceptsLite();
    	int nc=4;
    	    	
    	
    	int expectedNumConcepts=k.getNumberConcepts(I,nc);
    	Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, 16);
    	
    	HashMap<Long,Integer> n2=k.numElementsBySingleConcept(I,nc);
    	//Assert.assertEquals("Wrong number of concepts", expectedNumConcepts, n2.get(-1L).intValue());
    	for (int id=0;id<n.length;id++){
    		if (n[id]>0){
    			int numElements=n2.get((long)id);
    			Assert.assertEquals("Error in numElementsBySingleConcept",n[id],numElements);
    		}
    	}
    	
    }
	
}
