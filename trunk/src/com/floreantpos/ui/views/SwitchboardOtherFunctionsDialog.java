package com.floreantpos.ui.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.ecs.xhtml.strike;

import com.floreantpos.POSConstants;
import com.floreantpos.actions.AuthorizeTicketAction;
import com.floreantpos.actions.OpenKitchenDisplayAction;
import com.floreantpos.actions.TicketImportAction;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.extension.FloorLayoutPlugin;
import com.floreantpos.extension.TicketImportPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.swing.PosButton;
import com.floreantpos.ui.dialog.ManagerDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.PayoutDialog;

public class SwitchboardOtherFunctionsDialog extends POSDialog implements ActionListener {
	private SwitchboardView switchboardView;
	
	private PosButton btnBackOffice = new PosButton(POSConstants.BACK_OFFICE_BUTTON_TEXT);
	private PosButton btnManager = new PosButton(POSConstants.MANAGER_BUTTON_TEXT);
	private PosButton btnAuthorize = new PosButton(POSConstants.AUTHORIZE_BUTTON_TEXT, new AuthorizeTicketAction());
	private PosButton btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT, new OpenKitchenDisplayAction());
	private PosButton btnPayout = new PosButton(POSConstants.PAYOUT_BUTTON_TEXT);
	private PosButton btnTableManage = new PosButton(POSConstants.TABLE_MANAGE_BUTTON_TEXT);
	private PosButton btnOnlineTickets = new PosButton(POSConstants.ONLINE_TICKET_BUTTON_TEXT, new TicketImportAction());
	
	public SwitchboardOtherFunctionsDialog(SwitchboardView switchboardView) {
		super(Application.getPosWindow(), true);
		this.switchboardView = switchboardView;
		
		setTitle("OTHER FUNCTIONS");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 400);
		
		JPanel contentPane = new JPanel(new GridLayout(2, 0, 10, 10));
		contentPane.add(btnBackOffice);
		contentPane.add(btnManager);
		contentPane.add(btnAuthorize);
		contentPane.add(btnKitchenDisplay);
		contentPane.add(btnPayout);
		
		final FloorLayoutPlugin floorLayoutPlugin = Application.getPluginManager().getPlugin(FloorLayoutPlugin.class);
		if (floorLayoutPlugin != null) {
			contentPane.add(btnTableManage);
			
			btnTableManage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					floorLayoutPlugin.openTicketsAndTablesDisplay();
				}
			});
		}

		TicketImportPlugin ticketImportPlugin = Application.getPluginManager().getPlugin(TicketImportPlugin.class);
		if (ticketImportPlugin != null) {
			contentPane.add(btnOnlineTickets);
		}
		
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		
		setupPermission();
	}

	private void setupPermission() {
		User user = Application.getCurrentUser();
		UserType userType = user.getType();
		if (userType != null) {
			Set<UserPermission> permissions = userType.getPermissions();
			if (permissions != null) {
				for (UserPermission permission : permissions) {
					if (permission.equals(UserPermission.PAY_OUT)) {
						btnPayout.setEnabled(true);
					}
					else if (permission.equals(UserPermission.PERFORM_MANAGER_TASK)) {
						btnManager.setEnabled(true);
					}
					else if (permission.equals(UserPermission.VIEW_BACK_OFFICE)) {
						btnBackOffice.setEnabled(true);
					}
				}
			}
		}
	}
	
	private void doShowManagerWindow() {
		ManagerDialog dialog = new ManagerDialog();
		dialog.open();

		//updateTicketList();
	}
	
	private synchronized void doShowBackoffice() {
		BackOfficeWindow window = BackOfficeWindow.getInstance();
		if (window == null) {
			window = new BackOfficeWindow();
			Application.getInstance().setBackOfficeWindow(window);
		}
		window.setVisible(true);
		window.toFront();
	}
	
	private void doPayout() {
		PayoutDialog dialog = new PayoutDialog(Application.getPosWindow(), true);
		dialog.open();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		contentPane.add(btnBackOffice);
//		contentPane.add(btnManager);
//		contentPane.add(btnAuthorize);
//		contentPane.add(btnKitchenDisplay);
//		contentPane.add(btnPayout);
		
		Object source = e.getSource();
		if(source == btnBackOffice) {
			doShowBackoffice();
		}
		else if(source == btnManager) {
			doShowManagerWindow();
		}
		else if(source == btnPayout) {
			doPayout();
		}
	}
}
