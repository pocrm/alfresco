/**
 * 
 */
package fr.cnav.alfresco.as.service;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.cnav.alfresco.as.exception.TechniqueException;

/**
 * @author L306093
 *
 */
public interface PliService {
	
	public void executeTraitementPli(NodeRef pli) throws Exception;
	
	public void deleteContenuPli (NodeRef pli, QName type, boolean supprimeType) throws TechniqueException;

}
