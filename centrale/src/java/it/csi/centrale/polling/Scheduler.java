/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Scheduler for communication manager
// Change log:
//   2014-03-27: initial version
// ----------------------------------------------------------------------------
// $Id: Scheduler.java,v 1.13 2015/11/13 15:20:19 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

import it.csi.centrale.Centrale;
import it.csi.centrale.comm.CommManager;
import it.csi.centrale.comm.Connection;
import it.csi.centrale.comm.ConnectionException.Failure;
import it.csi.centrale.comm.DeviceBusyException;
import it.csi.centrale.comm.DeviceNotFoundException;
import it.csi.centrale.comm.IncompatibleDeviceException;
import it.csi.centrale.comm.config.CommConfig;
/**
 * Scheduler for communication manager
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class Scheduler implements Runnable {

	private static final int MAX_POLL_ATTEMPTS = 3;
	private static final int MAX_POLL_ATTEMPTS_DELAYED = MAX_POLL_ATTEMPTS + 3;
	private static final int POLLING_RETRY_DELAY_MIN_m = 2;
	private static final int POLLING_RETRY_DELAY_MAX_m = 10;
	private static final String THREAD_BASE_NAME = "scheduler";
	private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY + 1;
	private static final int THREAD_SLEEP_TIME = 1; // seconds
	private static int idCounter = 0;
	private static Logger logger = Logger.getLogger("centrale."
			+ Scheduler.class.getSimpleName());

	private CommManager commManager;
	private int pollingPeriod_m;
	private int pollingOffset_s;
	private Integer restPollingPeriod_m;
	private boolean restOnSaturday;
	private boolean restOnSunday;
	private Integer workTime_m;
	private Integer workSpan_m;
	private volatile Thread schedulerThread = null;
	private List<Pollable> pollableItems = null;
	private List<Pollable> waitList = new ArrayList<Pollable>();
	private Map<Pollable, Date> delayedWaitMap = new HashMap<Pollable, Date>();
	private List<PollingThread> pollingThreads = new ArrayList<PollingThread>();

	public Scheduler(CommManager commManager, int pollingPeriod_m,
			int pollingOffset_s, Integer restPollingPeriod_m,
			boolean restOnSaturday, boolean restOnSunday, Integer workTime_m,
			Integer workSpan_m) {
		if (commManager == null)
			throw new IllegalArgumentException(
					"Communication Manager should not be null");
		this.commManager = commManager;
		setPollingPeriod_m(pollingPeriod_m);
		setPollingOffset_s(pollingOffset_s);
		setRestPollingPeriod_m(restPollingPeriod_m);
		setRestOnSunday(restOnSunday);
		setRestOnSaturday(restOnSaturday);
		setWorkTime_m(workTime_m);
		setWorkSpan_m(workSpan_m);
	}

	public CommManager getCommManager() {
		return commManager;
	}

	public int getPollingPeriod_m() {
		return pollingPeriod_m;
	}

	public void setPollingPeriod_m(int pollingPeriod_m) {
		if (pollingPeriod_m < 1 || pollingPeriod_m > 1440)
			throw new IllegalArgumentException("Polling period should be in"
					+ " the range 1-1440 minutes, found " + pollingPeriod_m);
		if (restPollingPeriod_m != null
				&& pollingPeriod_m > restPollingPeriod_m)
			throw new IllegalArgumentException("Polling period should not"
					+ " be greater than Rest polling period");
		this.pollingPeriod_m = pollingPeriod_m;
	}

	public int getPollingOffset_s() {
		return pollingOffset_s;
	}

	public void setPollingOffset_s(int pollingOffset_s) {
		if (pollingPeriod_m < 0 || pollingPeriod_m > 600)
			throw new IllegalArgumentException("Polling offset should be in"
					+ " the range 0-600 seconds, found " + pollingOffset_s);
		this.pollingOffset_s = pollingOffset_s;
	}

	public Integer getRestPollingPeriod_m() {
		return restPollingPeriod_m;
	}

	public void setRestPollingPeriod_m(Integer restPollingPeriod_m) {
		if (restPollingPeriod_m != null
				&& (restPollingPeriod_m < 1 || restPollingPeriod_m > 1440))
			throw new IllegalArgumentException("Rest polling period should be"
					+ " in the range 1-1440 minutes, found "
					+ restPollingPeriod_m);
		if (restPollingPeriod_m != null
				&& restPollingPeriod_m < pollingPeriod_m)
			throw new IllegalArgumentException("Rest polling period should not"
					+ " be less than Polling period");
		if (workSpan_m != null && restPollingPeriod_m != null
				&& restPollingPeriod_m > workSpan_m)
			throw new IllegalArgumentException("Rest polling period should not"
					+ " be greater than Work span ");

		this.restPollingPeriod_m = restPollingPeriod_m;
	}

	public boolean isRestOnSaturday() {
		return restOnSaturday;
	}

	public void setRestOnSaturday(boolean restOnSaturday) {
		this.restOnSaturday = restOnSaturday;
	}

	public boolean isRestOnSunday() {
		return restOnSunday;
	}

	public void setRestOnSunday(boolean restOnSunday) {
		this.restOnSunday = restOnSunday;
	}

	public Integer getWorkTime_m() {
		return workTime_m;
	}

	public void setWorkTime_m(Integer workTime_m) {
		if (workTime_m != null && (workTime_m < 0 || workTime_m > 1440))
			throw new IllegalArgumentException("Work time should be"
					+ " in the range 0-1440 minutes, found " + workTime_m);
		this.workTime_m = workTime_m;
	}

	public Integer getWorkSpan_m() {
		return workSpan_m;
	}

	public void setWorkSpan_m(Integer workSpan_m) {
		if (workSpan_m != null && (workSpan_m < 0 || workSpan_m > 1440))
			throw new IllegalArgumentException("Work span should be"
					+ " in the range 0-1440 minutes, found " + workTime_m);
		if (workSpan_m != null && restPollingPeriod_m != null
				&& workSpan_m < restPollingPeriod_m)
			throw new IllegalArgumentException("Work span should not"
					+ " be less than Rest polling period");
		this.workSpan_m = workSpan_m;
	}

	public synchronized void start() {
		schedulerThread = new Thread(this, THREAD_BASE_NAME + "-" + ++idCounter);
		schedulerThread.setDaemon(false);
		schedulerThread.setPriority(THREAD_PRIORITY);
		schedulerThread.start();
	}

	public synchronized void stop() {
		Thread tmpThread = schedulerThread;
		schedulerThread = null;
		if (tmpThread != null) {
			logger.info("Scheduler thread shutdown in progress...");
			try {
				int maxWaitTime_s = 60;
				logger.info("Waiting up to " + maxWaitTime_s
						+ "s for scheduler to terminate...");
				tmpThread.join(maxWaitTime_s * 1000);
				logger.info("Finished waiting for scheduler to terminate");
			} catch (InterruptedException ie) {
				logger.error("Wait for scheduler to terminate interrupted");
			}
			for (PollingThread pt : pollingThreads)
				pt.stop();
			waitList.clear();
			delayedWaitMap.clear();
			pollingThreads.clear();
		}
	}

	public void run() {
		logger.info("Scheduler thread started");
		logMemory(true);
		initCustomPollingPeriods();
		long nextPollingTime = computeNextPollingTime(System
				.currentTimeMillis());
		logger.debug("Next polling time is " + new Date(nextPollingTime));
		while (Thread.currentThread() == schedulerThread) {
			manageCustomPollingPeriods();
			Date currentTime = new Date();
			if (currentTime.getTime() >= nextPollingTime) {
				nextPollingTime = computeNextPollingTime(currentTime.getTime());
				logMemory(true);
				logger.debug("Next polling time is "
						+ new Date(nextPollingTime));
				pollDefaultScheduledItems();
			} else if (computeNextPollingTime(currentTime.getTime()) < nextPollingTime) {
				// This code may run only in case of clock adjustment !
				nextPollingTime = computeNextPollingTime(currentTime.getTime());
				logger.warn("Next polling time recomputed due to clock adjustment");
			}
			managePollingThreads();
			manageWaitList();
			try {
				if (Thread.currentThread() == schedulerThread)
					Thread.sleep(THREAD_SLEEP_TIME * 1000);
			} catch (InterruptedException e) {
				logger.warn("Interruption requested for scheduler thread", e);
				break;
			}
		}
		logger.info("Scheduler thread stopped");
	}

	private synchronized void initCustomPollingPeriods() {
		Date currentTime = new Date();
		for (Pollable p : pollableItems) {
			if (p.getCustomPollingPeriod_m() == null)
				continue;
			int customPeriod_m = p.getCustomPollingPeriod_m();
			p.setNextCustomPollingTime_ms(computeNextTime(
					currentTime.getTime(), customPeriod_m));
		}
	}

	private synchronized void manageCustomPollingPeriods() {
		Date currentTime = new Date();
		for (Pollable p : pollableItems) {
			if (p.getCustomPollingPeriod_m() == null)
				continue;
			int customPeriod_m = p.getCustomPollingPeriod_m();
			if (currentTime.getTime() >= p.getNextCustomPollingTime_ms()) {
				long time = computeNextTime(currentTime.getTime(),
						customPeriod_m);
				p.setNextCustomPollingTime_ms(time);
				logger.debug("Next polling time for station " + p.getName()
						+ " is " + new Date(time));
				poll(p);
			} else if (computeNextTime(currentTime.getTime(), customPeriod_m) < p
					.getNextCustomPollingTime_ms()) {
				// This code may run only in case of clock adjustment !
				p.setNextCustomPollingTime_ms(computeNextTime(
						currentTime.getTime(), customPeriod_m));
				logger.warn("Next polling time for " + p.getName()
						+ " recomputed due to clock adjustment");
			}
		}
	}

	// Requisiti: workSpan_m >= restPollingPeriod_m >= pollingPeriod_m
	private long computeNextPollingTime(long currTime_ms) {
		long nextTime_ms = computeNextTime(currTime_ms, pollingPeriod_m);
		if (restPollingPeriod_m == null)
			return nextTime_ms;
		if (!isInRestTime(nextTime_ms))
			return nextTime_ms;
		long nextRestTime_ms = computeNextTime(currTime_ms, restPollingPeriod_m);
		if (isInRestTime(nextRestTime_ms))
			return nextRestTime_ms;
		return computeNextPollingTime(nextTime_ms);
	}

	private long computeNextTime(long currTime_ms, long period_m) {
		long period_ms = period_m * 60000;
		return (currTime_ms / period_ms) * period_ms + period_ms
				+ pollingOffset_s * 1000;
	}

	private boolean isInRestTime(long time_ms) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time_ms);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int time_m = cal.get(Calendar.HOUR_OF_DAY) * 60
				+ cal.get(Calendar.MINUTE);
		return (restOnSunday && Calendar.SUNDAY == dayOfWeek)
				|| (restOnSaturday && Calendar.SATURDAY == dayOfWeek)
				|| (workTime_m != null && time_m < workTime_m)
				|| (workTime_m != null && workSpan_m != null && time_m > workTime_m
						+ workSpan_m);
	}

	public synchronized void setPollableItems(
			Collection<? extends Pollable> items) {
		List<Pollable> tmpList = new ArrayList<Pollable>();
		if (items != null)
			tmpList.addAll(items);
		pollableItems = tmpList;
	}

	private synchronized void pollDefaultScheduledItems() {
		logger.info("Scheduling for polling all stations without custom period");
		for (Pollable p : pollableItems) {
			if (p.getCustomPollingPeriod_m() != null)
				continue;
			if (!waitList.contains(p))
				waitList.add(p);
			delayedWaitMap.remove(p);
		}
	}

	public synchronized void pollAll() {
		logger.info("Scheduling for polling all stations");
		for (Pollable p : pollableItems) {
			if (!waitList.contains(p))
				waitList.add(p);
			delayedWaitMap.remove(p);
		}
	}

	public synchronized void pollGroup(int groupId) {
		logger.info("Scheduling for polling all stations with group id "
				+ groupId);
		for (Pollable p : pollableItems) {
			if (p.getGroupId() != null && p.getGroupId() == groupId) {
				if (!waitList.contains(p))
					waitList.add(p);
				delayedWaitMap.remove(p);
			}
		}
	}

	public synchronized void poll(Pollable item) {
		if (item == null)
			throw new IllegalArgumentException("Station argument missing");
		logger.info("Scheduling for polling station " + item.getName());
		if (!waitList.contains(item))
			waitList.add(item);
		delayedWaitMap.remove(item);
	}

	public synchronized void poll(Collection<? extends Pollable> items) {
		if (items == null)
			throw new IllegalArgumentException(
					"Station collection argument missing");
		logger.info("Scheduling for polling the stations from supplied list");
		for (Pollable p : items) {
			if (!waitList.contains(p))
				waitList.add(p);
			delayedWaitMap.remove(p);
		}
	}

	private synchronized void manageWaitList() {
		if (waitList.isEmpty() && !delayedWaitMap.isEmpty()) {
			Iterator<Pollable> itDelayed = delayedWaitMap.keySet().iterator();
			while (itDelayed.hasNext()) {
				Pollable p = itDelayed.next();
				Date reschedulingDate = delayedWaitMap.get(p);
				if (reschedulingDate == null
						|| new Date().after(reschedulingDate)) {
					logger.info("Rescheduling station " + p.getName() + " for "
							+ "polling, suspended after " + MAX_POLL_ATTEMPTS
							+ " connection failures");
					itDelayed.remove();
					if (!waitList.contains(p))
						waitList.add(p);
				}
			}
		}
		if (waitList.isEmpty())
			return;
		logger.trace("Found " + waitList.size()
				+ " stations waiting for polling");
		ListIterator<Pollable> itWaitlList = waitList.listIterator();
		while (itWaitlList.hasNext()) {
			Pollable p = itWaitlList.next();
			if (!p.isEnabled()) {
				logger.debug("Removed station " + p.getName() + " from wait"
						+ " list because it is not enabled for polling");
				itWaitlList.remove();
				continue;
			}
			CommConfig commConfig = p.getCommunicationConfig();
			if (commConfig == null) {
				logger.warn("Removed station " + p.getName() + " from wait"
						+ " list because it does not have a communication"
						+ " configuration");
				itWaitlList.remove();
				continue;
			}
			PollingStatus ps = p.getPollingStatus();
			if (ps == PollingStatus.RUNNING || ps == PollingStatus.CONNECTING) {
				logger.warn("Skipped station " + p.getName() + " waiting"
						+ " for previous polling to complete");
				continue;
			}
			Connection conn;
			try {
				conn = commManager.getConnection(commConfig, false);
			} catch (DeviceNotFoundException e) {
				logger.warn("Removed station " + p.getName() + " from wait"
						+ " list because its communication configuration"
						+ " does not have an associated device", e);
				itWaitlList.remove();
				continue;
			} catch (IncompatibleDeviceException e) {
				logger.warn("Removed station " + p.getName() + " from wait"
						+ " list because its communication configuration"
						+ " does not have a compatible device", e);
				itWaitlList.remove();
				continue;
			} catch (DeviceBusyException e) {
				logger.trace("Skipped station " + p.getName() + " waiting for"
						+ " an available communication device", e);
				p.setPollingStatus(PollingStatus.WAITING);
				continue;
			}
			itWaitlList.remove();
			PollingThread pt = new PollingThread(p, conn);
			pt.start();
			pollingThreads.add(pt);
		}
		if (waitList.isEmpty())
			logger.debug("No more stations waiting for polling");
	}

	private Date getRandomRetryDelay() {
		double retryDelay_s = POLLING_RETRY_DELAY_MIN_m
				+ (POLLING_RETRY_DELAY_MAX_m - POLLING_RETRY_DELAY_MIN_m)
				* Math.random();
		return new Date(((long) (retryDelay_s * 60000))
				+ System.currentTimeMillis());
	}

	private synchronized void managePollingThreads() {
		if (pollingThreads.isEmpty())
			return;
		logger.trace("Found " + pollingThreads.size() + " polling threads");
		ListIterator<PollingThread> itThreads = pollingThreads.listIterator();
		while (itThreads.hasNext()) {
			PollingThread pt = itThreads.next();
			if (pt.isRunning())
				continue;
			itThreads.remove();
			Pollable pollable = pt.getPollable();
			Failure f = pollable.getConnectionFailure();
			if (f == Failure.REMOTE_DEVICE || f == Failure.REMOTE_HOST
					|| pollable.getPollingStatus() == PollingStatus.IO_ERROR) {
				if (pollable.getConsecutiveConnectionFailuresCount() < MAX_POLL_ATTEMPTS) {
					if (!waitList.contains(pollable)) {
						logger.warn("Rescheduling station "
								+ pollable.getName() + " for polling");
						waitList.add(pollable);
					}
				} else if (pollable.getConsecutiveConnectionFailuresCount() < MAX_POLL_ATTEMPTS_DELAYED) {
					Date when = getRandomRetryDelay();
					logger.warn("Station " + pollable.getName() + " will be "
							+ "rescheduled for delayed polling at " + when);
					delayedWaitMap.put(pollable, when);
				} else {
					logger.warn("Giving up rescheduling station "
							+ pollable.getName() + ": consecutive connection "
							+ "failures is "
							+ pollable.getConsecutiveConnectionFailuresCount()
							+ ", maximum allowed is "
							+ MAX_POLL_ATTEMPTS_DELAYED);
				}
			}
		}
		if (pollingThreads.isEmpty()) {
			logger.debug("All polling threads completed");
			new MossheInterface().createStatusFile(Centrale.getInstance()
					.getConfigInfo().getStVector());
		}
	}

	private void logMemory(boolean logInfo) {
		if (logInfo)
			logger.info(printMemory());
		else if (logger.isDebugEnabled())
			logger.debug(printMemory());
	}

	private String printMemory() {
		Runtime rt = java.lang.Runtime.getRuntime();
		long usedMem = rt.totalMemory() - rt.freeMemory();
		return "JVM heap memory used/total: " + (usedMem / 1024) + "/"
				+ (rt.totalMemory() / 1024) + " KiB";
	}

}
