package pippin;

import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

public class MachineModel extends Observable {

	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();
	private Registers cpu = new Registers();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private Code code = new Code();

	public MachineModel() {
		this(false);
	}

	public MachineModel(boolean b) {
		withGUI = b;

		//Populating the INSTRUCTIONS TreeMap

		//Entry for ADDI
		INSTRUCTIONS.put(0xA, arg -> {
			cpu.accumulator += arg;
			cpu.programCounter++;
		});

		//Entry for ADD
		INSTRUCTIONS.put(0xB, arg -> {
			INSTRUCTIONS.get(0xA).execute(memory.getData(arg));
		});

		//Entry for NOP
		INSTRUCTIONS.put(0x0, arg -> {
			cpu.programCounter ++;
		});

		//Entry for LODI
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.accumulator = arg;
			cpu.programCounter++;
		});

		//Entry for LOD
		INSTRUCTIONS.put(0x2, arg -> {
			INSTRUCTIONS.get(0x1).execute(memory.getData(arg));
		});

		//Entry for LODN
		INSTRUCTIONS.put(0x3, arg -> {
			INSTRUCTIONS.get(0x2).execute(memory.getData(arg));
		});

		//Entry for STO
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData(arg,  cpu.accumulator);
			cpu.programCounter ++;
		});

		//Entry for STON
		INSTRUCTIONS.put(0x5, arg -> {
			INSTRUCTIONS.get(0x4).execute(memory.getData(arg));
		});

		//Entry for JMPI
		INSTRUCTIONS.put(0x6, arg -> {
			cpu.programCounter = arg;
		});

		//Entry for JUMP
		INSTRUCTIONS.put(0x7, arg -> {
			INSTRUCTIONS.get(0x6).execute(memory.getData(arg));
		});

		//Entry for JMZI
		INSTRUCTIONS.put(0x8, arg -> {
			if (cpu.accumulator == 0) {
				cpu.programCounter = arg;
			} else {
				cpu.programCounter++;
			}
		});

		//Entry for JMPZ
		INSTRUCTIONS.put(0x9, arg -> {
			INSTRUCTIONS.get(0x8).execute(memory.getData(arg));
		});

		//Entry for ADDI
		INSTRUCTIONS.put(0xA, arg -> {
			cpu.accumulator += arg;
			cpu.programCounter++;
		});

		//Entry for ADD
		INSTRUCTIONS.put(0xB, arg -> {
			INSTRUCTIONS.get(0xA).execute(memory.getData(arg));
		});

		//Entry for ADDN
		INSTRUCTIONS.put(0xC, arg -> {
			INSTRUCTIONS.get(0xB).execute(memory.getData(arg));
		});

		//Entry for SUBI
		INSTRUCTIONS.put(0xD, arg -> {
			cpu.accumulator -= arg;
			cpu.programCounter++;
		});

		//Entry for SUB
		INSTRUCTIONS.put(0xE, arg -> {
			INSTRUCTIONS.get(0xD).execute(memory.getData(arg));
		});

		//Entry for SUBN
		INSTRUCTIONS.put(0xF, arg -> {
			INSTRUCTIONS.get(0xE).execute(memory.getData(arg));
		});

		//Entry for MULI
		INSTRUCTIONS.put(0x10, arg -> {
			cpu.accumulator = cpu.accumulator * arg;
			cpu.programCounter++;
		});

		//Entry for MUL
		INSTRUCTIONS.put(0x11, arg -> {
			INSTRUCTIONS.get(0x10).execute(memory.getData(arg));
		});

		//Entry for MULN
		INSTRUCTIONS.put(0x12, arg -> {
			INSTRUCTIONS.get(0x11).execute(memory.getData(arg));
		});

		//Entry for DIVI
		INSTRUCTIONS.put(0x13, arg -> {
			if (arg == 0) {
				throw new DivideByZeroException("Cannot divide by 0");
			}
			cpu.accumulator = cpu.accumulator / arg;
			cpu.programCounter++;
		});

		//Entry for DIV
		INSTRUCTIONS.put(0x14, arg -> {
			INSTRUCTIONS.get(0x13).execute(memory.getData(arg));
		});

		//Entry for DIVN
		INSTRUCTIONS.put(0x15, arg -> {
			INSTRUCTIONS.get(0x14).execute(memory.getData(arg));
		});

		//Entry for ANDI
		INSTRUCTIONS.put(0x16, arg -> {
			if (arg != 0 && cpu.accumulator != 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		//Entry for AND
		INSTRUCTIONS.put(0x17, arg -> {
			INSTRUCTIONS.get(0x16).execute(memory.getData(arg));
		});

		//Entry for NOT
		INSTRUCTIONS.put(0x18, arg -> {
			if (cpu.accumulator == 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		//Entry for CMPL
		INSTRUCTIONS.put(0x19, arg -> {
			if (memory.getData(arg) < 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		//Entry for CMPZ
		INSTRUCTIONS.put(0x1A, arg -> {
			if (memory.getData(arg) == 0) {
				cpu.accumulator = 1;
			} else {
				cpu.accumulator = 0;
			}
			cpu.programCounter++;
		});

		INSTRUCTIONS.put(0x1F, arg -> {
			halt();
		});
		
		//INSTRUCTION_MAP entry for "COPY"
		INSTRUCTIONS.put(0x1D,(arg) -> {
			this.copy(arg);
			cpu.programCounter++;
		});
		
		//INSTRUCTION_MAP entry for "CPYN"
		INSTRUCTIONS.put(0x1E, arg -> {
			INSTRUCTIONS.get(0x1D).execute(memory.getData(arg));
		});
	}

	public int getChangedIndex() {
		return memory.getChangedIndex();
	}
	public void clearMemory() {
		memory.clear();
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
	void halt() {
		if(withGUI) {
			//code to come here later
		} else {
			System.exit(0);
		}
	}
	
	//package private getter for code
	Code getCode() {
		return code;
	}
	
	//Delegate setter for setCode
	public void setCode(int op, int arg) {
		code.setCode(op, arg);
	}
	/**
	 * Copys values from the location which is stored in data at location arg,
	 * to the location that is stored in data at location arg+1, with a length of 
	 * what is stored in data at location arg+2
	 * @param arg location of the 3 arguments that copy will use, given by the lambda 0x1D.
	 * @throws IllegalArgumentException throws when attempting to read from the actual arg
	 * locations for copy. (arg, arg+1, arg+2), or when attempted to write over them.
	 * @throws ArrayIndexOutOfBoundsException throws when attempted to read, or write outside the
	 * bounds of the data array. 
	 * 
	 */
	public void copy(int arg) {
		 int args[] = {memory.getData(arg), memory.getData(arg+1), memory.getData(arg+2)};
		 for(int i = 0; i < 2; i++) {
			 if(args[i] >= arg && args[i] <= arg+ args[2]) {
				 throw new IllegalArgumentException("Instruction would corrupt args");
			 }
		 }
		 if (args[0] < 0 || args[0] > Memory.DATA_SIZE-1
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


	public class Registers {
		private int accumulator;
		private int programCounter;
	}
	
	public void step(){}
	
	public void clear() {
		Memory m = new Memory();
		m.clear();
		cpu.accumulator = 0;
		cpu.programCounter = 0;
	}
}
