package main;

import main.collegamento.Posizione;

public class CasellaSpeciale {
    public enum Tipo {PANCHINA, LOCANDA, DADI, MOLLA, DIVIETO, PESCA}

    private final Tipo tipo;

    private final Posizione pos;

    public CasellaSpeciale(Tipo tipo, Posizione pos) {
        this.tipo = tipo;
        this.pos = pos;
    }

    public Posizione getPos() {
        return pos;
    }
}
