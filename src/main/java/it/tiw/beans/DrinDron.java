package it.tiw.beans;

import java.sql.*;

public class DrinDron {
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                checkCredentials(connection);
            } else {
                System.out.println("Connection failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/grades", "root", "root");
            System.out.println("Connection successful.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }

    public static void checkCredentials(Connection connection) throws SQLException {
        String query = "SELECT * FROM Utenti";

        try (PreparedStatement pstatement = connection.prepareStatement(query);
             ResultSet result = pstatement.executeQuery()) {

            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            boolean found = false;
            while (result.next()) {
                found = true;
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = result.getString(i);
                    System.out.print(columnName + ": " + value + "  ");
                }
                System.out.println();
            }

            if (!found) {
                System.out.println("No users found.");
            }

        } catch (SQLException e) {
            throw new SQLException("Failure in user's data extraction", e);
        }
    }
}

