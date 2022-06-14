/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Silvia Vergnano
// Purpose of file: servlet for displaying map
// Change log:
//   2008-09-16: initial version
// ----------------------------------------------------------------------------
// $Id: MapsService.java,v 1.8 2015/01/19 12:01:05 vespa Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.server;

import it.csi.centrale.Centrale;
import it.csi.centrale.CentraleException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet for displaying map
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */

public class MapsService extends HttpServlet {

	private static final long serialVersionUID = 6928198845179192008L;

	private static final String IMAGE_PATH = "config/";

	private String locale;

	// Define a static logger variable
	private static Logger logger = Logger.getLogger("mapsservice." + Centrale.class.getSimpleName());

	public MapsService() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			logger.debug("doget di mapsservice");
			String function = request.getParameter("function");
			if (function == null) {
				throw new CentraleException("no_function_specified");
			}
			locale = request.getParameter("locale");
			if ("getMap".equals(function))
				getMap(request, response);
			else {
				throw new CentraleException("unknown_function");
			}
		} catch (CentraleException cEx) {
			sendError(response, cEx.getLocalizedMessage(locale));
		} catch (IllegalArgumentException e) {
			sendError(response, e.getMessage());
		}
	}

	private void getMap(HttpServletRequest request, HttpServletResponse response)
			throws CentraleException, IOException {

		response.setContentType("image/png");

		String imageName = request.getParameter("imagename");
		String imagePath = "";
		if (imageName != null) {
			imagePath = IMAGE_PATH + imageName;
		} else {
			imagePath = IMAGE_PATH + "piemonte.png";
		}
		logger.debug("getmap - imagepath: " + imagePath);
		File file = new File(imagePath);
		if (!file.exists()) {
			logger.error("Errore il file non esiste");
			throw new CentraleException("File '" + file.getAbsolutePath() + "' does not exist");
		}
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		byte[] input = new byte[1024];
		boolean eof = false;
		while (!eof) {
			int length = bis.read(input);
			if (length == -1) {
				eof = true;
			} else {
				bos.write(input, 0, length);
			}
		}
		bos.flush();
		bis.close();
		bos.close();

	} // end getMap

	private void sendError(HttpServletResponse response, String message) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter pw = response.getWriter();
		pw.println(message);
		pw.close();
	}

}
