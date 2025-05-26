package it.tiw.controller.professor;

import it.tiw.beans.Studente;
import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
import it.tiw.dao.StudenteDAO;
import it.tiw.util.DbConnectionHandler;
import it.tiw.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet per visualizzare gli iscritti a un appello di un corso specifico per un docente autenticato.
 * Gestisce la paginazione e ordinamento opzionale.
 */
@WebServlet("/IscrittiAppello")
public class IscrittiAppelloServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication thymeleafApp;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        thymeleafApp = JakartaServletWebApplication.buildApplication(getServletContext());

        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(thymeleafApp);
        templateResolver.setPrefix("/WEB-INF/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        connection = DbConnectionHandler.getConnection(getServletContext());
    }

    /**
     * Gestisce la richiesta GET per mostrare gli iscritti a un appello.
     *
     * @param req  richiesta HTTP contenente parametri idAppello e idCorso
     * @param resp risposta HTTP con la pagina renderizzata o codice di errore
     * @throws ServletException in caso di errori generici della servlet
     * @throws IOException      in caso di errori di I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Controllo autenticazione e ruolo docente
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Validazione parametri obbligatori idAppello e idCorso
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");
        if (idAppelloStr == null || idAppelloStr.trim().isEmpty()
                || idCorsoStr == null || idCorsoStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' obbligatori");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr.trim());
            idCorso = Integer.parseInt(idCorsoStr.trim());
            if (idAppello <= 0 || idCorso <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' devono essere positivi");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri 'idAppello' e 'idCorso' non validi");
            return;
        }

        // Parametri di ordinamento opzionali con valori di default
        String sortField = req.getParameter("sortField");
        String sortDir = req.getParameter("sortDir");

        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "cognome";
        }
        if (sortDir == null || (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))) {
            sortDir = "asc";
        }

        try {
            StudenteDAO studenteDAO = new StudenteDAO(connection);
            AppelloDAO appelloDAO = new AppelloDAO(connection);

            // Recupera iscritti ordinati per docente, appello e corso
            List<Studente> iscritti = studenteDAO.getIscrittiOrdinati(idAppello, idCorso, docente.getIdUtente(), sortField, sortDir);


            // if (iscritti.isEmpty()) {
            //     resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Nessun iscritto trovato per l'appello e corso specificati");
            //     return;
            // }

            // Conteggi personalizzati su stato valutazione
            int countInserito = Util.countStatoValutazioneValues(iscritti, "inserito");
            int countPubblicatoRifiutato = Util.countStatoValutazioneValues(iscritti, "pubblicato")
                    + Util.countStatoValutazioneValues(iscritti, "rifiutato");

            // Prepara contesto per Thymeleaf
            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            ctx.setVariable("iscritti", iscritti);
            ctx.setVariable("idAppello", idAppello);
            ctx.setVariable("idCorso", idCorso);
            ctx.setVariable("callDate", appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello));
            ctx.setVariable("sortField", sortField);
            ctx.setVariable("sortDir", sortDir);
            ctx.setVariable("numberOfstudentHasInserito", countInserito);
            ctx.setVariable("numberOfstudentHasPubblicatoAndRifiutato", countPubblicatoRifiutato);

            templateEngine.process("iscrittiPerAppello", ctx, resp.getWriter());

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database: " + e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // Valutare logging più strutturato in produzione
        }
    }
}
