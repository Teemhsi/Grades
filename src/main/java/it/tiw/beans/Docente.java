package it.tiw.beans;

/**
 * Bean che rappresenta un docente dell'università.
 * Un docente è identificato da un id utente, un nome e un cognome.
 */
public class Docente {
    private int idUtente;
    private String nome;
    private String cognome;

    /**
     * Costruttore vuoto.
     */
    public Docente() {}

    /**
     * Restituisce l'identificativo utente del docente.
     *
     * @return L'id utente del docente.
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'identificativo utente del docente.
     *
     * @param idUtente Identificativo dell'utente docente.
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce il nome del docente.
     *
     * @return Il nome del docente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del docente.
     *
     * @param nome Nome da assegnare al docente.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome del docente.
     *
     * @return Il cognome del docente.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome del docente.
     *
     * @param cognome Cognome da assegnare al docente.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
