/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: class for store basic information about an element
* Change log:
*   2008-03-20: initial version
* ----------------------------------------------------------------------------
* $Id: ElementObject.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class for store basic information about an element
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ElementObject implements Serializable {

	private static final long serialVersionUID = -5784557543015827069L;

	private int elementId;
	private String elementName;
	private String measureUnit;

	/**
	 * @return the elementId
	 */
	public int getElementId() {
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the measureUnit
	 */
	public String getMeasureUnit() {
		return measureUnit;
	}

	/**
	 * @param measureUnit the measureUnit to set
	 */
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

}// end class
