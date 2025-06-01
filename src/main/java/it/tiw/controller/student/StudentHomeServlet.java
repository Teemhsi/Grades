package it.tiw.controller.student;

import it.tiw.beans.Corso;
import it.tiw.beans.Utente;
import it.tiw.dao.CorsoDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/student-home")
public class StudentHomeServlet extends HttpServlet {
    private Connection connection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonResponse = new JsonObject();

        Utente studente = (Utente) req.getSession().getAttribute("user");
        if (studente == null || !"studente".equalsIgnoreCase(studente.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            CorsoDAO corsoDAO = new CorsoDAO(connection);
            List<Corso> corsi = corsoDAO.findCorsiByStudentIdOrderedDesc(studente.getIdUtente());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(corsi));

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Server internal error ");
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Server internal error ");
            resp.getWriter().write(jsonResponse.toString());
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            getServletContext().log("Error closing database connection", e);
        }
    }
}