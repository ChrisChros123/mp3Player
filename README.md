# mp3Player (Java)

## Introduction

This program was written in the frame of a Software Engineering university course and had the aim to apply basic 
software engineering principles. 

It is an mp3-Player which has three application modes:\
a) the command line mode, with some basic commands to get playlist information\
b) the access via a simple user interface to interact with the playlist\
c) making the playlist accessible on a server for other users \
The vlcj4 library was used.

The code can be found in the Maven_project.zip file.

## Usage

The executables can be found in the RadioApp.zip folder.

### general
The application can take as a last command line argument the path to the repository where to read mp3-files from. 
If it is started without this argument, it attempts to load mp3-files from the directory,it is started in.

### a) Command line mode
The command line mode is the default and does not require any additional command line argument. \
(e.g: ./gseRadio /home/cs/g-se-ws-2019-cstiller/src/test/resources )\
The following commands can be applied in this mode: 
- "song" to get the title and artist of the currently playing song
- "playlist" to get the playlist
- "exit" to stop the program.

### b) GUI mode
To access the application via a user interface, one of the command line arguments "--gui" or "-g" must applied.\
(e.g: ./gseRadio --gui /home/cs/g-se-ws-2019-cstiller/src/test/resources ) \
The gui shows the currently playing playlist. The user can control the order of the playlist according to 
her/his taste using the vote button.


### c) Server mode
The server mode can be entered by using "--server" as first command line argument. Additionally, the port by which
the stream should be accessible can be indicated in using a second argument "--streaming=<port>" where <port> must be 
replaced by the port number. \
(e.g: ./gseRadio --server --streaming=9000 /home/cs/g-se-ws-2019-cstiller/src/test/resources )\
If no port is put in, port 8080 is used by default. 
The server can be accessed via the URL "http://localhost:<PORT>/" and by appending it with "current-song" or "paylist",
the respective information will be displayed in the browser. 







