package com.floreantpos.config.ui;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.Database;
import com.floreantpos.config.AppConfig;
import com.floreantpos.main.Application;
import com.floreantpos.swing.POSPasswordField;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.ui.TitlePanel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.util.DatabaseUtil;

public class DatabaseConfigurationDialog extends POSDialog implements ActionListener {
	
	private static final String CONFIGURE_DB = "CD";
	private static final String SAVE = "SAVE";
	private static final String CANCEL = "cancel";
	private static final String TEST = "test";
	private POSTextField tfServerAddress;
	private POSTextField tfServerPort;
	private POSTextField tfDatabaseName;
	private POSTextField tfUserName;
	private POSPasswordField tfPassword;
	private JButton btnTestConnection;
	private JButton btnCreateDb;
	private JButton btnExit;
	private JButton btnSave;
	private JComboBox databaseCombo;
	
	private TitlePanel titlePanel;
	private JLabel lblServerAddress;
	private JLabel lblServerPort;
	private JLabel lblDbName;
	private JLabel lblUserName;
	private JLabel lblDbPassword;
	
	public DatabaseConfigurationDialog() throws HeadlessException {
		super();
		
		setFieldValues();
		addUIListeners();
	}

	public DatabaseConfigurationDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		
		setFieldValues();
		addUIListeners();
	}

	public DatabaseConfigurationDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		
		setFieldValues();
		addUIListeners();
	}
	
	protected void initUI() {
		getContentPane().setLayout(new MigLayout("fill","[][fill, grow]",""));
	
		titlePanel = new TitlePanel();
		tfServerAddress = new POSTextField();
		tfServerPort = new POSTextField();
		tfDatabaseName = new POSTextField();
		tfUserName = new POSTextField();
		tfPassword = new POSPasswordField();
		databaseCombo = new JComboBox(Database.values());

		String databaseProviderName = AppConfig.getDatabaseProviderName();
		if(StringUtils.isNotEmpty(databaseProviderName)) {
			databaseCombo.setSelectedItem(Database.getByProviderName(databaseProviderName));
		}

		getContentPane().add(titlePanel, "span, grow, wrap");
		
		getContentPane().add(new JLabel("Database: "));
		getContentPane().add(databaseCombo, "grow, wrap");
		lblServerAddress = new JLabel("Database Server Address" + ":");
		getContentPane().add(lblServerAddress);
		getContentPane().add(tfServerAddress, "grow, wrap");
		lblServerPort = new JLabel("Database Server Port" + ":");
		getContentPane().add(lblServerPort);
		getContentPane().add(tfServerPort, "grow, wrap");
		lblDbName = new JLabel("Database Name" + ":");
		getContentPane().add(lblDbName);
		getContentPane().add(tfDatabaseName, "grow, wrap");
		lblUserName = new JLabel("User Name" + ":");
		getContentPane().add(lblUserName);
		getContentPane().add(tfUserName, "grow, wrap");
		lblDbPassword = new JLabel("Database Password" + ":");
		getContentPane().add(lblDbPassword);
		getContentPane().add(tfPassword, "grow, wrap");
		getContentPane().add(new JSeparator(),"span, grow, gaptop 10");
		
		btnTestConnection = new JButton("Test Connection");
		btnTestConnection.setActionCommand(TEST);
		btnSave = new JButton("Save");
		btnSave.setActionCommand(SAVE);
		btnExit = new JButton("Cancel");
		btnExit.setActionCommand(CANCEL);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnCreateDb = new JButton("Create Database Schema");
		btnCreateDb.setActionCommand(CONFIGURE_DB);
		buttonPanel.add(btnCreateDb);
		buttonPanel.add(btnTestConnection);
		buttonPanel.add(btnSave);
		buttonPanel.add(btnExit);
		
		getContentPane().add(buttonPanel, "span, grow");
	}

	private void addUIListeners() {
		btnTestConnection.addActionListener(this);
		btnCreateDb.addActionListener(this);
		btnSave.addActionListener(this);
		btnExit.addActionListener(this);
		
		databaseCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Database selectedDb = (Database) databaseCombo.getSelectedItem();
				
				if(selectedDb == Database.DEMO_DATABASE) {
					setFieldsVisible(false);
					return;
				}
				
				setFieldsVisible(true);
				
				String databasePort = AppConfig.getDatabasePort();
				if(StringUtils.isEmpty(databasePort)) {
					databasePort = selectedDb.getDefaultPort();
				}
				
				tfServerPort.setText(databasePort);
			}
		});
	}

	private void setFieldValues() {
		Database selectedDb = (Database) databaseCombo.getSelectedItem();
		
		String databaseURL = AppConfig.getDatabaseURL();
		tfServerAddress.setText(databaseURL);
		
		String databasePort = AppConfig.getDatabasePort();
		if(StringUtils.isEmpty(databasePort)) {
			databasePort = selectedDb.getDefaultPort();
		}
		
		tfServerPort.setText(databasePort);
		tfDatabaseName.setText(AppConfig.getDatabaseName());
		tfUserName.setText(AppConfig.getDatabaseUser());
		tfPassword.setText(AppConfig.getDatabasePassword());
		
		if(selectedDb == Database.DEMO_DATABASE) {
			setFieldsVisible(false);
		}
		else {
			setFieldsVisible(true);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		Database selectedDb = (Database) databaseCombo.getSelectedItem();
		
		String providerName = selectedDb.getName();
		String databaseURL = tfServerAddress.getText();
		String databasePort = tfServerPort.getText();
		String databaseName = tfDatabaseName.getText();
		String user = tfUserName.getText();
		String pass = new String(tfPassword.getPassword());
		
		String connectionString = selectedDb.getConnectString(databaseURL, databasePort, databaseName);
		String hibernateDialect = selectedDb.getHibernateDialect();
		String driverClass = selectedDb.getHibernateConnectionDriverClass();
		
		if(TEST.equalsIgnoreCase(command)) {
			Application.getInstance().setSystemInitialized(false);
			saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
			
			if(DatabaseUtil.checkConnection(connectionString, hibernateDialect, driverClass, user, pass)) {
				JOptionPane.showMessageDialog(this, "Connection Successfull!");
			}
			else {
				JOptionPane.showMessageDialog(this, "Connection Failed!");
			}
		}
		else if(CONFIGURE_DB.equals(command)) {
			Application.getInstance().setSystemInitialized(false);
			
			int i = JOptionPane.showConfirmDialog(this, "This will remove existing database schemas, if exists. Proceed?", "Warning", JOptionPane.YES_NO_OPTION);
			if(i != JOptionPane.YES_OPTION) {
				return;
			}
			
			saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
			
			String connectionString2 = selectedDb.getCreateDbConnectString(databaseURL, databasePort, databaseName);
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			boolean createDatabase = DatabaseUtil.createDatabase(connectionString2, hibernateDialect, driverClass, user, pass);
			this.setCursor(Cursor.getDefaultCursor());
			
			if(createDatabase) {
				JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this, "Database created.");
			}
			else {
				JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this, "Database creation failed.");
			}
		}
		else if(SAVE.equalsIgnoreCase(command)) {
			saveConfig(selectedDb, providerName, databaseURL, databasePort, databaseName, user, pass, connectionString, hibernateDialect);
			dispose();
		}
		else if(CANCEL.equalsIgnoreCase(command)) {
			dispose();
		}
	}

	private void saveConfig(Database selectedDb, String providerName, String databaseURL, String databasePort, String databaseName, String user, String pass,
			String connectionString, String hibernateDialect) {
		AppConfig.setDatabaseProviderName(providerName);
		AppConfig.setHibernateConnectionDriverClass(selectedDb.getHibernateConnectionDriverClass());
		AppConfig.setHibernateDialect(hibernateDialect);
		AppConfig.setConnectString(connectionString);
		AppConfig.setDatabaseURL(databaseURL);
		AppConfig.setDatabasePort(databasePort);
		AppConfig.setDatabaseName(databaseName);
		AppConfig.setDatabaseUser(user);
		AppConfig.setDatabasePassword(pass);
	}

	public void setTitle(String title) {
		super.setTitle("Configure database");
		
		titlePanel.setTitle(title);
	}
	
	private void setFieldsVisible(boolean visible) {
		lblServerAddress.setVisible(visible);
		tfServerAddress.setVisible(visible);
		
		lblServerPort.setVisible(visible);
		tfServerPort.setVisible(visible);
		
		lblDbName.setVisible(visible);
		tfDatabaseName.setVisible(visible);
		
		lblUserName.setVisible(visible);
		tfUserName.setVisible(visible);
		
		lblDbPassword.setVisible(visible);
		tfPassword.setVisible(visible);
	}
	
	public static DatabaseConfigurationDialog show(Frame parent) {
		DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog(Application.getPosWindow(), true);
		dialog.setTitle("Configure database");
		dialog.pack();
		dialog.open();
		
		return dialog;
	}
}
