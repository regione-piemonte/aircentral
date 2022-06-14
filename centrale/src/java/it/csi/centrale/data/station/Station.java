/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing information about a station
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: Station.java,v 1.15 2014/09/19 14:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.polling.Pollable;
import it.csi.centrale.polling.PollingStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Class for storing information about a station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class Station implements Pollable, Serializable {

	private static final long serialVersionUID = 7973189421455206023L;

	private int stationId = -1;
	private String stationUUID;
	private Vector<Analyzer> analyzersVect = new Vector<Analyzer>();
	private Vector<ContainerAlarm> alarms = new Vector<ContainerAlarm>();
	private StationStatus stStatus;
	private StationInfo stInfo;

	// Inizio variabili per nuovo polling
	private Failure connectionFailure = null;
	private int connFailureCount = 0;
	private long nextCustomPollingTime_ms = 0;
	private volatile PollingStatus pollingStatus = PollingStatus.NONE;
	private Date lastPollingDate = null;
	private Date lastSuccessfulPollingDate = null;

	// Fine variabili per nuovo polling

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getStationUUID() {
		return stationUUID;
	}

	public void setStationUUID(String stationUUID) {
		this.stationUUID = stationUUID;
	}

	public Vector<Analyzer> getAnalyzersVect() {
		return analyzersVect;
	}

	public void setAnalyzersVect(Vector<Analyzer> analyzersVect) {
		this.analyzersVect = analyzersVect;
	}

	public Vector<ContainerAlarm> getContainerAlarm() {
		return this.alarms;
	}

	public void setAlarmsVect(Vector<ContainerAlarm> vt) {
		this.alarms = vt;
	}

	public StationStatus getStStatus() {
		return stStatus;
	}

	public void setStStatus(StationStatus stStatus) {
		this.stStatus = stStatus;
	}

	public StationInfo getStInfo() {
		return stInfo;
	}

	public void setStInfo(StationInfo stInfo) {
		this.stInfo = stInfo;
	}

	// Inizio funzioni per nuovo polling

	@Override
	public String getName() {
		return stInfo == null ? null : stInfo.getShortStationName();
	}

	@Override
	public Integer getCustomPollingPeriod_m() {
		return stInfo == null ? null : stInfo.getForcePollingTime();
	}

	@Override
	public Integer getGroupId() {
		return stInfo == null ? null : stInfo.getCopId();
	}

	@Override
	public CommConfig getCommunicationConfig() {
		CommDeviceInfo cdi = stInfo == null ? null : stInfo.getCommDevice();
		return cdi == null ? null : cdi.getCommunicationConfig();
	}

	@Override
	public void setConnectionFailure(Failure failure) {
		connectionFailure = failure;
		if (failure == null)
			connFailureCount = 0;
		else
			connFailureCount++;
	}

	@Override
	public Failure getConnectionFailure() {
		return connectionFailure;
	}

	@Override
	public int getConsecutiveConnectionFailuresCount() {
		return connFailureCount;
	}

	@Override
	public long getNextCustomPollingTime_ms() {
		return nextCustomPollingTime_ms;
	}

	@Override
	public void setNextCustomPollingTime_ms(long time) {
		nextCustomPollingTime_ms = time;
	}

	@Override
	public void setPollingStatus(PollingStatus pollingStatus) {
		Date now = new Date();
		// if (pollingStatus != PollingStatus.NONE
		// && pollingStatus != PollingStatus.WAITING
		// && pollingStatus != PollingStatus.CONNECTING
		// && pollingStatus != PollingStatus.RUNNING)
		lastPollingDate = now;
		if (pollingStatus == PollingStatus.OK)
			lastSuccessfulPollingDate = now;
		this.pollingStatus = pollingStatus;
	}

	@Override
	public PollingStatus getPollingStatus() {
		return pollingStatus;
	}

	@Override
	public boolean isEnabled() {
		return stInfo == null ? false : stInfo.isEnabled()
				&& !stInfo.isDeleted();
	}

	@Override
	public Date getLastPollingDate() {
		return lastPollingDate;
	}

	@Override
	public Date getLastSuccessfulPollingDate() {
		return lastSuccessfulPollingDate;
	}

	// Fine funzioni per nuovo polling

}
