package com.pahana.controller;

import com.pahana.model.Bill;
import com.pahana.model.Customer;
import com.pahana.service.BillService;
import com.pahana.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class BillController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Customer> customers = CustomerService.getInstance().getAllCustomers();
        request.setAttribute("customers", customers);
        request.setAttribute("items", com.pahana.service.ItemService.getInstance().getAllItems());

        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            handleDelete(request, response);
            return;
        }
        if ("view".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    int billId = Integer.parseInt(idStr);
                    Bill bill = BillService.getInstance().getBillWithItems(billId);
                    if (bill != null) {
                        request.setAttribute("bill", bill);
                        String customerName = null;
                        for (Customer c : customers) {
                            if (c.getId() == bill.getCustomerId()) {
                                customerName = c.getName();
                                break;
                            }
                        }
                        request.setAttribute("customerName", customerName);
                    } else {
                        request.setAttribute("error", "Bill not found.");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid bill id.");
                }
            }
            request.setAttribute("bills", BillService.getInstance().getAllBills());
            request.getRequestDispatcher("/WEB-INF/view/bill.jsp").forward(request, response);
            return;
        }

        String customerIdStr = request.getParameter("customerId");
        if (customerIdStr != null && !customerIdStr.isEmpty()) {
            try {
                int customerId = Integer.parseInt(customerIdStr);
                List<Bill> bills = BillService.getInstance().getBillsByCustomerId(customerId);
                request.setAttribute("bills", bills);
            } catch (NumberFormatException ignored) {
                request.setAttribute("bills", BillService.getInstance().getAllBills());
            }
        } else {
            request.setAttribute("bills", BillService.getInstance().getAllBills());
        }
        request.getRequestDispatcher("/WEB-INF/view/bill.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerIdStr = request.getParameter("customerId");
        String[] itemIds = request.getParameterValues("itemId");
        String[] quantities = request.getParameterValues("quantity");
        String[] unitPrices = request.getParameterValues("unitPrice");
        String error = null;
        Bill bill = null;
        List<Customer> customers = CustomerService.getInstance().getAllCustomers();
        request.setAttribute("customers", customers);
        request.setAttribute("items", com.pahana.service.ItemService.getInstance().getAllItems());

        if (customerIdStr == null || customerIdStr.isEmpty()) {
            error = "Please select a customer.";
        } else if (itemIds == null || quantities == null || unitPrices == null || itemIds.length == 0) {
            error = "Please add at least one item to the bill.";
        }

        List<com.pahana.model.BillItem> items = new java.util.ArrayList<>();
        int customerId = 0;
        if (error == null) {
            try {
                customerId = Integer.parseInt(customerIdStr);
                java.util.Map<Integer, com.pahana.model.Item> itemMap = new java.util.HashMap<>();
                java.util.List<com.pahana.model.Item> allItems = com.pahana.service.ItemService.getInstance()
                        .getAllItems();
                for (com.pahana.model.Item it : allItems) {
                    itemMap.put(it.getId(), it);
                }

                java.util.Map<Integer, Integer> requestedQty = new java.util.HashMap<>();
                for (int i = 0; i < itemIds.length; i++) {
                    if (itemIds[i] == null || itemIds[i].isEmpty())
                        continue;
                    int itemId = Integer.parseInt(itemIds[i]);
                    com.pahana.model.Item itemObj = itemMap.get(itemId);
                    if (itemObj == null) {
                        error = "Invalid item selected.";
                        break;
                    }
                    if (quantities[i] == null || quantities[i].isEmpty()) {
                        error = "Quantity is required.";
                        break;
                    }
                    int qty = Integer.parseInt(quantities[i]);
                    if (qty <= 0) {
                        error = "Quantity must be positive.";
                        break;
                    }
                    requestedQty.put(itemId, requestedQty.getOrDefault(itemId, 0) + qty);
                }

                if (error == null) {
                    for (java.util.Map.Entry<Integer, Integer> e : requestedQty.entrySet()) {
                        com.pahana.model.Item it = itemMap.get(e.getKey());
                        int available = (it == null) ? 0 : it.getQuantity();
                        if (e.getValue() > available) {
                            String name = (it == null || it.getName() == null) ? ("#" + e.getKey()) : it.getName();
                            error = "Requested quantity (" + e.getValue() + ") for '" + name
                                    + "' exceeds available stock (" + available + ").";
                            break;
                        }
                    }
                }

                if (error == null) {
                    for (int i = 0; i < itemIds.length; i++) {
                        if (itemIds[i] == null || itemIds[i].isEmpty())
                            continue;
                        int itemId = Integer.parseInt(itemIds[i]);
                        int qty = Integer.parseInt(quantities[i]);
                        com.pahana.model.Item itemObj = itemMap.get(itemId);
                        if (itemObj == null) {
                            error = "Invalid item selected.";
                            break;
                        }
                        double up = itemObj.getPrice();
                        if (up < 0) {
                            error = "Unit price must be non-negative.";
                            break;
                        }
                        com.pahana.model.BillItem bi = new com.pahana.model.BillItem(0, 0, itemId, qty, up, up * qty);
                        items.add(bi);
                    }
                    if (items.isEmpty() && error == null) {
                        error = "Please add at least one valid item.";
                    }
                }
            } catch (NumberFormatException e) {
                error = "Invalid item inputs.";
            }
        }

        if (error == null) {
            Customer selected = null;
            for (Customer c : customers) {
                if (c.getId() == customerId) {
                    selected = c;
                    break;
                }
            }
            if (selected == null) {
                error = "Selected customer not found.";
            } else {
                bill = BillService.getInstance().createBillWithItems(customerId, items);
                if (bill == null) {
                    error = "Insufficient stock for one or more items. Please refresh and try again.";
                } else {
                    request.setAttribute("bill", bill);
                    request.setAttribute("customerName", selected.getName());
                    List<Bill> bills = BillService.getInstance().getBillsByCustomerId(customerId);
                    request.setAttribute("bills", bills);
                    request.setAttribute("success", "Bill created successfully.");
                }
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher("/WEB-INF/view/bill.jsp").forward(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession(false);
        com.pahana.model.User user = (session != null) ? (com.pahana.model.User) session.getAttribute("user") : null;
        if (user == null || user.getRole() == null || !"SUPER_ADMIN".equals(user.getRole())) {
            request.setAttribute("error", "You are not authorized to delete bills.");
            request.setAttribute("bills", BillService.getInstance().getAllBills());
            request.getRequestDispatcher("/WEB-INF/view/bill.jsp").forward(request, response);
            return;
        }
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                BillService.getInstance().deleteBill(id);
            } catch (NumberFormatException ignored) {
            }
        }
        session = request.getSession();
        session.setAttribute("success", "Bill deleted successfully.");
        response.sendRedirect("bill");
    }
}
