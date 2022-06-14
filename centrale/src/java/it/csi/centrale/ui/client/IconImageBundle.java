/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: create bundles of static images that can be downloaded 
 * 					in a single HTTP request.
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: IconImageBundle.java,v 1.20 2014/09/18 09:46:57 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Create bundles of static images that can be downloaded in a single HTTP
 * request.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public interface IconImageBundle extends ClientBundle {
	/**
	 * Would match the file 'ledgreen.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/ledgreen.png")
	public ImageResource ledGreen();

	/**
	 * Would match the file 'ledred.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/ledred.png")
	public ImageResource ledRed();

	/**
	 * Would match the file 'ledgray.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/ledgray.png")
	public ImageResource ledGray();

	/**
	 * Would match the file 'ledyellow.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/ledyellow.png")
	public ImageResource ledYellow();

	/**
	 * Would match the file 'warning_high.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/warning_high.png")
	public ImageResource warningHigh();

	/**
	 * Would match the file 'warning_low.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/warning_low.png")
	public ImageResource warningLow();

	/**
	 * Would match the file 'alarm_high.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/alarm_high.png")
	public ImageResource alarmHigh();

	/**
	 * Would match the file 'alarm_low.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/alarm_low.png")
	public ImageResource alarmLow();

	/**
	 * Would match the file 'informatic_status_gray_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/informatic_status_gray_small.png")
	public ImageResource informaticStatusGray();

	/**
	 * Would match the file 'informatic_status_ko_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/informatic_status_ko_small.png")
	public ImageResource informaticStatusKo();

	/**
	 * Would match the file 'informatic_status_ok_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/informatic_status_ok_small.png")
	public ImageResource informaticStatusOk();

	/**
	 * Would match the file 'informatic_status_warning_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/informatic_status_warning_small.png")
	public ImageResource informaticStatusWarning();

	/**
	 * Would match the file 'station_alarm_gray_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/station_alarm_gray_small.png")
	public ImageResource stationStatusGray();

	/**
	 * Would match the file 'station_alarm_ko_small.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/station_alarm_ko_small.png")
	public ImageResource stationStatusKo();

	/**
	 * Would match the file 'station_alarm_ok_small.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/station_alarm_ok_small.png")
	public ImageResource stationStatusOk();

	/**
	 * Would match the file 'station_alarm_warning_small.png' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/station_alarm_warning_small.png")
	public ImageResource stationStatusWarning();

	/**
	 * Would match the file 'time_gray.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/time_gray.png")
	public ImageResource clockGray();

	/**
	 * Would match the file 'time_green.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/time_green.png")
	public ImageResource clock();

	/**
	 * Would match the file 'time_yellow.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/time_yellow.png")
	public ImageResource clockWarning();

	/**
	 * Would match the file 'time_red.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/time_red.png")
	public ImageResource clockError();

	/**
	 * Would match the file 'com_gray_no_frecce.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/com_gray_no_frecce.png")
	public ImageResource communicationGray();

	/**
	 * Would match the file 'router_router_ok_animated.gif' located in the
	 * package 'it.csi.centrale.ui.public.images', provided that this package is
	 * part of the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/router_router_ok_animated.gif")
	public ImageResource calling();

	/**
	 * Would match the file 'router_router_ko.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/router_router_ko.png")
	public ImageResource routerNotResponding();

	/**
	 * Would match the file 'router_locale_ko.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/router_locale_ko.png")
	public ImageResource routerLocaleNotResponding();

	/**
	 * Would match the file 'pc_not_responding.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/pc_not_responding.png")
	public ImageResource pcNotResponding();

	/**
	 * Would match the file 'com_ko.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/com_ko.png")
	public ImageResource swNotResponding();

	/**
	 * Would match the file 'com_ok.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/com_ok.png")
	public ImageResource pollingFinishedOk();

	/**
	 * Would match the file 'caduta_linea.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/caduta_linea.png")
	public ImageResource noLine();

	/**
	 * Would match the file 'open_door.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/open_door.png")
	public ImageResource doorOpen();

	/**
	 * Would match the file 'close_door.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/close_door.png")
	public ImageResource doorClose();

	/**
	 * Would match the file 'door_gray.png' located in the package
	 * 'it.csi.centrale.ui.public.images', provided that this package is part of
	 * the module's classpath.
	 */
	@Source("it/csi/centrale/ui/public/images/door_gray.png")
	public ImageResource doorGray();

}
