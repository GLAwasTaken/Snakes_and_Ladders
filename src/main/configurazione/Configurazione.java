package main.configurazione;

public class Configurazione {
    private final int numGiocatori;
    private final int righe, colonne;
    private final boolean dadoSingolo, lancioUnico, doppioSei;
    private final boolean caselleSosta, casellePremio, pescaCarta, ulterioriCarte;
    private final boolean automatico;

    public Configurazione(int numGiocatori, int righe, int colonne, boolean dadoSingolo, boolean lancioUnico,
                          boolean doppioSei, boolean caselleSosta, boolean casellePremio, boolean pescaCarta,
                          boolean ulterioriCarte, boolean automatico) {
        this.numGiocatori = numGiocatori;
        this.righe = righe;
        this.colonne = colonne;
        this.dadoSingolo = dadoSingolo;
        this.lancioUnico = lancioUnico;
        this.doppioSei = doppioSei;
        this.caselleSosta = caselleSosta;
        this.casellePremio = casellePremio;
        this.pescaCarta = pescaCarta;
        this.ulterioriCarte = ulterioriCarte;
        this.automatico = automatico;
    }

    public int getNumGiocatori() {
        return numGiocatori;
    }

    public int getRighe() {
        return righe;
    }

    public int getColonne() {
        return colonne;
    }

    public boolean isDadoSingolo() {
        return dadoSingolo;
    }

    public boolean isLancioUnico() {
        return lancioUnico;
    }

    public boolean isDoppioSei() {
        return doppioSei;
    }

    public boolean isCaselleSosta() {
        return caselleSosta;
    }

    public boolean isCasellePremio() {
        return casellePremio;
    }

    public boolean isPescaCarta() {
        return pescaCarta;
    }

    public boolean isUlterioriCarte() {
        return ulterioriCarte;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    @Override
    public String toString() {
        return "Configurazione{" +
                "numGiocatori=" + numGiocatori +
                ", righe=" + righe +
                ", colonne=" + colonne +
                ", dadoSingolo=" + dadoSingolo +
                ", lancioUnico=" + lancioUnico +
                ", doppioSei=" + doppioSei +
                ", caselleSosta=" + caselleSosta +
                ", casellePremio=" + casellePremio +
                ", pescaCarta=" + pescaCarta +
                ", ulterioriCarte=" + ulterioriCarte +
                ", automatico=" + automatico +
                '}';
    }
}
