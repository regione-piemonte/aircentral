/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for definition of asynchronous server functions for connecting
 * to Periferico application
 * Change log:
 *   2008-10-27: initial version
 * ----------------------------------------------------------------------------
 * $Id: ConnPerifServiceAsync.java,v 1.12 2014/05/30 10:03:42 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.connperif.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class for definition of asynchronous server functions for connecting to
 * Periferico application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public interface ConnPerifServiceAsync {

	void setLocale(String locale, AsyncCallback<Object> callback);

	void stopStationUi(int proxyId, AsyncCallback<Object> callback);

	void getLocalIPAddress(AsyncCallback<String> callback);

	void makeConnection(int stationId, AsyncCallback<Integer> callback);

	void isConnectionAlive(int proxyId, AsyncCallback<Object> callback);

}
