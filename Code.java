package pippin;

public class Code {
	
	public static final int CODE_MAX = 256;
	private int nextCodeLocation;
	private long[] code = new long[CODE_MAX];
	private int nextCodeIndex; 	
	
	
	public void setCode(int op, int arg) {
		long longOp = op;
		long longArg = arg;
		long longOpArg = longOp << 32;
		
		longArg = longArg & 0x00000000FFFFFFFFL;
		code[nextCodeLocation++] = longOpArg | longArg;
		
	}
	
	public int getProgramSize() {
		return nextCodeLocation;
	}
	
	public int getArg(int i) {
		return (int)code[i];
	}
	
	public int getOp(int i) {
		return (int)(code[i] >> 32);
	}
	
	public void clear() {
        for(int i = 0; i < CODE_MAX; i++) {
            code[i] = 0;
        }
        nextCodeIndex = 0;
    }
	
	public String getText(int i) {
		StringBuilder builder = new StringBuilder();
		if (i < nextCodeLocation) {
			builder.append(InstructionMap.mnemonics.get(getOp(i)));
			builder.append(' ');
			builder.append(getArg(i));
		}
		
		return builder.toString();
	}
	
	public static void main(String[] args) {
		  Code c = new Code();
		  c.setCode(12, 2015);
		  System.out.println(c.getText(0));
		  System.out.println(c.code[0]);
		  System.out.println(c.getOp(0));
		  System.out.println(c.getArg(0));
		  c.setCode(12, -2015);
		  System.out.println(c.getText(1));
		  System.out.println(c.code[1]);
		  System.out.println(c.getOp(1));
		  System.out.println(c.getArg(1));
		}

}
