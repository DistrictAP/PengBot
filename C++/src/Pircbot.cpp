/*
 * PircBot.cpp
 *
 * Last edited on 1/01/17
 * 	Contributors:
 * 		Nicholas Anderson
 */
#include "Pircbot.h"
#include <iostream>
#include <fstream>
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

using namespace std;

#define MAXDATASIZE 100

//Retrieves the settings from the preformatted settigns file
//TODO Encript the settings file (due to passwords and whatnot)
//TODO allow path to use windows \\

bool PircBot::getSettings(){
	ifstream settings;
	settings.open("../Resources/Settings/Settings.txt");
	//couldn't open file switch to default settings
	if(!settings.is_open()){
		settings.open("../Resources/Settings/Defaults.txt");
		//couldn't open defaults
		if(!settings.is_open()){
			cout<<"Unable to open file.\n";
			return false;
		}
	}
	string line;
	//First line is hostname
	getline(settings,line);
	host = new char[line.substr(9).length()+1];
	strcpy(host,line.substr(9).c_str());
	host[line.substr(9).length()-1] = '\0';
	
	//Second line is the port
	getline(settings,line);
	port = new char[line.substr(5).length()+1];
	strcpy(port,line.substr(5).c_str());
	port[line.substr(5).length()-1] = '\0';

	//Third line is the key/Password
	getline(settings,line);
	key = new char[line.substr(4).length()+1];
	strcpy(key,line.substr(4).c_str());
	key[line.substr(4).length()-1] = '\0';

	//Fourth line is the channel
	getline(settings,line);
	channel = new char[line.substr(8).length()+1];
	strcpy(channel,line.substr(8).c_str());
	channel[line.substr(8).length()-1] = '\0';

  //Fifth line is the Nickname
	getline(settings,line);
	nick = new char[line.substr(9).length()+1];
	strcpy(nick,line.substr(9).c_str());
	nick[line.substr(9).length()-1] = '\0';

	settings.close();
	cout<<port<<'\n'<<channel<<'\n'<<endl;
	return true;
}

PircBot::PircBot(){
	getSettings();
}

PircBot::~PircBot(){
	close(s);
}

void PircBot::start(){
	cout<<"**Starting**\n"<<endl;
	struct addrinfo hints, *servinfo;

	//Setup run with no errors
	setup = true;

	//ensure that the servinfo is clear
	memset(&hints, 0, sizeof hints); //make sure struct is empty

	//setup hints
	hints.ai_family = AF_UNSPEC; // any address family is acceptable (IPv4/IPv6)
	hints.ai_socktype = SOCK_STREAM; //Using TCP 	sockets

	//Setup the structs if error print why
	int res;
	if((res = getaddrinfo(host,port,&hints,&servinfo)) !=0){
		setup = false;
		fprintf(stderr,"getaddrinfo: %s\n",gai_strerror(res));
	}
	
	//setup the socket
	if((s = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol)) == -1){
		perror("client socket");
	}

	//Connect
	if(connect(s,servinfo->ai_addr,servinfo->ai_addrlen) == -1){
		close(s);
		perror("Client Connect");
	}
	
	//we dont need the server info anymore
	freeaddrinfo(servinfo);

	//Recieve some data
	int numbytes;
	char buf[MAXDATASIZE];

	int count = 0;

	while (1){
		//declares
		count++;
		char msg[100];
		switch(count){	
			//send key and nickname to server
			case 1:
				//send key
				strcat(msg,"PASS ");
				strcat(msg,key);
				sendData(msg);
				//send nickname
				memset(&msg[0],0,sizeof(msg));//Clear array
				strcat(msg,"NICK ");
				strcat(msg,nick);
				sendData(msg);
			//after 3 recieves send data to server(IRS Protocal)
			case 3:
				sendData(nick);
				break;
			//Join a channel after connection this time w choose beaker
			case 4:
				memset(&msg[0],0,sizeof(msg));//Clear array
				strcat(msg,"JOIN ");
				strcat(msg,channel);
				sendData(msg);
			default:
				break;
		}
		//Recieve & print Data
		numbytes = recv(s,buf,MAXDATASIZE-1,0);
		cout<<count<<endl;
		buf[numbytes] ='\0';
		cout<<buf<<endl;
		//buf is the data that is recived
		//Pass buf to the message handler
		char ping[4] = {'P','I','N','G'};
		if(charSearch(buf,ping)){//if message is ping, respond with pong, or connection will close
			sendPong(buf);
		}else{
			msgHandle(buf);
		}
		if(numbytes==0){
			cout << "**Connection Closed**";
			cout << timeNow() << endl;
			break;
		}
	}
}

bool connect(){
	return true;
}

bool PircBot::charSearch(char* toSearch, char* searchTerm){
	int len = strlen(toSearch);
	int termLen = strlen(searchTerm); //the length of the query field
	//if the given string is shorter than the query, return false
	bool found = true;
	if(termLen>len){return 0;}
	for(int i=0;i<len;i++){
		//if the active char is equal to the first char in the term continue matching
		if(toSearch[i]==searchTerm[0]){
			//search the char array for the term
			for(int j=1; j< termLen;j++){
				if(toSearch[i+j]!=searchTerm[j]){
					found = false;
					break;
				}
			}
		}
	
	}
	return found;
}

//Returns true if "/MOTD" is found in the input string
//If /MOTD is present, it is okay to join a channel
bool PircBot::isConnected(char *buf){
	char motd[5] = {'/','M','O','T','D'};
	if(charSearch(buf,motd) == true)
		return true;
	else
		return false;
}

//Returns the current date and time
char * PircBot::timeNow(){
	time_t rawtime;
	struct tm * timeinfo;

	time(&rawtime);
	timeinfo = localtime(&rawtime);

	return asctime (timeinfo);
}

//send some data
bool PircBot::sendData(char *msg){
	int len = strlen(msg);
	int bytes_sent = send(s,msg,len,0);
	cout<<'<'<<msg<<endl;
	return !(bytes_sent==0);
}

void PircBot::sendPong(char *buf)
{
	//Get the reply address
	//loop through bug and find the location of PING
	//Search through each char in toSearch
	//
	char toSearch[5] = {'P','I','N','G',' '};

	for (int i = 0; i < strlen(buf);i++){
		//If the active char is equil to the first search item then search toSearch
		if(buf[i] == toSearch[0]){
			bool found = true;
			//search the char array for search field
			for (int x = 1; x < 4; x++){
				if (buf[i+x]!=toSearch[x]){
					found = false;
				}
			}

			//if found return true;
			if(found == true){
				int count = 0;
				//Count the chars
				for (int x = (i+strlen(toSearch)); x < strlen(buf);x++){
					count++;
				}

				//Create the new char array
				char returnHost[count + 5] = {'P','O','N','G',' '};
				count = 0;
				//set the hostname data
				for (int x = (i+strlen(toSearch)); x < strlen(buf);x++){
					returnHost[count+5]=buf[x];
					count++;
				}

				//send the pong
				if (sendData(returnHost)){
					cout << timeNow() <<"  Ping Pong" << endl;
				}
				return;
			}
		}
	}
}

void PircBot::msgHandle(char * buf){	
		cout<<"Message Recieved";
}