package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.StreamInitializationException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.InfoApi;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.Meta;
import uk.co.caprica.vlcj.media.MetaData;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.waiter.media.ParsedWaiter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An MP3-Player class.
 */
public class Player {
    private static final String EMPTY = "  ";
    private static final String[] OUTPUT_TEXTS = {"Titel:  ", "Interpret:  ", "Genre:  ", "Laenge:  ", "Album:  "};
    private static final String ADDRESS_OF_STREAM = "rtp://localhost:8080";
    private static final String SERVER_MODE = "serverMode";
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 49151;
    private MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;
    private Map<Integer, Song> playlist;
    private String dirPath;
    private boolean running;
    private String mode;
    private PropertyChangeSupport support;
    private String mediaOptions;

    /**
     * Class constructor.
     * @param dirPath the directory path where the Player reads MP3s from.
     * @param mode gui-, commandLine-, client- or serverMode, in which the Player is used.
     * @param port port through which the server is accessed.
     * @throws StreamInitializationException thrown when the port is an invalid number.
     */
    public Player(final String dirPath, final String mode, String port) throws StreamInitializationException {
        //TODO eventually make an enum class for the modes
        this.mode = mode;
        this.running = true;
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
        this.dirPath = dirPath;
        this.support = new PropertyChangeSupport(this);
        if (mode.equals(SERVER_MODE)) {
            // TODO: eventually test it before (where is it an int?)
            // TODO: try to realize #9004 with StatusApi of VLCJ
            int portInt = Integer.parseInt(port);
            if (portInt < MIN_PORT || portInt > MAX_PORT) {
                throw new StreamInitializationException("Error: Stream could not be initialized.");
            }
            StringBuilder mediaOptionsBuilder = new StringBuilder(":sout=#rtp{dst=localhost,port=");
            mediaOptionsBuilder.append(port);
            mediaOptionsBuilder.append(",mux=ts}");
            this.mediaOptions = mediaOptionsBuilder.toString();
        }
        this.generatePlaylist();
        this.play();
    }
    public Player() { }

    /**
     * Add an observer to the observer list of the Player model (observable).
     * @param observer the added observer
     */
    public void addPropertyChangeListener(final PropertyChangeListener observer) {
        this.support.addPropertyChangeListener(observer);
    }

    /**
     * Ask if MP3-Player is running.
     * @return true if the Player is running
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Get the current playlist of the MP3-player.
     * @return the playlist attribute
     */
    public Map<Integer, Song> getPlaylist() {
        return this.playlist;
    }

    public Song getCurrentSong() {
        return playlist.get(0);
    }
    /**
     * * Prints the currently playing song on the console.
     */
    public void printCurrentSong() {
        printSong(playlist.get(0));
    }
    // TODO: transfer this method to the Song class (inspired by PMD-violation: DataClass)
    // TODO: eventually have a list or array of the whole song information as song attribute
    /**
     * Prints the title, artist, album, genre and duration of a song on the console.
     * @param song song, whose information should be printed
     */
    public static void printSong(final Song song) {
        final String duration =  song.getDuration() + " seconds";
        final String[] songInfo = {song.getTitle(), song.getArtist(), song.getAlbum(), song.getGenre(), duration};
        // TODO remove the null test as it is implemenyted in the constructor now.
        for (int i = 0; i < OUTPUT_TEXTS.length; i++) {
            if (songInfo[i] != null) {
                System.out.println(OUTPUT_TEXTS[i] + songInfo[i]);
            } else {
                System.out.println(OUTPUT_TEXTS[i] + EMPTY);
            }
        }
    }
    /**
     * Prints the list of songs that are going to be played.
     */
    public void printPlaylist() {
        for (int i = 0; i < playlist.size(); i++) {
            printSong(playlist.get(i));
        }
    }
    /**
     * Stops ths application and releases memory spaces.
     */
    public void exit() {
        mediaPlayer.controls().stop();
        mediaPlayerFactory.release();
        mediaPlayer.release();
        this.running = false;
    }
    /**
     * Prints user input options on console upon invalid input.
     */
    public void invalidCommand() {
        System.out.println("Invalid command. \nYour command options: \nsong: get song information");
        System.out.println("playlist: print list of songs played next \nexit: Quit the Mediaplayer");
    }
    /**
     * Generates a playlist of random order from the songs located in the folder with the path {@link #dirPath}.
     */
    private void generatePlaylist() {
        final File files = new File(dirPath);
        final String[] sortedFileArray = files.list();
        final ArrayList<String> sortedFilteredFileArrayList = new ArrayList<>();
        for (final String fileName: sortedFileArray) {
            if (fileName.endsWith(".mp3")) {
                sortedFilteredFileArrayList.add(fileName);
            }
        }
        Collections.shuffle(sortedFilteredFileArrayList);
        this.playlist = new HashMap<>();
        int songCounter = 0;

        for (final String fileName: sortedFilteredFileArrayList) {
            final Media media = mediaPlayerFactory.media().newMedia(this.dirPath + "/" + fileName);
            final ParsedWaiter parsed = new ParsedWaiter(media) {
                @Override
                protected boolean onBefore(final Media component) {
                    return media.parsing().parse();
                }
            };
            try {
                parsed.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final MetaData metaData = media.meta().asMetaData();
            final InfoApi info = media.info();
            final String title = metaData.get(Meta.TITLE);
            final String artist = metaData.get(Meta.ARTIST);
            final String album = metaData.get(Meta.ALBUM);
            final String genre = metaData.get(Meta.GENRE);
            final long duration = info.duration();
            media.release();
            final Song song = new Song(fileName, title, artist, album, genre, duration, songCounter);
            if (mode.equals("commandLineMode")) {
                printSong(song);
            }
            playlist.put(songCounter, song);
            songCounter++;
        }
    }
    /**
     * Moves all songs one index up in Playlist and the first song in last position.
     */
    private void updatePlaylistUponSongChange() {
        final Song oldFirstSong = playlist.get(0);
        for (int i = 0; i < playlist.size() - 1; i++) {
            playlist.replace(i, playlist.get(i + 1));
        }
        playlist.replace(playlist.size() - 1, oldFirstSong);
        getPlaylist().get(0).setNoOfVotesToZero();
        if (mode.equals("guiMode")) {
            support.firePropertyChange("updatePlaylist", false, true);
        }
    }
    /**
     * Resorts the playlist after the user voted for a Song according to the number of votes.
     * @param songIdx the playlist index of the song that was voted for
     */
     public void updatePlaylistUponVote(final Integer songIdx) {
        final Song upVotedSong = playlist.get(songIdx);
        int newSongIdx = songIdx;
        for (int i = songIdx - 1; i > 0; i--) {
            if (upVotedSong.getNoOfVotes() > playlist.get(i).getNoOfVotes()) {
                newSongIdx = i;
            } else {
                break;
            }
        }
        for (int i = songIdx; i > newSongIdx; i--) {
            playlist.replace(i, playlist.get(i - 1));
        }
        playlist.replace(newSongIdx, upVotedSong);
     }
    /**
     * Starts the endless music playing according to the playlist.
     */
    private void play()  {
        runFirstSong();

        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(final MediaPlayer mediaPlayer) {
                updatePlaylistUponSongChange();
                runFirstSong();
            }
        });
    }

    /**
     * Starts the first song of the playlist in a Runnable.
     */
    private void runFirstSong()  {
        final String filename = playlist.get(0).getFilename();

        mediaPlayer.submit(new Runnable() {
                @Override
                public void run() {
                    if (mode.equals(SERVER_MODE)) {
                        mediaPlayer.media().play(dirPath + "/" + filename, mediaOptions);
                    } else if (mode.equals("clientMode")) {
                        // mediaPlayer.media().play(dirPath + "/" + filename);
                        // TODO: unhardcode adres of stream!!!
                        mediaPlayer.media().play(ADDRESS_OF_STREAM);
                    } else {
                        mediaPlayer.media().play(dirPath + "/" + filename);
                    }
                }
        });
    }
}
