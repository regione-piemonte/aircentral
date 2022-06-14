/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for making a copy of an object
 * Change log:
 *   2008-10-15: initial version
 * ----------------------------------------------------------------------------
 * $Id: ObjectCopier.java,v 1.6 2009/11/20 09:50:22 puccio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * Class for making a copy of an object
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class ObjectCopier {

	static Logger logger = Logger.getLogger("centrale."
			+ ObjectCopier.class.getSimpleName());

	public static Object copy(Object original) throws IOException,
			ClassNotFoundException {

		Object obj = null;
		// try{ //write object to a byte array
		ByteArrayOutputStream byteArOutStr = new ByteArrayOutputStream();
		ByteArrayInputStream byteArInStr;
		logger.debug("copy() ByteArrayInput and OutputStream created");
		ObjectOutputStream outObj = new ObjectOutputStream(byteArOutStr);
		//logger.debug("copy() ObjectOutputStream created" + original);
		outObj.writeObject(original);
		//logger.debug("copy() ObjectOutputStream wrote");
		outObj.flush();
		outObj.close();
		//logger.debug("copy() ObjectOutputStream closed");
		byteArInStr = new ByteArrayInputStream(byteArOutStr.toByteArray());
		//logger.debug("copy() ByteArrayInputStream loaded");

		ObjectInputStream inStream = new ObjectInputStream(byteArInStr);
		//logger.debug("copy() ObjectInputStream initialized");
		obj = inStream.readObject();
		logger.info("copy() Object copied successfully");

		// }catch (IOException e){
		// logger.error("copy() - can't create Input or Output Stream to Serialization");
		// }catch (ClassNotFoundException cnfe){
		// logger.error("copy() - can't find Class to trasform Object to definition");
		// }
		return obj;
	}
}
