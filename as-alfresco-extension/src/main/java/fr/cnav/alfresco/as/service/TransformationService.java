/**
 * 
 */
package fr.cnav.alfresco.as.service;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author LECOCQ
 *
 */
public interface TransformationService {

	public List<NodeRef> executeTransformation(NodeRef pli);
}
