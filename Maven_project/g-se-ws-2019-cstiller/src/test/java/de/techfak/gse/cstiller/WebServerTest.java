package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.ServerConnectionException;
import de.techfak.gse.cstiller.exceptions.StreamInitializationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.fail;

class WebServerTest {
    private static final String INTERRUPTED_EX_MSG = "Interrupted Exception.";
    private static final String IO_EX_MSG = "IO exception.";
    private static final String STREAM_INIT_EX_MSG = "StreamInitialization exception.";
    private static final String SERVER_CON_EX_MSG = "ServerConnection exception";
    String musicPath;

    public  WebServerTest() {
        final File currentDir = new File("");
        final String currDir = currentDir.getAbsolutePath();
        StringBuilder musicPathBuilder = new StringBuilder(currDir);
        musicPathBuilder.append("/src/test/resources");
        this.musicPath = musicPathBuilder.toString();
    }
    /**
     * Test 2 for User Story 13, acceptance criterion 1. To detect, if the server does not respond with status code 200
     * in the standard case, the equivalence class of a valid URL to the currently laying song was chosen represented by
     * http://localhost:8081/current-song".
     */
    @Test
    void currSongStandardURLTest () {
        try {
            String port = "8081";
            Player player = new Player(musicPath,"serverMode", port);
            WebServer webServer = new WebServer(port, player);
            HttpClient client = HttpClient.newHttpClient();
            String currentSongURL = "http://localhost:8081/current-song";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(currentSongURL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException IOEx) {
            fail(IO_EX_MSG);
        } catch (InterruptedException interEx) {
            fail(INTERRUPTED_EX_MSG);
        } catch (StreamInitializationException streamInitEx) {
            fail(STREAM_INIT_EX_MSG);
        } catch (ServerConnectionException serverConEx) {
            fail(SERVER_CON_EX_MSG);
        }
    }


    /**
     * Test 2 for User Story 15, acceptance criterion 1. To detect, if the server does not respond with status code 200
     * in the standard case, the equivalence class of a valid URL to the current playlist was chosen represented by
     * "http://localhost:8082/playlist".
     */
    @Test
    void currPlaylistStandardURLTest () {
        try {
            String port = "8082";
            Player player = new Player(musicPath,"serverMode", port);
            WebServer webServer = new WebServer(port, player);
            HttpClient client = HttpClient.newHttpClient();
            String playlistURL = "http://localhost:8082/playlist";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(playlistURL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException IOEx) {
            fail(IO_EX_MSG);
        } catch (InterruptedException interEx) {
            fail(INTERRUPTED_EX_MSG);
        } catch (StreamInitializationException streamInitEx) {
            fail(STREAM_INIT_EX_MSG);
        } catch (ServerConnectionException serverConEx) {
            fail(SERVER_CON_EX_MSG);
        }
    }

    /**
     * Additional test for User story 13 acceptance criterion 1.
     * To detect if an invalid URL leads to a NOT_FOUND status code response, the equivalence class of invalid URLs
     * was chosen, represented by "http://localhost:8080/invalid".
     */
    @Test
    void invalidURLTest () {
        try {
            String port = "8083";
            Player player = new Player(musicPath,"serverMode", port);
            WebServer webServer = new WebServer(port, player);
            HttpClient client = HttpClient.newHttpClient();
            String currentSongURL = "http://localhost:8083/invalid";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(currentSongURL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertThat(response.statusCode()).isEqualTo(404);
        } catch (IOException IOEx) {
            fail(IO_EX_MSG);
        } catch (InterruptedException interEx) {
            fail(INTERRUPTED_EX_MSG);
        } catch (StreamInitializationException streamInitEx) {
            fail(STREAM_INIT_EX_MSG);
        } catch (ServerConnectionException serverConEx) {
            fail(SERVER_CON_EX_MSG);
        }
    }
}
