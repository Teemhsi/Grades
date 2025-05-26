package it.tiw.controller.professor;

import it.tiw.beans.Utente;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet per visualizzare i verbali associati al docente autenticato.
 * Recupera i verbali unici (basati sul codice verbale) e li passa alla view Thymeleaf.
 */
@WebServlet("/Verbali")
public class VerbaliServlet extends HttpServlet {

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
     * Gestisce la richiesta GET per mostrare la lista dei verbali del docente.
     * Verifica l'autenticazione e autorizzazione del docente.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errori servlet
     * @throws IOException      in caso di errori I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            VerbaleDAO verbaleDAO = new VerbaleDAO(connection);
            List<Object[]> rawResults = verbaleDAO.findVerbaliByDocenteId(docente.getIdUtente());

            Set<String> uniqueCodes = new LinkedHashSet<>();
            List<VerbaleEntry> verbali = new ArrayList<>();

            for (Object[] row : rawResults) {
                it.tiw.beans.Verbale verbale = (it.tiw.beans.Verbale) row[0];
                String codiceVerbale = verbale.getCodiceVerbale();
                java.sql.Timestamp dataCreazione = verbale.getDataCreazione();

                if (!uniqueCodes.contains(codiceVerbale)) {
                    uniqueCodes.add(codiceVerbale);
                    verbali.add(new VerbaleEntry(codiceVerbale, dataCreazione));
                }
            }

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());
            ctx.setVariable("verbali", verbali);

            templateEngine.process("verbali", ctx, resp.getWriter());

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

    /**
     * Classe interna per rappresentare un singolo verbale semplificato per la view.
     */
    public static class VerbaleEntry {
        private final String codiceVerbale;
        private final java.sql.Timestamp dataCreazione;

        public VerbaleEntry(String codiceVerbale, java.sql.Timestamp dataCreazione) {
            this.codiceVerbale = codiceVerbale;
            this.dataCreazione = dataCreazione;
        }

        public String getCodiceVerbale() {
            return codiceVerbale;
        }

        public java.sql.Timestamp getDataCreazione() {
            return dataCreazione;
        }
    }
}
