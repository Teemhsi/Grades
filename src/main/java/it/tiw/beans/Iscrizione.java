package it.tiw.beans;

/**
 * Bean che rappresenta l'iscrizione di uno studente a un appello.
 * Include informazioni sul voto e sullo stato di valutazione.
 */
public class Iscrizione {
    private int idStudente;
    private int idAppello;
    private String voto;
    private String statoValutazione;

    /**
     * Costruttore vuoto.
     */
    public Iscrizione() {}

    /**
     * Restituisce l'identificativo dello studente.
     *
     * @return L'id dello studente.
     */
    public int getIdStudente() {
        return idStudente;
    }

    /**
     * Imposta l'identificativo dello studente.
     *
     * @param idStudente Identificativo dello studente.
     */
    public void setIdStudente(int idStudente) {
        this.idStudente = idStudente;
    }

    /**
     * Restituisce l'identificativo dell'appello.
     *
     * @return L'id dell'appello.
     */
    public int getIdAppello() {
        return idAppello;
    }

    /**
     * Imposta l'identificativo dell'appello.
     *
     * @param idAppello Identificativo dell'appello.
     */
    public void setIdAppello(int idAppello) {
        this.idAppello = idAppello;
    }

    /**
     * Restituisce il voto assegnato allo studente per l'appello.
     *
     * @return Il voto come stringa.
     */
    public String getVoto() {
        return voto;
    }

    /**
     * Imposta il voto per l'appello.
     *
     * @param voto Voto da assegnare.
     */
    public void setVoto(String voto) {
        this.voto = voto;
    }

    /**
     * Restituisce lo stato della valutazione dello studente per l'appello.
     *
     * @return Stato della valutazione.
     */
    public String getStatoValutazione() {
        return statoValutazione;
    }

    /**
     * Imposta lo stato della valutazione dello studente per l'appello.
     *
     * @param statoValutazione Stato della valutazione.
     */
    public void setStatoValutazione(String statoValutazione) {
        this.statoValutazione = statoValutazione;
    }

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
