/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about an analyzer
 * Change log:
 *   2008-10-01: initial version
 * ----------------------------------------------------------------------------
 * $Id: Analyzer.java,v 1.39 2014/10/15 16:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.CentraleUtil;
import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.AnalyzerCfg;
import it.csi.centrale.polling.data.AnalyzerCfg.Status;
import it.csi.centrale.polling.data.AnalyzerCfg.Type;
import it.csi.centrale.polling.data.ElementCfg;
import it.csi.centrale.polling.data.ScalarElementCfg;
import it.csi.centrale.polling.data.WindElementCfg;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Class for storing information about an analyzer
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
/**
 * @author 1736
 *
 */
public class Analyzer implements DataNode, Serializable {

	static Logger logger = Logger.getLogger("centrale."
			+ Analyzer.class.getSimpleName());

	private static final long serialVersionUID = 3100973045004081814L;

	private String AnalyzerUUID;

	private String name;

	private String brand;

	private String model;

	private String description;

	private String serialNumber;

	private String status;

	private String type;

	private String notes;

	private int analyzerId;

	private Date deletionDate;

	private Date updateDate;

	private LinkedList<GenericElement> elements;

	private Vector<AnalyzerEventDatum> anStatus; // contains actual analyzer
													// status

	public Analyzer() {
		AnalyzerUUID = "";
		name = "";
		brand = "";
		model = "";
		description = "";
		serialNumber = "";
		status = "";
		type = "";
		notes = "";
		analyzerId = -1;
		elements = null;
		deletionDate = null;
		anStatus = new Vector<AnalyzerEventDatum>();
	}

	public Analyzer(AnalyzerCfg config) throws PollingException {
		this();
		AnalyzerUUID = config.getId().toString();
		instantiateLinkedList();
		update(config);
	}

	public void instantiateLinkedList() {
		elements = new LinkedList<GenericElement>();
	}

	/**
	 * @return size of element
	 */
	public int getElementSize() {
		return elements.size();
	}

	/**
	 * @return iterator of list
	 */
	public Iterator<GenericElement> getElementListIterator() {

		return elements.iterator();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * @return model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return uuid
	 */
	public String getAnalyzerUUID() {
		return AnalyzerUUID;
	}

	/**
	 * @param AnalyzerUUID
	 */
	public void setAnalyzerUUID(String AnalyzerUUID) {
		this.AnalyzerUUID = AnalyzerUUID;
	}

	/**
	 * @return delete date
	 */
	public Date getDeletionDate() {
		return deletionDate;
	}

	/**
	 * @param deletionDate
	 */
	public void setDeleted(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	/**
	 * @return analyzerId
	 */
	public int getAnalyzerId() {
		return analyzerId;
	}

	/**
	 * @param analyzerId
	 */
	public void setAnalyzerId(int analyzerId) {
		this.analyzerId = analyzerId;
	}

	public LinkedList<GenericElement> getElements() {
		return elements;
	}

	public void setElements(LinkedList<GenericElement> elements) {
		this.elements = elements;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Vector<AnalyzerEventDatum> getAnStatus() {
		return anStatus;
	}

	public void setAnStatus(Vector<AnalyzerEventDatum> anStatus) {
		this.anStatus = anStatus;
	}

	public void update(AnalyzerCfg config) throws PollingException {
		if (!config.getId().toString().equals(AnalyzerUUID))
			throw new PollingException("Cannot update analyzer "
					+ "configuration, UUID mismatch: expected'" + AnalyzerUUID
					+ "' found '" + config.getId() + "'");
		name = config.getName();
		brand = config.getBrand();
		model = config.getModel();
		description = config.getDescription();
		serialNumber = config.getSerialNumber();
		status = CentraleUtil.toString(config.getStatus());
		type = CentraleUtil.toString(config.getType());
		notes = config.getNotes();
		if (config.getStatus() == Status.DELETED) {
			if (deletionDate == null)
				deletionDate = new Date();
		} else {
			deletionDate = null;
		}

		Map<String, ElementCfg> mapElemConfigs = new HashMap<String, ElementCfg>();
		for (ElementCfg elemCfg : config.getListElements())
			mapElemConfigs.put(elemCfg.getParameterId(), elemCfg);
		Iterator<GenericElement> itElements = elements.iterator();
		while (itElements.hasNext()) {
			GenericElement ge = itElements.next();
			ElementCfg cfg = mapElemConfigs.get(ge.getName());
			if (cfg == null) {
				ge.setDeleted();
				continue;
			}
			ge.update(cfg);
			mapElemConfigs.remove(ge.getName());
		}
		for (ElementCfg cfg : mapElemConfigs.values()) {
			if (cfg instanceof ScalarElementCfg)
				elements.add(new ScalarElement((ScalarElementCfg) cfg, config
						.getType() == Type.RAIN ? GenericElement.RAIN
						: GenericElement.SCALAR, this));
			else if (cfg instanceof WindElementCfg)
				elements.add(new WindElement((WindElementCfg) cfg, this));
			else
				throw new PollingException("Unknow type of element "
						+ " configuration '" + cfg.getClass().getSimpleName()
						+ "'");
		}
	}

	public void setDeleted() {
		if (isDeleted())
			return;
		deletionDate = new Date();
		status = CentraleUtil.toString(Status.DELETED);
		if (elements != null)
			for (GenericElement ge : elements)
				ge.setDeleted();
	}

	public boolean isDeleted() {
		return deletionDate != null;
	}

	@Override
	public boolean isActive() {
		return !isDeleted() && Status.ENABLED.toString().equals(status);
	}

}
