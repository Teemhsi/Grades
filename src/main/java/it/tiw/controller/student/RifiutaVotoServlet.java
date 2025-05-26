package it.tiw.controller.student;

import it.tiw.beans.Utente;
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

@WebServlet("/RifiutaVoto")
public class RifiutaVotoServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Controllo autenticazione e ruolo
        Utente student = (Utente) req.getSession().getAttribute("user");
        if (student == null || !"studente".equalsIgnoreCase(student.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Parametri obbligatori
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idStudente = Integer.parseInt(idStudenteStr);
            int idAppello = Integer.parseInt(idAppelloStr);
            int idCorso = Integer.parseInt(idCorsoStr);

            // Ulteriore controllo: l'idStudente passato deve corrispondere all'utente loggato
            if (idStudente != student.getIdUtente()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Operazione non autorizzata");
                return;
            }

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            boolean success = iscrizioneDAO.rifiutaVotipubblicati(idAppello, idStudente);

            if (!success) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante il rifiuto del voto");
                return;
            }

            // Redirect alla pagina gestione voti dello studente
            resp.sendRedirect(req.getContextPath() + "/GestioneVotiStudente?idAppello=" + idAppello + "&idCorso=" + idCorso);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri numerici non validi");
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore DB: " + e.getMessage());
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
