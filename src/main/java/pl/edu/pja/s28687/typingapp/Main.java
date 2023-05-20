package pl.edu.pja.s28687.typingapp;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        String text = "This is not a very lengthy test text.";

        WordController wordController = new WordController();
        TextView textView = new TextView(wordController);
        WordManager wordManager = new WordManager(textView);
        wordController.setWordManager(wordManager);
        wordManager.setText(text);

        root.getChildren().addAll(textView.getFlowPane(), textView.getTextField());

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
