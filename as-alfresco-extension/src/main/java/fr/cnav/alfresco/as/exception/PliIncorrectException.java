package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class PliIncorrectException extends FonctionnelleException {

	public PliIncorrectException() {
	}

	public PliIncorrectException(String message) {
		super(message);
		setAction(CPPException.DELETE);
	}

	public PliIncorrectException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public PliIncorrectException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
