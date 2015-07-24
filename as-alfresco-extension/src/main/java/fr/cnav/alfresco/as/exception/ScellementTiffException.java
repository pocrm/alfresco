package fr.cnav.alfresco.as.exception;



@SuppressWarnings("serial")
public class ScellementTiffException extends FonctionnelleException {

	public ScellementTiffException() {
	}

	public ScellementTiffException(String message) {
		super(message);
		setupDestination();
	}

	public ScellementTiffException(Throwable cause) {
		super(cause);
		setupDestination();
	}

	public ScellementTiffException(String message, Throwable cause) {
		super(message, cause);
		setupDestination();
	}

	void setupDestination() {
		setAction(CPPException.DELETE);
		
	}
}
