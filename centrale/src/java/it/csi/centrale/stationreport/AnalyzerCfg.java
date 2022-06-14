/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class for analyzer configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 */
public abstract class AnalyzerCfg {

	public enum Status {
		ENABLED, DISABLED, OUT_OF_ORDER, REMOVED, DELETED
	};

	private UUID id;
	private String name;
	private String brand;
	private String model;
	private String description;
	private String serialNumber;
	private String userNotes;
	private Double minVoltage;
	private Double maxVoltage;
	private Boolean minRangeExtension;
	private Boolean maxRangeExtension;
	private Boolean differentialModeNeeded;
	private String hostName;
	private Integer ipPort;
	private String driverParams;
	private Status status;
	private Date deletionDate;
	private List<ElementCfg> listElement = new ArrayList<ElementCfg>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getIdAsString() {
		if (id == null)
			return "";
		return id.toString();
	}

	public void setIdAsString(String strId) {
		this.id = UUID.fromString(strId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(String userNotes) {
		this.userNotes = userNotes;
	}

	public Double getMaxVoltage() {
		return maxVoltage;
	}

	public void setMaxVoltage(Double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}

	public Double getMinVoltage() {
		return minVoltage;
	}

	public void setMinVoltage(Double minVoltage) {
		this.minVoltage = minVoltage;
	}

	public Boolean isDifferentialModeNeeded() {
		return differentialModeNeeded;
	}

	public void setDifferentialModeNeeded(Boolean differentialModeNeeded) {
		this.differentialModeNeeded = differentialModeNeeded;
	}

	public Boolean getMinRangeExtension() {
		return minRangeExtension;
	}

	public void setMinRangeExtension(Boolean minRangeExtension) {
		this.minRangeExtension = minRangeExtension;
	}

	public Boolean getMaxRangeExtension() {
		return maxRangeExtension;
	}

	public void setMaxRangeExtension(Boolean maxRangeExtension) {
		this.maxRangeExtension = maxRangeExtension;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getIpPort() {
		return ipPort;
	}

	public void setIpPort(Integer ipPort) {
		this.ipPort = ipPort;
	}

	public String getDriverParams() {
		return driverParams;
	}

	public void setDriverParams(String driverParams) {
		this.driverParams = driverParams;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean isEnabled() {
		return status == Status.ENABLED;
	}

	public String getStatusAsString() {
		return status.toString().toLowerCase();
	}

	public void setStatusAsString(String strStatus) {
		setStatus(Status.valueOf(strStatus.toUpperCase()));
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public List<ElementCfg> getListElement() {
		return listElement;
	}

	public void setListElement(List<ElementCfg> listElement) {
		this.listElement = listElement;
	}

}
