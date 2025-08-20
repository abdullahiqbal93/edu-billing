package com.pahana.controller;

import com.pahana.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class ReportController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        reportService = ReportService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
    String start = request.getParameter("startDate");
    String end = request.getParameter("endDate");
        if (type == null) type = "sales";

        byte[] pdf = null;
            try {
                if ("inventory".equalsIgnoreCase(type)) {
                    pdf = reportService.generateInventoryReportPdf();
                    response.setHeader("Content-Disposition", "attachment; filename=inventory_report.pdf");
                } else {
                    if (start != null && end != null && !start.isEmpty() && !end.isEmpty()) {
                        pdf = reportService.generateSalesReportPdf(start, end);
                        response.setHeader("Content-Disposition", "attachment; filename=sales_report_" + start + "_to_" + end + ".pdf");
                    } else {
                        pdf = reportService.generateSalesReportPdf();
                        response.setHeader("Content-Disposition", "attachment; filename=sales_report.pdf");
                    }
                }
            response.setContentType("application/pdf");
            response.setContentLength(pdf.length);
            response.getOutputStream().write(pdf);
        } catch (Exception e) {
            throw new ServletException("Failed to generate report", e);
        }
    }
}
