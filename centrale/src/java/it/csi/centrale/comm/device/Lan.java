/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Lan communication
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: Lan.java,v 1.2 2014/09/22 13:49:33 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.device;

import it.csi.centrale.comm.CommDevice;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.config.LanCommConfig;

/**
 * Lan communication
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class Lan extends CommDevice {

	private int connectTimeout_s;
	private int maxConnections;

	public Lan(String name, int connectTimeout_s, int maxConnections) {
		super(name);
		setConnectTimeout_s(connectTimeout_s);
		setMaxConnections(maxConnections);
	}

	/**
	 * @return timeout in seconds
	 */
	public int getConnectTimeout_s() {
		return connectTimeout_s;
	}

	public void setConnectTimeout_s(int connectTimeout_s) {
		if (connectTimeout_s < 0)
			throw new IllegalArgumentException("Connection timeout should " + "not be negative");
		this.connectTimeout_s = connectTimeout_s;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		if (maxConnections <= 0)
			throw new IllegalArgumentException("Maximum number of connections" + " should be greater than 0");
		this.maxConnections = maxConnections;
	}

	@Override
	protected LanConnection getConnectionImpl(CommConfig config, boolean urgent)
			throws IncompatibleDeviceException, DeviceBusyException {
		if (!(config instanceof LanCommConfig))
			throw new IncompatibleDeviceException(
					"Lan device cannot manage " + "communication configuration: " + config);
		int numConnections = countConnections();
		if (numConnections >= maxConnections && !urgent)
			throw new DeviceBusyException(
					"Active connections: " + numConnections + ", maximum connections: " + maxConnections);
		return new LanConnection(this, (LanCommConfig) config);
	}

	@Override
	public String toString() {
		return "Lan [name=" + getName() + ", connectTimeout_s=" + connectTimeout_s + ", maxConnections="
				+ maxConnections + "]";
	}

}
