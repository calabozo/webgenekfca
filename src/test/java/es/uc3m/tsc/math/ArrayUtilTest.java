package es.uc3m.tsc.math;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

	@Test
	public void test() {		
		double[] a1={8,7,6,5,4,3,2,1};
		int[] e1={7,6,5,4,3,2,1,0};
		Assert.assertArrayEquals(e1, ArrayUtils.sortIndex(a1));
	
		double[] a2={3,7,4,9,5,2,6,1};
		int[] e2={7,5,0,2,4,6,1,3};
		System.out.println(Arrays.toString(ArrayUtils.sortIndex(a2)));
		Assert.assertArrayEquals(e2, ArrayUtils.sortIndex(a2));
	
		
		double[] a3={3,7,4,4,5,2,2,1};
		//int[] e3={7,5,0,2,4,1};
		int[] e3={7, 5, 6, 0, 2, 3, 4, 1};
		System.out.println(Arrays.toString(ArrayUtils.sortIndex(a3)));
		Assert.assertArrayEquals(e3, ArrayUtils.sortIndex(a3));
	}

}
