/*
 * Settings.h
 * Last edited on 12/30/16
 * 	Contributors:
 * 		Nicholas Anderson
 */

#ifndef Settings_h
#define Settings_h

	//loads the settings from the given file into the bot.
	bool loadSettings(PircBot *bot,char const *file);
	//saves the settings from the given bot into the settings file
	bool saveSettings(PircBot bot,char const *file);
	//loads the list of commands into the bot
	bool loadCommands(PircBot *bot,char const *file);
#endif /* Settings_h */
