/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a scalar element
 * Change log:
 *   2008-10-03: initial version
 * ----------------------------------------------------------------------------
 * $Id: ScalarElement.java,v 1.22 2014/10/15 16:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.ScalarElementCfg;

import java.io.Serializable;

/**
 * Class for storing information about a scalar element
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class ScalarElement extends GenericElement implements Serializable {

	private static final long serialVersionUID = 8783122083400425619L;

	private String unit;

	private double minValue;

	private double maxValue;

	private int decimalQuantityValue;

	// private ListGenericData sampleData;

	public ScalarElement(String elementType, DataNode parent) {
		super(elementType, parent);
	}

	public ScalarElement(ScalarElementCfg config, String elementType,
			DataNode parent) throws PollingException {
		super(elementType, parent);
		setName(config.getParameterId());
		update(config);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public int getDecimalQuantityValue() {
		return decimalQuantityValue;
	}

	public void setDecimalQuantityValue(int decimalQuantityValue) {
		this.decimalQuantityValue = decimalQuantityValue;
	}

	public int getAvgPeriodValue(int position) {
		return avgPeriod.elementAt(position).getAvgPeriodVal();
	}

	public void update(ScalarElementCfg config) throws PollingException {
		super.update(config);
		unit = config.getMeasureUnit();
		minValue = config.getMinValue();
		maxValue = config.getMaxValue();
		decimalQuantityValue = config.getNumDecimals();
	}

}
