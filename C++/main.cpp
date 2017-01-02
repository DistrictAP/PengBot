#include <iostream>
#include "Pircbot.h"


using namespace std;


int main()
{
	PircBot bot = PircBot("NICK Justalilbitnerdy\r\n","USER guest tolmoon tolsun :Ronnie Reagan\r\n");
	bot.start();

  return 0;

}
