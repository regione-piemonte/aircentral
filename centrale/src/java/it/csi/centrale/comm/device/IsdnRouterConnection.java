/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class that represent the isdn router connection
// Change log:
//   2014-03-27: initial version
// ----------------------------------------------------------------------------
// $Id: IsdnRouterConnection.java,v 1.3 2014/09/16 16:42:32 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm.device;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.IsdnCommConfig;

import java.io.IOException;

/**
 * Class that represent the isdn router connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class IsdnRouterConnection extends Connection {

	private IsdnRouter device;
	private IsdnCommConfig config;
	private volatile boolean waitOnStopRequired = false;

	IsdnRouterConnection(IsdnRouter device, IsdnCommConfig config) {
		super(device, config);
		this.device = device;
		this.config = config;
	}

	/**
	 * Start connection
	 */
	@Override
	protected void startImpl() throws ConnectionException, IOException, InterruptedException {
		testImpl();
	}

	/**
	 * Test connection
	 */
	@Override
	protected void testImpl() throws ConnectionException, IOException, InterruptedException {
		waitOnStopRequired = true;
		if (device.getHost() != null && !Utils.ping(device.getHost(), 5))
			throw new ConnectionException(Failure.LOCAL_DEVICE);
		if (!Utils.ping(config.getRouterHost(), device.getConnectTimeout_s()))
			throw new ConnectionException(Failure.REMOTE_DEVICE);
		if (!Utils.ping(config.getHost(), device.getConnectTimeout_s()))
			throw new ConnectionException(Failure.REMOTE_HOST);
	}

	@Override
	protected void restartImpl() throws ConnectionException, IOException, InterruptedException {
		testImpl();
	}

	@Override
	protected void stopImpl() {
		if (waitOnStopRequired) {
			waitOnStopRequired = false;
			try {
				Thread.sleep(device.getHangupDelay_s() * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String getTypeName() {
		return "ISDN";
	}
}
