/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: class for implementation of server functions for connecting
* to Periferico application 
* Change log:
*   2008-10-27: initial version
*   2014-04-29: general rework and improvement by Pierfrancesco Vallosio
* ----------------------------------------------------------------------------
* $Id: ConnPerifServiceImpl.java,v 1.28 2015/10/29 17:33:47 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.connperif.server;

import it.csi.centrale.Centrale;
import it.csi.centrale.comm.CommManagerException;
import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.connperif.client.ConnPerifException;
import it.csi.centrale.connperif.client.ConnPerifService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.ServletHolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Class for implementation of server functions for connecting to Periferico
 * application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ConnPerifServiceImpl extends RemoteServiceServlet implements ConnPerifService, Runnable {

	private static final long serialVersionUID = 3992497826343759097L;
	// ATTENTION: if PROXY_BASE_PATH is modified, the Periferico class
	// it.csi.periferico.ui.server.RemoteServiceServletWithProxyPatch
	// should be modified accordingly to have Periferico UI working when
	// it is viewed using Centrale UI
	private static final String PROXY_BASE_PATH = "/proxy/station_";
	private static final int INACTIVITY_TIMEOUT_ms = 10 * 60 * 1000;
	private static final int NO_CHECK_TIMEOUT_ms = 30 * 1000;
	private static final int PROXY_REAPER_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
	private static final String PROXY_REAPER_THREAD_NAME = "proxy-reaper";
	private static final int PROXY_REAPER_THREAD_SLEEP_TIME_ms = 5 * 1000;
	private static Logger logger = Logger.getLogger("uiservice." + ConnPerifServiceImpl.class.getSimpleName());
	private Centrale centrale = Centrale.getInstance();
	private Map<Integer, ProxyInfo> proxyMap = new HashMap<Integer, ProxyInfo>();

	public ConnPerifServiceImpl() {
		Thread thread = new Thread(this);
		thread.setName(PROXY_REAPER_THREAD_NAME);
		thread.setPriority(PROXY_REAPER_THREAD_PRIORITY);
		thread.setDaemon(true);
		thread.start();
	}

	private HttpSession getSession() {
		return this.getThreadLocalRequest().getSession();
	}

	@Override
	public String getLocalIPAddress() {
		String result = this.getThreadLocalRequest().getLocalAddr();
		logger.debug("getLocalIPAddress called, result: " + result);
		return result;
	}

	@Override
	public void setLocale(String locale) {
		logger.debug("setLocale called with locale: " + locale);
		getSession().setAttribute("locale", locale);
	}

	@Override
	public void stopStationUi(int proxyId) throws ConnPerifException {
		dismissProxy(proxyId);
	}

	@Override
	public int makeConnection(int stationId) throws ConnPerifException {
		return setupProxy(stationId);
	}

	@Override
	public void isConnectionAlive(int proxyId) throws ConnPerifException {
		checkConnection(proxyId);
	}

	@Override
	public void run() {
		logger.info("Proxy reaper thread started");
		try {
			while (true) {
				reapProxies();
				Thread.sleep(PROXY_REAPER_THREAD_SLEEP_TIME_ms);
			}
		} catch (InterruptedException ex) {
			logger.info("Proxy reaper thread terminated");
		} catch (Exception ex) {
			logger.error("Proxy reaper thread terminated by unexpected error", ex);
		}
	}

	private int setupProxy(int stationId) throws ConnPerifException {
		ProxyInfo proxyInfo;
		synchronized (proxyMap) {
			proxyInfo = proxyMap.get(stationId);
			if (proxyInfo == null) {
				proxyInfo = new ProxyInfo(stationId);
				proxyMap.put(stationId, proxyInfo);
			}
		}
		synchronized (proxyInfo) {
			if (proxyInfo.isBound()) {
				logger.info("Proxy for station " + stationId + " already active, no need to add a new one");
				proxyInfo.addUser();
				return proxyInfo.getId();
			}
			logger.debug("Adding proxy for station " + stationId);
			Connection conn;
			try {
				conn = centrale.getConnection(stationId, true);
			} catch (DeviceBusyException e) {
				logger.warn("Cannot add proxy for station " + stationId + ": communication device is busy", e);
				throw new ConnPerifException("Dispositivo di comunicazione occupato");
			} catch (Exception e) {
				logger.error("Error adding proxy for station " + stationId, e);
				throw new ConnPerifException("Errore lato server '" + e.getMessage() + "'");
			}
			try {
				conn.start();
				CheckingTransparent transparentProxy = new CheckingTransparent(PROXY_BASE_PATH + proxyInfo.getId(),
						conn.getHost(), conn.getPort());
				ServletHolder proxyHolder = new ServletHolder(transparentProxy);
				centrale.addServlet(proxyHolder, PROXY_BASE_PATH + proxyInfo.getId() + "/*");
				transparentProxy.setProxtInfo(proxyInfo);
				proxyInfo.bind(proxyHolder, conn);
				proxyInfo.addUser();
				logger.info("Added proxy with ID " + proxyInfo.getId() + " for station " + stationId);
				return proxyInfo.getId();
			} catch (CommManagerException e) {
				conn.release();
				logger.warn("Cannot add proxy for station " + stationId + ": connection to remote host not established",
						e);
				throw new ConnPerifException("Impossibile stabilire la " + "connessione '" + e.getMessage() + "'");
			} catch (Exception e) {
				conn.release();
				logger.error("Error adding proxy for station " + stationId, e);
				throw new ConnPerifException("Errore lato server '" + e.getMessage() + "'");
			}
		}
	}

	private void dismissProxy(int proxyId) {
		ProxyInfo proxyInfo;
		synchronized (proxyMap) {
			proxyInfo = proxyMap.get(proxyId);
			if (proxyInfo == null) {
				logger.error("Cannot remove proxy with ID " + proxyId + ": cannot find proxy with this ID");
				return;
			}
		}
		synchronized (proxyInfo) {
			if (proxyInfo.isUnused()) {
				logger.info("Proxy with ID " + proxyId + ", already removed");
				return;
			}
			proxyInfo.removeUser();
			if (!proxyInfo.isUnused()) {
				logger.info("Keeping proxy with ID " + proxyId + ", still used by other users");
				return;
			}
			logger.debug("Removing proxy with ID " + proxyId + " on user request");
			proxyInfo.getProxyHolder().doStop();
			centrale.removeServlet(proxyInfo.getProxyHolder());
			proxyInfo.getConnection().release();
			proxyInfo.unbind();
			logger.info("Removed proxy with ID " + proxyId + " on user request");
		}
	}

	/**
	 * Check the connection
	 * 
	 * @param proxyId
	 * @throws ConnPerifException
	 */
	private void checkConnection(int proxyId) throws ConnPerifException {
		ProxyInfo proxyInfo;
		synchronized (proxyMap) {
			proxyInfo = proxyMap.get(proxyId);
			if (proxyInfo == null) {
				logger.error(
						"Cannot check connection for proxy with ID " + proxyId + ": cannot find proxy with this ID");
				throw new ConnPerifException("Impossibile trovare " + "informazioni sulla connessione");
			}
		}
		synchronized (proxyInfo) {
			logger.debug("Checking connection for proxy with ID " + proxyId);
			if (proxyInfo.isUnused()) {
				logger.info("Skipping connection check for proxy with ID " + proxyId + ": proxy is not active");
				throw new ConnPerifException("Connessione chiusa per " + "inattività dell'utente");
			}
			long now = System.currentTimeMillis();
			if (now - proxyInfo.getActivityTime() > INACTIVITY_TIMEOUT_ms) {
				logger.debug("Removing proxy with ID " + proxyId + " for user inactivity");
				proxyInfo.getProxyHolder().doStop();
				centrale.removeServlet(proxyInfo.getProxyHolder());
				proxyInfo.getConnection().release();
				proxyInfo.unbind();
				logger.info("Removed proxy with ID " + proxyId + " for user inactivity");
				throw new ConnPerifException("Connessione chiusa per " + "inattività dell'utente");
			}
			try {
				proxyInfo.getConnection().test();
				proxyInfo.updateCheckTime();
			} catch (ConnectionException e) {
				logger.warn("Connection for proxy with ID " + proxyId + " is dropped", e);
				throw new ConnPerifException("Caduta della connessione" + ", causa '" + e.getMessage() + "'");
			} catch (Exception e) {
				logger.error("Error checking connection for proxy with" + " ID " + proxyId, e);
				throw new ConnPerifException(
						"Errore durante la " + "verifica della connessione '" + e.getMessage() + "'");
			}
		}
	}

	private void reapProxies() {
		List<ProxyInfo> currentProxies;
		synchronized (proxyMap) {
			currentProxies = new ArrayList<ProxyInfo>(proxyMap.values());
		}
		for (ProxyInfo proxyInfo : currentProxies) {
			synchronized (proxyInfo) {
				if (proxyInfo.isUnused())
					continue;
				long now = System.currentTimeMillis();
				if (now - proxyInfo.getCheckTime() > NO_CHECK_TIMEOUT_ms) {
					logger.debug("Removing proxy with ID " + proxyInfo.getId() + " for browsers disconnection");
					proxyInfo.getProxyHolder().doStop();
					centrale.removeServlet(proxyInfo.getProxyHolder());
					proxyInfo.getConnection().release();
					proxyInfo.unbind();
					logger.warn("Removed proxy with ID " + proxyInfo.getId() + " for browsers disconnection");
				}
			}
		}
	}

}
