package it.tiw.controller.professor;

import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.dao.IscrizioneDAO;
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
import java.util.Arrays;
import java.util.List;

/**
 * Servlet per modificare il voto di uno studente in un appello specifico.
 * Restituisce una risposta JSON invece di fare redirect.
 */
@WebServlet("/ModificaVoto")
@MultipartConfig
public class ModificaVotoServlet extends HttpServlet {

    private Connection connection;

    /**
     * Lista dei voti ammessi (case insensitive).
     */
    private static final List<String> VALID_VOTI = Arrays.asList(
            "18", "19", "20", "21", "22", "23", "24", "25", "26",
            "27", "28", "29", "30", "30 e lode",
            "Assente", "Rimandato", "Riprovato"
    );

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta POST per modificare il voto di uno studente.
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

        // Lettura e validazione parametri
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        String votoStr = req.getParameter("voto");

        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null || votoStr == null ||
                idStudenteStr.trim().isEmpty() || idAppelloStr.trim().isEmpty() ||
                idCorsoStr.trim().isEmpty() || votoStr.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametri mancanti o vuoti");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        int idStudente, idAppello, idCorso;
        try {
            idStudente = Integer.parseInt(idStudenteStr.trim());
            idAppello = Integer.parseInt(idAppelloStr.trim());
            idCorso = Integer.parseInt(idCorsoStr.trim());
            if(idStudente < 1 || idAppello < 1 || idCorso < 1){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametri numerici non validi");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametri numerici non validi");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Normalizza il voto per il confronto (tutto lowercase)
        String votoNormalizzato = votoStr.trim().toLowerCase();

        // Controllo validità voto (case insensitive)
        boolean votoValido = VALID_VOTI.stream()
                .anyMatch(validVote -> validVote.toLowerCase().equals(votoNormalizzato));

        if (!votoValido) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Valore voto non valido. Valori ammessi: " + VALID_VOTI);
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

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            // Usa il voto originale (con maiuscole corrette) per il salvataggio
            boolean updateSuccess = iscrizioneDAO.UpdateIscrizioneByStudenIdandAppelloId(idStudente, idAppello, votoStr.trim());

            if (!updateSuccess) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Errore durante l'aggiornamento del voto");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            // Successo
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Voto aggiornato con successo");
            jsonResponse.addProperty("idStudente", idStudente);
            jsonResponse.addProperty("idAppello", idAppello);
            jsonResponse.addProperty("idCorso", idCorso);
            jsonResponse.addProperty("voto", votoStr.trim());
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
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}