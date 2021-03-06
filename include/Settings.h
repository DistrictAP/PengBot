/*
 * Settings.h
 * Last edited on 12/30/16
 * 	Contributors:
 * 		Nicholas Anderson
 */

#ifndef Settings_h
#define Settings_h

	//loads the settings from the given file into the bot.
	bool loadSettings(PircBot *bot,std::string file);
	//saves the settings from the given bot into the settings file
	bool saveSettings(PircBot bot,std::string file);
	//loads the list of commands into the given bot.
	bool loadCommands(PircBot *bot,std::string file);
	//loads the list of filter words into the given bot.
	bool loadFilter(PircBot *bot,std::string file);
#endif /* Settings_h */
