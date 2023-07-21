package main.state_singleton;

import main.Giocatore;

public interface State {
    boolean handleSostaReq(Giocatore g); //true se si può muovere, false altrimenti
}
