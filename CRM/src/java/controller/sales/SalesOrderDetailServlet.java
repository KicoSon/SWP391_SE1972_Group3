package controller.sales;

import dal.SalesOrderDAO;
import dal.QuotationDAO;
import dal.SalesOrderItemDAO;
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
    private SalesOrderItemDAO salesOrderItemDAO;

    @Override
    public void init() throws ServletException {
        salesOrderDAO = new SalesOrderDAO();
        quotationDAO  = new QuotationDAO();
        salesOrderItemDAO = new SalesOrderItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            SalesOrder order = salesOrderDAO.getById(id);
            if (order == null) { response.sendError(404); return; }

            if ("true".equals(request.getParameter("success"))) {
                request.setAttribute("successMsg", "Status updated successfully.");
            }

            request.setAttribute("order", order);
            request.setAttribute("orderItems", salesOrderItemDAO.getByOrderId(id));
            request.setAttribute("quotation", quotationDAO.getById(order.getQuotationId()));
            request.setAttribute("quotationItems", quotationDAO.getItemsByQuotationId(order.getQuotationId()));
            request.setAttribute("isManager", userSession.isAdmin());
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
        if (userSession == null || (!userSession.isSaleStaff() && !userSession.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            if (status != null && !status.trim().isEmpty()) {
                salesOrderDAO.updateStatus(id, status);
            }
            response.sendRedirect(request.getContextPath() + "/sales/order-detail?id=" + id + "&success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
