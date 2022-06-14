/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for definition of server functions for connecting
 * to Periferico application 
 * Change log:
 *   2008-10-27: initial version
 * ----------------------------------------------------------------------------
 * $Id: ConnPerifService.java,v 1.10 2014/05/30 10:03:42 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.connperif.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Class for definition of server functions for connecting to Periferico
 * application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public interface ConnPerifService extends RemoteService {

	void setLocale(String locale);

	void stopStationUi(int proxyId) throws ConnPerifException;

	String getLocalIPAddress();

	int makeConnection(int stationId) throws ConnPerifException;

	void isConnectionAlive(int proxyId) throws ConnPerifException;
}
