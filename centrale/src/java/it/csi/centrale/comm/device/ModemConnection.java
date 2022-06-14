/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Modem connection
// Change log:
//   2014-03-27: initial version
// ----------------------------------------------------------------------------
// $Id: ModemConnection.java,v 1.4 2014/10/10 15:57:35 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm.device;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.ModemCommConfig;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Modem connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class ModemConnection extends Connection {

	private static Logger logger = Logger.getLogger("centrale."
			+ ModemConnection.class.getSimpleName());

	private Modem device;
	private ModemCommConfig config;

	ModemConnection(Modem device, ModemCommConfig config) {
		super(device, config);
		this.device = device;
		this.config = config;
	}

	@Override
	protected void startImpl() throws ConnectionException, IOException,
			InterruptedException {
		StringBuilder phoneNumber = new StringBuilder();
		if (device.getCallPrefix() != null)
			phoneNumber.append(device.getCallPrefix());
		phoneNumber.append(config.getPhone());
		StringBuilder errMsg = new StringBuilder();
		int rc = Utils.execute(null, errMsg, "./bin/startPPP", device.getTty(),
				phoneNumber.toString(), config.getHost(),
				device.getCentraleHost());
		logger.debug("Start PPP return code is: " + rc);
		switch (rc) {
		case 0:
			break;
		case 10:
		case 11:
		case 15:
		case 16:
		case 17:
		case 81:
		case 82:
		case 83:
		case 84:
		case 85:
			throw new ConnectionException(Failure.REMOTE_DEVICE, rc,
					errMsg.toString());
		default:
			throw new ConnectionException(Failure.LOCAL_DEVICE, rc,
					errMsg.toString());
		}
	}

	@Override
	protected void testImpl() throws ConnectionException, IOException,
			InterruptedException {
		int rc = Utils.execute("./bin/checkPPP", device.getTty());
		logger.debug("Check PPP return code is: " + rc);
		if (rc != 0)
			throw new ConnectionException(Failure.REMOTE_DEVICE);
		if (!Utils.ping(config.getHost(), 30))
			throw new ConnectionException(Failure.REMOTE_DEVICE);
	}

	@Override
	protected void restartImpl() throws ConnectionException, IOException,
			InterruptedException {
		stopImpl();
		startImpl();
	}

	@Override
	protected void stopImpl() {
		try {
			int rc = Utils.execute("./bin/stopPPP", device.getTty());
			logger.debug("Stop PPP return code is: " + rc);
		} catch (Exception e) {
			logger.error("Error stopping PPP" + e);
		}
	}

	@Override
	public String getTypeName() {
		return "Modem";
	}

}
