/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Connection class
// Change log:
//   2014-03-14: initial version
// ----------------------------------------------------------------------------
// $Id: Connection.java,v 1.4 2014/09/16 16:42:32 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm;

import it.csi.centrale.comm.config.CommConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Connection class
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public abstract class Connection {

	private static Logger logger = Logger.getLogger("centrale." + Connection.class.getSimpleName());
	private static long count = 0;
	private CommDevice device;
	private CommConfig config;
	private long id = ++count;

	protected Connection(CommDevice device, CommConfig config) {
		if (device == null)
			throw new IllegalArgumentException("Communication device" + " not specified");
		this.device = device;
		if (config == null)
			throw new IllegalArgumentException("Communication configuration" + " not specified");
		this.config = config;
	}

	/**
	 * Start the connection
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void start() throws ConnectionException, IOException, InterruptedException {
		checkReleased();
		startImpl();
	}

	/**
	 * Test the connection
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void test() throws ConnectionException, IOException, InterruptedException {
		checkReleased();
		testImpl();
	}

	/**
	 * Restart the connection
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void restart() throws ConnectionException, IOException, InterruptedException {
		checkReleased();
		restartImpl();
	}

	/**
	 * Release the connection
	 */
	public void release() {
		stopImpl();
		device.releaseConnection(this);
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return config.getHost();
	}

	/**
	 * @return the port number
	 */
	public int getPort() {
		return config.getPort();
	}

	/**
	 * @param request
	 * @return the url connection
	 * @throws IOException
	 */
	public URLConnection getHttpConnection(String request) throws IOException {
		checkReleased();
		URL url = new URL("http://" + config.getHost() + ":" + config.getPort() + request);
		return openURLConnection(url);
	}

	public BufferedReader execHttpGet(String request, int connectTimeout_ms, int readTimeout_ms) throws IOException {
		logger.trace("Http GET: '" + request + "'");
		URLConnection uc = getHttpConnection(request);
		uc.setDoInput(true);
		uc.setDoOutput(false);
		uc.setUseCaches(false);
		uc.setConnectTimeout(connectTimeout_ms);
		uc.setReadTimeout(readTimeout_ms);
		return new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
	}

	public BufferedReader execHttpPost(String request, int connectTimeout_ms, int readTimeout_ms, List<String> content)
			throws IOException {
		logger.trace("Http POST: '" + request + "'");
		URLConnection uc = getHttpConnection(request);
		uc.setDoInput(true);
		uc.setDoOutput(true);
		uc.setUseCaches(false);
		uc.setConnectTimeout(connectTimeout_ms);
		uc.setReadTimeout(readTimeout_ms);
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		for (String s : content)
			pw.println(s);
		pw.flush();
		pw.close();
		return new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
	}

	public long getId() {
		return id;
	}

	private void checkReleased() {
		if (!device.hasConnection(this))
			throw new IllegalStateException("Connection already released");
	}

	abstract public String getTypeName();

	abstract protected void startImpl() throws ConnectionException, IOException, InterruptedException;

	abstract protected void testImpl() throws ConnectionException, IOException, InterruptedException;

	abstract protected void restartImpl() throws ConnectionException, IOException, InterruptedException;

	abstract protected void stopImpl();

	protected URLConnection openURLConnection(URL url) throws IOException {
		return url.openConnection(java.net.Proxy.NO_PROXY);
	}

}
