package it.tiw.dao;

import it.tiw.beans.*;
import it.tiw.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IscrizioneDAO {
    private final Connection connection;

    public IscrizioneDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean UpdateIscrizioneByStudenIdandAppelloId(int idStudente, int idAppello, String voto) throws SQLException {
        String query = "UPDATE grades.Iscrizioni " +
                "SET grades.Iscrizioni.voto = ?, grades.Iscrizioni.stato_valutazione = 'Inserito' " +
                "WHERE grades.Iscrizioni.id_studente = ? AND grades.Iscrizioni.id_appello = ?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, voto);
            ps.setInt(2, idStudente);
            ps.setInt(3, idAppello);
            System.out.println("query being executed is: " + query);
            System.out.println("id studente: " + idStudente +" id appeloo: " + idAppello + " voto: " + voto);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Nessuna riga aggiornata. Controlla id_studente e id_appello.");
            }else{
                return affected == 1;
            }
        }
    }

    public boolean pubblicaVotiInseriti(int idAppello) throws SQLException {
        String query = "UPDATE grades.Iscrizioni SET stato_valutazione = 'Pubblicato' " +
                "WHERE id_appello = ? AND stato_valutazione = 'Inserito'";

        boolean success = false;
        try {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, idAppello);
                int updatedRows = ps.executeUpdate();
                success = updatedRows >= 1;

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                throw e;
            }
        } finally {
            connection.setAutoCommit(true); // reset autocommit
        }

        return success;
    }

    public boolean rifiutaVotipubblicati(int idAppello, int idStudente) throws SQLException {
        String query = "UPDATE grades.Iscrizioni SET stato_valutazione = 'Rifiutato' " +
                "WHERE id_appello = ? AND id_studente = ? AND stato_valutazione = 'Pubblicato'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idAppello);
            ps.setInt(2, idStudente);
            return ps.executeUpdate() == 1;  // ritorna il numero di righe aggiornate
        }
    }
    public void sendEmailOnPublish(int idAppello) throws SQLException{
        String query = "SELECT u.email FROM grades.Iscrizioni I " +
                "join grades.Studenti s on I.id_studente = s.id_utente " +
                "join grades.Utenti u on u.id_utente = s.id_utente " +
                "WHERE I.id_appello = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idAppello);

            System.out.println("query for emails executed is: " + query);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Util.sendEmail(rs.getString("email"));
                }
            }

        }
    }
    public List<Object[]> findIscrizioneByIdCorsoIdAppelloStudentId(int idStudente, int idAppello, int idCorso) throws SQLException {
        String query =
                "SELECT I.id_studente, I.id_appello, I.voto, I.stato_valutazione, A.data_appello, C.nome, S.matricola, S.nome AS nome_studente, S.cognome, S.corso_di_laurea " +
                        "FROM grades.Iscrizioni I " +
                        "join grades.Appelli A on I.id_appello = A.id_appello " +
                        "join grades.Corsi C on C.id_corso = A.id_corso " +
                        "join grades.Studenti S on S.id_utente = I.id_studente " +
                        "where I.id_studente = ? and A.id_corso = ? and A.id_appello = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idStudente);
            ps.setInt(2, idCorso);
            ps.setInt(3, idAppello);
            try (ResultSet rs = ps.executeQuery()) {
                List<Object[]> results = new ArrayList<>();

                while (rs.next()) {

                    Studente studente = new Studente();
                    studente.setMatricola(rs.getString("matricola"));
                    studente.setNome(rs.getString("nome_studente"));
                    studente.setCognome(rs.getString("cognome"));
                    studente.setCorsoDiLaurea(rs.getString("corso_di_laurea"));


                    Iscrizione iscrizione = new Iscrizione();
                    iscrizione.setIdStudente(rs.getInt("id_studente"));
                    iscrizione.setIdAppello(rs.getInt("id_appello"));
                    iscrizione.setVoto(rs.getString("voto"));
                    iscrizione.setStatoValutazione(rs.getString("stato_valutazione"));

                    Appello appello = new Appello();
                    appello.setDataAppello(rs.getDate("data_appello"));

                    Corso corso = new Corso();
                    corso.setNome(rs.getString("nome"));


                    // Risultato combinato
                    Object[] row = new Object[] { iscrizione, appello, corso, studente};
                    results.add(row);
                }

                return results;
            }
        }
    }
    // Aggiungi questo metodo nella classe IscrizioneDAO

    /**
     * Aggiorna multipli voti in una transazione atomica.
     * Se anche un solo aggiornamento fallisce, viene fatto rollback di tutto.
     *
     * @param votiList Lista di oggetti contenenti idStudente, idAppello e voto
     * @return numero di righe aggiornate con successo
     * @throws SQLException se si verifica un errore durante l'operazione
     */
    public int updateMultipleVotiTransactional(List<VotoUpdate> votiList) throws SQLException {
        String query = "UPDATE grades.Iscrizioni " +
                "SET grades.Iscrizioni.voto = ?, grades.Iscrizioni.stato_valutazione = 'Inserito' " +
                "WHERE grades.Iscrizioni.id_studente = ? AND grades.Iscrizioni.id_appello = ? " +
                "AND grades.Iscrizioni.stato_valutazione = 'Non inserito'";

        int totalUpdated = 0;
        boolean autoCommit = connection.getAutoCommit();

        try {
            // Disabilita auto-commit per gestire la transazione
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                for (VotoUpdate voto : votiList) {
                    ps.setString(1, voto.getVoto());
                    ps.setInt(2, voto.getIdStudente());
                    ps.setInt(3, voto.getIdAppello());

                    int affected = ps.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException(
                                String.format("Impossibile aggiornare voto per studente %d nell'appello %d. " +
                                                "Verifica che lo studente sia iscritto e lo stato sia 'Non inserito'.",
                                        voto.getIdStudente(), voto.getIdAppello())
                        );
                    }
                    totalUpdated += affected;
                }
            }

            // Se arriviamo qui, tutti gli aggiornamenti sono andati a buon fine
            connection.commit();
            return totalUpdated;

        } catch (SQLException e) {
            // In caso di errore, rollback di tutte le modifiche
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                // Log dell'errore di rollback ma rilanciamo l'eccezione originale
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            // Ripristina l'auto-commit allo stato originale
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe helper per passare i dati
    public static class VotoUpdate {
        private final int idStudente;
        private final int idAppello;
        private final String voto;

        public VotoUpdate(int idStudente, int idAppello, String voto) {
            this.idStudente = idStudente;
            this.idAppello = idAppello;
            this.voto = voto;
        }

        public int getIdStudente() { return idStudente; }
        public int getIdAppello() { return idAppello; }
        public String getVoto() { return voto; }
    }

}
