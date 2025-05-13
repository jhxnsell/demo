package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ControladorFinal {

    @FXML private Label lblResultado;
    @FXML private HBox cajaBotones;

    private Stage escenario;
    private Runnable accionReintentar;
    private Runnable accionSiguiente;
    private Runnable accionSalir;

    public void inicializar(Stage escenario, boolean victoria,
                            Runnable reintentar, Runnable siguiente, Runnable salir) {
        this.escenario = escenario;
        this.accionReintentar = reintentar;
        this.accionSiguiente = siguiente;
        this.accionSalir = salir;

        if (victoria) {
            lblResultado.setText("¡Has ganado!");
            Button btnSiguiente = new Button("Siguiente nivel");
            btnSiguiente.setOnAction(e -> accionSiguiente.run());

            Button btnSalir = new Button("Salir del juego");
            btnSalir.setOnAction(e -> accionSalir.run());

            cajaBotones.getChildren().addAll(btnSiguiente, btnSalir);
        } else {
            lblResultado.setText("Has perdido. ¿Quieres volver a intentarlo?");
            Button btnReintentar = new Button("Reintentar");
            btnReintentar.setOnAction(e -> accionReintentar.run());
            cajaBotones.getChildren().add(btnReintentar);
        }
    }
}