/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with status information about an analyzer alarm 
* Change log:
*   2009-01-16: initial version
* ----------------------------------------------------------------------------
* $Id: AnalyzerAlarmStatusValuesObject.java,v 1.4 2009/10/09 09:36:50 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Object with status information about an analyzer alarm
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class AnalyzerAlarmStatusValuesObject implements Serializable {
	private static final long serialVersionUID = 4135088032404527698L;

	private Boolean value;
	private Date timestamp;
	private String timestampString;
	private String type;

	public AnalyzerAlarmStatusValuesObject() {
	}

	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Boolean value) {
		this.value = value;
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
