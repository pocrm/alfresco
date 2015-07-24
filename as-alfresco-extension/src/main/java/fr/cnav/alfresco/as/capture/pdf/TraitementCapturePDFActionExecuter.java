/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnav.alfresco.as.capture.pdf;

import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

import fr.cnav.alfresco.as.capture.pdf.metier.PliCapture;
import fr.cnav.alfresco.as.exception.FonctionnelleException;
import fr.cnav.alfresco.as.exception.TechniqueException;
import fr.cnav.alfresco.as.service.PliService;

/**
 * TraitementCapturePDF action executer.
 * 
 * Cette action transforme les plis CAPTURE déposés dans le répertoire
 * Import/Entrant. Un pli CAPTURE est composé d'un fichier nom_pli.xml, d'un
 * fichier nom_pli.go (vide) et de x images TIF Le fichier xml décrit la
 * composition des différents documents du pli (métadonnées, images composant
 * les documents)
 * 
 * L'action reforme les documents, au format PDF à partir des pages numérisées,
 * selon les informations du fichier XML
 * 
 * Le fichier XML est transformé en classe Lot par l'API JAXB.
 * 
 * Les documents PDF sont générés (avec les métadonnées issues du fichier XML)
 * dans le répertoiredu pli, puis le pli est déplacé dans le répertoire ATraiter
 * .
 * 
 * L'aspect ASignaler est positionné par une règle de contenu sur le répertoire
 * ATraiter.
 * 
 * Toute exception (Technique ou Fonctionnelle) est signalée par Email aux
 * adresses respectives définies dans penibilite.properties
 * 
 * Fichier context associé ==>
 * config/alfresco/extension/action-services-context.xml
 * 
 * 
 * @author CNAV
 */
public class TraitementCapturePDFActionExecuter extends ActionExecuterAbstractBase {

	static Logger logger = Logger.getLogger(TraitementCapturePDFActionExecuter.class);

	private PliService pliService;

	public void setPliService(PliService pliService) {
		this.pliService = pliService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

		PliCapture unPli = null;

		try {

			logger.trace("******* DEBUT ACTION  " + action.getActionDefinitionName() + " ***********");

			unPli = new PliCapture(actionedUponNodeRef);

			pliService.executeTraitementPli(actionedUponNodeRef);

			logger.trace("******** FIN ACTION  " + action.getActionDefinitionName() + " ***********");

		}

		catch (FonctionnelleException e) {
			logger.error("Erreur fonctionnelle pour le pli " + unPli.getNomPli() + " - " + e.getClass().getName());

		} catch (TechniqueException e) {

			logger.error("Erreur Technique :  ACTION  " + action.getActionDefinitionName() + " - " + e.getMessage());

		}

		catch (Exception e) {

			logger.error("Erreur innatendue : ACTION  " + action.getActionDefinitionName() + " - " + e.getMessage());

			e.printStackTrace();
		}

	}

	/**
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}

}
