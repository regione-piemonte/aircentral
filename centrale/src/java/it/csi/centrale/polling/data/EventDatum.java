/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for event datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: EventDatum.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;
/**
 * Class for event datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class EventDatum extends Datum {

	private Boolean status;
	private boolean hasAuxStatus = false;
	private Integer auxStatus = null;

	public EventDatum(String recordLine) throws IllegalArgumentException,
			ParseException {
		super(recordLine, true, 2);
	}

	public Boolean getStatus() {
		return status;
	}

	public boolean hasAuxStatus() {
		return hasAuxStatus;
	}

	public Integer getAuxStatus() {
		return auxStatus;
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
		if (fields.length > 2) {
			hasAuxStatus = true;
			if (!fields[2].isEmpty())
				auxStatus = (int) Long.parseLong(fields[2], 16);
		}
	}

	@Override
	public String toString() {
		return "EventDatum [timestamp=" + printTimeStamp(true) + ", status="
				+ status + ", hasAuxStatus=" + hasAuxStatus + ", auxStatus="
				+ auxStatus + "]";
	}

}
