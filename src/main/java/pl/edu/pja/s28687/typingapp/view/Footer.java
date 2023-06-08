package pl.edu.pja.s28687.typingapp.view;

import javafx.scene.layout.HBox;

public class Footer extends HBox {

    public Footer() {
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-image: url('footer.png'); -fx-background-repeat: stretch; -fx-background-size: 100% 100%;");
        setPrefWidth(1200);
        setPrefHeight(247);
    }
}