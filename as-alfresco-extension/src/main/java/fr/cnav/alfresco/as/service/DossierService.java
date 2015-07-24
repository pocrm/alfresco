/**
 * 
 */
package fr.cnav.alfresco.as.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author L306093
 *
 */
public interface DossierService {
	
	public void creationDossier(List<NodeRef> lesPieces ,NodeRef assure, Map<QName, Serializable> lesMetadatas);
	
	public void miseAJourDossier (NodeRef Dossier,List<NodeRef> lesPieces);

}
