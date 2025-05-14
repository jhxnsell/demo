package com.mazmorron.modelo;

/**
 * Clase que representa al protagonista del juego.
 * Extiende Personaje sin atributos adicionales.
 */
public class Prota extends Personaje {
    /**
     * Constructor del protagonista con estadísticas establecidas.
     * @param nombre Nombre del protagonista.
     * @param salud Puntos de vida.
     * @param ataque Valor de ataque.
     * @param defensa Valor de defensa.
     * @param velocidad Valor de velocidad.
     */
    public Prota(String nombre, int salud, int ataque, int defensa, int velocidad) {
        super(nombre, salud, ataque, defensa, velocidad);
    }
}
