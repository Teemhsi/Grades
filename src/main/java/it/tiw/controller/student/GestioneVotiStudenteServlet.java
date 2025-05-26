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

/**
 * Servlet per la gestione dei voti dello studente, mostra i dettagli
 * dell'iscrizione e permette di visualizzare/modificare il voto.
 */
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

        // Controllo autenticazione e ruolo
        Utente utente = (Utente) req.getSession().getAttribute("user");
        if (utente == null || !"studente".equalsIgnoreCase(utente.getRuolo())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String idAppelloStr = req.getParameter("idAppello");
        String idCorsoStr = req.getParameter("idCorso");

        if (idAppelloStr == null || idCorsoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri obbligatori mancanti");
            return;
        }

        try {
            int idAppello = Integer.parseInt(idAppelloStr);
            int idCorso = Integer.parseInt(idCorsoStr);
            int idStudente = utente.getIdUtente();

            IscrizioneDAO iscrizioneDAO = new IscrizioneDAO(connection);
            List<Object[]> risultati = iscrizioneDAO.findIscrizioneByIdCorsoIdAppelloStudentId(idStudente, idAppello, idCorso);

            IWebExchange webExchange = thymeleafApp.buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange, req.getLocale());

            if (risultati.isEmpty()) {
                // Nessuna iscrizione trovata
                ctx.setVariable("IscrizionPresente", 0);
            } else {
                Object[] risultato = risultati.get(0);

                Iscrizione iscrizione = (Iscrizione) risultato[0];
                Appello appello = (Appello) risultato[1];
                Corso corso = (Corso) risultato[2];
                Studente studente = (Studente) risultato[3];

                ctx.setVariable("iscrizionestudente", iscrizione);
                ctx.setVariable("appellostudente", appello);
                ctx.setVariable("corsostudente", corso);
                ctx.setVariable("studentdetail", studente);
                ctx.setVariable("IscrizionPresente", 1);
                ctx.setVariable("votiValidi", Arrays.asList(
                        "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30 e lode"));
            }

            ctx.setVariable("idCorsoStudente", idCorso);

            templateEngine.process("gestioneVotiStudente", ctx, resp.getWriter());

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato parametro non valido");
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database: " + e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            getServletContext().log("Errore chiusura connessione database", e);
        }
    }
}
