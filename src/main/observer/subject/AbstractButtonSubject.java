package main.observer.subject;

import main.observer.ButtonObserver;

import javax.swing.*;
import java.util.ArrayList;

public abstract class AbstractButtonSubject {
    protected JButton subject;
    protected ArrayList<ButtonObserver> observers;

    public AbstractButtonSubject() {
        observers = new ArrayList<>();
    }

    public JButton getSubject() {
        return subject;
    }

    public abstract void attach(ButtonObserver o);
    public void detach(ButtonObserver o) {
        observers.remove(o);
    }

    public void notifica() {
        for (ButtonObserver o:observers) {
            o.update();
        }
    }
}
