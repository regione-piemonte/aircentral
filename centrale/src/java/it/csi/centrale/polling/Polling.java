/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: 
// Change log:
//   2014-04-09: initial version
// ----------------------------------------------------------------------------
// $Id: Polling.java,v 1.230 2014/08/29 11:09:09 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.db.InternalDbManager;

import java.io.IOException;
/**
 * Exception for database
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
abstract class Polling {

	private Connection connection;
	private InternalDbManager dbManager;

	static Polling getInstance(Connection connection, Pollable pollable,
			InternalDbManager dbManager) {
		if (connection == null)
			throw new IllegalArgumentException("Connection should not be null");
		if (pollable == null)
			throw new IllegalArgumentException("Pollable should not be null");
		if (dbManager == null)
			throw new IllegalArgumentException("DB manager should not be null");
		Polling polling;
		if (pollable instanceof Station)
			polling = new StationPolling((Station) pollable);
		else
			throw new IllegalArgumentException("Unsupported Pollable class '"
					+ pollable.getClass().getSimpleName() + "'");
		polling.connection = connection;
		polling.dbManager = dbManager;
		return polling;
	}

	protected Connection getConn() {
		return connection;
	}

	protected InternalDbManager getDBM() {
		return dbManager;
	}

	abstract void execute() throws NoReplyException, IOException,
			ProtocolException, PollingException;

}
