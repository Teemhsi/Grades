package it.tiw.filter;

import it.tiw.beans.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class studentFilter implements Filter {

    public studentFilter() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginPath = req.getServletContext().getContextPath() + "/login.html";
        HttpSession session = req.getSession();
        Utente u =  null;
        u = (Utente) session.getAttribute("user");
        if (!u.getRuolo().equalsIgnoreCase("studente")) {
            res.setStatus(403);
            res.sendRedirect(loginPath);
            System.out.print("Login checker FAILED...\n");
            System.out.print("path: " + loginPath + "\n");
            return;
        }
        System.out.println("logged already");
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
