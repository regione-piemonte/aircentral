/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for gps datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: GpsDatum.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;

/**
 * Class for gps datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class GpsDatum extends Datum {

	public enum Fix {
		GPS_APP_ERROR, GPS_READ_ERROR, NO_FIX, FIX_2D, FIX_3D
	}

	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Fix fix;

	public GpsDatum(String recordLine) throws IllegalArgumentException, ParseException {
		super(recordLine, false, 5);
	}

	@Override
	protected void parseFields(String[] fields) throws IllegalArgumentException {
		latitude = fields[1].isEmpty() ? null : new Double(fields[1]);
		longitude = fields[2].isEmpty() ? null : new Double(fields[2]);
		altitude = fields[3].isEmpty() ? null : new Double(fields[3]);
		fix = Fix.valueOf(fields[4]);
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public Fix getFix() {
		return fix;
	}

	@Override
	public String toString() {
		return "GpsDatum [timestamp=" + printTimeStamp(false) + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", altitude=" + altitude + ", fix=" + fix + "]";
	}

}
