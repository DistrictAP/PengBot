package com.districtap.pengbot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class BotGUI {
	private static boolean active;
	private static Shell shell;
	private Text txtMessage;
	private Text txtContent;
	private static Bot bot;
	private Text txtIp;
	private static Text txtChannel;
	private static Text txtOutput;
	private static String tmp;
	private static List viewers;
	private static String scrollable = "";
	private static Label lblActiveViewers;
	private GUI aboutGui;
	private GUI cheatSheet;
	private GUI commandsGui;
	private GUI settingsGui;
	protected static Tray tray;
	
	

	@SuppressWarnings("static-access")
	public BotGUI(Display disp,Bot bot) {
		//create all the other shells
		aboutGui = new About();
		cheatSheet = new CheatSheet();
		commandsGui = new CommandsGUI();
		settingsGui = new SettingsGUI();
		
		
		tray = disp.getSystemTray();
		active = true;
		Display display = disp;
		this.bot = bot;
		createContents(display);
		shell.open();
		shell.layout();
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  active = false;
		    	  shell.setVisible(false);
		    	  event.doit = false;
		      }
		    });
		openTray(display);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
		shell.setSize(500, 352);
		shell.setText(Settings.getName());
		shell.setLayout(null);
		
		final Button chkAutoScroll = new Button(shell, SWT.CHECK);
		chkAutoScroll.setEnabled(false);
		chkAutoScroll.setBounds(380, 0, 93, 16);
		chkAutoScroll.setSelection(true);
		chkAutoScroll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO make auto scroll disableable
				//	boolean autoScroll = chkAutoScroll.getSelection();
			}
		});
		chkAutoScroll.setText("Auto scroll");
		
		txtOutput = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtOutput.setBounds(225, 21, 249, 234);
		txtOutput.setText("Welcome");
		txtOutput.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		txtOutput.addMouseTrackListener(new MouseTrackListener(){

			@Override
			public void mouseEnter(MouseEvent e) {
				scrollable = "txtOutput";
			}

			@Override
			public void mouseExit(MouseEvent e) {
				scrollable = "";
			}

			@Override
			public void mouseHover(MouseEvent e) {			
			}
		});
		
		Label lblChat = new Label(shell, SWT.NONE);
		lblChat.setBounds(334, 0, 25, 15);
		lblChat.setText("Chat");
		
		Button btnSend = new Button(shell, SWT.NONE);
		btnSend.setBounds(313, 261, 75, 22);
		btnSend.addSelectionListener(new SelectionAdapter() {
			//on CLick
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				bot.onSend(txtMessage.getText());
				txtMessage.setText("");
			}
		});
		btnSend.setText("Send");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setBounds(394, 261, 80, 22);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtOutput.setText("");
			}
		});
		btnNewButton_1.setText("Clear Chat");
		
		txtMessage = new Text(shell, SWT.BORDER);
		txtMessage.setBounds(10, 261, 297, 21);
		txtMessage.setFocus();
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menuFile = new Menu(mntmFile);
		mntmFile.setMenu(menuFile);
		
		MenuItem mntmAbout = new MenuItem(menuFile, SWT.NONE);
		mntmAbout.setText("About");
		mntmAbout.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				aboutGui.open();
			}});
		
		
		MenuItem menuItemQuit = new MenuItem(menuFile, SWT.NONE);
		menuItemQuit.setText("Quit");
		menuItemQuit.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				PengBotMain.disconnect();
				System.exit(0);
			}});
		
		MenuItem mntmSettings = new MenuItem(menu, SWT.CASCADE);
		mntmSettings.setText("Settings");
		
		Menu menu_2 = new Menu(mntmSettings);
		mntmSettings.setMenu(menu_2);
		
		MenuItem smntmSettings = new MenuItem(menu_2, SWT.NONE);
		smntmSettings.setText("Settings");
		smntmSettings.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setEnabled(false);
				settingsGui.open();
			}});
		
		MenuItem mntmCommands = new MenuItem(menu_2, SWT.NONE);
		mntmCommands.setText("Commands");
		mntmCommands.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setEnabled(false);
				commandsGui.open();
				Settings.loadBot();
				bot.resetCommands();
			}});
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(0, 0, 219, 255);
		
		TabItem tbtmHome = new TabItem(tabFolder, SWT.NONE);
		tbtmHome.setText("Home");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmHome.setControl(composite_1);
		
		Group grpStreaming = new Group(composite_1, SWT.NONE);
		grpStreaming.setText("Streaming");
		grpStreaming.setBounds(10, 10, 125, 106);
		
		final Button chkStreaming = new Button(grpStreaming, SWT.CHECK);
		chkStreaming.setBounds(20, 80, 93, 16);
		chkStreaming.setText("Streaming");
		
		FocusListener focus = new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
				public void focusLost(FocusEvent arg0) {
					bot.streamChange(chkStreaming.getSelection(),txtContent.getText(),txtIp.getText());
				}};
			
		
		
		txtContent = new Text(grpStreaming, SWT.BORDER);
		txtContent.setBounds(10, 26, 76, 21);
		txtContent.setMessage("Content");
		txtContent.setToolTipText("Whatever you are streaming.");
		txtContent.addFocusListener(focus);
		
		txtIp = new Text(grpStreaming, SWT.BORDER);
		txtIp.setBounds(10, 53, 76, 21);
		txtIp.setMessage("IP");
		txtIp.addFocusListener(focus);
				
		txtChannel = new Text(composite_1, SWT.BORDER);
		txtChannel.setBounds(9, 165, 111, 21);
		txtChannel.setMessage(Settings.getChannel());
		
		Button btnConnect = new Button(composite_1, SWT.NONE);
		btnConnect.setBounds(10, 192, 75, 25);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txtChannel.getText().equals("")){
					try {
						PengBotMain.Connect(bot);
					} catch (Exception e1) {
						txtOutput.setText(e1.getStackTrace().toString());
					}
				}else{
					try {
						PengBotMain.Connect(bot,txtChannel.getText());
					} catch (Exception e1) {
						txtOutput.setText(e1.getStackTrace().toString());
					}
				}
				
			}});
		btnConnect.setText("Connect");
		
		Button btnDisconnect = new Button(composite_1, SWT.NONE);
		btnDisconnect.setBounds(126, 192, 75, 25);
		btnDisconnect.setText("Disconnect");
				
		Label lblQuickChangeServer = new Label(composite_1, SWT.NONE);
		lblQuickChangeServer.setBounds(10, 144, 125, 17);
		lblQuickChangeServer.setText("Quick Change Server");
		btnDisconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PengBotMain.disconnect();
				addMessage("Disconnected");
			}});
		chkStreaming.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				
				if(txtContent.getText() == "" && txtIp.getText() == ""){
					if(AlertYesIgnore("Missing Content & IP","Warning, Both the Content and IP boxes are not filled out this may result "
							+ "in increased difficulty for your viewers to join your game. Continue anyways?")==SWT.OK){
						bot.streamChange(chkStreaming.getSelection(),txtContent.getText(),txtIp.getText());
					}else
						txtContent.setFocus();
					
				}else if(txtContent.getText() == null){
					if(AlertYesIgnore("Missing Content","Warning, the Content box is empty "
							+ "this may make it difficult for your Viewers to join your game (if applicable). "
							+ "Continue anyways?")==SWT.OK){
						bot.streamChange(chkStreaming.getSelection(),txtContent.getText(),txtIp.getText());
					}else
						txtContent.setFocus();
					
				}else if(txtIp.getText() == null){
					if(AlertYesIgnore("Missing IP","Warning, the IP address box is empty "
							+ "this may make it difficult for your Viewers to join your game (if applicable). "
							+ "Continue anyways?")==SWT.OK){
						bot.streamChange(chkStreaming.getSelection(),txtContent.getText(),txtIp.getText());
					}else
						txtIp.setFocus();
					
				}else
					bot.streamChange(chkStreaming.getSelection(),txtContent.getText(),txtIp.getText());
				
			}});

		TabItem tbtmCommands = new TabItem(tabFolder, SWT.NONE);
		tbtmCommands.setText("Quick Add");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmCommands.setControl(composite);
		
		final StyledText txtCommand = new StyledText(composite, SWT.BORDER);
		txtCommand.setBounds(10, 30, 191, 37);
		
		final StyledText txtReturn = new StyledText(composite, SWT.BORDER);
		txtReturn.setBounds(10, 90, 191, 90);
		
		Button btnSubmit = new Button(composite, SWT.NONE);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Settings.addCommand(shell,txtCommand.getText(), txtReturn.getText());
				Settings.loadBot();
				bot.resetCommands();
			}
		});
		btnSubmit.setBounds(126, 192, 75, 25);
		btnSubmit.setText("Submit");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setBounds(65, 9, 75, 15);
		lblNewLabel_1.setText("Command");
		
		
		
		Label lblReturn = new Label(composite, SWT.NONE);
		lblReturn.setAlignment(SWT.CENTER);
		lblReturn.setBounds(65, 73, 75, 15);
		lblReturn.setText("Return");
		
		Button btnCheatSheet = new Button(composite, SWT.NONE);
		btnCheatSheet.setBounds(10, 192, 75, 25);
		btnCheatSheet.setText("Cheat Sheet");
		btnCheatSheet.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				cheatSheet.open();
			}
		});
		
		TabItem tbtmModeration = new TabItem(tabFolder, SWT.NONE);
		tbtmModeration.setText("Moderation");
		
		Composite Moderation = new Composite(tabFolder, SWT.NONE);
		tbtmModeration.setControl(Moderation);
		
		viewers = new List(Moderation, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewers.setBounds(0, 20, 154, 104);
		viewers.addMouseTrackListener(new MouseTrackListener(){

			@Override
			public void mouseEnter(MouseEvent e) {
				scrollable = "viewers";
			}

			@Override
			public void mouseExit(MouseEvent e) {
			}

			@Override
			public void mouseHover(MouseEvent e) {
				
			}
			
		});
		
		lblActiveViewers = new Label(Moderation, SWT.NONE);
		lblActiveViewers.setBounds(0, 1, 201, 15);
		lblActiveViewers.setText("Active Viewers");
		
		final Scale scale = new Scale(Moderation, SWT.NONE);
		scale.setPageIncrement(1);
		scale.setMaximum(8);
		scale.setMinimum(1);
		scale.setSelection(1);
		scale.setBounds(0, 143, 201, 42);
		
		Label lblSecs = new Label(Moderation, SWT.NONE);
		lblSecs.setBounds(34, 191, 19, 23);
		lblSecs.setText("10");
		
		Label label_1 = new Label(Moderation, SWT.NONE);
		label_1.setBounds(155, 191, 19, 23);
		label_1.setText("60");
		
		Label lblBan = new Label(Moderation, SWT.CENTER);
		lblBan.setBounds(173, 191, 32, 15);
		lblBan.setText("Ban");
		
		Label lblNewLabel = new Label(Moderation, SWT.CENTER);
		lblNewLabel.setBounds(0, 130, 201, 15);
		lblNewLabel.setText("Timeout Mins");
		
		Button btnTimeout = new Button(Moderation, SWT.NONE);
		btnTimeout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//TODO
				if(viewers.getSelectionCount()!=0)
					bot.timeOut(viewers.getSelection()[viewers.getSelectionIndex()], scale.getSelection());
			}
		});
		btnTimeout.setBounds(65, 192, 75, 25);
		btnTimeout.setText("Timeout!");
		
		Label label = new Label(Moderation, SWT.NONE);
		label.setBounds(10, 191, 6, 15);
		label.setText("1");
		shell.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseScrolled(MouseEvent e) {
				switch (scrollable){
				case "viewers":
					if(e.count<0){
						viewers.select(viewers.getSelectionIndex()-1);
						viewers.showSelection();
					}else{
						viewers.select(viewers.getSelectionIndex()+1);
						viewers.showSelection();
					}
					break;
				case "txtOutput":
					if(e.count<0){
						txtOutput.setTopIndex(txtOutput.getTopIndex()-1);
					}else{
						txtOutput.setTopIndex(txtOutput.getTopIndex()+1);
					}
					break;
				default:
					break;
				}
			}
			
		});
	}
	
	 public static int AlertYesIgnore(String Title, String message) {
		    MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING |SWT.CANCEL | SWT.OK);
		    messageBox.setMessage(message);
		    messageBox.setText(Title);
		    int rc = messageBox.open();
		    return rc;
	 }

	public static void addMessage(String sender, String message){
		addMessage(sender + ": " + message);
	}
	
	public static void refreshViewers(){
		new Thread(new Runnable() {
		      public void run() {
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() {
		            	   viewers.removeAll();
		   				if(bot.getUsers()!=null){
		   					for(String user:bot.getUsers()){
		   						viewers.add(user);
		   						lblActiveViewers.setText("Active Viewers: " + bot.getUsers().size());
		   					}
		   				}
		               }
		            });
		      }
		   }).start();
	}
	
	public static void addMessage(final String text){
		tmp = text;
		//prevents not in same thread access error
		 new Thread(new Runnable() {
		      public void run() {
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() {
				           String s = "\n" + tmp;
				           txtOutput.append(s);
				           //TODO scroll to bottom
		               }
		            });
		      }
		   }).start();
    }
	
	public static void enable(){
		shell.setEnabled(true);
	}

	//TODO Make Tray Menu Work
	private void openTray(final Display display){	     
	    if (tray == null) {
	      System.out.println("The system tray is not available");
	    } else {
	    	final TrayItem trayMenu = new TrayItem(tray, SWT.NONE);
	    	trayMenu.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin.png"));
	    	trayMenu.setToolTipText(Settings.getName());
      	  	//setup System Tray Menu
	    	final Menu menu = new Menu(shell, SWT.POP_UP);
	     
	    	final MenuItem mntmConnected = new MenuItem(menu, SWT.CHECK);
			mntmConnected.setImage(SWTResourceManager.getImage("./Resources/IRCXPenguin16x20.png"));
			mntmConnected.setText("Connected");
			mntmConnected.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					
					if(mntmConnected.getSelection()){
						try {
							PengBotMain.Connect(bot);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						mntmConnected.setImage(SWTResourceManager.getImage("./Resources/IRCXPenguin16x20.png"));
					}else{
						PengBotMain.disconnect();
						mntmConnected.setImage(SWTResourceManager.getImage("./Resources/IRCPenguin16x20.png"));
					}
				}
			});
			
			MenuItem mntmCommands = new MenuItem(menu, SWT.CHECK);
			mntmCommands.setText("Commands");
			mntmCommands.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					shell.setEnabled(false);
					commandsGui.open();
					Settings.loadBot();
					bot.resetCommands();
				}});
			MenuItem mntmControlPanel = new MenuItem(menu, SWT.CHECK);
			mntmControlPanel.setText("Control Panel");
			
			MenuItem mntmSettings = new MenuItem(menu, SWT.CHECK);
			mntmSettings.setText("Settings");
			
			@SuppressWarnings("unused")
			MenuItem menuItem = new MenuItem(menu, SWT.SEPARATOR);
			
			MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
			mntmAbout.setText("About");
			mntmAbout.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					aboutGui.open();
				}});
			
			MenuItem mntmQuit = new MenuItem(menu, SWT.NONE);
			mntmQuit.setText("Quit");
			mntmQuit.addListener(SWT.Selection, new Listener() {
		        public void handleEvent(Event event) {
		        	PengBotMain.disconnect();
		        	tray.dispose();
		        	trayMenu.dispose();
					System.exit(0);
		        }
		      });
	      trayMenu.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
	        	menu.setVisible(true);
	        }
	      });
	      trayMenu.addListener(SWT.DefaultSelection, new Listener() {
	        public void handleEvent(Event event) {
	        	//if windows are open, hide all
	        	if(getActive()==true){
	        		active = false;
	        		for(Shell shell:display.getShells())
	        			shell.setVisible(false);
        		//if windows are closed, open all
	        	}else{
	        		active = true;
	        		for(Shell shell:display.getShells())
	        			shell.setVisible(true);
	        	}
	        }
	      });
	        menu.setDefaultItem(mntmControlPanel);
	      trayMenu.addListener(SWT.MenuDetect, new Listener() {
	        public void handleEvent(Event event) {
	          menu.setVisible(true);
	        }
	      });
	    }
	}
	
	private static boolean getActive(){
		  return active;
	  }
}
