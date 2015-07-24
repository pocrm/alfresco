package fr.cnav.alfresco.as.capture.pdf.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import fr.cnav.alfresco.as.helper.RepositoryHelper;
import fr.cnav.alfresco.as.model.AsModel;

public class CreateContentEntrantBehaviour implements NodeServicePolicies.OnCreateNodePolicy {

	// Dependencies
	private NodeService nodeService;
	private PolicyComponent policyComponent;
	RepositoryHelper repoHelper = RepositoryHelper.getInstance();

	static Logger logger = Logger.getLogger(CreateContentEntrantBehaviour.class);

	public void init() {

		this.policyComponent.bindClassBehaviour(OnCreateNodePolicy.QNAME, ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode",
				NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	@Override
	public void onCreateNode(ChildAssociationRef node) {
		try {

			logger.setLevel(repoHelper.getLogLevel());

			logger.trace("**** DEBUT Behaviour CreateContentEntrantBehaviour.onCreateNode *******");

			QName typeNodeParent = nodeService.getType(nodeService.getPrimaryParent(node.getChildRef()).getParentRef());
			QName typeNode = nodeService.getType(node.getChildRef());

			logger.debug("Node ajoute : " + repoHelper.getFileInfo(node.getChildRef()).getName() + " dont le parent est de type : "
					+ typeNodeParent.getLocalName());

			// le type du parent est-il bien as:pliCapture ?
			if (AsModel.TYPE_PLICAPTURE.isMatch(typeNodeParent)) {
				// si le type est as:piece
				if (AsModel.TYPE_PIECE.isMatch(typeNode)) {
					logger.debug("Pas d'indexation du contenu");
					repoHelper.addAspectNoIndexContent(node.getChildRef());

				} else {
					// si le type n'est pas as:piece
					logger.debug("Pas d'indexation du tout");
					repoHelper.addAspectNoIndex(node.getChildRef());
					
				}
				// si le parent n'est pas de type as:pliCapture on ne fait rien
			}

			logger.trace("**** FIN Behaviour CreateContentEntrantBehaviour.onCreateNode *******");
			return;
		} catch (Exception e) {

			logger.error("Erreur lors du behaviour  CreateContentEntrantBehaviour.onCreateNode : " + e.getMessage());
			e.printStackTrace();
		}

	}

}
