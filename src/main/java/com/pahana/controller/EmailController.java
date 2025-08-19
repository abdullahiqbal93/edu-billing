package com.pahana.controller;

import com.pahana.model.Bill;
import com.pahana.model.Customer;
import com.pahana.service.BillService;
import com.pahana.service.CustomerService;
import com.pahana.util.EmailUtil;
import com.pahana.service.ItemService;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmailController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String billIdStr = request.getParameter("billId");
        if (billIdStr == null) {
            response.sendError(400, "billId required");
            return;
        }
        int billId;
        try {
            billId = Integer.parseInt(billIdStr);
        } catch (NumberFormatException ex) {
            response.sendError(400, "invalid billId");
            return;
        }

        Bill bill = BillService.getInstance().getBillWithItems(billId);
        if (bill == null) {
            response.sendError(404, "Bill not found");
            return;
        }

        Customer customer = CustomerService.getInstance().findCustomerById(bill.getCustomerId());
        if (customer == null) {
            response.sendError(404, "Customer not found");
            return;
        }
        String toEmail = customer.getEmail() == null ? "" : customer.getEmail().trim();
        if (toEmail.isEmpty()) {
            response.sendError(409, "Customer email is missing");
            return;
        }
        try {
            InternetAddress.parse(toEmail, true);
        } catch (AddressException ex) {
            response.sendError(409, "Invalid customer email: " + ex.getRef());
            return;
        }

        Map<Integer, String> itemNames = new HashMap<>();
        for (com.pahana.model.Item it : ItemService.getInstance().getAllItems()) {
            itemNames.put(it.getId(), it.getName());
        }

        String html = BillService.getInstance().buildReceiptHtml(bill, customer.getName(), itemNames);

        EmailUtil.Config cfg = EmailUtil.loadFromEnv();
        if (cfg.username == null || cfg.username.isEmpty() || cfg.password == null || cfg.password.isEmpty()) {
            String hint = "SMTP credentials are not configured. Set MAIL_USER and MAIL_PASS as environment variables";
            if (cfg.host != null && cfg.host.toLowerCase().contains("gmail")) {
                hint += "; if using Gmail, create an App Password and use it for MAIL_PASS";
            }
            response.sendError(500, hint);
            return;
        }
        try {
            EmailUtil.sendHtml(cfg, toEmail, "Your Receipt - Pahana Edu Bookshop", html);
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"sent\"}");
        } catch (AddressException ae) {
            response.sendError(400, "Invalid email address: " + ae.getRef());
        } catch (MessagingException e) {
            response.sendError(500, "Failed to send email: " + e.getClass().getSimpleName() + "; from="
                    + EmailUtil.loadFromEnv().from + "; to=" + toEmail + "; message=" + e.getMessage());
        }
    }
}
