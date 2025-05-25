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
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Parametri richiesti
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        int idAppello, idCorso;
        try {
            idAppello = Integer.parseInt(idAppelloStr);
            idCorso = Integer.parseInt(idCorsoStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri non validi");
            return;
        }

        // Verifica autenticazione
        Utente docente = (Utente) req.getSession().getAttribute("user");
        if (docente == null || !"docente".equalsIgnoreCase(docente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Sort parameters
        String sortField = req.getParameter("sortField");
        String sortDir = req.getParameter("sortDir");

        if (sortField == null || sortDir == null) {
            sortField = "cognome"; // default sort field
            sortDir = "asc";       // default direction
        }

        try {
            StudenteDAO studenteDAO = new StudenteDAO(connection);
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            List<Studente> iscritti = studenteDAO.getIscrittiOrdinati(idAppello, idCorso, docente.getIdUtente(), sortField, sortDir);
            int numberOfstudentHasInserito = Util.countStatoValutazioneValues(iscritti, "inserito");
            int numberOfstudentHasPubblicatoAndRifiutato = Util.countStatoValutazioneValues(iscritti, "pubblicato");
            numberOfstudentHasPubblicatoAndRifiutato += Util.countStatoValutazioneValues(iscritti, "rifiutato");

            // Prepara contesto Thymeleaf
            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            ctx.setVariable("iscritti", iscritti);
            ctx.setVariable("idAppello", idAppello);
            ctx.setVariable("idCorso", idCorso);
            ctx.setVariable("callDate", appelloDAO.findAppelloDateByCourseCallDocente(idCorso, docente.getIdUtente(), idAppello));
            ctx.setVariable("sortField", sortField);
            ctx.setVariable("sortDir", sortDir);
            ctx.setVariable("numberOfstudentHasInserito", numberOfstudentHasInserito);
            ctx.setVariable("numberOfstudentHasPubblicatoAndRifiutato", numberOfstudentHasPubblicatoAndRifiutato);

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
            e.printStackTrace();
        }
    }
}
