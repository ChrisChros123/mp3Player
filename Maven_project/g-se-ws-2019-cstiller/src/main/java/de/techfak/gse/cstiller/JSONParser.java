package de.techfak.gse.cstiller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.techfak.gse.cstiller.exceptions.DeserializationException;
import de.techfak.gse.cstiller.exceptions.SerializationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to convert from and to JSON-Strings.
 */
public class JSONParser {
    private static final String SERIALIZATION_ERROR_MSG = "Serialisierung fehlgeschlagen";
    private static final String DESERIALIZATION_ERROR_MSG = "Deserialisierung fehlgeschlagen";
    private static final int MAX_PLAYLIST_SIZE = 100;
    private ObjectMapper objectMapper;
    public JSONParser() {
        objectMapper = new ObjectMapper()
            .findAndRegisterModules();
    }
    /**
     * Converts a song object to a JSON-String.
     * @param song song object to be converted to JSON.
     * @return the JSON string containing the song information.
     * @throws SerializationException thrown when serialisation fails.
     */
    public String songToJSON(Song song) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(song);
        } catch (JsonProcessingException e) {
            throw new SerializationException(SERIALIZATION_ERROR_MSG);
        }
    }

    /**
     * Converts a playlist to a JSON-String.
     * @param playlist is a map consisting of Song objects with Integer keys
     * @return the JSON string containing the playlist information
     * @throws SerializationException is thrown when serialisation fails.
     */
    public String playlistToJSON(Map<Integer, Song> playlist) throws SerializationException {
        if (playlist.size() > MAX_PLAYLIST_SIZE) {
            Map<Integer, Song> truncatedPlaylist = new HashMap<>();
            for (int i = 0; i < MAX_PLAYLIST_SIZE; i++) {
                truncatedPlaylist.put(i, playlist.get(i));
            }
            playlist = truncatedPlaylist;
        }
        try {
            return objectMapper.writeValueAsString(playlist);
        } catch (JsonProcessingException e) {
            throw new SerializationException(SERIALIZATION_ERROR_MSG);
        }
    }

    /**
     * Converts a JSON string into a Song object.
     * @param json the JSON String to be converted.
     * @return the Song object containing the information from the JSON.
     * @throws DeserializationException is thrown when deserialization fails.
     */
    public Song parseJSON(String json) throws DeserializationException {
        try {
            return objectMapper.readValue(json, Song.class);
        } catch (JsonProcessingException e) {
            throw new DeserializationException(DESERIALIZATION_ERROR_MSG);
        }
    }
    /**
     * Converts a JSON string into a playlist-map.
     * @param json the JSON String to be converted.
     * @return the Map object containing the song and key information from the JSON.
     * @throws DeserializationException is thrown when deserialization fails.
     */
    public HashMap<Integer, Song> parseJSONtoMap(String json) throws DeserializationException {
        try {
            HashMap<Integer, Song> res = objectMapper.readValue(json, new TypeReference<HashMap<Integer, Song>>() {});
            return res;
        } catch (JsonProcessingException e) {
            throw new DeserializationException(DESERIALIZATION_ERROR_MSG);
        }
    }
}
