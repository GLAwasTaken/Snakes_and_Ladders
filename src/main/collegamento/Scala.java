package main.collegamento;

import main.Giocatore;

public class Scala extends Collegamento {

    private int id;

    public Scala(int id, int cimaX, int cimaY, int baseX, int baseY) {
        if (cimaX <= baseX) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        top = new Posizione(cimaX,cimaY);
        bottom = new Posizione(baseX,baseY);
    }

    public Scala(int id, Posizione top, Posizione bottom) {
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
        g.setPos(top);
    }
}
