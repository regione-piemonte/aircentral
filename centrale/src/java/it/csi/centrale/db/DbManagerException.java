/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Pierfrancesco Vallosio
 * Purpose of file: exception for DB manager
 * Change log:
 *   2014-05-29: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbManagerException.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;
/**
 * exception for DB manager
 * 
 * @author Pierfrancesco Vallosio
 * 
 */
public class DbManagerException extends Exception {

	private static final long serialVersionUID = 5624535080963894422L;

	public DbManagerException() {
	}

	public DbManagerException(String message) {
		super(message);
	}

	public DbManagerException(Throwable cause) {
		super(cause);
	}

	public DbManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
