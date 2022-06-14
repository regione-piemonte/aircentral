/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing information about on error object from Db
 * Change log:
 *   2008-10-01: initial version
 * ----------------------------------------------------------------------------
 * $Id: RejectedObject.java,v 1.3 2009/04/07 10:59:07 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import java.sql.SQLException;

/**
 * 
 * Class for storing information about on error object from Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class RejectedObject {
	private Object obj;

	private String sqlCmd;

	private SQLException sqlEx;

	public RejectedObject() {
		this(null, null, null);
	}

	public RejectedObject(Object obj, String sqlCmd, SQLException sqlEx) {
		this.obj = obj;
		this.sqlCmd = sqlCmd;
		this.sqlEx = sqlEx;
	}

	public Object getObj() {
		return obj;
	}

	public String getSqlCmd() {
		return sqlCmd;
	}

	public SQLException getSqlEx() {
		return sqlEx;
	}

	@Override
	public String toString() {
		return (sqlCmd + " " + sqlEx.toString());
	}
}