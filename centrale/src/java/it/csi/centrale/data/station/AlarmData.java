/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about an alarm data
 * Change log:
 *   2009-02-12: initial version
 * ----------------------------------------------------------------------------
 * $Id: AlarmData.java,v 1.8 2014/09/24 15:48:23 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.BooleanDatum;
import it.csi.centrale.polling.data.ContainerAlarmStatus;
import it.csi.centrale.polling.data.DigitalContainerAlarmStatus;
import it.csi.centrale.polling.data.TriggerAlarmDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about an alarm data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class AlarmData implements Serializable {
	private static final long serialVersionUID = 3496165699945148051L;

	private Date timestamp;

	private String value;

	public AlarmData() {

	}

	public AlarmData(Date timestamp, String value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public AlarmData(BooleanDatum datum) {
		timestamp = datum.getTimestamp();
		if (datum.getStatus() == null)
			value = "";
		else if (datum.getStatus())
			value = "1";
		else
			value = "0";
	}

	public AlarmData(Date timestamp, ContainerAlarmStatus alarmStatus) {
		this.timestamp = timestamp;
		if (alarmStatus == null || alarmStatus.getStatus() == null) {
			value = "";
		} else {
			if (alarmStatus instanceof DigitalContainerAlarmStatus)
				value = ((DigitalContainerAlarmStatus) alarmStatus).getStatus() ? "1"
						: "0";
			else
				value = alarmStatus.getStatus().toString();
		}
	}

	public AlarmData(TriggerAlarmDatum datum) {
		timestamp = datum.getTimestamp();
		if (datum.getStatus() == null)
			value = "";
		value = datum.getStatus().toString();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
