package it.tiw.controller.professor;

import it.tiw.beans.Corso;
import it.tiw.beans.Utente;
import it.tiw.dao.CorsoDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet che gestisce la home page del docente, mostrando i corsi assegnati.
 * <p>
 * Controlla che l'utente sia autenticato e abbia ruolo "docente".
 * Recupera i corsi associati al docente e li passa al template Thymeleaf.
 */
@WebServlet("/professor-home")
public class ProfessorHomeServlet extends HttpServlet {

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
     * Gestisce la richiesta GET per mostrare la home del docente con i corsi associati.
     *
     * @param req  HttpServletRequest contenente sessione e parametri
     * @param resp HttpServletResponse per output HTML o errori HTTP
     * @throws IOException in caso di errori di I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        Utente professor = (Utente) req.getSession().getAttribute("user");

        if (professor == null || !"docente".equalsIgnoreCase(professor.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            CorsoDAO corsoDAO = new CorsoDAO(connection);
            List<Corso> corsi = corsoDAO.findCorsiByDocenteIdOrderedDesc(professor.getIdUtente());

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());
            ctx.setVariable("corsi", corsi);

            templateEngine.process("professorHome", ctx, resp.getWriter());

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
