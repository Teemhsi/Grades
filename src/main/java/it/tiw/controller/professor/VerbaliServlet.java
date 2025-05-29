package it.tiw.controller.professor;

import it.tiw.beans.Utente;
import it.tiw.beans.Verbale;
import it.tiw.dao.VerbaleDAO;
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet che restituisce i verbali del docente in formato JSON per chiamate AJAX.
 */
@WebServlet("/Verbali")
public class VerbaliServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta GET per restituire i verbali del docente in formato JSON.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errori servlet
     * @throws IOException      in caso di errori I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
            return;
        }

        try {
            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);
            List<Object[]> rawResults = verbaleDAO.findVerbaliByDocenteId(docente.getIdUtente());

            Set<String> uniqueCodes = new LinkedHashSet<>();
            List<VerbaleEntry> verbali = new ArrayList<>();

            for (Object[] row : rawResults) {
                Verbale verbale = (Verbale) row[0];
                String codiceVerbale = verbale.getCodiceVerbale();
                Timestamp dataCreazione = verbale.getDataCreazione();
                Date dataAppello = (Date) row[1];
                String nomeCorso = (String) row[2];

                if (!uniqueCodes.contains(codiceVerbale)) {
                    uniqueCodes.add(codiceVerbale);
                    verbali.add(new VerbaleEntry(codiceVerbale, dataCreazione, nomeCorso, dataAppello));
                }
            }

            // Configura Gson per gestire correttamente date e timestamp
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            String jsonResponse = gson.toJson(verbali);
            resp.getWriter().write(jsonResponse);

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

    /**
     * Classe per rappresentare un verbale in formato JSON.
     */
    public static class VerbaleEntry {
        private final String codiceVerbale;
        private final Timestamp dataCreazione;
        private final String nomeCorso;
        private final Date dataAppello;

        public VerbaleEntry(String codiceVerbale, Timestamp dataCreazione, String nomeCorso, Date dataAppello) {
            this.codiceVerbale = codiceVerbale;
            this.dataCreazione = dataCreazione;
            this.nomeCorso = nomeCorso;
            this.dataAppello = dataAppello;
        }

        public String getCodiceVerbale() {
            return codiceVerbale;
        }

        public Timestamp getDataCreazione() {
            return dataCreazione;
        }

        public String getNomeCorso() {
            return nomeCorso;
        }

        public Date getDataAppello() {
            return dataAppello;
        }
    }
}