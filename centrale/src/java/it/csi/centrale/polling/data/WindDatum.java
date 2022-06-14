/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for wind datum
// Change log:
//   2014-04-17: initial version
// ----------------------------------------------------------------------------
// $Id: WindDatum.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;

/**
 * Class for wind datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class WindDatum extends Datum {

	private Double vectorialSpeed;
	private Double vectorialDirection;
	private Double standardDeviation;
	private Double scalarSpeed;
	private Double gustSpeed;
	private Double gustDirection;
	private Double calmsNumberPercent;
	private Boolean calm;
	private boolean notValid;
	private int flags;

	public WindDatum(String recordLine) throws IllegalArgumentException, ParseException {
		super(recordLine, false, 11);
	}

	@Override
	protected void parseFields(String[] fields) throws IllegalArgumentException {
		vectorialSpeed = fields[1].isEmpty() ? null : new Double(fields[1]);
		vectorialDirection = fields[2].isEmpty() ? null : new Double(fields[2]);
		standardDeviation = fields[3].isEmpty() ? null : new Double(fields[3]);
		scalarSpeed = fields[4].isEmpty() ? null : new Double(fields[4]);
		gustSpeed = fields[5].isEmpty() ? null : new Double(fields[5]);
		gustDirection = fields[6].isEmpty() ? null : new Double(fields[6]);
		calmsNumberPercent = fields[7].isEmpty() ? null : new Double(fields[7]);
		calm = fields[8].isEmpty() ? null : new Boolean(fields[8]);
		if ("1".equals(fields[9]))
			notValid = true;
		else if ("0".equals(fields[9]))
			notValid = false;
		else
			throw new IllegalArgumentException("Unknown value for validity " + "flag '" + fields[9] + "'");
		flags = Integer.valueOf(fields[10], 16);
	}

	public Double getVectorialSpeed() {
		return vectorialSpeed;
	}

	public Double getVectorialDirection() {
		return vectorialDirection;
	}

	public Double getStandardDeviation() {
		return standardDeviation;
	}

	public Double getScalarSpeed() {
		return scalarSpeed;
	}

	public Double getGustSpeed() {
		return gustSpeed;
	}

	public Double getGustDirection() {
		return gustDirection;
	}

	public Double getCalmsNumberPercent() {
		return calmsNumberPercent;
	}

	public Boolean getCalm() {
		return calm;
	}

	public boolean isNotValid() {
		return notValid;
	}

	public int getFlags() {
		return flags;
	}

	@Override
	public String toString() {
		return "WindDatum [timestamp=" + printTimeStamp(false) + ", vectorialSpeed=" + vectorialSpeed
				+ ", vectorialDirection=" + vectorialDirection + ", standardDeviation=" + standardDeviation
				+ ", scalarSpeed=" + scalarSpeed + ", gustSpeed=" + gustSpeed + ", gustDirection=" + gustDirection
				+ ", calmsNumberPercent=" + calmsNumberPercent + ", calm=" + calm + ", notValid=" + notValid
				+ ", flags=" + Integer.toHexString(flags) + "]";
	}

}
