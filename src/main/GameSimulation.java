package main;

import main.configurazione.Configurazione;
import main.gui.FinestraConfigurazione;
import main.gui.FinestraPrincipale;
import main.observer.ButtonObserver;
import main.observer.ConfigurationObserver;
import main.observer.LabelsObserver;
import main.observer.subject.ConfigurationButtonSubject;
import main.observer.subject.LabelsButtonSubject;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class GameSimulation {
    private Configurazione conf;
    private Tabellone tabellone;

    public void start() {
        //TODO: finestra inziale in cui poter caricare una partita
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
        Giocatore vincitore;
        boolean running = true;
        while (running) {
            if (conf.isAutomatico()) {
            }
            else {
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
                    //salva(t);
                    if (!running) break;
                }
            }
        }
        //TODO: azioni di fine partita (salvataggio della configurazione?)
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

    public static void main(String[] args) {
        GameSimulation g = new GameSimulation();
        g.start();
    }
}
