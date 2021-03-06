/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage information about Centrale version
// Change log:
//   2008-07-29: initial version
// ----------------------------------------------------------------------------
// $Id: Version.java,v 1.8 2009/06/17 16:02:04 puccio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.log4j.Logger;

/**
 * Class for manage information about Centrale version
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class Version implements Comparable<Object>, Serializable {

	private static final long serialVersionUID = -6342111810668736087L;

	static Logger logger = Logger.getLogger("centrale."
			+ Version.class.getSimpleName());

	private int major;

	private int minor;

	private int bugfix;

	public Version(int major, int minor, int bugfix) {
		this.major = major;
		this.minor = minor;
		this.bugfix = bugfix;
	}

	/**
	 * @return maior number
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return minor number
	 */
	public int getMinor() {
		return minor;
	}

	public int getBugfix() {
		return bugfix;
	}

	@Override
	public String toString() {
		String versionStr = major + "." + minor + "." + bugfix;
		return versionStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bugfix;
		result = prime * result + major;
		result = prime * result + minor;
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
		Version other = (Version) obj;
		if (bugfix != other.bugfix)
			return false;
		if (major != other.major)
			return false;
		return minor == other.minor;
	}

	@Override
	public int compareTo(Object o) {
		Version ver = (Version) o;
		return (major * 100 + minor * 10 + bugfix)
				- (ver.getMajor() * 100 + ver.getMinor() * 10 + ver.getBugfix());
	}
	
	public int compareToMajorRelease(Object o) {
		Version ver = (Version) o;
		return (major) - (ver.getMajor());
	}

	public static Version valueOf(String string) throws ParseException,
			NumberFormatException {
		String[] fields = string.split("\\.", -1);
		if (fields.length != 3)
			throw new ParseException("Three fields needed, found "
					+ fields.length, 0);
		return new Version(Integer.parseInt(fields[0]), Integer
				.parseInt(fields[1]), Integer.parseInt(fields[2]));
	}

}
