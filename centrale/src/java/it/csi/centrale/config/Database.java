/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: class for storing informations about Database
* Change log:
*   2008-09-22: initial version
* ----------------------------------------------------------------------------
* $Id: Database.java,v 1.5 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.config;

import org.apache.log4j.Logger;

/**
 * Class for storing informations about Database
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class Database {
	// variables corresponding to element db of xml file
	private String description;

	private String dbName;
	private String address;
	private Integer port;
	private String user;
	private String password;
	private String engine;
	private Integer dbType;

	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale." + Database.class.getSimpleName());

	public Database() {
		super();
	}

	public Database(String description, String dbName, String address, Integer port, String user, String password,
			String engine, Integer dbType) {
		super();
		this.description = description;
		this.dbName = dbName;
		this.address = address;
		this.port = port;
		this.user = user;
		this.password = password;
		this.engine = engine;
		this.dbType = dbType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * @param engine the engine to set
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * @return the dbType
	 */
	public Integer getDbType() {
		return dbType;
	}

	/**
	 * @param dbType the dbType to set
	 */
	public void setDbType(Integer dbType) {
		this.dbType = dbType;
	}

	/**
	 * Print database information
	 */
	public void printDatabase() {
		String print = "\nDatabase configuration:\n";
		print += "  description: " + this.description + "\n";
		print += "  database: " + this.dbName + "\n";
		print += "  address: " + this.address + "\n";
		print += "  port: " + this.port + "\n";
		print += "  user: " + this.user + "\n";
		print += "  password: " + this.password + "\n";
		print += "  engine: " + this.engine + "\n";
		print += "  dbType: " + this.dbType + "\n";
		logger.debug(print);
	}

}
