package main.collegamento;

import main.Giocatore;

public class Serpente extends Collegamento {
    private int id;
    public Serpente(int id, int testaX, int testaY, int codaX, int codaY) {
        if (testaX <= codaX) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        top = new Posizione(testaX,testaY);
        bottom = new Posizione(codaX,codaY);
    }

    public Serpente(int id, Posizione top, Posizione bottom) {
        if (top.getX() >= bottom.getX()) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.top = new Posizione(top.getX(),top.getY());
        this.bottom = new Posizione(bottom.getX(),bottom.getY());
    }

    public int getId() {
        return id;
    }

    @Override
    public void percorri(Giocatore g) {
        g.setPos(bottom);
    }
}
