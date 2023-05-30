package pl.edu.pja.s28687.typingapp;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        WordController wordController = new WordController();
        TimeController timeController = new TimeController();
        LanguageController languageController = new LanguageController();
        HotkeyController hotkeyController = new HotkeyController();


        GameView gameView = new GameView(wordController, timeController, languageController, hotkeyController);
        WordManager wordManager = new WordManager(gameView, gameView.getTextView());
        wordController.setWordManager(wordManager);
        timeController.setWordManager(wordManager);
        hotkeyController.setWordManager(wordManager);
        wordManager.start();

        VBox root = gameView.getRoot();
        primaryStage.setScene(new Scene(root, 1200, 1200));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
