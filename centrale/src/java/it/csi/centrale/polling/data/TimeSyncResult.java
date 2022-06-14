/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for result of time synchronization
// Change log:
//   2014-04-17: initial version
// ----------------------------------------------------------------------------
// $Id: TimeSyncResult.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Class for result of time synchronization
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class TimeSyncResult {

	private final String DATA_TZ_DATE_FMT = "yyyyMMdd HHmmss Z";

	private Boolean syncResult;
	private Date remoteDate;

	public TimeSyncResult(Map<String, String> mapReply) throws IllegalArgumentException {
		if (!mapReply.containsKey("sync"))
			throw new IllegalArgumentException("Missing time sync result");
		String strSync = mapReply.get("sync");
		syncResult = strSync == null ? null : new Boolean(strSync);
		String strRemoteDate = mapReply.get("date");
		if (strRemoteDate == null)
			throw new IllegalArgumentException("Missing remote system date");
		try {
			remoteDate = new SimpleDateFormat(DATA_TZ_DATE_FMT).parse(strRemoteDate);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unparsable remote system date '" + strRemoteDate + "'", e);
		}
	}

	public boolean isRemoteTimeAlarm() {
		return syncResult == null;
	}

	public Boolean getSyncResult() {
		return syncResult;
	}

	public Date getRemoteDate() {
		return remoteDate;
	}

	@Override
	public String toString() {
		return "TimeSyncResult [remoteTimeAlarm=" + isRemoteTimeAlarm() + ", syncResult=" + syncResult + ", remoteDate="
				+ remoteDate + "]";
	}

}
