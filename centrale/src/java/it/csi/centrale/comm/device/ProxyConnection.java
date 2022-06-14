/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Proxy connection
// Change log:
//   2014-03-27: initial version
// ----------------------------------------------------------------------------
// $Id: ProxyConnection.java,v 1.3 2014/09/16 16:42:32 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm.device;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.ProxyCommConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
/**
 * Proxy connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class ProxyConnection extends Connection {

	private Proxy device;
	private ProxyCommConfig config;
	private java.net.Proxy httpProxy;

	ProxyConnection(Proxy device, ProxyCommConfig config) {
		super(device, config);
		this.device = device;
		this.config = config;
		this.httpProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
				new InetSocketAddress(device.getHost(), device.getPort()));
	}

	@Override
	protected void startImpl() throws ConnectionException, IOException,
			InterruptedException {
		testImpl();
	}

	@Override
	protected void testImpl() throws ConnectionException, IOException,
			InterruptedException {
		if (Utils.httpGetTest(config.getHost(), config.getPort(), device
				.getConnectTimeout_s(), httpProxy))
			return;
		boolean proxyAlive = Utils.socketTest(device.getHost(), device
				.getPort(), 10);
		if (proxyAlive)
			throw new ConnectionException(Failure.REMOTE_DEVICE);
		else
			throw new ConnectionException(Failure.LOCAL_DEVICE);
	}

	@Override
	protected void restartImpl() throws ConnectionException, IOException,
			InterruptedException {
		testImpl();
	}

	@Override
	protected void stopImpl() {
	}

	@Override
	protected URLConnection openURLConnection(URL url) throws IOException {
		return url.openConnection(httpProxy);
	}

	@Override
	public String getTypeName() {
		return "Proxy";
	}

}
