package main.state_singleton;

import main.Giocatore;

public enum Normale implements State {
    INSTANCE;

    @Override
    public boolean handleSostaReq(Giocatore g) {
        return true;
    }

}
