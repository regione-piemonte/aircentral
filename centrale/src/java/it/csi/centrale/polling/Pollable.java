/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Interface for polling
// Change log:
//   2014-03-27: initial version
// ----------------------------------------------------------------------------
// $Id: Pollable.java,v 1.4 2014/08/29 11:09:09 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.config.CommConfig;

import java.util.Date;
/**
 * Interface for polling
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public interface Pollable {

	public String getName();

	public Integer getCustomPollingPeriod_m();

	public long getNextCustomPollingTime_ms();

	public void setNextCustomPollingTime_ms(long time);

	public Integer getGroupId();

	public CommConfig getCommunicationConfig();

	void setConnectionFailure(Failure failure);

	public Failure getConnectionFailure();

	public int getConsecutiveConnectionFailuresCount();

	public void setPollingStatus(PollingStatus pollingStatus);

	public PollingStatus getPollingStatus();

	public boolean isEnabled();

	public Date getLastPollingDate();

	public Date getLastSuccessfulPollingDate();

}
