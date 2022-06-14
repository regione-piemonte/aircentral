/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about an element
 * Change log:
 *   2008-10-03: initial version
 * ----------------------------------------------------------------------------
 * $Id: GenericElement.java,v 1.43 2014/10/15 16:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.PollingException;
import it.csi.centrale.polling.data.ElementCfg;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Class for storing information about an element
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public abstract class GenericElement implements DataNode, Serializable {

	private static final long serialVersionUID = -2186249508847901093L;

	public static final String SCALAR = "scalar";

	public static final String WIND = "wind";

	public static final String RAIN = "rain";

	public static final String WINDCOMPONENTSPEED = "wind_component_speed";

	public static final String WINDCOMPONENTDIR = "wind_component_dir";

	static Logger logger = Logger.getLogger("centrale."
			+ GenericElement.class.getSimpleName());

	private String type;

	private boolean enabled;

	private String name;

	private int elementId;

	private Date deletionDate; // is not null if element was deleted from
								// configuration

	protected Vector<AvgPeriod> avgPeriod;

	private Date updateDate;

	private DataNode parent;

	protected GenericElement(String elementType, DataNode parent) {
		type = elementType;
		enabled = false;
		elementId = -1;
		avgPeriod = new Vector<AvgPeriod>();
		deletionDate = null;
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public abstract int getAvgPeriodValue(int position);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected int findAvg(int value) {
		int cursor = 0;
		int retPosition = -1;

		if (this.avgPeriod != null) {
			while ((cursor < this.avgPeriod.size()) && (retPosition == -1)) {
				if (this.avgPeriod.elementAt(cursor).getAvgPeriodVal() == value) {
					retPosition = cursor;
				}
				cursor++;
			}
		}
		return retPosition;
	}

	public int getAvgPeriodSize() {
		int size = 0;
		// logger.debug("getAvgPeriodSize() avgPeriod is " + avgPeriod);
		if (avgPeriod == null)
			return size;
		else
			return avgPeriod.size();
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public Vector<AvgPeriod> getAvgPeriod() {
		return avgPeriod;
	}

	public void setAvgPeriod(Vector<AvgPeriod> avgPeriod) {
		this.avgPeriod = avgPeriod;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	protected void update(ElementCfg config) throws PollingException {
		if (!config.getParameterId().equals(name))
			throw new PollingException("Cannot update element "
					+ "configuration, parameter mismatch: expected'" + name
					+ "' found '" + config.getParameterId() + "'");
		enabled = config.isEnabled();
		if (parent.isDeleted()) {
			if (deletionDate == null)
				setDeletionDate(new Date());
		} else {
			setDeletionDate(null);
		}

		Set<Integer> setAvgPeriodsValues = new HashSet<Integer>();
		setAvgPeriodsValues.addAll(config.getAvgPeriods());
		Iterator<AvgPeriod> itAvgPeriods = avgPeriod.iterator();
		while (itAvgPeriods.hasNext()) {
			AvgPeriod ap = itAvgPeriods.next();
			if (!setAvgPeriodsValues.remove(ap.getAvgPeriodVal()))
				ap.setDeleted();
			else
				ap.update();
		}
		for (Integer period : setAvgPeriodsValues)
			avgPeriod.add(new AvgPeriod(period, this));
	}

	public void setDeleted() {
		if (isDeleted())
			return;
		setDeletionDate(new Date());
		if (avgPeriod != null)
			for (AvgPeriod ap : avgPeriod)
				ap.setDeleted();
	}

	@Override
	public boolean isDeleted() {
		return deletionDate != null;
	}

	@Override
	public boolean isActive() {
		return !isDeleted() && enabled && parent.isActive();
	}

}
