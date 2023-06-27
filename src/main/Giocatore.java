package main;

import main.collegamento.Posizione;
import main.state_singleton.Normale;
import main.state_singleton.State;

public class Giocatore {

    private int id;
    private Posizione pos;
    private int casella;
    private State stato;
    private boolean divietoDiSosta;

    public Giocatore(int id) {
        this.id = id;
        pos = new Posizione(0,0);
        casella = 1;
        stato = Normale.INSTANCE;
    }

    public int getId() {
        return id;
    }

    public Posizione getPos() {
        return pos;
    }

    public int getCasella() {
        return casella;
    }

    public void setPos(Posizione pos) {
        this.pos.setX(pos.getX());
        this.pos.setY(pos.getY());
    }

    public State getStato() {
        return stato;
    }

    public void setStato(State stato) {
        this.stato = stato;
    }

    public boolean isDivietoDiSosta() {
        return divietoDiSosta;
    }

    public void setDivietoDiSosta(boolean divietoDiSosta) {
        this.divietoDiSosta = divietoDiSosta;
    }

    public boolean sostaReq() {
        return stato.handleSostaReq(this);
    }

    public State.Premio premioReq() {
        return stato.handlePremioReq(this);
    }

    public void setCasella(int casella) {
        this.casella = casella;
    }
}
