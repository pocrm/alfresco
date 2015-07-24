/**
 * 
 */
package fr.cnav.alfresco.as.service;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;



/**
 * @author L306093
 * 
 */
public interface ClassementService {

	public void classementDocument(NodeRef unDocument) throws Exception;

	
	public NodeRef getNodeAssure(String nir) throws Exception;


}
