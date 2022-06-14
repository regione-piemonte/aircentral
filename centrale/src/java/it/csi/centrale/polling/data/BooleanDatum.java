/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class describing a boolean datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: BooleanDatum.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;
/**
 * Class describing a boolean datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class BooleanDatum extends Datum {

	private Boolean status;

	public BooleanDatum(String recordLine) throws IllegalArgumentException,
			ParseException {
		super(recordLine, true, 2);
	}

	public Boolean getStatus() {
		return status;
	}

	@Override
	protected void parseFields(String[] fields) throws IllegalArgumentException {
		String strStatus = fields[1];
		if (strStatus.isEmpty())
			status = null;
		else if ("1".equals(strStatus))
			status = true;
		else if ("0".equals(strStatus))
			status = false;
		else
			throw new IllegalArgumentException("Unknown value for binary "
					+ "status '" + strStatus + "'");
	}

	@Override
	public String toString() {
		return "BooleanDatum [timestamp=" + printTimeStamp(true) + ", status="
				+ status + "]";
	}

}
