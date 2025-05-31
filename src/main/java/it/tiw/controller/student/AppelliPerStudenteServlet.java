package it.tiw.controller.student;

import it.tiw.beans.Appello;
import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
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

/**
 * Servlet che restituisce gli appelli di un corso per uno studente autenticato in formato JSON.
 */
@WebServlet("/AppelliStudente")
public class AppelliPerStudenteServlet extends HttpServlet {
    private Connection connection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta GET per restituire gli appelli di un corso specifico per lo studente loggato.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errore servlet
     * @throws IOException      in caso di errore I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonResponse = new JsonObject();

        // Recupera utente dalla sessione e verifica ruolo
        Utente student = (Utente) req.getSession().getAttribute("user");
        if (student == null || !"studente".equalsIgnoreCase(student.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Recupera e valida il parametro idCorso
        String idCorsoParam = req.getParameter("id");
        int idCorso;
        try {
            if (idCorsoParam == null || idCorsoParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametro 'id' mancante");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }
            idCorso = Integer.parseInt(idCorsoParam);
            if (idCorso <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametro id non valido");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametro 'id' non valido");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);

            // Ottiene gli appelli per il corso e studente, ordinati decrescente
            List<Appello> appelli = appelloDAO.findAppelliByCorsoAndStudenteOrderedDesc(idCorso, student.getIdUtente());
            System.out.println(appelli.get(0).getDataAppello());

            // Restituisce gli appelli in formato JSON
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(appelli));

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Errore database: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Errore server: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
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