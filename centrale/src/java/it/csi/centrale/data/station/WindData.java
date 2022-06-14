/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a wind data
 * Change log:
 *   2008-11-07: initial version
 * ----------------------------------------------------------------------------
 * $Id: WindData.java,v 1.10 2014/11/14 12:42:40 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.WindDatum;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing information about a wind data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class WindData extends GenericData implements Serializable {
	private static final long serialVersionUID = 8450481261220489192L;

	private Double vectorialSpeed;

	private Double vectorialDirection;

	private Double standardDev;

	private Double scalarSpeed;

	private Double gustSpeed;

	private Double gustDirection;

	private Double calmNumberPerc;

	private Boolean calm;

	public WindData(Date time, boolean validity, Integer flags,
			Double vectorialSpeed, Double vectorialDirection,
			Double standardDev, Double scalarSpeed, Double gustSpeed,
			Double gustDirection, Double calmNumberPerc, Boolean calm,
			Date updateDate) {
		super(time, validity, flags, updateDate);
		this.vectorialSpeed = vectorialSpeed;
		this.vectorialDirection = vectorialDirection;
		this.standardDev = standardDev;
		this.scalarSpeed = scalarSpeed;
		this.gustSpeed = gustSpeed;
		this.gustDirection = gustDirection;
		this.calmNumberPerc = calmNumberPerc;
		this.calm = calm;
	}

	public WindData(WindDatum datum) {
		this(datum.getTimestamp(), datum.isNotValid(), datum.getFlags(), datum
				.getVectorialSpeed(), datum.getVectorialDirection(), datum
				.getStandardDeviation(), datum.getScalarSpeed(), datum
				.getGustSpeed(), datum.getGustDirection(), datum
				.getCalmsNumberPercent(), datum.getCalm(), null);
	}

	public Double getVectorialSpeed() {
		return vectorialSpeed;
	}

	public void setVectorialSpeed(Double vectorialSpeed) {
		this.vectorialSpeed = vectorialSpeed;
	}

	public Double getVectorialDirection() {
		return vectorialDirection;
	}

	public void setVectorialDirection(Double vectorialDirection) {
		this.vectorialDirection = vectorialDirection;
	}

	public Double getStandardDev() {
		return standardDev;
	}

	public void setStandardDev(Double standardDev) {
		this.standardDev = standardDev;
	}

	public Double getScalarSpeed() {
		return scalarSpeed;
	}

	public void setScalarSpeed(Double scalarSpeed) {
		this.scalarSpeed = scalarSpeed;
	}

	public Double getGustSpeed() {
		return gustSpeed;
	}

	public void setGustSpeed(Double gustSpeed) {
		this.gustSpeed = gustSpeed;
	}

	public Double getGustDirection() {
		return gustDirection;
	}

	public void setGustDirection(Double gustDirection) {
		this.gustDirection = gustDirection;
	}

	public Double getCalmNumberPerc() {
		return calmNumberPerc;
	}

	public void setCalmNumberPerc(Double calmNumberPerc) {
		this.calmNumberPerc = calmNumberPerc;
	}

	public Boolean isCalm() {
		return calm;
	}

	public void setCalm(Boolean calm) {
		this.calm = calm;
	}

}
