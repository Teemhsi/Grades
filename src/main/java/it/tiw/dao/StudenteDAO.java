package it.tiw.dao;

import it.tiw.beans.Studente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StudenteDAO {
    private final Connection conn;

    public StudenteDAO(Connection connection) {
        this.conn = connection;
    }



    public Studente getStudentById(int id_studente){
        String query =
                "SELECT s.id_utente, s.matricola, s.cognome, s.nome, s.corso_di_laurea " +
                        "FROM grades.Studenti s " +
                        "WHERE s.id_utente= ? "
                        ;
        Studente studente = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id_studente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    studente = new Studente();
                    studente.setIdUtente(rs.getInt("id_utente"));
                    studente.setMatricola(rs.getString("matricola"));
                    studente.setCognome(rs.getString("cognome"));
                    studente.setNome(rs.getString("nome"));
                    //studente.setEmail(rs.getString("email"));
                    studente.setCorsoDiLaurea(rs.getString("corso_di_laurea"));
                    //studente.setVoto(rs.getString("voto"));
                    //studente.setStato_valutazione(rs.getString("stato_valutazione"));
                }
            }
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }

        return studente;
    }

    public List<Studente> getIscrittiPerAppello(int idAppello, int idCorso, int docenteId) throws SQLException {
        String query =
                "SELECT s.matricola, s.cognome, s.nome, u.email, s.corso_di_laurea, i.voto, i.stato_valutazione " +
                        "FROM grades.Iscrizioni i " +
                        "JOIN grades.Utenti u ON i.id_studente = u.id_utente " +
                        "JOIN grades.Studenti s ON s.id_utente = u.id_utente " +
                        "JOIN grades.Appelli a ON i.id_appello = a.id_appello " +
                        "JOIN grades.Corsi c ON a.id_corso = c.id_corso " +
                        "JOIN grades.Docenti d ON c.id_docente = d.id_utente " +
                        "WHERE i.id_appello = ? AND c.id_corso = ? AND d.id_utente = ? " +
                        "ORDER BY s.cognome DESC, s.nome DESC";

        List<Studente> studenti = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idAppello);
            ps.setInt(2, idCorso);
            ps.setInt(3, docenteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Studente studente = new Studente();
                    studente.setMatricola(rs.getString("matricola"));
                    studente.setCognome(rs.getString("cognome"));
                    studente.setNome(rs.getString("nome"));
                    studente.setEmail(rs.getString("email"));
                    studente.setCorsoDiLaurea(rs.getString("corso_di_laurea"));
                    studente.setVoto(rs.getString("voto"));
                    studente.setStato_valutazione(rs.getString("stato_valutazione"));
                    studenti.add(studente);
                }
            }
        }

        return studenti;
    }
    public List<Studente> getIscrittiOrdinati(int idAppello, int idCorso, int docenteId, String sortField, String sortDir) throws SQLException {
        List<String> allowedFields = Arrays.asList("matricola", "cognome", "nome", "email", "corso_di_laurea", "voto", "stato_valutazione");

        if (!allowedFields.contains(sortField)) {
            sortField = "cognome";
        }
        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            sortDir = "asc";
        }

        String query =
                "SELECT s.id_utente, s.matricola, s.cognome, s.nome, u.email, s.corso_di_laurea, i.voto, i.stato_valutazione " +
                        "FROM grades.Iscrizioni i " +
                        "JOIN grades.Utenti u ON i.id_studente = u.id_utente " +
                        "JOIN grades.Studenti s ON s.id_utente = u.id_utente " +
                        "JOIN grades.Appelli a ON i.id_appello = a.id_appello " +
                        "JOIN grades.Corsi c ON a.id_corso = c.id_corso " +
                        "JOIN grades.Docenti d ON c.id_docente = d.id_utente " +
                        "WHERE i.id_appello = ? AND c.id_corso = ? AND d.id_utente = ?";

        // Nota: rimuoviamo l'ORDER BY dalla query se sortField è "voto"
        if (!"voto".equalsIgnoreCase(sortField)) {
            query += " ORDER BY " + sortField + " " + sortDir;
        }

        List<Studente> studenti = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idAppello);
            ps.setInt(2, idCorso);
            ps.setInt(3, docenteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Studente studente = new Studente();
                    studente.setIdUtente(rs.getInt("id_utente"));
                    studente.setMatricola(rs.getString("matricola"));
                    studente.setCognome(rs.getString("cognome"));
                    studente.setNome(rs.getString("nome"));
                    studente.setEmail(rs.getString("email"));
                    studente.setCorsoDiLaurea(rs.getString("corso_di_laurea"));
                    studente.setVoto(rs.getString("voto"));
                    studente.setStato_valutazione(rs.getString("stato_valutazione"));
                    studenti.add(studente);
                }
            }


        // Ordinamento semantico dei voti in Java, se richiesto
        if ("voto".equalsIgnoreCase(sortField)) {
            Comparator<Studente> votoComparator = Comparator.comparingInt(s -> votoToPriority(s.getVoto()));
            if ("desc".equalsIgnoreCase(sortDir)) {
                votoComparator = votoComparator.reversed();
            }
            studenti.sort(votoComparator);
        }
    }
        return studenti;
    }

    // Metodo di supporto per convertire i voti in un indice ordinabile semanticamente
    private int votoToPriority(String voto) {
        if (voto == null || voto.trim().isEmpty()) return 0;
        switch (voto.toLowerCase()) {
            case "assente": return 1;
            case "rimandato": return 2;
            case "riprovato": return 3;
            case "30 e lode": return 17;
            default:
                try {
                    int val = Integer.parseInt(voto);
                    if (val >= 18 && val <= 30) {
                        return 3 + (val - 17); // 18 → 4, ..., 30 → 16
                    }
                } catch (NumberFormatException e) {
                    // Non è un voto valido numerico
                }
                return Integer.MAX_VALUE; // Valore sconosciuto o non gestito
        }
    }

}
