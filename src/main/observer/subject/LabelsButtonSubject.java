package main.observer.subject;

import main.observer.ButtonObserver;
import main.observer.LabelsObserver;

import javax.swing.*;
import java.awt.*;

public class LabelsButtonSubject extends AbstractButtonSubject {
    private Label[] labels;

    public LabelsButtonSubject(JButton subject) {
        super();
        this.subject = subject;
        subject.addActionListener(e -> notifica());
    }

    public Label[] getLabels() {
        return new Label[]{labels[0],labels[1]};
    }

    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    @Override
    public void attach(ButtonObserver o) {
        if (!(o instanceof LabelsObserver)) {
            throw new IllegalArgumentException();
        }
        observers.add(o);
    }
}
