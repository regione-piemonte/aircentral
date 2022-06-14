/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: class for reading from Internal Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbReader.java,v 1.150 2015/10/19 13:22:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleException;
import it.csi.centrale.data.station.AdvancedPollingConf;
import it.csi.centrale.data.station.AlarmData;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.AvgPeriod;
import it.csi.centrale.data.station.CommDeviceInfo;
import it.csi.centrale.data.station.CommDeviceInfo.CommunicationDevice;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.ContainerAlarm;
import it.csi.centrale.data.station.GenericData;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.GpsData;
import it.csi.centrale.data.station.ModemConf;
import it.csi.centrale.data.station.RainScalarData;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.data.station.StationStatus;
import it.csi.centrale.data.station.VirtualCop;
import it.csi.centrale.data.station.WindData;
import it.csi.centrale.data.station.WindElement;
import it.csi.centrale.polling.data.CommonConfigResult;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.stationreport.StationConfigHolder;
import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.data.AvgPeriodInfo;
import it.csi.centrale.ui.client.data.MeasureUnitInfo;
import it.csi.centrale.ui.client.data.ModemInfo;
import it.csi.centrale.ui.client.data.OtherInfo;
import it.csi.centrale.ui.client.data.ParameterInfo;
import it.csi.centrale.ui.client.data.StandardInfo;
import it.csi.centrale.ui.client.data.StorageManagerInfo;
import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.KeyValueObject;
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.centrale.ui.server.ValidationFlag;
import it.csi.periferico.config.common.AlarmName;
import it.csi.periferico.config.common.ConfigException;
import it.csi.periferico.config.common.MeasureUnit;
import it.csi.periferico.config.common.Parameter;
import it.csi.periferico.config.common.Standards;
import it.csi.periferico.config.common.StorageManagerCfg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Class for reading from Internal Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class DbReader {

	private Connection conn;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private SimpleDateFormat sdfSec = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale."
			+ DbReader.class.getSimpleName());

	public DbReader(Connection conn) {
		this.conn = conn;
	}

	public Integer checkConnection() throws SQLException {
		String classMethod = "[checkConnection] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT (1) AS test";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		Integer test = null;
		if (rs.next()) {
			test = rs.getInt(rs.findColumn("test"));
		}
		rs.close();
		stmt.close();
		return test;
	} // end readCop

	public ConfigInfo readConfig(int configId) throws SQLException {
		String classMethod = "[readConfig] ";
		ConfigInfo config = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max_num_phone_lines, reserved_line, "
				+ "synthetic_icon, polling_office_time, "
				+ "polling_extra_office, use_polling_extra, close_sat, "
				+ "close_sun, open_at, close_at, total_num_modem, "
				+ "num_modem_shared_lines, download_type_sample_data, "
				+ "download_alarm_data, min_temperature_threshold, "
				+ "max_temperature_threshold, alarm_max_temperature_threshold, "
				+ "cop_ip, router_timeout, router_try_timeout, "
				+ "update_date, common_config_update_date, cop_router_ip, name, "
				+ "generic_map_name, time_host_router, time_host_proxy, "
				+ "time_host_lan, time_host_modem, num_reserved_lines_ui, "
				+ "proxy_host, proxy_port, proxy_exclusion "
				+ "FROM config WHERE config_id=" + DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		if (rs.next()) {
			config = new ConfigInfo();
			config.setConfigId(configId);
			config.setSampleDataTypeToDownload(rs
					.getInt("download_type_sample_data"));
			config.setDownloadAlarmHistory(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("download_alarm_data")));
			config.setMaxNumLines(rs.getInt("max_num_phone_lines"));
			config.setTotalNumModem(rs.getInt("total_num_modem"));
			config.setNumModemSharedLines(rs.getInt("num_modem_shared_lines"));
			config.setReservedLine(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("reserved_line")));
			config.setMinThreshold(DbUtils.rsGetInt(rs,
					rs.findColumn("min_temperature_threshold")));
			config.setMaxThreshold(DbUtils.rsGetInt(rs,
					rs.findColumn("max_temperature_threshold")));
			config.setAlarmMaxThreshold(DbUtils.rsGetInt(rs,
					rs.findColumn("alarm_max_temperature_threshold")));
			config.setSyntheticIcon(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("synthetic_icon")));
			config.setCopIp(rs.getString("cop_ip"));
			config.setRouterTimeout(rs.getInt("router_timeout"));
			config.setRouterTryTimeout(rs.getInt("router_try_timeout"));
			config.setUpdateDate(rs.getTimestamp("update_date"));
			config.setCommonConfigUpdateDate(rs
					.getTimestamp("common_config_update_date"));
			config.setCopRouter(rs.getString("cop_router_ip"));
			config.setName(rs.getString(rs.findColumn("name")));
			config.setGenericMapName(rs.getString(rs
					.findColumn("generic_map_name")));
			config.setTimeHostLan(rs.getString(rs.findColumn("time_host_lan")));
			config.setTimeHostModem(rs.getString(rs
					.findColumn("time_host_modem")));
			config.setTimeHostRouter(rs.getString(rs
					.findColumn("time_host_router")));
			config.setTimeHostProxy(rs.getString(rs
					.findColumn("time_host_proxy")));
			config.setNumReservedLinesUi(rs.getInt(rs
					.findColumn("num_reserved_lines_ui")));
			config.setProxyHost(rs.getString(rs.findColumn("proxy_host")));
			config.setProxyPort(DbUtils.rsGetInt(rs,
					rs.findColumn("proxy_port")));
			config.setProxyExlusion(rs.getString(rs
					.findColumn("proxy_exclusion")));
			AdvancedPollingConf advancedPollingConf = new AdvancedPollingConf();
			advancedPollingConf.setPollingOfficeTime(rs
					.getInt("polling_office_time"));
			advancedPollingConf.setPollingExtraOffice(DbUtils.rsGetInt(rs,
					rs.findColumn("polling_extra_office")));
			advancedPollingConf.setUsePollingExtra(DbUtils.rsGetBooleanFromInt(
					rs, rs.findColumn("use_polling_extra")));
			advancedPollingConf.setCloseSat(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("close_sat")));
			advancedPollingConf.setCloseSun(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("close_sun")));
			advancedPollingConf.setOpenAt(DbUtils.rsGetInt(rs,
					rs.findColumn("open_at")));
			advancedPollingConf.setCloseAt(DbUtils.rsGetInt(rs,
					rs.findColumn("close_at")));
			config.setPollConfig(advancedPollingConf);
		}
		rs.close();
		stmt.close();
		return config;
	} // end readConfig

	public String getTimeHost(CommunicationDevice communicationDevice)
			throws SQLException {
		String classMethod = "[readConfig] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT time_host_router, time_host_proxy, "
				+ "time_host_lan, time_host_modem "
				+ "FROM config WHERE config_id="
				+ DbUtils.qVal(Centrale.CONFIG_ID);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		String timeHost = null;
		if (rs.next()) {
			if (communicationDevice.equals(CommunicationDevice.LAN))

				timeHost = rs.getString(rs.findColumn("time_host_lan"));
			else if (communicationDevice.equals(CommunicationDevice.MODEM))
				timeHost = rs.getString(rs.findColumn("time_host_modem"));
			else if (communicationDevice.equals(CommunicationDevice.ROUTER))
				timeHost = rs.getString(rs.findColumn("time_host_router"));
			else if (communicationDevice.equals(CommunicationDevice.PROXY))
				timeHost = rs.getString(rs.findColumn("time_host_proxy"));
		}
		rs.close();
		stmt.close();
		return timeHost;
	}

	public Vector<VirtualCop> readVirtualCop(int configId) throws SQLException {
		String classMethod = "[readVirtualCop] ";
		Vector<VirtualCop> virtualCopList = new Vector<VirtualCop>();
		VirtualCop virtualCop = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM virtual_cop WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			virtualCop = new VirtualCop();
			virtualCop.setConfigId(configId);
			virtualCop.setVirtualCopId(rs.getInt(rs
					.findColumn("virtual_cop_id")));
			virtualCop.setName(rs.getString(rs.findColumn("name")));
			virtualCop.setMapName(rs.getString(rs.findColumn("map_name")));
			virtualCopList.add(virtualCop);
		}
		rs.close();
		stmt.close();
		return virtualCopList;
	}

	public String readCopName(int virtualCopId) throws SQLException {
		String classMethod = "[readCopName] ";
		String copName = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT name FROM virtual_cop WHERE virtual_cop_id = "
				+ DbUtils.qVal(virtualCopId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			copName = rs.getString(rs.findColumn("name"));
		}
		rs.close();
		stmt.close();
		return copName;
	}

	public Vector<ModemConf> readModemVect() throws SQLException {
		String classMethod = "[readModemVect] ";
		Vector<ModemConf> modemVect = new Vector<ModemConf>();
		Statement stmt = conn.createStatement();
		String query = "SELECT device_id,shared_line,phone_prefix,update_date "
				+ " FROM modem";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			ModemConf modemConf = new ModemConf();
			modemConf.setDescription(rs.getString("device_id"));
			modemConf.setSharedLine(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("shared_line")));
			modemConf.setPhonePrefix(rs.getString("phone_prefix"));
			modemConf.setUpdateDate(rs.getTimestamp("update_date"));
			modemVect.add(modemConf);
		}
		rs.close();
		stmt.close();
		return modemVect;
	} // end readModemVect

	public Vector<String> readAnalyzerAlarmType() throws SQLException {
		String classMethod = "[readAnalyzerAlarmType] ";
		Vector<String> analyzerAlarmTypes = new Vector<String>();
		Statement stmt = conn.createStatement();
		String query = "SELECT analyzer_alarm_type FROM analyzer_alarm_type "
				+ "ORDER BY analyzer_alarm_type_id";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			String anAlarmType = rs.getString("analyzer_alarm_type");
			analyzerAlarmTypes.add(anAlarmType);
		}
		rs.close();
		stmt.close();
		return analyzerAlarmTypes;
	} // end readAnalyzerAlarmType

	public Vector<Station> readStations(int virtualCopId) throws SQLException,
			CentraleException {
		String classMethod = "[readStations] ";
		Vector<Station> stVect = new Vector<Station>();
		// read stations
		Statement stmt = conn.createStatement();

		String query = "SELECT station_id, uuid, shortname, name, province,"
				+ " city, address, location, notes, map_x, map_y, delete_date,"
				+ " enabled, ip_address, ip_port, tel_number, "
				+ " router_ip_address, use_modem, force_polling_time, "
				+ " use_gps, config_uuid, min_timestamp_for_polling, "
				+ " sample_data_download_enabled, "
				+ " config_author, config_notes, config_date, "
				+ " update_date, lan, proxy, common_config_date, "
				+ " send_common_config, generic_map_x, generic_map_y FROM station"
				+ " WHERE virtual_cop_id=" + DbUtils.qVal(virtualCopId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			Station station = new Station();
			station.setStationId(rs.getInt("station_id"));
			station.setStationUUID(rs.getString("uuid"));
			StationInfo stInfo = new StationInfo();
			stInfo.setShortStationName(rs.getString("shortname"));
			stInfo.setLongStationName(rs.getString("name"));
			stInfo.setProvince(rs.getString("province"));
			stInfo.setCity(rs.getString("city"));
			stInfo.setAddress(rs.getString("address"));
			stInfo.setLocation(rs.getString("location"));
			stInfo.setNotes(rs.getString("notes"));
			stInfo.setEnabled(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("enabled")));
			stInfo.setGps(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("use_gps")));
			stInfo.setConfigUUID(rs.getString("config_uuid"));
			stInfo.setMap_x(DbUtils.rsGetInt(rs, rs.findColumn("map_x")));
			stInfo.setMap_y(DbUtils.rsGetInt(rs, rs.findColumn("map_y")));
			stInfo.setDeletionDate(DbUtils.rsGetDate(rs,
					rs.findColumn("delete_date")));
			stInfo.setForcePollingTime(DbUtils.rsGetInt(rs,
					rs.findColumn("force_polling_time")));
			stInfo.setMinTimestampForPolling(DbUtils.rsGetDate(rs,
					rs.findColumn("min_timestamp_for_polling")));
			stInfo.setSampleDataDownloadEnable(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("sample_data_download_enabled")));
			stInfo.setConfigUserName(rs.getString("config_author"));
			stInfo.setConfigComment(rs.getString("config_notes"));
			stInfo.setConfigDate(DbUtils.rsGetDate(rs,
					rs.findColumn("config_date")));
			stInfo.setUpdateDate(rs.getTimestamp("update_date"));
			stInfo.setCommonConfigStationUpdateDate(rs
					.getTimestamp("common_config_date"));
			stInfo.setCommonConfigSendingEnable(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("send_common_config")));
			stInfo.setGenericMap_x((DbUtils.rsGetInt(rs,
					rs.findColumn("generic_map_x"))));
			stInfo.setGenericMap_y((DbUtils.rsGetInt(rs,
					rs.findColumn("generic_map_y"))));
			stInfo.setCopId(virtualCopId);
			stInfo.setCopName(readCopName(stInfo.getCopId()));
			CommDeviceInfo commDeviceInfo = new CommDeviceInfo();
			commDeviceInfo.setIp(rs.getString("ip_address"));
			commDeviceInfo.setPortNumber(DbUtils.rsGetInt(rs,
					rs.findColumn("ip_port")));
			commDeviceInfo.setModem(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("use_modem")));
			commDeviceInfo.setPhoneNumber(rs.getString("tel_number"));
			commDeviceInfo
					.setRouterIpAddress(rs.getString("router_ip_address"));
			commDeviceInfo.setLan(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("lan")));
			commDeviceInfo.setProxy(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("proxy")));
			stInfo.setCommDevice(commDeviceInfo);
			station.setStInfo(stInfo);
			StationStatus stStatus = new StationStatus();
			station.setStStatus(stStatus);
			stVect.add(station);

			// read analyzers for each station
			Vector<Analyzer> analyzersVect = new Vector<Analyzer>();
			Statement stmt2 = conn.createStatement();
			query = "SELECT analyzer_id, uuid, name, brand, model, description,"
					+ " serial_number, status, type, notes, delete_date,"
					+ " update_date FROM analyzer "
					+ "WHERE station_id="
					+ DbUtils.qVal(station.getStationId());
			ResultSet rs2 = null;
			try {
				rs2 = stmt2.executeQuery(query);
			} catch (SQLException e) {
				logger.error(classMethod + query, e);

				throw e;
			}
			while (rs2.next()) {
				Analyzer analyzer = new Analyzer();
				analyzer.setAnalyzerId(rs2.getInt("analyzer_id"));
				analyzer.setAnalyzerUUID(rs2.getString("uuid"));
				analyzer.setName(rs2.getString("name"));
				analyzer.setBrand(rs2.getString("brand"));
				analyzer.setModel(rs2.getString("model"));
				analyzer.setDescription(rs2.getString("description"));
				analyzer.setSerialNumber(rs2.getString("serial_number"));
				analyzer.setStatus(rs2.getString("status"));
				analyzer.setType(rs2.getString("type"));
				analyzer.setNotes(rs2.getString("notes"));
				analyzer.setDeleted(DbUtils.rsGetDate(rs2,
						rs2.findColumn("delete_date")));
				analyzer.setUpdateDate(rs2.getTimestamp("update_date"));
				analyzersVect.add(analyzer);

				// read element for each analyzer
				LinkedList<GenericElement> elements = new LinkedList<GenericElement>();
				Statement stmt3 = conn.createStatement();
				query = "SELECT element_id, param_id, enabled, type,"
						+ " delete_date, update_date"
						+ " FROM element WHERE analyzer_id="
						+ DbUtils.qVal(analyzer.getAnalyzerId())
						+ " ORDER BY element_id";
				ResultSet rs3 = null;
				try {
					rs3 = stmt3.executeQuery(query);
				} catch (SQLException e) {
					logger.error(classMethod + query, e);
					throw e;
				}
				WindElement windElement = null;
				while (rs3.next()) {
					GenericElement element = null;
					String type = rs3.getString("type");
					int elementId = rs3.getInt("element_id");
					if (type.equals(GenericElement.SCALAR)
							|| type.equals(GenericElement.RAIN)
							|| type.equals(GenericElement.WINDCOMPONENTSPEED)
							|| type.equals(GenericElement.WINDCOMPONENTDIR)) {
						element = new ScalarElement(type, analyzer);
						Statement stmt4 = conn.createStatement();
						query = "SELECT measure_name, min_val, max_val, num_dec"
								+ " FROM scalar_element WHERE element_id="
								+ DbUtils.qVal(elementId);
						ResultSet rs4 = null;
						try {
							rs4 = stmt4.executeQuery(query);
						} catch (SQLException e) {
							logger.error(classMethod + query, e);
							throw e;
						}
						if (rs4.next()) {
							((ScalarElement) (element)).setUnit(rs4
									.getString("measure_name"));
							((ScalarElement) (element)).setMinValue(rs4
									.getFloat("min_val"));
							((ScalarElement) (element)).setMaxValue(rs4
									.getFloat("max_val"));
							((ScalarElement) (element))
									.setDecimalQuantityValue(rs4
											.getInt("num_dec"));
						}
						rs4.close();
						stmt4.close();
					} else if (type.equals(GenericElement.WIND)) {
						windElement = new WindElement(analyzer);
						element = windElement;
					} else
						throw new CentraleException("unknown_element_type");
					element.setElementId(elementId);
					element.setName(rs3.getString("param_id"));
					element.setEnabled(DbUtils.rsGetBooleanFromInt(rs3,
							rs3.findColumn("enabled")));
					element.setDeletionDate(DbUtils.rsGetDate(rs3,
							rs3.findColumn("delete_date")));
					element.setUpdateDate(rs3.getTimestamp("update_date"));
					if (type.equals(GenericElement.WINDCOMPONENTSPEED))
						windElement.setSpeed((ScalarElement) element);
					else if (type.equals(GenericElement.WINDCOMPONENTDIR))
						windElement.setDirection((ScalarElement) element);
					else
						elements.add(element);

					// read avgPeriods for each element
					Vector<AvgPeriod> avgPeriods = new Vector<AvgPeriod>();
					Statement stmt5 = conn.createStatement();
					query = "SELECT avg_period_id, delete_date, update_date,"
							+ " all_data_downloaded, last_date_on_matched_db "
							+ " FROM element_avg_period WHERE element_id="
							+ DbUtils.qVal(elementId);
					ResultSet rs5 = null;
					try {
						rs5 = stmt5.executeQuery(query);
					} catch (SQLException e) {
						logger.error(classMethod + query, e);

						throw e;
					}
					while (rs5.next()) {
						AvgPeriod avgPeriod = new AvgPeriod(
								rs5.getInt("avg_period_id"), element);
						avgPeriod.setDeletionDate(DbUtils.rsGetDate(rs5,
								rs5.findColumn("delete_date")));
						avgPeriod
								.setUpdateDate(rs5.getTimestamp("update_date"));
						avgPeriod.setAllDataDownloaded(DbUtils
								.rsGetBooleanFromInt(rs5,
										rs5.findColumn("all_data_downloaded")));
						avgPeriods.add(avgPeriod);
						// logger.debug("readStations() set AvgPeriod val: "
						// + avgPeriod.getAvgPeriodVal());
					}

					element.setAvgPeriod(avgPeriods);
					rs5.close();
					stmt5.close();
				}
				analyzer.setElements(elements);
				rs3.close();
				stmt3.close();
			}
			station.setAnalyzersVect(analyzersVect);
			rs2.close();
			stmt2.close();

			// read container alarms for each station
			Vector<ContainerAlarm> cAlarmVect = new Vector<ContainerAlarm>();
			Statement stmt6 = conn.createStatement();
			query = "SELECT s.station_alarm_id, s.uuid, s.alarm_id,"
					+ " s.notes, s.delete_date, s.update_date, a.alarm_name, a.type"
					+ " FROM station_alarm s, alarm_name a"
					+ " WHERE s.station_id="
					+ DbUtils.qVal(station.getStationId())
					+ " AND s.alarm_id = a.alarm_id";
			ResultSet rs6 = null;
			try {
				rs6 = stmt6.executeQuery(query);
			} catch (SQLException e) {
				logger.error(classMethod + query, e);

				throw e;
			}
			while (rs6.next()) {
				ContainerAlarm cAlarm = new ContainerAlarm();
				cAlarm.setAlarmDbID(rs6.getInt("station_alarm_id"));
				cAlarm.setAlarmUUID(rs6.getString("uuid"));
				cAlarm.setAlarmID(rs6.getString("alarm_id"));
				cAlarm.setNotes(rs6.getString("notes"));
				cAlarm.setDeletionDate(rs6.getTimestamp("delete_date"));
				cAlarm.setUpdateDate(rs6.getTimestamp("update_date"));
				cAlarm.setAlarmName(rs6.getString("alarm_name"));
				cAlarm.setType(rs6.getString("type"));
				cAlarmVect.add(cAlarm);
			}
			station.setAlarmsVect(cAlarmVect);
			rs6.close();
			stmt6.close();

			// read container alarm status
			for (int i = 0; i < station.getContainerAlarm().size(); i++) {
				ContainerAlarm cAlarm = station.getContainerAlarm().get(i);
				Statement stmt7 = conn.createStatement();
				query = "SELECT status, update_date FROM station_alarm_status"
						+ " WHERE station_alarm_id="
						+ DbUtils.qVal(cAlarm.getAlarmDbID());
				ResultSet rs7 = null;
				try {
					rs7 = stmt7.executeQuery(query);
				} catch (SQLException e) {
					logger.error(classMethod + query, e);

					throw e;
				}
				if (rs7.next()) {
					AlarmData alarmData = new AlarmData();
					alarmData.setValue(rs7.getString("status"));
					alarmData.setTimestamp(rs7.getTimestamp("update_date"));
					cAlarm.setLastStatus(alarmData);
				}
				rs7.close();
				stmt7.close();
			}

			// read analyzer alarm status
			for (int i = 0; i < station.getAnalyzersVect().size(); i++) {
				Analyzer analyzer = station.getAnalyzersVect().get(i);
				Vector<AnalyzerEventDatum> anStatusVect = new Vector<AnalyzerEventDatum>();
				Statement stmt8 = conn.createStatement();
				query = "SELECT a.analyzer_alarm_type, a.status, a.update_date"
						+ " FROM analyzer_alarm_status a, analyzer_alarm_type at"
						+ " WHERE a.analyzer_id="
						+ DbUtils.qVal(analyzer.getAnalyzerId())
						+ " AND a.analyzer_alarm_type=at.analyzer_alarm_type"
						+ " ORDER by at.analyzer_alarm_type_id";
				ResultSet rs8 = null;
				try {
					rs8 = stmt8.executeQuery(query);
				} catch (SQLException e) {
					logger.error(classMethod + query, e);

					throw e;
				}
				while (rs8.next()) {
					AnalyzerEventDatum anStatus = new AnalyzerEventDatum();
					anStatus.setType(rs8.getString("analyzer_alarm_type"));
					anStatus.setValue(DbUtils.rsGetBooleanObjFromInt(rs8,
							rs8.findColumn("status")));
					anStatus.setTimestamp(rs8.getTimestamp("update_date"));
					anStatusVect.add(anStatus);
				}
				analyzer.setAnStatus(anStatusVect);
				rs8.close();
				stmt8.close();
			}
		}
		rs.close();
		stmt.close();
		return stVect;
	} // end readStations

	public Station readStation(int stationId) throws SQLException {
		String classMethod = "[readStation] ";
		Station station = new Station();
		Statement stmt = conn.createStatement();
		String query = "SELECT uuid, shortname, name, province,"
				+ " city, address, location, notes, map_x, map_y, delete_date,"
				+ " enabled, ip_address, ip_port, tel_number, "
				+ " router_ip_address, use_modem, force_polling_time, "
				+ " use_gps, config_uuid, min_timestamp_for_polling, "
				+ " sample_data_download_enabled, "
				+ " config_author, config_notes, config_date, "
				+ " update_date, common_config_date, lan, proxy, send_common_config, virtual_cop_id FROM station"
				+ " WHERE station_id=" + DbUtils.qVal(stationId)
				+ " ORDER BY name,shortname";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			station.setStationId(stationId);
			station.setStationUUID(rs.getString("uuid"));
			StationInfo stInfo = new StationInfo();
			stInfo.setShortStationName(rs.getString("shortname"));
			stInfo.setLongStationName(rs.getString("name"));
			stInfo.setProvince(rs.getString("province"));
			stInfo.setCity(rs.getString("city"));
			stInfo.setAddress(rs.getString("address"));
			stInfo.setLocation(rs.getString("location"));
			stInfo.setEnabled(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("enabled")));
			stInfo.setGps(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("use_gps")));
			stInfo.setConfigUUID(rs.getString("config_uuid"));
			stInfo.setMap_x(DbUtils.rsGetInt(rs, rs.findColumn("map_x")));
			stInfo.setMap_y(DbUtils.rsGetInt(rs, rs.findColumn("map_y")));
			stInfo.setDeletionDate(DbUtils.rsGetDate(rs,
					rs.findColumn("delete_date")));
			stInfo.setForcePollingTime(DbUtils.rsGetInt(rs,
					rs.findColumn("force_polling_time")));
			stInfo.setMinTimestampForPolling(DbUtils.rsGetDate(rs,
					rs.findColumn("min_timestamp_for_polling")));
			stInfo.setSampleDataDownloadEnable(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("sample_data_download_enabled")));
			stInfo.setConfigUserName(rs.getString("config_author"));
			stInfo.setConfigComment(rs.getString("config_notes"));
			stInfo.setConfigDate(DbUtils.rsGetDate(rs,
					rs.findColumn("config_date")));
			stInfo.setUpdateDate(rs.getTimestamp("update_date"));
			stInfo.setCommonConfigStationUpdateDate(rs
					.getTimestamp("common_config_date"));
			stInfo.setCommonConfigSendingEnable(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("send_common_config")));
			stInfo.setCopId(rs.getInt("virtual_cop_id"));
			stInfo.setCopName(readCopName(stInfo.getCopId()));
			CommDeviceInfo commDeviceInfo = new CommDeviceInfo();
			commDeviceInfo.setIp(rs.getString("ip_address"));
			commDeviceInfo.setPortNumber(DbUtils.rsGetInt(rs,
					rs.findColumn("ip_port")));
			commDeviceInfo.setModem(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("use_modem")));
			commDeviceInfo.setPhoneNumber(rs.getString("tel_number"));
			commDeviceInfo
					.setRouterIpAddress(rs.getString("router_ip_address"));
			commDeviceInfo.setLan(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("lan")));
			commDeviceInfo.setProxy(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("proxy")));
			stInfo.setCommDevice(commDeviceInfo);
			station.setStInfo(stInfo);
			StationStatus stStatus = new StationStatus();
			station.setStStatus(stStatus);
		}
		rs.close();
		stmt.close();
		return station;
	} // end readStation

	public int readStationId() throws SQLException {
		String classMethod = "[readStationId] ";
		int stationId = -1;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(station_id) FROM station";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			stationId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return stationId;
	} // end readStationId

	public Integer isExistingCopIdUuid(String uuid, int stId, int virtualCopId)
			throws SQLException {
		String classMethod = "[isExistingUuid] ";
		Integer stationId = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT station_id FROM station WHERE uuid = "
				+ DbUtils.qVal(uuid) + " AND station_id <> " + stId
				+ " AND virtual_cop_id = " + virtualCopId;
		logger.debug(query);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		if (rs.next()) {
			stationId = rs.getInt(1);
		}
		rs.close();
		stmt.close();

		return stationId;
	} // end isExistingUuid

	public int readAnalyzerId(String analyzerUUID) throws SQLException,
			CentraleException {
		String classMethod = "[readAnalyzerId] ";
		int analyzerId = -1;
		Statement stmt = conn.createStatement();
		if (analyzerUUID == null)
			throw new CentraleException("analyzerUUID is null");
		String query = "SELECT max(analyzer_id) FROM analyzer WHERE uuid="
				+ DbUtils.qVal(analyzerUUID);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			analyzerId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return analyzerId;
	} // end readAnalyzerId

	public int readElementId(String param, int analyzerId) throws SQLException,
			CentraleException {
		String classMethod = "[readElementId] ";
		int elementId = -1;
		Statement stmt = conn.createStatement();
		if (param == null)
			throw new CentraleException("param is null");
		String query = "SELECT max(element_id) FROM element"
				+ " WHERE param_id = " + DbUtils.qVal(param)
				+ " AND analyzer_id = " + DbUtils.qVal(analyzerId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			elementId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return elementId;
	} // end readElementId

	public int readContainerAlarmId(String alarmUUID) throws SQLException,
			CentraleException {
		String classMethod = "[readContainerAlarmId] ";
		int alarmDbId = -1;
		Statement stmt = conn.createStatement();
		if (alarmUUID == null)
			throw new CentraleException("alarmUUID is null");
		String query = "SELECT max(station_alarm_id) FROM station_alarm WHERE uuid="
				+ DbUtils.qVal(alarmUUID);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			alarmDbId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return alarmDbId;
	} // end readContainerAlarmId

	public String[] readAlarmNameInfo(String alarmId) throws SQLException,
			CentraleException {
		String classMethod = "[readAlarmNameInfo] ";
		String[] resValue = new String[2];
		Statement stmt = conn.createStatement();
		String query = "SELECT alarm_name, type FROM alarm_name WHERE alarm_id="
				+ DbUtils.qVal(alarmId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			resValue[0] = rs.getString("alarm_name");
			resValue[1] = rs.getString("type");
		}
		rs.close();
		stmt.close();
		return resValue;
	} // end readAlarmNameInfo

	public Date readLastSampleTimestamp(int elementId) throws SQLException {
		String classMethod = "[readLastSampleTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_sample_data) FROM sample_data "
				+ "WHERE element_id=" + DbUtils.qVal(elementId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastSampleTimestamp

	public long readLastConfiguration(int stationId) throws SQLException {
		String classMethod = "[readLastConfiguration] ";
		long retDateLong = -1;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(last_modified_time) FROM station_configuration "
				+ "WHERE station_id = " + DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDateLong = rs.getLong(1);
		}
		rs.close();
		stmt.close();
		return retDateLong;
	} // end readLastConfiguration

	public List<Date> readConfigFileDateList(int stationId, Date startDate,
			Date endDate, Integer maxNumber) throws SQLException {
		String classMethod = "[readConfigFileDateList] ";
		int maxNumberDates = (maxNumber != null ? maxNumber.intValue() : 10);
		Calendar cal = new GregorianCalendar();
		cal.setTime(Centrale.MIN_START_TIMESTAMP);
		long startDateMillis = cal.getTimeInMillis();
		if (startDate != null) {
			cal.setTime(startDate);
			startDateMillis = cal.getTimeInMillis();
		}
		cal.setTime(new Date());
		long endDateMillis = cal.getTimeInMillis();
		if (endDate != null) {
			cal.setTime(endDate);
			endDateMillis = cal.getTimeInMillis();
		}

		List<Date> retDateList = new ArrayList<Date>();
		Statement stmt = conn.createStatement();
		String query = "SELECT last_modified_time FROM station_configuration "
				+ "WHERE station_id = " + DbUtils.qVal(stationId)
				+ " AND last_modified_time >= " + DbUtils.qVal(startDateMillis)
				+ " AND last_modified_time < " + DbUtils.qVal(endDateMillis)
				+ " ORDER BY last_modified_time DESC";
		int i = 0;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next() && i < maxNumberDates) {
			long tmpDateLong = rs.getLong(1);
			cal.setTimeInMillis(tmpDateLong);
			retDateList.add(cal.getTime());
			i++;
		}
		rs.close();

		return retDateList;
	} // end readConfigFileDateList

	public StringBuilder readConfiguration(int stationId, long lastModifiedTime)
			throws SQLException {
		String classMethod = "[readConfiguration] ";
		StringBuilder retStringBuilder = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT configuration FROM station_configuration WHERE station_id = "
				+ DbUtils.qVal(stationId)
				+ " AND last_modified_time = "
				+ DbUtils.qVal(lastModifiedTime);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			String tmpString = new String(rs.getBytes(1));
			retStringBuilder = new StringBuilder(tmpString);
		}
		rs.close();

		return retStringBuilder;
	}// end readConfiguration

	public List<StationConfigHolder> readActiveConfigurations()
			throws SQLException {
		String classMethod = "[readActiveConfigurations] ";
		Statement stmt = conn.createStatement();
		String query = "select st.virtual_cop_id, sc.station_id, mx.max_time,"
				+ " sc.update_date, sc.configuration"
				+ " from station_configuration sc"
				+ " inner join (select station_id, virtual_cop_id from station"
				+ " where delete_date is null) st"
				+ " on sc.station_id=st.station_id"
				+ " inner join (select station_id, MAX(last_modified_time)"
				+ " as max_time from station_configuration group by station_id)"
				+ " mx on sc.station_id=mx.station_id"
				+ " and sc.last_modified_time=mx.max_time"
				+ " order by sc.station_id";
		List<StationConfigHolder> listConfigs = new ArrayList<StationConfigHolder>();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			listConfigs.add(new StationConfigHolder(rs.getInt(1), rs.getInt(2),
					rs.getLong(3), rs.getTimestamp(4), rs.getBytes(5)));
		}
		rs.close();
		stmt.close();
		return listConfigs;
	}

	public Long getLastStationModifiedConfDate(int stationId)
			throws SQLException {
		String classMethod = "[getLastStationModifiedConfDate] ";
		Long modifiedDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(last_modified_time) AS modified_date FROM "
				+ "station_configuration WHERE station_id = "
				+ DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			modifiedDate = rs.getLong(rs.findColumn("modified_date"));
		}
		rs.close();

		return modifiedDate;
	}// end getLastStationModifiedConfDate

	public Date readLastMeanTimestamp(int elementId, Integer avgPeriodId)
			throws SQLException {
		String classMethod = "[readLastMeanTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_mean_data) FROM mean_data "
				+ "WHERE element_id=" + DbUtils.qVal(elementId)
				+ " AND avg_period_id=" + DbUtils.qVal(avgPeriodId);
		ResultSet rs = null;
		logger.debug(classMethod + query);
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastMeanTimestamp

	public Date readLastMeanWindTimestamp(int elementId, Integer avgPeriodId)
			throws SQLException {
		String classMethod = "[readLastMeanWindTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_mean_wind_data) FROM mean_wind_data "
				+ "WHERE element_id="
				+ DbUtils.qVal(elementId)
				+ " AND avg_period_id=" + DbUtils.qVal(avgPeriodId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastMeanWindTimestamp

	public Date readLastCAlarmStatusTimestamp(int stationAlarmId)
			throws SQLException {
		String classMethod = "[readLastCAlarmStatusTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_station_alarm) "
				+ "FROM station_alarm_history " + "WHERE station_alarm_id="
				+ DbUtils.qVal(stationAlarmId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastCAlarmStatusTimestamp

	public Boolean readStationAlarmStatusHistory(int stationAlarmId,
			Date startDate, Date endDate, Integer numMaxData,
			List<AlarmStatusObject> alarmStObjList) throws SQLException {
		String classMethod = "[readStationAlarmStatusHistory] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT status, timestamp_station_alarm "
				+ "FROM station_alarm_history " + "WHERE station_alarm_id="
				+ DbUtils.qVal(stationAlarmId)
				+ " AND timestamp_station_alarm>" + DbUtils.qVal(startDate)
				+ " AND timestamp_station_alarm<=" + DbUtils.qVal(endDate)
				+ " ORDER BY timestamp_station_alarm";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int rowCount = 0;
		int maxRowCount = Integer.MAX_VALUE;
		if (numMaxData != null)
			maxRowCount = numMaxData.intValue();
		while (rs.next() && rowCount < maxRowCount) {
			AlarmStatusObject alarmStatusObj = new AlarmStatusObject();
			alarmStatusObj.setStatus(rs.getString("status"));
			alarmStatusObj.setTimestamp(rs
					.getTimestamp("timestamp_station_alarm"));
			alarmStatusObj.setTimestampString(sdf
					.format(alarmStatusObj.getTimestamp()));
			alarmStatusObj.setAlarmDbId(stationAlarmId);
			alarmStObjList.add(alarmStatusObj);
			rowCount++;
		}
		Boolean maxDataReached = null;
		if (numMaxData != null) {
			if (rowCount == numMaxData.intValue())
				maxDataReached = new Boolean(true);
			else
				maxDataReached = new Boolean(false);
		}
		rs.close();
		stmt.close();
		return maxDataReached;
	} // end readStationAlarmStatusHistory

	public Date readLastAnalyzerAlarmStatusTimestamp(int analyzerId,
			String anAlarmType) throws SQLException {
		String classMethod = "[readLastAnalyzerAlarmStatusTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_analyzer_alarm) "
				+ "FROM analyzer_alarm_history " + "WHERE analyzer_id="
				+ DbUtils.qVal(analyzerId) + " AND analyzer_alarm_type="
				+ DbUtils.qVal(anAlarmType);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();

		return retDate;
	} // end readLastAnalyzerAlarmStatusTimestamp

	public Boolean readAnalyzerAlarmStatusHistory(int analyzerId,
			String anAlarmType, Date startDate, Date endDate,
			Integer numMaxData,
			List<AnalyzerAlarmStatusValuesObject> anStatusObjList)
			throws SQLException {
		String classMethod = "[readAnalyzerAlarmStatusHistory] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT status, timestamp_analyzer_alarm "
				+ "FROM analyzer_alarm_history " + "WHERE analyzer_id="
				+ DbUtils.qVal(analyzerId) + " AND analyzer_alarm_type="
				+ DbUtils.qVal(anAlarmType) + " AND timestamp_analyzer_alarm>"
				+ DbUtils.qVal(startDate) + " AND timestamp_analyzer_alarm<="
				+ DbUtils.qVal(endDate) + " ORDER BY timestamp_analyzer_alarm";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int rowCount = 0;
		int maxRowCount = Integer.MAX_VALUE;
		if (numMaxData != null)
			maxRowCount = numMaxData.intValue();
		while (rs.next() && rowCount < maxRowCount) {
			AnalyzerAlarmStatusValuesObject anAlarmStValuesObj = new AnalyzerAlarmStatusValuesObject();
			anAlarmStValuesObj.setValue(DbUtils.rsGetBooleanObjFromInt(rs,
					rs.findColumn("status")));
			anAlarmStValuesObj.setTimestamp(rs
					.getTimestamp("timestamp_analyzer_alarm"));
			anAlarmStValuesObj.setTimestampString(sdf
					.format(anAlarmStValuesObj.getTimestamp()));
			anAlarmStValuesObj.setType(anAlarmType);
			anStatusObjList.add(anAlarmStValuesObj);
			rowCount++;
		}
		Boolean maxDataReached = null;
		if (numMaxData != null) {
			if (rowCount == numMaxData.intValue())
				maxDataReached = new Boolean(true);
			else
				maxDataReached = new Boolean(false);
		}
		rs.close();
		stmt.close();
		return maxDataReached;
	} // end readAnalyzerAlarmStatusHistory

	public Date readLastInformaticStatusTimestamp(int stationId)
			throws SQLException {
		String classMethod = "[readLastInformaticStatusTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_informatic_status)"
				+ " FROM informatic_status_history" + " WHERE station_id="
				+ DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastInformaticStatusTimestamp

	public PerifericoAppStatus readPerifericoAppStatusHistory(int stationId,
			Date informaticStatusTimestamp) throws SQLException {
		String classMethod = "[readInformaticAndFilesystemStatusHistory] ";
		PerifericoAppStatus appStatus = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT is_ok,board_manager_init_status,cfg_boards_number,"
				+ "init_boards_number,failed_boards_binding_number,dpa_ok,"
				+ "drvcfg_ok,enabled_dpa_number,init_dpa_drivers_number,"
				+ "failed_dpa_threads_number,load_cfg_status,save_cfg_status,"
				+ "cfg_activation_status,total_thread_failures,"
				+ "current_thread_failures,data_write_error_count,"
				+ "cmm_cfg_from_cop,date_in_future"
				+ " FROM informatic_status_history"
				+ " WHERE station_id="
				+ DbUtils.qVal(stationId)
				+ " AND timestamp_informatic_status="
				+ DbUtils.qVal(informaticStatusTimestamp);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			appStatus = new PerifericoAppStatus(
					DbUtils.rsGetBooleanFromInt(rs, rs.findColumn("is_ok")), //
					DbUtils.rsGetBooleanObjFromInt(rs,
							rs.findColumn("board_manager_init_status")), //
					DbUtils.rsGetInt(rs, rs.findColumn("cfg_boards_number")), //
					DbUtils.rsGetInt(rs, rs.findColumn("init_boards_number")), //
					DbUtils.rsGetInt(rs,
							rs.findColumn("failed_boards_binding_number")), //
					DbUtils.rsGetBooleanObjFromInt(rs, rs.findColumn("dpa_ok")), //
					DbUtils.rsGetBooleanObjFromInt(rs,
							rs.findColumn("drvcfg_ok")), //
					DbUtils.rsGetInt(rs, rs.findColumn("enabled_dpa_number")), //
					DbUtils.rsGetInt(rs,
							rs.findColumn("init_dpa_drivers_number")), //
					DbUtils.rsGetInt(rs,
							rs.findColumn("failed_dpa_threads_number")), //
					DbUtils.rsGetEnum(PerifericoAppStatus.ConfigStatus.class,
							rs, rs.findColumn("load_cfg_status")), //
					DbUtils.rsGetBooleanObjFromInt(rs,
							rs.findColumn("save_cfg_status")), //
					DbUtils.rsGetBooleanObjFromInt(rs,
							rs.findColumn("cfg_activation_status")), //
					DbUtils.rsGetEnum(CommonConfigResult.class, rs,
							rs.findColumn("cmm_cfg_from_cop")), //
					DbUtils.rsGetBooleanObjFromInt(rs,
							rs.findColumn("date_in_future")), //
					rs.getInt("total_thread_failures"), //
					rs.getInt("current_thread_failures"), //
					rs.getInt("data_write_error_count"));
		}
		rs.close();
		stmt.close();
		return appStatus;
	} // end readPerifericoAppStatusHistory

	public DiskStatus readPerifericoDiskStatusHistory(int stationId,
			Date informaticStatusTimestamp) throws SQLException {
		String classMethod = "[readInformaticAndFilesystemStatusHistory] ";
		DiskStatus diskStatus = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT" //
				+ " warning_level,alarm_level,smart_status,raid_status" //
				+ " FROM informatic_status_history" //
				+ " WHERE station_id="
				+ DbUtils.qVal(stationId) //
				+ " AND timestamp_informatic_status="
				+ DbUtils.qVal(informaticStatusTimestamp);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		if (rs.next()) {
			HashMap<String, Integer> hm_filesystem = new HashMap<String, Integer>();
			Statement stmt2 = conn.createStatement();
			String query2 = "SELECT filesystem_name,status"
					+ " FROM filesystem_status_history" + " WHERE station_id="
					+ DbUtils.qVal(stationId)
					+ " AND timestamp_informatic_status="
					+ DbUtils.qVal(informaticStatusTimestamp);
			ResultSet rs2 = null;
			try {
				rs2 = stmt2.executeQuery(query2);
			} catch (SQLException e) {
				logger.error(classMethod + query2, e);
				throw e;
			}
			while (rs2.next()) {
				String filesystemName = rs2.getString("filesystem_name");
				Integer status = DbUtils
						.rsGetInt(rs2, rs2.findColumn("status"));
				hm_filesystem.put(filesystemName, status);
			}
			diskStatus = new DiskStatus(
					DbUtils.rsGetEnum(DiskStatus.Status.class, rs,
							rs.findColumn("raid_status")), //
					DbUtils.rsGetEnum(DiskStatus.Status.class, rs,
							rs.findColumn("smart_status")), //
					rs.getInt("warning_level"), rs.getInt("alarm_level"),
					hm_filesystem.get("root"), hm_filesystem.get("temp"),
					hm_filesystem.get("data"));
			rs2.close();
			stmt2.close();
		}
		rs.close();
		stmt.close();
		return diskStatus;
	} // end readPerifericoDiskStatusHistory

	public Date readLastGpsDataTimestamp(int stationId) throws SQLException {
		String classMethod = "[readLastGpsDataTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT max(timestamp_gps_data) FROM gps_data "
				+ "WHERE station_id=" + DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readLastGpsDataTimestamp

	public GpsData readGpsData(int stationId, Date timestampGpsData)
			throws SQLException {
		String classMethod = "[readGpsData] ";
		GpsData gpsData = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT latitude,longitude,altitude,notes,update_date"
				+ " FROM gps_data WHERE station_id=" + DbUtils.qVal(stationId)
				+ " AND timestamp_gps_data=" + DbUtils.qVal(timestampGpsData);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			gpsData = new GpsData();
			gpsData.setTimestamp(timestampGpsData);
			gpsData.setLatitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("latitude")));
			gpsData.setLongitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("longitude")));
			gpsData.setAltitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("altitude")));
			gpsData.setComment(rs.getString("notes"));
			gpsData.setUpdateDate(rs.getTimestamp("update_date"));
		}
		rs.close();
		stmt.close();
		return gpsData;
	} // end readGpsData

	public List<String> readPhysicalDimension() throws SQLException {
		String classMethod = "[readPhysicalDimension] ";
		List<String> physicalDimension = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		String query = "SELECT physical_dimension FROM physical_dimension";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			physicalDimension.add(rs.getString(rs
					.findColumn("physical_dimension")));
		}
		rs.close();
		stmt.close();

		return physicalDimension;
	}// end readPhysicalDimension

	public List<String> readModem() throws SQLException {
		String classMethod = "[readModem] ";
		List<String> modemList = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		String query = "SELECT device_id FROM modem";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			modemList.add(rs.getString(rs.findColumn("device_id")));
		}
		rs.close();
		stmt.close();

		return modemList;
	}// end readModem

	public StorageManagerInfo readStorageManager(int configId)
			throws SQLException {
		String classMethod = "[readStorageManager] ";
		StorageManagerInfo storageManager = new StorageManagerInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT max_days_of_data, max_days_of_aggregate_data,"
				+ " disk_full_warning_threshold_percent,  "
				+ "disk_full_alarm_threshold_percent FROM config "
				+ "WHERE config_id =" + DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			Integer tmpInt = DbUtils.rsGetInt(rs,
					rs.findColumn("max_days_of_data"));
			if (tmpInt != null)
				storageManager.setMaxDaysOfData(tmpInt);
			tmpInt = DbUtils.rsGetInt(rs,
					rs.findColumn("max_days_of_aggregate_data"));
			if (tmpInt != null)
				storageManager.setMaxDaysOfAggregateData(tmpInt);
			tmpInt = DbUtils.rsGetInt(rs,
					rs.findColumn("disk_full_alarm_threshold_percent"));
			if (tmpInt != null)
				storageManager.setDiskFullAlarmThresholdPercent(tmpInt);
			tmpInt = DbUtils.rsGetInt(rs,
					rs.findColumn("disk_full_warning_threshold_percent"));
			if (tmpInt != null)
				storageManager.setDiskFullWarningThresholdPercent(tmpInt);
		}
		rs.close();
		stmt.close();

		return storageManager;
	}// end readStorageManager

	public StorageManagerCfg readAllStorageManager(int configId)
			throws SQLException {
		String classMethod = "[readAllStorageManager] ";
		StorageManagerCfg storageManager = new StorageManagerCfg();
		Statement stmt = conn.createStatement();
		String query = "SELECT max_days_of_data, max_days_of_aggregate_data,"
				+ " disk_full_warning_threshold_percent,  "
				+ "disk_full_alarm_threshold_percent FROM config "
				+ "WHERE config_id =" + DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			Integer tmpIntValue = DbUtils.rsGetInt(rs,
					rs.findColumn("max_days_of_data"));
			if (tmpIntValue != null)
				storageManager.setMaxDaysOfData(tmpIntValue);
			tmpIntValue = DbUtils.rsGetInt(rs,
					rs.findColumn("max_days_of_aggregate_data"));
			if (tmpIntValue != null)
				storageManager.setMaxDaysOfAggregateData(tmpIntValue);
			tmpIntValue = DbUtils.rsGetInt(rs,
					rs.findColumn("disk_full_alarm_threshold_percent"));
			if (tmpIntValue != null)
				storageManager.setDiskFullAlarmThresholdPercent(tmpIntValue);
			tmpIntValue = DbUtils.rsGetInt(rs,
					rs.findColumn("disk_full_warning_threshold_percent"));
			if (tmpIntValue != null)
				storageManager.setDiskFullWarningThresholdPercent(tmpIntValue);
		}
		rs.close();
		stmt.close();

		return storageManager;
	}// end readAllStorageManager

	public OtherInfo readOther(int configId) throws SQLException {
		String classMethod = "[readOther] ";
		OtherInfo other = new OtherInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT door_alarm_id, data_write_to_disk_period, "
				+ "manual_operations_auto_reset_period, cop_service_port,"
				+ " maps_site_url_formatter FROM config WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			other.setManualOperationsAutoResetPeriod(DbUtils.rsGetInt(rs,
					rs.findColumn("manual_operations_auto_reset_period")));
			other.setDataWriteToDiskPeriod(DbUtils.rsGetInt(rs,
					rs.findColumn("data_write_to_disk_period")));
			other.setDoorAlarmId(rs.getString(rs.findColumn("door_alarm_id")));
			other.setCopServicePort(DbUtils.rsGetInt(rs,
					rs.findColumn("cop_service_port")));
			other.setMapsSiteURLFormatter(rs.getString(rs
					.findColumn("maps_site_url_formatter")));
		}
		rs.close();
		stmt.close();

		return other;
	}// end readOther

	public Integer getCopServicePort(int configId) throws SQLException {
		String classMethod = "[getCopServicePort] ";
		Integer port = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT cop_service_port  FROM config WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			port = DbUtils.rsGetInt(rs, rs.findColumn("cop_service_port"));
		}
		rs.close();
		stmt.close();

		return port;
	}// end getCopServicePort

	public Integer getDataWriteToDiskPeriod(int configId) throws SQLException {
		String classMethod = "[getDataWriteToDiskPeriod] ";
		Integer dataWriteToDiskPeriod = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT data_write_to_disk_period FROM config WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			dataWriteToDiskPeriod = DbUtils.rsGetInt(rs,
					rs.findColumn("data_write_to_disk_period"));
		}
		rs.close();
		stmt.close();

		return dataWriteToDiskPeriod;
	}// end getDataWriteToDiskPeriod

	public Integer getManualOperationAutoResetPeriod(int configId)
			throws SQLException {
		String classMethod = "[getManualOperationAutoResetPeriod] ";
		Integer manualOperationsAutoResetPeriod = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT manual_operations_auto_reset_period FROM config WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			manualOperationsAutoResetPeriod = DbUtils.rsGetInt(rs,
					rs.findColumn("manual_operations_auto_reset_period"));
		}
		rs.close();
		stmt.close();

		return manualOperationsAutoResetPeriod;
	}// end getManualOperationAutoResetPeriod

	public String getMapsSiteURLFormatter(int configId) throws SQLException {
		String classMethod = "[getMapsSiteURLFormatter] ";
		String mapsSiteUrl = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT maps_site_url_formatter FROM config WHERE config_id = "
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			mapsSiteUrl = rs
					.getString(rs.findColumn("maps_site_url_formatter"));
			;
		}
		rs.close();
		stmt.close();

		return mapsSiteUrl;
	}// end getMapsSiteURLFormatter

	public StandardInfo readStandard(int configId) throws SQLException {
		String classMethod = "[readStorageManager] ";
		StandardInfo standard = new StandardInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT reference_temperature_k,"
				+ " reference_pressure_kpa FROM config " + "WHERE config_id ="
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			standard.setReferencePressureKPa(DbUtils.rsGetDouble(rs,
					rs.findColumn("reference_pressure_kpa")));
			standard.setReferenceTemperatureK(DbUtils.rsGetDouble(rs,
					rs.findColumn("reference_temperature_k")));
		}
		rs.close();
		stmt.close();

		return standard;
	}// end readStandard

	public ModemInfo readModemInfo(String deviceId) throws SQLException {
		String classMethod = "[readModemInfo] ";
		ModemInfo modem = new ModemInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT device_id, shared_line, phone_prefix FROM modem"
				+ " WHERE device_id =" + DbUtils.qVal(deviceId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			modem.setDeviceId(rs.getString(rs.findColumn("device_id")));
			modem.setSharedLine(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("shared_line")));
			modem.setPhonePrefix(rs.getString(rs.findColumn("phone_prefix")));
		}
		rs.close();
		stmt.close();

		return modem;
	}// end readModemInfo

	public Standards readAllStandard(int configId) throws SQLException {
		String classMethod = "[readAllStandard] ";
		Standards standard = new Standards();
		Statement stmt = conn.createStatement();
		String query = "SELECT reference_temperature_k,"
				+ " reference_pressure_kpa FROM config " + "WHERE config_id ="
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			standard.setReferencePressure_kPa(DbUtils.rsGetDouble(rs,
					rs.findColumn("reference_pressure_kpa")));
			standard.setReferenceTemperature_K(DbUtils.rsGetDouble(rs,
					rs.findColumn("reference_temperature_k")));
		}
		rs.close();
		stmt.close();

		return standard;
	}// end readAllStandard

	public String readDoorAlarmId(int configId) throws SQLException {
		String classMethod = "[readDoorAlarmId] ";
		String doorAlamrId = "";
		Statement stmt = conn.createStatement();
		String query = "SELECT door_alarm_id FROM config WHERE config_id ="
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			doorAlamrId = rs.getString(rs.findColumn("door_alarm_id"));
		}
		rs.close();
		stmt.close();

		return doorAlamrId;
	}// end readDoorAlarmId

	public Integer getAvgPeriodToUpdate() throws SQLException {
		String classMethod = "[getAvgPeriodToUpdate] ";
		Integer avgPeriod = new Integer(0);
		Statement stmt = conn.createStatement();
		String query = "SELECT avg_period_id FROM avg_period WHERE "
				+ "default_avg = '1'";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			avgPeriod = DbUtils.rsGetInt(rs, rs.findColumn("avg_period_id"));
		}
		rs.close();
		stmt.close();

		return avgPeriod;
	}// end getAvgPeriodToUpdate

	public List<AvgPeriodInfo> readAvgPeriod() throws SQLException {
		String classMethod = "[readAvgPeriod] ";
		List<AvgPeriodInfo> avgPeriodList = new ArrayList<AvgPeriodInfo>();
		Statement stmt = conn.createStatement();
		String query = "SELECT avg_period_id, default_avg FROM avg_period "
				+ "ORDER BY avg_period_id ASC";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			AvgPeriodInfo avgPeriod = new AvgPeriodInfo();
			avgPeriod.setAvgPeriodId(DbUtils.rsGetInt(rs,
					rs.findColumn("avg_period_id")));
			avgPeriod.setDefaultAvgPeriod(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("default_avg")));
			avgPeriodList.add(avgPeriod);
		}
		rs.close();
		stmt.close();

		return avgPeriodList;
	}// end readAvgPeriod

	public List<Integer> readNotDefaultAvgPeriod() throws SQLException {
		String classMethod = "[readNotDefaultAvgPeriod] ";
		List<Integer> avgPeriodList = new ArrayList<Integer>();
		Statement stmt = conn.createStatement();
		String query = "SELECT avg_period_id FROM avg_period "
				+ "WHERE default_avg = 0";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			avgPeriodList.add(DbUtils.rsGetInt(rs,
					rs.findColumn("avg_period_id")));
		}
		rs.close();
		stmt.close();

		return avgPeriodList;
	}// end readNotDefaultAvgPeriod

	public Integer getDefaultAvgPeriod() throws SQLException {
		String classMethod = "[getDefaultAvgPeriod] ";
		Integer defaultAvgPeriod = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT avg_period_id FROM avg_period "
				+ "WHERE default_avg = 1";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			defaultAvgPeriod = DbUtils.rsGetInt(rs,
					rs.findColumn("avg_period_id"));
		}
		rs.close();
		stmt.close();

		return defaultAvgPeriod;
	}// end getDefaultAvgPeriod

	public List<KeyValueObject> readAlarmName() throws SQLException {
		String classMethod = "[readAlarmName] ";
		List<KeyValueObject> alarmName = new ArrayList<KeyValueObject>();
		Statement stmt = conn.createStatement();
		String query = "SELECT alarm_id, alarm_name FROM alarm_name ORDER BY "
				+ "alarm_name";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			KeyValueObject keyValue = new KeyValueObject();
			keyValue.setKey(rs.getString(rs.findColumn("alarm_id")));
			keyValue.setValue(rs.getString(rs.findColumn("alarm_name")));
			alarmName.add(keyValue);
		}// end while
		rs.close();
		stmt.close();

		return alarmName;
	}// end readAlarmName

	public List<KeyValueObject> readParameter() throws SQLException {
		String classMethod = "[readParameter] ";
		List<KeyValueObject> parameter = new ArrayList<KeyValueObject>();
		Statement stmt = conn.createStatement();
		String query = "SELECT param_id, name FROM parameter ORDER BY name";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			KeyValueObject keyValue = new KeyValueObject();
			keyValue.setKey(rs.getString(rs.findColumn("param_id")));
			keyValue.setValue(rs.getString(rs.findColumn("name")));
			parameter.add(keyValue);
		}// end while
		rs.close();
		stmt.close();

		return parameter;
	}// end readParameter

	public ParameterInfo readParameterInfo(String param_id) throws SQLException {
		String classMethod = "[readParameterInfo] ";
		ParameterInfo parameter = new ParameterInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM parameter WHERE param_id = "
				+ DbUtils.qVal(param_id);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			parameter.setParam_id(rs.getString(rs.findColumn("param_id")));
			parameter.setName(rs.getString(rs.findColumn("name")));
			parameter.setPhysicalDimension(rs.getString(rs
					.findColumn("physical_dimension")));
			parameter.setMolecularWeight(DbUtils.rsGetDouble(rs,
					rs.findColumn("molecular_weight")));
			parameter.setType(rs.getString(rs.findColumn("type")));
		}// end while
		rs.close();
		stmt.close();

		return parameter;
	}// end readParameterInfo

	public List<Parameter> readAllParameter() throws SQLException,
			ConfigException {
		String classMethod = "[readAllParameter] ";
		List<Parameter> parameterList = new ArrayList<Parameter>();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM parameter ORDER BY param_id";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			Parameter parameter = new Parameter();
			parameter.setId(rs.getString(rs.findColumn("param_id")));
			parameter.setName(rs.getString(rs.findColumn("name")));
			parameter.setPhysicalDimension(rs.getString(rs
					.findColumn("physical_dimension")));
			parameter.setMolecularWeight(DbUtils.rsGetDouble(rs,
					rs.findColumn("molecular_weight")));
			parameter.setTypeAsString(rs.getString(rs.findColumn("type")));
			parameterList.add(parameter);
		}// end while
		rs.close();
		stmt.close();

		return parameterList;
	}// end readAllParameter

	public MeasureUnitInfo readMeasureUnitInfo(String measureName)
			throws SQLException {
		String classMethod = "[readMeasureUnitInfo] ";
		MeasureUnitInfo measure = new MeasureUnitInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM measure_unit WHERE measure_name = "
				+ DbUtils.qVal(measureName);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			measure.setMeasureName(rs.getString(rs.findColumn("measure_name")));
			measure.setDescription(rs.getString(rs.findColumn("description")));
			measure.setPhysicalDimension(rs.getString(rs
					.findColumn("physical_dimension")));
			measure.setAllowedForAcquisition(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("allowed_for_acquisition")));
			measure.setAllowedForAnalyzer(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("allowed_for_analyzer")));
			measure.setConversionAddendum(DbUtils.rsGetDouble(rs,
					rs.findColumn("conversion_addendum")));
			measure.setConversionFormula(rs.getString(rs
					.findColumn("conversion_formula")));
			measure.setConversionMultiplyer(DbUtils.rsGetDouble(rs,
					rs.findColumn("conversion_multiplyer")));
		}// end while
		rs.close();
		stmt.close();

		return measure;
	}// end readMeasureUnitInfo

	public List<MeasureUnit> readAllMeasureUnit() throws SQLException,
			ConfigException {
		String classMethod = "[readAllMeasureUnit] ";
		List<MeasureUnit> measureUnitList = new ArrayList<MeasureUnit>();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM measure_unit ORDER BY physical_dimension,"
				+ " measure_name";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			MeasureUnit measure = new MeasureUnit();
			measure.setName(rs.getString(rs.findColumn("measure_name")));
			measure.setDescription(rs.getString(rs.findColumn("description")));
			measure.setPhysicalDimension(rs.getString(rs
					.findColumn("physical_dimension")));
			Boolean allowedForAcquisition = DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("allowed_for_acquisition"));
			if (allowedForAcquisition != null)
				measure.setAllowedForAcquisition(allowedForAcquisition);
			Boolean allowedForAnalyzer = DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("allowed_for_analyzer"));
			if (allowedForAnalyzer != null)
				measure.setAllowedForAnalyzer(allowedForAnalyzer);
			Double conversionAddendum = DbUtils.rsGetDouble(rs,
					rs.findColumn("conversion_addendum"));
			if (conversionAddendum != null)
				measure.setConversionAddendum(conversionAddendum);
			measure.setConversionFormula(rs.getString(rs
					.findColumn("conversion_formula")));
			Double conversionMultiplyer = DbUtils.rsGetDouble(rs,
					rs.findColumn("conversion_multiplyer"));
			if (conversionMultiplyer != null)
				measure.setConversionMultiplyer(conversionMultiplyer);
			measureUnitList.add(measure);
		}// end while
		rs.close();
		stmt.close();

		return measureUnitList;
	}// end readAllMeasureUnit

	public AlarmNameInfo readAlarmName(String alarmId) throws SQLException {
		String classMethod = "[readAlarmName] ";
		AlarmNameInfo alarm = new AlarmNameInfo();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM alarm_name WHERE alarm_id = "
				+ DbUtils.qVal(alarmId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			alarm.setAlarmId(rs.getString(rs.findColumn("alarm_id")));
			alarm.setAlarmName(rs.getString(rs.findColumn("alarm_name")));
			alarm.setDataQualityRelevant(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("data_quality_relevant")));
			alarm.setType(rs.getString(rs.findColumn("type")));
		}// end while
		rs.close();
		stmt.close();

		return alarm;
	}// end readAlarmName

	public List<AlarmName> readAllAlarmNames() throws SQLException,
			ConfigException {
		String classMethod = "[readAllAlarmNames] ";
		List<AlarmName> alarmNameList = new ArrayList<AlarmName>();

		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM alarm_name";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			AlarmName alarm = new AlarmName();
			alarm.setId(rs.getString(rs.findColumn("alarm_id")));
			alarm.setName(rs.getString(rs.findColumn("alarm_name")));
			alarm.setDataQualityRelevant(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("data_quality_relevant")));
			alarm.setTypeAsString(rs.getString(rs.findColumn("type")));
			alarmNameList.add(alarm);
		}// end while
		rs.close();
		stmt.close();

		return alarmNameList;
	}// end readAllAlarmNames

	public List<KeyValueObject> readMeasureUnit() throws SQLException {
		String classMethod = "[readMeasureUnit] ";
		List<KeyValueObject> measureUnit = new ArrayList<KeyValueObject>();
		Statement stmt = conn.createStatement();
		String query = "SELECT measure_name, description FROM measure_unit "
				+ "ORDER BY description";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			KeyValueObject keyValue = new KeyValueObject();
			keyValue.setKey(rs.getString(rs.findColumn("measure_name")));
			keyValue.setValue(rs.getString(rs.findColumn("description")));
			measureUnit.add(keyValue);
		}// end while
		rs.close();
		stmt.close();

		return measureUnit;
	}// end readMeasureUnit

	public Date readStationMinTimestamp(int stationId) throws SQLException {
		String classMethod = "[readStationMinTimestamp] ";
		Date retDate = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT min_timestamp_for_polling FROM station "
				+ "WHERE station_id=" + DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			retDate = DbUtils.rsGetDate(rs, 1);
		}
		rs.close();
		stmt.close();
		return retDate;
	} // end readStationMinTimestamp

	public Boolean isNewAnalyzer(int analyzerId) throws SQLException {
		String classMethod = "[isNewAnalyzer] ";
		Boolean isNewAnalyzer = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT analyzer_id FROM analyzer "
				+ "WHERE analyzer_id=" + DbUtils.qVal(analyzerId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next())
			isNewAnalyzer = new Boolean(false);
		else
			isNewAnalyzer = new Boolean(true);
		rs.close();
		stmt.close();
		return isNewAnalyzer;
	} // end isNewAnalyzer

	public Boolean isNewElement(int elementId) throws SQLException {
		String classMethod = "[isNewElement] ";
		Boolean isNewElement = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT element_id FROM element " + "WHERE element_id="
				+ DbUtils.qVal(elementId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next())
			isNewElement = new Boolean(false);
		else
			isNewElement = new Boolean(true);
		rs.close();
		stmt.close();
		return isNewElement;
	} // end isNewElement

	public Boolean isNewAvgPeriod(int elementId, int avgPeriodId)
			throws SQLException {
		String classMethod = "[isNewAvgPeriod] ";
		Boolean isNewAvgPeriod = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT element_id FROM element_avg_period"
				+ " WHERE element_id=" + DbUtils.qVal(elementId)
				+ " AND avg_period_id=" + DbUtils.qVal(avgPeriodId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next())
			isNewAvgPeriod = new Boolean(false);
		else
			isNewAvgPeriod = new Boolean(true);
		rs.close();
		stmt.close();
		return isNewAvgPeriod;
	} // end isNewAvgPeriod

	public Boolean isNewContainerAlarm(int stationAlarmId) throws SQLException {
		String classMethod = "[isNewContainerAlarm] ";
		Boolean isNewContainerAlarm = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT station_alarm_id FROM station_alarm "
				+ "WHERE station_alarm_id=" + DbUtils.qVal(stationAlarmId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next())
			isNewContainerAlarm = new Boolean(false);
		else
			isNewContainerAlarm = new Boolean(true);
		rs.close();
		stmt.close();
		return isNewContainerAlarm;
	} // end isNewContainerAlarm

	public DataObject readMeansScalarData(int elementId, Integer avgPeriodId,
			Date timestamp) throws SQLException {
		String classMethod = "[readMeansScalarData] ";
		DataObject dataObj = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT mean_data_value,flag,multiple_flag FROM mean_data "
				+ "WHERE element_id="
				+ DbUtils.qVal(elementId)
				+ " AND avg_period_id="
				+ DbUtils.qVal(avgPeriodId)
				+ " AND timestamp_mean_data=" + DbUtils.qVal(timestamp);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			dataObj = new DataObject();
			dataObj.setValue(DbUtils.rsGetDouble(rs,
					rs.findColumn("mean_data_value")));
			dataObj.setDate(timestamp);
			dataObj.setFlag(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			dataObj.setMultipleFlag(rs.getInt("multiple_flag"));
			dataObj.setElementId(elementId);
			dataObj.setAvgPeriodId(avgPeriodId);
		}
		rs.close();
		stmt.close();
		return dataObj;
	} // end readMeansScalarData

	public DataObject readMeansWindData(int elementId, Integer avgPeriodId,
			Date timestamp) throws SQLException {
		String classMethod = "[readMeansWindData] ";
		DataObject dataObj = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT flag,multiple_flag,vectorial_speed,"
				+ "vectorial_direction,standard_deviation,scalar_speed,"
				+ "gust_speed,gust_direction,calms_number_percent,calm"
				+ " FROM mean_wind_data" + " WHERE element_id="
				+ DbUtils.qVal(elementId) + " AND avg_period_id="
				+ DbUtils.qVal(avgPeriodId) + " AND timestamp_mean_wind_data="
				+ DbUtils.qVal(timestamp);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		if (rs.next()) {
			dataObj = new WindDataObject();
			dataObj.setDate(timestamp);
			dataObj.setFlag(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			dataObj.setMultipleFlag(rs.getInt("multiple_flag"));
			((WindDataObject) (dataObj)).setVectorialSpeed(DbUtils.rsGetDouble(
					rs, rs.findColumn("vectorial_speed")));
			((WindDataObject) (dataObj)).setVectorialDirection(DbUtils
					.rsGetDouble(rs, rs.findColumn("vectorial_direction")));
			((WindDataObject) (dataObj)).setStandardDeviation(DbUtils
					.rsGetDouble(rs, rs.findColumn("standard_deviation")));
			((WindDataObject) (dataObj)).setScalarSpeed(DbUtils.rsGetDouble(rs,
					rs.findColumn("scalar_speed")));
			((WindDataObject) (dataObj)).setGustSpeed(DbUtils.rsGetDouble(rs,
					rs.findColumn("gust_speed")));
			((WindDataObject) (dataObj)).setGustDirection(DbUtils.rsGetDouble(
					rs, rs.findColumn("gust_direction")));
			((WindDataObject) (dataObj)).setCalmsNumberPercent(DbUtils
					.rsGetDouble(rs, rs.findColumn("calms_number_percent")));
			((WindDataObject) (dataObj)).setCalm(DbUtils.rsGetBooleanObjFromInt(
					rs, rs.findColumn("calm")));
			dataObj.setElementId(elementId);
			dataObj.setAvgPeriodId(avgPeriodId);
		}
		rs.close();
		stmt.close();
		return dataObj;
	} // end readMeansWindData

	public List<RainScalarData> readMeansScalarDataOnUpdateDate(int elementId,
			Integer avgPeriodId, Date currentTimestamp,
			Date lastDateOnMatchedDb, int toleranceInMillis,
			Date lastDatumDateInDestDB)
			throws SQLException {
		String classMethod = "[readMeansScalarDataOnUpdateDate] ";
		List<RainScalarData> dataList = new ArrayList<RainScalarData>();
		Calendar cal = new GregorianCalendar();
		cal.setTime(currentTimestamp);
		cal.add(Calendar.MILLISECOND, toleranceInMillis);
		StringBuffer strBuf = new StringBuffer();
		Statement stmt = conn.createStatement();
		strBuf.append("SELECT timestamp_mean_data,mean_data_value,flag,");
		strBuf.append("multiple_flag,update_date FROM mean_data");
		strBuf.append(" WHERE element_id=");
		strBuf.append(DbUtils.qVal(elementId));
		strBuf.append(" AND avg_period_id=");
		strBuf.append(DbUtils.qVal(avgPeriodId));
		if (lastDateOnMatchedDb != null) {
			strBuf.append(" AND update_date>=");
			strBuf.append(DbUtils.qValWithMilliseconds(lastDateOnMatchedDb));
			strBuf.append(" AND update_date<");
			strBuf.append(DbUtils.qValWithMilliseconds(cal.getTime()));
		}
		if (lastDatumDateInDestDB != null) {
			strBuf.append(" AND timestamp_mean_data>");
			strBuf.append(DbUtils.qValWithMilliseconds(lastDatumDateInDestDB));
		}
		strBuf.append(" ORDER BY timestamp_mean_data");
		String query = strBuf.toString();
		ResultSet rs = null;
		logger.debug(classMethod + query);
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			RainScalarData scalarData = new RainScalarData();
			scalarData.setTimestamp(rs.getTimestamp("timestamp_mean_data"));
			scalarData.setValue(DbUtils.rsGetDouble(rs,
					rs.findColumn("mean_data_value")));
			scalarData.setNotValid(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			scalarData.setInstrumentFlags(rs.getInt("multiple_flag"));
			scalarData.setUpdateDate(rs.getTimestamp("update_date"));
			dataList.add(scalarData);
		}
		rs.close();
		stmt.close();
		return dataList;
	} // end readMeansScalarDataOnUpdateDate

	public List<RainScalarData> readMeansWindDataOnUpdateDate(int elementId,
			Integer avgPeriodId, Date currentTimestamp,
			Date lastDateOnMatchedDb, int toleranceInMillis,
			Date lastDatumDateInDestDB, String component) throws SQLException {
		String classMethod = "[readMeansWindDataOnUpdateDate] ";
		List<RainScalarData> dataList = new ArrayList<RainScalarData>();
		Calendar cal = new GregorianCalendar();
		cal.setTime(currentTimestamp);
		cal.add(Calendar.MILLISECOND, toleranceInMillis);
		StringBuffer strBuf = new StringBuffer();
		Statement stmt = conn.createStatement();
		strBuf.append("SELECT timestamp_mean_wind_data,flag,");
		strBuf.append(component);
		strBuf.append(",multiple_flag,update_date FROM mean_wind_data");
		strBuf.append(" WHERE element_id=");
		strBuf.append(DbUtils.qVal(elementId));
		strBuf.append(" AND avg_period_id=");
		strBuf.append(DbUtils.qVal(avgPeriodId));
		if (lastDateOnMatchedDb != null) {
			strBuf.append(" AND update_date>=");
			strBuf.append(DbUtils.qValWithMilliseconds(lastDateOnMatchedDb));
			strBuf.append(" AND update_date<");
			strBuf.append(DbUtils.qValWithMilliseconds(cal.getTime()));
		}
		if (lastDatumDateInDestDB != null) {
			strBuf.append(" AND timestamp_mean_wind_data>");
			strBuf.append(DbUtils.qValWithMilliseconds(lastDatumDateInDestDB));
		}
		strBuf.append(" ORDER BY timestamp_mean_wind_data");
		String query = strBuf.toString();
		ResultSet rs = null;
		logger.debug(classMethod + query);
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			RainScalarData scalarData = new RainScalarData();
			scalarData
					.setTimestamp(rs.getTimestamp("timestamp_mean_wind_data"));
			scalarData.setValue(DbUtils.rsGetDouble(rs,
					rs.findColumn(component)));
			scalarData.setNotValid(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			scalarData.setInstrumentFlags(rs.getInt("multiple_flag"));
			scalarData.setUpdateDate(rs.getTimestamp("update_date"));
			dataList.add(scalarData);
		}
		rs.close();
		stmt.close();
		return dataList;
	} // end readMeansWindDataOnUpdateDate

	public List<GpsData> readGpsDataOnUpdateDate(int stationId,
			Date currentTimestamp, Date lastStationDateOnMatchedDb,
			int toleranceInMillis) throws SQLException {
		String classMethod = "[readGpsDataOnUpdateDate] ";
		List<GpsData> dataList = new ArrayList<GpsData>();
		Calendar cal = new GregorianCalendar();
		cal.setTime(currentTimestamp);
		cal.add(Calendar.MILLISECOND, toleranceInMillis);
		StringBuffer strBuf = new StringBuffer();
		Statement stmt = conn.createStatement();
		strBuf.append("SELECT timestamp_gps_data,latitude,longitude,altitude");
		strBuf.append(" FROM gps_data");
		strBuf.append(" WHERE station_id=");
		strBuf.append(DbUtils.qVal(stationId));
		if (lastStationDateOnMatchedDb != null) {
			strBuf.append(" AND update_date>=");
			strBuf.append(DbUtils
					.qValWithMilliseconds(lastStationDateOnMatchedDb));
			strBuf.append(" AND update_date<");
			strBuf.append(DbUtils.qValWithMilliseconds(cal.getTime()));
		}
		strBuf.append(" ORDER BY timestamp_gps_data");
		String query = strBuf.toString();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			GpsData gpsData = new GpsData();
			gpsData.setTimestamp(rs.getTimestamp("timestamp_gps_data"));
			gpsData.setLatitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("latitude")));
			gpsData.setLongitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("longitude")));
			gpsData.setAltitude(DbUtils.rsGetDouble(rs,
					rs.findColumn("altitude")));
			dataList.add(gpsData);
		}
		rs.close();
		stmt.close();
		return dataList;
	} // end readGpsDataOnUpdateDate

	public Boolean readMeansScalarData(int elementId, int avgPeriodId,
			Date startDate, Date endDate, List<DataObject> dataList,
			Integer numMaxData) throws SQLException {
		String classMethod = "[readMeansScalarData] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT timestamp_mean_data,mean_data_value,"
				+ "flag,multiple_flag FROM mean_data " + "WHERE element_id="
				+ DbUtils.qVal(elementId) + " AND avg_period_id="
				+ DbUtils.qVal(avgPeriodId) + " AND timestamp_mean_data>="
				+ DbUtils.qVal(startDate) + " AND timestamp_mean_data<="
				+ DbUtils.qVal(endDate) + " ORDER BY timestamp_mean_data";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int rowCount = 0;
		int maxRowCount = Integer.MAX_VALUE;
		if (numMaxData != null)
			maxRowCount = numMaxData.intValue();
		while (rs.next() && rowCount < maxRowCount) {
			DataObject dataObj = new DataObject();
			dataObj.setDate(rs.getTimestamp("timestamp_mean_data"));
			dataObj.setDateString(sdf.format(dataObj.getDate()));
			dataObj.setValue(DbUtils.rsGetDouble(rs,
					rs.findColumn("mean_data_value")));
			dataObj.setFlag(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			dataObj.setMultipleFlag(rs.getInt("multiple_flag"));
			dataObj.setElementId(elementId);
			dataObj.setAvgPeriodId(avgPeriodId);
			dataList.add(dataObj);
			rowCount++;
		}
		Boolean maxDataReached = null;
		if (numMaxData != null) {
			if (rowCount == numMaxData.intValue())
				maxDataReached = new Boolean(true);
			else
				maxDataReached = new Boolean(false);
		}
		rs.close();
		stmt.close();
		return maxDataReached;
	} // end readMeansScalarData

	public Boolean readMeansWindData(int elementId, int avgPeriodId,
			Date startDate, Date endDate, List<DataObject> dataList,
			Integer numMaxData) throws SQLException {
		String classMethod = "[readMeansWindData] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT timestamp_mean_wind_data,flag,multiple_flag,"
				+ "vectorial_speed,vectorial_direction,standard_deviation,"
				+ "scalar_speed,gust_speed,gust_direction,calms_number_percent,"
				+ "calm FROM mean_wind_data" + " WHERE element_id="
				+ DbUtils.qVal(elementId) + " AND avg_period_id="
				+ DbUtils.qVal(avgPeriodId) + " AND timestamp_mean_wind_data>"
				+ DbUtils.qVal(startDate) + " AND timestamp_mean_wind_data<="
				+ DbUtils.qVal(endDate) + " ORDER BY timestamp_mean_wind_data";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int rowCount = 0;
		int maxRowCount = Integer.MAX_VALUE;
		if (numMaxData != null)
			maxRowCount = numMaxData.intValue();
		while (rs.next() && rowCount < maxRowCount) {
			DataObject dataObj = new WindDataObject();
			dataObj.setDate(rs.getTimestamp("timestamp_mean_wind_data"));
			dataObj.setDateString(sdf.format(dataObj.getDate()));
			dataObj.setFlag(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			dataObj.setMultipleFlag(rs.getInt("multiple_flag"));
			((WindDataObject) (dataObj)).setVectorialSpeed(DbUtils.rsGetDouble(
					rs, rs.findColumn("vectorial_speed")));
			((WindDataObject) (dataObj)).setVectorialDirection(DbUtils
					.rsGetDouble(rs, rs.findColumn("vectorial_direction")));
			((WindDataObject) (dataObj)).setStandardDeviation(DbUtils
					.rsGetDouble(rs, rs.findColumn("standard_deviation")));
			((WindDataObject) (dataObj)).setScalarSpeed(DbUtils.rsGetDouble(rs,
					rs.findColumn("scalar_speed")));
			((WindDataObject) (dataObj)).setGustSpeed(DbUtils.rsGetDouble(rs,
					rs.findColumn("gust_speed")));
			((WindDataObject) (dataObj)).setGustDirection(DbUtils.rsGetDouble(
					rs, rs.findColumn("gust_direction")));
			((WindDataObject) (dataObj)).setCalmsNumberPercent(DbUtils
					.rsGetDouble(rs, rs.findColumn("calms_number_percent")));
			((WindDataObject) (dataObj)).setCalm(DbUtils.rsGetBooleanObjFromInt(
					rs, rs.findColumn("calm")));
			dataObj.setElementId(elementId);
			dataObj.setAvgPeriodId(avgPeriodId);
			dataList.add(dataObj);
			rowCount++;
		}
		Boolean maxDataReached = null;
		if (numMaxData != null) {
			if (rowCount == numMaxData.intValue())
				maxDataReached = new Boolean(true);
			else
				maxDataReached = new Boolean(false);
		}
		rs.close();
		stmt.close();
		return maxDataReached;
	} // end readMeansWindData

	public int countSampleData(int elementId, Date startDate, Date endDate)
			throws SQLException {
		String classMethod = "[countSampleData] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT count(*) AS count FROM sample_data "
				+ "WHERE element_id=" + DbUtils.qVal(elementId)
				+ " AND timestamp_sample_data>=" + DbUtils.qVal(startDate)
				+ " AND timestamp_sample_data<=" + DbUtils.qVal(endDate);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int count = 0;
		while (rs.next()) {
			count = rs.getInt(rs.findColumn("count"));
		}
		logger.debug("count: " + count);
		return count;
	}// countSampleData

	public Boolean readSampleData(int elementId, Date startDate, Date endDate,
			List<DataObject> dataList, Integer numMaxData) throws SQLException {
		String classMethod = "[readSampleData] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT timestamp_sample_data,sample_data_value,"
				+ "flag,multiple_flag FROM sample_data " + "WHERE element_id="
				+ DbUtils.qVal(elementId) + " AND timestamp_sample_data>="
				+ DbUtils.qVal(startDate) + " AND timestamp_sample_data<="
				+ DbUtils.qVal(endDate) + " ORDER BY timestamp_sample_data";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		int rowCount = 0;
		int maxRowCount = Integer.MAX_VALUE;
		if (numMaxData != null)
			maxRowCount = numMaxData.intValue();
		while (rs.next() && rowCount < maxRowCount) {
			DataObject dataObj = new DataObject();
			dataObj.setDate(rs.getTimestamp("timestamp_sample_data"));
			dataObj.setDateString(sdfSec.format(dataObj.getDate()));
			dataObj.setValue(DbUtils.rsGetDouble(rs,
					rs.findColumn("sample_data_value")));
			dataObj.setFlag(DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag")));
			dataObj.setMultipleFlag(rs.getInt("multiple_flag"));
			dataObj.setElementId(elementId);
			dataObj.setAvgPeriodId(null);
			dataList.add(dataObj);
			rowCount++;
		}
		logger.debug("datalist: " + dataList + " datalist size: "
				+ dataList.size());
		Boolean maxDataReached = null;
		if (numMaxData != null) {
			if (rowCount == numMaxData.intValue())
				maxDataReached = new Boolean(true);
			else
				maxDataReached = new Boolean(false);
		}
		rs.close();
		stmt.close();
		return maxDataReached;
	} // end readSampleData

	public List<GenericData> readScalarLastCalibrationMeans(int elementId,
			int avgPeriodId, Date timestamp) throws SQLException {
		String classMethod = "[readScalarLastCalibrationMeans] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT mean_data_value,timestamp_mean_data,flag,"
				+ "multiple_flag,update_date FROM mean_data "
				+ "WHERE element_id=" + DbUtils.qVal(elementId)
				+ " AND avg_period_id=" + DbUtils.qVal(avgPeriodId)
				+ " AND timestamp_mean_data>" + DbUtils.qVal(timestamp)
				+ " AND (CAST(multiple_flag as bit(32)) & "
				+ ValidationFlag.ANALYZER_MANUAL_CALIB + "::bit(32) = "
				+ ValidationFlag.ANALYZER_MANUAL_CALIB + "::bit(32))"
				+ " ORDER BY timestamp_mean_data";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		List<GenericData> dataList = new ArrayList<GenericData>();
		while (rs.next()) {
			Double value = DbUtils.rsGetDouble(rs,
					rs.findColumn("mean_data_value"));
			Date date = rs.getTimestamp("timestamp_mean_data");
			boolean flag = DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag"));
			Integer multipleFlag = DbUtils.rsGetInt(rs,
					rs.findColumn("multiple_flag"));
			Date updateDate = rs.getTimestamp("update_date");
			GenericData dataObj = new RainScalarData(date, value, flag,
					multipleFlag, updateDate);
			dataList.add(dataObj);
		}
		rs.close();
		stmt.close();
		return dataList;
	} // end readScalarLastCalibrationMeans

	public List<GenericData> readWindLastCalibrationMeans(int elementId,
			int avgPeriodId, Date timestamp) throws SQLException {
		String classMethod = "[readWindLastCalibrationMeans] ";
		Statement stmt = conn.createStatement();
		String query = "SELECT timestamp_mean_wind_data,vectorial_speed,"
				+ "vectorial_direction,standard_deviation,scalar_speed,"
				+ "gust_speed,gust_direction,calms_number_percent,calm,"
				+ "flag,multiple_flag,update_date" + " FROM mean_wind_data "
				+ "WHERE element_id=" + DbUtils.qVal(elementId)
				+ " AND avg_period_id=" + DbUtils.qVal(avgPeriodId)
				+ " AND timestamp_mean_wind_data>" + DbUtils.qVal(timestamp)
				+ " AND (CAST(multiple_flag as bit(32)) & "
				+ ValidationFlag.ANALYZER_MANUAL_CALIB + "::bit(32) = "
				+ ValidationFlag.ANALYZER_MANUAL_CALIB + "::bit(32))"
				+ " ORDER BY timestamp_mean_wind_data";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		List<GenericData> dataList = new ArrayList<GenericData>();
		while (rs.next()) {
			Date date = rs.getTimestamp("timestamp_mean_wind_data");
			Double vectorialSpeed = DbUtils.rsGetDouble(rs,
					rs.findColumn("vectorial_speed"));
			Double vectorialDir = DbUtils.rsGetDouble(rs,
					rs.findColumn("vectorial_direction"));
			Double standardDeviation = DbUtils.rsGetDouble(rs,
					rs.findColumn("standard_deviation"));
			Double scalarSpeed = DbUtils.rsGetDouble(rs,
					rs.findColumn("scalar_speed"));
			Double gustSpeed = DbUtils.rsGetDouble(rs,
					rs.findColumn("gust_speed"));
			Double gustDirection = DbUtils.rsGetDouble(rs,
					rs.findColumn("gust_direction"));
			Double calmsNumberPercent = DbUtils.rsGetDouble(rs,
					rs.findColumn("calms_number_percent"));
			Boolean calm = DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("calm"));
			boolean flag = DbUtils.rsGetBooleanFromInt(rs,
					rs.findColumn("flag"));
			Integer multipleFlag = DbUtils.rsGetInt(rs,
					rs.findColumn("multiple_flag"));
			Date updateDate = rs.getTimestamp("update_date");
			GenericData dataObj = new WindData(date, flag, multipleFlag,
					vectorialSpeed, vectorialDir, standardDeviation,
					scalarSpeed, gustSpeed, gustDirection, calmsNumberPercent,
					calm, updateDate);
			dataList.add(dataObj);
		}
		rs.close();
		stmt.close();
		return dataList;
	} // end readWindLastCalibrationMeans

	
	public int readAverageTemperature(int configId) throws SQLException {
		String classMethod = "[readAvgTempId] ";
		Integer averageTemperature = 20;
		Statement stmt = conn.createStatement();
		String query = "SELECT avg_temp FROM config WHERE config_id ="
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			averageTemperature = rs.getInt(rs.findColumn("avg_temp"));
		}
		rs.close();
		stmt.close();

		return averageTemperature;
	}

	public String readTemperatureId(int configId) throws SQLException {
		String classMethod = "[readTemperatureId] ";
		String temperatureId = "";
		Statement stmt = conn.createStatement();
		String query = "SELECT temp_id FROM config WHERE config_id ="
				+ DbUtils.qVal(configId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			temperatureId = rs.getString(rs.findColumn("temp_id"));
		}
		rs.close();
		stmt.close();

		return temperatureId;
	}

	public Integer getCommonConfigLastUpdateStatus(int stationId)
			throws SQLException {
		String classMethod = "[getCommonConfigLAstUpdateStatus] ";
		Integer lastUpdateStatus = null;
		Statement stmt = conn.createStatement();
		String query = "SELECT last_common_config_update_status FROM station WHERE station_id ="
				+ DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);

			throw e;
		}
		while (rs.next()) {
			lastUpdateStatus = DbUtils.rsGetInt(rs,
					rs.findColumn("last_common_config_update_status"));
		}
		rs.close();
		stmt.close();

		return lastUpdateStatus;
	}

	public String getCommonConfigProblem(int stationId) throws SQLException {
		String classMethod = "[getCommonConfigProblem] ";
		String problem = "";
		Statement stmt = conn.createStatement();
		String query = "SELECT common_config_problem FROM station WHERE station_id ="
				+ DbUtils.qVal(stationId);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(classMethod + query, e);
			throw e;
		}
		while (rs.next()) {
			problem = rs.getString(rs.findColumn("common_config_problem"));
		}
		rs.close();
		stmt.close();

		return problem;
	}

}// end class
