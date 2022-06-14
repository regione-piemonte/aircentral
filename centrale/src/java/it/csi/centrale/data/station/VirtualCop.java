/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
/**
 * Class for storing information about  a virtual cop
 * 
 * @author Isabella Vespa - CSI Piemonte 
 * 
 */
public class VirtualCop implements Serializable {

	private static final long serialVersionUID = -5768851392968734119L;
	private Integer virtualCopId;
	private String name;
	private Integer configId;
	private String mapName;

	public Integer getVirtualCopId() {
		return virtualCopId;
	}

	public void setVirtualCopId(Integer virtualCopId) {
		this.virtualCopId = virtualCopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

}
