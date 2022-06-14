/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a modem
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: ModemConf.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about a modem
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class ModemConf implements Serializable {
	// explain what type of modem can use COP to communicate with stations
	private static final long serialVersionUID = -5599729312325958516L;

	private String description; // device name to use to connect example ttyS0

	private boolean sharedLine; // is true if this modem is connect with an
								// external shared with router

	private String phonePrefix; // phone prefix, not null

	private Date updateDate;

	public ModemConf() {
	}

	public ModemConf(String descr, boolean shared, String prefix) {
		description = descr;
		sharedLine = shared;
		phonePrefix = prefix;
		updateDate = new Date();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSharedLine() {
		return sharedLine;
	}

	public void setSharedLine(boolean sharedLine) {
		this.sharedLine = sharedLine;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
