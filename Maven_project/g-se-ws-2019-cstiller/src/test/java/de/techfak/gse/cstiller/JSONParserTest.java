package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.DeserializationException;
import de.techfak.gse.cstiller.exceptions.SerializationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

class JSONParserTest {
    private static final String SERIALIZATION_EX_MSG = "Serialization exception.";
    private static final String DESERIALIZATION_EX_MSG = "Deserialization exception.";
    private static final String song1JSON = "{\"filename\":\"filename\",\"title\":\"title\",\"artist\":\"artist\",\"album\":\"album\",\"genre\":\"genre\",\"duration\":2,\"noOfVotes\":0,\"songID\":1}";
    private static final String song2JSON = "{\"filename\":\"filename2\",\"title\":\"title2\",\"artist\":\"artist2\",\"album\":\"album2\",\"genre\":\"genre2\",\"duration\":2,\"noOfVotes\":0,\"songID\":2}}";
    private static final String shortPlaylistJSON = "{\"1\":{\"filename\":\"filename\",\"title\":\"title\",\"artist\":\"artist\",\"album\":\"album\",\"genre\":\"genre\",\"duration\":2,\"noOfVotes\":0,\"songID\":1},\"2\":{\"filename\":\"filename2\",\"title\":\"title2\",\"artist\":\"artist2\",\"album\":\"album2\",\"genre\":\"genre2\",\"duration\":2,\"noOfVotes\":0,\"songID\":2}}";

    JSONParser jsonParser;
    Song testSong1;
    Song testSong2;

    public JSONParserTest() {
        jsonParser = new JSONParser();
        testSong1 =  new Song("filename", "title", "artist", "album", "genre", 2, 1);
        testSong2 = new Song("filename2", "title2", "artist2", "album2", "genre2", 2, 2);


    }
    /**
     * Test 1 for User Story 13 acceptance criterion 1. To detect the cases when the serialization of a song object doesn't result in
     * a regular JSON object or the JSON-object doesn't contain the required song information, the equivalence
     * class of a regular song object as input was chosen represented by testSong1.
     */
    @Test
    void songToJSONTest() {
        try {
            String result = jsonParser.songToJSON(testSong1);
            Assertions.assertThat(result).isEqualTo(song1JSON);
        } catch (SerializationException serEx) {
            fail(SERIALIZATION_EX_MSG);
        }

    }

    /**
     * Test 1 for User Story 15, acceptance criterion 2. To detect if the JSON of a  playlist of
     * length > 100 contains information of more than 100 Song, the equivalence class of playlists
     * of more than 100 song was chosen represented by the boundary case, a list of 101 elements.
     */
    @Test
    void tooLongPlaylistToJSONTest() {
        try {
            Map<Integer, Song> longPlaylist = new HashMap<>();
            for (int i = 0; i < 101; i++){
                longPlaylist.put(i, testSong1);
            }
            String json = jsonParser.playlistToJSON(longPlaylist);
            Assertions.assertThat(json).doesNotContain("100");
            HashMap<Integer, Song> playlist = jsonParser.parseJSONtoMap(json);
            Assertions.assertThat(playlist.size()).isEqualTo(100);
        } catch (SerializationException serEx) {
            fail(SERIALIZATION_EX_MSG);
        } catch (DeserializationException desEx) {
            fail(DESERIALIZATION_EX_MSG);
        }

    }

    /**
     * Test 2 for the User Story 15, acceptance criterion 2. To detect if not the full playlist is serialized
     * into JSON in the maximal length playlist case, the equivalence class of the boundary case was chosen
     * represented by a playlist of 100 songs.
     */
    @Test
    void boundaryLengthPlaylistToJSONTest () {
        try {
            Map<Integer, Song> longPlaylist = new HashMap<>();
            for (int i = 0; i < 100; i++){
                longPlaylist.put(i, testSong1);
            }
            String json = jsonParser.playlistToJSON(longPlaylist);
            Assertions.assertThat(json).contains("99");
            HashMap<Integer, Song> playlist = jsonParser.parseJSONtoMap(json);
            Assertions.assertThat(playlist.size()).isEqualTo(100);
        } catch (SerializationException serEx) {
            fail(SERIALIZATION_EX_MSG);
        } catch (DeserializationException desEx) {
            fail(DESERIALIZATION_EX_MSG);
        }

    }

    /**
     * Test 1 for User Story 15 acceptance criterion 1.
     * To detect any error in transforming a playlist into JSON,
     * the equivalence class of standard case playlists was chosen represented by shortPlaylist comprising
     * two songs.
     */
    @Test
    void shortPlaylistToJSONTest (){
        try {
            Map<Integer, Song> shortPlaylist = new HashMap<>();
            shortPlaylist.put(1, testSong1);
            shortPlaylist.put(2, testSong2);
            String result = jsonParser.playlistToJSON(shortPlaylist);
            Assertions.assertThat(result).isEqualTo(shortPlaylistJSON);
        } catch (SerializationException serEx) {
            fail(SERIALIZATION_EX_MSG);
        }

    }

    /**
     * Test 3 for User Story 15, acceptance criterion 2. To verify that it is really the first 100 songs
     * that are displayed, a list of length 101 with the last element uniquely identifiable was chosen.
     */
    @Test
    void lastSongOmitTest () {
        try {
            Map<Integer, Song> longPlaylist = new HashMap<>();
            for (int i = 0; i < 100; i++){
                longPlaylist.put(i, testSong1);
            }
            longPlaylist.put(100, testSong2);
            String result = jsonParser.playlistToJSON(longPlaylist);
            Assertions.assertThat(result).doesNotContain(song2JSON);
        } catch (SerializationException serEx) {
            fail(SERIALIZATION_EX_MSG);
        }

    }

    /**
     * Test 3 for the User story 15 acceptance criterion 1. To detect an issue with the deserialization of the JSON
     * to a playlist, the equivalence class of a JSON of a standard playlist was chosen represented by a JSON
     * of a playlist of two songs.
     */
    @Test
    void playlistDeserializationTest () {
        try {
            HashMap<Integer, Song> playlist = jsonParser.parseJSONtoMap(shortPlaylistJSON);
            Song song1 = playlist.get(1);
            Song song2 = playlist.get(2);
            Assertions.assertThat(song1.isEqualTo(testSong1)).isTrue();
            Assertions.assertThat(song2.isEqualTo(testSong2)).isTrue();
        } catch (DeserializationException desEx) {
            fail(DESERIALIZATION_EX_MSG);
        }
    }

    /**
     * Test 3 for the User story 13 acceptance criterion 1. To detect an issue with the deserialization of the JSON
     * to a song, the equivalence class of a JSON of a standard song was chosen represented by a JSON
     * of a song containing valid entries for all attributes.
     */
    @Test
    void songDeserializationTest () {
        try {
            Song song = jsonParser.parseJSON(song1JSON);
            Assertions.assertThat(song.isEqualTo(testSong1)).isTrue();
        } catch (DeserializationException desEx) {
            fail(DESERIALIZATION_EX_MSG);
        }

    }
}
