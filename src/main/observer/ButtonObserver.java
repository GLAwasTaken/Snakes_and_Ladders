package main.observer;

public abstract class ButtonObserver implements ObserverIF {
    public enum State {PRESSED, NOT_PRESSED}
    private State state;

    public ButtonObserver() {
        state = State.NOT_PRESSED;
    }

    public State getState() {
        return state;
    }

    @Override
    public void update() {
        switch (state) {
            case PRESSED -> state = State.NOT_PRESSED;
            case NOT_PRESSED -> state = State.PRESSED;
        }
    }
}
