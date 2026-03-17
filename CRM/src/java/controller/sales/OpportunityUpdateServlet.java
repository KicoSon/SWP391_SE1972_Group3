package controller.sales;

import dal.OpportunityDAO;
import dal.PipelineDAO;
import dal.AuthDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/sales/opportunity-update")
public class OpportunityUpdateServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;
    private PipelineDAO    pipelineDAO;
    private AuthDAO        authDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
        pipelineDAO    = new PipelineDAO();
        authDAO        = new AuthDAO();
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

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Opportunity opp = opportunityDAO.getById(id);
            if (opp == null) { response.sendError(404); return; }

            if (userSession.isSaleStaff() && opp.getAssignedSalesId() != userSession.getStaff().getId()) {
                response.sendError(403, "Access Denied");
                return;
            }

            request.setAttribute("opportunity", opp);
            request.setAttribute("customers", authDAO.getAllCustomers());
            request.setAttribute("staffList", authDAO.getAllStaff());
            request.setAttribute("pipelines", pipelineDAO.getAll());
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/sales/opportunity-form.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Opportunity existing = opportunityDAO.getById(id);
            if (existing == null) { response.sendError(404); return; }

            if (userSession.isSaleStaff() && existing.getAssignedSalesId() != userSession.getStaff().getId()) {
                response.sendError(403, "Access Denied");
                return;
            }

            Opportunity opp = new Opportunity();
            opp.setId(id);
            opp.setTitle(SalesInputValidator.requireText("Tiêu đề", request.getParameter("title"), 3, 255));
            opp.setCustomerId(SalesInputValidator.parseNullablePositiveInt("Khách hàng", request.getParameter("customerId")));
            int assignedSalesId = SalesInputValidator.parsePositiveIntOrDefault("Sales phụ trách", request.getParameter("assignedSalesId"), existing.getAssignedSalesId());
            opp.setAssignedSalesId(assignedSalesId);
            opp.setStage(SalesInputValidator.parseOpportunityStage(request.getParameter("stage"), existing.getStage()));
            opp.setStatus(existing.getStatus());
            opp.setExpectedValue(SalesInputValidator.parseNonNegativeDecimal("Giá trị dự kiến", request.getParameter("expectedValue"), BigDecimal.ZERO));
            opp.setCloseProbability(SalesInputValidator.parseDoubleInRange("Xác suất đóng", request.getParameter("closeProbability"), existing.getCloseProbability(), 0, 100));
            opp.setExpectedCloseDate(SalesInputValidator.parseOptionalDate("Ngày dự kiến đóng", request.getParameter("expectedCloseDate")));
            opp.setSource(SalesInputValidator.parseOpportunitySource(request.getParameter("source"), "Manual"));
            opp.setCampaignId(SalesInputValidator.parseNullablePositiveInt("Campaign", request.getParameter("campaignId")));
            opp.setPipelineId(SalesInputValidator.parsePositiveIntOrDefault("Pipeline", request.getParameter("pipelineId"), existing.getPipelineId()));
            opp.setNotes(SalesInputValidator.optionalText(request.getParameter("notes"), 2000));

            opportunityDAO.update(opp);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + id);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
