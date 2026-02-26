package controller.sales;

import dal.OpportunityDAO;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/opportunity-assign")
public class OpportunityAssignServlet extends HttpServlet {

    private OpportunityDAO opportunityDAO;

    @Override
    public void init() throws ServletException {
        opportunityDAO = new OpportunityDAO();
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

        // Only Manager or Admin can assign
        if (!userSession.isAdmin() && !userSession.hasRole("SALES_MANAGER")) {
            response.sendError(403, "Only Manager can assign opportunities");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int newSalesId = Integer.parseInt(request.getParameter("salesId"));
            opportunityDAO.assignToSales(id, newSalesId);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
