/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Page for manage parameter, measure unit and alarm name
*         			  of common configuration.
* Change log:
*   2009-04-06: initial version
* ----------------------------------------------------------------------------
* $Id: CommonCfgWidget.java,v 1.17 2014/09/18 09:46:58 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

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
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.common.KeyValueObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Page for manage parameter, measure unit and alarm name of common
 * configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class CommonCfgWidget extends UIPage {

	private VerticalPanel externalPanel;

	private PanelButtonWidget panelButton;

	Label newElementLabel;

	Label elementLabel;

	private FlexTable headerTable = new FlexTable();

	private FlexTable table = new FlexTable();

	private HashMap<Button, String> map = new HashMap<Button, String>();

	private Integer type = new Integer(0);

	public CommonCfgWidget() {

		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().common_cfg_title(), panelButton,
				false);

		newElementLabel = new Label();
		newElementLabel.setStyleName("gwt-Label-title");

		// panel that contains info for new element
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		panel.add(hPanel);

		Button newElementButton = new Button();
		newElementButton.setStyleName("gwt-button-new-orange");
		newElementButton.setTitle(CentraleUI.getMessages().lbl_new());
		newElementButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (type.equals(CentraleUIConstants.PARAMETER)) {
					CentraleUI.parameterWidget.setParameter("");
					CentraleUI.setCurrentPage(CentraleUI.parameterWidget);
				} else if (type.equals(CentraleUIConstants.MEASURE_UNIT)) {
					CentraleUI.measureUnitWidget.setParameter("");
					CentraleUI.setCurrentPage(CentraleUI.measureUnitWidget);
				} else if (type.equals(CentraleUIConstants.ALARM_NAME)) {
					CentraleUI.alarmNameWidget.setParameter("");
					CentraleUI.setCurrentPage(CentraleUI.alarmNameWidget);
				}
			}// end onClick

		});
		hPanel.add(newElementButton);

		externalPanel.add(newElementLabel);
		externalPanel.add(panel);

		elementLabel = new Label();
		elementLabel.setStyleName("gwt-Label-title");

		// panel that contains info for new element
		VerticalPanel panel2 = new VerticalPanel();
		panel2.setStyleName("gwt-post-boxed");

		headerTable.setText(0, 1, CentraleUI.getMessages().lbl_modify());
		headerTable.setText(0, 2, CentraleUI.getMessages().lbl_cancel());
		headerTable.setStyleName("gwt-table-header");
		headerTable.getCellFormatter().setWidth(0, 0, "400px");
		headerTable.getCellFormatter().setWidth(0, 1, "50px");
		headerTable.getCellFormatter().setWidth(0, 2, "50px");
		for (int j = 0; j < 3; j++) {
			headerTable.getCellFormatter().setStyleName(0, j, "gwt-table-header");
		}
		panel2.add(headerTable);
		panel2.add(table);

		externalPanel.add(elementLabel);
		externalPanel.add(panel2);

		initWidget(externalPanel);

	}// end constructor

	private void setField() {

		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		// load station matched
		AsyncCallback<List<KeyValueObject>> callback = new UIAsyncCallback<List<KeyValueObject>>() {
			public void onSuccess(List<KeyValueObject> elementList) {

				Iterator<KeyValueObject> iterator = elementList.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					KeyValueObject keyValue = iterator.next();
					table.setText(i, 0, keyValue.getValue());
					// modify button
					Button buttonModify = new Button();
					buttonModify.setStyleName("gwt-button-modify");
					buttonModify.setTitle(CentraleUI.getMessages().lbl_modify());
					buttonModify.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {

							if (type.equals(CentraleUIConstants.PARAMETER)) {
								CentraleUI.parameterWidget.setParameter(map.get(event.getSource()));
								CentraleUI.setCurrentPage(CentraleUI.parameterWidget);
							} else if (type.equals(CentraleUIConstants.MEASURE_UNIT)) {
								CentraleUI.measureUnitWidget.setParameter(map.get(event.getSource()));
								CentraleUI.setCurrentPage(CentraleUI.measureUnitWidget);
							} else if (type.equals(CentraleUIConstants.ALARM_NAME)) {
								CentraleUI.alarmNameWidget.setParameter(map.get(event.getSource()));
								CentraleUI.setCurrentPage(CentraleUI.alarmNameWidget);
							}
						}// end onClick

					});
					table.setWidget(i, 1, buttonModify);
					table.getCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_CENTER);
					map.put(buttonModify, keyValue.getKey());

					// delete button
					Button buttonCancel = new Button();
					buttonCancel.setStyleName("gwt-button-delete");
					buttonCancel.setTitle(CentraleUI.getMessages().delete());
					buttonCancel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
									.create(CentraleUIService.class);
							ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
							endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

							AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
								public void onSuccess(String errStr) {
									if (errStr == null) {
										if (type.equals(CentraleUIConstants.PARAMETER))
											Window.alert(CentraleUI.getMessages().deleted_parameter());
										else if (type.equals(CentraleUIConstants.MEASURE_UNIT))
											Window.alert(CentraleUI.getMessages().deleted_measure_unit());
										else if (type.equals(CentraleUIConstants.ALARM_NAME))
											Window.alert(CentraleUI.getMessages().deleted_alarm_name());
									} else
										Window.alert(errStr);
									reset();
									setField();
								}// end onSuccess

							};
							if (type.equals(CentraleUIConstants.PARAMETER))
								centraleService2.deleteParameter(map.get(event.getSource()), callback2);
							else if (type.equals(CentraleUIConstants.MEASURE_UNIT))
								centraleService2.deleteMeasureUnit(map.get(event.getSource()), callback2);

							else if (type.equals(CentraleUIConstants.ALARM_NAME))
								centraleService2.deleteAlarmName(map.get(event.getSource()), callback2);

						}// end onClick

					});
					table.setWidget(i, 2, buttonCancel);
					table.getCellFormatter().setHorizontalAlignment(i, 2, HasHorizontalAlignment.ALIGN_CENTER);
					map.put(buttonCancel, keyValue.getKey());

					for (int j = 0; j < 3; j++) {
						table.getCellFormatter().setStyleName(i, j, "gwt-table-data");
					} // end for
					i++;
				} // end while

			}// end onSuccess
		};
		centraleService.getCommonCfgElementList(type, callback);

	}// end setField

	public void setType(Integer typeCfg) {
		this.type = typeCfg;

		setPanelButton();

		setHeader();

	}// end setType

	private void setPanelButton() {
		panelButton.clearButton();
		panelButton.addButton(CentraleUIConstants.XML, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./exportService?function=getCommonConfig", "", "");
			}
		});

		if (!type.equals(CentraleUIConstants.PARAMETER)) {
			panelButton.addButton(CentraleUIConstants.PARAMETER, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CentraleUI.commonCfgWidget.setType(CentraleUIConstants.PARAMETER);
					CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
				}
			});
		} // end if parameter

		if (!type.equals(CentraleUIConstants.MEASURE_UNIT)) {
			panelButton.addButton(CentraleUIConstants.MEASURE_UNIT, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CentraleUI.commonCfgWidget.setType(CentraleUIConstants.MEASURE_UNIT);
					CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
				}
			});
		} // end if measure unit

		if (!type.equals(CentraleUIConstants.ALARM_NAME)) {
			panelButton.addButton(CentraleUIConstants.ALARM_NAME, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CentraleUI.commonCfgWidget.setType(CentraleUIConstants.ALARM_NAME);
					CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
				}
			});
		} // end if alarmName

		panelButton.addButton(CentraleUIConstants.PHYSICAL_DIMENSION, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.physicalDimensionWidget);
			}
		});
		panelButton.addButton(CentraleUIConstants.OTHER_COMMON_CFG, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.otherCommonCfgWidget);
			}
		});

		ClickHandler helpClickHandler = null;
		if (type.equals(CentraleUIConstants.PARAMETER))
			helpClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_parameterWidget", "", "");
				}
			};
		if (type.equals(CentraleUIConstants.MEASURE_UNIT))
			helpClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_measureUnitWidget", "", "");
				}
			};
		if (type.equals(CentraleUIConstants.ALARM_NAME))
			helpClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_alarmNameWidget", "", "");
				}
			};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
	}// end setPanelButton

	private void setHeader() {

		// set header table and label
		if (type.equals(CentraleUIConstants.ALARM_NAME)) {
			headerTable.setText(0, 0, CentraleUI.getMessages().lbl_alarm_name());
			newElementLabel.setText(CentraleUI.getMessages().new_alarm_name());
			elementLabel.setText(CentraleUI.getMessages().alarm_name_title());
		} else if (type.equals(CentraleUIConstants.PARAMETER)) {
			headerTable.setText(0, 0, CentraleUI.getMessages().lbl_parameter());
			newElementLabel.setText(CentraleUI.getMessages().new_parameter());
			elementLabel.setText(CentraleUI.getMessages().parameter_title());
		} else if (type.equals(CentraleUIConstants.MEASURE_UNIT)) {
			headerTable.setText(0, 0, CentraleUI.getMessages().lbl_measure_unit());
			newElementLabel.setText(CentraleUI.getMessages().new_measure_unit());
			elementLabel.setText(CentraleUI.getMessages().measure_unit_title());
		}

	}// end setHeader

	@Override
	protected void reset() {
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "400px");
		table.getCellFormatter().setWidth(0, 1, "50px");
		table.getCellFormatter().setWidth(0, 2, "50px");
	}

	@Override
	protected void loadContent() {
		setField();

	}

}// end class
