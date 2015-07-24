package fr.cnav.alfresco.as.capture.pdf.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import fr.cnav.alfresco.as.capture.pdf.metier.DocumentCapture;
import fr.cnav.alfresco.as.capture.pdf.metier.Lot;
import fr.cnav.alfresco.as.capture.pdf.metier.Lot.LotIndexTech;
import fr.cnav.alfresco.as.capture.pdf.metier.Lot.Pli.Document;
import fr.cnav.alfresco.as.capture.pdf.metier.Lot.Pli.Document.Page;
import fr.cnav.alfresco.as.capture.pdf.metier.Lot.Pli.PliIndexTech;
import fr.cnav.alfresco.as.capture.pdf.metier.PageCapture;
import fr.cnav.alfresco.as.capture.pdf.metier.PliCapture;
import fr.cnav.alfresco.as.exception.FonctionnelleException;
import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.exception.TifAbsentException;
import fr.cnav.alfresco.as.exception.XMLIncorrectException;
import fr.cnav.alfresco.as.helper.RepositoryHelper;
import fr.cnav.alfresco.as.model.AsModel;

public class MetadataFactory {

	public static final String TAG_LOT_ID = "lot_physique";
	public static final String TAG_PLI_ID = "lot_nom";
	public static final String TAG_DATE_RECEPTION = "date_reception";
	public static final String TAG_ORGANISME = "organisme";
	public static final String TAG_NIR = "nir";
	public static final String TAG_NOM = "nom";
	public static final String TAG_PRENOM= "prenom";
	public static final String TAG_TYPE_DEMANDE= "s_type_demande";
	public static final String TAG_CODE_DOCUMENT = "code_document";


	@SuppressWarnings("serial")
	public static final Map<String, QName> CONSTANT_QNAME_PROPERTY_MAP_PLI = Collections.unmodifiableMap(new HashMap<String, QName>() {
		{

			put(TAG_LOT_ID, AsModel.PROP_ID_LOT);
			put(TAG_PLI_ID, AsModel.PROP_ID_PLI);
			put(TAG_DATE_RECEPTION, AsModel.PROP_DATE_RECEPTION_PIECE);
			put(TAG_ORGANISME, AsModel.PROP_CODE_CAISSE_DOSSIER);
			put(TAG_NIR, AsModel.PROP_NIR);
			put(TAG_NOM, AsModel.PROP_NOM);
			put(TAG_PRENOM, AsModel.PROP_PRENOM);
			put(TAG_TYPE_DEMANDE, AsModel.PROP_TYPE_DOSSIER);

		}
	});
	
	

	static Logger logger = Logger.getLogger(MetadataFactory.class);

	/**
	 * Lecture des meta data du pli
	 * 
	 * @param unPli
	 *            Le pli à traiter
	 * @return les meta data du pli
	 * @throws TechniqueException
	 */
	public static Map<QName, Serializable> getMetadatas(PliCapture unPli) throws TechniqueException {
		logger.setLevel(RepositoryHelper.getInstance().getLogLevel());
		logger.trace("***** DEBUT getMetaDatas");

		// liste des métadatas à complèter (tous les documents issus du PDF
		// auront les mêmes métadatas)
		Map<QName, Serializable> lesMetadatas = new HashMap<QName, Serializable>();

		// recherche de tous les nodes index-lot_tech
		// NodeList listOfIndexTech =
		// docXml.getElementsByTagName(ICapturePdf.TAG_LOT_INDEX_TECH);
		List<Lot.LotIndexTech> lesIndexLot = unPli.getLeLot().getLotIndexTech();
		for (Iterator<LotIndexTech> iterator = lesIndexLot.iterator(); iterator.hasNext();) {
			Lot.LotIndexTech lotIndexTech = iterator.next();
			if (CONSTANT_QNAME_PROPERTY_MAP_PLI.containsKey(lotIndexTech.getNom())) {
				logger.debug("Property = " + lotIndexTech.getNom());
				logger.debug("Valeur = " + lotIndexTech.getVal());
				gestionFormatProperty(lesMetadatas, CONSTANT_QNAME_PROPERTY_MAP_PLI.get(lotIndexTech.getNom()), lotIndexTech.getVal());

			}

		}

		List<Lot.Pli.PliIndexTech> lesPliIndexTech = unPli.getLeLot().getPli().getPliIndexTech();

		for (Iterator<PliIndexTech> iterator = lesPliIndexTech.iterator(); iterator.hasNext();) {
			Lot.Pli.PliIndexTech pliIndexTech = iterator.next();
			if (CONSTANT_QNAME_PROPERTY_MAP_PLI.containsKey(pliIndexTech.getNom())) {
				gestionFormatProperty(lesMetadatas, CONSTANT_QNAME_PROPERTY_MAP_PLI.get(pliIndexTech.getNom()), pliIndexTech.getVal());
			}

		}

		

		logger.trace("***** FIN getMetaDatas");
		return lesMetadatas;
	}

	/**
	 * Recupération des meta data des pages des documents du pli
	 * 
	 * @param unLot
	 *            Le lot
	 * @param unPli
	 *            Le pli
	 * @return
	 * @throws TechniqueException
	 * @throws FonctionnelleException
	 */
	public static <E> ArrayList<DocumentCapture> getDocumentsPages(PliCapture unPli) throws TechniqueException, FonctionnelleException {

		RepositoryHelper repoHelper = RepositoryHelper.getInstance();
		logger.setLevel(repoHelper.getLogLevel());

		logger.trace("***** DEBUT getDocumentsPages");
		Lot unLot = unPli.getLeLot();
		// lesInfos = liste des documents issus du XML
		ArrayList<DocumentCapture> lesInfosPdf = new ArrayList<DocumentCapture>();
		// déclaration de la liste des pages
		TreeMap<Integer, PageCapture> lesPages;

		try {
			// recherche de tous les node document dans le XML
			List<Document> listOfDocuments = unLot.getPli().getDocument();
			DocumentCapture unDocumentATraiter = null;
			String nomFichier = null;
			// pour chaque node document
			for (Iterator<Document> iterator = listOfDocuments.iterator(); iterator.hasNext();) {
				Document unDocument = iterator.next();
				nomFichier = unPli.getNomPli() + "_" + unDocument.getRangDocPli();
				logger.debug("nom du fichier pdf a generer = " + nomFichier);
				
				lesPages = new TreeMap<Integer, PageCapture>();
				unDocumentATraiter = new DocumentCapture(nomFichier, lesPages);
				
				// AQ : On renseigne le rang du doc dans le pli
				unDocumentATraiter.setIntRangDocPli(unDocument.getRangDocPli());
				logger.debug("rang du document = " + unDocumentATraiter.getIntRangDocPli());
				
				// PL : on recupère le code document (le seul index technique du document)
				unDocumentATraiter.setCodeDocument(unDocument.getDocIndexTech().getVal());
				logger.debug("code du modele = " + unDocumentATraiter.getCodeDocument());
				lesInfosPdf.add(unDocumentATraiter);

				List<Page> listOfPages = unDocument.getPage();
				for (Iterator<Page> iteratorPages = listOfPages.iterator(); iteratorPages.hasNext();) {
					Page unePage = iteratorPages.next();

					String nomTiff = unePage.getPagePath();

					logger.debug("nom du tif en cours = " + nomTiff);
					NodeRef nodeTiff = repoHelper.searchSimple(unPli.getFileInfoFolder().getNodeRef(), nomTiff);

					if (nodeTiff == null) {
						logger.debug("Fichier TIF " + nomTiff + " non trouvé");
						// si le document tif n'est pas trouvé - Erreur
						throw new TifAbsentException("Pli " + unPli.getNomPli() + " - " + PliCapture.ERREUR_TIF_INTROUVABLE + nomTiff);
					} else {

						// création d'un objet Page
						PageCapture uneNouvellePage = new PageCapture(nodeTiff);
						// on ajoute la page au document à générer avec son rang
						unDocumentATraiter.put(unePage.getRangPage(), uneNouvellePage);
						

					}

				}

			}
			logger.trace("***** FIN getDocumentsPages");

			return lesInfosPdf;
		} catch (Exception e) {

			throw new XMLIncorrectException("Pli " + unPli.getNomPli() + " - Erreur Technique lors de l'extraction des metadonnées XML", e);

		}

	}

	private static void gestionFormatProperty(Map<QName, Serializable> lesMetadatas, QName property, String strToFormat) {

		logger.trace("***** DEBUT gestionFormatProperty  : " + strToFormat);

		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		// DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		// Date dTemp;

		logger.debug("chaine à formater = " + strToFormat);

		if (strToFormat.length() < 10) {
			logger.debug("Type <> date");
			lesMetadatas.put(property, strToFormat);

		} else {
			try {

				DateTime dt = new DateTime(strToFormat);
				logger.debug("Type = date");
				lesMetadatas.put(property, dt.toCalendar(Locale.FRENCH).getTime());
			} catch (Exception e) {

				logger.debug("Type <> date");
				lesMetadatas.put(property, strToFormat);

			}
		}

		logger.trace("***** FIN gestionFormatProperty");
	}

}
