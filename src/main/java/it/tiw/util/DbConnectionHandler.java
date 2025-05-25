package it.tiw.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionHandler {

    public DbConnectionHandler() {
    }

    public static Connection getConnection(ServletContext context) throws UnavailableException {
        Connection connection = null;
        try {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("worked");

        } catch (Exception e) {
            System.out.println("error is:" + e.getMessage() + " " +  e.getCause());
            System.out.println("error is:" + e.getMessage() + " " +  e.getCause());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
