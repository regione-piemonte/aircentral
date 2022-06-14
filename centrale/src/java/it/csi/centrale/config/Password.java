/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: class for storing information about user's password
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: Password.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.config;

import org.apache.log4j.Logger;

/**
 * Class for storing information about user's password
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class Password {

	// variables corresponding to element password of xml file
	private String type;
	private String value;
	private String mode;

	// Define a static logger variable
	static Logger logger = Logger.getLogger("centrale." + LoginCfg.class.getSimpleName());

	public Password() {
		super();
	}

	public Password(String type, String value, String mode) {
		super();
		this.type = type;
		this.value = value;
		this.mode = mode;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	public void printPassword() {

		String print = "\nStampa del contenuto del file di configurazione di login:\n";
		print += "  type: " + this.type + "\n";
		print += "  value: " + this.value + "\n";
		print += "  mode: " + this.mode + "\n";

		logger.debug(print);
	}

}
