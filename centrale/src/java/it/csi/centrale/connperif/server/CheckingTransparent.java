/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Check proxy
// Change log:
//   2014-04-29: initial version
// ----------------------------------------------------------------------------
// $Id: CheckingTransparent.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.connperif.server;

import java.net.MalformedURLException;
import java.net.URL;

import org.mortbay.servlet.ProxyServlet.Transparent;
/**
 * Check proxy
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
class CheckingTransparent extends Transparent {

	private ProxyInfo proxyInfo;

	CheckingTransparent(String prefix, String server, int port) {
		super(prefix, server, port);
	}

	@Override
	protected URL proxyHttpURL(final String scheme, final String serverName,
			int serverPort, final String uri) throws MalformedURLException {
		if (proxyInfo != null)
			proxyInfo.updateActivityTime();
		return super.proxyHttpURL(scheme, serverName, serverPort, uri);
	}

	void setProxtInfo(ProxyInfo proxyInfo) {
		this.proxyInfo = proxyInfo;
	}

}