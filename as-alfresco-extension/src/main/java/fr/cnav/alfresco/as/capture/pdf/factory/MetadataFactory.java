package fr.cnav.alfresco.as.capture.pdf.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import fr.cnav.alfresco.as.capture.pdf.pli.DocumentCapture;
import fr.cnav.alfresco.as.capture.pdf.pli.Lot;
import fr.cnav.alfresco.as.capture.pdf.pli.PageCapture;
import fr.cnav.alfresco.as.capture.pdf.pli.PliCapture;
import fr.cnav.alfresco.as.capture.pdf.pli.Lot.LotIndexTech;
import fr.cnav.alfresco.as.capture.pdf.pli.Lot.Pli.Document;
import fr.cnav.alfresco.as.capture.pdf.pli.Lot.Pli.PliIndexTech;
import fr.cnav.alfresco.as.capture.pdf.pli.Lot.Pli.Document.Page;
import fr.cnav.alfresco.as.exception.FonctionnelleException;
import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.exception.XMLIncorrectException;
import fr.cnav.alfresco.as.model.AsModel;

/**
 * @author LECOCQ
 * 
 */
public class MetadataFactory {

	static final String TAG_LOT_ID = "lot_physique";
	static final String TAG_PLI_ID = "lot_nom";
	static final String TAG_DATE_RECEPTION = "date_reception";
	static final String TAG_ORGANISME = "organisme";
	static final String TAG_NIR = "nir";
	static final String TAG_NOM = "nom";
	static final String TAG_PRENOM = "prenom";
	static final String TAG_TYPE_DEMANDE = "s_type_demande";
	static final String TAG_CODE_DOCUMENT = "code_document";

	// cette map doit correspondre aux metadatas de toutes les pièces du pli.
	// toutefois avant de creer le noeud piece
	// il faudra ajouter la métadata type_piece , qui est dans chaque document
	// créés lors de l'appel de la méthode getDocumentsPages
	// l'ajout doit être fait par le PlService, lors de la création de chaque
	// pièce
	// le type piece est récuperable en appelant getCodeDocument() sur le
	// document à créer
	// il me semble que c'est déjà fait ==> à vérifier
	@SuppressWarnings("serial")
	public static final Map<String, QName> CONSTANT_QNAME_PROPERTY_MAP_PIECE = Collections
			.unmodifiableMap(new HashMap<String, QName>() {
				{
					put(TAG_LOT_ID, AsModel.PROP_ID_LOT);
					put(TAG_PLI_ID, AsModel.PROP_ID_PLI);
					put(TAG_DATE_RECEPTION, AsModel.PROP_DATE_RECEPTION_PIECE);

				}
			});

	// cette map correspond aux métadatas d'un assure. il faudra récupérer les
	// metadatas de l''assuré
	// avant de lancer la création de l'assuré par l'AssureService
	@SuppressWarnings("serial")
	public static final Map<String, QName> CONSTANT_QNAME_PROPERTY_MAP_ASSURE = Collections
			.unmodifiableMap(new HashMap<String, QName>() {
				{
					put(TAG_NIR, AsModel.PROP_NIR);
					put(TAG_NOM, AsModel.PROP_NOM);
					put(TAG_PRENOM, AsModel.PROP_PRENOM);

				}
			});
	// cette map correspond aux métadatas d'un dossier. il faudra récupérer les
	// metadatas du dossier
	// avant de lancer la création du dossier par le DossierService
	@SuppressWarnings("serial")
	public static final Map<String, QName> CONSTANT_QNAME_PROPERTY_MAP_DOSSIER = Collections
			.unmodifiableMap(new HashMap<String, QName>() {
				{
					put(TAG_ORGANISME, AsModel.PROP_CODE_CAISSE_DOSSIER);
					put(TAG_NIR, AsModel.PROP_NIR);
					put(TAG_TYPE_DEMANDE, AsModel.PROP_TYPE_DOSSIER);

				}
			});

	static Logger logger = Logger.getLogger(MetadataFactory.class);

	/**
	 * Lecture des metadatas sur le lot et le pli
	 * 
	 * 
	 * @param unPli
	 *            : Le pli en cours
	 * @param datas
	 *            : une map correspondant à l'une de celles qui sont définies
	 *            dans cette classe
	 * 
	 *            exemple d'appel : MetadataFactory.getMetadatas(unPli,
	 *            MetadataFactory.CONSTANT_QNAME_PROPERTY_MAP_ASSURE)
	 * 
	 * @return les metadatas indiquées dans la map en entrée
	 * @throws TechniqueException
	 */
	public static Map<QName, Serializable> getMetadatas(PliCapture unPli,
			Map<String, QName> datas) throws TechniqueException {

		logger.trace("***** DEBUT getMetaDatas");

		Map<QName, Serializable> lesMetadatas = new HashMap<QName, Serializable>();

		// recherche de tous les nodes index-lot_tech
		List<Lot.LotIndexTech> lesIndexLot = unPli.getLeLot().getLotIndexTech();

		for (LotIndexTech lotIndexTech : lesIndexLot) {
			if (datas.containsKey(lotIndexTech.getNom())) {
				logger.debug("Property = " + lotIndexTech.getNom());
				logger.debug("Valeur = " + lotIndexTech.getVal());
				lesMetadatas.put(datas.get(lotIndexTech.getNom()),
						gestionFormatProperty(lotIndexTech.getVal()));
			}
		}

		List<Lot.Pli.PliIndexTech> lesPliIndexTech = unPli.getLeLot().getPli()
				.getPliIndexTech();
		for (PliIndexTech pliIndexTech : lesPliIndexTech) {
			if (datas.containsKey(pliIndexTech.getNom())) {
				logger.debug("Property = " + pliIndexTech.getNom());
				logger.debug("Valeur = " + pliIndexTech.getVal());
				lesMetadatas.put(datas.get(pliIndexTech.getNom()),
						gestionFormatProperty(pliIndexTech.getVal()));
			}
		}

		logger.trace("***** FIN getMetaDatas");
		return lesMetadatas;
	}

	/**
	 * Recupération des documents et des pages du pli ainsi que leurs métadatas
	 * 
	 * @param unPli
	 *            Le pli
	 * @return
	 * @throws TechniqueException
	 * @throws FonctionnelleException
	 */
	public static <E> ArrayList<DocumentCapture> getDocuments(
			PliCapture unPli) throws TechniqueException, FonctionnelleException {

		logger.trace("***** DEBUT getDocuments");
		
		ArrayList<DocumentCapture> lesDocuments = new ArrayList<DocumentCapture>();
		// déclaration de la liste des pages

		try {
			// recherche de tous les node document dans le XML
			DocumentCapture unDocumentATraiter = null;
			String nomFichier = null;
			// pour chaque node document

			for (Document unDocument : unPli.getLeLot().getPli().getDocument()) {

				nomFichier = unPli.getNomPli() + "_"
						+ unDocument.getRangDocPli();
				logger.debug("nom du fichier pdf a generer = " + nomFichier);
				logger.debug("rang du document = " + unDocument.getRangDocPli());
				logger.debug("code du modele = "+ unDocument.getDocIndexTech().getVal());
				
				unDocumentATraiter = new DocumentCapture(nomFichier, unDocument.getRangDocPli(),unDocument.getDocIndexTech()
						.getVal(),new TreeMap<Integer, PageCapture>());

				lesDocuments.add(unDocumentATraiter);

				for (Page unePage : unDocument.getPage()) {

					// on ajoute la page au document à générer avec son rang
					unDocumentATraiter.put(unePage.getRangPage(),
							new PageCapture(unePage.getPagePath()));

				}
			}
			logger.trace("***** FIN getDocuments");

			return lesDocuments;
		} catch (Exception e) {

			throw new XMLIncorrectException(
					"Pli "
							+ unPli.getNomPli()
							+ " - Erreur Technique lors de l'extraction des metadonnées XML",
					e);

		}

	}

	/**
	 * 
	 * @param strToFormat
	 */
	private static Serializable gestionFormatProperty(String strToFormat) {

		logger.trace("***** DEBUT gestionFormatProperty  : " + strToFormat);
		Object result = null;

		logger.debug("chaine à formater = " + strToFormat);

		if (strToFormat.length() < 10) {
			logger.debug("Type <> date");
			result = strToFormat;

		} else {
			try {

				DateTime dt = new DateTime(strToFormat);
				logger.debug("Type = date");
				result = dt.toCalendar(Locale.FRENCH).getTime();
			} catch (Exception e) {

				logger.debug("Type <> date");
				result = strToFormat;

			}
		}

		logger.trace("***** FIN gestionFormatProperty");
		return (Serializable) result;
	}

}
