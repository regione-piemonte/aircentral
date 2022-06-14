/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with status information about an alarm 
* Change log:
*   2009-01-16: initial version
* ----------------------------------------------------------------------------
* $Id: AlarmStatusObject.java,v 1.5 2009/10/09 09:36:50 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Object with status information about an alarm
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class AlarmStatusObject implements Serializable {
	private static final long serialVersionUID = -1945888109472935003L;

	private String alarmId;
	private int alarmDbId;
	private String alarmDescription;
	private String notes;
	private String status;
	private Date timestamp;
	private String timestampString;

	public AlarmStatusObject() {
	}

	/**
	 * @return the alarmId
	 */
	public String getAlarmId() {
		return alarmId;
	}

	/**
	 * @param alarmId the alarmId to set
	 */
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	/**
	 * @return the alarmDbId
	 */
	public int getAlarmDbId() {
		return alarmDbId;
	}

	/**
	 * @param alarmDbId the alarmDbId to set
	 */
	public void setAlarmDbId(int alarmDbId) {
		this.alarmDbId = alarmDbId;
	}

	/**
	 * @return the alarmDescription
	 */
	public String getAlarmDescription() {
		return alarmDescription;
	}

	/**
	 * @param alarmDescription the alarmDescription to set
	 */
	public void setAlarmDescription(String alarmDescription) {
		this.alarmDescription = alarmDescription;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the timestampString
	 */
	public String getTimestampString() {
		return timestampString;
	}

	/**
	 * @param timestampString the timestampString to set
	 */
	public void setTimestampString(String timestampString) {
		this.timestampString = timestampString;
	}

}
