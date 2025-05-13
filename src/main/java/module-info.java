module com.dungeonmaze {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.mazmorron.controlador to javafx.fxml;

    exports com.mazmorron;
}