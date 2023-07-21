package main.observer.subject;

import main.Configurazione;
import main.observer.ButtonObserver;
import main.observer.ConfigurationObserver;

import javax.swing.*;

public class ConfigurationButtonSubject extends AbstractButtonSubject {
    private Configurazione conf;

    public ConfigurationButtonSubject(JButton subject) {
        super(subject);
    }

    public Configurazione getConf() {
        return conf;
    }

    public void setConf(Configurazione conf) {
        this.conf = conf;
    }

    @Override
    public void attach(ButtonObserver o) {
        if (!(o instanceof ConfigurationObserver)) {
            throw new IllegalArgumentException();
        }
        observers.add(o);
    }
}
