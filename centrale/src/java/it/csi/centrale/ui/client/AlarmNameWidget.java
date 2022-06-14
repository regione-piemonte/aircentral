/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Page for manage  alarm name fields of common
* configuration.
* Change log:
*   2009-04-08: initial version
* ----------------------------------------------------------------------------
* $Id: AlarmNameWidget.java,v 1.10 2014/09/18 09:46:58 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.AlarmNameInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Page for manage alarm name fields of common configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class AlarmNameWidget extends UIPage {

	private static final String RELEVANT = "true";

	private static final String NOT_RELEVANT = "false";

	private static final String DIGITAL = "digital";

	private static final String TRIGGER = "trigger";

	private VerticalPanel externalPanel;

	private PanelButtonWidget panelButton;

	private FlexTable table = new FlexTable();

	private TextBox alarmIdTextBox;

	private TextBox alarmNameTextBox;

	private ListBox typeListBox;

	private ListBox dataQualityRelevantListBox;

	private String alarmId;

	public AlarmNameWidget() {

		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		panelButton.addButton(CentraleUIConstants.BACK, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		ClickHandler helpClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_alarmNameWidget", "", "");

			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().common_cfg_title(), panelButton,
				false);

		Label parameterLabel = new Label();
		parameterLabel.setStyleName("gwt-Label-title");
		parameterLabel.setText(CentraleUI.getMessages().lbl_alarm_name());

		// panel that contains info for new alarm name
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		table.getCellFormatter().setWidth(0, 0, "200px");
		table.getCellFormatter().setWidth(0, 1, "200px");
		table.getCellFormatter().setWidth(0, 2, "200px");
		table.getCellFormatter().setWidth(0, 3, "200px");
		table.setCellPadding(5);
		table.setCellSpacing(5);
		panel.add(table);

		table.setText(0, 0, "* " + CentraleUI.getMessages().alarm_id());
		alarmIdTextBox = new TextBox();
		alarmIdTextBox.setStyleName("gwt-bg-text-orange");
		alarmIdTextBox.setWidth("180px");
		table.setWidget(0, 1, alarmIdTextBox);

		table.setText(0, 2, "* " + CentraleUI.getMessages().alarm_name_lbl());
		alarmNameTextBox = new TextBox();
		alarmNameTextBox.setStyleName("gwt-bg-text-orange");
		alarmNameTextBox.setWidth("220px");
		table.setWidget(0, 3, alarmNameTextBox);

		table.setText(1, 0, CentraleUI.getMessages().data_quality_relevant());
		dataQualityRelevantListBox = new ListBox();
		dataQualityRelevantListBox.setStyleName("gwt-bg-text-orange");
		dataQualityRelevantListBox.setWidth("180px");
		dataQualityRelevantListBox.addItem(CentraleUI.getMessages().relevant(), RELEVANT);
		dataQualityRelevantListBox.addItem(CentraleUI.getMessages().not_relevant(), NOT_RELEVANT);
		table.setWidget(1, 1, dataQualityRelevantListBox);

		table.setText(1, 2, CentraleUI.getMessages().type());
		typeListBox = new ListBox();
		typeListBox.setStyleName("gwt-bg-text-orange");
		typeListBox.setWidth("220px");
		typeListBox.addItem(DIGITAL, DIGITAL);
		typeListBox.addItem(TRIGGER, TRIGGER);
		table.setWidget(1, 3, typeListBox);

		// create buttons for undo and save
		PanelButtonWidget panelButton2 = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		// button cancel for alarm name
		ClickHandler cancelClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				setField();

			}
		};
		panelButton2.addButton(CentraleUIConstants.UNDO, cancelClickHandler);

		// button send/verify
		ClickHandler sendVerifyClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AlarmNameInfo alarmInfo = new AlarmNameInfo();
				alarmInfo.setAlarmId(alarmIdTextBox.getText());
				alarmInfo.setAlarmName(alarmNameTextBox.getText());
				// verify mandatory fields
				if (alarmInfo.getAlarmId().equals("") || alarmInfo.getAlarmName().equals("")) {
					Window.alert(CentraleUI.getMessages().mandatory());
					reset();
					setField();
				} else {
					alarmInfo.setDataQualityRelevant((dataQualityRelevantListBox
							.getValue(dataQualityRelevantListBox.getSelectedIndex()).equals(RELEVANT) ? true : false));
					alarmInfo.setType(typeListBox.getItemText(typeListBox.getSelectedIndex()));

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					if (alarmId.equals("")) {
						// case of new alarm name

						AsyncCallback<String> callback = new UIAsyncCallback<String>() {
							public void onSuccess(String errStr) {
								if (errStr != null)
									Window.alert(errStr);
								else {
									Window.alert(CentraleUI.getMessages().alarm_inserted());
									CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
								} // end else
							}// end onSuccess
						};
						centraleService.insertAlarmName(alarmInfo, callback);
					} else {
						// case of existing alarm name
						AsyncCallback<String> callback = new UIAsyncCallback<String>() {
							public void onSuccess(String errStr) {
								if (errStr != null)
									Window.alert(errStr);
								else {
									Window.alert(CentraleUI.getMessages().alarm_updated());
									CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
								} // end else
							}// end onSuccess
						};
						centraleService.updateAlarmName(alarmInfo, callback);
					} // end else
				} // end else mandatory
			}// end onClick
		};
		panelButton2.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler);

		// panel that contains button for alarm name
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("gwt-button-panel");
		hPanel2.add(panelButton2);
		hPanel2.setSpacing(10);
		hPanel2.setCellHorizontalAlignment(panelButton2, HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(hPanel2);
		panel.add(new Label(CentraleUI.getMessages().mandatory()));

		externalPanel.add(parameterLabel);
		externalPanel.add(panel);
		initWidget(externalPanel);

	}// end constructor

	private void setField() {

		if (!alarmId.equals("")) {
			// load measure unit
			CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
			ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
			endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<AlarmNameInfo> callback2 = new UIAsyncCallback<AlarmNameInfo>() {
				public void onSuccess(AlarmNameInfo alarmInfo) {
					alarmIdTextBox.setText(alarmInfo.getAlarmId());
					alarmNameTextBox.setText(alarmInfo.getAlarmName());
					for (int i = 0; i < typeListBox.getItemCount(); i++) {
						if (typeListBox.getItemText(i).equals(alarmInfo.getType()))
							typeListBox.setSelectedIndex(i);
					} // end for
					if (alarmInfo.isDataQualityRelevant())
						// set to RELEVANT
						dataQualityRelevantListBox.setSelectedIndex(0);
					else
						// set to NOT_RELEVANT
						dataQualityRelevantListBox.setSelectedIndex(1);

				}// end onSuccess
			};
			centraleService2.getAlarmNameInfo(alarmId, callback2);
		} // end if

	}// end setField

	public void setParameter(String id) {
		this.alarmId = id;

	}

	@Override
	protected void reset() {
		dataQualityRelevantListBox.setSelectedIndex(-1);
		alarmIdTextBox.setText("");
		alarmNameTextBox.setText("");
		for (int i = 0; i < typeListBox.getItemCount(); i++) {
			typeListBox.setItemSelected(i, false);
		} // end for
			// set to NOT_RELEVANT
		dataQualityRelevantListBox.setSelectedIndex(1);
	}// end reset

	@Override
	protected void loadContent() {
		setField();

	}

}// end class
