/**
 * 
 */
package fr.cnav.alfresco.as.exception;

/**
 * @author L306093
 * 
 */
@SuppressWarnings("serial")
public class ClassementException extends FonctionnelleException {

	/**
	 * 
	 */
	public ClassementException() {
		setAction(CPPException.NOTHING);
	}

	/**
	 * @param message
	 */
	public ClassementException(String message) {
		super(message);
		setAction(CPPException.NOTHING);
	}

	/**
	 * @param cause
	 */
	public ClassementException(Throwable cause) {
		super(cause);
		setAction(CPPException.NOTHING);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ClassementException(String message, Throwable cause) {
		super(message, cause);
		setAction(CPPException.NOTHING);
	}

}
