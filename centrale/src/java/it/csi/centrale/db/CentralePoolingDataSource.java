/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: class for manage dataSource for accessing to Internal Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: CentralePoolingDataSource.java,v 1.10 2014/09/25 14:03:47 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import it.csi.centrale.config.Database;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * Class for manage dataSource for accessing to Internal Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class CentralePoolingDataSource {

	// Define a static logger variable
	private static Logger logger = Logger.getLogger("centrale."
			+ CentralePoolingDataSource.class.getSimpleName());
	private static final int MAX_ACTIVE = 100;

	DataSource dataSource = null;

	static GenericObjectPool connectionPool = null;

	public static GenericObjectPool getConnectionPool() {
		return connectionPool;
	}

	public CentralePoolingDataSource(Database database) {

		// TODO: mettere opzione per driver oracle con if su database.engine
		// Load the underlying JDBC driver
		logger.debug("Loading underlying JDBC driver");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("Cannot found main class for JDB driver", e);
		}
		logger.debug("Done");

		// Set up the PoolingDataSource.
		logger.info("Configuring data source for database "
				+ database.getDescription() + "...");
		this.dataSource = setupDataSource(database);
		logger.info("Done");

	}

	private static DataSource setupDataSource(Database database) {

		// Create a ObjectPool that serves as the actual pool of connections
		connectionPool = new GenericObjectPool(null);
		connectionPool.setMaxWait(1000);
		connectionPool.setMaxActive(MAX_ACTIVE);
		logger.debug("Connection pool: " + connectionPool.getNumActive() + "/"
				+ connectionPool.getMaxActive() + " num/max active, "
				+ connectionPool.getMinIdle() + "/"
				+ connectionPool.getNumIdle() + "/"
				+ connectionPool.getMaxIdle() + " min/num/max idle, "
				+ connectionPool.getMaxWait() + " max wait time");
		// Create a ConnectionFactory that the pool will use to create
		// Connections
		DriverManagerConnectionFactory connFactory;
		connFactory = new DriverManagerConnectionFactory("jdbc:postgresql://"
				+ database.getAddress() + ":" + database.getPort() + "/"
				+ database.getDbName(), database.getUser(), database.getPassword());

		new PoolableConnectionFactory(connFactory, connectionPool, null, null,
				false, true);

		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

		return dataSource;

	}

	public int getNumActive() {
		return CentralePoolingDataSource.connectionPool.getNumActive();

	}

	public int getNumIdle() {
		return CentralePoolingDataSource.connectionPool.getNumIdle();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}// end class
