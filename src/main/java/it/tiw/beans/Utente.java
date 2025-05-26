package it.tiw.beans;

/**
 * Bean che rappresenta un utente con email e ruolo.
 */
public class Utente {
    private int idUtente;
    private String email;
    private String ruolo;

    /**
     * Costruttore vuoto.
     */
    public Utente() {}

    /**
     * Restituisce l'id dell'utente.
     *
     * @return id utente.
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'id dell'utente.
     *
     * @param idUtente id utente da impostare.
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce l'email dell'utente.
     *
     * @return email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta l'email dell'utente.
     *
     * @param email email da impostare.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return ruolo.
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Imposta il ruolo dell'utente.
     *
     * @param ruolo ruolo da impostare.
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "idUtente=" + idUtente +
                ", email='" + email + '\'' +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }
}
