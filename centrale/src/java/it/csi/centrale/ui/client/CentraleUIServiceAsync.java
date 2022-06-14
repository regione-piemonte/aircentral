/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: Define asynchronous interface to the service to be called 
* 					from the client-side code.
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: CentraleUIServiceAsync.java,v 1.67 2015/01/19 12:01:05 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

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

/***
 * Define asynchronous interface to the service to be called from the
 * client-side code.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public interface CentraleUIServiceAsync {

	void verifyLogin(Boolean localAccess, String username, String password, AsyncCallback<Boolean> callback);

	void setLocale(String locale, AsyncCallback<Object> callback);

	void getStMapObjList(boolean includeDeleted, boolean isInConfiguration,
			AsyncCallback<List<StationMapObject>> callback);

	void saveStMapObjList(List<StationMapObject> stMapObjList, AsyncCallback<String> callback);

	void getStationInfo(int stationId, AsyncCallback<StInfoObject> callback);

	void callSingleStation(int stationId, AsyncCallback<Boolean> callback);

	void useModem(int stationId, AsyncCallback<Boolean> callback);

	void callAllStations(List<StationMapObject> stMapObjList, AsyncCallback<Boolean> callback);

	void saveStationInfo(boolean isNewStation, StInfoObject connectionInfo, StInfoObject downloadedInfo,
			AsyncCallback<Integer> callback);

	void setStationDeleted(Integer stationId, Integer virtualCopId, AsyncCallback<String> callback);

	void verifySameStationFields(StInfoObject stationInfo, AsyncCallback<Boolean> callback);

	void downloadStationInfo(StInfoObject stationInfo, AsyncCallback<StInfoObject> callback);

	void savePollingStationInfo(int stationId, StInfoObject pollingInfo, AsyncCallback<Object> callback);

	void getStationStatusList(AsyncCallback<List<StationStatusObject>> callback);

	void getStationStatus(int stationId, AsyncCallback<StationStatusObject> callback);

	void getConfigInfo(AsyncCallback<ConfigInfoObject> callback);

	void saveConfigInfo(ConfigInfoObject configInfoObj, boolean isNewCop, AsyncCallback<String> callback);

	void verifySameCopFields(ConfigInfoObject configInfoObj, AsyncCallback<Boolean> callback);

	void getMeansStationDataFields(int stationId, AsyncCallback<Vector<List<DataObject>>> callback);

	void getInformaticStatusObject(int stationId, AsyncCallback<InformaticStatusObject> callback);

	void getStationAlarmStatus(int stationId, AsyncCallback<List<AlarmStatusObject>> callback);

	void getAnalyzersStatusFields(int stationId, AsyncCallback<List<AnalyzerAlarmStatusObject>> callback);

	void getCurrentDate(AsyncCallback<String[]> callback);

	void createAndGetChartName(int type, String elementIdStr, String startDate, String endDate, Integer limit,
			String avgPeriod, String title, String legend, String measureUnit, boolean showMinMax, String elementType,
			int numDec, int dirNumDec, AsyncCallback<String> callback);

	void getHistoryData(int type, String elementId, String start, String end, Integer limit, String avgPeriod,
			String elementType, int numDec, int dirNumDec, AsyncCallback<List<DataObject>> callback);

	void countHistorySampleData(String elementId, String start, String end, AsyncCallback<Integer> callback);

	void getHistoryAnalyzerAlarm(String alarmType, String analyzerId, String startDate, String endDate, int limit,
			AsyncCallback<List<AnalyzerAlarmStatusValuesObject>> callback);

	void getAlarmType(AsyncCallback<List<String>> callback);

	void getHistoryStationAlarm(String alarmId, String start, String end, int maxTableData,
			AsyncCallback<List<AlarmStatusObject>> callback);

	void stopVerifyInternalDb(AsyncCallback<Object> callback);

	void getWindComponents(int windElementId, AsyncCallback<List<ElementObject>> callback);

	void getPhysicalDimension(AsyncCallback<List<String>> callback);

	void deletePhysicalDimension(String name, AsyncCallback<String> callback2);

	void addPhysicalDimension(String text, AsyncCallback<String> callback);

	void getCommonCfgElementList(Integer type, AsyncCallback<List<KeyValueObject>> callback);

	void deleteParameter(String param_id, AsyncCallback<String> callback2);

	void getParameterInfo(String param_id, AsyncCallback<ParameterInfo> callback);

	void insertParameter(ParameterInfo parInfo, AsyncCallback<String> callback);

	void updateParameter(ParameterInfo parInfo, AsyncCallback<String> callback);

	void getMeasureUnitInfo(String measureName, AsyncCallback<MeasureUnitInfo> callback2);

	void insertMeasureUnit(MeasureUnitInfo measureInfo, AsyncCallback<String> callback);

	void updateMeasureUnit(MeasureUnitInfo measureInfo, AsyncCallback<String> callback);

	void deleteMeasureUnit(String name, AsyncCallback<String> callback2);

	void deleteAlarmName(String alarm_id, AsyncCallback<String> callback2);

	void getAlarmNameInfo(String alarmId, AsyncCallback<AlarmNameInfo> callback2);

	void insertAlarmName(AlarmNameInfo alarmInfo, AsyncCallback<String> callback);

	void updateAlarmName(AlarmNameInfo alarmInfo, AsyncCallback<String> callback);

	void readAvgPeriod(AsyncCallback<List<AvgPeriodInfo>> callback);

	void deleteAvgPeriod(int avgPeriod, AsyncCallback<String> callback2);

	void updateDefaultAvgPeriod(Integer avgPeriodId, boolean defaultAvgPeriod, AsyncCallback<String> callback2);

	void addAvgPeriod(String text, AsyncCallback<String> callback);

	void readStorageManager(AsyncCallback<StorageManagerInfo> callback2);

	void updateStorageManager(StorageManagerInfo storageManager, AsyncCallback<String> callback);

	void readStandard(AsyncCallback<StandardInfo> callback3);

	void updateStandard(StandardInfo standard, AsyncCallback<String> callback);

	void readOther(AsyncCallback<OtherInfo> callback4);

	void updateOther(OtherInfo other, AsyncCallback<String> callback);

	void getModemtList(AsyncCallback<List<String>> callback);

	void deleteModem(String deviceId, AsyncCallback<String> callback2);

	void updateModem(String deviceId, ModemInfo modemInfo, AsyncCallback<String> callback);

	void readModem(String deviceId, AsyncCallback<ModemInfo> callback3);

	void insertModem(ModemInfo modemInfo, AsyncCallback<String> callback);

	void getLastStationModifiedConfDate(Integer stationId, AsyncCallback<Long> callback);

	void getVersion(AsyncCallback<String> callback);

	void getName(AsyncCallback<String> getCopName);

	void getMapName(AsyncCallback<String> callback);

	void getEnabledVirtualCop(AsyncCallback<List<VirtualCopInfo>> callback);

}
