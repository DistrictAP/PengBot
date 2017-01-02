/*
 * PircBot.cpp
 *
 * Last edited on 1/01/17
 * 	Contributors:
 * 		Nicholas Anderson
 */
#include "Pircbot.h"
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <time.h>

using namespace std;

#define MAXDATASIZE 100

PircBot::PircBot(char* _nick,char* _usr){
	nick = _nick;
	usr = _usr;
}

PircBot::~PircBot(){
	close(s);
}

void PircBot::start(){
	struct addrinfo hints, *servinfo;

	//Setup run with no errors
	setup = true;
	port = "6667";

	//ensure that the servinfo is clear
	memset(&hints, 0, sizeof hints); //make sure struct is empty

	//setup hints
	hints.ai_family = AF_UNSPEC; //don't care IPv4 or IPv6
	hints.ai_socktype = SOCK_STREAM; //TCP stream sockets

	//Setup the structs if error print why
	int res;
	if((res = getaddrinfo("irc.ubuntu.com",port,&hints,&servinfo)) !=0){
		setup = false;
		fprintf(stderr,"getaddrinfo: %s\n",gai_strerror(res));
	}
	
	//setup the socket
	if((s = socket(servinfo->ai_family,servinfo->ai_socktype,servinfo->ai_protocol)) == -1){
		perror("client Connect");
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
		//declars
		//count++;
		
		switch(count){
			//after 3 recieves send data to server(IRS Protocal)
			case 3:
				sendData(nick);
				sendData(usr);
				break;
			//Join a channel after connection this time w choose beaker
			case 4:
				sendData("JOIN #ubuntu\r\n");
			default:
				break;
		}

		//Recieve & print Data
		numbytes = recv(s,buf,MAXDATASIZE-1,0);
		buf[numbytes] ='\0';
		cout << buf;
		//buf is the data that is recived
		//Pass buf to the message handler
		if(charSearch(buf,"PING")){//if message is ping, respond with pong, or connection will close
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

bool PircBot::charSearch(char *toSearch, char *searchTerm){
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
	if(charSearch(buf,"/MOTD") == true)
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

	return !(bytes_sent==0);
}

void PircBot::sendPong(char *buf)
{
	//Get the reply address
	//loop through bug and find the location of PING
	//Search through each char in toSearch

	char * toSearch = "PING ";

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
	if(charSearch(buf,"hi scooby")){
		sendData("PRIVMSG #ubuntu :hi, hows it going\r\n");
	}
}



