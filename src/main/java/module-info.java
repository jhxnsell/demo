module com.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mazmorron to javafx.fxml;
    exports com.mazmorron;
}
