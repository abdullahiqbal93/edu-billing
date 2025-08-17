package com.pahana.controller;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class DashboardController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/view/dashboard.jsp").forward(request, response);
    }
}
