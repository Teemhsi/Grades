// GestioneVotiStudenteServlet.java
package it.tiw.controller.student;

import it.tiw.beans.*;
import it.tiw.dao.IscrizioneDAO;
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
import java.util.Arrays;
import java.util.List;

@WebServlet("/GestioneVotiStudente")
public class GestioneVotiStudenteServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Check if user is logged in and is a student
        Utente utente = (Utente) req.getSession().getAttribute("user");
        if (utente == null || !"studente".equalsIgnoreCase(utente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Get parameters
        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        try {
            int idAppello = Integer.parseInt(idAppelloStr);
            int idCorso = Integer.parseInt(idCorsoStr);
            int idStudente = utente.getIdUtente();

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            List<Object[]> risultati = iscrizioneDAO.findIscrizioneByIdCorsoIdAppelloStudentId(idStudente, idAppello, idCorso);

            if (risultati.isEmpty()) {
                // No enrollment found - redirect back or show error
                //resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Iscrizione non trovata");
                //return;
                ctx.setVariable("IscrizionPresente", 0);

            }else{
                Object[] risultato = risultati.get(0);


                Iscrizione iscrizione = (Iscrizione) risultato[0]; // Cast to your Iscrizione bean class
                Appello appello = (Appello )risultato[1];    // Cast to your Appello bean class
                Corso corso = (Corso) risultato[2];      // Cast to your Corso bean class
                Studente studente = (Studente) risultato[3];
                System.out.println("result are : " + studente.toString());

                ctx.setVariable("iscrizionestudente", iscrizione);
                ctx.setVariable("appellostudente", appello);
                ctx.setVariable("corsostudente", corso);
                ctx.setVariable("IscrizionPresente", 1);
                ctx.setVariable("studentdetail", studente);
                ctx.setVariable("votiValidi", Arrays.asList("18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30 e lode"));

            }
            ctx.setVariable("idCorsoStudente", idCorso);

            templateEngine.process("gestioneVotiStudente", ctx, resp.getWriter());
            //resp.sendRedirect(req.getContextPath() + "/Funcase");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter format");
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    // doPost method will be implemented later for grade rejection functionality

    @Override
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log the error but don't throw exception in destroy()
            getServletContext().log("Error closing database connection", e);
        }
    }
}