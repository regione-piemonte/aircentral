/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file: Page for manage physical dimension of common configuration.
* Change log:
*   2009-04-06: initial version
* ----------------------------------------------------------------------------
* $Id: PhysicalDimensionWidget.java,v 1.9 2014/09/18 09:46:58 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for manage physical dimension of common configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class PhysicalDimensionWidget extends UIPage {

	private VerticalPanel externalPanel;

	private FlexTable table = new FlexTable();

	private TextBox newPhysicalDimensionTextBox;

	private HashMap<Button, String> map = new HashMap<Button, String>();

	public PhysicalDimensionWidget() {

		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		panelButton.addButton(CentraleUIConstants.XML, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./exportService?function=getCommonConfig", "", "");
			}
		});

		panelButton.addButton(CentraleUIConstants.PARAMETER, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.PARAMETER);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.MEASURE_UNIT, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.MEASURE_UNIT);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.ALARM_NAME, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.ALARM_NAME);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.OTHER_COMMON_CFG, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.otherCommonCfgWidget);
			}
		});

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_physicalDimensionWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().common_cfg_title(), panelButton,
				false);

		Label newPhysicalDimensionLabel = new Label();
		newPhysicalDimensionLabel.setText(CentraleUI.getMessages().new_physical_dimension());
		newPhysicalDimensionLabel.setStyleName("gwt-Label-title");

		// panel that contains info for new physical dimension
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		panel.add(hPanel);

		newPhysicalDimensionTextBox = new TextBox();
		newPhysicalDimensionTextBox.setStyleName("gwt-bg-text-orange");
		newPhysicalDimensionTextBox.setWidth("300px");
		hPanel.add(newPhysicalDimensionTextBox);

		Button newPhysicalDimensionButton = new Button();
		newPhysicalDimensionButton.setStyleName("gwt-button-new-orange");
		newPhysicalDimensionButton.setTitle(CentraleUI.getMessages().lbl_new());
		newPhysicalDimensionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (newPhysicalDimensionTextBox.getText().equals("")) {
					Window.alert(CentraleUI.getMessages().insert_physical_dimension());
				} else {
					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errorStr) {
							if (errorStr == null)
								Window.alert(CentraleUI.getMessages().added_physical_dimension());
							else
								Window.alert(errorStr);
							reset();
							setField();

						}// end onSuccess
					};
					centraleService.addPhysicalDimension(newPhysicalDimensionTextBox.getText(), callback);
				} // end else
			}// end onClick

		});
		hPanel.add(newPhysicalDimensionButton);

		externalPanel.add(newPhysicalDimensionLabel);
		externalPanel.add(panel);

		Label physicalDimensionLabel = new Label();
		physicalDimensionLabel.setText(CentraleUI.getMessages().physical_dimension_title());
		physicalDimensionLabel.setStyleName("gwt-Label-title");

		// panel that contains info for new physical dimension
		VerticalPanel panel2 = new VerticalPanel();
		panel2.setStyleName("gwt-post-boxed");

		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().lbl_physical_dimension());
		headerTable.setText(0, 1, CentraleUI.getMessages().lbl_cancel());
		headerTable.setStyleName("gwt-table-header");
		headerTable.getCellFormatter().setWidth(0, 0, "400px");
		headerTable.getCellFormatter().setWidth(0, 1, "50px");
		for (int j = 0; j < 2; j++) {
			headerTable.getCellFormatter().setStyleName(0, j, "gwt-table-header");
		}
		panel2.add(headerTable);
		panel2.add(table);

		externalPanel.add(physicalDimensionLabel);
		externalPanel.add(panel2);

		initWidget(externalPanel);

	}// end constructor

	private void setField() {

		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		// load station matched
		AsyncCallback<List<String>> callback = new UIAsyncCallback<List<String>>() {
			public void onSuccess(List<String> physicalDimensionList) {

				Iterator<String> iterator = physicalDimensionList.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					String name = iterator.next();
					table.setText(i, 0, name);
					Button buttonCancel = new Button();
					buttonCancel.setStyleName("gwt-button-delete");
					buttonCancel.setTitle(CentraleUI.getMessages().delete());
					buttonCancel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							String name = map.get(event.getSource());
							CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
									.create(CentraleUIService.class);
							ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
							endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
							AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
								public void onSuccess(String errorMsg) {
									if (errorMsg == null)
										Window.alert(CentraleUI.getMessages().deleted_physical_dimension());
									else
										Window.alert(errorMsg);
									reset();
									setField();
								}// end onSuccess
							};
							centraleService2.deletePhysicalDimension(name, callback2);

						}// end onClick

					});
					table.setWidget(i, 1, buttonCancel);
					table.getCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_CENTER);
					map.put(buttonCancel, name);
					for (int j = 0; j < 2; j++) {
						table.getCellFormatter().setStyleName(i, j, "gwt-table-data");
					} // end for
					i++;
				} // end while

			}// end onSuccess
		};
		centraleService.getPhysicalDimension(callback);
	}// end setField

	@Override
	protected void reset() {
		newPhysicalDimensionTextBox.setText("");
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "400px");
		table.getCellFormatter().setWidth(0, 1, "50px");

	}// end reset

	@Override
	protected void loadContent() {
		setField();
	}

}// end class
