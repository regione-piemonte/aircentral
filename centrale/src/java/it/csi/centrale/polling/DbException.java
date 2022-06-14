/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Exception for database
// Change log:
//   2014-09-10: initial version
// ----------------------------------------------------------------------------
// $Id: DbException.java,v 1.1 2014/09/16 08:14:09 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;
/**
 * Exception for database
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class DbException extends PollingException {

	private static final long serialVersionUID = 8683437296693920766L;

	public DbException() {
	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

}
