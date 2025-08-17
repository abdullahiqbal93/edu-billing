package com.pahana.util;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class AuthenticationChecker implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.equals("/favicon.ico") || path.startsWith("/favicon")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.equals("/login") || path.startsWith("/login")
                || path.equals("/logout") || path.startsWith("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
