/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Marco Puccio
// Purpose of file: This class creates a new Exception type
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: ServiceException.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.servlet;

public class ServiceException extends Exception {

	/**
	 * This class creates a new Exception type 
	 * 
	 *  @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
	 * 
	 */
	private static final long serialVersionUID = -1815002326100414523L;
	
	
	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	

}
