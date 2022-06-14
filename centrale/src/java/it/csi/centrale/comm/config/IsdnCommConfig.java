/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Isdn configuration class
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: IsdnCommConfig.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.config;

/**
 * Isdn configuration class
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class IsdnCommConfig extends CommConfig {

	private String routerHost;

	public IsdnCommConfig(String host, Integer port, String deviceName, String routerHost) {
		super(host, port, deviceName);
		setRouterHost(routerHost);
	}

	/**
	 * @return the router host
	 */
	public String getRouterHost() {
		return routerHost;
	}

	/**
	 * set the router host
	 * 
	 * @param routerHost
	 */
	public void setRouterHost(String routerHost) {
		if (routerHost == null || (routerHost = routerHost.trim()).isEmpty())
			throw new IllegalArgumentException("Router host name or IP not specified");
		this.routerHost = routerHost;
	}

	@Override
	public String toString() {
		return "IsdnCommConfig [host=" + getHost() + ", port=" + getPort() + ", device=" + getDeviceName()
				+ ", routerHost=" + routerHost + "]";
	}

}
