/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Proxy information class
// Change log:
//   2014-04-29: initial version
// ----------------------------------------------------------------------------
// $Id: ProxyInfo.java,v 1.2 2015/10/29 17:33:47 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.connperif.server;

import org.mortbay.jetty.servlet.ServletHolder;

import it.csi.centrale.comm.Connection;
/**
 * Proxy information class
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
class ProxyInfo {
	private int id;
	private ServletHolder proxyHolder;
	private Connection connection;
	private volatile long checkTime;
	private volatile long activityTime;
	private volatile int numUsers;

	ProxyInfo(int id) {
		this.id = id;
		checkTime = activityTime = System.currentTimeMillis();
		this.numUsers = 0;
	}

	int getId() {
		return id;
	}

	void bind(ServletHolder proxyHolder, Connection connection) {
		this.proxyHolder = proxyHolder;
		this.connection = connection;
		checkTime = activityTime = System.currentTimeMillis();
		this.numUsers = 0;
	}

	void unbind() {
		proxyHolder = null;
		connection = null;
		this.numUsers = 0;
	}

	boolean isBound() {
		return proxyHolder != null;
	}

	ServletHolder getProxyHolder() {
		return proxyHolder;
	}

	Connection getConnection() {
		return connection;
	}

	void updateCheckTime() {
		checkTime = System.currentTimeMillis();
	}

	long getCheckTime() {
		return checkTime;
	}

	void updateActivityTime() {
		activityTime = System.currentTimeMillis();
	}

	long getActivityTime() {
		return activityTime;
	}

	void addUser() {
		numUsers++;
	}

	void removeUser() {
		numUsers--;
	}

	boolean isUnused() {
		return numUsers <= 0;
	}
}