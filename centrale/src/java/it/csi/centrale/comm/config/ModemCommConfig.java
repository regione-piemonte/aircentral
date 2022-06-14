/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Modem configuration
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: ModemCommConfig.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.config;

/**
 * Modem configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class ModemCommConfig extends CommConfig {

	private String phone;

	public ModemCommConfig(String host, Integer port, String deviceName, String phone) {
		super(host, port, deviceName);
		setPhone(phone);
	}

	/**
	 * @return phone numebr
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Set phone numebr
	 * 
	 * @param phone
	 */
	public void setPhone(String phone) {
		if (phone == null || (phone = phone.trim()).isEmpty())
			throw new IllegalArgumentException("Phone number not specified");
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "ModemCommConfig [host=" + getHost() + ", port=" + getPort() + ", device=" + getDeviceName() + ", phone="
				+ phone + "]";
	}

}
