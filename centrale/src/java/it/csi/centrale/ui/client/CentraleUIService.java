/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file:  Define an interface for the service that extends 
* 					RemoteService and lists all RPC methods.
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: CentraleUIService.java,v 1.70 2015/01/19 12:01:05 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;

import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.data.AvgPeriodInfo;
import it.csi.centrale.ui.client.data.ConfigInfoObject;
import it.csi.centrale.ui.client.data.ElementObject;
import it.csi.centrale.ui.client.data.InformaticStatusObject;
import it.csi.centrale.ui.client.data.MeasureUnitInfo;
import it.csi.centrale.ui.client.data.ModemInfo;
import it.csi.centrale.ui.client.data.OtherInfo;
import it.csi.centrale.ui.client.data.ParameterInfo;
import it.csi.centrale.ui.client.data.StInfoObject;
import it.csi.centrale.ui.client.data.StandardInfo;
import it.csi.centrale.ui.client.data.StationMapObject;
import it.csi.centrale.ui.client.data.StationStatusObject;
import it.csi.centrale.ui.client.data.StorageManagerInfo;
import it.csi.centrale.ui.client.data.VirtualCopInfo;
import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.KeyValueObject;

/**
 * 
 * Define an interface for the service that extends RemoteService and lists all
 * RPC methods.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public interface CentraleUIService extends RemoteService {

	Boolean verifyLogin(Boolean localAccess, String username, String password);

	void setLocale(String locale);

	List<StationMapObject> getStMapObjList(boolean includeDeleted, boolean isInConfiguration);

	String saveStMapObjList(List<StationMapObject> stMapObjList) throws FatalException;

	StInfoObject getStationInfo(int stationId);

	Boolean callSingleStation(int stationId);

	Boolean useModem(int stationId);

	Boolean callAllStations(List<StationMapObject> stMapObjList);

	Integer saveStationInfo(boolean isNewStation, StInfoObject connectionInfo, StInfoObject downloadedInfo)
			throws UserParamsException, FatalException;

	String setStationDeleted(Integer stationId, Integer virtualCopId) throws FatalException;

	Boolean verifySameStationFields(StInfoObject stationInfo);

	StInfoObject downloadStationInfo(StInfoObject stationInfo) throws UserParamsException;

	void savePollingStationInfo(int stationId, StInfoObject pollingInfo) throws UserParamsException, FatalException;

	List<StationStatusObject> getStationStatusList();

	StationStatusObject getStationStatus(int stationId);

	ConfigInfoObject getConfigInfo();

	String saveConfigInfo(ConfigInfoObject configInfoObj, boolean isNewCop) throws FatalException;

	Boolean verifySameCopFields(ConfigInfoObject configInfoObj);

	Vector<List<DataObject>> getMeansStationDataFields(int stationId) throws Exception;

	List<AlarmStatusObject> getStationAlarmStatus(int stationId);

	List<AnalyzerAlarmStatusObject> getAnalyzersStatusFields(int stationId);

	InformaticStatusObject getInformaticStatusObject(int stationId) throws FatalException;

	public String[] getCurrentDate();

	String createAndGetChartName(int type, String elementIdStr, String startDate, String endDate, Integer limit,
			String avgPeriod, String title, String legend, String measureUnit, boolean showMinMax, String elementType,
			int numDec, int dirNumDec) throws Exception;

	List<DataObject> getHistoryData(int type, String elementId, String start, String end, Integer limit,
			String avgPeriod, String elementType, int numDec, int dirNumDec) throws Exception;

	Integer countHistorySampleData(String elementId, String start, String end) throws Exception;

	List<AnalyzerAlarmStatusValuesObject> getHistoryAnalyzerAlarm(String alarmType, String analyzerId, String startDate,
			String endDate, int limit) throws Exception;

	List<String> getAlarmType() throws Exception;

	List<AlarmStatusObject> getHistoryStationAlarm(String alarmId, String start, String end, int maxTableData)
			throws Exception;

	void stopVerifyInternalDb();

	List<ElementObject> getWindComponents(int windElementId);

	List<String> getPhysicalDimension() throws FatalException;

	String deletePhysicalDimension(String name) throws FatalException;

	String addPhysicalDimension(String name) throws FatalException;

	List<KeyValueObject> getCommonCfgElementList(Integer type) throws FatalException;

	String deleteParameter(String param_id) throws FatalException;

	ParameterInfo getParameterInfo(String param_id) throws FatalException;

	String insertParameter(ParameterInfo parInfo) throws FatalException;

	String updateParameter(ParameterInfo parInfo) throws FatalException;

	MeasureUnitInfo getMeasureUnitInfo(String measureName) throws FatalException;

	String insertMeasureUnit(MeasureUnitInfo measureInfo) throws FatalException;

	String updateMeasureUnit(MeasureUnitInfo measureInfo) throws FatalException;

	String deleteMeasureUnit(String name) throws FatalException;

	String deleteAlarmName(String alarm_id) throws FatalException;

	AlarmNameInfo getAlarmNameInfo(String alarmId) throws FatalException;

	String insertAlarmName(AlarmNameInfo alarmInfo) throws FatalException;

	String updateAlarmName(AlarmNameInfo alarmInfo) throws FatalException;

	List<AvgPeriodInfo> readAvgPeriod() throws FatalException;

	String deleteAvgPeriod(int avgPeriod) throws FatalException;

	String updateDefaultAvgPeriod(Integer avgPeriodId, boolean defaultAvgPeriod) throws FatalException;

	String addAvgPeriod(String text) throws FatalException;

	StorageManagerInfo readStorageManager() throws FatalException;

	String updateStorageManager(StorageManagerInfo storageManager) throws FatalException;

	StandardInfo readStandard() throws FatalException;

	String updateStandard(StandardInfo standard) throws FatalException;

	OtherInfo readOther() throws FatalException;

	String updateOther(OtherInfo other) throws FatalException;

	List<String> getModemtList() throws FatalException;

	String deleteModem(String deviceId) throws FatalException;

	String updateModem(String deviceId, ModemInfo modemInfo) throws FatalException;

	ModemInfo readModem(String deviceId) throws FatalException;

	String insertModem(ModemInfo modemInfo) throws FatalException;

	Long getLastStationModifiedConfDate(Integer stationId) throws FatalException;

	String getVersion();

	String getName();

	String getMapName();

	List<VirtualCopInfo> getEnabledVirtualCop();
}
