package controller.sales;

import dal.QuotationDAO;
import model.sales.Quotation;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/quotation-detail")
public class QuotationDetailServlet extends HttpServlet {

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
            int id = Integer.parseInt(request.getParameter("id"));
            Quotation q = quotationDAO.getById(id);
            if (q == null) { response.sendError(404); return; }

            request.setAttribute("quotation", q);
            request.setAttribute("items", quotationDAO.getItemsByQuotationId(id));
            request.setAttribute("isManager", userSession.isAdmin() || userSession.hasRole("SALES_MANAGER"));
            request.getRequestDispatcher("/sales/quotation-detail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
