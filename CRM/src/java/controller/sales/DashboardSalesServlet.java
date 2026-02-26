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
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        boolean isManager = userSession.isAdmin() || userSession.hasRole("SALES_MANAGER");
        Integer salesId = isManager ? null : userSession.getStaff().getId();

        try {
            // All open opportunities
            List<model.sales.Opportunity> openOpps = opportunityDAO.filterOpportunities(null, null, "Open", salesId);
            List<model.sales.Opportunity> wonOpps  = opportunityDAO.filterOpportunities(null, null, "Won",  salesId);
            List<model.sales.Opportunity> lostOpps = opportunityDAO.filterOpportunities(null, null, "Lost", salesId);

            // KPI calculations
            int totalOpen = openOpps.size();
            int totalWon  = wonOpps.size();
            int totalLost = lostOpps.size();
            int total = totalOpen + totalWon + totalLost;

            BigDecimal totalValue = openOpps.stream()
                .map(o -> o.getExpectedValue() != null ? o.getExpectedValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal wonValue = wonOpps.stream()
                .map(o -> o.getExpectedValue() != null ? o.getExpectedValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            double winRate = total > 0 ? (totalWon * 100.0 / (totalWon + totalLost > 0 ? totalWon + totalLost : 1)) : 0;

            BigDecimal avgDealSize = totalWon > 0 ?
                wonValue.divide(new BigDecimal(totalWon), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

            // Count by stage
            Map<String, Integer> countByStage = isManager ?
                opportunityDAO.countByStage() : opportunityDAO.countByStageForSales(salesId);

            // Revenue forecast by month (current year)
            int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            List<Object[]> monthlyRevenue = opportunityDAO.getMonthlyRevenue(year);

            // Top 5 closest to close date
            List<model.sales.Opportunity> topOpps = openOpps.stream()
                .filter(o -> o.getExpectedCloseDate() != null)
                .sorted(Comparator.comparing(model.sales.Opportunity::getExpectedCloseDate))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

            // Lost reason analysis
            Map<String, Long> lostByReason = new LinkedHashMap<>();
            for (model.sales.Opportunity o : lostOpps) {
                String r = o.getLostReason() != null ? o.getLostReason() : "Không rõ";
                lostByReason.merge(r, 1L, Long::sum);
            }

            request.setAttribute("totalOpen", totalOpen);
            request.setAttribute("totalWon", totalWon);
            request.setAttribute("totalLost", totalLost);
            request.setAttribute("totalValue", totalValue);
            request.setAttribute("wonValue", wonValue);
            request.setAttribute("winRate", String.format("%.1f", winRate));
            request.setAttribute("avgDealSize", avgDealSize);
            request.setAttribute("countByStage", countByStage);
            request.setAttribute("monthlyRevenue", monthlyRevenue);
            request.setAttribute("topOpps", topOpps);
            request.setAttribute("lostByReason", lostByReason);
            request.setAttribute("isManager", isManager);
            request.getRequestDispatcher("/sales/sales-dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
