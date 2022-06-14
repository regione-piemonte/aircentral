/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object for store information about a single wind data
* Change log:
*   2008-12-23: initial version
* ----------------------------------------------------------------------------
* $Id: WindDataObject.java,v 1.5 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data.common;

import java.io.Serializable;

/**
 * Object for store information about a single wind data
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class WindDataObject extends DataObject implements Serializable {
	private static final long serialVersionUID = 5728269061527508379L;

	private Double vectorialSpeed;
	private Double vectorialDirection;
	private Double standardDeviation;
	private Double scalarSpeed;
	private Double gustSpeed;
	private Double gustDirection;
	private Double calmsNumberPercent;
	private Boolean calm;
	private String dirMeasureUnit;
	private int dirNumDec;

	/**
	 * @return the vectorialSpeed
	 */
	public Double getVectorialSpeed() {
		return vectorialSpeed;
	}

	/**
	 * @param vectorialSpeed the vectorialSpeed to set
	 */
	public void setVectorialSpeed(Double vectorialSpeed) {
		this.vectorialSpeed = vectorialSpeed;
	}

	/**
	 * @return the vectorialDirection
	 */
	public Double getVectorialDirection() {
		return vectorialDirection;
	}

	/**
	 * @param vectorialDirection the vectorialDirection to set
	 */
	public void setVectorialDirection(Double vectorialDirection) {
		this.vectorialDirection = vectorialDirection;
	}

	/**
	 * @return the standardDeviation
	 */
	public Double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * @param standardDeviation the standardDeviation to set
	 */
	public void setStandardDeviation(Double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	/**
	 * @return the scalarSpeed
	 */
	public Double getScalarSpeed() {
		return scalarSpeed;
	}

	/**
	 * @param scalarSpeed the scalarSpeed to set
	 */
	public void setScalarSpeed(Double scalarSpeed) {
		this.scalarSpeed = scalarSpeed;
	}

	/**
	 * @return the gustSpeed
	 */
	public Double getGustSpeed() {
		return gustSpeed;
	}

	/**
	 * @param gustSpeed the gustSpeed to set
	 */
	public void setGustSpeed(Double gustSpeed) {
		this.gustSpeed = gustSpeed;
	}

	/**
	 * @return the gustDirection
	 */
	public Double getGustDirection() {
		return gustDirection;
	}

	/**
	 * @param gustDirection the gustDirection to set
	 */
	public void setGustDirection(Double gustDirection) {
		this.gustDirection = gustDirection;
	}

	/**
	 * @return the calmsNumberPercent
	 */
	public Double getCalmsNumberPercent() {
		return calmsNumberPercent;
	}

	/**
	 * @param calmsNumberPercent the calmsNumberPercent to set
	 */
	public void setCalmsNumberPercent(Double calmsNumberPercent) {
		this.calmsNumberPercent = calmsNumberPercent;
	}

	/**
	 * @return the calm
	 */
	public Boolean getCalm() {
		return calm;
	}

	/**
	 * @param calm the calm to set
	 */
	public void setCalm(Boolean calm) {
		this.calm = calm;
	}

	/**
	 * @return the dirMeasureUnit
	 */
	public String getDirMeasureUnit() {
		return dirMeasureUnit;
	}

	/**
	 * @param dirMeasureUnit the dirMeasureUnit to set
	 */
	public void setDirMeasureUnit(String dirMeasureUnit) {
		this.dirMeasureUnit = dirMeasureUnit;
	}

	/**
	 * @return the dirNumDec
	 */
	public int getDirNumDec() {
		return dirNumDec;
	}

	/**
	 * @param dirNumDec the dirNumDec to set
	 */
	public void setDirNumDec(int dirNumDec) {
		this.dirNumDec = dirNumDec;
	}

}
