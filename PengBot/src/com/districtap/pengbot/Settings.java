package com.districtap.pengbot;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
public class Settings {
	private static String hostName = "irc.twitch.tv";
	private static String port = "6667";
	private static String key;
	//Channel to join
	private static String channel = "";
	//placeholder if no name is found
	private static String name = "Unnamed Bot";
	//Whether or not numbers will be added to the name, if the name is taken
	private static String autoChange = "true";
	private static ArrayList<String>[] filter;
	private static ArrayList<String> commands;
	private static ArrayList<String> replies;
	//goes back one directory to the Settings.txt file
	private static String fileName = "./Settings/Settings.txt";
	
	public static void main(String[] args){
		for(String s:getCheatSheet()){
			System.out.println(s);
		}
		
		loadBot();
		System.out.println(hostName);
		System.out.println(port);
		System.out.println(key);
		System.out.println(channel);
		System.out.println(name);
		System.out.println(autoChange);
	}

	//TODO Make remove
	public static void removeCommand(String Command){
		for(int index = 0;index < commands.size();index++){
			String c = commands.get(index);
			if(Command.equals(c)){
				commands.remove(index);
				publishBot(hostName, port, key, channel, name, autoChange, commands, replies);
				return;
			}
		}
	}
	
	public static boolean addCommand(Shell instigator,String Command, String Return){
		MessageBox messageBox = new MessageBox(instigator, SWT.ICON_ERROR | SWT.OK);
		if(Command.equals("")){
			 messageBox.setMessage("The command Box was left empty! Command not created");
			 messageBox.open();
			 return false;
		}else if(Return.equals("")){
			 messageBox.setMessage("The Return Message box was left empty! Command not created");
			 messageBox.open();
			 return false;
		}
		
		for(int index = 0;index < commands.size();index++){
			String c = commands.get(index);
			String r = replies.get(index);
			//if the exact same command return combo is already present
			if (Command.equalsIgnoreCase(c)&&Return.equalsIgnoreCase(r)){
			    messageBox.setMessage("Warning that command and return message combination is already taken, please "
			    		+ "either edit the name of this command, or the return of "+
			    		Command);
			    messageBox.open();
			    return false;
			    
			  //TODO Check to see if duplicate commands are allowed
			  //If the command is already taken, but has a different return message
			}else if(Command.equalsIgnoreCase(c)){
				replies.set(index, Return);
				publishBot(hostName, port, key, channel, name, autoChange, commands, replies);
				return true;
			}else if(Return.equalsIgnoreCase(r)){
				commands.set(index, Command);
				publishBot(hostName, port, key, channel, name, autoChange, commands, replies);
				return true;
			}
		}
		commands.add(Command);
		replies.add(Return);
		publishBot(hostName, port, key, channel, name, autoChange, commands, replies);
		return true;
	}
	
	public static void publishCommands(ArrayList<String> Commands, ArrayList<String> Returns){
		publishBot(hostName, port, key, channel, name, autoChange, Commands, Returns);
	}
	
	public static void publishBot(String hostName, String port, String key,
			String Channel,String Name,String Autochange, ArrayList<String> Commands,
			ArrayList<String> Results){
		try {
	        // FileReader reads text files in the default encoding.
	        FileWriter filewriter = 
	            new FileWriter(fileName);
	        // Always wrap FileReader in BufferedReader.
	        BufferedWriter writer = 
	            new BufferedWriter(filewriter);
	        PrintWriter clearer = new PrintWriter(fileName);
	        clearer.close();
	        writer.write("HostName:"+hostName);
	        writer.newLine();
	        writer.write("Port:"+port);
	        writer.newLine();
	        writer.write("Key:"+key);
	        writer.newLine();
	        writer.write("Channel:"+Channel);
	        writer.newLine();
	        writer.write("NickName:"+Name);
	        writer.newLine();
	        writer.write("Auto Name Change:"+autoChange);
	        writer.newLine();
	        writer.write("Commands:");
	        writer.newLine();
	        for(String s:Commands){
	        	writer.write(s);
	        	writer.newLine();
	        }
	        writer.write("Results:");
	        for(String s:Results){
	        	writer.newLine();
	        	writer.write("#"+s);
	        }
	        writer.close();
		}
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            fileName + "'");                
	    }
	    catch(IOException ex) {
	    	System.out.println(
		            "Error reading file '" 
		            + fileName + "'");                   
	    }
		loadBot();
	}

	public static void resetBot(){
		String line;
		try {
	        // FileReader reads text files in the default encoding.
	        FileWriter filewriter = 
	            new FileWriter(fileName);
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedWriter writer = 
	            new BufferedWriter(filewriter);
	        
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = 
	            new FileReader("./Settings/Defaults.txt");
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = 
	            new BufferedReader(fileReader);
	
	        //while the current line is not null
	        while((line = bufferedReader.readLine()) != null) {
	        	writer.write(line);
	        	writer.newLine();
	        }
	        // Always close files.
	        bufferedReader.close();
	        writer.close();
		}
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + fileName + "'");                   
	    }
	}

	public static void loadBot(){
		load(1,"HostName:");
		load(2,"Port:");
		load(3,"Key:");
		load(4,"Channel:");
		load(5,"NickName:");
		load(6,"Auto Name Change:");
		loadCommands();
		loadResults();
	}

	public static void load(int setting,String searchTerm){
		Pattern pattern = Pattern.compile(searchTerm);
		String line;
		String value = "";
		try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = 
	            new FileReader(fileName);
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = 
	            new BufferedReader(fileReader);
	
	        //while the current line is not null
	        while((line = bufferedReader.readLine()) != null) {
	        	Matcher matcher = pattern.matcher(line);
	        	if(matcher.find()){
	        		value = line.substring(searchTerm.length());
	        	}
	        	switch (setting) {
	            case 1: hostName = value;
	            		break;
	            case 2: port = value;
	            		break;
        		case 3:  key = value;
	                    break;
	            case 4:  channel = value;
	                    break;
	            case 5:  name = value;
	                    break;
	            case 6:  autoChange = value;
	                    break;
	        	}
	        	
	        }
	        // Always close files.
        bufferedReader.close();	
		}
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + fileName + "'");                   
	    }
	}

	public static void loadCommands(){
		commands = new ArrayList<String>();
		String line;
		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            //while the current line is not null
            while((line = bufferedReader.readLine()) != null) {
                if(line.charAt(0) == "!".charAt(0)){
                	commands.add(line);
                }
            }

            // Always close files.
            bufferedReader.close();
		}
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                   
        }
	}
	
	public static void loadResults(){
		replies = new ArrayList<String>();
		String line;
		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            //while the current line is not null
            while((line = bufferedReader.readLine()) != null) {
                if(line.charAt(0)== "#".charAt(0)){
                	replies.add(line.substring(1));
                }
            }

            // Always close files.
            bufferedReader.close();
		}
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                   
        }
	}
	
	public static void loadFilter(){
		//example
		//Term:Fuck you!
		//Reply:Time=5:$Sender Please do not use that language.
		Pattern term = Pattern.compile("Term:");
		Pattern reply = Pattern.compile("Reply:");
		Matcher termFinder;
		Matcher replyFinder;
		String line = "";
		int last = 1;
		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader("./Settings/Filter.txt");

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            //while the current line is not null
            while((line = bufferedReader.readLine()) != null) {
            	termFinder = term.matcher(line);
            	replyFinder = reply.matcher(line);
            	if(termFinder.find()){
            		filter[1].add(line.substring(5));
            		last = 1;
            	}else if(replyFinder.find()){
            		filter[2].add(line);
        			last = 2;
            	}else{
            		int index = filter[last].size()-1;
            		String replacement = filter[last].get(index) + line;
            		filter[last].set(index, replacement);
            	}
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                   
        }
	}

	public static ArrayList<String>[] getFilter(){
		return filter;
	}

	public static String getHostName(){
		return hostName;
	}
	
	public static int getPort(){
		return Integer.parseInt(port);
	}
	
	public static String getKey(){
		return key;
	}
	
	public static String getChannel(){
		return channel;
	}
	
	public static String getName(){
		return name;
	}
	
	public static boolean isAutoChange(){
		return Boolean.getBoolean(autoChange);
	}
	
	public static ArrayList<String> getCommands(){
		return commands;
	}
	
	public static ArrayList<String> getReplies(){
		return replies;
	}

	public static ArrayList<String> getCheatSheet(){
		ArrayList<String> arr = new ArrayList<String>();
		String line;
		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader("./Settings/CheatSheet.txt");

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            //while the current line is not null
            while((line = bufferedReader.readLine()) != null) {
                arr.add(line);
            }

            // Always close files.
            bufferedReader.close();
		}
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                   
        }
		
		return arr;
	}

}
