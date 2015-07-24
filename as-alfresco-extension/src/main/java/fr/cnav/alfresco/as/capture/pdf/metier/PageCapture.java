package fr.cnav.alfresco.as.capture.pdf.metier;

import java.io.IOException;
import java.io.InputStream;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.MD5;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.helper.RepositoryHelper;

public class PageCapture {

	private NodeRef nodeTiff;
	private String hashMD5;
	private String endossage;

	static Logger logger = Logger.getLogger(PageCapture.class);

	public PageCapture(NodeRef nodeTiff) {
		super();
		this.nodeTiff = nodeTiff;

	}

	public boolean isModified() throws TechniqueException {

		RepositoryHelper repoHelper = RepositoryHelper.getInstance();
		logger.setLevel(repoHelper.getLogLevel());
		logger.trace("***** DEBUT isModified");

		InputStream isMd5 = getPageStream();
		String md5Calcul;
		try {
			md5Calcul = MD5.Digest(IOUtils.toByteArray(isMd5)).toUpperCase();

			if (getHashMD5().equals(md5Calcul)) {

				logger.debug("***** FIN isModified  : L'image est conforme à l'original - cle de scellement = " + getHashMD5());
				return false;
			} else {
				logger.info("********************************");
				logger.info("L'image n'est pas conforme à l'original");
				logger.info("cle de scellement originale = " + getHashMD5());
				logger.info("cle de scellement claculée = " + md5Calcul);
				logger.info("********************************");
				logger.trace("***** FIN isModified");
				return true;
			}
		} catch (IOException e) {
			throw new TechniqueException(PliCapture.ERREUR_TECHNIQUE_SCELLEMENT, e);
		}

	}

	public NodeRef getNodeTiff() {
		return nodeTiff;
	}

	public void setNodeTiff(NodeRef nodeTiff) {
		this.nodeTiff = nodeTiff;
	}

	public String getHashMD5() {
		return hashMD5;
	}

	public void setHashMD5(String hashMD5) {
		this.hashMD5 = hashMD5;
	}

	public String getEndossage() {
		return endossage;
	}

	public void setEndossage(String endossage) {
		this.endossage = endossage;
	}

	public InputStream getPageStream() {

		return RepositoryHelper.getInstance().getReader(nodeTiff, ContentModel.PROP_CONTENT).getContentInputStream();
	}

}
