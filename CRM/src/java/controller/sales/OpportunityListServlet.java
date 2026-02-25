package controller.sales;

import dal.OpportunityDAO;
import dal.AuthDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/sales/opportunities")
public class OpportunityListServlet extends HttpServlet {

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
        if (userSession == null || !userSession.isSaleStaff()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String search  = request.getParameter("search");
        String stage   = request.getParameter("stage");
        String status  = request.getParameter("status");

        Integer salesId = null;
        if (userSession.isSaleStaff()) {
            salesId = userSession.getStaff().getId();
        }

        try {
            List<Opportunity> list = opportunityDAO.filterOpportunities(search, stage, status, salesId);
            request.setAttribute("opportunityList", list);
            System.out.println("hehehe" + list.size());
            request.setAttribute("searchVal", search);
            request.setAttribute("stageVal", stage);
            request.setAttribute("statusVal", status);
            request.setAttribute("isManager",
                userSession.isAdmin() || userSession.hasRole("SALES_MANAGER"));
            request.getRequestDispatcher("/sales/opportunity-list.jsp").forward(request, response);
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

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Opportunity opp = opportunityDAO.getById(id);
                if (opp != null) {
                    if (userSession.isSaleStaff() && opp.getAssignedSalesId() != userSession.getStaff().getId()) {
                        response.sendError(403, "Access Denied");
                        return;
                    }
                    opportunityDAO.delete(id);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        response.sendRedirect(request.getContextPath() + "/sales/opportunities");
    }
}
