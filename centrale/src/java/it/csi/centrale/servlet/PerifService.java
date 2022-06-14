/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Marco Puccio
// Purpose of file: This class replies to Periferico questions
// Change log:
//   2008-07-29: initial version
// ----------------------------------------------------------------------------
// $Id: PerifService.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.servlet;

import it.csi.centrale.Centrale;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.db.InternalDbManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class PerifService extends HttpServlet {

	/**
	 * 
	 * This class replies to Periferico questions
	 * 
	 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
	 * 
	 */

	private static final long serialVersionUID = 5667540419740415432L;

	static Logger logger = Logger.getLogger("centrale."
			+ PerifService.class.getSimpleName());

	private static final int ERR_CODE_BAD_REQUEST = 1;

	private static final int ERR_CODE_FN_BAD_PARAMS = 2;

	private static final int ERR_CODE_FN_EXEC_FAIL = 3;

	private static final String MSG_INV_PARAM_FORMAT = "Invalid format for parameter ";

	private static final String MSG_PARAM_MISSING = "Missing requested parameter ";

	private static final String MSG_PERIF_NOT_INIT = "Periferico application not initialized";

	private static final String UPS_DIR = "log";

	private static final String UPS_FILE = "ups.csv";

	private static final long UPS_FILE_MAX_LENGTH_KB = 10 * 1024;

	private static final String UPS_DATE_FMT = "yyyy-MM-dd' 'HH:mm:ss";

	private final DateFormat requestDateFmt = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private Vector<Station> stationVect;

	public PerifService() {

		stationVect = Centrale.getInstance().getConfigInfo().getStVector();
	}

	/*
	 * public PerifService(Vector<Station> stVt){ this.stationVect = stVt; }
	 */

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String function = request.getParameter("function");
		if (function == null) {
			sendError(response, ERR_CODE_BAD_REQUEST, "No function specified");
			return;
		}
		try {
			if ("listStationConfigs".equals(function))
				listStationConfigs(request, response);
			else if ("listStationNames".equals(function))
				listStationNames(request, response);
			else if ("getStationConfig".equals(function))
				getStationConfig(request, response);
			else {
				sendError(response, ERR_CODE_BAD_REQUEST, "Unknown function");
				return;
			}
		} catch (ServiceException e) {
			sendError(response, ERR_CODE_BAD_REQUEST, e.getMessage());
		} catch (IllegalArgumentException e) {
			sendError(response, ERR_CODE_FN_BAD_PARAMS, e.getMessage());
		} catch (Exception e) {
			sendError(response, ERR_CODE_FN_EXEC_FAIL, e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BufferedReader br = request.getReader();
		// NOTE: using Jetty as application server, it is not possible to read
		// request parameters calling getParameter function and then obtaining
		// an InputStream or Reader to read request postdata: for this reason
		// function identifier is passed as the first line of request postadata
		String function = br.readLine();
		if (function == null) {
			sendError(response, ERR_CODE_BAD_REQUEST, "No function specified");
			return;
		}
		try {
			if ("setUpsStatus".equals(function.trim()))
				setUpsStatus(br, response);
			else {
				sendError(response, ERR_CODE_BAD_REQUEST, "Unknown function");
				return;
			}
		} catch (ServiceException e) {
			sendError(response, ERR_CODE_BAD_REQUEST, e.getMessage());
		} catch (IllegalArgumentException e) {
			sendError(response, ERR_CODE_FN_BAD_PARAMS, e.getMessage());
		} catch (Exception e) {
			sendError(response, ERR_CODE_FN_EXEC_FAIL, e.getMessage());
		}
	}

	private void listStationNames(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			ServiceException {

		PrintWriter pw = getPrintWriterFromResponse(response);
		pw.println("OK");
		if (!stationVect.isEmpty()) {
			int i;
			int size = 0;
			for (i = 0; i < stationVect.size(); i++) {
				if (stationVect.elementAt(i).getStInfo().getDeletionDate() == null)
					size++;
			}
			pw.println(size);
			for (i = 0; i < stationVect.size(); i++) {

				if (stationVect.elementAt(i).getStInfo().getDeletionDate() == null) {
					pw.println(stationVect.elementAt(i).getStInfo()
							.getShortStationName());
				}

			}

		}
		pw.close();
	}

	private void listStationConfigs(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			ServiceException {

		InternalDbManager inDbM = null;
		List<Date> listStConf;
		PrintWriter pw = getPrintWriterFromResponse(response);

		try {
			inDbM = new InternalDbManager();
			Integer stationPosition;
			String stName = parseString(request, "station", false);
			Date startDate = parseDate(request, "startDate", true);
			Date endDate = parseDate(request, "endDate", true);
			Integer limit = parseInt(request, "limit", true);

			if (stationVect == null) {
				sendError(response, ERR_CODE_FN_EXEC_FAIL, MSG_PERIF_NOT_INIT);

			} else {

				if ((stationPosition = searchStationPosition(stName)) != null) {
					logger.debug("listStationConfigs() find position "
							+ stationPosition + " for station name:" + stName);
					listStConf = inDbM.readConfigFileDateList(stationVect
							.elementAt(stationPosition).getStationId(),
							startDate, endDate, limit);

					pw.println("OK");

					Iterator<Date> iter;
					Date elementDate;
					iter = listStConf.iterator();
					pw.println(listStConf.size());
					while (iter.hasNext()) {
						elementDate = iter.next();
						pw.println(elementDate.getTime());
					}

				} else {
					sendError(response, ERR_CODE_FN_EXEC_FAIL,
							"station doesn't find");
				}

				pw.close();
				inDbM.disconnect();
			}
		} catch (ServiceException se) {
			if (se.getMessage().contains(MSG_PARAM_MISSING))
				sendError(response, ERR_CODE_FN_BAD_PARAMS, se.getMessage());
			else if (se.getMessage().contains(MSG_INV_PARAM_FORMAT))
				sendError(response, ERR_CODE_FN_BAD_PARAMS, se.getMessage());
			try {
				if (inDbM != null) {
					inDbM.disconnect();
				}
			} catch (SQLException sqle) {
				logger.error(
						"listStationConfigs() InternalDbManager exception",
						sqle);
			}
		} catch (SQLException sqle) {
			logger.error("listStationConfigs() InternalDbManager exception",
					sqle);
		} catch (ServletException serve) {
			logger.error("listStationConfigs() Servlet Exception ", serve);
		} catch (IOException ioe) {
			logger.error("listStationConfigs() IO Exception ", ioe);
		}

	}

	private void setUpsStatus(BufferedReader br, HttpServletResponse response)
			throws ServletException, IOException, ServiceException {
		String stationName = br.readLine();
		if (stationName == null || (stationName = stationName.trim()).isEmpty())
			throw new IllegalArgumentException("Station name missing");
		if (stationName.length() > 64)
			throw new IllegalArgumentException(
					"Station name length exceeding 64 characters");
		String stationId = br.readLine();
		if (stationId == null || (stationId = stationId.trim()).isEmpty())
			throw new IllegalArgumentException("Station ID missing");
		if (stationId.length() > 64)
			throw new IllegalArgumentException(
					"Station ID length exceeding 64 characters");
		String upsId = br.readLine();
		if (upsId == null || (upsId = upsId.trim()).isEmpty())
			throw new IllegalArgumentException("UPS event ID missing");
		if (upsId.length() > 64)
			throw new IllegalArgumentException(
					"UPS event ID length exceeding 64 characters");
		String timestampStr = br.readLine();
		if (timestampStr == null
				|| (timestampStr = timestampStr.trim()).isEmpty())
			throw new IllegalArgumentException("Timestamp missing");
		if (timestampStr.length() > 64)
			throw new IllegalArgumentException(
					"Timestamp length exceeding 64 characters");
		UUID stationUUID;
		try {
			if ("NULL".equalsIgnoreCase(stationId))
				stationUUID = null;
			else
				stationUUID = UUID.fromString(stationId);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Station ID parsing error", e);
		}
		Date timestamp;
		DateFormat df = new SimpleDateFormat(UPS_DATE_FMT);
		try {
			timestamp = df.parse(timestampStr);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Timestamp parsing error", e);
		}
		logger.debug("UPS event: station='" + stationName + "', stationID="
				+ stationUUID + ", event=" + upsId + ", timestamp="
				+ df.format(timestamp));
		appendUpsStatus(timestamp, stationName, stationUUID, upsId);
	}

	private Integer searchStationPosition(String stationName) {

		int cursor = 0;
		Integer retPosition = null;

		while ((cursor < this.stationVect.size()) && (retPosition == null)) {
			if (this.stationVect.elementAt(cursor).getStInfo()
					.getShortStationName().equalsIgnoreCase(stationName)) {
				retPosition = cursor;
				logger.debug("searchStationId() searched station" + stationName
						+ " was found with id:"
						+ stationVect.elementAt(retPosition).getStationId());
			}
			cursor++;
		}

		return retPosition;
	}

	private void getStationConfig(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			ServiceException {

		InternalDbManager inDbM = null;
		StringBuilder configFile;
		PrintWriter pw = null;

		String xml = parseString(request, "xml", true);
		if (xml != null) {
			logger.info("Preparing XML file...");
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=station.xml");
			pw = new PrintWriter(new OutputStreamWriter(
					response.getOutputStream(), "UTF-8"), true);
		} else
			pw = getPrintWriterFromResponse(response);

		try {
			inDbM = new InternalDbManager();
			Integer stationPosition;
			StringReader configReader;
			BufferedReader bufReader;
			String stName = parseString(request, "station", false);
			Long configName = parseLong(request, "config", false);

			if (stationVect == null) {
				sendError(response, ERR_CODE_FN_EXEC_FAIL, MSG_PERIF_NOT_INIT);

			} else {

				if ((stationPosition = searchStationPosition(stName)) != null) {
					inDbM = new InternalDbManager();
					logger.debug("getStationConfig() find position "
							+ stationPosition + " for station name:" + stName
							+ " configName:" + configName);
					configFile = inDbM.readConfigFile(
							stationVect.elementAt(stationPosition)
									.getStationId(), configName);

					if (configFile != null) {

						String lineToSend;
						configReader = new StringReader(configFile.toString());
						bufReader = new BufferedReader(configReader);
						if (xml == null)
							pw.println("OK");

						while ((lineToSend = bufReader.readLine()) != null) {
							pw.println(lineToSend);
						}
					} else {
						inDbM.disconnect();
						logger.debug("getStationConfig" + configFile);
						throw new SQLException();
					}

				} else {
					sendError(response, ERR_CODE_FN_EXEC_FAIL,
							"station doesn't find");
				}

				pw.close();
				inDbM.disconnect();
				if (xml != null)
					logger.debug("Done.");
			}
		} catch (ServiceException se) {
			if (se.getMessage().contains(MSG_PARAM_MISSING))
				sendError(response, ERR_CODE_FN_BAD_PARAMS, se.getMessage());
			else if (se.getMessage().contains(MSG_INV_PARAM_FORMAT))
				sendError(response, ERR_CODE_FN_BAD_PARAMS, se.getMessage());
			try {
				if (inDbM != null) {
					inDbM.disconnect();
				}
			} catch (SQLException sqle) {
				logger.error("getStationConfig() InternalDbManager exception",
						sqle);
			}
		} catch (SQLException sqle) {
			sendError(response, ERR_CODE_FN_EXEC_FAIL, "config file is blank");
			logger.error("getStationConfig() InternalDbManager exception", sqle);
		} catch (ServletException serve) {
			logger.error("getStationConfig() Servlet Exception ", serve);
		} catch (IOException ioe) {
			logger.error("getStationConfig() IO Exception ", ioe);
		}

	}

	private PrintWriter getPrintWriterFromResponse(HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		return new PrintWriter(new OutputStreamWriter(
				response.getOutputStream(), "UTF-8"), true);
	}

	private String parseString(HttpServletRequest request, String paramName,
			boolean optional) throws ServletException, IOException,
			ServiceException {
		String paramValue = request.getParameter(paramName);
		if (paramValue == null) {
			if (optional)
				return null;
			throw new ServiceException(MSG_PARAM_MISSING + paramName);
		}
		return paramValue;
	}

	private Integer parseInt(HttpServletRequest request, String paramName,
			boolean optional) throws ServletException, IOException,
			ServiceException {
		String paramValue = request.getParameter(paramName);
		if (paramValue == null) {
			if (optional)
				return null;
			throw new ServiceException(MSG_PARAM_MISSING + paramName);
		}
		try {
			return Integer.parseInt(paramValue);
		} catch (NumberFormatException e) {
			throw new ServiceException(MSG_INV_PARAM_FORMAT + paramName);
		}
	}

	private Long parseLong(HttpServletRequest request, String paramName,
			boolean optional) throws ServletException, IOException,
			ServiceException {
		String paramValue = request.getParameter(paramName);
		if (paramValue == null) {
			if (optional)
				return null;
			throw new ServiceException(MSG_PARAM_MISSING + paramName);
		}
		try {
			return Long.parseLong(paramValue);
		} catch (NumberFormatException e) {
			throw new ServiceException(MSG_INV_PARAM_FORMAT + paramName);
		}
	}

	private Date parseDate(HttpServletRequest request, String paramName,
			boolean optional) throws ServletException, IOException,
			ServiceException {
		String paramValue = request.getParameter(paramName);
		if (paramValue == null) {
			if (optional)
				return null;
			throw new ServiceException(MSG_PARAM_MISSING + paramName);
		}
		try {
			return requestDateFmt.parse(paramValue);
		} catch (ParseException e) {
			throw new ServiceException(MSG_INV_PARAM_FORMAT + paramName);
		}
	}

	private void sendError(HttpServletResponse response, int code,
			String message) throws ServletException, IOException {
		PrintWriter pw = getPrintWriterFromResponse(response);
		pw.println("ERROR " + code);
		pw.println(message);
		pw.close();
	}

	void appendUpsStatus(Date timestamp, String stationName, UUID stationId,
			String upsEvent) {
		File statusDir = new File(UPS_DIR);
		File statusFile = new File(UPS_DIR + File.separator + UPS_FILE);
		PrintWriter pw = null;
		try {
			if (!statusDir.exists())
				if (!statusDir.mkdirs()) {
					logger.error("Cannot create folder '" + statusDir
							+ "' for UPS status file");
					return;
				}
			if (!statusDir.isDirectory()) {
				logger.error("The path '" + statusDir
						+ "' for UPS status folder is not a folder");
				return;
			}
			if (!statusDir.canWrite()) {
				logger.error("Cannot write to UPS status folder '" + statusDir
						+ "'");
				return;
			}
			if (statusFile.exists() && !statusFile.canWrite()) {
				logger.error("Cannot write to UPS status file '" + statusFile
						+ "'");
				return;
			}
			if (statusFile.length() > UPS_FILE_MAX_LENGTH_KB * 1024) {
				logger.error("UPS status file '" + statusFile
						+ "' exceeds maximum size, write disabled");
				return;
			}
			DateFormat df = new SimpleDateFormat(UPS_DATE_FMT);
			pw = new PrintWriter(new FileWriter(statusFile, true));
			pw.println(df.format(timestamp) + ", " + stationName + ", "
					+ stationId + ", " + upsEvent);
		} catch (Exception e) {
			logger.error("Error writing UPS status file '" + statusFile + "'",
					e);
			return;
		} finally {
			if (pw != null)
				pw.close();
		}
	}
}
