package it.tiw.beans;

import java.sql.Timestamp;

/**
 * Bean che rappresenta un verbale associato a un appello.
 */
public class Verbale {
    private int idVerbale;
    private String codiceVerbale;
    private int idAppello;
    private Timestamp dataCreazione;

    /**
     * Costruttore vuoto.
     */
    public Verbale() {}

    /**
     * Restituisce l'id del verbale.
     *
     * @return id del verbale.
     */
    public int getIdVerbale() {
        return idVerbale;
    }

    /**
     * Imposta l'id del verbale.
     *
     * @param idVerbale id da impostare.
     */
    public void setIdVerbale(int idVerbale) {
        this.idVerbale = idVerbale;
    }

    /**
     * Restituisce il codice identificativo del verbale.
     *
     * @return codice del verbale.
     */
    public String getCodiceVerbale() {
        return codiceVerbale;
    }

    /**
     * Imposta il codice identificativo del verbale.
     *
     * @param codiceVerbale codice da impostare.
     */
    public void setCodiceVerbale(String codiceVerbale) {
        this.codiceVerbale = codiceVerbale;
    }

    /**
     * Restituisce l'id dell'appello associato.
     *
     * @return id appello.
     */
    public int getIdAppello() {
        return idAppello;
    }

    /**
     * Imposta l'id dell'appello associato.
     *
     * @param idAppello id da impostare.
     */
    public void setIdAppello(int idAppello) {
        this.idAppello = idAppello;
    }

    /**
     * Restituisce la data e ora di creazione del verbale.
     *
     * @return data di creazione.
     */
    public Timestamp getDataCreazione() {
        return dataCreazione;
    }

    /**
     * Imposta la data e ora di creazione del verbale.
     *
     * @param dataCreazione data da impostare.
     */
    public void setDataCreazione(Timestamp dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    @Override
    public String toString() {
        return "Verbale{" +
                "idVerbale=" + idVerbale +
                ", codiceVerbale='" + codiceVerbale + '\'' +
                ", idAppello=" + idAppello +
                ", dataCreazione=" + dataCreazione +
                '}';
    }
}
