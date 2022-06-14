/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents standard.
* Change log:
*   2009-04-21: initial version
* ----------------------------------------------------------------------------
* $Id: StandardInfo.java,v 1.1 2009/04/21 11:13:52 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class that represents standard.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class StandardInfo implements Serializable {

	private static final long serialVersionUID = 5048617835086640536L;

	private Double referenceTemperatureK;
	private Double referencePressureKPa;

	/**
	 * @return the referenceTemperatureK
	 */
	public Double getReferenceTemperatureK() {
		return referenceTemperatureK;
	}

	/**
	 * @param referenceTemperatureK the referenceTemperatureK to set
	 */
	public void setReferenceTemperatureK(Double referenceTemperatureK) {
		this.referenceTemperatureK = referenceTemperatureK;
	}

	/**
	 * @return the referencePressureKPa
	 */
	public Double getReferencePressureKPa() {
		return referencePressureKPa;
	}

	/**
	 * @param referencePressureKPa the referencePressureKPa to set
	 */
	public void setReferencePressureKPa(Double referencePressureKPa) {
		this.referencePressureKPa = referencePressureKPa;
	}

}
