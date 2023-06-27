package main.collegamento;

import main.Giocatore;

public abstract class Collegamento {
    protected Posizione top, bottom;

    public abstract void percorri(Giocatore g);

    public Posizione getTop() {
        return top;
    }

    public Posizione getBottom() {
        return bottom;
    }

    public String toString() {
        return "[Top="+top+", Bottom="+bottom+"]";
    }
}
