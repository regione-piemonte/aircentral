/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for scalar element configuration
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: ScalarElementCfg.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

/**
 * Class for scalar element configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class ScalarElementCfg extends ElementCfg {

	private String measureUnit;
	private double minValue;
	private double maxValue;
	private int numDecimals;

	ScalarElementCfg(String parameterId, boolean enabled, String measureUnit, double minValue, double maxValue,
			int numDecimals) {
		super(parameterId, enabled);
		this.measureUnit = measureUnit;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.numDecimals = numDecimals;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public int getNumDecimals() {
		return numDecimals;
	}

	@Override
	public String toString() {
		return "ScalarElementCfg [parameterId=" + getParameterId() + ", enabled=" + isEnabled() + ", measureUnit="
				+ measureUnit + ", minValue=" + minValue + ", maxValue=" + maxValue + ", numDecimals=" + numDecimals
				+ ", avgPeriods=" + getAvgPeriods() + "]";
	}

}
