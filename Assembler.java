package pippin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
			//int blankLineNumber = 0;
			int consecutiveBlankLines = 0;
			boolean isBlankLine = false;
			String line;

			while (inp.hasNextLine() && retVal == 0) {
				currentLineNumber ++;
				//blankLineNumber++;
				line = inp.nextLine();
				
				//Check for blank line
				//If line is not blank, check to see if boolean is true, if it is, 
				//We have a blank line error
				if (!(line.trim().length() == 0)) {
					if (isBlankLine) {
						retVal = currentLineNumber - consecutiveBlankLines;
						error.append("Illegal blank line in source code.");
					}
				}
				
				//Check to see if we have a blank line
				//If we do, increment consecutiveBlankLines (which we'll subtract 
				//from current line number to keep track of where blank line occured)
				//and set isBlankLine to true
				if (line.trim().length() == 0) {
					consecutiveBlankLines++;
					isBlankLine = true;
					
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
			ArrayList<String> outputCode = new ArrayList<>();
			ArrayList<String> outputData = new ArrayList<>();
			if (retVal == 0) {
				boolean valuesAreCode = true;
				for(int i = 0; i < inputText.size() && retVal == 0; i++) {
					if (valuesAreCode == false) {
						inputData.add(inputText.get(i));
					} else {
						inputCode.add(inputText.get(i));
					}
					if (inputText.get(i).equalsIgnoreCase("ENDCODE")) {
						if(!(inputText.get(i).equals("ENDCODE"))) {
							error.append("Error on line "+ (i+1) + ": \"ENDCODE\" must be upper case");
							retVal = i+1;
						}
						valuesAreCode = false;
					}
				}
				//Populate output code array
				for(int i = 0; i< inputCode.size() && retVal == 0; i++) {
					String[] parts = inputCode.get(i).split("\\s+");
					if (parts[0].equals("ENDCODE")) {
						outputCode.add("-1");
					} else if (!InstructionMap.opcode.containsKey(parts[0].toUpperCase())) {
						error.append("Error on line " + (i+1) + ": illegal mnemonic");
						retVal = i+1;
					} else if(!(parts[0].equals(parts[0].toUpperCase()))) {
						error.append("Error on line " + (i+1) + ": mnemonic must be upper case");
						retVal = i+1;
					} else if (noArgument.contains(parts[0])) {
						if(parts.length > 1) {
							error.append("Error on line " + (i+1) + ": this mnemonic cannot take arguments");
							retVal = i+1;
						}
						else {
							outputCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " 0");	
						}
					} else 
						if(parts.length > 2) {
							error.append("Error on line " + (i+1) + ": this mnemonic has too many arguments");
							retVal = i+1;
						} else {
						try {
							int arg = Integer.parseInt(parts[1],16);
							outputCode.add(Integer.toString(InstructionMap.opcode.get(parts[0]), 16) + " " + Integer.toString(arg, 16));
						} catch(Exception e) {
							error.append("Error on line " + (i+1) + ": argument is not a hex number");
							retVal = i+1;
						} 
					}
				}
				for(int i = 0; i < inputData.size() && retVal == 0; i++) {
					String[] parts = inputData.get(i).split("\\s+");
					if (parts.length > 2) {
						error.append("Error on line" + (outputCode.size() + i + 1) +": data has too many arguments");
						retVal = outputCode.size() + i + 1;
					}
					try {
						int arg = Integer.parseInt(parts[0],16);
						int arg2 = Integer.parseInt(parts[1],16);
						outputData.add(inputData.get(i));
					} catch (Exception e) {
						error.append("Error on line " + (outputCode.size() + i + 1) + ": argument is not a hex number");
						retVal = outputCode.size() + i + 1;
					}
				}
				if(retVal == 0) {
					try (PrintWriter outp = new PrintWriter(output)){
						// for the Strings str in outputCode, write them using outp.println(str)
						// output -1 to separate code from data
						// output the Strings in outputData
						for(String s: outputCode) {
							outp.println(s);
						}
						for(String s: outputData) {
							outp.println(s);
						}
						outp.close();
					} catch (FileNotFoundException e) {
						error.append("Error: Unable to write the assembled program to the output file");
						retVal = -1;
					}
				}
			}	
		} catch (FileNotFoundException e) {
			error.append("Unable to open the assembled file.");
			retVal = -1;
		}
		return retVal;
	}
	
//	 public static void main(String[] args) {
//	        StringBuilder error = new StringBuilder();
//	        int i = assemble(new File("merge.pasm"), new File("outputFile.pexe"), error);
//	        System.out.println(i + " " + error);
//	    }
}