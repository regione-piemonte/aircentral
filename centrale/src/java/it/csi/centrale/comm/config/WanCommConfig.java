/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Wan configuration
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: WanCommConfig.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.config;

/**
 * Wan configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class WanCommConfig extends CommConfig {

	public WanCommConfig(String host, Integer port, String deviceName) {
		super(host, port, deviceName);
	}

	@Override
	public String toString() {
		return "WanCommConfig [host=" + getHost() + ", port=" + getPort() + ", device=" + getDeviceName() + "]";
	}

}
