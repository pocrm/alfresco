package fr.cnav.alfresco.as.exception;


@SuppressWarnings("serial")
public class PliExistantException extends FonctionnelleException {

	public PliExistantException() {
	}

	public PliExistantException(String message) {
		super(message);
		setupDestination();
	}

	public PliExistantException(Throwable cause) {
		super(cause);
		setupDestination();
	}

	public PliExistantException(String message, Throwable cause) {
		super(message, cause);
		setupDestination();
	}

	void setupDestination() {
		setAction(CPPException.DELETE);
		
	}
}
