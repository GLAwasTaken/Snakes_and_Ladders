package main.observer;

import main.observer.subject.LabelsButtonSubject;

import java.awt.*;

public class LabelsObserver extends ButtonObserver {
    private LabelsButtonSubject subject;
    private Label[] labels;

    public LabelsObserver(LabelsButtonSubject subject) {
        super();
        this.subject = subject;
    }

    public Label[] getLabels() {
        return labels;
    }

    @Override
    public void update() {
        state = subject.getState();
        labels = subject.getLabels();
    }
}
