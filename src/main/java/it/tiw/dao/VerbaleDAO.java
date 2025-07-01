package it.tiw.dao;

import it.tiw.beans.Studente;
import it.tiw.beans.Verbale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VerbaleDAO {
    private final Connection connection;

    public VerbaleDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Object[]> findVerbaliByDocenteId(int docenteId) throws SQLException {
        String query =
                "SELECT v.codice_verbale, v.data_creazione, " +
                        "a.data_appello, " +
                        "c.nome AS nome_corso, " +
                        "s.matricola, s.nome, s.cognome, " +
                        "d.voto " +
                        "FROM grades.Verbali v " +
                        "JOIN grades.Appelli a ON v.id_appello = a.id_appello " +
                        "JOIN grades.Corsi c ON a.id_corso = c.id_corso " +
                        "JOIN grades.DettaglioVerbale d ON v.id_verbale = d.id_verbale " +
                        "JOIN grades.Studenti s ON d.id_studente = s.id_utente " +
                        "WHERE c.id_docente = ? " +
                        "ORDER BY c.nome ASC, a.data_appello ASC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, docenteId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Object[]> results = new ArrayList<>();

                while (rs.next()) {
                    // Verbale
                    Verbale verbale = new Verbale();
                    verbale.setCodiceVerbale(rs.getString("codice_verbale"));
                    verbale.setDataCreazione(rs.getTimestamp("data_creazione"));


                    // Appello (solo data)
                    Date dataAppello = rs.getDate("data_appello");

                    // Nome corso
                    String nomeCorso = rs.getString("nome_corso");

                    // Studente
                    Studente studente = new Studente();
                    studente.setMatricola(rs.getString("matricola"));
                    studente.setNome(rs.getString("nome"));
                    studente.setCognome(rs.getString("cognome"));
                    studente.setVoto(rs.getString("voto"));

                    // Risultato combinato
                    Object[] row = new Object[] { verbale, dataAppello, nomeCorso, studente };
                    results.add(row);
                }

                return results;
            }
        }
    }

    /***
     *
     * Selezione: recupera tutte le iscrizioni con stato "Pubblicato" o "Rifiutato" per l'appello specificato
     * Inserimento verbale: crea il record principale del verbale nella tabella Verbali
     * Inserimento dettaglio: aggiunge i singoli risultati nella tabella DettaglioVerbale
     * Aggiornamento iscrizioni: cambia lo stato delle iscrizioni da "Pubblicato/Rifiutato" a "Verbalizzato"
     */
    public String creaVerbaleConIscrizioni(int idAppello) throws SQLException {
        String selectIscrizioniSQL = "SELECT id_studente, voto, stato_valutazione " +
                "FROM Iscrizioni " +
                "WHERE id_appello = ? AND (stato_valutazione = 'Pubblicato' OR stato_valutazione = 'Rifiutato')";

        String insertVerbaleSQL = "INSERT INTO Verbali (codice_verbale, id_appello, data_creazione) VALUES (?, ?, ?)";

        String insertDettaglioSQL = "INSERT INTO DettaglioVerbale (id_verbale, id_studente, voto) VALUES (?, ?, ?)";

        // Modificato per aggiornare anche il voto, non solo lo stato_valutazione
        String updateIscrizioneSQL = "UPDATE Iscrizioni SET stato_valutazione = 'Verbalizzato', voto = ? WHERE id_studente = ? AND id_appello = ?";

        String codiceVerbale = "VER-" + idAppello + "-" + System.currentTimeMillis();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psSelect = connection.prepareStatement(selectIscrizioniSQL)) {
                psSelect.setInt(1, idAppello);

                try (ResultSet rs = psSelect.executeQuery()) {
                    int idVerbale;

                    // Inserisce il Verbale e recupera la chiave generata
                    try (PreparedStatement psInsertVerbale = connection.prepareStatement(insertVerbaleSQL, Statement.RETURN_GENERATED_KEYS)) {
                        psInsertVerbale.setString(1, codiceVerbale);
                        psInsertVerbale.setInt(2, idAppello);
                        psInsertVerbale.setTimestamp(3, now);

                        int rows = psInsertVerbale.executeUpdate();
                        if (rows == 0) {
                            connection.rollback();
                            return null;
                        }

                        try (ResultSet generatedKeys = psInsertVerbale.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                idVerbale = generatedKeys.getInt(1);
                            } else {
                                connection.rollback();
                                return null;
                            }
                        }
                    }

                    try (PreparedStatement psInsertDettaglio = connection.prepareStatement(insertDettaglioSQL);
                         PreparedStatement psUpdateIscrizione = connection.prepareStatement(updateIscrizioneSQL)) {

                        while (rs.next()) {
                            int idStudente = rs.getInt("id_studente");
                            String stato = rs.getString("stato_valutazione");
                            String votoOriginale = rs.getString("voto");

                            // Se rifiutato, cambia il voto in "Rimandato"
                            String votoFinale = "Rifiutato".equalsIgnoreCase(stato) ? "Rimandato" : votoOriginale;

                            // Inserisce nel dettaglio verbale
                            psInsertDettaglio.setInt(1, idVerbale);
                            psInsertDettaglio.setInt(2, idStudente);
                            psInsertDettaglio.setString(3, votoFinale);
                            psInsertDettaglio.executeUpdate();

                            // Aggiorna iscrizione con nuovo voto (se rifiutato, sarà "Rimandato") e stato
                            psUpdateIscrizione.setString(1, votoFinale);
                            psUpdateIscrizione.setInt(2, idStudente);
                            psUpdateIscrizione.setInt(3, idAppello);
                            psUpdateIscrizione.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();
            return codiceVerbale;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }



}
