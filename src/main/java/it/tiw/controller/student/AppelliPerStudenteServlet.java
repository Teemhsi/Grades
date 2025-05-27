package it.tiw.controller.student;

import it.tiw.beans.Appello;
import it.tiw.beans.Utente;
import it.tiw.dao.AppelloDAO;
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
import java.util.List;

/**
 * Servlet che mostra gli appelli di un corso per uno studente autenticato.
 */
@WebServlet("/AppelliStudente")
public class AppelliPerStudenteServlet extends HttpServlet {
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
     * Gestisce la richiesta GET per mostrare gli appelli di un corso specifico per lo studente loggato.
     *
     * @param req  richiesta HTTP
     * @param resp risposta HTTP
     * @throws ServletException in caso di errore servlet
     * @throws IOException      in caso di errore I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        // Recupera utente dalla sessione e verifica ruolo
        Utente student = (Utente) req.getSession().getAttribute("user");
        if (student == null || !"studente".equalsIgnoreCase(student.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Recupera e valida il parametro idCorso
        String idCorsoParam = req.getParameter("id");
        int idCorso;
        try {
            if (idCorsoParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'id' mancante");
                return;
            }
            idCorso = Integer.parseInt(idCorsoParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro 'id' non valido");
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);

            // Ottiene gli appelli per il corso e studente, ordinati decrescente
            List<Appello> appelli = appelloDAO.findAppelliByCorsoAndStudenteOrderedDesc(idCorso, student.getIdUtente());

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            ctx.setVariable("appellistudente", appelli);
            ctx.setVariable("idCorso", idCorso);

            templateEngine.process("studenteAppelliPerCorso", ctx, resp.getWriter());

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
            e.printStackTrace();
        }
    }
}
