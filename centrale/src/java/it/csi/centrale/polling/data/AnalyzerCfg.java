/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for analyzer configuration
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: AnalyzerCfg.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * Class for analyzer configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class AnalyzerCfg {

	public enum Status {
		ENABLED, DISABLED, OUT_OF_ORDER, REMOVED, DELETED
	};

	public enum Type {
		DPA, AVG, RAIN, SAMPLE, WIND
	};

	private UUID id;
	private String name;
	private String brand;
	private String model;
	private String description;
	private String serialNumber;
	private Status status;
	private Type type;
	private String notes;
	private List<ElementCfg> listElements;

	public AnalyzerCfg(UUID id, Map<String, String> attributes)
			throws IllegalArgumentException {
		this.id = id;
		this.name = attributes.get("name");
		this.brand = attributes.get("brand");
		this.model = attributes.get("model");
		this.description = attributes.get("description");
		this.serialNumber = attributes.get("sn");
		this.status = parseStatus(attributes.get("status"));
		this.type = parseType(attributes.get("type"));
		this.notes = attributes.get("notes");
		listElements = new ArrayList<ElementCfg>();
	}

	private Status parseStatus(String value) {
		if (value == null)
			throw new IllegalArgumentException("Missing attribute 'status'");
		return Status.valueOf(value.toUpperCase());
	}

	private Type parseType(String value) {
		if (value == null)
			throw new IllegalArgumentException("Missing attribute 'type'");
		return Type.valueOf(value.toUpperCase());
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public String getModel() {
		return model;
	}

	public String getDescription() {
		return description;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public Status getStatus() {
		return status;
	}

	public Type getType() {
		return type;
	}

	public String getNotes() {
		return notes;
	}

	public List<ElementCfg> getListElements() {
		return listElements;
	}

	public void addElement(ElementCfg elementCfg) {
		listElements.add(elementCfg);
	}

	@Override
	public String toString() {
		return "AnalyzerCfg [id=" + id + ", name=" + name + ", brand=" + brand
				+ ", model=" + model + ", description=" + description
				+ ", serialNumber=" + serialNumber + ", status=" + status
				+ ", type=" + type + ", notes=" + notes + ", listElements="
				+ listElements + "]";
	}

}
