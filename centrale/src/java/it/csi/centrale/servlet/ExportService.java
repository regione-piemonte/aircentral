/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Isabella Vespa
// Purpose of file: This class provides methods to create and download csv file.
// Change log:
//   2009-02-21: initial version
// ----------------------------------------------------------------------------
// $Id: ExportService.java,v 1.17 2015/10/15 12:10:24 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.servlet;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleException;
import it.csi.centrale.PropertyUtil;
import it.csi.centrale.chart.ChartGenerator;
import it.csi.centrale.config.ConfigManager;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.db.InternalDbManager;
import it.csi.centrale.stationreport.StationConfigHolder;
import it.csi.centrale.stationreport.StationReport;
import it.csi.centrale.ui.client.FatalException;
import it.csi.centrale.ui.client.UserParamsException;
import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.centrale.ui.server.ValidationFlag;
import it.csi.periferico.config.common.CommonCfg;
import it.csi.periferico.config.common.ConfigException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;

/**
 * This class provides methods to create and download csv file.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 */

public class ExportService extends HttpServlet {

	private static final long serialVersionUID = -5141034919598474780L;

	private static final String FIELD_SEPARATOR = ",";

	private static final String DECIMAL_SEPARATOR = ".";

	private static final int SAMPLE = 1;

	private static final int MEANS = 2;

	private static final String CSV = "csv";

	private static final String TABLE = "table";

	private static final String CHART = "chart";

	private static final int CSV_LIMIT = 65535;

	private PropertyUtil propertyUtil;

	private String locale = null;

	private String analyzerName;

	private String stationName;

	private String parameterName;

	private String dataType;

	private String measureUnit;

	private String fieldSeparator;

	private String decimalSeparator;

	private String avgPeriod;

	private String elementIdStr;

	private int type;

	private String mode = null;

	private boolean isWind = false;

	private int numDec;

	private int dirNumDec;

	private List<DataObject> dataList;

	private DecimalFormat df = new DecimalFormat();

	private SimpleDateFormat sdfTimestamp = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	private String exportDate;

	// Define a static logger variable
	private static Logger logger = Logger.getLogger("exportservice."
			+ Centrale.class.getSimpleName());

	public ExportService() {
		try {
			propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		} catch (IOException e) {
			propertyUtil = null;
		}
	}// end constructor

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			String function = request.getParameter("function");
			if (function == null)
				throw new CentraleException("no_function_specified");
			locale = request.getParameter("locale");
			if ("getSampleData".equals(function))
				type = SAMPLE;
			else if ("getMeansData".equals(function))
				type = MEANS;
			else if ("getCommonConfig".equals(function)) {
				// get common config
				getCommonConfig(request, response);
			} else if ("getStationReport".equals(function)) {
				getStationReport(request, response);
			} else
				throw new CentraleException("unknown_function");
			getData(request, response);
		} catch (CentraleException pEx) {
			sendError(response, pEx.getLocalizedMessage(locale));
		} catch (IllegalArgumentException e) {
			sendError(response, e.getMessage());
		} catch (Exception e) {
			sendError(response, e.getMessage());
			logger.error("", e);
		}
	}// end doGet

	private void getCommonConfig(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, ConfigException,
			MarshalException, ValidationException, IOException,
			MappingException {
		InternalDbManager internalDbManager = new InternalDbManager();
		CommonCfg commonCfg = internalDbManager.getCommonConfig();

		ConfigManager configMng = new ConfigManager();

		String xml = configMng.writeCommonCfg(commonCfg);

		logger.info("Preparing XML file...");
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		String filename = "common_config.xml";
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				response.getOutputStream(), "UTF-8"), true);
		pw.println(xml);
		pw.close();
		logger.info("Done.");

	}// end getCommonConfig

	private void getStationReport(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, ConfigException,
			MarshalException, ValidationException, IOException,
			MappingException {
		logger.info("Building station configurations report...");
		InternalDbManager internalDbManager = new InternalDbManager();
		List<StationConfigHolder> configs = internalDbManager
				.readActiveConfigurations();
		StationReport stationReport = new StationReport(configs);
		List<String[]> reportLines = stationReport.makeFullReport();
		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");
		String strDate = new SimpleDateFormat("yyyyMMdd_HHmm")
				.format(new Date());
		String filename = "configuration_report_" + strDate + ".csv";
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				response.getOutputStream(), "UTF-8"), true);
		for (String[] line : reportLines) {
			for (int i = 0; i < line.length - 1; i++) {
				pw.print(line[i].replace(",", " "));
				pw.print(", ");
			}
			pw.println(line[line.length - 1].replace(",", " "));
		}
		pw.close();
		logger.info("Report completed");
	}

	private void getData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		readParameter(request);

		String startDateStr = request.getParameter("startDate");
		if (startDateStr == null
				|| (mode == null && elementIdStr == null)
				|| (mode != null )) {
			throw new CentraleException("missing_parameter");
		}

		Date startDate = prepareDate(startDateStr,
				request.getParameter("startHour"), true);
		String endDateStr = request.getParameter("endDate");
		Date endDate = prepareDate((endDateStr != null ? endDateStr
				: startDateStr), request.getParameter("endHour"), false);

		String isWindReq = request.getParameter("iswind");
		if (isWindReq.equals(GenericElement.WIND))
			isWind = true;
		else
			isWind = false;

		if (startDate.after(endDate))
			throw new CentraleException("start_after_end");

		String numDecStr = request.getParameter("numDec");
		if (numDecStr != null && numDecStr.trim().length() > 0)
			numDec = new Integer(numDecStr).intValue();
		String dirNumDecStr = request.getParameter("dirNumDec");
		if (dirNumDecStr != null && dirNumDecStr.trim().length() > 0)
			dirNumDec = new Integer(dirNumDecStr).intValue();

		Integer limit = new Integer(CSV_LIMIT);

		dataList = new ArrayList<DataObject>();

		// connect to db
		InternalDbManager dbManager = null;
		Boolean overLimit = null;
		StringBuffer dbError = new StringBuffer();
		try {
			dbManager = new InternalDbManager();

			if (mode != null && mode.equals(CHART) && isWind)
				sendError(response, localize("not_available"));
			else {
				logger.info("Reading data for " + type + ": start date: "
						+ startDate + " and end date: " + endDate + "...");
				if (type == SAMPLE) {
					dataType = localize("sample");
					overLimit = dbManager.readSampleData(new Integer(
							elementIdStr).intValue(), startDate, endDate,
							dataList, limit);
					for (int i = 0; i < dataList.size(); i++) {
						DataObject dObj = dataList.get(i);
						try {
							dObj.setTitle(ValidationFlag.getMultipleFlagsTitle(
									dObj.getMultipleFlag(), locale));
						} catch (IOException e) {
							logger.error("", e);
						}
						dObj.setNumDec(numDec);
					}
				}
				if (type == MEANS) {
					dataType = localize("means");
					if (isWind) {
						overLimit = dbManager.readMeansData(new Integer(
								elementIdStr).intValue(),
								new Integer(avgPeriod).intValue(), startDate,
								endDate, dataList, limit, GenericElement.WIND);
						for (int i = 0; i < dataList.size(); i++) {
							DataObject dObj = dataList.get(i);
							dObj.setNumDec(numDec);
							((WindDataObject) dObj).setDirNumDec(dirNumDec);
						}
					} else {
						overLimit = dbManager
								.readMeansData(
										new Integer(elementIdStr).intValue(),
										new Integer(avgPeriod).intValue(),
										startDate, endDate, dataList, limit,
										GenericElement.SCALAR);
						for (int i = 0; i < dataList.size(); i++) {
							DataObject dObj = dataList.get(i);
							dObj.setNumDec(numDec);
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error("", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}
		logger.info("Done.");
		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		}// end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());

		if (overLimit)
			sendError(response, "over_limit");
		else {
			if (mode == null || mode.equals(CSV)) {
				// prepare csv file
				prepareCsv(response, dataList);
			} else if (mode.equals(TABLE)) {
				prepareTable(response, dataList);

			} else if (mode.equals(CHART)) {
				// prepare chart
				prepareChart(response, dataList);

			}
		}// end else

	}// end getData

	private Date prepareDate(String dateStr, String hour, boolean startDate)
			throws CentraleException {

		String dateHour = null;
		if (startDate)
			dateHour = dateStr + " " + (hour == null ? "00:00" : hour) + ":00";
		else
			dateHour = dateStr + " " + (hour == null ? "23:59" : hour) + ":59";

		// create startDate and endDate

		Date date = null;
		try {
			date = sdfTimestamp.parse(dateHour);

		} catch (ParseException pEx) {
			String msg = "";
			if (startDate)
				msg = "start_date_error";
			else
				msg = "end_date_error";
			throw new CentraleException(msg);
		}
		return date;
	}

	private void prepareChart(HttpServletResponse response,
			List<DataObject> dataList) throws UserParamsException, IOException {
		logger.info("Preparing chart...");
		ChartGenerator chartGenerator = new ChartGenerator(locale);
		chartGenerator.prepareParameter(dataList, elementIdStr, false);

		JFreeChart chart = chartGenerator.generateChart(stationName
				+ (analyzerName != null ? " - " + analyzerName : ""),
				parameterName, measureUnit, true, type);
		// Rendering del grafico in PNG
		if (chart != null) {
			response.setContentType("image/png");
			OutputStream out = response.getOutputStream();

			ChartRenderingInfo lInfo = new ChartRenderingInfo(
					new StandardEntityCollection());
			ChartUtilities.writeChartAsPNG(out, chart, 640, 480, lInfo);

			out.flush();
			out.close();
		}
		logger.info("Done.");
	}

	private void prepareTable(HttpServletResponse response,
			List<DataObject> dataList) throws UnsupportedEncodingException,
			IOException {
		logger.info("Preparing table...");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		exportDate = sdfTimestamp.format(new Date());
		response.setHeader("Content_Type", "text/html");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				response.getOutputStream(), "UTF-8"), true);
		pw.println("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
		pw.println("<html>");

		pw.println("<head>");
		pw.println("<title>" + stationName
				+ (analyzerName != null ? " - " + analyzerName : "") + " - "
				+ parameterName + "</title>");
		pw.println(" <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
		pw.println(" <link rel='stylesheet' href='css/export.css' type='text/css'>");
		pw.println("</head>");
		pw.println("<body><br><br>");
		pw.println("<h1 align='center'>" + localize("export") + "</h1>");

		pw.println("<div id='view-box'>");
		pw.println("<p class='title'>" + stationName
				+ (analyzerName != null ? " - " + analyzerName : "") + " - "
				+ parameterName + "</p>");
		pw.println("<table align='center' width='100%'>");
		pw.println("<tr>");
		pw.println("	<td align='center' class='header'>" + localize("date")
				+ "</td>");
		pw.println("	<td align='center' class='header'>" + localize("value")
				+ "</td>");
		pw.println("	<td align='center' class='header'>" + localize("validity")
				+ "</td>");
		pw.println("	<td align='center' class='header'>"
				+ localize("multi_flag") + "</td>");
		pw.println("</tr>");

		setDecimals();
		Iterator<DataObject> iterator = dataList.iterator();
		while (iterator.hasNext()) {
			DataObject dataObj = iterator.next();

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(dataObj.getNumDec());
			nf.setMinimumFractionDigits(dataObj.getNumDec());
			nf.setGroupingUsed(false);

			df.setMinimumFractionDigits(dataObj.getNumDec());
			df.setMaximumFractionDigits(dataObj.getNumDec());

			pw.println("<tr>");
			pw.println("	<td align='center' class='data'>"
					+ sdfTimestamp.format(dataObj.getDate()) + "</td>");
			pw.println("	<td align='center' class='data'>"
					+ (dataObj.getValue() != null ? df.format(dataObj.getValue()) : "")
					+ "</td>");
			pw.println("	<td align='center' class='data'>"
					+ (dataObj.isFlag() ? localize("not_valid") : localize("valid"))
					+ "</td>");
			pw.println("	<td align='center' style='cursor:pointer' class='data' title='"
					+ dataObj.getTitle()
					+ "'>"
					+ Integer.toHexString(dataObj.getMultipleFlag()) + "</td>");
			pw.println("</tr>");

		}// end while

		pw.println("</table>");
		pw.println("</div>");
		pw.println("</body>");
		pw.println("</html>");
		pw.close();
		logger.info("Done.");
	}

	private void readParameter(HttpServletRequest request)
			throws CentraleException {

		mode = request.getParameter("mode");
		if (mode == null) {
			// case of csv request from uiservice
			elementIdStr = request.getParameter("elementIdStr");
			avgPeriod = request.getParameter("avgPeriod");
		}

		stationName = request.getParameter("stationName");
		analyzerName = request.getParameter("analyzerName");
		parameterName = request.getParameter("elementName");
		measureUnit = request.getParameter("measureUnitStr");

		// set fieldSeparator and decimalSeparator
		fieldSeparator = request.getParameter("fieldSeparator");
		decimalSeparator = request.getParameter("decimalSeparator");
		if (fieldSeparator == null)
			fieldSeparator = FIELD_SEPARATOR;
		if (decimalSeparator == null)
			decimalSeparator = DECIMAL_SEPARATOR;
		if (fieldSeparator != null && decimalSeparator != null
				&& fieldSeparator.equals(decimalSeparator)) {
			throw new CentraleException("wrong_separator");
		}

	}// end readParameter

	private void prepareCsv(HttpServletResponse response,
			List<DataObject> dataList) throws UnsupportedEncodingException,
			IOException {
		logger.info("Preparing CSV file...");
		response.setContentType("text/x-comma-separated-values");
		response.setCharacterEncoding("UTF-8");
		String filename = "";
		exportDate = sdfTimestamp.format(new Date());
		if (type == SAMPLE)
			filename = "sample_data_";
		else if (type == MEANS)
			filename = "means_data_";
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename + parameterName + "_" + exportDate + ".csv");

		setDecimals();

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				response.getOutputStream(), "UTF-8"), true);
		setCsvHeader(pw);

		Iterator<DataObject> iterator = dataList.iterator();
		while (iterator.hasNext()) {
			DataObject dataObj = iterator.next();

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(dataObj.getNumDec());
			nf.setMinimumFractionDigits(dataObj.getNumDec());
			nf.setGroupingUsed(false);

			df.setMinimumFractionDigits(dataObj.getNumDec());
			df.setMaximumFractionDigits(dataObj.getNumDec());

			StringBuffer buffer = new StringBuffer();
			buffer.append(sdfTimestamp.format(dataObj.getDate()));
			buffer.append(fieldSeparator);
			if (dataObj instanceof WindDataObject) {
				NumberFormat dirNf = NumberFormat.getInstance();
				dirNf.setMaximumFractionDigits(((WindDataObject) dataObj).getDirNumDec());
				dirNf.setMinimumFractionDigits(((WindDataObject) dataObj).getDirNumDec());
				dirNf.setGroupingUsed(false);

				WindDataObject windData = (WindDataObject) dataObj;
				buffer.append(windData.getVectorialSpeed() != null ? nf
						.format(windData.getVectorialSpeed()) : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getVectorialDirection() != null ? dirNf
						.format(windData.getVectorialDirection()) : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getStandardDeviation() != null ? windData.getStandardDeviation()
						.toString() : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getScalarSpeed() != null ? windData.getScalarSpeed()
						.toString() : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getGustSpeed() != null ? windData.getGustSpeed()
						.toString() : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getGustDirection() != null ? windData.getGustDirection()
						.toString() : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getCalmsNumberPercent() != null ? windData.getCalmsNumberPercent()
						.toString() : " ");
				buffer.append(fieldSeparator);
				buffer.append(windData.getCalm() != null ? windData.getCalm().toString()
						: " ");
				buffer.append(fieldSeparator);
			} else {
				buffer.append(dataObj.getValue() != null ? df.format(dataObj.getValue())
						: "");
				buffer.append(fieldSeparator);
				buffer.append((dataObj.isFlag() ? localize("not_valid")
						: localize("valid")));
				buffer.append(fieldSeparator);
			}

			buffer.append(Integer.toHexString(dataObj.getMultipleFlag()));
			pw.println(buffer.toString());

		}// end while

		pw.close();
		logger.info("Done.");
	}// end prepareCsv

	private void setCsvHeader(PrintWriter pw) {

		pw.println(localize("station") + fieldSeparator + stationName);
		if (analyzerName != null)
			pw.println(localize("analyzer") + fieldSeparator + analyzerName);
		pw.println(localize("parameter") + fieldSeparator + parameterName);
		pw.println(localize("measure_unit") + fieldSeparator + measureUnit);
		if (type == MEANS)
			pw.println(localize("avg_period") + fieldSeparator + avgPeriod);
		pw.println(localize("data_type") + fieldSeparator + dataType);
		pw.println(localize("export_date") + fieldSeparator + exportDate);
		pw.println();
		if (isWind && type == MEANS)
			pw.println(localize("date") + fieldSeparator
					+ localize("vectorial_speed") + fieldSeparator
					+ localize("vectorial_direction") + fieldSeparator
					+ localize("standard_deviation") + fieldSeparator
					+ localize("scalar_speed") + fieldSeparator
					+ localize("gust_speed") + fieldSeparator
					+ localize("gust_direction") + fieldSeparator
					+ localize("calm_percent") + fieldSeparator
					+ localize("calm") + fieldSeparator
					+ localize("multi_flag"));
		else
			pw.println(localize("date") + fieldSeparator + localize("value")
					+ fieldSeparator + localize("validity") + fieldSeparator
					+ localize("multi_flag"));

	}// end setHeader

	private String localize(String message) {
		if (message == null)
			return "";
		if (propertyUtil == null || locale == null)
			return message;
		String msgBundle = null;
		try {
			msgBundle = propertyUtil.getProperty(locale, message);
		} catch (MissingResourceException mrex) {
			return message;
		}
		return msgBundle;
	}// end localize

	private void sendError(HttpServletResponse response, String message)
			throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter pw = response.getWriter();
		pw.println(message);
		pw.close();
	}// end sendError

	private void setDecimals() {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalSeparator.charAt(0));
		// DecimalFormat df = new DecimalFormat();
		df.setDecimalFormatSymbols(dfs);
		df.setGroupingUsed(false);
	}

}// end class
