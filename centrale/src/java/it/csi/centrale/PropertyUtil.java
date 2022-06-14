/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Isabella Vespa
// Purpose of file: class for manages message internationalization
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: PropertyUtil.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Class for manages message internationalization
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class PropertyUtil {

	private Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>(); // Map<String,

	// ResourceBundle>

	private String propertyFileName;

	public PropertyUtil(String propertyFileName) throws IOException {
		this.propertyFileName = propertyFileName;
	}

	private ResourceBundle getBundle(String locale) {
		ResourceBundle bundle = (ResourceBundle) bundles.get(locale);
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(propertyFileName, new Locale(
					locale));
			bundles.put(locale, bundle);

		}
		return bundle;
	}

	/**
	 * @param locale
	 * @param name
	 * @return the string message
	 */
	public String getProperty(String locale, String name) {
		if (locale == null)
			locale = Locale.getDefault().toString();
		if (locale.equals("en"))
			locale = "";
		return getBundle(locale).getString(name);
	}
}
