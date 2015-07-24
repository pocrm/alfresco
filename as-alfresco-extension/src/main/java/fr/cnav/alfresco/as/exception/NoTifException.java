package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class NoTifException extends FonctionnelleException {

	public NoTifException() {
	}

	public NoTifException(String message) {
		super(message);
		setAction(CPPException.DELETE);
	}

	public NoTifException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public NoTifException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
