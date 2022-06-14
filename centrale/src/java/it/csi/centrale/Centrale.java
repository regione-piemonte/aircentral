/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: starts Centrale application
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: Centrale.java,v 1.98 2015/10/22 13:54:51 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

import it.csi.centrale.comm.CommManager;
import it.csi.centrale.comm.CommManagerException;
import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.DeviceNotFoundException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.PhoneLine;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.device.IsdnRouter;
import it.csi.centrale.comm.device.Lan;
import it.csi.centrale.comm.device.Modem;
import it.csi.centrale.comm.device.Proxy;
import it.csi.centrale.comm.device.Wan;
import it.csi.centrale.config.ConfigManager;
import it.csi.centrale.config.Database;
import it.csi.centrale.config.DbConfig;
import it.csi.centrale.config.LoginCfg;
import it.csi.centrale.data.station.AdvancedPollingConf;
import it.csi.centrale.data.station.CommDeviceInfo;
import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.InsertableObject;
import it.csi.centrale.data.station.ModemConf;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.data.station.StationInfo;
import it.csi.centrale.db.CentraleDb;
import it.csi.centrale.db.CentralePoolingDataSource;
import it.csi.centrale.db.CheckDb;
import it.csi.centrale.db.DataWriterThread;
import it.csi.centrale.db.InternalDbManager;
import it.csi.centrale.polling.PerifericoProtocol;
import it.csi.centrale.polling.ProtocolException;
import it.csi.centrale.polling.Scheduler;
import it.csi.centrale.polling.data.StationConfig;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Starts Centrale application
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class Centrale extends Thread implements Thread.UncaughtExceptionHandler {

	public static final Version VERSION = new Version(3, 3, 5);

	private static final String THREAD_NAME = "centrale";

	private static String regexp = null;

	public static final int CONFIG_ID = 1;

	private static final int CENTRALE_PORT = 55000;

	public static final int DEFAULT_PERIFERICO_PORT = 55000;

	public static final int MAX_AVG_PERIOD = 1440;


	public static final Date MIN_START_TIMESTAMP = new Date(0);

	public enum InitStatus {
		STARTED, STARTED_WITH_ERRORS, NOT_STARTED
	}

	private static final int RESTART_COUNT_RESET_PERIOD_M = 60; // minutes

	private static final int MAX_RESTARTS_ALLOWED = 3;

	public static final int DB_ARIA = 0;

	// Define a static logger variable
	private static Logger logger;

	private static Centrale centraleApp = null;

	private static Date minimumUpdateDateForDataTransferToDbAria = null;

	private static Date min_timestamp = new Date();

	private InitStatus initStatus = InitStatus.NOT_STARTED;

	private CentraleStatus status;

	private ConfigManager configManager;

	private CentraleDb centraleDb;

	private Database matchedDataBase;

	private volatile ConfigInfo configInfo;

	private volatile Vector<ModemConf> modemConfigs;

	private Server jettyServer = null;

	private WebAppContext webAppContext = null;

	private int coreThreadsExceptionCount = 0;

	private int restartCount = 0;

	private long lastRestartTime = 0;

	private volatile Scheduler scheduler;

	private ConcurrentLinkedQueue<InsertableObject> dataQueue;

	private ConcurrentLinkedQueue<InsertableObject> matchedDbQueue;

	private DataWriterThread dataWriterThread;


	/**
	 * Costruttore
	 * 
	 */
	private Centrale() {
		super(THREAD_NAME);
		// Load log4j configuration
		Properties log4jProperties = ConfigManager.readLog4jProperties();
		if (log4jProperties == null)
			PropertyConfigurator.configure(getClass().getResource(
					"/log4j.properties"));
		else
			PropertyConfigurator.configure(log4jProperties);
		Runtime.getRuntime().addShutdownHook(this);
		logger = Logger.getLogger("centrale." + getClass().getSimpleName());
		logger.info("Starting Centrale application, version " + VERSION
				+ " ...");
		if (log4jProperties == null)
			logger.info("Logger initialized using application"
					+ " built in properties");
		else
			logger.info("Logger initialized using user defined properties");
		regexp = System.getProperty("centrale.regexp", null);
		if (regexp != null)
			logger.info("Found property centrale.regexp='" + regexp + "'");
		String strDate = System.getProperty(
				"centrale.dbaria.minimumUpdateDateForDataTransfer", null);
		if (strDate != null) {
			logger.info("Found property "
					+ "centrale.dbaria.minimumUpdateDateForDataTransfer='"
					+ strDate + "'");
			try {
				minimumUpdateDateForDataTransferToDbAria = new SimpleDateFormat(
						"yyyyMMddHHmm").parse(strDate);
				logger.info("Minimum update date for data transfer to DbAria is "
						+ minimumUpdateDateForDataTransferToDbAria);
			} catch (ParseException e) {
				logger.error("Error parsing minimum update date for data "
						+ "transfer to DbAria, use format 'YYYYMMDDhhmm'", e);
			}
		}
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public static String getRegexp() {
		return regexp;
	}

	public static Date getMinimumUpdateDateForDataTransferToDbAria() {
		return minimumUpdateDateForDataTransferToDbAria;
	}

	public int getCoreThreadsExceptionCount() {
		return coreThreadsExceptionCount;
	}

	public static Date getMin_timestamp() {
		return min_timestamp;
	}


	private boolean init() {

		dataQueue = new ConcurrentLinkedQueue<InsertableObject>();
		matchedDbQueue = new ConcurrentLinkedQueue<InsertableObject>();

		initStatus = InitStatus.NOT_STARTED;

		status = new CentraleStatus();
		logger.info("Initializing configuration manager...");
		configManager = new ConfigManager();
		if (!configManager.init()) {
			status.setLoadConfigurationStatus(false);
			return false;
		}
		status.setLoadConfigurationStatus(true);

		Calendar cal = new GregorianCalendar(2009, 1, 6);
		min_timestamp = cal.getTime();

		// TODO: capire se bisogna controllare la dimensione di dbconfig
		logger.info("Creating DataSource for internal DB...");
		Database dataBase = configManager.getDbConfig().getDatabaseFromIndex(0);
		CentralePoolingDataSource poolingDs = new CentralePoolingDataSource(
				dataBase);
		centraleDb = new CentraleDb(poolingDs.getDataSource(), dataBase);

		// TODO: mettere nel file di configurazione dell'applicazione opzione
		// se usare o meno dbaria e creare datasource per dbaria solo se c'e'
		// l'opzione settata
		logger.info("Reading database information for DB to match...");
		matchedDataBase = configManager.getDbConfig().getDatabaseFromIndex(1);
		status.setLoadMatchedDbInfoStatus(true);

		// load anagraphic info from internal DB
		logger.info("Loading anagraphic info from internal DB...");
		InternalDbManager dbManager = null;
		try {
			dbManager = new InternalDbManager();
			configInfo = dbManager.loadConfigInfo();
			modemConfigs = dbManager.readModemConf();
			status.setLoadInternalDbInfoStatus(true);
		} catch (Exception e) {
			status.setLoadInternalDbInfoStatus(false);
			logger.error("Error loading anagraphic information", e);
		}
		if (dbManager != null) {
			try {
				dbManager.disconnect();
			} catch (SQLException e1) {
				logger.error("Error disconnecting from data base", e1);
			}
		}

		scheduler = initPolling(configInfo, modemConfigs);
		scheduler.start();

		dataWriterThread = new DataWriterThread();
		dataWriterThread.start();

		if (status.isOK())
			initStatus = InitStatus.STARTED;
		return true;
	}

	private Scheduler initPolling(ConfigInfo cfg, Vector<ModemConf> modemCfg) {
		try {
			logger.info("Initializing polling configuration...");
			CommManager commManager = new CommManager();
			commManager.addDevice(new Lan("LAN", 5, 10));
			commManager.addDevice(new Wan("WAN", 10, 10));
			initProxy(commManager, cfg);
			PhoneLine isdnLine = new PhoneLine("ISDN Line",
					cfg.getMaxNumLines(), cfg.getNumReservedLinesUi());
			commManager.addLine(isdnLine);
			initRouter(commManager, cfg);
			setModems(modemCfg, commManager, cfg);
			AdvancedPollingConf apc = cfg.getPollConfig();
			Scheduler scheduler;
			if (apc == null) {
				scheduler = new Scheduler(commManager, 60, 60, null, false,
						false, null, null);
			} else {
				Integer workTime_m = apc.getOpenAt() == null ? null : apc
						.getOpenAt() * 60;
				Integer workSpan_m = workTime_m == null
						|| apc.getCloseAt() == null ? null : apc.getCloseAt()
						* 60 - workTime_m;
				scheduler = new Scheduler(commManager,
						apc.getPollingOfficeTime(), 60,
						apc.getPollingExtraOffice(), apc.isCloseSat(),
						apc.isCloseSun(), workTime_m, workSpan_m);
			}
			Vector<Station> stations = cfg.getStVector();
			scheduler.setPollableItems(stations);
			return scheduler;
		} catch (Exception ex) {
			logger.error("Invalid polling configuration", ex);
			return null;
		}
	}

	private boolean updateSchedulerConfig(Scheduler scheduler, ConfigInfo cfg) {
		if (scheduler == null)
			throw new IllegalArgumentException("Scheduler should not be null");
		try {
			logger.info("Updating polling configuration...");
			CommManager commManager = scheduler.getCommManager();
			commManager.removeAllDevices(Proxy.class);
			initProxy(commManager, cfg);
			PhoneLine isdnLine = commManager.getLine("ISDN Line");
			if (isdnLine != null) {
				isdnLine.setTotalChannels(cfg.getMaxNumLines());
				isdnLine.setReservedChannels(cfg.getNumReservedLinesUi());
			}
			commManager.removeAllDevices(IsdnRouter.class);
			initRouter(commManager, cfg);
			AdvancedPollingConf apc = cfg.getPollConfig();
			if (apc == null) {
				scheduler.setPollingPeriod_m(60);
				scheduler.setPollingOffset_s(60);
				scheduler.setRestPollingPeriod_m(null);
				scheduler.setRestOnSaturday(false);
				scheduler.setRestOnSunday(false);
				scheduler.setWorkTime_m(null);
				scheduler.setWorkSpan_m(null);
			} else {
				Integer workTime_m = apc.getOpenAt() == null ? null : apc
						.getOpenAt() * 60;
				Integer workSpan_m = workTime_m == null
						|| apc.getCloseAt() == null ? null : apc.getCloseAt()
						* 60 - workTime_m;
				scheduler.setPollingPeriod_m(apc.getPollingOfficeTime());
				scheduler.setPollingOffset_s(60);
				scheduler.setRestPollingPeriod_m(apc.getPollingExtraOffice());
				scheduler.setRestOnSaturday(apc.isCloseSat());
				scheduler.setRestOnSunday(apc.isCloseSun());
				scheduler.setWorkTime_m(workTime_m);
				scheduler.setWorkSpan_m(workSpan_m);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Invalid polling configuration", ex);
			return false;
		}
	}

	private void initRouter(CommManager commManager, ConfigInfo cfg)
			throws CommManagerException {
		String routerHost = cfg.getCopRouter();
		if (routerHost != null && !routerHost.trim().isEmpty())
			commManager.addDevice(new IsdnRouter("Router", routerHost,
					"ISDN Line", cfg.getRouterTryTimeout(), cfg
							.getRouterTimeout()));
	}

	private void initProxy(CommManager commManager, ConfigInfo cfg)
			throws CommManagerException {
		String proxyHost = cfg.getProxyHost();
		Integer proxyPort = cfg.getProxyPort();
		if (proxyHost != null && !proxyHost.trim().isEmpty()
				&& proxyPort != null) {
			commManager.addDevice(new Proxy("Proxy", proxyHost, proxyPort, 10,
					10));
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort.toString());
			String exclusionList = cfg.getProxyExlusion();
			StringBuilder defExcl = new StringBuilder();
			defExcl.append("localhost|*.localdomain|127.*|10.*|192.168.*");
			for (int i = 16; i <= 31; i++)
				defExcl.append("|172." + i + ".*");
			if (exclusionList != null)
				exclusionList = exclusionList.trim();
			if (exclusionList == null || exclusionList.isEmpty())
				System.setProperty("http.nonProxyHosts", defExcl.toString());
			else if (exclusionList.startsWith("|"))
				System.setProperty("http.nonProxyHosts", defExcl
						+ exclusionList);
			else
				System.setProperty("http.nonProxyHosts", exclusionList);
		} else {
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			System.clearProperty("http.nonProxyHosts");
		}
	}

	private void setModems(Vector<ModemConf> modemCfg, CommManager commManager,
			ConfigInfo cfg) throws CommManagerException {
		commManager.removeAllDevices(Modem.class);
		if (modemCfg != null && !modemCfg.isEmpty()) {
			// TODO: viene gestito solo il primo modem perché nella
			// configurazione delle stazioni non si può scegliere quale usare
			ModemConf mc = modemCfg.get(0);
			commManager.addDevice(new Modem("Modem", mc.getDescription(), mc
					.getPhonePrefix(), cfg.getCopIp(),
					mc.isSharedLine() ? "ISDN Line" : null));
		}
	}

	private CommManager getCommManager() throws CentraleException {
		Scheduler tmp = scheduler;
		if (tmp == null)
			throw new CentraleException("Global communication configuration "
					+ "not valid or missing");
		return tmp.getCommManager();
	}

	public Connection getConnection(int stationId, boolean urgent)
			throws CentraleException, DeviceNotFoundException,
			IncompatibleDeviceException, DeviceBusyException {
		Station st = getStation(stationId);
		if (st == null)
			throw new CentraleException("Cannot find station with ID "
					+ stationId);
		CommConfig cc = st.getCommunicationConfig();
		if (cc == null)
			throw new CentraleException("Communication configuration not "
					+ "available for station " + st.getName());
		return getCommManager().getConnection(cc, urgent);
	}

	public StationInfo readStationInfo(CommDeviceInfo commDeviceInfo)
			throws CentraleException, DeviceNotFoundException,
			IncompatibleDeviceException, DeviceBusyException,
			ConnectionException, IOException, InterruptedException,
			ProtocolException {
		if (commDeviceInfo == null)
			throw new CentraleException(
					"Communication configuration not specified");
		Connection conn = getCommManager().getConnection(
				commDeviceInfo.getCommunicationConfig(), true);
		try {
			conn.start();
			PerifericoProtocol pp = new PerifericoProtocol();
			StationConfig stationCfg = pp.getStationConfig(conn, null, null);
			return new StationInfo(stationCfg);
		} finally {
			conn.release();
		}
	}

	public Station getStation(int stationId) {
		ConfigInfo ciTmp = configInfo;
		if (ciTmp == null || ciTmp.getStVector() == null)
			return null;
		for (Station st : ciTmp.getStVector())
			if (st.getStationId() == stationId)
				return st;
		return null;
	}

	// TODO: valutare uso di synchronized e/o altre precauzioni
	public void addStation(Station station) throws CentraleException {
		ConfigInfo ciTmp = configInfo;
		if (ciTmp == null)
			throw new CentraleException("Cannot add new station: application"
					+ " configuration missing");
		Vector<Station> stVect = ciTmp.getStVector();
		if (stVect == null)
			throw new CentraleException("Cannot add new station: station list"
					+ " not initialized");
		for (Station st : stVect)
			if (st.getStationId() == station.getStationId())
				throw new CentraleException("Cannot add new station: station"
						+ " with id '" + station.getStationId() + "' already"
						+ " present in station list");
		stVect.addElement(station);
	}

	public void updateModems(Vector<ModemConf> modemCfg) {
		Scheduler tmp = scheduler;
		if (tmp == null)
			return;
		try {
			setModems(modemCfg, tmp.getCommManager(), configInfo);
		} catch (CommManagerException e) {
			logger.error("Cannot update modem list", e);
		}
	}

	public Boolean pollStation(int stationId) {
		Station st = getStation(stationId);
		return st == null ? null : pollStation(st);
	}

	public boolean pollStation(Station station) {
		Scheduler tmpScheduler = scheduler;
		if (tmpScheduler == null)
			return false;
		tmpScheduler.poll(station);
		return true;
	}

	public InitStatus getInitStatus() {
		return initStatus;
	}

	public CentraleDb getCentraleDb() {
		return centraleDb;
	}

	public LoginCfg getLoginCfg() {
		return configManager.getLoginCfg();
	}

	public DbConfig getDbConfig() {
		return configManager.getDbConfig();
	}

	public static void main(String[] args) throws Exception {
		try {
			Centrale c = getInstance();
			if (!c.startJetty())
				System.exit(2);
		} catch (Exception ex) {
			System.exit(1);
		}

	}

	// NOTE: this function exists in order to start the application both in
	// standard environment and in GWT emulator
	public static synchronized Centrale getInstance() {
		if (centraleApp == null) {
			centraleApp = new Centrale();
			if (!centraleApp.init()) {
				String msg = "Fatal error: cannot start Centrale application";
				logger.error(msg);
				System.out.println("   " + msg);
				throw new RuntimeException(msg);
			}
		}
		return (centraleApp);
	}

	private boolean startJetty() {
		try {
			logger.info("Starting Jetty application server...");
			int port = CENTRALE_PORT;
			String strPort = System.getProperty("centrale.port");
			if (strPort != null) {
				try {
					int tmpPort = Integer.parseInt(strPort);
					if (tmpPort <= 0)
						throw new IllegalArgumentException(
								"Centrale application port should be > 0");
					port = tmpPort;
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException(
							"Cannot parse value for Centrale application port: "
									+ strPort);
				}
			}

			Server server = new Server();
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			connector.setHost("0.0.0.0");
			server.addConnector(connector);
			WebAppContext wac = new WebAppContext();
			wac.setContextPath("/");
			wac.setWar("bin/centraleUI.war");
			// set unlimited session timeout
			wac.getSessionHandler().getSessionManager()
					.setMaxInactiveInterval(-1);
			server.setStopAtShutdown(true);
			configureRequestLogging(server, wac);
			server.start();
			String msg = "User interface service started";
			logger.info(msg);
			System.out.println("   " + msg);
			// NOTE: do not change this message because it is used to trigger
			// startup completion detection in the init script:
			// /etc/init.d/centrale
			System.out.println("   Centrale startup completed");
			jettyServer = server;
			webAppContext = wac;

			return true;
		} catch (Exception ex) {
			String msg = "Fatal error: cannot start user interface service";
			logger.error(msg, ex);
			System.out.println("   " + msg);
			return false;
		}
	}

	public void addServlet(ServletHolder servletHolder, String pathSpec) {
		try {
			webAppContext.addServlet(servletHolder, pathSpec);
			logger.debug("Added servlet holder '" + servletHolder.getName()
					+ "', with servlet '" + servletHolder.getServlet()
					+ "' at path '" + pathSpec + "'");
		} catch (ServletException e) {
			logger.error("Error adding servlet " + servletHolder.getName(), e);
		}
	}

	public void removeServlet(ServletHolder servletHolder) {
		try {
			logger.debug("Removing servlet holder '" + servletHolder.getName()
					+ "', with servlet " + servletHolder.getServlet());
			ServletHandler handler = webAppContext.getServletHandler();
			List<ServletHolder> holders = new ArrayList<ServletHolder>();
			Set<String> names = new HashSet<String>();
			for (ServletHolder holder : handler.getServlets()) {
				if (servletHolder.getServlet() == holder.getServlet()) {
					logger.trace("Discarding servlet holder '"
							+ holder.getName() + "', with servlet "
							+ holder.getServlet());
					names.add(holder.getName());
				} else {
					logger.trace("Keeping servlet holder '" + holder.getName()
							+ "', with servlet " + holder.getServlet());
					holders.add(holder);
				}
			}
			List<ServletMapping> mappings = new ArrayList<ServletMapping>();
			for (ServletMapping mapping : handler.getServletMappings()) {
				if (!names.contains(mapping.getServletName())) {
					mappings.add(mapping);
					logger.trace("Keeping mapping: " + mapping.getServletName());
				} else {
					logger.trace("Discarding mapping: "
							+ mapping.getServletName());
				}
			}
			handler.setServletMappings(mappings
					.toArray(new ServletMapping[mappings.size()]));
			handler.setServlets(holders.toArray(new ServletHolder[holders
					.size()]));
		} catch (Exception e) {
			logger.error("Error removing servlet " + servletHolder.getName(), e);
		}
	}

	// TODO: completare
	public void stopVerifyInternalDb(String caller) {
		// clear anagraphic informations about cop and stations
		logger.info("stopVerifyInternalDb from: " + caller);
		configInfo = null;

		// stops thread
		if (scheduler != null)
			scheduler.stop();
		dataWriterThread.setContinueRun(false);

		// start thread to check connection to internal db
		CheckDb checkDb = new CheckDb();
		checkDb.start();

	}// end stopVerifyInternalDb

	public void restartThreads() throws SQLException, CentraleException {
		logger.debug("Restarting thread....");
		InternalDbManager dbManager = new InternalDbManager();
		configInfo = dbManager.loadConfigInfo();
		modemConfigs = dbManager.readModemConf();
		dbManager.disconnect();

		if (scheduler != null)
			scheduler.stop();
		scheduler = initPolling(configInfo, modemConfigs);
		scheduler.start();

		dataWriterThread = new DataWriterThread();
		dataWriterThread.start();
	}

	public ConfigInfo getConfigInfo() {
		return configInfo;
	}

	public void setConfigInfo(ConfigInfo configInfo) {
		this.configInfo = configInfo;
		if (scheduler != null)
			updateSchedulerConfig(scheduler, configInfo);
	}

	public ConcurrentLinkedQueue<InsertableObject> getDataQueue() {
		return dataQueue;
	}

	public void setDataQueue(ConcurrentLinkedQueue<InsertableObject> dataQueue) {
		this.dataQueue = dataQueue;
	}

	public ConcurrentLinkedQueue<InsertableObject> getMatchedDbQueue() {
		return matchedDbQueue;
	}

	public void setMatchedDbQueue(
			ConcurrentLinkedQueue<InsertableObject> matchedDbQueue) {
		this.matchedDbQueue = matchedDbQueue;
	}

	public Database getMatchedDataBase() {
		return matchedDataBase;
	}

	private static void configureRequestLogging(Server server, WebAppContext wac) {
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		RequestLogHandler logHandler = new RequestLogHandler();
		handlers.setHandlers(new Handler[] { contexts, wac, logHandler });
		server.setHandler(handlers);

		NCSARequestLog requestLog = new NCSARequestLog(
				"./log/jetty-yyyy_mm_dd.request.log");
		requestLog.setRetainDays(30);
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone("GMT-1");
		logHandler.setRequestLog(requestLog);
	}

	@Override
	public void run() {
		// NOTE: this is the application's shutdown hook
		String msg = "Stopping Centrale application...";
		logger.info(msg);
		if (jettyServer != null)
			try {
				logger.info("Waiting for user interface service to terminate");
				jettyServer.join();
				msg = "User interface service stopped";
				logger.info(msg);
				System.out.println("   " + msg);
			} catch (InterruptedException e) {
				logger.error("Wait for user interface service interrupted", e);
			}
		msg = "Centrale application stopped";
		logger.info(msg);
		System.out.println("   " + msg);
	}

	@Override
	public synchronized void uncaughtException(Thread t, Throwable e) {
		logger.error("Crash detected for thread: " + t.getName(), e);
		coreThreadsExceptionCount++;
		status.incrementThreadFailures();
		if (lastRestartTime < System.currentTimeMillis()
				- RESTART_COUNT_RESET_PERIOD_M * 60 * 1000) {
			restartCount = 0;
			logger.info("Resetting restart counter: "
					+ "no errors detected during reset period");
		}
		if (restartCount < MAX_RESTARTS_ALLOWED) {
			lastRestartTime = System.currentTimeMillis();
			restartCount++;
			logger.warn("Trying to recover restarting current configuration ...");
			try {
				restartThreads();
				status.resetCurrentThreadFailures();
			} catch (Exception e1) {
				logger.error("Failure restarting threads", e1);
			}
		} else {
			logger.error("Too many restarts attempted (" + restartCount
					+ "), give up restarting");
		}
	}
}// end class
