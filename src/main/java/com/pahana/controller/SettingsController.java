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
            } else if ("change-password".equals(action)) {
                String currentPassword = request.getParameter("currentPassword");
                String newPassword = request.getParameter("newPassword");
                if (svc.changePassword(current.getId(), currentPassword, newPassword)) {
                    success = "Password updated.";
                } else {
                    error = "Failed to update password (check current password).";
                }
            } else if ("add-user".equals(action)) {
                if (!svc.isSuperAdmin(current)) {
                    error = "Only SUPER_ADMIN can add users.";
                } else {
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    if (svc.addUser(username, password)) {
                        success = "User added.";
                    } else {
                        error = "Failed to add user (username may exist).";
                    }
                }
            } else if ("delete-user".equals(action)) {
                if (!svc.isSuperAdmin(current)) {
                    error = "Only SUPER_ADMIN can delete users.";
                } else {
                    String idStr = request.getParameter("id");
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id == current.getId()) {
                            error = "You cannot delete your own account while logged in.";
                        } else if (svc.removeUser(id)) {
                            success = "User deleted.";
                        } else {
                            error = "Failed to delete user.";
                        }
                    } catch (NumberFormatException e) {
                        error = "Invalid user id.";
                    }
                }
            } else if ("update-role".equals(action)) {
                if (!svc.isSuperAdmin(current)) {
                    error = "Only SUPER_ADMIN can update roles.";
                } else {
                    String idStr = request.getParameter("id");
                    String role = request.getParameter("role");
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id == current.getId() && "ADMIN".equals(role)) {
                            error = "You cannot demote yourself from SUPER_ADMIN.";
                        } else if (svc.updateRole(id, role)) {
                            success = "Role updated.";
                        } else {
                            error = "Failed to update role.";
                        }
                    } catch (NumberFormatException e) {
                        error = "Invalid user id.";
                    }
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
