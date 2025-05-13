package com.mazmorron.modelo;

public class Enemigo extends Personaje {
    private int vision;

    public Enemigo(String nombre, int salud, int ataque, int defensa, int velocidad, int vision) {
        super(nombre, salud, ataque, defensa, velocidad);
        this.vision = vision;
    }

    public int getVision() {return vision;}
}