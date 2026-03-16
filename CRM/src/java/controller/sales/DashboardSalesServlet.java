package controller.sales;

import dal.OpportunityDAO;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/sales/dashboard")
public class DashboardSalesServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        boolean isManager = userSession.isAdmin();
        Integer salesId = isManager ? null : userSession.getStaff().getId();

        try {
            // All opportunities
            List<model.sales.Opportunity> openOpps = opportunityDAO.filterOpportunities(null, null, "Open", salesId);
            List<model.sales.Opportunity> wonOpps  = opportunityDAO.filterOpportunities(null, null, "Won",  salesId);
            List<model.sales.Opportunity> lostOpps = opportunityDAO.filterOpportunities(null, null, "Lost", salesId);

            // KPI calculations
            int totalOpen = openOpps.size();
            int totalWon  = wonOpps.size();
            int totalLost = lostOpps.size();
            int total = totalOpen + totalWon + totalLost;

            BigDecimal wonValue = wonOpps.stream()
                .map(o -> o.getExpectedValue() != null ? o.getExpectedValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Count by stage – JSP reads "stageCount"
            Map<String, Integer> stageCount = isManager ?
                opportunityDAO.countByStage() : opportunityDAO.countByStageForSales(salesId);

            // Revenue forecast by month → convert List<Object[]> to LinkedHashMap<month,revenue>
            int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            List<Object[]> rawRevenue = opportunityDAO.getMonthlyRevenue(year);
            Map<Integer, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
            for (Object[] row : rawRevenue) {
                monthlyRevenue.put((Integer) row[0], (BigDecimal) row[1]);
            }

            // Top 5 closest to close date
            List<model.sales.Opportunity> topOpps = openOpps.stream()
                .filter(o -> o.getExpectedCloseDate() != null)
                .sorted(Comparator.comparing(model.sales.Opportunity::getExpectedCloseDate))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

            // Lost reason analysis – JSP reads "lostReasonData"
            Map<String, Long> lostReasonData = new LinkedHashMap<>();
            for (model.sales.Opportunity o : lostOpps) {
                String r = o.getLostReason() != null ? o.getLostReason() : "Không rõ";
                lostReasonData.merge(r, 1L, Long::sum);
            }

            // Set attributes matching JSP EL names
            request.setAttribute("kpiTotalOpps", total);
            request.setAttribute("kpiWon", totalWon);
            request.setAttribute("kpiLost", totalLost);
            request.setAttribute("kpiValueWon", wonValue);
            request.setAttribute("kpiQuotations", 0);
            request.setAttribute("stageCount", stageCount);
            request.setAttribute("monthlyRevenue", monthlyRevenue);
            request.setAttribute("topOpps", topOpps);
            request.setAttribute("lostReasonData", lostReasonData);
            request.setAttribute("isManager", isManager);
            request.getRequestDispatcher("/sales/sales-dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
