/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: utility class for methods from reading and writing from 
 * Internal Db
 * Change log:
 *   2008-12-22: initial version
 * ----------------------------------------------------------------------------
 * $Id: DbUtils.java,v 1.13 2014/09/19 15:16:17 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Utility class for methods from reading and writing from Internal Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class DbUtils {

	public static final String CURRENT_TIMESTAMP = "current_timestamp(0)";

	public static String qVal(String value) {
		if (value == null)
			return ("NULL");
		return ("'" + DbUtils.cleanString(value) + "'");
	}

	public static String qVal(Number value) {
		if (value == null)
			return ("NULL");
		return (value.toString());
	}

	public static String qVal(java.util.Date value) {
		if (value == null)
			return ("NULL");
		SimpleDateFormat sdf_timestamp = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return ("'" + sdf_timestamp.format(value) + "'");
	}

	public static String qValWithMilliseconds(java.util.Date value) {
		if (value == null)
			return ("NULL");
		SimpleDateFormat sdf_timestamp_with_milliseconds = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		return ("'" + sdf_timestamp_with_milliseconds.format(value) + "'");
	}

	private static String cleanString(String str) {
		if (str == null)
			return null;
		str = str.replace("\\", "\\\\"); // Questa deve restare la prima!
		str = str.replace("'", "\\'");
		str = str.replace("\"", "\\\"");
		str = str.replace("%", "\\%");
		return str;
	}// end cleanString

	public static String booleanToIntStr(boolean value) {
		if (value)
			return (new Integer(1).toString());
		else
			return (new Integer(0).toString());
	}

	public static String booleanObjToIntStr(Boolean value) {
		if (value == null)
			return ("NULL");
		if (value)
			return (new Integer(1).toString());
		else
			return (new Integer(0).toString());
	}

	public static Integer rsGetInt(ResultSet rs, int column)
			throws SQLException {
		Integer tmp = new Integer(rs.getInt(column));
		if (rs.wasNull())
			return (null);
		return (tmp);
	}

	public static boolean rsGetBooleanFromInt(ResultSet rs, int column)
			throws SQLException {
		int tmp = rs.getInt(column);
		if (tmp == 0) {
			return new Boolean(false);
		} else {
			return new Boolean(true);
		}
	}

	public static Boolean rsGetBooleanObjFromInt(ResultSet rs, int column)
			throws SQLException {
		int tmp = rs.getInt(column);
		if (rs.wasNull())
			return (null);
		if (tmp == 0) {
			return new Boolean(false);
		} else {
			return new Boolean(true);
		}
	}

	public static Double rsGetDouble(ResultSet rs, int column)
			throws SQLException {
		Double tmp = new Double(rs.getDouble(column));
		if (rs.wasNull())
			return (null);
		return (tmp);
	}

	public static Float rsGetFloat(ResultSet rs, int column)
			throws SQLException {
		Float tmp = new Float(rs.getFloat(column));
		if (rs.wasNull())
			return (null);
		return (tmp);
	}

	public static java.util.Date rsGetDate(ResultSet rs, int column)
			throws SQLException {
		Timestamp tmp = rs.getTimestamp(column);
		if (rs.wasNull())
			return (null);
		return (new java.util.Date(tmp.getTime()));
	}

	public static <E extends Enum<E>> E rsGetEnum(Class<E> enumClass,
			ResultSet rs, String columnName) throws SQLException {
		return rsGetEnum(enumClass, rs, rs.findColumn(columnName));
	}

	public static <E extends Enum<E>> E rsGetEnum(Class<E> enumClass,
			ResultSet rs, int column) throws SQLException {
		String tmp = rs.getString(column);
		if (tmp == null)
			return null;
		for (E e : enumClass.getEnumConstants())
			if (tmp.equals(e.name()))
				return e;
		throw new IllegalArgumentException("Undefined constant '" + tmp
				+ "' in enum class '" + enumClass.getSimpleName() + "'");
	}

	public static <E extends Enum<E>> String qVal(E value) {
		if (value == null)
			return ("NULL");
		return ("'" + value.name() + "'");
	}

}
