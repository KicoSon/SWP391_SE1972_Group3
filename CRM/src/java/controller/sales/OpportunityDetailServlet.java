package controller.sales;

import dal.OpportunityDAO;
import dal.QuotationDAO;
import dal.ActivityDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/opportunity-detail")
public class OpportunityDetailServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;
    private QuotationDAO quotationDAO;
    private ActivityDAO activityDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
        quotationDAO   = new QuotationDAO();
        activityDAO    = new ActivityDAO();
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
            int id = Integer.parseInt(request.getParameter("id"));
            Opportunity opp = opportunityDAO.getById(id);
            if (opp == null) { response.sendError(404, "Opportunity not found"); return; }

            if (userSession.isSaleStaff() && opp.getAssignedSalesId() != userSession.getStaff().getId()) {
                response.sendError(403, "Access Denied");
                return;
            }

            request.setAttribute("opportunity", opp);
            request.setAttribute("quotations", quotationDAO.getByOpportunityId(id));
//            request.setAttribute("activities", activityDAO.getActivitiesByOpportunityId(id));
            request.setAttribute("isManager",
                userSession.isAdmin() || userSession.hasRole("SALES_MANAGER"));
            request.getRequestDispatcher("/sales/opportunity-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
