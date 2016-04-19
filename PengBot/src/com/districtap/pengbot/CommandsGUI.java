package com.districtap.pengbot;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;


public class CommandsGUI implements GUI{

	protected Shell shell;
	private Display display;
	private static ArrayList<String> Commands;
	private static ArrayList<String> Returns;
	
	public CommandsGUI(){
		shell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE | SWT.PRIMARY_MODAL);
		shell.setSize(300, 270);
		shell.setText(Settings.getName() + "\'s Command list");
		shell.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  BotGUI.enable();
		      }
		});
		display = Display.getDefault();
		Commands = Settings.getCommands();
		Returns = Settings.getReplies();
		createContents();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void resetCommands(Combo cmbCommands){
		cmbCommands.setItems(new String[0]);
		cmbCommands.add("New Command");
		for(String s:Commands){
			cmbCommands.add(s.substring(1));
		}
	}
	/**
	 * Create contents of the window.
	 */
	public void createContents() {
		final StyledText txtReturn = new StyledText(shell, SWT.BORDER | SWT.WRAP);
		txtReturn.setBounds(10, 71, 264, 120);
		final Combo cmbCommands = new Combo(shell,SWT.NONE);
		cmbCommands.setBounds(10, 23, 264, 23);
		cmbCommands.setFocus();
		resetCommands(cmbCommands);
		cmbCommands.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				int selection = cmbCommands.getSelectionIndex();
				if(selection==0||selection==-1){
					txtReturn.setText("Enter Return Message");
				}else
					txtReturn.setText(Returns.get(selection-1));
			}
		});
		
		txtReturn.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				txtReturn.selectAll();
			}
		});
			
		Button btnCheatSheet = new Button(shell, SWT.NONE);
		btnCheatSheet.setBounds(10, 197, 75, 25);
		btnCheatSheet.setText("Cheat Sheet");
		btnCheatSheet.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				CheatSheet ch = new CheatSheet();
				ch.open();
			}
		});
		
		Button btnRemove = new Button(shell, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String Command = "!" + cmbCommands.getText();
				Settings.removeCommand(Command);
			}
		});
		btnRemove.setBounds(105, 197, 75, 25);
		btnRemove.setText("Remove");		
		
		Button btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String Command = "!" + cmbCommands.getText();
				String message = txtReturn.getText();
				if(Settings.addCommand(shell,Command,message)==true){
					cmbCommands.setText("");
					txtReturn.setText("");
					resetCommands(cmbCommands);
				}
			}
		});
		btnSubmit.setBounds(199, 197, 75, 25);
		btnSubmit.setText("Submit");
		
		Label lblCommands = new Label(shell, SWT.NONE);
		lblCommands.setAlignment(SWT.CENTER);
		lblCommands.setBounds(10, 2, 264, 15);
		lblCommands.setText("Commands");
		
		Label lblReturn = new Label(shell, SWT.NONE);
		lblReturn.setAlignment(SWT.CENTER);
		lblReturn.setBounds(10, 50, 264, 15);
		lblReturn.setText("Return");

	}


	public void setVisable(boolean b) {
		shell.setVisible(b);
	}
}
