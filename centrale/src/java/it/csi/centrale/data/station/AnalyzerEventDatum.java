/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about the status of an
 * analyzer
 * Change log:
 *   2009-01-14: initial version
 * ----------------------------------------------------------------------------
 * $Id: AnalyzerEventDatum.java,v 1.1 2014/09/19 11:21:01 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.EventDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about the status of an analyzer
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class AnalyzerEventDatum implements Serializable {

	private static final long serialVersionUID = 6256764114962693223L;

	private Boolean value;

	private Date timestamp;

	private String type;

	public AnalyzerEventDatum() {
		timestamp = null;
	}

	public AnalyzerEventDatum(Date anStatusTime, Boolean statusValue, String type) {
		this.value = statusValue;
		this.timestamp = anStatusTime;
		this.type = type;
	}

	public AnalyzerEventDatum(EventDatum datum, String type) {
		this.value = datum.getStatus();
		this.timestamp = datum.getTimestamp();
		this.type = type;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
