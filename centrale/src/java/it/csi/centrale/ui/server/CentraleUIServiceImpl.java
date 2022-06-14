/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file:   This class implements the server-side code that extends
*         			  RemoteServiceServlet and implements the defined 
*         			  interface.
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: CentraleUIServiceImpl.java,v 1.204 2015/10/15 12:36:34 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.server;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleUtil;
import it.csi.centrale.chart.ChartGenerator;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.config.Database;
import it.csi.centrale.config.DbConfig;
import it.csi.centrale.config.LoginCfg;
import it.csi.centrale.config.Password;
import it.csi.centrale.config.UserSessionInfo;
import it.csi.centrale.data.station.AdvancedPollingConf;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.AnalyzerEventDatum;
import it.csi.centrale.data.station.CommDeviceInfo;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.ContainerAlarm;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.data.station.StationStatus;
import it.csi.centrale.data.station.VirtualCop;
import it.csi.centrale.data.station.WindElement;
import it.csi.centrale.db.DbManagerException;
import it.csi.centrale.db.InternalDbManager;
import it.csi.centrale.polling.PollingStatus;
import it.csi.centrale.ui.client.CentraleUIConstants;
import it.csi.centrale.ui.client.CentraleUIService;
import it.csi.centrale.ui.client.FatalException;
import it.csi.centrale.ui.client.UserParamsException;
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
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.webauth.db.dao.DbAuth;
import it.csi.webauth.db.dao.DbConnParams;
import it.csi.webauth.db.dao.DbToolkit;
import it.csi.webauth.db.model.AmbitoAcl;
import it.csi.webauth.db.model.FunctionFlags;
import it.csi.webauth.db.model.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.servlet.ServletUtilities;

/**
 * This class implements the server-side code that extends RemoteServiceServlet
 * and implements the defined interface.
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class CentraleUIServiceImpl extends RemoteServiceServletWithProxyPatch implements CentraleUIService {

	private PropertyUtilServer propertyUtil;

	private static final long serialVersionUID = 382648489447778696L;

	static final String GENERIC_MAP_NAME = "piemonte";

	public static final int SAMPLE = 1;

	static final int MEANS = 2;

	static final int ANALYZER_ALARM_HISTORY = 3;

	static final int STATION_ALARM_HISTORY = 4;

	private static final String DEFAULT_MAPS_SITE_URL_FORMATTER = "http://maps.google.com/maps?q=%f,%f&hl=%s";

	private SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy");

	public static final Integer PARAMETER = new Integer(23);

	public static final Integer MEASURE_UNIT = new Integer(24);

	public static final Integer ALARM_NAME = new Integer(25);

	private SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");

	private SimpleDateFormat sdfTimestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	SimpleDateFormat sdfNoSecond = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private String[] windComponents = null;

	static Logger logger = Logger.getLogger("uiservice." + CentraleUIServiceImpl.class.getSimpleName());

	public CentraleUIServiceImpl() {

		try {
			propertyUtil = new PropertyUtilServer("it/csi/centrale/ui/server/MessageBundleService");
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}

		windComponents = new String[8];
		windComponents[0] = "vectorial_speed";
		windComponents[1] = "vectorial_direction";
		windComponents[2] = "standard_deviation";
		windComponents[3] = "scalar_speed";
		windComponents[4] = "gust_speed";
		windComponents[5] = "gust_direction";
		windComponents[6] = "calms_number_percent";
		windComponents[7] = "calm";
	}

	/**
	 * 
	 * Returns the current session
	 * 
	 * @return The current Session
	 * 
	 */

	private HttpSession getSession() {

		// Get the current request and then return its session

		return this.getThreadLocalRequest().getSession();

	}

	public void setLocale(String locale) {
		getSession().setAttribute("locale", locale);
	}

	/*
	 * Method for verify login
	 */

	public Boolean verifyLogin(Boolean localAccess, String username, String password) {
		// Load login configuration
		Centrale centraleApp = Centrale.getInstance();
		UserSessionInfo userSessionInfo = null;
		if (localAccess) {
			LoginCfg loginCfg = centraleApp.getLoginCfg();

			for (int i = 0; i < loginCfg.getPasswordList().size(); i++) {
				Password pwd = loginCfg.getPasswordList().get(i);
				if (pwd.getValue().equals(password)) {
					userSessionInfo = new UserSessionInfo();
					userSessionInfo.setLocalAccess(true);
					if (pwd == null || pwd.getType() == null || !pwd.getType().trim().equalsIgnoreCase("readwrite")) {
						userSessionInfo.setRW(false);
					} else
						userSessionInfo.setRW(true);
					getSession().setAttribute("usersessioninfo", userSessionInfo);
					// set unlimited timeout for session
					logger.info("Set unlimited session time out.");
					getSession().setMaxInactiveInterval(-1);
					return new Boolean(true);
				}
			} // end for
			return new Boolean(false);
		} else {
			// verify username and password from dbauth
			try {
				userSessionInfo = getUserSessionInfo(centraleApp.getDbConfig(), username, password);
				if (userSessionInfo != null) {
					getSession().setAttribute("usersessioninfo", userSessionInfo);
					// set unlimited timeout for session
					logger.info("Set unlimited session time out.");
					getSession().setMaxInactiveInterval(-1);
					return new Boolean(true);
				} else
					return new Boolean(false);
			} catch (Exception e) {
				logger.error("Username don't match with password. " + "Retry login.", e);
				e.printStackTrace();
				return new Boolean(false);
			}
		}

	}

	private UserSessionInfo getUserSessionInfo(DbConfig dbConfig, String username, String password) throws Exception {
		logger.info("Load database authentication information..");
		Database databeAuthentication = dbConfig.getDatabaseFromIndex(2);
		DbConnParams dbConnParams = new DbConnParams(databeAuthentication.getAddress(), databeAuthentication.getPort(),
				databeAuthentication.getDbType(), databeAuthentication.getDbName(), databeAuthentication.getUser(),
				databeAuthentication.getPassword());
		logger.info("Register JDBC driver for Postgres");
		DbToolkit.getDbToolkit().registerJDBCDriver(DbToolkit.DB_POSTGRES);
		logger.info("Connect to dbauth");
		DbAuth dbAuth = new DbAuth("Autenticazione Aria Web", dbConnParams);
		dbAuth.connect();
		Utente user = dbAuth.readUser(dbAuth.readUserId(username));

		if (DbAuth.isAuthorizedPasswordForApache(user.getPassword().substring(0, 2), password, user.getPassword())) {
			// se le password sono uguali, utente riconosciuto
			logger.info("Right password for user " + user.getUtente());
			// creo l'oggetto per mantenere in sessione le info su utente
			UserSessionInfo userSessionInfo = new UserSessionInfo();
			// verifico se ha la funzione centrale
			Map<Integer, FunctionFlags> permissionMap = dbAuth.getPermissionMap(user.getUtente(),
					CentraleUIConstants.CENTRALE_FUNCTION);
			if (permissionMap.size() == 0) { // l'utente non ha la funzione
				// centrale
				logger.info("User is not enable to function centrale");
				dbAuth.disconnect();
				return null;
			}
			userSessionInfo.setPermissionMap(permissionMap);
			Integer copIdOggetto = dbAuth.readTypeObjectId(CentraleUIConstants.TIPO_OGGETTO_COP);
			Integer reteIdOggetto = dbAuth.readTypeObjectId(CentraleUIConstants.TIPO_OGGETTO_RETE);

			// verifico se ha ambiti presenti in ACL e carico anche la lista
			// delle reti
			List<AmbitoAcl> ambitiAclList = new ArrayList<AmbitoAcl>();
			List<AmbitoAcl> retiAlcList = new ArrayList<AmbitoAcl>();
			Iterator<Integer> it = permissionMap.keySet().iterator();
			while (it.hasNext()) {
				Integer idAmbito = it.next();
				ambitiAclList.addAll(dbAuth.readDomainAcl(idAmbito, copIdOggetto));
				retiAlcList.addAll(dbAuth.readDomainAcl(idAmbito, reteIdOggetto));
			}
			userSessionInfo.setAmbitiAclList(ambitiAclList);
			logger.info("L'utente " + user.getUtente() + " ha " + userSessionInfo.getAmbitiAclList().size()
					+ " ambiti acl associati come virtual cop.");
			userSessionInfo.setRetiAclList(retiAlcList);
			logger.info("L'utente " + user.getUtente() + " ha " + userSessionInfo.getRetiAclList().size()
					+ " ambiti acl associati come reti.");

			dbAuth.disconnect();
			return userSessionInfo;
		} else {
			// utente non riconosciuto
			logger.info("Bad password for user " + user.getUtente());
			dbAuth.disconnect();
			return null;
		}
	}

	public List<VirtualCopInfo> getEnabledVirtualCop() {
		UserSessionInfo userSessionInfo = ((UserSessionInfo) getSession().getAttribute("usersessioninfo"));
		List<VirtualCopInfo> virtualCopList = new ArrayList<VirtualCopInfo>();
		Centrale centraleApp = Centrale.getInstance();
		ConfigInfo configInfo = centraleApp.getConfigInfo();
		if (userSessionInfo.isLocalAccess() && userSessionInfo.isRW(null)) {
			// l'accesso e' da locale pertanto mostro tutti i cop
			for (int j = 0; j < configInfo.getVirtualCopVector().size(); j++) {
				VirtualCop vCop = configInfo.getVirtualCopVector().elementAt(j);
				VirtualCopInfo vCopInfo = new VirtualCopInfo();
				vCopInfo.setVirtualCopId(vCop.getVirtualCopId());
				vCopInfo.setCopName(vCop.getName());
				virtualCopList.add(vCopInfo);
			} // end for j
		} else {
			// l'acceso e' tramite il dbauth
			for (int i = 0; i < userSessionInfo.getAmbitiAclList().size(); i++) {
				for (int j = 0; j < configInfo.getVirtualCopVector().size(); j++) {
					VirtualCop vCop = configInfo.getVirtualCopVector().elementAt(j);
					if (new Integer(userSessionInfo.getAmbitiAclList().get(i).getIdOggetto())
							.equals(vCop.getVirtualCopId())) {
						VirtualCopInfo vCopInfo = new VirtualCopInfo();
						vCopInfo.setVirtualCopId(vCop.getVirtualCopId());
						vCopInfo.setCopName(vCop.getName());
						if (!virtualCopList.contains(vCopInfo)) {
							virtualCopList.add(vCopInfo);
						}
					}
				} // end for j
			} // end for i
		}
		Collections.sort(virtualCopList, new VirtualCopComparator());
		return virtualCopList;
	}

	public String getMapName() {
		String mapName = null;
		UserSessionInfo userSessionInfo = ((UserSessionInfo) getSession().getAttribute("usersessioninfo"));
		Centrale centraleApp = Centrale.getInstance();
		if (userSessionInfo.isLocalAccess()) {
			// l'accesso e' da locale quindi mostro la mappa del piemonte
			mapName = centraleApp.getConfigInfo().getGenericMapName();
		} else {
			// l'acceso e' tramite il dbauth
			List<AmbitoAcl> ambitiAclList = userSessionInfo.getAmbitiAclList();

			if (ambitiAclList.size() == 1) {
				// l'utente ha un solo ambito e pertanto si mostra la cartina
				// relativa a quell'ambito il cui nome e' salvato nella tabella
				// virtual_cop
				Integer virtulaCopId = new Integer(ambitiAclList.get(0).getIdOggetto());
				Vector<VirtualCop> virtulaCopVector = centraleApp.getConfigInfo().getVirtualCopVector();
				for (int i = 0; i < virtulaCopVector.size(); i++) {
					VirtualCop virtualCop = virtulaCopVector.elementAt(i);
					if (virtualCop.getVirtualCopId().equals(virtulaCopId))
						mapName = virtualCop.getMapName();
				}
			} else {
				// l'utente ha piu' ambiti e pertanto si mostra la cartina
				// generica
				// del piemonte il cui nome e' salvato nella tabella config
				mapName = centraleApp.getConfigInfo().getGenericMapName();
			}
		}
		logger.debug("nome mappa trovata: " + mapName);
		return mapName;
	}

	/*
	 * Methods to manage anagraphic informations
	 */

	public List<StationMapObject> getStMapObjList(boolean includeDeleted, boolean isInConfiguration) {
		List<StationMapObject> stMapObjList = new ArrayList<StationMapObject>();
		Centrale centraleApp = Centrale.getInstance();
		UserSessionInfo userSessionInfo = ((UserSessionInfo) getSession().getAttribute("usersessioninfo"));
		Vector<Station> stVect = null;
		if (isInConfiguration) {
			stVect = userSessionInfo.getWritableStationVector(centraleApp.getConfigInfo());
			logger.debug("in configurazione  ho " + stVect.size() + " stazioni");
		} else {
			stVect = userSessionInfo.getReadableStationVector(centraleApp.getConfigInfo());
			logger.debug("non in configurazione  ho " + stVect.size() + " stazioni");
		}
		Iterator<Station> it_st = stVect.iterator();
		while (it_st.hasNext()) {
			Station station = it_st.next();
			// if station is not deleted
			if (includeDeleted || (!includeDeleted && station.getStInfo().getDeletionDate() == null)) {
				StationMapObject stMapObj = new StationMapObject();
				stMapObj.setVirtualCopId(station.getStInfo().getCopId());
				stMapObj.setStationId(station.getStationId());
				stMapObj.setShortName(station.getStInfo().getShortStationName());
				stMapObj.setLongName(station.getStInfo().getLongStationName());
				stMapObj.setEnabled(station.getStInfo().isEnabled());
				if (!userSessionInfo.isLocalAccess() && userSessionInfo.getAmbitiAclList().size() == 1) {
					// l'utente ha un solo ambito e quindi le coordinate sono
					// quelle proprie della stazione
					stMapObj.setMap_x(station.getStInfo().getMap_x());
					stMapObj.setMap_y(station.getStInfo().getMap_y());
				} else {
					// l'utente ha piu' ambiti e quindi si fanno vedere le
					// coordinate generiche
					stMapObj.setMap_x(station.getStInfo().getGenericMap_x());
					stMapObj.setMap_y(station.getStInfo().getGenericMap_y());
				}

				stMapObj.setStationUuid(station.getStationUUID());
				stMapObj.setIp(station.getStInfo().getCommDevice().getIp());
				stMapObjList.add(stMapObj);
			}
		}
		List<StationMapObject> orderedList = new ArrayList<StationMapObject>();

		List<VirtualCopInfo> vCopInfoList = getEnabledVirtualCop();
		for (int i = 0; i < vCopInfoList.size(); i++) {
			VirtualCopInfo vCopInfo = vCopInfoList.get(i);
			List<StationMapObject> stationToAddList = new ArrayList<StationMapObject>();
			for (int j = 0; j < stMapObjList.size(); j++) {
				// per ogni stazione verifico se appartiene al cop
				StationMapObject stMapObj = stMapObjList.get(j);
				if (vCopInfo.getVirtualCopId().equals(stMapObj.getVirtualCopId()))
					stationToAddList.add(stMapObj);
			} // end for j
			Collections.sort(stationToAddList, new StationMapObjComparator());
			orderedList.addAll(stationToAddList);
		} // end for i

		// Collections.sort(stMapObjList, new StationMapObjComparator());
		return orderedList;
	}

	public String saveStMapObjList(List<StationMapObject> stMapObjList) throws FatalException {
		String errString = null;
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		for (int i = 0; i < stMapObjList.size(); i++) {
			StationMapObject stMapObj = stMapObjList.get(i);
			Iterator<Station> it_st = stVect.iterator();
			while (it_st.hasNext() && errString == null) {
				Station station = it_st.next();
				if (station.getStationId() == stMapObj.getStationId())
					if (station.getStInfo().getDeletionDate() != null) {
						String[] args = new String[1];
						args[0] = stMapObj.getShortName();
						errString = propertyUtil.getLocalizedMessage((String) getSession().getAttribute("locale"),
								"station_already_deleted", args);
					}
					// station is not deleted
					else {
						if (!isUserEnabledToSave(station.getStInfo().getCopId()))
							errString = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
									"user_not_enabled_to_save");
						else {
							InternalDbManager dbManager = null;
							StringBuffer dbError = new StringBuffer();
							try {
								dbManager = new InternalDbManager();
							} catch (SQLException e) {
								logger.error("Error during creating dbManager", e);
								dbError.append(e.getLocalizedMessage());
							}
							UserSessionInfo userSessionInfo = (UserSessionInfo) getSession()
									.getAttribute("usersessioninfo");
							boolean genericMap = false;
							if (userSessionInfo.getAmbitiAclList().size() != 1)
								genericMap = true;
							if (dbManager != null && (stMapObj.getMoved() != null && stMapObj.getMoved())) {
								errString = dbManager.updateStMapPosition(station.getStationId(), stMapObj.getMap_x(),
										stMapObj.getMap_y(), genericMap);
								if (errString == null) {
									if (genericMap) {
										station.getStInfo().setGenericMap_x(stMapObj.getMap_x());
										station.getStInfo().setGenericMap_y(stMapObj.getMap_y());
									} else {
										station.getStInfo().setMap_x(stMapObj.getMap_x());
										station.getStInfo().setMap_y(stMapObj.getMap_y());
									}
									stMapObj.setMoved(null);
								}
								try {
									dbManager.disconnect();
								} catch (SQLException e1) {
									logger.error("Error during disconnect to dbManager", e1);
									dbError.append(e1.getLocalizedMessage());
								}

							} // end if
							if (dbError.length() > 0)
								throw new FatalException("db_error" + " " + dbError.toString());
						}
					}
			}
		}
		return errString;
	}

	public String getVersion() {
		return Centrale.VERSION.toString();
	}

	public String getName() {
		Centrale centraleApp = Centrale.getInstance();
		String copName = "";
		UserSessionInfo userSessionInfo = ((UserSessionInfo) getSession().getAttribute("usersessioninfo"));
		ConfigInfo configInfo = centraleApp.getConfigInfo();
		if (userSessionInfo.getAmbitiAclList().size() == 1) {
			// l'utente ha un solo ambito e quindi si mette il nome dell'ambito
			// preso dalla tabella cop virtuale
			Vector<VirtualCop> virtualCopVector = configInfo.getVirtualCopVector();
			for (int i = 0; i < virtualCopVector.size(); i++) {
				if (virtualCopVector.elementAt(i).getVirtualCopId()
						.equals(new Integer(userSessionInfo.getAmbitiAclList().get(0).getIdOggetto()))) {
					copName = virtualCopVector.elementAt(i).getName();
				}
			}

		} else {
			// l'utente ha piu' ambiti e quindi si mette il nome generico che si
			// trova nella tabella config
			copName = centraleApp.getConfigInfo().getName();
		}
		return copName;
	}

	public ConfigInfoObject getConfigInfo() {
		ConfigInfoObject configInfoObj = null;
		Centrale centraleApp = Centrale.getInstance();
		ConfigInfo configuredConfigInfo = centraleApp.getConfigInfo();
		// case of existing configuration
		if (configuredConfigInfo.getConfigId() != -1) {
			configInfoObj = new ConfigInfoObject();
			configInfoObj.setConfigId(configuredConfigInfo.getConfigId());
			configInfoObj.setPollingOfficeTime(configuredConfigInfo.getPollConfig().getPollingOfficeTime());
			configInfoObj.setPollingExtraOffice(configuredConfigInfo.getPollConfig().getPollingExtraOffice());
			configInfoObj.setUsePollingExtra(configuredConfigInfo.getPollConfig().isUsePollingExtra());
			configInfoObj.setCloseSat(configuredConfigInfo.getPollConfig().isCloseSat());
			configInfoObj.setCloseSun(configuredConfigInfo.getPollConfig().isCloseSun());
			configInfoObj.setOpenAt(configuredConfigInfo.getPollConfig().getOpenAt());
			configInfoObj.setCloseAt(configuredConfigInfo.getPollConfig().getCloseAt());
			configInfoObj.setSampleDataTypeToDownload(configuredConfigInfo.getSampleDataTypeToDownload());
			configInfoObj.setDownloadAlarm(configuredConfigInfo.isDownloadAlarmHistory());
			configInfoObj.setMaxNumLines(configuredConfigInfo.getMaxNumLines());
			configInfoObj.setTotalNumModem(configuredConfigInfo.getTotalNumModem());
			configInfoObj.setNumModemSharedLines(configuredConfigInfo.getNumModemSharedLines());
			configInfoObj.setNumReservedSharedLinesUi(configuredConfigInfo.getNumReservedLinesUi());
			configInfoObj.setRouterTimeout(configuredConfigInfo.getRouterTimeout());
			configInfoObj.setRouterTryTimeout(configuredConfigInfo.getRouterTryTimeout());
			configInfoObj.setCopIp(configuredConfigInfo.getCopIp());
			configInfoObj.setCopRouterIp(configuredConfigInfo.getCopRouter());
			configInfoObj.setTimeHostLan(configuredConfigInfo.getTimeHostLan());
			configInfoObj.setTimeHostModem(configuredConfigInfo.getTimeHostModem());
			configInfoObj.setTimeHostProxy(configuredConfigInfo.getTimeHostProxy());
			configInfoObj.setTimeHostRouter(configuredConfigInfo.getTimeHostRouter());
			configInfoObj.setName(configuredConfigInfo.getName());
			configInfoObj.setReservedLine(configuredConfigInfo.isReservedLine());
			configInfoObj.setMinThreshold(configuredConfigInfo.getMinThreshold());
			configInfoObj.setMaxThreshold(configuredConfigInfo.getMaxThreshold());
			configInfoObj.setAlarmMaxThreshold(configuredConfigInfo.getAlarmMaxThreshold());
			configInfoObj.setSyntheticIcon(configuredConfigInfo.isSyntheticIcon());
			configInfoObj.setGenericMapName(configuredConfigInfo.getGenericMapName());
			configInfoObj.setProxyHost(configuredConfigInfo.getProxyHost());
			configInfoObj.setProxyPort(configuredConfigInfo.getProxyPort());
			configInfoObj.setProxyExclusion(configuredConfigInfo.getProxyExlusion());
		}
		return configInfoObj;
	}

	public StInfoObject getStationInfo(int stationId) {
		Station station = Centrale.getInstance().getStation(stationId);
		return station == null ? null : makeStationInfo(station);
	}

	private StInfoObject makeStationInfo(Station station) {
		StInfoObject stInfo = new StInfoObject();
		stInfo.setStationId(station.getStationId());
		stInfo.setUuid(station.getStationUUID());
		stInfo.setShortName(station.getStInfo().getShortStationName());
		stInfo.setName(station.getStInfo().getLongStationName());
		stInfo.setLocation(station.getStInfo().getLocation());
		stInfo.setAddress(station.getStInfo().getAddress());
		stInfo.setCity(station.getStInfo().getCity());
		stInfo.setProvince(station.getStInfo().getProvince());
		stInfo.setNotes(station.getStInfo().getNotes());
		stInfo.setUseGps(station.getStInfo().hasGps());
		stInfo.setEnabled(station.getStInfo().isEnabled());
		stInfo.setIpAddress(station.getStInfo().getCommDevice().getIp());
		stInfo.setIpPort(station.getStInfo().getCommDevice().getPortNumber());
		stInfo.setPhoneNumber(station.getStInfo().getCommDevice().getPhoneNumber());
		stInfo.setUseModem(station.getStInfo().getCommDevice().useModem());
		stInfo.getVirtualCopInfo().setVirtualCopId(station.getStInfo().getCopId());
		stInfo.getVirtualCopInfo().setCopName(station.getStInfo().getCopName());
		stInfo.setLan(station.getStInfo().getCommDevice().isLan());
		stInfo.setProxy(station.getStInfo().getCommDevice().isProxy());
		stInfo.setRouterIpAddress(station.getStInfo().getCommDevice().getRouterIpAddress());
		stInfo.setForcePollingTime(station.getStInfo().getForcePollingTime());
		stInfo.setMinTimestampForPolling(station.getStInfo().getMinTimestampForPolling());
		stInfo.setDownloadSampleDataEnabled(station.getStInfo().isSampleDataDownloadEnable());
		return stInfo;
	}

	public Long getLastStationModifiedConfDate(Integer stationId) throws FatalException {
		InternalDbManager dbManager = null;
		StringBuffer dbError = new StringBuffer();
		Long lastTime = null;
		try {
			dbManager = new InternalDbManager();
			lastTime = dbManager.getLastStationModifiedConfDate(stationId);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}
		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return lastTime;
	}// end getLastStationModifiedConfDate

	
	public List<AvgPeriodInfo> readAvgPeriod() throws FatalException {
		List<AvgPeriodInfo> avgPeriod = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			avgPeriod = dbManager.readAvgPeriod();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return avgPeriod;
	}// end readAvgPeriod

	public StorageManagerInfo readStorageManager() throws FatalException {
		StorageManagerInfo storageManager = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			storageManager = dbManager.readStorageManager();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return storageManager;
	}// end readStorageManager

	public OtherInfo readOther() throws FatalException {
		OtherInfo other = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			other = dbManager.readOther();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return other;
	}// end readOther

	public StandardInfo readStandard() throws FatalException {
		StandardInfo standard = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			standard = dbManager.readStandard();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return standard;
	}// end readStandard

	public ModemInfo readModem(String deviceId) throws FatalException {
		ModemInfo modem = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			modem = dbManager.readModemInfo(deviceId);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return modem;
	}// end readModem

	/**
	 * Get physical dimension.
	 * 
	 * @return list of physical dimension.
	 * @throws FatalException
	 */
	public List<String> getPhysicalDimension() throws FatalException {
		List<String> physicalDimension = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			physicalDimension = dbManager.readPhysicalDimension();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return physicalDimension;
	}// end getPhysicalDimension

	public List<String> getModemtList() throws FatalException {
		List<String> modemList = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			modemList = dbManager.readModem();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return modemList;
	}// end getModemtList

	public ParameterInfo getParameterInfo(String param_id) throws FatalException {
		ParameterInfo parameterInfo = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			parameterInfo = dbManager.readParameterInfo(param_id);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return parameterInfo;
	}// end getParameterInfo

	public MeasureUnitInfo getMeasureUnitInfo(String measureName) throws FatalException {
		MeasureUnitInfo measureInfo = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			measureInfo = dbManager.readMeasureUnitInfo(measureName);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return measureInfo;
	}// end getMeasureUnitInfo

	public AlarmNameInfo getAlarmNameInfo(String alarmId) throws FatalException {
		AlarmNameInfo alarmInfo = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			alarmInfo = dbManager.readAlarmNameInfo(alarmId);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return alarmInfo;
	}// end getAlarmNameInfo

	public String insertMeasureUnit(MeasureUnitInfo measureInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.insertMeasureUnit(measureInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end insertMeasureUnit

	public String insertAlarmName(AlarmNameInfo alarmInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			Date currentDate = new Date();
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.insertAlarmName(alarmInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end insertAlarmName

	public String updateAlarmName(AlarmNameInfo alarmInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			Date currentDate = new Date();
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateAlarmName(alarmInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());

			Centrale centraleApp = Centrale.getInstance();
			centraleApp.getConfigInfo().setCommonConfigUpdateDate(currentDate);
			Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
			Iterator<Station> it_st = stVect.iterator();
			while (it_st.hasNext()) {
				Station station = it_st.next();
				for (int i = 0; i < station.getContainerAlarm().size(); i++) {
					ContainerAlarm cAlarm = station.getContainerAlarm().get(i);
					if (cAlarm.getAlarmID().equals(alarmInfo.getAlarmId())) {
						cAlarm.setAlarmName(alarmInfo.getAlarmName());
						cAlarm.setType(alarmInfo.getType());
					}
				}
			}

		} // end else
		return errorMsg;
	}// end updateAlarmName

	public String updateMeasureUnit(MeasureUnitInfo measureInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			Date currentDate = new Date();
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateMeasureUnit(measureInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateMeasureUnit

	public String insertParameter(ParameterInfo parInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.insertParameter(parInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end insertParameter

	public String insertModem(ModemInfo modemInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.insertModem(modemInfo);
				Centrale.getInstance().updateModems(dbManager.readModemConf());
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
		} // end else
		return errorMsg;
	}// end insertModem

	public String updateStorageManager(StorageManagerInfo storageManager) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			Date currentDate = new Date();
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateStorageManager(storageManager, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateStorageManager

	public String updateStandard(StandardInfo standard) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateStandard(standard, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateStandard

	public String updateOther(OtherInfo other) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateOther(other, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateOther

	public String updateParameter(ParameterInfo parInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateParameter(parInfo, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateParameter

	public String updateModem(String deviceId, ModemInfo modemInfo) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateModem(deviceId, modemInfo);
				Centrale.getInstance().updateModems(dbManager.readModemConf());
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
		} // end else
		return errorMsg;
	}// end updateModem

	public List<KeyValueObject> getCommonCfgElementList(Integer type) throws FatalException {
		StringBuffer dbError = new StringBuffer();
		List<KeyValueObject> list = null;
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			if (type.equals(ALARM_NAME))
				list = dbManager.readAlarmName();
			else if (type.equals(PARAMETER))
				list = dbManager.readParameter();
			else if (type.equals(MEASURE_UNIT))
				list = dbManager.readMeasureUnit();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return list;
	}// end getCommonCfgElementList

	/**
	 * Delete physical dimension.
	 * 
	 * @param name of physical dimension to delete
	 * @return null if the physical dimension was successfully deleted, error string
	 *         otherwise.
	 * @throws FatalException
	 */
	public String deletePhysicalDimension(String name) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deletePhysicalDimension(name, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		}
		return errorMsg;
	}

	public String deleteAvgPeriod(int avgPeriod) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deleteAvgPeriod(avgPeriod, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end deleteAvgPeriod

	public String deleteParameter(String param_id) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deleteParameter(param_id, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		}
		return errorMsg;
	}// end deleteParameter

	public String deleteModem(String deviceId) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deleteModem(deviceId);
				Centrale.getInstance().updateModems(dbManager.readModemConf());
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
		}
		return errorMsg;
	}// end deleteModem

	public String deleteMeasureUnit(String name) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deleteMeasureUnit(name, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end deleteMeasureUnit

	public String addAvgPeriod(String avgPeriod) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.addAvgPeriod(new Integer(avgPeriod), currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end addAvgPeriod

	public String updateDefaultAvgPeriod(Integer avgPeriodId, boolean defaultAvgPeriod) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.updateDefaultAvgPeriod(avgPeriodId, defaultAvgPeriod, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end updateDefaultAvgPeriod

	public String deleteAlarmName(String alarm_id) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.deleteAlarmName(alarm_id, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		} // end else
		return errorMsg;
	}// end deleteAlarmName

	/**
	 * Add physical dimension.
	 * 
	 * @param name of physical dimension to add
	 * @return null if the physical dimension was successfully added, error string
	 *         otherwise.
	 * @throws FatalException
	 */
	public String addPhysicalDimension(String name) throws FatalException {
		String errorMsg = null;
		StringBuffer dbError = new StringBuffer();
		if (!hasAdvancedFunctionFlag())
			errorMsg = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			InternalDbManager dbManager = null;
			Date currentDate = new Date();
			try {
				dbManager = new InternalDbManager();
				errorMsg = dbManager.addPhysicalDimension(name, currentDate);
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage() + " ");
			}

			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage() + " ");
				}
			} // end if
			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
			Centrale.getInstance().getConfigInfo().setCommonConfigUpdateDate(currentDate);
		}
		return errorMsg;
	}// end addPhysicalDimension

	public Boolean verifySameStationFields(StInfoObject stationInfo) {
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		boolean isSame = true;
		while (it_st.hasNext()) {
			Station currentStation = it_st.next();
			if (currentStation.getStationId() == stationInfo.getStationId()) {
				logger.debug("verifico......");
				logger.debug("stationId: " + stationInfo.getStationId() + " - " + currentStation.getStationId());
				logger.debug("campi di stInfoObj - currentStation:");
				logger.debug("ipAddress:" + stationInfo.getIpAddress() + " - "
						+ currentStation.getStInfo().getCommDevice().getIp());
				logger.debug("virtualCopId:" + stationInfo.getVirtualCopInfo().getVirtualCopId() + " - "
						+ currentStation.getStInfo().getCopId());
				logger.debug("ipPort:" + stationInfo.getIpPort() + " - "
						+ currentStation.getStInfo().getCommDevice().getPortNumber());
				logger.debug("phoneNumber:" + stationInfo.getPhoneNumber() + " - "
						+ currentStation.getStInfo().getCommDevice().getPhoneNumber());
				logger.debug("useModem:" + stationInfo.isUseModem() + " - "
						+ currentStation.getStInfo().getCommDevice().useModem());
				logger.debug("lan:" + stationInfo.isLan() + " - " + currentStation.getStInfo().getCommDevice().isLan());
				logger.debug("proxy:" + stationInfo.isProxy() + " - "
						+ currentStation.getStInfo().getCommDevice().isProxy());
				logger.debug("routerIpAddress:" + stationInfo.getRouterIpAddress() + " - "
						+ currentStation.getStInfo().getCommDevice().getRouterIpAddress());
				logger.debug("enabled:" + stationInfo.isEnabled() + " - " + currentStation.getStInfo().isEnabled());
				logger.debug("forcePollingTime:" + stationInfo.getForcePollingTime() + " - "
						+ currentStation.getStInfo().getForcePollingTime());
				logger.debug("minTimestampForPolling:" + stationInfo.getMinTimestampForPolling() + " - "
						+ currentStation.getStInfo().getMinTimestampForPolling());
				logger.debug("downloadSampleDataEnabled:" + stationInfo.isDownloadSampleDataEnabled() + " - "
						+ currentStation.getStInfo().isSampleDataDownloadEnable());

				// verify copid field
				if (stationInfo.getVirtualCopInfo().getVirtualCopId() == null) {
					if (currentStation.getStInfo().getCopId() != null) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getCopId() == null) {
					if (stationInfo.getVirtualCopInfo().getVirtualCopId() != null) {
						isSame = false;
					}
				} else if (!stationInfo.getVirtualCopInfo().getVirtualCopId()
						.equals(currentStation.getStInfo().getCopId())) {
					isSame = false;
				}

				// verify ipAddress field
				if (stationInfo.getIpAddress() == null) {
					if (currentStation.getStInfo().getCommDevice().getIp() != null
							&& !currentStation.getStInfo().getCommDevice().getIp().trim().equals("")) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getCommDevice().getIp() == null
						&& !stationInfo.getIpAddress().trim().equals("")) {
					isSame = false;
				} else if (currentStation.getStInfo().getCommDevice().getIp() == null
						&& stationInfo.getIpAddress().trim().equals("")) {
					// non deve fare niente perche' sono uguali
				} else if (!stationInfo.getIpAddress().equals(currentStation.getStInfo().getCommDevice().getIp())) {
					isSame = false;
				}

				// verify phoneNumber field
				if (stationInfo.getPhoneNumber() == null) {
					if (currentStation.getStInfo().getCommDevice().getPhoneNumber() != null
							&& !currentStation.getStInfo().getCommDevice().getPhoneNumber().trim().equals("")) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getCommDevice().getPhoneNumber() == null
						&& !stationInfo.getPhoneNumber().trim().equals("")) {
					isSame = false;
				} else if (currentStation.getStInfo().getCommDevice().getPhoneNumber() == null
						&& stationInfo.getPhoneNumber().trim().equals("")) {
					// non deve fare niente perche' sono uguali
				} else if (!stationInfo.getPhoneNumber()
						.equals(currentStation.getStInfo().getCommDevice().getPhoneNumber())) {
					isSame = false;
				}

				// verify routerIpAddress field
				if (stationInfo.getRouterIpAddress() == null) {
					if (currentStation.getStInfo().getCommDevice().getRouterIpAddress() != null
							&& !currentStation.getStInfo().getCommDevice().getRouterIpAddress().trim().equals("")) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getCommDevice().getRouterIpAddress() == null
						&& !stationInfo.getRouterIpAddress().trim().equals("")) {
					isSame = false;
				} else if (currentStation.getStInfo().getCommDevice().getRouterIpAddress() == null
						&& stationInfo.getRouterIpAddress().trim().equals("")) {
					// non deve fare niente perche' sono uguali
				} else if (!stationInfo.getRouterIpAddress()
						.equals(currentStation.getStInfo().getCommDevice().getRouterIpAddress())) {
					isSame = false;
				}
				// verify ipPort field
				if (stationInfo.getIpPort() == null) {
					if (currentStation.getStInfo().getCommDevice().getPortNumber() != null) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getCommDevice().getPortNumber() == null) {
					if (stationInfo.getIpPort() != null) {
						isSame = false;
					}
				} else if (!stationInfo.getIpPort()
						.equals(currentStation.getStInfo().getCommDevice().getPortNumber())) {
					isSame = false;
				}

				// verify forcePollingTime field
				if (stationInfo.getForcePollingTime() == null) {
					if (currentStation.getStInfo().getForcePollingTime() != null) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getForcePollingTime() == null) {
					if (stationInfo.getForcePollingTime() != null) {
						isSame = false;
					}
				} else if (!stationInfo.getForcePollingTime()
						.equals(currentStation.getStInfo().getForcePollingTime())) {
					isSame = false;
				}

				// verify minTimestampForPolling field
				if (stationInfo.getMinTimestampForPolling() == null) {
					if (currentStation.getStInfo().getMinTimestampForPolling() != null) {
						isSame = false;
					}
				} else if (currentStation.getStInfo().getMinTimestampForPolling() == null) {
					if (stationInfo != null) {
						isSame = false;
					}
				} else if (!stationInfo.getMinTimestampForPolling()
						.equals(currentStation.getStInfo().getMinTimestampForPolling())) {
					isSame = false;
				}

				if (stationInfo.isUseModem() != currentStation.getStInfo().getCommDevice().useModem()
						|| stationInfo.isLan() != currentStation.getStInfo().getCommDevice().isLan()
						|| stationInfo.isProxy() != currentStation.getStInfo().getCommDevice().isProxy()
						|| stationInfo.isEnabled() != currentStation.getStInfo().isEnabled()
						|| stationInfo.isDownloadSampleDataEnabled() != currentStation.getStInfo()
								.isSampleDataDownloadEnable()) {
					isSame = false;
				}
			}

		} // end while

		return isSame;
	}// end verifySameStationFields

	public Boolean verifySameCopFields(ConfigInfoObject configInfoObj) {
		Boolean resultValue = null;
		Centrale centraleApp = Centrale.getInstance();
		ConfigInfo configInfo = centraleApp.getConfigInfo();
		logger.debug("campi di configInfoObj:");
		logger.debug("configInfoObj.name: " + configInfoObj.getName());
		logger.debug("configInfoObj.pollingOfficeTime:" + configInfoObj.getPollingOfficeTime());
		logger.debug("configInfoObj.usePollingExtra:" + configInfoObj.isUsePollingExtra());
		logger.debug("configInfoObj.sampleDataTypeToDownload:" + configInfoObj.getSampleDataTypeToDownload());
		logger.debug("configInfoObj.downloadAlarm:" + configInfoObj.isDownloadAlarm());
		logger.debug("configInfoObj.maxNumLines:" + configInfoObj.getMaxNumLines());
		logger.debug("configInfoObj.totalNumModem:" + configInfoObj.getTotalNumModem());
		logger.debug(" configInfoObj.numModemSharedLines:" + configInfoObj.getNumModemSharedLines());
		logger.debug(" configInfoObj.numReservedLinesUi:" + configInfoObj.getNumReservedSharedLinesUi());
		logger.debug("configInfoObj.routerTimeout:" + configInfoObj.getRouterTimeout());
		logger.debug("configInfoObj.routerTryTimeout:" + configInfoObj.getRouterTryTimeout());
		logger.debug("configInfoObj.copIp:" + configInfoObj.getCopIp());
		logger.debug("configInfoObj.copRouterIp:" + configInfoObj.getCopRouterIp());
		logger.debug("configInfoObj.reservedLine:" + configInfoObj.isReservedLine());
		logger.debug("configInfoObj.openAt:" + configInfoObj.getOpenAt());
		logger.debug("configInfoObj.closeAt:" + configInfoObj.getCloseAt());
		logger.debug("configInfoObj.closeSat:" + configInfoObj.isCloseSat());
		logger.debug("configInfoObj.closeSun:" + configInfoObj.isCloseSun());
		logger.debug("configInfoObj.pollingExtraOffice:" + configInfoObj.getPollingExtraOffice());
		logger.debug("configInfoObj.minThreshold:" + configInfoObj.getMinThreshold());
		logger.debug("configInfoObj.maxThreshold:" + configInfoObj.getMaxThreshold());
		logger.debug("configInfoObj.alarmMaxThreshold:" + configInfoObj.getAlarmMaxThreshold());
		logger.debug("configInfoObj.proxyHost:" + configInfoObj.getProxyHost());
		logger.debug("configInfoObj.proxyPort:" + configInfoObj.getProxyPort());
		logger.debug("configInfoObj.proxyExclusion:" + configInfoObj.getProxyExclusion());

		logger.debug("campi di configInfo:");
		logger.debug("configInfo.getPollConfig(): " + configInfo.getName());
		logger.debug("configInfo.getPollConfig().getPollingOfficeTime():"
				+ configInfo.getPollConfig().getPollingOfficeTime());
		logger.debug(
				"configInfo.getPollConfig().isUsePollingExtra():" + configInfo.getPollConfig().isUsePollingExtra());
		logger.debug("configInfo.getSampleDataTypeToDownload():" + configInfo.getSampleDataTypeToDownload());
		logger.debug("configInfo.isDownloadAlarmHistory():" + configInfo.isDownloadAlarmHistory());
		logger.debug("configInfo.getMaxNumLines():" + configInfo.getMaxNumLines());
		logger.debug("configInfo.getTotalNumModem():" + configInfo.getTotalNumModem());
		logger.debug("configInfo.getNumModemSharedLines():" + configInfo.getNumModemSharedLines());
		logger.debug("configInfo.getNumReservedLinesUi():" + configInfo.getNumReservedLinesUi());
		logger.debug("configInfo.getRouterTimeout():" + configInfo.getRouterTimeout());
		logger.debug("configInfo.getRouterTryTimeout():" + configInfo.getRouterTryTimeout());
		logger.debug("configInfo.getCopIp():" + configInfo.getCopIp());
		logger.debug("configInfo.getCopRouterIp():" + configInfo.getCopRouter());
		logger.debug("configInfo.isReservedLine():" + configInfo.isReservedLine());
		logger.debug("configInfo.getPollConfig().getOpenAt():" + configInfo.getPollConfig().getOpenAt());
		logger.debug("configInfo.getPollConfig().getCloseAt():" + configInfo.getPollConfig().getCloseAt());
		logger.debug("configInfo.getPollConfig().isCloseSat():" + configInfo.getPollConfig().isCloseSat());
		logger.debug("configInfo.getPollConfig().isCloseSun():" + configInfo.getPollConfig().isCloseSun());
		logger.debug("configInfo.getPollConfig().getPollingExtraOffice():"
				+ configInfo.getPollConfig().getPollingExtraOffice());
		logger.debug("configInfo.getMinThreshold():" + configInfo.getMinThreshold());
		logger.debug("configInfo.getMaxThreshold():" + configInfo.getMaxThreshold());
		logger.debug("configInfo.getAlarmMaxThreshold():" + configInfo.getAlarmMaxThreshold());
		logger.debug("configInfo.getProxyHost():" + configInfo.getProxyHost());
		logger.debug("configInfo.getProxyPort():" + configInfo.getProxyPort());
		logger.debug("configInfo.getProxyExlusion():" + configInfo.getProxyExlusion());
		if (configInfoObj.getPollingOfficeTime() != configInfo.getPollConfig().getPollingOfficeTime()
				|| configInfoObj.isUsePollingExtra() != configInfo.getPollConfig().isUsePollingExtra()
				|| configInfoObj.getSampleDataTypeToDownload() != configInfo.getSampleDataTypeToDownload()
				|| configInfoObj.isDownloadAlarm() != configInfo.isDownloadAlarmHistory()
				|| configInfoObj.getMaxNumLines() != configInfo.getMaxNumLines()
				|| configInfoObj.getTotalNumModem() != configInfo.getTotalNumModem()
				|| configInfoObj.getNumModemSharedLines() != configInfo.getNumModemSharedLines()
				|| configInfoObj.getNumReservedSharedLinesUi() != configInfo.getNumReservedLinesUi()
				|| configInfoObj.getRouterTimeout() != configInfo.getRouterTimeout()
				|| configInfoObj.getRouterTryTimeout() != configInfo.getRouterTryTimeout()
				|| !configInfoObj.getCopIp().equals(configInfo.getCopIp())
				|| (configInfoObj.getCopRouterIp() == null && configInfo.getCopRouter() != null)
				|| (configInfoObj.getCopRouterIp() != null
						&& !configInfoObj.getCopRouterIp().equals(configInfo.getCopRouter()))
				|| configInfoObj.isReservedLine() != configInfo.isReservedLine()
				|| (configInfoObj.getOpenAt() == null && configInfo.getPollConfig().getOpenAt() != null)
				|| (configInfoObj.getOpenAt() != null
						&& !configInfoObj.getOpenAt().equals(configInfo.getPollConfig().getOpenAt()))
				|| (configInfoObj.getCloseAt() == null && configInfo.getPollConfig().getCloseAt() != null)
				|| (configInfoObj.getCloseAt() != null
						&& !configInfoObj.getCloseAt().equals(configInfo.getPollConfig().getCloseAt()))
				|| configInfoObj.isCloseSat() != configInfo.getPollConfig().isCloseSat()
				|| configInfoObj.isCloseSun() != configInfo.getPollConfig().isCloseSun()
				|| (configInfoObj.getPollingExtraOffice() == null
						&& configInfo.getPollConfig().getPollingExtraOffice() != null)
				|| (configInfoObj.getPollingExtraOffice() != null && !configInfoObj.getPollingExtraOffice()
						.equals(configInfo.getPollConfig().getPollingExtraOffice()))
				|| (configInfoObj.getMinThreshold() == null && configInfo.getMinThreshold() != null)
				|| (configInfoObj.getMinThreshold() != null
						&& !configInfoObj.getMinThreshold().equals(configInfo.getMinThreshold()))
				|| (configInfoObj.getMaxThreshold() == null && configInfo.getMaxThreshold() != null)
				|| (configInfoObj.getMaxThreshold() != null
						&& !configInfoObj.getMaxThreshold().equals(configInfo.getMaxThreshold()))
				|| (configInfoObj.getAlarmMaxThreshold() == null && configInfo.getAlarmMaxThreshold() != null)
				|| (configInfoObj.getProxyHost() == null && configInfo.getProxyHost() != null)
				|| (configInfoObj.getProxyPort() == null && configInfo.getProxyPort() != null)
				|| (configInfoObj.getProxyExclusion() == null && configInfo.getProxyExlusion() != null)
				|| (configInfoObj.getName() == null && configInfo.getName() != null)
				|| (configInfoObj.getName() != null && !configInfoObj.getName().equals(configInfo.getName()))
				|| (configInfoObj.getAlarmMaxThreshold() != null
						&& !configInfoObj.getAlarmMaxThreshold().equals(configInfo.getAlarmMaxThreshold())))
			resultValue = new Boolean(false);
		else
			resultValue = new Boolean(true);
		return resultValue;
	} // end verifySameCopFields

	public String saveConfigInfo(ConfigInfoObject configInfoObj, boolean isNewCop) throws FatalException {
		String errString = null;
		if (!hasAdvancedFunctionFlag())
			errString = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			// TODO verificare se e' necessario mettere qualche controllo sul
			// numero di linee riservate all'ui
			if (configInfoObj.getNumModemSharedLines() > configInfoObj.getTotalNumModem())
				errString = propertyUtil.getProperty((String) getSession().getAttribute("locale"), "modem_tot_>");
			else if (configInfoObj.getMaxNumLines() < configInfoObj.getNumModemSharedLines())
				errString = propertyUtil.getProperty((String) getSession().getAttribute("locale"), "max_line_>");
			else {
				Centrale centraleApp = Centrale.getInstance();
				ConfigInfo configInfo = centraleApp.getConfigInfo();
				ConfigInfo modifiedConfigInfo = new ConfigInfo();
				if (!isNewCop)
					modifiedConfigInfo.setConfigId(configInfo.getConfigId());
				else
					modifiedConfigInfo.setConfigId(Centrale.CONFIG_ID);
				AdvancedPollingConf pollConfig = new AdvancedPollingConf();
				modifiedConfigInfo.setPollConfig(pollConfig);
				modifiedConfigInfo.getPollConfig().setPollingOfficeTime(configInfoObj.getPollingOfficeTime());
				modifiedConfigInfo.getPollConfig().setUsePollingExtra(configInfoObj.isUsePollingExtra());
				modifiedConfigInfo.setSampleDataTypeToDownload(configInfoObj.getSampleDataTypeToDownload());
				modifiedConfigInfo.setDownloadAlarmHistory(configInfoObj.isDownloadAlarm());
				modifiedConfigInfo.setMaxNumLines(configInfoObj.getMaxNumLines());
				modifiedConfigInfo.setTotalNumModem(configInfoObj.getTotalNumModem());
				modifiedConfigInfo.setNumModemSharedLines(configInfoObj.getNumModemSharedLines());
				modifiedConfigInfo.setNumReservedLinesUi(configInfoObj.getNumReservedSharedLinesUi());
				modifiedConfigInfo.setRouterTimeout(configInfoObj.getRouterTimeout());
				modifiedConfigInfo.setRouterTryTimeout(configInfoObj.getRouterTryTimeout());
				modifiedConfigInfo.setCopIp(configInfoObj.getCopIp());
				modifiedConfigInfo.setName(configInfoObj.getName());
				modifiedConfigInfo.setCopRouter(configInfoObj.getCopRouterIp());
				modifiedConfigInfo.setTimeHostLan(configInfoObj.getTimeHostLan());
				modifiedConfigInfo.setTimeHostProxy(configInfoObj.getTimeHostProxy());
				modifiedConfigInfo.setTimeHostRouter(configInfoObj.getTimeHostRouter());
				modifiedConfigInfo.setTimeHostModem(configInfoObj.getTimeHostModem());
				modifiedConfigInfo.setReservedLine(configInfoObj.isReservedLine());
				modifiedConfigInfo.getPollConfig().setOpenAt(configInfoObj.getOpenAt());
				modifiedConfigInfo.getPollConfig().setCloseAt(configInfoObj.getCloseAt());
				modifiedConfigInfo.getPollConfig().setCloseSat(configInfoObj.isCloseSat());
				modifiedConfigInfo.getPollConfig().setCloseSun(configInfoObj.isCloseSun());
				modifiedConfigInfo.getPollConfig().setPollingExtraOffice(configInfoObj.getPollingExtraOffice());
				modifiedConfigInfo.setMinThreshold(configInfoObj.getMinThreshold());
				modifiedConfigInfo.setMaxThreshold(configInfoObj.getMaxThreshold());
				modifiedConfigInfo.setAlarmMaxThreshold(configInfoObj.getAlarmMaxThreshold());
				modifiedConfigInfo.setGenericMapName(configInfoObj.getGenericMapName());
				modifiedConfigInfo.setProxyHost(configInfoObj.getProxyHost());
				modifiedConfigInfo.setProxyPort(configInfoObj.getProxyPort());
				modifiedConfigInfo.setProxyExlusion(configInfoObj.getProxyExclusion());

				InternalDbManager dbManager = null;
				StringBuffer dbError = new StringBuffer();
				try {
					dbManager = new InternalDbManager();
				} catch (SQLException e) {
					logger.error("Error", e);
					dbError.append(e.getLocalizedMessage());
				}
				if (dbManager != null) {
					errString = dbManager.writeConfigInfo(modifiedConfigInfo, isNewCop);
					if (errString == null) {
						modifiedConfigInfo.setStVector(configInfo.getStVector());
						modifiedConfigInfo.setVirtualCopVector(configInfo.getVirtualCopVector());
						modifiedConfigInfo.setAnalyzerStatusType(configInfo.getAnalyzerStatusType());
						centraleApp.setConfigInfo(modifiedConfigInfo);
					}
					try {
						dbManager.disconnect();
					} catch (SQLException e1) {
						logger.error("Error", e1);
						dbError.append(e1.getLocalizedMessage());
					}

				} // end if
				if (dbError.length() > 0)
					throw new FatalException("db_error" + " " + dbError.toString());
			}
		}

		return errString;
	} // end saveConfigInfo

	// isNewStation: is true for new stations only
	// connectionInfo: contains connection information, stationId, virtualCopId
	// plus shortName for new stations when downloadedInfo is not available
	// downloadedInfo: is null in case of communication failure
	public Integer saveStationInfo(boolean isNewStation, StInfoObject connectionInfo, StInfoObject downloadedInfo)
			throws UserParamsException, FatalException {
		Centrale centraleApp = Centrale.getInstance();
		// TODO verificare questo messaggio cop not existing......
		if (centraleApp.getConfigInfo().getConfigId() == -1)
			throw new UserParamsException(localize("cop_not_existing"));
		if (!isUserEnabledToSave(connectionInfo.getVirtualCopInfo().getVirtualCopId()))
			throw new UserParamsException(localize("user_not_enabled_to_save"));
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Station station = null;
		if (isNewStation) {
			station = new Station();
			StationInfo si = new StationInfo();
			if (downloadedInfo == null)
				si.setShortStationName(connectionInfo.getShortName());
			si.setEnabled(true);
			si.setCommDevice(new CommDeviceInfo());
			station.setStInfo(si);
		} else {
			for (Station st : stVect) {
				if (st.getStationId() == connectionInfo.getStationId()) {
					station = st;
					break;
				}
			}
			if (station == null)
				throw new UserParamsException(localize("station_not_found"));
		}
		StationInfo si = station.getStInfo();
		if (si.getCopId() != null && !si.getCopId().equals(connectionInfo.getVirtualCopInfo().getVirtualCopId()))
			throw new UserParamsException(localize("cop_id_mismatch"));
		si.setCopId(connectionInfo.getVirtualCopInfo().getVirtualCopId());
		si.setCopName(connectionInfo.getVirtualCopInfo().getCopName());
		si.getCommDevice().setIp(connectionInfo.getIpAddress());
		si.getCommDevice().setPortNumber(connectionInfo.getIpPort());
		si.getCommDevice().setPhoneNumber(connectionInfo.getPhoneNumber());
		si.getCommDevice().setModem(connectionInfo.isUseModem());
		si.getCommDevice().setLan(connectionInfo.isLan());
		si.getCommDevice().setProxy(connectionInfo.isProxy());
		si.getCommDevice().setRouterIpAddress(connectionInfo.getRouterIpAddress());
		if (downloadedInfo != null) {
			if (station.getStationUUID() != null && !station.getStationUUID().equals(downloadedInfo.getUuid()))
				throw new UserParamsException(localize("station_uuid_mismatch"));
			station.setStationUUID(downloadedInfo.getUuid());
			if (si.getUuid() != null && !si.getUuid().equals(downloadedInfo.getUuid()))
				throw new UserParamsException(localize("station_uuid_mismatch"));
			si.setUuid(downloadedInfo.getUuid());
			si.setShortStationName(downloadedInfo.getShortName());
			si.setLongStationName(downloadedInfo.getName());
			si.setLocation(downloadedInfo.getLocation());
			si.setAddress(downloadedInfo.getAddress());
			si.setCity(downloadedInfo.getCity());
			si.setProvince(downloadedInfo.getProvince());
			si.setNotes(downloadedInfo.getNotes());
			si.setGps(downloadedInfo.isUseGps());
			if (isNewStation)
				for (Station st : stVect)
					if (CentraleUtil.areEqual(st.getStationUUID(), downloadedInfo.getUuid()) && CentraleUtil
							.areEqual(st.getStInfo().getCopId(), connectionInfo.getVirtualCopInfo().getVirtualCopId()))
						throw new UserParamsException(localize("same_station_uuid"));
		}
		InternalDbManager dbManager;
		try {
			dbManager = new InternalDbManager();
		} catch (SQLException e) {
			logger.error("Error", e);
			throw new FatalException("db_error" + " " + e.getLocalizedMessage());
		}
		try {
			Integer stationId;
			if (isNewStation) {
				stationId = dbManager.writeStConnInfo(si);
				station.setStationId(stationId);
				StationStatus stStatus = new StationStatus();
				station.setStStatus(stStatus);
				stVect.add(station);
			} else {
				stationId = station.getStationId();
				dbManager.updateStConnInfo(stationId, si, downloadedInfo != null);
			}
			return stationId;
		} catch (DbManagerException e) {
			throw new UserParamsException(e.getMessage());
		} finally {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("DB disconnect failed", e1);
			}
		}
	}// end saveStationInfo

	public void savePollingStationInfo(int stationId, StInfoObject pollingInfo)
			throws UserParamsException, FatalException {
		Centrale centraleApp = Centrale.getInstance();
		if (centraleApp.getConfigInfo().getConfigId() == -1)
			throw new UserParamsException(localize("cop_not_existing"));
		if (!isUserEnabledToSave(pollingInfo.getVirtualCopInfo().getVirtualCopId()))
			throw new UserParamsException(localize("user_not_enabled_to_save"));
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Station station = null;
		for (Station st : stVect) {
			if (st.getStationId() == stationId) {
				station = st;
				break;
			}
		}
		if (station == null)
			throw new UserParamsException(localize("station_not_found"));
		StationInfo si = station.getStInfo();
		si.setEnabled(pollingInfo.isEnabled());
		si.setForcePollingTime(pollingInfo.getForcePollingTime());
		si.setMinTimestampForPolling(pollingInfo.getMinTimestampForPolling());
		si.setSampleDataDownloadEnable(pollingInfo.isDownloadSampleDataEnabled());
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			String[] result = dbManager.writePollingInfo(station, false, centraleApp.getConfigInfo().getConfigId());
			if (result[0] != null)
				throw new UserParamsException(result[0]);
		} catch (SQLException e) {
			logger.error("Error", e);
			throw new FatalException("db_error" + " " + e.getLocalizedMessage());
		} finally {
			if (dbManager != null) {
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("DB disconnect failed", e1);
				}
			}
		}
	} // end savePollingStationInfo

	public String setStationDeleted(Integer stationId, Integer virtualCopId) throws FatalException {
		String errString = null;
		StringBuffer dbError = new StringBuffer();
		if (!isUserEnabledToSave(virtualCopId))
			errString = propertyUtil.getProperty((String) getSession().getAttribute("locale"),
					"user_not_enabled_to_save");
		else {
			Centrale centraleApp = Centrale.getInstance();
			Date currentDate = new Date();

			InternalDbManager dbManager = null;
			try {
				dbManager = new InternalDbManager();
			} catch (SQLException e) {
				logger.error("Error", e);
				dbError.append(e.getLocalizedMessage());
			}
			if (dbManager != null) {
				errString = dbManager.setStationDeleted(stationId, currentDate);
				boolean found = false;
				if (errString == null) {
					Iterator<Station> it_st = centraleApp.getConfigInfo().getStVector().iterator();
					while (it_st.hasNext() && !found) {
						Station currentStation = it_st.next();
						if (currentStation.getStationId() == stationId) {
							currentStation.getStInfo().setDeletionDate(currentDate);
							found = true;
							// centraleApp.removeProxy(new
							// Integer(currentStation
							// .getStationId()).toString());
						}
					}
				}
				try {
					dbManager.disconnect();
				} catch (SQLException e1) {
					logger.error("Error", e1);
					dbError.append(e1.getLocalizedMessage());
				}
			} // end if

			if (dbError.length() > 0)
				throw new FatalException("db_error" + " " + dbError.toString());
		} // end else
		return errString;
	}

	/*
	 * Methods to use polling functions
	 */
	public Boolean callSingleStation(int stationId) {
		return Centrale.getInstance().pollStation(stationId);
	}

	public Boolean callAllStations(List<StationMapObject> stMapObjList) {
		Boolean resultValue = null;
		for (int i = 0; i < stMapObjList.size(); i++) {
			// se l'operazione precedente era true lo sovrascrivo, se era false
			// mantengo il risultato che qualcosa e' fallito
			if (resultValue == null || resultValue)
				resultValue = callSingleStation(stMapObjList.get(i).getStationId());
		} // end for

		return resultValue;
	}

	public Boolean useModem(int stationId) {

		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Station stationFound = null;
		int i = 0;
		for (i = 0; i < stVect.size() && stationFound == null; i++) {
			Station station = stVect.get(i);
			if (station.getStationId() == stationId)
				stationFound = station;
		}
		if (stationFound != null) {
			CommDeviceInfo commDeviceInfo = stationFound.getStInfo().getCommDevice();
			return commDeviceInfo.useModem();
		} else {
			return null;
		}
	}

	@Override
	public StInfoObject downloadStationInfo(StInfoObject stInfoObj) throws UserParamsException {
		try {
			Centrale centraleApp = Centrale.getInstance();
			CommDeviceInfo cdi = new CommDeviceInfo();
			cdi.setIp(stInfoObj.getIpAddress());
			cdi.setPortNumber(stInfoObj.getIpPort());
			cdi.setPhoneNumber(stInfoObj.getPhoneNumber());
			cdi.setModem(stInfoObj.isUseModem());
			cdi.setLan(stInfoObj.isLan());
			cdi.setProxy(stInfoObj.isProxy());
			cdi.setRouterIpAddress(stInfoObj.getRouterIpAddress());
			StationInfo stationInfo = centraleApp.readStationInfo(cdi);
			StInfoObject stInfoResult = new StInfoObject();
			stInfoResult.setStationId(stInfoObj.getStationId());
			stInfoResult.setUuid(stationInfo.getUuid());
			stInfoResult.setShortName(stationInfo.getShortStationName());
			stInfoResult.setName(stationInfo.getLongStationName());
			stInfoResult.setLocation(stationInfo.getLocation());
			stInfoResult.setAddress(stationInfo.getAddress());
			stInfoResult.setCity(stationInfo.getCity());
			stInfoResult.setProvince(stationInfo.getProvince());
			stInfoResult.setNotes(stationInfo.getNotes());
			stInfoResult.setUseGps(stationInfo.hasGps());
			stInfoResult.setEnabled(stationInfo.isEnabled());
			stInfoResult.setIpAddress(cdi.getIp());
			stInfoResult.setIpPort(cdi.getPortNumber());
			stInfoResult.setPhoneNumber(cdi.getPhoneNumber());
			stInfoResult.setUseModem(cdi.useModem());
			stInfoResult.setLan(cdi.isLan());
			stInfoResult.setProxy(cdi.isProxy());
			stInfoResult.setRouterIpAddress(cdi.getRouterIpAddress());
			stInfoResult.setForcePollingTime(stationInfo.getForcePollingTime());
			stInfoResult.setMinTimestampForPolling(stationInfo.getMinTimestampForPolling());
			stInfoResult.setDownloadSampleDataEnabled(stationInfo.isSampleDataDownloadEnable());
			stInfoResult.getVirtualCopInfo().setVirtualCopId(stInfoObj.getVirtualCopInfo().getVirtualCopId());
			InternalDbManager dbManager = new InternalDbManager();
			stInfoResult.getVirtualCopInfo()
					.setCopName(dbManager.getCopName(stInfoObj.getVirtualCopInfo().getVirtualCopId()));

			return stInfoResult;
		} catch (DeviceBusyException e) {
			logger.debug("Cannot download station information", e);
			throw new UserParamsException("Dispositivo di comunicazione " + "occupato, riprovare più tardi");
		} catch (ConnectionException e) {
			logger.debug("Cannot download station information", e);
			throw new UserParamsException("Errore di comunicazione con la " + "stazione '" + e.getMessage() + "'");
		} catch (Exception e) {
			logger.error("Cannot download station information", e);
			throw new UserParamsException("Impossibile chiamare la stazione '" + e.getMessage() + "'");
		}
	}

	public List<StationStatusObject> getStationStatusList() {
		List<StationStatusObject> stStatusObjList = new ArrayList<StationStatusObject>();
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		while (it_st.hasNext()) {
			Station station = it_st.next();
			// if station is not deleted
			if (station.getStInfo().getDeletionDate() == null) {
				// da togliere quando e' tutto a posto
				/*
				 * if (station.getStationId() == 33) logger.debug("*data:" + new Date() +
				 * "**********progress:" + stationStatus.getPollingProgress());
				 */
				stStatusObjList.add(makeStationStatusObj(station));
			}
		}

		return stStatusObjList;
	} // end getStationStatusList

	public StationStatusObject getStationStatus(int stationId) {
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		while (it_st.hasNext()) {
			Station station = it_st.next();
			if (station.getStationId() == stationId && station.getStInfo().getDeletionDate() == null) {
				return makeStationStatusObj(station);
			}
		}
		return new StationStatusObject();
	} // end getStationStatus

	private StationStatusObject makeStationStatusObj(Station station) {
		StationStatusObject stStatusObj = new StationStatusObject();
		stStatusObj.setStationId(station.getStationId());
		stStatusObj.setShortName(station.getStInfo().getShortStationName());
		stStatusObj.setEnabled(station.getStInfo().isEnabled());
		StationStatus stationStatus = station.getStStatus();
		stStatusObj.setLastCorrectPollingDate(station.getLastSuccessfulPollingDate());
		stStatusObj.setLastPollingDate(station.getLastPollingDate());
		stStatusObj.setLastTemperatureValue(stationStatus.getTemperature());
		// logger.debug("********stationStatus.getTemperature() : "
		// + stationStatus.getTemperature());
		// logger.debug("********stStatusObj.lastTemperatureValue : "
		// + stStatusObj.lastTemperatureValue);
		// logger.debug("******la stazione " + stStatusObj.shortName
		// + " da fill isduring polling: " + stStatusObj.isDuringPolling);
		// logger.debug("*****stationStatus.getPollingProgress() "
		// + stationStatus.getPollingProgress());
		PollingStatus ps = station.getPollingStatus();
		stStatusObj.setDuringPolling(
				ps == PollingStatus.WAITING || ps == PollingStatus.CONNECTING || ps == PollingStatus.RUNNING);
		stStatusObj.setCommunicationStatus(
				setCommunicationStatus(station.getPollingStatus(), station.getConnectionFailure()));

		if (stationStatus.getInformaticStatus() == null)
			stStatusObj.setInformaticStatus(CentraleUIConstants.UNKNOWN);
		else {
			if (stationStatus.getInformaticStatus().equals(CentraleUIConstants.INFORMATIC_STATUS_ALARM))
				stStatusObj.setInformaticStatus(CentraleUIConstants.ALARM);
			else if (stationStatus.getInformaticStatus().equals(CentraleUIConstants.INFORMATIC_STATUS_WARNING))
				stStatusObj.setInformaticStatus(CentraleUIConstants.WARNING);
			else
				stStatusObj.setInformaticStatus(CentraleUIConstants.OK);
		}
		if (stationStatus.isStationAlarm() == null && stationStatus.isInstrumentInAlarm() == null)
			stStatusObj.setStationStatus(CentraleUIConstants.UNKNOWN);
		else {
			if ((stationStatus.isStationAlarm() != null && stationStatus.isStationAlarm().booleanValue() == true)
					|| (stationStatus.isInstrumentInAlarm() != null
							&& stationStatus.isInstrumentInAlarm().booleanValue() == true))
				stStatusObj.setStationStatus(CentraleUIConstants.ALARM);
			else
				stStatusObj.setStationStatus(CentraleUIConstants.OK);
		}
		stStatusObj.setClockStatus(CentraleUIConstants.UNKNOWN);
		if (stationStatus.getSync() == 1)
			stStatusObj.setClockStatus(CentraleUIConstants.OK);
		else if (stationStatus.getSync() == 2)
			stStatusObj.setClockStatus(CentraleUIConstants.WARNING);
		else if (stationStatus.getSync() == 3)
			stStatusObj.setClockStatus(CentraleUIConstants.ALARM);
		if (stationStatus.isDoorOpen() == null)
			stStatusObj.setDoorStatus(CentraleUIConstants.UNKNOWN);
		else if (stationStatus.isDoorOpen().booleanValue())
			stStatusObj.setDoorStatus(CentraleUIConstants.ALARM);
		else
			stStatusObj.setDoorStatus(CentraleUIConstants.OK);
		// stStatusObj.printObjToStdOut();

		return stStatusObj;
	} // end fillStationStatusObjFields

	private int setCommunicationStatus(PollingStatus ps, Failure f) {
		switch (ps) {
		case WAITING:
			return CentraleUIConstants.WAIT;
		case CONNECTING:
			return CentraleUIConstants.IS_CALLING;
		case CONNECT_ERROR:
			switch (f) {
			case LOCAL_DEVICE:
				return CentraleUIConstants.ROUTER_LOCALE_NOT_RESPONING;
			case REMOTE_DEVICE:
				return CentraleUIConstants.ROUTER_REMOTE_NOT_RESPONING;
			case REMOTE_HOST:
				return CentraleUIConstants.PC_NOT_RESPONDING;
			default:
				// TODO: inventare un'icona che rappresenti un errore nel
				// centrale
				return CentraleUIConstants.SW_NOT_RESPONDING;
			}
		case RUNNING:
			return CentraleUIConstants.SW_RESPONDING;
		case OK:
			return CentraleUIConstants.POLLING_FINISHED_OK;
		case IO_ERROR:
			return CentraleUIConstants.COMMUNICATION_LOST;
		case SOFTWARE_NOT_RESPONDING:
			return CentraleUIConstants.SW_NOT_RESPONDING;
		case PROTOCOL_ERROR:
			return CentraleUIConstants.PROTOCOL_ERROR;
		case POLLING_ERROR:
			return CentraleUIConstants.PROTOCOL_ERROR;
		case UNEXPECTED_ERROR:
			// TODO: inventare un'icona che rappresenti un errore nel
			// centrale
			return CentraleUIConstants.UNEXPECTED_ERROR;
		case NONE:
		default:
			return CentraleUIConstants.UNKNOWN;
		}

	}

	public List<AlarmStatusObject> getStationAlarmStatus(int stationId) {
		List<AlarmStatusObject> alarmStatusList = new ArrayList<AlarmStatusObject>();
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		boolean founded = false;
		while (it_st.hasNext() && !founded) {
			Station station = it_st.next();
			if (station.getStationId() == stationId && station.getStInfo().getDeletionDate() == null) {
				founded = true;
				if (station.getContainerAlarm() != null) {
					for (int i = 0; i < station.getContainerAlarm().size(); i++) {
						ContainerAlarm cAlarm = station.getContainerAlarm().get(i);
						if (cAlarm.getDeletionDate() == null) {
							AlarmStatusObject alarmStObj = new AlarmStatusObject();
							alarmStObj.setAlarmId(cAlarm.getAlarmID());
							alarmStObj.setAlarmDbId(cAlarm.getAlarmDbID());
							alarmStObj.setNotes(cAlarm.getNotes());
							alarmStObj.setAlarmDescription(cAlarm.getAlarmName());
							if (cAlarm.getLastStatus() != null) {
								alarmStObj.setStatus(cAlarm.getLastStatus().getValue());
								alarmStObj.setTimestamp(cAlarm.getLastStatus().getTimestamp());
							}
							alarmStatusList.add(alarmStObj);
						}
					}
				}
			}
		}

		return alarmStatusList;
	} // end getStationAlarmStatus

	public List<AnalyzerAlarmStatusObject> getAnalyzersStatusFields(int stationId) {
		List<AnalyzerAlarmStatusObject> analyzersAlarmStatusList = new ArrayList<AnalyzerAlarmStatusObject>();
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		boolean founded = false;
		while (it_st.hasNext() && !founded) {
			Station station = it_st.next();
			if (station.getStationId() == stationId && station.getStInfo().getDeletionDate() == null) {
				founded = true;
				for (int i = 0; i < station.getAnalyzersVect().size(); i++) {
					Analyzer analyzer = station.getAnalyzersVect().get(i);
					if (analyzer.getDeletionDate() == null) {
						Vector<AnalyzerEventDatum> anStatusVect = analyzer.getAnStatus();
						AnalyzerAlarmStatusObject analyzerAlarmObj = new AnalyzerAlarmStatusObject();
						analyzerAlarmObj.setAnalyzerId(analyzer.getAnalyzerId());
						analyzerAlarmObj.setAnalyzerName(analyzer.getName());
						analyzerAlarmObj
								.setBrandModel((analyzer.getBrand() == null ? "" : analyzer.getBrand())
										+ (((analyzer.getBrand() == null || analyzer.getBrand().equals(""))
												&& (analyzer.getModel() == null || analyzer.getModel().equals(""))) ? ""
														: " - ")
										+ (analyzer.getModel() == null ? "" : analyzer.getModel()));
						analyzerAlarmObj.setAnAlarmValues(new ArrayList<AnalyzerAlarmStatusValuesObject>());
						for (int j = 0; j < anStatusVect.size(); j++) {
							AnalyzerEventDatum anStatus = anStatusVect.get(j);
							AnalyzerAlarmStatusValuesObject anAlarmValues = new AnalyzerAlarmStatusValuesObject();
							anAlarmValues.setValue(anStatus.getValue());
							anAlarmValues.setType(anStatus.getType());
							anAlarmValues.setTimestamp(anStatus.getTimestamp());
							analyzerAlarmObj.getAnAlarmValues().add(anAlarmValues);
						}
						analyzersAlarmStatusList.add(analyzerAlarmObj);
					}
				}
			}
		}

		return analyzersAlarmStatusList;
	} // end getAnalyzersStatusFields

	public List<ElementObject> getWindComponents(int windElementId) {
		List<ElementObject> windComponents = new ArrayList<ElementObject>();
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		boolean founded = false;
		while (it_st.hasNext() && !founded) {
			Station station = it_st.next();
			if (station.getStInfo().getDeletionDate() == null) {
				for (int i = 0; i < station.getAnalyzersVect().size() && !founded; i++) {
					Analyzer analyzer = station.getAnalyzersVect().get(i);
					if (analyzer.getDeletionDate() == null) {
						for (int j = 0; j < analyzer.getElements().size() && !founded; j++) {
							GenericElement element = analyzer.getElements().get(j);
							if (element.getDeletionDate() == null && element.getElementId() == windElementId
									&& element instanceof WindElement) {
								founded = true;
								ScalarElement speedElement = ((WindElement) (element)).getSpeed();
								ElementObject elementObj = new ElementObject();
								elementObj.setElementId(speedElement.getElementId());
								elementObj.setElementName(speedElement.getName());
								elementObj.setMeasureUnit(speedElement.getUnit());
								windComponents.add(elementObj);
								ScalarElement dirElement = ((WindElement) (element)).getDirection();
								elementObj = new ElementObject();
								elementObj.setElementId(dirElement.getElementId());
								elementObj.setElementName(dirElement.getName());
								elementObj.setMeasureUnit(dirElement.getUnit());
								windComponents.add(elementObj);
							}
						}
					}
				}
			}
		}
		return windComponents;
	}

	public List<String> getAlarmType() throws FatalException {
		List<String> alarmType = null;
		StringBuffer dbError = new StringBuffer();
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			alarmType = dbManager.getAlarmType();
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return alarmType;

	}// end getAlarmType

	/*
	 * Methods to manage data
	 */
	public InformaticStatusObject getInformaticStatusObject(int stationId) throws FatalException {
		InformaticStatusObject informaticStatusObj = null;
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		boolean found = false;
		while (it_st.hasNext() && !found) {
			Station station = it_st.next();
			if (station.getStationId() == stationId && station.getStInfo().getDeletionDate() == null) {
				found = true;
				InternalDbManager dbManager = null;
				StringBuffer dbError = new StringBuffer();
				try {
					dbManager = new InternalDbManager();
					informaticStatusObj = dbManager.getInformaticStatusObject(stationId);
					informaticStatusObj.setUseGps(station.getStInfo().hasGps());
					String url = dbManager.getMapsSiteUrlFormatter();
					if (url == null)
						url = DEFAULT_MAPS_SITE_URL_FORMATTER;
					if (informaticStatusObj.getGpsLatitude() != null && informaticStatusObj.getGpsLongitude() != null) {
						informaticStatusObj.setLinkToGoogleMaps(String.format(Locale.US, url,
								informaticStatusObj.getGpsLatitude(), informaticStatusObj.getGpsLongitude(),
								(String) getSession().getAttribute("locale")));
					}
					// TODO: rifare in base ai nuovi oggetti...
					informaticStatusObj.setCommunicationStatus(
							setCommunicationStatus(station.getPollingStatus(), station.getConnectionFailure()));
					informaticStatusObj.setLastCorrectPollingTime(station.getLastSuccessfulPollingDate());

				} catch (SQLException e) {
					logger.error("Error", e);
					dbError.append(e.getLocalizedMessage() + " ");
				}

				if (dbManager != null) {
					try {
						dbManager.disconnect();
					} catch (SQLException e1) {
						logger.error("Error", e1);
						dbError.append(e1.getLocalizedMessage() + " ");
					}
				} // end if
				if (dbError.length() > 0)
					throw new FatalException("db_error" + " " + dbError.toString());
			} // end if
		} // end while

		return informaticStatusObj;
	} // end getInformaticStatusObject

	public Vector<List<DataObject>> getMeansStationDataFields(int stationId) throws FatalException {
		Centrale centraleApp = Centrale.getInstance();
		Vector<Station> stVect = centraleApp.getConfigInfo().getStVector();
		Iterator<Station> it_st = stVect.iterator();
		Station stationReq = null;
		StringBuffer dbError = new StringBuffer();
		while (it_st.hasNext() && stationReq == null) {
			Station station = it_st.next();
			if (station.getStationId() == stationId)
				stationReq = station;
		}
		Vector<List<DataObject>> dataListVector = null;
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			dataListVector = dbManager.readMeansData(stationReq);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		for (int r = 0; r < dataListVector.size(); r++) {
			List<DataObject> dataList = dataListVector.get(r);
			for (int i = 0; i < dataList.size(); i++) {
				DataObject dObj = dataList.get(i);
				try {
					dObj.setTitle(ValidationFlag.getMultipleFlagsTitle(dObj.getMultipleFlag(),
							(String) (getSession().getAttribute("locale"))));
				} catch (IOException e) {
					logger.error("Error", e);
				}
			}
			Collections.sort(dataList, new DataObjectComparator());
		}
		return dataListVector;
	}

	public Integer countHistorySampleData(String elementId, String start, String end)
			throws UserParamsException, FatalException {

		// conto i dati
		return new Integer(countNumData(start, end, elementId));

	}// end countHistorySampleData

	public List<DataObject> getHistoryData(int type, String elementId, String start, String end, Integer limit,
			String avgPeriod, String elementType, int numDec, int dirNumDec)
			throws UserParamsException, FatalException {
		// leggo i dati
		List<DataObject> dataList = new ArrayList<DataObject>();
		readData(type, start, end, dataList, elementId, limit, avgPeriod, elementType);
		for (int i = 0; i < dataList.size(); i++) {
			DataObject dObj = dataList.get(i);
			try {
				dObj.setTitle(ValidationFlag.getMultipleFlagsTitle(dObj.getMultipleFlag(),
						(String) (getSession().getAttribute("locale"))));
			} catch (IOException e) {
				logger.error("Error", e);
			}
			dObj.setNumDec(numDec);
			if (dObj instanceof WindDataObject)
				((WindDataObject) dObj).setDirNumDec(dirNumDec);
		}
		return dataList;

	}// end getHistoryData

	public List<AnalyzerAlarmStatusValuesObject> getHistoryAnalyzerAlarm(String alarmType, String analyzerId,
			String start, String end, int limit) throws UserParamsException, FatalException {
		List<AnalyzerAlarmStatusValuesObject> alarmData = new ArrayList<AnalyzerAlarmStatusValuesObject>();
		readData(start, end, alarmData, analyzerId, alarmType, limit);
		return alarmData;
	}// end getHistoryAnalyzerAlarm

	public List<AlarmStatusObject> getHistoryStationAlarm(String alarmId, String start, String end, int limit)
			throws UserParamsException, FatalException {
		List<AlarmStatusObject> alarmStatus = new ArrayList<AlarmStatusObject>();
		readData(start, end, alarmStatus, limit, alarmId);
		return alarmStatus;
	}// end getHistoryStationAlarm

	/**
	 * Get current date
	 * 
	 * @return string that represents date
	 */
	public String[] getCurrentDate() {
		String[] resStrings = new String[2];
		resStrings[0] = sdfDay.format(new Date());
		resStrings[1] = sdfHour.format(new Date());
		return resStrings;
	}

	/*
	 * Method to stop Centrale
	 */
	public void stopVerifyInternalDb() {
		logger.error("stopVerifyInternalDb");
		Centrale.getInstance().stopVerifyInternalDb("CentraleUIServiceImpl");
	}

	private void readData(String start, String end, List<AlarmStatusObject> listAlarm, Integer limit, String alarmId)
			throws UserParamsException, FatalException {
		readData(STATION_ALARM_HISTORY, start, end, null, null, listAlarm, alarmId, limit, null, null);

	}

	private void readData(String start, String end, List<AnalyzerAlarmStatusValuesObject> listanalyzer,
			String analyzerId, String alarmType, Integer limit) throws UserParamsException, FatalException {
		readData(ANALYZER_ALARM_HISTORY, start, end, null, listanalyzer, null, analyzerId, limit, null, alarmType);
	}

	private void readData(int type, String start, String end, List<DataObject> listDataObj, String elementId,
			Integer limit, String avgPeriod, String elementType) throws UserParamsException, FatalException {
		readData(type, start, end, listDataObj, null, null, elementId, limit, avgPeriod, elementType);
	}

	private int countNumData(String start, String end, String elementId) throws UserParamsException, FatalException {
		Date[] retDate = verifyDate(start, end);
		InternalDbManager dbManager = null;
		StringBuffer dbError = new StringBuffer();
		int numData = 0;
		try {
			dbManager = new InternalDbManager();
			numData = dbManager.countSampleData(new Integer(elementId).intValue(), retDate[0], retDate[1]);
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
		return numData;
	}

	private void readData(int type, String startDate, String endDate, List<DataObject> dataList,
			List<AnalyzerAlarmStatusValuesObject> alarmDataList, List<AlarmStatusObject> statusObj, String id,
			Integer limit, String avgPeriod, String alarmType) throws UserParamsException, FatalException {
		Date[] retDate = verifyDate(startDate, endDate);
		StringBuffer dbError = new StringBuffer();
		// connect to db
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			boolean overLimit = false;
			if (type == SAMPLE) {
				overLimit = dbManager.readSampleData(new Integer(id).intValue(), retDate[0], retDate[1], dataList,
						limit);
			}
			if (type == MEANS)
				overLimit = dbManager.readMeansData(new Integer(id).intValue(), new Integer(avgPeriod).intValue(),
						retDate[0], retDate[1], dataList, limit, alarmType);
			if (type == ANALYZER_ALARM_HISTORY)
				overLimit = dbManager.readAnalyzerAlarmStatusHistory(new Integer(id).intValue(), retDate[0], retDate[1],
						alarmDataList, limit, alarmType);
			if (type == STATION_ALARM_HISTORY)
				overLimit = dbManager.readStationAlarmStatusHistory(new Integer(id).intValue(), retDate[0], retDate[1],
						statusObj, limit);
			if (overLimit) {
				if (type == SAMPLE) {
					// bisogna presentare di nuovo la pagina in modo che si
					// possano selezionare i minuti
					dataList.clear();
				} else
					throw new UserParamsException(
							propertyUtil.getProperty((String) getSession().getAttribute("locale"), "error_limit"));
			}
		} catch (SQLException e) {
			logger.error("Error", e);
			dbError.append(e.getLocalizedMessage() + " ");
		}

		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error", e1);
				dbError.append(e1.getLocalizedMessage() + " ");
			}
		} // end if
		if (dbError.length() > 0)
			throw new FatalException("db_error" + " " + dbError.toString());
	}// end readData

	private Date[] verifyDate(String startDate, String endDate) throws UserParamsException {
		/*
		 * retDate:
		 * 
		 * [0] : startDate
		 * 
		 * [1] : end Date
		 */
		Date[] retDate = new Date[2];
		try {
			retDate[0] = sdfTimestamp.parse(startDate);
		} catch (ParseException e2) {
			logger.error("Error", e2);
			throw new UserParamsException(
					propertyUtil.getProperty((String) getSession().getAttribute("locale"), "error_start_date"));
		}
		try {
			retDate[1] = sdfTimestamp.parse(endDate);
		} catch (ParseException e2) {
			logger.error("Error", e2);
			throw new UserParamsException(
					propertyUtil.getProperty((String) getSession().getAttribute("locale"), "error_end_date"));
		}
		if (retDate[0].after(retDate[1]))
			throw new UserParamsException(
					propertyUtil.getProperty((String) getSession().getAttribute("locale"), "error_after_date"));
		return retDate;
	}// end verifyDate

	public String createAndGetChartName(int type, String elementIdStr, String startDate, String endDate, Integer limit,
			String avgPeriod, String title, String legend, String measureUnit, boolean showMinMax, String elementType,
			int numDec, int dirNumDec) throws UserParamsException, FatalException {
		String chartName = null;
		boolean interpolated = true;

		List<DataObject> dataList = new ArrayList<DataObject>();
		readData(type, startDate, endDate, dataList, elementIdStr, limit, avgPeriod, elementType);
		for (int i = 0; i < dataList.size(); i++) {
			DataObject dObj = dataList.get(i);
			dObj.setNumDec(numDec);
			if (dObj instanceof WindDataObject)
				((WindDataObject) dObj).setDirNumDec(dirNumDec);
		}

		chartName = legend;

		ChartGenerator chartGenerator = new ChartGenerator(((String) getSession().getAttribute("locale")));
		chartGenerator.prepareParameter(dataList, elementIdStr, showMinMax);

		JFreeChart chart = chartGenerator.generateChart(title, legend, measureUnit, interpolated, type);

		/*
		 * Save the chart as an '980px x 460px' jpeg image.
		 */
		HttpSession session = getThreadLocalRequest().getSession();
		try {
			chartName = ServletUtilities.saveChartAsJPEG(chart, 980, 460, null, session);
		} catch (IOException e) {
			throw new UserParamsException(
					propertyUtil.getProperty((String) getSession().getAttribute("locale"), "not_available"));
		}

		/*
		 * } else { throw new UserParamsException(propertyUtil.getProperty( (String)
		 * getSession().getAttribute("locale"), "no_data")); }
		 */

		return chartName;
	} // end createAndGetChartNameOfMeansData

	private boolean isUserEnabledToSave(Integer virtualCopId) {
		UserSessionInfo userSessionInfo = (UserSessionInfo) getSession().getAttribute("usersessioninfo");
		if (userSessionInfo.isLocalAccess()) {
			if (userSessionInfo.isRW(null))
				return true;
			else
				return false;
		} else {

			return ((UserSessionInfo) getSession().getAttribute("usersessioninfo")).isRW(virtualCopId);
		}
	}

	private boolean hasAdvancedFunctionFlag() {
		UserSessionInfo userSessionInfo = (UserSessionInfo) getSession().getAttribute("usersessioninfo");
		if (userSessionInfo.isLocalAccess()) {
			// l'accesso e' da lcoale
			if (userSessionInfo.isRW(null))
				return true;
			else
				return false;

		} else {
			return ((UserSessionInfo) getSession().getAttribute("usersessioninfo")).hasAdvancedFlags();
		}
	}

	private String localize(String id) {
		return propertyUtil.getProperty((String) getSession().getAttribute("locale"), id);
	}

}// end class
