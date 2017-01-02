/*
 * PircBot.h
 * Last edited on 12/30/16
 * 	Contributors:
 * 		Nicholas Anderson
 */
#ifndef PircBot_h
#define PircBot_h
class PircBot{

public:
	PircBot(char* _nick,char* _usr);
	virtual ~PircBot();

	bool setup;
	void start();
	bool charSearch(char *toSearch, char *searchFor);
private:
	char const *port;
	int s; //the socket descriptor

	char *nick;
	char *usr;

	bool isConnected(char *buf);
	char * timeNow();
	bool sendData(char *msg);
	void sendPong(char *buf);
	void msgHandle(char *buf);
};
#endif /* PircBot_h */

