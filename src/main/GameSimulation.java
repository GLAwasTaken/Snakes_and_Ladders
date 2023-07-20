package main;

import main.caselle_speciali.CasellaSpeciale;
import main.collegamento.Posizione;
import main.collegamento.Scala;
import main.collegamento.Serpente;
import main.configurazione.Configurazione;
import main.gui.FinestraConfigurazione;
import main.gui.FinestraPrincipale;
import main.observer.ButtonObserver;
import main.observer.ConfigurationObserver;
import main.observer.LabelsObserver;
import main.observer.subject.ConfigurationButtonSubject;
import main.observer.subject.LabelsButtonSubject;
import main.state_singleton.Locanda;
import main.state_singleton.Panchina;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class GameSimulation {
    private Configurazione conf;
    private Tabellone tabellone;

    /*
        PREMESSA:
        Sono state volutamente lasciate le stampe su terminale (nel caso di partita non automatica)
        in questo ed in altri codici per facilitare la comprensione di ciò che succede nei turni di
        gioco, anche se è possibile giocare anche senza.

        Una "soluzione intermedia" sarebbe stata creare un file di "storico partita" anche per le
        partite non automatiche, ma ciò non è stato implementato perché:
            1. lo storico sarebbe stato visualizzabile solo a fine partita
               (altrimenti accessi intermedi avrebbero potuto causare corruzione del file ed eccezioni indesiderate)
            2. a mio parere non avrebbe più avuto senso avere la possibilità di una partita non automatica
     */

    public void start() {
        ConfigurationButtonSubject submit = new ConfigurationButtonSubject(new JButton("Submit"));
        Semaphore mutexConf = new Semaphore(0); //per la sincronizzazione
        FinestraConfigurazione fc = new FinestraConfigurazione(submit,mutexConf);
        fc.init();
        ConfigurationObserver o1 = new ConfigurationObserver(submit);
        submit.attach(o1);
        if (o1.getState() == ButtonObserver.State.NOT_PRESSED) {
            try {
                mutexConf.acquire();
            } catch (InterruptedException e) {
                System.out.println("Interruzione indesiderata");
            }
        }
        conf = o1.getConf();
        tabellone = new Tabellone(conf);
        tabellone.init();
        Giocatore vincitore;
        boolean running = true;

        if (conf.isAutomatico()) {
            JFileChooser chooser = new JFileChooser();
            JOptionPane.showMessageDialog(chooser,"Scegliere il file (.txt) nel quale salvare lo storico della partita");
            File storico = null;
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                storico = chooser.getSelectedFile();
            }
            if (storico != null) {
                try {
                    Thread messaggio = new Thread(() -> {
                        String title = "Ti verrà comunicata la fine della partita";
                        String msg = "Non aprire il file dello storico prima che la partita finisca.";
                        JOptionPane.showMessageDialog(chooser,msg,title,JOptionPane.INFORMATION_MESSAGE);
                    }); //uso un thread a parte per evitare che l'esecuzione si blocchi se l'osservatore non clicca OK
                    messaggio.start();
                    PrintWriter out = new PrintWriter(new FileOutputStream(storico),true);
                    while (running) {
                        for (int i = 0; i<tabellone.getNumGiocatori(); i++) {
                            Giocatore cur = tabellone.getGiocatori()[i];
                            out.println("Turno di p"+(cur.getId()+1));

                            int oldCasella = cur.getCasella();
                            int lancio = calcolaLancio(cur);
                            int casella = Tabellone.validaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                            if (conf.isDoppioSei() && lancio == 12) {
                                out.println("Doppio 6! Il giocatore p"+(cur.getId()+1)+" tira di nuovo i dadi");
                                lancio = calcolaLancio(cur);
                                casella = Tabellone.validaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                            }
                            out.println("Numero estratto: "+lancio);
                            out.println("Il giocatore p"+(cur.getId()+1)+" si muove da "+oldCasella+" a "+casella+"\n");

                            Posizione newPos = getPos(casella);
                            tabellone.move(cur.getId(),casella,newPos);

                            if (lancio > 0) {
                                controlloCasellaSpeciale(cur,lancio,newPos,out);
                            }

                            if (hasWon(cur)) {
                                vincitore = cur;
                                running = false;
                                out.println("Il giocatore p"+(vincitore.getId()+1)+" ha vinto");
                                JOptionPane.showMessageDialog(chooser,"La partita è termintata, ora puoi visualizzare lo storico!");
                                break;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File non trovato");
                }
            }
        }
        else {
            LabelsButtonSubject roll = new LabelsButtonSubject(new JButton());
            if (tabellone.getConf().isDadoSingolo()) {
                roll.getSubject().setText("Tira il dado");
            } else {
                roll.getSubject().setText("Tira i dadi");
            }
            Semaphore mutexMain = new Semaphore(0); //per la sincronizzazione
            FinestraPrincipale fp = new FinestraPrincipale(tabellone,roll,mutexMain);
            fp.init();
            LabelsObserver o2 = new LabelsObserver(roll);
            roll.attach(o2);
            while (running) {
                for (int i=0; i<tabellone.getNumGiocatori(); i++) {
                    Giocatore cur = tabellone.getGiocatori()[i];
                    if (o2.getState() == ButtonObserver.State.NOT_PRESSED) {
                        try {
                            mutexMain.acquire();
                        } catch (InterruptedException e) {
                            System.out.println("Interruzione indesiderata");
                        }
                    }
                    int lancio = calcolaLancio(cur);
                    int casella = Tabellone.validaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                    if (conf.isDoppioSei() && lancio == 12) {
                        fp.move(cur.getId(),casella);
                        lancio = calcolaLancio(cur);
                        casella = Tabellone.validaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                    }
                    System.out.println("Il giocatore p"+(cur.getId()+1)+" si muove da "+cur.getCasella()+" a "+casella);
                    fp.move(cur.getId(),casella); //muovo il giocatore
                    if (hasWon(cur)) {
                        vincitore = cur;
                        running = false;
                        System.out.println("Il giocatore p"+(vincitore.getId()+1)+" ha vinto");
                        fp.mostraVincitore(cur);
                    }
                    else {
                        //modifico il testo delle label in base al prossimo giocatore
                        fp.nextTurn(getNewLabels(cur,lancio,o2.getLabels()));
                    }
                }
            }
        }
    }

    private int calcolaLancio(Giocatore g) {
        if (g.sostaReq()) {
            //genera un numero casuale (basato sul numero di dadi e sulla configurazione)
            int casella = g.getCasella();
            int ultimaCasella = tabellone.getN() * tabellone.getM();
            int lancio = ((int) Math.rint((Math.random() * 5))) + 1;
            if (!conf.isDadoSingolo() && (casella < ultimaCasella - 6 || !conf.isLancioUnico())) {
                lancio += ((int) Math.rint((Math.random() * 5))) + 1;
            }
            return lancio;
        }
        return 0; //lo stato del giocatore impone di stare fermi
    }

    private boolean hasWon(Giocatore g) {
        return g.getCasella() == tabellone.getN()*tabellone.getM();
    }

    private String[] getNewLabels(Giocatore g, int lancio, Label[] lables) {
        String l1 = lables[0].getText();
        int i = g.getId();
        i = (i+1) % tabellone.getNumGiocatori();
        l1 = l1.substring(0,l1.length()-1)+(i+1);

        String l2 = lables[1].getText();
        l2 = l2.substring(0,l2.indexOf(':')+1)+lancio;

        return new String[]{l1,l2};
    }

    private Posizione getPos(int casella) {
        int id = tabellone.getN()* tabellone.getM();
        if ((tabellone.getN()-1)%2 == 0) {
            id -=  tabellone.getM()-1;
        }
        for (int i = 0; i<tabellone.getN(); i++) {
            boolean verso; //true (-->), false (<--)
            //scegliamo il verso di ogni riga in base al numero di righe totali (dato che iniziamo da quella più in alto)
            if (tabellone.getN() % 2 == 0) {
                verso = i % 2 != 0;
            } else {
                verso = i % 2 == 0;
            }
            for (int j = 0; j< tabellone.getM(); j++) {
                if (id == casella) {
                    return new Posizione(i,j);
                }
                if (verso) {
                    id++;
                } else {
                    id--;
                }
            }
            if (!verso) {
                id -=  tabellone.getM()-1;
            }
            else {
                id -=  tabellone.getM()+1;
            } //per far ricominciare il conteggio dalla casella sotto
        }
        return null;
    }

    private int getCasella(Posizione pos) {
        int id = tabellone.getN()* tabellone.getM();
        if ((tabellone.getN()-1)%2 == 0) {
            id -=  tabellone.getM()-1;
        }
        for (int i = 0; i<tabellone.getN(); i++) {
            boolean verso; //true (-->), false (<--)
            //scegliamo il verso di ogni riga in base al numero di righe totali (dato che iniziamo da quella più in alto)
            if (tabellone.getN() % 2 == 0) {
                verso = i % 2 != 0;
            } else {
                verso = i % 2 == 0;
            }
            for (int j = 0; j< tabellone.getM(); j++) {
                if (i == pos.getX() && j == pos.getY()) {
                    return id;
                }
                if (verso) {
                    id++;
                } else {
                    id--;
                }
            }
            if (!verso) {
                id -=  tabellone.getM()-1;
            }
            else {
                id -=  tabellone.getM()+1;
            } //per far ricominciare il conteggio dalla casella sotto
        }
        return 0;
    }

    private void controlloCasellaSpeciale(Giocatore cur, int lancio, Posizione newPos, PrintWriter out) {
        boolean trovato = false;

        for (Scala s:tabellone.getScale()) {
            if (s.getBottom().equals(newPos)) {
                trovato = true;

                int nuova = getCasella(s.getTop());
                out.println("Il giocatore p"+(cur.getId()+1)+" è finito su una SCALA, arriva alla casella "+nuova+"\n");
                tabellone.move(cur.getId(),nuova,s.getTop());
            }
        }

        if (!trovato) {
            for (Serpente s : tabellone.getSerpenti()) {
                if (s.getTop().equals(newPos)) {
                    trovato = true;

                    int nuova = getCasella(s.getBottom());
                    out.println("Il giocatore p" + (cur.getId() + 1) + " è finito su un SERPENTE, ritorna alla casella "+nuova+"\n");
                    tabellone.move(cur.getId(),nuova,s.getBottom());
                }
            }
        }

        if (!trovato) {
            for (CasellaSpeciale.Tipo t : tabellone.getCaselleSpeciali().keySet()) {
                for (CasellaSpeciale c : tabellone.getCaselleSpeciali().get(t)) {
                    System.out.println(c.getPos() + " " + newPos);
                    if (c.getPos().equals(newPos)) {
                        out.println("Il giocatore p" + (cur.getId() + 1) + " è finito su " + t.name() + "\n");
                        performAction(t, cur, lancio, out);
                    }
                }
            }
        }
    }

    private void performAction(CasellaSpeciale.Tipo t, Giocatore cur, int lancio, PrintWriter out) {
        switch (t) {
            case PANCHINA -> cur.setStato(Panchina.INSTANCE);
            case LOCANDA -> cur.setStato(new Locanda());
            case DIVIETO -> cur.setDivietoDiSosta(true);
            case DADI -> {
                int newLancio = calcolaLancio(cur);
                int nuovaCasella = Tabellone.validaCasella(cur.getCasella(),newLancio,tabellone.getN()*tabellone.getM());
                out.println("Il giocatore p"+(cur.getId()+1)+" tira di nuovo i dadi");
                if (conf.isDoppioSei() && newLancio == 12) {
                    out.println("Doppio 6! Il giocatore p"+(cur.getId()+1)+" tira di nuovo i dadi");
                    newLancio = calcolaLancio(cur);
                    nuovaCasella = Tabellone.validaCasella(cur.getCasella(),newLancio,tabellone.getN()*tabellone.getM());
                }
                out.println("Numero estratto: "+newLancio);
                out.println("Il giocatore p"+(cur.getId()+1)+" si muove da "+cur.getCasella()+" a "+nuovaCasella+"\n");

                Posizione nuovaPos = getPos(nuovaCasella);
                tabellone.move(cur.getId(),nuovaCasella,nuovaPos);

                controlloCasellaSpeciale(cur,newLancio,nuovaPos,out);
            }
            case MOLLA -> {
                int nuovaCasella = Tabellone.validaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                out.println("Il giocatore p"+(cur.getId()+1)+" avanza di altre "+lancio+" caselle, arriva alla casella "+nuovaCasella+"\n");
                Posizione nuovaPos = getPos(nuovaCasella);
                tabellone.move(cur.getId(),nuovaCasella,nuovaPos);

                controlloCasellaSpeciale(cur,lancio,nuovaPos,out);
            }
            case PESCA -> {
                int i;
                if (tabellone.getConf().isUlterioriCarte()) {
                    i = (int) Math.rint(Math.random() * 4); //con 4 escludo l'elemento PESCA
                }
                else {
                    i = (int) Math.rint(Math.random() * 3); //con 3 escludo DIVIETO e PESCA
                }
                CasellaSpeciale.Tipo cartaPescata = CasellaSpeciale.Tipo.values()[i];
                out.println("Il giocatore pesca: "+cartaPescata);
                performAction(cartaPescata,cur,lancio,out);
            }
        }
    }

    public static void main(String[] args) {
        GameSimulation g = new GameSimulation();
        g.start();
    }
}
