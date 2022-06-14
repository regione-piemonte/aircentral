/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: stores informations about the cop to be configured
* Change log:
*   2008-11-27: initial version
* ----------------------------------------------------------------------------
* $Id: ConfigInfoObject.java,v 1.4 2014/09/22 11:01:43 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Stores informations about the cop to be configured
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ConfigInfoObject implements Serializable {

	private static final long serialVersionUID = 1485391833224294550L;

	private int configId;
	private int pollingOfficeTime;
	private Integer pollingExtraOffice; // cadency in minute
	private boolean usePollingExtra;
	private boolean closeSat;
	private boolean closeSun;
	private Integer openAt;
	private Integer closeAt;
	private int sampleDataTypeToDownload; // 0 disable 1 only if calibration 2
	// all data
	private boolean downloadAlarm;
	private int maxNumLines;
	private int totalNumModem;
	private int numModemSharedLines;
	private int numReservedSharedLinesUi;
	private int routerTimeout;
	private int routerTryTimeout;
	private String copIp;
	private String copRouterIp;
	private boolean reservedLine;
	private Integer minThreshold;
	private Integer maxThreshold;
	private Integer alarmMaxThreshold;
	private boolean syntheticIcon;
	private String name;
	private String genericMapName;
	private String timeHostProxy;
	private String timeHostLan;
	private String timeHostRouter;
	private String timeHostModem;
	private String proxyHost;
	private Integer proxyPort;
	private String proxyExclusion;

	public ConfigInfoObject() {
	}

	/**
	 * @return the configId
	 */
	public int getConfigId() {
		return configId;
	}

	/**
	 * @param configId the configId to set
	 */
	public void setConfigId(int configId) {
		this.configId = configId;
	}

	/**
	 * @return the pollingOfficeTime
	 */
	public int getPollingOfficeTime() {
		return pollingOfficeTime;
	}

	/**
	 * @param pollingOfficeTime the pollingOfficeTime to set
	 */
	public void setPollingOfficeTime(int pollingOfficeTime) {
		this.pollingOfficeTime = pollingOfficeTime;
	}

	/**
	 * @return the pollingExtraOffice
	 */
	public Integer getPollingExtraOffice() {
		return pollingExtraOffice;
	}

	/**
	 * @param pollingExtraOffice the pollingExtraOffice to set
	 */
	public void setPollingExtraOffice(Integer pollingExtraOffice) {
		this.pollingExtraOffice = pollingExtraOffice;
	}

	/**
	 * @return the usePollingExtra
	 */
	public boolean isUsePollingExtra() {
		return usePollingExtra;
	}

	/**
	 * @param usePollingExtra the usePollingExtra to set
	 */
	public void setUsePollingExtra(boolean usePollingExtra) {
		this.usePollingExtra = usePollingExtra;
	}

	/**
	 * @return the closeSat
	 */
	public boolean isCloseSat() {
		return closeSat;
	}

	/**
	 * @param closeSat the closeSat to set
	 */
	public void setCloseSat(boolean closeSat) {
		this.closeSat = closeSat;
	}

	/**
	 * @return the closeSun
	 */
	public boolean isCloseSun() {
		return closeSun;
	}

	/**
	 * @param closeSun the closeSun to set
	 */
	public void setCloseSun(boolean closeSun) {
		this.closeSun = closeSun;
	}

	/**
	 * @return the openAt
	 */
	public Integer getOpenAt() {
		return openAt;
	}

	/**
	 * @param openAt the openAt to set
	 */
	public void setOpenAt(Integer openAt) {
		this.openAt = openAt;
	}

	/**
	 * @return the closeAt
	 */
	public Integer getCloseAt() {
		return closeAt;
	}

	/**
	 * @param closeAt the closeAt to set
	 */
	public void setCloseAt(Integer closeAt) {
		this.closeAt = closeAt;
	}

	/**
	 * @return the sampleDataTypeToDownload
	 */
	public int getSampleDataTypeToDownload() {
		return sampleDataTypeToDownload;
	}

	/**
	 * @param sampleDataTypeToDownload the sampleDataTypeToDownload to set
	 */
	public void setSampleDataTypeToDownload(int sampleDataTypeToDownload) {
		this.sampleDataTypeToDownload = sampleDataTypeToDownload;
	}

	/**
	 * @return the downloadAlarm
	 */
	public boolean isDownloadAlarm() {
		return downloadAlarm;
	}

	/**
	 * @param downloadAlarm the downloadAlarm to set
	 */
	public void setDownloadAlarm(boolean downloadAlarm) {
		this.downloadAlarm = downloadAlarm;
	}

	/**
	 * @return the maxNumLines
	 */
	public int getMaxNumLines() {
		return maxNumLines;
	}

	/**
	 * @param maxNumLines the maxNumLines to set
	 */
	public void setMaxNumLines(int maxNumLines) {
		this.maxNumLines = maxNumLines;
	}

	/**
	 * @return the totalNumModem
	 */
	public int getTotalNumModem() {
		return totalNumModem;
	}

	/**
	 * @param totalNumModem the totalNumModem to set
	 */
	public void setTotalNumModem(int totalNumModem) {
		this.totalNumModem = totalNumModem;
	}

	/**
	 * @return the numModemSharedLines
	 */
	public int getNumModemSharedLines() {
		return numModemSharedLines;
	}

	/**
	 * @param numModemSharedLines the numModemSharedLines to set
	 */
	public void setNumModemSharedLines(int numModemSharedLines) {
		this.numModemSharedLines = numModemSharedLines;
	}

	/**
	 * @return the numReservedSharedLinesUi
	 */
	public int getNumReservedSharedLinesUi() {
		return numReservedSharedLinesUi;
	}

	/**
	 * @param numReservedSharedLinesUi the numReservedSharedLinesUi to set
	 */
	public void setNumReservedSharedLinesUi(int numReservedSharedLinesUi) {
		this.numReservedSharedLinesUi = numReservedSharedLinesUi;
	}

	/**
	 * @return the routerTimeout
	 */
	public int getRouterTimeout() {
		return routerTimeout;
	}

	/**
	 * @param routerTimeout the routerTimeout to set
	 */
	public void setRouterTimeout(int routerTimeout) {
		this.routerTimeout = routerTimeout;
	}

	/**
	 * @return the routerTryTimeout
	 */
	public int getRouterTryTimeout() {
		return routerTryTimeout;
	}

	/**
	 * @param routerTryTimeout the routerTryTimeout to set
	 */
	public void setRouterTryTimeout(int routerTryTimeout) {
		this.routerTryTimeout = routerTryTimeout;
	}

	/**
	 * @return the copIp
	 */
	public String getCopIp() {
		return copIp;
	}

	/**
	 * @param copIp the copIp to set
	 */
	public void setCopIp(String copIp) {
		this.copIp = copIp;
	}

	/**
	 * @return the copRouterIp
	 */
	public String getCopRouterIp() {
		return copRouterIp;
	}

	/**
	 * @param copRouterIp the copRouterIp to set
	 */
	public void setCopRouterIp(String copRouterIp) {
		this.copRouterIp = copRouterIp;
	}

	/**
	 * @return the reservedLine
	 */
	public boolean isReservedLine() {
		return reservedLine;
	}

	/**
	 * @param reservedLine the reservedLine to set
	 */
	public void setReservedLine(boolean reservedLine) {
		this.reservedLine = reservedLine;
	}

	/**
	 * @return the minThreshold
	 */
	public Integer getMinThreshold() {
		return minThreshold;
	}

	/**
	 * @param minThreshold the minThreshold to set
	 */
	public void setMinThreshold(Integer minThreshold) {
		this.minThreshold = minThreshold;
	}

	/**
	 * @return the maxThreshold
	 */
	public Integer getMaxThreshold() {
		return maxThreshold;
	}

	/**
	 * @param maxThreshold the maxThreshold to set
	 */
	public void setMaxThreshold(Integer maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	/**
	 * @return the alarmMaxThreshold
	 */
	public Integer getAlarmMaxThreshold() {
		return alarmMaxThreshold;
	}

	/**
	 * @param alarmMaxThreshold the alarmMaxThreshold to set
	 */
	public void setAlarmMaxThreshold(Integer alarmMaxThreshold) {
		this.alarmMaxThreshold = alarmMaxThreshold;
	}

	/**
	 * @return the syntheticIcon
	 */
	public boolean isSyntheticIcon() {
		return syntheticIcon;
	}

	/**
	 * @param syntheticIcon the syntheticIcon to set
	 */
	public void setSyntheticIcon(boolean syntheticIcon) {
		this.syntheticIcon = syntheticIcon;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the genericMapName
	 */
	public String getGenericMapName() {
		return genericMapName;
	}

	/**
	 * @param genericMapName the genericMapName to set
	 */
	public void setGenericMapName(String genericMapName) {
		this.genericMapName = genericMapName;
	}

	/**
	 * @return the timeHostProxy
	 */
	public String getTimeHostProxy() {
		return timeHostProxy;
	}

	/**
	 * @param timeHostProxy the timeHostProxy to set
	 */
	public void setTimeHostProxy(String timeHostProxy) {
		this.timeHostProxy = timeHostProxy;
	}

	/**
	 * @return the timeHostLan
	 */
	public String getTimeHostLan() {
		return timeHostLan;
	}

	/**
	 * @param timeHostLan the timeHostLan to set
	 */
	public void setTimeHostLan(String timeHostLan) {
		this.timeHostLan = timeHostLan;
	}

	/**
	 * @return the timeHostRouter
	 */
	public String getTimeHostRouter() {
		return timeHostRouter;
	}

	/**
	 * @param timeHostRouter the timeHostRouter to set
	 */
	public void setTimeHostRouter(String timeHostRouter) {
		this.timeHostRouter = timeHostRouter;
	}

	/**
	 * @return the timeHostModem
	 */
	public String getTimeHostModem() {
		return timeHostModem;
	}

	/**
	 * @param timeHostModem the timeHostModem to set
	 */
	public void setTimeHostModem(String timeHostModem) {
		this.timeHostModem = timeHostModem;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public Integer getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the proxyExclusion
	 */
	public String getProxyExclusion() {
		return proxyExclusion;
	}

	/**
	 * @param proxyExclusion the proxyExclusion to set
	 */
	public void setProxyExclusion(String proxyExclusion) {
		this.proxyExclusion = proxyExclusion;
	}

}
