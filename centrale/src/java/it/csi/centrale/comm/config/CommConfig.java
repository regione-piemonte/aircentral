/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Common configuration class
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: CommConfig.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.config;

/**
 * Common configuration class
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public abstract class CommConfig {

	private String host;
	private Integer port;
	private String deviceName;

	public CommConfig(String host, Integer port, String deviceName) {
		setHost(host);
		setPort(port);
		setDeviceName(deviceName);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (host == null || (host = host.trim()).isEmpty())
			throw new IllegalArgumentException("Host name or IP not specified");
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		if (port == null)
			throw new IllegalArgumentException("IP port not specified");
		if (port < 1 || port > 65535)
			throw new IllegalArgumentException("IP port " + port + " not in range 1-65535");
		this.port = port;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		if (deviceName == null || (deviceName = deviceName.trim()).isEmpty())
			throw new IllegalArgumentException("Device name not specified");
		this.deviceName = deviceName;
	}

}
