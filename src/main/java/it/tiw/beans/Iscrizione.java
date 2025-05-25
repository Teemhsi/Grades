package it.tiw.beans;

public class Iscrizione {
    private int idStudente;
    private int idAppello;
    private String voto;
    private String statoValutazione;

    public int getIdStudente() { return idStudente; }
    public void setIdStudente(int idStudente) { this.idStudente = idStudente; }

    public int getIdAppello() { return idAppello; }
    public void setIdAppello(int idAppello) { this.idAppello = idAppello; }

    public String getVoto() { return voto; }
    public void setVoto(String voto) { this.voto = voto; }

    public String getStatoValutazione() { return statoValutazione; }
    public void setStatoValutazione(String statoValutazione) { this.statoValutazione = statoValutazione; }

    @Override
    public String toString() {
        return "Iscrizione{" +
                "idStudente=" + idStudente +
                ", idAppello=" + idAppello +
                ", voto='" + voto + '\'' +
                ", statoValutazione='" + statoValutazione + '\'' +
                '}';
    }
}

