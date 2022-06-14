/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class that represent the modem
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: Modem.java,v 1.4 2014/10/10 15:57:35 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.device;

import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.config.ModemCommConfig;
/**
 * Class that represent the modem
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class Modem extends LineBasedDevice {

	private String tty;
	private String callPrefix;
	private String centraleHost;

	public Modem(String name, String tty, String callPrefix, String centraleHost) {
		this(name, tty, callPrefix, centraleHost, null);
	}

	public Modem(String name, String tty, String callPrefix,
			String centraleHost, String sharedLineName) {
		super(name, sharedLineName);
		setTty(tty);
		setCallPrefix(callPrefix);
		setCentraleHost(centraleHost);
	}

	/**
	 * @return tty
	 */
	public String getTty() {
		return tty;
	}

	public void setTty(String tty) {
		if (tty == null || (tty = tty.trim()).isEmpty())
			throw new IllegalArgumentException("TTY port not specified");
		this.tty = tty;
	}

	/**
	 * @return prefix
	 */
	public String getCallPrefix() {
		return callPrefix;
	}

	/**
	 * Set the prefix
	 * @param callPrefix
	 */
	public void setCallPrefix(String callPrefix) {
		if (callPrefix != null)
			callPrefix = callPrefix.trim();
		this.callPrefix = callPrefix;
	}

	/**
	 * @return the centrale host
	 */
	public String getCentraleHost() {
		return centraleHost;
	}

	public void setCentraleHost(String centraleHost) {
		if (centraleHost == null
				|| (centraleHost = centraleHost.trim()).isEmpty())
			this.centraleHost = null;
		else
			this.centraleHost = centraleHost;
	}

	@Override
	protected ModemConnection getConnectionImpl(CommConfig config,
			boolean urgent) throws IncompatibleDeviceException,
			DeviceBusyException {
		if (!(config instanceof ModemCommConfig))
			throw new IncompatibleDeviceException("Modem device cannot manage "
					+ "communication configuration: " + config);
		int numConnections = countConnections();
		if (numConnections >= 1)
			throw new DeviceBusyException("Active connections: "
					+ numConnections + ", maximum connections: 1");
		return new ModemConnection(this, (ModemCommConfig) config);
	}

	@Override
	public String toString() {
		return "Modem [name=" + getName() + ", lineName=" + getLineName()
				+ ", tty=" + tty + ", callPrefix=" + callPrefix
				+ ", centraleHost=" + centraleHost + "]";
	}

}
