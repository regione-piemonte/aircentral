/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for crepresenting double datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: DoubleDatum.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;
/**
 * Class for crepresenting double datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class DoubleDatum extends Datum {

	private Double value;
	private boolean notValid;
	private int flags;

	public DoubleDatum(String recordLine) throws IllegalArgumentException,
			ParseException {
		super(recordLine, false, 4);
	}

	@Override
	protected void parseFields(String[] fields) throws IllegalArgumentException {
		value = fields[1].isEmpty() ? null : new Double(fields[1]);
		if ("1".equals(fields[2]))
			notValid = true;
		else if ("0".equals(fields[2]))
			notValid = false;
		else
			throw new IllegalArgumentException("Unknown value for validity "
					+ "flag '" + fields[2] + "'");
		flags = Integer.valueOf(fields[3], 16);
	}

	public Double getValue() {
		return value;
	}

	public boolean isNotValid() {
		return notValid;
	}

	public int getFlags() {
		return flags;
	}

	@Override
	public String toString() {
		return "DoubleDatum [timestamp=" + printTimeStamp(false) + ", value="
				+ value + ", notValid=" + notValid + ", flags="
				+ Integer.toHexString(flags) + "]";
	}

}
