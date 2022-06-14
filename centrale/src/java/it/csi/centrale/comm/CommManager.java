/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: 
// Change log: Class that manage the connection
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: CommManager.java,v 1.4 2014/09/22 16:24:32 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm;

import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.device.LineBasedDevice;
import it.csi.centrale.comm.device.Modem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Class that manage the connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class CommManager {

	private static Logger logger = Logger.getLogger("centrale."
			+ CommManager.class.getSimpleName());

	private Map<String, PhoneLine> mapLines = new HashMap<String, PhoneLine>();
	private Map<String, CommDevice> mapDevices = new HashMap<String, CommDevice>();

	public synchronized void addLine(PhoneLine line)
			throws CommManagerException {
		if (line == null)
			throw new IllegalArgumentException("Phone line not specified");
		String name = line.getName();
		if (mapLines.get(name) != null)
			throw new CommManagerException("Another line with name '" + name
					+ "' was previously added");
		mapLines.put(name, line);
	}

	public synchronized PhoneLine removeLine(String name) {
		return mapLines.remove(name);
	}

	public synchronized PhoneLine getLine(String name) {
		return mapLines.get(name);
	}

	public synchronized List<String> listLines() {
		List<String> result = new ArrayList<String>();
		result.addAll(mapLines.keySet());
		Collections.sort(result);
		return result;
	}

	public synchronized void addDevice(CommDevice device)
			throws CommManagerException {
		if (device == null)
			throw new IllegalArgumentException(
					"Communication device not specified");
		logger.info("Adding communication device: " + device);
		String name = device.getName();
		if (mapDevices.get(name) != null)
			throw new CommManagerException("Another device with name '" + name
					+ "' was previously added");
		mapDevices.put(name, device);
	}

	public synchronized CommDevice removeDevice(String name) {
		return mapDevices.remove(name);
	}

	public synchronized void removeAllDevices(Class<? extends CommDevice> type) {
		Iterator<CommDevice> iterator = mapDevices.values().iterator();
		while (iterator.hasNext()) {
			CommDevice cd = iterator.next();
			if (type.isInstance(cd)) {
				logger.debug("Removed communication device: " + cd);
				iterator.remove();
			}
		}
	}

	public synchronized CommDevice getDevice(String name) {
		return mapDevices.get(name);
	}

	public synchronized List<String> listDevices() {
		List<String> result = new ArrayList<String>();
		result.addAll(mapDevices.keySet());
		Collections.sort(result);
		return result;
	}

	public synchronized Connection getConnection(CommConfig config,
			boolean urgent) throws DeviceNotFoundException,
			IncompatibleDeviceException, DeviceBusyException {
		if (config == null)
			throw new IllegalArgumentException(
					"Communication config not specified");
		String name = config.getDeviceName();
		CommDevice device = mapDevices.get(name);
		if (device == null)
			throw new DeviceNotFoundException("No device found with name '"
					+ name + "'");
		if (device instanceof LineBasedDevice)
			checkLines((LineBasedDevice) device, urgent);
		return device.getConnection(config, urgent);
	}

	private void checkLines(LineBasedDevice lbd, boolean urgent)
			throws DeviceNotFoundException, DeviceBusyException {
		if (lbd.getLineName() == null && lbd instanceof Modem)
			return;
		PhoneLine line = getLine(lbd.getLineName());
		if (line == null)
			throw new DeviceNotFoundException("No line found with name "
					+ lbd.getLineName() + " for device " + lbd.getName());
		int numConnections = 0;
		for (CommDevice dev : mapDevices.values()) {
			if (!(dev instanceof LineBasedDevice))
				continue;
			LineBasedDevice lbDev = (LineBasedDevice) dev;
			if (!line.getName().equals(lbDev.getLineName()))
				continue;
			numConnections += lbDev.countConnections();
		}
		int allowedConnections = line.getTotalChannels()
				- (urgent ? 0 : line.getReservedChannels());
		if (numConnections >= allowedConnections)
			throw new DeviceBusyException("Device type: "
					+ lbd.getClass().getSimpleName() + ", name: "
					+ lbd.getName() + ", line: " + line.getName()
					+ ", active connections: " + numConnections
					+ ", maximum connections: " + line.getTotalChannels()
					+ ", reserved connections: " + line.getReservedChannels()
					+ ", urgent: " + urgent);
	}

}
