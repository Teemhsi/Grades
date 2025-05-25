package it.tiw.dao;


import it.tiw.beans.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {
    private Connection connection;

    public UtenteDAO(Connection connection) {
        this.connection = connection;
    }

    public Utente checkCredentials(String email, String password) throws SQLException {
        String query = "SELECT id_utente, email, password, ruolo FROM Utenti WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (storedPassword.equals(password)) { // ⚠️ Hash comparison in production!
                        Utente user = new Utente();
                        user.setIdUtente(rs.getInt("id_utente"));
                        user.setEmail(rs.getString("email"));
                        user.setRuolo(rs.getString("ruolo"));
                        // Do NOT set password
                        return user;
                    }
                }
            }
        }
        return null;
    }
}
