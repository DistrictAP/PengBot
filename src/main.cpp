#include <iostream>
#include "Pircbot.h"
#include "Settings.h"

using namespace std;


int main(){
	PircBot bot = PircBot();
	if(!loadSettings(&bot,"Resources/Settings/Settings.txt")){
		cout<< "unable to load settings, quitting now"<<endl;
		return 1;
	}
	loadCommands(&bot,"Resources/Settings/Commands.txt");
	loadFilter(&bot,"Resources/Settings/Filter.txt");
	bot.start();

  return 0;
}
