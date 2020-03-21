package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.PathException;
import de.techfak.gse.cstiller.exceptions.ServerConnectionException;
import de.techfak.gse.cstiller.exceptions.StreamInitializationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * An MP3-Player application.
 */
public final class GSERadio {
    // TODO eventually. make enum classes from exit codes and error messages?
    private static final String COMMAND_LINE_MODE = "commandLineMode";
    private static final String SERVER_MODE = "serverMode";
    private static final int PATH_EX_EXIT_CODE = 100;
    private static final int SERVER_CONNECTION_EX_EXIT_CODE = 101;
    private static final int STREAM_INITIALIZATION_EX_EXIT_CODE = 102;
    private static final int CONTRADICTING_ARGUMENTS_EXIT_CODE = 103;
    private static final String PATH_EX_ERROR_MSG = "Error: couldn't find mp3 files.";
    private static final String SERVER_CONNECTION_EX_ERROR_MSG = "Error: connection to Server couldn't be established.";
    private static final String CONTRADICTING_ARGUMENTS_ERROR_MSG = "Error: these arguments exclude each other: ";
    private static final String STREAM_INITIALIZATION_EX_ERROR_MSG = "Error: stream could not be initialized.";
    private static final String GUI_S_ARG = "-g";
    private static final String GUI_L_ARG = "--gui";
    private static final String CLIENT_ARG = "--client";
    private static final String SERVER_ARG = "--server";
    private static final String COMMA = ", ";
    private static final String DEFAULT_PORT = "8080";
    private static final String CLIENT_MODE = "clientMode";
    private static final String GUI_MODE = "guiMode";

    private GSERadio() {
    }

    /**
     * Starts the Player in command line mode.
     *
     * @param directoryPath the path where to play MP3-files from
     * @throws PathException Invalid path: directory doesn't contain MP3's or pathname does not denote a directory
     * @throws IOException thrown during use of BufferedReader
     */
    private static void playInCommandLineMode(final String directoryPath, String mode, String port)
        throws  IOException, ServerConnectionException, StreamInitializationException {
        Player player;
            if (mode.equals(SERVER_MODE)) {
                player = new Player(directoryPath, mode, port);
                try {
                    WebServer webServer = new WebServer(port, player);
                } catch (NumberFormatException NFException) {
                    throw new ServerConnectionException(SERVER_CONNECTION_EX_ERROR_MSG);
                }
            } else {
                player = new Player(directoryPath, mode, null);
            }
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (player.isRunning()) {
            line = br.readLine();
            switch (line.toLowerCase()) {
                case "song":
                    player.printCurrentSong();
                    break;
                case "playlist":
                    player.printPlaylist();
                    break;
                case "exit":
                    player.exit();
                    break;
                default:
                    player.invalidCommand();
                    break;
            }
        }
    }
    private static void checkPath(final String dirPath) throws PathException {
        final File files = new File(dirPath);
        final String[] sortedFileArray = files.list();
        if (sortedFileArray == null) {
            throw new PathException(PATH_EX_ERROR_MSG);
        }
        if (sortedFileArray.length == 0) {
            throw new PathException(PATH_EX_ERROR_MSG);
        }
        final ArrayList<String> sortedFilteredFileArrayList = new ArrayList<>();
        for (final String fileName: sortedFileArray) {
            if (fileName.endsWith(".mp3")) {
                sortedFilteredFileArrayList.add(fileName);
            }
        }
        if (sortedFilteredFileArrayList.isEmpty()) {
            throw new PathException(PATH_EX_ERROR_MSG);
        }
    }
    /**
     * The main entry point of the program: sets up an Mp3-Player.
     *
     * @param args the path of the directory where to read the MP3s from as command line argument
     */
    public static void main(final String[] args) {
        int exitCode = 0;
        try {
            final File currentDir = new File("");
            final String currDir = currentDir.getAbsolutePath();
            if (args.length == 0) {
                checkPath(currDir);
                playInCommandLineMode(currDir, COMMAND_LINE_MODE, null);
            } else if (args[0].equals(GUI_S_ARG) || args[0].equals(GUI_L_ARG) || args[0].equals(CLIENT_ARG)) {
                //TODO decide if the transformation from string to string to the Mode-nomenclature makes sense.
                //TODO: evtl. separate GUI and client mode cases here
                if (args.length == 1) {
                    checkPath(currDir);
                    if (args[0].equals(GUI_S_ARG) || args[0].equals(GUI_L_ARG)) {
                        GSERadioGuiApplication.main(currDir, GUI_MODE);
                    } else {
                        GSERadioGuiApplication.main(currDir, CLIENT_MODE);
                    }
                } else {
                    checkPath(args[1]);
                    switch (args[0]) {
                        case CLIENT_ARG:
                            if (args[1].equals(GUI_S_ARG) || args[1].equals(GUI_L_ARG) || args[1].equals(SERVER_ARG)) {
                                exitCode = CONTRADICTING_ARGUMENTS_EXIT_CODE;
                                System.out.println(CONTRADICTING_ARGUMENTS_ERROR_MSG + args[0] + COMMA + args[1]);
                            } else {
                                GSERadioGuiApplication.main(args[1], CLIENT_MODE);
                            }
                            break;
                        default:
                            if (args[1].equals(CLIENT_ARG) || args[1].equals(SERVER_ARG)) {
                                exitCode = CONTRADICTING_ARGUMENTS_EXIT_CODE;
                                System.out.println(CONTRADICTING_ARGUMENTS_ERROR_MSG + args[0] + COMMA + args[1]);
                            } else {
                                GSERadioGuiApplication.main(args[1], GUI_MODE);
                            }
                    }
                }
            } else if (args[0].equals(SERVER_ARG)) {
                if (args.length == 1) {
                    checkPath(currDir);
                    playInCommandLineMode(currDir, COMMAND_LINE_MODE, DEFAULT_PORT);
                } else {
                    int portIdx = args[1].indexOf("=") + 1;
                    String subArg = args[1].substring(0, portIdx);
                    if (subArg.equals("--streaming=") || subArg.equals("--port=")) {
                        String port = args[1].substring(portIdx);
                        if (args.length == 2) {
                            checkPath(currDir);
                            playInCommandLineMode(currDir, SERVER_MODE, port);
                        } else {
                            checkPath(args[2]);
                            playInCommandLineMode(args[2], SERVER_MODE, port);
                        }
                    } else if (args[1].equals(CLIENT_ARG) || args[1].equals(GUI_S_ARG) || args[1].equals(GUI_L_ARG)) {
                        exitCode = CONTRADICTING_ARGUMENTS_EXIT_CODE;
                        System.out.println(CONTRADICTING_ARGUMENTS_ERROR_MSG + args[0] + COMMA + args[1]);
                    } else {
                        checkPath(args[1]);
                        playInCommandLineMode(args[1], SERVER_MODE, DEFAULT_PORT);
                    }
                }
            } else {
                checkPath(args[0]);
                playInCommandLineMode(args[0], COMMAND_LINE_MODE, null);
            }
        } catch (PathException pathException) {
            exitCode = PATH_EX_EXIT_CODE;
            System.out.println(PATH_EX_ERROR_MSG);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (StreamInitializationException SIException) {
            exitCode = STREAM_INITIALIZATION_EX_EXIT_CODE;
            System.out.println(STREAM_INITIALIZATION_EX_ERROR_MSG);
        } catch (ServerConnectionException SEException) {
            exitCode = SERVER_CONNECTION_EX_EXIT_CODE;
            System.out.println(SERVER_CONNECTION_EX_ERROR_MSG);
        }
        System.exit(exitCode);
    }
}
