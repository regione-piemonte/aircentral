/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a scalar or rain data
 * Change log:
 *   2008-11-07: initial version
 * ----------------------------------------------------------------------------
 * $Id: RainScalarData.java,v 1.11 2014/09/16 08:14:10 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.DoubleDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about a scalar or rain data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class RainScalarData extends GenericData implements Serializable {

	private static final long serialVersionUID = -3608790394307818413L;

	private Double value;

	public RainScalarData() {
		super();
	}

	public RainScalarData(Date time, Double meanValue, boolean meanValidity,
			Integer meanFlags, Date updateDate) {
		super(time, meanValidity, meanFlags, updateDate);
		this.value = meanValue;
	}

	public RainScalarData(DoubleDatum datum) {
		this(datum.getTimestamp(), datum.getValue(), datum.isNotValid(), datum
				.getFlags(), null);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String toString() {
		return ("timestamp:" + this.timestamp + " value:" + this.value
				+ " valid:" + this.notValid + " flags:" + this.instrumentFlags
				+ " updateDate:" + this.updateDate);
	}

}
