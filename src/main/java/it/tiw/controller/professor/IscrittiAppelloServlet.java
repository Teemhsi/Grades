package it.tiw.controller.professor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.tiw.beans.Studente;
import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.dao.StudenteDAO;
import it.tiw.util.DbConnectionHandler;
import it.tiw.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet per restituire gli iscritti a un appello in formato JSON per SPA.
 * Gestisce ordinamento e conteggi degli stati di valutazione.
 */
@WebServlet("/IscrittiAppello")
public class IscrittiAppelloServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta GET per restituire gli iscritti a un appello in formato JSON.
     *
     * @param req  richiesta HTTP contenente parametri idAppello, idCorso e opzioni di sorting
     * @param resp risposta HTTP con JSON o codice di errore
     * @throws ServletException in caso di errori generici della servlet
     * @throws IOException      in caso di errori di I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // Controllo autenticazione e ruolo docente
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
            return;
        }

        // Validazione parametri obbligatori idAppello e idCorso
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        if (idAppelloStr == null || idAppelloStr.trim().isEmpty()
                || idCorsoStr == null || idCorsoStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' obbligatori");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr.trim());
            idCorso = Integer.parseInt(idCorsoStr.trim());
            if (idAppello <= 0 || idCorso <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' devono essere positivi");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' non validi");
            return;
        }


        try {
            StudenteDAO studenteDAO = new StudenteDAO(connection);
            AppelloDAO appelloDAO = new AppelloDAO(connection);

            // Recupera iscritti ordinati per docente, appello e corso
            List<Studente> iscritti = studenteDAO.getIscrittiOrdinati(idAppello, idCorso, docente.getIdUtente());

            // Conteggi personalizzati su stato valutazione
            int countInserito = Util.countStatoValutazioneValues(iscritti, "inserito");
            int countPubblicatoRifiutato = Util.countStatoValutazioneValues(iscritti, "pubblicato")
                    + Util.countStatoValutazioneValues(iscritti, "rifiutato");

            // Recupera data appello
            Date callDateObj = appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello);
            String callDate = "";
            if (callDateObj != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                callDate = sdf.format(callDateObj);
            }

            // Crea risposta JSON usando Map
            Map<String, Object> response = new HashMap<>();
            response.put("iscritti", iscritti);
            response.put("idAppello", idAppello);
            response.put("idCorso", idCorso);
            response.put("callDate", callDate);
            response.put("numberOfstudentHasInserito", countInserito);
            response.put("numberOfstudentHasPubblicatoAndRifiutato", countPubblicatoRifiutato);

            // Configura Gson per gestire correttamente le date
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();

            String jsonResponse = gson.toJson(response);
            resp.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database: " + e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // Valutare logging più strutturato in produzione
        }
    }
}