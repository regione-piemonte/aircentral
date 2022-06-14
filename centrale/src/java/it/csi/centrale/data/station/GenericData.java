/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a generic data
 * Change log:
 *   2008-11-07: initial version
 * ----------------------------------------------------------------------------
 * $Id: GenericData.java,v 1.12 2014/09/16 08:14:10 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.Datum;
import it.csi.centrale.polling.data.DoubleDatum;
import it.csi.centrale.polling.data.WindDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about a generic data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public abstract class GenericData implements Serializable {
	private static final long serialVersionUID = 5493142607697513428L;

	Date timestamp;

	boolean notValid;

	Integer instrumentFlags;

	Date updateDate;

	public GenericData() {
	}

	public GenericData(Date time, boolean validity, Integer flags,
			Date updateDate) {
		this.timestamp = time;
		this.notValid = validity;
		this.instrumentFlags = flags;
		this.updateDate = updateDate;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isNotValid() {
		return notValid;
	}

	public void setNotValid(boolean valid) {
		this.notValid = valid;
	}

	public Integer getInstrumentFlags() {
		return instrumentFlags;
	}

	public void setInstrumentFlags(Integer instrumentFlags) {
		this.instrumentFlags = instrumentFlags;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public static GenericData newInstance(Datum datum) throws PollingException {
		if (datum instanceof DoubleDatum)
			return new RainScalarData((DoubleDatum) datum);
		if (datum instanceof WindDatum)
			return new WindData((WindDatum) datum);
		throw new PollingException("Unknown class of data: '"
				+ datum.getClass().getSimpleName() + "'");
	}

}
