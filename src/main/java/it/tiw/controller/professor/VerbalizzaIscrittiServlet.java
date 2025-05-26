package it.tiw.controller.professor;

import it.tiw.dao.IscrizioneDAO;
import it.tiw.dao.VerbaleDAO;
import it.tiw.dao.AppelloDAO;
import it.tiw.beans.Utente;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet che gestisce la verbalizzazione degli iscritti ad un appello.
 * Verifica i parametri, l'autenticazione e autorizzazione del docente,
 * crea il verbale e aggiorna le iscrizioni associate.
 */
@WebServlet("/VerbalizzaIscritti")
public class VerbalizzaIscrittiServlet extends HttpServlet {
    private Connection connection;

    /**
     * Inizializza la connessione al database.
     *
     * @throws ServletException se si verifica un errore durante l'inizializzazione
     */
    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta POST per verbalizzare gli iscritti.
     * Controlla parametri, ruolo docente e autorizzazioni, poi crea il verbale.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errore servlet
     * @throws IOException      in caso di errore I/O
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'idAppello' o 'idCorso' mancante");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'idAppello' o 'idCorso' non valido");
            return;
        }

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            // Verifica che l'appello appartenga al docente
            if (appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello) == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato all'appello");
                return;
            }

            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);
            // Crea il verbale e aggiorna le iscrizioni
            String idVerbale = verbaleDAO.creaVerbaleConIscrizioni(idAppello);

            if (idVerbale == null || idVerbale.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante la verbalizzazione");
                return;
            }

            // Reindirizza alla pagina di dettaglio verbale
            resp.sendRedirect(req.getContextPath() + "/DettaglioVerbale?codice=" + idVerbale);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database: " + e.getMessage());
        }
    }

    /**
     * Chiude la connessione al database all'atto della distruzione della servlet.
     */
    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
