package it.tiw.controller.professor;

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

@WebServlet("/ModificaVoto")
public class ModificaVotoServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        String votoStr = req.getParameter("voto");

        if (idStudenteStr == null || idAppelloStr == null || votoStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try {
            int idStudente = Integer.parseInt(idStudenteStr);
            int idAppello = Integer.parseInt(idAppelloStr);
            //int voto = Integer.parseInt(votoStr);
            int idCorso = Integer.parseInt(idCorsoStr);



            IscrizioneDAO iscrizioniDAO = new IscrizioneDAO(connection);
            boolean success = iscrizioniDAO.UpdateIscrizioneByStudenIdandAppelloId(idStudente, idAppello, votoStr);

            if (!success) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante l'aggiornamento");
                return;
            }

            // Redirect di nuovo alla pagina degli iscritti all'appello
            resp.sendRedirect(req.getContextPath() + "/IscrittiAppello?idAppello=" + idAppello + "&idCorso=" + idCorso);

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
