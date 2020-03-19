# mp3Player (Java)

## Introduction

This program was written in the frame of a Software Engineering university course and had the aim to apply basic 
software engineering principles. 

It is an mp3-Player which has three application modes:\
a) command line mode, with some basic commands to get playlist information\
b) access via a simple user interface to interact with the playlist\
c) make the playlist accessible on a server for other users \
The 

## Usage

### a) Command line mode
The command line mode is the default and does not require any additional command line argument. 
(e.g: ./gseRadio /home/cs/g-se-ws-2019-cstiller/src/test/resources )
The following commands can be applied in this mode: 
- "song" to get the title and artist of the currently playing song
- "playlist" to get the playlist
- "exit" to stop the program.

### b) GUI mode
To access the application via a user interface, one of the command line arguments "--gui" or "-g" must applied.
(e.g: ./gseRadio --gui /home/cs/g-se-ws-2019-cstiller/src/test/resources ) 
The gui shows the currently playing playlist. The user can control the order of the playlist according to 
her/his taste using the vote button.


### c) Server mode
The server mode can be entered by using "--server" as first command line argument. Additionally, the port by which
the stream should be accessible can be indicated in using a second argument "--streaming=<port>" where <port> must be 
replaced by the port number. 
(e.g: ./gseRadio --server --streaming=9000 /home/cs/g-se-ws-2019-cstiller/src/test/resources )

### general
The application can take as last command line argument the path to repository where to read mp3-files from. 
If it is started without this arguments, it attempts to load mp3-files from the directory,it is started in.






