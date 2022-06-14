/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: class for storing information about Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: CentraleDb.java,v 1.12 2014/09/22 10:57:52 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import it.csi.centrale.config.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * Class for storing information about Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class CentraleDb {

	private DataSource dataSource = null;

	private String dbName;

	private String port;

	private String url;

	private String password;

	private String user;

	private Integer dbType;

	GenericObjectPool connectionPool;

	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale."
			+ CentraleDb.class.getSimpleName());

	public CentraleDb(DataSource dataSource, Database database) {
		this.dataSource = dataSource;
		this.connectionPool = CentralePoolingDataSource.getConnectionPool();
		this.dbName = database.getDbName();
		this.port = database.getPort().toString();
		this.password = database.getPassword();
		this.url = database.getAddress();
		this.user = database.getUser();
		this.dbType = database.getDbType();
	}

	/**
	 * Connect to the database
	 * @return connection
	 * @throws SQLException
	 */
	public Connection connect() throws SQLException {
		if (dataSource == null)
			throw new DbRuntimeException("Error: empty DataSource");
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(true);
		logger.debug("Connect executed");
		return connection;
	}

	/**
	 * @return dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @return dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @return dbType
	 */
	public Integer getDbType() {
		return dbType;
	}

	/**
	 * @return a connection
	 * @throws SQLException
	 */
	public Connection getSingleConnection() throws SQLException {
		logger.debug("Creating a single connection...");
		Connection retConn = DriverManager.getConnection("jdbc:postgresql://"
				+ url + ":" + port + "/" + dbName, user, password);
		logger.debug("Done.");
		return retConn;
	}// end getSingleConnection

	/**
	 * @param singleConnection
	 * @throws SQLException
	 */
	public void disconnectSingleConnection(Connection singleConnection)
			throws SQLException {
		logger.debug("Closing single connection...");
		singleConnection.close();
		logger.debug("Done.");
	}// end disconnectSingleConnection

	/**
	 * @return num active connection
	 */
	private int getNumActive() {

		return connectionPool.getNumActive();
	}

	/**
	 * @return num of idle connection
	 */
	private int getNumIdle() {
		return connectionPool.getNumIdle();
	}

	public void printConnectionPoolVariable() {
		logger.debug("Connection pool: " + getNumActive() + " active, "
				+ getNumIdle() + " idle connections");
	}

}// end class
