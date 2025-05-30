package it.tiw.controller.professor;

import it.tiw.dao.IscrizioneDAO;
import it.tiw.dao.VerbaleDAO;
import it.tiw.dao.AppelloDAO;
import it.tiw.beans.Utente;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet che gestisce la verbalizzazione degli iscritti ad un appello.
 * Restituisce una risposta JSON invece di fare redirect.
 */
@WebServlet("/VerbalizzaIscritti")
@MultipartConfig
public class VerbalizzaIscrittiServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta POST per verbalizzare gli iscritti.
     * Restituisce JSON con successo o errore.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonResponse = new JsonObject();

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idAppelloStr == null || idCorsoStr == null || idAppelloStr.trim().isEmpty() || idCorsoStr.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametro 'idAppello' o 'idCorso' mancante");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
            if (idAppello <= 0 || idCorso <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametri 'idAppello' e 'idCorso' devono essere positivi");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametro 'idAppello' o 'idCorso' non valido");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            // Verifica che l'appello appartenga al docente
            if (appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello) == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Accesso negato all'appello");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);
            // Crea il verbale e aggiorna le iscrizioni
            String idVerbale = verbaleDAO.creaVerbaleConIscrizioni(idAppello);

            if (idVerbale == null || idVerbale.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Errore durante la verbalizzazione");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            // Successo
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Verbalizzazione completata con successo");
            jsonResponse.addProperty("idVerbale", idVerbale);
            jsonResponse.addProperty("idAppello", idAppello);
            jsonResponse.addProperty("idCorso", idCorso);
            resp.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Database error: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Server error: " + e.getMessage());
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