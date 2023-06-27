package main.caselle_speciali;

import main.collegamento.Posizione;

public class CasellaSpeciale {
    public enum Tipo {PANCHINA, LOCANDA, DADI, MOLLA, DIVIETO, PESCA}

    private final Tipo tipo;

    private Posizione pos;

    public CasellaSpeciale(Tipo tipo, Posizione pos) {
        this.tipo = tipo;
        this.pos = pos;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Posizione getPos() {
        return pos;
    }

    public void setPos(Posizione pos) {
        this.pos = pos;
    }
}
