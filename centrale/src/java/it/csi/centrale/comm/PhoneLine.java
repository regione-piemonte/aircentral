/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class that implements phone line
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: PhoneLine.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm;

/**
 * Class that implements phone line
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class PhoneLine {

	private String name;
	private int totalChannels;
	private int reservedChannels;

	public PhoneLine(String name, int totalChannels, int reservedChannels) {
		setName(name);
		setTotalChannels(totalChannels);
		setReservedChannels(reservedChannels);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Phone line name not specified");
		this.name = name;
	}

	public int getTotalChannels() {
		return totalChannels;
	}

	public void setTotalChannels(int totalChannels) {
		if (totalChannels < 1)
			throw new IllegalArgumentException("The number of channels" + " should be greater than 0");
		this.totalChannels = totalChannels;
	}

	public int getReservedChannels() {
		return reservedChannels;
	}

	public void setReservedChannels(int reservedChannels) {
		if (reservedChannels < 0)
			throw new IllegalArgumentException("The number of reserved" + " channels should not be negative");
		this.reservedChannels = reservedChannels;
	}

}
