package com.districtap.pengbot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SettingsGUI implements GUI{

	private Shell shell;
	private Display display;
	private TabFolder tabFolder;
	private TabItem tbtmConnection;
	private Composite compConnections;
	private Composite compLeft;
	private Text txtHostName;
	private Text txtPassword;
	private Text txtChannel;
	private Text txtUsername;
	private Text txtPort;

	
	public SettingsGUI(){
		display = Display.getDefault();
		shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setSize(377, 273);
		shell.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		createContents();
	}
	/**
	 * Open the window.
	 */
	public void open() {
		try {
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Create contents of the window.
	 */
	public void createContents() {
		tabFolder = new TabFolder(shell, SWT.NONE);
		
		tbtmConnection = new TabItem(tabFolder, SWT.NONE);
		tbtmConnection.setText("Connection");
		
		compConnections = new Composite(tabFolder, SWT.NONE);
		tbtmConnection.setControl(compConnections);
		compConnections.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		compLeft = new Composite(compConnections, SWT.NONE);
		compLeft.setLayout(new FillLayout(SWT.VERTICAL));
		
		Label lblHostName = new Label(compLeft, SWT.NONE);
		lblHostName.setToolTipText("Host Name of the IRC Server.");
		lblHostName.setText("Host Name:");
		
		Label lblPort = new Label(compLeft, SWT.NONE);
		lblPort.setToolTipText("Port that the IRC Channel is listening on.");
		lblPort.setText("Port:");
		
		Label lblUserName = new Label(compLeft, SWT.NONE);
		lblUserName.setToolTipText("Either the username needed to login, or the nickname to use in the IRC Channel.");
		lblUserName.setText("User/Nick Name:");
		
		Label lblPassword = new Label(compLeft, SWT.NONE);
		lblPassword.setToolTipText("Password or authorization key if needed to login to the IRC account.");
		lblPassword.setText("Key/Password:");
		
		Label lblChannel = new Label(compLeft, SWT.NONE);
		lblChannel.setToolTipText("The specific channel to join.");
		lblChannel.setText("Channel:");
		
		Label lblAutoChange = new Label(compLeft, SWT.NONE);
		lblAutoChange.setToolTipText("Whether or not to change the user/nick name by adding "
				+ "numbers to the end of the name until an available name is found).");
		lblAutoChange.setText("Auto Change Name:");
		
		Composite compReset = new Composite(compLeft, SWT.NONE);
		compReset.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Button btnReset = new Button(compReset, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
			}
		});
		btnReset.setText("Reset to defaults");
		
		Composite compRight = new Composite(compConnections, SWT.NONE);
		compRight.setLayout(new FillLayout(SWT.VERTICAL));
		
		txtHostName = new Text(compRight, SWT.BORDER);
		txtHostName.setText("irc.twitch.tv");
		
		txtPort = new Text(compRight, SWT.BORDER);
		txtPort.setText("6667");
		
		txtUsername = new Text(compRight, SWT.BORDER);
		txtUsername.setText("Unnamed Bot");
		
		txtPassword = new Text(compRight, SWT.BORDER);
		txtPassword.setText("");
		
		txtChannel = new Text(compRight, SWT.BORDER | SWT.PASSWORD);
		txtChannel.setText("");
		
		Button chkAutoChange = new Button(compRight, SWT.CHECK);
		chkAutoChange.setText("On");
		chkAutoChange.setSelection(false);
		
		Composite compSubmit = new Composite(compRight, SWT.NONE);
		compSubmit.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Button btnSubmit = new Button(compSubmit, SWT.CENTER);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
			}
		});
		btnSubmit.setText("Submit");
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  BotGUI.enable();
		      }
		});
		setValues();
	}

	private void setValues(){
	}

	@Override
	public void setVisable(boolean b) {
		shell.setVisible(b);
	}
}
