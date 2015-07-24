package fr.cnav.alfresco.as.capture.pdf.metier;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileInfo;
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
import fr.cnav.alfresco.as.exception.ScellementTiffException;
import fr.cnav.alfresco.as.exception.TechniqueException;
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

	protected RepositoryHelper repoHelper = RepositoryHelper.getInstance();

	private FileInfo fileInfoFolder;

	private NodeRef nodePdf;

	private Lot leLot;

	private Map<QName, Serializable> lesMetadatas;

	private String nomPli;

	private NodeRef captureXml;

	private List<DocumentCapture> lesDocuments;

	private long nombreImages;

	private boolean anomalie = false;

	private boolean suppression = false;

	public PliCapture(FileInfo fileInfoFolder) {
		super();
		this.fileInfoFolder = fileInfoFolder;
		this.nomPli = fileInfoFolder.getName();

	}

	public PliCapture(NodeRef nodeRef) throws TechniqueException {

		logger.setLevel(repoHelper.getLogLevel());
		// on verifie l'existence du noeud à traiter
		if (repoHelper.exists(nodeRef)) {
			this.fileInfoFolder = repoHelper.getFileInfo(nodeRef);
			this.nomPli = fileInfoFolder.getName();
		} else {
			throw new NodeAtraiterAbsent(ERREUR_ABSENCE_NODE + nodeRef.getId());
		}

	}

	public void initLot() throws XMLIncorrectException {

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

	}

	private boolean isImagesNonModifiees() throws ScellementTiffException, TechniqueException {
		logger.trace("***** DEBUT isImagesNonModifiee");

		for (Iterator<DocumentCapture> iterator = lesDocuments.iterator(); iterator.hasNext();) {
			DocumentCapture unDocumentCapture = iterator.next();
			Collection<PageCapture> lesPages = unDocumentCapture.getLesPages().values();

			for (Iterator<PageCapture> iterator2 = lesPages.iterator(); iterator2.hasNext();) {
				PageCapture pageCapture = iterator2.next();

				if (pageCapture.isModified()) {
					String message = "Pli " + getNomPli() + " - " + ERREUR_SCELLEMENT_IMAGE
							+ repoHelper.getFileInfo(pageCapture.getNodeTiff()).getName();

					logger.error(message);

					throw new ScellementTiffException(message);
				}

			}

		}
		logger.trace("***** FIN isImagesNonModifiee");
		return true;
	}

	public boolean isImagesCorrectes() throws FonctionnelleException, TechniqueException {

		return this.isNbTifOK() ;//&& this.isImagesNonModifiees();
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

		logger.trace("***** DEBUT isATraiter");

		// si le pli a été créé depuis plus de x minutes (cf paramètre dans
		// global.properties)

		if (isDelaisTransfoPdfOK()) {

			// si le fichier go est présent
			if (hasGoFile()) {

				// sil le fichier XML est présent
				if (hasXmlFile()) {

					// et si il existe au moins un fichier tif
					if (hasImagesTif()) {

						setCaptureXml(getXmlFile());
						logger.trace("***** FIN isATraiter : " + getNomPli() + " OK");
						return true;
					}
					// pas de fichiers TIF
					else {
						String messageTif = "Pli " + getNomPli() + " - " + ERREUR_ABSENCE_TIF;
						logger.error(messageTif);

						throw new NoTifException(messageTif);
					}
					// pas de fichier XML
				} else {
					String messageXml = "Pli " + getNomPli() + " - " + ERREUR_ABSENCE_XML;
					logger.error(messageXml);

					throw new NoXMLException(messageXml);
				}
				// pas de fichier GO
			} else {
				String messageGo = "Pli " + getNomPli() + " - " + ERREUR_ABSENCE_GO;
				logger.error(messageGo);

				throw new NoGoException(messageGo);
			}

			// le répertoire a été créé il y a moins de x minutes
		} else {
			logger.trace("***** FIN isATraiter : " + getNomPli() + " - Date creation : " + getCreatedDate() + " - Delais inferieur à "
					+ repoHelper.getLesProprietes().get(RepositoryHelper.GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF) + " minutes");
			return false;
		}

	}

	private NodeRef getXmlFile() {

		return repoHelper.searchSimple(getNodeRefFolder(), getNomFichierXml());
	}

	private boolean hasXmlFile() {

		return repoHelper.searchSimple(getNodeRefFolder(), getNomFichierXml()) != null;
	}

	private boolean hasGoFile() {

		return repoHelper.searchSimple(getNodeRefFolder(), getNomFichierGo()) != null;
	}

	private boolean isDelaisTransfoPdfOK() throws NumberFormatException {
		return repoHelper.isDelaisDepasseMinutes(getCreatedDate(), RepositoryHelper.GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF);

	}

	public Lot getLeLot() {
		return leLot;
	}

	public void setLeLot(Lot leLot) {
		this.leLot = leLot;
	}

	public NodeRef getCaptureXml() {
		return captureXml;
	}

	public DateTime getCreatedDate() {
		return new DateTime(fileInfoFolder.getCreatedDate());
	}

	public Map<QName, Serializable> getLesMetadatas() {
		return lesMetadatas;
	}

	public NodeRef getNodeRefFolder() {
		return fileInfoFolder.getNodeRef();
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
		this.captureXml = captureXml;
	}

	public void setLesMetadatas(Map<QName, Serializable> lesMetadatas) {
		this.lesMetadatas = lesMetadatas;
	}

	public void setNodeRefFolder(FileInfo nodeRefFolder) {
		this.fileInfoFolder = nodeRefFolder;
	}

	public void setNombreImages(long nombreImages) {
		this.nombreImages = nombreImages;
	}

	public void setNomPli(String nomPli) {
		this.nomPli = nomPli;
	}

	public FileInfo getFileInfoFolder() {
		return fileInfoFolder;
	}

	public void setFileInfoFolder(FileInfo fileInfoFolder) {
		this.fileInfoFolder = fileInfoFolder;
	}

	public List<DocumentCapture> getLesDocuments() {
		return lesDocuments;
	}

	public void setLesDocuments(List<DocumentCapture> lesDocuments) {
		this.lesDocuments = lesDocuments;
	}

	public void supprimeContenuPli() throws TechniqueException {

		repoHelper.deleteContenuPli(getNodeRefFolder(),AsModel.PROP_TYPE_PIECE);

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
		long nbTif = repoHelper.searchFiles(getNodeRefFolder(), PATTERN_TIF).size();
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

		repoHelper.delete(getNodeRefFolder());

	}

}
