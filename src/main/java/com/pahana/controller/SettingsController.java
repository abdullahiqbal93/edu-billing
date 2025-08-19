package com.pahana.controller;

import com.pahana.model.User;
import com.pahana.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class SettingsController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        request.setAttribute("users", UserService.getInstance().listUsers());
        request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        User current = (User) session.getAttribute("user");

        String action = request.getParameter("action");
        String success = null;
        String error = null;
        UserService svc = UserService.getInstance();

        try {
            if ("change-username".equals(action)) {
                String newUsername = request.getParameter("newUsername");
                if (svc.changeUsername(current.getId(), newUsername)) {
                    current.setUsername(newUsername);
                    success = "Username updated.";
                } else {
                    error = "Failed to update username (maybe already taken?).";
                }
            }
        } catch (Exception e) {
            error = "Unexpected error: " + e.getMessage();
        }

        if (success != null)
            request.setAttribute("success", success);
        if (error != null)
            request.setAttribute("error", error);
        request.setAttribute("users", svc.listUsers());
        request.getRequestDispatcher("/WEB-INF/view/settings.jsp").forward(request, response);
    }
}
