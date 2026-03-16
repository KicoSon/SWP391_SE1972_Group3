package controller.sales;

import dal.LeadDAO;
import model.Lead;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/sales/my-leads")
public class SalesLeadServlet extends HttpServlet {

    private LeadDAO leadDAO;

    @Override
    public void init() throws ServletException {
        leadDAO = new LeadDAO();
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
            // getQualifiedLeadsFull returns all qualified/assigned leads with full fields + sale name
            List<Lead> leads = leadDAO.getQualifiedLeadsFull();

            // Sales staff only see leads assigned to them
            if (!userSession.isAdmin()) {
                long staffId = userSession.getStaff().getId();
                leads = leads.stream()
                        .filter(l -> l.getAssignedSalesId() != null && l.getAssignedSalesId() == staffId)
                        .collect(Collectors.toList());
            }

            request.setAttribute("leads", leads);
            request.setAttribute("isManager", userSession.isAdmin());
            request.getRequestDispatcher("/sales/my-leads.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
