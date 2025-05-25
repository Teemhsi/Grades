package it.tiw.dao;

import it.tiw.beans.Corso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CorsoDAO {
    private final Connection connection;

    public CorsoDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Corso> findCorsiByDocenteIdOrderedDesc(int idDocente) throws SQLException {
        String query = "SELECT * FROM Corsi WHERE id_docente = ? ORDER BY nome DESC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idDocente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Corso> corsi = new ArrayList<>();
                while (rs.next()) {
                    Corso corso = new Corso();
                    corso.setIdCorso(rs.getInt("id_corso"));
                    corso.setNome(rs.getString("nome"));
                    corso.setIdDocente(rs.getInt("id_docente"));
                    corsi.add(corso);
                }
                return corsi;
            }
        }
    }
    public List<Corso> findCorsiByStudentIdOrderedDesc(int idStudente) throws SQLException {
        String query = "SELECT C.id_corso, C.nome FROM grades.IscrizioniCorsi I " +
                "join grades.Corsi C on C.id_corso = I.id_corso " +
                "where I.id_studente = ? " +
                "order by C.nome desc";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idStudente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Corso> corsi = new ArrayList<>();
                while (rs.next()) {
                    Corso corso = new Corso();
                    corso.setIdCorso(rs.getInt("id_corso"));
                    corso.setNome(rs.getString("nome"));
                    corsi.add(corso);
                }
                return corsi;
            }
        }
    }
}
