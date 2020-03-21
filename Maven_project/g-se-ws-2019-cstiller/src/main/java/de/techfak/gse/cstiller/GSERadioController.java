package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.StreamInitializationException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller class of the GUI-Application.
 */
public class GSERadioController implements PropertyChangeListener {
    private static final String EMPTY_STRING = "   ";
    private static final int COL_1_CONSTRAINT = 400;
    private static final int COL_2_CONSTRAINT = 200;
    private static final int COL_3_CONSTRAINT = 100;
    private static final int COL_4_CONSTRAINT = 50;
    private static final int[] COL_CONSTRAINTS;

    static {
        COL_CONSTRAINTS = new int[]{COL_1_CONSTRAINT, COL_2_CONSTRAINT, COL_3_CONSTRAINT, COL_4_CONSTRAINT};
    }

    private static final int DELAY_MS = 0;
    private static final int PERIOD_MS = 10000;
    private static final String NULL_PORT = null;
    private static final String CLIENT_MODE = "clientMode";
    private static final String HTTP = "http://";
    private static final String SLASHES = "//";
    private static final String COLON = ":";
    private static final String SERVER_CONNECTION_ERROR_MSG = "Error: connection to Server not possible.";
    private static final int STATUS_CODE_OK = 200;
    private Player playerModel;
    private String mode;
    @FXML
    private VBox vBox;

    /**
     * Class constructor.
     */
    public GSERadioController() {
    }

    /**
     * Triggers a GUI update when the Controller is informed about a playlist change.
     *
     * @param evt contains the name of the change event and the old and the new value
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (evt.getPropertyName().equals("updatePlaylist")) {
                    setGUI();
                }
            }
        });
    }

    /**
     * Sets up the GUI with the current playlist.
     */
    private void setGUI() {
        this.vBox.getChildren().clear();
        setHeaderRow();
        final Map<Integer, Song> playlist = this.playerModel.getPlaylist();
        for (int i = 0; i < playlist.size(); i++) {
            displaySong(playlist.get(i), i);
        }
    }

    /**
     * Sets up the header row of teh playlist in the GUI.
     */
    private void setHeaderRow() {
        final GridPane gridPane = new GridPane();
        final BorderStroke borderStroke = new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID,
            null,
            null);
        final List<BorderStroke> borderStrokeList = new ArrayList<>();
        borderStrokeList.add(borderStroke);
        final Border border = new Border(borderStrokeList, null);
        gridPane.setBorder(border);
        setBackgroundColor(gridPane, Color.SILVER);
        final Label title = new Label("Titel");
        final Label artist = new Label("Interpret");
        final Label empty = new Label("");
        final Label noOfVotes = new Label("Votes");
        final List<Node> headerRowContent = new ArrayList<>();
        headerRowContent.add(title);
        headerRowContent.add(artist);
        headerRowContent.add(empty);
        headerRowContent.add(noOfVotes);
        createGridPaneRow(gridPane, headerRowContent);
        this.vBox.getChildren().add(gridPane);
    }

    /**
     * Sets up the playlist row of a song on the GUI comprising the title, artist, a vote button and the
     * number of votes.
     *
     * @param song    the song to be displayed
     * @param songIdx the playlist index of the displayed song
     */
    private void displaySong(final Song song, final Integer songIdx) {
        final String title = song.getTitle();
        final String artist = song.getArtist();
        Label titleLabel;
        Label artistLabel;
        // TODO: I could already save an empty string in the song object
        if (title != null) {
            titleLabel = new Label(title);
        } else {
            titleLabel = new Label(EMPTY_STRING);
        }
        if (artist != null) {
            artistLabel = new Label(artist);
        } else {
            artistLabel = new Label(EMPTY_STRING);
        }
        final GridPane gridPane = new GridPane();
        final List<Node> rowContent = new ArrayList<>();
        rowContent.add(titleLabel);
        rowContent.add(artistLabel);
        if (mode.equals("guiMode")) {
            final Label noOfVotesLabel = new Label(String.valueOf(song.getNoOfVotes()));
            final Button voteButton = new Button("Vote");
            final EventHandler<ActionEvent> voteButtonHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent actionEvent) {
                    if (songIdx != 0) {
                        incrementSongVote(songIdx);
                    }
                }
            };
            voteButton.setOnAction(voteButtonHandler);
            rowContent.add(voteButton);
            rowContent.add(noOfVotesLabel);
        }
        createGridPaneRow(gridPane, rowContent);
        if (songIdx == 0) {
            setBackgroundColor(gridPane, Color.YELLOW);
        }
        this.vBox.getChildren().add(gridPane);
    }

    /**
     * Set the background color of a playlist row.
     *
     * @param gridPane the playlist row
     * @param color    the background color
     */
    private void setBackgroundColor(final GridPane gridPane, final Color color) {
        final BackgroundFill backgroundFill = new BackgroundFill(color, null, null);
        final Background background = new Background(backgroundFill);
        gridPane.setBackground(background);
    }

    /**
     * Sets up a playlist row with universal column constraints.
     *
     * @param gridPane   the gridPane frame of the playlist row
     * @param rowContent array of row elements
     */
    private void createGridPaneRow(final GridPane gridPane, final List<Node> rowContent) {
        for (int i = 0; i < rowContent.size(); i++) {
            gridPane.add(rowContent.get(i), i, 0);
            gridPane.getColumnConstraints().add(new ColumnConstraints(COL_CONSTRAINTS[i]));
        }
    }

    /**
     * Increments the number of Votes of a song on the GUI.
     *
     * @param songIdx index of the song whose number of votes is incremented
     */
    private void incrementSongVote(final Integer songIdx) {
        this.playerModel.getPlaylist().get(songIdx).incrementNoOfVotes();
        Platform.runLater(() -> setGUI());
        if (songIdx > 0) {
            this.playerModel.updatePlaylistUponVote(songIdx);
        }
    }

    /**
     * Connects the Controller with the model and triggers the GUI setup.
     *
     * @param playerDirectoryPath path where the player is supposed to read mp3s from
     * @param mode                can be gui- commandLine- server- or clientMode, in which the Player is used
     * @throws StreamInitializationException is thrown when port is an invalid number
     */
    public void initialize(final String playerDirectoryPath, String mode) throws StreamInitializationException {
        this.mode = mode;
        // TODO: move this to else and initlaize an own pkayer for client mode after webserver
        this.playerModel = new Player(playerDirectoryPath, mode, NULL_PORT);
        playerModel.addPropertyChangeListener(this);
        setGUI();
    }
}
