package controller.sales;

import dal.LeadDAO;
import dal.OpportunityDAO;
import dal.PipelineDAO;
import dal.AuthDAO;
import model.Lead;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;


@WebServlet("/sales/convert-lead")
public class ConvertLeadToOpportunityServlet extends HttpServlet {

    private LeadDAO       leadDAO;
    private OpportunityDAO opportunityDAO;
    private PipelineDAO   pipelineDAO;
    private AuthDAO       authDAO;

    @Override
    public void init() throws ServletException {
        leadDAO        = new LeadDAO();
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
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            long leadId = Long.parseLong(request.getParameter("leadId"));
            Lead lead = leadDAO.getById(leadId);
            if (lead == null) { response.sendError(404, "Lead not found"); return; }

            request.setAttribute("lead", lead);
            request.setAttribute("customers", authDAO.getAllCustomers());
            request.setAttribute("staffList", authDAO.getAllStaff());
            request.setAttribute("pipelines", pipelineDAO.getAll());
            request.setAttribute("mode", "convert");
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
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            long leadId = Long.parseLong(request.getParameter("leadId"));

            Opportunity opp = new Opportunity();
            opp.setTitle(request.getParameter("title"));
            String custId = request.getParameter("customerId");
            opp.setCustomerId(custId != null && !custId.isEmpty() ? Integer.parseInt(custId) : null);
            opp.setLeadId(leadId);
            String salesId = request.getParameter("assignedSalesId");
            opp.setAssignedSalesId(salesId != null && !salesId.isEmpty() ?
                Integer.parseInt(salesId) : userSession.getStaff().getId());
            opp.setStage("Qualification");
            opp.setStatus("Open");
            opp.setSource("Lead");
            String ev = request.getParameter("expectedValue");
            opp.setExpectedValue(ev != null && !ev.isEmpty() ? new BigDecimal(ev) : BigDecimal.ZERO);
            String cp = request.getParameter("closeProbability");
            opp.setCloseProbability(cp != null && !cp.isEmpty() ? Double.parseDouble(cp) : 10);
            String dateStr = request.getParameter("expectedCloseDate");
            if (dateStr != null && !dateStr.isEmpty()) {
                opp.setExpectedCloseDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
            }
            String plId = request.getParameter("pipelineId");
            opp.setPipelineId(plId != null && !plId.isEmpty() ? Integer.parseInt(plId) : 1);
            opp.setNotes(request.getParameter("notes"));
            opp.setCreatedBy(userSession.getStaff().getId());

            opportunityDAO.insert(opp);
            if (opp.getId() > 0) {
                // Update lead status to Converted
                leadDAO.updateStatus(leadId, "Converted");
            }
            response.sendRedirect(request.getContextPath() + "/sales/opportunities");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
