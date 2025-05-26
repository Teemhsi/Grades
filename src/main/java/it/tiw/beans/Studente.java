package it.tiw.beans;

/**
 * Bean che rappresenta uno studente con informazioni personali e dati relativi alla valutazione.
 * I campi email, voto e stato di valutazione sono opzionali.
 */
public class Studente {
    private int idUtente;
    private String matricola;
    private String nome;
    private String cognome;
    private String email; // opzionale
    private String corsoDiLaurea;
    private String voto; // opzionale
    private String stato_valutazione; // opzionale

    /**
     * Costruttore vuoto.
     */
    public Studente() {}

    /**
     * Restituisce l'identificativo dell'utente studente.
     *
     * @return L'id utente.
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'identificativo dell'utente studente.
     *
     * @param idUtente L'id utente da impostare.
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce la matricola dello studente.
     *
     * @return La matricola.
     */
    public String getMatricola() {
        return matricola;
    }

    /**
     * Imposta la matricola dello studente.
     *
     * @param matricola La matricola da impostare.
     */
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    /**
     * Restituisce il nome dello studente.
     *
     * @return Il nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dello studente.
     *
     * @param nome Il nome da impostare.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dello studente.
     *
     * @return Il cognome.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome dello studente.
     *
     * @param cognome Il cognome da impostare.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce l'email dello studente.
     *
     * @return L'email, opzionale.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta l'email dello studente.
     *
     * @param email L'email da impostare.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce il corso di laurea dello studente.
     *
     * @return Il corso di laurea.
     */
    public String getCorsoDiLaurea() {
        return corsoDiLaurea;
    }

    /**
     * Imposta il corso di laurea dello studente.
     *
     * @param corsoDiLaurea Il corso di laurea da impostare.
     */
    public void setCorsoDiLaurea(String corsoDiLaurea) {
        this.corsoDiLaurea = corsoDiLaurea;
    }

    /**
     * Restituisce il voto dello studente.
     *
     * @return Il voto, opzionale.
     */
    public String getVoto() {
        return voto;
    }

    /**
     * Imposta il voto dello studente.
     *
     * @param voto Il voto da impostare.
     */
    public void setVoto(String voto) {
        this.voto = voto;
    }

    /**
     * Restituisce lo stato di valutazione dello studente.
     *
     * @return Lo stato di valutazione, opzionale.
     */
    public String getStato_valutazione() {
        return stato_valutazione;
    }

    /**
     * Imposta lo stato di valutazione dello studente.
     *
     * @param stato_valutazione Lo stato di valutazione da impostare.
     */
    public void setStato_valutazione(String stato_valutazione) {
        this.stato_valutazione = stato_valutazione;
    }

    @Override
    public String toString() {
        return "Studente{" +
                "idUtente=" + idUtente +
                ", matricola='" + matricola + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", corsoDiLaurea='" + corsoDiLaurea + '\'' +
                ", voto='" + voto + '\'' +
                ", stato_valutazione='" + stato_valutazione + '\'' +
                '}';
    }
}
