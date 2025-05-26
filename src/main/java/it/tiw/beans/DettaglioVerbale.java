package it.tiw.beans;

/**
 * Bean che rappresenta un dettaglio di un verbale,
 * associando uno studente al verbale con il voto assegnato.
 */
public class DettaglioVerbale {
    private int idVerbale;
    private int idStudente;
    private String voto;

    /**
     * Costruttore vuoto.
     */
    public DettaglioVerbale() {}

    /**
     * Restituisce l'identificativo del verbale.
     *
     * @return L'id del verbale.
     */
    public int getIdVerbale() {
        return idVerbale;
    }

    /**
     * Imposta l'identificativo del verbale.
     *
     * @param idVerbale Identificativo del verbale.
     */
    public void setIdVerbale(int idVerbale) {
        this.idVerbale = idVerbale;
    }

    /**
     * Restituisce l'identificativo dello studente associato al verbale.
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
     * Restituisce il voto assegnato nello specifico verbale.
     *
     * @return Il voto come stringa.
     */
    public String getVoto() {
        return voto;
    }

    /**
     * Imposta il voto nello specifico verbale.
     *
     * @param voto Voto da assegnare.
     */
    public void setVoto(String voto) {
        this.voto = voto;
    }
}
