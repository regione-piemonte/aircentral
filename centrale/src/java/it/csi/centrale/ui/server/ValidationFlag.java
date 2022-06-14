/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for storing information about validation flag
// Change log:
//   2008-12-22: initial version
// ----------------------------------------------------------------------------
// $Id: ValidationFlag.java,v 1.1 2014/09/17 07:30:20 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.ui.server;

import it.csi.centrale.PropertyUtil;

import java.io.IOException;

/**
 * Class for storing information about validation flag
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class ValidationFlag {

	// Bits 00-07: analyzer flags
	// Bits 08-15: element flags
	// Bits 16-23: acquisition flags
	// Bits 24-27: station flags
	// Bits 28-31: aggregation flags
	public static final int ANALYZER_FAULT = 0x00000001;
	public static final int ANALYZER_MAINTENANCE = 0x00000002;
	public static final int ANALYZER_MANUAL_CALIB = 0x00000004;
	public static final int ANALYZER_AUTO_CALIB = 0x00000008;
	public static final int VALUE_OUT_OF_RANGE = 0x00000100;
	public static final int CHANNEL_UNTRIMMED = 0x00000200;
	public static final int ACQ_ERROR = 0x00010000;
	public static final int ACQ_OUT_OF_SCALE = 0x00020000;
	public static final int ENVIRONMENT_NOT_OK = 0x01000000;
	public static final int MISSING_DATA = 0x10000000;
	public static final int NOT_CONSTANT_DATA = 0x20000000;

	private static PropertyUtil propertyUtil;

	public static String getMultipleFlagsTitle(int flag, String locale) throws IOException {

		propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		if (0 == flag)
			return propertyUtil.getProperty(locale, "flag_ok");
		else {
			String lineSeparator = " - ";
			StringBuffer tooltip = new StringBuffer();
			if ((ACQ_ERROR & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "acq_error") + lineSeparator);
			if ((ACQ_OUT_OF_SCALE & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "acq_out_of_scale") + lineSeparator);
			if ((ANALYZER_AUTO_CALIB & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "analyzer_auto_calib") + lineSeparator);
			if ((ANALYZER_FAULT & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "analyzer_fault") + lineSeparator);
			if ((ANALYZER_MAINTENANCE & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "analyzer_maintenance") + lineSeparator);
			if ((ANALYZER_MANUAL_CALIB & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "analyzer_manual_calib") + lineSeparator);
			if ((CHANNEL_UNTRIMMED & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "channel_untrimmed") + lineSeparator);
			if ((ENVIRONMENT_NOT_OK & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "environment_not_ok") + lineSeparator);
			if ((MISSING_DATA & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "missing_data") + lineSeparator);
			if ((NOT_CONSTANT_DATA & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "not_constant_data") + lineSeparator);
			if ((VALUE_OUT_OF_RANGE & flag) != 0)
				tooltip.append(propertyUtil.getProperty(locale, "value_out_of_range") + lineSeparator);
			String returnString = tooltip.toString();
			return returnString.substring(0, returnString.length() - 3);
		}
	}

}
