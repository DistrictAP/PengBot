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
	PircBot();
	virtual ~PircBot();

	bool setup;
	void start();
	bool charSearch(char* toSearch, char* searchFor);
private:
	char* host;
	char* port;
	char* key;
	char* channel;
	char* nick;
	int s; //the socket descriptor
	
	bool getSettings();
	bool isConnected(char *buf);
	char * timeNow();
	bool sendData(char *msg);
	void sendPong(char *buf);
	void msgHandle(char *buf);
};
#endif /* PircBot_h */

