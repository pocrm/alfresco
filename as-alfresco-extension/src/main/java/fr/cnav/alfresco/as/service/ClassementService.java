/**
 * 
 */
package fr.cnav.alfresco.as.service;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author LECOCQ
 *
 */
public interface ClassementService {

	public void executeClassement(NodeRef dossier);
	
	public Map<String, Object> getCriteresClassement();
}
