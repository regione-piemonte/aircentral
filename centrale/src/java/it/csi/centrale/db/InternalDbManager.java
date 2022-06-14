/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: manager for operations on Internal Db
 * Change log:
 *   2008-09-24: initial version
 * ----------------------------------------------------------------------------
 * $Id: InternalDbManager.java,v 1.151 2015/10/19 13:22:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleException;
import it.csi.centrale.data.station.AlarmData;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.AvgPeriod;
import it.csi.centrale.data.station.CommDeviceInfo;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.ContainerAlarm;
import it.csi.centrale.data.station.GenericData;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.GpsData;
import it.csi.centrale.data.station.InsertableObject;
import it.csi.centrale.data.station.ModemConf;
import it.csi.centrale.data.station.RainScalarData;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.data.station.StationStatus;
import it.csi.centrale.data.station.VirtualCop;
import it.csi.centrale.data.station.WindData;
import it.csi.centrale.data.station.WindElement;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.stationreport.StationConfigHolder;
import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.data.AvgPeriodInfo;
import it.csi.centrale.ui.client.data.InformaticStatusObject;
import it.csi.centrale.ui.client.data.MeasureUnitInfo;
import it.csi.centrale.ui.client.data.ModemInfo;
import it.csi.centrale.ui.client.data.OtherInfo;
import it.csi.centrale.ui.client.data.ParameterInfo;
import it.csi.centrale.ui.client.data.StandardInfo;
import it.csi.centrale.ui.client.data.StorageManagerInfo;
import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.KeyValueObject;
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.periferico.config.common.CommonCfg;
import it.csi.periferico.config.common.ConfigException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Manager for operations on Internal Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class InternalDbManager {
	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale."
			+ InternalDbManager.class.getSimpleName());

	private CentraleDb internalDb;

	private Connection connection;

	private DbReader reader;

	private DbWriter writer;

	public InternalDbManager() throws SQLException {
		connect();
	}

	public void connect() throws SQLException {

		Centrale.getInstance().getCentraleDb().printConnectionPoolVariable();
		this.internalDb = Centrale.getInstance().getCentraleDb();
		logger.debug("making connection to internal db");
		connection = internalDb.connect();
		reader = new DbReader(connection);
		writer = new DbWriter(connection);
		Centrale.getInstance().getCentraleDb().printConnectionPoolVariable();
	}

	public void disconnect() throws SQLException {
		logger.debug("close connection to internal db");
		Centrale.getInstance().getCentraleDb().printConnectionPoolVariable();
		connection.close();
		connection = null;
		reader = null;
		writer = null;
		Centrale.getInstance().getCentraleDb().printConnectionPoolVariable();
	}

	public boolean isConnected() throws SQLException {
		if (connection == null)
			return false;
		return !connection.isClosed();
	}

	public boolean isConnectionUp() throws SQLException {
		if (reader.checkConnection().equals(new Integer(1)))
			return true;
		return false;
	}

	/*
	 * Methods for read information from DB
	 */
	/*
	 * public List<ConfigInfo> loadListConfigInfo(List<Integer> idCopList)
	 * throws SQLException, CentraleException { List<ConfigInfo> configInfoList
	 * = new ArrayList<ConfigInfo>(); for (int i = 0; i < idCopList.size(); i++)
	 * { configInfoList.add(loadConfigInfo(idCopList.get(i))); } return
	 * configInfoList; }
	 */

	/*
	 * public ConfigInfo loadConfigInfo(Integer idConfig) throws SQLException,
	 * CentraleException { ConfigInfo configInfo = new ConfigInfo();
	 * 
	 * configInfo = reader.readConfig(idConfig); if (configInfo != null) {
	 * configInfo.setStVector(reader .readStations(configInfo.getConfigId()));
	 * setAllDataDownloadedInStTrees(configInfo.getStVector()); } else {
	 * configInfo = new ConfigInfo(); configInfo.setStVector(new
	 * Vector<Station>()); }
	 * configInfo.setAnalyzerStatusType(reader.readAnalyzerAlarmType()); return
	 * configInfo; }
	 */
	public String getTimeHost(
			CommDeviceInfo.CommunicationDevice communicationDevice)
			throws SQLException {
		return reader.getTimeHost(communicationDevice);
	}

	public ConfigInfo loadConfigInfo() throws SQLException, CentraleException {
		ConfigInfo configInfo = new ConfigInfo();

		configInfo = reader.readConfig(Centrale.CONFIG_ID);
		if (configInfo != null) {
			configInfo.setVirtualCopVector(reader.readVirtualCop(configInfo
					.getConfigId()));
			for (int i = 0; i < configInfo.getVirtualCopVector().size(); i++) {
				VirtualCop virtualCop = configInfo.getVirtualCopVector().get(i);
				configInfo.setStVector(reader.readStations(virtualCop
						.getVirtualCopId()));
			}
		} else {
			configInfo = new ConfigInfo();
			configInfo.setVirtualCopVector(new Vector<VirtualCop>());
			configInfo.setStVector(new Vector<Station>());
		}
		configInfo.setAnalyzerStatusType(reader.readAnalyzerAlarmType());
		return configInfo;
	}

	public Vector<ModemConf> readModemConf() throws SQLException {
		return reader.readModemVect();
	}

	public Station readStation(int stationId) throws SQLException {
		Station station = reader.readStation(stationId);
		return station;
	}

	public Date getLastDataTimestamp(StationInfo stInfo, int stationId,
			GenericElement element, Integer avgPeriodId) throws SQLException {
		Date lastTimestamp = null;
		// case of sample data
		/*
		 * logger.debug("************* getlastSampleTimestamp avgPeriodId:" +
		 * avgPeriodId + " instance " + (element instanceof ScalarElement));
		 */

		if (avgPeriodId == null) {
			if (element instanceof ScalarElement)
				lastTimestamp = reader.readLastSampleTimestamp(element
						.getElementId());
		}
		// case of mean data
		else {
			if (element instanceof ScalarElement)
				lastTimestamp = reader.readLastMeanTimestamp(
						element.getElementId(), avgPeriodId);
			else if (element instanceof WindElement)
				lastTimestamp = reader.readLastMeanWindTimestamp(
						element.getElementId(), avgPeriodId);
		}
		// compare stationMinTimestamp with lastTimestamp
		/*
		 * logger.debug("************* getlastSampleTimestamp lastTimestamp " +
		 * lastTimestamp);
		 */
		lastTimestamp = compareLastTimestamp(lastTimestamp,
				stInfo.getMinTimestampForPolling());

		logger.debug("Last datum timestamp for element '" + element.getName()
				+ "', period '" + avgPeriodId + "' is: " + lastTimestamp);
		return lastTimestamp;
	}

	Date getLastMeansDataTimestamp(GenericElement element, Integer avgPeriodId)
			throws SQLException {
		Date lastTimestamp = null;
		if (element instanceof ScalarElement)
			lastTimestamp = reader.readLastMeanTimestamp(
					element.getElementId(), avgPeriodId);
		else if (element instanceof WindElement)
			lastTimestamp = reader.readLastMeanWindTimestamp(
					element.getElementId(), avgPeriodId);
		return lastTimestamp;
	}

	public Date getLastDataTimestamp(StationInfo stInfo, int stationAlarmId)
			throws SQLException {
		Date lastTimestamp = reader
				.readLastCAlarmStatusTimestamp(stationAlarmId);

		// compare stationMinTimestamp with lastTimestamp
		lastTimestamp = compareLastTimestamp(lastTimestamp,
				stInfo.getMinTimestampForPolling());

		logger.debug("Last datum timestamp for container alarm '"
				+ stationAlarmId + "' is: " + lastTimestamp);
		return lastTimestamp;
	}

	public Date getLastDataTimestamp(StationInfo stInfo, int analyzerId,
			String anAlarmType) throws SQLException {
		Date lastTimestamp = reader.readLastAnalyzerAlarmStatusTimestamp(
				analyzerId, anAlarmType);

		// compare stationMinTimestamp with lastTimestamp
		lastTimestamp = compareLastTimestamp(lastTimestamp,
				stInfo.getMinTimestampForPolling());

		logger.debug("Last datum timestamp for analyzer '" + analyzerId
				+ "', alarm '" + anAlarmType + "' is: " + lastTimestamp);
		return lastTimestamp;
	}

	public Date getLastGpsDataTimestamp(StationInfo stInfo, int stationId)
			throws SQLException {
		Date lastTimestamp = reader.readLastGpsDataTimestamp(stationId);

		// compare stationMinTimestamp with lastTimestamp
		lastTimestamp = compareLastTimestamp(lastTimestamp,
				stInfo.getMinTimestampForPolling());

		logger.debug("Last datum timestamp for gps is: " + lastTimestamp);
		return lastTimestamp;
	}

	public Date getLastConfigTimestamp(int stationId) throws SQLException {
		long lastConfigFileTime = reader.readLastConfiguration(stationId);
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(lastConfigFileTime);
		Date lastTimestamp = cal.getTime();

		// compare stationMinTimestamp with lastTimestamp
		lastTimestamp = compareLastTimestamp(lastTimestamp, null);

		logger.debug("Last config timestamp is: " + lastTimestamp);
		return lastTimestamp;
	}

	public Vector<List<DataObject>> readMeansData(Station station)
			throws SQLException {
		Vector<List<DataObject>> dataListVector = new Vector<List<DataObject>>(
				2);
		// il vettore contiene nel primo campo la lista dei dati degli
		// analizzatori attivi e nel secondo la lista dei dati degli
		// analizzatori ancellati
		List<DataObject> dataList = new ArrayList<DataObject>();
		List<DataObject> dataListDeletedAnalyzer = new ArrayList<DataObject>();
		for (int i = 0; i < station.getAnalyzersVect().size(); i++) {
			Analyzer analyzer = station.getAnalyzersVect().get(i);
			for (int j = 0; j < analyzer.getElements().size(); j++) {
				GenericElement element = analyzer.getElements().get(j);
				for (int k = 0; k < element.getAvgPeriodSize(); k++) {
					AvgPeriod avgPeriod = element.getAvgPeriod().get(k);
					if (element instanceof ScalarElement) {
						Date lastTimestamp = reader.readLastMeanTimestamp(
								element.getElementId(),
								avgPeriod.getAvgPeriodVal());
						DataObject dataObj = reader.readMeansScalarData(
								element.getElementId(),
								avgPeriod.getAvgPeriodVal(), lastTimestamp);
						if (dataObj != null) {
							dataObj.setMeasureUnit(((ScalarElement) element)
									.getUnit());
							dataObj.setAnalyzerId(analyzer.getAnalyzerId());
							dataObj.setAnalyzerName(analyzer.getName());
							dataObj.setElementName(element.getName());
							dataObj.setNumDec(((ScalarElement) element)
									.getDecimalQuantityValue());
							if (analyzer.getStatus().equals("DELETED")
									&& analyzer.getDeletionDate() != null) {
								dataListDeletedAnalyzer.add(dataObj);
							} else
								dataList.add(dataObj);
						}
					} else if (element instanceof WindElement) {
						Date lastTimestamp = reader.readLastMeanWindTimestamp(
								element.getElementId(),
								avgPeriod.getAvgPeriodVal());
						DataObject dataObj = reader.readMeansWindData(
								element.getElementId(),
								avgPeriod.getAvgPeriodVal(), lastTimestamp);
						if (dataObj != null) {
							dataObj.setMeasureUnit(((WindElement) (element))
									.getSpeedUnit());
							((WindDataObject) (dataObj)).setDirMeasureUnit(((WindElement) (element))
									.getDirUnit());
							dataObj.setAnalyzerId(analyzer.getAnalyzerId());
							dataObj.setAnalyzerName(analyzer.getName());
							dataObj.setElementName(element.getName());
							dataObj.setNumDec(((WindElement) element)
									.getSpeedDecimalQuantityValue());
							((WindDataObject) (dataObj)).setDirNumDec(((WindElement) element)
									.getDirDecimalQuantityValue());
							if (analyzer.getStatus().equals("DELETED")
									&& analyzer.getDeletionDate() != null)
								dataListDeletedAnalyzer.add(dataObj);
							else
								dataList.add(dataObj);
						}
					}
				}
			}
		}
		dataListVector.add(dataList);
		dataListVector.add(dataListDeletedAnalyzer);
		return dataListVector;
	}


	public List<GpsData> readGpsDataOnUpdateDate(int stationId,
			Date currentTimestamp, Date lastStationDateOnMatchedDb,
			int toleranceInMillis) throws SQLException {
		return reader.readGpsDataOnUpdateDate(stationId, currentTimestamp,
				lastStationDateOnMatchedDb, toleranceInMillis);
	}

	public Boolean readMeansData(int elementId, int avgPeriodId,
			Date startDate, Date endDate, List<DataObject> dataList,
			Integer numMaxData, String type) throws SQLException {
		Boolean maxDataReached = new Boolean(false);
		if (dataList == null)
			dataList = new ArrayList<DataObject>();
		if (GenericElement.SCALAR.equals(type))
			maxDataReached = reader.readMeansScalarData(elementId, avgPeriodId,
					startDate, endDate, dataList, numMaxData);
		else if (GenericElement.WIND.equals(type))
			maxDataReached = reader.readMeansWindData(elementId, avgPeriodId,
					startDate, endDate, dataList, numMaxData);
		return maxDataReached;
	}

	public List<Date> readConfigFileDateList(int stationId, Date startDate,
			Date endDate, Integer maxNumber) throws SQLException {
		return reader.readConfigFileDateList(stationId, startDate, endDate,
				maxNumber);
	}

	public String getMapsSiteUrlFormatter() throws SQLException {
		return reader.getMapsSiteURLFormatter(Centrale.CONFIG_ID);
	}

	public StringBuilder readConfigFile(int stationId, long lastModifiedTime)
			throws SQLException {
		return reader.readConfiguration(stationId, lastModifiedTime);
	}// end readConfigFile

	public List<StationConfigHolder> readActiveConfigurations()
			throws SQLException {
		return reader.readActiveConfigurations();
	}

	public Long getLastStationModifiedConfDate(int stationId)
			throws SQLException {
		return reader.getLastStationModifiedConfDate(stationId);
	}

	public List<String> readPhysicalDimension() throws SQLException {
		return reader.readPhysicalDimension();
	}// end readPhysicalDimension

	public List<String> readModem() throws SQLException {
		return reader.readModem();
	}// end readModem

	public ModemInfo readModemInfo(String deviceId) throws SQLException {
		return reader.readModemInfo(deviceId);
	}// end readModemInfo

	public List<AvgPeriodInfo> readAvgPeriod() throws SQLException {
		return reader.readAvgPeriod();
	}// end readAvgPeriod

	public List<KeyValueObject> readAlarmName() throws SQLException {
		return reader.readAlarmName();
	}

	public List<KeyValueObject> readParameter() throws SQLException {
		return reader.readParameter();
	}

	public List<KeyValueObject> readMeasureUnit() throws SQLException {
		return reader.readMeasureUnit();
	}

	public ParameterInfo readParameterInfo(String param_id) throws SQLException {
		return reader.readParameterInfo(param_id);
	}

	public MeasureUnitInfo readMeasureUnitInfo(String measureName)
			throws SQLException {
		return reader.readMeasureUnitInfo(measureName);
	}// end readMeasureUnitInfo

	public String getDoorAlarmId() throws SQLException {
		return reader.readDoorAlarmId(Centrale.CONFIG_ID);
	}// end getDoorAlarmId

	public AlarmNameInfo readAlarmNameInfo(String alarmId) throws SQLException {
		return reader.readAlarmName(alarmId);
	}// end readAlarmNameInfo

	public StorageManagerInfo readStorageManager() throws SQLException {
		return reader.readStorageManager(Centrale.CONFIG_ID);
	}// end readStorageManager

	public OtherInfo readOther() throws SQLException {
		return reader.readOther(Centrale.CONFIG_ID);
	}// end readOther

	public StandardInfo readStandard() throws SQLException {
		return reader.readStandard(Centrale.CONFIG_ID);
	}// end readStandard

	public String deletePhysicalDimension(String name, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.deletePhysicalDimension(name);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end deletePhysicalDimension

	public String deleteModem(String deviceId) throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		errObj = writer.deleteModem(deviceId);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end deleteModem

	public String deleteAvgPeriod(int avgPeriod, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.deleteAvgPeriod(avgPeriod);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end deleteAvgPeriod

	public String deleteParameter(String param_id, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.deleteParameter(param_id);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end deleteParameter

	public String deleteMeasureUnit(String name, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.deleteMeasureUnit(name);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end deleteMeasureUnit

	public String deleteAlarmName(String alarm_id, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.deleteAlarmName(alarm_id);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end deleteAlarmName

	public String addPhysicalDimension(String name, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.addPhysicalDimension(name, updateDate);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end addPhysicalDimension

	public String addAvgPeriod(Integer avgPeriodId, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.addAvgPeriod(avgPeriodId, updateDate);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end addAvgPeriod

	public String insertParameter(ParameterInfo parInfo, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.insertParameter(parInfo, updateDate);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end insertParameter

	public String insertModem(ModemInfo modemInfo) throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		errObj = writer.insertModem(modemInfo);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end insertModem

	public String insertMeasureUnit(MeasureUnitInfo measureInfo, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.insertMeasureUnit(measureInfo, updateDate);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end insertMeasureUnit

	public String insertAlarmName(AlarmNameInfo alarmInfo, Date updateDate)
			throws SQLException {
		RejectedObject errObj = null;
		String errMsg = null;
		writer.setAutoCommit(false);
		errObj = writer.insertAlarmName(alarmInfo, updateDate);
		if (errObj == null)
			errObj = writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID,
					updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end insertAlarmName

	public String updateStorageManager(StorageManagerInfo storageManager,
			Date updateDate) {
		RejectedObject errObj = null;
		String errMsg = null;
		errObj = writer.updateStorageManager(storageManager,
				Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end updateStorageManager

	public String updateStandard(StandardInfo standard, Date updateDate) {
		RejectedObject errObj = null;
		String errMsg = null;
		errObj = writer
				.updateStandard(standard, Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end updateStandard

	public String updateOther(OtherInfo other, Date updateDate) {
		RejectedObject errObj = null;
		String errMsg = null;
		errObj = writer.updateOther(other, Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end updateOther

	public List<Date[]> getCalibrationIntervals(StationInfo stInfo,
			int stationId, GenericElement element) throws SQLException {
		logger.debug("Searching calibration intervals for station with id '"
				+ stationId + "', element '" + element.getName() + "'");
		Date lastSampleTimestamp = null;
		if (element instanceof ScalarElement)
			lastSampleTimestamp = getLastDataTimestamp(stInfo, stationId,
					element, null);
		else if (element instanceof WindElement)
			lastSampleTimestamp = getLastDataTimestamp(stInfo, stationId,
					((WindElement) element).getSpeed(), null);
		else
			throw new IllegalStateException("Unknown element type '"
					+ element.getClass().getSimpleName() + "'");
		Integer minAvgPeriod = null;
		for (AvgPeriod ap : element.getAvgPeriod()) {
			if (!ap.isActive())
				continue;
			if (minAvgPeriod == null || minAvgPeriod > ap.getAvgPeriodVal())
				minAvgPeriod = ap.getAvgPeriodVal();
		}
		List<Date[]> intervalsList = new ArrayList<Date[]>();
		if (minAvgPeriod == null) {
			logger.debug("No active aggregation period found for element '"
					+ element.getName() + "'");
			return intervalsList;
		}
		logger.trace("Minimum aggregation period is " + minAvgPeriod);
		List<GenericData> dataList;
		if (element instanceof ScalarElement)
			dataList = reader.readScalarLastCalibrationMeans(
					element.getElementId(), minAvgPeriod, lastSampleTimestamp);
		else if (element instanceof WindElement)
			dataList = reader.readWindLastCalibrationMeans(
					element.getElementId(), minAvgPeriod, lastSampleTimestamp);
		else
			throw new IllegalStateException("Unknown element type '"
					+ element.getClass().getSimpleName() + "'");
		if (dataList.isEmpty()) {
			logger.debug("No aggregations with calibration found for element '"
					+ element.getName() + "'");
			return intervalsList;
		}
		logger.trace(dataList.size() + " aggregations with calibration found "
				+ "for element '" + element.getName() + "'");
		Date[] interval = { null, null };
		for (GenericData datum : dataList) {
			Date[] newInterval = appendTo(interval, datum, minAvgPeriod);
			if (newInterval != null) {
				intervalsList.add(interval);
				interval = newInterval;
			}
		}
		intervalsList.add(interval);
		logger.debug("Found " + intervalsList.size() + " distinct intervals of"
				+ " calibration for element '" + element.getName() + "'");
		return intervalsList;
	}

	private Date[] appendTo(Date[] interval, GenericData datum, int period) {
		Date[] newInterval = null;
		Date startTime = getAcqStartTime(datum, period);
		if (interval[0] == null) {
			interval[0] = startTime;
			interval[1] = datum.getTimestamp();
		} else if (startTime.equals(interval[1])) {
			interval[1] = datum.getTimestamp();
		} else {
			newInterval = new Date[] { startTime, datum.getTimestamp() };
		}
		return newInterval;
	}

	private Date getAcqStartTime(GenericData datum, int period) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(datum.getTimestamp());
		cal.add(Calendar.MINUTE, -period);
		return cal.getTime();
	}

	public Boolean readSampleData(int elementId, Date startDate, Date endDate,
			List<DataObject> dataList, Integer numMaxData) throws SQLException {
		if (dataList == null)
			dataList = new ArrayList<DataObject>();
		return reader.readSampleData(elementId, startDate, endDate, dataList,
				numMaxData);

	}

	public int countSampleData(int elementId, Date startDate, Date endDate)
			throws SQLException {
		return reader.countSampleData(elementId, startDate, endDate);
	}

	public Boolean readAnalyzerAlarmStatusHistory(int analyzerId,
			Date startDate, Date endDate,
			List<AnalyzerAlarmStatusValuesObject> alarmDataList,
			Integer numMaxData, String alarmType) throws SQLException {
		return reader.readAnalyzerAlarmStatusHistory(analyzerId, alarmType,
				startDate, endDate, numMaxData, alarmDataList);
	}

	public Boolean readStationAlarmStatusHistory(int stationAlarmId,
			Date startDate, Date endDate,
			List<AlarmStatusObject> alarmDataList, Integer numMaxData)
			throws SQLException {
		return reader.readStationAlarmStatusHistory(stationAlarmId, startDate,
				endDate, numMaxData, alarmDataList);
	}

	public InformaticStatusObject getInformaticStatusObject(int stationId)
			throws SQLException {
		InformaticStatusObject informaticStatusObj = new InformaticStatusObject();
		informaticStatusObj.setStationId(stationId);
		Date informaticStatusTimestamp = reader
				.readLastInformaticStatusTimestamp(stationId);
		PerifericoAppStatus appStatus = null;
		DiskStatus diskStatus = null;
		if (informaticStatusTimestamp != null) {
			appStatus = reader.readPerifericoAppStatusHistory(stationId,
					informaticStatusTimestamp);
			diskStatus = reader.readPerifericoDiskStatusHistory(stationId,
					informaticStatusTimestamp);
		}
		Date gpsTimestamp = reader.readLastGpsDataTimestamp(stationId);
		GpsData gpsData = null;
		if (gpsTimestamp != null)
			gpsData = reader.readGpsData(stationId, gpsTimestamp);
		if (appStatus != null) {
			informaticStatusObj.setOK(appStatus.isOK());
			informaticStatusObj.setBoardManInitStatus(appStatus
					.getBoardManagerInitStatus());
			informaticStatusObj.setCfgBoardsNumber(appStatus
					.getConfiguredBoardsNumber());
			informaticStatusObj.setInitBoardsNumber(appStatus
					.getInitializedBoardsNumber());
			informaticStatusObj.setFailedBoardBindingsNumber(appStatus
					.getFailedBoardBindingsNumber());
			informaticStatusObj.setDpaOk(appStatus.isDataPortAnalyzersOK());
			informaticStatusObj.setDriverConfigsOk(appStatus.isDriverConfigsOK());
			informaticStatusObj.setEnabledDPANumber(appStatus
					.getEnabledDataPortAnalyzersNumber());
			informaticStatusObj.setInitDPADriversNumber(appStatus
					.getInitializedDataPortDriversNumber());
			informaticStatusObj.setFailedDPAThreadsNumber(appStatus
					.getFailedDataPortThreadsNumber());
			informaticStatusObj.setLoadCfgStatus(appStatus
					.getLoadConfigurationStatus().toString());
			informaticStatusObj.setSaveCfgStatus(appStatus
					.getSaveNewConfigurationStatus());
			informaticStatusObj.setCfgActivationStatus(appStatus
					.getConfigActivationStatus());
			informaticStatusObj.setTotalThreadFailures(appStatus
					.getTotalThreadFailures());
			informaticStatusObj.setCurrentThreadFailures(appStatus
					.getCurrentThreadFailures());
			informaticStatusObj.setDataWriteErrorCount(appStatus
					.getDataWriteErrorCount());
			informaticStatusObj.setWarningLevel(diskStatus
					.getSpaceWarnThreshold());
			informaticStatusObj.setAlarmLevel(diskStatus
					.getSpaceAlarmThreshold());
			informaticStatusObj.setFilesystem(diskStatus.getFsSpaceMap());
			informaticStatusObj.setSmartStatus(diskStatus.getSmartStatus()
					.toString());
			informaticStatusObj.setRaidStatus(diskStatus.getRaidStatus()
					.toString());
			informaticStatusObj.setCommonCfgFromCopStatus(appStatus
					.getCommonConfigFromCopStatus() == null ? null : appStatus
					.getCommonConfigFromCopStatus().toString());
			informaticStatusObj.setDateInFuture(appStatus.isDataInTheFuture());
		}
		if (gpsData != null) {
			informaticStatusObj.setGpsTimestamp(gpsData.getTimestamp());
			informaticStatusObj.setGpsLatitude(gpsData.getLatitude());
			informaticStatusObj.setGpsLongitude(gpsData.getLongitude());
			informaticStatusObj.setGpsAltitude(gpsData.getAltitude());
			informaticStatusObj.setGpsFix(gpsData.getComment());
		}

		informaticStatusObj.setCommonConfigLastUpdateStatus(reader
				.getCommonConfigLastUpdateStatus(stationId));
		informaticStatusObj.setCommonConfigProblem(reader
				.getCommonConfigProblem(stationId));

		return informaticStatusObj;
	}

	/*
	 * Methods for write information on DB
	 */

	public int writeStConnInfo(StationInfo stationInfo)
			throws DbManagerException {
		try {
			writer.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DbManagerException(e.getMessage());
		}
		int newStationId;
		try {
			RejectedObject errObj = writer.writeConnStationInfo(stationInfo);
			if (errObj != null) {
				logger.error("Error: " + errObj.getSqlEx() + " on object: "
						+ errObj.getObj());
				throw new DbManagerException(errObj.getSqlEx().getMessage());
			}
			newStationId = reader.readStationId();
			logger.debug("New station id: " + newStationId);
			writer.commit();
			logger.debug("Commit done");
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage());
			String errMsg = e.getMessage();
			try {
				writer.rollback();
				writer.setAutoCommit(true);
				logger.debug("Rollback done");
			} catch (SQLException sqlEx) {
				logger.error(sqlEx);
				errMsg += " " + sqlEx.getMessage();
			}
			throw new DbManagerException(errMsg);
		}
		try {
			writer.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DbManagerException(e.getMessage());
		}
		return newStationId;
	}

	public void updateStConnInfo(int stationId, StationInfo stationInfo,
			boolean updateAnagraphicInfo) throws DbManagerException {
		RejectedObject errObj = writer.updateConnStationInfo(stationId,
				stationInfo, updateAnagraphicInfo);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object: " + errObj.getObj());
			throw new DbManagerException(errObj.getSqlEx().getMessage());
		}
	}

	// Method to get common config from Database
	public CommonCfg getCommonConfig() throws SQLException, ConfigException {
		logger.info("Reading common config from database...");
		CommonCfg commonCfg = new CommonCfg();
		commonCfg.setAlarmNames(reader.readAllAlarmNames());
		commonCfg.setAvgPeriods(reader.readNotDefaultAvgPeriod());
		commonCfg.setDefaultAvgPeriod(reader.getDefaultAvgPeriod());
		commonCfg.setCopServicePort(reader
				.getCopServicePort(Centrale.CONFIG_ID));
		commonCfg.setDataWriteToDiskPeriod(reader
				.getDataWriteToDiskPeriod(Centrale.CONFIG_ID));
		commonCfg.setManualOperationsAutoResetPeriod(reader
				.getManualOperationAutoResetPeriod(Centrale.CONFIG_ID));
		commonCfg.setMapsSiteURLFormatter(reader
				.getMapsSiteURLFormatter(Centrale.CONFIG_ID));
		commonCfg.setMeasureUnits(reader.readAllMeasureUnit());
		commonCfg.setParameters(reader.readAllParameter());
		commonCfg.setStandards(reader.readAllStandard(Centrale.CONFIG_ID));
		commonCfg.setStorageManagerCfg(reader
				.readAllStorageManager(Centrale.CONFIG_ID));
		logger.info("Done.");

		return commonCfg;
	}// end getCommonConfig

	public String writeConfigInfo(ConfigInfo config, boolean isNewCop) {
		String errMsg = null;
		RejectedObject errObj = null;
		if (!isNewCop)
			errObj = writer.updateConfigInfo(config);
		else
			errObj = writer.writeConfigInfo(config);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String[] writePollingInfo(Station station, boolean isNewStation,
			int copId) {
		String[] retStrArray = new String[2];
		String errMsg = null;
		RejectedObject errObj = writer.updatePollingInfo(station);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		} else {
			retStrArray[1] = new Integer(station.getStationId()).toString();
			logger.debug("1 imposto station id a: " + retStrArray[1]);
		}

		retStrArray[0] = errMsg;
		return retStrArray;
	}

	public String[] writeStInfo(Station station, boolean isNewStation,
			int configId) {
		String[] retStrArray = new String[2];
		String errMsg = null;
		RejectedObject errObj = null;
		if (!isNewStation) {
			errObj = writer.updateConnectionAnagraphicStationInfo(station);
			if (errObj != null) {
				logger.error("Error: " + errObj.getSqlEx() + " on object:"
						+ errObj.getObj());
				errMsg = errObj.getSqlEx().getMessage();
			} else {
				retStrArray[1] = new Integer(station.getStationId()).toString();
				// logger.debug("1 imposto station id a: " + retStrArray[1]);
			}
		} else {
			try {
				writer.setAutoCommit(false);
			} catch (SQLException e) {
				errMsg = e.getMessage();
			}
			errObj = writer.writeStationInfo(station, configId);
			if (errObj != null) {
				logger.error("Error: " + errObj.getSqlEx() + " on object:"
						+ errObj.getObj());
				errMsg = errObj.getSqlEx().getMessage();
				try {
					writer.rollback();
					writer.setAutoCommit(true);
					logger.debug("Rollback done");
				} catch (SQLException e) {
					errMsg += e.getMessage();
				}
			} else {

				try {
					int newStationId = reader.readStationId();
					retStrArray[1] = new Integer(newStationId).toString();
				} catch (Exception e) {
					logger.error("Error: ", e);
					errMsg += e.getMessage();
					try {
						writer.rollback();
						writer.setAutoCommit(true);
						logger.debug("Rollback done");
					} catch (SQLException sqlEx) {
						errMsg += sqlEx.getMessage();
					}
				}
			}
			if (errMsg == null) {
				try {
					writer.commit();
					writer.setAutoCommit(true);
					logger.debug("Commit done");
				} catch (SQLException e) {
					errMsg = e.getMessage();
				}
			}
		}// end else new station

		retStrArray[0] = errMsg;
		return retStrArray;
	}

	public String writeStTree(Station station, boolean isNewStation, int copId) {
		String errMsg = null;
		try {
			writer.setAutoCommit(false);
		} catch (SQLException e) {
			errMsg = e.getMessage();
		}

		if (errMsg == null) {
			// write station info
			RejectedObject errObj = null;
			if (isNewStation) {
				errObj = writer.writeStationInfo(station, copId);
				try {
					station.setStationId(reader.readStationId());
				} catch (Exception readingEx) {
					logger.error("Error 1: " + readingEx
							+ " on reading object:" + station.getStationId(),
							readingEx);
					errMsg = readingEx.getMessage();
					try {
						writer.rollback();
						writer.setAutoCommit(true);
						logger.debug("Rollback done 1");
					} catch (SQLException e) {
						errMsg += e.getMessage();
					}
				}
			} else {
				// prima di aggiornare la stazione devo verificare se c'e'
				// un'altra stazione con lo stesso uuid: in tal caso questa
				// stazione deve essere riesumata e si deve cancellare quella
				// che non aveva l'uuid
				Integer stationId = null;
				try {
					stationId = reader.isExistingCopIdUuid(
							station.getStationUUID(), station.getStationId(),
							station.getStInfo().getCopId());
				} catch (SQLException e) {
					logger.error("Error reading uuid", e);
					e.printStackTrace();
				}
				if (stationId != null) {
					// esiste gia' un uuid quindi devo aggiornare quella con il
					// nuovo station uuid e devo poi cancellare la nuova
					// stazione che non aveva l'uuid
					Integer oldStationId = station.getStationId();
					RejectedObject retObj = writer.deleteStation(oldStationId);
					if (retObj != null)
						station.setStationId(stationId);

				} else {
					// non esiste nessuna stazione con quell'uuid quindi posso
					// aggiornare quella che ho gia'

				}
				errObj = writer.updateStationInfo(station);
			}
			if (errObj != null) {
				logger.error("Error 2: " + errObj.getSqlEx() + " on object:"
						+ errObj.getObj());
				errMsg = errObj.getSqlEx().getMessage();
				try {
					writer.rollback();
					writer.setAutoCommit(true);
					logger.debug("Rollback done 2");
				} catch (SQLException e) {
					errMsg += e.getMessage();
				}
			} else {
				// write each analyzer info
				RejectedObject errObjAnalyzer = null;
				/*
				 * logger.debug("numero di analizzatori:" +
				 * station.getAnalyzersVect().size());
				 */
				for (int i = 0; i < station.getAnalyzersVect().size()
						&& errObjAnalyzer == null; i++) {
					Analyzer analyzer = station.getAnalyzersVect().get(i);
					Boolean isNewAnalyzer = null;
					try {
						// logger.debug("analyzer.id:" +
						// analyzer.getAnalyzerId());
						isNewAnalyzer = reader.isNewAnalyzer(analyzer
								.getAnalyzerId());
					} catch (SQLException readingEx) {
						errObjAnalyzer = new RejectedObject();
						logger.error(
								"Error 3: " + readingEx + " on reading object:"
										+ analyzer.getAnalyzerId(), readingEx);
						errMsg = readingEx.getMessage();
						try {
							writer.rollback();
							writer.setAutoCommit(true);
							logger.debug("Rollback done 3");
						} catch (SQLException e) {
							errMsg += e.getMessage();
						}
					}
					if (isNewAnalyzer != null) {
						if (isNewAnalyzer.booleanValue()) {
							errObjAnalyzer = writer.writeAnalyzer(analyzer,
									station.getStationId());
							try {
								analyzer.setAnalyzerId(reader
										.readAnalyzerId(analyzer
												.getAnalyzerUUID()));
							} catch (Exception readingEx) {
								errObjAnalyzer = new RejectedObject();
								logger.error(
										"Error 4: " + readingEx
												+ " on reading object:"
												+ analyzer.getAnalyzerId(),
										readingEx);
								errMsg = readingEx.getMessage();
								try {
									writer.rollback();
									writer.setAutoCommit(true);
									logger.debug("Rollback done 4");
								} catch (SQLException e) {
									errMsg += e.getMessage();
								}
							}
						} else
							errObjAnalyzer = writer.updateAnalyzer(analyzer);
						if (errObjAnalyzer != null) {
							logger.error("Error 5: " + errObjAnalyzer.getSqlEx()
									+ " on object:" + errObjAnalyzer.getObj());
							errMsg = errObjAnalyzer.getSqlEx().getMessage();
							try {
								writer.rollback();
								writer.setAutoCommit(true);
								logger.debug("Rollback done 5");
							} catch (SQLException e) {
								errMsg += e.getMessage();
							}
						} else {
							if (analyzer.getDeletionDate() != null) {
								// delete all record of analyzerAlarmStatus in
								// case of analyzer deleted
								errObjAnalyzer = writer
										.deleteAnalyzerAlarmStatus(analyzer
												.getAnalyzerId());
								if (errObjAnalyzer != null) {
									logger.error("Error: "
											+ errObjAnalyzer.getSqlEx()
											+ " on object:"
											+ errObjAnalyzer.getObj());
									errMsg = errObjAnalyzer.getSqlEx().getMessage();
									try {
										writer.rollback();
										writer.setAutoCommit(true);
										logger.debug("Rollback done");
									} catch (SQLException e) {
										errMsg += e.getMessage();
									}
								}
							}
							if (errObjAnalyzer == null) {
								// write each element info
								RejectedObject errObjElement = null;
								for (int j = 0; j < analyzer.getElements()
										.size() && errObjElement == null; j++) {
									GenericElement element = analyzer
											.getElements().get(j);
									Boolean isNewElement = null;
									try {
										isNewElement = reader
												.isNewElement(element
														.getElementId());
									} catch (SQLException readingEx) {
										errObjElement = new RejectedObject();
										errObjAnalyzer = errObjElement;
										logger.error("Error 6: " + readingEx
												+ " on reading object:"
												+ element.getElementId(),
												readingEx);
										errMsg = readingEx.getMessage();
										try {
											writer.rollback();
											writer.setAutoCommit(true);
											logger.debug("Rollback done 6");
										} catch (SQLException e) {
											errMsg += e.getMessage();
										}
									}
									if (isNewElement != null) {
										// case of insert element information
										if (isNewElement.booleanValue()) {
											if (element instanceof ScalarElement) {
												errObjElement = writer
														.writeElement(
																element,
																analyzer.getAnalyzerId());
												if (errObjElement == null) {
													try {
														int newElementId = reader
																.readElementId(
																		element.getName(),
																		analyzer.getAnalyzerId());
														element.setElementId(newElementId);
														errObjElement = writer
																.writeScalarElement(
																		element,
																		newElementId);
													} catch (Exception readingEx) {
														errObjElement = new RejectedObject();
														errObjAnalyzer = errObjElement;
														logger.error(
																"Error 7: "
																		+ readingEx
																		+ " on reading object:"
																		+ element
																				.getElementId(),
																readingEx);
														errMsg = readingEx
																.getMessage();
														try {
															writer.rollback();
															writer.setAutoCommit(true);
															logger.debug("Rollback done 7");
														} catch (SQLException e) {
															errMsg += e
																	.getMessage();
														}
													}
												}
											} else if (element instanceof WindElement) {
												RejectedObject errGenericObjElement = writer
														.writeElement(
																element,
																analyzer.getAnalyzerId());
												RejectedObject errSpeedObjElement = writer
														.writeElement(
																((WindElement) element)
																		.getSpeed(),
																analyzer.getAnalyzerId());
												RejectedObject errDirObjElement = writer
														.writeElement(
																((WindElement) element)
																		.getDirection(),
																analyzer.getAnalyzerId());
												if (errGenericObjElement == null
														&& errSpeedObjElement == null
														&& errDirObjElement == null) {
													try {
														int newGenericElementId = reader
																.readElementId(
																		element.getName(),
																		analyzer.getAnalyzerId());
														element.setElementId(newGenericElementId);
														int newSpeedElementId = reader
																.readElementId(
																		((WindElement) element)
																				.getSpeed()
																				.getName(),
																		analyzer.getAnalyzerId());
														((WindElement) element)
																.getSpeed()
																.setElementId(
																		newSpeedElementId);
														int newDirElementId = reader
																.readElementId(
																		((WindElement) element)
																				.getDirection()
																				.getName(),
																		analyzer.getAnalyzerId());
														((WindElement) element)
																.getDirection()
																.setElementId(
																		newDirElementId);
														errObjElement = writer
																.writeScalarElement(
																		((WindElement) element)
																				.getSpeed(),
																		newSpeedElementId);
														if (errObjElement == null)
															errObjElement = writer
																	.writeScalarElement(
																			((WindElement) element)
																					.getDirection(),
																			newDirElementId);
													} catch (Exception readingEx) {
														errObjElement = new RejectedObject();
														errObjAnalyzer = errObjElement;
														logger.error(
																"Error 7: "
																		+ readingEx
																		+ " on reading object:"
																		+ element
																				.getElementId(),
																readingEx);
														errMsg = readingEx
																.getMessage();
														try {
															writer.rollback();
															writer.setAutoCommit(true);
															logger.debug("Rollback done 7");
														} catch (SQLException e) {
															errMsg += e
																	.getMessage();
														}
													}
												} else {
													if (errGenericObjElement != null)
														errObjElement = errGenericObjElement;
													else if (errSpeedObjElement != null)
														errObjElement = errSpeedObjElement;
													else if (errDirObjElement != null)
														errObjElement = errDirObjElement;
												}
											}
										}
										// case of update element information
										else {
											if (element instanceof ScalarElement) {
												errObjElement = writer
														.updateElement(element);
												if (errObjElement == null) {
													errObjElement = writer
															.updateScalarElement(element);
												}
											} else if (element instanceof WindElement) {
												RejectedObject errGenericElementObj = writer
														.updateElement(element);
												RejectedObject errSpeedElementObj = writer
														.updateElement(((WindElement) element)
																.getSpeed());
												RejectedObject errDirElementObj = writer
														.updateElement(((WindElement) element)
																.getDirection());
												if (errGenericElementObj == null
														|| errSpeedElementObj == null
														|| errDirElementObj == null) {
													errSpeedElementObj = writer
															.updateScalarElement(((WindElement) element)
																	.getSpeed());
													errDirElementObj = writer
															.updateScalarElement(((WindElement) element)
																	.getDirection());
												}
												if (errGenericElementObj != null)
													errObjElement = errGenericElementObj;
												else if (errSpeedElementObj != null)
													errObjElement = errSpeedElementObj;
												else if (errDirElementObj != null)
													errObjElement = errDirElementObj;
											}
										}
										if (errObjElement != null) {
											errObjAnalyzer = errObjElement;
											logger.error("Error 8: "
													+ errObjElement.getSqlEx()
													+ " on object:"
													+ errObjElement.getObj());
											errMsg = errObjElement.getSqlEx()
													.getMessage();
											try {
												writer.rollback();
												writer.setAutoCommit(true);
												logger.debug("Rollback done 8");
											} catch (SQLException e) {
												errMsg += e.getMessage();
											}
										}
									}

									if (errObjElement == null) {
										// write each avg_period for an element
										RejectedObject errObjAvgPeriod = null;
										for (int k = 0; k < element
												.getAvgPeriodSize()
												&& errObjAvgPeriod == null; k++) {
											AvgPeriod avgPeriod = element
													.getAvgPeriod().get(k);
											Boolean isNewAvgPeriod = null;
											try {
												isNewAvgPeriod = reader
														.isNewAvgPeriod(
																element.getElementId(),
																avgPeriod
																		.getAvgPeriodVal());
											} catch (SQLException readingEx) {
												errObjAvgPeriod = new RejectedObject();
												errObjElement = errObjAvgPeriod;
												errObjAnalyzer = errObjAvgPeriod;
												logger.error(
														"Error 9: "
																+ readingEx
																+ " on reading object:"
																+ avgPeriod
																		.getAvgPeriodVal(),
														readingEx);
												errMsg = readingEx.getMessage();
												try {
													writer.rollback();
													writer.setAutoCommit(true);
													logger.debug("Rollback done 9");
												} catch (SQLException e) {
													errMsg += e.getMessage();
												}
											}
											if (isNewAvgPeriod != null) {
												if (isNewAvgPeriod
														.booleanValue()) {
													errObjAvgPeriod = writer
															.writeAvgPeriod(
																	avgPeriod,
																	element.getElementId());

												} else
													errObjAvgPeriod = writer
															.updateAvgPeriod(
																	avgPeriod,
																	element.getElementId());
												if (errObjAvgPeriod != null) {
													errObjElement = errObjAvgPeriod;
													errObjAnalyzer = errObjAvgPeriod;
													logger.error("Error 10: "
															+ errObjAvgPeriod.getSqlEx()
															+ " on object:"
															+ errObjAvgPeriod.getObj());
													errMsg = errObjAvgPeriod.getSqlEx()
															.getMessage();
													try {
														writer.rollback();
														writer.setAutoCommit(true);
														logger.debug("Rollback done 10");
													} catch (SQLException e) {
														errMsg += e
																.getMessage();
													}
												} else {
													
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			// write each ContainerAlarm info
			RejectedObject errObjCAlarm = null;
			/*
			 * logger.debug("numero di container alarm:" +
			 * station.getStData().getContainerAlarm().size());
			 */
			for (int i = 0; i < station.getContainerAlarm().size()
					&& errObjCAlarm == null; i++) {
				ContainerAlarm cAlarm = station.getContainerAlarm().get(i);
				Boolean isNewCAlarm = null;
				try {
					// logger.debug("cAlarm.dbid:" + cAlarm.getAlarmDbID());
					isNewCAlarm = reader.isNewContainerAlarm(cAlarm
							.getAlarmDbID());
				} catch (SQLException readingEx) {
					errObjCAlarm = new RejectedObject();
					logger.error("Error: " + readingEx + " on reading object:"
							+ cAlarm.getAlarmDbID(), readingEx);
					errMsg = readingEx.getMessage();
					try {
						writer.rollback();
						writer.setAutoCommit(true);
						logger.debug("Rollback done");
					} catch (SQLException e) {
						errMsg += e.getMessage();
					}
				}
				if (isNewCAlarm != null) {
					if (isNewCAlarm.booleanValue()) {
						errObjCAlarm = writer.writeContainerAlarm(cAlarm,
								station.getStationId());
						try {
							cAlarm.setAlarmDbID(reader
									.readContainerAlarmId(cAlarm.getAlarmUUID()));
							String[] cAInfo = reader.readAlarmNameInfo(cAlarm
									.getAlarmID());
							cAlarm.setAlarmName(cAInfo[0]);
							cAlarm.setType(cAInfo[1]);
						} catch (Exception readingEx) {
							errObjCAlarm = new RejectedObject();
							logger.error(
									"Error: " + readingEx
											+ " on reading object:"
											+ cAlarm.getAlarmDbID(), readingEx);
							errMsg = readingEx.getMessage();
							try {
								writer.rollback();
								writer.setAutoCommit(true);
								logger.debug("Rollback done 4");
							} catch (SQLException e) {
								errMsg += e.getMessage();
							}
						}
					} else
						errObjCAlarm = writer.updateContainerAlarm(cAlarm);
					if (errObjCAlarm != null) {
						logger.error("Error: " + errObjCAlarm.getSqlEx()
								+ " on object:" + errObjCAlarm.getObj());
						errMsg = errObjCAlarm.getSqlEx().getMessage();
						try {
							writer.rollback();
							writer.setAutoCommit(true);
							logger.debug("Rollback done");
						} catch (SQLException e) {
							errMsg += e.getMessage();
						}
					} else {
						if (cAlarm.getDeletionDate() != null) {
							errObjCAlarm = writer
									.deleteContainerAlarmStatus(cAlarm
											.getAlarmDbID());
							if (errObjCAlarm != null) {
								logger.error("Error: " + errObjCAlarm.getSqlEx()
										+ " on object:" + errObjCAlarm.getObj());
								errMsg = errObjCAlarm.getSqlEx().getMessage();
								try {
									writer.rollback();
									writer.setAutoCommit(true);
									logger.debug("Rollback done");
								} catch (SQLException e) {
									errMsg += e.getMessage();
								}
							}
						}
					}
				}
			}

			try {
				if (errMsg == null) {
					writer.commit();
					writer.setAutoCommit(true);
					logger.debug("Commit done");
				}
			} catch (SQLException e) {
				errMsg += e.getMessage();
			}
		}
		return errMsg;
	}

	public String setStationDeleted(Integer stationId, Date currentDate) {
		String errMsg = null;
		RejectedObject errObj = writer.setStationDeleted(stationId.intValue(),
				currentDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	
	public String updateStMapPosition(int stationId, Integer map_x,
			Integer map_y, Boolean isGenericPosition) {
		String errMsg = null;
		RejectedObject errObj = writer.updateStMapPosition(stationId, map_x,
				map_y, isGenericPosition);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String updateAllDataDownloaded(int elementId, int avgPeriodId,
			boolean allDataDownloaded) {
		String errMsg = null;
		RejectedObject errObj = writer.updateAllDataDownloadedForAvgPeriod(
				elementId, avgPeriodId, allDataDownloaded);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	
	public String updateContainerAlarmStatus(
			Vector<ContainerAlarm> stationAlarms) {
		String errMsg = null;
		RejectedObject errObj = null;
		for (int i = 0; i < stationAlarms.size() && errObj == null; i++) {
			ContainerAlarm stationAlarm = stationAlarms.get(i);
			errObj = writer.updateContainerAlarmStatus(
					stationAlarm.getAlarmDbID(), stationAlarm.getLastStatus());
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String updateAnalyzerStatus(int analyzerId,
			Vector<AnalyzerEventDatum> analyzerAlarms) {
		String errMsg = null;
		RejectedObject errObj = null;
		for (int i = 0; i < analyzerAlarms.size() && errObj == null; i++) {
			AnalyzerEventDatum analyzerAlarm = analyzerAlarms.get(i);
			errObj = writer
					.updateAnalyzerAlarmStatus(analyzerId, analyzerAlarm);
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String updateParameter(ParameterInfo parInfo, Date updateDate)
			throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;
		writer.setAutoCommit(false);
		errObj = writer.updateParameter(parInfo, updateDate);
		if (errObj == null)
			writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end updateParameter

	public String updateCommonConfigInfo(Integer lastUpdateStatus,
			String problemCommonConfig, int stationId) throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;
		writer.setAutoCommit(false);
		errObj = writer.updateCommonConfigInfo(lastUpdateStatus,
				problemCommonConfig, stationId);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end updateParameter

	public String updateModem(String deviceId, ModemInfo modemInfo)
			throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;

		errObj = writer.updateModem(deviceId, modemInfo);

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}// end updateModem

	public String updateMeasureUnit(MeasureUnitInfo measureInfo, Date updateDate)
			throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;
		writer.setAutoCommit(false);
		errObj = writer.updateMeasureUnit(measureInfo, updateDate);
		if (errObj == null)
			writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end updateMeasureUnit

	public String updateAlarmName(AlarmNameInfo alarmInfo, Date updateDate)
			throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;
		writer.setAutoCommit(false);
		errObj = writer.updateAlarmName(alarmInfo, updateDate);
		if (errObj == null)
			writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID, updateDate);

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end updateAlarmName

	public String updateDefaultAvgPeriod(Integer avgPeriodId,
			boolean defaultAvgPeriod, Date updateDate) throws SQLException {
		String errMsg = null;
		RejectedObject errObj = null;
		Integer avgPeriodToUpdate = reader.getAvgPeriodToUpdate();
		writer.setAutoCommit(false);
		errObj = writer.updateDefaultAvgPeriod(avgPeriodId, defaultAvgPeriod,
				avgPeriodToUpdate, updateDate);
		if (errObj == null)
			writer.updateConfigCommonConfigDate(Centrale.CONFIG_ID, updateDate);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
			writer.rollback();
		} else
			writer.commit();
		writer.setAutoCommit(true);
		return errMsg;
	}// end updateDefaultAvgPeriod

	public String updateCommonConfigDate(Date commonConfigDate, int stationId) {
		String errMsg = null;
		RejectedObject errObj = writer.updateCommonConfigDate(commonConfigDate,
				stationId);
		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeContainerAlarmHistory(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		for (int i = 0; i < dataList.size() && errObj == null; i++) {
			AlarmData stationAlarm = (AlarmData) dataList.get(i);
			errObj = writer.writeContainerAlarmHistory(key[0].intValue(),
					stationAlarm);
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeAnalyzerStatusHistory(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		for (int i = 0; i < dataList.size() && errObj == null; i++) {
			AnalyzerEventDatum anStatus = (AnalyzerEventDatum) dataList.get(i);
			errObj = writer.writeAnalyzerStatusHistory(key[0].intValue(),
					anStatus);
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeMeansData(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		Object firstObj = dataList.get(0);
		if (firstObj instanceof RainScalarData) {
			for (int j = 0; j < dataList.size() && errObj == null; j++) {
				RainScalarData scalarData = (RainScalarData) dataList.get(j);
				/*
				 * System.out.println("j vale:" + j + " scalarData" + scalarData
				 * + " " + key[0] + " " + key[1] + " dbwriter vale:" + writer);
				 */
				errObj = writer.writeScalarMeansData(key[0].intValue(),
						key[1].intValue(), scalarData);
			}
		} else if (firstObj instanceof WindData) {
			for (int j = 0; j < dataList.size() && errObj == null; j++) {
				WindData windData = (WindData) dataList.get(j);
				errObj = writer.writeWindMeansData(key[0].intValue(),
						key[1].intValue(), windData);
			}
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeSampleData(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		for (int i = 0; i < dataList.size() && errObj == null; i++) {
			RainScalarData scalarData = (RainScalarData) dataList.get(i);
			errObj = writer
					.writeScalarSampleData(key[0].intValue(), scalarData);
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeGpsData(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		for (int i = 0; i < dataList.size() && errObj == null; i++) {
			GpsData gpsData = (GpsData) dataList.get(i);
			errObj = writer.writeGpsData(key[0].intValue(), gpsData);
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeInformaticStatusHistory(InsertableObject insertableObj) {
		String errMsg = null;
		RejectedObject errObj = null;
		LinkedList<Object> dataList = insertableObj.getDataList();
		Integer[] key = insertableObj.getKey();
		// TODO capire come mai ci sono piu' di un valore nella dataList
		for (int i = 0; i < dataList.size() && errObj == null; i++) {
			StationStatus stStatus = (StationStatus) dataList.get(i);
			errObj = writer.writeInformaticStatusHistory(key[0].intValue(),
					stStatus);
			if (errObj == null) {
				Iterator<String> it_hm = stStatus.getDiskStatus()
						.getFsSpaceMap().keySet().iterator();
				while (it_hm.hasNext() && errObj == null) {
					String filesystemName = it_hm.next();
					Integer status = stStatus.getDiskStatus().getFsSpaceMap()
							.get(filesystemName);
					errObj = writer.writeFilesystemStatusHistory(
							key[0].intValue(), stStatus.getTimestamp(),
							filesystemName, status);
				}
			}
		}

		if (errObj != null) {
			logger.error("Error: " + errObj.getSqlEx() + " on object:" + errObj.getObj());
			errMsg = errObj.getSqlEx().getMessage();
		}
		return errMsg;
	}

	public String writeConfiguration(int stationId, StringBuilder conf,
			long lastModifiedTime) {
		String errMsg = null;
		RejectedObject errObj = null;
		if (conf != null) {

			try {
				errObj = writer.writeConfiguration(stationId, lastModifiedTime,
						conf);
			} catch (IOException e) {
				logger.error("Error on closing byteArrayInputStream", e);
				errMsg = e.getMessage();
			}

			if (errObj != null) {
				logger.error("Error: " + errObj.getSqlEx() + " on object:"
						+ errObj.getObj());
				errMsg += errObj.getSqlEx().getMessage();
			}
		} else
			logger.error("Null configuration for station with id " + stationId);

		return errMsg;
	}// end writeConfiguration


	/*
	 * Supporting methods
	 */

	private Date compareLastTimestamp(Date lastTimestamp, Date stMinTimestamp) {
		if (lastTimestamp == null) {
			if (stMinTimestamp != null)
				lastTimestamp = stMinTimestamp;
			else
				lastTimestamp = Centrale.getMin_timestamp();
		} else {
			if (stMinTimestamp != null && lastTimestamp.before(stMinTimestamp))
				lastTimestamp = stMinTimestamp;
		}

		return lastTimestamp;
	}

	public List<String> getAlarmType() throws SQLException {
		List<String> alarmType = reader.readAnalyzerAlarmType();
		return alarmType;
	}

	public String getTemperatureId() throws SQLException {
		return reader.readTemperatureId(Centrale.CONFIG_ID);
	}

	public int getAverageTemperature() throws SQLException {
		return reader.readAverageTemperature(Centrale.CONFIG_ID);

	}

	public String getCopName(int virtualCopId) throws SQLException {

		return reader.readCopName(virtualCopId);
	}

}// end class

class AvgPeriodDate {
	int avgPeriodVal;

	Date minDate;
}
