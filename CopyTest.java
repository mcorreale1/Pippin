package pippin;

import static org.junit.Assert.*;

import org.junit.Test;

public class CopyTest {

	
	@Test
	public void test1() {
		MachineModel test = new MachineModel();
		//Copy code
		test.setData(0, 0);
		test.setData(1, 1);
		test.setData(2, 2);
		//Copy data
		test.setData(4, 1);
		test.setData(5, 2);
		test.setData(6, 3);
		test.setData(7, 4);
		test.setData(8, 5);
		test.setData(9, 6);
		test.setData(10, 7);
		Instruction in = test.get(0x1D);
		boolean corrupted = false;
		try {
			in.execute(0);
		} catch (IllegalArgumentException e) {
			corrupted = true;
		}
		assertTrue(corrupted);
	}

}
