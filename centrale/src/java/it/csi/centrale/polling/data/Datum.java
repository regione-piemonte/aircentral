/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class representing datum
// Change log:
//   2014-04-16: initial version
// ----------------------------------------------------------------------------
// $Id: Datum.java,v 1.2 2014/09/16 08:14:10 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Class representing datum
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public abstract class Datum {

	private static final String DATA_DATE_FMT_STR = "yyyyMMdd HHmmss";
	private static final String DATA_DATE_FMT_MS_STR = "yyyyMMdd HHmmss.SSS";

	private Date timestamp;

	public Datum(String recordLine, boolean timestampWithMillis,
			int minimumFields) throws IllegalArgumentException, ParseException {
		if (recordLine == null || recordLine.isEmpty())
			throw new IllegalArgumentException("Empty line");
		String[] fields = recordLine.split(",", -1);
		if (fields.length < minimumFields)
			throw new IllegalArgumentException("Expected at least "
					+ minimumFields + " fields, found " + fields.length);
		for (int i = 0; i < fields.length; i++)
			fields[i] = fields[i].trim();
		DateFormat df = new SimpleDateFormat(
				timestampWithMillis ? DATA_DATE_FMT_MS_STR : DATA_DATE_FMT_STR);
		timestamp = df.parse(fields[0]);
		parseFields(fields);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	protected abstract void parseFields(String[] fields)
			throws IllegalArgumentException;

	protected String printTimeStamp(boolean timestampWithMillis) {
		DateFormat df = new SimpleDateFormat(
				timestampWithMillis ? DATA_DATE_FMT_MS_STR : DATA_DATE_FMT_STR);
		return df.format(timestamp);
	}
	
}
