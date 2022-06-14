/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for control the update of periferico
// Change log:
//   2014-09-16: initial version
// ----------------------------------------------------------------------------
// $Id: CustomTask.java,v 1.1 2014/09/16 16:42:32 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import it.csi.centrale.polling.data.Version;

/**
 * Class for control the update of periferico
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class CustomTask {
	private static final String CUSTOMTASK = "usertasks/customtask.sh";
	static Logger logger = Logger.getLogger("centrale." + CustomTask.class.getSimpleName());

	private String host;
	private String stationName;
	private Version perifericoVersion;
	private String commDeviceType;

	CustomTask(String host, String stationName, Version perifericoVersion, String commDeviceType) {
		this.host = host;
		this.stationName = stationName;
		this.perifericoVersion = perifericoVersion;
		this.commDeviceType = commDeviceType;
	}

	void execute() {
		File taskScript = new File(CUSTOMTASK);
		if (!taskScript.exists()) {
			logger.debug("Custom task script '" + taskScript + "' does not exist: skipping execution");
			return;
		}
		if (!taskScript.canExecute()) {
			logger.error("Custom task script '" + taskScript + "' is not executable");
			return;
		}
		String ls = System.getProperty("line.separator");
		ProcessBuilder pb = new ProcessBuilder(
				new String[] { CUSTOMTASK, host, stationName, perifericoVersion.toString(), commDeviceType });
		logger.debug("Excecuting custom task script " + pb.command() + "...");
		Process p = null;
		BufferedReader br = null;
		try {
			p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			StringBuffer errMessage = new StringBuffer();
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (!firstLine)
					errMessage.append(ls);
				errMessage.append(line);
				firstLine = false;
			}
			int result = p.waitFor();
			logger.log(result == 0 ? Level.INFO : Level.ERROR,
					"Custom task script '" + taskScript + "' return code is: " + result);
			if (result != 0)
				logger.error("Custom task script error: '" + errMessage + "'");
		} catch (Exception e) {
			logger.error("Custom task script '" + taskScript + "' execution failed", e);
		} finally {
			if (p != null) {
				try {
					p.getOutputStream().close();
				} catch (IOException e) {
				}
				try {
					p.getInputStream().close();
				} catch (IOException e) {
				}
				try {
					if (br != null)
						br.close();
					else
						p.getErrorStream().close();
				} catch (Exception e) {
				}
			}
		}
	}

}
