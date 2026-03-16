package controller.sales;

import dal.QuotationDAO;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/quotation-approve")
public class QuotationApproveServlet extends HttpServlet {

    private QuotationDAO quotationDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO = new QuotationDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }
        if (!userSession.isAdmin() && !userSession.hasRole("SALES_MANAGER")) {
            response.sendError(403, "Only Manager can approve quotations"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action"); // approve or reject or send

            String newStatus;
            if ("approve".equals(action)) newStatus = "Approved";
            else if ("reject".equals(action)) newStatus = "Rejected";
            else if ("send".equals(action)) newStatus = "Sent";
            else newStatus = "Pending Approval";

            Integer approvedBy = "approve".equals(action) ? userSession.getStaff().getId() : null;
            quotationDAO.updateStatus(id, newStatus, approvedBy);
            response.sendRedirect(request.getContextPath() + "/sales/quotation-detail?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
