/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: page for choosing station for viewing data
* Change log:
*   2008-12-04: initial version
* ----------------------------------------------------------------------------
* $Id: ViewDataWidget.java,v 1.15 2014/09/18 09:46:57 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.StationMapObject;
import it.csi.centrale.ui.client.data.VirtualCopInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/***
 * Page for choosing station for viewing data
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ViewDataWidget extends UIPage {

	private final ListBox stationsListBox = new ListBox();

	private ListBox listBoxVirtualCop;

	public ViewDataWidget() {

		// panel that represents the page
		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#view_viewDataWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().view_station_data(),
				panelButton, false);

		/*
		 * List stations panel
		 */
		// Prepare panel for stations list
		Label titleNew = new Label();
		titleNew.setText(CentraleUI.getMessages().choose_station());
		titleNew.setStyleName("gwt-Label-title-blue");

		HorizontalPanel chooseStPanel = new HorizontalPanel();
		chooseStPanel.setStyleName("gwt-post-boxed-blue");
		chooseStPanel.setSpacing(10);

		HorizontalPanel listStationsPanel = new HorizontalPanel();
		// listStationsPanel.setStyleName("gwt-post-boxed-blue");
		listStationsPanel.setSpacing(5);
		listStationsPanel.setWidth("500");
		chooseStPanel.add(listStationsPanel);
		listStationsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		Label virtualCopLabel = new Label();
		virtualCopLabel.setText(CentraleUI.getMessages().lbl_virtual_cop());
		virtualCopLabel.setStyleName("gwt-cop-title-blue");
		listStationsPanel.add(virtualCopLabel);

		listBoxVirtualCop = new ListBox();
		listBoxVirtualCop.setWidth("150px");
		// listBoxVirtualCop.setSize("150px", "30px");
		listStationsPanel.add(listBoxVirtualCop);
		listBoxVirtualCop.setVisibleItemCount(1);
		listBoxVirtualCop.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String selectedValue = listBoxVirtualCop.getValue(listBoxVirtualCop.getSelectedIndex());
				getStationList(new Integer(selectedValue));

			}
		});

		stationsListBox.setWidth("300px");
		stationsListBox.setVisibleItemCount(1);
		listStationsPanel.setSpacing(10);
		listStationsPanel.add(stationsListBox);

		Button chooseButton = new Button();
		chooseButton.setStyleName("gwt-button-send-verify-blue");
		chooseButton.setTitle(CentraleUI.getMessages().station_button_choose());
		chooseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String selectedValue = stationsListBox.getValue(stationsListBox.getSelectedIndex());
				String stationName = stationsListBox.getItemText(stationsListBox.getSelectedIndex());
				CentraleUI.viewStationDataWidget.setStationInfos(new Integer(selectedValue).intValue(), stationName);
				CentraleUI.setCurrentPage(CentraleUI.viewStationDataWidget);
			}
		});
		listStationsPanel.add(chooseButton);

		// setting the alignment
		listStationsPanel.setCellHorizontalAlignment(stationsListBox, HasHorizontalAlignment.ALIGN_LEFT);
		listStationsPanel.setCellVerticalAlignment(stationsListBox, HasVerticalAlignment.ALIGN_MIDDLE);
		listStationsPanel.setCellHorizontalAlignment(chooseButton, HasHorizontalAlignment.ALIGN_LEFT);
		listStationsPanel.setCellVerticalAlignment(chooseButton, HasVerticalAlignment.ALIGN_MIDDLE);
		listStationsPanel.setCellWidth(stationsListBox, "310px");

		listStationsPanel.setCellHorizontalAlignment(listBoxVirtualCop, HasHorizontalAlignment.ALIGN_LEFT);
		listStationsPanel.setCellVerticalAlignment(listBoxVirtualCop, HasVerticalAlignment.ALIGN_MIDDLE);

		externalPanel.add(titleNew);
		externalPanel.add(chooseStPanel);

		initWidget(externalPanel);

	}// end constructor

	private void getStationList(final Integer virtualCopId) {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<List<StationMapObject>> callback = new UIAsyncCallback<List<StationMapObject>>() {
			public void onSuccess(List<StationMapObject> stations) {
				stationsListBox.clear();
				for (int i = 0; i < stations.size(); i++) {
					StationMapObject stObj = stations.get(i);
					if (stObj.getVirtualCopId().equals(virtualCopId)) {
						String stName = stObj.getShortName();
						if (stObj.getLongName() != null && !stObj.getLongName().trim().equals(""))
							stName = stObj.getLongName();
						stationsListBox.addItem(stName, new Integer(stObj.getStationId()).toString());
					}
				} // end for
			}
		};
		centraleService.getStMapObjList(false, false, callback);
	}// end getStationList

	@Override
	protected void loadContent() {
		getVirtualCop();
	}

	private void getVirtualCop() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<List<VirtualCopInfo>> callback = new UIAsyncCallback<List<VirtualCopInfo>>() {
			public void onSuccess(List<VirtualCopInfo> virtualCopList) {
				listBoxVirtualCop.clear();
				for (int i = 0; i < virtualCopList.size(); i++) {
					VirtualCopInfo vCop = virtualCopList.get(i);
					listBoxVirtualCop.addItem(vCop.getCopName(), vCop.getVirtualCopId().toString());
					getStationList(new Integer(listBoxVirtualCop.getValue(listBoxVirtualCop.getSelectedIndex())));
				}
			}
		};
		centraleService.getEnabledVirtualCop(callback);

	}

}// end class
