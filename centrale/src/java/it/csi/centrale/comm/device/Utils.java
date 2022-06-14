/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Utils class for connection
// Change log:
//   2014-03-26: initial version
// ----------------------------------------------------------------------------
// $Id: Utils.java,v 1.3 2014/10/10 15:57:35 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Utils class for connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class Utils {

	private static Logger logger = Logger.getLogger("centrale." + Utils.class.getSimpleName());

	public static boolean ping(String host, int timeout_s) throws IOException, InterruptedException {
		return 0 == execute("ping", "-c", "1", "-w", Integer.toString(timeout_s), "-n", host);
	}

	public static boolean socketTest(String host, int port, int timeout_s) {
		try {
			Socket client = new Socket(host, port);
			client.setSoTimeout(timeout_s * 1000);
			InputStream is = client.getInputStream();
			is.close();
			return true;
		} catch (Exception ex) {
			logger.debug("Socket test failed for host " + host + ", port " + port + ": " + ex.getLocalizedMessage());
			return false;
		}
	}

	public static boolean httpGetTest(String host, int port, int timeout_s) {
		return httpGetTest(host, port, timeout_s, null);
	}

	public static boolean httpGetTest(String host, int port, int timeout_s, Proxy proxy) {
		try {
			URL url = new URL("http://" + host + ":" + port);
			URLConnection conn = proxy == null ? url.openConnection() : url.openConnection(proxy);
			conn.setUseCaches(false);
			conn.setConnectTimeout(timeout_s * 1000);
			conn.setReadTimeout(timeout_s * 1000);
			InputStream is = conn.getInputStream();
			is.close();
			return true;
		} catch (Exception ex) {
			logger.debug("HTTP GET test failed for host " + host + ", port " + port + ": " + ex.getLocalizedMessage());
			return false;
		}
	}

	public static int execute(String... command) throws IOException, InterruptedException {
		return execute(null, null, command);
	}

	/**
	 * @param out
	 * @param err
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execute(StringBuilder out, StringBuilder err, String... command)
			throws IOException, InterruptedException {
		logger.debug("Execution of command: '" + Arrays.toString(command) + "'");
		ProcessBuilder pb = new ProcessBuilder(command);
		Process p = null;
		InputStream isStdOut = null;
		InputStream isStdErr = null;
		OutputStream osStdIn = null;
		try {
			p = pb.start();
			isStdOut = p.getInputStream();
			isStdErr = p.getErrorStream();
			osStdIn = p.getOutputStream();
			byte[] buffOut = new byte[256];
			byte[] buffErr = new byte[256];
			int pOut = 0;
			int pErr = 0;
			do {
				while (isStdOut.available() > 0) {
					int ch = isStdOut.read();
					if (ch != -1 && out != null) {
						buffOut[pOut++] = (byte) ch;
						if (pOut == buffOut.length) {
							out.append(new String(buffOut, 0, pOut));
							pOut = 0;
						}
					}
				}
				while (isStdErr.available() > 0) {
					int ch = isStdErr.read();
					if (ch != -1 && err != null) {
						buffErr[pErr++] = (byte) ch;
						if (pErr == buffErr.length) {
							err.append(new String(buffErr, 0, pErr));
							pErr = 0;
						}
					}
				}
				if (isStdOut.available() == 0 && isStdErr.available() == 0)
					Thread.sleep(250);
			} while ((isStdOut.available() > 0 || isStdErr.available() > 0) || isRunning(p));
			if (out != null)
				out.append(new String(buffOut, 0, pOut));
			if (err != null)
				err.append(new String(buffErr, 0, pErr));
			return p.waitFor();
		} finally {
			if (p != null) {
				try {
					osStdIn.close();
				} catch (IOException e) {
				}
				try {
					isStdOut.close();
				} catch (IOException e) {
				}
				try {
					isStdErr.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static boolean isRunning(Process process) {
		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			return true;
		}
		return false;
	}

}
