package com.districtap.pengbot;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


public class CheatSheet implements GUI{
	private static boolean open = false;
	private Display display;
	protected Shell shell;
	private static ArrayList<String> results;
	
	public CheatSheet(){
		display = Display.getDefault();
		shell = new Shell(~SWT.RESIZE);
		shell.setSize(315, 276);
		shell.setText("Bot Cheat Sheet");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  open = false;
		    	  shell.setVisible(false);
		    	  event.doit = false;
		      }
		});
		createContents();
	}
		
	/**
	 * Open the window.
	 */
	public void open() {
		if(!open){
			open = true;
			results = Settings.getCheatSheet();
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
	}

	/**
	 * Create contents of the window.
	 */
	public void createContents() {
		final List list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		list.setItems(new String[] {"$Sender", "$Content", "$IP", "$Time"});
		
		Group grpResultingText = new Group(shell, SWT.NONE);
		grpResultingText.setText("Resulting Text");
		grpResultingText.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final Browser browser = new Browser(grpResultingText, SWT.NONE);
		
		list.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				browser.setText(results.get(list.getSelectionIndex()));
			}
		});
	}
	@Override
	public void setVisable(boolean b) {
		shell.setVisible(b);
	}
}
