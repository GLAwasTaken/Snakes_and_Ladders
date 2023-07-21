package main.observer.subject;

import main.observer.ButtonObserver;

import javax.swing.*;
import java.util.ArrayList;

public abstract class AbstractButtonSubject {
    public enum State {PRESSED, NOT_PRESSED}
    protected JButton subject;

    protected State state;
    protected ArrayList<ButtonObserver> observers;

    public AbstractButtonSubject(JButton subject) {
        this.subject = subject;
        state = State.NOT_PRESSED;
        observers = new ArrayList<>();
    }

    public JButton getSubject() {
        return subject;
    }

    public abstract void attach(ButtonObserver o);
    public void detach(ButtonObserver o) {
        observers.remove(o);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void notifica() {
        for (ButtonObserver o:observers) {
            o.update();
        }
    }
}
