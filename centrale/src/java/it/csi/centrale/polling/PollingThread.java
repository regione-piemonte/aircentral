/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Thread for managing polling
// Change log:
//   2014-03-31: initial version
// ----------------------------------------------------------------------------
// $Id: PollingThread.java,v 1.8 2014/09/25 14:07:00 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import it.csi.centrale.Centrale;
import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.db.InternalDbManager;

import java.io.IOException;

import org.apache.log4j.Logger;
/**
 * Thread for managing polling
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class PollingThread implements Runnable {

	private static final String THREAD_BASE_NAME = "polling";
	private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;
	private static int idCounter = 0;

	private final int id = ++idCounter;
	private final String THREAD_NAME = THREAD_BASE_NAME + "-" + id;
	private Logger logger = Logger.getLogger("centrale."
			+ getClass().getSimpleName() + "-" + id);
	private volatile Pollable pollable;
	private volatile Connection connection;
	private volatile Thread pollingThread = null;

	PollingThread(Pollable pollable, Connection connection) {
		if (pollable == null)
			throw new IllegalArgumentException("Pollable should not be null");
		this.pollable = pollable;
		if (connection == null)
			throw new IllegalArgumentException("Connection should not be null");
		this.connection = connection;
	}

	Pollable getPollable() {
		return pollable;
	}

	void start() {
		pollingThread = new Thread(this, THREAD_NAME);
		pollingThread.setDaemon(false);
		pollingThread.setPriority(THREAD_PRIORITY);
		pollingThread.start();
	}

	void stop() {
		Thread tmpThread = pollingThread;
		pollingThread = null;
		if (tmpThread != null) {
			logger.debug("Polling thread shutdown in progress...");
			try {
				int maxWaitTime_s = 60;
				logger.debug("Waiting up to " + maxWaitTime_s
						+ "s for polling to terminate...");
				tmpThread.join(maxWaitTime_s * 1000);
				logger.debug("Finished waiting for polling to terminate");
			} catch (InterruptedException ie) {
				logger.error("Wait for polling to terminate interrupted");
			}
		}
	}

	boolean isRunning() {
		return pollingThread != null;
	}

	public void run() {
		InternalDbManager dbManager = null;
		try {
			logger.debug("Polling thread started for " + pollable.getName());
			try {
				logger.debug("Opening connection to internal data base");
				dbManager = new InternalDbManager();
			} catch (Exception e) {
				logger.error("Connection to internal data base failed", e);
				pollable.setPollingStatus(PollingStatus.POLLING_ERROR);
				return;
			}
			try {
				logger.info("Connecting to " + pollable.getName() + "...");
				pollable.setPollingStatus(PollingStatus.CONNECTING);
				connection.start();
				pollable.setConnectionFailure(null);
			} catch (ConnectionException e) {
				logger.warn("Cannot connect to " + pollable.getName() + ": "
						+ e.getMessage());
				pollable.setPollingStatus(PollingStatus.CONNECT_ERROR);
				pollable.setConnectionFailure(e.getFailure());
				logger.debug(pollable.getConsecutiveConnectionFailuresCount()
						+ " consecutive connection failures for "
						+ pollable.getName());
				return;
			} catch (Exception e) {
				logger.error("Execution of connection start failed", e);
				pollable.setPollingStatus(PollingStatus.POLLING_ERROR);
				return;
			}
			try {
				logger.debug("Polling started for " + pollable.getName()
						+ "...");
				pollable.setPollingStatus(PollingStatus.RUNNING);
				Polling polling = Polling.getInstance(connection, pollable,
						dbManager);
				polling.execute();
				pollable.setPollingStatus(PollingStatus.OK);
				logger.info("Polling completed successfully");
			} catch (NoReplyException e) {
				pollable.setPollingStatus(PollingStatus.SOFTWARE_NOT_RESPONDING);
				logger.warn("Periferico software not responding", e);
			} catch (IOException e) {
				pollable.setPollingStatus(PollingStatus.IO_ERROR);
				// TODO: valutare test della connessione ed eventuale ripresa
				// del polling
				logger.warn("Connection dropped during polling", e);
			} catch (ProtocolException e) {
				pollable.setPollingStatus(PollingStatus.PROTOCOL_ERROR);
				logger.error("Polling failed for protocol error", e);
			} catch (DbException e) {
				pollable.setPollingStatus(PollingStatus.POLLING_ERROR);
				logger.error("Polling failed for internal DB error", e);
				Centrale.getInstance().stopVerifyInternalDb(
						"Internal DB error: " + e);
			} catch (PollingException e) {
				pollable.setPollingStatus(PollingStatus.POLLING_ERROR);
				logger.error("Polling failed for internal error", e);
			} catch (Exception e) {
				pollable.setPollingStatus(PollingStatus.UNEXPECTED_ERROR);
				logger.error("Polling failed for unexpected error", e);
			}
		} finally {
			connection.release();
			if (dbManager != null)
				try {
					dbManager.disconnect();
				} catch (Exception e) {
					logger.error("Disconnection from internal data base "
							+ "failed", e);
				}
			pollingThread = null;
			logger.debug("Polling thread stopped for " + pollable.getName());
		}
	}
}
