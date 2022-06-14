/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a wind element
 * Change log:
 *   2008-10-03: initial version
 * ----------------------------------------------------------------------------
 * $Id: WindElement.java,v 1.26 2014/10/15 16:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.WindElementCfg;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Class for storing information about a wind element
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class WindElement extends GenericElement implements Serializable {

	private static final long serialVersionUID = 4354946615918510514L;

	static Logger logger = Logger.getLogger("centrale."
			+ WindElement.class.getSimpleName());

	private ScalarElement speed;

	private ScalarElement direction;

	public WindElement(DataNode parent) {
		super(WIND, parent);
		speed = new ScalarElement(WINDCOMPONENTSPEED, parent);
		direction = new ScalarElement(WINDCOMPONENTDIR, parent);
	}

	public WindElement(WindElementCfg config, DataNode parent)
			throws PollingException {
		super(WIND, parent);
		setName(config.getParameterId());
		super.update(config);
		speed = new ScalarElement(config.getSpeedConfig(), WINDCOMPONENTSPEED,
				parent);
		direction = new ScalarElement(config.getDirectionConfig(),
				WINDCOMPONENTDIR, parent);
	}

	public String getSpeedParam() {
		return this.speed.getName();
	}

	public String getSpeedUnit() {
		return this.speed.getUnit();
	}

	public double getSpeedMinValue() {
		return this.speed.getMinValue();
	}

	public double getSpeedMaxValue() {
		return this.speed.getMaxValue();
	}

	public int getSpeedDecimalQuantityValue() {
		return this.speed.getDecimalQuantityValue();
	}

	public String getDirParam() {
		return this.direction.getName();
	}

	public String getDirUnit() {
		return this.direction.getUnit();
	}

	public double getDirMinValue() {
		return this.direction.getMinValue();
	}

	public double getDirMaxValue() {
		return this.direction.getMaxValue();
	}

	public int getDirDecimalQuantityValue() {
		return this.direction.getDecimalQuantityValue();
	}

	public int getAvgPeriodValue(int position) {
		return avgPeriod.elementAt(position).getAvgPeriodVal();

	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.speed.setEnabled(enabled);
		this.direction.setEnabled(enabled);
	}

	public void setDeletionDate(Date deletionDate) {
		super.setDeletionDate(deletionDate);
		this.speed.setDeletionDate(deletionDate);
		this.direction.setDeletionDate(deletionDate);
	}

	public ScalarElement getSpeed() {
		return speed;
	}

	public void setSpeed(ScalarElement speed) {
		this.speed = speed;
	}

	public ScalarElement getDirection() {
		return direction;
	}

	public void setDirection(ScalarElement direction) {
		this.direction = direction;
	}

	public void update(WindElementCfg config) throws PollingException {
		super.update(config);
		speed.update(config.getSpeedConfig());
		direction.update(config.getDirectionConfig());
	}

}
