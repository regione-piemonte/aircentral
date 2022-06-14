/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class for virtual cop information
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class VirtualCopInfo implements Serializable {

	private static final long serialVersionUID = 546841249574233104L;

	private Integer virtualCopId;
	private String copName;

	/**
	 * @return the virtualCopId
	 */
	public Integer getVirtualCopId() {
		return virtualCopId;
	}

	/**
	 * @param virtualCopId the virtualCopId to set
	 */
	public void setVirtualCopId(Integer virtualCopId) {
		this.virtualCopId = virtualCopId;
	}

	/**
	 * @return the copName
	 */
	public String getCopName() {
		return copName;
	}

	/**
	 * @param copName the copName to set
	 */
	public void setCopName(String copName) {
		this.copName = copName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCopName() == null) ? 0 : getCopName().hashCode());
		result = prime * result + ((getVirtualCopId() == null) ? 0 : getVirtualCopId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualCopInfo other = (VirtualCopInfo) obj;
		if (getCopName() == null) {
			if (other.getCopName() != null)
				return false;
		} else if (!getCopName().equals(other.getCopName()))
			return false;
		if (getVirtualCopId() == null) {
			if (other.getVirtualCopId() != null)
				return false;
		} else if (!getVirtualCopId().equals(other.getVirtualCopId()))
			return false;
		return true;
	}

}
