/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file:  a composite Widget that implements the login interface 
 *                   for the centrale.
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: LoginWidget.java,v 1.15 2015/10/22 13:52:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.pagecontrol.UIPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A composite Widget that implements the login interface for the centrale.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */

public class LoginWidget extends UIPage {

	private FlexTable loginGrid = new FlexTable();

	private TextBox userNameTextBox;

	private PasswordTextBox passwordTextBox;

	private CheckBox localAccess;

	private Button loginButton;

	Label titleLabel;

	public LoginWidget() {

		DockPanel panelPage = new DockPanel();
		panelPage.setWidth("960px");
		panelPage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		titleLabel = new Label();
		titleLabel.setStyleName("gwt-h1");
		panelPage.add(titleLabel, DockPanel.NORTH);

		final CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<String> getCentraleVersion = new UIAsyncCallback<String>() {
			public void onSuccess(String version) {
				titleLabel.setText(CentraleUI.getMessages().lbl_cop() + " "
						+ version);
			}
		};

		centraleService.getVersion(getCentraleVersion);

		final AsyncCallback<Boolean> verifyLoginCallback = new UIAsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				if (result.booleanValue()) {
					CentraleUI.navBar.setLocale(CentraleUI.getLocale());
					CentraleUI.navBar.setVisible(true);
					CentraleUI.navBar.setBarForView();
					CentraleUI.slotPage.setVisible(false);
					if (CentraleUI.slotView != null) {
						CentraleUI.slotView.setVisible(true);
						CentraleUI.setCurrentPage(CentraleUI.viewMapWidget);
					}
				} else
					Window.alert(CentraleUI.getMessages().lbl_login_error());

			}
		};

		Label loginLabel = new Label();
		loginLabel.setText(CentraleUI.getMessages().lbl_login_title());
		loginLabel.setStyleName("gwt-label-login");
		VerticalPanel externalPanel = new VerticalPanel();
		externalPanel.add(loginLabel);

		loginGrid.setWidth("100%");
		loginGrid.setCellSpacing(3);

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("300px");
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.setStyleName("gwt-login-boxed");

		loginGrid.setText(0, 0, CentraleUI.getMessages().username());

		userNameTextBox = new TextBox();
		userNameTextBox.setStyleName("gwt-bg-text-blue");
		userNameTextBox.setTabIndex(1);
		loginGrid.setWidget(0, 1, userNameTextBox);

		loginGrid.setText(1, 0, CentraleUI.getMessages().password());

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setStyleName("gwt-bg-text-blue");
		passwordTextBox.setTabIndex(2);
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				int nativeCode = event.getNativeEvent().getKeyCode();
				if (nativeCode == KeyCodes.KEY_ENTER) {
					centraleService.verifyLogin(localAccess.getValue(),
							userNameTextBox.getText(),
							passwordTextBox.getText(), verifyLoginCallback);
					passwordTextBox.setText("");
					userNameTextBox.setText("");
				}
			}

		});
		loginGrid.setWidget(1, 1, passwordTextBox);

		loginGrid.setText(2, 0, CentraleUI.getMessages().lbl_local_acces_title());

		localAccess = new CheckBox();
		localAccess.setStyleName("gwt-bg-text-orange");
		localAccess.setTabIndex(3);
		localAccess.setValue(false);
		localAccess.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (localAccess.getValue()) {
					// disabilitare la casella per inserire username
					userNameTextBox.setEnabled(false);
				} else {
					userNameTextBox.setEnabled(true);
				}
			}
		});

		loginGrid.setWidget(2, 1, localAccess);

		vPanel.add(loginGrid);

		loginButton = new Button();
		loginButton.setStyleName("gwt-button-login");
		loginButton.setTitle(CentraleUI.getMessages().button_login());
		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				centraleService.verifyLogin(localAccess.getValue(),
						userNameTextBox.getText(), passwordTextBox.getText(),
						verifyLoginCallback);
				userNameTextBox.setText("");
				passwordTextBox.setText("");
			}
		});

		loginButton.setTabIndex(4);
		vPanel.add(loginButton);
		vPanel.setCellHeight(loginButton, "50px");
		vPanel.setCellVerticalAlignment(loginButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		externalPanel.add(vPanel);

		panelPage.add(externalPanel, DockPanel.CENTER);

		initWidget(panelPage);

	}

	@Override
	protected void reset() {
		passwordTextBox.setTabIndex(2);
		localAccess.setValue(false);
		localAccess.setTabIndex(3);
		userNameTextBox.setTabIndex(1);
		userNameTextBox.setFocus(true);
		userNameTextBox.setEnabled(true);
		loginButton.setTabIndex(4);
	}

	@Override
	protected void loadContent() {
	}

}// end class
