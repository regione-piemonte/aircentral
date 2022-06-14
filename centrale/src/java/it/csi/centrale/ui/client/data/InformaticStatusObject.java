/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with information about informatic status of a station
* Change log:
*   2009-02-24: initial version
* ----------------------------------------------------------------------------
* $Id: InformaticStatusObject.java,v 1.12 2015/10/19 13:22:22 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Object with information about informatic status of a station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class InformaticStatusObject implements Serializable {

	private static final long serialVersionUID = -2118950565433833646L;

	private int stationId;
	private boolean isOK;
	private Boolean boardManInitStatus;
	private Integer cfgBoardsNumber;
	private Integer initBoardsNumber;
	private Integer failedBoardBindingsNumber;
	private Boolean dpaOk;
	private Boolean driverConfigsOk;
	private Integer enabledDPANumber;
	private Integer initDPADriversNumber;
	private Integer failedDPAThreadsNumber;
	private String loadCfgStatus;
	private Boolean saveCfgStatus;
	private Boolean cfgActivationStatus;
	private Boolean dateInFuture;
	private String commonCfgFromCopStatus;
	private int totalThreadFailures;
	private int currentThreadFailures;
	private int dataWriteErrorCount;
	private HashMap<String, Integer> filesystem;
	private String smartStatus;
	private String raidStatus;
	private int warningLevel;
	private int alarmLevel;
	private boolean useGps;
	private Date gpsTimestamp;
	private Double gpsLatitude;
	private Double gpsLongitude;
	private Double gpsAltitude;
	private String gpsFix;
	private String linkToGoogleMaps;
	private int communicationStatus;
	private Date lastCorrectPollingTime = null;
	private String commonConfigProblem;
	private Integer commonConfigLastUpdateStatus;

	public InformaticStatusObject() {
		setStationId(-1);
	}

	/**
	 * @return the stationId
	 */
	public int getStationId() {
		return stationId;
	}

	/**
	 * @param stationId the stationId to set
	 */
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	/**
	 * @return the isOK
	 */
	public boolean isOK() {
		return isOK;
	}

	/**
	 * @param isOK the isOK to set
	 */
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	/**
	 * @return the boardManInitStatus
	 */
	public Boolean getBoardManInitStatus() {
		return boardManInitStatus;
	}

	/**
	 * @param boardManInitStatus the boardManInitStatus to set
	 */
	public void setBoardManInitStatus(Boolean boardManInitStatus) {
		this.boardManInitStatus = boardManInitStatus;
	}

	/**
	 * @return the cfgBoardsNumber
	 */
	public Integer getCfgBoardsNumber() {
		return cfgBoardsNumber;
	}

	/**
	 * @param cfgBoardsNumber the cfgBoardsNumber to set
	 */
	public void setCfgBoardsNumber(Integer cfgBoardsNumber) {
		this.cfgBoardsNumber = cfgBoardsNumber;
	}

	/**
	 * @return the initBoardsNumber
	 */
	public Integer getInitBoardsNumber() {
		return initBoardsNumber;
	}

	/**
	 * @param initBoardsNumber the initBoardsNumber to set
	 */
	public void setInitBoardsNumber(Integer initBoardsNumber) {
		this.initBoardsNumber = initBoardsNumber;
	}

	/**
	 * @return the failedBoardBindingsNumber
	 */
	public Integer getFailedBoardBindingsNumber() {
		return failedBoardBindingsNumber;
	}

	/**
	 * @param failedBoardBindingsNumber the failedBoardBindingsNumber to set
	 */
	public void setFailedBoardBindingsNumber(Integer failedBoardBindingsNumber) {
		this.failedBoardBindingsNumber = failedBoardBindingsNumber;
	}

	/**
	 * @return the dpaOk
	 */
	public Boolean getDpaOk() {
		return dpaOk;
	}

	/**
	 * @param dpaOk the dpaOk to set
	 */
	public void setDpaOk(Boolean dpaOk) {
		this.dpaOk = dpaOk;
	}

	/**
	 * @return the driverConfigsOk
	 */
	public Boolean getDriverConfigsOk() {
		return driverConfigsOk;
	}

	/**
	 * @param driverConfigsOk the driverConfigsOk to set
	 */
	public void setDriverConfigsOk(Boolean driverConfigsOk) {
		this.driverConfigsOk = driverConfigsOk;
	}

	/**
	 * @return the enabledDPANumber
	 */
	public Integer getEnabledDPANumber() {
		return enabledDPANumber;
	}

	/**
	 * @param enabledDPANumber the enabledDPANumber to set
	 */
	public void setEnabledDPANumber(Integer enabledDPANumber) {
		this.enabledDPANumber = enabledDPANumber;
	}

	/**
	 * @return the initDPADriversNumber
	 */
	public Integer getInitDPADriversNumber() {
		return initDPADriversNumber;
	}

	/**
	 * @param initDPADriversNumber the initDPADriversNumber to set
	 */
	public void setInitDPADriversNumber(Integer initDPADriversNumber) {
		this.initDPADriversNumber = initDPADriversNumber;
	}

	/**
	 * @return the failedDPAThreadsNumber
	 */
	public Integer getFailedDPAThreadsNumber() {
		return failedDPAThreadsNumber;
	}

	/**
	 * @param failedDPAThreadsNumber the failedDPAThreadsNumber to set
	 */
	public void setFailedDPAThreadsNumber(Integer failedDPAThreadsNumber) {
		this.failedDPAThreadsNumber = failedDPAThreadsNumber;
	}

	/**
	 * @return the loadCfgStatus
	 */
	public String getLoadCfgStatus() {
		return loadCfgStatus;
	}

	/**
	 * @param loadCfgStatus the loadCfgStatus to set
	 */
	public void setLoadCfgStatus(String loadCfgStatus) {
		this.loadCfgStatus = loadCfgStatus;
	}

	/**
	 * @return the saveCfgStatus
	 */
	public Boolean getSaveCfgStatus() {
		return saveCfgStatus;
	}

	/**
	 * @param saveCfgStatus the saveCfgStatus to set
	 */
	public void setSaveCfgStatus(Boolean saveCfgStatus) {
		this.saveCfgStatus = saveCfgStatus;
	}

	/**
	 * @return the cfgActivationStatus
	 */
	public Boolean getCfgActivationStatus() {
		return cfgActivationStatus;
	}

	/**
	 * @param cfgActivationStatus the cfgActivationStatus to set
	 */
	public void setCfgActivationStatus(Boolean cfgActivationStatus) {
		this.cfgActivationStatus = cfgActivationStatus;
	}

	/**
	 * @return the dateInFuture
	 */
	public Boolean getDateInFuture() {
		return dateInFuture;
	}

	/**
	 * @param dateInFuture the dateInFuture to set
	 */
	public void setDateInFuture(Boolean dateInFuture) {
		this.dateInFuture = dateInFuture;
	}

	/**
	 * @return the commonCfgFromCopStatus
	 */
	public String getCommonCfgFromCopStatus() {
		return commonCfgFromCopStatus;
	}

	/**
	 * @param commonCfgFromCopStatus the commonCfgFromCopStatus to set
	 */
	public void setCommonCfgFromCopStatus(String commonCfgFromCopStatus) {
		this.commonCfgFromCopStatus = commonCfgFromCopStatus;
	}

	/**
	 * @return the totalThreadFailures
	 */
	public int getTotalThreadFailures() {
		return totalThreadFailures;
	}

	/**
	 * @param totalThreadFailures the totalThreadFailures to set
	 */
	public void setTotalThreadFailures(int totalThreadFailures) {
		this.totalThreadFailures = totalThreadFailures;
	}

	/**
	 * @return the currentThreadFailures
	 */
	public int getCurrentThreadFailures() {
		return currentThreadFailures;
	}

	/**
	 * @param currentThreadFailures the currentThreadFailures to set
	 */
	public void setCurrentThreadFailures(int currentThreadFailures) {
		this.currentThreadFailures = currentThreadFailures;
	}

	/**
	 * @return the dataWriteErrorCount
	 */
	public int getDataWriteErrorCount() {
		return dataWriteErrorCount;
	}

	/**
	 * @param dataWriteErrorCount the dataWriteErrorCount to set
	 */
	public void setDataWriteErrorCount(int dataWriteErrorCount) {
		this.dataWriteErrorCount = dataWriteErrorCount;
	}

	/**
	 * @return the filesystem
	 */
	public HashMap<String, Integer> getFilesystem() {
		return filesystem;
	}

	/**
	 * @param filesystem the filesystem to set
	 */
	public void setFilesystem(HashMap<String, Integer> filesystem) {
		this.filesystem = filesystem;
	}

	/**
	 * @return the smartStatus
	 */
	public String getSmartStatus() {
		return smartStatus;
	}

	/**
	 * @param smartStatus the smartStatus to set
	 */
	public void setSmartStatus(String smartStatus) {
		this.smartStatus = smartStatus;
	}

	/**
	 * @return the raidStatus
	 */
	public String getRaidStatus() {
		return raidStatus;
	}

	/**
	 * @param raidStatus the raidStatus to set
	 */
	public void setRaidStatus(String raidStatus) {
		this.raidStatus = raidStatus;
	}

	/**
	 * @return the warningLevel
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	/**
	 * @param warningLevel the warningLevel to set
	 */
	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	/**
	 * @return the alarmLevel
	 */
	public int getAlarmLevel() {
		return alarmLevel;
	}

	/**
	 * @param alarmLevel the alarmLevel to set
	 */
	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	/**
	 * @return the useGps
	 */
	public boolean isUseGps() {
		return useGps;
	}

	/**
	 * @param useGps the useGps to set
	 */
	public void setUseGps(boolean useGps) {
		this.useGps = useGps;
	}

	/**
	 * @return the gpsTimestamp
	 */
	public Date getGpsTimestamp() {
		return gpsTimestamp;
	}

	/**
	 * @param gpsTimestamp the gpsTimestamp to set
	 */
	public void setGpsTimestamp(Date gpsTimestamp) {
		this.gpsTimestamp = gpsTimestamp;
	}

	/**
	 * @return the gpsLatitude
	 */
	public Double getGpsLatitude() {
		return gpsLatitude;
	}

	/**
	 * @param gpsLatitude the gpsLatitude to set
	 */
	public void setGpsLatitude(Double gpsLatitude) {
		this.gpsLatitude = gpsLatitude;
	}

	/**
	 * @return the gpsLongitude
	 */
	public Double getGpsLongitude() {
		return gpsLongitude;
	}

	/**
	 * @param gpsLongitude the gpsLongitude to set
	 */
	public void setGpsLongitude(Double gpsLongitude) {
		this.gpsLongitude = gpsLongitude;
	}

	/**
	 * @return the gpsAltitude
	 */
	public Double getGpsAltitude() {
		return gpsAltitude;
	}

	/**
	 * @param gpsAltitude the gpsAltitude to set
	 */
	public void setGpsAltitude(Double gpsAltitude) {
		this.gpsAltitude = gpsAltitude;
	}

	/**
	 * @return the gpsFix
	 */
	public String getGpsFix() {
		return gpsFix;
	}

	/**
	 * @param gpsFix the gpsFix to set
	 */
	public void setGpsFix(String gpsFix) {
		this.gpsFix = gpsFix;
	}

	/**
	 * @return the linkToGoogleMaps
	 */
	public String getLinkToGoogleMaps() {
		return linkToGoogleMaps;
	}

	/**
	 * @param linkToGoogleMaps the linkToGoogleMaps to set
	 */
	public void setLinkToGoogleMaps(String linkToGoogleMaps) {
		this.linkToGoogleMaps = linkToGoogleMaps;
	}

	/**
	 * @return the communicationStatus
	 */
	public int getCommunicationStatus() {
		return communicationStatus;
	}

	/**
	 * @param communicationStatus the communicationStatus to set
	 */
	public void setCommunicationStatus(int communicationStatus) {
		this.communicationStatus = communicationStatus;
	}

	/**
	 * @return the lastCorrectPollingTime
	 */
	public Date getLastCorrectPollingTime() {
		return lastCorrectPollingTime;
	}

	/**
	 * @param lastCorrectPollingTime the lastCorrectPollingTime to set
	 */
	public void setLastCorrectPollingTime(Date lastCorrectPollingTime) {
		this.lastCorrectPollingTime = lastCorrectPollingTime;
	}

	/**
	 * @return the commonConfigProblem
	 */
	public String getCommonConfigProblem() {
		return commonConfigProblem;
	}

	/**
	 * @param commonConfigProblem the commonConfigProblem to set
	 */
	public void setCommonConfigProblem(String commonConfigProblem) {
		this.commonConfigProblem = commonConfigProblem;
	}

	/**
	 * @return the commonConfigLastUpdateStatus
	 */
	public Integer getCommonConfigLastUpdateStatus() {
		return commonConfigLastUpdateStatus;
	}

	/**
	 * @param commonConfigLastUpdateStatus the commonConfigLastUpdateStatus to set
	 */
	public void setCommonConfigLastUpdateStatus(Integer commonConfigLastUpdateStatus) {
		this.commonConfigLastUpdateStatus = commonConfigLastUpdateStatus;
	}

}
