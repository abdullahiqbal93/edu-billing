package com.pahana.controller;

import com.pahana.model.User;
import com.pahana.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }
        User user = UserService.getInstance().authenticate(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("dashboard");
        } else {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }
}
