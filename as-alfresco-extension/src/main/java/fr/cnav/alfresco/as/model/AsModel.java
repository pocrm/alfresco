/**
 * 
 */
package fr.cnav.alfresco.as.model;

import org.alfresco.service.namespace.QName;

/**
 * @author LECOCQ
 *
 */
public class AsModel {
	
	public static final String AS_NAMESPACE_URI = "http://www.cnav.fr/model/content/1.0";
	public static final String AS_PREFIX = "as";
		
	public static final String AS_TYPE_ASSURE = "assure";
	public static final String AS_PROPS_NIR = "nir";
	public static final String AS_PROPS_NOM = "nom";
	public static final String AS_PROPS_PRENOM = "prenom";
	
	public static final String AS_TYPE_PIECE = "piece";
	public static final String AS_PROPS_TYPE_PIECE = "typePiece";
	public static final String AS_PROPS_LIBELLE_PIECE= "libellePiece";
	public static final String AS_PROPS_REF_PIECE= "refPiece";
	public static final String AS_PROPS_CLE_SCELLEMENT_PIECE= "cleScellementPiece";
	
	public static final String AS_TYPE_DOSSIER = "dossier";
	public static final String AS_PROPS_NIR_DOSSIER = "nirDossier";
	public static final String AS_PROPS_TYPE_DOSSIER = "typeDossier";
	public static final String AS_PROPS_CODE_CAISSE_DOSSIER= "codeCaisseDossier";
	
	public static final String AS_TYPE_PJ = "pj";
	public static final String AS_TYPE_PG = "pg";
	
		
	public static final QName TYPE_ASSURE = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_ASSURE);
	public static final QName PROPS_NIR = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_NIR);
	public static final QName PROPS_NOM = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_NOM);
	public static final QName PROPS_PRENOM = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_PRENOM);
	
	
	public static final QName TYPE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PIECE);
	public static final QName PROPS_TYPE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_TYPE_PIECE);
	public static final QName PROPS_LIBELLE_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_LIBELLE_PIECE);
	public static final QName PROPS_REF_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_REF_PIECE);
	public static final QName PROPS_CLE_SCELLEMENT_PIECE = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_CLE_SCELLEMENT_PIECE);
	
	public static final QName TYPE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_DOSSIER);
	public static final QName PROPS_NIR_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_NIR_DOSSIER);
	public static final QName PROPS_TYPE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_TYPE_DOSSIER);
	public static final QName PROPS_CODE_CAISSE_DOSSIER = QName.createQName(AS_NAMESPACE_URI, AS_PROPS_CODE_CAISSE_DOSSIER);
	
	public static final QName TYPE_PJ = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PJ);
	public static final QName TYPE_PG = QName.createQName(AS_NAMESPACE_URI, AS_TYPE_PG);
	

}
