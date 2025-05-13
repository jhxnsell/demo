package com.mazmorron;

import com.mazmorron.controlador.ControladorApp;
import com.mazmorron.controlador.ControladorInicio;
import com.mazmorron.modelo.ModeloJuego;
import com.mazmorron.modelo.Prota;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppJuego extends Application {

    private Stage escenario;
    private ModeloJuego modelo;
    private ControladorApp controlador;

    private int nivelActual = 0;
    private final String[] mapas = {
            "/nivel1.txt",
            "/nivel2.txt",
            "/nivel3.txt"
    };
    private final String[] enemigos = {
            "/enemigos1.txt",
            "/enemigos2.txt",
            "/enemigos3.txt"
    };

    @Override
    public void start(Stage stage) {
        this.escenario = stage;

        mostrarPantallaInicio();
    }

    private void mostrarPantallaInicio() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicio.fxml"));
        Parent root = loader.load();

        ControladorInicio controladorInicio = loader.getController();

        Stage ventanaInicio = new Stage();
        ventanaInicio.setTitle("Mazmorron - Inicio");
        ventanaInicio.setScene(new Scene(root));
        ventanaInicio.initOwner(escenario);  // opcional
        ventanaInicio.centerOnScreen();
        ventanaInicio.showAndWait(); // Espera a que el jugador haga clic en "Iniciar"

        if (controladorInicio.isDatosConfirmados()) {
            Prota prota = new Prota(
                controladorInicio.getNombre(),
                controladorInicio.getSalud(),
                controladorInicio.getAtaque(),
                controladorInicio.getDefensa(),
                controladorInicio.getVelocidad()
            );
            prota.setPosicion(1, 1);
            lanzarJuego(prota);
        } else {
            System.out.println("Inicio cancelado.");
            System.exit(0);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void lanzarJuego(Prota protagonista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            Parent root = loader.load();

            controlador = loader.getController();

            modelo = new ModeloJuego();
            modelo.setProtagonista(protagonista);

            modelo.setAccionFin(() -> {
                boolean victoria = modelo.getProtagonista().getSalud() > 0;

                if (victoria) {
                    nivelActual++;
                    if (nivelActual < mapas.length) {
                        cargarNivel(protagonista);
                    } else {
                        mostrarPantallaFinal(true);
                    }
                } else {
                    mostrarPantallaFinal(false);
                }
            });

            modelo.cargarMapaDesde(getClass().getResourceAsStream(mapas[nivelActual]));
            modelo.cargarEnemigosDesde(getClass().getResourceAsStream(enemigos[nivelActual]));

            controlador.setModelo(modelo);
            controlador.inicializarJuego();

            Scene escena = new Scene(root);
            escena.setOnKeyPressed(controlador::alPresionarTecla);
            escenario.setScene(escena);
            escenario.setTitle("Mazmorron - Juego");
            escenario.setMaximized(true);
            escenario.centerOnScreen();
            escenario.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarNivel(Prota protagonista) {
        try {
            modelo = new ModeloJuego();
            modelo.setProtagonista(protagonista);
            protagonista.setPosicion(1, 1);

            modelo.setAccionFin(() -> {
                boolean victoria = modelo.getProtagonista().getSalud() > 0;

                if (victoria) {
                    nivelActual++;
                    if (nivelActual < mapas.length) {
                        cargarNivel(protagonista);
                    } else {
                        mostrarPantallaFinal(true);
                    }
                } else {
                    mostrarPantallaFinal(false);
                }
            });

            modelo.cargarMapaDesde(getClass().getResourceAsStream(mapas[nivelActual]));
            modelo.cargarEnemigosDesde(getClass().getResourceAsStream(enemigos[nivelActual]));

            controlador.setModelo(modelo);
            controlador.inicializarJuego();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPantallaFinal(boolean gano) {
        // Aquí podrías cargar una VistaFinal.fxml con botones "Salir" o "Reintentar"
        System.out.println(gano ? "¡Has ganado el juego!" : "Has sido derrotado.");
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
