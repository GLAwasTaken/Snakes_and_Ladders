package main.gui;

import main.Giocatore;
import main.Tabellone;
import main.CasellaSpeciale;
import main.collegamento.Posizione;
import main.collegamento.Scala;
import main.collegamento.Serpente;
import main.observer.subject.LabelsButtonSubject;
import main.state_singleton.Locanda;
import main.state_singleton.Panchina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class FinestraPrincipale {
    enum Colore { GIALLO, BIANCO, ROSSO, BLU, VERDE, ARANCIO, VIOLA, CIANO, ROSA, NERO, GRIGIO_C, GRIGIO_S }
    private static final Colore[] COLOR_ROTATION = {Colore.GIALLO,Colore.BIANCO,Colore.ROSSO,Colore.BLU,Colore.VERDE};
    private JFrame finestra;
    private Casella[] caselle;
    private Ladder[] scale;
    private Snake[] serpenti;
    private ArrayList<Pedina> pedine;
    private final Tabellone tabellone;
    private final int r,c;
    private LabelsButtonSubject roll;
    private Semaphore mutex;

    public FinestraPrincipale(Tabellone tabellone, LabelsButtonSubject roll, Semaphore mutex) {
        this.tabellone = tabellone;
        r = tabellone.getN();
        c = tabellone.getM();
        this.roll = roll;
        this.mutex = mutex;
    }

    public void init() {
        finestra = new JFrame();
        finestra.setTitle("Scale e Serpenti");
        finestra.setBounds(400,50,600,620);
        finestra.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        finestra.setLayout(new BorderLayout());

        finestra.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String msg = "La partita corrente verrà persa per sempre.";
                String title = "Sicuro di voler uscire?";
                if (JOptionPane.showConfirmDialog(finestra,msg,title,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

        JMenuBar menu = new JMenuBar();

        JMenu help = new JMenu("Help");

        JMenuItem cosaFare = new JMenuItem("Cosa Fare?");
        cosaFare.addActionListener(e -> {
            String msg = "Benvenuto in partita!\n";
            msg += "Guardando in basso vedrai in ordine:\n";
            msg += "    -il turno del giocatore corrente\n";
            msg += "    -il bottone per tirare "+(tabellone.getConf().isDadoSingolo()?"il dado":"i dadi")+"\n";
            msg += "    -il numero di caselle che il giocatore precedente ha percorso con l'ultimo tiro\n";
            msg += "\nSpiegazione del tabellone di gioco:\n";
            msg += "    -i tubi verdi (bonus) rappresentano le scale\n";
            msg += "    -i tubi rossi (malus) rappresentano i serpenti\n";
            msg += "\nSe hai selezionato le rispettive scelte nella configurazione, su alcune caselle vedrai delle scritte:\n";
            msg += "    - PAN = panchina\n";
            msg += "    - LOC = locanda\n";
            msg += "    - DAD = dadi\n";
            msg += "    - MOL = molla\n";
            msg += "    - PES = pesca\n";
            msg += "\nDovrebbe essere tutto, buona partita!";
            JOptionPane.showMessageDialog(finestra,msg);
        });

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> {
            String msg = "Autore del progetto: Gianmarco La Marca\n";
            msg += "matricola: 220465\n";
            msg += "GitHub per il codice sorgente: GLAwasTaken\n\n";
            msg += "La proprietà intellettuale del gioco non appartiene all'autore\n";
            msg += "Grazie per aver giocato";
            JOptionPane.showMessageDialog(finestra,msg);
        });

        help.add(cosaFare);
        help.add(about);

        menu.add(help);

        caselle = new Casella[(r*c)+1]; //lascio la prima posizione vuota per avere corrispondenza di indici
        pedine = new ArrayList<>(tabellone.getNumGiocatori());
        scale = new Ladder[tabellone.getNumCollegamenti()];
        serpenti = new Snake[tabellone.getNumCollegamenti()];

        JLayeredPane center = new JLayeredPane();

        JPanel grid = new JPanel(new GridLayout(r,c));
        grid.setSize(585,520);
        generateGrid(grid); //metodo di generazione grafica del tabellone di gioco

        generateSL(center); //metodo di generazione grafica delle scale e dei serpenti
        center.add(grid);

        Label turnoDi = new Label("E' il turno di p1");
        Label estrazione = new Label("ultimo numero estratto: ");

        roll.setLabels(new Label[]{turnoDi,estrazione});
        roll.getSubject().addActionListener(e -> {
            mutex.release();
        });

        JPanel south = new JPanel(new FlowLayout());
        south.add(turnoDi);
        south.add(roll.getSubject());
        south.add(estrazione);

        finestra.add(menu,BorderLayout.NORTH);
        finestra.add(center,BorderLayout.CENTER);
        finestra.add(south,BorderLayout.SOUTH);
        finestra.setVisible(true);
    }

    private void generateGrid(JPanel grid) {
        int id = r*c;
        if ((r-1)%2 == 0) {
            id -= c-1;
        }
        int k = -3;
        for (int i = 0; i<r; i++) {
            k = (k+3)%COLOR_ROTATION.length;
            boolean verso; //true (-->), false (<--)
            //scegliamo il verso di ogni riga in base al numero di righe totali (dato che iniziamo da quella più in alto)
            if (r%2 == 0) {
                verso = i%2 != 0;
            } else {
                verso = i%2 == 0;
            }
            for (int j = 0; j<c; j++) {
                Casella casella;
                if (verso) {
                    casella = new Casella(id++,COLOR_ROTATION[k],new Posizione(i,j));
                } else {
                    casella = new Casella(id--,COLOR_ROTATION[k],new Posizione(i,j));
                }
                caselle[casella.getId()] = casella; //inserisco in questo modo per ricerca efficiente
                grid.add(casella);
                k++;
                if (k > COLOR_ROTATION.length-1) {
                    k = 0;
                }
            }
            if (!verso) {
                id -= c-1;
            }
            else {
                id -= c+1;
            } //per far ricominciare il conteggio dalla casella sotto
        }
    }

    private void generateSL(JLayeredPane div) {
        Scala[] scale = tabellone.getScale();
        for (int i = 0; i<tabellone.getNumCollegamenti(); i++) {
            Scala s = scale[i];
            Casella top = find(s.getTop());
            Casella bottom = find(s.getBottom());
            Ladder l = new Ladder(top,bottom);
            this.scale[i] = l;
            div.add(l);
        }

        Serpente[] serpenti = tabellone.getSerpenti();
        for (int i = 0; i<tabellone.getNumCollegamenti(); i++) {
            Serpente s = serpenti[i];
            Casella top = find(s.getTop());
            Casella bottom = find(s.getBottom());
            Snake sn = new Snake(top,bottom);
            this.serpenti[i] = sn;
            div.add(sn);
        }
    }

    private Casella find(Posizione pos) {
        for (int i = 1; i<caselle.length; i++) {
            Casella c = caselle[i];
            if (c.getPos().equals(pos)) {
                return c;
            }
        }
        //le caselle ricoprono tutte le posizioni del tabellone, non è possibile arrivare a questo punto del codice
        return null;
    }

    //metodo invocato da GameSimulation per far muovere (graficamente e sul tabellone) i giocatori
    public void move(int giocatore, int casella) {
        Giocatore g = tabellone.getGiocatori()[giocatore];
        if (g.getCasella() == casella) {
            return;
        }

        Casella c = caselle[casella];
        Pedina p = pedine.get(giocatore);

        Casella old = p.getParent();
        old.remove(p); //rimuovo p dalla vecchia casella
        p.setParent(c); //aggiorno la casella di p
        c.add(p); //aggiungo la pedina p alla casella c

        //per aggiornare il contenuto della vecchia casella, rimuovo e riaggiungo tutto
        JLabel num = (JLabel) old.getComponent(0); old.remove(0); //isolo l'etichetta
        Component[] components = old.getComponents();
        old.removeAll();
        old.add(num,BorderLayout.SOUTH,0); //L'etichetta non va messa al centro
        for (Component component:components) {
            old.add(component,BorderLayout.CENTER);
        }

        old.repaint(); old.revalidate(); //repaint della vecchia casella
        c.repaint(); c.revalidate(); //repaint della nuova casella

        for (int i = 0; i<tabellone.getNumCollegamenti(); i++) {
            scale[i].repaint();
            serpenti[i].repaint();
        }
        for (Pedina pe:pedine) {
            pe.repaint();
        }

        Posizione pos = c.getPos(); //nuova posizione di p

        tabellone.move(giocatore,c.getId(),pos);

        controlloScala(giocatore,pos);
        controlloSerpente(giocatore,pos);

        controlloPanchina(giocatore,pos);
        controlloLocanda(giocatore,pos);
        controlloDadi(giocatore,pos);
        controlloMolla(giocatore,old.getPos());
        controllaPesca(giocatore,pos,old.getPos());
    }

    private void controlloScala(int giocatore, Posizione pos) {
        for (Scala s:tabellone.getScale()) { //cerco una scala tale che scala.bottom=pos
            if (s.getBottom().equals(pos)) {
                System.out.println("SCALA!");
                for (int i = 1; i<(r*c)+1; i++) { //cerco la casella che ha pos=scala.top
                    Casella nuova = caselle[i];
                    if (s.getTop().equals(nuova.getPos())) {
                        move(giocatore,nuova.getId());
                    }
                }
            }
        }
    }

    private void controlloSerpente(int giocatore, Posizione pos) {
        for (Serpente s:tabellone.getSerpenti()) { //cerco un serpente tale che serpente.bottom=pos
            if (s.getTop().equals(pos)) {
                System.out.println("SERPENTE!");
                for (int i = 1; i<(r*c)+1; i++) { //cerco la casella che ha pos=serpente.bottom
                    Casella nuova = caselle[i];
                    if (s.getBottom().equals(nuova.getPos())) {
                        move(giocatore,nuova.getId());
                    }
                }
            }
        }
    }

    private void controlloPanchina(int giocatore, Posizione pos) {
        if (tabellone.getConf().isCaselleSosta()) {
            CasellaSpeciale[] panchine = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.PANCHINA);
            for (CasellaSpeciale p:panchine) {
                if (p.getPos().equals(pos)) {
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    if (!(g.getStato() instanceof Panchina)) {
                        g.setStato(Panchina.INSTANCE); //aggiorno lo stato solo se sono appena arrivato sulla casella
                        System.out.println("Il giocatore p"+(giocatore+1)+" è finito su una panchina");
                    }
                    break;
                }
            }
        }
    }

    private void controlloLocanda(int giocatore, Posizione pos) {
        if (tabellone.getConf().isCaselleSosta()) {
            CasellaSpeciale[] locande = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.LOCANDA);
            for (CasellaSpeciale l:locande) {
                if (l.getPos().equals(pos)) {
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    if (!(g.getStato() instanceof Locanda)) {
                        g.setStato(new Locanda()); //aggiorno lo stato solo se sono appena arrivato sulla casella
                        System.out.println("Il giocatore p"+(giocatore+1)+" è finito su una locanda");
                    }
                    break;
                }
            }
        }
    }

    private void controlloDadi(int giocatore, Posizione pos) {
        if (tabellone.getConf().isCasellePremio()) {
            CasellaSpeciale[] dadi = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.DADI);
            for (CasellaSpeciale d:dadi) {
                if (d.getPos().equals(pos)) { //sono su una casella dadi
                    actionDadi(giocatore);
                }
            }
        }
    }

    private void actionDadi(int giocatore) {
        Giocatore g = tabellone.getGiocatori()[giocatore];
        int lancio = lanciaDadi(g);
        int nuova = Tabellone.validaCasella(g.getCasella(),lancio,r*c);
        System.out.println("DADI! Il giocatore avanza di "+lancio+" caselle");
        if (tabellone.getConf().isDoppioSei() && lancio == 12) {
            move(g.getId(),nuova);
            lancio = lanciaDadi(g);
            nuova = Tabellone.validaCasella(g.getCasella(),lancio,r*c);
        }
        move(g.getId(),nuova);
    }

    private int lanciaDadi(Giocatore g) {
        int casella = g.getCasella();
        int ultimaCasella = r*c;
        int lancio = ((int) Math.rint((Math.random() * 5))) + 1;
        if (!tabellone.getConf().isDadoSingolo() &&
            (casella < ultimaCasella - 6 || !tabellone.getConf().isLancioUnico())) {
                lancio += ((int) Math.rint((Math.random() * 5))) + 1;
        }
        return lancio;
    }

    private void controlloMolla(int giocatore, Posizione vecchiaPos) {
        if (tabellone.getConf().isCasellePremio()) {
            CasellaSpeciale[] molle = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.MOLLA);
            for (CasellaSpeciale m:molle) {
                Giocatore g = tabellone.getGiocatori()[giocatore];
                if (m.getPos().equals(g.getPos())) { //sono su una casella molla
                    actionMolla(giocatore,vecchiaPos);
                }
            }
        }
    }

    private void actionMolla(int giocatore, Posizione vecchia) {
        Giocatore g = tabellone.getGiocatori()[giocatore];
        for (int i = 1; i<caselle.length; i++) {
            Casella casella = caselle[i];
            if (casella.getPos().equals(vecchia)) {
                int distanza = Math.abs(g.getCasella()-casella.getId());
                int nuova = g.getCasella()+distanza;
                int ultimaCasella = r*c;
                if (nuova > ultimaCasella) {
                    int offset = ultimaCasella-nuova;
                    int fallback = distanza-offset;
                    nuova = ultimaCasella-fallback;
                }
                System.out.println("MOLLA! Il giocatore p"+(giocatore+1)+" avanza di "+distanza);
                move(g.getId(),nuova);
            }
        }
    }

    private void controllaPesca(int giocatore, Posizione pos, Posizione vecchia) {
        if (tabellone.getConf().isPescaCarta()) {
            CasellaSpeciale[] pescaCarte = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.PESCA);
            for (CasellaSpeciale p:pescaCarte) {
                if (p.getPos().equals(pos)) { //sono su una casella pesca
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    int i;
                    if (tabellone.getConf().isUlterioriCarte()) {
                        i = (int) Math.rint(Math.random() * 4); //con 4 escludo l'elemento PESCA
                    }
                    else {
                        i = (int) Math.rint(Math.random() * 3); //con 3 escludo DIVIETO e PESCA
                    }
                    CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.values()[i];
                    switch (t) {
                        case PANCHINA -> {
                            g.setStato(Panchina.INSTANCE);
                            System.out.println("Il giocatore p" + (giocatore + 1) + " ha pescato: PANCHINA");
                        }
                        case LOCANDA -> {
                            g.setStato(new Locanda());
                            System.out.println("Il giocatore p" + (giocatore + 1) + " ha pescato: LOCANDA");
                        }
                        case DADI -> {
                            actionDadi(giocatore);
                            System.out.println("Il giocatore p" + (giocatore + 1) + " ha pescato: DADI");
                        }
                        case MOLLA -> {
                            actionMolla(giocatore,vecchia);
                            System.out.println("Il giocatore p" + (giocatore + 1) + " ha pescato: MOLLA");
                        }
                        case DIVIETO -> {
                            g.setDivietoDiSosta(true);
                            System.out.println("Il giocatore p" + (giocatore + 1) + "ha pescato: DIVIETO DI SOSTA");
                        }
                        default -> throw new IllegalStateException(); //non possiamo trovarci nello stato PESCA
                    }
                }
            }
        }
    }

    public void nextTurn(String[] newLables) {
        Label[] labels = roll.getLabels();
        System.out.println(newLables[0]+" - "+newLables[1]);
        labels[0].setText(newLables[0]);
        labels[1].setText(newLables[1]);
        roll.notifica();
    }

    public void mostraVincitore(Giocatore g) {
        roll.getSubject().setEnabled(false);
        String msg = "Il giocatore p"+(g.getId()+1)+" ha vinto";
        String title = "C'è un vincitore!";
        JOptionPane.showConfirmDialog(finestra,msg,title,JOptionPane.DEFAULT_OPTION);
        finestra.dispose();
    }

    class Casella extends JPanel {
        private final int id;
        private final Colore colore;
        private final Posizione pos; //associo ad ogni casella la posizione nella matrice (board)
        private final int WIDTH = 10, HEIGHT = 10;

        public Casella(int id,Colore colore, Posizione pos) {
            super();
            this.id = id;
            this.colore = colore;
            this.pos = pos;
            setLayout(new BorderLayout());
            setSize(WIDTH,HEIGHT);
            switch (colore) {
                case GIALLO -> setBackground(Color.YELLOW);
                case BIANCO -> setBackground(Color.WHITE);
                case ROSSO -> setBackground(Color.RED);
                case BLU -> setBackground(Color.BLUE);
                case VERDE -> setBackground(Color.GREEN);
                default -> throw new IllegalArgumentException();
            }
            riempi(this);
        }

        public int getId() {
            return id;
        }

        public Posizione getPos() {
            return pos;
        }
    }

    private void riempi(Casella casella) {
        JLabel num = new JLabel(Integer.toString(casella.getId()));
        setColor(num,casella);
        casella.add(num,BorderLayout.SOUTH,0); //mettere un indice seve per l'operazione di aggiornamento
        if (casella.getId() == 1) {
            for (int i = 0; i<tabellone.getNumGiocatori(); i++) {
                Pedina pedina = new Pedina(i,casella,"p"+(i+1),
                        casella.getX(),casella.getY(),casella.getSize().width,casella.getSize().height);
                pedine.add(pedina);
            } //ad inizio partita tutti i giocatori si trovano sulla prima casella

            JLabel start = new JLabel("Start -->");
            setColor(start,casella);

            casella.add(start,BorderLayout.CENTER);
            for (Pedina p:pedine) {
                casella.add(p);
            }
        } else if (casella.getId() == r*c) {
            JLabel finish = new JLabel("Finish!");
            setColor(finish,casella);
            casella.add(finish,BorderLayout.CENTER);
        }
        for (CasellaSpeciale.Tipo t:CasellaSpeciale.Tipo.values()) {
            CasellaSpeciale[] target = tabellone.getCaselleSpeciali().get(t);
            if (target != null) {
                for (CasellaSpeciale speciale:target) {
                    if (casella.getPos().equals(speciale.getPos())) {
                        JLabel tipo = new JLabel("    "+t.name().substring(0,3));
                        setColor(tipo,casella);
                        casella.add(tipo);
                    }
                }
            }
        }
    }

    private void setColor(JLabel target,Casella c) {
        if (c.getBackground().equals(Color.BLUE) ||
                c.getBackground().equals(Color.RED)) {
            target.setForeground(Color.WHITE);
        }
    }

}
