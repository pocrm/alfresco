package fr.cnav.alfresco.as.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.capture.pdf.metier.DocumentCapture;

public interface TransformationPDFService {

	public ByteArrayInputStream transforme(DocumentCapture unDocument) throws TechniqueException;

	public Map<String, ByteArrayInputStream> transforme(List<DocumentCapture> lesDocuments) throws TechniqueException;
}
