/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for wind element configuration
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: WindElementCfg.java,v 1.2 2014/10/15 09:08:20 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

/**
 * Class for wind element configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class WindElementCfg extends ElementCfg {

	private String speedParamId;
	private String speedMeasureUnit;
	private double speedMinValue;
	private double speedMaxValue;
	private int speedNumDecimals;
	private String directionParamId;
	private double directionMinValue;
	private double directionMaxValue;
	private int directionNumDecimals;

	WindElementCfg(String parameterId, boolean enabled, String speedParamId, String speedMeasureUnit,
			double speedMinValue, double speedMaxValue, int speedNumDecimals, String directionParamId,
			double directionMinValue, double directionMaxValue, int directionNumDecimals) {
		super(parameterId, enabled);
		this.speedParamId = speedParamId;
		this.speedMeasureUnit = speedMeasureUnit;
		this.speedMinValue = speedMinValue;
		this.speedMaxValue = speedMaxValue;
		this.speedNumDecimals = speedNumDecimals;
		this.directionParamId = directionParamId;
		this.directionMinValue = directionMinValue;
		this.directionMaxValue = directionMaxValue;
		this.directionNumDecimals = directionNumDecimals;
	}

	public String getSpeedParamId() {
		return speedParamId;
	}

	public String getSpeedMeasureUnit() {
		return speedMeasureUnit;
	}

	public double getSpeedMinValue() {
		return speedMinValue;
	}

	public double getSpeedMaxValue() {
		return speedMaxValue;
	}

	public int getSpeedNumDecimals() {
		return speedNumDecimals;
	}

	public String getDirectionParamId() {
		return directionParamId;
	}

	public double getDirectionMinValue() {
		return directionMinValue;
	}

	public double getDirectionMaxValue() {
		return directionMaxValue;
	}

	public int getDirectionNumDecimals() {
		return directionNumDecimals;
	}

	public ScalarElementCfg getSpeedConfig() {
		return new ScalarElementCfg(speedParamId, isEnabled(), speedMeasureUnit, speedMinValue, speedMaxValue,
				speedNumDecimals);
	}

	public ScalarElementCfg getDirectionConfig() {
		return new ScalarElementCfg(directionParamId, isEnabled(), "°", directionMinValue, directionMaxValue,
				directionNumDecimals);
	}

	@Override
	public String toString() {
		return "WindElementCfg [parameterId=" + getParameterId() + ", enabled=" + isEnabled() + ", speedParamId="
				+ speedParamId + ", speedMeasureUnit=" + speedMeasureUnit + ", speedMinValue=" + speedMinValue
				+ ", speedMaxValue=" + speedMaxValue + ", speedNumDecimals=" + speedNumDecimals + ", directionParamId="
				+ directionParamId + ", directionMinValue=" + directionMinValue + ", directionMaxValue="
				+ directionMaxValue + ", directionNumDecimals=" + directionNumDecimals + ", avgPeriods="
				+ getAvgPeriods() + "]";
	}

}
