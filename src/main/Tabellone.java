package main;

import main.collegamento.Posizione;
import main.collegamento.Scala;
import main.collegamento.Serpente;

import java.util.HashMap;
import java.util.Map;

public class Tabellone {
    private static final double RATE = 0.08; //rateo di generazione di scale/serpenti/caselle speciali rispetto alle dimensioni della matrice
    private final Configurazione conf;
    private boolean[][] collegamentiSpeciali;
    private final int n,m,numCollegamenti,numGiocatori,numSpeciali;
    private Serpente[] serpenti;
    private Scala[] scale;
    private Giocatore[] giocatori;
    private Map<CasellaSpeciale.Tipo,CasellaSpeciale[]> caselleSpeciali;

    public Tabellone(Configurazione conf) {
        this.conf = conf;
        n = conf.getRighe();
        m = conf.getColonne();
        numCollegamenti = (int) Math.rint((n*m)*RATE);
        numGiocatori = conf.getNumGiocatori();
        numSpeciali = numCollegamenti-2;
        collegamentiSpeciali = new boolean[n][m];
        serpenti = new Serpente[numCollegamenti];
        scale = new Scala[numCollegamenti];
        giocatori = new Giocatore[numGiocatori];
        caselleSpeciali = new HashMap<>(); //inizializzo gli array se c'è bisogno
    }

    public void init() {
        int num_prog1=0, num_prog2=1;
        for (int i = 0; i<numCollegamenti; i++) {
            Posizione[] pos = generateSL();
            Scala sc = new Scala(num_prog1,pos[0],pos[1]);
            num_prog1+=2;
            scale[i] = sc;
            pos = generateSL();
            Serpente se = new Serpente(num_prog2,pos[0],pos[1]);
            num_prog2+=2;
            serpenti[i] = se;
        }
        if (conf.isCasellePremio()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.DADI;
            caselleSpeciali.put(t,new CasellaSpeciale[numSpeciali]); //inizializzo l'array
            for (int i = 0; i < numSpeciali; i++) {
                Posizione pos = generateSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t,pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
            t = CasellaSpeciale.Tipo.MOLLA;
            caselleSpeciali.put(t,new CasellaSpeciale[numSpeciali]); //inizializzo l'array
            for (int i = 0; i < numSpeciali; i++) {
                Posizione pos = generateSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t,pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
        if (conf.isCaselleSosta()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.PANCHINA;
            caselleSpeciali.put(t,new CasellaSpeciale[numSpeciali]); //inizializzo l'array
            for (int i = 0; i < numSpeciali; i++) {
                Posizione pos = generateSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t,pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
            t = CasellaSpeciale.Tipo.LOCANDA;
            caselleSpeciali.put(t,new CasellaSpeciale[numSpeciali]); //inizializzo l'array
            for (int i = 0; i < numSpeciali; i++) {
                Posizione pos = generateSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t,pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
        if (conf.isPescaCarta()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.PESCA;
            caselleSpeciali.put(t,new CasellaSpeciale[numSpeciali]); //inizializzo l'array
            for (int i = 0; i < numSpeciali; i++) {
                Posizione pos = generateSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t,pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
    }

    private Posizione[] generateSL() {
        boolean trovato1 = false;
        Posizione[] p = new Posizione[2];
        while (!trovato1) {
            int bX = (int) Math.rint(Math.random()*(n-2))+1; //deve essere almeno 1 per il costruttore di Scala e Serpente
            int bY = (int) Math.rint(Math.random()*(m-1));
            if (!collegamentiSpeciali[bX][bY] && bY != 0) {
                collegamentiSpeciali[bX][bY] = trovato1 = true;
                Posizione b = new Posizione(bX,bY);
                boolean trovato2 = false;
                while (!trovato2) {
                    int tX = (int) Math.rint(Math.random()*(n-1));
                    int tY = (int) Math.rint(Math.random()*(m-1));
                    Posizione t = new Posizione(tX,tY);
                    if (!t.equals(b) && tX < bX && !collegamentiSpeciali[tX][tY] && (tX != 0 || tY != 0)) {
                        collegamentiSpeciali[tX][tY] = trovato2 = true;
                        p[0] = t;
                        p[1] = b;
                    }
                }
            }
        }
        return p;
    }

    private Posizione generateSpeciali() {
        Posizione ris = null;
        boolean trovato = false;
        while (!trovato) {
            int x = (int) Math.rint(Math.random()*(n-1));
            int y = (int) Math.rint(Math.random()*(m-1));
            if (!(x == 0 && y == 0) && !(x == m-1 && y == 0) && !(x == 0 && y == m-1) && !collegamentiSpeciali[x][y]) {
                ris = new Posizione(x, y);
                collegamentiSpeciali[x][y] = trovato = true;
            }
        }
        return ris;
    }

    public void move(int giocatore, int casella, Posizione pos) {
        Giocatore target = null;
        for (Giocatore g:giocatori) {
            if (g.getId() == giocatore) {
                target = g;
                break;
            }
        }
        if (target == null || pos.getX() >= n || pos.getY() >= m) {
            throw new IllegalArgumentException();
        }
        Posizione cur = target.getPos();
        target.setPos(pos);
        target.setCasella(casella);
    }

    public Configurazione getConf() {
        return conf;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getNumGiocatori() {
        return numGiocatori;
    }

    public int getNumCollegamenti() {
        return numCollegamenti;
    }

    public Giocatore[] getGiocatori() {
        return giocatori;
    }

    public Scala[] getScale() {
        return scale;
    }

    public Serpente[] getSerpenti() {
        return serpenti;
    }

    public Map<CasellaSpeciale.Tipo, CasellaSpeciale[]> getCaselleSpeciali() {
        return caselleSpeciali;
    }

    public static int validaCasella(int casella, int lancio, int ultimaCasella) {
        int nuovaCasella;
        if (casella + lancio < ultimaCasella) {
            nuovaCasella = casella + lancio;
        } else if (casella + lancio > ultimaCasella) { //superiamo la fine e torniamo indietro
            int offset = ultimaCasella - casella;
            int fallBack = lancio - offset;
            nuovaCasella = ultimaCasella - fallBack;
        } else {
            nuovaCasella = ultimaCasella;
        }
        return nuovaCasella;
    }

}