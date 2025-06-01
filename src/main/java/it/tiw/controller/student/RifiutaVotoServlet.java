package it.tiw.controller.student;

import it.tiw.beans.Utente;
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

@WebServlet("/RifiutaVoto")
@MultipartConfig
public class RifiutaVotoServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonResponse = new JsonObject();

        // Controllo autenticazione e ruolo
        Utente student = (Utente) req.getSession().getAttribute("user");
        if (student == null || !"studente".equalsIgnoreCase(student.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Parametri obbligatori
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null ||
                idAppelloStr.trim().isEmpty() || idCorsoStr.trim().isEmpty() || idStudenteStr.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametri mancanti");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            int idStudente = Integer.parseInt(idStudenteStr);
            int idAppello = Integer.parseInt(idAppelloStr);
            int idCorso = Integer.parseInt(idCorsoStr);

            if(idAppello < 1 || idCorso < 1 || idStudente < 1){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Parametri numerici non validi");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            // Ulteriore controllo: l'idStudente passato deve corrispondere all'utente loggato
            if (idStudente != student.getIdUtente()) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Operazione non autorizzata");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            boolean success = iscrizioneDAO.rifiutaVotipubblicati(idAppello, idStudente);

            if (!success) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Errore durante il rifiuto del voto");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            // Successo
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Voto rifiutato con successo");
            jsonResponse.addProperty("idAppello", idAppello);
            jsonResponse.addProperty("idCorso", idCorso);
            resp.getWriter().write(jsonResponse.toString());

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametri numerici non validi");
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