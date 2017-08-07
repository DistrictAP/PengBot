#include "Pircbot.h"
#include "Settings.h"
#include <thread>
#include <iostream>
#include <string>
#include <algorithm>

void getInput(PircBot *bot);
bool handleCMD(std::string cmd,PircBot *bot);//returns true if quit was given
bool findInString(std::string msg,std::string query);
void makePoll(std::string info,PircBot *bot);
void printHelp();

int main(int argc, char* argv[]){
	if(argc!=2){
		std::cout<<"Usage: "<<argv[0]<<" [settings directory]\n";
		return 0;
	}
	std::string dir = argv[1];
	PircBot bot = PircBot();
	if(!loadSettings(&bot,dir+"/Settings.txt")){
		std::cout<< "unable to load settings. Incorrect settings folder?\n";
		return 1;
	}
	loadCommands(&bot,dir+"/Commands.txt");
	loadFilter(&bot,dir+"/Filter.txt");
	std::thread input (getInput,&bot);
	bot.start();

	return 0;
}

void getInput(PircBot *bot){
	std::cout<<"For a list of commands type \"help\"\n";
	std::string cmd;
	bool quit = false;
	while(!quit){
		std::cout<<'>';
		std::getline(std::cin,cmd);
		quit = handleCMD(cmd,bot);
	}
	bot->stop();
}

bool handleCMD(std::string cmd,PircBot *bot){
	if(findInString(cmd,"say")){
		std::string message = "PRIVMSG " + bot->getChannel() + " :"
			+cmd.substr(3)+"\r\n";
		if(bot->sendData(message)){
			std::cout<<"message sent\n";
		}else{
			std::cout<<"Message unable to be sent\n";
		}
	}else if(findInString(cmd,"help")){
		printHelp();
	}else if(findInString(cmd,"poll ")){
		cmd = cmd.substr(5);
		makePoll(cmd,bot);
	}else if(findInString(cmd,"endPoll")){
		std::pair<std::string,int> winner = bot->closePoll();
		std::cout<<"poll has completed, the winner is "<<winner.first<<" with "<<winner.second<<" votes"<<std::endl;
	}else if(cmd.compare("quit")==0){
		return true;
	}else{
		std::cout<<"Command not recognized: " + cmd + "\n"+
			"If you need a list of available commands use the command help\n";
	}
	return false;
}

void makePoll(std::string info,PircBot *bot){
	PircBot::Poll poll;
	int pos = info.find("\"",1);
	poll.query = info.substr(1,pos-2);
	while(info.find("\"",1)!=std::string::npos){
		pos = info.find("\"",1);
		info = info.substr(pos+2);
		pos = info.find("\"",1);
		poll.options.insert(std::pair<std::string,int>
				(info.substr(1,pos-1),0));
		if(pos>=info.size()-1){break;}
	}
	if(bot->createPoll(poll)){//poll was successfully created
		std::cout<<"Poll successfully created.\n";
	}else{
		std::cout<<"There is currently a poll open please use endPoll to end the current poll\n";
	}
}

bool findInString(std::string msg,std::string phrase){//returns true if the message contains a phrase in the filter set.
	//make message lowercase
	auto it = search(
			msg.begin(),msg.end(),
			phrase.begin(),phrase.end(),
		[](char ch1, char ch2) {return std::toupper(ch1) == toupper(ch2);}
	);
	return it != msg.end();
}

void printHelp(){
	std::cout<<"*****HELP*****\n"<<
		"say \"message\"\n"<<
		"  Sends the given message to whatever channel the bot is connected to.\n"<<
		"poll \"Question?\" option1 option2 option 3 ...\n"<<
		"  Creates a poll for the users to vote on\n"<<
		"endPoll\n"<<
		"  ends the poll and both sends the results to the chat and prints it out."<<
		"quit\n"<<
		"  Closes the connections and quits\n";
}
