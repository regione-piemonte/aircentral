/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Silvia Vergnano
// Purpose of file: thread for checking Internal Db
// Change log:
//   2009-05-04: initial version
// ----------------------------------------------------------------------------
// $Id: CheckDb.java,v 1.5 2014/09/22 10:57:52 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.db;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleException;

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Thread for checking Internal Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class CheckDb extends Thread {

	// Define a static logger variable
	private static Logger logger = Logger.getLogger("centrale."
			+ CheckDb.class.getSimpleName());

	private static final String THREAD_BASE_NAME = "checkDB";
	private static int idCounter = 0;

	private static int LOOP_TIME = 10000;

	private boolean continueRun = true;

	public CheckDb() {
		super(THREAD_BASE_NAME + "-" + ++idCounter);
	}

	public void run() {
		logger.info("Start run checkDb...");
		while (continueRun) {
			try {
				InternalDbManager dbManager = new InternalDbManager();
				logger.debug("During run dbManager:" + dbManager);
				// logger.debug("e- connesso " + dbManager.isConnectionUp());
				if (dbManager != null && dbManager.isConnectionUp()) {
					// case of exiting from checkDb thread
					logger.debug("Case of exiting from checkDb thread and "
							+ "restarts centrale threads");
					continueRun = false;
					dbManager.disconnect();
					Centrale.getInstance().restartThreads();
				}
			} catch (SQLException e) {
				logger.error("", e);
				try {
					logger.debug("Now sleep for " + LOOP_TIME + "ms...");
					Thread.sleep(LOOP_TIME);
				} catch (InterruptedException ie) {
					logger.error("", ie);
				}
			} catch (CentraleException e) {
				logger.error("", e);
				// case of stop centrale application
				continueRun = false;
				System.exit(1);
			}
		}
	}

}
