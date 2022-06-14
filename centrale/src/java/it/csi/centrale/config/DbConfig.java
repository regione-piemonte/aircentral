/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: class for manage configuration of Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbConfig.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.config;

import java.util.List;

/**
 * Class for manage configuration of Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class DbConfig {

	private List<Database> databaseList;

	public DbConfig() {
		super();
	}

	public DbConfig(List<Database> databaseList) {
		super();
		this.databaseList = databaseList;
	}

	/**
	 * @return the databaseList
	 */
	public List<Database> getDatabaseList() {
		return databaseList;
	}

	/**
	 * @param databaseList
	 * the databaseList to set
	 */
	public void setDatabaseList(List<Database> databaseList) {
		this.databaseList = databaseList;
	}

	/**
	 * Print all database in list
	 */
	public void printDbConfig() {
		for (int i = 0; i < databaseList.size(); i++) {
			Database databaseObj = databaseList.get(i);
			databaseObj.printDatabase();
		}
	}

	/**
	 * @param index
	 * @return databse at the index specified
	 */
	public Database getDatabaseFromIndex(int index) {
		return databaseList.get(index);
	}
}
