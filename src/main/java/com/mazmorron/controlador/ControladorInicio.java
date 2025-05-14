package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de inicio del juego.
 * Permite al usuario introducir el nombre y distribuir puntos de estadísticas.
 */
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

    /**
     * Valida los valores introducidos, comprueba que el total no exceda el máximo permitido
     * y cierra la ventana de inicio si los datos son correctos.
     */
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
            btnIniciar.getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Entrada inválida");
            alerta.setHeaderText("Debes introducir valores numéricos en todos los campos.");
            alerta.showAndWait();
        }
    }

    /**
     * Indica si los datos han sido confirmados correctamente.
     * @return True si los datos son válidos.
     */
    public boolean isDatosConfirmados() {
        return datosConfirmados;
    }

    /**
     * Obtiene el nombre del protagonista.
     * @return Nombre introducido.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la salud asignada al protagonista.
     * @return Valor de salud.
     */
    public int getSalud() {
        return salud;
    }

    /**
     * Obtiene el ataque asignado al protagonista.
     * @return Valor de ataque.
     */
    public int getAtaque() {
        return ataque;
    }

    /**
     * Obtiene la defensa asignada al protagonista.
     * @return Valor de defensa.
     */
    public int getDefensa() {
        return defensa;
    }

    /**
     * Obtiene la velocidad asignada al protagonista.
     * @return Valor de velocidad.
     */
    public int getVelocidad() {
        return velocidad;
    }
}