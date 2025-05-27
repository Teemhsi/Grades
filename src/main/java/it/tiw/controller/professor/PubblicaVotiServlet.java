package it.tiw.controller.professor;

import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.dao.IscrizioneDAO;
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
 * Servlet per pubblicare i voti inseriti relativi a un appello specifico.
 * <p>
 * Riceve tramite POST gli idAppello e idCorso, valida i parametri, chiama il DAO
 * per pubblicare i voti, e in caso di successo effettua un redirect verso la pagina
 * degli iscritti all'appello.
 */
@WebServlet("/PubblicaVoti")
public class PubblicaVotiServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta POST per pubblicare i voti di un appello.
     *
     * @param req  HttpServletRequest con parametri idAppello e idCorso
     * @param resp HttpServletResponse per inviare risposta o errori HTTP
     * @throws ServletException in caso di errori servlet
     * @throws IOException      in caso di errori di I/O
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");


        if (idAppelloStr == null || idCorsoStr == null || idAppelloStr.trim().isEmpty() || idCorsoStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
            if (idAppello <= 0 || idCorso <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' devono essere positivi");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri numerici non validi");
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            // Verifica che l'appello appartenga al docente
            if (appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello) == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato all'appello");
                return;
            }
            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            boolean success = iscrizioneDAO.pubblicaVotiInseriti(idAppello);

            if (!success) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Nessun voto pubblicato o errore nel DB");
                return;
            }
            iscrizioneDAO.sendEmailOnPublish(idAppello);


            // Redirect alla pagina degli iscritti all'appello con idAppello e idCorso
            resp.sendRedirect(req.getContextPath() + "/IscrittiAppello?idAppello=" + idAppello + "&idCorso=" + idCorso);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
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
