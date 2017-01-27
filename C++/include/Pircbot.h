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

	struct User{
		std::string id;
		int points;
		int strikes;
		bool operator<(const User &usr) const{
			return id < usr.id;
		}
		bool operator==(const User &usr) const{
			return id.compare(usr.id)==0;
		}
	};
public:
	void start();
	//getters and setters
	void setHost(std::string h);
	void setPort(std::string p);
	void setKey(std::string k);
	void setChannel(std::string c);
	void setNick(std::string n);
	void setCommands(std::map<std::string,std::string> c);
	void setFilter(std::set<std::string> f);

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
	static std::set<User> users;
	std::set<std::string> filter;

	bool connectToHost();
	bool isConnected(char *buf);
	std::string timeNow();
	bool sendData(std::string msg);
	void onPing(const char *buf);
	void onMessage(Message msg);
	void onJoin(std::string usr);
	void onPart(std::string usr);
	std::string formatReply(std::string reply,Message msg);
	bool filterMessage(std::string msg);
	void givePoints();
};
#endif /* PircBot_h */
