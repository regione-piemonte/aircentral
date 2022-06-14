/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with status information about an analyzer alarm 
* Change log:
*   2009-01-16: initial version
* ----------------------------------------------------------------------------
* $Id: AnalyzerAlarmStatusObject.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.List;

/**
 * Object with status information about an analyzer alarm
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class AnalyzerAlarmStatusObject implements Serializable {
	private static final long serialVersionUID = -6149269758192122454L;

	private int analyzerId;
	private String analyzerName;
	private String brandModel;
	private List<AnalyzerAlarmStatusValuesObject> anAlarmValues;

	public AnalyzerAlarmStatusObject() {
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
	 * @return the brandModel
	 */
	public String getBrandModel() {
		return brandModel;
	}

	/**
	 * @param brandModel the brandModel to set
	 */
	public void setBrandModel(String brandModel) {
		this.brandModel = brandModel;
	}

	/**
	 * @return the anAlarmValues
	 */
	public List<AnalyzerAlarmStatusValuesObject> getAnAlarmValues() {
		return anAlarmValues;
	}

	/**
	 * @param anAlarmValues the anAlarmValues to set
	 */
	public void setAnAlarmValues(List<AnalyzerAlarmStatusValuesObject> anAlarmValues) {
		this.anAlarmValues = anAlarmValues;
	}

}
