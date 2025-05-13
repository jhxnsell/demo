// src/main/java/com/mazmorron/modelo/ModeloJuego.java
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
        for (EscuchaModelo e : escuchas) e.alCambiarModelo();
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

    public int getTurnoActual() {
        return turnoActual;
    }

    public void setAccionFin(Runnable accion) {
        this.accionFin = accion;
    }

    private void notificarFin(boolean victoria) {
        if (accionFin != null) accionFin.run();
    }

    public void cargarMapaDesde(InputStream in) {
        if (in == null) throw new IllegalArgumentException("Mapa: recurso no encontrado.");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            List<String> lines = new ArrayList<>();
            String line; int maxC = 0;
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
                int x = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
                e.setPosicion(x, y);
                enemigos.add(e);
                mapa[x][y].setOcupante(e);
            }
            notificarEscuchas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean moverProtagonista(int dx, int dy) {
        if (!(personajeActual instanceof Prota)) return false;
        int x = protagonista.getX(), y = protagonista.getY();
        int nx = x + dx, ny = y + dy;
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

    private void prepararTurnos() {
        colaTurnos.clear();
        List<Personaje> all = new ArrayList<>();
        all.add(protagonista);
        all.addAll(enemigos);
        all.sort((a, b) -> Integer.compare(b.getVelocidad(), a.getVelocidad()));
        colaTurnos.addAll(all);
    }

    public void verificarFin() {
        enemigos.removeIf(e -> e.getSalud() <= 0);
        if (protagonista.getSalud() <= 0) notificarFin(false);
        else if (enemigos.isEmpty())    notificarFin(true);
    }

    private void accionEnemigo(Enemigo e) {
        int ex = e.getX(), ey = e.getY();
        int px = protagonista.getX(), py = protagonista.getY();
        int dx = Integer.compare(px, ex), dy = Integer.compare(py, ey);
        int dist = Math.abs(px - ex) + Math.abs(py - ey);
        int nx = ex + dx, ny = ey + dy;

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

    private void moverAleatoriamente(Enemigo e) {
        int ex = e.getX(), ey = e.getY();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        List<int[]> opts = Arrays.asList(dirs);
        Collections.shuffle(opts);
        for (int[] d : opts) {
            int nx = ex + d[0], ny = ey + d[1];
            if (enLimites(nx, ny) && !mapa[nx][ny].esMuro() && mapa[nx][ny].getOcupante()==null) {
                mapa[ex][ey].setOcupante(null);
                e.setPosicion(nx, ny);
                mapa[nx][ny].setOcupante(e);
                break;
            }
        }
    }

    private void atacar(Personaje atk, Personaje def) {
        int danio = Math.max(1, atk.getAtaque() - def.getDefensa());
        def.setSalud(def.getSalud() - danio);
        if (def.getSalud() <= 0) {
            mapa[def.getX()][def.getY()].setOcupante(null);
        }
    }

    private boolean enLimites(int x, int y) {
        return x >= 0 && y >= 0 && x < mapa.length && y < mapa[0].length;
    }
}