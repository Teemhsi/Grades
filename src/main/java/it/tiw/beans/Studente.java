package it.tiw.beans;

public class Studente {
    private int idUtente;
    private String matricola;
    private String nome;
    private String cognome;
    private String email;//op
    private String corsoDiLaurea;
    private String voto;//op
    private String stato_valutazione;//op

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCorsoDiLaurea() { return corsoDiLaurea; }
    public void setCorsoDiLaurea(String corsoDiLaurea) { this.corsoDiLaurea = corsoDiLaurea; }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public String getStato_valutazione() {
        return stato_valutazione;
    }

    public void setStato_valutazione(String stato_valutazione) {
        this.stato_valutazione = stato_valutazione;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

