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



PircBot::PircBot(){
	host = (char*) malloc (sizeof(char)*100);
	port = (char*) malloc (sizeof(char)*10);
	key = (char*) malloc (sizeof(char)*100);
	channel = (char*) malloc (sizeof(char)*25);
	nick = (char*) malloc (sizeof(char)*25);

}

//free all memory and close socket
PircBot::~PircBot(){
	free(host);
	free(port);
	free(key);
	free(channel);
	free(nick);
	close(s);
}

void PircBot::start(){
	cout<<"**Starting**\n"<<endl;
	bool connected = connectToHost();
	int numbytes;
	char buf[MAXDATASIZE];
	int msgCount = 0;
	bool joined = false;
	//loop to recieve messages
	while (connected){
		msgCount++;
		//declarations
		char msg[100] = {};	
		switch(msgCount){
			case 1:
				//send key and nickname to server
				//send key
				strcat(msg,"PASS ");
				strcat(msg,key);
				strcat(msg,"\r\n");
				sendData(msg);
				//send nickname
				memset(&msg[0],0,sizeof(msg));//Clear array
				strcat(msg,"NICK ");
				strcat(msg,nick);
				strcat(msg,"\r\n");
				sendData(msg);
				break;
							break;
			default:
				break;
		}
		//Recieve & print Data
		numbytes = recv(s,buf,MAXDATASIZE-1,0);
		buf[numbytes] ='\0';
		cout<<buf;
		
		//after twitch says hello, join a channel
		char c[2] = {'>',0};
		if(charSearch(buf,c)&&!joined){
			//send join request
			memset(&msg[0],0,sizeof(msg));//Clear array
			strcat(msg,"JOIN ");
			strcat(msg,channel);
			strcat(msg,"\r\n");
			sendData(msg);
			joined = true;
		}
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
			connected = false;
		}
	}
}

bool PircBot::connectToHost(){
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
	cout<<host<<endl<<port<<endl;
	if((res = getaddrinfo(host,port,&hints,&servinfo)) !=0){
		setup = false;
		fprintf(stderr,"getaddrinfo: %s\n",gai_strerror(res));
	}
	
	//setup the socket
	if((s = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol)) == -1){
		perror("client socket");
	}

	//Connect
	cout<<"Connecting to \""<<host<<"\""<<endl;
	if(connect(s,servinfo->ai_addr,servinfo->ai_addrlen) == -1){
		close(s);
		perror("Client Connect");
		return false;
	}	
	//we dont need the server info anymore
	freeaddrinfo(servinfo);
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
	printf("%.*s\n",len-2,msg);
	return !(bytes_sent==0);
}

void PircBot::sendPong(char *buf)
{
	//Get the reply address
	//loop through bug and find the location of PING
	//Search through each char in toSearch
	//
	char toSearch[5] = {'P','I','N','G',' '};

	for (unsigned int i = 0; i < strlen(buf);i++){
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
				for (unsigned int x = (i+strlen(toSearch)); x < strlen(buf);x++){
					count++;
				}

				//Create the new char array
				char returnHost[count + 5] = {'P','O','N','G',' '};
				count = 0;
				//set the hostname data
				for (unsigned int x = (i+strlen(toSearch)); x < strlen(buf);x++){
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

//getters and setters
void PircBot::setHost(string h){
	strcpy(host,h.c_str());
	host[h.length()-1] = 0;
}
void PircBot::setPort(string p){
	strcpy(port,p.c_str());
	port[p.length()-1] = 0;
}
void PircBot::setKey(string k){
	strcpy(key,k.c_str());
	key[k.length()-1] = 0;
}
void PircBot::setChannel(string c){
	strcpy(channel,c.c_str());
	channel[c.length()-1] = 0;
}
void PircBot::setNick(string n){
	strcpy(nick,n.c_str());
	nick[n.length()-1] = 0;
}
	
char* PircBot::getHost(){return host;}
char* PircBot::getPort(){return port;}
char* PircBot::getKey(){return key;}
char* PircBot::getChannel(){return channel;}
char* PircBot::getNick(){return nick;}
