package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControladorInicio {

    private static final int MAX_PUNTOS = 80;

    @FXML private TextField txtNombre;
    @FXML private TextField txtSalud;
    @FXML private TextField txtAtaque;
    @FXML private TextField txtDefensa;
    @FXML private TextField txtVelocidad;
    @FXML private Button btnIniciar;

    private boolean datosConfirmados = false;
    private String nombre;
    private int salud, ataque, defensa, velocidad;

    @FXML
    private void alHacerClickIniciar() {
        try {
            nombre   = txtNombre.getText().trim();
            salud    = Integer.parseInt(txtSalud.getText());
            ataque   = Integer.parseInt(txtAtaque.getText());
            defensa  = Integer.parseInt(txtDefensa.getText());
            velocidad= Integer.parseInt(txtVelocidad.getText());

            int total = salud + ataque + defensa + velocidad;
            if (total > MAX_PUNTOS) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Límite de puntos excedido");
                alerta.setHeaderText("Has asignado " + total + " puntos");
                alerta.setContentText("El máximo permitido es " + MAX_PUNTOS + " puntos.");
                alerta.showAndWait();
                return;
            }

            datosConfirmados = true;
            // Cerrar la ventana de inicio
            btnIniciar.getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Entrada inválida");
            alerta.setHeaderText("Debes introducir valores numéricos en todos los campos.");
            alerta.showAndWait();
        }
    }

    public boolean isDatosConfirmados() {
        return datosConfirmados;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSalud() {
        return salud;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }
}