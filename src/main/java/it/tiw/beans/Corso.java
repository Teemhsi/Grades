package it.tiw.beans;

/**
 * Bean che rappresenta un corso universitario.
 * Ogni corso ha un identificativo, un nome e un docente associato.
 */
public class Corso {
    private int idCorso;
    private String nome;
    private int idDocente;

    /**
     * Costruttore vuoto.
     */
    public Corso() {}

    /**
     * Restituisce l'identificativo del corso.
     *
     * @return L'id del corso.
     */
    public int getIdCorso() {
        return idCorso;
    }

    /**
     * Imposta l'identificativo del corso.
     *
     * @param idCorso Identificativo univoco del corso.
     */
    public void setIdCorso(int idCorso) {
        this.idCorso = idCorso;
    }

    /**
     * Restituisce il nome del corso.
     *
     * @return Il nome del corso.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del corso.
     *
     * @param nome Nome da assegnare al corso.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce l'identificativo del docente associato al corso.
     *
     * @return L'id del docente.
     */
    public int getIdDocente() {
        return idDocente;
    }

    /**
     * Imposta l'identificativo del docente per il corso.
     *
     * @param idDocente Identificativo del docente.
     */
    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    @Override
    public String toString() {
        return "Corso{idCorso=" + idCorso + ", nome='" + nome + "', idDocente=" + idDocente + "}";
    }
}
