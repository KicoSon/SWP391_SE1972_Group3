package controller;

import dal.AdminDAO;
import java.io.IOException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;

@WebServlet(name = "ManagerDashboardController", urlPatterns = {"/admin/dashboard"})
public class ManagerDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");

        // Check admin
        if (!userSession.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AdminDAO adminDAO = new AdminDAO();

        /* ================= STATISTICS ================= */
        request.setAttribute("totalRevenue", adminDAO.getTotalRevenue());
        request.setAttribute("totalOrders", adminDAO.getTotalOrders());
        request.setAttribute("totalCustomers", adminDAO.getTotalCustomers());
        request.setAttribute("totalProducts", adminDAO.getTotalProducts());
        request.setAttribute("totalStaff", adminDAO.getTotalStaff());
        request.setAttribute("totalTickets", adminDAO.getTotalTickets());

        /* ================= CHART DATA ================= */
        request.setAttribute("revenueByMonth", adminDAO.getRevenueByMonth());
        request.setAttribute("ordersByStatus", adminDAO.getOrdersByStatus());
        request.setAttribute("ticketsByStatus", adminDAO.getTicketsByStatus());
        request.setAttribute("ticketsByPriority", adminDAO.getTicketsByPriority());
        
        /* ================= FORWARD ================= */
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
