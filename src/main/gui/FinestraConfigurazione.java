package main.gui;

import main.Tabellone;
import main.configurazione.Configurazione;
import main.mediator.Mediator;
import main.observer.subject.ConfigurationButtonSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FinestraConfigurazione {
    private JFrame finestra;
    private Configurazione conf;

    private ConfigurationButtonSubject submit;

    public FinestraConfigurazione(ConfigurationButtonSubject submit) {
        this.submit = submit;
    }

    private final String PLACEHOLDER1 = "(max=12, default=2)", PLACEHOLDER2 = "(min=5, max=10, default=10)";

    public void init() {
        finestra = new JFrame();
        finestra.setTitle("Scale e Serpenti - configurazione partita");
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.setBounds(400,200,400,300);
        finestra.setLayout(new FlowLayout());

        //TODO: aggiungere il menu con l'opzione di salvataggio/caricamento partita su/da filesystem

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

        //ConfigurationButtonSubject submit = new ConfigurationButtonSubject(new JButton("Submit"));
        submit.getSubject().addActionListener(e -> {
            try {
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

                if (ng < 2 || ng > 12 ||
                    r < 5 || r > 10 ||
                    c < 5 || c > 10) {
                        throw new IllegalArgumentException();
                }
                int conferma = conferma();
                if (conferma == JOptionPane.YES_OPTION) {
                    conf = new Configurazione(
                            ng,r,c,
                            dadoSingolo.isSelected(), lancioUnico.isSelected(), doppioSei.isSelected(),
                            caselleSosta.isSelected(), casellePremio.isSelected(),
                            pescaCarta.isSelected(), ulterioriCarte.isSelected(),
                            automatico.isSelected()
                    );
                    submit.setConf(conf);
                    submit.notifica();
                    finestra.dispose();
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

    public static void main(String[] args) {
        //FinestraConfigurazione f = new FinestraConfigurazione();
        //f.init();
    }
}
