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
import java.text.SimpleDateFormat;
import java.util.List;

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
        if (userSession == null || !userSession.isSaleStaff()) {
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
        if (userSession == null || !userSession.isSaleStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Opportunity opp = buildFromRequest(request);
            opp.setCreatedBy(userSession.getStaff().getId());

            // Sales staff can only create for themselves
            if (userSession.isSaleStaff()) {
                opp.setAssignedSalesId(userSession.getStaff().getId());
            }

            String title = opp.getTitle();
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Tiêu đề không được để trống");
                request.setAttribute("customers", authDAO.getAllCustomers());
                request.setAttribute("staffList", authDAO.getAllStaff());
                request.setAttribute("pipelines", pipelineDAO.getAll());
                request.setAttribute("mode", "create");
                request.getRequestDispatcher("/sales/opportunity-form.jsp").forward(request, response);
                return;
            }

            opportunityDAO.insert(opp);
            response.sendRedirect(request.getContextPath() + "/sales/opportunities");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }

    protected Opportunity buildFromRequest(HttpServletRequest request) throws Exception {
        Opportunity opp = new Opportunity();
        opp.setTitle(request.getParameter("title"));
        String custId = request.getParameter("customerId");
        opp.setCustomerId(custId != null && !custId.isEmpty() ? Integer.parseInt(custId) : null);
        String salesId = request.getParameter("assignedSalesId");
        opp.setAssignedSalesId(salesId != null && !salesId.isEmpty() ? Integer.parseInt(salesId) : 0);
        opp.setStage(request.getParameter("stage") != null ? request.getParameter("stage") : "Qualification");
        opp.setStatus("Open");
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
        opp.setPipelineId(plId != null && !plId.isEmpty() ? Integer.parseInt(plId) : 1);
        opp.setNotes(request.getParameter("notes"));
        return opp;
    }
}
