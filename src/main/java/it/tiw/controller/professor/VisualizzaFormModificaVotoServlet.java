package it.tiw.controller.professor;

import it.tiw.beans.Studente;
import it.tiw.beans.Utente;
import it.tiw.dao.StudenteDAO;
import it.tiw.util.DbConnectionHandler;
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

/**
 * Servlet che visualizza il form per la modifica del voto di uno studente
 * iscritto ad un appello, fornendo i dati necessari alla pagina.
 */
@WebServlet("/VisualizzaFormModificaVoto")
public class VisualizzaFormModificaVotoServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private JakartaServletWebApplication thymeleafApp;
    private Connection connection;

    /**
     * Inizializza il motore Thymeleaf e la connessione al database.
     *
     * @throws ServletException in caso di errore di inizializzazione
     */
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
     * Gestisce la richiesta POST per visualizzare il form di modifica voto,
     * controllando autenticazione, parametri e caricando i dati studente.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errore servlet
     * @throws IOException      in caso di errore I/O
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        // Controllo autenticazione docente
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        String idStudenteStr = req.getParameter("idStudente");
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");



        // Verifica parametri obbligatori
        if (idStudenteStr == null || idAppelloStr == null || idCorsoStr == null ||
                idStudenteStr.trim().isEmpty() || idAppelloStr.trim().isEmpty() ||
                idCorsoStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        int idStudente, idAppello, idCorso;
        try {
            idStudente = Integer.parseInt(idStudenteStr);
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
            if(idStudente < 1 || idAppello < 1 || idCorso < 1){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri numerici non validi");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri non validi");
            return;
        }

        try {
            StudenteDAO studenteDAO = new StudenteDAO(connection);
            Studente studente = studenteDAO.getStudentById(idStudente);

            if (studente == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Studente non trovato");
                return;
            }

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            ctx.setVariable("studente", studente);
            ctx.setVariable("idAppello", idAppello);
            ctx.setVariable("idCorso", idCorso);

            templateEngine.process("formModificaVoto", ctx, resp.getWriter());

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    /**
     * Chiude la connessione al database alla distruzione della servlet.
     */
    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
