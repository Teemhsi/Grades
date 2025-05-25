package it.tiw.dao;

import it.tiw.beans.Appello;
import it.tiw.util.DbConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppelloDAO {
    private final Connection connection;

    public AppelloDAO(Connection connection) {
        this.connection = connection;
    }

    // This method now takes both the course ID and the professor ID to filter appelli
    public List<Appello> findAppelliByCorsoAndDocenteOrderedDesc(int idCorso, int idDocente) throws SQLException {
        List<Appello> appelli = new ArrayList<>();
        String query = "SELECT a.id_appello, a.id_corso, a.data_appello " +
                "FROM grades.Appelli AS a " +
                "INNER JOIN grades.Corsi AS c ON c.id_corso = a.id_corso " +
                "WHERE a.id_corso = ? AND c.id_docente = ? " +
                "ORDER BY a.data_appello DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCorso);      // Setting the course ID
            stmt.setInt(2, idDocente);    // Setting the professor ID
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appello appello = new Appello();
                appello.setIdAppello(rs.getInt("id_appello"));
                appello.setIdCorso(rs.getInt("id_corso"));
                appello.setDataAppello(rs.getDate("data_appello"));
                appelli.add(appello);
            }
        }

        return appelli;
    }
    // This method now takes both the course ID and the professor ID to filter appelli
    public Date findAppelloDateByCourseCallDocente(int idCorso, int idDocente, int idAppello) throws SQLException {
        Appello appello = new Appello();
        String query = "SELECT a.data_appello " +
                "FROM grades.Appelli AS a " +
                "INNER JOIN grades.Corsi AS c ON c.id_corso = a.id_corso " +
                "WHERE a.id_corso = ? AND c.id_docente = ? and a.id_appello = ? " +
                "limit 1";
        System.out.println("excuting query: " + query);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCorso);      // Setting the course ID
            stmt.setInt(2, idDocente);    // Setting the professor ID
            stmt.setInt(3, idAppello);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appello.setDataAppello(rs.getDate("data_appello"));
            }
        }
        return appello.getDataAppello();
    }

    // This method now takes both the course ID and the student ID to filter appelli
    public List<Appello> findAppelliByCorsoAndStudenteOrderedDesc(int idCorso, int idStudente) throws SQLException {
        List<Appello> appelli = new ArrayList<>();
        String query = " SELECT I.id_corso, A.id_appello, A.data_appello FROM grades.IscrizioniCorsi I " +
                "join grades.Appelli A on I.id_corso = A.id_corso " +
                "where I.id_corso = ? and I.id_studente = ? " +
                "order by A.data_appello desc";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idCorso);      // Setting the course ID
            stmt.setInt(2, idStudente);    // Setting the student ID
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appello appello = new Appello();
                appello.setIdAppello(rs.getInt("id_appello"));
                appello.setIdCorso(rs.getInt("id_corso"));
                appello.setDataAppello(rs.getDate("data_appello"));
                appelli.add(appello);
            }
        }

        return appelli;
    }
}
