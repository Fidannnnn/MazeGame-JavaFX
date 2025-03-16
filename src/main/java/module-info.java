module com.example.mazegameee {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.mazegameee.game;
    opens com.example.mazegameee.game to javafx.graphics;
}