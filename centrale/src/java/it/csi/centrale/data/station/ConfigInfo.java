/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing information about a Cop
 * Change log:
 *   2008-09-24: initial version
 * ----------------------------------------------------------------------------
 * $Id: ConfigInfo.java,v 1.7 2014/09/22 11:01:42 vespa Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Class for storing information about a Cop
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ConfigInfo implements Serializable {
	private static final long serialVersionUID = -5845916610648913715L;
	public static final int CALIBRATION_SAMPLE_DOWNLOAD = 1;
	public static final int ALL_SAMPLE_DOWNLOAD = 2;

	private int configId;

	private Vector<Station> stVector = new Vector<Station>();

	private AdvancedPollingConf pollConfig;

	private int sampleDataTypeToDownload; // 0 disable 1 only if calibration 2

	// all data

	private int maxNumLines;

	private int totalNumModem;

	private int numReservedLinesUi;

	private int numModemSharedLines;

	private boolean reservedLine; //

	private Integer minThreshold;

	private Integer maxThreshold;

	private Integer alarmMaxThreshold; // for temperature color

	private boolean downloadAlarmHistory; // 0 disable 1 if we should download

	// alarm history from Periferico

	private boolean syntheticIcon;

	private String copIp;

	private int routerTimeout; // timeout after disconnect from station before

	// router line goes down

	private int routerTryTimeout; // timeout for router to connect to a station

	private String copRouter; // cop router ip address

	private String timeHostRouter;

	private String timeHostModem;

	private String timeHostLan;

	private String timeHostProxy;

	// when line is free

	private Date updateDate;

	private Date commonConfigUpdateDate;

	private Vector<ModemConf> vtModem;

	private Vector<String> analyzerStatusType;

	private String genericMapName;

	private Vector<VirtualCop> virtualCopVector;

	private String proxyHost;

	private Integer proxyPort;

	private String proxyExlusion;

	public Vector<VirtualCop> getVirtualCopVector() {
		return virtualCopVector;
	}

	public void setVirtualCopVector(Vector<VirtualCop> virtualCopVector) {
		if (this.virtualCopVector == null || this.virtualCopVector.size() == 0) {
			this.virtualCopVector = virtualCopVector;
		} else {
			this.virtualCopVector.addAll(virtualCopVector);
		}
	}

	public String getGenericMapName() {
		return genericMapName;
	}

	public void setGenericMapName(String genericMapName) {
		this.genericMapName = genericMapName;
	}

	public int getNumReservedLinesUi() {
		return numReservedLinesUi;
	}

	public void setNumReservedLinesUi(int num_reserved_lines_ui) {
		this.numReservedLinesUi = num_reserved_lines_ui;
	}

	private String name; // cop name

	public ConfigInfo() {
		stVector = null;
		virtualCopVector = null;
		pollConfig = null;
		sampleDataTypeToDownload = 0;
		configId = -1;
		routerTimeout = 0;
		routerTryTimeout = 0;
		copRouter = null;
		timeHostLan = null;
		timeHostModem = null;
		timeHostProxy = null;
		timeHostRouter = null;
		proxyHost = null;
		proxyPort = null;
		proxyExlusion = null;

	}

	public String getTimeHostRouter() {
		return timeHostRouter;
	}

	public void setTimeHostRouter(String timeHostRouter) {
		this.timeHostRouter = timeHostRouter;
	}

	public String getTimeHostModem() {
		return timeHostModem;
	}

	public void setTimeHostModem(String timeHostModem) {
		this.timeHostModem = timeHostModem;
	}

	public String getTimeHostLan() {
		return timeHostLan;
	}

	public void setTimeHostLan(String timeHostLan) {
		this.timeHostLan = timeHostLan;
	}

	public String getTimeHostProxy() {
		return timeHostProxy;
	}

	public void setTimeHostProxy(String timeHostProxy) {
		this.timeHostProxy = timeHostProxy;
	}

	public Vector<Station> getStVector() {
		return stVector;
	}

	public void setStVector(Vector<Station> stVector) {
		if (this.stVector == null || this.stVector.size() == 0) {
			this.stVector = stVector;
		} else {
			this.stVector.addAll(stVector);
		}

	}

	public AdvancedPollingConf getPollConfig() {
		return pollConfig;
	}

	public void setPollConfig(AdvancedPollingConf pollConfig) {
		this.pollConfig = pollConfig;
	}

	public int getSampleDataTypeToDownload() {
		return sampleDataTypeToDownload;
	}

	public void setSampleDataTypeToDownload(int sampleDataTypeToDownload) {
		this.sampleDataTypeToDownload = sampleDataTypeToDownload;
	}

	public int getMaxNumLines() {
		return maxNumLines;
	}

	public void setMaxNumLines(int maxNumLines) {
		this.maxNumLines = maxNumLines;
	}

	public int getTotalNumModem() {
		return totalNumModem;
	}

	public void setTotalNumModem(int totalNumModem) {
		this.totalNumModem = totalNumModem;
	}

	public int getNumModemSharedLines() {
		return numModemSharedLines;
	}

	public void setNumModemSharedLines(int numModemSharedLines) {
		this.numModemSharedLines = numModemSharedLines;
	}

	public boolean isReservedLine() {
		return reservedLine;
	}

	public void setReservedLine(boolean reservedLine) {
		this.reservedLine = reservedLine;
	}

	public Integer getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(Integer minThreshold) {
		this.minThreshold = minThreshold;
	}

	public Integer getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(Integer maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	public Integer getAlarmMaxThreshold() {
		return alarmMaxThreshold;
	}

	public void setAlarmMaxThreshold(Integer alarmMaxThreshold) {
		this.alarmMaxThreshold = alarmMaxThreshold;
	}

	public int getConfigId() {
		return configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public boolean isSyntheticIcon() {
		return syntheticIcon;
	}

	public void setSyntheticIcon(boolean syntheticIcon) {
		this.syntheticIcon = syntheticIcon;
	}

	public Vector<ModemConf> getVtModem() {
		return vtModem;
	}

	public void setVtModem(Vector<ModemConf> vtModem) {
		this.vtModem = vtModem;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Vector<String> getAnalyzerStatusType() {
		return analyzerStatusType;
	}

	public void setAnalyzerStatusType(Vector<String> analyzerStatusType) {
		this.analyzerStatusType = analyzerStatusType;
	}

	public boolean isDownloadAlarmHistory() {
		return downloadAlarmHistory;
	}

	public void setDownloadAlarmHistory(boolean downloadAlarmHistory) {
		this.downloadAlarmHistory = downloadAlarmHistory;
	}

	public String getCopIp() {
		return copIp;
	}

	public void setCopIp(String copIp) {
		this.copIp = copIp;
	}

	public int getRouterTimeout() {
		return routerTimeout;
	}

	public void setRouterTimeout(int routerTimeout) {
		this.routerTimeout = routerTimeout;
	}

	public int getRouterTryTimeout() {
		return routerTryTimeout;
	}

	public void setRouterTryTimeout(int routerTryTimeout) {
		this.routerTryTimeout = routerTryTimeout;
	}

	public Date getCommonConfigUpdateDate() {
		return commonConfigUpdateDate;
	}

	public void setCommonConfigUpdateDate(Date commonConfigUpdateDate) {
		this.commonConfigUpdateDate = commonConfigUpdateDate;
	}

	public String getCopRouter() {
		return copRouter;
	}

	public void setCopRouter(String copRouter) {
		this.copRouter = copRouter;
	}

	public String getName() {
		return name;
	}

	public void setName(String copName) {
		this.name = copName;

	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyExlusion() {
		return proxyExlusion;
	}

	public void setProxyExlusion(String proxyExlusion) {
		this.proxyExlusion = proxyExlusion;
	}

}
