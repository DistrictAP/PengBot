/*
 * PircBot.h
 * Last edited on 1/17/17
 * 	Contributors:
 * 		Nicholas Anderson
 */

#ifndef PircBot_h
#define PircBot_h

#include <string>
#include <map>
#include <set>

class PircBot{
	
struct Message{
	std::string user;
	std::string type;
	std::string channel;
	std::string message;
};

public:
	bool setup;
	void start();
	//getters and setters
	void setHost(std::string h);
	void setPort(std::string p);
	void setKey(std::string k);
	void setChannel(std::string c);
	void setNick(std::string n);
	void setCommands(std::map<std::string,std::string> c);

	std::string getHost();
	std::string getPort();
	std::string getKey();
	std::string getChannel();
	std::string getNick();
	std::map<std::string,std::string> getCommands();
private:
	std::string host;
	std::string port;
	std::string key;
	std::string channel;
	std::string nick;
	int s; //the socket descriptor
	std::map<std::string,std::string> commands;
	std::set<std::string> users;

	bool connectToHost();
	bool isConnected(char *buf);
	char * timeNow();
	bool sendData(std::string msg);
	void onPing(const char *buf);
	void onMessage(Message msg);
	void onJoin(std::string usr);
	void onPart(std::string usr);
	std::string formatReply(std::string reply,Message msg);
};
#endif /* PircBot_h */
