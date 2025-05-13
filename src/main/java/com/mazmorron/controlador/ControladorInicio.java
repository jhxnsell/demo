package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControladorInicio {

    @FXML private TextField txtNombre;
    @FXML private TextField txtSalud;
    @FXML private TextField txtAtaque;
    @FXML private TextField txtDefensa;
    @FXML private TextField txtVelocidad;
    @FXML private Button btnIniciar;

    private boolean datosConfirmados = false;

    private String nombre;
    private int salud;
    private int ataque;
    private int defensa;
    private int velocidad;

    @FXML
    private void alHacerClickIniciar() {
        try {
            nombre = txtNombre.getText().trim();
            salud = Integer.parseInt(txtSalud.getText());
            ataque = Integer.parseInt(txtAtaque.getText());
            defensa = Integer.parseInt(txtDefensa.getText());
            velocidad = Integer.parseInt(txtVelocidad.getText());

            datosConfirmados = true;

            // Cierra ventana actual
            btnIniciar.getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            System.err.println("Introduce valores válidos en todos los campos.");
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