/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a communication device
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: CommDeviceInfo.java,v 1.2 2014/09/08 09:40:14 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.Centrale;
import it.csi.centrale.comm.config.CommConfig;
import it.csi.centrale.comm.config.IsdnCommConfig;
import it.csi.centrale.comm.config.LanCommConfig;
import it.csi.centrale.comm.config.ModemCommConfig;
import it.csi.centrale.comm.config.ProxyCommConfig;

import java.io.Serializable;

/**
 * Class for storing information about a communication device
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class CommDeviceInfo implements Serializable {
	// explain what is communication device on Station
	private static final long serialVersionUID = -8907544916428187983L;
	
	public enum CommunicationDevice {
		LAN, PROXY, ROUTER, MODEM
	};

	private String ip;

	private Integer portNumber;

	private String phoneNumber;

	private boolean modem;

	private boolean lan;

	private boolean proxy;

	private String routerIpAddress; // this is local router ip address or
									// gateway if station is in lan

	public CommDeviceInfo() {
		modem = false;
		portNumber = null;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean useModem() {
		return modem;
	}

	public void setModem(boolean modem) {
		this.modem = modem;
	}

	public String getRouterIpAddress() {
		return routerIpAddress;
	}

	public void setRouterIpAddress(String routerIpAddress) {
		this.routerIpAddress = routerIpAddress;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}

	public boolean isLan() {
		return lan;
	}

	public void setLan(boolean lan) {
		this.lan = lan;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public CommConfig getCommunicationConfig() {
		int port = portNumber == null ? Centrale.DEFAULT_PERIFERICO_PORT
				: portNumber;
		CommConfig commConfig;
		if (lan) {
			commConfig = new LanCommConfig(ip, port, "LAN");
		} else if (proxy) {
			commConfig = new ProxyCommConfig(ip, port, "Proxy");
		} else if (modem) {
			commConfig = new ModemCommConfig(ip, port, "Modem", phoneNumber);
		} else {
			commConfig = new IsdnCommConfig(ip, port, "Router", routerIpAddress);
		}
		return commConfig;
	}

}
