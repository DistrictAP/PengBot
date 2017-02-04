main:
	mkdir -p bin
	g++ -pthread -o bin/Pircbot -I include src/main.cpp src/Pircbot.cpp src/Settings.cpp
debug:
	g++ -pthread -g -Wall -o bin/Pircbot -I include src/main.cpp src/Pircbot.cpp src/Settings.cpp
clean:
	rm -f bin/Pircbot
