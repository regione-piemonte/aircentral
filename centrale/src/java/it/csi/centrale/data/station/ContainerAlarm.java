/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about an alarm of a station
 * Change log:
 *   2008-12-05: initial version
 * ----------------------------------------------------------------------------
 * $Id: ContainerAlarm.java,v 1.21 2014/09/22 07:18:40 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.ContainerAlarmCfg;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about an alarm of a station
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class ContainerAlarm extends InsertableObject implements Serializable {

	// TODO: introdurre enum per tipo DIGITAL, TRIGGER

	private static final long serialVersionUID = 7072194795781499664L;

	private String alarmUUID;

	private String alarmID;

	private int alarmDbID;

	private String notes;

	private String type;

	private Date updateDate;

	private Date deletionDate;

	private String alarmName;

	private AlarmData lastStatus; // last polling alarm status

	public ContainerAlarm() {
		deletionDate = null;
		alarmDbID = -1;
		lastStatus = new AlarmData();
	}

	public ContainerAlarm(ContainerAlarmCfg config, String type, String name)
			throws PollingException {
		this();
		alarmUUID = config.getId().toString();
		this.type = type;
		alarmName = name;
		update(config);
	}

	public String getAlarmUUID() {
		return alarmUUID;
	}

	public void setAlarmUUID(String alarmUUID) {
		this.alarmUUID = alarmUUID;
	}

	public String getAlarmID() {
		return alarmID;
	}

	public void setAlarmID(String alarmID) {
		this.alarmID = alarmID;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getAlarmDbID() {
		return alarmDbID;
	}

	public void setAlarmDbID(int alarmDbID) {
		this.alarmDbID = alarmDbID;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public AlarmData getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(AlarmData lastStatus) {
		this.lastStatus = lastStatus;
	}

	public void update(ContainerAlarmCfg config) throws PollingException {
		if (!config.getId().toString().equals(alarmUUID))
			throw new PollingException("Cannot update container alarm"
					+ " configuration, UUID mismatch: expected'" + alarmUUID
					+ "' found '" + config.getId() + "'");
		alarmID = config.getAlarmNameId();
		notes = config.getNotes();
		deletionDate = null;
	}

	public void setDeleted() {
		if (deletionDate == null)
			deletionDate = new Date();
	}

	public boolean isDeleted() {
		return deletionDate != null;
	}

}
