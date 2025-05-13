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

public class ControladorApp implements ModeloJuego.EscuchaModelo {

    @FXML private GridPane panelCuadricula;
    @FXML private Label lblSalud, lblAtaque, lblDefensa, lblVelocidad;
    @FXML private Label lblTurnoActual;
    @FXML private ListView<String> lvOrdenTurnos;

    private ModeloJuego modelo;

    public void setModelo(ModeloJuego modelo) {
        this.modelo = modelo;
        modelo.agregarEscucha(this);
    }

    public void inicializarJuego() {
        dibujarMapa();
        actualizarEstadisticas();
        actualizarOrdenTurnos();

        // Forzar foco al tablero
        panelCuadricula.requestFocus();

        // Reasignar foco si se pierde
        panelCuadricula.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                panelCuadricula.requestFocus();
            }
        });
    }

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

    public void actualizarEstadisticas() {
        Prota p = modelo.getProtagonista();
        lblSalud.setText("Salud: " + p.getSalud());
        lblAtaque.setText("Ataque: " + p.getAtaque());
        lblDefensa.setText("Defensa: " + p.getDefensa());
        lblVelocidad.setText("Velocidad: " + p.getVelocidad());
    }

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

    @FXML
    public void alPresionarTecla(KeyEvent evento) {
        if (!(modelo.getPersonajeActual() instanceof Prota)) {
            return;
        }

        boolean seMovio = switch (evento.getCode()) {
            case W, UP -> modelo.moverProtagonista(-1, 0);
            case S, DOWN -> modelo.moverProtagonista(1, 0);
            case A, LEFT -> modelo.moverProtagonista(0, -1);
            case D, RIGHT -> modelo.moverProtagonista(0, 1);
            default -> false;
        };

        if (seMovio) {
            modelo.verificarFin();
            modelo.notificarEscuchas();
            modelo.turnoSiguiente();
        }
    }

    @Override
    public void alCambiarModelo() {
        dibujarMapa();
        actualizarEstadisticas();
        actualizarOrdenTurnos();
    }
}