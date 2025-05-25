package it.tiw.controller;

import it.tiw.beans.Utente;
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
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import java.io.IOException;


public class IndexRedirectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication thymeleafApp;

    @Override
    public void init() throws ServletException {
        thymeleafApp = JakartaServletWebApplication.buildApplication(getServletContext());

        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(thymeleafApp);
        templateResolver.setPrefix("/"); // Assuming templates are in src/main/webapp
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("lets see..indexing");
        Utente user = req.getSession(false) != null ? (Utente) req.getSession(false).getAttribute("user") : null;
        if (user != null) {
            if (user.getRuolo().equals("studente")) {
                resp.sendRedirect(req.getContextPath() + "/student-home");
            } else if (user.getRuolo().equals("docente")) {
                resp.sendRedirect(req.getContextPath() + "/professor-home");
            }
            System.out.println("lets see..indexing passed---> home");
        } else {
            System.out.println("lets see..indexing faild---> login");
            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
            resp.setDateHeader("Expires", 0); // Proxies
            templateEngine.process("login", ctx, resp.getWriter()); // login.html
        }
    }
}
