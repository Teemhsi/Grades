package it.tiw.controller;

import it.tiw.beans.Utente;
import it.tiw.dao.UtenteDAO;
import it.tiw.util.DbConnectionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.web.IWebExchange;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginHandler")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    private JakartaServletWebApplication thymeleafApp;

    @Override
    public void init() throws ServletException {
        // Set up DB connection
        connection = DbConnectionHandler.getConnection(getServletContext());

        // Initialize Jakarta Thymeleaf app context
        thymeleafApp = JakartaServletWebApplication.buildApplication(getServletContext());

        // Create template resolver using app context
        org.thymeleaf.templateresolver.WebApplicationTemplateResolver templateResolver =
                new org.thymeleaf.templateresolver.WebApplicationTemplateResolver(thymeleafApp);
        templateResolver.setPrefix("/");  // Root of webapp where login.html is located
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange, req.getLocale());

        String loginPage = "login";

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            ctx.setVariable("errorMsg", "Credenziali vuote o mancanti");
            templateEngine.process(loginPage, ctx, resp.getWriter());
            return;
        }

        try {
            UtenteDAO dao = new UtenteDAO(connection);
            Utente user = dao.checkCredentials(email, password);

            if (user != null) {
                req.getSession().setAttribute("user", user);

                switch (user.getRuolo().toLowerCase()) {
                    case "studente":
                        resp.sendRedirect(req.getContextPath() + "/student-home");
                        break;
                    case "docente":
                        resp.sendRedirect(req.getContextPath() + "/professor-home");
                        break;
                    default:
                        ctx.setVariable("errorMsg", "Ruolo utente non riconosciuto");
                        templateEngine.process(loginPage, ctx, resp.getWriter());
                        break;
                }
            } else {
                ctx.setVariable("errorMsg", "Username o password errati");
                templateEngine.process(loginPage, ctx, resp.getWriter());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ctx.setVariable("errorMsg", "Errore del server. Riprova più tardi.");
            templateEngine.process(loginPage, ctx, resp.getWriter());
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
