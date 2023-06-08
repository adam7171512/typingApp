package pl.edu.pja.s28687.typingapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.pja.s28687.typingapp.controllers.HotkeyController;
import pl.edu.pja.s28687.typingapp.controllers.LanguageController;
import pl.edu.pja.s28687.typingapp.controllers.TimeController;
import pl.edu.pja.s28687.typingapp.controllers.WordController;
import pl.edu.pja.s28687.typingapp.model.WordManager;
import pl.edu.pja.s28687.typingapp.view.GameView;

public class App extends Application {


    public static void main(String[] args) {
        launch(args);
    }

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

        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public void startApp() {
        launch();
    }
}
