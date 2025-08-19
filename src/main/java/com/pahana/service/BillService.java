package com.pahana.service;

import com.pahana.dao.BillDAO;
import com.pahana.model.Bill;
import com.pahana.model.BillItem;
import java.util.List;

public class BillService {
    private static BillService instance;
    private BillDAO billDAO;

    private BillService() {
        billDAO = BillDAO.getInstance();
    }

    public static synchronized BillService getInstance() {
        if (instance == null) {
            instance = new BillService();
        }
        return instance;
    }

    public List<Bill> getBillsByCustomerId(int customerId) {
        return billDAO.getBillsByCustomerId(customerId);
    }

    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }

    public Bill getBillWithItems(int billId) {
        Bill b = billDAO.getBillById(billId);
        if (b != null) {
            b.setItems(billDAO.getBillItems(billId));
        }
        return b;
    }

    public String buildReceiptHtml(Bill bill, String customerName, java.util.Map<Integer, String> itemNames) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:640px;margin:auto;border:1px solid #ddd;padding:16px'>");
        sb.append(
                "<div style='display:flex;align-items:center;gap:12px;padding-bottom:8px;border-bottom:1px solid #eee'>");
        sb.append(
                "<div><div style='font-size:18px;font-weight:700'>Pahana Edu Bookshop</div><div style='color:#666'>Official Receipt</div></div>");
        sb.append("</div>");
        sb.append("<table style='width:100%;margin-top:8px;font-size:14px'>");
        sb.append("<tr><td style='color:#666'>Invoice No.</td><td style='text-align:right'>#" + bill.getId()
                + "</td></tr>");
        sb.append("<tr><td style='color:#666'>Date</td><td style='text-align:right'>"
                + (bill.getCreatedAt() == null ? "" : bill.getCreatedAt()) + "</td></tr>");
        sb.append("<tr><td style='color:#666'>Customer</td><td style='text-align:right'>"
                + (customerName == null ? "N/A" : customerName) + " (ID: " + bill.getCustomerId() + ")</td></tr>");
        sb.append("</table>");

        sb.append("<table style='width:100%;margin-top:12px;border-collapse:collapse;font-size:13px'>");
        sb.append(
                "<thead><tr><th style='text-align:left;border-bottom:1px solid #ccc;padding:8px 6px'>Item</th><th style='text-align:right;border-bottom:1px solid #ccc;padding:8px 6px'>Qty</th><th style='text-align:right;border-bottom:1px solid #ccc;padding:8px 6px'>Unit</th><th style='text-align:right;border-bottom:1px solid #ccc;padding:8px 6px'>Amount</th></tr></thead>");
        sb.append("<tbody>");
        if (bill.getItems() != null)
            for (com.pahana.model.BillItem bi : bill.getItems()) {
                String name = itemNames == null ? null : itemNames.get(bi.getItemId());
                if (name == null)
                    name = "#" + bi.getItemId();
                sb.append("<tr>")
                        .append("<td style='padding:6px'>").append(name).append("</td>")
                        .append("<td style='padding:6px;text-align:right'>").append(String.valueOf(bi.getQuantity()))
                        .append("</td>")
                        .append("<td style='padding:6px;text-align:right'>")
                        .append(String.format("%.2f", bi.getUnitPrice())).append("</td>")
                        .append("<td style='padding:6px;text-align:right'>")
                        .append(String.format("%.2f", bi.getTotalPrice())).append("</td>")
                        .append("</tr>");
            }
        sb.append("</tbody></table>");
        sb.append(
                "<div style='margin-top:10px;border-top:1px solid #eee;padding-top:8px;display:flex;justify-content:space-between'>");
        sb.append("<div style='color:#666'>Total Units</div><div><b>").append(bill.getTotalUnits())
                .append("</b></div>");
        sb.append("</div>");
        sb.append(
                "<div style='margin-top:4px;border-top:1px dashed #ddd;padding-top:6px;display:flex;justify-content:space-between'>");
        sb.append("<div style='font-weight:700'>Total Amount</div><div style='font-weight:700'>LKR ")
                .append(String.format("%.2f", bill.getTotalAmount())).append("</div>");
        sb.append("</div>");
        sb.append(
                "<div style='margin-top:14px;color:#777;font-size:12px;text-align:center'>This is an automated email. Do not reply.</div>");
        sb.append("</div>");
        return sb.toString();
    }

    public Bill createBillWithItems(int customerId, java.util.List<BillItem> items) {
        int totalUnits = 0;
        double totalAmount = 0.0;
        for (BillItem bi : items) {
            totalUnits += bi.getQuantity();
            totalAmount += bi.getTotalPrice();
        }

        Bill bill = new Bill(0, customerId, totalUnits, totalAmount, null);
        int billId = billDAO.addBill(bill);
        if (billId > 0) {
            billDAO.addBillItems(billId, items);
            bill = billDAO.getBillById(billId);
            if (bill != null) {
                bill.setItems(items);
            } else {
                bill = new Bill(billId, customerId, totalUnits, totalAmount, null);
                bill.setItems(items);
            }
        }
        return bill;
    }

    public boolean deleteBill(int id) {
        return billDAO.deleteBill(id);
    }
}
