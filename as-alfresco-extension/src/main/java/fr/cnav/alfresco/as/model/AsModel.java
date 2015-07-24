package fr.cnav.alfresco.as.model;

import org.alfresco.service.namespace.QName;

/**
 * @author L306093
 *
 */
public class AsModel {
	
	public static final String AS_NAMESPACE_URI = "http://www.cnav.fr/model/content/1.0";
	public static final String AS_PREFIX = "as";
		
	public static final String AS_TYPE_PLICAPTURE = "pliCapture";
	
	public static final String AS_TYPE_ASSURE = "assure";
	public static final String AS_PROP_NIR = "nir";
	public static final String AS_PROP_NOM = "nom";
	public static final String AS_PROP_PRENOM = "prenom";
	
	
	public static final String AS_TYPE_PIECE = "piece";
	public static final String AS_PROP_TYPE_PIECE = "typePiece";
	public static final String AS_PROP_LIBELLE_PIECE= "libellePiece";
	public static final String AS_PROP_REF_PIECE= "refPiece";
	public static final String AS_PROP_CLE_SCELLEMENT_PIECE= "cleScellementPiece";
	private static final String AS_PROP_DATE_RECEPTION_PIECE = "dateReceptionPiece";
	private static final String AS_PROP_ID_LOT= "identifiantPli";
	private static final String AS_PROP_ID_PLI = "identifiantPli";
	
	public static final String AS_TYPE_DOSSIER = "dossier";
	public static final String AS_PROP_NIR_DOSSIER = "nirDossier";
	public static final String AS_PROP_TYPE_DOSSIER = "typeDossier";
	public static final String AS_PROP_CODE_CAISSE_DOSSIER= "codeCaisseDossier";

	
	public static final String AS_TYPE_PJ = "pj";
	public static final String AS_TYPE_PG = "pg";
	
	public static final QName TYPE_PLICAPTURE = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PLICAPTURE);
		
	public static final QName TYPE_ASSURE = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_ASSURE);
	public static final QName PROP_NIR = QName.createQName(AS_NAMESPACE_URI, AS_PROP_NIR);
	public static final QName PROP_NOM = QName.createQName(AS_NAMESPACE_URI, AS_PROP_NOM);
	public static final QName PROP_PRENOM = QName.createQName(AS_NAMESPACE_URI, AS_PROP_PRENOM);
	
	
	
	public static final QName TYPE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PIECE);
	public static final QName PROP_TYPE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROP_TYPE_PIECE);
	public static final QName PROP_LIBELLE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROP_LIBELLE_PIECE);
	public static final QName PROP_REF_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROP_REF_PIECE);
	public static final QName PROP_CLE_SCELLEMENT_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROP_CLE_SCELLEMENT_PIECE);
	public static final QName PROP_DATE_RECEPTION_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROP_DATE_RECEPTION_PIECE);
	public static final QName PROP_ID_LOT = QName.createQName(AS_NAMESPACE_URI, AS_PROP_ID_LOT);
	public static final QName PROP_ID_PLI = QName.createQName(AS_NAMESPACE_URI, AS_PROP_ID_PLI);
	
	public static final QName TYPE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_DOSSIER);
	public static final QName PROP_NIR_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROP_NIR_DOSSIER);
	public static final QName PROP_TYPE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROP_TYPE_DOSSIER);
	public static final QName PROP_CODE_CAISSE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROP_CODE_CAISSE_DOSSIER);

	
	public static final QName TYPE_PJ = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PJ);
	public static final QName TYPE_PG = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PG);
	

	

}
