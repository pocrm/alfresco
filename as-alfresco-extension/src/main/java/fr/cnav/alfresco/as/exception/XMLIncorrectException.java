package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class XMLIncorrectException extends FonctionnelleException {

	public XMLIncorrectException() {

	}

	public XMLIncorrectException(String message) {
		super(message);
		setAction(CPPException.DELETE);
	}

	public XMLIncorrectException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public XMLIncorrectException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
