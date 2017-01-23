/*
 * PircBot.cpp
 *
 * Last edited on 1/01/17
 * 	Contributors:
 * 		Nicholas Anderson
 */
#include "Pircbot.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <time.h>
#include <cstring>
#include <iostream>
#include <sys/socket.h>

using namespace std;

#define MAXDATASIZE 100

//Retrieves the settings from the preformatted settigns file
//TODO Encript the settings file (due to passwords and whatnot)
//TODO allow path to use windows

void PircBot::start(){
	bool connected = connectToHost();
	int numbytes;
	char buffer[MAXDATASIZE];
	string buf;
	Message msg;
	//if connected send pass,nick and join request
	if(connected){
		//send key
		sendData("PASS " + key + "\r\n");

		//send nickname
		sendData("NICK " + nick + "\r\n");

		//send request for some twitch specific stuff
		sendData("CAP REQ :twitch.tv/membership\r\n");

		//send Join request
		sendData("JOIN " + channel + "\r\n");

	}

	//loop to recieve messages
	while (connected){
		//declarations
		//Recieve & print Data
		numbytes = recv(s,buffer,MAXDATASIZE-1,0);
		buf = string(buffer);
		cout<<buf;

		//Pass buf to the message handler
		if(buf.find("PING")!=string::npos){//if message is ping, respond with pong, or connection will close
			onPing(buf.c_str());
		//the buffer contains a Message, Join or Part
		}else if(buf.find("PRIVMSG")!=string::npos){
			int tPos = buf.find(" ")+1;
			int tLength = buf.find('#',tPos)-tPos-1;
			int cPos = buf.find('#',tPos+tLength);
			int cLength = buf.find(':',cPos)-cPos-1;
			msg.user = buf.substr(1,buf.find('!')-1);
			msg.type = buf.substr(tPos,tLength);
			msg.channel = buf.substr(cPos,cLength);
			msg.message = buf.substr(buf.find(':',cPos+cLength));
			onMessage(msg);
		}else if(buf.find("JOIN")!=string::npos){
			onJoin(buf.substr(1,buf.find('!')));
		}else if(buf.find("PART")!=string::npos){
			onPart(buf.substr(1,buf.find('!')));
		}
		//if the buffer is now empty
		if(numbytes==0){
			cout << "**Connection Closed**";
			cout << timeNow() << endl;
			connected = false;
		}
		buf.clear();
		memset(buffer, 0, sizeof buffer);
	}
}

//connects to the host using the stored host and port
bool PircBot::connectToHost(){
	struct addrinfo hints, *servinfo;

	//ensure that the servinfo is clear
	memset(&hints, 0, sizeof hints); //make sure struct is empty

	//setup hints
	hints.ai_family = AF_UNSPEC; // any address family is acceptable (IPv4/IPv6)
	hints.ai_socktype = SOCK_STREAM; //Using TCP 	sockets

	//Setup the structs if unsuccessfull print why
	int res;
	if((res = getaddrinfo(host.c_str(),port.c_str(),&hints,&servinfo)) !=0){
		fprintf(stderr,"getaddrinfo: %s\n",gai_strerror(res));
		return false;
	}

	//setup the socket
	if((s = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol)) == -1){
		perror("client socket");
		return false;
	}

	//Connect to the host
	if(connect(s,servinfo->ai_addr,servinfo->ai_addrlen) == -1){
		close(s);
		perror("Client Connect");
		return false;
	}
	//release address information as we don't need it anymore
	freeaddrinfo(servinfo);
	return true;
}

//Returns the current date and time
string PircBot::timeNow(){
	string timeNow;
	time_t t = time(0);   // get time now
	struct tm * now = localtime( & t );//turn into a struct with readable information
	if(now->tm_hour==0){//it is 12am
		timeNow = to_string(now->tm_hour) + ':' + to_string(now->tm_min) + "am";
	}else if(now->tm_hour<12){//it is between 12am and noon
		timeNow = to_string(now->tm_hour) + ':' + to_string(now->tm_min) + "am";
	}else if(now->tm_hour==12){//it is 12pm
		timeNow = to_string(now->tm_hour) + ':' + to_string(now->tm_min) + "pm";
	}else{//it between 12pm and 12am
		timeNow = to_string(now->tm_hour%12) + ':' + to_string(now->tm_min) + "pm";
	}
	return timeNow;
}

//send some data
bool PircBot::sendData(string data){
	int len = data.length();
	int bytes_sent = send(s,data.c_str(),len,0);
	return !(bytes_sent==0);
}
//on recieving a PING, send Pong
void PircBot::onPing(const char *buf){
	sendData("PONG :tmi.twitch.tv\r\n");
}

void PircBot::onMessage(Message msg){
	for(auto const entry:commands){
		if(msg.message.find(entry.first)!=string::npos){
			string message = "PRIVMSG " + channel + " :"
				+ formatReply(entry.second,msg) + "\r\n";
			sendData(message);
			break;
		}
	}
}
void PircBot::onJoin(string usr){
	users.insert(usr);
}
void PircBot::onPart(string usr){
	users.erase(usr);
}
//replaces the '$'keywords with their respective values
string PircBot::formatReply(string reply,Message msg){
	while(reply.find("$User")!=string::npos){
		reply.replace(reply.find("$User"),5,msg.user);
	}
	while(reply.find("$Time")!=string::npos){
		reply.replace(reply.find("$Time"),5,timeNow());
	}
	return reply;
}


//getters and setters
void PircBot::setHost(string h){
	host = h;
}
void PircBot::setPort(string p){
	port = p;
}
void PircBot::setKey(string k){
	key = k;
}
void PircBot::setChannel(string c){
	channel = c;
}
void PircBot::setNick(string n){
	nick = n;
}
void PircBot::setCommands(map<string,string> c){
	commands = map<string,string>(c);
}
string PircBot::getHost(){return host;}
string PircBot::getPort(){return port;}
string PircBot::getKey(){return key;}
string PircBot::getChannel(){return channel;}
string PircBot::getNick(){return nick;}
map<string,string> PircBot::getCommands(){
	return commands;
}
