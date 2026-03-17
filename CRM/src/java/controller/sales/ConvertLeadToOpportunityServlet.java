package controller.sales;

import dal.LeadDAO;
import dal.OpportunityDAO;
import dal.PipelineDAO;
import dal.AuthDAO;
import dal.CustomerDAO;
import model.Lead;
import model.Customer;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/sales/convert-lead")
public class ConvertLeadToOpportunityServlet extends HttpServlet {

    private LeadDAO       leadDAO;
    private OpportunityDAO opportunityDAO;
    private PipelineDAO   pipelineDAO;
    private AuthDAO       authDAO;
    private CustomerDAO   customerDAO;

    @Override
    public void init() throws ServletException {
        leadDAO        = new LeadDAO();
        opportunityDAO = new OpportunityDAO();
        pipelineDAO    = new PipelineDAO();
        authDAO        = new AuthDAO();
        customerDAO    = new CustomerDAO();
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
            long leadId = Long.parseLong(request.getParameter("leadId"));
            Lead lead = leadDAO.getById(leadId);
            if (lead == null) { response.sendError(404, "Lead not found"); return; }

            if (!userSession.isAdmin()) {
                if (lead.getAssignedSalesId() == null
                        || lead.getAssignedSalesId().intValue() != userSession.getStaff().getId()) {
                    response.sendError(403, "Lead này không được giao cho bạn");
                    return;
                }
            }

            Integer defaultAssignedSalesId = null;
            if (lead.getAssignedSalesId() != null) {
                defaultAssignedSalesId = lead.getAssignedSalesId().intValue();
            } else if (!userSession.isAdmin()) {
                defaultAssignedSalesId = userSession.getStaff().getId();
            }

            request.setAttribute("lead", lead);
            request.setAttribute("customers", authDAO.getAllCustomers());
            request.setAttribute("staffList", authDAO.getSalesStaff());
            request.setAttribute("defaultAssignedSalesId", defaultAssignedSalesId);
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
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            long leadId = Long.parseLong(SalesInputValidator.requireText("Lead", request.getParameter("leadId"), 1, 20));
            Lead lead = leadDAO.getById(leadId);
            if (lead == null) {
                response.sendError(404, "Lead not found");
                return;
            }

            if (!userSession.isAdmin()) {
                if (lead.getAssignedSalesId() == null
                        || lead.getAssignedSalesId().intValue() != userSession.getStaff().getId()) {
                    response.sendError(403, "Lead này không được giao cho bạn");
                    return;
                }
            }

            if (userSession.getStaff() == null) {
                response.sendError(403, "Không tìm thấy thông tin nhân viên");
                return;
            }

            Opportunity opp = new Opportunity();
            opp.setTitle(SalesInputValidator.requireText("Tiêu đề", request.getParameter("title"), 3, 255));
            
            Integer customerId = SalesInputValidator.parseNullablePositiveInt("Khách hàng", request.getParameter("customerId"));
            if (customerId == null) {
                // Auto create Customer from Lead
                Customer newCust = new Customer();
                newCust.setFullName(lead.getFullName());
                newCust.setPhone(lead.getPhone());
                newCust.setEmail(lead.getEmail());
                newCust.setAddress(lead.getAddress() != null ? lead.getAddress() : "");
                newCust.setPassword("12345678"); // default pass or random
                newCust.setOwnerId(userSession.getStaff().getId());
                newCust.setStatus("Active");
                int newCustId = customerDAO.insertAndReturnId(newCust);
                if (newCustId > 0) {
                    customerId = newCustId;
                }
            }
            opp.setCustomerId(customerId);
            
            opp.setLeadId(leadId);
            int assignedSalesId = SalesInputValidator.parsePositiveIntOrDefault("Sales phụ trách", request.getParameter("assignedSalesId"), userSession.getStaff().getId());
            opp.setAssignedSalesId(assignedSalesId);
            opp.setStage(SalesInputValidator.parseOpportunityStage(request.getParameter("stage"), "Qualification"));
            opp.setStatus("Open");
            opp.setSource("Lead");
            opp.setExpectedValue(SalesInputValidator.parseNonNegativeDecimal("Giá trị dự kiến", request.getParameter("expectedValue"), BigDecimal.ZERO));
            opp.setCloseProbability(SalesInputValidator.parseDoubleInRange("Xác suất đóng", request.getParameter("closeProbability"), 10, 0, 100));
            opp.setExpectedCloseDate(SalesInputValidator.parseOptionalDate("Ngày dự kiến đóng", request.getParameter("expectedCloseDate")));
            opp.setPipelineId(SalesInputValidator.parsePositiveIntOrDefault("Pipeline", request.getParameter("pipelineId"), 1));
            opp.setNotes(SalesInputValidator.optionalText(request.getParameter("notes"), 2000));
            opp.setCreatedBy(userSession.getStaff().getId());

            boolean success = opportunityDAO.convertLeadToOpportunity(leadId, opp);
            if (success) {
                // Ensure lead status is definitively set to Converted
                leadDAO.updateStatus(leadId, "Converted");
                response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + opp.getId());
            } else {
                response.sendError(500, "Không thể convert Lead sang Opportunity.");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
