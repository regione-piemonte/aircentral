/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with information about a station key and name
* Change log:
*   2009-04-01: initial version
* ----------------------------------------------------------------------------
* $Id: KeyValueObject.java,v 1.2 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data.common;

import java.io.Serializable;

/**
 * Object with information about a station key and name
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class KeyValueObject implements Serializable {

	private static final long serialVersionUID = 4739454463042359311L;

	private String key;
	private String value;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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

}
