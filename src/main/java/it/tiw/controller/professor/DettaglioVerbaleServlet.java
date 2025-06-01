package it.tiw.controller.professor;

import it.tiw.beans.Studente;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet per mostrare i dettagli di un verbale in formato JSON.
 */
@WebServlet("/DettaglioVerbale")
public class DettaglioVerbaleServlet extends HttpServlet {
    private Connection connection;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonResponse = new JsonObject();

        // Verifica autenticazione e ruolo
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Validazione parametro 'codice'
        String codiceVerbale = req.getParameter("codice");
        if (codiceVerbale == null || codiceVerbale.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Codice verbale mancante o non valido");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);
            List<Object[]> rawResults = verbaleDAO.findVerbaliByDocenteId(docente.getIdUtente());

            // Filter results by codice verbale
            List<VerbaleDetailEntry> dettagliVerbale = new ArrayList<>();
            for (Object[] row : rawResults) {
                Verbale verbale = (Verbale) row[0];
                if (codiceVerbale.equals(verbale.getCodiceVerbale())) {
                    java.sql.Date dataAppello = (java.sql.Date) row[1];
                    String nomeCorso = (String) row[2];
                    Studente studente = (Studente) row[3];

                    dettagliVerbale.add(new VerbaleDetailEntry(verbale, dataAppello, nomeCorso, studente));
                }
            }

            if (dettagliVerbale.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Verbale non trovato");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            // Costruisci la risposta JSON
            JsonObject verbaleInfo = new JsonObject();
            VerbaleDetailEntry firstEntry = dettagliVerbale.get(0);

            verbaleInfo.addProperty("codiceVerbale", codiceVerbale);

            // Formatta la data di creazione
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            verbaleInfo.addProperty("dataCreazione", dateFormat.format(firstEntry.getVerbale().getDataCreazione()));

            // Formatta la data appello
            SimpleDateFormat dateFormatSimple = new SimpleDateFormat("dd/MM/yyyy");
            verbaleInfo.addProperty("dataAppello", dateFormatSimple.format(firstEntry.getDataAppello()));

            verbaleInfo.addProperty("nomeCorso", firstEntry.getNomeCorso());

            // Array di studenti
            JsonArray studentiArray = new JsonArray();
            for (VerbaleDetailEntry entry : dettagliVerbale) {
                JsonObject studenteObj = new JsonObject();
                studenteObj.addProperty("matricola", entry.getStudente().getMatricola());
                studenteObj.addProperty("nome", entry.getStudente().getNome());
                studenteObj.addProperty("cognome", entry.getStudente().getCognome());
                studenteObj.addProperty("voto", entry.getStudente().getVoto());
                studentiArray.add(studenteObj);
            }

            jsonResponse.add("verbaleInfo", verbaleInfo);
            jsonResponse.add("studenti", studentiArray);
            jsonResponse.addProperty("totaleStudenti", dettagliVerbale.size());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Server internal error ");
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

    // Inner class rimane uguale
    public static class VerbaleDetailEntry {
        private final Verbale verbale;
        private final java.sql.Date dataAppello;
        private final String nomeCorso;
        private final Studente studente;

        public VerbaleDetailEntry(Verbale verbale, java.sql.Date dataAppello, String nomeCorso, Studente studente) {
            this.verbale = verbale;
            this.dataAppello = dataAppello;
            this.nomeCorso = nomeCorso;
            this.studente = studente;
        }

        public Verbale getVerbale() { return verbale; }
        public java.sql.Date getDataAppello() { return dataAppello; }
        public String getNomeCorso() { return nomeCorso; }
        public Studente getStudente() { return studente; }
    }
}