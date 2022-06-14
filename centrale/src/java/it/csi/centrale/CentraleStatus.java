/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for storing informations about status of the application
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: CentraleStatus.java,v 1.6 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

/**
 * Class for storing informations about status of the application
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class CentraleStatus {

	private Boolean loadConfigurationStatus = null;

	private Boolean loadInternalDbInfoStatus = null;

	private Boolean loadMatchedDbInfoStatus = null;

	private Boolean configActivationStatus = null;

	private int totalThreadFailures = 0;

	private int currentThreadFailures = 0;

	public boolean isOK() {
		if (loadConfigurationStatus == null || !loadConfigurationStatus)
			return false;
		if (loadInternalDbInfoStatus == null || !loadInternalDbInfoStatus)
			return false;
		if (loadMatchedDbInfoStatus == null || !loadMatchedDbInfoStatus)
			return false;
		if (currentThreadFailures > 0)
			return false;
		if (configActivationStatus == null || !configActivationStatus)
			return false;
		return true;
	}

	public Boolean getLoadConfigurationStatus() {
		return loadConfigurationStatus;
	}

	public void setLoadConfigurationStatus(Boolean loadConfigurationStatus) {
		this.loadConfigurationStatus = loadConfigurationStatus;
	}

	public Boolean getLoadInternalDbInfoStatus() {
		return loadInternalDbInfoStatus;
	}

	public void setLoadInternalDbInfoStatus(Boolean loadInternalDbInfoStatus) {
		this.loadInternalDbInfoStatus = loadInternalDbInfoStatus;
	}

	public Boolean getLoadMatchedDbInfoStatus() {
		return loadMatchedDbInfoStatus;
	}

	public void setLoadMatchedDbInfoStatus(Boolean loadMatchedDbInfoStatus) {
		this.loadMatchedDbInfoStatus = loadMatchedDbInfoStatus;
	}

	void incrementThreadFailures() {
		totalThreadFailures++;
		currentThreadFailures++;
	}

	public int getTotalThreadFailures() {
		return totalThreadFailures;
	}

	public void setConfigActivationStatus(Boolean configActivationStatus) {
		this.configActivationStatus = configActivationStatus;
	}

	public Boolean getConfigActivationStatus() {
		return configActivationStatus;
	}

	void resetCurrentThreadFailures() {
		currentThreadFailures = 0;
	}
}// end class
