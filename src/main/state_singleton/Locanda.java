package main.state_singleton;

import main.Giocatore;

//Non posso costruire Locanda come un Singleton perché più giocatori potrebbero trovarsi
//contemporareamente in questo stato e per ognuno di essi va tenuta memoria del numero di
//turni in cui sono fermi
public class Locanda implements State {
    //Ogni volta che lo stato di un giocatore diventa "Locanda" creo un oggetto Locanda diverso
    private int count=3;

    @Override
    public boolean handleSostaReq(Giocatore g) {
        if (g.isDivietoDiSosta()) {
            g.setStato(Normale.INSTANCE);
            g.setDivietoDiSosta(false);
            return true;
        }
        count--;
        if (count == 0) {
            g.setStato(Normale.INSTANCE);
            //se ho azzerato il count in questo turno, al prossimo turno g si muove normalmente
        }
        return false;
    }

    @Override
    public Premio handlePremioReq(Giocatore g) {
        return Premio.NESSUNO;
    }
}
