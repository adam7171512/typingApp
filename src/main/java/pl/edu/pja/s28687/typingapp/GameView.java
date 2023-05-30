package pl.edu.pja.s28687.typingapp;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameView {

    private final WordController wordController;
    private VBox root;
    private HBox middlePane;
    private Footer footer;
    private Header header;
    private TextView textView;
    private LanguageController languageController;
    private TimeController timeController;
    private HotkeyController hotkeyController;
    private VBox clockView;

    public GameView(WordController wordController, TimeController timeController, LanguageController languageController, HotkeyController hotkeyController) {
        this.timeController = timeController;
        this.languageController = languageController;
        this.wordController = wordController;
        this.hotkeyController = hotkeyController;

        initialize();
    }

    private void initialize() {
        root = new VBox();
//        root.setAlignment(Pos.CENTER);
        root.setPrefHeight(1200);
        footer = new Footer();
        footer.setAlignment(Pos.BOTTOM_CENTER);
        header = new Header(timeController, languageController);
        header.setAlignment(Pos.TOP_CENTER);
        middlePane = new HBox();
        middlePane.setPrefHeight(706);
        middlePane.setAlignment(Pos.CENTER);

        textView = new TextView(wordController);
        clockView = new VBox();
        clockView.setAlignment(Pos.CENTER);
        Label timeLabel = new Label("Ready?");
        timeLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill : red");
        clockView.getChildren().add(timeLabel);

        middlePane.getChildren().addAll(textView.getTextField(), textView.getFlowPane());

        footer.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(header, middlePane, clockView,  footer);


        // make something so that middlePane could get focus, some sort of listener..
        middlePane.setOnMouseClicked(e -> {
            textView.getTextField().requestFocus();
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, hotkeyController);
        root.addEventFilter(KeyEvent.KEY_RELEASED, hotkeyController);
//        root.addEventHandler(KeyEvent.KEY_PRESSED, hotkeyController);
//        root.addEventHandler(KeyEvent.KEY_RELEASED, hotkeyController);

    }

    public TextView getTextView() {
        return textView;
    }

    public VBox getRoot() {
        return root;
    }

    public void updateTime(int timeLeft){
        ((Label) clockView.getChildren().get(0)).setText(timeLeft + "s");
    }

    public void setPaused(boolean paused) {
        String timeLeft = ((Label) clockView.getChildren().get(0)).getText();
        if (paused) {
            ((Label) clockView.getChildren().get(0)).setText("Paused (" + timeLeft + ")");
        } else {
            ((Label) clockView.getChildren().get(0)).setText("Go !");
        }
    }
}
