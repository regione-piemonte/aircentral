/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Interface to Mosshe
// Change log:
//   2014-09-16: initial version
// ----------------------------------------------------------------------------
// $Id: MossheInterface.java,v 1.5 2015/01/08 16:29:38 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.csi.centrale.Centrale;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.data.station.StationStatus;

/**
 * Interface to Mosshe
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class MossheInterface {

	private static Logger logger = Logger.getLogger("centrale." + MossheInterface.class.getSimpleName());

	private static final String STATUS_DIR = "log";
	private static final String STATUS_FILE = "mosshe.txt";
	private static final String MOSSHE_DATE_FMT = "yyyy-MM-dd' 'HH:mm:ss";

	private String statusDirName;
	private String statusFileName;

	MossheInterface() {
		this(STATUS_DIR, STATUS_FILE);
	}

	MossheInterface(String statusDirName, String statusFileName) {
		this.statusDirName = statusDirName;
		this.statusFileName = statusFileName;
	}

	boolean createStatusFile(Vector<Station> stationVect) {
		File statusDir = null;
		File statusFile = null;
		PrintWriter pw = null;
		try {
			statusDir = new File(statusDirName);
			statusFile = new File(statusDir + File.separator + statusFileName);
			if (!statusDir.exists())
				if (!statusDir.mkdirs()) {
					logger.error("Cannot create folder '" + statusDir + "' for Mosshe status file");
					return false;
				}
			if (!statusDir.isDirectory()) {
				logger.error("The path '" + statusDir + "' for Mosshe status folder is not a folder");
				return false;
			}
			if (!statusDir.canWrite()) {
				logger.error("Cannot write to Mosshe status folder '" + statusDir + "'");
				return false;
			}
			if (statusFile.exists() && !statusFile.canWrite()) {
				logger.error("Cannot write to Mosshe status file '" + statusFile + "'");
				return false;
			}
			DateFormat dfDates = new SimpleDateFormat(MOSSHE_DATE_FMT);

			pw = new PrintWriter(new FileWriter(statusFile));
			pw.println("# " + Centrale.getInstance().getName());
			pw.println("# Data aggiornamento: " + dfDates.format(new Date()));
			pw.println("# Nome stazione ; Data polling       ;DIS;COM;SWR;SWS;CLK;DOR;CAB;TEMP");
			for (Station stz : stationVect) {
				StationInfo stInfo = stz.getStInfo();
				if (stInfo == null || stInfo.getDeletionDate() != null)
					continue;
				StationStatus stStatus = stz.getStStatus();

				char conn;
				char swr;
				switch (stz.getPollingStatus()) {
				case IO_ERROR:
					conn = '1';
					swr = '0';
					break;
				case CONNECT_ERROR:
					conn = '2';
					swr = '0';
					break;
				case SOFTWARE_NOT_RESPONDING:
				case PROTOCOL_ERROR:
				case POLLING_ERROR:
				case UNEXPECTED_ERROR:
					conn = '0';
					swr = '1';
					break;
				default:
					conn = '0';
					swr = '0';
					break;
				}

				char sws;
				Integer is = stStatus.getInformaticStatus();
				if (is == null || is == StationStatus.INFORMATIC_STATUS_OK)
					sws = '0';
				else if (is == StationStatus.INFORMATIC_STATUS_WARNING)
					sws = '1';
				else
					sws = '2';

				char cab = Boolean.TRUE.equals(stStatus.isStationAlarm())
						|| Boolean.TRUE.equals(stStatus.isInstrumentInAlarm()) ? '1' : '0';

				char clk;
				switch (stStatus.getSync()) {
				case StationStatus.CLKUNKNOWN:
				case StationStatus.CLKOK:
					clk = '0';
					break;
				case StationStatus.CLKNOTSYNCBUTOK:
					clk = '1';
					break;
				case StationStatus.CLKNOTOK:
				default:
					clk = '2';
				}

				Date lastPollingDate = stz.getLastSuccessfulPollingDate();
				String date = pad(lastPollingDate == null ? "" : dfDates.format(lastPollingDate), 19);

				String temperature = pad(stStatus.getTemperature(), 3);

				char door = Boolean.TRUE.equals(stStatus.isDoorOpen()) ? '1' : '0';

				String line = pad(stInfo.getShortStationName(), 16) + "; " + date + ";  "
						+ (stInfo.isEnabled() ? '0' : '1') + ";  " + conn + ";  " + swr + ";  " + sws + ";  " + clk
						+ ";  " + door + ";  " + cab + "; " + temperature;
				pw.println(line);
			}
			logger.info("Mosshe status file '" + statusFile + "' successfully written");
			return true;
		} catch (Exception e) {
			logger.error("Error writing Mosshe status file '" + statusFile + "'", e);
			return false;
		} finally {
			if (pw != null)
				pw.close();
		}

	}

	private String pad(Object object, int padlen) {
		String str = object == null ? "" : object.toString();
		if (padlen < 0)
			padlen = 0;
		int len = padlen - str.length();
		if (len <= 0)
			return str.substring(0, padlen);
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < len; i++)
			sb.append(' ');
		return sb.toString();
	}

}
