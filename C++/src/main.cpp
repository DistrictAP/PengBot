#include <iostream>
#include "Pircbot.h"
#include "Settings.h"

using namespace std;


int main(){
	PircBot bot = PircBot();
	loadSettings(&bot,"Resources/Settings/Settings.txt");
	bot.start();

  return 0;
}
