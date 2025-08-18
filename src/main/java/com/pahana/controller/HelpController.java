package com.pahana.controller;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class HelpController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/help.jsp").forward(request, response);
    }
}
