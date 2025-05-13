package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.mazmorron.modelo.Prota;

public class ControladorInicio {

    @FXML private TextField campoSalud;
    @FXML private TextField campoAtaque;
    @FXML private TextField campoDefensa;
    @FXML private TextField campoVelocidad;

    private Stage escenario;
    private Runnable accionDespues;
    private Prota protagonista;

    public void inicializar(Stage escenario, Runnable continuar) {
        this.escenario = escenario;
        this.accionDespues = continuar;
    }

    @FXML
    public void alHacerClickIniciar() {
        try {
            int salud = Integer.parseInt(campoSalud.getText());
            int ataque = Integer.parseInt(campoAtaque.getText());
            int defensa = Integer.parseInt(campoDefensa.getText());
            int velocidad = Integer.parseInt(campoVelocidad.getText());

            protagonista = new Prota("Héroe", salud, ataque, defensa, velocidad, 0);

            if (accionDespues != null) {
                accionDespues.run();
            }

        } catch (NumberFormatException e) {
            System.out.println("Introduce valores numéricos válidos.");
        }
    }

    public Prota getProtagonista() {
        return protagonista;
    }
}