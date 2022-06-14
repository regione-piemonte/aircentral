/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for analyzer status
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: AnalyzerStatus.java,v 1.2 2014/09/24 12:09:28 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import it.csi.centrale.polling.data.AnalyzerCfg.Status;

import java.util.UUID;
/**
 * Class for analyzer status
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class AnalyzerStatus {

	private UUID id;
	private Status status;
	private Boolean faultStatus;
	private Boolean dataValidStatus;
	private Boolean maintenanceInProgress;
	private Boolean manualCalibrationRunning;
	private Boolean autoCheckRunning;
	private Boolean autoCheckFailed;

	public AnalyzerStatus(UUID id, Status status, Boolean faultStatus,
			Boolean dataValidStatus, Boolean maintenanceInProgress,
			Boolean manualCalibrationRunning, Boolean autoCheckRunning,
			Boolean autoCheckFailed) {
		this.id = id;
		this.status = status;
		this.faultStatus = faultStatus;
		this.dataValidStatus = dataValidStatus;
		this.maintenanceInProgress = maintenanceInProgress;
		this.manualCalibrationRunning = manualCalibrationRunning;
		this.autoCheckRunning = autoCheckRunning;
		this.autoCheckFailed = autoCheckFailed;
	}

	public UUID getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public Boolean getFaultStatus() {
		return faultStatus;
	}

	public Boolean getDataValidStatus() {
		return dataValidStatus;
	}

	public Boolean getMaintenanceInProgress() {
		return maintenanceInProgress;
	}

	public Boolean getManualCalibrationRunning() {
		return manualCalibrationRunning;
	}

	public Boolean getAutoCheckRunning() {
		return autoCheckRunning;
	}

	public Boolean getAutoCheckFailed() {
		return autoCheckFailed;
	}

	@Override
	public String toString() {
		return "AnalyzerStatus [id=" + id + ", status=" + status
				+ ", faultStatus=" + faultStatus + ", dataValidStatus="
				+ dataValidStatus + ", maintenanceInProgress="
				+ maintenanceInProgress + ", manualCalibrationRunning="
				+ manualCalibrationRunning + ", autoCheckRunning="
				+ autoCheckRunning + ", autoCheckFailed=" + autoCheckFailed
				+ "]";
	}

	public static AnalyzerStatus valueOf(String value)
			throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException("Null argument not allowed");
		String[] fields = value.split(",", 8);
		if (fields.length < 7 || fields.length > 8)
			throw new IllegalArgumentException("7 or 8 fields expected, found "
					+ fields.length);
		int index = 0;
		return new AnalyzerStatus(
				UUID.fromString(fields[index++]), //
				Status.valueOf(fields[index++].toUpperCase()),
				fields.length == 8 ? parseBinaryStatus(fields[index++]) : null,
				parseBinaryStatus(fields[index++]),
				parseBinaryStatus(fields[index++]),
				parseBinaryStatus(fields[index++]),
				parseBinaryStatus(fields[index++]),
				parseBinaryStatus(fields[index++]));
	}

	private static Boolean parseBinaryStatus(String value) {
		if (value.isEmpty())
			return null;
		value = value.trim();
		if ("1".equals(value))
			return true;
		if ("0".equals(value))
			return false;
		throw new IllegalArgumentException("Unparsable value for "
				+ "binary status field: '" + value + "'");
	}

}
