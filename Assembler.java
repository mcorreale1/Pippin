package pippin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Assembler {
	public static Set<String> noArgument = new TreeSet<String>();
	static {
		noArgument.add("HALT");
		noArgument.add("NOP");
		noArgument.add("NOT");
	}
	/**
	 * Method to assemble a file to its executable representation. If the input has errors
	 * one of the errors will be reported the StringBulder. The error may not be the first
	 * error in the code and will depend on the order in which instructions are checked.
	 * The line number of the error that is reported is returned as the value of the method.
	 * A return value of 0 indicates that the code had no errors and an output file was 
	 * produced and saved. If the input or output cannot be opened, the return value is -1.
	 * The unchecked exception IllegalArgumentException is thrown if the error parameter is
	 * null, since it would not be possible to provide error information about the source
	 * code.
	 * @param input the source assembly language file
	 * @param output the executable version of the program if the source program is correctly formatted
	 * @param error the StringBuilder to store the description of the error that is reported. 
	 * It will be empty if no error is found
	 * @return 0 if the source code is correct and the executable is saved, -1 if the input or
	 * output files cannot be opened, otherwise the line number of the reported error
	 */
	public static int assemble(File input, File output, StringBuilder error) {
		if(error == null) throw new IllegalArgumentException("Coding error: the error buffer is null");

		int retVal = 0;
		try (Scanner inp = new Scanner(input)) {

			ArrayList<String> inputText = new ArrayList<>();
			int currentLineNumber = 0;
			int blankLineNumber = 0;
			boolean isBlankLine = false;
			String line;

			while (inp.hasNextLine() && retVal == 0) {
				currentLineNumber ++;
				blankLineNumber++;
				line = inp.nextLine();
				//Check for blank line
				if (line.trim().length() == 0) {
					if (isBlankLine) {
						error.append("Illegal blank line in source code.");
						retVal = blankLineNumber;
					} else {
						isBlankLine = true;
					}	
				}
				//Check for whitespace at the start of non blank line
				if (!(line.trim().length() == 0)) {
					if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
						error.append("Line starts with illegal white space");
						retVal = currentLineNumber;
					}
				}
				
				
				//Now if nothing wrong with line, trim and add to arrayList
				//In other words, if line is non blank and has no blank space
				//at the beginning of the line..
				if (!(line.trim().length() == 0) && 
						!(line.charAt(0) == ' ') &&
						!(line.charAt(0) == '\t')) {
					inputText.add(line.trim());
				}
			}
			
			ArrayList<String> inputCode = new ArrayList<>();
			ArrayList<String> inputData = new ArrayList<>();
			if (retVal == 0) {
				boolean valuesAreCode = true;
				for(int i = 0; i < inputText.size() && retVal == 0; i++) {
					if (valuesAreCode == false) {
						inputData.add(inputText.get(i));
					} else {
						inputCode.add(inputText.get(i));
					}
					if (inputText.get(i).equals("ENDCODE")) {
						if(!(inputText.get(i).equalsIgnoreCase("ENDCODE"))) {
							error.append("Error on line "+ (i+1) + ": \"ENDCODE\" must be upper case");
							retVal = i+1;
						}
						valuesAreCode = false;
					} 
				}
			}
		} catch (FileNotFoundException e) {
			error.append("Unable to open the assembled file.");
			retVal = -1;
		}
		return retVal;
	}
}