package main.gui;

import javax.swing.*;
import java.awt.*;

public class Ladder extends JLabel {
    private FinestraPrincipale.Casella top, bottom;
    private final int THICKNESS = 5;

    public Ladder(FinestraPrincipale.Casella top, FinestraPrincipale.Casella bottom) {
        this.top = top;
        this.bottom = bottom;
        setBounds(Math.min(top.getX(),bottom.getX()),bottom.getY(),600,500);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //facciamo puntare le linee al centro delle caselle
        int x1 = top.getX()+(top.getSize().width/2);
        int y1 = top.getY()+(top.getSize().height/2);
        int x2 = bottom.getX()+(bottom.getSize().width/2);
        int y2 = bottom.getY()+(bottom.getSize().height/2);
        g.setColor(Color.BLACK);
        g.drawLine(x1-1,y1,x2-1,y2);
        g.setColor(new Color(80,255,80));
        for (int i = 0; i<THICKNESS; i++) {
            g.drawLine(x1+i,y1,x2+i,y2);
        }
        g.setColor(Color.BLACK);
        g.drawLine(x1+THICKNESS,y1,x2+THICKNESS,y2);
    }
}
