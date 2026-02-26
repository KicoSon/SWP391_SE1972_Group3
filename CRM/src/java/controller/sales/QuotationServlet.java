package controller.sales;

import dal.QuotationDAO;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/quotations")
public class QuotationServlet extends HttpServlet {

    private QuotationDAO quotationDAO;

    @Override
    public void init() throws ServletException {
        quotationDAO = new QuotationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isSaleStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int oppId = Integer.parseInt(request.getParameter("opportunityId"));
            request.setAttribute("quotations", quotationDAO.getByOpportunityId(oppId));
            request.setAttribute("opportunityId", oppId);
            request.getRequestDispatcher("/sales/quotation-list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
