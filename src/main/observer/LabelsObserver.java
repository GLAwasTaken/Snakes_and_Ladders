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

    public LabelsButtonSubject getSubject() {
        return subject;
    }

    public Label[] getLabels() {
        return labels;
    }

    @Override
    public void update() {
        super.update();
        labels = subject.getLabels();
    }
}
