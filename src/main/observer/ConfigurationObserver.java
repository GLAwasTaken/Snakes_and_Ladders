package main.observer;

import main.Configurazione;
import main.observer.subject.ConfigurationButtonSubject;

public class ConfigurationObserver extends ButtonObserver {
    private ConfigurationButtonSubject subject;
    private Configurazione conf;

    public ConfigurationObserver(ConfigurationButtonSubject subject) {
        super();
        this.subject = subject;
    }

    public Configurazione getConf() {
        return conf;
    }

    @Override
    public void update() {
        state = subject.getState();
        conf = subject.getConf();
    }
}
