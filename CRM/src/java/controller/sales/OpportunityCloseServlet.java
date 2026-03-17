package controller.sales;

import dal.OpportunityDAO;
import model.sales.Opportunity;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/opportunity-close")
public class OpportunityCloseServlet extends HttpServlet {

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
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int id = SalesInputValidator.parsePositiveInt("Opportunity", request.getParameter("id"));
            String status = SalesInputValidator.requireText("Trạng thái", request.getParameter("status"), 3, 10);
            String lostReason = SalesInputValidator.optionalText(request.getParameter("lostReason"), 500);

            if (!"Won".equalsIgnoreCase(status) && !"Lost".equalsIgnoreCase(status)) {
                throw new IllegalArgumentException("Trạng thái đóng không hợp lệ");
            }
            if ("Lost".equalsIgnoreCase(status) && (lostReason == null || lostReason.isEmpty())) {
                throw new IllegalArgumentException("Vui lòng nhập lý do thất bại");
            }

            Opportunity opp = opportunityDAO.getById(id);
            if (opp == null) { response.sendError(404); return; }

            if (userSession.isSaleStaff() && opp.getAssignedSalesId() != userSession.getStaff().getId()) {
                response.sendError(403, "Access Denied");
                return;
            }

            opportunityDAO.closeOpportunity(id, status, lostReason);
            response.sendRedirect(request.getContextPath() + "/sales/opportunity-detail?id=" + id);
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
