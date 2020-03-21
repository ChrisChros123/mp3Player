package de.techfak.gse.cstiller;

import de.techfak.gse.cstiller.exceptions.StreamInitializationException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Entry Point to the GUI mode of the program.
 */
public class GSERadioGuiApplication extends Application {
    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT = 500;
    /**
     * The main entry point for the JavaFX applications.
     * @param stage the primary stage for this application, onto which the application scene can be set
     * @throws IOException thrown when getting the parameters fails
     * @throws StreamInitializationException is thrown when port is an invalid number
     */
    @Override
    public void start(final Stage stage) throws IOException, StreamInitializationException {
        final Parameters params = getParameters();
        final List<String> paramList = params.getRaw();
        final String playerDirectoryPath = paramList.get(0);
        final String mode = paramList.get(1);
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("GSERadioView.fxml"));
        final Pane root = fxmlLoader.load();
        final GSERadioController gseRadioController = fxmlLoader.getController();
        gseRadioController.initialize(playerDirectoryPath, mode);
        final Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.BLUEVIOLET);
        stage.setTitle("GSERadio");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Launches the JavaFX application.
     * @param args contains the path where to play MP3-files from
     */
    public static void main(final String... args) {
        launch(args);
    }
}
