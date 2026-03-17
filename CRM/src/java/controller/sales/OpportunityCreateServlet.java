package controller.sales;

import dal.OpportunityDAO;
import dal.PipelineDAO;
import dal.AuthDAO;
import model.sales.Opportunity;
import model.sales.Pipeline;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/sales/opportunity-create")
public class OpportunityCreateServlet extends HttpServlet {

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
            request.setAttribute("customers", authDAO.getAllCustomers());
            request.setAttribute("staffList", authDAO.getAllStaff());
            request.setAttribute("pipelines", pipelineDAO.getAll());
            request.setAttribute("mode", "create");
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
            Opportunity opp = buildFromRequest(request);
            if (userSession.getStaff() == null) {
                response.sendError(403, "Không tìm thấy thông tin nhân viên");
                return;
            }
            opp.setCreatedBy(userSession.getStaff().getId());

            // Sales staff can only create for themselves
            if (userSession.isSaleStaff()) {
                opp.setAssignedSalesId(userSession.getStaff().getId());
            }

            opportunityDAO.insert(opp);
            response.sendRedirect(request.getContextPath() + "/sales/opportunities");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            renderCreateForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }

    protected Opportunity buildFromRequest(HttpServletRequest request) throws Exception {
        Opportunity opp = new Opportunity();
        opp.setTitle(SalesInputValidator.requireText("Tiêu đề", request.getParameter("title"), 3, 255));
        opp.setCustomerId(SalesInputValidator.parseNullablePositiveInt("Khách hàng", request.getParameter("customerId")));
        opp.setAssignedSalesId(SalesInputValidator.parsePositiveInt("Sales phụ trách", request.getParameter("assignedSalesId")));
        opp.setStage(SalesInputValidator.parseOpportunityStage(request.getParameter("stage"), "Qualification"));
        opp.setStatus("Open");
        opp.setExpectedValue(SalesInputValidator.parseNonNegativeDecimal("Giá trị dự kiến", request.getParameter("expectedValue"), BigDecimal.ZERO));
        opp.setCloseProbability(SalesInputValidator.parseDoubleInRange("Xác suất đóng", request.getParameter("closeProbability"), 0, 0, 100));
        opp.setExpectedCloseDate(SalesInputValidator.parseOptionalDate("Ngày dự kiến đóng", request.getParameter("expectedCloseDate")));
        opp.setSource(SalesInputValidator.parseOpportunitySource(request.getParameter("source"), "Manual"));
        opp.setCampaignId(SalesInputValidator.parseNullablePositiveInt("Campaign", request.getParameter("campaignId")));
        opp.setPipelineId(SalesInputValidator.parsePositiveIntOrDefault("Pipeline", request.getParameter("pipelineId"), 1));
        opp.setNotes(SalesInputValidator.optionalText(request.getParameter("notes"), 2000));
        return opp;
    }

    private void renderCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("customers", authDAO.getAllCustomers());
            request.setAttribute("staffList", authDAO.getAllStaff());
            request.setAttribute("pipelines", pipelineDAO.getAll());
            request.setAttribute("mode", "create");
            request.getRequestDispatcher("/sales/opportunity-form.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
