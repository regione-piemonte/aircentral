/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents modem.
* Change log:
*   2009-05-04: initial version
* ----------------------------------------------------------------------------
* $Id: ModemInfo.java,v 1.1 2009/05/04 12:05:46 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class that represents modem.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ModemInfo implements Serializable {

	private static final long serialVersionUID = -1452958147643204874L;

	private String deviceId;
	private boolean sharedLine;
	private String phonePrefix;

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the sharedLine
	 */
	public boolean isSharedLine() {
		return sharedLine;
	}

	/**
	 * @param sharedLine the sharedLine to set
	 */
	public void setSharedLine(boolean sharedLine) {
		this.sharedLine = sharedLine;
	}

	/**
	 * @return the phonePrefix
	 */
	public String getPhonePrefix() {
		return phonePrefix;
	}

	/**
	 * @param phonePrefix the phonePrefix to set
	 */
	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

}
