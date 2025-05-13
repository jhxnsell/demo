package com.mazmorron.controlador;

import java.io.IOException;
import javafx.fxml.FXML;

public class ControladorApp {

    @FXML
    private void switchToSecondary() throws IOException {
        AppJuego.setRoot("secondary");
    }
}
