package controller.sales;

import dal.SalesOrderDAO;
import dal.QuotationDAO;
import model.sales.SalesOrder;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/order-detail")
public class SalesOrderDetailServlet extends HttpServlet {

    private SalesOrderDAO salesOrderDAO;
    private QuotationDAO  quotationDAO;

    @Override
    public void init() throws ServletException {
        salesOrderDAO = new SalesOrderDAO();
        quotationDAO  = new QuotationDAO();
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
            SalesOrder order = salesOrderDAO.getById(id);
            if (order == null) { response.sendError(404); return; }

            request.setAttribute("order", order);
            request.setAttribute("quotation", quotationDAO.getById(order.getQuotationId()));
            request.setAttribute("quotationItems", quotationDAO.getItemsByQuotationId(order.getQuotationId()));
            request.setAttribute("isManager", userSession.isAdmin() || userSession.hasRole("SALES_MANAGER"));
            request.getRequestDispatcher("/sales/order-detail.jsp").forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newStatus = request.getParameter("status");
            salesOrderDAO.updateStatus(id, newStatus);
            response.sendRedirect(request.getContextPath() + "/sales/order-detail?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
