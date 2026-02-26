
package controller;

import java.io.IOException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import util.PermissionChecker;

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
        
        // Check if user is admin - dashboard can be accessed by any admin staff
        if (!userSession.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
//        AdminDAO adminDAO = new AdminDAO();
//        
//        // Get statistics
//        double totalRevenue = adminDAO.getTotalRevenue();
//        int totalOrders = adminDAO.getTotalOrders();
//        int totalCustomers = adminDAO.getTotalCustomers();
//        int totalProducts = adminDAO.getTotalProducts();
//        int totalStaff = adminDAO.getTotalStaff();
//        int totalTickets = adminDAO.getTotalTickets();
//        
//        // Get chart data
//        List<DashboardTrend> revenueByMonth = adminDAO.getRevenueByMonth();
//        List<DashboardTrend> customerGrowth = adminDAO.getCustomerGrowthByMonth();
//        Map<String, Integer> ordersByStatus = adminDAO.getOrdersByStatus();
//        Map<String, Integer> staffByDepartment = adminDAO.getStaffByDepartment();
//        Map<String, Integer> ticketsByStatus = adminDAO.getTicketsByStatus();
//        Map<String, Integer> ticketsByPriority = adminDAO.getTicketsByPriority();
//        List<Map<String, Object>> topProducts = adminDAO.getTopProducts(5);
//        List<Order> recentOrders = adminDAO.getRecentOrders(10);
//        
//        // Set attributes
//        request.setAttribute("totalRevenue", totalRevenue);
//        request.setAttribute("totalOrders", totalOrders);
//        request.setAttribute("totalCustomers", totalCustomers);
//        request.setAttribute("totalProducts", totalProducts);
//        request.setAttribute("totalStaff", totalStaff);
//        request.setAttribute("totalTickets", totalTickets);
//        request.setAttribute("revenueByMonth", revenueByMonth);
//        request.setAttribute("customerGrowth", customerGrowth);
//        request.setAttribute("ordersByStatus", ordersByStatus);
//        request.setAttribute("staffByDepartment", staffByDepartment);
//        request.setAttribute("ticketsByStatus", ticketsByStatus);
//        request.setAttribute("ticketsByPriority", ticketsByPriority);
//        request.setAttribute("topProducts", topProducts);
//        request.setAttribute("recentOrders", recentOrders);
        
        // Forward to dashboard page
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
