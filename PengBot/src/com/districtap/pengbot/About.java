package com.districtap.pengbot;


import java.awt.Desktop;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.FlowLayout;

public class About implements GUI{

	protected Shell shell;
	static Display display;
	private boolean open = false;
	/**
	 * Launch the application.
	 * @param args
	 * @wbp.parser.entryPoint
	 */
	
	public About(){
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(384, 389);
		shell.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
		shell.setText("About PengBot");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				open = false;
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
		Label penguin = new Label(shell, SWT.CENTER|SWT.FILL);
		penguin.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
		
		Label lblTitle = new Label(composite_1, SWT.NONE);
		lblTitle.setText("PengBot v1.0\n"
				+ "DistrictAP team penguin\n"
				+ "\u00A92015 DistrictAP\n");
		lblTitle.setAlignment(SWT.CENTER);
		
		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Link linkHomePage = new Link(composite, 0);
		linkHomePage.setText("<a>Home Page</a>");
		linkHomePage.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					  try {
						Desktop.getDesktop().browse(new URI("http://www.districtap.com"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		Link linkAuthors = new Link(composite, 0);
		linkAuthors.setText("<a>Authors</a>");
		linkAuthors.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					  try {
						Desktop.getDesktop().browse(new URI("http://www.districtap.com/Penguins"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		Link linkLiscense = new Link(composite, 0);
		linkLiscense.setText("<a>License</a>");
		linkLiscense.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					  try {
						Desktop.getDesktop().browse(new URI("http://www.gnu.org/licenses/gpl.html"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		Link linkSupport = new Link(composite, SWT.NONE);
		linkSupport.setText("<a>Support</a>");
		linkSupport.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					  try {
						Desktop.getDesktop().browse(new URI("mailto:support@districtap.com"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Button btnOkay = new Button(composite_2, SWT.CENTER);
		btnOkay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnOkay.setText("   Okay   ");
	}

	public void setVisable(boolean b){
		shell.setVisible(b);
	}
}
