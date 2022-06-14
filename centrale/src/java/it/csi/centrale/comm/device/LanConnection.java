/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Lan connection
// Change log:
//   2014-03-26: initial version
// ----------------------------------------------------------------------------
// $Id: LanConnection.java,v 1.3 2014/09/16 16:42:32 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm.device;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.LanCommConfig;

import java.io.IOException;

/**
 * Lan connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class LanConnection extends Connection {

	private Lan device;
	private LanCommConfig config;

	LanConnection(Lan device, LanCommConfig config) {
		super(device, config);
		this.device = device;
		this.config = config;
	}

	@Override
	protected void startImpl() throws ConnectionException, IOException, InterruptedException {
		testImpl();
	}

	@Override
	protected void testImpl() throws ConnectionException, IOException, InterruptedException {
		if (!Utils.ping(config.getHost(), device.getConnectTimeout_s()))
			throw new ConnectionException(Failure.REMOTE_HOST);
	}

	@Override
	protected void restartImpl() throws ConnectionException, IOException, InterruptedException {
		testImpl();
	}

	@Override
	protected void stopImpl() {
	}

	@Override
	public String getTypeName() {
		return "LAN";
	}

}
