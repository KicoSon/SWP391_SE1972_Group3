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
import java.text.SimpleDateFormat;

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
            opp.setTitle(request.getParameter("title"));
            String custId = request.getParameter("customerId");
            opp.setCustomerId(custId != null && !custId.isEmpty() ? Integer.parseInt(custId) : null);
            String salesId = request.getParameter("assignedSalesId");
            opp.setAssignedSalesId(salesId != null && !salesId.isEmpty() ? Integer.parseInt(salesId) : existing.getAssignedSalesId());
            opp.setStage(request.getParameter("stage") != null ? request.getParameter("stage") : existing.getStage());
            opp.setStatus(existing.getStatus());
            String ev = request.getParameter("expectedValue");
            opp.setExpectedValue(ev != null && !ev.isEmpty() ? new BigDecimal(ev) : BigDecimal.ZERO);
            String cp = request.getParameter("closeProbability");
            opp.setCloseProbability(cp != null && !cp.isEmpty() ? Double.parseDouble(cp) : 0);
            String dateStr = request.getParameter("expectedCloseDate");
            if (dateStr != null && !dateStr.isEmpty()) {
                opp.setExpectedCloseDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
            }
            opp.setSource(request.getParameter("source"));
            String campId = request.getParameter("campaignId");
            opp.setCampaignId(campId != null && !campId.isEmpty() ? Integer.parseInt(campId) : null);
            String plId = request.getParameter("pipelineId");
            opp.setPipelineId(plId != null && !plId.isEmpty() ? Integer.parseInt(plId) : existing.getPipelineId());
            opp.setNotes(request.getParameter("notes"));

            opportunityDAO.update(opp);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
