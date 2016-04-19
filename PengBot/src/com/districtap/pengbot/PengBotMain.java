package com.districtap.pengbot;
import org.eclipse.swt.widgets.Display;

public class PengBotMain {
	static Bot bot;
	 @SuppressWarnings("unused")
	 public static void main(String[] args) throws Exception{
		 Settings.loadBot();
		  bot = new Bot();
		//start the GUI
		Display display = new Display();
	    BotGUI gui = new BotGUI(display,bot);
	    display.dispose();
	}
	 
	public static void Connect(Bot chatBot, String channel)throws Exception{
		//TODO https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
			//creates Bot
			bot = chatBot;
			//enables debugging output
			bot.setVerbose(true);
			//Connect to the IRC server
			bot.connect(Settings.getHostName(),
				Settings.getPort(),Settings.getKey());
			//twitch specific "to get users"
			bot.sendRawLine("CAP REQ :twitch.tv/membership");
			bot.joinChannel("#"+channel);
			BotGUI.addMessage("Connected to: " + channel);
	}
	 
	public static void Connect(Bot chatBot)throws Exception{
		    
			//creates Bot
			bot = chatBot;
			//enables debugging output
			bot.setVerbose(true);
			
			//Connect to the IRC server
			bot.connect(Settings.getHostName(),
				Settings.getPort(),Settings.getKey());
			//twitch specific "to get users"
			bot.sendRawLine("CAP REQ :twitch.tv/membership");
			bot.joinChannel("#"+Settings.getChannel());
			bot.sendRawLine("NAMES");
			BotGUI.addMessage("Connected to: " + Settings.getChannel());
	}
	
	public static void disconnect(){
		bot.disconnect();
	}
}
