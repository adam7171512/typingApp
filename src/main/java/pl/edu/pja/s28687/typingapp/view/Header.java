package pl.edu.pja.s28687.typingapp.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.edu.pja.s28687.typingapp.controllers.LanguageController;
import pl.edu.pja.s28687.typingapp.controllers.TimeController;

public class Header extends HBox {

    ObservableList<String> langItems;
    ObservableList<Integer> timeItems;
    private TimeController timeController;
    private LanguageController languageController;
    private ListView<String> languageList;
    private ListView<Integer> timeList;
    private VBox languageBox;
    private VBox timeBox;

    public Header(TimeController timeController, LanguageController languageController) {
        this.timeController = timeController;
        this.languageController = languageController;
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-image: url('header.png'); -fx-background-repeat: stretch; -fx-background-size: 100% 100%;");
        setPrefWidth(1200);
        setPrefHeight(247);

        this.languageBox = new VBox();
        this.languageBox.setAlignment(Pos.CENTER);

        this.timeBox = new VBox();
        this.timeBox.setAlignment(Pos.CENTER);

        Label label = new Label("Choose language:");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        this.languageBox.getChildren().add(label);
        langItems = FXCollections.observableArrayList(LanguageController.getLanguages());

        languageList = new ListView<>(langItems);
        //Todo: move controller init from here
        languageList.getSelectionModel().selectedItemProperty().addListener(languageController);
        languageBox.getChildren().add(languageList);

        Label label2 = new Label("Choose time:");
        label2.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        this.timeBox.getChildren().add(label2);

        timeItems = FXCollections.observableArrayList(15, 20, 45, 60, 90, 120, 300);
        timeList = new ListView<>(timeItems);
        timeList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super Integer>) timeController);

        this.timeBox.getChildren().add(timeList);
        getChildren().add(timeBox);
        getChildren().add(languageBox);

    }
}
