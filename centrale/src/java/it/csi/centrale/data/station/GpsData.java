/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a Gps data
 * Change log:
 *   2009-02-23: initial version
 * ----------------------------------------------------------------------------
 * $Id: GpsData.java,v 1.6 2014/09/16 08:14:10 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.GpsDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about a Gps data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class GpsData implements Serializable {
	private static final long serialVersionUID = 3477925958459792072L;

	private Date timestamp;

	private Double latitude;

	private Double longitude;

	private Double altitude;

	private String comment;

	private Date updateDate;

	public GpsData() {

	}

	public GpsData(Date timestamp, Double latitude, Double longitude,
			Double altitude, String comment, Date updateDate) {
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.comment = comment;
	}

	public GpsData(GpsDatum datum) {
		this(datum.getTimestamp(), datum.getLatitude(), datum.getLongitude(),
				datum.getAltitude(), datum.getFix().toString(), null);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
