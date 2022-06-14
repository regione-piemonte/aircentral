/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing information about the status of a station
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: StationStatus.java,v 1.58 2015/10/27 16:34:54 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.AnalyzerStatus;
import it.csi.centrale.polling.data.CommonConfigResult;
import it.csi.centrale.polling.data.ContainerAlarmStatus;
import it.csi.centrale.polling.data.DigitalContainerAlarmStatus;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.polling.data.TimeSyncResult;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class for storing information about the status of a station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationStatus implements Serializable {
	private static final long serialVersionUID = -7074932171880571851L;

	public static final int CLKUNKNOWN = 0;
	public static final int CLKOK = 1;
	public static final int CLKNOTSYNCBUTOK = 2;
	public static final int CLKNOTOK = 3;
	private static final int CLKTOLERANCE_s = 60;

	private static final int COMMONCFGSENTOK = 0;
	private static final int COMMONCFGNOTSENTFORCFGERR = 1;
	private static final int COMMONCFGNOTSENTFORPERIFERICOERR = 2;
	private static final int COMMONCFGNOTSENTFORPERIFERICOCRASH = 3;

	public static final int INFORMATIC_STATUS_ALARM = 1;
	public static final int INFORMATIC_STATUS_WARNING = 2;
	public static final int INFORMATIC_STATUS_OK = 3;

	private int sync; // means if we success clock synchronization. 0 start
						// value,1 all ok,2 in tolerance but without sync, 3 out
						// of sync and tolerance
	private Boolean instrumentInAlarm; // it's true if some instrument alarm is
										// on. Is null if there aren't analyzer
										// status configured
	private Boolean doorOpen; // it's true if station door is open
	private Integer temperature; // it contains temperature inside station
	private Boolean stationAlarm; // it's true if some station alarm is on. Is
									// null if there aren't station alarm
									// configured
	private Boolean gpsStatus; // means gps status, is null if it isn't
								// installed, true if there aren't gpsData on
								// last polling, false otherwise
	private String problemCommonConfigString; // keep string value sent from
												// periferico in common config
												// error case
	private int cmmCfgLastUpdateStatus; // mean how last common config
										// configuration went at specified
										// update date
	private PerifericoAppStatus appStatus;
	private DiskStatus diskStatus;
	private Date updateDate; // Update date of the DB record
	private Date timestamp; // Updated during polling

	// Nota: l'informaticStatus di questa classe viene calcolato tenendo conto
	// dello stato informatico del periferico + lo stato dischi (gps non usato)

	public StationStatus() {
		sync = CLKUNKNOWN;
		doorOpen = null;
		temperature = null;
		stationAlarm = null;
		instrumentInAlarm = null;
		gpsStatus = null;
		appStatus = null;
		diskStatus = null;
		cmmCfgLastUpdateStatus = 0;
		problemCommonConfigString = null;
		updateDate = null;
		timestamp = null;
	}

	public Integer getInformaticStatus() {
		Integer informaticStatus = null;
		if (diskStatus != null) {
			switch (diskStatus.getGlobalStatus()) {
			case OK:
				informaticStatus = INFORMATIC_STATUS_OK;
				break;
			case WARNING:
				informaticStatus = INFORMATIC_STATUS_WARNING;
				break;
			case ERROR:
				informaticStatus = INFORMATIC_STATUS_ALARM;
				break;
			case UNAVAILABLE:
				break;
			}
		}
		if (appStatus != null) {
			if (appStatus.isOK() && informaticStatus == null)
				informaticStatus = INFORMATIC_STATUS_OK;
			if (!appStatus.isOK())
				informaticStatus = INFORMATIC_STATUS_ALARM;
			if (informaticStatus == INFORMATIC_STATUS_OK)
				if (Boolean.FALSE.equals(appStatus.isDriverConfigsOK()))
					informaticStatus = INFORMATIC_STATUS_WARNING;
		}
		return informaticStatus;
	}

	public int getSync() {
		return sync;
	}

	public void setSync(TimeSyncResult result) {
		if (result == null) {
			sync = CLKUNKNOWN;
		} else {
			long clkDiff_s = Math.abs(System.currentTimeMillis()
					- result.getRemoteDate().getTime()) / 1000;
			if (result.isRemoteTimeAlarm() || clkDiff_s > CLKTOLERANCE_s)
				sync = CLKNOTOK;
			else
				sync = result.getSyncResult() ? CLKOK : CLKNOTSYNCBUTOK;
		}
	}

	public Boolean isInstrumentInAlarm() {
		return instrumentInAlarm;
	}

	public void setInstrumentInAlarm(List<AnalyzerStatus> listAnalyzerStatus) {
		if (listAnalyzerStatus == null)
			instrumentInAlarm = null;
		boolean value = false;
		for (AnalyzerStatus as : listAnalyzerStatus)
			value = value || Boolean.TRUE.equals(as.getFaultStatus());
		instrumentInAlarm = value;
	}

	public Boolean isDoorOpen() {
		return doorOpen;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public void setTemperature(Double value) {
		temperature = value == null ? null : (int) Math.round(value);
	}

	public Boolean isStationAlarm() {
		return stationAlarm;
	}

	public void setStationAlarm(List<ContainerAlarmStatus> listAlarms,
			UUID doorAlarmId) {
		if (listAlarms == null) {
			stationAlarm = null;
			doorOpen = null;
			return;
		}
		boolean value = false;
		for (ContainerAlarmStatus ca : listAlarms) {
			if (ca.getId().equals(doorAlarmId)
					&& ca instanceof DigitalContainerAlarmStatus) {
				doorOpen = ((DigitalContainerAlarmStatus) ca).getStatus();
			} else {
				value = value || ca.isActive();
			}
		}
		stationAlarm = value;
	}

	public PerifericoAppStatus getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(PerifericoAppStatus appStatus) {
		this.appStatus = appStatus;
	}

	public DiskStatus getDiskStatus() {
		return diskStatus;
	}

	public void setDiskStatus(DiskStatus diskStatus) {
		this.diskStatus = diskStatus;
	}

	public int getCmmCfgLastUpdateStatus() {
		return cmmCfgLastUpdateStatus;
	}

	public String getProblemCommonConfigString() {
		return problemCommonConfigString;
	}

	public void setCommonConfigResult(CommonConfigResult result)
			throws PollingException {
		problemCommonConfigString = result.toString();
		switch (result) {
		case OK:
			cmmCfgLastUpdateStatus = COMMONCFGSENTOK;
			break;
		case CONSISTENCY_ERROR:
		case UNPARSABLE:
			cmmCfgLastUpdateStatus = COMMONCFGNOTSENTFORCFGERR;
			break;
		case SAVE_ERROR:
		case LOAD_ERROR:
			cmmCfgLastUpdateStatus = COMMONCFGNOTSENTFORPERIFERICOERR;
			break;
		case INCOMPATIBLE:
		case CONFIG_START_ERROR:
		case CONFIG_LOAD_ERROR:
			cmmCfgLastUpdateStatus = COMMONCFGNOTSENTFORPERIFERICOCRASH;
			break;
		default:
			throw new PollingException("CommonConfigResult value " + result
					+ " not managed");
		}
	}

	public Boolean getGpsStatus() {
		return gpsStatus;
	}

	public void setGpsStatus(Boolean gpsStatus) {
		this.gpsStatus = gpsStatus;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void onInfoUpdated() {
		timestamp = new Date();
	}

}
