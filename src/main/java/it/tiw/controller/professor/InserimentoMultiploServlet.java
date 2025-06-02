package it.tiw.controller.professor;

import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.dao.IscrizioneDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet per l'inserimento multiplo dei voti.
 * Riceve un array di oggetti con idStudente e voto.
 */
@WebServlet("/InserimentoMultiplo")
@MultipartConfig
public class InserimentoMultiploServlet extends HttpServlet {

    private Connection connection;
    private Gson gson = new Gson();

    /**
     * Lista dei voti ammessi (case insensitive).
     */
    private static final List<String> VALID_VOTI = Arrays.asList(
            "18", "19", "20", "21", "22", "23", "24", "25", "26",
            "27", "28", "29", "30", "30 e lode",
            "Assente", "Rimandato", "Riprovato"
    );

    @Override
    public void init() throws ServletException {
        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonResponse = new JsonObject();

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Non autorizzato");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Leggi il corpo della richiesta come JSON
        JsonObject requestBody;
        try {
            requestBody = gson.fromJson(req.getReader(), JsonObject.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Formato JSON non valido");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        // Estrai parametri
        Integer idAppello = requestBody.has("idAppello") ? requestBody.get("idAppello").getAsInt() : null;
        Integer idCorso = requestBody.has("idCorso") ? requestBody.get("idCorso").getAsInt() : null;
        JsonArray voti = requestBody.has("voti") ? requestBody.getAsJsonArray("voti") : null;

        if (idAppello == null || idCorso == null || voti == null || voti.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Parametri mancanti o array voti vuoto");
            resp.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            // Verifica che l'appello appartenga al docente
            if (appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello) == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Accesso negato all'appello");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);

            // Prepara la lista di aggiornamenti
            List<IscrizioneDAO.VotoUpdate> votoUpdates = new ArrayList<>();

            // Valida tutti i voti prima di procedere
            for (JsonElement votoElement : voti) {
                JsonObject votoObj = votoElement.getAsJsonObject();
                Integer idStudente = votoObj.has("idStudente") ? votoObj.get("idStudente").getAsInt() : null;
                String voto = votoObj.has("voto") ? votoObj.get("voto").getAsString() : null;

                if (idStudente == null || voto == null || voto.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    jsonResponse.addProperty("error", "Dati mancanti per uno o più studenti");
                    resp.getWriter().write(jsonResponse.toString());
                    return;
                }

                // Valida il voto
                String votoNormalizzato = voto.trim().toLowerCase();
                boolean votoValido = VALID_VOTI.stream()
                        .anyMatch(validVote -> validVote.toLowerCase().equals(votoNormalizzato));

                if (!votoValido) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    jsonResponse.addProperty("error", "Voto non valido: " + voto + " per studente " + idStudente);
                    resp.getWriter().write(jsonResponse.toString());
                    return;
                }

                votoUpdates.add(new IscrizioneDAO.VotoUpdate(idStudente, idAppello, voto.trim()));
            }

            try {
                // Esegue tutti gli aggiornamenti in una transazione
                int updated = iscrizioneDAO.updateMultipleVotiTransactional(votoUpdates);

                // Prepara la risposta di successo
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", String.format("Inserimento completato: %d voti inseriti con successo", updated));
                jsonResponse.addProperty("updatedCount", updated);
                resp.getWriter().write(jsonResponse.toString());

            } catch (SQLException e) {
                // In caso di errore, tutti gli aggiornamenti sono stati annullati
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                jsonResponse.addProperty("error", "Errore durante l'inserimento: " + e.getMessage());
                resp.getWriter().write(jsonResponse.toString());
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Database error: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            jsonResponse.addProperty("error", "Server error: " + e.getMessage());
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