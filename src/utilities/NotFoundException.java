package utilities;

@SuppressWarnings("serial")
public class NotFoundException extends Exception {

	public NotFoundException() {
		super();
	}

	public NotFoundException(String msg) {
		super(msg);
	}

	public String getMessage() {
		return super.getMessage();
	}

}
