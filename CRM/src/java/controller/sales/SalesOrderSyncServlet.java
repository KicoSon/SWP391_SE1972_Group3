package controller.sales;

import dal.SalesOrderDAO;
import model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/sales/order-sync")
public class SalesOrderSyncServlet extends HttpServlet {

    private SalesOrderDAO salesOrderDAO;

    @Override
    public void init() throws ServletException {
        salesOrderDAO = new SalesOrderDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
        if (userSession == null || !userSession.isStaff()) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = salesOrderDAO.syncToCustomerCore(id);
            String msg = success ? "syncSuccess" : "syncFailed";
            response.sendRedirect(request.getContextPath() + "/sales/order-detail?id=" + id + "&msg=" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Internal Server Error");
        }
    }
}
