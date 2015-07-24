package fr.cnav.alfresco.as.exception;

import org.alfresco.service.cmr.repository.NodeRef;

@SuppressWarnings("serial")
public class CPPException extends Exception {

	public static final String NOTHING = "Rien";
	public static final String DELETE = "SUPPRESSION";
	public static final String DEPLACEMENT = "DEPLACEMENT";

	protected NodeRef destination = null;
	protected String destinationMessage = null;
	protected String action = NOTHING;

	public CPPException() {
		// TODO Auto-generated constructor stub
	}

	public CPPException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CPPException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CPPException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NodeRef getDestination() {
		return destination;
	}

	public String getAction() {
		return action;
	}

	public void setDestination(NodeRef destination) {
		this.destination = destination;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDestinationMessage() {
		return destinationMessage;
	}

	public void setDestinationMessage(String destinationMessage) {
		this.destinationMessage = destinationMessage;
	}

}
