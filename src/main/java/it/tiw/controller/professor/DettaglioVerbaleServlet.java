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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet per mostrare i dettagli di un verbale associato a un docente autenticato.
 * Risponde a richieste GET su /DettaglioVerbale.
 */
@WebServlet("/DettaglioVerbale")
public class DettaglioVerbaleServlet extends HttpServlet {
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
     * Gestisce richieste GET per mostrare i dettagli di un verbale specifico.
     * Controlla autenticazione, validità del parametro e accesso al database.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException se si verifica un errore nella logica della servlet
     * @throws IOException      se si verifica un errore di I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Verifica autenticazione e ruolo
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Validazione parametro 'codice'
        String codiceVerbale = req.getParameter("codice");
        if (codiceVerbale == null || (codiceVerbale = codiceVerbale.trim()).isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Codice verbale mancante o non valido");
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
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Verbale non trovato");
                return;
            }

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());
            ctx.setVariable("dettagliVerbale", dettagliVerbale);
            ctx.setVariable("codiceVerbale", codiceVerbale);

            templateEngine.process("dettaglioVerbale", ctx, resp.getWriter());

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

    // Inner class for verbale details
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