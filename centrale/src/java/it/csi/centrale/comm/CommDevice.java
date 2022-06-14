/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class that implement the communication with device
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: CommDevice.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import it.csi.centrale.comm.config.CommConfig;

/**
 * Class that implement the communication with device
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public abstract class CommDevice {

	private static Logger logger = Logger.getLogger("centrale." + CommDevice.class.getSimpleName());

	private String name;
	private Set<Connection> setConnections = new HashSet<Connection>();

	protected CommDevice(String name) {
		if (name == null || (name = name.trim()).isEmpty())
			throw new IllegalArgumentException("Communication device name" + " not specified");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @param config
	 * @param urgent
	 * @return connection
	 * @throws IncompatibleDeviceException
	 * @throws DeviceBusyException
	 */
	synchronized final Connection getConnection(CommConfig config, boolean urgent)
			throws IncompatibleDeviceException, DeviceBusyException {
		if (config == null)
			throw new IllegalArgumentException("Communication configuration" + " not specified");
		logger.trace("Requesting new connection to device " + name + " of type " + getClass().getSimpleName()
				+ " for configuration: " + config);
		Connection conn = getConnectionImpl(config, urgent);
		setConnections.add(conn);
		logger.debug("Device " + name + " offers new connection with id " + conn.getId() + " for configuration: "
				+ config + ". Now this device has " + setConnections.size() + " connections");
		return conn;
	}

	/**
	 * Release the connection
	 * 
	 * @param connection
	 */
	synchronized final void releaseConnection(Connection connection) {
		if (connection == null)
			throw new IllegalArgumentException("Connection should not be null");
		setConnections.remove(connection);
		logger.debug("Released connection with id " + connection.getId() + " from device " + name + ", still having "
				+ setConnections.size() + " connections");
	}

	synchronized final boolean hasConnection(Connection connection) {
		return setConnections.contains(connection);
	}

	synchronized protected int countConnections() {
		return setConnections.size();
	}

	abstract protected Connection getConnectionImpl(CommConfig config, boolean urgent)
			throws IncompatibleDeviceException, DeviceBusyException;

	protected String checkHost(String host) throws IllegalArgumentException {
		if (host == null || (host = host.trim()).isEmpty())
			throw new IllegalArgumentException("Host name or IP not specified");
		try {
			InetAddress.getByName(host);
			return host;
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Not valid host name or IP '" + host + "'", e);
		}
	}

	protected int checkPort(int port) throws IllegalArgumentException {
		if (port < 1 || port > 65535)
			throw new IllegalArgumentException("IP port " + port + " not in range 1-65535");
		return port;
	}

	protected int checkPort(String port) throws IllegalArgumentException {
		if (port == null || (port = port.trim()).isEmpty())
			throw new IllegalArgumentException("IP port not specified");
		int iPort;
		try {
			iPort = new Integer(port);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value for IP port '" + port + "'", e);
		}
		return checkPort(iPort);
	}

}
