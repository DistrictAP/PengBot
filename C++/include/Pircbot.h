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
	void setFilter(int strikes,std::string *w,std::set<std::string> f);

	std::string getHost();
	std::string getPort();
	std::string getKey();
	std::string getChannel();
	std::string getNick();
	std::map<std::string,std::string> getCommands();
private:
	int s; //the socket descriptor
	int maxStrikes;//max amount of strikes before a ban
	std::string host;//host to connect to
	std::string port;//port to connect to
	std::string channel;//channel to join
	std::string key;//password/Oauth code to send at the start
	std::string nick;//nickname to send at the start
	std::string filterList;//string which has every filtered word seperated by
	std::string *warnings;//array holding the warning messages to be sent if a chatter breaks the filter rule
	std::map<std::string,std::string> commands;//maps !comands with replys
	std::set<User> users;//set containing all users which are currently listening
	std::set<std::string> filter;//set containing all the filtered words

	int onStrike(std::string id);
	bool connectToHost();
	bool isConnected(char *buf);
	std::string timeNow();
	bool sendData(std::string msg);
	void onPing(const char *buf);
	void onMessage(Message msg);
	void onJoin(std::string usr);
	void onPart(std::string usr);
	std::string formatMessage(std::string reply,Message msg);
	bool filterMessage(std::string msg);
};
#endif /* PircBot_h */
