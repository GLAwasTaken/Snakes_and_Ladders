package main.gui;

import javax.swing.*;
import java.awt.*;

public class Pedina extends JPanel {
    private static final FinestraPrincipale.Colore[] PEDINE = {FinestraPrincipale.Colore.GIALLO, FinestraPrincipale.Colore.BIANCO, FinestraPrincipale.Colore.ROSSO, FinestraPrincipale.Colore.BLU, FinestraPrincipale.Colore.VERDE,
            FinestraPrincipale.Colore.ARANCIO, FinestraPrincipale.Colore.VIOLA, FinestraPrincipale.Colore.CIANO, FinestraPrincipale.Colore.ROSA,
            FinestraPrincipale.Colore.NERO, FinestraPrincipale.Colore.GRIGIO_C, FinestraPrincipale.Colore.GRIGIO_S};
    private int id;
    private FinestraPrincipale.Casella parent;
    private String text;

    public Pedina(int id, FinestraPrincipale.Casella parent, int x, int y, int w, int h) {
        this.id = id;
        this.parent = parent;
        setBounds(x,y,w,h);
        setBackground(parent.getBackground());
    }

    public Pedina(int id, FinestraPrincipale.Casella parent, String text, int x, int y, int w, int h) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        setBounds(getX(),getY(),getSize().width,getSize().height);
    }

    public int getId() {
        return id;
    }

    public FinestraPrincipale.Casella getParent() {
        return parent;
    }

    public void setParent(FinestraPrincipale.Casella parent) {
        this.parent = parent;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setBackground(parent.getBackground());
        Color colore = scegliColore(PEDINE[id]);
        g.setColor(colore);
        g.fillOval(getX(),getY(),getWidth(),getHeight());
        g.setColor(Color.BLACK);
        g.drawOval(getX(),getY(),getWidth(),getHeight());
        if (text != null) {
            g.drawString(text,
                    getX()+(getSize().width/2)-(getSize().width/7),
                    getY()+(getSize().height/2)+(getSize().height/9)
            );
        }
    }

    private Color scegliColore(FinestraPrincipale.Colore colore) {
        return switch (colore) {
            case GIALLO -> Color.YELLOW;
            case BIANCO -> Color.WHITE;
            case ROSSO -> Color.RED;
            case BLU -> Color.BLUE;
            case VERDE -> Color.GREEN;
            case ARANCIO -> Color.ORANGE;
            case VIOLA -> Color.MAGENTA;
            case CIANO -> Color.CYAN;
            case ROSA -> Color.PINK;
            case NERO -> Color.BLACK;
            case GRIGIO_C -> Color.LIGHT_GRAY;
            case GRIGIO_S -> Color.DARK_GRAY;
        };
    }
}
