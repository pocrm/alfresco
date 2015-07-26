package fr.cnav.alfresco.as.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.GetNodesWithAspectCannedQueryFactory;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.springframework.beans.factory.InitializingBean;

import fr.cnav.alfresco.as.capture.pdf.pli.PliCapture;
import fr.cnav.alfresco.as.exception.PliExistantException;
import fr.cnav.alfresco.as.exception.PropertiesException;
import fr.cnav.alfresco.as.exception.TechniqueException;


public class RepositoryHelper implements InitializingBean{

	public static final String GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF = "cpp.pdf.delais.traitement";
	public static final String GLOBAL_PROPS_LOG_LEVEL = "cpp.log_level";
	public static final String GLOBAL_PROPS_URL_DOC = "cpp.url.doc";
	public static final String GLOBAL_PROPS_LOCK_TTL = "cpp.lock.ttl";

//	public static RepositoryHelper getInstance() {
//
//		return instance;
//
//	}

	private ServiceRegistry serviceRegistry;
	private  GetNodesWithAspectCannedQueryFactory nodesWithAspectCannedQueryFactory;
	private  NodeService nodeService;
	private  Properties properties;
	
	private  ContentService contentService;
	private FileFolderService fileFolderService;
	private  SearchService searchService;
	
	private StoreRef nodeStore;
	private Map<String, String> lesProprietes;

	static RepositoryHelper instance;

	static Logger logger = Logger.getLogger(RepositoryHelper.class);

	public static RepositoryHelper initialise(Properties properties, ServiceRegistry serviceRegistry, 
			GetNodesWithAspectCannedQueryFactory cannedQuerryFactory) throws TechniqueException {

		if (instance == null) {
			instance = new RepositoryHelper(properties, serviceRegistry, cannedQuerryFactory);
		}
		return instance;
	}

	private RepositoryHelper(Properties cppProperties, ServiceRegistry serviceRegistry, 
			GetNodesWithAspectCannedQueryFactory cannedQuerryFactory) throws TechniqueException {

		this.serviceRegistry = serviceRegistry;
		this.nodesWithAspectCannedQueryFactory = cannedQuerryFactory;
		this.properties = cppProperties;
		this.nodeService = serviceRegistry.getNodeService();
		this.contentService = serviceRegistry.getContentService();
		this.fileFolderService = serviceRegistry.getFileFolderService();
		this.searchService = serviceRegistry.getSearchService();
		
		this.nodeStore = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		this.initLesProprietes();
		logger.setLevel(getLogLevel());

	}

	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setNodesWithAspectCannedQueryFactory(
			GetNodesWithAspectCannedQueryFactory nodesWithAspectCannedQueryFactory) {
		this.nodesWithAspectCannedQueryFactory = nodesWithAspectCannedQueryFactory;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean hasAspect(NodeRef node, QName aspect) throws TechniqueException {
		boolean bret = false;

		try {

			bret = nodeService.hasAspect(node, aspect);
			logger.debug("[" + aspect.getLocalName() + "] : " + bret);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException(e);
		}

		return bret;
	}

	

	public NodeRef createNode(NodeRef parent, String name, QName contentType) throws TechniqueException, PliExistantException {

		try {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
			props.put(ContentModel.PROP_NAME, name);

			// use the node service to create a new node
			return nodeService.createNode(parent, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
					contentType, props).getChildRef();
		} catch (DuplicateChildNodeNameException e) {

			throw new PliExistantException(PliCapture.ERREUR_CREATION_PDF + PliCapture.ERREUR_PLI_EXISTANT + name, e);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException("Erreur lors de la création du document GED : " + name, e);
		}

	}

	public NodeRef createNodeWithProps(NodeRef parent, String name, QName contentType, Map<QName, Serializable> props) throws TechniqueException {

		try {
			props.put(ContentModel.PROP_NAME, name);

			// use the node service to create a new node
			return nodeService.createNode(parent, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
					contentType, props).getChildRef();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException("Erreur lors de la creation du noeud : " + name, e);
		}

	}

	public void delete(NodeRef node) throws TechniqueException {
		try {
			nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(node);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException("Erreur lors de la suppression du noeud " + node.getId(), e);
		}
	}

	public void deleteContenuPli(NodeRef nodeRefFolder,QName type) throws TechniqueException {
		logger.trace("***** DEBUT deleteContenuPli");
		try {

			List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRefFolder);
			for (ChildAssociationRef childAssoc : children) {
				NodeRef childNodeRef = childAssoc.getChildRef();
				// si le noeud enfant n'est pas de type docCpp ==> on supprime
				if (!fileFolderService.getFileInfo(childNodeRef).getType().isMatch(type)) {
					this.delete(childNodeRef);
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException("Erreur lors de la suppression du repertoire : " + nodeRefFolder.getId(), e);
		}
		logger.trace("***** FIN deleteContenuPli");
	}

	public ResultSet doQuery(SearchParameters sp) {

		return searchService.query(sp);
	}

	public boolean exists(NodeRef actionedUponNodeRef) {

		return nodeService.exists(actionedUponNodeRef);
	}

	public String getDelais(String propsDelais) {

		return lesProprietes.get(propsDelais);
	}

	public FileInfo getFileInfo(NodeRef arg0) {
		return fileFolderService.getFileInfo(arg0);
	}

	private String getCppProperty(String key) throws TechniqueException {
		String value = properties.getProperty(key);

		if (value == null) {
			logger.error("Initialisation du RepositoryHelper : la propriete " + key + " est non definie dans penibilite.properties");
			throw new PropertiesException("Initialisation du RepositoryHelper : La propriété " + key
					+ " n'est pas définie dans penibilite.properties");
		} else {
			return value;
		}

	}

	public Map<String, String> getLesProprietes() {
		return lesProprietes;
	}

	public Level getLogLevel() {

		return Level.toLevel(lesProprietes.get(GLOBAL_PROPS_LOG_LEVEL));
	}

	public StoreRef getNodeStore() {
		return nodeStore;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public NodeRef getParent(NodeRef nodeRef) {

		return nodeService.getPrimaryParent(nodeRef).getParentRef();
	}

	public ChildAssociationRef getPrimaryParent(NodeRef arg0) throws InvalidNodeRefException {

		return nodeService.getPrimaryParent(arg0);
	}

	public Serializable getProperty(NodeRef nodeRef, QName qName) throws InvalidNodeRefException {
		return nodeService.getProperty(nodeRef, qName);
	}

	public ContentReader getReader(NodeRef arg0, QName arg1) throws InvalidNodeRefException, InvalidTypeException {
		return contentService.getReader(arg0, arg1);
	}

	public ContentWriter getWriter(NodeRef arg0, QName arg1, boolean arg2) throws InvalidNodeRefException, InvalidTypeException {
		return contentService.getWriter(arg0, arg1, arg2);
	}

	private void initLesProprietes() throws TechniqueException {
		lesProprietes = new HashMap<String, String>();

		lesProprietes.put(GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF, getCppProperty(GLOBAL_PROPS_DELAIS_TRAITEMENT_PDF));
		lesProprietes.put(GLOBAL_PROPS_LOCK_TTL, getCppProperty(GLOBAL_PROPS_LOCK_TTL));
		lesProprietes.put(GLOBAL_PROPS_LOG_LEVEL, getCppProperty(GLOBAL_PROPS_LOG_LEVEL));
		lesProprietes.put(GLOBAL_PROPS_URL_DOC, getCppProperty(GLOBAL_PROPS_URL_DOC));
		

	}

	public List<NodeRef> searchFiles(NodeRef nodeRefFolder, String pattern) {

		List<NodeRef> lesNodes = new ArrayList<NodeRef>();

		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRefFolder);
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			if (fileFolderService.getFileInfo(childNodeRef) != null && fileFolderService.getFileInfo(childNodeRef).getName().endsWith(pattern)) {

				lesNodes.add(childNodeRef);

			}

		}
		return lesNodes;
	}

	public NodeRef searchSimple(NodeRef folder, String nomNode) {
		return fileFolderService.searchSimple(folder, nomNode);
	}

	public void setNodeStore(StoreRef nodeStore) {
		this.nodeStore = nodeStore;
	}


	public boolean isDelaisDepasseMinutes(DateTime dateReference, String property) {

		Minutes delais = Minutes.minutes(Integer.parseInt(getDelais(property)));
		return Minutes.minutesBetween(dateReference, new DateTime(new Date())).isGreaterThan(delais);
	}

	public boolean isDelaisDepasseMinutes(DateTime dateReference, int delaisMin) {

		Minutes delais = Minutes.minutes(delaisMin);
		return Minutes.minutesBetween(dateReference, new DateTime(new Date())).isGreaterThan(delais);
	}

	public boolean isDelaisDepasseJours(DateTime dateReference, String property) {

		Days delaisJour = Days.days(Integer.parseInt(getDelais(property)));
		return Days.daysBetween(dateReference, new DateTime(new Date())).isGreaterThan(delaisJour);
	}


	public boolean isNodeNameExistsInFolder(NodeRef folder, String nomPli) {

		logger.debug("Recherche du node " + nomPli + " dans le repertoire " + fileFolderService.getFileInfo(folder).getName());
		return (fileFolderService.searchSimple(folder, nomPli) != null);

	}
	
	public String getUrlDoc() {

		return lesProprietes.get(GLOBAL_PROPS_URL_DOC) + getNodeStore().toString() + "/";
	}

	
	public Properties getGlobalProperties() {
		return properties;
	}

	
	public void addAspectControlIndex(NodeRef node, boolean isIndexed, boolean isContentIndexed) throws TechniqueException {
		logger.trace("***** DEBUT addAspectControlIndex");

		try {

			Map<QName, Serializable> Aspect_Value = null;

			QName Aspect_Prop = null;
			Aspect_Value = new HashMap<QName, Serializable>();
			Aspect_Prop = ContentModel.PROP_IS_INDEXED;
			Aspect_Value.put(Aspect_Prop, isIndexed);

			Aspect_Prop = ContentModel.PROP_IS_CONTENT_INDEXED;
			Aspect_Value.put(Aspect_Prop, isContentIndexed);

			nodeService.addAspect(node, ContentModel.ASPECT_INDEX_CONTROL, Aspect_Value);
			Aspect_Value.clear();

		}

		catch (Exception e) {
			logger.error(e.getMessage());
			throw new TechniqueException("Erreur de mise à jour (ajout) d'aspect IndexControl", e);
		}

		logger.trace("***** FIN addAspectControlIndex");
		;

	}

	public void addAspectNoIndex(NodeRef node) throws TechniqueException {
		logger.trace("***** DEBUT addAspectNoIndex");

		addAspectControlIndex(node, false, false);

		logger.trace("***** FIN addAspectNoIndex");
	}

	public void addAspectNoIndexContent(NodeRef node) throws TechniqueException {
		logger.trace("***** DEBUT addAspectNoIndexFT");

		addAspectControlIndex(node, true, false);

		logger.trace("***** FIN addAspectNoIndexFT");
	}

	public GetNodesWithAspectCannedQueryFactory getNodesWithAspectCannedQueryFactory() {
		return nodesWithAspectCannedQueryFactory;
	}

	public FileFolderService getFileFolderService() {
		
		return fileFolderService;
	}

	public void move(NodeRef nodeSource, NodeRef destination) throws FileExistsException, FileNotFoundException, TechniqueException {

		logger.debug("Déplacement du node " + nodeSource.getId() + " vers le répertoire " + destination.getId());
		
		try {
		
			fileFolderService.moveFrom(nodeSource, nodeService.getPrimaryParent(nodeSource).getParentRef(), destination, null);
		} catch (Exception e) {
			logger.error("Probleme de deplacement du node : " + nodeSource + " - " + e.getMessage());
		}
	}

	public AssociationRef createRelation(NodeRef source, NodeRef target, QName relation){
		
		return nodeService.createAssociation(source, target, relation);
	}

	public void afterPropertiesSet() throws Exception {
		
		nodeService = serviceRegistry.getNodeService();
		contentService = serviceRegistry.getContentService();
		fileFolderService = serviceRegistry.getFileFolderService();
		searchService = serviceRegistry.getSearchService();
		
		this.nodeStore = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		this.initLesProprietes();
		logger.setLevel(getLogLevel());
		
	}

	public String getNodeName(NodeRef nodeRef) {
		
		return (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
	}

	public DateTime getCreatedDate(NodeRef nodeRef) {
		
		return new DateTime(nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED));
	}

}
