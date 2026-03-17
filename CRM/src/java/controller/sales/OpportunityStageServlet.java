package controller.sales;

import dal.OpportunityDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/opportunity-stage")
public class OpportunityStageServlet extends HttpServlet {

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
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendError(401, "Unauthorized");
            return;
        }

        try {
            int id = SalesInputValidator.parsePositiveInt("Opportunity", request.getParameter("id"));
            String newStage = SalesInputValidator.parseOpportunityStage(request.getParameter("newStage"), "Qualification");

            Opportunity opp = opportunityDAO.getById(id);
            if (opp == null) { response.sendError(404); return; }

            // Only check ownership for non-manager sales staff
            if (!userSession.isAdmin() && userSession.isSaleStaff()
                    && opp.getAssignedSalesId() != userSession.getStaff().getId()) {
                response.sendError(403, "Access Denied");
                return;
            }

            opportunityDAO.updateStage(id, newStage);
            response.setStatus(200);
            response.getWriter().write("OK");
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
