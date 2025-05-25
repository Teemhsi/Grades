package it.tiw.beans;

public class Corso {
    private int idCorso;
    private String nome;
    private int idDocente;

    public int getIdCorso() { return idCorso; }
    public void setIdCorso(int idCorso) { this.idCorso = idCorso; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }
    @Override
    public String toString() {
        return "Corso{idCorso=" + idCorso + ", nome='" + nome + "', idDocente=" + idDocente + "}";
    }

}

