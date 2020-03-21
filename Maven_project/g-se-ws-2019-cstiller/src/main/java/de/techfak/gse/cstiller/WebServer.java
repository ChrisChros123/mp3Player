package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.SerializationException;
import de.techfak.gse.cstiller.exceptions.ServerConnectionException;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

/**
 * The web server class where the MP3-player can be used/hosted.
 */
public class WebServer extends NanoHTTPD {
    private static final String MIME_TYPE = "application/json";
    Player playerModel;
    JSONParser jsonParser;

    /**
     * Class constructor.
     * @param port port through which the server is accessed.
     * @param playerModel the MP3-player object executed on the server.
     * @throws ServerConnectionException is thrown when the server connection fails through the given port.
     */
    public WebServer(String port, Player playerModel) throws ServerConnectionException {
        // TODO: thows NFEX, IOEX, interEX
        // throws numberFormatException, but cannot be caught here, because super must be first statement
        super(Integer.parseInt(port));
        this.playerModel = playerModel;
        this.jsonParser = new JSONParser();
        try {
            start(SOCKET_READ_TIMEOUT, false);
        } catch (IOException ioException) {
            throw new ServerConnectionException("Connection to Server could not be established.");
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case "/current-song":
                case "/current-song/":
                    String songJson = jsonParser.songToJSON(playerModel.getCurrentSong());
                    return newFixedLengthResponse(Response.Status.OK, MIME_TYPE, songJson);
                case "/playlist":
                case "/playlist/":
                    String playlistJson = jsonParser.playlistToJSON(playerModel.getPlaylist());
                    this.jsonParser = new JSONParser();
                    return newFixedLengthResponse(Response.Status.OK, MIME_TYPE, playlistJson);
                case "":
                case "/":
                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "GSE Radio");
                default:
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Path not found");
            }
        } catch (SerializationException e) {
            System.out.println("Serialization failed");
            String errorMsg = "Serialization error.";
            return newFixedLengthResponse(Response.Status.UNSUPPORTED_MEDIA_TYPE, MIME_PLAINTEXT, errorMsg);

        }
    }

}

