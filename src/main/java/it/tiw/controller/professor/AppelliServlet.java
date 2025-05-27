package it.tiw.controller.professor;

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
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet per la visualizzazione degli appelli di un corso associato al docente loggato.
 */
@WebServlet("/Appelli")
public class AppelliServlet extends HttpServlet {
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

        Utente professor = (Utente) req.getSession().getAttribute("user");

        if (professor == null || !"docente".equalsIgnoreCase(professor.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        int idCorso;
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id mancante");
                return;
            }
            idCorso = Integer.parseInt(idParam);
            if (idCorso <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id non valido");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id non valido");
            return;
        }

        try {
            AppelloDAO appelloDAO = new AppelloDAO(connection);
            List<Appello> appelli = appelloDAO.findAppelliByCorsoAndDocenteOrderedDesc(idCorso, professor.getIdUtente());

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            ctx.setVariable("appelli", appelli);
            ctx.setVariable("idCorso", idCorso);

            templateEngine.process("appelliPerCorso", ctx, resp.getWriter());

        } catch (SQLException e) {
            log("Database error in AppelliServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log("Unexpected error in AppelliServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        try {
            DbConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            log("Error closing DB connection in AppelliServlet", e);
        }
    }
}
