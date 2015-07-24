/**
 * 
 */
package fr.cnav.alfresco.as.service;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author L306093
 *
 */
public interface AssureService {
	
	public NodeRef creationAssure(NodeRef parent, Map<QName, Serializable> lesMetadatas);
	

}
