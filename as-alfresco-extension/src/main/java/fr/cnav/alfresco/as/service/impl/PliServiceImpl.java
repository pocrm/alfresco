/**
 * 
 */
package fr.cnav.alfresco.as.service.impl;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.helper.RepositoryHelper;
import fr.cnav.alfresco.as.service.PliService;

/**
 * @author LECOCQ
 *
 */


public class PliServiceImpl implements PliService {
	
	private RepositoryHelper repoHelper;
	

	public void setRepoHelper(RepositoryHelper repoHelper) {
		this.repoHelper = repoHelper;
	}

	/* (non-Javadoc)
	 * @see fr.cnav.alfresco.as.service.PliService#executeTraitementPli(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public void executeTraitementPli(NodeRef pli) throws Exception {
		// TODO Auto-generated method stub
		//Creation du pli
		// transformation du pli
		
		

	}

	/* (non-Javadoc)
	 * @see fr.cnav.alfresco.as.service.PliService#deleteContenuPli(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, boolean)
	 */
	public void deleteContenuPli(NodeRef pli, QName type, boolean supprimeType)
			throws TechniqueException {
		// TODO Auto-generated method stub

	}

}
