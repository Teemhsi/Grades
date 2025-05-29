package it.tiw.controller.professor;

import it.tiw.beans.Corso;
import it.tiw.beans.Utente;
import it.tiw.dao.CorsoDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet che gestisce la home page del docente, mostrando i corsi assegnati.
 * <p>
 * Controlla che l'utente sia autenticato e abbia ruolo "docente".
 * Recupera i corsi associati al docente.
 */
@WebServlet("/professor-home")
public class ProfessorHomeServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Utente professor = (Utente) req.getSession().getAttribute("user");

        if (professor == null || !"docente".equalsIgnoreCase(professor.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Utente non autorizzato o non autenticato\"}");
            return;
        }

        try {
            CorsoDAO corsoDAO = new CorsoDAO(connection);
            List<Corso> corsi = corsoDAO.findCorsiByDocenteIdOrderedDesc(professor.getIdUtente());

            com.google.gson.Gson gson = new com.google.gson.Gson();
            String json = gson.toJson(corsi);
            resp.getWriter().write(json);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Errore database: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
