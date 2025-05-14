// src/main/java/com/mazmorron/AppJuego.java
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
import java.nio.file.*;

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

    @Override
    public void start(Stage primaryStage) {
        this.escenario = primaryStage;
        mostrarPantallaInicio();
    }

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
     * Intenta cargar un recurso del classpath (con getResourceAsStream 
     * y ClassLoader), y si falla, lo busca como fichero en disco:
     * ./mapas/nivel1.txt  o ./enemigos/enemigos1.txt
     */
    private InputStream abrirRecurso(String ruta) throws IOException {
        // 1) getResourceAsStream con '/':
        InputStream is = getClass().getResourceAsStream(ruta);
        if (is != null) return is;

        // 2) ClassLoader (sin '/'):
        String sinSlash = ruta.startsWith("/") ? ruta.substring(1) : ruta;
        is = getClass().getClassLoader().getResourceAsStream(sinSlash);
        if (is != null) return is;

        // 3) desde disco, relativo al working dir:
        Path p = Paths.get(System.getProperty("user.dir"), sinSlash);
        if (Files.exists(p)) {
            return Files.newInputStream(p);
        }

        // no encontrado en ninguno de los anteriores
        throw new IOException("No se encontró el recurso: " + ruta + 
            " ni en classpath ni en disco (./" + sinSlash + ")");
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
                if (victoria && ++nivelActual < mapas.length) {
                    lanzarJuego(protagonista);
                } else {
                    System.out.println(victoria
                        ? "¡Has completado todos los niveles!"
                        : "Has sido derrotado.");
                    System.exit(0);
                }
            });

            // 1) Cargar mapa
            InputStream mapaStream = abrirRecurso(mapas[nivelActual]);
            modelo.cargarMapaDesde(mapaStream);

            // 2) Cargar enemigos
            InputStream enemStream = abrirRecurso(enemigos[nivelActual]);
            modelo.cargarEnemigosDesde(enemStream);

            // 3) Sitúa al protagonista en el mapa
            Celda[][] m = modelo.getMapa();
            m[protagonista.getX()][protagonista.getY()]
              .setOcupante(protagonista);
            modelo.notificarEscuchas();

            // s) Iniciar la vista
            controlador.setModelo(modelo);
            controlador.inicializarJuego();

            // 5) Arrancar el primer turno (para que personajeActual = protagonista)
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

    public static void main(String[] args) {
        launch(args);
    }
}