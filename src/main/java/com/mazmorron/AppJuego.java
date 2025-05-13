package com.mazmorron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mazmorron.modelo.ModeloJuego;
import com.mazmorron.modelo.Prota;
import com.mazmorron.controlador.ControladorApp;
import com.mazmorron.controlador.ControladorInicio;
import com.mazmorron.controlador.ControladorFinal;

public class AppJuego extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        FXMLLoader loaderInicio = new FXMLLoader(getClass().getResource("/vista/VistaInicio.fxml"));
        Parent raizInicio = loaderInicio.load();
        ControladorInicio controladorInicio = loaderInicio.getController();

        Scene escenaInicio = new Scene(raizInicio);
        escenarioPrincipal.setTitle("Configuración del Juego");
        escenarioPrincipal.setScene(escenaInicio);
        escenarioPrincipal.show();

        controladorInicio.inicializar(escenarioPrincipal, () -> {
            try {
                lanzarJuego(escenarioPrincipal, controladorInicio.getProtagonista());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void lanzarJuego(Stage escenario, Prota protagonista) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
        Parent raiz = loader.load();

        ModeloJuego modelo = new ModeloJuego();
        modelo.cargarMapaDesde(getClass().getResourceAsStream("/datos/mapa.txt"));
        modelo.cargarEnemigosDesde(getClass().getResourceAsStream("/datos/enemigos.txt"));

        protagonista.setPosicion(1, 1);
        modelo.setProtagonista(protagonista);
        if (modelo.getMapa()[1][1].getOcupante() != null) {
            modelo.getMapa()[1][1].setOcupante(null);
        }
        modelo.getMapa()[1][1].setOcupante(protagonista);

        modelo.setAccionFin(() -> mostrarFin(modelo.getProtagonista().getSalud() > 0, escenario));

        ControladorApp controlador = loader.getController();
        controlador.setModelo(modelo);

        Scene escena = new Scene(raiz);
        escena.setOnKeyPressed(controlador::alPresionarTecla);

        escenario.setTitle("Juego de Mazmorra");
        escenario.setScene(escena);
        escenario.show();

        controlador.inicializarJuego();
        modelo.turnoSiguiente();
    }

    private void mostrarFin(boolean victoria, Stage escenario) {
        try {
            FXMLLoader loaderFin = new FXMLLoader(getClass().getResource("/vista/VistaFinal.fxml"));
            Parent raiz = loaderFin.load();
            ControladorFinal controladorFin = loaderFin.getController();

            Scene escenaFin = new Scene(raiz);

            controladorFin.inicializar(escenario, victoria,
                () -> {
                    try {
                        start(escenario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                () -> {
                    System.out.println("Aquí podrías cargar un nuevo mapa para el siguiente nivel.");
                    System.exit(0);
                },
                () -> System.exit(0)
            );

            escenario.setScene(escenaFin);
            escenario.setTitle("Fin de la partida");
            escenario.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}