package com.mazmorron.modelo;

/**
 * Clase abstracta que define atributos y comportamiento común de los personajes.
 */
public abstract class Personaje {
    protected String nombre;
    protected int salud, ataque, defensa, velocidad;
    protected int x, y;

    /**
     * Constructor de personaje con estadísticas básicas.
     * @param nombre Nombre del personaje.
     * @param salud Puntos de vida.
     * @param ataque Valor de ataque.
     * @param defensa Valor de defensa.
     * @param velocidad Valor de velocidad.
     */
    public Personaje(String nombre, int salud, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    /** @return Nombre del personaje. */
    public String getNombre() { return nombre; }

    /** @return Puntos de salud. */
    public int getSalud() { return salud; }

    /** @param salud Nuevo valor de salud. */
    public void setSalud(int salud) { this.salud = salud; }

    /** @return Valor de ataque. */
    public int getAtaque() { return ataque; }

    /** @return Valor de defensa. */
    public int getDefensa() { return defensa; }

    /** @return Valor de velocidad. */
    public int getVelocidad() { return velocidad; }

    /** @return Coordenada X en el mapa. */
    public int getX() { return x; }

    /** @return Coordenada Y en el mapa. */
    public int getY() { return y; }

    /**
     * Establece la posición del personaje en el mapa.
     * @param x Coordenada X.
     * @param y Coordenada Y.
     */
    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }
}