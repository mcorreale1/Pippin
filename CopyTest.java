package pippin;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class CopyTest {
	MachineModel model;
	Instruction in;
	int[] dataAry;
	
	/**
	 * Sets data for testing purposes into model and sets instruction. Called at the top
	 * of every test function
	 * @param a1 Source location for copy arg, stored at 0
	 * @param a2 Target location for copy arg, stored at 1
	 * @param a3 Length amount for copy arg, stored at 2
	 */
	private void setData(int a1, int a2, int a3) {
		model = new MachineModel();
		//Set args
		model.setData(0, a1);
		model.setData(1, a2);
		model.setData(2, a3);
		//Set data
		model.setData(3, 0);
		model.setData(4, 1);
		model.setData(5, 2);
		model.setData(6, 3);
		model.setData(7, 4);
		model.setData(8, 5);
		model.setData(9, 6);
		model.setData(10, 7);
		
		dataAry = new int[model.getData().length];
		for(int i = 0; i < model.getData().length; i++) {
			dataAry[i] = model.getData(i);
		}
	}
	
	
	/**
	 *	Tests for Copy would corrupt args
	 *	Trys to read from copy args
	 */
	@Test
	public void test1() {
		setData(1, 3, 2);
		in = model.get(0x1D);
		boolean corrupted = false;
		try {
			in.execute(0);
		} catch (IllegalArgumentException e) {
			corrupted = true;
		}
		assertTrue(corrupted);
	}
	
	/**
	 *	Tests for Copy would corrupt args
	 *	Trys to paste over copy args
	 */
	@Test
	public void test2() {
		setData(6, 1, 2);
		in = model.get(0x1D);
		boolean corrupted = false;
		try {
			in.execute(0);
		} catch (IllegalArgumentException e) {
			corrupted = true;
		}
		assertTrue(corrupted);
	}
	
	/**
	 *	Tests that values are copied correctly.
	 *	Source < Target with no overlap
	 */
	@Test
	public void test3() {
		setData(4, 7, 2);
		in = model.get(0x1D);
		dataAry[7] = dataAry[4];
		dataAry[8] = dataAry[5];
		in.execute(0);
		assertTrue(Arrays.equals(dataAry, model.getData()));
	}
	
	/**
	 *	Tests that values are copied correctly.
	 *	Source > Target with no overlap
	 */
	@Test
	public void test4() {
		setData(7, 4, 2);
		in = model.get(0x1D);
		dataAry[4] = dataAry[7];
		dataAry[5] = dataAry[8];
		in.execute(0);
		assertTrue(Arrays.equals(dataAry, model.getData()));
	}
	
	/**
	 *	Tests that values are copied correctly.
	 *	Source < Target with overlap
	 */
	@Test
	public void test5() {
		setData(4, 5, 3);
		in = model.get(0x1D);
		dataAry[7] = dataAry[6];
		dataAry[6] = dataAry[5];
		dataAry[5] = dataAry[4];

		in.execute(0);
		assertTrue(Arrays.equals(dataAry, model.getData()));
	}
	
	/**
	 *	Tests that values are copied correctly.
	 *	Source > Target with overlap
	 */
	@Test
	public void test6() {
		setData(5, 4, 3);
		in = model.get(0x1D);
		dataAry[4] = dataAry[5];
		dataAry[5] = dataAry[6];
		dataAry[6] = dataAry[7];

		in.execute(0);
		assertTrue(Arrays.equals(dataAry, model.getData()));
	}
	
	/**
	 *	Tests that values are copied correctly.
	 *	Source > Target with overlap
	 */
	@Test
	public void test7() {
		setData(4, 7, 2);
		in = model.get(0x1E);
		dataAry[7] = dataAry[4];
		dataAry[8] = dataAry[5];
		//3 has 0 stored in it, which is where args are stored
		in.execute(3);
		assertTrue(Arrays.equals(dataAry, model.getData()));
	}

}
