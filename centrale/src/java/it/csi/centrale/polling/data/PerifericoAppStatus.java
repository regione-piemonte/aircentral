/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: holds the status of the application
// Change log:
//   2014-08-29: initial version
// ----------------------------------------------------------------------------
// $Id: PerifericoAppStatus.java,v 1.3 2015/10/19 13:22:22 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.polling.data;

import java.util.Map;

/**
 * holds the status of the application
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class PerifericoAppStatus {

	public enum ConfigStatus {
		MISSING, CORRUPTED, NO_CHK, CHK_ERR, OK
	}

	private boolean ok = false;
	private Boolean boardManagerInitStatus = null;
	private Integer configuredBoardsNumber = null;
	private Integer initializedBoardsNumber = null;
	private Integer failedBoardBindingsNumber = null;
	private Boolean dpaOK = null;
	private Boolean driverConfigsOK = null;
	private Integer enabledDataPortAnalyzersNumber = null;
	private Integer initializedDataPortDriversNumber = null;
	private Integer failedDataPortThreadsNumber = null;
	private ConfigStatus loadConfigurationStatus = null;
	private Boolean saveNewConfigurationStatus = null;
	private Boolean configActivationStatus = null;
	private CommonConfigResult commonConfigFromCopStatus = null;
	private boolean dataInTheFuture = false;
	private int totalThreadFailures = 0;
	private int currentThreadFailures = 0;
	private int dataWriteErrorCount = 0;

	public PerifericoAppStatus(boolean ok, Boolean boardManagerInitStatus,
			Integer configuredBoardsNumber, Integer initializedBoardsNumber,
			Integer failedBoardBindingsNumber, Boolean dpaOK,
			Boolean driverConfigsOK, Integer enabledDataPortAnalyzersNumber,
			Integer initializedDataPortDriversNumber,
			Integer failedDataPortThreadsNumber,
			ConfigStatus loadConfigurationStatus,
			Boolean saveNewConfigurationStatus, Boolean configActivationStatus,
			CommonConfigResult commonConfigFromCopStatus,
			boolean dataInTheFuture, int totalThreadFailures,
			int currentThreadFailures, int dataWriteErrorCount) {
		this.ok = ok;
		this.boardManagerInitStatus = boardManagerInitStatus;
		this.configuredBoardsNumber = configuredBoardsNumber;
		this.initializedBoardsNumber = initializedBoardsNumber;
		this.failedBoardBindingsNumber = failedBoardBindingsNumber;
		this.dpaOK = dpaOK;
		this.driverConfigsOK = driverConfigsOK;
		this.enabledDataPortAnalyzersNumber = enabledDataPortAnalyzersNumber;
		this.initializedDataPortDriversNumber = initializedDataPortDriversNumber;
		this.failedDataPortThreadsNumber = failedDataPortThreadsNumber;
		this.loadConfigurationStatus = loadConfigurationStatus;
		this.saveNewConfigurationStatus = saveNewConfigurationStatus;
		this.configActivationStatus = configActivationStatus;
		this.commonConfigFromCopStatus = commonConfigFromCopStatus;
		this.dataInTheFuture = dataInTheFuture;
		this.totalThreadFailures = totalThreadFailures;
		this.currentThreadFailures = currentThreadFailures;
		this.dataWriteErrorCount = dataWriteErrorCount;
	}

	public PerifericoAppStatus(Map<String, String> attributes)
			throws IllegalArgumentException {
		ok = new Boolean(attributes.get("isOK"));
		boardManagerInitStatus = mkBool(attributes.get("boardManInitStatus"));
		configuredBoardsNumber = mkInt(attributes.get("cfgBoardsNumber"));
		initializedBoardsNumber = mkInt(attributes.get("initBoardsNumber"));
		failedBoardBindingsNumber = mkInt(attributes
				.get("failedBoardBindingsNumber"));
		dpaOK = mkBool(attributes.get("areDPAOK"));
		driverConfigsOK = mkBool(attributes.get("areDriverConfigsOK"));
		enabledDataPortAnalyzersNumber = mkInt(attributes
				.get("enabledDPANumber"));
		initializedDataPortDriversNumber = mkInt(attributes
				.get("initDPADriversNumber"));
		failedDataPortThreadsNumber = mkInt(attributes
				.get("failedDPAThreadsNumber"));
		loadConfigurationStatus = mkCfgStatus(attributes.get("loadCfgStatus"));
		saveNewConfigurationStatus = mkBool(attributes.get("saveCfgStatus"));
		configActivationStatus = mkBool(attributes.get("cfgActivationStatus"));
		commonConfigFromCopStatus = mkCCResult(attributes
				.get("commonCfgFromCopStatus"));
		dataInTheFuture = new Boolean(attributes.get("dataInTheFuture"));
		totalThreadFailures = new Integer(attributes.get("totalThreadFailures"));
		currentThreadFailures = new Integer(
				attributes.get("currentThreadFailures"));
		dataWriteErrorCount = new Integer(attributes.get("dataWriteErrorCount"));
	}

	private Boolean mkBool(String value) {
		return value == null ? null : Boolean.valueOf(value);
	}

	private Integer mkInt(String value) {
		return value == null ? null : Integer.valueOf(value);
	}

	private ConfigStatus mkCfgStatus(String value) {
		return value == null ? null : ConfigStatus.valueOf(value.toUpperCase());
	}

	private CommonConfigResult mkCCResult(String value) {
		return value == null ? null : CommonConfigResult.valueOf(value
				.toUpperCase());
	}

	public boolean isOK() {
		return ok;
	}

	public Boolean isDataPortAnalyzersOK() {
		return dpaOK;
	}

	public Boolean isDriverConfigsOK() {
		return driverConfigsOK;
	}

	public Boolean getBoardManagerInitStatus() {
		return boardManagerInitStatus;
	}

	public Integer getConfiguredBoardsNumber() {
		return configuredBoardsNumber;
	}

	public Integer getInitializedBoardsNumber() {
		return initializedBoardsNumber;
	}

	public ConfigStatus getLoadConfigurationStatus() {
		return loadConfigurationStatus;
	}

	public Boolean getSaveNewConfigurationStatus() {
		return saveNewConfigurationStatus;
	}

	public Boolean getConfigActivationStatus() {
		return configActivationStatus;
	}

	public Integer getFailedBoardBindingsNumber() {
		return failedBoardBindingsNumber;
	}

	public Integer getEnabledDataPortAnalyzersNumber() {
		return enabledDataPortAnalyzersNumber;
	}

	public Integer getInitializedDataPortDriversNumber() {
		return initializedDataPortDriversNumber;
	}

	public Integer getFailedDataPortThreadsNumber() {
		return failedDataPortThreadsNumber;
	}

	public int getTotalThreadFailures() {
		return totalThreadFailures;
	}

	public CommonConfigResult getCommonConfigFromCopStatus() {
		return commonConfigFromCopStatus;
	}

	public boolean isDataInTheFuture() {
		return dataInTheFuture;
	}

	public int getCurrentThreadFailures() {
		return currentThreadFailures;
	}

	public int getDataWriteErrorCount() {
		return dataWriteErrorCount;
	}

	@Override
	public String toString() {
		return "PerifericoStatus [ok=" + ok + ", boardManagerInitStatus="
				+ boardManagerInitStatus + ", configuredBoardsNumber="
				+ configuredBoardsNumber + ", initializedBoardsNumber="
				+ initializedBoardsNumber + ", failedBoardBindingsNumber="
				+ failedBoardBindingsNumber + ", dpaOK=" + dpaOK
				+ ", driverConfigsOK=" + driverConfigsOK
				+ ", enabledDataPortAnalyzersNumber="
				+ enabledDataPortAnalyzersNumber
				+ ", initializedDataPortDriversNumber="
				+ initializedDataPortDriversNumber
				+ ", failedDataPortThreadsNumber="
				+ failedDataPortThreadsNumber + ", loadConfigurationStatus="
				+ loadConfigurationStatus + ", saveNewConfigurationStatus="
				+ saveNewConfigurationStatus + ", configActivationStatus="
				+ configActivationStatus + ", commonConfigFromCopStatus="
				+ commonConfigFromCopStatus + ", dataInTheFuture="
				+ dataInTheFuture + ", totalThreadFailures="
				+ totalThreadFailures + ", currentThreadFailures="
				+ currentThreadFailures + ", dataWriteErrorCount="
				+ dataWriteErrorCount + "]";
	}

}
