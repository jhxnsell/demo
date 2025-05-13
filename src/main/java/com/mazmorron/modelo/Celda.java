package com.mazmorron.modelo;

public class Celda {
    private boolean muro;
    private Personaje ocupante;

    public Celda(boolean muro) {this.muro = muro;}
    public boolean esMuro() {return muro;}
    public Personaje getOcupante() {return ocupante;}
    public void setOcupante(Personaje p) {this.ocupante = p;}
}