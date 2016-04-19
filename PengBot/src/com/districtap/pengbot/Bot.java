package com.districtap.pengbot;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class Bot extends PircBot{
	private static ArrayList<String> commands;
	private static ArrayList<String> replies;
	private static ArrayList<String>[] filter;
	private String Message;
	@SuppressWarnings("unused")
	private boolean streaming;
	private String Content;
	private String ip;
	@SuppressWarnings("unused")
	private Timestamp streamStart;
	private Date date = new Date();
	private ArrayList<String> users = new ArrayList<String>();

	
	public Bot(){
			//setting the name
			setName(Settings.getName());
			//Adds numbers to the name if it is taken
			setAutoNickChange(Settings.isAutoChange());
			commands = Settings.getCommands();
			replies = Settings.getReplies();
			filter = Settings.getFilter();
			Content = "A game they have not told me";
			ip = "An ip they have not told me";
	}
	
	public void timeOut(String Viewer,int seconds){
		sendMessage("#" + Settings.getChannel(), "/timeout " + Viewer + " " + seconds);
	}
	
	protected void onUserList(String channel, User[] users){
		for(User u:users){
			if(!u.getNick().equalsIgnoreCase(Settings.getName()))
				this.users.add(u.getNick());
		}
		Collections.sort(this.users);
		BotGUI.refreshViewers();
	}

	public void onPart(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		System.out.println("User Left");
		 for (int i = 0;i<users.size();i++){
			 if (users.get(i).equals(sourceNick)){
				 users.remove(i);
			 }
		 }
		 BotGUI.refreshViewers();
	}
	
	public void resetUsers(){
		users = new ArrayList<String>();
		BotGUI.refreshViewers();
	}
	
	public void resetCommands(){
		commands = Settings.getCommands();
		replies = Settings.getReplies();
	}
	
	public void onJoin(String channel, String sender, String login, String hostname) {
		System.out.println("User Joined");
		users.add(sender);
		Collections.sort(users);
		BotGUI.refreshViewers();
	}
	
	public void onMessage(String channel, String sender,
			String login, String hostname, String message){
		//adds message to output textbox
		BotGUI.addMessage(sender, message);
		for(int i=0;i<commands.size();i++){
			String Command = commands.get(i);
			if(message.equalsIgnoreCase(Command)){
				//TODO
//				if(checkFilter(sender, message))
//					return;
				makeReply(sender, message, i);
				sendMessage("#" + Settings.getChannel(), Message);
				return;
			}
		}
	}
	
	public void onSend(String message){
		makeReply(Settings.getName(), message,0);
		sendMessage("#" + Settings.getChannel(), Message);
	}
	
	public void streamChange(boolean streaming,String content, String ip){
		this.streaming = streaming;
		if (streaming){
			streamStart = new Timestamp(date.getTime());
			this.Content = content;
			this.ip = ip;
		}
	}
	
	public ArrayList<String> getUsers(){
		return this.users;
	}
	
	//add custom variables into message "$" denotes special code
	public void makeReply(String sender, String message, int i){
		String feedback="";
		if(sender !=Settings.getName()){
			feedback = replies.get(i);
		}else{
			feedback = message;
		}
		feedback = feedback.replaceAll("\\$Sender",sender);
		feedback = feedback.replaceAll("\\$IP", ip);
		feedback = feedback.replaceAll("\\$Content",Content);
		feedback = feedback.replaceAll("\\$Time", Long.toString(date.getTime()));
		Message = feedback;
	}
	
	public String makeReply(String sender, String message, String reply){
		reply = reply.replaceAll("\\$Sender", sender);
		reply = reply.replaceAll("\\$IP", ip);
		reply = reply.replaceAll("\\$Content", Content);
		reply = reply.replaceAll("\\$Time", Long.toString(date.getTime()));
		return reply;
	}
	//TODO
//	public boolean checkFilter(String sender, String message){
//		Pattern punishment = Pattern.compile("Time=[0-9]+:");
//		Matcher timeout;
//		String time = "1";
//		for(int i=0;i<filter[0].size();i++){
//			if(message.contains(filter[0].get(i))){
//				String reply = filter[1].get(i);
//				timeout = punishment.matcher(reply);
//				reply = makeReply(sender,message,reply);
//				if(timeout.find()){
//					time = timeout.group(1);
//					sendMessage("#" + Settings.getChannel(),"/timeout "+sender + " " + time);
//				}else{
//					sendMessage("#" + Settings.getChannel(),"/ban" + sender);
//				}
//				sendMessage("#" + Settings.getChannel(),reply);	
//				return true;
//			}
//		}
//		return false;
//	}
	
}
