package it.tiw.controller.student;

import com.google.gson.GsonBuilder;
import it.tiw.beans.*;
import it.tiw.dao.IscrizioneDAO;
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
import java.util.Arrays;
import java.util.List;

/**
 * Servlet per la gestione dei voti dello studente in formato JSON.
 */
@WebServlet("/GestioneVotiStudente")
public class GestioneVotiStudenteServlet extends HttpServlet {
    private Connection connection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject jsonResponse = new JsonObject();

        // Controllo autenticazione e ruolo
        Utente utente = (Utente) req.getSession().getAttribute("user");
        if (utente == null || !"studente".equalsIgnoreCase(utente.getRuolo())) {
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
            jsonResponse.addProperty("error", "Parametri obbligatori mancanti");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            int idAppello = Integer.parseInt(idAppelloStr);
            int idCorso = Integer.parseInt(idCorsoStr);
            int idStudente = utente.getIdUtente();

            if(idAppello < 1 || idCorso < 1){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametri numerici non validi");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            List<Object[]> risultati = iscrizioneDAO.findIscrizioneByIdCorsoIdAppelloStudentId(idStudente, idAppello, idCorso);

            if (risultati.isEmpty()) {
                // Nessuna iscrizione trovata
                jsonResponse.addProperty("iscrizionePresente", false);
                jsonResponse.addProperty("idCorso", idCorso);
            } else {
                Object[] risultato = risultati.get(0);

                Iscrizione iscrizione = (Iscrizione) risultato[0];
                Appello appello = (Appello) risultato[1];
                Corso corso = (Corso) risultato[2];
                Studente studente = (Studente) risultato[3];

                gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();

                jsonResponse.addProperty("iscrizionePresente", true);
                jsonResponse.add("iscrizione", gson.toJsonTree(iscrizione));
                jsonResponse.add("appello", gson.toJsonTree(appello));
                jsonResponse.add("corso", gson.toJsonTree(corso));
                jsonResponse.add("studente", gson.toJsonTree(studente));
                jsonResponse.addProperty("idCorso", idCorso);

                // Aggiungi lista voti validi per validazione
                jsonResponse.add("votiValidi", gson.toJsonTree(Arrays.asList(
                        "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30 e lode")));
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse.toString());

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Formato parametro non valido");
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
            getServletContext().log("Errore chiusura connessione database", e);
        }
    }
}