module pl.edu.pja.s28687.typingapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.edu.pja.s28687.typingapp to javafx.fxml;
    exports pl.edu.pja.s28687.typingapp;
    exports pl.edu.pja.s28687.typingapp.model;
    opens pl.edu.pja.s28687.typingapp.model to javafx.fxml;
    exports pl.edu.pja.s28687.typingapp.controllers;
    opens pl.edu.pja.s28687.typingapp.controllers to javafx.fxml;
    exports pl.edu.pja.s28687.typingapp.view;
    opens pl.edu.pja.s28687.typingapp.view to javafx.fxml;
}