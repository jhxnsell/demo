package com.mazmorron.modelo;

public abstract class Personaje {
    protected String nombre;
    protected int salud, ataque, defensa, velocidad;
    protected int x, y;

    public Personaje(String nombre, int salud, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    public String getNombre() { return nombre; }
    public int getSalud() { return salud; }
    public void setSalud(int salud) { this.salud = salud; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }
}