/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Silvia Vergnano
// Purpose of file: thread for writing data to Internal Db
// Change log:
//   2008-12-29: initial version
// ----------------------------------------------------------------------------
// $Id: DataWriterThread.java,v 1.22 2014/09/22 10:57:52 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.db;

import it.csi.centrale.Centrale;
import it.csi.centrale.data.station.AlarmData;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.GenericData;
import it.csi.centrale.data.station.GpsData;
import it.csi.centrale.data.station.InsertableObject;
import it.csi.centrale.data.station.StationStatus;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Thread for writing data to Internal Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class DataWriterThread extends Thread {

	// Define a static logger variable
	private static Logger logger = Logger.getLogger("centrale."
			+ DataWriterThread.class.getSimpleName());
	private static final String THREAD_BASE_NAME = "dataWriter";
	private static int idCounter = 0;

	private static int LOOP_TIME = 10000;

	private InternalDbManager dbManager;

	private boolean continueRun = true;

	public DataWriterThread() {
		super(THREAD_BASE_NAME + "-" + ++idCounter);
	}

	public void run() {
		try {
			dbManager = new InternalDbManager();
		} catch (SQLException e) {
			logger.error("Error creating Internal DbManager ", e);
			continueRun = false;
		}
		int timeoutForDbConn = 0;
		while (continueRun) {
			InsertableObject insertableObj = Centrale.getInstance()
					.getDataQueue().poll();
			if (insertableObj != null)
				logger.debug("Writing data for " + insertableObj + "...");
			/*
			 * logger.debug("while continueRun insertableObj value:" +
			 * insertableObj + " timeout value:" + timeoutForDbConn);
			 */
			// insert data in DB
			String errMSg = null;
			if (insertableObj != null) {
				timeoutForDbConn = 0;
				try {
					if (!dbManager.isConnected())
						dbManager.connect();
				} catch (SQLException e) {
					logger.error("Error connecting dbManager ", e);
					continueRun = false;
				}
				LinkedList<Object> dataList = insertableObj.getDataList();
				Integer[] key = insertableObj.getKey();
				if (dataList != null && dataList.size() > 0) {
					Object firstObj = dataList.get(0);
					if (firstObj instanceof GenericData) {
						if (key[0] != null && key[1] != null) {
							// case of means data
							errMSg = dbManager.writeMeansData(insertableObj);
						} else if (key[0] != null) {
							// case of sample data
							errMSg = dbManager.writeSampleData(insertableObj);
						}
						// TODO: capire se bisogna gestire caso errato con
						// numero di chiavi diverso anche negli else if seguenti
						// case of station alarm data
					} else if (firstObj instanceof AlarmData) {
						if (key[0] != null)
							errMSg = dbManager
									.writeContainerAlarmHistory(insertableObj);
					}
					// case of station alarm data
					else if (firstObj instanceof AnalyzerEventDatum) {
						if (key[0] != null)
							errMSg = dbManager
									.writeAnalyzerStatusHistory(insertableObj);
					}
					// case of allDownloaded element_avg_period or case
					// of allDownloaded gpsData
					else if (firstObj instanceof Date) {
						if ((key[0] != null && key[1] != null)
								|| (key[0] != null && key[1] == null))
							// TODO: gestire oggetto Date per sviluppo parte
							// piu' avanzata
							Centrale.getInstance().getMatchedDbQueue().offer(
									insertableObj);
					}
					// case of gps data
					else if (firstObj instanceof GpsData) {
						if (key[0] != null)
							errMSg = dbManager.writeGpsData(insertableObj);
					}
					// case of informatic status history
					else if (firstObj instanceof StationStatus) {
						if (key[0] != null)
							errMSg = dbManager
									.writeInformaticStatusHistory(insertableObj);
					}
				}
				// case of allDownloaded element_avg_period for deleted
				else if (dataList == null) {
					if (key[0] != null && key[1] != null)
						errMSg = dbManager.updateAllDataDownloaded(key[0]
								.intValue(), key[1].intValue(), true);
				}

				if (errMSg != null) {
					logger.error("Error Message is: " + errMSg);
					Centrale.getInstance().stopVerifyInternalDb(
							"DataWriterThread " + "in run after errMSg:"
									+ errMSg);
				}
			} else {
				if (timeoutForDbConn >= 6) {
					try {
						if (dbManager.isConnected())
							dbManager.disconnect();
					} catch (SQLException e1) {
						logger.error("Error disconnecting dbManager ", e1);
						continueRun = false;
					}
					timeoutForDbConn = 0;
				} else
					timeoutForDbConn++;
				try {
					Thread.sleep(LOOP_TIME);
				} catch (InterruptedException e) {
					logger.error("Error on thread loop time ", e);
				}
			}
		}
		if (dbManager != null) {
			try {
				if (dbManager.isConnected())
					dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error on diconnecting dbManager ", e1);
			}
		}
	}

	public synchronized void setContinueRun(boolean continueRun) {
		this.continueRun = continueRun;
	}
}
