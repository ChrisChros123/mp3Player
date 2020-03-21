package de.techfak.gse.cstiller;

/**
 * A Song class.
 */
public class Song {
    private String filename;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private long duration;
    private int noOfVotes;
    private int songID;
    /**
     * Class constructor.
     * @param filename the name of the MP3-file of the song
     * @param title the title of the song extracted from the ID3-tag
     * @param artist the artist of the song extracted from the ID3-tag
     * @param album the album, which the song is from, extracted from the ID3-tag
     * @param genre the genre of the song extracted from the ID3-tag
     * @param duration the duration of the song
     * @param songID the ID of the song
     * */
    @SuppressWarnings("checkstyle:DeclarationOrder")
    public Song(final String filename, final String title, final String artist,
                final String album, final String genre, final long duration, final int songID) {
        //TODO test for null here and not elsewhere
        if (filename != null) {
            this.filename = filename;
        } else {
            this.filename = "";
        }
        if (title != null) {
            this.title = title;
        } else {
            this.title = "";
        }
        if (artist != null) {
            this.artist = artist;
        } else {
            this.artist = null;
        }
        if (album != null) {
            this.album = album;
        } else {
            this.album = "";
        }
        if (genre != null) {
            this.genre = genre;
        } else {
            this.genre = "";
        }
        this.duration = duration;
        this.noOfVotes = 0;
        this.songID = songID;
    }
    public Song() {
    }
    /**
     * Tests if the parameter sng equals the method calling song.
     * @param song song object to compare with.
     * @return true if songs equal in all attributes.
     */
    @SuppressWarnings("checkstyle:OperatorWrap")
    public boolean isEqualTo(Song song) {
        boolean res = getDuration() == song.getDuration() &&
            getNoOfVotes() == song.getNoOfVotes() &&
            getSongID() == song.getSongID() &&
            getFilename().equals(song.getFilename()) &&
            getTitle().equals(song.getTitle()) &&
            getArtist().equals(song.getArtist()) &&
            getAlbum().equals(song.getAlbum()) &&
            getGenre().equals(song.getGenre());
        return res;
    }
    /**
     * Get the filename attribute.
     * @return the name of the MP3-file.
     */
    public String getFilename() {
        return this.filename;
    }
    /**
     * Get the Title attribute of the song.
     * @return the title of the song.
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Get the artist attribute of the song.
     * @return the artist of the song.
     */
    public String getArtist() {
        return this.artist;
    }
    /**
     * Increments the number of votes of the song.
     */
    public void incrementNoOfVotes() {
        this.noOfVotes++;
    }
    /**
     * Get the noOfVotes attribute of the song.
     * @return the number of votes of the song
     */
    public int getNoOfVotes() {
        return this.noOfVotes;
    }
    /**
     * Sets the number of votes of the song to zero.
     */
    public void setNoOfVotesToZero() {
        this.noOfVotes = 0;
    }
    /**
     * Get the album attribute of the song.
     * @return the name of the album the song is from
     */
    public String getAlbum() {
        return this.album;
    }
    /**
     * Get the genre attribute of the song.
     * @return the genre of the song
     */
    public String getGenre() {
        return this.genre;
    }
    /**
     * Get the duration attribute of the song.
     * @return the duration of the song
     */
    public long getDuration() {
        return this.duration;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }
}
