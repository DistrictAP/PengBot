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
#include <thread>
#include <algorithm>

using namespace std;

#define MAXDATASIZE 2000

//Retrieves the settings from the preformatted settigns file
//TODO Encript the settings file (due to passwords and whatnot)
//TODO allow path to use windows

void PircBot::start(){
	connected = connectToHost();
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

		//request /commands for /w and /ban
		sendData("CAP REQ :twitch.tv/commands\r\n");
	}

	//loop to recieve messages
	while (connected){
		//declarations
		//Recieve & print Data
		numbytes = recv(s,buffer,MAXDATASIZE-1,0);
		buf = string(buffer);
		//Pass buf to the message handler
		buf.pop_back();
		//the buffer contains a Message, Join or Part
		if(buf.find("PRIVMSG")!=string::npos){
			int tPos = buf.find(" ")+1;
			int tLength = buf.find('#',tPos)-tPos-1;
			int cPos = buf.find('#',tPos+tLength);
			int cLength = buf.find(':',cPos)-cPos-1;
			msg.user = buf.substr(1,buf.find('!')-1);
			msg.type = buf.substr(tPos,tLength);
			msg.channel = buf.substr(cPos,cLength);
			msg.message = buf.substr(buf.find(':',cPos+cLength)+1);
			onMessage(msg);
		}else if(buf.find("WHISPER")!=string::npos){
			msg.user = buf.substr(1,buf.find('!')-1);
			msg.type = "WHISPER";
			msg.channel = channel;
			msg.message = buf.substr(buf.rfind(':',1));
			onWhisper(msg);
		}else if(buf.find("JOIN")!=string::npos){
			onJoin(buf.substr(1,buf.find('!')));
		}else if(buf.find("PART")!=string::npos){
			onPart(buf.substr(1,buf.find('!')));
		}else if(buf.find("PING")!=string::npos){//if message is ping, respond with pong, or connection will close
			onPing(buf.c_str());
		}
		//if the buffer is now empty
		if(numbytes==0){

			connected = false;
		}
		buf.clear();
		memset(buffer, 0, sizeof buffer);
	}
	cout << "**Connection Closed**";
	cout << timeNow() << endl;
}
void PircBot::stop(){
	shutdown(s, SHUT_RDWR);
	connected = false;
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
//Handle messages once recieved.
void PircBot::onMessage(Message msg){
	string message;
	if(filterMessage(msg.message)){
		int strike = onStrike(msg.user);
		if(strike==maxStrikes){//user has used up all of their strikes
			//ban the offender
			message = "PRIVMSG " + msg.channel + " :" + "/ban "+ msg.user + "\r\n";
			sendData(message);
			//Public message explaining the ban
			message = "PRIVMSG " + msg.channel + " :" + formatMessage(warnings[strike-1] + "\r\n",msg);
			sendData(message);
			return;
		}else{
			//timeout the user for 30 seconds
			message = "PRIVMSG " + channel + " :" + "/timeout "+ msg.user + " 30" + "\r\n";
			sendData(message);		//Public message explaining the ban
			//send a message as to why the timeout occured
			message = "PRIVMSG " + channel + " :" + formatMessage(warnings[strike-1] + "\r\n",msg);
			sendData(message);
		}
		return;
	}
	//search to see if a command was used
	for(auto const entry:commands){
		if(msg.message.find(entry.first)!=string::npos){
			string message = "PRIVMSG " + msg.channel + " :"
				+ formatMessage(entry.second,msg) + "\r\n";
			sendData(message);
			break;
		}
	}
	if(msg.message.find("!vote")!=string::npos){
		onVote(msg);
	}
}
void PircBot::onWhisper(Message msg){
	string message;
	//search to see if a command was used
	for(auto const entry:commands){
		if(msg.message.find(entry.first)!=string::npos){
			string message = "PRIVMSG " + msg.channel + " :"
				+ formatMessage("$wr"+entry.second,msg) + "\r\n";
			sendData(message);
			break;
		}
	}
}
void PircBot::onJoin(string id){
	User usr;
	usr.id = id;
	usr.points = 0;
	usr.strikes = 0;
	users.insert(usr);
}
void PircBot::onPart(string id){
	User usr;
	usr.id = id;
	usr.points = 0;
	usr.strikes = 0;
	users.erase(usr);
}
//replaces the '$'keywords with their respective values
string PircBot::formatMessage(string reply,Message msg){
	while(reply.find("$User")!=string::npos){
		reply.replace(reply.find("$User"),5,msg.user);
	}
	while(reply.find("$Time")!=string::npos){
		reply.replace(reply.find("$Time"),5,timeNow());
	}
	while(reply.find("$Filter")!=string::npos){
		reply.replace(reply.find("$Filter"),7,filterList);
	}
	while(reply.find("$wr")!=string::npos){//whisper reply
		reply.replace(reply.find("$wr"),3,".w "+msg.user + " ");
	}
	return reply;
}
//returns true if the message contains a phrase in the filter set.
bool PircBot::filterMessage(string msg){
	//match case regardless of case
	for(auto const phrase:filter){
		auto it = search(msg.begin(),msg.end(),phrase.begin(),phrase.end(),
		[](char ch1, char ch2) {return std::toupper(ch1) == toupper(ch2);}
		);
		if(it != msg.end()){
			cout<<msg<<"="<<phrase<<endl;
			return true;
		}
	}
	return false;
}
//creates and starts a poll.
bool PircBot::createPoll(Poll p){
	string msg;
	if(poll.isOpen){
		return false;
	}
	poll = p;

	msg = "PRIVMSG " + channel + " :";
	msg.append("A poll has been created: ");
	msg.append(p.query);
	msg.append(" The options are as follows: ");
	for(auto const entry:poll.options){
		msg.append(entry.first+",");
	}
	msg.append("Use !vote \"option\" (without quotes) to enter");
	msg.append("\r\n");
	sendData(msg);
	poll.isOpen = true;
	return true;
}
//closes the current poll revealing the results
pair<string,int> PircBot::closePoll(){
	poll.isOpen = false;
	string msg = "PRIVMSG " + channel + " :The poll is now closed: "+
		"The winner is: ";
	pair<string,int> top = pair<string,int>("",0);
	for(auto const entry:poll.options){
		if(entry.second>top.second){
			top = entry;
		}
	}
	msg.append(top.first+" with " + to_string(top.second) + " votes.\r\n");
	sendData(msg);
	return top;
}

//adds a vote into the current poll should one exist
void PircBot::onVote(Message msg){
	std::string vote = msg.message.substr(6);
	std::map<string,int>::iterator option;
	vote.resize(vote.size()-1);
	if(poll.isOpen){
		option = poll.options.find(vote);
		//vote is invalid
		if(option==poll.options.end()){//vote is invalid
			string message = "PRIVMSG " + msg.channel + " :"+msg.user+ ", that is not a valid option." + "\r\n";
			sendData(message);
		}else if(poll.voters.insert(msg.user).second==false){//user already voted
			string message = "PRIVMSG " + msg.channel + " :"+msg.user+ ", your vote has already been counted." + "\r\n";
			sendData(message);
		}else{
			option->second = option->second+1;
			poll.totalVotes++;
			poll.voters.insert(msg.user);
			string message = "PRIVMSG " + msg.channel + " :"+msg.user+ ", your vote has been counted." + "\r\n";
			sendData(message);
		}
	}else{
		string message = "PRIVMSG " + msg.channel + " :"+msg.user+ ", there are no polls open currently." + "\r\n";
		sendData(message);
	}
}
//returns true if this is the third strike for a user.
int PircBot::onStrike(string id){
	User usr;
	usr.id = id;
	set<User>::iterator iter = users.find(usr);
	if (iter != users.end()){
		User tmp = *iter;
		usr.strikes = tmp.strikes+1;
		usr.points = tmp.points;
		users.erase(tmp);
		users.insert(usr);
	}else{
		onJoin(id);
		return onStrike(id);
	}
	return usr.strikes;
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
void PircBot::setFilter(int strikes,string *w,set<string> f){
	maxStrikes=strikes;
	warnings = new string[strikes];
	copy(w,w+strikes,warnings);
	for(auto const phrase:f){
		filterList.append(phrase+", ");
	}
	filter = f;
}
string PircBot::getHost(){return host;}
string PircBot::getPort(){return port;}
string PircBot::getKey(){return key;}
string PircBot::getChannel(){return channel;}
string PircBot::getNick(){return nick;}
map<string,string> PircBot::getCommands(){
	return commands;
}
