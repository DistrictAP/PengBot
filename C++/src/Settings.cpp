#include "Pircbot.h"
#include <iostream>
#include <fstream>
#include <string.h>

using namespace std;
bool loadSettings(PircBot *bot,char const *file){
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
	host = line.substr(9);
	(*bot).setHost(host);

	//Second line is the port
	getline(settings,line);
	port = line.substr(5);
	(*bot).setPort(port);

	//Third line is the key/Password
	getline(settings,line);
	key = line.substr(4).c_str();
	(*bot).setKey(key);

	//Fourth line is the channel
	getline(settings,line);
	channel = line.substr(8).c_str();
	(*bot).setChannel(channel);

  //Fifth line is the Nickname
	getline(settings,line);
	nick = line.substr(9).c_str();
	(*bot).setNick(nick);

	settings.close();
	return true;
}
