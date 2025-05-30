package it.tiw.controller.professor;

import it.tiw.beans.Studente;
import it.tiw.beans.Utente;
import it.tiw.dao.StudenteDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet che restituisce i dati dello studente in formato JSON
 * per la modifica del voto in modalità SPA
 */
@WebServlet("/VisualizzaFormModificaVoto")
@MultipartConfig
public class VisualizzaFormModificaVotoServlet extends HttpServlet {
    private Connection connection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Controllo autenticazione docente
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            JsonObject error = new JsonObject();
            error.addProperty("error", "Non autorizzato");
            resp.getWriter().write(error.toString());
            return;
        }

        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        // Verifica parametri obbligatori
        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null ||
                idStudenteStr.trim().isEmpty() || idAppelloStr.trim().isEmpty() ||
                idCorsoStr.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            JsonObject error = new JsonObject();
            error.addProperty("error", "Parametri mancanti");
            resp.getWriter().write(error.toString());
            return;
        }

        int idStudente, idAppello, idCorso;
        try {
            idStudente = Integer.parseInt(idStudenteStr);
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
            if(idStudente < 1 || idAppello < 1 || idCorso < 1){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                JsonObject error = new JsonObject();
                error.addProperty("error", "Parametri numerici non validi");
                resp.getWriter().write(error.toString());
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            JsonObject error = new JsonObject();
            error.addProperty("error", "Parametri non validi");
            resp.getWriter().write(error.toString());
            return;
        }

        try {
            StudenteDAO studenteDAO = new StudenteDAO(connection);
            Studente studente = studenteDAO.getStudentById(idStudente);

            if (studente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                JsonObject error = new JsonObject();
                error.addProperty("error", "Studente non trovato");
                resp.getWriter().write(error.toString());
                return;
            }

            // Costruisci la risposta JSON
            JsonObject response = new JsonObject();
            response.add("studente", gson.toJsonTree(studente));
            response.addProperty("idAppello", idAppello);
            response.addProperty("idCorso", idCorso);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(response.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            JsonObject error = new JsonObject();
            error.addProperty("error", "Server error: " + e.getMessage());
            resp.getWriter().write(error.toString());
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