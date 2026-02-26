package controller.sales;

import dal.OpportunityDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;



@WebServlet("/sales/opportunity-export")
public class OpportunityExportServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String stage  = request.getParameter("stage");
        String status = request.getParameter("status");

        List<Opportunity> list = opportunityDAO.getForExport(stage, status);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"opportunities.csv\"");

        PrintWriter out = response.getWriter();
        // BOM for Excel UTF-8
        out.write('\uFEFF');
        out.println("ID,Tiêu đề,Khách hàng,Sales phụ trách,Stage,Status,Giá trị dự kiến,Xác suất (%),Ngày dự kiến đóng,Nguồn,Ngày tạo");

        for (Opportunity o : list) {
            out.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.0f,\"%s\",\"%s\",\"%s\"%n",
                o.getId(),
                safe(o.getTitle()),
                safe(o.getCustomerName()),
                safe(o.getAssignedSalesName()),
                safe(o.getStage()),
                safe(o.getStatus()),
                o.getExpectedValue() != null ? o.getExpectedValue().doubleValue() : 0,
                o.getCloseProbability(),
                o.getExpectedCloseDate() != null ? o.getExpectedCloseDate().toString() : "",
                safe(o.getSource()),
                o.getCreatedAt() != null ? o.getCreatedAt().toString() : ""
            );
        }
        out.flush();
    }

    private String safe(String s) { return s != null ? s.replace("\"", "\"\"") : ""; }
}
