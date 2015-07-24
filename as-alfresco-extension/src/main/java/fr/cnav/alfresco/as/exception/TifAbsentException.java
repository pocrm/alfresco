package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class TifAbsentException extends FonctionnelleException {

	public TifAbsentException() {
	}

	public TifAbsentException(String message) {
		super(message);
		setAction(CPPException.DELETE);
	}

	public TifAbsentException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public TifAbsentException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
