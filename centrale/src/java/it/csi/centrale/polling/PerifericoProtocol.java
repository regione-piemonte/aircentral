/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Communication protocol with periferico
// Change log:
//   2014-04-09: initial version
// ----------------------------------------------------------------------------
// $Id: PerifericoProtocol.java,v 1.9 2014/09/19 14:13:25 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import it.csi.centrale.comm.Connection;
import it.csi.centrale.polling.ProtocolException.Failure;
import it.csi.centrale.polling.data.AnalyzerCfg;
import it.csi.centrale.polling.data.AnalyzerStatus;
import it.csi.centrale.polling.data.BooleanDatum;
import it.csi.centrale.polling.data.CommonConfigResult;
import it.csi.centrale.polling.data.ContainerAlarmStatus;
import it.csi.centrale.polling.data.ContainerAlarmCfg;
import it.csi.centrale.polling.data.Datum;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.DoubleDatum;
import it.csi.centrale.polling.data.ElementCfg;
import it.csi.centrale.polling.data.EventDatum;
import it.csi.centrale.polling.data.GpsDatum;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.polling.data.StationConfig;
import it.csi.centrale.polling.data.TimeSyncResult;
import it.csi.centrale.polling.data.TriggerAlarmDatum;
import it.csi.centrale.polling.data.Version;
import it.csi.centrale.polling.data.WindDatum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
/**
 * Communication protocol with periferico
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class PerifericoProtocol {

	private static Logger logger = Logger.getLogger("centrale."
			+ PerifericoProtocol.class.getSimpleName());
	private static final int DEFAULT_CONNECT_TIMEOUT_MS = 30000;
	private static final int DEFAULT_READ_TIMEOUT_MS = 30000;
	private static final String SERVICE_URL = "/copservice";
	// NOTE: keep synchronized with CopService.java in Periferico
	private static final int ERR_CODE_BAD_REQUEST = 1;
	private static final int ERR_CODE_FN_BAD_PARAMS = 2;
	private static final int ERR_CODE_FN_EXEC_FAIL = 3;
	public static final int MAX_DATA_FOR_REQUEST = 14400;
	//
	private static final String REQUEST_DATE_FMT = "yyyyMMddHHmmss";
	private static final String REQUEST_TZ_DATE_FMT = "yyyyMMddHHmmssZ";

	private int connTimeout_ms = DEFAULT_CONNECT_TIMEOUT_MS;
	private int readTimeout_ms = DEFAULT_READ_TIMEOUT_MS;

	int getConnTimeout_ms() {
		return connTimeout_ms;
	}

	void setConnTimeout_ms(int connTimeout_ms) {
		this.connTimeout_ms = connTimeout_ms;
	}

	int getReadTimeout_ms() {
		return readTimeout_ms;
	}

	void setReadTimeout_ms(int readTimeout_ms) {
		this.readTimeout_ms = readTimeout_ms;
	}

	public Version getPerifericoVersion(Connection conn) throws IOException,
			ProtocolException {
		logger.debug("Reading periferico version...");
		String request = SERVICE_URL + "?function=getPerifericoVersion";
		return getVersion(conn, request);
	}

	public Version getProtocolVersion(Connection conn) throws IOException,
			ProtocolException {
		logger.debug("Reading protocol version...");
		String request = SERVICE_URL + "?function=getProtocolVersion";
		return getVersion(conn, request);
	}

	private Version getVersion(Connection conn, String request)
			throws IOException, ProtocolException {
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			String reply = readLine(br);
			if (reply == null)
				throw new ProtocolException(request, Failure.NO_REPLY);
			return Version.valueOf(reply);
		} catch (NumberFormatException e) {
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY, e);
		} catch (ParseException e) {
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY, e);
		} finally {
			if (br != null)
				br.close();
		}
	}

	DiskStatus getDiskUsage(Connection conn, UUID stationId)
			throws IOException, ProtocolException {
		logger.debug("Reading disk usage...");
		String request = SERVICE_URL + "?function=getDiskUsage";
		if (stationId != null)
			request += "&station=" + stationId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			Map<String, String> mapParams = new HashMap<String, String>();
			Map<String, Integer> mapFS = new HashMap<String, Integer>();
			boolean readParams = true;
			int numExpectedFilesystems = 0;
			int numFoundFilesystems = 0;
			String line;
			while ((line = readLine(br)) != null) {
				if (readParams) {
					if (line.startsWith("filesystems:")) {
						readParams = false;
						numExpectedFilesystems = parseQuantityOf("filesystems",
								line, request);
					} else {
						appendKeyStringValue(request, line, mapParams);
					}
				} else {
					appendKeyIntegerValue(request, line, mapFS);
					numFoundFilesystems++;
				}
			}
			if (numExpectedFilesystems != numFoundFilesystems)
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Expected information for " + numExpectedFilesystems
								+ " filesystems, found " + numFoundFilesystems);
			try {
				return new DiskStatus(mapParams, mapFS);
			} catch (IllegalArgumentException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Disk usage and status not parsable", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	PerifericoAppStatus getApplicationStatus(Connection conn, UUID stationId)
			throws IOException, ProtocolException {
		logger.debug("Reading application status...");
		String request = SERVICE_URL + "?function=getApplicationStatus";
		if (stationId != null)
			request += "&station=" + stationId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			return new PerifericoAppStatus(readAttributes(request, br));
		} finally {
			if (br != null)
				br.close();
		}
	}

	List<ContainerAlarmStatus> listContainerAlarms(Connection conn,
			UUID stationId) throws IOException, ProtocolException {
		logger.debug("Reading status for container alarms...");
		String request = SERVICE_URL + "?function=listContainerAlarms";
		if (stationId != null)
			request += "&station=" + stationId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			List<String> listLines = readStringList(request, br);
			List<ContainerAlarmStatus> listAlarms = new ArrayList<ContainerAlarmStatus>();
			try {
				for (String str : listLines)
					listAlarms.add(ContainerAlarmStatus.valueOf(str));
				return listAlarms;
			} catch (IllegalArgumentException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Container alarms list not parsable", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	List<AnalyzerStatus> listAnalyzers(Connection conn, UUID stationId)
			throws IOException, ProtocolException {
		logger.debug("Reading status for analyzers...");
		String request = SERVICE_URL + "?function=listAnalyzers";
		if (stationId != null)
			request += "&station=" + stationId;
		request += "&dataValid=true";
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			List<String> listLines = readStringList(request, br);
			List<AnalyzerStatus> listAS = new ArrayList<AnalyzerStatus>();
			try {
				for (String str : listLines)
					listAS.add(AnalyzerStatus.valueOf(str));
				return listAS;
			} catch (IllegalArgumentException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Analyzers status list not parsable", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	public StationConfig getStationConfig(Connection conn, UUID stationId,
			UUID configId) throws IOException, ProtocolException {
		logger.debug("Reading station configuration...");
		String request = SERVICE_URL + "?function=getStationConfig";
		if (stationId != null)
			request += "&station=" + stationId;
		if (configId != null)
			request += "&config=" + configId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			UUID perifConfigId = readUUID(request, br, true);
			boolean expected = !perifConfigId.equals(configId);
			UUID perifStationId = readUUID(request, br, expected);
			if (perifStationId == null)
				return null;
			try {
				return new StationConfig(perifStationId, perifConfigId,
						readAttributes(request, br));
			} catch (Exception e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Station configuration not parsable'", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	List<ContainerAlarmCfg> getContainerAlarmsConfig(Connection conn,
			UUID stationId, UUID configId) throws IOException,
			ProtocolException {
		logger.debug("Reading configuration for container alarms...");
		String request = SERVICE_URL + "?function=getContainerAlarmsConfig";
		if (stationId != null)
			request += "&station=" + stationId;
		if (configId != null)
			request += "&config=" + configId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			UUID perifConfigId = readUUID(request, br, true);
			boolean sameConfig = perifConfigId.equals(configId);
			if (sameConfig)
				return null;
			String line = readLine(br);
			int numItems = parseQuantityOf("containerAlarms", line, request);
			List<ContainerAlarmCfg> listConfigs = new ArrayList<ContainerAlarmCfg>();
			for (int i = 0; i < numItems; i++) {
				UUID id = readUUID(request, br, true);
				Map<String, String> attributes = readAttributes(request, br);
				try {
					listConfigs.add(new ContainerAlarmCfg(id, attributes));
				} catch (IllegalArgumentException e) {
					throw new ProtocolException(request,
							Failure.MALFORMED_REPLAY,
							"Container alarms configurations not parsable'", e);
				}
			}
			return listConfigs;
		} finally {
			if (br != null)
				br.close();
		}
	}

	List<AnalyzerCfg> getAnalyzersConfig(Connection conn, UUID stationId,
			UUID configId) throws IOException, ProtocolException {
		logger.debug("Reading configuration for analyzers...");
		String request = SERVICE_URL + "?function=getAnalyzersConfig";
		if (stationId != null)
			request += "&station=" + stationId;
		if (configId != null)
			request += "&config=" + configId;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			UUID perifConfigId = readUUID(request, br, true);
			boolean sameConfig = perifConfigId.equals(configId);
			if (sameConfig)
				return null;
			String line = readLine(br);
			int numAnalyzers = parseQuantityOf("analyzers", line, request);
			List<AnalyzerCfg> listConfigs = new ArrayList<AnalyzerCfg>();
			for (int i = 0; i < numAnalyzers; i++) {
				UUID id = readUUID(request, br, true);
				Map<String, String> attributes = readAttributes(request, br);
				try {
					AnalyzerCfg anCfg = new AnalyzerCfg(id, attributes);
					line = readLine(br);
					int numElements = parseQuantityOf("elements", line, request);
					for (int j = 0; j < numElements; j++) {
						anCfg.addElement(ElementCfg.valueOf(readLine(br),
								readAttributes(request, br)));
					}
					listConfigs.add(anCfg);
				} catch (IllegalArgumentException e) {
					throw new ProtocolException(request,
							Failure.MALFORMED_REPLAY,
							"Analyzers configuration not parsable'", e);
				}
			}
			return listConfigs;
		} finally {
			if (br != null)
				br.close();
		}
	}

	List<EventDatum> getEventData(Connection conn, UUID stationId,
			UUID analyzerId, String eventId, Date startDate, Date endDate)
			throws IOException, ProtocolException {
		DataReader<EventDatum> dr = new DataReader<EventDatum>("event",
				"getEventData", "analyzer", "event", null, EventDatum.class);
		return dr.getData(conn, stationId, analyzerId, eventId, null,
				alignToSecond(startDate, true), alignToSecond(endDate, false));
	}

	List<DoubleDatum> getSampleData(Connection conn, UUID stationId,
			UUID analyzerId, String parameterId, Date startDate, Date endDate)
			throws IOException, ProtocolException {
		DataReader<DoubleDatum> dr = new DataReader<DoubleDatum>("sample",
				"getSampleData", "analyzer", "parameter", null,
				DoubleDatum.class);
		return dr.getData(conn, stationId, analyzerId, parameterId, null,
				startDate, endDate);
	}

	List<DoubleDatum> getMeanData(Connection conn, UUID stationId,
			UUID analyzerId, String parameterId, int period, Date startDate,
			Date endDate) throws IOException, ProtocolException {
		DataReader<DoubleDatum> dr = new DataReader<DoubleDatum>("mean",
				"getMeanData", "analyzer", "parameter", "period",
				DoubleDatum.class);
		return dr.getData(conn, stationId, analyzerId, parameterId, period,
				startDate, endDate);
	}

	List<DoubleDatum> getTotalData(Connection conn, UUID stationId,
			UUID analyzerId, String parameterId, int period, Date startDate,
			Date endDate) throws IOException, ProtocolException {
		DataReader<DoubleDatum> dr = new DataReader<DoubleDatum>("total",
				"getTotalData", "analyzer", "parameter", "period",
				DoubleDatum.class);
		return dr.getData(conn, stationId, analyzerId, parameterId, period,
				startDate, endDate);
	}

	List<WindDatum> getWindData(Connection conn, UUID stationId,
			UUID analyzerId, String parameterId, int period, Date startDate,
			Date endDate) throws IOException, ProtocolException {
		DataReader<WindDatum> dr = new DataReader<WindDatum>("wind",
				"getWindData", "analyzer", "parameter", "period",
				WindDatum.class);
		return dr.getData(conn, stationId, analyzerId, parameterId, period,
				startDate, endDate);
	}

	List<BooleanDatum> getDigitalCAData(Connection conn, UUID stationId,
			UUID containerAlarmId, String alarmId, Date startDate, Date endDate)
			throws IOException, ProtocolException {
		DataReader<BooleanDatum> dr = new DataReader<BooleanDatum>(
				"digital alarm", "getDigitalCAData", "containerAlarm", "alarm",
				null, BooleanDatum.class);
		return dr.getData(conn, stationId, containerAlarmId, alarmId, null,
				alignToSecond(startDate, true), alignToSecond(endDate, false));
	}

	List<TriggerAlarmDatum> getTriggerCAData(Connection conn, UUID stationId,
			UUID containerAlarmId, String alarmId, Date startDate, Date endDate)
			throws IOException, ProtocolException {
		DataReader<TriggerAlarmDatum> dr = new DataReader<TriggerAlarmDatum>(
				"trigger alarm", "getTriggerCAData", "containerAlarm", "alarm",
				null, TriggerAlarmDatum.class);
		return dr.getData(conn, stationId, containerAlarmId, alarmId, null,
				alignToSecond(startDate, true), alignToSecond(endDate, false));
	}

	List<GpsDatum> getGpsData(Connection conn, UUID stationId,
			Boolean bestHourlyDatumOnly, Date startDate, Date endDate)
			throws IOException, ProtocolException {
		DataReader<GpsDatum> dr = new DataReader<GpsDatum>("gps", "getGpsData",
				null, null, "hourly", GpsDatum.class);
		return dr.getData(conn, stationId, null, null, bestHourlyDatumOnly,
				startDate, endDate);
	}

	private class DataReader<T extends Datum> {

		private String dataDescription;
		private String function;
		private String dataHolderName;
		private String dataTypeName;
		private String auxArgName;
		private Class<T> dataClass;

		DataReader(String dataDescription, String function,
				String dataHolderName, String dataTypeName, String auxArgName,
				Class<T> dataClass) {
			this.dataDescription = dataDescription;
			this.function = function;
			this.dataHolderName = dataHolderName;
			this.dataTypeName = dataTypeName;
			this.auxArgName = auxArgName;
			this.dataClass = dataClass;
		}

		List<T> getData(Connection conn, UUID stationId, UUID dataHolderId,
				String dataTypeId, Object auxArgId, Date startDate, Date endDate)
				throws IOException, ProtocolException {
			DateFormat df = new SimpleDateFormat(REQUEST_DATE_FMT);
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Reading " + dataDescription + " data for: ");
				if (dataHolderName != null)
					sb.append(dataHolderName + " '" + dataHolderId + "', ");
				if (dataTypeName != null)
					sb.append(dataTypeName + " '" + dataTypeId + "', ");
				if (auxArgName != null)
					sb.append(auxArgName + " '" + auxArgId + "', ");
				if (startDate != null)
					sb.append("start date '" + df.format(startDate) + "', ");
				if (endDate != null)
					sb.append("end date '" + df.format(endDate) + "', ");
				sb.delete(sb.length() - 2, sb.length());
				sb.append("...");
				logger.debug(sb.toString());
			}
			String request = SERVICE_URL + "?function=" + function;
			if (stationId != null)
				request += "&station=" + stationId;
			if (dataHolderName != null && dataHolderId != null)
				request += "&" + dataHolderName + "=" + dataHolderId;
			if (dataTypeName != null && dataTypeId != null)
				request += "&" + dataTypeName + "=" + urlEncode(dataTypeId);
			if (auxArgName != null && auxArgId != null)
				request += "&" + auxArgName + "="
						+ urlEncode(auxArgId.toString());
			if (startDate != null)
				request += "&startDate=" + df.format(startDate);
			if (endDate != null)
				request += "&endDate=" + df.format(endDate);
			BufferedReader br = null;
			try {
				br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
				checkHeader(request, br);
				String inputLine = readLine(br);
				if (inputLine == null)
					throw new ProtocolException(request,
							Failure.TRUNCATED_REPLAY,
							"The number of data records is missing");
				int numData;
				try {
					numData = Integer.parseInt(inputLine);
				} catch (NumberFormatException nfe) {
					throw new ProtocolException(request,
							Failure.MALFORMED_REPLAY,
							"Error parsing the number of data records", nfe);
				}
				List<T> data = new ArrayList<T>();
				while ((inputLine = readLine(br)) != null) {
					try {
						data.add(dataClass.getConstructor(String.class)
								.newInstance(inputLine));
					} catch (Exception e) {
						throw new ProtocolException(request,
								Failure.MALFORMED_REPLAY, "Cannot parse line '"
										+ inputLine + "' as datum of type "
										+ dataClass.getSimpleName(), e);
					}
				}
				if (data.size() != numData)
					throw new ProtocolException(request,
							Failure.MALFORMED_REPLAY, "The number of data "
									+ "records received (" + data.size() + ") "
									+ "is different " + "from the number of "
									+ "data records declared (" + numData + ")");
				logger.debug(data.size() + " data records read");
				return data;
			} finally {
				if (br != null)
					br.close();
			}
		}
	}

	List<String> getConfigFile(Connection conn, UUID stationId,
			long modifiedAfterThisTime_ms, long[] resultForConfigFileTimestamp)
			throws IOException, ProtocolException {
		logger.debug("Reading configuration file...");
		String request = SERVICE_URL + "?function=getConfigFile";
		if (stationId != null)
			request += "&station=" + stationId;
		request += "&lastModifyTime=" + modifiedAfterThisTime_ms;
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			String foundStr = readLine(br);
			if (foundStr == null)
				throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
						"File existance information missing");
			boolean found = new Boolean(foundStr);
			if (!found)
				return null;
			List<String> listCfgLines = readStringList(request, br);
			if (listCfgLines.isEmpty())
				throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
						"Configuration file timestamp is missing");
			try {
				long timestamp = new Long(listCfgLines.remove(0));
				if (resultForConfigFileTimestamp != null
						&& resultForConfigFileTimestamp.length > 0)
					resultForConfigFileTimestamp[0] = timestamp;
				return listCfgLines;
			} catch (NumberFormatException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Configuration file timestamp is unparsable", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	TimeSyncResult doTimeSync(Connection conn, String server)
			throws IOException, ProtocolException {
		logger.debug("Executing time sync using server '" + server + "'...");
		String request = SERVICE_URL + "?function=doTimeSync";
		DateFormat dfTZ = new SimpleDateFormat(REQUEST_TZ_DATE_FMT);
		request += "&server=" + urlEncode(server);
		request += "&date=" + urlEncode(dfTZ.format(new Date()));
		BufferedReader br = null;
		try {
			br = conn.execHttpGet(request, connTimeout_ms, readTimeout_ms);
			checkHeader(request, br);
			Map<String, String> mapReply = new HashMap<String, String>();
			String line;
			while ((line = readLine(br)) != null)
				appendKeyStringValue(request, line, mapReply);
			try {
				return new TimeSyncResult(mapReply);
			} catch (IllegalArgumentException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Unparsable time sync result", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	CommonConfigResult setCommonFile(Connection conn, UUID stationId,
			String commonConfig) throws IOException, ProtocolException {
		logger.debug("Sending common configuration file...");
		String request = SERVICE_URL + "POST setCommonFile";
		List<String> content = new ArrayList<String>();
		content.add("setCommonFile");
		content.add(stationId.toString());
		content.add(commonConfig);
		BufferedReader br = null;
		try {
			br = conn.execHttpPost(SERVICE_URL, connTimeout_ms, readTimeout_ms,
					content);
			checkHeader(request, br);
			String result = readLine(br);
			if (result == null)
				throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
						"Common Config set result missing");
			try {
				return CommonConfigResult.valueOf(result.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Cannot parse Common Config set result '" + result
								+ "'", e);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}

	private UUID readUUID(String request, BufferedReader br, boolean expected)
			throws IOException, ProtocolException {
		String line = readLine(br);
		if (line == null) {
			if (expected)
				throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
						"UUID expected");
			return null;
		}
		try {
			return UUID.fromString(line);
		} catch (IllegalArgumentException e) {
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"Cannot parse UUID in line '" + line + "'", e);
		}
	}

	private Map<String, String> readAttributes(String request, BufferedReader br)
			throws IOException, ProtocolException {
		String tagLine = readLine(br);
		int numAttributes = parseQuantityOf("attributes", tagLine, request);
		Map<String, String> mapAttributes = new HashMap<String, String>();
		for (int i = 0; i < numAttributes; i++) {
			String line = readLine(br);
			if (line == null)
				throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
						"Expected " + numAttributes + " attributes, found " + i);
			appendKeyStringValue(request, line, mapAttributes);
		}
		return mapAttributes;
	}

	private List<String> readStringList(String request, BufferedReader br)
			throws IOException, ProtocolException {
		List<String> responseLines = new ArrayList<String>();
		String inputLine;
		while ((inputLine = readLine(br)) != null)
			responseLines.add(inputLine);
		if (responseLines.isEmpty())
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"The number of response lines is missing");
		String strResponseLinesNumber = responseLines.remove(0);
		int responseLinesNumber;
		try {
			responseLinesNumber = Integer.parseInt(strResponseLinesNumber);
		} catch (NumberFormatException nfe) {
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"Error parsing the number of response lines", nfe);
		}
		if (responseLines.size() != responseLinesNumber)
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"The number of response lines received ("
							+ responseLines.size() + ") is different "
							+ "from the number of response lines declared ("
							+ responseLinesNumber + ")");
		return responseLines;
	}

	private void checkHeader(String request, BufferedReader br)
			throws IOException, ProtocolException {
		String header = readLine(br);
		if ("OK".equals(header))
			return;
		if (header == null)
			throw new ProtocolException(request, Failure.NO_REPLY);
		if (header.startsWith("ERROR")) {
			try {
				String strCode = header.substring(5).trim();
				int code = Integer.parseInt(strCode);
				String message = readLine(br);
				Failure f;
				switch (code) {
				case ERR_CODE_BAD_REQUEST:
					f = Failure.BAD_REQUEST;
					break;
				case ERR_CODE_FN_BAD_PARAMS:
					f = Failure.BAD_FUNCTION_PARAMS;
					break;
				case ERR_CODE_FN_EXEC_FAIL:
					f = Failure.FUNCTION_EXEC_FAILURE;
					break;
				default:
					f = Failure.MALFORMED_REPLAY;
					break;
				}
				throw new ProtocolException(request, f, message);
			} catch (RuntimeException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Unparsable header", e);
			}
		}
		throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
				"Unparsable header: '" + header + "'");
	}

	private String readLine(BufferedReader br) throws IOException {
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty())
				break;
		}
		return line;
	}

	private int parseQuantityOf(String tag, String tagLine, String request)
			throws ProtocolException {
		if (tagLine == null)
			throw new ProtocolException(request, Failure.TRUNCATED_REPLAY,
					"Expected line with tag '" + tag + "'");
		tagLine = tagLine.trim();
		if (tagLine.startsWith(tag + ":")) {
			try {
				String strNumber = tagLine.substring(tag.length() + 1).trim();
				return Integer.parseInt(strNumber);
			} catch (RuntimeException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Expected '" + tag + ": integer value' in line '"
								+ tagLine + "'", e);
			}
		} else {
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"Tag '" + tag + "' not found in line '" + tagLine + "'");
		}
	}

	private void appendKeyStringValue(String request, String line,
			Map<String, String> map) throws ProtocolException {
		String[] fields = splitKeyValue(request, line);
		map.put(fields[0], fields[1]);
	}

	private void appendKeyIntegerValue(String request, String line,
			Map<String, Integer> map) throws ProtocolException {
		String[] fields = splitKeyValue(request, line);
		Integer value = null;
		if (fields[1] != null && !"NULL".equalsIgnoreCase(fields[1])) {
			try {
				value = new Integer(fields[1]);
			} catch (NumberFormatException e) {
				throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
						"Not integer type for value in 'key = value' pair '"
								+ line + "'");
			}
		}
		map.put(fields[0], value);
	}

	private String[] splitKeyValue(String request, String line)
			throws ProtocolException {
		line = line.trim();
		String[] fields = line.split("=", 2);
		if (fields.length != 2)
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"'key = value' pair expected in '" + line + "'");
		fields[0] = fields[0].trim();
		if (fields[0].isEmpty())
			throw new ProtocolException(request, Failure.MALFORMED_REPLAY,
					"Key missing in 'key = value' pair '" + line + "'");
		fields[1] = fields[1].trim();
		if (fields[1].isEmpty())
			fields[1] = null;
		return fields;
	}

	private String urlEncode(String str) throws UnsupportedEncodingException {
		if (str == null)
			return "";
		return URLEncoder.encode(str, "UTF-8");
	}

	// NOTE: this function is a hack to solve the limitation of Periferico
	// protocol that does not manage milliseconds when reading event data.
	// In this way downloading again the same event data should not occur.
	private Date alignToSecond(Date date, boolean isStartDate) {
		if (isStartDate && date == null)
			return null;
		if (date == null)
			date = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		if (cal.get(Calendar.MILLISECOND) != 0) {
			cal.set(Calendar.MILLISECOND, 0);
			if (isStartDate)
				cal.add(Calendar.SECOND, 1);
		}
		return cal.getTime();
	}

}
