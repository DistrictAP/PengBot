/*
 * PircBot.h
 * Last edited on 12/30/16
 * 	Contributors:
 * 		Nicholas Anderson
 */

#ifndef PircBot_h
#define PircBot_h
#include <string>

class PircBot{

public:
	PircBot();
	virtual ~PircBot();

	bool setup;
	void start();
	bool charSearch(char* toSearch, char* searchFor);
	//getters and setters
	void setHost(std::string h);
	void setPort(std::string p);
	void setKey(std::string k);
	void setChannel(std::string c);
	void setNick(std::string n);
	char* getHost();
	char* getPort();
	char* getKey();
	char* getChannel();
	char* getNick();

private:
	char* host;
	char* port;
	char* key;
	char* channel;
	char* nick;
	int s; //the socket descriptor

	bool connectToHost();
	bool isConnected(char *buf);
	char * timeNow();
	bool sendData(char *msg);
	void sendPong(char *buf);
	void msgHandle(char *buf);
};
#endif /* PircBot_h */

