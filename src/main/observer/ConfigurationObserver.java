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

    public ConfigurationButtonSubject getSubject() {
        return subject;
    }

    public Configurazione getConf() {
        return conf;
    }

    @Override
    public void update() {
        super.update();
        conf = subject.getConf();
    }
}
