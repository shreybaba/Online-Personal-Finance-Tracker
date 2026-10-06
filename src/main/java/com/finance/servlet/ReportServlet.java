package com.finance.servlet;

import com.finance.service.ReportService;
import com.finance.service.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** GET /user/reports?startDate=&endDate=&category= (all filters optional) */
@WebServlet("/user/reports")
public class ReportServlet extends BaseServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = currentUser(request).getId();
        try {
            request.setAttribute("categoriesList", reportService.categories(userId));
            try {
                ReportService.Report report = reportService.build(userId,
                        request.getParameter("startDate"),
                        request.getParameter("endDate"),
                        request.getParameter("category"));
                request.setAttribute("reportTotalSpending", report.totalSpending());
                request.setAttribute("reportAvgExpense", report.averageExpense());
                request.setAttribute("categoryReportList", report.categoryTotals());
                request.setAttribute("budgetComparisonList", report.budgetComparison());
            } catch (ValidationException e) {
                request.setAttribute("errorMessage", e.getMessage());
            }
        } catch (SQLException e) {
            throw new ServletException("Could not build report", e);
        }
        render(request, response, "user/reports");
    }
}
