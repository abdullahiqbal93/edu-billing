package com.pahana.service;

import com.pahana.dao.BillDAO;
import com.pahana.dao.ItemDAO;
import com.pahana.model.Bill;
import com.pahana.model.Item;
import com.pahana.model.Customer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportService {
    private static ReportService instance;
    private BillDAO billDAO;
    private ItemDAO itemDAO;

    private ReportService() {
        billDAO = BillDAO.getInstance();
        itemDAO = ItemDAO.getInstance();
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public byte[] generateSalesReportPdf() throws IOException {
        return generateSalesReportPdf(null, null);
    }

    public byte[] generateSalesReportPdf(String startDate, String endDate) throws IOException {
        List<Bill> bills;
        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            bills = billDAO.getBillsByDateRange(startDate, endDate);
        } else {
            bills = billDAO.getAllBills();
        }

        PDDocument doc = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float margin = 50f;
            PDRectangle media = page.getMediaBox();
            float tableWidth = media.getWidth() - 2 * margin;
            float yStart = media.getHeight() - margin;
            float y = yStart;

            float idW = 40f;
            float dateW = 110f;
            float unitsW = 60f;
            float amountW = 90f;
            float custW = tableWidth - (idW + dateW + unitsW + amountW);
            float[] colWidths = {idW, custW, dateW, unitsW, amountW};
            float x = margin;
            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

            int pageNum = 1;
            float cellPadding = 6f;
            float rowFontSize = 10f;

            cs.setNonStrokingColor(new Color(0x1f, 0x4e, 0x9a));
            cs.addRect(margin, y - 56, tableWidth, 56);
            cs.fill();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
            cs.setNonStrokingColor(Color.WHITE);
            cs.newLineAtOffset(margin + 10, y - 34);
            try { cs.showText("Pahana Edu Bookshop"); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 10);
            cs.newLineAtOffset(margin + 10, y - 48);
            String range = (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) ? (startDate + " to " + endDate) : "All time";
            try { cs.showText("Sales Report — " + range); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 9);
            cs.setNonStrokingColor(Color.WHITE);
            cs.newLineAtOffset(margin + tableWidth - 220, y - 20);
            String genDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            try { cs.showText("Generated: " + genDate); } catch (IOException ignored) {}
            cs.endText();

            y -= 80;

            cs.setNonStrokingColor(new Color(230, 230, 230));
            cs.addRect(x, y - 22, tableWidth, 22);
            cs.fill();

            cs.setNonStrokingColor(Color.BLACK);
            float headerFontSize = 11f;
            float[] colX = new float[colWidths.length];
            colX[0] = x;
            for (int i = 1; i < colWidths.length; i++) colX[i] = colX[i - 1] + colWidths[i - 1];

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize);
            cs.newLineAtOffset(colX[0] + cellPadding, y - 16);
            try { cs.showText("ID"); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize);
            cs.newLineAtOffset(colX[1] + cellPadding, y - 16);
            try { cs.showText("Customer"); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize);
            cs.newLineAtOffset(colX[2] + cellPadding, y - 16);
            try { cs.showText("Date"); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize);
            String unitsHdr = "Units";
            float unitsHdrW = PDType1Font.HELVETICA_BOLD.getStringWidth(unitsHdr) / 1000 * headerFontSize;
            float unitsHdrX = colX[3] + colWidths[3] - cellPadding - unitsHdrW;
            cs.newLineAtOffset(unitsHdrX, y - 16);
            try { cs.showText(unitsHdr); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize);
            String amtHdr = "Amount";
            float amtHdrW = PDType1Font.HELVETICA_BOLD.getStringWidth(amtHdr) / 1000 * headerFontSize;
            float amtHdrX = colX[4] + colWidths[4] - cellPadding - amtHdrW;
            cs.newLineAtOffset(amtHdrX, y - 16);
            try { cs.showText(amtHdr); } catch (IOException ignored) {}
            cs.endText();

            y -= 26;

            cs.setFont(PDType1Font.HELVETICA, 10);
            double totalAmount = 0;
            int totalUnits = 0;
            CustomerService custSvc = CustomerService.getInstance();

            for (Bill b : bills) {
                if (y < margin + 60) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                    cs.setNonStrokingColor(Color.DARK_GRAY);
                    cs.newLineAtOffset(margin, 30);
                    try { cs.showText("Page " + pageNum); } catch (IOException ignored) {}
                    cs.endText();
                    cs.close();

                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    pageNum++;
                    y = page.getMediaBox().getHeight() - margin;

                    cs.setNonStrokingColor(new Color(230, 230, 230));
                    cs.addRect(x, y - 22, tableWidth, 22);
                    cs.fill();
                    cs.setNonStrokingColor(Color.BLACK);
                    float headerFontSize2 = 11f;
                    float[] colX2 = new float[colWidths.length];
                    colX2[0] = x;
                    for (int i = 1; i < colWidths.length; i++) colX2[i] = colX2[i - 1] + colWidths[i - 1];

                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize2); cs.newLineAtOffset(colX2[0] + cellPadding, y - 16); try { cs.showText("ID"); } catch (IOException ignored) {} cs.endText();
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize2); cs.newLineAtOffset(colX2[1] + cellPadding, y - 16); try { cs.showText("Customer"); } catch (IOException ignored) {} cs.endText();
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize2); cs.newLineAtOffset(colX2[2] + cellPadding, y - 16); try { cs.showText("Date"); } catch (IOException ignored) {} cs.endText();
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize2); String unitsHdr2 = "Units"; float unitsHdrW2 = PDType1Font.HELVETICA_BOLD.getStringWidth(unitsHdr2) / 1000 * headerFontSize2; float unitsHdrX2 = colX2[3] + colWidths[3] - cellPadding - unitsHdrW2; cs.newLineAtOffset(unitsHdrX2, y - 16); try { cs.showText(unitsHdr2); } catch (IOException ignored) {} cs.endText();
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize2); String amtHdr2 = "Amount"; float amtHdrW2 = PDType1Font.HELVETICA_BOLD.getStringWidth(amtHdr2) / 1000 * headerFontSize2; float amtHdrX2 = colX2[4] + colWidths[4] - cellPadding - amtHdrW2; cs.newLineAtOffset(amtHdrX2, y - 16); try { cs.showText(amtHdr2); } catch (IOException ignored) {} cs.endText();
                    y -= 26;
                }

                String custName = "#" + b.getCustomerId();
                try {
                    Customer c = custSvc.findCustomerById(b.getCustomerId());
                    if (c != null && c.getName() != null) custName = c.getName();
                } catch (Exception ignored) {}

                String date = b.getCreatedAt() == null ? "" : b.getCreatedAt();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, rowFontSize);
                cs.newLineAtOffset(colX[0] + cellPadding, y - 14);
                try { cs.showText(String.valueOf(b.getId())); } catch (IOException ignored) {}
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, rowFontSize);
                String custFit = fitText(PDType1Font.HELVETICA, rowFontSize, custName, colWidths[1] - 2 * cellPadding);
                cs.newLineAtOffset(colX[1] + cellPadding, y - 14);
                try { cs.showText(custFit); } catch (IOException ignored) {}
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, rowFontSize);
                String dateFit = fitText(PDType1Font.HELVETICA, rowFontSize, date, colWidths[2] - 2 * cellPadding);
                cs.newLineAtOffset(colX[2] + cellPadding, y - 14);
                try { cs.showText(dateFit); } catch (IOException ignored) {}
                cs.endText();

                String unitsStr = String.valueOf(b.getTotalUnits());
                float unitsWidth = PDType1Font.HELVETICA.getStringWidth(unitsStr) / 1000 * rowFontSize;
                float unitsPos = colX[3] + colWidths[3] - cellPadding - unitsWidth;
                cs.beginText();
                cs.newLineAtOffset(unitsPos, y - 14);
                try { cs.showText(unitsStr); } catch (IOException ignored) {}
                cs.endText();

                String amtDisplay = currency.format(b.getTotalAmount());
                float amtWidth = PDType1Font.HELVETICA.getStringWidth(amtDisplay) / 1000 * rowFontSize;
                float amtPos = colX[4] + colWidths[4] - cellPadding - amtWidth;
                cs.beginText();
                cs.newLineAtOffset(amtPos, y - 14);
                try { cs.showText(amtDisplay); } catch (IOException ignored) {}
                cs.endText();

                cs.setStrokingColor(new Color(220, 220, 220));
                cs.moveTo(x, y - 20);
                cs.lineTo(x + tableWidth, y - 20);
                cs.stroke();

                y -= 20;
                totalAmount += b.getTotalAmount();
                totalUnits += b.getTotalUnits();
            }

            if (y < margin + 40) {
                cs.close();
                page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);
                y = page.getMediaBox().getHeight() - margin;
            }
            cs.setNonStrokingColor(new Color(245, 245, 245));
            cs.addRect(x, y - 20, tableWidth, 20);
            cs.fill();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
            cs.setNonStrokingColor(Color.BLACK);
            cs.newLineAtOffset(colX[0] + cellPadding, y - 14);
            try { cs.showText("Totals"); } catch (IOException ignored) {}
            cs.endText();

            String totalUnitsStr = String.valueOf(totalUnits);
            float totalUnitsW = PDType1Font.HELVETICA_BOLD.getStringWidth(totalUnitsStr) / 1000 * rowFontSize;
            float totalUnitsPos = colX[3] + colWidths[3] - cellPadding - totalUnitsW;
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, rowFontSize);
            cs.newLineAtOffset(totalUnitsPos, y - 14);
            try { cs.showText(totalUnitsStr); } catch (IOException ignored) {}
            cs.endText();

            String totalAmtDisplay = currency.format(totalAmount);
            float totalAmtW = PDType1Font.HELVETICA_BOLD.getStringWidth(totalAmtDisplay) / 1000 * rowFontSize;
            float totalAmtPos = colX[4] + colWidths[4] - cellPadding - totalAmtW;
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, rowFontSize);
            cs.newLineAtOffset(totalAmtPos, y - 14);
            try { cs.showText(totalAmtDisplay); } catch (IOException ignored) {}
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
            cs.setNonStrokingColor(Color.DARK_GRAY);
            cs.newLineAtOffset(margin, 30);
            try { cs.showText("Page " + pageNum); } catch (IOException ignored) {}
            cs.endText();

            cs.close();
            doc.save(out);
            return out.toByteArray();
        } finally {
            try { doc.close(); } catch (Exception ignored) {}
        }
    }

    public byte[] generateInventoryReportPdf() throws IOException {
        List<Item> items = itemDAO.getAllItems();
        PDDocument doc = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float margin = 50f;
            PDRectangle media = page.getMediaBox();
            float tableWidth = media.getWidth() - 2 * margin;
            float y = media.getHeight() - margin;

            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            float idW = 40f;
            float qtyW = 60f;
            float priceW = 90f;
            float nameW = tableWidth - (idW + qtyW + priceW);
            float[] colWidths = {idW, nameW, priceW, qtyW};
            float[] colX = new float[colWidths.length];
            colX[0] = margin;
            for (int i = 1; i < colWidths.length; i++) colX[i] = colX[i - 1] + colWidths[i - 1];

            cs.setNonStrokingColor(new Color(0x1f, 0x4e, 0x9a));
            cs.addRect(margin, y - 56, tableWidth, 56);
            cs.fill();
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, 18); cs.setNonStrokingColor(Color.WHITE); cs.newLineAtOffset(margin + 10, y - 34); try { cs.showText("Inventory Report"); } catch (IOException ignored) {} cs.endText();
            y -= 70;

            cs.setNonStrokingColor(new Color(230, 230, 230));
            cs.addRect(margin, y - 22, tableWidth, 22);
            cs.fill();

            float headerFontSize = 11f;
            float cellPadding = 6f;
            cs.setNonStrokingColor(Color.BLACK);
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(colX[0] + cellPadding, y - 16); try { cs.showText("ID"); } catch (IOException ignored) {} cs.endText();
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(colX[1] + cellPadding, y - 16); try { cs.showText("Name"); } catch (IOException ignored) {} cs.endText();
            String priceHdr = "Price";
            float priceHdrW = PDType1Font.HELVETICA_BOLD.getStringWidth(priceHdr) / 1000 * headerFontSize;
            float priceHdrX = colX[2] + colWidths[2] - cellPadding - priceHdrW;
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(priceHdrX, y - 16); try { cs.showText(priceHdr); } catch (IOException ignored) {} cs.endText();
            String qtyHdr = "Qty";
            float qtyHdrW = PDType1Font.HELVETICA_BOLD.getStringWidth(qtyHdr) / 1000 * headerFontSize;
            float qtyHdrX = colX[3] + colWidths[3] - cellPadding - qtyHdrW;
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(qtyHdrX, y - 16); try { cs.showText(qtyHdr); } catch (IOException ignored) {} cs.endText();

            y -= 26;
            float rowFontSize = 10f;
            int pageNum = 1;
            int totalQty = 0;
            double totalValue = 0d;

            for (Item it : items) {
                if (y < margin + 60) {
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9); cs.setNonStrokingColor(Color.DARK_GRAY); cs.newLineAtOffset(margin, 30); try { cs.showText("Page " + pageNum); } catch (IOException ignored) {} cs.endText();
                    cs.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    pageNum++;
                    y = page.getMediaBox().getHeight() - margin;

                    cs.setNonStrokingColor(new Color(230, 230, 230)); cs.addRect(margin, y - 22, tableWidth, 22); cs.fill();
                    cs.setNonStrokingColor(Color.BLACK);
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(colX[0] + cellPadding, y - 16); try { cs.showText("ID"); } catch (IOException ignored) {} cs.endText();
                    cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(colX[1] + cellPadding, y - 16); try { cs.showText("Name"); } catch (IOException ignored) {} cs.endText();
                    float priceHdrX2 = colX[2] + colWidths[2] - cellPadding - priceHdrW; cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(priceHdrX2, y - 16); try { cs.showText(priceHdr); } catch (IOException ignored) {} cs.endText();
                    float qtyHdrX2 = colX[3] + colWidths[3] - cellPadding - qtyHdrW; cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, headerFontSize); cs.newLineAtOffset(qtyHdrX2, y - 16); try { cs.showText(qtyHdr); } catch (IOException ignored) {} cs.endText();
                    y -= 26;
                }

                cs.beginText(); cs.setFont(PDType1Font.HELVETICA, rowFontSize); cs.newLineAtOffset(colX[0] + cellPadding, y - 14); try { cs.showText(String.valueOf(it.getId())); } catch (IOException ignored) {} cs.endText();

                String nameFit = fitText(PDType1Font.HELVETICA, rowFontSize, it.getName(), colWidths[1] - 2 * cellPadding);
                cs.beginText(); cs.setFont(PDType1Font.HELVETICA, rowFontSize); cs.newLineAtOffset(colX[1] + cellPadding, y - 14); try { cs.showText(nameFit); } catch (IOException ignored) {} cs.endText();

                String priceDisplay = currency.format(it.getPrice());
                float priceWpts = PDType1Font.HELVETICA.getStringWidth(priceDisplay) / 1000 * rowFontSize;
                float pricePos = colX[2] + colWidths[2] - cellPadding - priceWpts;
                cs.beginText(); cs.setFont(PDType1Font.HELVETICA, rowFontSize); cs.newLineAtOffset(pricePos, y - 14); try { cs.showText(priceDisplay); } catch (IOException ignored) {} cs.endText();

             
                String qtyStr = String.valueOf(it.getQuantity());
                float qtyWpts = PDType1Font.HELVETICA.getStringWidth(qtyStr) / 1000 * rowFontSize;
                float qtyPos = colX[3] + colWidths[3] - cellPadding - qtyWpts;
                cs.beginText(); cs.setFont(PDType1Font.HELVETICA, rowFontSize); cs.newLineAtOffset(qtyPos, y - 14); try { cs.showText(qtyStr); } catch (IOException ignored) {} cs.endText();

                cs.setStrokingColor(new Color(220, 220, 220)); cs.moveTo(margin, y - 20); cs.lineTo(margin + tableWidth, y - 20); cs.stroke();

                y -= 20;
                totalQty += it.getQuantity();
                totalValue += it.getQuantity() * it.getPrice();
            }

            if (y < margin + 40) {
                cs.close(); page = new PDPage(PDRectangle.LETTER); doc.addPage(page); cs = new PDPageContentStream(doc, page); y = page.getMediaBox().getHeight() - margin;
            }
            cs.setNonStrokingColor(new Color(245, 245, 245)); cs.addRect(margin, y - 20, tableWidth, 20); cs.fill();

            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, 11); cs.setNonStrokingColor(Color.BLACK); cs.newLineAtOffset(colX[0] + cellPadding, y - 14); try { cs.showText("Totals"); } catch (IOException ignored) {} cs.endText();

            String totalQtyStr = String.valueOf(totalQty);
            float totalQtyW = PDType1Font.HELVETICA_BOLD.getStringWidth(totalQtyStr) / 1000 * rowFontSize;
            float totalQtyPos = colX[3] + colWidths[3] - cellPadding - totalQtyW;
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, rowFontSize); cs.newLineAtOffset(totalQtyPos, y - 14); try { cs.showText(totalQtyStr); } catch (IOException ignored) {} cs.endText();

            String totalValueDisplay = currency.format(totalValue);
            float totalValW = PDType1Font.HELVETICA_BOLD.getStringWidth(totalValueDisplay) / 1000 * rowFontSize;
            float totalValPos = colX[2] + colWidths[2] - cellPadding - totalValW;
            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_BOLD, rowFontSize); cs.newLineAtOffset(totalValPos, y - 14); try { cs.showText(totalValueDisplay); } catch (IOException ignored) {} cs.endText();

            cs.beginText(); cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9); cs.setNonStrokingColor(Color.DARK_GRAY); cs.newLineAtOffset(margin, 30); try { cs.showText("Page " + 1); } catch (IOException ignored) {} cs.endText();

            cs.close(); doc.save(out); return out.toByteArray();
        } finally {
            try { doc.close(); } catch (Exception ignored) {}
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private String fitText(PDType1Font font, float fontSize, String text, float maxWidth) {
        if (text == null) return "";
        try {
            float textWidth = font.getStringWidth(text) / 1000 * fontSize;
            if (textWidth <= maxWidth) return text;
            String ell = "...";
            float ellWidth = font.getStringWidth(ell) / 1000 * fontSize;
            int len = text.length();
            while (len > 0) {
                String sub = text.substring(0, len);
                textWidth = font.getStringWidth(sub) / 1000 * fontSize;
                if (textWidth + ellWidth <= maxWidth) return sub + ell;
                len--;
            }
            return "";
        } catch (IOException e) {
            int maxChars = Math.max(0, (int) (maxWidth / (fontSize * 0.5f)) - 3);
            if (text.length() <= maxChars) return text;
            return text.substring(0, Math.max(0, maxChars)) + "...";
        }
    }
}
