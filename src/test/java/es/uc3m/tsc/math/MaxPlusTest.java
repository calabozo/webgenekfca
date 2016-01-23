package es.uc3m.tsc.math;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MaxPlusTest {
	
	@Test
	public void testAdd(){
		double[][] A={{0,0,1},
    			{3,2,1},
    			{1,1,1},
    			{1,1,1}
    	};
    	MaxPlus Am=new MaxPlus(A);
    	
    	double[][] B={{1,5,1},
    			{0,2,1},
    			{0,1,1},
    			{1,3,0}
    	};
    	MaxPlus Bm=new MaxPlus(B);
    	
    	double [][] eR={{1,5,1},
				        {3,2,1},
				        {1,1,1},
				        {1,3,1}
    	};
    	MaxPlus eRm=new MaxPlus(eR);    	
    	MaxPlus Rm=Am.add(Bm);
    	
    	//System.out.println(Arrays.deepToString(Rm.getMatrix()));
    	Assert.assertTrue("Error adding",Rm.equals(eRm));     
	}
	
    @Test
    public void testMultiply1() {
    	double[][] A={{0,0,1},
    			{0,2,1},
    			{1,1,1},
    			{1,1,1}
    	};
    	MaxPlus Am=new MaxPlus(A);
    	   
    	double[][] B={{1,1,4,8},
    			{2,0,3,7},
    			{3,1,2,6}
    	};
    	MaxPlus Bm=new MaxPlus(B);
    	
    	double [][] eR={{4,2,4,8},
    					{4,2,5,9},
    					{4,2,5,9},
    					{4,2,5,9}
    	};
    	MaxPlus eRm=new MaxPlus(eR);
    	    	
    	MaxPlus Rm=Am.multiply(Bm);
    	
    	//System.out.println(Arrays.deepToString(Rm.getMatrix()));
    	Assert.assertTrue("Error mutiplying",Rm.equals(eRm));        	
    }

    @Test
    public void testMultiplyT1() {
    	double[][] A={{0,0,1},
    			{0,2,1},
    			{1,1,1},
    			{1,1,1}
    	};
    	MaxPlus Am=new MaxPlus(A);
    	
    	double[][] B={{1,2,3},
    				  {1,0,1},
    				  {4,3,2},
    				  {8,7,6}
    	};
    	MaxPlus Bm=new MaxPlus(B);
    	
    	double [][] eR={{4,2,4,8},
    					{4,2,5,9},
    					{4,2,5,9},
    					{4,2,5,9}
    	};
    	MaxPlus eRm=new MaxPlus(eR);
    	
    	MaxPlus Rm=Am.multiplyTranspose(Bm);
    	    	    	
    	//System.out.println(Arrays.deepToString(Rm.getMatrix()));
    	Assert.assertTrue("Error mutiplying",Rm.equals(eRm));        	
    }
    
    @Test
    public void testMultiplyT2() {
    	
    	boolean[][] A={{false,false,false,false,true},
    			      {true,false,true,true,true},
    			      {false,false,false,false,true},
    			      {true,false,false,false,true},
    			      {false,false,false,false,false}			      
    	};
    	
    	
    	boolean[][] eR={{false,false,false,false,true},
			      {false,false,true,true,false},
			      {false,false,false,false,true},
			      {true,false,false,false,false},
			      {false,false,false,false,false}			      
    	};
    	
    	MaxPlus Am=new MaxPlus(A);
    	boolean[][] R=Am.shortestAdjacentMatrix();
    	
    	System.out.println(Arrays.deepToString(Am.calcKleeneStarN().getMatrix()));
    	System.out.println(Arrays.deepToString(R));
    	Assert.assertTrue("Error",ArrayUtils.equal(R, eR));
    	
    	/*
    	System.out.println(Arrays.deepToString(Am.getMatrix()));
    	System.out.println(Arrays.deepToString(Am.calcMaxPath().getMatrix()));
    	System.out.println(Arrays.deepToString(Rm.getMatrix()));
    	*/
    }
    
    @Test
    public void testMultiplyT3() {
    	double[][] A={{-1,-2},
    			      {2,-Double.MAX_VALUE} 			      
    	};
    	
    	double[][] eR={{0,-2},
			      {2,0} 			      
    	};
    	
    	MaxPlus Am=new MaxPlus(A);
    	MaxPlus Rm=Am.calcKleeneStarN();    	    
    	
    	Assert.assertTrue("Error",Rm.equals(new MaxPlus(eR)));
    	
    }
    
    @Test
    public void testMultiplyT4() {
    	double[][] A={{-Double.MAX_VALUE,1,-Double.MAX_VALUE},
    				  {-Double.MAX_VALUE,-Double.MAX_VALUE,1},
    				  {-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE}
    	};
    	
    	double[][] A2={{-Double.MAX_VALUE,1,1},
				  {-Double.MAX_VALUE,-Double.MAX_VALUE,1},
				  {-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE}
    	};
    	
    	MaxPlus Am=new MaxPlus(A);
    	MaxPlus Rm=Am.calcKleeneStarN();
    	MaxPlus Am2=new MaxPlus(A2);
    	MaxPlus Rm2=Am2.calcKleeneStarN();
    	/*
    	System.out.println(Arrays.deepToString(Am.getMatrix()));
    	System.out.println(Arrays.deepToString(MaxPlus.makeIdentidy(3).add(Am).getMatrix()));
    	System.out.println(Arrays.deepToString(Rm.getMatrix()));
    	*/
    	Assert.assertTrue("Error",Rm.equals(Rm2));
    	
    }
    

}
