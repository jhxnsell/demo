package com.mazmorron.modelo;

/**
 * Clase que representa a un enemigo en el juego.
 * Extiende Personaje e incluye visión para detección.
 */
public class Enemigo extends Personaje {
    private int vision;

    /**
     * Crea un enemigo con estadísticas y rango de visión.
     * @param nombre Nombre del enemigo.
     * @param salud Puntos de vida.
     * @param ataque Valor de ataque.
     * @param defensa Valor de defensa.
     * @param velocidad Valor de velocidad.
     * @param vision Rango de detección.
     */
    public Enemigo(String nombre, int salud, int ataque, int defensa, int velocidad, int vision) {
        super(nombre, salud, ataque, defensa, velocidad);
        this.vision = vision;
    }

    /** @return Distancia máxima de detección. */
    public int getVision() { return vision; }
}
