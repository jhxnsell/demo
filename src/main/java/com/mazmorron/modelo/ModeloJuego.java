package com.mazmorron.modelo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ModeloJuego {

    public interface EscuchaModelo {
        void alCambiarModelo();
    }

    private final List<EscuchaModelo> escuchas = new ArrayList<>();
    private final List<Enemigo> enemigos = new ArrayList<>();
    private Queue<Personaje> colaTurnos = new LinkedList<>();
    private Personaje personajeActual;
    private Prota protagonista;
    private Celda[][] mapa;
    private Runnable accionFin;
    private int turnoActual = 1;

    public void agregarEscucha(EscuchaModelo escucha) {
        escuchas.add(escucha);
    }

    public void notificarEscuchas() {
        for (EscuchaModelo escucha : escuchas) {
            escucha.alCambiarModelo();
        }
    }

    public void setProtagonista(Prota p) {
        this.protagonista = p;
    }

    public Prota getProtagonista() {
        return protagonista;
    }

    public Personaje getPersonajeActual() {
        return personajeActual;
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }

    public Celda[][] getMapa() {
        return mapa;
    }

    public void setAccionFin(Runnable accion) {
        this.accionFin = accion;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    private void notificarFin(boolean victoria) {
        if (accionFin != null) {
            accionFin.run();
        }
    }

    public void cargarMapaDesde(InputStream input) {
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(input))) {
            List<String> lineas = new ArrayList<>();
            String linea;
            int maxColumnas = 0;

            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                    maxColumnas = Math.max(maxColumnas, linea.length());
                }
            }

            int filas = lineas.size();
            mapa = new Celda[filas][maxColumnas];

            for (int i = 0; i < filas; i++) {
                String fila = lineas.get(i);
                for (int j = 0; j < maxColumnas; j++) {
                    char c = j < fila.length() ? fila.charAt(j) : '.';
                    mapa[i][j] = new Celda(c == '#');
                }
            }

            notificarEscuchas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarEnemigosDesde(InputStream input) {
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(input))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",");
                String nombre = partes[0];
                int x = Integer.parseInt(partes[1]);
                int y = Integer.parseInt(partes[2]);
                int salud = Integer.parseInt(partes[3]);
                int ataque = Integer.parseInt(partes[4]);
                int defensa = Integer.parseInt(partes[5]);
                int velocidad = Integer.parseInt(partes[6]);
                int vision = Integer.parseInt(partes[7]);

                Enemigo enemigo = new Enemigo(nombre, salud, ataque, defensa, velocidad, vision);
                enemigo.setPosicion(x, y);
                enemigos.add(enemigo);
                mapa[x][y].setOcupante(enemigo);
            }

            notificarEscuchas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean moverProtagonista(int dx, int dy) {
        if (!(personajeActual instanceof Prota)) return false;

        int x = protagonista.getX();
        int y = protagonista.getY();
        int nuevoX = x + dx;
        int nuevoY = y + dy;

        if (!enLimites(nuevoX, nuevoY) || mapa[nuevoX][nuevoY].esMuro()) return false;

        Personaje objetivo = mapa[nuevoX][nuevoY].getOcupante();
        if (objetivo instanceof Enemigo) {
            atacar(protagonista, objetivo);
        } else if (objetivo == null) {
            mapa[x][y].setOcupante(null);
            mapa[nuevoX][nuevoY].setOcupante(protagonista);
            protagonista.setPosicion(nuevoX, nuevoY);
        }

        return true;
    }

    public void turnoSiguiente() {
        if (colaTurnos.isEmpty()) {
            turnoActual++;
            prepararTurnos();
        }

        personajeActual = colaTurnos.poll();
        if (personajeActual == null || personajeActual.getSalud() <= 0) {
            turnoSiguiente();
            return;
        }

        if (personajeActual instanceof Enemigo enemigo) {
            PauseTransition pausa = new PauseTransition(Duration.millis(300));
            pausa.setOnFinished(e -> {
                accionEnemigo(enemigo);
                verificarFin();
                notificarEscuchas();
                turnoSiguiente();
            });
            pausa.play();
        }
        // Si es el protagonista, se espera a su entrada desde teclado
    }

    private void prepararTurnos() {
        colaTurnos.clear();
        List<Personaje> todos = new ArrayList<>();
        todos.add(protagonista);
        todos.addAll(enemigos);
        todos.sort((a, b) -> Integer.compare(b.getVelocidad(), a.getVelocidad()));
        colaTurnos.addAll(todos);
    }

    public void verificarFin() {
        enemigos.removeIf(e -> e.getSalud() <= 0);

        if (protagonista.getSalud() <= 0) {
            notificarFin(false);
        } else if (enemigos.isEmpty()) {
            notificarFin(true);
        }
    }

    private void accionEnemigo(Enemigo e) {
        int ex = e.getX(), ey = e.getY();
        int px = protagonista.getX(), py = protagonista.getY();

        int dx = Integer.compare(px, ex);
        int dy = Integer.compare(py, ey);
        int distancia = Math.abs(px - ex) + Math.abs(py - ey);

        int nuevoX = ex + dx;
        int nuevoY = ey + dy;

        if (distancia <= e.getVision() && enLimites(nuevoX, nuevoY)) {
            if (mapa[nuevoX][nuevoY].getOcupante() instanceof Prota) {
                atacar(e, protagonista);
            } else if (!mapa[nuevoX][nuevoY].esMuro() && mapa[nuevoX][nuevoY].getOcupante() == null) {
                mapa[ex][ey].setOcupante(null);
                e.setPosicion(nuevoX, nuevoY);
                mapa[nuevoX][nuevoY].setOcupante(e);
            }
        } else {
            moverAleatoriamente(e);
        }
    }

    private void moverAleatoriamente(Enemigo e) {
        int ex = e.getX();
        int ey = e.getY();

        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        List<int[]> opciones = Arrays.asList(direcciones);
        Collections.shuffle(opciones);

        for (int[] dir : opciones) {
            int nx = ex + dir[0];
            int ny = ey + dir[1];

            if (enLimites(nx, ny) && !mapa[nx][ny].esMuro() && mapa[nx][ny].getOcupante() == null) {
                mapa[ex][ey].setOcupante(null);
                e.setPosicion(nx, ny);
                mapa[nx][ny].setOcupante(e);
                break;
            }
        }
    }

    private void atacar(Personaje atacante, Personaje defensor) {
        int danio = Math.max(1, atacante.getAtaque() - defensor.getDefensa());
        defensor.setSalud(defensor.getSalud() - danio);

        if (defensor.getSalud() <= 0) {
            mapa[defensor.getX()][defensor.getY()].setOcupante(null);
        }
    }

    private boolean enLimites(int x, int y) {
        return x >= 0 && y >= 0 && x < mapa.length && y < mapa[0].length;
    }
}