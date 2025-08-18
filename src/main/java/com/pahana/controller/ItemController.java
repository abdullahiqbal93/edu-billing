package com.pahana.controller;

import com.pahana.model.Item;
import com.pahana.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ItemController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        itemService = ItemService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            listItems(request, response);
        } else if (action.equals("add")) {
            showAddForm(request, response);
        } else if (action.equals("edit")) {
            showEditForm(request, response);
        } else if (action.equals("delete")) {
            deleteItem(request, response);
        } else {
            listItems(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addItem(request, response);
        } else if ("update".equals(action)) {
            updateItem(request, response);
        } else {
            response.sendRedirect("item?action=list");
        }
    }

    private void listItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        List<Item> items;
        if (q != null && !q.trim().isEmpty()) {
            items = itemService.searchItems(q.trim());
            request.setAttribute("q", q.trim());
        } else {
            items = itemService.getAllItems();
        }
        request.setAttribute("items", items);
        request.getRequestDispatcher("/WEB-INF/view/item.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/item.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Missing item ID.");
            listItems(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID format.");
            listItems(request, response);
            return;
        }

        Item existingItem = itemService.findItemById(id);
        if (existingItem == null) {
            request.setAttribute("error", "Item not found.");
            listItems(request, response);
            return;
        }

        request.setAttribute("item", existingItem);
        request.getRequestDispatcher("/WEB-INF/view/item.jsp").forward(request, response);
    }

    private void addItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String error = null;

        if (name == null || name.isEmpty() ||
                description == null || description.isEmpty() ||
                priceStr == null || priceStr.isEmpty() ||
                quantityStr == null || quantityStr.isEmpty()) {
            error = "All fields are required.";
        }

        double price = 0;
        int quantity = 0;
        if (error == null) {
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    error = "Price must be non-negative.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid price format.";
            }
        }
        if (error == null) {
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    error = "Quantity must be non-negative.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid quantity format.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("price", priceStr);
            request.setAttribute("quantity", quantityStr);
            request.getRequestDispatcher("/WEB-INF/view/item.jsp").forward(request, response);
            return;
        }

        Item item = new Item(0, name, description, price, quantity);
        itemService.addItem(item);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Item added successfully.");
        response.sendRedirect("item?action=list");
    }

    private void updateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String error = null;

        if (idStr == null || idStr.isEmpty() ||
                name == null || name.isEmpty() ||
                description == null || description.isEmpty() ||
                priceStr == null || priceStr.isEmpty() ||
                quantityStr == null || quantityStr.isEmpty()) {
            error = "All fields are required.";
        }

        int id = 0;
        double price = 0;
        int quantity = 0;
        if (error == null) {
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                error = "Invalid item ID format.";
            }
        }
        if (error == null) {
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    error = "Price must be non-negative.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid price format.";
            }
        }
        if (error == null) {
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    error = "Quantity must be non-negative.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid quantity format.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("id", idStr);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("price", priceStr);
            request.setAttribute("quantity", quantityStr);
            request.getRequestDispatcher("/WEB-INF/view/item.jsp").forward(request, response);
            return;
        }

        Item item = new Item(id, name, description, price, quantity);
        itemService.updateItem(item);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Item updated successfully.");
        response.sendRedirect("item?action=list");
    }

    private void deleteItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        com.pahana.model.User user = (session != null) ? (com.pahana.model.User) session.getAttribute("user") : null;
        if (user == null || user.getRole() == null || !"SUPER_ADMIN".equals(user.getRole())) {
            request.setAttribute("error", "You are not authorized to delete items.");
            listItems(request, response);
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Missing item ID for deletion.");
            listItems(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid item ID format for deletion.");
            listItems(request, response);
            return;
        }

        itemService.deleteItem(id);
        session = request.getSession();
        session.setAttribute("success", "Item deleted successfully.");
        response.sendRedirect("item?action=list");
    }
}
