package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class NoGoException extends FonctionnelleException {

	public NoGoException() {
	}

	public NoGoException(String message) {
		super(message);

		setAction(CPPException.DELETE);
	}

	public NoGoException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public NoGoException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
