package com.mazmorron.modelo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Modelo del juego que gestiona mapa, personajes, turnos y lógica de combate.
 */
public class ModeloJuego {

    /**
     * Interfaz para escuchar cambios en el estado del modelo.
     */
    public interface EscuchaModelo {
        /**
         * Se invoca cuando el modelo cambia para actualizar la vista.
         */
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

    /**
     * Registra un escucha para recibir notificaciones de cambio.
     * @param escucha Implementación de EscuchaModelo.
     */
    public void agregarEscucha(EscuchaModelo escucha) {
        escuchas.add(escucha);
    }

    /**
     * Notifica a todos los escuchas que el modelo ha cambiado.
     */
    public void notificarEscuchas() {
        for (EscuchaModelo e : escuchas) {
            e.alCambiarModelo();
        }
    }

    /**
     * Establece el protagonista del juego.
     * @param p Instancia del protagonista.
     */
    public void setProtagonista(Prota p) {
        this.protagonista = p;
    }

    /**
     * Obtiene el protagonista actual.
     * @return Objeto Prota.
     */
    public Prota getProtagonista() {
        return protagonista;
    }

    /**
     * Obtiene el personaje cuyo turno está activo.
     * @return Personaje actual.
     */
    public Personaje getPersonajeActual() {
        return personajeActual;
    }

    /**
     * Obtiene la lista de enemigos vivos.
     * @return Lista de Enemigo.
     */
    public List<Enemigo> getEnemigos() {
        return enemigos;
    }

    /**
     * Obtiene la matriz de celdas del mapa.
     * @return Matriz de Celda.
     */
    public Celda[][] getMapa() {
        return mapa;
    }

    /**
     * Obtiene el número de turno actual.
     * @return Entero de turno.
     */
    public int getTurnoActual() {
        return turnoActual;
    }

    /**
     * Establece la acción a ejecutar al finalizar el juego o nivel.
     * @param accion Runnable a ejecutar.
     */
    public void setAccionFin(Runnable accion) {
        this.accionFin = accion;
    }

    /**
     * Notifica la finalización del juego con resultado.
     * @param victoria True si victoria; false si derrota.
     */
    private void notificarFin(boolean victoria) {
        if (accionFin != null) {
            accionFin.run();
        }
    }

    /**
     * Carga un mapa desde un InputStream, crea celdas y notifica cambios.
     * @param in Flujo de entrada del archivo de mapa.
     */
    public void cargarMapaDesde(InputStream in) {
        if (in == null) throw new IllegalArgumentException("Mapa: recurso no encontrado.");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            List<String> lines = new ArrayList<>();
            String line;
            int maxC = 0;
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                    maxC = Math.max(maxC, line.length());
                }
            }
            mapa = new Celda[lines.size()][maxC];
            for (int i = 0; i < lines.size(); i++) {
                String row = lines.get(i);
                for (int j = 0; j < maxC; j++) {
                    mapa[i][j] = new Celda(j < row.length() && row.charAt(j) == '#');
                }
            }
            notificarEscuchas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga enemigos desde un InputStream, los coloca en el mapa y notifica cambios.
     * @param in Flujo de entrada del archivo de enemigos.
     */
    public void cargarEnemigosDesde(InputStream in) {
        if (in == null) throw new IllegalArgumentException("Enemigos: recurso no encontrado.");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                Enemigo e = new Enemigo(
                    p[0],
                    Integer.parseInt(p[3]),
                    Integer.parseInt(p[4]),
                    Integer.parseInt(p[5]),
                    Integer.parseInt(p[6]),
                    Integer.parseInt(p[7])
                );
                int x = Integer.parseInt(p[1]);
                int y = Integer.parseInt(p[2]);
                e.setPosicion(x, y);
                enemigos.add(e);
                mapa[x][y].setOcupante(e);
            }
            notificarEscuchas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intenta mover al protagonista, gestiona combate y retorna si hubo movimiento.
     * @param dx Desplazamiento en X.
     * @param dy Desplazamiento en Y.
     * @return True si se movió o atacó.
     */
    public boolean moverProtagonista(int dx, int dy) {
        if (!(personajeActual instanceof Prota)) return false;
        int x = protagonista.getX();
        int y = protagonista.getY();
        int nx = x + dx;
        int ny = y + dy;
        if (!enLimites(nx, ny) || mapa[nx][ny].esMuro()) return false;

        Personaje obj = mapa[nx][ny].getOcupante();
        if (obj instanceof Enemigo) {
            atacar(protagonista, obj);
        } else {
            mapa[x][y].setOcupante(null);
            mapa[nx][ny].setOcupante(protagonista);
            protagonista.setPosicion(nx, ny);
        }
        return true;
    }

    /**
     * Avanza al siguiente turno, ejecuta acción de enemigo tras pausa si aplica.
     */
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
        if (personajeActual instanceof Enemigo e) {
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(evt -> {
                accionEnemigo(e);
                verificarFin();
                notificarEscuchas();
                turnoSiguiente();
            });
            pause.play();
        }
    }

    /**
     * Prepara la cola de turnos ordenando personajes por velocidad.
     */
    private void prepararTurnos() {
        colaTurnos.clear();
        List<Personaje> all = new ArrayList<>();
        all.add(protagonista);
        all.addAll(enemigos);
        all.sort((a, b) -> Integer.compare(b.getVelocidad(), a.getVelocidad()));
        colaTurnos.addAll(all);
    }

    /**
     * Comprueba condiciones de fin de juego: derrota o eliminación de enemigos.
     */
    public void verificarFin() {
        enemigos.removeIf(e -> e.getSalud() <= 0);
        if (protagonista.getSalud() <= 0) notificarFin(false);
        else if (enemigos.isEmpty())    notificarFin(true);
    }

    /**
     * Lógica de acción de un enemigo: movimiento o ataque según visión.
     * @param e Enemigo que actúa.
     */
    private void accionEnemigo(Enemigo e) {
        int ex = e.getX();
        int ey = e.getY();
        int px = protagonista.getX();
        int py = protagonista.getY();
        int dx = Integer.compare(px, ex);
        int dy = Integer.compare(py, ey);
        int dist = Math.abs(px - ex) + Math.abs(py - ey);
        int nx = ex + dx;
        int ny = ey + dy;

        if (dist <= e.getVision() && enLimites(nx, ny)) {
            if (mapa[nx][ny].getOcupante() instanceof Prota) atacar(e, protagonista);
            else if (!mapa[nx][ny].esMuro() && mapa[nx][ny].getOcupante() == null) {
                mapa[ex][ey].setOcupante(null);
                e.setPosicion(nx, ny);
                mapa[nx][ny].setOcupante(e);
            }
        } else {
            moverAleatoriamente(e);
        }
    }

    /**
     * Mueve un enemigo en dirección aleatoria válida.
     * @param e Enemigo a mover.
     */
    private void moverAleatoriamente(Enemigo e) {
        int ex = e.getX();
        int ey = e.getY();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        List<int[]> opts = Arrays.asList(dirs);
        Collections.shuffle(opts);
        for (int[] d : opts) {
            int nx = ex + d[0];
            int ny = ey + d[1];
            if (enLimites(nx, ny) && !mapa[nx][ny].esMuro() && mapa[nx][ny].getOcupante()==null) {
                mapa[ex][ey].setOcupante(null);
                e.setPosicion(nx, ny);
                mapa[nx][ny].setOcupante(e);
                break;
            }
        }
    }

    /**
     * Realiza un ataque entre dos personajes y actualiza salud.
     * @param atk Atacante.
     * @param def Defensor.
     */
    private void atacar(Personaje atk, Personaje def) {
        int danio = Math.max(1, atk.getAtaque() - def.getDefensa());
        def.setSalud(def.getSalud() - danio);
        if (def.getSalud() <= 0) {
            mapa[def.getX()][def.getY()].setOcupante(null);
        }
    }

    /**
     * Comprueba si unas coordenadas están dentro de los límites del mapa.
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return True si dentro del mapa.
     */
    private boolean enLimites(int x, int y) {
        return x >= 0 && y >= 0 && x < mapa.length && y < mapa[0].length;
    }
}