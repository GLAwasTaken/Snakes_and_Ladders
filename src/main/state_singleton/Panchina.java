package main.state_singleton;

import main.Giocatore;

//TODO AGGIUSTARE PANCHINA O LOCANDA
public enum Panchina implements State {
    INSTANCE;

    @Override
    public boolean handleSostaReq(Giocatore g) {
        if (g.isDivietoDiSosta()) {
            g.setStato(Normale.INSTANCE);
            g.setDivietoDiSosta(false);
            return true;
        }
        g.setStato(Normale.INSTANCE);
        return false;
    }

    @Override
    public Premio handlePremioReq(Giocatore g) {
        return Premio.NESSUNO;
    }
}
