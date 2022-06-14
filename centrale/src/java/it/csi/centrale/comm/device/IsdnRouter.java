/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class that represent the router isdn
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: IsdnRouter.java,v 1.3 2014/09/22 13:49:33 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.device;

import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.config.IsdnCommConfig;

/**
 * Class that represent the router isdn
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class IsdnRouter extends LineBasedDevice {

	private String host;
	private int connectTimeout_s;
	private int hangupDelay_s;

	public IsdnRouter(String name, String host, String lineName, int connectTimeout_s, int hangupDelay_s) {
		super(name, lineName);
		setHost(host);
		setConnectTimeout_s(connectTimeout_s);
		setHangupDelay_s(hangupDelay_s);
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set the host
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = checkHost(host);
	}

	/**
	 * @return timeout seconds
	 */
	public int getConnectTimeout_s() {
		return connectTimeout_s;
	}

	public void setConnectTimeout_s(int connectTimeout_s) {
		if (connectTimeout_s <= 0)
			throw new IllegalArgumentException("Connection timeout should be" + " greater than 0");
		this.connectTimeout_s = connectTimeout_s;
	}

	public int getHangupDelay_s() {
		return hangupDelay_s;
	}

	public void setHangupDelay_s(int hangupDelay_s) {
		if (hangupDelay_s <= 0)
			throw new IllegalArgumentException("Hangup delay should be" + " greater than 0");
		this.hangupDelay_s = hangupDelay_s;
	}

	@Override
	protected IsdnRouterConnection getConnectionImpl(CommConfig config, boolean urgent)
			throws IncompatibleDeviceException, DeviceBusyException {
		if (!(config instanceof IsdnCommConfig))
			throw new IncompatibleDeviceException(
					"Isdn router device cannot " + "manage communication configuration: " + config);
		return new IsdnRouterConnection(this, (IsdnCommConfig) config);
	}

	@Override
	public String toString() {
		return "IsdnRouter [name=" + getName() + ", lineName=" + getLineName() + ", host=" + host
				+ ", connectTimeout_s=" + connectTimeout_s + ", hangupDelay_s=" + hangupDelay_s + "]";
	}

}
