package pippin;

public class CodeAccessException extends RuntimeException {   /**
	 * 
	 */
	private static final long serialVersionUID = -3870343547745128405L;
// use Eclipse to generate serial version ID
	/**                              // by clicking on the warning symbol
	 * No-argument constructor needed for serialization     // (that calls the serialver program)
	 */
	public CodeAccessException() {
		super();
	}
	/**
	 * Preferred constructor that sets the inherited message field
	 * of the exception object
	 * @param arg0 message passed by the exception that was thrown
	 */
	public CodeAccessException(String arg0) {
		super(arg0);
	}
}