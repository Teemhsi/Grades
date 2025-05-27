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
import java.util.Arrays;
import java.util.List;

/**
 * Servlet per modificare il voto di uno studente in un appello specifico.
 * <p>
 * Riceve i parametri via POST:
 * <ul>
 *     <li>idStudente (int): ID dello studente</li>
 *     <li>idAppello (int): ID dell'appello</li>
 *     <li>idCorso (int): ID del corso</li>
 *     <li>voto (String): voto valido (es. "18", "30 e lode", "assente", etc.)</li>
 * </ul>
 * <p>
 * Valida i parametri, aggiorna il voto nel DB tramite IscrizioneDAO e gestisce gli errori HTTP.
 */
@WebServlet("/ModificaVoto")
public class ModificaVotoServlet extends HttpServlet {

    private Connection connection;

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

    /**
     * Gestisce la richiesta POST per modificare il voto di uno studente.
     *
     * @param req  HttpServletRequest con parametri idStudente, idAppello, idCorso e voto
     * @param resp HttpServletResponse per redirect o errori HTTP
     * @throws ServletException in caso di errori generali della servlet
     * @throws IOException      in caso di errori di I/O
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Lettura e validazione parametri
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        String votoStr = req.getParameter("voto");

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null || votoStr == null ||
                idStudenteStr.trim().isEmpty() || idAppelloStr.trim().isEmpty() ||
                idCorsoStr.trim().isEmpty() || votoStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti o vuoti");
            return;
        }

        int idStudente, idAppello, idCorso;
        try {
            idStudente = Integer.parseInt(idStudenteStr.trim());
            idAppello = Integer.parseInt(idAppelloStr.trim());
            idCorso = Integer.parseInt(idCorsoStr.trim());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri numerici non validi");
            return;
        }

        String votoSanitized = votoStr.trim().toLowerCase();

        // Controllo validità voto (case sensitive)
        boolean votoValido = VALID_VOTI.stream()
                .anyMatch(validVote -> validVote.equals(votoSanitized));

        if (!votoValido) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Valore voto non valido. Valori ammessi: " + VALID_VOTI);
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
            boolean updateSuccess = iscrizioneDAO.UpdateIscrizioneByStudenIdandAppelloId(idStudente, idAppello, votoStr.trim());

            if (!updateSuccess) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante l'aggiornamento del voto");
                return;
            }

            // Redirect alla pagina degli iscritti all'appello
            resp.sendRedirect(req.getContextPath() + "/IscrittiAppello?idAppello=" + idAppello + "&idCorso=" + idCorso);

        }catch (SQLException e) {
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
            e.printStackTrace(); // In ambiente produttivo usare logger appropriato
        }
    }
}
