package fr.cnav.alfresco.as.capture.pdf.pli;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import fr.cnav.alfresco.as.exception.FonctionnelleException;
import fr.cnav.alfresco.as.exception.NoGoException;
import fr.cnav.alfresco.as.exception.NoTifException;
import fr.cnav.alfresco.as.exception.NoXMLException;
import fr.cnav.alfresco.as.exception.NodeAtraiterAbsent;
import fr.cnav.alfresco.as.exception.PliIncorrectException;
import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.exception.TifAbsentException;
import fr.cnav.alfresco.as.exception.XMLIncorrectException;
import fr.cnav.alfresco.as.helper.RepositoryHelper;
import fr.cnav.alfresco.as.model.AsModel;

public class PliCapture {

	public static final String ERREUR_ABSENCE_GO = "Pli incomplet : fichier .GO absent";
	public static final String ERREUR_ABSENCE_XML = "Pli incomplet : fichier .XML absent";
	public static final String ERREUR_ABSENCE_TIF = "Pli incomplet : fichiers .TIF absents";
	public static final String ERREUR_CREATION_PDF = "Erreur lors de la création du PDF : ";
	public static final String ERREUR_NOMBRE_IMAGES = "Pli incorrect : Nombre d'images .TIF incorrect : ";
	public static final String ERREUR_ABSENCE_NODE = "Traitement impossible : La tâche planifiée de transformation PDF a reçu une reférence erronée : ";
	public static final String ERREUR_PLI_EXISTANT = "La création du repertoire des PDF a échoué - Le nom de pli suivant existe déjà : ";
	public static final String ERREUR_SCELLEMENT_IMAGE = "Image non conforme à l'originale :  ";
	public static final String ERREUR_TIF_INTROUVABLE = "Anomalie de transformation  - Fichier TIFF inexistant : ";
	public static final String ERREUR_TECHNIQUE_SCELLEMENT = "Anomalie de lecture d'une image";

	public static final String EXTENSION_GO = ".go";
	public static final String EXTENSION_XML = ".xml";
	public static final String PATTERN_TIF = ".tif";
	public static String EXTENSION_PDF = ".pdf";

	static Logger logger = Logger.getLogger(PliCapture.class);

	protected RepositoryHelper repoHelper;

	private String delaisTraitement;
	
	private NodeRef nodeFolder;
	
	private DateTime createdDate;

	private NodeRef nodePdf;

	private Lot leLot;

	private Map<QName, Serializable> lesMetadatas;

	private String nomPli;

	private NodeRef nodeXml;
		
	private List<DocumentCapture> lesDocuments;

	private long nombreImages;

	private boolean anomalie = false;

	private boolean suppression = false;

	

	public PliCapture(NodeRef nodeRef, RepositoryHelper repoHelper) throws Exception {

		this.repoHelper=repoHelper;
		logger.setLevel(repoHelper.getLogLevel());
		// on verifie l'existence du noeud à traiter
		if (repoHelper.exists(nodeRef)) {
			this.nodeFolder= nodeRef;
			this.nomPli = repoHelper.getNodeName(nodeRef);
			this.createdDate = repoHelper.getCreatedDate(nodeRef);
			this.delaisTraitement = repoHelper.getLesProprietes().get(RepositoryHelper.GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF);
			NodeRef nodeGo= repoHelper.searchSimple(nodeFolder, getNomFichierGo());
			if (nodeGo==null)
				{
				String messageGo = "Pli " + nomPli + " - " + ERREUR_ABSENCE_GO;
				logger.error(messageGo);

				throw new NoGoException(messageGo);
				}
			nodeXml = repoHelper.searchSimple(nodeFolder, getNomFichierXml());
			
			if (nodeXml ==null){
				String messageXml = "Pli " + nomPli + " - " + ERREUR_ABSENCE_XML;
				logger.error(messageXml);

				throw new NoXMLException(messageXml);
			}
			
			if (!hasImagesTif()) {

				String messageTif = "Pli " + nomPli + " - " + ERREUR_ABSENCE_TIF;
				logger.error(messageTif);

				throw new NoTifException(messageTif);

			}
			
			ContentReader contentReader = repoHelper.getReader(getCaptureXml(), ContentModel.PROP_CONTENT);

			JAXBContext jc;
			try {
				logger.trace("***** Intialisation du Lot - JAXB");
				jc = JAXBContext.newInstance("fr.cnav.alfresco.cpp.capture.pdf.metier");
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				setLeLot((Lot) unmarshaller.unmarshal(contentReader.getContentInputStream()));
			} catch (JAXBException e) {

				throw new XMLIncorrectException("Pli " + getNomPli() + " - Initialisation : Le fichier XML est incorrect", e);
			}
		} else {
			throw new NodeAtraiterAbsent(ERREUR_ABSENCE_NODE + nodeRef.getId());
		}

	}

	

	private boolean isImagesPresentes() throws  Exception {
		logger.trace("***** DEBUT isImagesPresentes");

		for (DocumentCapture unDocumentCapture : lesDocuments) {
			for (PageCapture pageCapture : unDocumentCapture.getLesPages().values()) {
				
				NodeRef nodeTiff = repoHelper.searchSimple(nodeFolder, pageCapture.getNomTiff());
				if (nodeTiff == null) {
					logger.debug("Fichier TIF " + pageCapture.getNomTiff() + " non trouvé");
					// si le document tif n'est pas trouvé - Erreur
					throw new TifAbsentException("Pli " + nomPli + " - " + PliCapture.ERREUR_TIF_INTROUVABLE + pageCapture.getNomTiff());
				}
				else
				{
					pageCapture.setNodeTiff(nodeTiff);
					
				}
			} 
			
		}
		
		logger.trace("***** FIN isImagesPresentes");
		return true;
	}

	public boolean isImagesCorrectes() throws Exception {

		return this.isNbTifOK() && isImagesPresentes() ;
	}

	private boolean isNbTifOK() throws PliIncorrectException {
		logger.trace("***** DEBUT isNbTifOK");
		long nbImagesAttendues = 0;

		for (Iterator<DocumentCapture> iterator = lesDocuments.iterator(); iterator.hasNext();) {
			DocumentCapture unDocument = iterator.next();
			nbImagesAttendues += unDocument.getNbPagesAttendues();
		}

		if (this.nombreImages == nbImagesAttendues) {

			logger.trace("***** FIN isNbTifOK");
			return true;
		} else {
			String message = "Pli " + getNomPli() + "  - " + ERREUR_NOMBRE_IMAGES + "nombre attendu=" + nbImagesAttendues + " / nombre réel="
					+ this.nombreImages;
			logger.error(message);

			throw new PliIncorrectException(message);
		}

	}

	public boolean isATransformerPdf() throws FonctionnelleException, TechniqueException {
		
		
		if (!repoHelper.isDelaisDepasseMinutes(getCreatedDate(), RepositoryHelper.GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF)) {

			
			logger.trace("***** FIN isATraiter : " +nomPli + " - Date creation : " +createdDate + " - Delais inferieur à "
					+ delaisTraitement + " minutes");
			return false;
		}
		return true;

	}


	public Lot getLeLot() {
		return leLot;
	}

	public void setLeLot(Lot leLot) {
		this.leLot = leLot;
	}

	public NodeRef getCaptureXml() {
		return nodeXml;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public Map<QName, Serializable> getLesMetadatas() {
		return lesMetadatas;
	}


	public NodeRef getNodeFolder() {
		return nodeFolder;
	}

	public void setNodeFolder(NodeRef nodeFolder) {
		this.nodeFolder = nodeFolder;
	}

	public long getNombreImages() {
		return nombreImages;
	}

	public String getNomFichierGo() {

		return this.getNomPli() + EXTENSION_GO;
	}

	public String getNomFichierXml() {

		return this.getNomPli() + EXTENSION_XML;
	}

	public String getNomPli() {
		return nomPli;
	}

	public void setCaptureXml(NodeRef captureXml) {
		this.nodeXml = captureXml;
	}

	public void setLesMetadatas(Map<QName, Serializable> lesMetadatas) {
		this.lesMetadatas = lesMetadatas;
	}

	

	public void setNombreImages(long nombreImages) {
		this.nombreImages = nombreImages;
	}

	public void setNomPli(String nomPli) {
		this.nomPli = nomPli;
	}


	public List<DocumentCapture> getLesDocuments() {
		return lesDocuments;
	}

	public void setLesDocuments(List<DocumentCapture> lesDocuments) {
		this.lesDocuments = lesDocuments;
	}

	public void supprimeContenuPli() throws TechniqueException {

		repoHelper.deleteContenuPli(nodeFolder,AsModel.PROP_TYPE_PIECE);

	}

	public NodeRef getNodePdf() {
		return nodePdf;
	}

	public void setNodePdf(NodeRef nodePdf) {
		this.nodePdf = nodePdf;
	}

	public boolean hasImagesTif() {

		logger.trace("***** DEBUT hasImagesTif");
		boolean retour = false;
		long nbTif = repoHelper.searchFiles(nodeFolder, PATTERN_TIF).size();
		if (nbTif > 0) {

			logger.debug(nbTif + " images tif presentes");
			setNombreImages(nbTif);
			retour = true;
		}
		logger.trace("***** FIN hasImagesTif");
		return retour;

	}

	public boolean isAnomalie() {
		return anomalie;
	}

	public void setAnomalie(boolean anomalie) {
		this.anomalie = anomalie;
	}

	public boolean isSuppression() {
		return suppression;
	}

	public void setSuppression(boolean suppression) {
		this.suppression = suppression;
	}

	public RepositoryHelper getRepoHelper() {
		return repoHelper;
	}

	public void deletePli() throws TechniqueException {

		repoHelper.delete(nodeFolder);

	}

}
