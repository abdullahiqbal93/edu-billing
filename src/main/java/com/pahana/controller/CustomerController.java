package com.pahana.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.pahana.model.Customer;
import com.pahana.service.CustomerService;

public class CustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        customerService = CustomerService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            listCustomers(request, response);
        } else if (action.equals("add")) {
            showAddForm(request, response);
        } else if (action.equals("edit")) {
            showEditForm(request, response);
        } else if (action.equals("delete")) {
            deleteCustomer(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action.equals("add")) {
            addCustomer(request, response);
        } else if (action.equals("update")) {
            updateCustomer(request, response);
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        List<Customer> customerList = new ArrayList<>();
        if (q != null && !q.trim().isEmpty()) {
            customerList = customerService.searchCustomers(q.trim());
            request.setAttribute("q", q.trim());
        } else {
            customerList = customerService.getAllCustomers();
        }
        request.setAttribute("customers", customerList);
        request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Missing customer ID.");
            request.getRequestDispatcher("WEB-INF/view/error.jsp").forward(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid customer ID format.");
            request.getRequestDispatcher("WEB-INF/view/error.jsp").forward(request, response);
            return;
        }

        Customer existingCustomer = customerService.findCustomerById(id);
        if (existingCustomer == null) {
            request.setAttribute("error", "Customer not found.");
            request.getRequestDispatcher("WEB-INF/view/error.jsp").forward(request, response);
            return;
        }
        request.setAttribute("customer", existingCustomer);
        request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephone = request.getParameter("telephone");
        String email = request.getParameter("email");

        String error = null;

        if (accountNumber == null || accountNumber.isEmpty()
                || name == null || name.isEmpty()
                || address == null || address.isEmpty()
                || telephone == null || telephone.isEmpty()
                || email == null || email.isEmpty()) {
            error = "All fields are required.";
        }
        if (error == null && name != null && name.matches(".*\\d.*")) {
            error = "Customer name cannot contain numbers.";
        }
        if (error == null && telephone != null) {
            String phoneDigits = telephone.replaceAll("[^0-9]", "");
            if (phoneDigits.length() < 7 || phoneDigits.length() > 15) {
                error = "Please enter a valid phone number (7-15 digits).";
            }
        }
        if (error == null && email != null && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            error = "Please enter a valid email address.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephone", telephone);
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
            return;
        }

        Customer existingByAcc = customerService.findCustomerByAccountNumber(accountNumber);
        if (existingByAcc != null) {
            request.setAttribute("error", "Account number already exists.");
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephone", telephone);
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setName(name);
        customer.setAddress(address);
        customer.setTelephone(telephone);
        customer.setEmail(email);

        boolean created = customerService.addCustomer(customer);
        if (!created) {
            request.setAttribute("error", "Failed to add customer. Account number may already exist.");
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephone", telephone);
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute("success", "Customer added successfully.");
        response.sendRedirect("customer?action=list");
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephone = request.getParameter("telephone");
        String email = request.getParameter("email");

        String error = null;

        if (idStr == null || idStr.isEmpty()
                || accountNumber == null || accountNumber.isEmpty()
                || name == null || name.isEmpty()
                || address == null || address.isEmpty()
                || telephone == null || telephone.isEmpty()
                || email == null || email.isEmpty()) {
            error = "All fields are required.";
        }

        int id = 0;
        if (error == null) {
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                error = "Invalid customer ID format.";
            }
        }
        if (error == null && name != null && name.matches(".*\\d.*")) {
            error = "Customer name cannot contain numbers.";
        }
        if (error == null && telephone != null) {
            String phoneDigits = telephone.replaceAll("[^0-9]", "");
            if (phoneDigits.length() < 7 || phoneDigits.length() > 15) {
                error = "Please enter a valid phone number (7-15 digits).";
            }
        }
        if (error == null && email != null && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            error = "Please enter a valid email address.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("id", idStr);
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephone", telephone);
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
            return;
        }

        Customer existingByAcc = customerService.findCustomerByAccountNumber(accountNumber);
        if (existingByAcc != null && existingByAcc.getId() != id) {
            request.setAttribute("error", "Account number already exists.");
            request.setAttribute("id", idStr);
            request.setAttribute("accountNumber", accountNumber);
            request.setAttribute("name", name);
            request.setAttribute("address", address);
            request.setAttribute("telephone", telephone);
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setId(id);
        customer.setAccountNumber(accountNumber);
        customer.setName(name);
        customer.setAddress(address);
        customer.setTelephone(telephone);
        customer.setEmail(email);

        customerService.updateCustomer(customer);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Customer updated successfully.");
        response.sendRedirect("customer?action=list");
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        com.pahana.model.User user = (session != null) ? (com.pahana.model.User) session.getAttribute("user") : null;
        if (user == null || user.getRole() == null || !"SUPER_ADMIN".equals(user.getRole())) {
            request.setAttribute("error", "You are not authorized to delete customers.");
            listCustomers(request, response);
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Missing customer ID for deletion.");
            request.getRequestDispatcher("WEB-INF/view/error.jsp").forward(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid customer ID format for deletion.");
            request.getRequestDispatcher("WEB-INF/view/error.jsp").forward(request, response);
            return;
        }

        customerService.deleteCustomer(id);
        session = request.getSession();
        session.setAttribute("success", "Customer deleted successfully.");
        response.sendRedirect("customer?action=list");
    }
}
