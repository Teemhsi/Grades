package it.tiw.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class IsLoggedGloballyCheck implements Filter{

    public IsLoggedGloballyCheck() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginPath = req.getServletContext().getContextPath() + "/login.html";
        HttpSession session = req.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            res.setStatus(403);
            //res.setHeader("Location", loginPath);
            //res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato");

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
