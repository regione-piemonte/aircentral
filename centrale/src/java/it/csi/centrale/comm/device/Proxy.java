/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Proxy connection
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: Proxy.java,v 1.3 2014/09/22 13:49:33 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.device;

import it.csi.centrale.comm.CommDevice;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.config.ProxyCommConfig;
/**
 * Proxy connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class Proxy extends CommDevice {

	private String host;
	private int port;
	private int connectTimeout_s;
	private int maxConnections;

	public Proxy(String name, String host, int port, int connectTimeout_s,
			int maxConnections) {
		super(name);
		setHost(host);
		setPort(port);
		setConnectTimeout_s(connectTimeout_s);
		setMaxConnections(maxConnections);
	}

	public Proxy(String name, String host, String port, int connectTimeout_s,
			int maxConnections) {
		super(name);
		setHost(host);
		setPort(port);
		setConnectTimeout_s(connectTimeout_s);
		setMaxConnections(maxConnections);
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = checkHost(host);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = checkPort(port);
	}

	public void setPort(String port) {
		this.port = checkPort(port);
	}

	public int getConnectTimeout_s() {
		return connectTimeout_s;
	}

	public void setConnectTimeout_s(int connectTimeout_s) {
		if (connectTimeout_s < 0)
			throw new IllegalArgumentException("Connection timeout should "
					+ "not be negative");
		this.connectTimeout_s = connectTimeout_s;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		if (maxConnections <= 0)
			throw new IllegalArgumentException("Maximum number of connections"
					+ " should be greater than 0");
		this.maxConnections = maxConnections;
	}

	@Override
	protected ProxyConnection getConnectionImpl(CommConfig config,
			boolean urgent) throws IncompatibleDeviceException,
			DeviceBusyException {
		if (!(config instanceof ProxyCommConfig))
			throw new IncompatibleDeviceException("Proxy device cannot manage "
					+ "communication configuration: " + config);
		int numConnections = countConnections();
		if (numConnections >= maxConnections && !urgent)
			throw new DeviceBusyException("Active connections: "
					+ numConnections + ", maximum connections: "
					+ maxConnections);
		return new ProxyConnection(this, (ProxyCommConfig) config);
	}

	@Override
	public String toString() {
		return "Proxy [name=" + getName() + ", host=" + host + ", port=" + port
				+ ", connectTimeout_s=" + connectTimeout_s
				+ ", maxConnections=" + maxConnections + "]";
	}

}
