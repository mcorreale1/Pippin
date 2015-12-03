package pippin;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.ranges.Range;

public class MachineModel {
	public class Registers {
		private int accumulator;
		private int programCounter;
	}
	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();
	private Registers cpu = new Registers();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	
	public MachineModel() {
		this(false);
	}
	
	public MachineModel(boolean gui) {
		this.withGUI = gui;
		
		//INSTRUCTIONS entry for "NOP"
		INSTRUCTIONS.put(0x0, arg -> {
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "LODI"
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.accumulator = arg;
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "LOD"
		INSTRUCTIONS.put(0x2, arg -> {
			INSTRUCTIONS.get(0x1).execute(memory.getData(arg));
		});		
		
		//INSTRUCTIONS entry for "LODN"
		INSTRUCTIONS.put(0x3, arg -> {
			INSTRUCTIONS.get(0x2).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "STO"
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData(arg, cpu.accumulator);
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "STON"
		INSTRUCTIONS.put(0x5, arg -> {
			INSTRUCTIONS.get(0x4).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "JMPI"
		INSTRUCTIONS.put(0x6, arg -> {
			cpu.programCounter = arg;
		});
		
		//INSTRUCTIONS entry for "JUMP"
		INSTRUCTIONS.put(0x7, arg -> {
			INSTRUCTIONS.get(0x6).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "JMZI"
		INSTRUCTIONS.put(0x8, arg -> {
			if(cpu.accumulator == 0) {
				cpu.programCounter = arg;
			} else {
				cpu.programCounter++;
			}
		});
		
		//INSTRUCTIONS entry for "JMPZ"
		INSTRUCTIONS.put(0x9, arg -> {
			INSTRUCTIONS.get(0x8).execute(memory.getData(arg));
		});
		
	    //INSTRUCTIONS entry for "ADDI"
	    INSTRUCTIONS.put(0xA, arg -> {
	        cpu.accumulator += arg;
	        cpu.programCounter++;
	    });

	    //INSTRUCTIONS entry for "ADD"
	    INSTRUCTIONS.put(0xB, arg -> {
	        INSTRUCTIONS.get(0xA).execute(memory.getData(arg));
	    });
	    
		//INSTRUCTIONS entry for "ADDN"
		INSTRUCTIONS.put(0xC, arg -> {
			INSTRUCTIONS.get(0xB).execute(memory.getData(arg));
		});
		
	    //INSTRUCTIONS entry for "SUBI"
	    INSTRUCTIONS.put(0xD, arg -> {
	        cpu.accumulator -= arg;
	        cpu.programCounter++;
	    });
	    
		//INSTRUCTIONS entry for "SUB"
		INSTRUCTIONS.put(0xE, arg -> {
			INSTRUCTIONS.get(0xD).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "SUBN"
		INSTRUCTIONS.put(0xF, arg -> {
			INSTRUCTIONS.get(0xE).execute(memory.getData(arg));
		});
		
	    //INSTRUCTIONS entry for "MULI"
	    INSTRUCTIONS.put(0x10, arg -> {
	        cpu.accumulator *= arg;
	        cpu.programCounter++;
	    });
	    
		//INSTRUCTIONS entry for "MUL"
		INSTRUCTIONS.put(0x11, arg -> {
			INSTRUCTIONS.get(0x10).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "MULN"
		INSTRUCTIONS.put(0x12, arg -> {
			INSTRUCTIONS.get(0x11).execute(memory.getData(arg));
		});
		
	    //INSTRUCTIONS entry for "DIVI"
	    INSTRUCTIONS.put(0x13, arg -> {
	    	if(arg == 0) throw new DivideByZeroException();
	        cpu.accumulator /= arg;
	        cpu.programCounter++;
	    });
	    
		//INSTRUCTIONS entry for "DIV"
		INSTRUCTIONS.put(0x14, arg -> {
			INSTRUCTIONS.get(0x13).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "DIVN"
		INSTRUCTIONS.put(0x15, arg -> {
			INSTRUCTIONS.get(0x14).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "ANDI"
		INSTRUCTIONS.put(0x16, arg -> {
			if(cpu.accumulator != 0 && arg != 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "AND"
		INSTRUCTIONS.put(0x17, arg -> {
			INSTRUCTIONS.get(0x16).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "NOT"
		INSTRUCTIONS.put(0x18, arg -> {
			if(cpu.accumulator == 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "CMPL"
		INSTRUCTIONS.put(0x19, arg -> {
			if(memory.getData(arg) < 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "CMPZ"
		INSTRUCTIONS.put(0x1A, arg -> {
			if(memory.getData(arg) == 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "COPY"
		INSTRUCTIONS.put(0x1D, (arg) -> {
			this.copy(arg);
			cpu.programCounter++;
		});
		
		//INSTRUCTIONS entry for "CPYN"
		INSTRUCTIONS.put(0x1E, (arg) -> {
			INSTRUCTIONS.get(0x16).execute(memory.getData(arg));
		});
		
		//INSTRUCTIONS entry for "HALT"
		INSTRUCTIONS.put(0x1F, arg -> {
			this.halt();
		});
	}
	
	 public int getData(int index) {
	        return memory.getData(index);
	 }
	 public void setData(int index, int value) {
		 memory.setData(index, value);
	 }
	 public Instruction get(Object key) {
		 return INSTRUCTIONS.get(key);
	 }
	 int[] getData() {
		 return memory.getData();
	 }
	 int getProgramCounter() {
		 return cpu.programCounter;
	 }
	 int getAccumulator() {
		 return cpu.accumulator;
	 }
	 void setAccumulator(int i) {
		 cpu.accumulator = i;
	 }
	 void setProgramCounter(int i) {
		 cpu.programCounter = i;
	 }
	 public void copy(int arg) {
		 int args[] = {memory.getData(arg), memory.getData(arg+1), memory.getData(arg+2)};
		 int range = args[0] + args[2] - arg;
		 if (range > 0 && range <= args[2]) {
			 throw new IllegalArgumentException("Copy would corrupt arg");
		 } else if (args[0] < 0 || args[0] > Memory.DATA_SIZE-1
				 ||	args[1] < 0 || args[1] > Memory.DATA_SIZE-1) {
			 throw new ArrayIndexOutOfBoundsException("Source or target out of bounds");
		 } 
		 if(args[0] == args[1]) {
			 return;
		 } else if(args[0] > args[1]) {
			 for(int i = 0; i < args[2]; i++) {
				 this.setData(args[1]+i, this.getData(args[0]+i));
			 }
		 } else {
			 for(int i = args[2]; i > 0; i--) {
				 this.setData(args[1]+i-1, this.getData(args[0]+i-1));
			 }
		 }
	 }
	 public int getChangedIndex() {
		 return memory.getChangedIndex();
	 }
	 public void clearMemory() {
		 memory.clear();
	 }
	 void halt() { 
		 if(withGUI) { 
			 //code to come here later
		 } else {
			 System.exit(0);
		 }
	 }
}
