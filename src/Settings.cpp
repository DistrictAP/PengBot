#include "Pircbot.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <map>

using namespace std;

bool loadSettings(PircBot *bot,string file){
	string host;
	string port;
	string key;
	string channel;
	string nick;

	ifstream settings;
	settings.open(file);
	//couldn't open file switch to default settings
	if(!settings.is_open()){
		settings.open("../Resources/Settings/Defaults.txt");
		//couldn't open defaults
		if(!settings.is_open()){
			cout<<"Unable to open Settings file.\n";
			return false;
		}
	}

	string line;
	//First line is hostname
	getline(settings,line);
	line.pop_back();
	host = line.substr(9);
	(*bot).setHost(host);

	//Second line is the port
	getline(settings,line);
	line.pop_back();
	port = line.substr(5);
	(*bot).setPort(port);

	//Third line is the key/Password
	getline(settings,line);
	key = line.substr(4,line.length()-1).c_str();
	(*bot).setKey(key);

	//Fourth line is the channel
	getline(settings,line);
	line.pop_back();
	channel = line.substr(8).c_str();
	(*bot).setChannel(channel);

	//Fifth line is the Nickname
	getline(settings,line);
	line.pop_back();
	nick = line.substr(9).c_str();
	(*bot).setNick(nick);

	settings.close();
	return true;
}

bool saveSettings(PircBot bot,std::string file){
	ofstream settings;
	settings.open(file);
	if(!settings.is_open()){
		cout<<"Problem Writing to disk.";
		return false;
	}
	settings<<"HostName:"<<bot.getHost()<<endl;
	settings<<"Port:"<<bot.getPort()<<endl;
	settings<<"Key:"<<bot.getKey()<<endl;
	settings<<"Channel:"<<bot.getChannel()<<endl;
	settings<<"NickName:"<<bot.getNick()<<endl;
	settings.close();
	return true;
}

bool loadCommands(PircBot *bot,std::string file){
	map<string,string> commands;

	ifstream settings;
	settings.open(file);
	if(!settings.is_open()){
		return false;
	}
	string command;
	string result;
	//While there are two lines left, continue storing them in the map.
	while(getline(settings,command)&&getline(settings,result)){
		commands.insert(pair<string,string>(command,result));
	}
	bot->setCommands(commands);

	return true;
}

bool loadFilter(PircBot *bot,std::string file){
	set<string> filter;
	int strikes = 0;
	string phrase;

	ifstream settings;
	settings.open(file);

	if(!settings.is_open()){
		return false;
	}
	//get number of strikes
	if(getline(settings,phrase)){
		//get the charachters after : and turn that into the number of strikes
		strikes = stoi(phrase.substr(phrase.find(':')+1));
	}
	string warnings[strikes];
	for(int i=0;i<strikes&&getline(settings,phrase);i++){
		warnings[i] = phrase;
	}

	//While there are two lines left, continue storing them in the map.
	while(getline(settings,phrase)){
		filter.insert(phrase);
	}
	bot->setFilter(strikes,warnings,filter);

	return true;
}
