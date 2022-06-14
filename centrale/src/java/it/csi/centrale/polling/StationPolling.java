/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for control station polling
// Change log:
//   2014-04-09: initial version
// ----------------------------------------------------------------------------
// $Id: StationPolling.java,v 1.23 2015/10/15 12:10:24 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import it.csi.centrale.Centrale;
import it.csi.centrale.config.ConfigManager;
import it.csi.centrale.data.station.AlarmData;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.AvgPeriod;
import it.csi.centrale.data.station.CommDeviceInfo.CommunicationDevice;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.ContainerAlarm;
import it.csi.centrale.data.station.GenericData;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.GpsData;
import it.csi.centrale.data.station.InsertableObject;
import it.csi.centrale.data.station.ListGenericData;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationStatus;
import it.csi.centrale.data.station.WindElement;
import it.csi.centrale.polling.data.AnalyzerCfg;
import it.csi.centrale.polling.data.AnalyzerStatus;
import it.csi.centrale.polling.data.BooleanDatum;
import it.csi.centrale.polling.data.CommonConfigResult;
import it.csi.centrale.polling.data.ContainerAlarmCfg;
import it.csi.centrale.polling.data.ContainerAlarmStatus;
import it.csi.centrale.polling.data.Datum;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.DoubleDatum;
import it.csi.centrale.polling.data.EventDatum;
import it.csi.centrale.polling.data.GpsDatum;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.polling.data.StationConfig;
import it.csi.centrale.polling.data.TimeSyncResult;
import it.csi.centrale.polling.data.TriggerAlarmDatum;
import it.csi.centrale.polling.data.Version;
import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.periferico.config.common.CommonCfg;
import it.csi.periferico.config.common.ConfigException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.XMLException;
/**
 * Class for control station polling
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class StationPolling extends Polling {

	private static Logger logger = Logger.getLogger("centrale."
			+ StationPolling.class.getSimpleName());

	private Station station;

	StationPolling(Station station) {
		this.station = station;
	}

	@Override
	void execute() throws NoReplyException, IOException, ProtocolException,
			PollingException {
		StationStatus stationStatus = station.getStStatus();
		PerifericoProtocol pp = new PerifericoProtocol();
		Version perifVersion;
		try {
			perifVersion = pp.getPerifericoVersion(getConn());
		} catch (IOException e) {
			throw new NoReplyException(e);
		}
		logger.debug("Station '" + station.getName()
				+ "' - Periferico version is: " + perifVersion);
		Version protocolVersion = pp.getProtocolVersion(getConn());
		logger.debug("Station '" + station.getName()
				+ "' - Protocol version is: " + protocolVersion);
		UUID stationId = str2uuid(station.getStationUUID());
		UUID currentConfigId = str2uuid(station.getStInfo().getConfigUUID());
		StationConfig stCfg = pp.getStationConfig(getConn(), stationId,
				currentConfigId);
		boolean downloadLimitReached = false;
		if (stCfg != null) { // New station or configuration changed
			logger.trace("Station config: " + stCfg);
			if (stationId == null)
				stationId = stCfg.getStationId();
			if (currentConfigId != null)
				downloadLimitReached = !downloadAllData(pp);
			if (!downloadLimitReached) {
				List<AnalyzerCfg> anConfigs = pp.getAnalyzersConfig(getConn(),
						stationId, null);
				logger.trace("Analyzers config: " + anConfigs);
				List<ContainerAlarmCfg> caConfigs = pp
						.getContainerAlarmsConfig(getConn(), stationId, null);
				logger.trace("Container alarms config: " + caConfigs);
				updateStationConfig(stCfg, anConfigs, caConfigs);
				downloadLastConfigFile(pp);
			}
		}
		PerifericoAppStatus appStatus = pp.getApplicationStatus(getConn(),
				stationId);
		logger.trace("Application status is: " + appStatus);
		stationStatus.setAppStatus(appStatus);
		DiskStatus diskStatus = pp.getDiskUsage(getConn(), stationId);
		logger.trace("Disk status is: " + diskStatus);
		stationStatus.setDiskStatus(diskStatus);
		List<AnalyzerStatus> listAnStatus = pp.listAnalyzers(getConn(),
				stationId);
		logger.trace("Analyzers status: " + listAnStatus);
		stationStatus.setInstrumentInAlarm(listAnStatus);
		setAnalyzerStatus(listAnStatus);
		List<ContainerAlarmStatus> contAlarms = pp.listContainerAlarms(
				getConn(), stationId);
		logger.trace("Container alarms: " + contAlarms);
		stationStatus.setStationAlarm(contAlarms, getDoorAlarmUuid());
		setContainerAlarmStatus(contAlarms);
		if (!downloadLimitReached) {
			downloadLimitReached = !downloadAllData(pp);
			if (!downloadLimitReached && isCommonConfigToSend()) {
				sendCommonConfig(pp, stationStatus);
				appStatus = pp.getApplicationStatus(getConn(), stationId);
				logger.trace("Application status is: " + appStatus);
				stationStatus.setAppStatus(appStatus);
			}
		}

		TimeSyncResult tsr = pp.doTimeSync(getConn(), getTimeHost());
		logger.trace("Station '" + station.getName() + "' - Time sync result: "
				+ tsr);
		stationStatus.setSync(tsr);
		Integer[] key = { station.getStationId(), null };
		Centrale.getInstance().getDataQueue()
				.offer(new InsertableObject(key, stationStatus));
		stationStatus.onInfoUpdated();
		CustomTask ct = new CustomTask(getConn().getHost(), station.getName(),
				perifVersion, getConn().getTypeName());
		ct.execute();
	}

	private void setAnalyzerStatus(List<AnalyzerStatus> listAnStatus)
			throws DbException {
		Date now = new Date();
		Map<String, AnalyzerStatus> mapAnStatus = new HashMap<String, AnalyzerStatus>();
		for (AnalyzerStatus as : listAnStatus)
			mapAnStatus.put(uuid2str(as.getId()), as);
		for (Analyzer analyzer : station.getAnalyzersVect()) {
			// TODO: valutare come gestire gli analizzatori disabilitati
			if (analyzer.isDeleted())
				continue;
			AnalyzerStatus anStatus = mapAnStatus.get(analyzer
					.getAnalyzerUUID());
			// ATTENTION: keep statusForType in sync with DB table
			// 'analyzer_alarm_type'
			Boolean[] statusForType;
			if (anStatus == null)
				statusForType = new Boolean[6];
			else
				statusForType = new Boolean[] { anStatus.getFaultStatus(),
						anStatus.getMaintenanceInProgress(),
						anStatus.getManualCalibrationRunning(),
						anStatus.getAutoCheckRunning(),
						anStatus.getAutoCheckFailed(),
						anStatus.getDataValidStatus() };
			Vector<AnalyzerEventDatum> anStatusComponents = new Vector<AnalyzerEventDatum>();
			Vector<String> statusTypes = Centrale.getInstance().getConfigInfo()
					.getAnalyzerStatusType();
			for (int i = 0; i < statusTypes.size() && i < statusForType.length; i++) {
				AnalyzerEventDatum statusComponent = new AnalyzerEventDatum();
				statusComponent.setType(statusTypes.elementAt(i));
				statusComponent.setValue(statusForType[i]);
				statusComponent.setTimestamp(now);
				anStatusComponents.addElement(statusComponent);
			}
			analyzer.setAnStatus(anStatusComponents);
			String error = getDBM().updateAnalyzerStatus(
					analyzer.getAnalyzerId(), anStatusComponents);
			if (error != null)
				throw new DbException("Error updating analyzer status for "
						+ "station '" + station.getName() + "' (id="
						+ station.getStationId() + "), analyzer '"
						+ analyzer.getName() + "': " + error);
		}
	}

	private void setContainerAlarmStatus(List<ContainerAlarmStatus> listCAStatus)
			throws DbException {
		Date now = new Date();
		Map<String, ContainerAlarmStatus> mapCAStatus = new HashMap<String, ContainerAlarmStatus>();
		for (ContainerAlarmStatus as : listCAStatus)
			mapCAStatus.put(uuid2str(as.getId()), as);
		for (ContainerAlarm contAlarm : station.getContainerAlarm()) {
			if (contAlarm.isDeleted())
				continue;
			ContainerAlarmStatus contAlarmStatus = mapCAStatus.get(contAlarm
					.getAlarmUUID());
			contAlarm.setLastStatus(new AlarmData(now, contAlarmStatus));
		}
		String error = getDBM().updateContainerAlarmStatus(
				station.getContainerAlarm());
		if (error != null)
			throw new DbException("Error updating container alarm status for "
					+ "station '" + station.getName() + "' (id="
					+ station.getStationId() + "): " + error);
	}

	private UUID getDoorAlarmUuid() throws DbException {
		try {
			String doorId = getDBM().getDoorAlarmId();
			if (doorId == null || doorId.trim().isEmpty())
				return null;
			for (ContainerAlarm ca : station.getContainerAlarm()) {
				if (doorId.equals(ca.getAlarmID()))
					return UUID.fromString(ca.getAlarmUUID());
			}
			return null;
		} catch (SQLException e) {
			throw new DbException("Error reading door alarm ID", e);
		}
	}

	private String getTimeHost() throws DbException {
		try {
			if (station.getStInfo().getCommDevice().isLan())
				return getDBM().getTimeHost(CommunicationDevice.LAN);
			if (station.getStInfo().getCommDevice().isProxy())
				return getDBM().getTimeHost(CommunicationDevice.PROXY);
			if (station.getStInfo().getCommDevice().useModem())
				return getDBM().getTimeHost(CommunicationDevice.MODEM);
			return getDBM().getTimeHost(CommunicationDevice.ROUTER);
		} catch (SQLException e) {
			throw new DbException("Error reading time host information", e);
		}
	}

	private void updateStationConfig(StationConfig stationConfig,
			List<AnalyzerCfg> analyzerConfigs,
			List<ContainerAlarmCfg> containerAlarmConfigs)
			throws PollingException {
		if (station.getStationUUID() == null)
			station.setStationUUID(uuid2str(stationConfig.getStationId()));
		station.getStInfo().update(stationConfig);

		Map<String, AnalyzerCfg> mapAnConfigs = new HashMap<String, AnalyzerCfg>();
		for (AnalyzerCfg cfg : analyzerConfigs)
			mapAnConfigs.put(uuid2str(cfg.getId()), cfg);
		Iterator<Analyzer> itAnalyzers = station.getAnalyzersVect().iterator();
		while (itAnalyzers.hasNext()) {
			Analyzer an = itAnalyzers.next();
			AnalyzerCfg cfg = mapAnConfigs.get(an.getAnalyzerUUID());
			if (cfg == null) {
				an.setDeleted();
				continue;
			}
			an.update(cfg);
			mapAnConfigs.remove(an.getAnalyzerUUID());
		}
		for (AnalyzerCfg cfg : mapAnConfigs.values())
			station.getAnalyzersVect().add(new Analyzer(cfg));

		Map<String, ContainerAlarmCfg> mapAlarms = new HashMap<String, ContainerAlarmCfg>();
		for (ContainerAlarmCfg cfg : containerAlarmConfigs)
			mapAlarms.put(uuid2str(cfg.getId()), cfg);
		Iterator<ContainerAlarm> itContAlarms = station.getContainerAlarm()
				.iterator();
		while (itContAlarms.hasNext()) {
			ContainerAlarm ca = itContAlarms.next();
			ContainerAlarmCfg cfg = mapAlarms.get(ca.getAlarmUUID());
			if (cfg == null) {
				ca.setDeleted();
				continue;
			}
			ca.update(cfg);
			mapAlarms.remove(ca.getAlarmUUID());
		}
		for (ContainerAlarmCfg cfg : mapAlarms.values()) {
			try {
				AlarmNameInfo anInfo = getDBM().readAlarmNameInfo(
						cfg.getAlarmNameId());
				if (anInfo == null)
					throw new PollingException("Alarm definition not found "
							+ " for id '" + cfg.getAlarmNameId() + "'");
				station.getContainerAlarm().add(
						new ContainerAlarm(cfg, anInfo.getType(), anInfo.getAlarmName()));
			} catch (SQLException e) {
				throw new DbException("Error reading information for "
						+ "alarm name '" + cfg.getAlarmNameId() + "'", e);
			}
		}
		saveStation(station);
	}

	private void saveStation(Station station) throws DbException {
		String error = getDBM().writeStTree(station, false,
				Centrale.getInstance().getConfigInfo().getConfigId());
		if (error != null)
			throw new DbException("Error writing configuration for station '"
					+ station.getName() + "' (id=" + station.getStationId()
					+ "): " + error);
	}

	private boolean downloadAllData(PerifericoProtocol pp) throws IOException,
			ProtocolException, PollingException {
		try {
			logger.debug("Downloading data from station...");
			boolean all = true;
			boolean downloadAlarms = Centrale.getInstance().getConfigInfo()
					.isDownloadAlarmHistory();
			String temperatureElement = getDBM().getTemperatureId();
			boolean hasTemperature = false;
			UUID stationUUID = str2uuid(station.getStationUUID());
			for (Analyzer analyzer : station.getAnalyzersVect()) {
				boolean allForAnalyzer = true;
				UUID analyzerUUID = str2uuid(analyzer.getAnalyzerUUID());
				for (GenericElement element : analyzer.getElements()) {
					boolean allForElement = true;
					for (AvgPeriod avgPeriod : element.getAvgPeriod()) {
						if (avgPeriod.isAllDataDownloaded())
							continue;
						if (downloadAggregateData(pp, stationUUID,
								analyzerUUID, element, avgPeriod)) {
							if (avgPeriod.onDownloadCompleted())
								saveStation(station);
						} else {
							allForElement = false;
						}
					}
					if (element.isActive()) {
						if (element.getName().equals(temperatureElement)) {
							hasTemperature = true;
							Double temperature = readStationTemperature(pp,
									stationUUID, analyzerUUID,
									temperatureElement);
							station.getStStatus().setTemperature(temperature);
						}
						int sampleDownloadType = Centrale.getInstance()
								.getConfigInfo().getSampleDataTypeToDownload();
						if (sampleDownloadType == ConfigInfo.CALIBRATION_SAMPLE_DOWNLOAD) {
							allForElement &= downloadCalibrationSampleData(pp,
									stationUUID, analyzerUUID, element);
						} else if (sampleDownloadType == ConfigInfo.ALL_SAMPLE_DOWNLOAD
								&& station.getStInfo()
										.isSampleDataDownloadEnable()) {
							allForElement &= downloadSampleData(pp,
									stationUUID, analyzerUUID, element);
						}
					}
					allForAnalyzer &= allForElement;
				}
				if (downloadAlarms && analyzer.isActive())
					allForAnalyzer &= downloadAnalyzerAlarms(pp, stationUUID,
							analyzer);
				all &= allForAnalyzer;
			}
			if (!hasTemperature)
				station.getStStatus().setTemperature((Double) null);
			if (downloadAlarms) {
				for (ContainerAlarm alarm : station.getContainerAlarm()) {
					if (alarm.isDeleted())
						continue;
					all &= downloadContainerAlarmData(pp, stationUUID, alarm);
				}
			}
			if (station.getStInfo().hasGps())
				all &= downloadGpsData(pp, stationUUID);
			else
				station.getStStatus().setGpsStatus(null);
			if (!all)
				logger.warn("Data download limit reached for station '"
						+ station.getName() + "', download will continue "
						+ "during next polling");
			logger.debug("Data download completed");
			return all;
		} catch (SQLException ex) {
			throw new DbException("Error while managing data download for"
					+ " station '" + station.getName() + "' (id="
					+ station.getStationId() + ")", ex);
		}

	}

	private boolean downloadAggregateData(PerifericoProtocol pp,
			UUID stationUUID, UUID analyzerUUID, GenericElement element,
			AvgPeriod avgPeriod) throws IOException, ProtocolException,
			SQLException, PollingException {
		boolean all = true;
		int period = avgPeriod.getAvgPeriodVal();
		Date lastDatumDate = getDBM().getLastDataTimestamp(station.getStInfo(),
				station.getStationId(), element, period);
		List<? extends Datum> data;
		if (element instanceof ScalarElement) {
			if (GenericElement.RAIN.equals(element.getType())) {
				data = pp.getTotalData(getConn(), stationUUID, analyzerUUID,
						element.getName(), period, lastDatumDate, null);
			} else {
				data = pp.getMeanData(getConn(), stationUUID, analyzerUUID,
						element.getName(), period, lastDatumDate, null);
			}
		} else if (element instanceof WindElement) {
			data = pp.getWindData(getConn(), stationUUID, analyzerUUID,
					element.getName(), period, lastDatumDate, null);
		} else {
			throw new PollingException("Unknown element type" + " '"
					+ element.getClass().getSimpleName() + "'");
		}
		all &= data.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
		saveData(element, period, data);
		return all;
	}

	private boolean downloadAnalyzerAlarms(PerifericoProtocol pp,
			UUID stationUUID, Analyzer analyzer) throws IOException,
			ProtocolException, SQLException, PollingException {
		boolean all = true;
		UUID analyzerUUID = str2uuid(analyzer.getAnalyzerUUID());
		List<AnalyzerEventDatum> listAnStatus = new ArrayList<AnalyzerEventDatum>();
		Vector<String> analyzerStatusTypes = Centrale.getInstance()
				.getConfigInfo().getAnalyzerStatusType();
		for (String statusType : analyzerStatusTypes) {
			Date lastStatusDate = getDBM().getLastDataTimestamp(
					station.getStInfo(), analyzer.getAnalyzerId(), statusType);
			List<EventDatum> eventData = pp
					.getEventData(getConn(), stationUUID, analyzerUUID,
							statusType, lastStatusDate, null);
			all &= eventData.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
			for (EventDatum ed : eventData)
				listAnStatus.add(new AnalyzerEventDatum(ed, statusType));
		}
		Integer[] key = { analyzer.getAnalyzerId(), null };
		Centrale.getInstance().getDataQueue()
				.offer(new InsertableObject(key, listAnStatus));
		return all;
	}

	private boolean downloadCalibrationSampleData(PerifericoProtocol pp,
			UUID stationUUID, UUID analyzerUUID, GenericElement element)
			throws IOException, ProtocolException, SQLException,
			PollingException {
		boolean all = true;
		if (element instanceof ScalarElement) {
			List<Date[]> intervals = getDBM().getCalibrationIntervals(
					station.getStInfo(), station.getStationId(), element);
			for (Date[] interval : intervals) {
				List<DoubleDatum> data = pp.getSampleData(getConn(),
						stationUUID, analyzerUUID, element.getName(),
						interval[0], interval[1]);
				all &= data.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
				saveData(element, null, data);
			}
		} else if (element instanceof WindElement) {
			WindElement wind = (WindElement) element;
			downloadCalibrationSampleData(pp, stationUUID, analyzerUUID,
					wind.getSpeed());
			downloadCalibrationSampleData(pp, stationUUID, analyzerUUID,
					wind.getDirection());
		}
		return all;
	}

	private boolean downloadSampleData(PerifericoProtocol pp, UUID stationUUID,
			UUID analyzerUUID, GenericElement element) throws IOException,
			ProtocolException, SQLException, PollingException {
		boolean all = true;
		if (element instanceof ScalarElement) {
			Date lastDatumDate = getDBM().getLastDataTimestamp(
					station.getStInfo(), station.getStationId(), element, null);
			List<DoubleDatum> data = pp.getSampleData(getConn(), stationUUID,
					analyzerUUID, element.getName(), lastDatumDate, null);
			all &= data.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
			saveData(element, null, data);
		} else if (element instanceof WindElement) {
			WindElement wind = (WindElement) element;
			downloadSampleData(pp, stationUUID, analyzerUUID, wind.getSpeed());
			downloadSampleData(pp, stationUUID, analyzerUUID,
					wind.getDirection());
		}
		return all;
	}

	private Double readStationTemperature(PerifericoProtocol pp,
			UUID stationUUID, UUID analyzerUUID, String temperatureElementName)
			throws IOException, ProtocolException, SQLException,
			PollingException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MINUTE, -5);
		List<DoubleDatum> data = pp.getSampleData(getConn(), stationUUID,
				analyzerUUID, temperatureElementName, cal.getTime(), null);
		double accumulator = 0.0;
		int numSamples = 0;
		for (DoubleDatum dd : data) {
			if (dd.getValue() == null)
				continue;
			numSamples++;
			accumulator += dd.getValue();
		}
		return numSamples == 0 ? null : accumulator / numSamples;
	}

	private boolean downloadContainerAlarmData(PerifericoProtocol pp,
			UUID stationUUID, ContainerAlarm alarm) throws SQLException,
			IOException, ProtocolException, PollingException {
		boolean all = true;
		UUID alarmUUID = str2uuid(alarm.getAlarmUUID());
		Date lastAlarmDate = getDBM().getLastDataTimestamp(station.getStInfo(),
				alarm.getAlarmDbID());
		List<AlarmData> listAlarmData = new ArrayList<AlarmData>();
		if ("TRIGGER".equalsIgnoreCase(alarm.getType())) {
			List<TriggerAlarmDatum> caData = pp.getTriggerCAData(getConn(),
					stationUUID, alarmUUID, alarm.getAlarmID(), lastAlarmDate,
					null);
			all &= caData.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
			for (TriggerAlarmDatum tad : caData)
				listAlarmData.add(new AlarmData(tad));
		} else if ("DIGITAL".equalsIgnoreCase(alarm.getType())) {
			List<BooleanDatum> caData = pp.getDigitalCAData(getConn(),
					stationUUID, alarmUUID, alarm.getAlarmID(), lastAlarmDate,
					null);
			for (BooleanDatum bd : caData)
				listAlarmData.add(new AlarmData(bd));
		} else {
			throw new PollingException("Unknow alarm type '" + alarm.getType()
					+ "'");
		}
		if (!listAlarmData.isEmpty()) {
			Integer[] key = { alarm.getAlarmDbID(), null };
			InsertableObject io = new InsertableObject(key, listAlarmData);
			Centrale.getInstance().getDataQueue().offer(io);
		}
		return all;
	}

	private boolean downloadGpsData(PerifericoProtocol pp, UUID stationUUID)
			throws SQLException, IOException, ProtocolException {
		boolean all = true;
		Date lastDatumDate = getDBM().getLastGpsDataTimestamp(
				station.getStInfo(), station.getStationId());
		List<GpsDatum> data = pp.getGpsData(getConn(), stationUUID, true,
				lastDatumDate, null);
		all &= data.size() < PerifericoProtocol.MAX_DATA_FOR_REQUEST;
		List<GpsData> gpsData = new ArrayList<GpsData>();
		for (GpsDatum gd : data)
			gpsData.add(new GpsData(gd));
		if (gpsData.isEmpty()) {
			station.getStStatus().setGpsStatus(true);
		} else {
			station.getStStatus().setGpsStatus(false);
			Integer[] key = { station.getStationId(), null };
			InsertableObject io = new InsertableObject(key, gpsData);
			Centrale.getInstance().getDataQueue().offer(io);
			Centrale.getInstance().getDataQueue()
					.offer(io.makeDbAriaLoaderTrigger());
		}
		return all;
	}

	private void saveData(GenericElement element, Integer period,
			List<? extends Datum> data) throws PollingException {
		if (data.isEmpty())
			return;
		ListGenericData listGeneric = new ListGenericData();
		for (Datum datum : data)
			listGeneric.addElement(GenericData.newInstance(datum));
		Integer[] key = { element.getElementId(), period };
		InsertableObject io = new InsertableObject(key,
				listGeneric.getLstData());
		Centrale.getInstance().getDataQueue().offer(io);
		if (period != null)
			Centrale.getInstance().getDataQueue()
					.offer(io.makeDbAriaLoaderTrigger());
	}

	private void downloadLastConfigFile(PerifericoProtocol pp)
			throws IOException, ProtocolException, PollingException {
		UUID stationUUID = str2uuid(station.getStationUUID());
		long lastConfigFileTimestamp;
		try {
			lastConfigFileTimestamp = getDBM().getLastConfigTimestamp(
					station.getStationId()).getTime();
		} catch (SQLException e) {
			throw new DbException("Error reading configuration file "
					+ "timestamp for station '" + station.getName() + "' (id="
					+ station.getStationId() + ")", e);
		}
		long[] stationConfigFileTimestamp = { 0 };
		List<String> cfgLines = pp.getConfigFile(getConn(), stationUUID,
				lastConfigFileTimestamp, stationConfigFileTimestamp);
		if (cfgLines == null) {
			logger.warn("Configuration file not available for station '"
					+ station.getName() + "' with timestamp after "
					+ lastConfigFileTimestamp);
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String line : cfgLines)
			sb.append(line);
		logger.trace("Config file: " + sb.toString());
		String error = getDBM().writeConfiguration(station.getStationId(), sb,
				stationConfigFileTimestamp[0]);
		if (error != null)
			throw new DbException("Error writing configuration file "
					+ "for station '" + station.getName() + "' (id="
					+ station.getStationId() + "): " + error);
	}

	private boolean isCommonConfigToSend() {
		String disable = System.getProperty("centrale.commonconfig.disable",
				null);
		if ("true".equalsIgnoreCase(disable)) {
			logger.warn("Sending common configuration disabled by user config");
			return false;
		}
		Date copDate = Centrale.getInstance().getConfigInfo()
				.getCommonConfigUpdateDate();
		if (copDate == null) {
			logger.error("No common configuration available");
			return false;
		}
		if (!station.getStInfo().isCommonConfigSendingEnable()) {
			logger.warn("Station '" + station.getName() + "' is not enabled "
					+ "to receive common configuration");
			return false;
		}
		Date stationDate = station.getStInfo()
				.getCommonConfigStationUpdateDate();
		logger.debug("Common configuration date on Cop is " + copDate
				+ ", on station " + station.getName() + " is " + stationDate);
		return stationDate == null || copDate.after(stationDate);
	}

	private void sendCommonConfig(PerifericoProtocol pp,
			StationStatus stationStatus) throws IOException, ProtocolException,
			PollingException {
		String commonCfgStr;
		try {
			CommonCfg commonCfg = getDBM().getCommonConfig();
			commonCfgStr = new ConfigManager().writeCommonCfg(commonCfg);
		} catch (SQLException e) {
			throw new DbException("Error reading common configuration", e);
		} catch (ConfigException e) {
			throw new PollingException("Common configuration error", e);
		} catch (XMLException e) {
			throw new PollingException("Error converting common configuration"
					+ " to string", e);
		} catch (MappingException e) {
			throw new PollingException("Error converting common configuration"
					+ " to string", e);
		}
		UUID stationUUID = str2uuid(station.getStationUUID());
		CommonConfigResult ccr = pp.setCommonFile(getConn(), stationUUID,
				commonCfgStr);
		logger.info("Common configuration sent to station '"
				+ station.getName() + "' with result: " + ccr);
		stationStatus.setCommonConfigResult(ccr);
		if (ccr == CommonConfigResult.OK) {
			Date now = new Date();
			station.getStInfo().setCommonConfigStationUpdateDate(now);
			String error = getDBM().updateCommonConfigDate(now,
					station.getStationId());
			if (error != null)
				throw new DbException("Error updating common configuration "
						+ "date for station '" + station.getName() + "' (id="
						+ station.getStationId() + "): " + error);
		}
		try {
			getDBM().updateCommonConfigInfo(
					stationStatus.getCmmCfgLastUpdateStatus(), ccr.toString(),
					station.getStationId());
		} catch (SQLException e) {
			throw new DbException("Error updating common configuration "
					+ "result for station '" + station.getName() + "' (id="
					+ station.getStationId() + ")", e);
		}
	}

	private String uuid2str(UUID uuid) {
		return uuid == null ? null : uuid.toString();
	}

	private UUID str2uuid(String string) throws PollingException {
		try {
			return string == null ? null : UUID.fromString(string);
		} catch (IllegalArgumentException e) {
			throw new PollingException("Cannot convert string '" + string
					+ "' to UUID", e);
		}
	}

}
