/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents measure unit.
* Change log:
*   2009-04-15: initial version
* ----------------------------------------------------------------------------
* $Id: MeasureUnitInfo.java,v 1.2 2009/04/22 11:17:04 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents measure unit.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class MeasureUnitInfo implements Serializable {

	private static final long serialVersionUID = 5597465947876872833L;

	private String measureName;
	private String description;
	private String physicalDimension;
	private Date updateDate;
	private boolean allowedForAnalyzer;
	private boolean allowedForAcquisition;
	private Double conversionMultiplyer;
	private Double conversionAddendum;
	private String conversionFormula;

	/**
	 * @return the measureName
	 */
	public String getMeasureName() {
		return measureName;
	}

	/**
	 * @param measureName the measureName to set
	 */
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the physicalDimension
	 */
	public String getPhysicalDimension() {
		return physicalDimension;
	}

	/**
	 * @param physicalDimension the physicalDimension to set
	 */
	public void setPhysicalDimension(String physicalDimension) {
		this.physicalDimension = physicalDimension;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the allowedForAnalyzer
	 */
	public boolean isAllowedForAnalyzer() {
		return allowedForAnalyzer;
	}

	/**
	 * @param allowedForAnalyzer the allowedForAnalyzer to set
	 */
	public void setAllowedForAnalyzer(boolean allowedForAnalyzer) {
		this.allowedForAnalyzer = allowedForAnalyzer;
	}

	/**
	 * @return the allowedForAcquisition
	 */
	public boolean isAllowedForAcquisition() {
		return allowedForAcquisition;
	}

	/**
	 * @param allowedForAcquisition the allowedForAcquisition to set
	 */
	public void setAllowedForAcquisition(boolean allowedForAcquisition) {
		this.allowedForAcquisition = allowedForAcquisition;
	}

	/**
	 * @return the conversionMultiplyer
	 */
	public Double getConversionMultiplyer() {
		return conversionMultiplyer;
	}

	/**
	 * @param conversionMultiplyer the conversionMultiplyer to set
	 */
	public void setConversionMultiplyer(Double conversionMultiplyer) {
		this.conversionMultiplyer = conversionMultiplyer;
	}

	/**
	 * @return the conversionAddendum
	 */
	public Double getConversionAddendum() {
		return conversionAddendum;
	}

	/**
	 * @param conversionAddendum the conversionAddendum to set
	 */
	public void setConversionAddendum(Double conversionAddendum) {
		this.conversionAddendum = conversionAddendum;
	}

	/**
	 * @return the conversionFormula
	 */
	public String getConversionFormula() {
		return conversionFormula;
	}

	/**
	 * @param conversionFormula the conversionFormula to set
	 */
	public void setConversionFormula(String conversionFormula) {
		this.conversionFormula = conversionFormula;
	}

}// end class
