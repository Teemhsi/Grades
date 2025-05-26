package it.tiw.beans;

import java.sql.Date;

/**
 * Bean che rappresenta un appello d'esame associato a un corso.
 * Ogni appello ha un identificativo, un corso di riferimento e una data.
 *
 * Utilizzato nel modello MVC come oggetto di trasporto dati (DTO).
 *
 */
public class Appello {
    private int idAppello;
    private int idCorso;
    private Date dataAppello;

    /**
     * Costruttore vuoto.
     */
    public Appello() {}


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
     * @param idAppello Identificativo univoco da assegnare all'appello.
     */
    public void setIdAppello(int idAppello) {
        this.idAppello = idAppello;
    }

    /**
     * Restituisce l'identificativo del corso associato all'appello.
     *
     * @return L'id del corso.
     */
    public int getIdCorso() {
        return idCorso;
    }

    /**
     * Imposta l'identificativo del corso per l'appello.
     *
     * @param idCorso Identificativo del corso.
     */
    public void setIdCorso(int idCorso) {
        this.idCorso = idCorso;
    }

    /**
     * Restituisce la data dell'appello.
     *
     * @return La data dell'appello.
     */
    public Date getDataAppello() {
        return dataAppello;
    }

    /**
     * Imposta la data dell'appello.
     *
     * @param dataAppello Data da assegnare all'appello.
     */
    public void setDataAppello(Date dataAppello) {
        this.dataAppello = dataAppello;
    }

    /**
     * Rappresentazione testuale dell'appello.
     *
     * @return Stringa con id, idCorso e data.
     */
    @Override
    public String toString() {
        return "Appello [idAppello=" + idAppello + ", idCorso=" + idCorso + ", dataAppello=" + dataAppello + "]";
    }
}
