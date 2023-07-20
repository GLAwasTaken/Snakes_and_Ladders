package main.gui;


import main.Configurazione;
import main.mediator.Mediator;
import main.observer.subject.ConfigurationButtonSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class FinestraConfigurazione {
    private JFrame finestra;
    private Configurazione conf;

    private ConfigurationButtonSubject submit;
    private Semaphore mutex;

    public FinestraConfigurazione(ConfigurationButtonSubject submit, Semaphore mutex) {
        this.submit = submit;
        this.mutex = mutex;
    }

    private final String PLACEHOLDER1 = "(max=12, default=2)", PLACEHOLDER2 = "(min=5, max=10, default=10)";

    public void init() {
        finestra = new JFrame();
        finestra.setTitle("Scale e Serpenti - configurazione partita");
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.setBounds(400,200,400,300);
        finestra.setLayout(new FlowLayout());

        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem apri = new JMenuItem("Apri");
        apri.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            try {
                if (chooser.showOpenDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    File configurazione = chooser.getSelectedFile();
                    conf = getConfigurazione(configurazione);
                    if (conf != null && configurazioneCorretta(conf)) {
                        submit.setConf(conf);
                        submit.notifica();
                        mutex.release();
                        finestra.dispose();
                    } else JOptionPane.showMessageDialog(finestra,"Il file non rappresenta una configurazione valida");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        JMenuItem salva = new JMenuItem("Salva");
        JMenuItem salvaConNome = new JMenuItem("Salva con nome");
        JMenuItem esci = new JMenuItem("Esci");
        esci.addActionListener(e -> {
            finestra.dispatchEvent(new WindowEvent(finestra,WindowEvent.WINDOW_CLOSING));
        });

        file.add(apri);
        file.add(salva);
        file.add(salvaConNome);
        file.addSeparator();
        file.add(esci);

        JMenu help = new JMenu("Help");

        JMenuItem cosaFare = new JMenuItem("Cosa fare?");
        cosaFare.addActionListener(e -> {
            String msg = "Utilizzare i pulsanti per definire la configurazione di questa partita.\n";
            msg += "In alternativa si può scegliere di:";
            msg += "\n    -caricare una configurazione (file .config) da filesystem";
            msg += "\n    -salvare o salvare con nome la configurazione corrente sul filesystem";
            JOptionPane.showMessageDialog(finestra,msg,"Scelta della Configurazione della partita",JOptionPane.INFORMATION_MESSAGE);
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

        menu.add(file);
        menu.add(help);
        finestra.setJMenuBar(menu);

        JTextField numGiocatori = new JTextField(20);
        Placeholder.setPlaceholder(numGiocatori,PLACEHOLDER1);
        numGiocatori.addFocusListener(Placeholder.getFocusListener(numGiocatori,PLACEHOLDER1));

        JTextField righe = new JTextField(20);
        Placeholder.setPlaceholder(righe,PLACEHOLDER2);
        righe.addFocusListener(Placeholder.getFocusListener(righe,PLACEHOLDER2));

        JTextField colonne = new JTextField(20);
        Placeholder.setPlaceholder(colonne,PLACEHOLDER2);
        colonne.addFocusListener(Placeholder.getFocusListener(colonne,PLACEHOLDER2));

        JCheckBox dadoSingolo = new JCheckBox("dado singolo");

        JCheckBox lancioUnico = new JCheckBox("lancio unico");

        JCheckBox doppioSei = new JCheckBox("doppio sei");

        JCheckBox caselleSosta = new JCheckBox("caselle sosta");

        JCheckBox casellePremio = new JCheckBox("caselle premio");

        JCheckBox pescaCarta = new JCheckBox("pesca carta");

        JCheckBox ulterioriCarte = new JCheckBox("ulteriori carte");

        JCheckBox automatico = new JCheckBox("avanzamento automatico");

        //gli action listener vanno aggiunti dopo l'istanziazione dei JCheckBox
        salva.addActionListener(e -> {
            conf = buildConfigurazione(
                    numGiocatori,righe,colonne,
                    dadoSingolo,lancioUnico,doppioSei,
                    caselleSosta,casellePremio,pescaCarta,ulterioriCarte,
                    automatico);
            if (!configurazioneCorretta(conf)) {
                JOptionPane.showMessageDialog(finestra,"Impossibile salvare, configurazione non valida!","Errore di inserimento",JOptionPane.ERROR_MESSAGE);
            }
            else {
                JFileChooser chooser = new JFileChooser();
                File output = null;
                if (chooser.showSaveDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    output = chooser.getSelectedFile();
                }
                if (output != null) {
                    int i = output.getName().lastIndexOf('.');
                    String ext = output.getName().substring(i+1);
                    if (!ext.equals("config")) {
                        JOptionPane.showMessageDialog(finestra,"L'estensione '.config' è l'unica valida!");
                    }
                    else {
                        if (!output.exists()) {
                            save(output);
                        }
                        else {
                            int conferma = JOptionPane.showConfirmDialog(finestra,
                                    "Sovrascrivere "+output.getName()+"?",
                                    "Conferma salvataggio",
                                    JOptionPane.YES_NO_OPTION);
                            if (conferma == JOptionPane.YES_OPTION) {
                                save(output);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(finestra,"Nessun Salvataggio");
                }
            }
        });

        salvaConNome.addActionListener(e -> {
            conf = buildConfigurazione(
                    numGiocatori,righe,colonne,
                    dadoSingolo,lancioUnico,doppioSei,
                    caselleSosta,casellePremio,pescaCarta,ulterioriCarte,
                    automatico);
            if (!configurazioneCorretta(conf)) {
                JOptionPane.showMessageDialog(finestra,"Impossibile salvare, configurazione non valida!","Errore di inserimento",JOptionPane.ERROR_MESSAGE);
            }
            else {
                JFileChooser chooser = new JFileChooser();
                File output = null;
                if (chooser.showSaveDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    output = chooser.getSelectedFile();
                }
                if (output != null) {
                    int i = output.getName().lastIndexOf('.');
                    String ext = output.getName().substring(i+1);
                    if (!ext.equals("config")) {
                        JOptionPane.showMessageDialog(finestra,"Estensione non compatibile");
                    } else {
                        if (!output.exists()) {
                            save(output);
                        } else {
                            JOptionPane.showMessageDialog(finestra,"Esiste già un file con questo nome nella cartella");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(finestra,"Nessun Salvataggio");
                }
            }
        });

        submit.getSubject().addActionListener(e -> {
            try {
                int conferma = conferma();
                if (conferma == JOptionPane.YES_OPTION) {
                    conf = buildConfigurazione(
                            numGiocatori,righe,colonne,
                            dadoSingolo, lancioUnico, doppioSei,
                            caselleSosta, casellePremio, pescaCarta, ulterioriCarte,
                            automatico
                    );
                    if (configurazioneCorretta(conf)) {
                        submit.setConf(conf);
                        submit.notifica();
                        mutex.release();
                        finestra.dispose();
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(finestra,"Inserire solo numeri interi positivi nelle aree di testo!","Errore di inserimento",JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(finestra,"I valori inseriti non rispettano i limiti!","Errore di inserimento",JOptionPane.ERROR_MESSAGE);
            }
        });

        Mediator mediator = new Mediator();

        mediator.setDadoSingolo(dadoSingolo);
        mediator.setLancioUnico(lancioUnico);
        mediator.setDoppioSei(doppioSei);

        mediator.setPescaCarta(pescaCarta);
        mediator.setUlterioriCarte(ulterioriCarte);

        dadoSingolo.addActionListener(e -> mediator.sceltaDado(dadoSingolo));
        lancioUnico.addActionListener(e -> mediator.sceltaDado(lancioUnico));
        doppioSei.addActionListener(e -> mediator.sceltaDado(doppioSei));

        ulterioriCarte.setEnabled(false);
        pescaCarta.addActionListener(e -> mediator.pescaCarta(pescaCarta));

        finestra.add(new JLabel("numero giocatori "));
        finestra.add(numGiocatori);
        finestra.add(new JLabel("numero righe "));
        finestra.add(righe);
        finestra.add(new JLabel("numero colonne "));
        finestra.add(colonne);
        finestra.add(dadoSingolo);
        finestra.add(lancioUnico);
        finestra.add(doppioSei);
        finestra.add(caselleSosta);
        finestra.add(casellePremio);
        finestra.add(pescaCarta);
        finestra.add(ulterioriCarte);
        finestra.add(automatico);
        finestra.add(submit.getSubject());

        finestra.setVisible(true);
    }

    private Configurazione buildConfigurazione(
            JTextField numGiocatori, JTextField righe, JTextField colonne,
            JCheckBox dadoSingolo, JCheckBox lancioUnico, JCheckBox doppioSei,
            JCheckBox caselleSosta, JCheckBox casellePremio, JCheckBox pescaCarta,
            JCheckBox ulterioriCarte, JCheckBox automatico
    ) {
        int ng,r,c;

        if (numGiocatori.getText().equals(PLACEHOLDER1)) {
            ng = 2;
        } else {
            ng = Integer.parseInt(numGiocatori.getText());
        }

        if (righe.getText().equals(PLACEHOLDER2)) {
            r = 10;
        } else {
            r = Integer.parseInt(righe.getText());
        }

        if (colonne.getText().equals(PLACEHOLDER2)) {
            c = 10;
        } else {
            c = Integer.parseInt(colonne.getText());
        }

        return new Configurazione(
                ng, r, c,
                dadoSingolo.isSelected(), lancioUnico.isSelected(), doppioSei.isSelected(),
                caselleSosta.isSelected(), casellePremio.isSelected(),
                pescaCarta.isSelected(), ulterioriCarte.isSelected(),
                automatico.isSelected()
        );
    }

    private Configurazione getConfigurazione(File f) throws FileNotFoundException {
        if (!f.exists()) {
            JOptionPane.showMessageDialog(finestra,"File non esistente");
        }
        else {
            int i = f.getName().lastIndexOf('.');
            String ext = f.getName().substring(i+1);
            if (!ext.equals("config")) {
                JOptionPane.showMessageDialog(finestra,"Estensione del file non supportata");
            }
            else {
                int ng = 0, r = 0, c = 0;
                boolean dadoSingolo = false, lancioUnico = false, doppioSei = false,
                        caselleSosta = false, casellePremio = false, pescaCarta = false, ulterioriCarte = false,
                        automatico = false;
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                while (true) {
                    try {
                        String line = in.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] entry = line.trim().split(":");
                        switch (entry[0]) {
                            case "Configurazione {", "}" -> {}
                            case "numGiocatori" -> {
                                ng = Integer.parseInt(entry[1]);
                            }
                            case "numRighe" -> {
                                r = Integer.parseInt(entry[1]);
                            }
                            case "numColonne" -> {
                                c = Integer.parseInt(entry[1]);
                            }
                            case "dadoSingolo" -> {
                                dadoSingolo = entry[1].equals("true");
                            }
                            case "lancioUnico" -> {
                                lancioUnico = entry[1].equals("true");
                            }
                            case "doppioSei" -> {
                                doppioSei = entry[1].equals("true");
                            }
                            case "caselleSosta" -> {
                                caselleSosta = entry[1].equals("true");
                            }
                            case "casellePremio" -> {
                                casellePremio = entry[1].equals("true");
                            }
                            case "pescaCarta" -> {
                                pescaCarta = entry[1].equals("true");
                            }
                            case "ulterioriCarte" -> {
                                ulterioriCarte = entry[1].equals("true");
                            }
                            case "automatico" -> {
                                automatico = entry[1].equals("true");
                            }
                            default -> throw new IllegalArgumentException();
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
                return new Configurazione(ng,r,c,dadoSingolo,lancioUnico,doppioSei,caselleSosta,casellePremio,pescaCarta,ulterioriCarte,automatico);
            }
        }
        return null;
    }

    //serve per controllare la correttezza di una configurazione caricata da filesystem
    private boolean configurazioneCorretta(Configurazione conf) {
        if (conf.getNumGiocatori() < 2 || conf.getNumGiocatori() > 12 ||
                conf.getRighe() < 5 || conf.getRighe() > 10 ||
                conf.getColonne() < 5 || conf.getColonne() > 10) {
            return false;
        }
        if (conf.isDadoSingolo() && (conf.isLancioUnico() || conf.isDoppioSei())) {
            return false;
        }
        return conf.isPescaCarta() || !conf.isUlterioriCarte();
    }

    private void save(File f) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(f),true);

            out.println("Configurazione {");
            out.println("\tnumGiocatori:"+conf.getNumGiocatori());
            out.println("\tnumRighe:"+conf.getRighe());
            out.println("\tnumColonne:"+conf.getColonne());
            out.println("\tdadoSingolo:"+conf.isDadoSingolo());
            out.println("\tlancioUnico:"+conf.isLancioUnico());
            out.println("\tdoppioSei:"+conf.isDoppioSei());
            out.println("\tcaselleSosta:"+conf.isCaselleSosta());
            out.println("\tcasellePremio:"+conf.isCasellePremio());
            out.println("\tpescaCarta:"+conf.isPescaCarta());
            out.println("\tulterioriCarte:"+conf.isUlterioriCarte());
            out.println("\tautomatico:"+conf.isAutomatico());
            out.println("}");

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //classe di utilità per implementare il placeholder dei JTextField
    static class Placeholder {
        public static FocusListener getFocusListener(JTextField field, String placeholder) {
            return new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText(null);
                        field.setForeground(Color.BLACK);
                        field.setFont(field.getFont().deriveFont(Font.PLAIN));
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText() == null || field.getText().equals("")) {
                        field.setFont(field.getFont().deriveFont(Font.ITALIC));
                        field.setText(placeholder);
                        field.setForeground(Color.GRAY);
                    }
                }
            };
        }

        public static void setPlaceholder(JTextField field, String placeholder) {
            field.setFont(field.getFont().deriveFont(Font.ITALIC));
            field.setText(placeholder);
            field.setForeground(Color.GRAY);
        }
    }

    private int conferma() {
        String msg = "Sicuro di voler continuare con questa configurazione?";
        String title = "Conferma Submit";
        return JOptionPane.showConfirmDialog(finestra,msg,title,JOptionPane.YES_NO_OPTION);
    }

}
