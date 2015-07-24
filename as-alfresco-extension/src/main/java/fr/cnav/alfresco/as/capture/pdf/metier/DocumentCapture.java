package fr.cnav.alfresco.as.capture.pdf.metier;

import java.util.Collection;
import java.util.TreeMap;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

public class DocumentCapture {

	private TreeMap<Integer, PageCapture> lesPages;
	private String nomPdf;
	private NodeRef nodePdf;
	private int intRangDocPli = 0;
	private String codeDocument;
	
	static Logger logger = Logger.getLogger(DocumentCapture.class);

	public DocumentCapture() {

	}

	public DocumentCapture(String nomPdf, TreeMap<Integer, PageCapture> lesPages) {
		super();
		this.nomPdf = nomPdf + PliCapture.EXTENSION_PDF;
		this.lesPages = lesPages;

	}

	public int getNbPagesAttendues() {

		return lesPages.size();
	}

	public String getNomPdf() {
		return nomPdf;
	}

	public void setNomPdf(String nomPdf) {
		this.nomPdf = nomPdf;
	}

	public TreeMap<Integer, PageCapture> getLesPages() {
		return lesPages;
	}

	public PageCapture getPage(int indexPage) {
		return lesPages.get(new Integer(indexPage));
	}

	public PageCapture put(Integer key, PageCapture value) {
		return lesPages.put(key, value);
	}

	public void setLesPages(TreeMap<Integer, PageCapture> lesPages) {
		this.lesPages = lesPages;
	}

	public Collection<PageCapture> getPages() {
		return lesPages.values();
	}

	public NodeRef getNodePdf() {
		return nodePdf;
	}

	public void setNodePdf(NodeRef nodePdf) {
		this.nodePdf = nodePdf;
	}

	public void setIntRangDocPli(int intRangDocPli) {
		this.intRangDocPli = intRangDocPli;
	}

	public int getIntRangDocPli() {
		return intRangDocPli;
	}

	public String getCodeDocument() {
		return codeDocument;
	}

	public void setCodeDocument(String codeDocument) {
		this.codeDocument = codeDocument;
	}

}