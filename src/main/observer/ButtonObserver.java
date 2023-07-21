package main.observer;
import main.observer.subject.AbstractButtonSubject;

public abstract class ButtonObserver implements ObserverIF {
    protected AbstractButtonSubject.State state; //prende lo stato del subject

    public ButtonObserver() {
        state = AbstractButtonSubject.State.NOT_PRESSED;
    }

    public AbstractButtonSubject.State getState() {
        return state;
    }
}
