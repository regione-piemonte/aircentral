/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for utilities for Centrale application
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: CentraleUtil.java,v 1.4 2014/09/09 10:25:50 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for utilities for Centrale application
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class CentraleUtil {

	public static boolean areEqual(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;
		if (obj1 == null || obj2 == null)
			return false;
		return obj1.equals(obj2);
	}

	public static Object copy(Object objOriginal) throws IOException,
			ClassNotFoundException {
		if (objOriginal == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(objOriginal);
		oos.flush();
		oos.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object objCopy = ois.readObject();
		return (objCopy);
	}

	public static String toString(Object object) {
		return object == null ? null : object.toString();
	}

}
