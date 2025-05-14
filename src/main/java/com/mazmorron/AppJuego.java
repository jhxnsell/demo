package com.mazmorron;

import com.mazmorron.controlador.ControladorApp;
import com.mazmorron.controlador.ControladorInicio;
import com.mazmorron.modelo.ModeloJuego;
import com.mazmorron.modelo.Prota;
import com.mazmorron.modelo.Celda;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Clase principal de la aplicación JavaFX.
 * Gestiona la pantalla de inicio, carga de niveles y ciclo de juego.
 * @author Lucas Rasmussen Marcos
 * @author Jhansell Francisco García Vargas
 */
public class AppJuego extends Application {

    private Stage escenario;
    private ModeloJuego modelo;
    private ControladorApp controlador;

    private int nivelActual = 0;
    private final String[] mapas = {
        "/mapas/nivel1.txt",
        "/mapas/nivel2.txt",
        "/mapas/nivel3.txt"
    };
    private final String[] enemigos = {
        "/enemigos/enemigos1.txt",
        "/enemigos/enemigos2.txt",
        "/enemigos/enemigos3.txt"
    };

    /**
     * Método de arranque de JavaFX, muestra la pantalla de inicio.
     * @param primaryStage Escenario principal proporcionado por JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        this.escenario = primaryStage;
        mostrarPantallaInicio();
    }

    /**
     * Muestra la ventana de inicio para capturar datos del jugador y lanza el juego.
     */
    private void mostrarPantallaInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicio.fxml"));
            Parent root = loader.load();
            ControladorInicio ctrl = loader.getController();

            Stage inicio = new Stage();
            inicio.setTitle("Mazmorron – Inicio");
            inicio.setScene(new Scene(root));
            inicio.showAndWait();

            if (!ctrl.isDatosConfirmados()) {
                System.exit(0);
            }

            Prota prota = new Prota(
                ctrl.getNombre(),
                ctrl.getSalud(),
                ctrl.getAtaque(),
                ctrl.getDefensa(),
                ctrl.getVelocidad()
            );
            prota.setPosicion(1, 1);
            lanzarJuego(prota);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intenta abrir un recurso dado su ruta, buscando en classpath o en disco.
     * @param ruta Ruta del recurso.
     * @return Flujo de entrada del recurso.
     * @throws IOException Si no se encuentra el recurso.
     */
    private InputStream abrirRecurso(String ruta) throws IOException {
        InputStream is = getClass().getResourceAsStream(ruta);
        if (is != null) {
            return is;
        }
        String sinSlash = ruta.startsWith("/") ? ruta.substring(1) : ruta;
        is = getClass().getClassLoader().getResourceAsStream(sinSlash);
        if (is != null) {
            return is;
        }
        Path p = Paths.get(System.getProperty("user.dir"), sinSlash);
        if (Files.exists(p)) {
            return Files.newInputStream(p);
        }
        throw new IOException("No se encontró el recurso: " + ruta);
    }

    /**
     * Configura la escena de juego para el nivel actual, carga mapa y enemigos,
     * inicializa modelo y controlador y muestra la ventana.
     * @param protagonista Instancia del personaje principal.
     */
    private void lanzarJuego(Prota protagonista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            Parent root = loader.load();
            controlador = loader.getController();

            modelo = new ModeloJuego();
            modelo.setProtagonista(protagonista);

            modelo.setAccionFin(() -> {
                boolean victoria = modelo.getProtagonista().getSalud() > 0;
                if (victoria && ++nivelActual < mapas.length) {
                    lanzarJuego(protagonista);
                } else {
                    System.out.println(victoria
                        ? "¡Has completado todos los niveles!"
                        : "Has sido derrotado.");
                    System.exit(0);
                }
            });

            InputStream mapaStream = abrirRecurso(mapas[nivelActual]);
            modelo.cargarMapaDesde(mapaStream);

            InputStream enemStream = abrirRecurso(enemigos[nivelActual]);
            modelo.cargarEnemigosDesde(enemStream);

            Celda[][] m = modelo.getMapa();
            m[protagonista.getX()][protagonista.getY()].setOcupante(protagonista);
            modelo.notificarEscuchas();

            controlador.setModelo(modelo);
            controlador.inicializarJuego();
            modelo.turnoSiguiente();

            Scene escena = new Scene(root);
            escena.setOnKeyPressed(controlador::alPresionarTecla);
            escenario.setTitle("Mazmorron – Nivel " + (nivelActual + 1));
            escenario.setScene(escena);
            escenario.setMaximized(true);
            escenario.centerOnScreen();
            escenario.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Punto de entrada de la aplicación.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}