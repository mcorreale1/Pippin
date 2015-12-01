package pippin;

import java.io.File;

public class Tester {
	public static void main(String args[]) {
		File in = new File("test.txt");
		File out = new File("testOut.txt");
		StringBuilder test = new StringBuilder();
		Assembler.assemble(in, out, test);
	}
}
