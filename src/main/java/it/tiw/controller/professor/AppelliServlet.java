package it.tiw.controller.professor;

import it.tiw.beans.Appello;
import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet che restituisce gli appelli di un corso in formato JSON per chiamate AJAX.
 * Mantiene la stessa logica di validazione dell'AppelliServlet originale.
 */
@WebServlet("/Appelli")
public class AppelliServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta GET per restituire gli appelli del corso in formato JSON.
     * Usa la stessa logica di validazione dell'AppelliServlet originale.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errori servlet
     * @throws IOException      in caso di errori I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("hello from appelli-data servlet");
        resp.setContentType("application/json;charset=UTF-8");

        Utente professor = (Utente) req.getSession().getAttribute("user");

        if (professor == null || !"docente".equalsIgnoreCase(professor.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
            return;
        }

        int idCorso;
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id mancante");
                return;
            }
            idCorso = Integer.parseInt(idParam);
            if (idCorso <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id non valido");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id non valido");
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            List<Appello> appelli = appelloDAO.findAppelliByCorsoAndDocenteOrderedDesc(idCorso, professor.getIdUtente());

            // Configura Gson per gestire correttamente le date
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();

            String jsonResponse = gson.toJson(appelli);
            resp.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            log("Database error in AppelliDataServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (Exception e) {
            log("Unexpected error in AppelliDataServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            log("Error closing DB connection in AppelliDataServlet", e);
        }
    }
}