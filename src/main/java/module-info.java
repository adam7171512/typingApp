module pl.edu.pja.s28687.typingapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.edu.pja.s28687.typingapp to javafx.fxml;
    exports pl.edu.pja.s28687.typingapp;
}