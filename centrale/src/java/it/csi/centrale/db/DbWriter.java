/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: Class for writing to Internal Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbWriter.java,v 1.121 2015/10/19 13:22:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import it.csi.centrale.data.station.AlarmData;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.AvgPeriod;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.ContainerAlarm;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.GpsData;
import it.csi.centrale.data.station.ModemConf;
import it.csi.centrale.data.station.RainScalarData;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.data.station.StationStatus;
import it.csi.centrale.data.station.WindData;
import it.csi.centrale.polling.data.DiskStatus;
import it.csi.centrale.polling.data.PerifericoAppStatus;
import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.centrale.ui.client.data.MeasureUnitInfo;
import it.csi.centrale.ui.client.data.ModemInfo;
import it.csi.centrale.ui.client.data.OtherInfo;
import it.csi.centrale.ui.client.data.ParameterInfo;
import it.csi.centrale.ui.client.data.StandardInfo;
import it.csi.centrale.ui.client.data.StorageManagerInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Class for writing to Internal Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class DbWriter {

	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale."
			+ DbWriter.class.getSimpleName());

	private Connection conn;

	public DbWriter() {
	}

	public DbWriter(Connection conn) {
		this.conn = conn;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		conn.setAutoCommit(autoCommit);
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public void rollback() throws SQLException {
		conn.rollback();
	}

	public RejectedObject updateConfigInfo(ConfigInfo config) {
		String methodName = "[updateConfigInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" max_num_phone_lines = "
					+ DbUtils.qVal(config.getMaxNumLines()));
			cmd.append(", reserved_line = "
					+ DbUtils.booleanToIntStr(config.isReservedLine()));
			cmd.append(", synthetic_icon = "
					+ DbUtils.booleanToIntStr(config.isSyntheticIcon()));
			cmd.append(", polling_office_time = "
					+ DbUtils.qVal(config.getPollConfig()
							.getPollingOfficeTime()));
			cmd.append(", polling_extra_office = "
					+ DbUtils.qVal(config.getPollConfig()
							.getPollingExtraOffice()));
			cmd.append(", use_polling_extra = "
					+ DbUtils.booleanToIntStr(config.getPollConfig()
							.isUsePollingExtra()));
			cmd.append(", close_sat = "
					+ DbUtils.booleanToIntStr(config.getPollConfig()
							.isCloseSat()));
			cmd.append(", close_sun = "
					+ DbUtils.booleanToIntStr(config.getPollConfig()
							.isCloseSun()));
			cmd.append(", open_at = "
					+ DbUtils.qVal(config.getPollConfig().getOpenAt()));
			cmd.append(", close_at = "
					+ DbUtils.qVal(config.getPollConfig().getCloseAt()));
			cmd.append(", total_num_modem = "
					+ DbUtils.qVal(config.getTotalNumModem()));
			cmd.append(", num_modem_shared_lines = "
					+ DbUtils.qVal(config.getNumModemSharedLines()));
			cmd.append(", download_type_sample_data = "
					+ DbUtils.qVal(config.getSampleDataTypeToDownload()));
			cmd.append(", download_alarm_data = "
					+ DbUtils.booleanToIntStr(config.isDownloadAlarmHistory()));
			cmd.append(", min_temperature_threshold = "
					+ DbUtils.qVal(config.getMinThreshold()));
			cmd.append(", max_temperature_threshold = "
					+ DbUtils.qVal(config.getMaxThreshold()));
			cmd.append(", alarm_max_temperature_threshold = "
					+ DbUtils.qVal(config.getAlarmMaxThreshold()));
			cmd.append(", cop_ip = " + DbUtils.qVal(config.getCopIp()));
			cmd.append(", router_timeout = "
					+ DbUtils.qVal(config.getRouterTimeout()));
			cmd.append(", router_try_timeout = "
					+ DbUtils.qVal(config.getRouterTryTimeout()));
			cmd.append(", cop_router_ip = "
					+ DbUtils.qVal(config.getCopRouter()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(", name = " + DbUtils.qVal(config.getName()));
			cmd.append(", generic_map_name = "
					+ DbUtils.qVal(config.getGenericMapName()));
			cmd.append(", time_host_proxy = "
					+ DbUtils.qVal(config.getTimeHostProxy()));
			cmd.append(", time_host_lan = "
					+ DbUtils.qVal(config.getTimeHostLan()));
			cmd.append(", time_host_router = "
					+ DbUtils.qVal(config.getTimeHostRouter()));
			cmd.append(", time_host_modem = "
					+ DbUtils.qVal(config.getTimeHostModem()));
			cmd.append(",  num_reserved_lines_ui = "
					+ DbUtils.qVal(config.getNumReservedLinesUi()));
			cmd.append(",  proxy_host = " + DbUtils.qVal(config.getProxyHost()));
			cmd.append(",  proxy_port = " + DbUtils.qVal(config.getProxyPort()));
			cmd.append(",  proxy_exclusion = "
					+ DbUtils.qVal(config.getProxyExlusion()));
			cmd.append(" WHERE config_id = "
					+ DbUtils.qVal(config.getConfigId()));
			update = "UPDATE config SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(config.getConfigId(), update, e);
		}
		return (errObj);
	}// end updateConfigInfo

	public RejectedObject updateConnectionAnagraphicStationInfo(Station station) {
		String methodName = "[updateConnectionAnagraphicStationInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" uuid = " + DbUtils.qVal(station.getStationUUID()));
			cmd.append(", shortname = "
					+ DbUtils.qVal(station.getStInfo().getShortStationName()));
			cmd.append(", name = "
					+ DbUtils.qVal(station.getStInfo().getLongStationName()));
			cmd.append(", province = "
					+ DbUtils.qVal(station.getStInfo().getProvince()));
			cmd.append(", city = "
					+ DbUtils.qVal(station.getStInfo().getCity()));
			cmd.append(", address = "
					+ DbUtils.qVal(station.getStInfo().getAddress()));
			cmd.append(", location = "
					+ DbUtils.qVal(station.getStInfo().getLocation()));
			cmd.append(", notes = "
					+ DbUtils.qVal(station.getStInfo().getNotes()));
			cmd.append(", ip_address = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice().getIp()));
			cmd.append(", ip_port = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPortNumber()));
			cmd.append(", tel_number = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPhoneNumber()));
			cmd.append(", router_ip_address = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getRouterIpAddress()));
			cmd.append(", lan = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().isLan()));
			cmd.append(", proxy = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().isProxy()));
			cmd.append(", use_modem = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().useModem()));
			cmd.append(", use_gps = "
					+ DbUtils.booleanToIntStr(station.getStInfo().hasGps()));
			update = "UPDATE station SET " + cmd + " WHERE station_id = "
					+ DbUtils.qVal(station.getStationId());
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(station.getStationId(), update, e);
		}
		return (errObj);
	}

	public RejectedObject updatePollingInfo(Station station) {
		String methodName = "[updatePollingInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" enabled = "
					+ DbUtils.booleanToIntStr(station.getStInfo().isEnabled()));
			cmd.append(", force_polling_time = "
					+ DbUtils.qVal(station.getStInfo().getForcePollingTime()));
			cmd.append(", min_timestamp_for_polling = "
					+ DbUtils.qVal(station.getStInfo()
							.getMinTimestampForPolling()));
			cmd.append(", sample_data_download_enabled = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.isSampleDataDownloadEnable()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE station_id = "
					+ DbUtils.qVal(station.getStationId()));
			update = "UPDATE station SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(station.getStationId(), update, e);
		}
		return (errObj);
	}

	public RejectedObject updateStationInfo(Station station) {
		String methodName = "[updateStationInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" uuid = " + DbUtils.qVal(station.getStationUUID()));
			cmd.append(", shortname = "
					+ DbUtils.qVal(station.getStInfo().getShortStationName()));
			cmd.append(", name = "
					+ DbUtils.qVal(station.getStInfo().getLongStationName()));
			cmd.append(", province = "
					+ DbUtils.qVal(station.getStInfo().getProvince()));
			cmd.append(", city = "
					+ DbUtils.qVal(station.getStInfo().getCity()));
			cmd.append(", address = "
					+ DbUtils.qVal(station.getStInfo().getAddress()));
			cmd.append(", location = "
					+ DbUtils.qVal(station.getStInfo().getLocation()));
			cmd.append(", notes = "
					+ DbUtils.qVal(station.getStInfo().getNotes()));
			cmd.append(", ip_address = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice().getIp()));
			cmd.append(", ip_port = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPortNumber()));
			cmd.append(", tel_number = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPhoneNumber()));
			cmd.append(", router_ip_address = "
					+ DbUtils.qVal(station.getStInfo().getCommDevice()
							.getRouterIpAddress()));
			cmd.append(", lan = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().isLan()));
			cmd.append(", proxy = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().isProxy()));
			cmd.append(", use_modem = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.getCommDevice().useModem()));
			cmd.append(", use_gps = "
					+ DbUtils.booleanToIntStr(station.getStInfo().hasGps()));
			cmd.append(", enabled = "
					+ DbUtils.booleanToIntStr(station.getStInfo().isEnabled()));
			cmd.append(", config_uuid = "
					+ DbUtils.qVal(station.getStInfo().getConfigUUID()));
			cmd.append(", force_polling_time = "
					+ DbUtils.qVal(station.getStInfo().getForcePollingTime()));
			cmd.append(", min_timestamp_for_polling = "
					+ DbUtils.qVal(station.getStInfo()
							.getMinTimestampForPolling()));
			cmd.append(", sample_data_download_enabled = "
					+ DbUtils.booleanToIntStr(station.getStInfo()
							.isSampleDataDownloadEnable()));
			cmd.append(", config_author = "
					+ DbUtils.qVal(station.getStInfo().getConfigUserName()));
			cmd.append(", config_notes = "
					+ DbUtils.qVal(station.getStInfo().getConfigComment()));
			cmd.append(", config_date = "
					+ DbUtils.qVal(station.getStInfo().getConfigDate()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(", delete_date = " + DbUtils.qVal((Date) null));
			cmd.append(", common_config_date = "
					+ DbUtils.qVal(station.getStInfo()
							.getCommonConfigStationUpdateDate()));
			cmd.append(" WHERE station_id = "
					+ DbUtils.qVal(station.getStationId()));
			update = "UPDATE station SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(station.getStationId(), update, e);
		}
		return (errObj);
	}// end updateStationInfo

	public RejectedObject updateCommonConfigInfo(Integer lastUpdateStatus,
			String problemCommonConfig, int stationId) {
		String methodName = "[updateCommonConfigInfo] ";
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			update = "UPDATE station SET last_common_config_update_status = "
					+ DbUtils.qVal(lastUpdateStatus)
					+ ", common_config_problem = "
					+ DbUtils.qVal(problemCommonConfig)
					+ " WHERE station_id = " + DbUtils.qVal(stationId);
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationId, update, e);
		}
		return errObj;
	}// end updateCommonConfigInfo

	public RejectedObject updateCommonConfigDate(Date commonConfigDate,
			Integer stationId) {
		String methodName = "[updateCommonConfigDate] ";
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			update = "UPDATE station SET common_config_date = "
					+ DbUtils.qVal(commonConfigDate) + " WHERE station_id = "
					+ DbUtils.qVal(stationId);
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationId, update, e);
		}
		return (errObj);
	}// end updateCommonConfigDate

	RejectedObject updateConfigCommonConfigDate(int configId,
			Date commonConfigDate) {
		String methodName = "[updateCopCommonConfigDate] ";
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			update = "UPDATE config SET common_config_update_date = "
					+ DbUtils.qVal(commonConfigDate) + " WHERE config_id = "
					+ DbUtils.qVal(configId);
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(commonConfigDate, update, e);
		}
		return (errObj);
	}// end updateCopCommonConfigDate

	public RejectedObject updateConnStationInfo(int stationId,
			StationInfo stInfo, boolean updateAnagraphicInfo) {
		String methodName = "[updateConnStationInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" ip_address = "
					+ DbUtils.qVal(stInfo.getCommDevice().getIp()));
			cmd.append(", ip_port = "
					+ DbUtils.qVal(stInfo.getCommDevice().getPortNumber()));
			cmd.append(", tel_number = "
					+ DbUtils.qVal(stInfo.getCommDevice().getPhoneNumber()));
			cmd.append(", router_ip_address = "
					+ DbUtils.qVal(stInfo.getCommDevice().getRouterIpAddress()));
			cmd.append(", use_modem = "
					+ DbUtils
							.booleanToIntStr(stInfo.getCommDevice().useModem()));
			cmd.append(", lan = "
					+ DbUtils.booleanToIntStr(stInfo.getCommDevice().isLan()));
			cmd.append(", proxy = "
					+ DbUtils.booleanToIntStr(stInfo.getCommDevice().isProxy()));
			cmd.append(", enabled = "
					+ DbUtils.booleanToIntStr(stInfo.isEnabled()));
			if (updateAnagraphicInfo) {
				cmd.append(", uuid = " + DbUtils.qVal(stInfo.getUuid()));
				cmd.append(", shortname = "
						+ DbUtils.qVal(stInfo.getShortStationName()));
				cmd.append(", name = "
						+ DbUtils.qVal(stInfo.getLongStationName()));
				cmd.append(", province = " + DbUtils.qVal(stInfo.getProvince()));
				cmd.append(", city = " + DbUtils.qVal(stInfo.getCity()));
				cmd.append(", address = " + DbUtils.qVal(stInfo.getAddress()));
				cmd.append(", location = " + DbUtils.qVal(stInfo.getLocation()));
				cmd.append(", notes = " + DbUtils.qVal(stInfo.getNotes()));
				cmd.append(", use_gps = "
						+ DbUtils.booleanToIntStr(stInfo.hasGps()));
			}
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE station_id = " + DbUtils.qVal(stationId));
			update = "UPDATE station SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationId, update, e);
		}
		return (errObj);
	}// end updateConnStationInfo

	public RejectedObject updateAnalyzer(Analyzer analyzer) {
		String methodName = "[updateAnalyzer] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" name = " + DbUtils.qVal(analyzer.getName()));
			cmd.append(", brand = " + DbUtils.qVal(analyzer.getBrand()));
			cmd.append(", model = " + DbUtils.qVal(analyzer.getModel()));
			cmd.append(", description = "
					+ DbUtils.qVal(analyzer.getDescription()));
			cmd.append(", serial_number = "
					+ DbUtils.qVal(analyzer.getSerialNumber()));
			cmd.append(", status = " + DbUtils.qVal(analyzer.getStatus()));
			cmd.append(", type = " + DbUtils.qVal(analyzer.getType()));
			cmd.append(", notes = " + DbUtils.qVal(analyzer.getNotes()));
			cmd.append(", delete_date = "
					+ DbUtils.qVal(analyzer.getDeletionDate()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE analyzer_id = "
					+ DbUtils.qVal(analyzer.getAnalyzerId()));
			update = "UPDATE analyzer SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(analyzer.getBrand() + "/"
					+ analyzer.getModel(), update, e);
		}
		return (errObj);
	}// end updateAnalyzer

	public RejectedObject updateElement(GenericElement element) {
		String methodName = "[updateElement] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" enabled = "
					+ DbUtils.booleanToIntStr(element.isEnabled()));
			cmd.append(", delete_date = "
					+ DbUtils.qVal(element.getDeletionDate()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE element_id = "
					+ DbUtils.qVal(element.getElementId()));
			update = "UPDATE element SET" + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(element.getName(), update, e);
		}
		return (errObj);
	}// end updateElement

	public RejectedObject updateScalarElement(GenericElement element) {
		String methodName = "[updateScalarElement] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";

		ScalarElement scalarElement = (ScalarElement) element;
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" measure_name = "
					+ DbUtils.qVal(scalarElement.getUnit()));
			cmd.append(", min_val = "
					+ DbUtils.qVal(scalarElement.getMinValue()));
			cmd.append(", max_val = "
					+ DbUtils.qVal(scalarElement.getMaxValue()));
			cmd.append(", num_dec = "
					+ DbUtils.qVal(scalarElement.getDecimalQuantityValue()));
			cmd.append(" WHERE element_id = "
					+ DbUtils.qVal(element.getElementId()));
			update = "UPDATE scalar_element SET" + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(element.getName(), update, e);
		}
		return (errObj);
	}// end updateScalarElement

	public RejectedObject updateAvgPeriod(AvgPeriod avgPeriod, int elementId) {
		String methodName = "[updateAvgPeriod] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" delete_date = "
					+ DbUtils.qVal(avgPeriod.getDeletionDate()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(", all_data_downloaded = "
					+ DbUtils.booleanToIntStr(avgPeriod.isAllDataDownloaded()));
			cmd.append(" WHERE element_id = " + DbUtils.qVal(elementId));
			cmd.append(" AND avg_period_id = "
					+ DbUtils.qVal(avgPeriod.getAvgPeriodVal()));
			update = "UPDATE element_avg_period SET " + cmd;
			logger.debug("updateAvgPeriod() " + update);
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(elementId + "/"
					+ avgPeriod.getAvgPeriodVal(), update, e);
		}
		return (errObj);
	}// end updateAvgPeriod

	public RejectedObject updateParameter(ParameterInfo parInfo, Date updateDate) {
		String methodName = "[updateParameter] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append("param_id = " + DbUtils.qVal(parInfo.getParam_id()));
			cmd.append(", name = " + DbUtils.qVal(parInfo.getName()));
			cmd.append(", physical_dimension = "
					+ DbUtils.qVal(parInfo.getPhysicalDimension()));
			cmd.append(", type = " + DbUtils.qVal(parInfo.getType()));
			cmd.append(", molecular_weight = "
					+ DbUtils.qVal(parInfo.getMolecularWeight()));
			cmd.append(", update_date = " + DbUtils.qVal(updateDate));
			cmd.append(" WHERE param_id = " + DbUtils.qVal(parInfo.getParam_id()));
			update = "UPDATE parameter SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(parInfo.getParam_id(), update, e);
		}
		return (errObj);
	}// end updateParameter

	public RejectedObject updateModem(String deviceId, ModemInfo modemInfo) {
		String methodName = "[updateModem] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append("device_id = " + DbUtils.qVal(modemInfo.getDeviceId()));
			cmd.append(", shared_line = "
					+ DbUtils.booleanToIntStr(modemInfo.isSharedLine()));
			cmd.append(", phone_prefix = "
					+ DbUtils.qVal(modemInfo.getPhonePrefix()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE device_id = " + DbUtils.qVal(deviceId));
			update = "UPDATE modem SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(modemInfo.getDeviceId(), update, e);
		}
		return (errObj);
	}// end updateModem

	public RejectedObject updateMeasureUnit(MeasureUnitInfo measureInfo,
			Date updateDate) {
		String methodName = "[updateMeasureUnit] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" update_date = " + DbUtils.qVal(updateDate));
			cmd.append(", description = "
					+ DbUtils.qVal(measureInfo.getDescription()));
			cmd.append(", physical_dimension = "
					+ DbUtils.qVal(measureInfo.getPhysicalDimension()));
			cmd.append(", allowed_for_analyzer = "
					+ DbUtils.booleanToIntStr(measureInfo.isAllowedForAnalyzer()));
			cmd.append(", allowed_for_acquisition = "
					+ DbUtils
							.booleanToIntStr(measureInfo.isAllowedForAcquisition()));
			cmd.append(", conversion_multiplyer = "
					+ DbUtils.qVal(measureInfo.getConversionMultiplyer()));
			cmd.append(", conversion_addendum = "
					+ DbUtils.qVal(measureInfo.getConversionAddendum()));
			cmd.append(", conversion_formula = "
					+ DbUtils.qVal(measureInfo.getConversionFormula()));
			cmd.append(" WHERE measure_name = "
					+ DbUtils.qVal(measureInfo.getMeasureName()));
			update = "UPDATE measure_unit SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(measureInfo.getMeasureName(), update, e);
		}
		return (errObj);
	}// end updateMeasureUnit

	public RejectedObject updateAlarmName(AlarmNameInfo alarmInfo,
			Date updateDate) {
		String methodName = "[updateAlarmName] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" update_date = " + DbUtils.qVal(updateDate));
			cmd.append(", alarm_name = " + DbUtils.qVal(alarmInfo.getAlarmName()));
			cmd.append(", type = " + DbUtils.qVal(alarmInfo.getType()));
			cmd.append(", data_quality_relevant = "
					+ DbUtils.booleanToIntStr(alarmInfo.isDataQualityRelevant()));
			cmd.append(" WHERE alarm_id = " + DbUtils.qVal(alarmInfo.getAlarmId()));
			update = "UPDATE alarm_name SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(alarmInfo.getAlarmId(), update, e);
		}
		return (errObj);
	}// end updateAlarmName

	public RejectedObject updateDefaultAvgPeriod(Integer avgPeriodId,
			boolean defaultAvgPeriod, Integer avgPeriodToUpdate, Date updateDate) {
		String methodName = "[updateDeafultAvgPeriod] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();

			if (!avgPeriodToUpdate.equals(0)) {
				cmd = new StringBuffer();
				cmd.append(" update_date = " + DbUtils.qVal(updateDate));
				cmd.append(", default_avg = "
						+ DbUtils.booleanToIntStr(!defaultAvgPeriod));
				cmd.append(" WHERE avg_period_id = "
						+ DbUtils.qVal(avgPeriodToUpdate));
				update = "UPDATE avg_period SET " + cmd;
				stmt.executeUpdate(update);
			}
			cmd = new StringBuffer();
			cmd.append(" update_date = " + DbUtils.qVal(updateDate));
			cmd.append(", default_avg = "
					+ DbUtils.booleanToIntStr(defaultAvgPeriod));
			cmd.append(" WHERE avg_period_id = " + DbUtils.qVal(avgPeriodId));
			update = "UPDATE avg_period SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException e1) {
				logger.error("Error during rollback", e1);
				errObj = new RejectedObject(avgPeriodId, update, e);
			}
			errObj = new RejectedObject(avgPeriodId, update, e);
		}
		return (errObj);
	}// end updateDeafultAvgPeriod

	public RejectedObject updateContainerAlarm(ContainerAlarm stationAlarm) {
		String methodName = "[updateContainerAlarm] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" notes = " + DbUtils.qVal(stationAlarm.getNotes()));
			cmd.append(", delete_date = "
					+ DbUtils.qVal(stationAlarm.getDeletionDate()));
			cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
			cmd.append(" WHERE station_alarm_id = "
					+ DbUtils.qVal(stationAlarm.getAlarmDbID()));
			update = "UPDATE station_alarm SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationAlarm.getAlarmID(), update, e);
		}
		return (errObj);
	}// end updateContainerAlarm

	public RejectedObject updateAllDataDownloadedForAvgPeriod(int elementId,
			int avgPeriodId, boolean allDataDownloaded) {
		String methodName = "[updateAllDataDownloadedForAvgPeriod] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append("all_data_downloaded = "
					+ DbUtils.booleanToIntStr(allDataDownloaded));
			cmd.append(" WHERE element_id = " + DbUtils.qVal(elementId));
			cmd.append(" AND avg_period_id = " + DbUtils.qVal(avgPeriodId));
			update = "UPDATE element_avg_period SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(elementId + "/" + avgPeriodId, update,
					e);
		}
		return (errObj);
	}// end updateAllDataDownloadedForAvgPeriod

	public RejectedObject updateStMapPosition(int stationId, Integer map_x,
			Integer map_y, Boolean isGenericPosition) {
		String methodName = "[updateStMapPosition] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			if (isGenericPosition) {
				cmd.append(" generic_map_x = " + DbUtils.qVal(map_x));
				cmd.append(", generic_map_y = " + DbUtils.qVal(map_y));
			} else {
				cmd.append(" map_x = " + DbUtils.qVal(map_x));
				cmd.append(", map_y = " + DbUtils.qVal(map_y));
			}

			cmd.append(" WHERE station_id = " + DbUtils.qVal(stationId));
			update = "UPDATE station SET " + cmd;
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationId, update, e);
		}
		return (errObj);
	}// end updateStMapPosition

	public RejectedObject updateContainerAlarmStatus(int stationAlarmId,
			AlarmData alarmData) {
		String methodName = "[updateContainerAlarmStatus] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" status = " + DbUtils.qVal(alarmData.getValue()));
			cmd.append(", update_date = "
					+ DbUtils.qVal(alarmData.getTimestamp()));
			cmd.append(" WHERE station_alarm_id = "
					+ DbUtils.qVal(stationAlarmId));
			update = "UPDATE station_alarm_status SET " + cmd;
			int rowsUpdated = stmt.executeUpdate(update);

			// in case of update 0 rows, it try to do an insert operation
			if (rowsUpdated == 0) {
				String ins = "";
				Statement stmt2 = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" (station_alarm_id,status,update_date)");
				cmd.append(" VALUES (");
				cmd.append(DbUtils.qVal(stationAlarmId)).append(",");
				cmd.append(DbUtils.qVal(alarmData.getValue())).append(",");
				cmd.append(DbUtils.qVal(alarmData.getTimestamp()));
				cmd.append(")");
				ins = "INSERT INTO station_alarm_status" + cmd;
				stmt2.executeUpdate(ins);
				stmt2.close();
			}
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + update, insEx);

			errObj = new RejectedObject(stationAlarmId, update, insEx);
		}
		return (errObj);
	}// end updateContainerAlarmStatus

	public RejectedObject deleteContainerAlarmStatus(int stationAlarmId) {
		String methodName = "[deleteContainerAlarmStatus] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM station_alarm_status WHERE station_alarm_id = "
					+ DbUtils.qVal(stationAlarmId);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(stationAlarmId, delete, e);
		}
		return (errObj);
	}// end deleteContainerAlarmStatus

	public RejectedObject deletePhysicalDimension(String name) {
		String methodName = "[deletePhysicalDimension] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM physical_dimension WHERE physical_dimension = "
					+ DbUtils.qVal(name);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(name, delete, e);
		}
		return (errObj);
	}// end deletePhysicalDimension

	public RejectedObject deleteModem(String deviceId) {
		String methodName = "[deleteModem] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM modem WHERE device_id = "
					+ DbUtils.qVal(deviceId);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(deviceId, delete, e);
		}
		return (errObj);
	}// end deleteModem

	public RejectedObject deleteAvgPeriod(int avgPeriod) {
		String methodName = "[deleteAvgPeriod] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM avg_period WHERE avg_period_id = "
					+ DbUtils.qVal(avgPeriod);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(avgPeriod, delete, e);
		}
		return (errObj);
	}// end deleteAvgPeriod

	public RejectedObject deleteParameter(String param_id) {
		String methodName = "[deleteParameter] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM parameter WHERE param_id = "
					+ DbUtils.qVal(param_id);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(param_id, delete, e);
		}
		return (errObj);
	}// end deleteParameter

	public RejectedObject deleteMeasureUnit(String name) {
		String methodName = "[deleteMeasureUnit] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM measure_unit WHERE measure_name = "
					+ DbUtils.qVal(name);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(name, delete, e);
		}
		return (errObj);
	}// end deleteMeasureUnit

	public RejectedObject deleteAlarmName(String alarm_id) {
		String methodName = "[deleteAlarmName] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM alarm_name WHERE alarm_id = "
					+ DbUtils.qVal(alarm_id);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(alarm_id, delete, e);
		}
		return (errObj);
	}// end deleteAlarmName

	public RejectedObject addPhysicalDimension(String name, Date updateDate) {
		String methodName = "[addPhysicalDimension] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO physical_dimension (physical_dimension, "
					+ "update_date) VALUES (" + DbUtils.qVal(name) + ", "
					+ DbUtils.qVal(updateDate) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(name, insert, e);
		}
		return (errObj);
	}// end addPhysicalDimension

	public RejectedObject addAvgPeriod(Integer avgPeriodId, Date updateDate) {
		String methodName = "[addAvgPeriod] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO avg_period (avg_period_id, default_avg, update_date) VALUES ("
					+ DbUtils.qVal(avgPeriodId)
					+ ", "
					+ DbUtils.booleanToIntStr(false)
					+ ", "
					+ DbUtils.qVal(updateDate) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(avgPeriodId, insert, e);
		}
		return (errObj);
	}// end addAvgPeriod

	public RejectedObject insertParameter(ParameterInfo parInfo, Date updateDate) {
		String methodName = "[insertParameter] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO parameter (param_id, name, physical_dimension, "
					+ "type, molecular_weight, update_date) VALUES ("
					+ DbUtils.qVal(parInfo.getParam_id())
					+ ", "
					+ DbUtils.qVal(parInfo.getName())
					+ ", "
					+ DbUtils.qVal(parInfo.getPhysicalDimension())
					+ ", "
					+ DbUtils.qVal(parInfo.getType())
					+ ", "
					+ DbUtils.qVal(parInfo.getMolecularWeight())
					+ ", "
					+ DbUtils.qVal(updateDate) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(parInfo.getParam_id(), insert, e);
		}
		return (errObj);
	}// end insertParameter

	public RejectedObject insertModem(ModemInfo modemInfo) {
		String methodName = "[insertModem] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO modem (device_id, shared_line, phone_prefix)"
					+ " VALUES (" + DbUtils.qVal(modemInfo.getDeviceId()) + ", "
					+ DbUtils.booleanToIntStr(modemInfo.isSharedLine()) + ", "
					+ DbUtils.qVal(modemInfo.getPhonePrefix()) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(modemInfo.getDeviceId(), insert, e);
		}
		return (errObj);
	}// end insertModem

	public RejectedObject updateStorageManager(
			StorageManagerInfo storageManager, int configId,
			Date commonCfgUpdateDate) {
		String methodName = "[updateStorageManager] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "UPDATE config SET max_days_of_data = "
					+ DbUtils.qVal(storageManager.getMaxDaysOfData())
					+ ", max_days_of_aggregate_data = "
					+ DbUtils.qVal(storageManager.getMaxDaysOfAggregateData())
					+ ", disk_full_warning_threshold_percent = "
					+ DbUtils
							.qVal(storageManager.getDiskFullWarningThresholdPercent())
					+ ", disk_full_alarm_threshold_percent = "
					+ DbUtils
							.qVal(storageManager.getDiskFullAlarmThresholdPercent())
					+ ", common_config_update_date = "
					+ DbUtils.qVal(commonCfgUpdateDate) + " WHERE config_id = "
					+ DbUtils.qVal(configId);
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(storageManager.getMaxDaysOfAggregateData()
					+ " - " + storageManager.getMaxDaysOfData() + " - "
					+ storageManager.getDiskFullAlarmThresholdPercent() + " - "
					+ storageManager.getDiskFullWarningThresholdPercent(), insert, e);
		}
		return (errObj);
	}// end updateStorageManager

	public RejectedObject updateStandard(StandardInfo standard, int configId,
			Date commonCfgUpdateDate) {
		String methodName = "[updateStandard] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "UPDATE config SET reference_temperature_k = "
					+ DbUtils.qVal(standard.getReferenceTemperatureK())
					+ ", reference_pressure_kpa = "
					+ DbUtils.qVal(standard.getReferencePressureKPa())
					+ ", common_config_update_date = "
					+ DbUtils.qVal(commonCfgUpdateDate) + " WHERE config_id = "
					+ DbUtils.qVal(configId);
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(standard.getReferencePressureKPa() + " - "
					+ standard.getReferenceTemperatureK(), insert, e);
		}
		return (errObj);
	}// end updateStandard

	public RejectedObject updateOther(OtherInfo other, int configId,
			Date commonCfgUpdateDate) {
		String methodName = "[updateOther] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "UPDATE config SET door_alarm_id = "
					+ DbUtils.qVal(other.getDoorAlarmId())
					+ ", data_write_to_disk_period = "
					+ DbUtils.qVal(other.getDataWriteToDiskPeriod())
					+ ", manual_operations_auto_reset_period = "
					+ DbUtils.qVal(other.getManualOperationsAutoResetPeriod())
					+ ", cop_service_port = "
					+ DbUtils.qVal(other.getCopServicePort())
					+ ", maps_site_url_formatter = "
					+ DbUtils.qVal(other.getMapsSiteURLFormatter())
					+ ", common_config_update_date = "
					+ DbUtils.qVal(commonCfgUpdateDate) + " WHERE config_id = "
					+ DbUtils.qVal(configId);
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(other.getDoorAlarmId() + " - "
					+ other.getDataWriteToDiskPeriod() + " - "
					+ other.getManualOperationsAutoResetPeriod(), insert, e);
		}
		return (errObj);
	}// end updateOther

	public RejectedObject insertMeasureUnit(MeasureUnitInfo measureInfo,
			Date updateDate) {
		String methodName = "[insertMeasureUnit] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO measure_unit (measure_name, description, "
					+ "physical_dimension, update_date) VALUES ("
					+ DbUtils.qVal(measureInfo.getMeasureName()) + ", "
					+ DbUtils.qVal(measureInfo.getDescription()) + ", "
					+ DbUtils.qVal(measureInfo.getPhysicalDimension()) + ", "
					+ DbUtils.qVal(updateDate) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(measureInfo.getMeasureName(), insert, e);
		}
		return (errObj);
	}// end insertMeasureUnit

	public RejectedObject insertAlarmName(AlarmNameInfo alarmInfo,
			Date updateDate) {
		String methodName = "[insertAlarmName] ";
		RejectedObject errObj = null;
		String insert = "";
		try {
			Statement stmt = conn.createStatement();
			insert = "INSERT INTO alarm_name (alarm_id, alarm_name, type, "
					+ "data_quality_relevant, update_date) VALUES ("
					+ DbUtils.qVal(alarmInfo.getAlarmId()) + ", "
					+ DbUtils.qVal(alarmInfo.getAlarmName()) + ", "
					+ DbUtils.qVal(alarmInfo.getType()) + ", "
					+ DbUtils.booleanToIntStr(alarmInfo.isDataQualityRelevant())
					+ ", " + DbUtils.qVal(updateDate) + ")";
			stmt.executeUpdate(insert);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + insert, e);

			errObj = new RejectedObject(alarmInfo.getAlarmId(), insert, e);
		}
		return (errObj);
	}// end insertAlarmName

	public RejectedObject deleteAnalyzerAlarmStatus(int analyzerId) {
		String methodName = "[deleteAnalyzerAlarmStatus] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM analyzer_alarm_status WHERE analyzer_id = "
					+ DbUtils.qVal(analyzerId);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);

			errObj = new RejectedObject(analyzerId, delete, e);
		}
		return (errObj);
	}// end deleteAnalyzerAlarmStatus

	public RejectedObject updateAnalyzerAlarmStatus(int analyzerId,
			AnalyzerEventDatum anStatus) {
		String methodName = "[updateAnalyzerAlarmStatus] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String update = "";
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" status = "
					+ DbUtils.booleanObjToIntStr(anStatus.getValue()));
			cmd.append(", update_date = "
					+ DbUtils.qVal(anStatus.getTimestamp()));
			cmd.append(" WHERE analyzer_id = " + DbUtils.qVal(analyzerId));
			cmd.append(" AND analyzer_alarm_type = "
					+ DbUtils.qVal(anStatus.getType()));
			update = "UPDATE analyzer_alarm_status SET " + cmd;
			int rowsUpdated = stmt.executeUpdate(update);

			// in case of update 0 rows, it try to do an insert operation
			if (rowsUpdated == 0) {

				Statement stmt2 = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" (analyzer_id,analyzer_alarm_type,status,update_date)");
				cmd.append(" VALUES (");
				cmd.append(DbUtils.qVal(analyzerId)).append(",");
				cmd.append(DbUtils.qVal(anStatus.getType())).append(",");
				cmd.append(DbUtils.booleanObjToIntStr(anStatus.getValue()))
						.append(",");
				cmd.append(DbUtils.qVal(anStatus.getTimestamp()));
				cmd.append(")");
				ins = "INSERT INTO analyzer_alarm_status" + cmd;
				stmt2.executeUpdate(ins);
				stmt2.close();
			}
			stmt.close();

		} catch (SQLException e) {
			String query = "";
			if (ins.equals(""))
				query = update;
			else
				query = ins;
			logger.error(methodName + query, e);

			errObj = new RejectedObject(analyzerId + "/" + anStatus.getType(),
					update, e);
		}
		return (errObj);
	}// end updateAnalyzerAlarmStatus

	public RejectedObject writeConfigInfo(ConfigInfo cop) {
		String methodName = "[writeConfigInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (max_num_phone_lines, reserved_line, synthetic_icon");
			cmd.append(", polling_office_time, polling_extra_office");
			cmd.append(", use_polling_extra, close_sat, close_sun,open_at");
			cmd.append(", close_at, total_num_modem, num_modem_shared_lines");
			cmd.append(", download_type_sample_data, download_alarm_data");
			cmd.append(", min_temperature_threshold, max_temperature_threshold");
			cmd.append(", alarm_max_temperature_threshold, cop_ip");
			cmd.append(", router_timeout, router_try_timeout, cop_router_ip,"
					+ " name, generic_map_name, time_host_proxy, "
					+ "time_host_lan, time_host_modem, time_host_router, "
					+ "num_reserved_lines_ui, proxy_host, proxy_port, proxy_exclusion)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(cop.getMaxNumLines())).append(",");
			cmd.append(DbUtils.booleanToIntStr(cop.isReservedLine())).append(
					",");
			cmd.append(DbUtils.booleanToIntStr(cop.isSyntheticIcon())).append(
					",");
			cmd.append(DbUtils.qVal(cop.getPollConfig().getPollingOfficeTime()))
					.append(",");
			cmd.append(
					DbUtils.qVal(cop.getPollConfig().getPollingExtraOffice()))
					.append(",");
			cmd.append(
					DbUtils.booleanToIntStr(cop.getPollConfig()
							.isUsePollingExtra())).append(",");
			cmd.append(
					DbUtils.booleanToIntStr(cop.getPollConfig().isCloseSat()))
					.append(",");
			cmd.append(
					DbUtils.booleanToIntStr(cop.getPollConfig().isCloseSun()))
					.append(",");
			cmd.append(DbUtils.qVal(cop.getPollConfig().getOpenAt())).append(
					",");
			cmd.append(DbUtils.qVal(cop.getPollConfig().getCloseAt())).append(
					",");
			cmd.append(DbUtils.qVal(cop.getTotalNumModem())).append(",");
			cmd.append(DbUtils.qVal(cop.getNumModemSharedLines())).append(",");
			cmd.append(DbUtils.qVal(cop.getSampleDataTypeToDownload())).append(
					",");
			cmd.append(DbUtils.booleanToIntStr(cop.isDownloadAlarmHistory()))
					.append(",");
			cmd.append(DbUtils.qVal(cop.getMinThreshold())).append(",");
			cmd.append(DbUtils.qVal(cop.getMaxThreshold())).append(",");
			cmd.append(DbUtils.qVal(cop.getAlarmMaxThreshold())).append(",");
			cmd.append(DbUtils.qVal(cop.getCopIp())).append(",");
			cmd.append(DbUtils.qVal(cop.getRouterTimeout())).append(",");
			cmd.append(DbUtils.qVal(cop.getRouterTryTimeout())).append(",");
			cmd.append(DbUtils.qVal(cop.getCopRouter())).append(",");
			cmd.append(DbUtils.qVal(cop.getName())).append(",");
			cmd.append(DbUtils.qVal(cop.getGenericMapName())).append(",");
			cmd.append(DbUtils.qVal(cop.getTimeHostProxy())).append(",");
			cmd.append(DbUtils.qVal(cop.getTimeHostLan())).append(",");
			cmd.append(DbUtils.qVal(cop.getTimeHostModem())).append(",");
			cmd.append(DbUtils.qVal(cop.getTimeHostRouter())).append(",");
			cmd.append(DbUtils.qVal(cop.getNumReservedLinesUi())).append(",");
			cmd.append(DbUtils.qVal(cop.getProxyHost())).append(",");
			cmd.append(DbUtils.qVal(cop.getProxyPort())).append(",");
			cmd.append(DbUtils.qVal(cop.getProxyExlusion()));
			cmd.append(")");
			ins = "INSERT INTO cop" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(cop, ins, e);
		}
		return (errObj);
	}// end writeConfigInfo

	public RejectedObject writeModem(ModemConf modemConf) {
		String methodName = "[writeModem] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (device_id,shared_line,phone_prefix)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(modemConf.getDescription())).append(",");
			cmd.append(DbUtils.booleanToIntStr(modemConf.isSharedLine()))
					.append(",");
			cmd.append(DbUtils.qVal(modemConf.getPhonePrefix()));
			cmd.append(")");
			ins = "INSERT INTO modem" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(modemConf, ins, e);
		}
		return (errObj);
	}// end writeModem

	public RejectedObject writeStationInfo(Station station, int virtualCopId) {
		String methodName = "[writeStationInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (uuid,shortname,name,province,city");
			cmd.append(",address,location,notes,use_gps,config_uuid");
			cmd.append(",enabled,ip_address,ip_port,tel_number");
			cmd.append(",router_ip_address,use_modem,force_polling_time");
			cmd.append(",sample_data_download_enabled");
			cmd.append(",config_author,config_notes,config_date");
			cmd.append(",virtual_cop_id, common_config_date, lan, proxy)");

			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(station.getStationUUID())).append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getShortStationName()))
					.append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getLongStationName()))
					.append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getProvince())).append(
					",");
			cmd.append(DbUtils.qVal(station.getStInfo().getCity())).append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getAddress())).append(
					",");
			cmd.append(DbUtils.qVal(station.getStInfo().getLocation())).append(
					",");
			cmd.append(DbUtils.qVal(station.getStInfo().getNotes()))
					.append(",");
			cmd.append(DbUtils.booleanToIntStr(station.getStInfo().hasGps()))
					.append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getConfigUUID()))
					.append(",");
			cmd.append(DbUtils.booleanToIntStr(station.getStInfo().isEnabled()))
					.append(",");
			cmd.append(
					DbUtils.qVal(station.getStInfo().getCommDevice().getIp()))
					.append(",");
			cmd.append(
					DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPortNumber())).append(",");
			cmd.append(
					DbUtils.qVal(station.getStInfo().getCommDevice()
							.getPhoneNumber())).append(",");
			cmd.append(
					DbUtils.qVal(station.getStInfo().getCommDevice()
							.getRouterIpAddress())).append(",");
			cmd.append(
					DbUtils.booleanToIntStr(station.getStInfo().getCommDevice()
							.useModem())).append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getForcePollingTime()))
					.append(",");
			cmd.append(
					DbUtils.booleanToIntStr(station.getStInfo()
							.isSampleDataDownloadEnable())).append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getConfigUserName()))
					.append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getConfigComment()))
					.append(",");
			cmd.append(DbUtils.qVal(station.getStInfo().getConfigDate()))
					.append(",");
			cmd.append(DbUtils.qVal(virtualCopId)).append(",");
			cmd.append(
					DbUtils.qVal(station.getStInfo()
							.getCommonConfigStationUpdateDate())).append(",");
			cmd.append(
					DbUtils.booleanToIntStr(station.getStInfo().getCommDevice()
							.isLan())).append(",");
			cmd.append(DbUtils.booleanToIntStr(station.getStInfo()
					.getCommDevice().isProxy()));
			cmd.append(")");
			ins = "INSERT INTO station" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(station.getStInfo()
					.getShortStationName(), ins, e);
		}
		return (errObj);
	}// end writeStationInfo

	public RejectedObject writeConnStationInfo(StationInfo stInfo) {
		String methodName = "[writeConnStationInfo] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (uuid, shortname, name, province, city, address,"
					+ " location, notes, ip_address, ip_port, tel_number");
			cmd.append(", router_ip_address, use_modem");
			cmd.append(", virtual_cop_id, lan, proxy)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stInfo.getUuid())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getShortStationName())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getLongStationName())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getProvince())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getCity())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getAddress())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getLocation())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getNotes())).append(",");
			cmd.append(DbUtils.qVal(stInfo.getCommDevice().getIp()))
					.append(",");
			cmd.append(DbUtils.qVal(stInfo.getCommDevice().getPortNumber()))
					.append(",");
			cmd.append(DbUtils.qVal(stInfo.getCommDevice().getPhoneNumber()))
					.append(",");
			cmd.append(
					DbUtils.qVal(stInfo.getCommDevice().getRouterIpAddress()))
					.append(",");
			cmd.append(
					DbUtils.booleanToIntStr(stInfo.getCommDevice().useModem()))
					.append(",");
			cmd.append(DbUtils.qVal(stInfo.getCopId())).append(",");
			cmd.append(DbUtils.booleanToIntStr(stInfo.getCommDevice().isLan()))
					.append(",");
			cmd.append(DbUtils
					.booleanToIntStr(stInfo.getCommDevice().isProxy()));
			cmd.append(")");
			ins = "INSERT INTO station" + cmd;
			logger.debug(ins);
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(stInfo.getShortStationName(), ins, e);
		}
		return (errObj);
	}// end writeConnStationInfo

	public RejectedObject writeAnalyzer(Analyzer analyzer, int stationId) {
		String methodName = "[writeAnalyzer] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (station_id,uuid,name,brand,model,description");
			cmd.append(",serial_number,status,type,notes,delete_date)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationId)).append(",");
			cmd.append(DbUtils.qVal(analyzer.getAnalyzerUUID())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getName())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getBrand())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getModel())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getDescription())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getSerialNumber())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getStatus())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getType())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getNotes())).append(",");
			cmd.append(DbUtils.qVal(analyzer.getDeletionDate()));
			cmd.append(")");
			ins = "INSERT INTO analyzer" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(analyzer.getBrand() + "/"
					+ analyzer.getModel(), ins, e);
		}
		return (errObj);
	}// end writeAnalyzer

	public RejectedObject writeElement(GenericElement element, int analyzerId) {
		String methodName = "[writeElement] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (analyzer_id,param_id,enabled");
			cmd.append(",delete_date,type)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(analyzerId)).append(",");
			cmd.append(DbUtils.qVal(element.getName())).append(",");
			cmd.append(DbUtils.booleanToIntStr(element.isEnabled()))
					.append(",");
			cmd.append(DbUtils.qVal(element.getDeletionDate())).append(",");
			cmd.append(DbUtils.qVal(element.getType()));
			cmd.append(")");
			ins = "INSERT INTO element" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(element.getName(), ins, e);
		}
		return (errObj);
	}// end writeElement

	public RejectedObject writeScalarElement(GenericElement element,
			int elementId) {
		String methodName = "[writeScalarElement] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		ScalarElement scalarElement = (ScalarElement) element;
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (element_id,measure_name,min_val,max_val,num_dec)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(elementId)).append(",");
			cmd.append(DbUtils.qVal(scalarElement.getUnit())).append(",");
			cmd.append(DbUtils.qVal(scalarElement.getMinValue())).append(",");
			cmd.append(DbUtils.qVal(scalarElement.getMaxValue())).append(",");
			cmd.append(DbUtils.qVal(scalarElement.getDecimalQuantityValue()));
			cmd.append(")");
			ins = "INSERT INTO scalar_element" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(element.getName(), ins, e);
		}
		return (errObj);
	}// end writeScalarElement

	public RejectedObject writeAvgPeriod(AvgPeriod avgPeriod, int elementId) {
		String methodName = "[writeAvgPeriod] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (element_id,avg_period_id,delete_date,");
			cmd.append("all_data_downloaded)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(elementId)).append(",");
			cmd.append(DbUtils.qVal(avgPeriod.getAvgPeriodVal())).append(",");
			cmd.append(DbUtils.qVal(avgPeriod.getDeletionDate())).append(",");
			cmd.append(DbUtils.booleanToIntStr(avgPeriod.isAllDataDownloaded()));
			cmd.append(")");
			ins = "INSERT INTO element_avg_period" + cmd;
			logger.debug("writeAvgPeriod() " + ins);
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(elementId + "/"
					+ avgPeriod.getAvgPeriodVal(), ins, e);
		}
		return (errObj);
	}// end writeAvgPeriod

	public RejectedObject writeContainerAlarm(ContainerAlarm stationAlarm,
			int stationId) {
		String methodName = "[writeContainerAlarm] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (uuid,station_id,alarm_id,notes,delete_date)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationAlarm.getAlarmUUID())).append(",");
			cmd.append(DbUtils.qVal(stationId)).append(",");
			cmd.append(DbUtils.qVal(stationAlarm.getAlarmID())).append(",");
			cmd.append(DbUtils.qVal(stationAlarm.getNotes())).append(",");
			cmd.append(DbUtils.qVal(stationAlarm.getDeletionDate()));
			cmd.append(")");
			ins = "INSERT INTO station_alarm" + cmd;
			;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(stationAlarm.getAlarmID(), ins, e);
		}
		return (errObj);
	}// end writeContainerAlarm

	public RejectedObject setStationDeleted(int stationId, Date currentDate) {
		String methodName = "[setStationDeleted] ";
		RejectedObject errObj = null;
		String update = "";
		try {
			Statement stmt = conn.createStatement();
			update = "UPDATE station SET delete_date="
					+ DbUtils.qVal(currentDate) + ", update_date = "
					+ DbUtils.CURRENT_TIMESTAMP + " WHERE station_id = "
					+ DbUtils.qVal(stationId);
			stmt.executeUpdate(update);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + update, e);

			errObj = new RejectedObject(stationId, update, e);
		}
		return (errObj);
	}// end setStationDeleted

	/*
	 * Methods for writing data
	 */
	public RejectedObject writeScalarMeansData(int elementId, int avgPeriodId,
			RainScalarData scalarData) {
		String methodName = "[writeScalarMeansData] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (element_id,avg_period_id,timestamp_mean_data,");
			cmd.append("mean_data_value,flag,multiple_flag)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(elementId)).append(",");
			cmd.append(DbUtils.qVal(avgPeriodId)).append(",");
			cmd.append(DbUtils.qVal(scalarData.getTimestamp())).append(",");
			cmd.append(DbUtils.qVal(scalarData.getValue())).append(",");
			cmd.append(DbUtils.booleanToIntStr(scalarData.isNotValid()))
					.append(",");
			cmd.append(DbUtils.qVal(scalarData.getInstrumentFlags()));
			cmd.append(")");
			ins = "INSERT INTO mean_data" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" mean_data_value = "
						+ DbUtils.qVal(scalarData.getValue()));
				cmd.append(", flag = "
						+ DbUtils.booleanToIntStr(scalarData.isNotValid()));
				cmd.append(", multiple_flag = "
						+ DbUtils.qVal(scalarData.getInstrumentFlags()));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(" WHERE element_id = " + DbUtils.qVal(elementId));
				cmd.append(" AND avg_period_id = " + DbUtils.qVal(avgPeriodId));
				cmd.append(" AND timestamp_mean_data = "
						+ DbUtils.qVal(scalarData.getTimestamp()));
				update = "UPDATE mean_data SET" + cmd;
				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(scalarData, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeScalarMeansData

	public RejectedObject writeWindMeansData(int elementId, int avgPeriodId,
			WindData windData) {
		String methodName = "[writeWindMeansData] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (element_id, avg_period_id, timestamp_mean_wind_data, ");
			cmd.append("vectorial_speed, vectorial_direction, ");
			cmd.append("standard_deviation, scalar_speed, gust_speed, ");
			cmd.append("gust_direction, calms_number_percent, calm, ");
			cmd.append("flag, multiple_flag)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(elementId)).append(",");
			cmd.append(DbUtils.qVal(avgPeriodId)).append(",");
			cmd.append(DbUtils.qVal(windData.getTimestamp())).append(",");
			cmd.append(DbUtils.qVal(windData.getVectorialSpeed())).append(",");
			cmd.append(DbUtils.qVal(windData.getVectorialDirection())).append(
					",");
			cmd.append(DbUtils.qVal(windData.getStandardDev())).append(",");
			cmd.append(DbUtils.qVal(windData.getScalarSpeed())).append(",");
			cmd.append(DbUtils.qVal(windData.getGustSpeed())).append(",");
			cmd.append(DbUtils.qVal(windData.getGustDirection())).append(",");
			cmd.append(DbUtils.qVal(windData.getCalmNumberPerc())).append(",");
			cmd.append(DbUtils.booleanObjToIntStr(windData.isCalm())).append(
					",");
			cmd.append(DbUtils.booleanToIntStr(windData.isNotValid())).append(
					",");
			cmd.append(DbUtils.qVal(windData.getInstrumentFlags()));
			cmd.append(")");
			ins = "INSERT INTO mean_wind_data" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" vectorial_speed = "
						+ DbUtils.qVal(windData.getVectorialSpeed()));
				cmd.append(", vectorial_direction = "
						+ DbUtils.qVal(windData.getVectorialDirection()));
				cmd.append(", standard_deviation = "
						+ DbUtils.qVal(windData.getStandardDev()));
				cmd.append(", scalar_speed = "
						+ DbUtils.qVal(windData.getScalarSpeed()));
				cmd.append(", gust_speed = "
						+ DbUtils.qVal(windData.getGustSpeed()));
				cmd.append(", gust_direction = "
						+ DbUtils.qVal(windData.getGustDirection()));
				cmd.append(", calms_number_percent = "
						+ DbUtils.qVal(windData.getCalmNumberPerc()));
				cmd.append(", flag = "
						+ DbUtils.booleanToIntStr(windData.isNotValid()));
				cmd.append(", multiple_flag = "
						+ DbUtils.qVal(windData.getInstrumentFlags()));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(" WHERE element_id = " + DbUtils.qVal(elementId));
				cmd.append(" AND avg_period_id = " + DbUtils.qVal(avgPeriodId));
				cmd.append(" AND timestamp_mean_wind_data = "
						+ DbUtils.qVal(windData.getTimestamp()));
				update = "UPDATE mean_wind_data SET" + cmd;
				logger.debug(methodName + update);
				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(windData, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeWindMeansData

	public RejectedObject writeScalarSampleData(int elementId,
			RainScalarData scalarData) {
		String methodName = "[writeScalarSampleData] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (element_id,timestamp_sample_data,");
			cmd.append("sample_data_value,flag,multiple_flag)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(elementId)).append(",");
			cmd.append(DbUtils.qVal(scalarData.getTimestamp())).append(",");
			cmd.append(DbUtils.qVal(scalarData.getValue())).append(",");
			cmd.append(DbUtils.booleanToIntStr(scalarData.isNotValid()))
					.append(",");
			cmd.append(DbUtils.qVal(scalarData.getInstrumentFlags()));
			cmd.append(")");
			ins = "INSERT INTO sample_data" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" sample_data_value = "
						+ DbUtils.qVal(scalarData.getValue()));
				cmd.append(", flag = "
						+ DbUtils.booleanToIntStr(scalarData.isNotValid()));
				cmd.append(", multiple_flag = "
						+ DbUtils.qVal(scalarData.getInstrumentFlags()));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(" WHERE element_id = " + DbUtils.qVal(elementId));
				cmd.append(" AND timestamp_sample_data = "
						+ DbUtils.qVal(scalarData.getTimestamp()));
				update = "UPDATE sample_data SET" + cmd;

				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(scalarData, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeScalarSampleData

	public RejectedObject writeContainerAlarmHistory(int stationAlarmId,
			AlarmData alarm) {
		String methodName = "[writeContainerAlarmHistory] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (station_alarm_id,status,timestamp_station_alarm)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationAlarmId)).append(",");
			cmd.append(DbUtils.qVal(alarm.getValue())).append(",");
			cmd.append(DbUtils.qValWithMilliseconds(alarm.getTimestamp()));
			cmd.append(")");
			ins = "INSERT INTO station_alarm_history" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" status = " + DbUtils.qVal(alarm.getValue()));
				cmd.append(" WHERE station_alarm_id = "
						+ DbUtils.qVal(stationAlarmId));
				cmd.append(" AND timestamp_station_alarm = "
						+ DbUtils.qValWithMilliseconds(alarm.getTimestamp()));
				update = "UPDATE station_alarm_history SET" + cmd;

				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(alarm, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeContainerAlarmHistory

	public RejectedObject writeAnalyzerStatusHistory(int analyzerId,
			AnalyzerEventDatum anStatus) {
		String methodName = "[writeAnalyzerStatusHistory] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (analyzer_id,analyzer_alarm_type,status,");
			cmd.append("timestamp_analyzer_alarm)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(analyzerId)).append(",");
			cmd.append(DbUtils.qVal(anStatus.getType())).append(",");
			cmd.append(DbUtils.booleanToIntStr(anStatus.getValue()))
					.append(",");
			cmd.append(DbUtils.qValWithMilliseconds(anStatus.getTimestamp()));
			cmd.append(")");
			ins = "INSERT INTO analyzer_alarm_history" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" status = "
						+ DbUtils.booleanToIntStr(anStatus.getValue()));
				cmd.append(" WHERE analyzer_id = " + DbUtils.qVal(analyzerId));
				cmd.append(" AND analyzer_alarm_type = "
						+ DbUtils.qVal(anStatus.getType()));
				cmd.append(" AND timestamp_analyzer_alarm = "
						+ DbUtils.qValWithMilliseconds(anStatus.getTimestamp()));
				update = "UPDATE analyzer_alarm_history SET" + cmd;

				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(anStatus, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeAnalyzerStatusHistory

	public RejectedObject writeGpsData(int stationId, GpsData gpsData) {
		String methodName = "[writeGpsData] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (station_id,timestamp_gps_data,");
			cmd.append("latitude,longitude,altitude,notes)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationId)).append(",");
			cmd.append(DbUtils.qVal(gpsData.getTimestamp())).append(",");
			cmd.append(DbUtils.qVal(gpsData.getLatitude())).append(",");
			cmd.append(DbUtils.qVal(gpsData.getLongitude())).append(",");
			cmd.append(DbUtils.qVal(gpsData.getAltitude())).append(",");
			cmd.append(DbUtils.qVal(gpsData.getComment()));
			cmd.append(")");
			ins = "INSERT INTO gps_data" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" latitude = " + DbUtils.qVal(gpsData.getLatitude()));
				cmd.append(", longitude = "
						+ DbUtils.qVal(gpsData.getLongitude()));
				cmd.append(", altitude = "
						+ DbUtils.qVal(gpsData.getAltitude()));
				cmd.append(", notes = " + DbUtils.qVal(gpsData.getComment()));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(" WHERE station_id = " + DbUtils.qVal(stationId));
				cmd.append(" AND timestamp_gps_data = "
						+ DbUtils.qVal(gpsData.getTimestamp()));
				update = "UPDATE gps_data SET" + cmd;
				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(gpsData, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeGpsData

	public RejectedObject writeInformaticStatusHistory(int stationId,
			StationStatus stationStatus) {
		PerifericoAppStatus appStatus = stationStatus.getAppStatus();
		DiskStatus diskStatus = stationStatus.getDiskStatus();
		String methodName = "[writeInformaticStatusHistory] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (station_id,timestamp_informatic_status,");
			cmd.append("is_ok,board_manager_init_status,cfg_boards_number,");
			cmd.append("init_boards_number,failed_boards_binding_number,");
			cmd.append("dpa_ok,drvcfg_ok,enabled_dpa_number,");
			cmd.append("init_dpa_drivers_number,failed_dpa_threads_number,");
			cmd.append("load_cfg_status,save_cfg_status,cfg_activation_status,");
			cmd.append("total_thread_failures,current_thread_failures,");
			cmd.append("data_write_error_count,warning_level,alarm_level,");
			cmd.append("smart_status,raid_status,cmm_cfg_from_cop,date_in_future)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationId)).append(",");
			cmd.append(DbUtils.qVal(stationStatus.getTimestamp())).append(",");
			cmd.append(DbUtils.booleanToIntStr(appStatus.isOK())).append(",");
			cmd.append(
					DbUtils.booleanObjToIntStr(appStatus
							.getBoardManagerInitStatus())).append(",");
			cmd.append(DbUtils.qVal(appStatus.getConfiguredBoardsNumber()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getInitializedBoardsNumber()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getFailedBoardBindingsNumber()))
					.append(",");
			cmd.append(
					DbUtils.booleanObjToIntStr(appStatus
							.isDataPortAnalyzersOK())).append(",");
			cmd.append(
					DbUtils.booleanObjToIntStr(appStatus.isDriverConfigsOK()))
					.append(",");
			cmd.append(
					DbUtils.qVal(appStatus.getEnabledDataPortAnalyzersNumber()))
					.append(",");
			cmd.append(
					DbUtils.qVal(appStatus
							.getInitializedDataPortDriversNumber()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getFailedDataPortThreadsNumber()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getLoadConfigurationStatus()))
					.append(",");
			cmd.append(
					DbUtils.booleanObjToIntStr(appStatus
							.getSaveNewConfigurationStatus())).append(",");
			cmd.append(
					DbUtils.booleanObjToIntStr(appStatus
							.getConfigActivationStatus())).append(",");
			cmd.append(DbUtils.qVal(appStatus.getTotalThreadFailures()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getCurrentThreadFailures()))
					.append(",");
			cmd.append(DbUtils.qVal(appStatus.getDataWriteErrorCount()))
					.append(",");
			cmd.append(DbUtils.qVal(diskStatus.getSpaceWarnThreshold()))
					.append(",");
			cmd.append(DbUtils.qVal(diskStatus.getSpaceAlarmThreshold()))
					.append(",");
			cmd.append(DbUtils.qVal(diskStatus.getSmartStatus())).append(",");
			cmd.append(DbUtils.qVal(diskStatus.getRaidStatus())).append(",");
			cmd.append(DbUtils.qVal(appStatus.getCommonConfigFromCopStatus()))
					.append(",");
			cmd.append(DbUtils.booleanObjToIntStr(appStatus.isDataInTheFuture()));
			cmd.append(")");
			ins = "INSERT INTO informatic_status_history" + cmd;
			logger.debug(ins);
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" is_ok = "
						+ DbUtils.booleanToIntStr(appStatus.isOK()));
				cmd.append(", board_manager_init_status = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.getBoardManagerInitStatus()));
				cmd.append(", cfg_boards_number = "
						+ DbUtils.qVal(appStatus.getConfiguredBoardsNumber()));
				cmd.append(", init_boards_number = "
						+ DbUtils.qVal(appStatus.getInitializedBoardsNumber()));
				cmd.append(", failed_boards_binding_number = "
						+ DbUtils.qVal(appStatus.getFailedBoardBindingsNumber()));
				cmd.append(", dpa_ok = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.isDataPortAnalyzersOK()));
				cmd.append(", drvcfg_ok = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.isDriverConfigsOK()));
				cmd.append(", enabled_dpa_number = "
						+ DbUtils.qVal(appStatus
								.getEnabledDataPortAnalyzersNumber()));
				cmd.append(", init_dpa_drivers_number = "
						+ DbUtils.qVal(appStatus
								.getInitializedDataPortDriversNumber()));
				cmd.append(", failed_dpa_threads_number = "
						+ DbUtils.qVal(appStatus
								.getFailedDataPortThreadsNumber()));
				cmd.append(", load_cfg_status = "
						+ DbUtils.qVal(appStatus.getLoadConfigurationStatus()));
				cmd.append(", save_cfg_status = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.getSaveNewConfigurationStatus()));
				cmd.append(", cfg_activation_status = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.getConfigActivationStatus()));
				cmd.append(", total_thread_failures = "
						+ DbUtils.qVal(appStatus.getTotalThreadFailures()));
				cmd.append(", current_thread_failures = "
						+ DbUtils.qVal(appStatus.getCurrentThreadFailures()));
				cmd.append(", data_write_error_count = "
						+ DbUtils.qVal(appStatus.getDataWriteErrorCount()));
				cmd.append(", warning_level = "
						+ DbUtils.qVal(diskStatus.getSpaceWarnThreshold()));
				cmd.append(", alarm_level = "
						+ DbUtils.qVal(diskStatus.getSpaceAlarmThreshold()));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(", smart_status = "
						+ DbUtils.qVal(diskStatus.getSmartStatus()));
				cmd.append(", raid_status = "
						+ DbUtils.qVal(diskStatus.getRaidStatus()));
				cmd.append(", cmm_cfg_from_cop = "
						+ DbUtils.qVal(appStatus.getCommonConfigFromCopStatus()));
				cmd.append(", date_in_future = "
						+ DbUtils.booleanObjToIntStr(appStatus
								.isDataInTheFuture()));
				cmd.append(" WHERE station_id = " + DbUtils.qVal(stationId));
				cmd.append(" AND timestamp_informatic_status = "
						+ DbUtils.qVal(stationStatus.getTimestamp()));
				update = "UPDATE informatic_status_history SET" + cmd;
				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(appStatus, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeInformaticStatusHistory

	public RejectedObject writeFilesystemStatusHistory(int stationId,
			Date informaticStatusTimestamp, String filesystemName,
			Integer status) {
		String methodName = "[writeFilesystemStatusHistory] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			Statement stmt = conn.createStatement();
			cmd = new StringBuffer();
			cmd.append(" (station_id,timestamp_informatic_status,");
			cmd.append("filesystem_name,status)");
			cmd.append(" VALUES (");
			cmd.append(DbUtils.qVal(stationId)).append(",");
			cmd.append(DbUtils.qVal(informaticStatusTimestamp)).append(",");
			cmd.append(DbUtils.qVal(filesystemName)).append(",");
			cmd.append(DbUtils.qVal(status));
			cmd.append(")");
			ins = "INSERT INTO filesystem_status_history" + cmd;
			stmt.executeUpdate(ins);
			stmt.close();
		} catch (SQLException insEx) {
			logger.error(methodName + ins, insEx);

			String update = "";
			try {
				Statement stmt = conn.createStatement();
				cmd = new StringBuffer();
				cmd.append(" status = " + DbUtils.qVal(status));
				cmd.append(", update_date = " + DbUtils.CURRENT_TIMESTAMP);
				cmd.append(" WHERE station_id = " + DbUtils.qVal(stationId));
				cmd.append(" AND filesystem_name = "
						+ DbUtils.qVal(filesystemName));
				cmd.append(" AND timestamp_informatic_status = "
						+ DbUtils.qVal(informaticStatusTimestamp));
				update = "UPDATE filesystem_status_history SET" + cmd;
				stmt.executeUpdate(update);
				stmt.close();
			} catch (SQLException updateEx) {
				logger.error(methodName + update, updateEx);

				errObj = new RejectedObject(filesystemName, ins, updateEx);
			}
		}
		return (errObj);
	}// end writeFilesystemStatusHistory

	public RejectedObject writeConfiguration(int stationId,
			long last_modified_time, StringBuilder conf) throws IOException {
		String methodName = "[writeConfiguration] ";
		StringBuffer cmd = null;
		RejectedObject errObj = null;
		String ins = "";
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(conf
					.toString().getBytes());
			cmd = new StringBuffer();
			cmd.append(" (station_id, last_modified_time, configuration) ");
			cmd.append(" VALUES (?, ?, ?)");
			ins = "INSERT INTO station_configuration" + cmd;
			PreparedStatement ps = conn.prepareStatement(ins);
			ps.setInt(1, stationId);
			ps.setLong(2, last_modified_time);
			ps.setBinaryStream(3, bais, conf.toString().getBytes().length);
			ps.executeUpdate();
			ps.close();
			bais.close();
		} catch (SQLException e) {
			logger.error(methodName + ins, e);

			errObj = new RejectedObject(stationId + " " + last_modified_time,
					ins, e);
		}
		return (errObj);

	}// end writeConfiguration

	public RejectedObject deleteStation(Integer oldStationId) {
		String methodName = "[deleteStation] ";
		RejectedObject errObj = null;
		String delete = "";
		try {
			Statement stmt = conn.createStatement();
			delete = "DELETE FROM station WHERE station_id = "
					+ DbUtils.qVal(oldStationId);
			logger.debug(delete);
			stmt.executeUpdate(delete);
			stmt.close();
		} catch (SQLException e) {
			logger.error(methodName + delete, e);
			errObj = new RejectedObject(oldStationId, delete, e);
		}
		return (errObj);

	}

}// end class
