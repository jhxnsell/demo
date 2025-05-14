package com.mazmorron.modelo;

/**
 * Representa una celda del mapa.
 * Puede ser un muro o contener un personaje.
 */
public class Celda {
    private boolean muro;
    private Personaje ocupante;

    /**
     * Constructor que establece si la celda es muro.
     * @param muro True si es un muro.
     */
    public Celda(boolean muro) { this.muro = muro; }

    /** @return True si es muro. */
    public boolean esMuro() { return muro; }

    /** @return Personaje que ocupa la celda, o null si está vacía. */
    public Personaje getOcupante() { return ocupante; }

    /**
     * Asigna un personaje a la celda.
     * @param p Personaje a colocar.
     */
    public void setOcupante(Personaje p) { this.ocupante = p; }
}
