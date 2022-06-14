/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Page for manage parameter fields of common configuration.
* Change log:
*   2009-04-08: initial version
* ----------------------------------------------------------------------------
* $Id: ParameterWidget.java,v 1.10 2014/09/18 09:46:58 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.ParameterInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.Iterator;
import java.util.List;

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

/**
 * Page for manage parameter fields of common configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ParameterWidget extends UIPage {

	private static final String CHEMICAL = "chemical";

	private static final String METEO = "meteo";

	private static final String OTHER = "other";

	private static final String RAIN = "rain";

	private static final String WIND = "wind";

	private static final String WIND_DIR = "wind_dir";

	private static final String WIND_VEL = "wind_vel";

	private VerticalPanel externalPanel;

	private PanelButtonWidget panelButton;

	private FlexTable table = new FlexTable();

	private TextBox paramIdTextBox;

	private TextBox nameTextBox;

	private ListBox typeListBox;

	private TextBox molecularWeightTextBox;

	private ListBox physicalDimensionListBox;

	private String parameterId;

	public ParameterWidget() {

		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		panelButton.addButton(CentraleUIConstants.BACK, new ClickHandler() {
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_parameterWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);

		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().common_cfg_title(), panelButton,
				false);

		Label parameterLabel = new Label();
		parameterLabel.setStyleName("gwt-Label-title");
		parameterLabel.setText(CentraleUI.getMessages().parameter_config());

		// panel that contains info for new physical dimension
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");
		panel.add(table);

		table.getCellFormatter().setWidth(0, 0, "150px");
		table.getCellFormatter().setWidth(0, 1, "320px");
		table.getCellFormatter().setWidth(0, 2, "150px");
		table.getCellFormatter().setWidth(0, 3, "320px");
		table.setCellPadding(5);
		table.setCellSpacing(5);

		table.setText(0, 0, "* " + CentraleUI.getMessages().param_id());
		paramIdTextBox = new TextBox();
		paramIdTextBox.setStyleName("gwt-bg-text-orange");
		paramIdTextBox.setWidth("180px");
		table.setWidget(0, 1, paramIdTextBox);

		table.setText(0, 2, "* " + CentraleUI.getMessages().param_name());
		nameTextBox = new TextBox();
		nameTextBox.setStyleName("gwt-bg-text-orange");
		nameTextBox.setWidth("180px");
		table.setWidget(0, 3, nameTextBox);

		table.setText(1, 0, CentraleUI.getMessages().physical_dim());
		physicalDimensionListBox = new ListBox();
		physicalDimensionListBox.setStyleName("gwt-bg-text-orange");
		physicalDimensionListBox.setWidth("180px");
		table.setWidget(1, 1, physicalDimensionListBox);

		table.setText(1, 2, CentraleUI.getMessages().type());
		typeListBox = new ListBox();
		typeListBox.setStyleName("gwt-bg-text-orange");
		typeListBox.setWidth("180px");
		typeListBox.addItem(CentraleUI.getMessages().chemical(), CHEMICAL);
		typeListBox.addItem(CentraleUI.getMessages().meteo(), METEO);
		typeListBox.addItem(CentraleUI.getMessages().other(), OTHER);
		typeListBox.addItem(CentraleUI.getMessages().rain(), RAIN);
		typeListBox.addItem(CentraleUI.getMessages().wind(), WIND);
		typeListBox.addItem(CentraleUI.getMessages().wind_dir(), WIND_DIR);
		typeListBox.addItem(CentraleUI.getMessages().wind_vel(), WIND_VEL);

		table.setWidget(1, 3, typeListBox);

		table.setText(2, 0, CentraleUI.getMessages().molecular_weight());
		molecularWeightTextBox = new TextBox();
		molecularWeightTextBox.setStyleName("gwt-bg-text-orange");
		molecularWeightTextBox.setWidth("180px");
		TextBoxKeyPressHandler textBoxKeyPressHandler = new TextBoxKeyPressHandler(true);
		molecularWeightTextBox.addKeyPressHandler(textBoxKeyPressHandler);
		table.setWidget(2, 1, molecularWeightTextBox);

		// create buttons for undo and send and verify
		PanelButtonWidget panelButton2 = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		// button cancel
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

				ParameterInfo parInfo = new ParameterInfo();
				parInfo.setParam_id(paramIdTextBox.getText());
				parInfo.setName(nameTextBox.getText());
				// verify mandatory fields
				if (parInfo.getParam_id().equals("") || parInfo.getName().equals("")) {
					Window.alert(CentraleUI.getMessages().mandatory());
					reset();
					setField();
				} else {
					parInfo.setType(typeListBox.getValue(typeListBox.getSelectedIndex()));
					parInfo.setMolecularWeight((molecularWeightTextBox.getText().equals("") ? null
							: new Double(molecularWeightTextBox.getText())));
					parInfo.setPhysicalDimension(
							physicalDimensionListBox.getValue(physicalDimensionListBox.getSelectedIndex()));

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					if (parameterId.equals("")) {
						// case of new parameter
						AsyncCallback<String> callback = new UIAsyncCallback<String>() {
							public void onSuccess(String errStr) {
								if (errStr != null)
									Window.alert(errStr);
								else {
									Window.alert(CentraleUI.getMessages().parameter_inserted());
									CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
								} // end else
							}// end onSuccess
						};
						centraleService.insertParameter(parInfo, callback);
					} else {
						// case of existing parameter
						AsyncCallback<String> callback = new UIAsyncCallback<String>() {
							public void onSuccess(String errStr) {
								if (errStr != null)
									Window.alert(errStr);
								else {
									Window.alert(CentraleUI.getMessages().parameter_updated());
									CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
								} // end else
							}// end onSuccess
						};
						centraleService.updateParameter(parInfo, callback);
					} // end else
				} // end else manadatory
			}// end onClick
		};
		panelButton2.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler);

		// panel that contains button for connection info
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
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		// load physical dimension
		AsyncCallback<List<String>> callback = new UIAsyncCallback<List<String>>() {
			public void onSuccess(List<String> elementList) {
				Iterator<String> it = elementList.iterator();
				while (it.hasNext()) {
					String name = it.next();
					physicalDimensionListBox.addItem(name, name);
				} // end while
				if (physicalDimensionListBox.getItemCount() != 0) {
					if (!parameterId.equals("")) {
						// load parameter
						CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
								.create(CentraleUIService.class);
						ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
						endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
						AsyncCallback<ParameterInfo> callback2 = new UIAsyncCallback<ParameterInfo>() {
							public void onSuccess(ParameterInfo parameterInfo) {
								paramIdTextBox.setText(parameterInfo.getParam_id());
								nameTextBox.setText(parameterInfo.getName());
								for (int i = 0; i < typeListBox.getItemCount(); i++) {
									if (typeListBox.getValue(i).equals(parameterInfo.getType()))
										typeListBox.setSelectedIndex(i);
								} // end for
								molecularWeightTextBox.setText((parameterInfo.getMolecularWeight() != null
										? parameterInfo.getMolecularWeight().toString()
										: ""));
								for (int i = 0; i < physicalDimensionListBox.getItemCount(); i++) {
									if (physicalDimensionListBox.getValue(i)
											.equals(parameterInfo.getPhysicalDimension()))
										physicalDimensionListBox.setItemSelected(i, true);
								} // end for

							}// end onSuccess
						};
						centraleService2.getParameterInfo(parameterId, callback2);
					} // end if
				} else {
					Window.alert(CentraleUI.getMessages().insert_physical_dimension());
					CentraleUI.setCurrentPage(CentraleUI.physicalDimensionWidget);
				}
			}// end onSuccess
		};
		centraleService.getPhysicalDimension(callback);

	}// end setField

	public void setParameter(String param_id) {
		this.parameterId = param_id;
	}// end setParameter

	@Override
	protected void reset() {
		physicalDimensionListBox.clear();
		paramIdTextBox.setText("");
		nameTextBox.setText("");
		molecularWeightTextBox.setText("");
		for (int i = 0; i < typeListBox.getItemCount(); i++) {
			typeListBox.setItemSelected(i, false);
		} // end for
	}// end reset

	@Override
	protected void loadContent() {
		setField();

	}

}// end class
