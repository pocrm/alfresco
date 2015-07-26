package fr.cnav.alfresco.as.capture.pdf.pli;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

public class PageCapture {

	private NodeRef nodeTiff;
	private String hashMD5;
	private String endossage;
	private String nomTiff;

	public String getNomTiff() {
		return nomTiff;
	}

	public void setNomTiff(String nomTiff) {
		this.nomTiff = nomTiff;
	}

	static Logger logger = Logger.getLogger(PageCapture.class);

	public PageCapture(String nomTiff) {
		super();
		this.nomTiff = nomTiff;

	}

	

	public NodeRef getNodeTiff() {
		return nodeTiff;
	}

	public void setNodeTiff(NodeRef nodeTiff) {
		this.nodeTiff = nodeTiff;
	}

	public String getHashMD5() {
		return hashMD5;
	}

	public void setHashMD5(String hashMD5) {
		this.hashMD5 = hashMD5;
	}

	public String getEndossage() {
		return endossage;
	}

	public void setEndossage(String endossage) {
		this.endossage = endossage;
	}

	

}
