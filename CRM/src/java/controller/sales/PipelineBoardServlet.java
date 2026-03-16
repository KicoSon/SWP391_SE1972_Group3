package controller.sales;

import dal.OpportunityDAO;
import dal.PipelineDAO;
import model.sales.Opportunity;
import model.sales.PipelineStage;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/sales/pipeline-board")
public class PipelineBoardServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;
    private PipelineDAO    pipelineDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
        pipelineDAO    = new PipelineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        boolean isManager = userSession.isAdmin();
        Integer salesId = isManager ? null : userSession.getStaff().getId();

        try {
            // Get default pipeline stages
            int pipelineId = 1;
            String plParam = request.getParameter("pipelineId");
            if (plParam != null && !plParam.isEmpty()) pipelineId = Integer.parseInt(plParam);

            List<PipelineStage> stages = pipelineDAO.getStagesByPipelineId(pipelineId);
            List<Opportunity> allOpps = opportunityDAO.filterOpportunities(null, null, "Open", salesId);

            // Group by stage
            Map<String, List<Opportunity>> board = new LinkedHashMap<>();
            for (PipelineStage st : stages) {
                board.put(st.getStageName(), new ArrayList<>());
            }
            for (Opportunity o : allOpps) {
                board.computeIfAbsent(o.getStage(), k -> new ArrayList<>()).add(o);
            }

            // KPI
            Map<String, Integer> countByStage = isManager ?
                opportunityDAO.countByStage() : opportunityDAO.countByStageForSales(salesId != null ? salesId : 0);
            Map<String, BigDecimal> valueByStage = opportunityDAO.valueByStage();

            int kpiTotal     = allOpps.size();
            BigDecimal kpiValueOpen = allOpps.stream()
                .map(o -> o.getExpectedValue() != null ? o.getExpectedValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            long kpiCountOpen      = allOpps.stream().filter(o -> o.getStage() != null && !"Closed Won".equals(o.getStage()) && !"Closed Lost".equals(o.getStage())).count();
            long kpiCountQuotation = allOpps.stream().filter(o -> "Quotation".equals(o.getStage())).count();
            long kpiCountLost      = allOpps.stream().filter(o -> "Closed Lost".equals(o.getStage())).count();

            // Monthly revenue map (month-name -> value) for chart
            Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
            String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            for (Object[] row : opportunityDAO.getMonthlyRevenue(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR))) {
                int m = (int) row[0];
                monthlyRevenue.put(monthNames[m - 1], row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO);
            }

            request.setAttribute("boardData", board);
            request.setAttribute("stages", stages);
            request.setAttribute("stageCount", countByStage);
            request.setAttribute("valueByStage", valueByStage);
            request.setAttribute("kpiTotal", kpiTotal);
            request.setAttribute("kpiValueOpen", kpiValueOpen);
            request.setAttribute("kpiCountOpen", kpiCountOpen);
            request.setAttribute("kpiCountQuotation", kpiCountQuotation);
            request.setAttribute("kpiCountLost", kpiCountLost);
            request.setAttribute("monthlyRevenue", monthlyRevenue);
            request.setAttribute("pipelines", pipelineDAO.getAll());
            request.setAttribute("currentPipelineId", pipelineId);
            request.setAttribute("isManager", isManager);
            request.getRequestDispatcher("/sales/pipeline-board.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
