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

@WebServlet("/VerbalizzaIscritti")
public class VerbalizzaIscrittiServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        if (idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'idAppello' mancante");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'idAppello' non valido");
            return;
        }

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);

            // Verifica che l'appello appartenga al docente
            if (appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello) == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato all'appello");
                return;
            }

            // Crea verbale e aggiorna iscrizioni
            String idVerbale = verbaleDAO.creaVerbaleConIscrizioni(idAppello);
            System.out.println("verbale creato: " + idVerbale);

            if (idVerbale == null || idVerbale.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante la verbalizzazione");
                return;
            }

            // Reindirizza alla pagina aggiornata
            resp.sendRedirect(req.getContextPath() + "/DettaglioVerbale?codice=" + idVerbale );

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database: " + e.getMessage());
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
