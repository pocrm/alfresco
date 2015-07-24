package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class NoXMLException extends FonctionnelleException {

	public NoXMLException() {
	}

	public NoXMLException(String message) {
		super(message);
		setAction(CPPException.DELETE);
	}

	public NoXMLException(Throwable cause) {
		super(cause);
		setAction(CPPException.DELETE);
	}

	public NoXMLException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.DELETE);
	}

}
