/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for element configuration
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: ElementCfg.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Class for element configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public abstract class ElementCfg {

	private String parameterId;
	private boolean enabled;
	private List<Integer> avgPeriods;

	ElementCfg(String parameterId, boolean enabled) {
		this.parameterId = parameterId;
		this.enabled = enabled;
		avgPeriods = new ArrayList<Integer>();
	}

	public String getParameterId() {
		return parameterId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<Integer> getAvgPeriods() {
		return avgPeriods;
	}

	public void addAvgPeriod(int period) {
		avgPeriods.add(period);
	}

	public static ElementCfg valueOf(String paramId,
			Map<String, String> attributes) throws IllegalArgumentException {
		if (paramId == null || paramId.isEmpty())
			throw new IllegalArgumentException(
					"Parameter id of element expected");
		String type = attributes.get("type");
		if (type == null)
			throw new IllegalArgumentException("Type attribute missing in "
					+ "element configuration for " + paramId);
		ElementCfg cfg;
		if ("scalar".equals(type)) {
			cfg = new ScalarElementCfg(paramId, new Boolean(attributes
					.get("enabled")), attributes.get("unit"), new Double(
					attributes.get("minval")), new Double(attributes
					.get("maxval")), new Integer(attributes.get("numdec")));
		} else if ("wind".equals(type)) {
			String speedParam = attributes.get("speedParam");
			if (speedParam == null || speedParam.isEmpty())
				throw new IllegalArgumentException(
						"Missing speed parameter id for element " + paramId);
			String dirParam = attributes.get("dirParam");
			if (dirParam == null || dirParam.isEmpty())
				throw new IllegalArgumentException(
						"Missing direction parameter id for element " + paramId);
			cfg = new WindElementCfg(paramId, new Boolean(attributes
					.get("enabled")), speedParam, attributes.get("speedUnit"),
					new Double(attributes.get("speedMinval")), new Double(
							attributes.get("speedMaxval")), new Integer(
							attributes.get("speedNumdec")), dirParam,
					new Double(attributes.get("dirMinval")), new Double(
							attributes.get("dirMaxval")), new Integer(
							attributes.get("dirNumdec")));
		} else {
			throw new IllegalArgumentException("Unknown element type '" + type
					+ "' for " + paramId);
		}
		String avgPeriods = attributes.get("avgperiods");
		if (avgPeriods != null && !avgPeriods.isEmpty()) {
			String[] values = avgPeriods.split(",", -1);
			for (String value : values)
				cfg.addAvgPeriod(new Integer(value));
		}
		return cfg;
	}

}
