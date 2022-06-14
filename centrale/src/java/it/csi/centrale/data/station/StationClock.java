/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about the synchronization
 * of a station
 * Change log:
 *   2009-03-20: initial version
 * ----------------------------------------------------------------------------
 * $Id: StationClock.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about the synchronization
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class StationClock implements Serializable {

	private static final long serialVersionUID = 4313307262888286863L;

	Date clock;

	Boolean synched;

	public Date getClock() {
		return clock;
	}

	public void setClock(Date date) {
		this.clock = date;
	}

	public Boolean getSynched() {
		if (synched == null)
			return null;
		else
			return synched;
	}

	public void setSynched(Boolean synched) {
		this.synched = synched;
	}

}
