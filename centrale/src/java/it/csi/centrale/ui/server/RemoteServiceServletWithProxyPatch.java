/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: hack to use reverse proxy from Centrale
// Change log:
//   2013-06-05: initial version
// ----------------------------------------------------------------------------
// $Id: RemoteServiceServletWithProxyPatch.java,v 1.2 2013/11/07 17:15:02 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.ui.server;

import it.csi.centrale.Centrale;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;

/**
 * hack to use reverse proxy from Centrale
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 */
public class RemoteServiceServletWithProxyPatch extends RemoteServiceServlet {

	private static final long serialVersionUID = -914039502965207671L;

	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL,
			String strongName) {
		String mbu;
		if (moduleBaseURL != null && Centrale.getRegexp() != null)
			mbu = moduleBaseURL.replaceFirst(Centrale.getRegexp(), "/");
		else
			mbu = moduleBaseURL;
		return super.doGetSerializationPolicy(request, mbu, strongName);
	}

}
