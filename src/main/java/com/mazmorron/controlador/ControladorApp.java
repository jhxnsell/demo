package com.mazmorron.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import com.mazmorron.modelo.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Controlador principal de la aplicación de juego de mazmorras.
 * Gestiona la interfaz gráfica, actualiza la vista según el modelo y
 * maneja la interacción del usuario.
 */
public class ControladorApp implements ModeloJuego.EscuchaModelo {

    @FXML private GridPane panelCuadricula;
    @FXML private Label lblSalud, lblAtaque, lblDefensa, lblVelocidad;
    @FXML private Label lblTurnoActual;
    @FXML private ListView<String> lvOrdenTurnos;

    private ModeloJuego modelo;

    /**
     * Asocia el modelo de juego al controlador y registra este controlador como escucha.
     * @param modelo Instancia del modelo de juego.
     */
    public void setModelo(ModeloJuego modelo) {
        this.modelo = modelo;
        modelo.agregarEscucha(this);
    }

    /**
     * Inicializa la vista del juego: dibuja el mapa, las estadísticas y el orden de turnos,
     * asegura el foco en el tablero y arranca la secuencia de turnos.
     */
    public void inicializarJuego() {
        dibujarMapa();
        actualizarEstadisticas();
        actualizarOrdenTurnos();

        // Mantener foco en el panel de cuadrícula
        panelCuadricula.requestFocus();
        panelCuadricula.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) panelCuadricula.requestFocus();
        });

        // Arranca la secuencia de turnos para el protagonista
        modelo.turnoSiguiente();
    }

    /**
     * Dibuja el mapa de juego en la cuadrícula, colorea muros, suelo,
     * protagonista y enemigos, y actualiza la etiqueta del turno actual.
     */
    public void dibujarMapa() {
        panelCuadricula.getChildren().clear();
        Celda[][] mapa = modelo.getMapa();
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                Rectangle celda = new Rectangle(40, 40);
                celda.setStroke(Color.BLACK);

                if (mapa[i][j].esMuro()) {
                    celda.setFill(Color.DARKGRAY);
                } else {
                    celda.setFill(Color.BEIGE);
                }

                Personaje ocupante = mapa[i][j].getOcupante();
                if (ocupante instanceof Prota) {
                    celda.setFill(Color.BLUE);
                } else if (ocupante instanceof Enemigo) {
                    celda.setFill(Color.RED);
                }

                panelCuadricula.add(celda, j, i);
            }
        }

        if (lblTurnoActual != null) {
            lblTurnoActual.setText("Turno: " + modelo.getTurnoActual());
        }
    }

    /**
     * Actualiza las etiquetas de estadísticas con los valores actuales del protagonista.
     */
    public void actualizarEstadisticas() {
        Prota p = modelo.getProtagonista();
        lblSalud.setText("Salud: " + p.getSalud());
        lblAtaque.setText("Ataque: " + p.getAtaque());
        lblDefensa.setText("Defensa: " + p.getDefensa());
        lblVelocidad.setText("Velocidad: " + p.getVelocidad());
    }

    /**
     * Actualiza la lista que muestra el orden de turnos según la velocidad de cada personaje.
     */
    public void actualizarOrdenTurnos() {
        List<Personaje> todos = new ArrayList<>();
        todos.add(modelo.getProtagonista());
        todos.addAll(modelo.getEnemigos());

        todos.sort((a, b) -> Integer.compare(b.getVelocidad(), a.getVelocidad()));
        lvOrdenTurnos.getItems().clear();

        for (Personaje p : todos) {
            String texto = p.getNombre();
            if (p instanceof Enemigo) {
                texto += " - Salud: " + p.getSalud();
            } else {
                texto += " (Tú)";
            }
            texto += " - Velocidad: " + p.getVelocidad();
            lvOrdenTurnos.getItems().add(texto);
        }
    }

    /**
     * Maneja los eventos de teclado para mover al protagonista.
     * @param evento Evento de tecla presionada.
     */
    @FXML
    public void alPresionarTecla(KeyEvent evento) {
        if (!(modelo.getPersonajeActual() instanceof Prota)) {
            return;
        }

        boolean seMovio = switch (evento.getCode()) {
            case W, UP    -> modelo.moverProtagonista(-1, 0);
            case S, DOWN  -> modelo.moverProtagonista(1, 0);
            case A, LEFT  -> modelo.moverProtagonista(0, -1);
            case D, RIGHT -> modelo.moverProtagonista(0, 1);
            default        -> false;
        };

        if (seMovio) {
            modelo.verificarFin();
            modelo.notificarEscuchas();
            modelo.turnoSiguiente();
        }
    }

    /**
     * Actualiza la vista cuando el modelo notifica un cambio.
     */
    @Override
    public void alCambiarModelo() {
        dibujarMapa();
        actualizarEstadisticas();
        actualizarOrdenTurnos();
    }
}