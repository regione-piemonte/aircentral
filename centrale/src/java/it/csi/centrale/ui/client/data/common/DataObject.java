/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object for store information about a single data
* Change log:
*   2008-12-23: initial version
* ----------------------------------------------------------------------------
* $Id: DataObject.java,v 1.11 2015/01/19 12:01:06 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data.common;

import java.io.Serializable;
import java.util.Date;

/**
 * Object for store information about a single data
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class DataObject implements Serializable {

	private static final long serialVersionUID = -8160228381670568082L;

	private Double value;
	private String measureUnit; // for WindDataObject, is speedMeasureUnit
	private Date date;
	private String dateString;
	private boolean flag;
	private int multipleFlag;
	private int elementId;
	private int analyzerId;
	private Integer avgPeriodId;
	private String elementName;
	private String analyzerName;
	private int numDec; // for WindDataObject, is speedNumDec
	private String title;

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
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

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the dateString
	 */
	public String getDateString() {
		return dateString;
	}

	/**
	 * @param dateString the dateString to set
	 */
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * @return the multipleFlag
	 */
	public int getMultipleFlag() {
		return multipleFlag;
	}

	/**
	 * @param multipleFlag the multipleFlag to set
	 */
	public void setMultipleFlag(int multipleFlag) {
		this.multipleFlag = multipleFlag;
	}

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
	 * @return the analyzerId
	 */
	public int getAnalyzerId() {
		return analyzerId;
	}

	/**
	 * @param analyzerId the analyzerId to set
	 */
	public void setAnalyzerId(int analyzerId) {
		this.analyzerId = analyzerId;
	}

	/**
	 * @return the avgPeriodId
	 */
	public Integer getAvgPeriodId() {
		return avgPeriodId;
	}

	/**
	 * @param avgPeriodId the avgPeriodId to set
	 */
	public void setAvgPeriodId(Integer avgPeriodId) {
		this.avgPeriodId = avgPeriodId;
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
	 * @return the analyzerName
	 */
	public String getAnalyzerName() {
		return analyzerName;
	}

	/**
	 * @param analyzerName the analyzerName to set
	 */
	public void setAnalyzerName(String analyzerName) {
		this.analyzerName = analyzerName;
	}

	/**
	 * @return the numDec
	 */
	public int getNumDec() {
		return numDec;
	}

	/**
	 * @param numDec the numDec to set
	 */
	public void setNumDec(int numDec) {
		this.numDec = numDec;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
