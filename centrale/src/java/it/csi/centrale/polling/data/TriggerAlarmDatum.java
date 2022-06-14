/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for trigger alarm datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: TriggerAlarmDatum.java,v 1.2 2014/09/19 11:21:01 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.ParseException;

import it.csi.centrale.polling.data.TriggerContainerAlarmStatus.Status;

/**
 * Class for trigger alarm datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class TriggerAlarmDatum extends Datum {

	private Status status;

	public TriggerAlarmDatum(String recordLine) throws IllegalArgumentException, ParseException {
		super(recordLine, true, 2);
	}

	public Status getStatus() {
		return status;
	}

	@Override
	protected void parseFields(String[] fields) throws IllegalArgumentException {
		String strStatus = fields[1];
		status = TriggerContainerAlarmStatus.statusFromString(strStatus);
	}

	@Override
	public String toString() {
		return "TriggerAlarmDatum [timestamp=" + printTimeStamp(true) + ", status=" + status + "]";
	}

}
