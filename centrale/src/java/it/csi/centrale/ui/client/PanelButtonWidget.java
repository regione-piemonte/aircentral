/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file:  a composite Widget that implements the button panel for
				the centrale.
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: PanelButtonWidget.java,v 1.32 2015/01/19 12:01:05 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A composite Widget that implements the button panel for the centrale.
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class PanelButtonWidget extends Composite {

	public static final int NO_COLOR = 0;

	public static final int ORANGE = 1;

	public static final int BLUE = 2;

	private Vector<Integer> buttonTypes;

	private HorizontalPanel hPanel;

	private static final int DEFAULT_BUTTON_SPACING = 10;

	private int color;

	public PanelButtonWidget(int color) {
		this.color = color;
		buttonTypes = new Vector<Integer>();
		hPanel = new HorizontalPanel();
		hPanel.setSpacing(10);
		initWidget(hPanel);
	}

	public PanelButtonWidget(int color, int spacing) {
		this.color = color;
		buttonTypes = new Vector<Integer>();
		hPanel = new HorizontalPanel();
		hPanel.setSpacing(spacing);
		initWidget(hPanel);
	}

	public PanelButtonWidget() {
		this(DEFAULT_BUTTON_SPACING);
	}

	public void addButton(Button button) {
		hPanel.add(button);
	}

	public Button addButton(Integer buttonType, ClickHandler clickHandler) {
		Button currentButton = new Button();
		setButtonType(currentButton, buttonType);
		currentButton.addClickHandler(clickHandler);
		hPanel.add(currentButton);
		buttonTypes.add(buttonType);
		return currentButton;
	}

	public void setEnabledButton(Integer buttonType, boolean enabled) {
		for (int i = 0; i < buttonTypes.size(); i++) {
			if (buttonTypes.get(i).equals(buttonType)) {
				Button button = ((Button) (hPanel.getWidget(i)));
				button.setEnabled(enabled);
				if (enabled && buttonType.equals(CentraleUIConstants.INFORMATIC_STATUS))
					button.setStyleName("gwt-button-informatic_status");
				if (!enabled && buttonType.equals(CentraleUIConstants.INFORMATIC_STATUS))
					button.setStyleName("gwt-button-informatic_status-disabled");
				if (enabled && buttonType.equals(CentraleUIConstants.ANALYZER_STATUS))
					button.setStyleName("gwt-button-analyzers_status");
				if (!enabled && buttonType.equals(CentraleUIConstants.ANALYZER_STATUS))
					button.setStyleName("gwt-button-analyzers_status-disabled");
				if (enabled && buttonType.equals(CentraleUIConstants.STATION_STATUS))
					button.setStyleName("gwt-button-station_status");
				if (!enabled && buttonType.equals(CentraleUIConstants.STATION_STATUS))
					button.setStyleName("gwt-button-station_status-disabled");
			}
		}
	}

	public void setVisibleButton(Integer buttonType, boolean visible) {
		for (int i = 0; i < buttonTypes.size(); i++) {
			if (buttonTypes.get(i).equals(buttonType)) {
				((Button) (hPanel.getWidget(i))).setVisible(visible);
			}
		}
	}

	public boolean containsButtonType(Integer buttonType) {
		return buttonTypes.contains(buttonType);
	}

	public void changeButtonType(Integer oldButtonType, Integer newButtonType) {
		for (int i = 0; i < buttonTypes.size(); i++) {
			if (buttonTypes.get(i).equals(oldButtonType)) {
				Button currentButton = (Button) (hPanel.getWidget(i));
				if (newButtonType.equals(CentraleUIConstants.DURING_POLLING))
					currentButton.setEnabled(false);
				else
					currentButton.setEnabled(true);
				setButtonType(currentButton, newButtonType);
				buttonTypes.remove(i);
				buttonTypes.insertElementAt(newButtonType, i);
			}
		}
	}

	public void clearButton() {
		hPanel.clear();
		buttonTypes.clear();
	}// end clearButton

	private void setButtonType(Button currentButton, Integer buttonType) {
		if (buttonType.equals(CentraleUIConstants.SAVE)) {
			currentButton.setTitle(CentraleUI.getMessages().button_save());
			currentButton.setStyleName("gwt-button-save");
		} // end if SAVE

		if (buttonType.equals(CentraleUIConstants.BACK)) {
			currentButton.setTitle(CentraleUI.getMessages().button_back());
			if (color == ORANGE)
				currentButton.setStyleName("gwt-button-back");
			else
				currentButton.setStyleName("gwt-button-back-blue");
		} // end if BACK

		if (buttonType.equals(CentraleUIConstants.HELP)) {
			currentButton.setTitle(CentraleUI.getMessages().button_help());
			if (color == ORANGE)
				currentButton.setStyleName("gwt-button-help");
			else
				currentButton.setStyleName("gwt-button-help-blue");
		} // end if HELP

		if (buttonType.equals(CentraleUIConstants.UNDO)) {
			currentButton.setTitle(CentraleUI.getMessages().button_undo());
			if (color == ORANGE)
				currentButton.setStyleName("gwt-button-undo");
			else
				currentButton.setStyleName("gwt-button-undo-blue");
		} // end if UNDO

		if (buttonType.equals(CentraleUIConstants.DOWNLOAD)) {
			currentButton.setTitle(CentraleUI.getMessages().download());
			currentButton.setStyleName("gwt-button-download");
		} // end if DOWNLOAD

		if (buttonType.equals(CentraleUIConstants.SEND_VERIFY)) {
			if (color == ORANGE) {
				currentButton.setTitle(CentraleUI.getMessages().button_send_verify());
				currentButton.setStyleName("gwt-button-send-verify");
			} else {
				// TODO capire se serve
				// sendVerifyButton.setTitle(CentraleUI.messages.ok());
				currentButton.setStyleName("gwt-button-send-verify-blue");
			}
		} // end if SENDVERIFY

		if (buttonType.equals(CentraleUIConstants.REFRESH)) {
			currentButton.setTitle(CentraleUI.getMessages().button_refresh());
			currentButton.setStyleName("gwt-button-reload");
		} // end if REFRESH

		if (buttonType.equals(CentraleUIConstants.REFRESH_REMOVED)) {
			currentButton.setTitle(CentraleUI.getMessages().button_refresh_removed());
			currentButton.setStyleName("gwt-button-view-deleted-analyzers");
		} // end if REFRESH_REMOVED

		if (buttonType.equals(CentraleUIConstants.TABLE_SMALL)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_table());
			currentButton.setStyleName("gwt-button-table-small");
		} // end if TABLE_SMALL

		if (buttonType.equals(CentraleUIConstants.CSV_SMALL)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_csv());
			currentButton.setStyleName("gwt-button-csv-small");
		} // end if CSV_SMALL

		if (buttonType.equals(CentraleUIConstants.CHART_SMALL)) {
			currentButton.setTitle(CentraleUI.getMessages().chart_title());
			currentButton.setStyleName("gwt-button-chart-small");
		} // end if CHART_SMALL

		if (buttonType.equals(CentraleUIConstants.INFORMATIC_STATUS)) {
			currentButton.setTitle(CentraleUI.getMessages().informatic_status_title());
			currentButton.setStyleName("gwt-button-informatic_status");
		} // end if INFORMATIC_STATUS

		if (buttonType.equals(CentraleUIConstants.ANALYZER_STATUS)) {
			currentButton.setTitle(CentraleUI.getMessages().analyzers_status_title());
			currentButton.setStyleName("gwt-button-analyzers_status");
		} // end if ANALYZER_STATUS

		if (buttonType.equals(CentraleUIConstants.LINK_DB)) {
			currentButton.setTitle(CentraleUI.getMessages().link_db_title());
			currentButton.setStyleName("gwt-button-linkdb");
		} // end if LINK_DB

		if (buttonType.equals(CentraleUIConstants.UNLINK_DB)) {
			currentButton.setTitle(CentraleUI.getMessages().unlink_station_db_title());
			currentButton.setStyleName("gwt-button-unlinkdb");
		} // end if UNLINK_DB

		if (buttonType.equals(CentraleUIConstants.STATION_STATUS)) {
			currentButton.setTitle(CentraleUI.getMessages().station_status_title());
			if (color == ORANGE)
				currentButton.setStyleName("gwt-button-station_status_orange");
			else
				currentButton.setStyleName("gwt-button-station_status");

		} // end if STATION_STATUS

		if (buttonType.equals(CentraleUIConstants.POLLING)) {
			currentButton.setTitle(CentraleUI.getMessages().polling_immediate_title());
			currentButton.setStyleName("gwt-button-polling");
		} // end if POLLING

		if (buttonType.equals(CentraleUIConstants.CHART)) {
			currentButton.setTitle(CentraleUI.getMessages().chart_title());
			currentButton.setStyleName("gwt-button-chart");
		} // end if CHART

		if (buttonType.equals(CentraleUIConstants.CSV)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_csv());
			if (color == ORANGE)
				currentButton.setStyleName("gwt-button-csv_orange");
			else
				currentButton.setStyleName("gwt-button-csv");
		} // end if CSV

		if (buttonType.equals(CentraleUIConstants.PHYSICAL_DIMENSION)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_physical_dimension());
			currentButton.setStyleName("gwt-button-physical-dimension");
		} // end if PHYSICAL_DIMENSION

		if (buttonType.equals(CentraleUIConstants.PARAMETER)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_parameter());
			currentButton.setStyleName("gwt-button-parameter");
		} // end if PARAMETER

		if (buttonType.equals(CentraleUIConstants.MEASURE_UNIT)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_measure_unit());
			currentButton.setStyleName("gwt-button-measure-unit");
		} // end if MEASURE_UNIT

		if (buttonType.equals(CentraleUIConstants.ALARM_NAME)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_alarm_name());
			currentButton.setStyleName("gwt-button-alarm-name");
		} // end if ALARM_NAME

		if (buttonType.equals(CentraleUIConstants.OTHER_COMMON_CFG)) {
			currentButton.setTitle(CentraleUI.getMessages().lbl_other_common_cfg());
			currentButton.setStyleName("gwt-button-other-common-cfg");
		} // end if OTHER_COMMON_CFG

		if (buttonType.equals(CentraleUIConstants.DURING_POLLING)) {
			currentButton.setTitle(CentraleUI.getMessages().during_polling());
			currentButton.setStyleName("gwt-button-during-polling");
		} // end if POLLING

		if (buttonType.equals(CentraleUIConstants.XML)) {
			currentButton.setTitle(CentraleUI.getMessages().get_xml());
			currentButton.setStyleName("gwt-button-xml");
		} // end if XML
	}
}// end class
