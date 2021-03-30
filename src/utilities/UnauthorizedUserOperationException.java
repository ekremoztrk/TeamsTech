package utilities;

@SuppressWarnings("serial")
public class UnauthorizedUserOperationException extends Exception {

	public UnauthorizedUserOperationException() {
		super();
	}

	public UnauthorizedUserOperationException(String msg) {
		super(msg);
	}

	public String getMessage() {
		return super.getMessage();
	}

}
