/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: class for runtime exception from Internal Db
 * Change log:
 *   2008-09-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbRuntimeException.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

/**
 * Class for runtime exception from Internal Db
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class DbRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6582216327944617471L;

	public DbRuntimeException() {
		super();
	}

	public DbRuntimeException(String description) {
		super(description);
	}

}
