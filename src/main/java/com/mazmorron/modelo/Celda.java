package com.mazmorron.modelo;

/**
 * Representa una celda del mapa.
 * Puede ser un muro, suelo o una trampa.
 */
public class Celda {
    public enum TipoCelda {
        SUELO, MURO, TRAMPA
    }

    private TipoCelda tipo;
    private Personaje ocupante;
    
    /**
     * Constructor que establece el tipo de celda.
     * @param muro Tipo de la celda (SUELO, MURO, TRAMPA).
     */
    public Celda(boolean muro) {
        this.tipo = muro ? TipoCelda.TRAMPA : TipoCelda.SUELO;
    }
    public Celda(TipoCelda suelo) {
        this.tipo = suelo;
    }
    /** @return True si es muro. */
    public boolean esMuro() {
        return tipo == TipoCelda.MURO;
    }

    /** @return Personaje que ocupa la celda, o null si está vacía. */
    public Personaje getOcupante() {
        return ocupante;
    }

    public void setOcupante(Personaje p) {
        this.ocupante = p;
        if (tipo == TipoCelda.TRAMPA && p != null) {
            p.recibirDanio(10);
        }
    }

    /** @return Tipo de la celda. */
    public TipoCelda getTipo() {
        return tipo;
    }
}
